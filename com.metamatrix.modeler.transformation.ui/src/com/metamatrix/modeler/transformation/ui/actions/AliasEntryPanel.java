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

package com.metamatrix.modeler.transformation.ui.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * AliasEntryPanel
 */
public class AliasEntryPanel extends Composite implements ModifyListener {

    private static final int LABEL_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING; 
    private static final String LEADING_TEXT = UiConstants.Util.getString("AliasEntryPanel.leadingText"); //$NON-NLS-1$
    private static final String ALIAS_LABEL_TEXT = UiConstants.Util.getString("AliasEntryPanel.aliasLabel.text"); //$NON-NLS-1$
    private static final String SOURCE_LABEL_TEXT = UiConstants.Util.getString("AliasEntryPanel.tableText"); //$NON-NLS-1$
    private static final String NO_ALIAS_ENTERED = UiConstants.Util.getString("AliasEntryPanel.noAliasEntered"); //$NON-NLS-1$
    private static final String DUPLICATE_ALIAS_ENTERED = UiConstants.Util.getString("AliasEntryPanel.duplicateAliasEntered"); //$NON-NLS-1$

    private Text aliasText;
    private String tableString;
    private CLabel messageLabel;
    private Object sourceEObject;
    private Object transRoot;
    private AliasEntryDialog dlg;
    
    //============================================================
    // Constructors
    //============================================================
    /**
     * Constructor.
     * 
     * @param parent    Parent of this control
     */
    public AliasEntryPanel(AliasEntryDialog dlg, Composite parent, String tableName, Object transRoot, Object sourceEObject) {
        super(parent, SWT.NONE);
        this.tableString = tableName;
        this.transRoot = transRoot;
        this.sourceEObject = sourceEObject;
        this.dlg = dlg;
        init();
    }
    
    //============================================================
    // Instance methods
    //============================================================
    
    /**
     * Initialize the panel.
     */
    private void init( ) {
        //------------------------------        
        // Set layout for the Composite
        //------------------------------        
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        this.setLayoutData(gridData);
        
        WidgetFactory.createLabel(this,LABEL_GRID_STYLE,1,LEADING_TEXT);

        Composite aliasPanel = new Composite(this, SWT.NONE);
        GridLayout aliasLayout = new GridLayout();
        aliasPanel.setLayout(aliasLayout);
        aliasLayout.numColumns = 2;
        
        Label tableLabel = new Label(aliasPanel, SWT.NONE);
        tableLabel.setText(SOURCE_LABEL_TEXT + " "); //$NON-NLS-1$
        Label sourceLabel = new Label(aliasPanel, SWT.NONE);
        sourceLabel.setText(tableString); 
        
        Label aliasLabel = new Label(aliasPanel, SWT.NONE);
        aliasLabel.setText(ALIAS_LABEL_TEXT+ " "); //$NON-NLS-1$
        aliasText = WidgetFactory.createTextField(aliasPanel);
        aliasText.setTextLimit(50);
        aliasText.addModifyListener(this);
        GridData aliasTextGridData = new GridData();
        aliasText.setLayoutData(aliasTextGridData);        
        this.messageLabel = WidgetFactory.createLabel(this);
        this.messageLabel.setText(" "); //$NON-NLS-1$
        GridData messageData = new GridData(GridData.FILL_BOTH);
        messageData.grabExcessHorizontalSpace = true;
        messageData.grabExcessVerticalSpace = true;
        this.messageLabel.setLayoutData(messageData);
    }
    
    public String getAliasName() {
        return aliasText.getText(); 
    }
    
    

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     */
    public void modifyText(ModifyEvent e) {
        String text = this.aliasText.getText();
        if ((text == null) || (text.trim().equals(""))) { //$NON-NLS-1$
            setError(NO_ALIAS_ENTERED);
            dlg.setOkEnabled(false);
        } else if (!isValidAlias(text)) {
            setError(DUPLICATE_ALIAS_ENTERED);
            dlg.setOkEnabled(false);            
        } else {
            setError(null);
            dlg.setOkEnabled(true);            
        }
    }
    
    /**
     * @param text
     * @return
     */
    private boolean isValidAlias(String text) {
        return !TransformationHelper.containsSqlAliasName(this.transRoot, text, this.sourceEObject);
        
    }

    private void setError(String message) {
        if (message == null) {
            this.messageLabel.setImage(null);
        } else {
            Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            this.messageLabel.setImage(image);
        }
        this.messageLabel.setText(message);
        this.messageLabel.redraw();
    }
}
