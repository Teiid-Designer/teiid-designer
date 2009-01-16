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

import org.eclipse.swt.widgets.Shell;

/** Provides an abstraction from a Dialog that allows a unified
  *  interface to multiple dialogs as well as delayed construction.
  *  
  * @author PForhan
  */
public interface DialogProvider {
    public void showDialog(Shell shell, Object initialValue);
    public boolean wasCancelled();
    public Object getValue();
    public String getLaunchButtonText();
}
