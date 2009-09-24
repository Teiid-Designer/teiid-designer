/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
