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

package com.metamatrix.modeler.internal.dqp.ui.workspace;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;


/** 
 * @since 5.0
 */
public class ConnectorsViewDragAdapter extends DragSourceAdapter {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    protected ISelectionProvider selectionProvider;
    /** 
     * 
     * @since 5.0
     */
    public ConnectorsViewDragAdapter(ISelectionProvider theProvider) {
        super();
        selectionProvider = theProvider;
    }

    /**
     * This implementation of <code>dragStart</code> permits the drag operation to start.
     * For additional information see <code>DragSourceListener.dragStart</code>.
     */
    @Override
    public void dragStart(DragSourceEvent event){
        //System.out.println("ConnectorsViewDragAdapter.dragStart()");
    }
    /**
     * This implementation of <code>dragFinished</code> does nothing.
     * For additional information see <code>DragSourceListener.dragFinished</code>.
     */
    @Override
    public void dragFinished(DragSourceEvent event){
        //System.out.println("ConnectorsViewDragAdapter.dragFinished()");
    }
    /**
     * This implementation of <code>dragSetData</code> does nothing.
     * For additional information see <code>DragSourceListener.dragSetData</code>.
     */
    @Override
    public void dragSetData(DragSourceEvent event){
        //System.out.println("ConnectorsViewDragAdapter.dragSetData()");
    }
}
