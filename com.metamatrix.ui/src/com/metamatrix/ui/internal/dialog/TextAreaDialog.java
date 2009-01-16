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

package com.metamatrix.ui.internal.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.text.ScaledFontManager;


/**
 * A dialog that displays text in a {@link org.eclipse.swt.custom.StyledText} widget. Dialog is
 * centered on the screen.
 * @since 4.4
 */
public class TextAreaDialog extends Dialog {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private String content;
    
    private String headerText;
    
    private boolean editable;
    
    private int fontSize = 10;
    
    private boolean resizable = true;
    
    private int tabWidth = 4;
    
    private StyledText txtArea;
    
    private boolean wordWrap;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public TextAreaDialog(Shell theShell,
                          String theTitle,
                          String theContent) {
        super(theShell, theTitle);
        this.content = theContent;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public TextAreaDialog(Shell theShell,
                          String theTitle,
                          String theContent,
                          String theHeader) {
        this(theShell, theTitle, theContent);
        this.headerText = theHeader;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite theParent) {
        Composite composite = (Composite)super.createDialogArea(theParent);
        
        if( this.headerText != null ) {
            StyledText txtInstructions = new StyledText(composite, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI);
            txtInstructions.setText(headerText); 
            txtInstructions.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            txtInstructions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
        
        
        int style = (this.wordWrap ? SWT.V_SCROLL : SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        this.txtArea = new StyledText(composite, style);
        this.txtArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.txtArea.setEditable(this.editable);
        this.txtArea.setWordWrap(this.wordWrap);
        this.txtArea.setTabs(this.tabWidth);
        this.txtArea.setText(this.content);
        this.txtArea.setFont(new ScaledFontManager().createFontOfSize(this.fontSize));

        StyleRange bodyRange = new StyleRange();
        bodyRange.start = 0;
        bodyRange.length = this.content.length();
        this.txtArea.setStyleRange(bodyRange);
        
        // set default size and location
        setSizeRelativeToScreen(75, 70);
        setCenterOnDisplay(true);
        
        return composite;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        if (this.resizable) {
            setShellStyle(getShellStyle() | SWT.RESIZE);
        }
        
        super.create();
        super.getShell().setText(getTitle());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button okButton = createButton(
            parent,
            IDialogConstants.OK_ID,
            IDialogConstants.OK_LABEL,
            true);
        okButton.setFocus();
    }
    
    /**
     * Obtains the dialog content. 
     * @return the content
     * @since 4.4
     */
    public String getContent() {
        return this.txtArea.getText();
    }
    
    /**
     * Sets the dialog content to be editable or not. 
     * @param theEditableFlag the flag indicating if the content is editable
     * @since 4.4
     */
    public void setEditable(boolean theEditableFlag) {
        this.editable = theEditableFlag;
    }
    
    /**
     * Sets the dialog content font size. 
     * @param theSize the size
     * @since 4.4
     */
    public void setFontSize(int theSize) {
        this.fontSize = theSize;
    }
    
    /**
     * Sets the dialog to be resizable or not. 
     * @param theResizableFlag the flag indicating if the dialog is resizable
     * @since 4.4
     */
    public void setResizable(boolean theResizableFlag) {
        this.resizable = theResizableFlag;
    }
    
    /**
     * Sets the dialog content tab width. 
     * @param theWidth the width
     * @since 4.4
     */
    public void setTabWidth(int theWidth) {
        this.tabWidth = theWidth;
    }
    
    /**
     * Sets the dialog content to word wrap or not. 
     * @param theWordWrapFlag the flag indicating if the dialog content is word wrapped
     * @since 4.4
     */
    public void setWordWrap(boolean theWordWrapFlag) {
        this.wordWrap = theWordWrapFlag;
    }
    
}
