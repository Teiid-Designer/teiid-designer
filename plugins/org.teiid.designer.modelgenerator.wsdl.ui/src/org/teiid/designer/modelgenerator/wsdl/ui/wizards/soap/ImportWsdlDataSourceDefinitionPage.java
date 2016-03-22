package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

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
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.datatools.connection.DataSourceConnectionHelper;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiPlugin;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.WSDLImportWizardManager;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;

public class ImportWsdlDataSourceDefinitionPage extends AbstractWizardPage implements UiConstants {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ImportWsdlDataSourceDefinitionPage.class);
	private static final String TITLE = getString("title"); //$NON-NLS-1$

	private static String getString(final String id) {
		return ModelGeneratorWsdlUiPlugin.UTIL.getString(I18N_PREFIX + id);
	}
	
	private static String getString(final String id, final Object var) {
		return ModelGeneratorWsdlUiPlugin.UTIL.getString(I18N_PREFIX + id, var);
	}

	private final WSDLImportWizardManager theImportManager;
    
    private Text jndiNameField;
    private String jndiName;
    private Button autoCreateDataSource;
    
    private boolean synchronizing;

	public ImportWsdlDataSourceDefinitionPage(WSDLImportWizardManager theImportManager, ImportWsdlSoapWizard wizard) {
		super(ImportWsdlDataSourceDefinitionPage.class.getSimpleName(), TITLE);
		this.theImportManager = theImportManager;
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
    	((GridLayout)theGroup.getLayout()).marginLeft = 10;
    	((GridLayout)theGroup.getLayout()).marginRight = 10;
        
    	Label label = WidgetFactory.createLabel(theGroup, getString("jndiLabel")); //$NON-NLS-1$
        label.setToolTipText(getString("jndiToolTip")); //$NON-NLS-1$
        
        // Check to see if server is available and connected
        boolean serverDefined = DataSourceConnectionHelper.isServerDefined();
        boolean serverActive = DataSourceConnectionHelper.isServerConnected();
        
        this.jndiNameField = WidgetFactory.createTextField(theGroup);
        this.jndiNameField.setToolTipText(getString("jndiToolTip")); //$NON-NLS-1$
        this.jndiName = theImportManager.getJBossJndiName();
        if( !StringUtilities.isEmpty(this.jndiName) ) {
        	this.jndiNameField.setText(this.jndiName);
        } else {
        	String modelName = theImportManager.getSourceModelName();
        	if( !StringUtilities.isEmpty(modelName) ) {
        		if( modelName.toUpperCase().endsWith(".XMI") ) {
        			int nameLength = modelName.length();
        			modelName = modelName.substring(0, nameLength-4);
        		}
        		this.theImportManager.setJBossJndiNameName(modelName);
                this.jndiName = theImportManager.getJBossJndiName();
                this.jndiNameField.setText(this.jndiName);
        	}
        }
        
        
        this.jndiNameField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if( synchronizing ) return;
				
				if( jndiNameField.getText() != null && jndiNameField.getText().length() > 0 ) {
					jndiName = jndiNameField.getText();
					theImportManager.setJBossJndiNameName(jndiName);
				} else {
					jndiName = ""; //$NON-NLS-1$
					theImportManager.setJBossJndiNameName(null);
				}
				
			}
		});
	        
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(jndiNameField);
        
        this.autoCreateDataSource = WidgetFactory.createCheckBox(theGroup, "Auto-create Data Source");
        GridDataFactory.fillDefaults().span(2,  1).grab(true,  false).applyTo(autoCreateDataSource);
        this.autoCreateDataSource.setSelection(theImportManager.doCreateDataSource());
        
        if( serverActive ) {
	        this.autoCreateDataSource.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					theImportManager.setCreateDataSource(autoCreateDataSource.getSelection());
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
        	Group serverMessageGroup = WidgetFactory.createGroup(theGroup, getString("serverUnavailableGroup"), SWT.NONE, 2, 3); //$NON-NLS-1$
        	serverMessageGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            
       	
        	Text msgText = new Text(serverMessageGroup, SWT.WRAP | SWT.READ_ONLY);
        	msgText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        	msgText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        	GridDataFactory.fillDefaults().span(2, 1).grab(true,  false).hint(0,  55).applyTo(serverMessageGroup);

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
        
        this.jndiName = theImportManager.getJBossJndiName();
        if( !StringUtilities.isEmpty(this.jndiName) ) {
        	this.jndiNameField.setText(this.jndiName);
        } else {
        	String modelName = theImportManager.getSourceModelName();
        	if( !StringUtilities.isEmpty(modelName) ) {
        		if( modelName.toUpperCase().endsWith(".XMI") ) {
        			int nameLength = modelName.length();
        			modelName = modelName.substring(0, nameLength-4);
        		}
        		this.theImportManager.setJBossJndiNameName(modelName);
                this.jndiName = theImportManager.getJBossJndiName();
                this.jndiNameField.setText(this.jndiName);
        	}
        }
                
        synchronizing = false;
    }
}
