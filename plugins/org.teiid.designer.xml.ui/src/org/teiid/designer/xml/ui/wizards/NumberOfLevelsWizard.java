/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metamodels.xml.XmlDocumentBuilderImpl;
import org.teiid.designer.metamodels.xml.XmlElement;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.xml.ui.ModelerXmlUiConstants;
import org.teiid.designer.xml.ui.ModelerXmlUiPlugin;


/**
 * NumberOfLevelsWizard
 */
public class NumberOfLevelsWizard extends AbstractWizard implements ModelerXmlUiConstants {
    // ============================================================================================================================
    // Variables

    private NumberOfLevelsWizardPage page;

    // ============================================================================================================================
    // Constructors

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public NumberOfLevelsWizard() {
        super(ModelerXmlUiPlugin.getDefault(), null, null);
    }

    // ============================================================================================================================
    // Implemented Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    @Override
    public boolean finish() {
        final XmlElement element = getSelectedElement();
        final int levels = page.getValue();

        // build the document within a transaction
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
			public void run( IProgressMonitor theMonitor ) {
                // open and activate editor first so that the editor is dirty after building
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(element);

                if (mr != null) {
                    // Changed to use method that insures Object editor mode is on
                    ModelEditorManager.openInEditMode(mr,
                                                      true,
                                                      org.teiid.designer.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
                }

                // start transaction
                final String description = Util.getString("NumberOfLevelsWizard.transactionText"); //$NON-NLS-1$
                boolean started = ModelerCore.startTxn(true, true, description, this);
                boolean succeeded = false;

                try {
                    XmlDocumentBuilderImpl builder = new XmlDocumentBuilderImpl(levels);
                    builder.buildDocument(element, theMonitor);
                    succeeded = true;
                } catch (final Exception theException) {
                    final String msg = Util.getString("NumberOfLevelsWizard.buildErrorMessage"); //$NON-NLS-1$
                    Util.log(IStatus.ERROR, theException, msg);
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }

                    theMonitor.done();
                }
            }
        };

        try {
            IWizardContainer container = getContainer();
            container.run(false, false, op); // builder currently does not support canceling
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), Util.getString("NumberOfLevelsWizard.error"), realException.getMessage()); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
        // This method is not being called by any infrastructure. Inserted getSelectedElement()
        // method to get the selection. BWP 11/04/03
    }

    // ============================================================================================================================
    // Overridden Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    @Override
    public void addPages() {
        page = new NumberOfLevelsWizardPage();
        addPage(page);
    }

    // ============================================================================================================================
    // MVC Controller Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    protected XmlElement getSelectedElement() {
        IWorkbenchWindow window = ModelerXmlUiPlugin.getDefault().getCurrentWorkbenchWindow();
        if (window != null) {
            ISelection selection = window.getSelectionService().getSelection();
            if (selection instanceof IStructuredSelection) {
                IStructuredSelection ssel = (IStructuredSelection)selection;
                if (ssel instanceof XmlElement) {
                    return (XmlElement)ssel;
                }
                Object firstElement = ssel.getFirstElement();
                if (firstElement instanceof XmlElement) {
                    return (XmlElement)firstElement;
                }
                return null;
            }
        }
        return null;
    }
}
