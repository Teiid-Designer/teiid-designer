/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.profiles.jdg;

import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.util.JndiUtil;
import org.teiid.designer.datatools.profiles.jbossds.IJBossDsProfileConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;

public class JDGProfilePropertyPage extends ProfileDetailsPropertyPage
		implements Listener, IJDGProfileConstants.PropertyKeys, IContextProvider, DatatoolsUiConstants {

	private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
			DatatoolsUiPlugin.getDefault().getBundle().getSymbolicName());
	private Composite scrolled;
	
    private CLabel profileText;
    private CLabel descriptionText;
    private Text jndiText;
    private Text cacheTypeMapText;
    private Button remoteServerListRB;
    private Text remoteServerListText;
    private Button cacheJndiNameRB;
    private Text cacheJndiNameText;
    private Button hotRodClientRB;
    private Text hotRodClientPropFileText;
    
    private Button enableMaterialization;
    private Text stagingCacheNameText;
    private Text aliasCacheNameText;
    private org.teiid.designer.ui.common.widget.Label stagingCacheNameLabel;
    private org.teiid.designer.ui.common.widget.Label aliasCacheNameLabel;
    
    /*
    Define JDG Schema via Annotations or Protobuf Files
			 - if PROTOBUF.. then add
				- ProtobufDefinitionFile (REQUIRED) Path to the Google Protobin file that's packaged in a jar (ex: /quickstart/addressbook.protobin)
				- MessageMarshallers (REQUIRED) Contains Class names mapped its respective message marshaller, (className:marshallerClassName,className:marshallerClassName...), that are to be registered for serialization
				- MessageDescriptor (REQUIED) Message descriptor class name for the root object in cache 
				- "module"  (OPTIONAL)is a general/optional property on the resource adapter
    */
   private Button schemaAnotationsRB;
   private Button schemaProtobufRB;
   private Text protobufDefFileText;
   private Text messageMarshallersText;
   private Text messageDescriptorText;
   private Text moduleText;
   
   private boolean settingProperty = false;
   
	public JDGProfilePropertyPage() {
		super();
		setPingButtonEnabled(false);
	}

	@Override
	public IContext getContext(Object target) {
		return contextProviderDelegate.getContext(target);
	}

	@Override
	public int getContextChangeMask() {
		return contextProviderDelegate.getContextChangeMask();
	}

	@Override
	public String getSearchExpression(Object target) {
		return contextProviderDelegate.getSearchExpression(target);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
		this.setPingButtonVisible(true);
		return result;
	}

	@Override
	protected void createCustomContents(Composite parent) {
		final Composite mainPanel = parent; //scrolledComposite.getPanel();
		mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainPanel.setLayout(new GridLayout(1, false));

        scrolled = new Composite(mainPanel, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(scrolled);
        GridDataFactory.fillDefaults().grab(true,  true).applyTo(scrolled);

        createLabel(scrolled, SWT.NONE, UTIL.getString("Common.Profile.Label"), null);

        profileText = createLabel(scrolled, SWT.NONE, getConnectionProfile().getName(), null);

        createLabel(scrolled, SWT.NONE, UTIL.getString("Common.Description.Label"), null);

        descriptionText = createLabel(scrolled, SWT.NONE, getConnectionProfile().getDescription(), null);

        createLabel(scrolled, SWT.NONE, 
        		UTIL.getString("JBossDsPropertyPage.jndi.Label"), 
        		UTIL.getString("JBossDsPropertyPage.jndi.ToolTip"));
        jndiText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
        		UTIL.getString("JBossDsPropertyPage.jndi.ToolTip"), 1, true);
        jndiText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
	            String jndiName = JndiUtil.addJavaPrefix(jndiText.getText());
	            setProperty(IJBossDsProfileConstants.JNDI_PROP_ID, jndiName);
			}
		});
    	
        createLabel(scrolled, SWT.NONE, Messages.CacheTypeMap, Messages.CacheTypeMapToolTip);
    	cacheTypeMapText = createTextField(scrolled, SWT.SINGLE | SWT.BORDER, 
    			Messages.CacheTypeMapToolTip, 1, true);
    	cacheTypeMapText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				setProperty(CACHE_TYPE_MAP, cacheTypeMapText.getText());
			}
		});
        
        // General Remote Cache Properties
        
        {
        	// Create Group
        	Composite generalGroup = WidgetFactory.createGroup(scrolled, Messages.ConfigureConnectionToCache, SWT.BORDER, 2, 2);
        	GridDataFactory.fillDefaults().grab(true,  false).span(2,  1).applyTo(generalGroup);
        	
        	remoteServerListRB = new Button(generalGroup, SWT.RADIO);
        	remoteServerListRB.setSelection(true);
        	remoteServerListRB.setText(Messages.RemoteServerList);
        	remoteServerListRB.setToolTipText(Messages.RemoteServerListToolTip);
        	remoteServerListRB.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					if( remoteServerListRB.getSelection() ) {
						remoteServerListText.setEnabled(true);
						cacheJndiNameText.setText(StringConstants.EMPTY_STRING);
						hotRodClientPropFileText.setText(StringConstants.EMPTY_STRING);
						cacheJndiNameText.setEnabled(false);
						hotRodClientPropFileText.setEnabled(false);
					} else if( cacheJndiNameRB.getSelection() ) {
						cacheJndiNameRB.setEnabled(true);
						remoteServerListText.setText(StringConstants.EMPTY_STRING);
						hotRodClientPropFileText.setText(StringConstants.EMPTY_STRING);
						remoteServerListText.setEnabled(false);
						hotRodClientPropFileText.setEnabled(false);
					} else {
						hotRodClientPropFileText.setEnabled(true);
						cacheJndiNameText.setText(StringConstants.EMPTY_STRING);
						remoteServerListText.setText(StringConstants.EMPTY_STRING);
						remoteServerListText.setEnabled(false);
						cacheJndiNameText.setEnabled(false);
					}

					updateState();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

        	remoteServerListText = createTextField(generalGroup, SWT.SINGLE | SWT.BORDER, 
        			Messages.RemoteServerListToolTip, 1, true);
        	remoteServerListText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( settingProperty ) return;
					
					setProperty(REMOTE_SERVER_LIST, remoteServerListText.getText());
				}
			});

        	cacheJndiNameRB = new Button(generalGroup, SWT.RADIO);
        	cacheJndiNameRB.setText(Messages.CacheJNDIName);
        	cacheJndiNameRB.setToolTipText(Messages.CacheJNDINameToolTip);
        	cacheJndiNameRB.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					cacheJndiNameText.setEnabled(cacheJndiNameRB.getSelection());
					remoteServerListText.setEnabled(remoteServerListRB.getSelection());
					hotRodClientPropFileText.setEnabled(hotRodClientRB.getSelection());
					remoteServerListText.setText(StringConstants.EMPTY_STRING);
					hotRodClientPropFileText.setText(StringConstants.EMPTY_STRING);
					updateState();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
        	cacheJndiNameText = createTextField(generalGroup, SWT.SINGLE | SWT.BORDER, 
        			Messages.CacheJNDINameToolTip, 1, true);
        	cacheJndiNameText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( settingProperty ) return;
					
					setProperty(CACHE_JNDI_NAME, cacheJndiNameText.getText());
				}
			});
        	cacheJndiNameText.setEnabled(false);
        			
        	hotRodClientRB = new Button(generalGroup, SWT.RADIO);
        	hotRodClientRB.setText(Messages.HotRodClientPropertiesFile);
        	hotRodClientRB.setToolTipText(Messages.HotRodClientPropertiesFileToolTip);
        	hotRodClientRB.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					cacheJndiNameText.setEnabled(cacheJndiNameRB.getSelection());
					remoteServerListText.setEnabled(remoteServerListRB.getSelection());
					hotRodClientPropFileText.setEnabled(hotRodClientRB.getSelection());
					cacheJndiNameText.setText(StringConstants.EMPTY_STRING);
					remoteServerListText.setText(StringConstants.EMPTY_STRING);
					updateState();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
        	hotRodClientPropFileText = createTextField(generalGroup, SWT.SINGLE | SWT.BORDER, 
        			Messages.HotRodClientPropertiesFileToolTip, 1, true);
        	hotRodClientPropFileText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( settingProperty ) return;
					
					setProperty(HOT_ROD_CLIENT_PROPERTIES_FILE, hotRodClientPropFileText.getText());
				}
			});
        	hotRodClientPropFileText.setEnabled(false);
        }

        
        // Set default to FALSE
        {
            
        	// Materialization Group
        	Composite matGroup = WidgetFactory.createGroup(scrolled, Messages.JDGCacheNames, SWT.BORDER, 2, 2);
        	GridDataFactory.fillDefaults().grab(true,  false).span(2,  1).applyTo(matGroup);
        	
            enableMaterialization = new Button(matGroup,  SWT.CHECK);
            enableMaterialization.setText(Messages.EnableMaterialization);
            GridDataFactory.fillDefaults().grab(true,  false).span(2, 1).applyTo(enableMaterialization);
            enableMaterialization.setSelection(false);
            enableMaterialization.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				boolean enable = enableMaterialization.getSelection();
    				
    				if( !enable ) {
    					setProperty(STAGING_CACHE_NAME, StringConstants.EMPTY_STRING);
        				setProperty(ALIAS_CACHE_NAME, StringConstants.EMPTY_STRING);
    				} else {
    					setProperty(STAGING_CACHE_NAME, stagingCacheNameText.getText());
        				setProperty(ALIAS_CACHE_NAME, aliasCacheNameText.getText());
    				}
    				stagingCacheNameText.setEnabled(enable);
    				aliasCacheNameText.setEnabled(enable);
    				stagingCacheNameLabel.setEnabled(enable);
    				aliasCacheNameLabel.setEnabled(enable);
    				updateState();
    			}
    			
    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    			}
    		});

            
        	stagingCacheNameLabel = createLabel(matGroup, SWT.NONE, Messages.StagingCacheName, Messages.StagingCacheNameToolTip);
   			stagingCacheNameText = createTextField(matGroup, SWT.SINGLE | SWT.BORDER, 
        			Messages.StagingCacheNameToolTip, 1, true);
        	stagingCacheNameText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( settingProperty ) return;
					
					setProperty(STAGING_CACHE_NAME, stagingCacheNameText.getText());
				}
			});
        	stagingCacheNameText.setEnabled(false);
        			
        	aliasCacheNameLabel = createLabel(matGroup, SWT.NONE, Messages.AliasCacheName, Messages.AliasCacheNameToolTip);
        	aliasCacheNameText = createTextField(matGroup, SWT.SINGLE | SWT.BORDER, 
        			Messages.AliasCacheNameToolTip, 1, true);
        	aliasCacheNameText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( settingProperty ) return;
					
					setProperty(ALIAS_CACHE_NAME, aliasCacheNameText.getText());
				}
			});
        	aliasCacheNameText.setEnabled(true);
        }
        
        {
        	// Create Group
        	Composite schemaGroup = WidgetFactory.createGroup(scrolled, Messages.JDGSchemaOptions, SWT.BORDER, 2, 2);
        	GridDataFactory.fillDefaults().grab(true,  false).span(2,  1).applyTo(schemaGroup);
        	
        	schemaAnotationsRB = new Button(schemaGroup, SWT.RADIO);
        	schemaAnotationsRB.setText(Messages.Annotations);
        	schemaAnotationsRB.setToolTipText(Messages.AnnotationsTooltip);
        	schemaAnotationsRB.setSelection(true);
        	schemaAnotationsRB.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateState();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

        	
        	schemaProtobufRB = new Button(schemaGroup, SWT.RADIO);
        	schemaProtobufRB.setText(Messages.Protobuf); 
        	schemaProtobufRB.setToolTipText(Messages.ProtobufTooltip);

        	/*
        	 	- ProtobufDefinitionFile
				- MessageMarshallers
				- MessageDescriptor
        	 */
        	
        	
        	// Module is a protobuf feature/property
        	createLabelRightJustified(schemaGroup, SWT.NONE, Messages.ProtobufDefinitionFile, Messages.ProtobufDefinitionFileTooltip);
        	protobufDefFileText = createTextField(schemaGroup, SWT.SINGLE | SWT.BORDER, 
        			Messages.ProtobufDefinitionFileTooltip, 1, true);

        	protobufDefFileText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( settingProperty ) return;
					
					setProperty(PROTOBUF_DEFINITION_FILE, protobufDefFileText.getText());
				}
			});
        	protobufDefFileText.setEnabled(false);
        	createLabelRightJustified(schemaGroup, SWT.NONE, Messages.MessageMarshallers, Messages.MessageMarshallersTooltip);
        	messageMarshallersText = createTextField(schemaGroup, SWT.SINGLE | SWT.BORDER, 
        			Messages.MessageMarshallersTooltip, 1, true);

        	messageMarshallersText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( settingProperty ) return;
					
					setProperty(MESSAGE_MARSHALLERS, messageMarshallersText.getText());
				}
			});
        	messageMarshallersText.setEnabled(false);
        	
        	createLabelRightJustified(schemaGroup, SWT.NONE, Messages.MessageDescriptor, Messages.MessageDescriptorTooltip);
        	messageDescriptorText = createTextField(schemaGroup, SWT.SINGLE | SWT.BORDER, 
        			Messages.MessageDescriptorTooltip, 1, true);

        	messageDescriptorText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( settingProperty ) return;
					
					setProperty(MESSAGE_DESCRIPTOR, messageDescriptorText.getText());
				}
			});
        	messageDescriptorText.setEnabled(false);
        	
        	createLabelRightJustified(schemaGroup, SWT.NONE, Messages.Module, Messages.ModuleTooltip);
        	moduleText = createTextField(schemaGroup, SWT.SINGLE | SWT.BORDER, 
        			Messages.ModuleTooltip, 1, true);

        	moduleText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( settingProperty ) return;
					
					setProperty(MODULE, moduleText.getText());
				}
			});
        }
		
        setPingButtonVisible(false);
        addListeners();


		initControls();
	}
	
    /**
     * 
     */
    private void addListeners() {
        jndiText.addListener(SWT.Modify, this);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void handleEvent( Event event ) {

        if (event.widget == jndiText) {
            Properties properties = getConnectionProfile().getBaseProperties();
            properties.setProperty(IJDGProfileConstants.JNDI_PROP_ID, jndiText.getText());
        }

        updateState();
    }

	protected void validate() {
		String errorMessage = null;
		boolean valid = true;

		setErrorMessage(errorMessage);
		this.setPingButtonEnabled(valid);
		setValid(valid);

	}
	
    void updateState() {
		boolean enableProtobuf = schemaProtobufRB.getSelection();
		protobufDefFileText.setEnabled(enableProtobuf);
		messageDescriptorText.setEnabled(enableProtobuf);
		messageMarshallersText.setEnabled(enableProtobuf);
		
        profileText.setText(getConnectionProfile().getName());
        profileText.getParent().layout(true);
        descriptionText.setText(getConnectionProfile().getDescription());
        descriptionText.getParent().layout(true);

        Properties properties = getConnectionProfile().getBaseProperties();
        if (null == properties.get(IJBossDsProfileConstants.JNDI_PROP_ID)
            || properties.get(IJBossDsProfileConstants.JNDI_PROP_ID).toString().isEmpty()) {
            setErrorMessage(UTIL.getString("JBossDsPropertyPage.jndi.Error.Message")); //$NON-NLS-1$
            setPingButtonEnabled(false);
            setValid(false);
            return;
        }
        
        if( remoteServerListRB.getSelection() && 
        		StringUtilities.isEmpty((String)properties.get(REMOTE_SERVER_LIST)) ) {
            setErrorMessage(Messages.RemoteServerListMissing); //$NON-NLS-1$
            setValid(false);
            return;
        }
        
        if( cacheJndiNameRB.getSelection() && 
        		StringUtilities.isEmpty((String)properties.get(CACHE_JNDI_NAME)) ) {
            setErrorMessage(Messages.CacheJNDINameMissing); //$NON-NLS-1$
            setValid(false);
            return;
        }
        
        if( hotRodClientRB.getSelection() && 
        		StringUtilities.isEmpty((String)properties.get(HOT_ROD_CLIENT_PROPERTIES_FILE)) ) {
            setErrorMessage(Messages.HotRodClientPropertiesFileMissing); //$NON-NLS-1$
            setValid(false);
            return;
        }
        
        // If materialization is ON, then 
        //   StagingCacheName is REQUIRED
        //   AliasCacheName is REQUIRED
        if( enableMaterialization.getSelection() ) {
        	if (StringUtilities.isEmpty((String)properties.get(STAGING_CACHE_NAME)) ) {
                setErrorMessage(Messages.StagingCacheNameMissing); //$NON-NLS-1$
                setValid(false);
                return;
        	}
        	
        	if (StringUtilities.isEmpty((String)properties.get(ALIAS_CACHE_NAME)) ) {
                setErrorMessage(Messages.AliasCacheNameMissing); //$NON-NLS-1$
                setValid(false);
                return;
        	}
        }
        
        
        // PROTOBUF PROPERTIES
        if (schemaProtobufRB.getSelection() ) {
        	if (StringUtilities.isEmpty((String)properties.get(PROTOBUF_DEFINITION_FILE)) ) {
                setErrorMessage(Messages.ProtobufDefinitionFileMissing); //$NON-NLS-1$
                setValid(false);
                return;
        	}

        	if (StringUtilities.isEmpty((String)properties.get(MESSAGE_MARSHALLERS)) ) {
                setErrorMessage(Messages.MessageMarshallersMissing); //$NON-NLS-1$
                setValid(false);
                return;
        	}

        	if (StringUtilities.isEmpty((String)properties.get(MESSAGE_DESCRIPTOR)) ) {
                setErrorMessage(Messages.MessageDescriptorMissing); //$NON-NLS-1$
                setValid(false);
                return;
        	}
        }


        setValid(true);
        setErrorMessage(null);
        setMessage(UTIL.getString("Click.Next.or.Finish")); //$NON-NLS-1$

    }

	/**
	* 
	*/
	private void initControls() {
		IConnectionProfile profile = getConnectionProfile();
		Properties props = profile.getBaseProperties();
		if (propExists(props, ALIAS_CACHE_NAME)) {
			aliasCacheNameText.setText((String) props.get(ALIAS_CACHE_NAME));
		}
		if (propExists(props, IJBossDsProfileConstants.JNDI_PROP_ID)) {
			jndiText.setText((String) props.get(IJBossDsProfileConstants.JNDI_PROP_ID));
		}
		if (propExists(props, CACHE_TYPE_MAP)) {
			cacheTypeMapText.setText((String) props.get(CACHE_TYPE_MAP));
		}
		if (propExists(props, CACHE_JNDI_NAME)) {
			cacheJndiNameText.setText((String) props.get(CACHE_JNDI_NAME));
		}
		if (propExists(props, MODULE)) {
			moduleText.setText((String) props.get(MODULE));
		}
		if (propExists(props, REMOTE_SERVER_LIST)) {
			remoteServerListText.setText((String) props.get(REMOTE_SERVER_LIST));
		}
		if (propExists(props, ALIAS_CACHE_NAME)) {
			aliasCacheNameText.setText((String) props.get(ALIAS_CACHE_NAME));
		}
		if (propExists(props, STAGING_CACHE_NAME)) {
			stagingCacheNameText.setText((String) props.get(STAGING_CACHE_NAME));
		}
		
		if (!StringUtilities.isEmpty((String)props.get(HOT_ROD_CLIENT_PROPERTIES_FILE))) {
			hotRodClientPropFileText.setText((String)props.get(HOT_ROD_CLIENT_PROPERTIES_FILE));
			hotRodClientPropFileText.setEnabled(true);
			remoteServerListRB.setSelection(false);
			cacheJndiNameRB.setSelection(false);
			hotRodClientRB.setSelection(true);
		} else if (!StringUtilities.isEmpty((String)props.get(CACHE_JNDI_NAME))) {
			cacheJndiNameText.setText((String)props.get(CACHE_JNDI_NAME));
			cacheJndiNameText.setEnabled(true);
			remoteServerListRB.setSelection(false);
			cacheJndiNameRB.setSelection(true);
			hotRodClientRB.setSelection(false);
		} else if (!StringUtilities.isEmpty((String)props.get(REMOTE_SERVER_LIST))) {
			remoteServerListText.setText((String)props.get(REMOTE_SERVER_LIST));
			remoteServerListText.setEnabled(true);
			remoteServerListRB.setSelection(true);
			cacheJndiNameRB.setSelection(false);
			hotRodClientRB.setSelection(false);
		}
		


    	boolean protobufSelected = false;
    	if (propExists(props, PROTOBUF_DEFINITION_FILE)) {
    		protobufDefFileText.setText((String)props.get(PROTOBUF_DEFINITION_FILE));
    		protobufSelected = true;
    	}
    	if (propExists(props, MESSAGE_MARSHALLERS) ) {
    		messageMarshallersText.setText((String)props.get(MESSAGE_MARSHALLERS));
    		protobufSelected = true;
    	}
    	if (propExists(props, MESSAGE_DESCRIPTOR) ) {
    		messageDescriptorText.setText((String)props.get(MESSAGE_DESCRIPTOR));
    		protobufSelected = true;
    	}
    	schemaProtobufRB.setSelection(protobufSelected);
    	
		boolean matEnabled = props.get(ALIAS_CACHE_NAME) != null ||
				props.getProperty(STAGING_CACHE_NAME) != null ||
				props.getProperty(IJDGProfileConstants.TranslatorOverrides.SUPPORTS_DIRECT_QUERY_PROCEDURE) != null ||
				props.getProperty(IJDGProfileConstants.TranslatorOverrides.SUPPORTS_NATIVE_QUERIES) != null;
		enableMaterialization.setSelection(matEnabled);
		if( matEnabled ) {
	    	if (propExists(props, STAGING_CACHE_NAME)) {
	    		stagingCacheNameText.setText((String)props.get(STAGING_CACHE_NAME));	
	    		stagingCacheNameText.setEnabled(true);
	    		stagingCacheNameText.setEditable(true);
	    	}
	    	if (propExists(props, ALIAS_CACHE_NAME) ) {
	    		aliasCacheNameText.setText((String)props.get(ALIAS_CACHE_NAME));
	    		aliasCacheNameText.setEnabled(true);
	    		aliasCacheNameText.setEditable(true);
	    	}
		}
		validate();
	}
	
	private boolean propExists(Properties props, String key) {
		String value = props.getProperty(key);
		
		if (StringUtilities.isEmpty(value)) return false;
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage#collectProperties()
	 */
	@Override
	protected Properties collectProperties() {
		Properties result = super.collectProperties();
		if (null == result) {
			result = new Properties();
		}

		return result;
	}
	
    
	private void setProperty(String id, String value) {
		settingProperty = true;
		
		IConnectionProfile profile = getConnectionProfile();
		Properties properties = profile.getBaseProperties();
		
        if( REMOTE_SERVER_LIST.equals(id)) {
        	// remove other 2 properties
        	properties.remove(CACHE_JNDI_NAME);
        	cacheJndiNameText.setText(StringConstants.EMPTY_STRING);
        	properties.remove(HOT_ROD_CLIENT_PROPERTIES_FILE);
        	hotRodClientPropFileText.setText(StringConstants.EMPTY_STRING);
        } else if( CACHE_JNDI_NAME.equals(id)) {
        	// remove other 2 properties
        	properties.remove(HOT_ROD_CLIENT_PROPERTIES_FILE);
        	hotRodClientPropFileText.setText(StringConstants.EMPTY_STRING);
        	properties.remove(REMOTE_SERVER_LIST);
        	remoteServerListText.setText(StringConstants.EMPTY_STRING);
        } else if( HOT_ROD_CLIENT_PROPERTIES_FILE.equals(id)) {
        	// remove other 2 properties
        	properties.remove(CACHE_JNDI_NAME);
        	cacheJndiNameText.setText(StringConstants.EMPTY_STRING);
        	properties.remove(REMOTE_SERVER_LIST);
        	remoteServerListText.setText(StringConstants.EMPTY_STRING);
        }
		
        if( ! StringUtilities.isEmpty(value) ) {
        	properties.setProperty(id, value);
        } else {
        	properties.remove(id);
        }
        
        profile.setBaseProperties(properties);
        
        updateState();
        
        settingProperty = false;
	}
	
	private Text createTextField(Composite parent, int swtStyle, String tooltip, int hSpan, boolean grabHorizontal) {
    	Text textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
    	textField.setToolTipText(tooltip);
    	GridDataFactory.fillDefaults().span(hSpan, 1).grab(grabHorizontal,  false).applyTo(textField);
    	return textField;
	}
	
	
	private org.teiid.designer.ui.common.widget.Label createLabel(Composite parent, int swtStyle, String text, String tooltip) {
		org.teiid.designer.ui.common.widget.Label theLabel = WidgetFactory.createLabel(parent, text);
		theLabel.setToolTipText(tooltip);
    	GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.CENTER).applyTo(theLabel);
    	return theLabel;
	}
	
	
	private org.teiid.designer.ui.common.widget.Label createLabelRightJustified(Composite parent, int swtStyle, String text, String tooltip) {
		org.teiid.designer.ui.common.widget.Label theLabel = WidgetFactory.createLabel(parent, text);
		theLabel.setToolTipText(tooltip);
    	GridDataFactory.fillDefaults().align(GridData.END, GridData.CENTER).applyTo(theLabel);
    	return theLabel;
	}
}
