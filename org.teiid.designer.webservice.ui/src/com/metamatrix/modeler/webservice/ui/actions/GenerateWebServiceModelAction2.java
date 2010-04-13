/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import com.metamatrix.common.xmi.XMIHeader;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.internal.webservice.ui.wizard.GenerateWebServiceModelWizard;
import com.metamatrix.modeler.internal.xml.ui.wizards.XmlDocumentContentProvider;
import com.metamatrix.modeler.internal.xml.ui.wizards.XmlDocumentSelectorDialog;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

public class GenerateWebServiceModelAction2 extends SortableSelectionAction implements IInternalUiConstants {

    private static final String KEY_NO_SEL_DLG_MSG = "GenerateWebServiceModelAction.noSelMsg"; //$NON-NLS-1$
    private static final String NO_SEL_DLG_TITLE = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.noSelTitle"); //$NON-NLS-1$
    private static final String DEFAULT_EXPLORER_TITLE = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.defaultViewName"); //$NON-NLS-1$;

    private boolean canIgnoreSelection = false;

    public GenerateWebServiceModelAction2() {
        super();
        setImageDescriptor(WebServiceUiPlugin.getDefault().getImageDescriptor(WebServiceUiPlugin.Images.CREATE_WEB_SERVICE));
    }

    /**
     * 
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return isValidSelection(selection);
    }

    private boolean isDocumentOrRootSelected( ISelection selection ) {
        if (!SelectionUtilities.isSingleSelection(selection)) {
            return false;
        }

        Object selectedObject = SelectionUtilities.getSelectedObject(selection);

        if (selectedObject != null && (selectedObject instanceof XmlDocument || selectedObject instanceof XmlRoot)) {
            return true;
        }

        return false;
    }

    private boolean isSingleXmlDocumentModelSelected( ISelection selection ) {
        if (!SelectionUtilities.isSingleSelection(selection)) {
            return false;
        }
        Object selectedObject = SelectionUtilities.getSelectedObject(selection);

        if (selectedObject instanceof IResource && ModelUtilities.isModelFile((IResource)selectedObject)) {
            IResource iResource = (IResource)selectedObject;
            XMIHeader header = ModelUtil.getXmiHeader(iResource);
            if (header != null) {
                String mmURI = header.getPrimaryMetamodelURI();
                if (mmURI != null && mmURI.equals(XmlDocumentPackage.eNS_URI)) {
                    return true;
                }
            }
        }
        return false;
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

        if (isValid && SelectionUtilities.isSingleSelection(selection)) {
            final Collection objs = SelectionUtilities.getSelectedObjects(selection);
            final Iterator selections = objs.iterator();
            while (selections.hasNext() && isValid) {
                final Object next = selections.next();
                if (next instanceof XmlDocument || next instanceof XmlRoot) {
                    isValid = true;
                } else {
                    isValid = false;
                }

                // stop processing if no longer valid:
                if (!isValid) {
                    break;
                } // endif -- valid
            } // endwhile -- all selected
        } else {
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void run() {
        IWorkbenchWindow iww = WebServiceUiPlugin.getDefault().getCurrentWorkbenchWindow();

        // ----------------------------------------------------
        // Defect 22355: added preProcess() check to see if VDB is dirty or any Models are Dirty.
        // We need to pre-process this through product characteristics
        // ----------------------------------------------------
        if (!ProductCustomizerMgr.getInstance().getProductCharacteristics().preProcess(this, iww.getShell())) {
            notifyResult(false);
            return;
        }
        // ----------------------------------------------------

        ISelection selection = getSelection();

        // We can handle single selection, multiple selection AND NO selection.
        // 1) If a document or document root is NOT selected we
        // go ahead and process.
        // 2) If Document Model selected ONLY, we present dialog to select document or document root within model
        // 3) If NULL, NONE OF THE ABOVE are selected, we present a dialog to document or document root within model
        // from the whole workspace/VDB

        IStructuredSelection thisSelection = null;
        if (selection != null && !selection.isEmpty() && isDocumentOrRootSelected(selection)) {
            thisSelection = (IStructuredSelection)selection;
        } else if (canIgnoreSelection) {
            // Set to false in case there's an exception and it doesn't get set back.
            canIgnoreSelection = false;
            // Present user a selection dialog for THIS XML DOCUMENT MODEL ONLY

            ILabelProvider labelProvider = new ModelExplorerLabelProvider();
            ITreeContentProvider contentProvider = new XmlDocumentContentProvider();

            XmlDocumentSelectorDialog xmlDialog = new XmlDocumentSelectorDialog(
                                                                                WebServiceUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                                                                labelProvider, contentProvider);
            xmlDialog.setAllowMultiple(false);

            if (selection != null && !selection.isEmpty() && isSingleXmlDocumentModelSelected(selection)) {
                xmlDialog.setInput(SelectionUtilities.getSelectedObject(selection));
            }
            xmlDialog.open();

            if (xmlDialog.getReturnCode() == Window.OK) {
                Object[] oSelectedObjects = xmlDialog.getResult();

                // add the selected location to this Relationship
                if (oSelectedObjects.length == 1) {
                    if (oSelectedObjects[0] instanceof XmlDocument || oSelectedObjects[0] instanceof XmlRoot) {
                        thisSelection = new StructuredSelection(oSelectedObjects[0]);
                    } else {
                        // ERROR !!!!!
                        String title = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.selectionError.title"); //$NON-NLS-1$
                        String dlgMsg = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.selectionError.wrongType"); //$NON-NLS-1$
                        MessageDialog.openError(iww.getShell(), title, dlgMsg);
                        notifyResult(false);
                        return;
                    }
                } else {
                    // ERROR !!!!!
                    String title = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.selectionError.title"); //$NON-NLS-1$
                    String dlgMsg = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.selectionError.emptySelection"); //$NON-NLS-1$
                    MessageDialog.openError(iww.getShell(), title, dlgMsg);
                    notifyResult(false);
                    return;
                }
            } else {
                return;
            }

        } else {
            // selection empty, tell user to select something:
            IViewReference ivr = iww.getActivePage().findViewReference(ProductCustomizerMgr.getInstance().getProductCharacteristics().getPrimaryNavigationViewId());
            String viewTitle;
            if (ivr != null) {
                viewTitle = ivr.getTitle();
            } else {
                // don't have a view ref, use a default name:
                viewTitle = DEFAULT_EXPLORER_TITLE;
            } // endif

            // show the dialog:
            String dlgMsg = IInternalUiConstants.UTIL.getString(KEY_NO_SEL_DLG_MSG, viewTitle);
            MessageDialog.openError(iww.getShell(), NO_SEL_DLG_TITLE, dlgMsg);
            return;
        }

        final GenerateWebServiceModelWizard wizard = new GenerateWebServiceModelWizard();
        wizard.init(iww.getWorkbench(), thisSelection);
        final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
        int rc = dialog.open();

        if (rc == Window.CANCEL) {
            notifyResult(false);
            return;
        }

        final MultiStatus result = wizard.getResult();
        final int severity = result.getSeverity();
        if (severity == IStatus.ERROR) {
            final String errTitle = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.errTitle"); //$NON-NLS-1$
            final String err = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.errFinish"); //$NON-NLS-1$
            ErrorDialog.openError(wizard.getShell(), errTitle, err, result);
        } else if (severity == IStatus.WARNING) {
            final String warnTitle = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.warnTitle"); //$NON-NLS-1$
            final String warn = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.warnFinish"); //$NON-NLS-1$
            ErrorDialog.openError(wizard.getShell(), warnTitle, warn, result);
        } else {
            final String okTitle = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.successTitle"); //$NON-NLS-1$
            final String ok = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.successFinish"); //$NON-NLS-1$

            List msgs = new ArrayList(result.getChildren().length);
            for (int i = 0; i < result.getChildren().length; i++) {
                msgs.add(result.getChildren()[i].getMessage());
            }
            if (msgs.size() > 0) {
                ListMessageDialog.openInformation(wizard.getShell(), okTitle, null, ok, msgs, null);
            } else {
                MessageDialog.openInformation(wizard.getShell(), okTitle, ok);
            }
        }

        notifyResult(severity < IStatus.ERROR && rc != Window.CANCEL);
    }

    /**
     * @param theCanIgnoreSelection The canIgnoreSelection to set.
     * @since 5.0
     */
    public void setCanIgnoreSelection( boolean theCanIgnoreSelection ) {
        this.canIgnoreSelection = theCanIgnoreSelection;
    }
}
