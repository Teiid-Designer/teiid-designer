/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationView;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * SetFocusAction is an action for the NavigationView to use to change the focused object inside
 * the view.  It is NOT the global action for sending an object in the workbench down into the
 * NavigationView.
 */
public class SetFocusAction extends Action implements ISelectionListener, UiConstants.Images {

    private static final String LABEL = UiConstants.Util.getString("SetFocusAction.label"); //$NON-NLS-1$
    private static final String TOOLTIP = UiConstants.Util.getString("SetFocusAction.tooltip"); //$NON-NLS-1$

    private NavigationView viewer;
    private NavigationNode selectedNode;

    /**
     * Construct an instance of SetFocusAction.
     * 
     */
    public SetFocusAction(NavigationView viewer) {
        this.viewer= viewer;
        setText(LABEL);
        setToolTipText(TOOLTIP);

        setDisabledImageDescriptor(UiPlugin.getDefault().getImageDescriptor(FOCUS_D)); 
        setHoverImageDescriptor(UiPlugin.getDefault().getImageDescriptor(FOCUS_C));
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(FOCUS_E));

        setEnabled(false);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged(IWorkbenchPart thePart, ISelection selection) {
        setEnabled(false);
        if ( SelectionUtilities.isSingleSelection(selection) ) {
            Object selectedObject = SelectionUtilities.getSelectedObject(selection);
            if ( selectedObject instanceof NavigationNode ) {
                this.selectedNode = ((NavigationNode) selectedObject);
                if ( ! selectedNode.equals(viewer.getCurrentNavigationContext().getFocusNode()) ) {
                    setEnabled(true);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        this.viewer.setCurrentObject(selectedNode);
    }

}
