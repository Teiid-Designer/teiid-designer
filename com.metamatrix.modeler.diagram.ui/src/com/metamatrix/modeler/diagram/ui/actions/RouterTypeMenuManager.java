/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.MenuManager;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;

/**
 * MenuManager desiged to create and maintain router type selection actions. This includes handling the update to the preferences.
 * 
 * @since 4.2
 */
public class RouterTypeMenuManager extends MenuManager implements DiagramUiConstants.LinkRouter {
    private static final String LABEL = DiagramUiConstants.Util.getString("LinkSelectionAction.label"); //$NON-NLS-1$
    private RouterTypeAction directAction;
    private RouterTypeAction orthogonalAction;
    private RouterTypeAction manualAction;
    private boolean singleLinkEdit = false;

    /**
     * @since 4.2
     */
    public RouterTypeMenuManager() {
        super(LABEL, "LinkSelectionAction"); //$NON-NLS-1$

        createMenu();
    }

    public void createMenu() {
        int currentIndex = DiagramEditorUtil.getCurrentDiagramRouterStyle();
        String actionString = DiagramUiConstants.Util.getString("LinkSelectionAction.direct"); //$NON-NLS-1$
        directAction = new RouterTypeAction(actionString, DIRECT, this);
        directAction.setToolTipText(actionString);
        directAction.setEnabled(true);
        directAction.setChecked(true);
        addActionToMenu(directAction);

        actionString = DiagramUiConstants.Util.getString("LinkSelectionAction.orthogonal"); //$NON-NLS-1$
        orthogonalAction = new RouterTypeAction(actionString, ORTHOGONAL, this);
        orthogonalAction.setToolTipText(actionString);
        orthogonalAction.setEnabled(true);
        orthogonalAction.setChecked(true);
        addActionToMenu(orthogonalAction);

        actionString = DiagramUiConstants.Util.getString("LinkSelectionAction.manual"); //$NON-NLS-1$
        manualAction = new RouterTypeAction(actionString, MANUAL, this);
        manualAction.setToolTipText(actionString);
        manualAction.setEnabled(true);
        manualAction.setChecked(true);
        addActionToMenu(manualAction);

        // don't know why, but all check boxes needed to be intialized to "true", then unchecked.
        if (currentIndex != MANUAL) manualAction.setChecked(false);
        if (currentIndex != ORTHOGONAL) orthogonalAction.setChecked(false);
        if (currentIndex != DIRECT) directAction.setChecked(false);
    }

    protected void addActionToMenu( Action action ) {
        ActionContributionItem item = new ActionContributionItem(action);
        this.add(item);
    }

    public void handleSelection( int index,
                                 boolean initialSet ) {
        switch (index) {
            case DIRECT: {
                directAction.setChecked(true);
                manualAction.setChecked(false);
                orthogonalAction.setChecked(false);
                DiagramEditorUtil.setCurrentDiagramRouterStyle(index);
            }
                break;
            case ORTHOGONAL: {
                orthogonalAction.setChecked(true);
                manualAction.setChecked(false);
                directAction.setChecked(false);
                DiagramEditorUtil.setCurrentDiagramRouterStyle(index);
            }
                break;

            case MANUAL: {
                manualAction.setChecked(true);
                directAction.setChecked(false);
                orthogonalAction.setChecked(false);
                DiagramEditorUtil.setCurrentDiagramRouterStyle(index);
            }
        }
    }

    public void setInitialSelection() {
        int index = DiagramEditorUtil.getCurrentDiagramRouterStyle();
        handleSelection(index, true);
    }

    /**
     * @return Returns the singleLinkEdit.
     * @since 4.2
     */
    public boolean isSingleLinkEdit() {
        return this.singleLinkEdit;
    }

    /**
     * @param singleLinkEdit The singleLinkEdit to set.
     * @since 4.2
     */
    public void setSingleLinkEdit( boolean singleLinkEdit ) {
        this.singleLinkEdit = singleLinkEdit;
    }

    public void setEnabled( boolean enabled ) {
        directAction.setEnabled(enabled);
        orthogonalAction.setEnabled(enabled);
        manualAction.setEnabled(enabled);
    }
}
