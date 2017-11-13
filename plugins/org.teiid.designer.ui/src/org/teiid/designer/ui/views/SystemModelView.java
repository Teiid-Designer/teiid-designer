/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.ui.viewsupport.SystemModelTreeViewer;


/** 
 * @since 8.0
 */
public class SystemModelView extends ModelerView {

    
    private TreeViewer treeViewer;
    
    
    /**
     * Construct a SystemModelView for the Modeler
     * @since 4.3
     */
    public SystemModelView() {
        super();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    public void createPartControl(final Composite parent) {

        super.createPartControl(parent);
        
        treeViewer = new SystemModelTreeViewer(parent);

        // hook up this view's selection provider to this site
        getViewSite().setSelectionProvider(treeViewer);
        
        treeViewer.expandToLevel(2);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 4.3
     */
    @Override
    public void setFocus() {
        if ( treeViewer != null && ! treeViewer.getTree().isDisposed() ) {
            treeViewer.getTree().setFocus();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
    }    

}
