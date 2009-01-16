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

package com.metamatrix.modeler.internal.ui.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.modeler.internal.ui.viewsupport.SystemModelTreeViewer;


/** 
 * @since 4.3
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

        
        // hook up our status bar manager for EObject selection inside this view
        treeViewer.addSelectionChangedListener(getStatusBarListener());

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
