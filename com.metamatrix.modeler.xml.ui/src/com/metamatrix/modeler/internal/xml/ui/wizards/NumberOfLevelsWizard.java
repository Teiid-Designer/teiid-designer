/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.internal.xml.ui.wizards;

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
import com.metamatrix.metamodels.internal.xml.XmlDocumentBuilderImpl;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiConstants;
import com.metamatrix.modeler.xml.ui.ModelerXmlUiPlugin;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

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
            public void run( IProgressMonitor theMonitor ) {
                // open and activate editor first so that the editor is dirty after building
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(element);

                if (mr != null) {
                    // Changed to use method that insures Object editor mode is on
                    ModelEditorManager.openInEditMode(mr,
                                                      true,
                                                      com.metamatrix.modeler.ui.UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
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
