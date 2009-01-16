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

package com.metamatrix.modeler.internal.ui.forms;

import java.util.EventObject;

public class ComponentSetEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    //    public final ComponentCategory category;
    public final Object value;
    public final LinkedComponentSet componentSet;
    public final boolean isDelete;
    
    /** Means for someone to tell a LinkedComponentSet that this event is invalid
      *  and its results should be discarded.  Currently only obeyed by FormTextComponentSet.
      */
    public boolean doit = true;

    //
    // Constructors:
    //
    public ComponentSetEvent(LinkedComponentSet lcs, boolean delete, Object newValue) {
        super(lcs);
//        category = cc;
        componentSet = lcs;
        value = newValue;
        isDelete = delete;
    }
    
    //
    // Overrides:
    //
    @Override
    public String toString() {
        return "ComponentSetEvent: source-id="+componentSet.getID()+"; del="+isDelete+"; newValue="+value;  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }
}
