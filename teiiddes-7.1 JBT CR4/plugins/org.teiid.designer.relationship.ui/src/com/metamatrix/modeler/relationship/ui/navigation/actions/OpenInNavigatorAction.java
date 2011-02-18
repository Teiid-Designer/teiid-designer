/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.metamatrix.modeler.internal.ui.actions.ModelObjectAction;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationView;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * OpenInNavigatorAction is the global action for opening the selected EObject in the
 * Navigator View.
 */
public class OpenInNavigatorAction extends ModelObjectAction {

    /**
     * Construct an instance of NewCustomDiagramAction.
     */
    public OpenInNavigatorAction() {
        super(UiPlugin.getDefault());
        setText(UiConstants.Util.getString("OpenInNavigatorAction.label")); //$NON-NLS-1$
        setImageDescriptor(
        		UiPlugin.imageDescriptorFromPlugin(UiConstants.PLUGIN_ID, PluginConstants.Images.RELATIONSHIP_NAVIGATOR_ICON));
    }

    /**
     * Construct an instance of NewCustomDiagramAction.
     * 
     * @param theStyle
     */
    public OpenInNavigatorAction( int theStyle ) {
        super(UiPlugin.getDefault(), theStyle);
    }

    @Override
    public void doRun() {
        EObject obj = SelectionUtilities.getSelectedEObject(getSelection());
        try {
            IViewPart viewPart = UiUtil.getWorkbenchPage().showView(UiConstants.Extensions.Navigator.VIEW_ID);
            if ( viewPart instanceof NavigationView ) {
                ((NavigationView) viewPart).setCurrentObject(obj);
            }
        } catch (final PartInitException err) {
            UiConstants.Util.log(err);
            WidgetUtil.showError(err.getLocalizedMessage());
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
     * @since 4.0
     */
    private void determineEnablement() {
        boolean enable = false;
        
        if ( SelectionUtilities.isSingleSelection(getSelection()) ) {
            if ( SelectionUtilities.getSelectedEObject(getSelection()) != null ) {
                enable = true;
            }
        }

        setEnabled(enable);
    }

	@Override
	protected boolean requiresEditorForRun() {
		// TODO Auto-generated method stub
		return false;
	}
}
