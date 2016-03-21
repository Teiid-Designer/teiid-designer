/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.file;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.datatools.connection.DataSourceConnectionHelper;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

public class TeiidMetadataImportDataSourcePage extends AbstractWizardPage implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportDataSourcePage.class);
	private static final String TITLE = getString("title"); //$NON-NLS-1$

	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}
	
	private static String getString(final String id, final Object var) {
		return Util.getString(I18N_PREFIX + id, var);
	}

	private final TeiidMetadataImportInfo info;
    
    private Text jndiNameField;
    private String jndiName;
    private Button autoCreateDataSource;
    
    private boolean synchronizing;

	public TeiidMetadataImportDataSourcePage(TeiidMetadataImportInfo fileInfo) {
		super(TeiidFlatFileImportOptionsPage.class.getSimpleName(), TITLE);
		this.info = fileInfo;
	}

	@Override
	public void createControl(Composite parent) {
		// Create page

		final Composite hostPanel = new Composite(parent, SWT.NONE);
		hostPanel.setLayout(new GridLayout(1, false));
		hostPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Create page
		DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel);
		hostPanel.setLayout(new GridLayout(1, false));
		hostPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Composite mainPanel = scrolledComposite.getPanel();
		mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainPanel.setLayout(new GridLayout(1, false));

		// Add widgets to page
    	Group theGroup = WidgetFactory.createGroup(mainPanel, getString("jndiGroup"), SWT.NONE, 2, 3); //$NON-NLS-1$
    	theGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        WidgetFactory.createLabel(theGroup, "JNDI Name "); //$NON-NLS-1$
        
        // Check to see if server is available and connected
        boolean serverDefined = DataSourceConnectionHelper.isServerDefined();
        boolean serverActive = DataSourceConnectionHelper.isServerConnected();
        
        this.jndiNameField = WidgetFactory.createTextField(theGroup);
        this.jndiName = info.getJBossJndiName();
        if( this.jndiName != null && this.jndiName.length() > 0 ) {
        	this.jndiNameField.setText(this.jndiName);
        }
        
        this.jndiNameField.setEnabled(serverActive);
        
        this.jndiNameField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if( synchronizing ) return;
				
				if( jndiNameField.getText() != null && jndiNameField.getText().length() > 0 ) {
					jndiName = jndiNameField.getText();
					info.setJBossJndiNameName(jndiName);
				} else {
					jndiName = ""; //$NON-NLS-1$
					info.setJBossJndiNameName(null);
				}
				
			}
		});
	        
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(jndiNameField);
        
        this.autoCreateDataSource = WidgetFactory.createCheckBox(theGroup, "Auto-create Data Source");
        GridDataFactory.fillDefaults().span(2,  1).grab(true,  false).applyTo(autoCreateDataSource);
        this.autoCreateDataSource.setSelection(info.doCreateDataSource());
        
        if( serverActive ) {
	        this.autoCreateDataSource.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					info.setCreateDataSource(autoCreateDataSource.getSelection());
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// NOTHING
				}
			});
        }
        
        this.autoCreateDataSource.setEnabled(serverActive);
        
        
        if( !serverActive ) {
        	// if server still exists and NOT connected display message of NOT CONNECTED/STARTED
        	Group serverMessageGroup = WidgetFactory.createGroup(mainPanel, getString("serverUnavailableGroup"), SWT.NONE, 2, 3); //$NON-NLS-1$
        	theGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            
       	
        	Text msgText = new Text(serverMessageGroup, SWT.WRAP | SWT.READ_ONLY);
        	msgText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        	msgText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        	GridDataFactory.fillDefaults().span(2, 1).grab(true,  false).hint(0,  35).applyTo(serverMessageGroup);

            if( !serverDefined ) {  
            	msgText.setText(getString("noServerDefined", ModelerCore.getTeiidServerManager().getDefaultServer().getDisplayName())); //$NON-NLS-1$
            } else {
            	
            	msgText.setText(getString("serverNotStarted")); //$NON-NLS-1$
            }

        	
        	// if server == null, then display message of NO DEFAULT SERVER DEFINED
        }

		scrolledComposite.sizeScrolledPanel();

		setControl(hostPanel);

		setMessage(getString("initialMessage")); //$NON-NLS-1$

		setPageComplete(true);
	}


	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			synchronizeUI();
		}
	}
	
    void synchronizeUI(){
    	synchronizing = true;
        
        if( this.info.getJBossJndiName() != null ) {
        	this.jndiNameField.setText(this.info.getJBossJndiName());
        } else {
        	this.jndiNameField.setText(StringConstants.EMPTY_STRING);
        }
                
        synchronizing = false;
    }
}
