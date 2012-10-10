/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.pakkage.actions;

import java.util.List;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.diagram.ui.actions.DiagramAction;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.diagram.ui.util.directedit.DirectEditPart;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * @since 8.0
 */
public class RenameAction extends DiagramAction implements UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RenameAction.class);

    private static final String RENAME_LABEL_ID = "renameLabel"; //$NON-NLS-1$

    private static final String DIALOG_TITLE = getString("dialogTitle"); //$NON-NLS-1$

    private DiagramEditor diagramEditor;

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.0
     */
    static String getString( final String id,
                             final Object parameter ) {
        return Util.getString(I18N_PREFIX + id, parameter);
    }

    String name;
    private EAttribute nameAttr;

    /**
     * @since 4.0
     */
    public RenameAction() {
        super();
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        final EObject obj = (EObject)getSelectedObject();
        boolean wasRenamed = false;
        // We need to:
        // 1) get the current diagram editor
        // 2) using the selected object, get the edit part for it
        // 3) and call it's rename.....
        if (diagramEditor != null) {
            List seps = diagramEditor.getDiagramViewer().getSelectedEditParts();
            if (seps != null && !seps.isEmpty() && seps.size() == 1) {
                EditPart ep = (EditPart)seps.get(0);
                if (ep instanceof DirectEditPart && ((DiagramModelNode)ep.getModel()).getModelObject() == obj) {
                    ((DirectEditPart)ep).performDirectEdit();
                    wasRenamed = true;
                }
            }
        }

        if (!wasRenamed) renameWithDialog();
    }

    private void renameWithDialog() {
        final EObject obj = (EObject)getSelectedObject();
        String existingName = ModelerCore.getModelEditor().getName(obj);
        if (existingName != null) {
            final String oldName = existingName;

            final Dialog dlg = new Dialog(Display.getDefault().getActiveShell(), DIALOG_TITLE) {
                @Override
                protected Control createDialogArea( final Composite parent ) {
                    final Composite dlgPanel = (Composite)super.createDialogArea(parent);
                    WidgetFactory.createLabel(dlgPanel, getString(RENAME_LABEL_ID, oldName));
                    final Text nameText = WidgetFactory.createTextField(dlgPanel, GridData.FILL_HORIZONTAL, oldName);
                    nameText.setSelection(0, oldName.length());
                    nameText.addModifyListener(new ModifyListener() {
                        @Override
						public void modifyText( final ModifyEvent event ) {
                            handleModifyText(nameText);
                        }
                    });
                    return dlgPanel;
                }

                @Override
                protected void createButtonsForButtonBar( final Composite parent ) {
                    super.createButtonsForButtonBar(parent);
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                }

                void handleModifyText( Text nameText ) {
                    final String newName = nameText.getText();
                    final boolean valid = (newName.length() > 0 && !newName.equals(oldName));
                    getButton(IDialogConstants.OK_ID).setEnabled(valid);
                    if (valid) {
                        name = nameText.getText();
                    }
                }
            };

            if (dlg.open() == Window.OK) {
                ModelObjectUtilities.rename(obj, this.name, this);
            }
        }
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged( final IWorkbenchPart part,
                                  final ISelection selection ) {
        super.selectionChanged(part, selection);
        determineEnablement();
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    @Override
    public void selectionChanged( SelectionChangedEvent theEvent ) {
        super.selectionChanged(theEvent);
        determineEnablement();
    }

    /**
     * @since 4.0
     */
    protected EAttribute getNameAttribute() {
        return this.nameAttr;
    }

    /**
     * @since 4.0
     */
    protected void determineEnablement() {
        boolean enable = false;
        if (!isEmptySelection() && !isReadOnly() && canLegallyEditResource()) {
            if (SelectionUtilities.isSingleSelection(getSelection())) {
                final EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
                if (eObj != null) {
                    enable = (ModelerCore.getModelEditor().hasName(eObj) && ModelerCore.getModelEditor().getName(eObj) != null);
                }
            }
        }
        setEnabled(enable);
    }

    /**
     * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }

    /**
     * @param editor
     */
    public void setDiagramEditor( DiagramEditor editor ) {
        diagramEditor = editor;
    }

}
