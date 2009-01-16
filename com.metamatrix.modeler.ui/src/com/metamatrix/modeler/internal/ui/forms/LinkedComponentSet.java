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
