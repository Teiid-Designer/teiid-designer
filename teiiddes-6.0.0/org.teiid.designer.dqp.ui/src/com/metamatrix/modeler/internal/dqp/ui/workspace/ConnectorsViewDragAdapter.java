/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
