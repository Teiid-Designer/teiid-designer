/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.relationship.NavigationLink;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * OpenWrapperAction is a wrapper class for the global Open action in the NavigatorView.  
 * It is necessary because the NavigationView does not fire selection events to the
 * Workbench where the global action could hear it.
 */
public class OpenWrapperAction extends Action implements ISelectionListener {

    private static final String LABEL = UiConstants.Util.getString("OpenWrapperAction.label"); //$NON-NLS-1$
    private static final String TOOLTIP = UiConstants.Util.getString("OpenWrapperAction.tooltip"); //$NON-NLS-1$

    private URI selectedURI;
    
    /**
     * Construct an instance of OpenWrapperAction.
     */
    public OpenWrapperAction() {
        super();
        setText(LABEL);
        setToolTipText(TOOLTIP);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        
        boolean enable = false;
        if ( SelectionUtilities.isSingleSelection(theSelection) ) {
            Object obj = SelectionUtilities.getSelectedObject(theSelection);
            if ( obj instanceof NavigationNode ) {
                selectedURI = ((NavigationNode) obj).getModelObjectUri();
                enable = true;
            } else if ( obj instanceof NavigationLink ) {
                selectedURI = ((NavigationLink) obj).getModelObjectUri();
                enable = true;
            }
        }
        setEnabled(enable);
    }

    @Override
    public void run() {
        Container container;
        try {
            container = ModelerCore.getModelContainer();
            EObject selectedObject = container.getEObject(selectedURI, true);
            ModelEditorManager.open(selectedObject, true);
        } catch (CoreException e) {
            UiConstants.Util.log(e);
            WidgetUtil.showError(e.getLocalizedMessage());
        }
    }

}
