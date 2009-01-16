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

package com.metamatrix.ui.internal.widget;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A label that can be added to a toolbar.
 */
public class LabelContributionItem extends ControlContribution {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final int DEFAULT_WIDTH = 150;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private CLabel lbl = null;
    
    private int width = DEFAULT_WIDTH;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public LabelContributionItem() {
        super("LabelContributionItem");  //$NON-NLS-1$
    }
    
    public LabelContributionItem(int theWidth) {
        this();
        this.width = theWidth;
    }
    
    /* (non-Javadoc)  
     * @see org.eclipse.jface.action.ControlContribution#computeWidth(org.eclipse.swt.widgets.Control)
     */
    @Override
    protected int computeWidth(Control control) {
        return control.computeSize(this.width, SWT.DEFAULT, true).x;
    }

    /* (non-Javadoc)  
     * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createControl(Composite theParent) {
        this.lbl = new CLabel(theParent, SWT.CENTER);
        return this.lbl;
    }
    
    /**
     * Sets the label text. 
     * @param theText the text
     * @since 4.3
     */
    private void setText(String theText) {
        this.lbl.setText(theText);
    }
    
    /** 
     * @see org.eclipse.jface.action.ContributionItem#update(java.lang.String)
     * @since 4.3
     */
    @Override
    public void update(String theText) {
        setText(theText);
        update();
    }
}
