/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.connection.VdbDataSourceInfo;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Label;

/**
 *
 */
public class CreateVdbDataSourceDialog extends TitleAreaDialog  {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(CreateVdbDataSourceDialog.class);
    
    private static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }
    
    private static String getString( final String id, final String param) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, param);
    }
    
	private StringNameValidator nameValidator;
    private VdbDataSourceInfo vdbDataSourceInfo;
    private String vdbName;
    
    private Text vdbNameText;
    private Text dataSourceNameText;
    private Button passThroughButton;
    private ITeiidServer server;

    /**
     * CreateDataSourceDialog constructor
     * @param shell the shell
     * @param teiidImportServer the TeiidServer
     * @param editDSName if non-null, this is edit of an existing source.  Otherwise this is
     * creation of a new source.
     */
    public CreateVdbDataSourceDialog(Shell shell, VdbDataSourceInfo vdbDataSourceInfo, ITeiidServer server) {
        super(shell);
        this.vdbName = vdbDataSourceInfo.getVdbName();
        this.vdbDataSourceInfo = vdbDataSourceInfo;
        this.nameValidator = new StringNameValidator(new char[] {'_','-'});
        this.server = server;
    }
        
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(getString("title"));  //$NON-NLS-1$
    }
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createButtonBar( Composite parent ) {
        Control buttonBar = super.createButtonBar(parent);

        getButton(OK).setEnabled(true);
        
        validate();

        return buttonBar;
    }
   
    /**
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    protected Control createDialogArea(Composite parent) {
    	setTitle(getString("subTitle")); //$NON-NLS-1$
    	setMessage(getString("initialMessage")); //$NON-NLS-1$
    	
        Composite pnl = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(10,  10).equalWidth(false).applyTo(pnl);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(pnl);
        
        WidgetFactory.createLabel(pnl, getString("vdbNameLabel")); //$NON-NLS-1$
        vdbNameText = new Text(pnl, SWT.NONE);
        vdbNameText.setText(this.vdbName);
        vdbNameText.setEditable(false);
        vdbNameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(vdbNameText);
        
        Label label = WidgetFactory.createLabel(pnl, getString("jndiNameLabel")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(label);
        
        dataSourceNameText = new Text(pnl, SWT.BORDER);
        dataSourceNameText.setText(this.vdbDataSourceInfo.getJndiName());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(dataSourceNameText);
        dataSourceNameText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				// Validate
				String text = dataSourceNameText.getText();
				if(text != null && text.trim().length() > 0 ) {
					vdbDataSourceInfo.setJndiName(text);
				}
				validate();
				
			}
		});
        
        //WidgetFactory.createLabel(pnl, "Pass Thru Authentication");
        passThroughButton = new Button(pnl, SWT.CHECK);
        passThroughButton.setText(getString("passTroughAuthenticationLabel")); //$NON-NLS-1$
        passThroughButton.setToolTipText(getString("passTroughAuthenticationTooltip")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().span(2, 1).applyTo(passThroughButton);
        passThroughButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				vdbDataSourceInfo.setPassThroughAuthentication(passThroughButton.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// NOOP
			}
		});
        
        return pnl;
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

    private boolean validate() {
    	String errorMessage = null;
    	String warningMessage = null;
    	if(!this.nameValidator.isValidName(this.vdbDataSourceInfo.getJndiName())) {
            errorMessage = getString("invalidJndiName"); //$NON-NLS-1$
        }
    	// Check for duplicate Data Source name
    	if(errorMessage == null ) {
    		try {
				if( server.getDataSource(this.vdbDataSourceInfo.getJndiName()) != null ) {
					warningMessage = getString("dataSourceExistsMessage", this.vdbDataSourceInfo.getJndiName()); //$NON-NLS-1$
				}
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
    	}
    	if( errorMessage == null ) {
    		setErrorMessage(null);
    		getButton(OK).setEnabled(true);
    		if( warningMessage != null ) {
    			setMessage(warningMessage, IMessageProvider.WARNING);
    		} else {
    			setMessage(getString("okMessage", this.vdbDataSourceInfo.getJndiName()), IMessageProvider.NONE); //$NON-NLS-1$
    		}
    		
    		
    	} else {
    		getButton(OK).setEnabled(false);
    		setErrorMessage(errorMessage);
    	}
        
        return true;

    }
    
}