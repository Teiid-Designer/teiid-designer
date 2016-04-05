/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.profiles.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.datatools.connectivity.model.Parameter;
import org.teiid.designer.core.translators.SimpleProperty;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.CredentialsComposite;

public class PropertyPage extends ProfileDetailsPropertyPage implements
		IContextProvider, DatatoolsUiConstants {

	
	private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
			DatatoolsUiPlugin.getDefault().getBundle().getSymbolicName());
	private Composite scrolled;
	private Label urlLabel;
	private Text urlText;
	private Label urlPreviewLabel;
	Text urlPreviewText;
	private CredentialsComposite credentialsComposite;
	private Map<String, Parameter> parameterMap = new LinkedHashMap<String, Parameter>();
	private Label responseTypeLabel;
    private Combo responseTypeCombo; 
	
	private TabItem parametersTab;
    private TabItem headerPropertiesTab;
    ParameterPanel parameterPanel;
	
	private TableViewerBuilder propertiesViewer;
	
	private Properties extraProperties;

	
	public PropertyPage() {
        super();
        extraProperties = new Properties();
    }

    @Override
    public IContext getContext( Object target ) {
        return contextProviderDelegate.getContext(target);
    }

    @Override
    public int getContextChangeMask() {
        return contextProviderDelegate.getContextChangeMask();
    }

    @Override
    public String getSearchExpression( Object target ) {
        return contextProviderDelegate.getSearchExpression(target);
    }
    
    @Override
	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
        this.setPingButtonEnabled(false);
        this.setPingButtonVisible(false);
        return result;
	}

	@Override
    protected void createCustomContents( Composite parent ) {
        GridData gd;

        Group group = WidgetFactory.createSimpleGroup(parent,
                                                      UTIL.getString("Common.Properties.Label")); //$NON-NLS-1$;

        scrolled = new Composite(group, SWT.FILL);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        scrolled.setLayout(gridLayout);

        urlLabel = new Label(scrolled, SWT.NONE);
        urlLabel.setText(UTIL.getString("Common.URL.Label")); //$NON-NLS-1$
        urlLabel.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.CENTER;
        urlLabel.setLayoutData(gd);

        urlText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        urlText.setToolTipText(UTIL.getString("Common.URL.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        urlText.setLayoutData(gd);
        
        Label spacerLabel = new Label(scrolled, SWT.NONE);
        spacerLabel.setVisible(false);
        GridDataFactory.swtDefaults().grab(false, false).applyTo(spacerLabel);

        credentialsComposite = new CredentialsComposite(scrolled, SWT.BORDER, "rest");  //$NON-NLS-1$
        gd = new GridData(GridData.FILL_HORIZONTAL);
        credentialsComposite.setLayoutData(gd);

        responseTypeLabel = new Label(scrolled, SWT.NONE);
        responseTypeLabel.setText(UTIL.getString("Common.ResponseType.Label")); //$NON-NLS-1$
        responseTypeLabel.setToolTipText(UTIL.getString("Common.ResponseType.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        responseTypeLabel.setLayoutData(gd);

        responseTypeCombo = WidgetFactory.createCombo(scrolled,
        		SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        responseTypeCombo.setLayoutData(gd);
        responseTypeCombo.setItems(new String[] { IWSProfileConstants.XML,
        		IWSProfileConstants.JSON });
        responseTypeCombo.select(0);
        responseTypeCombo.setText(IWSProfileConstants.XML);
        responseTypeCombo.setToolTipText(UTIL.getString("Common.ResponseType.ToolTip"));  //$NON-NLS-1$
        GridDataFactory.swtDefaults().grab(false, false).applyTo(responseTypeCombo);
        responseTypeCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleResponseTypeChanged(((Combo)e.widget).getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

        responseTypeCombo.setVisibleItemCount(2);
        
        urlPreviewLabel = new Label(scrolled, SWT.NONE);
        urlPreviewLabel.setText(UTIL.getString("WSProfileDetailsWizardPage.urlPreviewLabel")); //$NON-NLS-1$
        gd = new GridData();
        urlPreviewLabel.setLayoutData(gd);

        urlPreviewText = new Text(scrolled, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 40;
        gd.horizontalSpan = 3;
        urlPreviewText.setLayoutData(gd);
        urlPreviewText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        initControls();
        
        TabFolder tabFolder = new TabFolder(scrolled, SWT.TOP | SWT.BORDER);
  		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
          
  		Composite parameterPanel = WidgetFactory.createPanel(tabFolder);
  		this.parametersTab = new TabItem(tabFolder, SWT.FILL);
  		this.parametersTab.setControl(parameterPanel);
  		this.parametersTab.setText(UTIL.getString("ParametersPanel_groupTitle")); //$NON-NLS-1$
  		this.parameterPanel = new ParameterPanel(this, parameterPanel, parameterMap, 8);
  		this.urlPreviewText.setText(updateUrlPreview().toString());
  		
  		Composite headerPropertiesPanel = WidgetFactory.createPanel(tabFolder);
  		this.headerPropertiesTab = new TabItem(tabFolder, SWT.FILL);
  		this.headerPropertiesTab.setControl(headerPropertiesPanel);
  		this.headerPropertiesTab.setText(UTIL.getString("HeaderPropertiesPanel_groupTitle")); //$NON-NLS-1$
        new HeaderPropertiesPanel(this, headerPropertiesPanel, parameterMap, 6);
        
        addlisteners();
    }
	
	 /**
	 * @return the extraProperties
	 */
	public Properties getExtraProperties() {
		this.extraProperties = new Properties();
    	for( String key : this.getParameterMap().keySet() )  {
    		Parameter para = this.getParameterMap().get(key);
    		this.extraProperties.put(para.getPropertyKey(), para.getPropertyValue());
    	}

		return this.extraProperties;
	}

	/**
	 * @param extraProperties the extraProperties to set
	 */
	public void setExtraProperties(Properties extraProperties) {
    	for( String key : this.getParameterMap().keySet() )  {
    		Parameter para = this.getParameterMap().get(key);
    		extraProperties.put(para.getPropertyKey(), para.getPropertyValue());
    	}
		this.extraProperties = extraProperties;
	}
	
	 void handleResponseTypeChanged(String type) {
		 this.extraProperties.put(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY, type);
		 validate();
     }
	
	/**
	 * @return the parameterMap
	 */
	public Map<String, Parameter> getParameterMap() {
		return this.parameterMap;
	}

	/**
	 * @param parameterMap the parameterMap to set
	 */
	public void setParameterMap(Map<String, Parameter> parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	 /**
	 * @return
	 */
	StringBuilder updateUrlPreview() {
		StringBuilder previewUrl = new StringBuilder();
		String urlText = this.urlText.getText();
		if (urlText == null || urlText.trim().equals(StringConstants.EMPTY_STRING)){
			urlText = "{base URL}"; //$NON-NLS-1$
		}
		StringBuilder parameters = buildParameterString();
		previewUrl.append(urlText).append(parameters);
		return previewUrl;
	}

	/**
	 * @return
	 */
	/**
	 * @return
	 */
	private StringBuilder buildParameterString() {
		
		StringBuilder parameterString = new StringBuilder();
		if (this.parameterMap==null) return parameterString;
		Map<String, Parameter> parameterMap = this.parameterMap;

		for (String key : parameterMap.keySet()) {
	      Parameter value = parameterMap.get(key);
	      if (value.getType().equals(Parameter.Type.URI)) {
	    	  parameterString.append("/").append(value.getDefaultValue()); //$NON-NLS-1$
	      }
	      if (value.getType().equals(Parameter.Type.Query)) {
	    	  if (parameterString.length()==0 || !parameterString.toString().contains("?")){ //$NON-NLS-1$
	    		  parameterString.append("?");   //$NON-NLS-1$
	    	  }else{
	    		  parameterString.append("&");   //$NON-NLS-1$  
	    	  }
	    	  parameterString.append(value.getName()).append("=").append(value.getDefaultValue()); //$NON-NLS-1$
	      }
	    }

		return parameterString;
	}

    /* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.ui.wizards.ProfilePropertyPage#performOk()
	 */
	@Override
	public boolean performOk() {
		getConnectionProfile().setProperties(getPropertiesID(), collectProperties());
		return super.performOk();
	}

	/**
     * 
     */
    private void addlisteners() {
    	
    	 Listener listener = new Listener() {

             @Override
             public void handleEvent(Event event) {
                 validate();
             }
         };
    	
        urlText.addListener(SWT.Modify, listener);
        credentialsComposite.addSecurityOptionListener(SWT.Modify, listener);
        credentialsComposite.addUserNameListener(SWT.Modify, listener);
        credentialsComposite.addPasswordListener(SWT.Modify, listener);

    }

    protected void validate() {
        String errorMessage = null;
        boolean valid = true;
        if (null == urlText.getText() || urlText.getText().isEmpty()) {
            errorMessage = UTIL.getString("Common.URL.Error.Message"); //$NON-NLS-1$
            valid = false;
            setErrorMessage(errorMessage);
            setValid(valid);
            return;
        }
        setErrorMessage(errorMessage);
        setValid(valid);

    }

    /**
     * 
     */
    private void initControls() {
        IConnectionProfile profile = getConnectionProfile();
        Properties props = profile.getBaseProperties();
        
        // Check properties and load any existing parameters into parametersMap
        loadParameters(props);
        
        String securityType = props.getProperty(ICredentialsCommon.SECURITY_TYPE_ID);
        if (null != securityType) {
            credentialsComposite.setSecurityOption(securityType);
        }

        String username = props.getProperty(ICredentialsCommon.USERNAME_PROP_ID);
        if (null != username) {
            credentialsComposite.setUserName(username);
        }

        String password = props.getProperty(ICredentialsCommon.PASSWORD_PROP_ID);
        if (null != password) {
            credentialsComposite.setPassword(password);
        }
        
        String url = ConnectionInfoHelper.readEndPointProperty(props);
        if (null != url) {
            urlText.setText(url);
        }
        
        if (null != props.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY)) {
        	responseTypeCombo.setText((String)props.get(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY));
        }else{
        	props.put(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY, IWSProfileConstants.XML);
        }
        
        for( Object key : props.keySet() ) {
        	String keyStr = (String)key;
        	if( ICredentialsCommon.PASSWORD_PROP_ID.equalsIgnoreCase(keyStr) ||
            		ICredentialsCommon.SECURITY_TYPE_ID.equalsIgnoreCase(keyStr) ||
            		ICredentialsCommon.USERNAME_PROP_ID.equalsIgnoreCase(keyStr) ||
            		IWSProfileConstants.END_POINT_URI_PROP_ID.equalsIgnoreCase(keyStr) ) {
        		// do nothing;
        	} else {
        		extraProperties.put(key, props.get(key));
        	}
        }
        
    }
    
    /*
    * Need to load the parameters map from general profile properties
    *
    * KEYS will look like:  "rest_param:myParam"
    * VALUES will look like:  "Query:myDefaultValue"
    * The Parameter class includes a constructor that will take these two values and extract the 
    * appropriate parameter name, type and default value values
    */
    private void loadParameters(Properties props) {
    	for( Object key : props.keySet() )  {
    		String keyStr = (String)key;
    		if( keyStr.startsWith(Parameter.PREFIX) ||
    		    keyStr.startsWith(Parameter.HEADER_PREFIX)) {
    			Parameter newParam = new Parameter(keyStr, props.getProperty(keyStr));
    			parameterMap.put(keyStr, newParam);
    		}
    	}
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
        result.setProperty(IWSProfileConstants.END_POINT_URI_PROP_ID, urlText.getText());
        result.setProperty(ICredentialsCommon.SECURITY_TYPE_ID, credentialsComposite.getSecurityOption().name());
        if( credentialsComposite.getUserName() != null ) {
        	result.setProperty(ICredentialsCommon.USERNAME_PROP_ID, credentialsComposite.getUserName());
        }
        if( credentialsComposite.getPassword() != null) {
        	result.setProperty(ICredentialsCommon.PASSWORD_PROP_ID, credentialsComposite.getPassword());
        }
        result.setProperty(IWSProfileConstants.RESPONSE_TYPE_PROPERTY_KEY, responseTypeCombo.getText());
        
        Properties extraProps = getExtraProperties();
        for( Object key : extraProps.keySet() ) {
        	result.put(key, extraProps.get(key));
        }
        
        return result;
    }
    
    
    class ExtraPropertiesPanel implements DatatoolsUiConstants {
    	Button addPropertyButton;
    	Button removePropertyButton;
    	
    	/**
    	 * Constructor
         * @param parent the parent Composite
         * @param propertiesManager the TeiidpropertiesManager
         * @param visibleTableRows the number of visible rows to be shown in the table
         */
        public ExtraPropertiesPanel(Composite parent) {
        	super();
        	createPanel(parent);
        }
        
        /*
         * create the panel
         * @param parent the parent composite
         */
    	private void createPanel(Composite parent) {
        	Composite panel = WidgetFactory.createGroup(parent, UTIL.getString("ExtraPropertiesPanel_groupTitle"), SWT.FILL, 2, 1);  //$NON-NLS-1$
            //GridDataFactory.swtDefaults().grab(true, true).applyTo(panel);
        	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        	gd.horizontalSpan = 2;
        	panel.setLayoutData(gd);

            propertiesViewer = new TableViewerBuilder(panel, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
            ColumnViewerToolTipSupport.enableFor(propertiesViewer.getTableViewer());
            propertiesViewer.setContentProvider(new IStructuredContentProvider() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
                 */
                @Override
                public void dispose() {
                    // nothing to do
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
                 */
                @Override
                public Object[] getElements( Object inputElement ) {
                	Properties props = extraProperties;

                    if (props.isEmpty()) {
                        return new Object[0];
                    }
                    
                    List<SimpleProperty> properties= new ArrayList<SimpleProperty>();
                    
                    for( Object key : props.keySet() ) {
                    	String keyStr = (String)key;
                    	properties.add(new SimpleProperty(keyStr, (String)props.get(keyStr)));
                    }
                    return properties.toArray(new SimpleProperty[0]);
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                 *      java.lang.Object)
                 */
                @Override
                public void inputChanged( Viewer viewer,
                                          Object oldInput,
                                          Object newInput ) {
                    // nothing to do
                }
            });

            // sort the table rows by display name
            propertiesViewer.setComparator(new ViewerComparator() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
                 *      java.lang.Object)
                 */
                @Override
                public int compare( Viewer viewer,
                                    Object e1,
                                    Object e2 ) {
                    SimpleProperty prop1 = (SimpleProperty)e1;
                    SimpleProperty prop2 = (SimpleProperty)e2;

                    return super.compare(viewer, prop1.getName(), prop2.getName());
                }
            });

            GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(propertiesViewer.getTableComposite());
            ((GridData)propertiesViewer.getTable().getLayoutData()).heightHint = propertiesViewer.getTable().getItemHeight() * 6;

            // create columns
            TableViewerColumn column = propertiesViewer.createColumn(SWT.LEFT, 50, 40, true);
            column.getColumn().setText(UTIL.getString("ExtraPropertiesPanel_name") + "                   ");  //$NON-NLS-1$ //$NON-NLS-2$
            column.setLabelProvider(new PropertyLabelProvider(0));

            column = propertiesViewer.createColumn(SWT.LEFT, 50, 40, true);
            column.getColumn().setText(UTIL.getString("ExtraPropertiesPanel_value"));  //$NON-NLS-1$
            column.setLabelProvider(new PropertyLabelProvider(1));
            column.setEditingSupport(new PropertyNameEditingSupport(propertiesViewer.getTableViewer(), 1));

            propertiesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
                 */
                @Override
                public void selectionChanged( SelectionChangedEvent event ) {
                    handlePropertySelected();
                }
            });

            //
            // add toolbar below the table
            //
            
            Composite toolbarPanel = WidgetFactory.createPanel(panel, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 2);
            
            this.addPropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.addPropertyButton.setImage(DatatoolsUiPlugin.getDefault().getImage(Images.ADD_PROPERTY_ICON)); 
            this.addPropertyButton.setToolTipText(UTIL.getString("HeaderPropertiesPanel_addNewPropertyButton_tooltip")); //$NON-NLS-1$
            this.addPropertyButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleAddProperty();
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    			}
    		});
            
            this.removePropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.removePropertyButton.setImage(DatatoolsUiPlugin.getDefault().getImage(Images.REMOVE_PROPERTY_ICON));
            this.removePropertyButton.setToolTipText(UTIL.getString("HeaderPropertiesPanel_removePropertyButton_tooltip"));  //$NON-NLS-1$
            this.removePropertyButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleRemoveProperty();
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    			}
    		});
            this.removePropertyButton.setEnabled(false);
            
            propertiesViewer.setInput(this);
    	}
    	
    	void handlePropertySelected() {
    		boolean hasSelection = !propertiesViewer.getSelection().isEmpty();
    		this.removePropertyButton.setEnabled(hasSelection);
    	}
    	
        private SimpleProperty getSelectedProperty() {
            IStructuredSelection selection = (IStructuredSelection)propertiesViewer.getSelection();

            if (selection.isEmpty()) {
                return null;
            }

            return (SimpleProperty)selection.getFirstElement();
        }
    	
        void handleAddProperty() {
            assert (!propertiesViewer.getSelection().isEmpty());
            
            Set<String> keys = new HashSet<String>();
            for( Object key : extraProperties.keySet() ) {
            	keys.add((String)key);
            }

            AddHeaderPropertyDialog dialog = new AddHeaderPropertyDialog(propertiesViewer.getControl().getShell(), keys);

            if (dialog.open() == Window.OK) {
                // update model
                String name = dialog.getName();
                String value = dialog.getValue();
                extraProperties.put(name, value);

                // update UI from model
                propertiesViewer.refresh();

                // select the new property
                
                
                SimpleProperty prop = null;
                
                for(TableItem item : propertiesViewer.getTable().getItems() ) {
                	if( item.getData() instanceof SimpleProperty && ((SimpleProperty)item.getData()).getName().equals(name) ) {
                		prop = (SimpleProperty)item.getData();
                		break;
                	}
                }

                if( prop != null ) {
                    propertiesViewer.setSelection(new StructuredSelection(prop), true);
                }
            }
        }
        
        void handleRemoveProperty() {
            SimpleProperty selectedProperty = getSelectedProperty();
            assert (selectedProperty != null);

            // update model
            extraProperties.remove(selectedProperty.getName());

            // update UI
            propertiesViewer.refresh();
        }
    	
    	class PropertyLabelProvider extends ColumnLabelProvider {

            private final int columnID;

            public PropertyLabelProvider( int columnID ) {
                this.columnID = columnID;
            }

    		/* (non-Javadoc)
    		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
    		 */
    		@Override
    		public String getText(Object element) {
    			if( element instanceof SimpleProperty ) {
    				if( columnID == 0 ) {
    					return ((SimpleProperty)element).getName();
    				} else if( columnID == 1 ) {
    					return ((SimpleProperty)element).getValue();
    				}
    			}
    			return super.getText(element);
    		}
    	}

    }

    class PropertyNameEditingSupport extends EditingSupport {
    	int columnID;
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
         * @param viewer the viewer where the editing support is being provided (cannot be <code>null</code>)
		 * @param columnID the column id
		 */
		public PropertyNameEditingSupport(ColumnViewer viewer, int columnID) {
			super(viewer);
			this.columnID = columnID;
			this.editor = new TextCellEditor((Composite) viewer.getControl());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override
		protected Object getValue(Object element) {
			if( element instanceof SimpleProperty ) {
				if( columnID == 0 ) {
					return ((SimpleProperty)element).getName();
				} else if( columnID == 1 ) {
					return ((SimpleProperty)element).getValue();
				}
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			if( element instanceof SimpleProperty ) {
				if( columnID == 0 ) {
					String oldKey = ((SimpleProperty)element).getName();
					String oldValue = ((SimpleProperty)element).getValue();
					String newKey = (String)value;
					if( newKey != null && newKey.length() > 0 && !newKey.equalsIgnoreCase(oldKey)) {
						extraProperties.remove(oldKey);
						extraProperties.put(newKey,oldValue);
						propertiesViewer.refresh();
					}
				} else if( columnID == 1 ) {
					String key = ((SimpleProperty)element).getName();
					String oldValue = ((SimpleProperty)element).getValue();
					String newValue = (String)value;
					if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
						extraProperties.put(key,newValue);
						propertiesViewer.refresh();
					}
				}

			}
		}

	}
    
    class AddHeaderPropertyDialog  extends MessageDialog {

        private Button btnOk;
        private final Set<String> existingNames;
        private String name;
        private String value;

        /**
         * @param parentShell the parent shell (may be <code>null</code>)
         * @param existingPropertyNames the existing property names (can be <code>null</code>)
         */
        public AddHeaderPropertyDialog( Shell parentShell,
                                  Set<String> existingPropertyNames ) {
            super(parentShell, UTIL.getString("AddHeaderPropertyDialog_title"), null,   //$NON-NLS-1$
            		UTIL.getString("AddHeaderPropertyDialog_message"), MessageDialog.INFORMATION,  //$NON-NLS-1$
                    new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

            if( existingPropertyNames == null ) {
            	this.existingNames = new HashSet<String>(0);
            } else {
            	this.existingNames = existingPropertyNames;
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.MessageDialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
         */
        @Override
        protected Button createButton( Composite parent,
                                       int id,
                                       String label,
                                       boolean defaultButton ) {
            Button btn = super.createButton(parent, id, label, defaultButton);

            if (id == IDialogConstants.OK_ID) {
                // disable OK button initially
                this.btnOk = btn;
                btn.setEnabled(false);
            }

            return btn;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createCustomArea( Composite parent ) {
            Composite pnl = new Composite(parent, SWT.NONE);
            pnl.setLayout(new GridLayout(2, false));
            pnl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Label lblName = new Label(pnl, SWT.NONE);
            lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            lblName.setText(UTIL.getString("AddHeaderPropertyDialog_lblName_text"));  //$NON-NLS-1$

            Text txtName = new Text(pnl, SWT.BORDER);
            txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtName.setToolTipText(UTIL.getString("AddHeaderPropertyDialog_txtName_toolTip")); //$NON-NLS-1$
            txtName.addModifyListener(new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleNameChanged(((Text)e.widget).getText());
                }
            });

            Label lblValue = new Label(pnl, SWT.NONE);
            lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            lblValue.setText(UTIL.getString("AddHeaderPropertyDialog_lblValue_text"));  //$NON-NLS-1$

            Text txtValue = new Text(pnl, SWT.BORDER);
            txtValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtValue.setToolTipText(UTIL.getString("AddHeaderPropertyDialog_txtValue_toolTip"));  //$NON-NLS-1$
            txtValue.addModifyListener(new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleValueChanged(((Text)e.widget).getText());
                }
            });

            return pnl;
        }

        /**
         * @return the new property name (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public String getName() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return name;
        }
        
        /**
         * @return the new property value (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public String getValue() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return value;
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

        void handleNameChanged( String newName ) {
            this.name = newName;
            updateState();
        }

        void handleValueChanged( String newValue ) {
            this.value = newValue.trim();
            updateState();
        }

        private void updateState() {
            // check to see if new name is valid
            String msg = validateName();

            // empty message means field is valid
            if (StringUtilities.isEmpty(msg)) {
                // if name is valid check value
                msg = validateValue();
            }

            // update UI controls
            if (StringUtilities.isEmpty(msg)) {
                if (!this.btnOk.isEnabled()) {
                    this.btnOk.setEnabled(true);
                }

                if (this.imageLabel.getImage() != null) {
                    this.imageLabel.setImage(null);
                }

                this.imageLabel.setImage(getInfoImage());
                msg = UTIL.getString("AddHeaderPropertyDialog_message"); //$NON-NLS-1$
            } else {
                // value is not valid
                if (this.btnOk.isEnabled()) {
                    this.btnOk.setEnabled(false);
                }

                this.imageLabel.setImage(getErrorImage());
            }

            this.messageLabel.setText(msg);
            this.messageLabel.pack();
        }

        private String validateName() {
            String errorMsg = validateName(this.name);

            if (errorMsg == null) {
                // make sure property ID doesn't already exist
                for (String existingName : this.existingNames) {
                    if (existingName.equals(this.name)) {
                        errorMsg = UTIL.getString("AddHeaderPropertyDialog_customPropertyAlreadyExists", this.name); //$NON-NLS-1$
                        break;
                    }
                }
            }

            return errorMsg;
        }

        private String validateValue() {
            return validateValue(this.value);
        }
        
        /**
         * @param proposedName the proposed property name
         * @return an error message or <code>null</code> if name is valid
         */
        public String validateName( String proposedName ) {
            // must have a name
            if (StringUtilities.isEmpty(proposedName)) {
                return UTIL.getString("AddHeaderPropertyDialog_emptyPropertyName");  //$NON-NLS-1$
            }

            // make sure only letters
            for (char c : proposedName.toCharArray()) {
                if ( ! isValidChar(c)) {
                    return UTIL.getString("AddHeaderPropertyDialog_invalidPropertyName");  //$NON-NLS-1$
                }
            }

            // valid name
            return null;
        }
        
        private boolean isValidChar(char c) {
        	if((Character.isLetter(c) || Character.isDigit(c)) || c == '-' || c == '_' || c=='.') return true;
        	
        	return false;
        }
        
        /**
         * @param proposedValue the proposed value
         * @return an error message or <code>null</code> if value is valid
         */
        public String validateValue( String proposedValue ) {
            // must have a value
            if (StringUtilities.isEmpty(proposedValue)) {
                return UTIL.getString("AddHeaderPropertyDialog_emptyPropertyValue"); //$NON-NLS-1$
            }

            // valid
            return null;
        }

    }

}
