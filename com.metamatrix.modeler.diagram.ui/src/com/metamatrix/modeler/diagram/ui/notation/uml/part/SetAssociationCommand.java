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
package com.metamatrix.modeler.diagram.ui.notation.uml.part;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.wizards.NewModelObjectWizardManager;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * SetAssociationCommand
 */
public class SetAssociationCommand extends org.eclipse.gef.commands.Command implements DiagramUiConstants {
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SetAssociationCommand.class);

    private List selectedEObjects;

    public SetAssociationCommand( List selectedEObjects ) {
        super("Set Foreign Key"); //$NON-NLS-1$
        this.selectedEObjects = selectedEObjects;
    }

    @Override
    public void execute() {
        if (selectedEObjects != null) {
            Collection descriptors = null;

            try {
                descriptors = ModelerCore.getModelEditor().getNewAssociationDescriptors(selectedEObjects);
            } catch (ModelerCoreException theException) {
                Util.log(theException);
            }

            if ((descriptors != null) && !descriptors.isEmpty()) {
                AssociationDescriptor descriptor = null;

                if (descriptors.size() == 1) {
                    descriptor = (AssociationDescriptor)descriptors.iterator().next();
                } else {
                    LabelProvider provider = new AssociationDescriptorLabelProvider();
                    // display dialog to let user determine the association type
                    ListDialog dialog = new ListDialog(UiUtil.getWorkbenchShellOnlyIfUiThread());
                    dialog.setLabelProvider(provider);
                    // Defect 18296 the content provider Interface changed. Adapted the
                    // AssociationDescriptorLabelProvider to do both content and label....
                    dialog.setContentProvider((IStructuredContentProvider)provider);
                    // ListContentProvider.inputChanged() is look/checking for a input of
                    // type "List", so we need to create one from the Collection.
                    // Goutam chanaged it the other day to be a HashSet collection, which
                    // busted it.
                    dialog.setInput(new ArrayList(descriptors));
                    dialog.setTitle(Util.getString(PREFIX + "dialog.title")); //$NON-NLS-1$
                    dialog.setMessage(Util.getString(PREFIX + "dialog.msg")); //$NON-NLS-1$
                    dialog.setInitialSelections(new Object[] {descriptors.iterator().next()});

                    if (dialog.open() == Window.OK) {
                        Object[] result = dialog.getResult();

                        if ((result != null) && (result.length > 0)) {
                            // should only be one
                            descriptor = (AssociationDescriptor)result[0];
                        }
                    }
                }

                // create the association
                if (descriptor != null) {
                    Shell shell = DiagramUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
                    ISelection selection = new StructuredSelection(selectedEObjects);

                    if (NewModelObjectWizardManager.isAssociationDescriptorValid(shell, descriptor, selection)) {
                        if (!descriptor.creationComplete()) {
                            String undoText = Util.getString(PREFIX + "undoText", new Object[] {descriptor.getText()}); //$NON-NLS-1$
                            boolean requiredStart = ModelerCore.startTxn(false, false, undoText, this);
                            boolean succeeded = false;

                            try {
                                EObject newAssociation = ModelerCore.getModelEditor().createNewAssociationFromDescriptor(descriptor);
                                // Let's call ModelWorkspaceManager and force open an editor?
                                ModelEditorManager.open(newAssociation, true);
                                succeeded = true;
                            } catch (ModelerCoreException theException) {
                                Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "msg.createDescriptorProblem", //$NON-NLS-1$
                                                                                     new Object[] {descriptor}));
                            } finally {
                                if (requiredStart) {
                                    if (succeeded) {
                                        ModelerCore.commitTxn();
                                    } else {
                                        ModelerCore.rollbackTxn();
                                    }
                                }
                            }
                        } else {
                            EObject newAssociation = ((AbstractAssociationDescriptor)descriptor).getNewAssociation();
                            if (newAssociation != null) {
                                ModelEditorManager.open(newAssociation, true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void redo() {

    }

    @Override
    public void undo() {

    }

    /**
     * The <code>AssociationDescriptorLabelProvider</code> provides text and images for {@link AssociationDescriptor}s.
     */
    class AssociationDescriptorLabelProvider extends LabelProvider implements IStructuredContentProvider {
        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.3
         */
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
        }

        /**
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object theElement ) {
            String result = null;

            if ((theElement != null) && (theElement instanceof AssociationDescriptor)) {
                result = ((AssociationDescriptor)theElement).getText();
            } else {
                result = super.getText(theElement);
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 4.3
         */
        public Object[] getElements( Object inputElement ) {
            // We have a list
            return ((List)inputElement).toArray();
        }

    }
}
