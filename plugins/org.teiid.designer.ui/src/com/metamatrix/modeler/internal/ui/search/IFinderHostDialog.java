/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.search;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


/** 
 * @since 4.2
 */
public interface IFinderHostDialog {
    
    public Button getOkButton();    
    public Button getCancelButton();
    public void okPressed();
    public void cancelPressed();
    public void updateTheStatus( IStatus status );
    public Button createButton( Composite parent, int id, String label, boolean defaultButton );
    public int getReturnCode();
}

