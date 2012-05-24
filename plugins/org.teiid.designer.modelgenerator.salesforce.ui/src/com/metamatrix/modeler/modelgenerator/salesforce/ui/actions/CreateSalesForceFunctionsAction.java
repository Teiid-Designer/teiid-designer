/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.actions;

import static com.metamatrix.modeler.modelgenerator.salesforce.SalesforceConstants.NAMESPACE_PROVIDER;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.function.FunctionFactory;
import com.metamatrix.metamodels.function.FunctionPackage;
import com.metamatrix.metamodels.function.FunctionParameter;
import com.metamatrix.metamodels.function.PushDownType;
import com.metamatrix.metamodels.function.ReturnParameter;
import com.metamatrix.metamodels.function.ScalarFunction;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.Activator;
import com.metamatrix.modeler.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * 
 */
public class CreateSalesForceFunctionsAction extends SortableSelectionAction {

    /**
     * 
     */
    public CreateSalesForceFunctionsAction() {
        super(ModelGeneratorSalesforceUiConstants.UTIL.getString("create.functions.label"), SWT.DEFAULT); //$NON-NLS-1$
        setImageDescriptor(Activator.getDefault().getImageDescriptor(ModelGeneratorSalesforceUiConstants.Images.NEW_MODEL_BANNER));
    }

    /**
     * @param text
     * @param style
     */
    public CreateSalesForceFunctionsAction( String text,
                                            int style ) {
        super(text, style);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    protected boolean isValidSelection( ISelection selection ) {
        return sourceModelSelected(selection);
    }

    private boolean sourceModelSelected( ISelection theSelection ) {
        if (SelectionUtilities.isSingleSelection(theSelection) && theSelection instanceof IStructuredSelection) {
            Object selectedObj = ((IStructuredSelection)theSelection).getFirstElement();

            if ((selectedObj instanceof IFile) && ModelIdentifier.isRelationalSourceModel((IFile)selectedObj)) {
                File file = ((IFile)selectedObj).getLocation().toFile();
                try {
                    ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                            .getRegistry()
                                                                                                            .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
                    return assistant.hasExtensionProperties(file);
                } catch (Exception e) {
                    UiConstants.Util.log(e);
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor theMonitor ) {

                String taskName = ModelGeneratorSalesforceUiConstants.UTIL.getString("create.functions.job"); //$NON-NLS-1$
                final boolean started = ModelerCore.startTxn(true, true, taskName, this);
                boolean succeeded = false;
                try {
                    IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
                    IFile file = modelFile.getParent().getFile(new Path("SalesforceFunctions.xmi")); //$NON-NLS-1$
                    theMonitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
                    final ModelResource modelResource = ModelerCore.create(file);
                    theMonitor.worked(1000);
                    if (modelResource != null) {
                        final ModelAnnotation modelAnnotation = modelResource.getModelAnnotation();
                        modelAnnotation.setPrimaryMetamodelUri(FunctionPackage.eNS_URI);
                        modelAnnotation.setModelType(ModelType.FUNCTION_LITERAL);
                        createFunctions(modelResource, theMonitor);
                        modelResource.save(theMonitor, false);
                    }

                    succeeded = true;
                } catch (ModelWorkspaceException e) {
                    final String msg = ModelGeneratorSalesforceUiConstants.UTIL.getString("create.functions.errorMessage", new Object[] { e.getMessage() }); //$NON-NLS-1$
                    UiConstants.Util.log(IStatus.ERROR, e, msg);
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                theMonitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
        } catch (final InterruptedException e) {
        } catch (final InvocationTargetException e) {
            UiConstants.Util.log(e.getTargetException());
        }
    }

    /**
     * @param modelResource
     */
    protected void createFunctions( ModelResource modelResource,
                                    IProgressMonitor theMonitor ) throws ModelWorkspaceException {
        ScalarFunction function = createIncludesFunction();
        modelResource.getEmfResource().getContents().add(function);
        theMonitor.worked(1000);
        function = createExcludesFunction();
        modelResource.getEmfResource().getContents().add(function);
        theMonitor.worked(1000);
    }

    /**
     * @param createFunction
     * @return
     */
    private ScalarFunction createExcludesFunction() {
        ScalarFunction function = createCommonProps();
        function.setName("excludes"); //$NON-NLS-1$
        return function;
    }

    /**
     * @param createFunction
     * @return
     */
    private ScalarFunction createIncludesFunction() {
        ScalarFunction function = createCommonProps();
        function.setName("includes"); //$NON-NLS-1$
        return function;
    }

    private ScalarFunction createCommonProps() {
        ScalarFunction function = FunctionFactory.eINSTANCE.createScalarFunction();
        function.setCategory("SalesForce"); //$NON-NLS-1$
        function.setPushDown(PushDownType.REQUIRED_LITERAL);
        function.setInvocationClass("None"); //$NON-NLS-1$
        function.setInvocationMethod("None"); //$NON-NLS-1$

        FunctionParameter param = FunctionFactory.eINSTANCE.createFunctionParameter();
        param.setName("columnName"); //$NON-NLS-1$
        param.setType("string"); //$NON-NLS-1$
        function.getInputParameters().add(param);

        param = FunctionFactory.eINSTANCE.createFunctionParameter();
        param.setName("param"); //$NON-NLS-1$
        param.setType("string"); //$NON-NLS-1$
        function.getInputParameters().add(param);

        ReturnParameter result = FunctionFactory.eINSTANCE.createReturnParameter();
        result.setType("boolean"); //$NON-NLS-1$
        function.setReturnParameter(result);
        return function;
    }
}
