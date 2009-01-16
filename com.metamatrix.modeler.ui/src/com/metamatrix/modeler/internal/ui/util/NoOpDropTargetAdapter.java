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
