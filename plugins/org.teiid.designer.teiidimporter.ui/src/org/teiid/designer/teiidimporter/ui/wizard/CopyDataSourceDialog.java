/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.teiidimporter.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.ui.util.JndiNameHelper;

/**
 *  CopyDataSourceDialog
 *  This Dialog handles Copy of a DataSource
 */
public class CopyDataSourceDialog extends TitleAreaDialog implements Listener, UiConstants {

    private Text dataSourceNameText;
    private String newSourceName;
    private Collection<String> existingSourceNames = new ArrayList<String>();
    private JndiNameHelper nameValidator;

    /**
     * CreateDataSourceDialog constructor
     * @param shell the shell
     * @param teiidImportServer the TeiidImportServer
     */
    public CopyDataSourceDialog(Shell shell, ITeiidImportServer teiidImportServer) {
        super(shell);
        initExistingDataSourceNames(teiidImportServer);
        this.nameValidator = new JndiNameHelper();
    }
        
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.copyDataSourceDialog_title); 
    }
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createButtonBar( Composite parent ) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(OK).setEnabled(false);

        return buttonBar;
    }
   
    /**
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite pnl = new Composite(parent, SWT.NONE);
        pnl.setLayout(new GridLayout(1, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createDataSourceNamePanel(pnl);

        // set title and initial message
        setTitle(Messages.copyDataSourceDialog_title);
        setErrorMessage(Messages.copyDataSourceDialogErrorNameEmpty);

        return pnl;
    }
    
    /*
     * Data Source Name Panel
     * @param parent the parent Composite
     */
    private void createDataSourceNamePanel(Composite parent) {
        // -------------------------------------
        // DataSource Name
        // -------------------------------------
        Composite namePanel = new Composite(parent,SWT.NONE);
        namePanel.setLayout(new GridLayout(2, false));
        namePanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label dsNameLabel = new Label(namePanel,SWT.NONE);
        dsNameLabel.setText(Messages.createDataSourcePanel_name);
        
        dataSourceNameText = new Text(namePanel, SWT.BORDER | SWT.SINGLE);
        dataSourceNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dataSourceNameText.addListener(SWT.Modify, this);
    }
    
    /*
     * Initialize the list of existing data sources
     * @param teiidServer the TeiidServer
     */
    private void initExistingDataSourceNames(ITeiidImportServer teiidImportServer) {
        existingSourceNames.clear();
        try {
            Collection<ITeiidDataSource> sources = teiidImportServer.getDataSources();
            for(ITeiidDataSource source : sources) {
                existingSourceNames.add(source.getName());
            }
        } catch (Exception ex) {
            UTIL.log(ex);
        }
    }

    @Override
    public void handleEvent( Event event ) {
        updateStatus();
    }

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the name of the DataSource (never <code>null</code> or empty when OK button has been pressed)
     */
    public String getNewDataSourceName() {
        assert (getReturnCode() == Window.OK);
        return this.newSourceName;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#getShellStyle()
     */
    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    /**
     * Update the Status
     */
    private void updateStatus() {
        this.newSourceName=this.dataSourceNameText.getText();
        
        // Validate the Name
        IStatus status = validateName();
        
        if(status.isOK()) {
            getButton(OK).setEnabled(true);
            setErrorMessage(null);
            setMessage(status.getMessage());
        } else {
            getButton(OK).setEnabled(false);
            setErrorMessage(status.getMessage());
        }
    }
    
    /*
     * Validate the DataSource name
     * @return the name status
     */
    private IStatus validateName() {
        // Check if null or empty
        if(CoreStringUtil.isEmpty(newSourceName)) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Messages.copyDataSourceDialogErrorNameEmpty);
        }
        
        // For new Source, cannot duplicate name
        if(existingSourceNames.contains(newSourceName)) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Messages.copyDataSourceDialogErrorNameExists);
        }
        
        // Check for invalid chars
        String status = nameValidator.checkValidName(newSourceName);
        
        // Check if null or empty
        if(status != null ) {
            return new Status(IStatus.ERROR, PLUGIN_ID, status);
        }
        
        return new Status(IStatus.OK, PLUGIN_ID, Messages.copyDataSourceDialogOk);        
    }
    
}
