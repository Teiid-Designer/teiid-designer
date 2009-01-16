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

package com.metamatrix.modeler.internal.webservice.ui.war.wizards;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.dqp.ui.config.ConnectorBindingsPanel;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditingContext;


/** 
 * @since 4.3
 */
public class ShowConnectorBindingsDialog extends TitleAreaDialog implements InternalModelerLdsUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ShowConnectorBindingsDialog.class);
    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$    
    
    private File theVdb;
    private VdbEditingContext theVdbContext;    
    private VdbContextEditor theVdbContextEditor;    
    
//  location/size configuration
    private IDialogSettings dialogSettings;
    private Point dialogLocation;
    private Point dialogSize;
    
    private Point defaultLocation;
    private int DEFAULT_WIDTH = 550;
    private int DEFAULT_HEIGHT = 650;
    
    /**
     *  
     * @param theShell
     * @param theVdb
     * @param theVdbContext
     * @since 4.3
     */
    public ShowConnectorBindingsDialog(Shell theShell, 
                                       File theVdb,
                                       VdbEditingContext theVdbContext,
                                       Point defaultLocation) {
        super(theShell);      
        this.setShellStyle(getShellStyle() | SWT.RESIZE);        
        
        ImageDescriptor id = WebServiceUiPlugin.getDefault().
                getImageDescriptor(InternalModelerLdsUiConstants.WebServicesImages.WAR_FILE_ICON);
        if( id != null )
            setDefaultImage(id.createImage());
        
        
        this.theVdb = theVdb;
        this.theVdbContext = theVdbContext;
        this.theVdbContextEditor = null;
        this.defaultLocation = defaultLocation;
        readConfiguration();
    }
    public ShowConnectorBindingsDialog(Shell theShell, 
                                       File theVdb,
                                       VdbContextEditor theVdbContext,
                                       Point defaultLocation) {
        super(theShell);      
        this.setShellStyle(getShellStyle() | SWT.RESIZE);        
        
        ImageDescriptor id = WebServiceUiPlugin.getDefault().
                getImageDescriptor(InternalModelerLdsUiConstants.WebServicesImages.WAR_FILE_ICON);
        if( id != null )
            setDefaultImage(id.createImage());
        
        
        this.theVdb = theVdb;
        this.theVdbContext = null;
        this.theVdbContextEditor = theVdbContext;
        this.defaultLocation = defaultLocation;
        readConfiguration();
    }
    
    /**
     *  
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     * @since 4.3
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(TITLE); 
        
        // dialog location
        if (dialogLocation != null)
            shell.setLocation(dialogLocation);
        else {
            defaultLocation.x += 30;
            defaultLocation.y -= 30;
            shell.setLocation(defaultLocation);
        }
        
        // dialog size
        if (dialogSize != null)
            shell.setSize(dialogSize);
        else
            shell.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
    }
    
    /**
     *  
     * @param id
     * @return
     * @since 4.3
     */
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }
    
    /**
     *  
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    protected Control createDialogArea(Composite theParent) {
        
        Composite bindingsPanel = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH, 1);
        
        GridLayout layout = new GridLayout();
        bindingsPanel.setLayout(layout);        
        layout.numColumns = 1;   

        createConnectorBindingArea(bindingsPanel);        
        createLink(bindingsPanel);
        
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);        
        gridData.minimumWidth = 550;
        gridData.minimumHeight = 350;
        gridData.heightHint = 450;
        
        bindingsPanel.setLayoutData(gridData);
        
        ImageDescriptor id = WebServiceUiPlugin.getDefault().
                                    getImageDescriptor(InternalModelerLdsUiConstants.WebServicesImages.WAR_FILE_ICON);
        if( id != null )
            this.setTitleImage(id.createImage());

        this.setMessage(INITIAL_MESSAGE);        
        
        return bindingsPanel;
    }
    
    /**
     *  
     * @param parent
     * @return
     * @since 4.3
     */
    protected ConnectorBindingsPanel createConnectorBindingArea(Composite parent) {
          
        ConnectorBindingsPanel pnlBindings = null;           
        try{
            if (this.theVdbContext != null) {
                pnlBindings = new ConnectorBindingsPanel(parent, this.theVdb, this.theVdbContext);              
            } else if (this.theVdbContextEditor != null) {
                pnlBindings = new ConnectorBindingsPanel(parent, this.theVdb, this.theVdbContextEditor);              
            }
            pnlBindings.setReadonly(true);
            pnlBindings.setSaveOnChange(false);
            pnlBindings.setFocus();              
        }catch(Exception err) {
            Util.log(err);
        }
          
        return pnlBindings; 
    }
    
    /**
     *  
     * @param pnlMain
     * @since 4.3
     */
    private void createLink(final Composite pnlMain) {
        
        FormToolkit toolkit = WebServiceUiPlugin.getDefault().getFormToolkit(pnlMain.getDisplay());
        toolkit.setBackground(pnlMain.getBackground());
          
        final Hyperlink importLink = toolkit.createHyperlink(pnlMain, getString("editConnectorBindingLinkText"), SWT.WRAP); //$NON-NLS-1$
        GridData iLinkGD = new GridData(SWT.BEGINNING, SWT.FILL, true, false);
        iLinkGD.minimumWidth  = 50;
        iLinkGD.minimumHeight = 50;
        importLink.setLayoutData(iLinkGD);
        
        final Color bkgdColor = pnlMain.getBackground();
        importLink.setBackground(bkgdColor);
        importLink.addHyperlinkListener(new HyperlinkAdapter() {

            @Override
            public void linkActivated(HyperlinkEvent e) {
                displayBindingsEditorPage();                
            }
            
            @Override
            public void linkEntered(HyperlinkEvent e) {
                importLink.setBackground(bkgdColor);
                importLink.update();
            }
            @Override
            public void linkExited(HyperlinkEvent e) {
                importLink.setBackground(bkgdColor);
                importLink.update();
            }
        });
    }
    
    void displayBindingsEditorPage() {
        storeSettings();
        super.okPressed();
    }
    
    /**
     *  
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, InternalModelerLdsUiConstants.CLOSE, false);
    }
    
    /**
     *  
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     * @since 4.3
     */
    @Override
    protected void cancelPressed() {
        //Since we do not process anything unless and until OK is pressed, there is nothing
        //to do here
        storeSettings();
        super.cancelPressed();
    }       
    
