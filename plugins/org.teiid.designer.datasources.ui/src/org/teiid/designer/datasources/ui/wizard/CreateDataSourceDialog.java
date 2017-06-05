/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datasources.ui.wizard;

import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.datasources.ui.Messages;
import org.teiid.designer.datasources.ui.panels.CreateDataSourcePanel;
import org.teiid.designer.datasources.ui.panels.CreateDataSourcePanelListener;

/**
 *  CreateDataSourceDialog
 *  This Dialog handles Create and Edit of a DataSource
 */
public class CreateDataSourceDialog extends TitleAreaDialog implements CreateDataSourcePanelListener {

    private ITeiidImportServer teiidImportServer;
    private CreateDataSourcePanel createDataSourcePanel;
    private String editDSName;

    /**
     * CreateDataSourceDialog constructor
     * @param shell the shell
     * @param teiidImportServer the TeiidServer
     * @param editDSName if non-null, this is edit of an existing source.  Otherwise this is
     * creation of a new source.
     */
    public CreateDataSourceDialog(Shell shell, ITeiidImportServer teiidImportServer, String editDSName) {
        super(shell);
        this.teiidImportServer = teiidImportServer;
        this.editDSName = editDSName;
    }
        
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if(CoreStringUtil.isEmpty(this.editDSName)) {
            shell.setText(Messages.createDataSourceDialog_title); 
        } else {
            shell.setText(Messages.editDataSourceDialog_title); 
        }
    }
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createButtonBar( Composite parent ) {
        Control buttonBar = super.createButtonBar(parent);
        if(CoreStringUtil.isEmpty(this.editDSName)) {
            getButton(OK).setEnabled(false);
        } else {
            getButton(OK).setEnabled(true);
        }

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

        this.createDataSourcePanel = new CreateDataSourcePanel(pnl,teiidImportServer,this.editDSName);
        this.createDataSourcePanel.addListener(this);

        // set title and initial message
        if(CoreStringUtil.isEmpty(this.editDSName)) {
            setTitle(Messages.createDataSourceDialog_title);
            setErrorMessage(Messages.createDataSourcePanelErrorNameEmpty);
        } else {
            setTitle(Messages.editDataSourceDialog_title);
            setMessage(Messages.editDataSourcePanelEnterChanges);
        }

        return pnl;
    }
        
    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the name of the DataSource (never <code>null</code> or empty when OK button has been pressed)
     */
    public String getDataSourceName() {
        assert (getReturnCode() == Window.OK);
        return this.createDataSourcePanel.getDataSourceName();
    }
    
    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the name of the DataSource Driver (never <code>null</code> or empty when OK button has been pressed)
     */
    public String getDataSourceDriverName() {
        assert (getReturnCode() == Window.OK);
        return this.createDataSourcePanel.getDataSourceDriverName();
    }

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the DataSource Properties (never <code>null</code> or empty when OK button has been pressed)
     */
    public Properties getDataSourceProperties() {
        assert (getReturnCode() == Window.OK);
        return this.createDataSourcePanel.getDataSourceProperties();
    }
    
    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return property changed status (never <code>null</code> or empty when OK button has been pressed)
     */
    public boolean hasPropertyChanges() {
        assert (getReturnCode() == Window.OK);
        return this.createDataSourcePanel.hasPropertyChanges();
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

    private void updateState() {
        IStatus status = this.createDataSourcePanel.getStatus();
        
        if(status.isOK()) {
            getButton(OK).setEnabled(true);
            setErrorMessage(null);
            setMessage(status.getMessage());
        } else {
            getButton(OK).setEnabled(false);
            setErrorMessage(status.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.importer.ui.panels.CreateDataSourcePanelListener#stateChanged()
     */
    @Override
    public void stateChanged() {
        updateState();
    }
    
}
