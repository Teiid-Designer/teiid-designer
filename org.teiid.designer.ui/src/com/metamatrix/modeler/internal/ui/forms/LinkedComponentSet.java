/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/** A LinkedComponentSet is a way for one or more components that
  *  work on the same data value to be linked.
  * A ComponentCategory groups multiple LinkedComponentSets together.
  * @author PForhan
  */
public interface LinkedComponentSet extends Cloneable {
    /** Get the ID of this LCS.  Not user-visible. */
    public String getID();
    public void setCategory(ComponentCategory cat);
    public ComponentCategory getCategory();

    // GUI methods:
    public int     getControlCount();
    public void    addFormControls(Composite parent, FormToolkit ftk, int totalColumns);
    public void    setEditible(boolean enabled);

    // value methods:
    public boolean isUserSet();
    public void    setValue(Object o);
//    public Object  getValue();
    /** revert to the last set value or to defaults */
    public void    reset();
    
    // Update handling:
    public void    setMonitor(ComponentSetMonitor csl);
    
    public LinkedComponentSet  cloneSet();
}
