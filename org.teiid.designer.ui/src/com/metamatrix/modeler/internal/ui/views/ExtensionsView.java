/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.PropertySheet;

import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * ExtensionsView is a specialized PropertySheet for displaying Metamodel Extension properties is a seperate view from the normal
 * PropertySheet viewer.
 */
public class ExtensionsView extends PropertySheet {

    /**
     * Construct an instance of ExtenstionsView.
     */
    public ExtensionsView() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partActivated(IWorkbenchPart part) {
        // overridden because the base class method assumes it is the only PropertySheet in the workspace.
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        setPartName(UiConstants.Util.getString("ExtensionsViewer.title")); //$NON-NLS-1$
        
        getViewSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection sel ) {
        if (SelectionUtilities.isSingleSelection(sel) && SelectionUtilities.getSelectedEObject(sel) != null) {
            //swjTODO: get the Extension object for this EObject and display it's properties

        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        getViewSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
        
    }

}
