/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
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
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.ui.viewsupport.DiagramHelperManager;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * @since 8.0
 */
public class RenameAction extends ModelObjectAction implements UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RenameAction.class);

    private static final String RENAME_LABEL_ID = "renameLabel"; //$NON-NLS-1$
    private static final String RENAME_NULL_LABEL_ID = "renameNullLabel"; //$NON-NLS-1$

    private static final String DIALOG_TITLE = getString("dialogTitle"); //$NON-NLS-1$

    /**
     * @since 4.0
     */
    static String getString( final String id ) {
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
        super(UiPlugin.getDefault());
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {
        final EObject obj = (EObject)getSelectedObject();
        final String oldName = ModelerCore.getModelEditor().getName(obj);
        final Dialog dlg = new Dialog(Display.getDefault().getActiveShell(), DIALOG_TITLE) {
            @Override
            protected Control createDialogArea( final Composite parent ) {
                final Composite dlgPanel = (Composite)super.createDialogArea(parent);
                if (oldName == null) {
                    WidgetFactory.createLabel(dlgPanel, getString(RENAME_NULL_LABEL_ID));
                } else {
                    WidgetFactory.createLabel(dlgPanel, getString(RENAME_LABEL_ID, oldName));
                }
                final Text nameText = WidgetFactory.createTextField(dlgPanel, GridData.FILL_HORIZONTAL, oldName);
                if (oldName != null) {
                    nameText.setSelection(0, oldName.length());
                }
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
                    if (eObj instanceof Diagram) {
                        enable = DiagramHelperManager.canRename((Diagram)eObj);
                    } else if (eObj instanceof ModelImport) {
                        enable = false;
                    } else {
                        enable = ModelerCore.getModelEditor().hasName(eObj);
                    }
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

}
