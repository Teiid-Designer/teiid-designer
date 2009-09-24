/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.util;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;



/** 
 * @since 4.3
 */
public class NoOpDropTargetAdapter extends DropTargetAdapter {
    
    private static NoOpDropTargetAdapter adapter = new NoOpDropTargetAdapter();
    
    public static DropTargetAdapter getInstance() {
        return adapter;
    }
    
    /** 
     * 
     * @since 4.3
     */
    public NoOpDropTargetAdapter() {
        super();
    }

    /** 
     * @see org.eclipse.swt.dnd.DropTargetAdapter#drop(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    @Override
    public void drop(DropTargetEvent event) {
        // nothing, don't drop
    }

    /** 
     * @see org.eclipse.swt.dnd.DropTargetAdapter#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    @Override
    public void dropAccept(DropTargetEvent event) {
        // nothing;
    }


}
