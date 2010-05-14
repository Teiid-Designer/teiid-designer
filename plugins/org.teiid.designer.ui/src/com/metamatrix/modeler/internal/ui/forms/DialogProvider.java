/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
