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

package com.metamatrix.modeler.internal.dqp.ui.views;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.internal.dqp.ui.actions.CopyXmlResultsToClipboardAction;
import com.metamatrix.modeler.internal.dqp.ui.actions.SaveXmlResultsToFileAction;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResultsProvider;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.XmlDocumentResultsModel;
import com.metamatrix.ui.internal.widget.LabelContributionItem;


/** 
 * @since 4.3
 */
public final class XmlDocumentSqlResultsView extends AbstractResultsView implements IPropertyChangeListener, IResultsProvider {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(XmlDocumentSqlResultsView.class);
    
    private static String getI18nString(String key) {
        return UTIL.getString(PREFIX + key);
    }
    
    private static String getI18nString(String key, Object value) {
        return UTIL.getString(PREFIX + key, value);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private IAction copyAction;
    private SaveXmlResultsToFileAction saveToFileAction;
    
    private LabelContributionItem lblRowCount;
    
    private long maxXmlDocLength = 10000;
    
    private StyledText xmlResultsText;
    
    private boolean success = false; // indicates action completed successfully
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#createResultsControl(org.eclipse.swt.widgets.Composite, com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults)
     * @since 4.3
     */
    @Override
    protected Control createResultsControl(Composite theParent,
                                           IResults theResults) {
        ArgCheck.isInstanceOf(XmlDocumentResultsModel.class, theResults);
        
        XmlDocumentResultsModel resultsModel = (XmlDocumentResultsModel)theResults;
        
        xmlResultsText =  new StyledText(theParent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        xmlResultsText.setLayoutData(new GridData(GridData.FILL_BOTH));

        // construct the data
        String text = theResults.getStatus().getMessage();
        IPreferenceStore store = SQLExplorerPlugin.getDefault().getPreferenceStore();
        maxXmlDocLength = store.getLong(IConstants.XML_CHAR_LIMIT);
        
        if (theResults.getStatus().getSeverity() != IStatus.ERROR) {
            int confirmation = confirmDisplayAllXmlResults(resultsModel);
            
            switch (confirmation) {
                case 0: { // RESULTS ARE TOO LARGE AND USER WANTS TO SAVE TO FILE
                    this.success = false;

                    // run save action
                    this.saveToFileAction.run();

                    // this.success gets set by the property listener method
                    if (this.success) {
                        text = getI18nString("tooLargeDocumentSaved") + StringUtil.Constants.DBL_SPACE + this.saveToFileAction.getFileName(); //$NON-NLS-1$
                    } else {
                        text = getI18nString("tooLargeDocumentSaveCancelled"); //$NON-NLS-1$
                    }
                } break;
                
                case 2: {
                    text = getI18nString("tooLargeDisplayCancelled"); //$NON-NLS-1$
                } break;

                default: {
                    text = resultsModel.getResultsAsText();
                }
            }
            
            // store results model
            xmlResultsText.setData(resultsModel);
        }
        
        xmlResultsText.setText(text.toString());
        xmlResultsText.setMenu(createMenu(xmlResultsText));

        return xmlResultsText;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResultsProvider#getResults()
     * @since 5.5
     */
    public IResults getResults() {
        Control c = getSelectedResultsControl();
        
        if ((c != null) && (c.getData() != null) && (c.getData() instanceof XmlDocumentResultsModel)) {
            return (IResults)c.getData();
        }

        return null;
    }
    
    @Override
    protected IAction getSaveToFileAction() {
        if (this.saveToFileAction == null) {
            this.saveToFileAction = new SaveXmlResultsToFileAction(this);
            this.saveToFileAction.addPropertyChangeListener(this);
        }
        
        return saveToFileAction;
    }
    
    /**
     * Creates a context menu with the copy action. 
     * @param theControl the control whose context menu is being created
     * @return the menu
     * @since 5.0
     */
    private Menu createMenu(Control theControl) {
        MenuManager mgr = new MenuManager();
        mgr.add(this.copyAction);
        mgr.add(this.saveToFileAction);
        Menu menu = mgr.createContextMenu(theControl);
        
        return menu;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#fillActionBars(org.eclipse.ui.IActionBars)
     * @since 4.3
     */
    @Override
    protected void fillActionBars(IActionBars theActionBars) {
        IToolBarManager toolBarMgr = theActionBars.getToolBarManager();

        //
        // row count label
        //
        
        this.lblRowCount = new LabelContributionItem();
        toolBarMgr.add(this.lblRowCount);
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#getCopyResultsAction()
     * @since 4.3
     */
    @Override
    protected IAction getCopyResultsAction() {
        if (this.copyAction == null) {
            this.copyAction = new CopyXmlResultsToClipboardAction(this, this);
        }

        return this.copyAction;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#getLocalizationKeyPrefix()
     * @since 4.3
     */
    @Override
    protected String getLocalizationKeyPrefix() {
        return PREFIX;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#handleGetResultsViewPartId()
     * @since 4.3
     */
    @Override
    protected String handleGetResultsViewPartId() {
        return Extensions.XML_DOC_SQL_RESULTS_VIEW;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#handleResultSelected(org.eclipse.swt.widgets.Control)
     * @since 4.3
     */
    @Override
    protected void handleResultSelected(Control theControl) {
        updateState();
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.views.AbstractResultsView#updateState()
     * @since 4.3
     */
    @Override
    protected void updateState() {
        super.updateState();

        boolean enable = false;
        Control c = getSelectedResultsControl();
        
        if ((c != null) && (c.getData() != null) && (c.getData() instanceof XmlDocumentResultsModel)) {
            enable = true;
        }

        // update row count label
        String msg = ""; //$NON-NLS-1$

        if (enable) {
            XmlDocumentResultsModel model = (XmlDocumentResultsModel)c.getData();
            
            if (model.getStatus().getSeverity() != IStatus.ERROR) {
                msg = UTIL.getString(PREFIX + "recordCount", new Object[] {Integer.toString(model.getResults().length), //$NON-NLS-1$
                                                                           Integer.toString(model.getTotalDocumentCount())});
            }
            
            // don't enable action if nothing to copy
            if (model.getTotalDocumentCount() == 0) {
                enable = false;
            }
        }
        
        this.saveToFileAction.setEnabled(true);
        // update copy action state
        this.copyAction.setEnabled(enable);
        
        // update row count label
        this.lblRowCount.update(msg);
    }
    
    private int confirmDisplayAllXmlResults(XmlDocumentResultsModel xmlModel) {
        int okToDisplay = Window.CANCEL;
        
        String[] xmlDocs = xmlModel.getResults();
        long totalStringLength = 0;
        for (int i = 0; i < xmlDocs.length; ++i) {
            totalStringLength += xmlDocs[i].length();
        }
        
        if( totalStringLength < maxXmlDocLength ) {
            // Now display to user if they
        } else {
            okToDisplay = Window.OK;
            String title = getI18nString("confirmDisplayResults.title");  //$NON-NLS-1$
            String line_1 = getI18nString("confirmDisplayResults.line_1", Long.toString(maxXmlDocLength));  //$NON-NLS-1$
            String line_2 = getI18nString("confirmDisplayResults.line_2");  //$NON-NLS-1$
            String line_3 = getI18nString("confirmDisplayResults.line_3");  //$NON-NLS-1$
            String line_4 = getI18nString("confirmDisplayResults.line_4");  //$NON-NLS-1$
            
            String message = line_1 + line_2 + line_3 + line_4;
            
            okToDisplay = openCancelableQuestion(getViewSite().getShell(), title, message);
            
        }
        
        return okToDisplay;
    }
    

    private int openCancelableQuestion(Shell parent, String title,
            String message) {
        MessageDialog dialog = new MessageDialog(parent, title, null, // accept
                // the
                // default
                // window
                // icon
                message, MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL,
                        IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0); // yes is the default
        return dialog.open();
    }

    /** 
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     * @since 5.5.3
     */
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(IAction.RESULT)) {
            this.success = event.getNewValue().equals(Boolean.FALSE);
        }
    }
}