//  --------------- configuration handling --------------
    /**
     * Stores the current state in the dialog settings.
     * 
     * @since 2.0
     */
    private void storeSettings() {
        writeConfiguration();
    }

    /**
     * Returns the dialog settings object used to share state between several
     * event detail dialogs.
     * 
     * @return the dialog settings to be used
     */
    private IDialogSettings getDialogSettings() {
        IDialogSettings settings = WebServiceUiPlugin.getDefault().getDialogSettings();
        dialogSettings = settings.getSection(getClass().getName());
        if (dialogSettings == null)
            dialogSettings = settings.addNewSection(getClass().getName());
        return dialogSettings;
    }

    /**
     * Initializes itself from the dialog settings with the same state as at the
     * previous invocation.
     */
    private void readConfiguration() {
        IDialogSettings s = getDialogSettings();
        try {
            int x = s.getInt("x"); //$NON-NLS-1$
            int y = s.getInt("y"); //$NON-NLS-1$
            dialogLocation = new Point(x, y);
            x = s.getInt("width"); //$NON-NLS-1$
            y = s.getInt("height"); //$NON-NLS-1$
            dialogSize = new Point(x, y);
        } catch (NumberFormatException e) {
            dialogLocation = null;
            dialogSize = null;
        }
    }

    private void writeConfiguration() {
        IDialogSettings s = getDialogSettings();
        Point location = getShell().getLocation();
        s.put("x", location.x); //$NON-NLS-1$
        s.put("y", location.y); //$NON-NLS-1$
        Point size = getShell().getSize();
        s.put("width", size.x); //$NON-NLS-1$
        s.put("height", size.y); //$NON-NLS-1$
    }
}
