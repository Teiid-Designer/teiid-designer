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
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.profiles.ws.PropertyPage.ExtraPropertiesPanel.Property;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.ui.common.ICredentialsCommon;
import org.teiid.designer.ui.common.ICredentialsCommon.SecurityType;
import org.teiid.designer.ui.common.util.WidgetFactory;

public class PropertyPage extends ProfileDetailsPropertyPage implements
		IContextProvider, DatatoolsUiConstants {

	
	private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
			DatatoolsUiPlugin.getDefault().getBundle().getSymbolicName());
	private Composite scrolled;
	private Label usernameLabel;
	private Text usernameText;
	private Label passwordLabel;
	private Text passwordText;
	private Label urlLabel;
	private Text urlText;
	private Text securityText;
	private Label securityLabel;
	
	private TableViewer propertiesViewer;
	
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

        scrolled = new Composite(group, SWT.NONE);
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

        securityLabel = new Label(scrolled, SWT.NONE);
        securityLabel.setText(UTIL.getString("Common.Security.Type.Label")); //$NON-NLS-1$
        securityLabel.setToolTipText(UTIL.getString("Common.Security.Type.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.CENTER;
        securityLabel.setLayoutData(gd);

        securityText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        securityText.setToolTipText(UTIL.getString("Common.Security.Type.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        securityText.setLayoutData(gd);

        usernameLabel = new Label(scrolled, SWT.NONE);
        usernameLabel.setText(UTIL.getString("Common.Username.Label")); //$NON-NLS-1$
        usernameLabel.setToolTipText(UTIL.getString("Common.Username.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.CENTER;
        usernameLabel.setLayoutData(gd);

        usernameText = new Text(scrolled, SWT.SINGLE | SWT.BORDER);
        usernameText.setToolTipText(UTIL.getString("Common.Username.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        usernameText.setLayoutData(gd);

        passwordLabel = new Label(scrolled, SWT.NONE);
        passwordLabel.setText(UTIL.getString("Common.Password.Label")); //$NON-NLS-1$
        passwordLabel.setToolTipText(UTIL.getString("Common.Password.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.verticalAlignment = GridData.CENTER;
        passwordLabel.setLayoutData(gd);

        passwordText = new Text(scrolled, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        passwordText.setToolTipText(UTIL.getString("Common.Password.ToolTip")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        passwordText.setLayoutData(gd);

        initControls();
        
        new ExtraPropertiesPanel(scrolled);

        addlisteners();
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
        usernameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

        passwordText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

        urlText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });
        securityText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText( ModifyEvent e ) {
                validate();
            }
        });

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
        //An empty or null security type value is treated the same as "None"
        String securityType = securityText.getText();
        if (securityType!=null && (!securityType.trim().equals(StringUtilities.EMPTY_STRING) &&
        	!SecurityType.None.name().equals(securityType)) &&
                !SecurityType.HTTPBasic.name().equals(securityText.getText())) {
        	errorMessage = UTIL.getString("Common.Security.Error.Message"); //$NON-NLS-1$
    		valid = false;
    		setErrorMessage(errorMessage);
            setValid(valid);
            return;
        }
        if (securityType!=null && !securityType.trim().equals(StringUtilities.EMPTY_STRING) &&
           (!SecurityType.None.name().equals(securityType))) {
        	if (null == passwordText.getText() || passwordText.getText().isEmpty()) {
        		errorMessage = UTIL.getString("Common.Password.Error.Message"); //$NON-NLS-1$
        		valid = false;
        		setErrorMessage(errorMessage);
                setValid(valid);
                return;
        	}
        	if (null == usernameText.getText() || usernameText.getText().isEmpty()) {
        		errorMessage = UTIL.getString("Common.Username.Error.Message"); //$NON-NLS-1$
        		valid = false;
        		setErrorMessage(errorMessage);
                setValid(valid);
                return;
        	}
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
        if (null != props.get(ICredentialsCommon.USERNAME_PROP_ID)) {
            usernameText.setText((String)props.get(ICredentialsCommon.USERNAME_PROP_ID));
        }
        if (null != props.get(ICredentialsCommon.PASSWORD_PROP_ID)) {
            passwordText.setText((String)props.get(ICredentialsCommon.PASSWORD_PROP_ID));
        }
        String url = ConnectionInfoHelper.readEndPointProperty(props);
        if (null != url) {
            urlText.setText(url);
        }
        if (null != props.get(ICredentialsCommon.SECURITY_TYPE_ID)) {
            securityText.setText((String)props.get(ICredentialsCommon.SECURITY_TYPE_ID));
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
        result.setProperty(ICredentialsCommon.SECURITY_TYPE_ID, securityText.getText().trim());
        result.setProperty(ICredentialsCommon.USERNAME_PROP_ID, usernameText.getText());
        result.setProperty(ICredentialsCommon.PASSWORD_PROP_ID, passwordText.getText());
        
        for( Object key : extraProperties.keySet() ) {
        	result.put(key, extraProperties.get(key));
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

            propertiesViewer = new TableViewer(panel, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
            ColumnViewerToolTipSupport.enableFor(propertiesViewer);
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
                    
                    List<Property> properties= new ArrayList<Property>();
                    
                    for( Object key : props.keySet() ) {
                    	String keyStr = (String)key;
                    	properties.add(new Property(keyStr, (String)props.get(keyStr)));
                    }
                    return properties.toArray(new Property[0]);
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
                    Property prop1 = (Property)e1;
                    Property prop2 = (Property)e2;

                    return super.compare(viewer, prop1.getName(), prop2.getName());
                }
            });

            Table table = propertiesViewer.getTable();
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(new TableLayout());
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)table.getLayoutData()).horizontalSpan = 2;
            ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * 6;

            // create columns
            TableViewerColumn column = new TableViewerColumn(propertiesViewer, SWT.LEFT);
            column.getColumn().setText(UTIL.getString("ExtraPropertiesPanel_name") + "                   ");  //$NON-NLS-1$ //$NON-NLS-2$
            column.setLabelProvider(new PropertyLabelProvider(0));
            //column.setEditingSupport(new PropertyNameEditingSupport(propertiesViewer, 0));
            column.getColumn().pack();

            column = new TableViewerColumn(propertiesViewer, SWT.LEFT);
            column.getColumn().setText(UTIL.getString("ExtraPropertiesPanel_value"));  //$NON-NLS-1$
            column.setLabelProvider(new PropertyLabelProvider(1));
            column.setEditingSupport(new PropertyNameEditingSupport(propertiesViewer, 1));
            column.getColumn().pack();
            
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
    	
        private Property getSelectedProperty() {
            IStructuredSelection selection = (IStructuredSelection)propertiesViewer.getSelection();

            if (selection.isEmpty()) {
                return null;
            }

            return (Property)selection.getFirstElement();
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
                
                
                Property prop = null;
                
                for(TableItem item : propertiesViewer.getTable().getItems() ) {
                	if( item.getData() instanceof Property && ((Property)item.getData()).getName().equals(name) ) {
                		prop = (Property)item.getData();
                		break;
                	}
                }

                if( prop != null ) {
                    propertiesViewer.setSelection(new StructuredSelection(prop), true);
                }
            }
        }
        
        void handleRemoveProperty() {
            Property selectedProperty = getSelectedProperty();
            assert (selectedProperty != null);

            // update model
            extraProperties.remove(selectedProperty.getName());

            // update UI
            propertiesViewer.refresh();
        }
    	    		
    	class Property {
    		private String name;
    		private String value;
    		
    		public Property(String name, String value) {
    			super();
    			this.name = name;
    			this.value = value;
    		}

    		/**
    		 * @return the name
    		 */
    		public String getName() {
    			return this.name;
    		}

    		/**
    		 * @param name the name to set
    		 */
    		public void setName(String name) {
    			this.name = name;
    		}

    		/**
    		 * @return the value
    		 */
    		public String getValue() {
    			return this.value;
    		}

    		/**
    		 * @param value the value to set
    		 */
    		public void setValue(String value) {
    			this.value = value;
    		}
    		
    	    /**
    	     * {@inheritDoc}
    	     * 
    	     * @see java.lang.Object#equals(java.lang.Object)
    	     */
    	    @Override
    	    public boolean equals( Object obj ) {
    	        if (this == obj) {
    	            return true;
    	        }

    	        if (obj == null) {
    	            return false;
    	        }

    	        if (!getClass().equals(obj.getClass())) {
    	            return false;
    	        }

    	        return this.getName().equals(((Property)obj).getName());
    	    }
    		
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
    			if( element instanceof Property ) {
    				if( columnID == 0 ) {
    					return ((Property)element).getName();
    				} else if( columnID == 1 ) {
    					return ((Property)element).getValue();
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
			if( element instanceof Property ) {
				if( columnID == 0 ) {
					return ((Property)element).getName();
				} else if( columnID == 1 ) {
					return ((Property)element).getValue();
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
			if( element instanceof Property ) {
				if( columnID == 0 ) {
					String oldKey = ((Property)element).getName();
					String oldValue = ((Property)element).getValue();
					String newKey = (String)value;
					if( newKey != null && newKey.length() > 0 && !newKey.equalsIgnoreCase(oldKey)) {
						extraProperties.remove(oldKey);
						extraProperties.put(newKey,oldValue);
						propertiesViewer.refresh();
					}
				} else if( columnID == 1 ) {
					String key = ((Property)element).getName();
					String oldValue = ((Property)element).getValue();
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
