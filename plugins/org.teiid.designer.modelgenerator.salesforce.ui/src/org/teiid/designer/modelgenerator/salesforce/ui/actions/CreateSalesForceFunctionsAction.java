/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.salesforce.ui.actions;

import static org.teiid.designer.modelgenerator.salesforce.SalesforceConstants.NAMESPACE_PROVIDER;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.modelgenerator.salesforce.ui.Activator;
import org.teiid.designer.modelgenerator.salesforce.ui.ModelGeneratorSalesforceUiConstants;
import org.teiid.designer.relational.model.DatatypeProcessor;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;


/**
 * 
 *
 * @since 8.0
 */
public class CreateSalesForceFunctionsAction extends SortableSelectionAction {
	private DatatypeProcessor datatypeProcessor;
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
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
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
                    if( assistant != null ) {
                    	return assistant.hasExtensionProperties(file);
                    }
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
    	this.datatypeProcessor = new DatatypeProcessor();
    	
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( final IProgressMonitor theMonitor ) {

                String taskName = ModelGeneratorSalesforceUiConstants.UTIL.getString("create.functions.job"); //$NON-NLS-1$
                final boolean started = ModelerCore.startTxn(true, true, taskName, this);
                boolean succeeded = false;
                try {
                    IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
                    theMonitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
                    final ModelResource modelResource = ModelUtil.getModelResource(modelFile, true);
                    theMonitor.worked(1000);
                    if (modelResource != null) {
                        createPushdownFunctions(modelResource, theMonitor);
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
    protected void createPushdownFunctions( ModelResource modelResource,
                                    IProgressMonitor theMonitor ) throws ModelWorkspaceException {
    	Procedure function = createIncludesFunction();
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
    private Procedure createExcludesFunction() {
        Procedure function = createCommonProps();
        function.setName("excludes"); //$NON-NLS-1$
        return function;
    }

    /**
     * @param createFunction
     * @return
     */
    private Procedure createIncludesFunction() {
    	Procedure function = createCommonProps();
        function.setName("includes"); //$NON-NLS-1$
        return function;
    }

    private Procedure createCommonProps() {
    	Procedure function = RelationalFactory.eINSTANCE.createProcedure();
    	function.setFunction(true);
    	
    	EObject stringDatatype = this.datatypeProcessor.findDatatype("string"); //$NON-NLS-1$

        ProcedureParameter param = RelationalFactory.eINSTANCE.createProcedureParameter();
        param.setName("columnName"); //$NON-NLS-1$
        if( stringDatatype != null ) {
            param.setType(stringDatatype);
            param.setLength(DatatypeProcessor.DEFAULT_DATATYPE_LENGTH);
        }
        param.setDirection(DirectionKind.IN_LITERAL);
        function.getParameters().add(param);

        param = RelationalFactory.eINSTANCE.createProcedureParameter();
        param.setName("param"); //$NON-NLS-1$

        if( stringDatatype != null ) {
            param.setType(stringDatatype);
            param.setLength(DatatypeProcessor.DEFAULT_DATATYPE_LENGTH);
        }
        param.setDirection(DirectionKind.IN_LITERAL);
        function.getParameters().add(param);

        ProcedureParameter result = RelationalFactory.eINSTANCE.createProcedureParameter();
        result.setDirection(DirectionKind.RETURN_LITERAL);
    	EObject booleanDatatype = this.datatypeProcessor.findDatatype("boolean"); //$NON-NLS-1$
        if( booleanDatatype != null ) {
        	result.setType(booleanDatatype);
        }
        result.setName("returnParam"); //$NON-NLS-1$
        function.getParameters().add(result);
        return function;
    }
}
