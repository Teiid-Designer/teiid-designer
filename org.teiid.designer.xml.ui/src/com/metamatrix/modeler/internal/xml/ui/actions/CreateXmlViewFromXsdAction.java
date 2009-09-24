/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.actions;

//import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.actions.DeleteResourceAction;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelSelectorDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelSelectorInfo;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelWorkspaceViewerFilter;
import com.metamatrix.modeler.internal.xml.ui.wizards.XmlViewModelSelectionValidator;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiPlugin;
import com.metamatrix.modeler.xml.ui.dialogs.ConfirmSaveXsdModelDialog;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.0
 */
public class CreateXmlViewFromXsdAction extends SortableSelectionAction implements ModelerXmlUiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final String KEY_NO_SEL_DLG_MSG = "CreateXmlViewFromXsdAction.noSelMsg"; //$NON-NLS-1$
    private static final String NO_SEL_DLG_TITLE = Util.getString("CreateXmlViewFromXsdAction.noSelTitle"); //$NON-NLS-1$
    private static final String DEFAULT_EXPLORER_TITLE = Util.getString("CreateXmlViewFromXsdAction.defaultViewName"); //$NON-NLS-1$;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public CreateXmlViewFromXsdAction() {
        super();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The model editor for the specified resource is activated. If the editor is not already opened it is opened.
     * 
     * @param theModel the model resource whose editor has been requested to be activated
     */
    protected void activateModelEditor( final ModelResource theModel ) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                try {
                    final IFile file = (IFile)theModel.getUnderlyingResource();

                    if (file != null) {
                        // Changed to use method that insures Object editor mode is on
                        ModelEditorManager.openInEditMode(theModel,
                                                          true,
                                                          com.metamatrix.modeler.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
                        ModelEditorManager.save(file);
                    }
                } catch (ModelWorkspaceException theException) {
                    Util.log(theException);
                }
            }
        });
    }

    protected boolean executeBuild( List theXsdRoots,
                                    final IProgressMonitor theMonitor ) {
        boolean result = false;
        final String PREFIX = I18nUtil.getPropertyPrefix(CreateXmlViewFromXsdAction.class);

        theMonitor.beginTask("Building View Model Documents:  ", 100); //$NON-NLS-1$;

        // first select/create a XML Document Model
        ModelResource xmlDocModel = null;
        ModelSelectorInfo newModelInfo = new ModelSelectorInfo(
                                                               Util.getString(PREFIX + "modelTypeName"), //$NON-NLS-1$
                                                               ModelType.VIRTUAL_LITERAL, XmlDocumentPackage.eNS_URI,
                                                               Util.getString(PREFIX + "newModelNameLabel"), //$NON-NLS-1$
                                                               Util.getString(PREFIX + "modelSelectorTitle")); //$NON-NLS-1$
        ModelSelectorDialog mwdDialog = new ModelSelectorDialog(getShell(), newModelInfo);
        mwdDialog.addFilter(new ModelWorkspaceViewerFilter(true));
        mwdDialog.setValidator(new XmlViewModelSelectionValidator());
        mwdDialog.setAllowMultiple(false);
        mwdDialog.open();
        boolean modelCreated = mwdDialog.isNewModel();
        if (mwdDialog.getReturnCode() == Window.OK) {
            Object[] selectedObjects = mwdDialog.getResult();

            if ((selectedObjects.length == 1) && (selectedObjects[0] instanceof IFile)) {
                IFile theFile = (IFile)selectedObjects[0];

                if (theFile != null) {
                    try {
                        xmlDocModel = ModelUtilities.getModelResource(theFile, false);
                    } catch (ModelWorkspaceException theException) {
                        Util.log(theException);
                    }
                }
            }
        }

        if (xmlDocModel != null) {
            // start contributor wizard
            final ModelResource xmlModel = xmlDocModel;
            ModelResource schemaModelResource = getSchemaModel(theXsdRoots);

            if (schemaModelResource != null) {
                try {
                    final IFile schemaModel = (IFile)schemaModelResource.getUnderlyingResource();
                    ISelection schemaSelection = new StructuredSelection(schemaModel);

                    ViewXmlMessageStructureWizard wizard = new ViewXmlMessageStructureWizard(xmlModel, schemaSelection);
                    XSDElementDeclaration[] roots = (XSDElementDeclaration[])theXsdRoots.toArray(new XSDElementDeclaration[theXsdRoots.size()]);
                    wizard.setDocumentRoots(roots);

                    // show wizard
                    if (new WizardDialog(getShell(), wizard).open() == Window.OK) {
                        result = true;
                        activateModelEditor(xmlModel);
                    } else if (modelCreated) {
                        // delete model is just created and wizard canceled
                        if (!xmlModel.getPath().toFile().getAbsoluteFile().exists()) {
                            Display.getCurrent().asyncExec(new Runnable() {
                                public void run() {
                                    try {
                                        IResource resource = xmlModel.getUnderlyingResource();

                                        if (resource != null) {
                                            DeleteResourceAction action = new DeleteResourceAction();
                                            action.selectionChanged(null, new StructuredSelection(resource));
                                            action.run();
                                        }
                                    } catch (ModelWorkspaceException theException) {
                                        Util.log(theException);
                                    }
                                }
                            });
                        }
                    }

                    // need to refresh Model Explorer if opened since when validation is run the XSD is unloaded and
                    // reloaded which makes new instances of schema elements. refresh updates the to the new elements.
                    IViewPart part = UiUtil.getViewPart(UiConstants.Extensions.Explorer.VIEW);

                    if (part != null) {
                        ((ResourceNavigator)part).getViewer().refresh(schemaModel);
                    }
                } catch (Exception theException) {
                    Util.log(theException);
                }
            }
        }

        notifyResult(result);

        return result;
    }

    protected boolean processXsdRoots( final List theXsdRoots ) {
        final boolean[] result = new boolean[1];

        if ((theXsdRoots != null) && !theXsdRoots.isEmpty()) {
            final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
                @Override
                public void execute( IProgressMonitor theMonitor ) {
                    try {
                        // make sure XSD is saved before building
                        if (isXsdSaved(theXsdRoots)) {
                            result[0] = executeBuild(theXsdRoots, theMonitor);
                        }
                    } finally {
                        theMonitor.done();
                    }
                }
            };

            final boolean startedTxn = ModelerCore.startTxn(false, false, "Create XML View Model", this); //$NON-NLS-1$
            boolean success = false;

            try {
                new ProgressMonitorDialog(getShell()).run(false, false, op);
                success = result[0];
            } catch (InterruptedException theException) {
                success = false;
            } catch (InvocationTargetException theException) {
                final String PREFIX = I18nUtil.getPropertyPrefix(CreateXmlViewFromXsdAction.class);
                success = false;
                Throwable realException = theException.getTargetException();
                String msg = realException.getMessage();

                if (StringUtil.isEmpty(msg)) {
                    msg = Util.getString(PREFIX + "noDetailsMsg"); //$NON-NLS-1$
                }

                String title = Util.getString(PREFIX + "errorBuildingDialogTitle"); //$NON-NLS-1$
                MessageDialog.openError(getShell(), title, msg);
            } finally {
                if (startedTxn) {
                    if (success) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        return result[0];
    }

    /**
     * Obtains the schema model resource for the specified list of XSD element roots.
     * 
     * @param theXsdRoots the XSD element whose schema model is being requested
     * @return the schema model or <code>null</code> if not found
     * @since 5.0.2
     */
    protected ModelResource getSchemaModel( List theXsdRoots ) {
        ModelResource result = null;

        try {
            result = ModelUtil.getModel(theXsdRoots.get(0));
        } catch (ModelWorkspaceException theException) {
            Util.log(theException);
        }

        return result;
    }

    /**
     * Obtains the <code>Shell</code> for the
     * 
     * @return
     * @since 5.0.2
     */
    protected Shell getShell() {
        return ModelerXmlUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

    /**
     * Indicates if the XSD schema resource for the specified XSD elements is saved.
     * 
     * @param theXsdRoots the XSD element roots whose schema resource is being checked
     * @return <code>true</code> if saved; <code>false</code> otherwise.
     * @since 5.0.2
     */
    protected boolean isXsdSaved( List theXsdRoots ) {
        boolean result = true;

        // if XSD is dirty require the user to authorize a save to continue
        try {
            ModelResource schemaModelResource = getSchemaModel(theXsdRoots);

            if (schemaModelResource == null) {
                result = false;
            } else if (schemaModelResource.getEmfResource().isModified()) {
                ConfirmSaveXsdModelDialog dialog = new ConfirmSaveXsdModelDialog(getShell(), schemaModelResource);
                result = ((dialog.open() == Window.OK) && dialog.isSchemaSaved());
            }
        } catch (ModelWorkspaceException theException) {
            result = false;
            Util.log(theException);
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 5.0.2
     */
    @Override
    public void run() {
        IWorkbenchWindow iww = ModelerXmlUiPlugin.getDefault().getCurrentWorkbenchWindow();
        ISelection selection = getSelection();

        if (isValidSelection(selection)) {
            final List docRoots = SelectionUtilities.getSelectedEObjects(selection);
            processXsdRoots(docRoots);
        } else {
            // selection empty, tell user to select something:
            String viewId = ProductCustomizerMgr.getInstance().getProductCharacteristics().getPrimaryNavigationViewId();
            IViewReference viewRef = iww.getActivePage().findViewReference(viewId);
            String viewTitle = ((viewRef == null) ? DEFAULT_EXPLORER_TITLE : viewRef.getTitle());

            // show the dialog:
            String dlgMsg = Util.getString(KEY_NO_SEL_DLG_MSG, viewTitle);
            MessageDialog.openError(iww.getShell(), NO_SEL_DLG_TITLE, dlgMsg);
            return;
        }
    }

    /**
     * Valid selections include Relational Tables, Procedures or Relational Models. The roots instance variable will populated
     * with all Tables and Procedures contained within the current selection.
     * 
     * @return
     * @since 4.1
     */
    @Override
    protected boolean isValidSelection( ISelection selection ) {
        boolean isValid = true;
        if (SelectionUtilities.isEmptySelection(selection)) {
            isValid = false;
        }

        if (isValid) {
            XSDSchema schema = null;
            final List objs = SelectionUtilities.getSelectedObjects(selection);
            for (Iterator iter = objs.iterator(); iter.hasNext();) {
                Object nextObj = iter.next();

                if (nextObj instanceof XSDElementDeclaration) {
                    // make sure all selected elements are from same schema
                    XSDSchema temp = ((XSDElementDeclaration)nextObj).getSchema();

                    if (schema == null) {
                        schema = temp;
                    } else {
                        isValid = schema.equals(temp);
                    }
                } else {
                    isValid = false;
                }
                if (!isValid) {
                    break;
                }
            }
        } // endif -- is empty sel

        return isValid;
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0.2
     */
    @Override
    public boolean isApplicable( ISelection theSelection ) {
        return isValidSelection(theSelection);
    }

}
