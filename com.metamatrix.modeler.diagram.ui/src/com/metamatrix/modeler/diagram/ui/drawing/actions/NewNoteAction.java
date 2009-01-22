/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing.actions;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingConstants;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingModelFactory;
import com.metamatrix.modeler.diagram.ui.drawing.model.NoteModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * NewNoteAction
 */
public class NewNoteAction extends DrawingAction {
    private static String titleStringId = "TextEntryPanel.title"; //$NON-NLS-1$
    static String topLabelId = "TextEntryPanel.topLabel"; //$NON-NLS-1$

    String newText;

    /**
     * Construct an instance of NewNoteAction.
     */
    public NewNoteAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.NEW_NOTE));
    }

    /**
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        // Let's get ahold of the current diagram...
        DiagramModelNode diagramNode = editor.getCurrentModel();
        if (diagramNode != null) {

            final String oldName = "SAMPLE NOTE"; //$NON-NLS-1$
            final Dialog dlg = new Dialog(Display.getDefault().getActiveShell(), DiagramUiConstants.Util.getString(titleStringId)) {
                @Override
                protected Control createDialogArea( final Composite parent ) {
                    final Composite dlgPanel = (Composite)super.createDialogArea(parent);
                    dlgPanel.setBounds(dlgPanel.getLocation().x, dlgPanel.getLocation().y, 200, 300);
                    WidgetFactory.createLabel(dlgPanel, DiagramUiConstants.Util.getString(topLabelId));

                    final Text nameText = WidgetFactory.createTextBox(dlgPanel, SWT.NONE, GridData.FILL_BOTH, oldName);
                    nameText.setText(oldName);
                    newText = oldName;
                    nameText.setSelection(0, oldName.length());
                    nameText.addModifyListener(new ModifyListener() {
                        public void modifyText( final ModifyEvent event ) {
                            handleModifyText(nameText);
                        }
                    });
                    return dlgPanel;
                }

                @Override
                protected void createButtonsForButtonBar( final Composite parent ) {
                    super.createButtonsForButtonBar(parent);
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                }

                void handleModifyText( Text nameText ) {
                    final String newName = nameText.getText();
                    final boolean valid = (newName.length() > 0);
                    getButton(IDialogConstants.OK_ID).setEnabled(valid);
                    if (valid) {
                        NewNoteAction.this.newText = newName;
                    }
                }
            };

            if (dlg.open() == Window.OK && this.newText != null) {
                DiagramModelNode newNode = DrawingModelFactory.createModelNode(DrawingConstants.TypeId.NOTE, diagramNode);

                if (newNode != null) {
                    if (newNode instanceof NoteModelNode) {
                        ((NoteModelNode)newNode).setUserString(newText);
                    }
                    diagramNode.addChild(newNode);
                    ((NoteModelNode)newNode).setInitialSize();
                }
            }
        }
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart thePart,
                                  ISelection theSelection ) {
        super.selectionChanged(thePart, theSelection);
    }
}
