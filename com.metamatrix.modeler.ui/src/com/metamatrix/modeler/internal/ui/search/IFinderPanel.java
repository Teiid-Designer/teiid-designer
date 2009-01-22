/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.search;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;


/** 
 * @since 4.2
 */
public interface IFinderPanel {

    public void createButtonsForButtonBar( Composite parent );
    public void handleOkPressed();
    public void handleCancelPressed();
    public Object[] getResult();
    public String getTitle();
    public void updateOKStatus();
    public void setValidator( ISelectionStatusValidator validator );
    
}
