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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.datatools.connectivity.model.Parameter;
import org.teiid.datatools.connectivity.model.Parameter.Type;
import org.teiid.designer.core.translators.SimpleProperty;
import org.teiid.designer.datatools.profiles.ws.ParameterPanel.AddParameterDialog;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 *
 */
public class HeaderPropertiesPanel implements DatatoolsUiConstants {
	static final String PREFIX = I18nUtil.getPropertyPrefix(HeaderPropertiesPanel.class);
	
    TableViewerBuilder propertiesViewer;
	Button addPropertyButton;
	Button removePropertyButton;
	private Map<String, Parameter> parameterMap;
	private int visibleTableRows;
	
	private WSProfileDetailsWizardPage wsProfileDetailsWizardPage;
	private PropertyPage propertyPage;
	
	/**
	 * Constructor
     * @param parent the parent Composite
     * @param propertiesManager the TeiidpropertiesManager
     * @param visibleTableRows the number of visible rows to be shown in the table
     */
    public HeaderPropertiesPanel(WSProfileDetailsWizardPage wsProfileDetailsWizardPage, Composite parent, Map<String, Parameter> parameterMap, int visibleTableRows) {
    	super();
    	this.parameterMap = parameterMap;
    	this.visibleTableRows = visibleTableRows;
    	this.wsProfileDetailsWizardPage = wsProfileDetailsWizardPage;
    	createPanel(parent);
    }
    

	/**
	 * Constructor
     * @param parent the parent Composite
     * @param propertiesManager the TeiidpropertiesManager
     * @param visibleTableRows the number of visible rows to be shown in the table
     */
    public HeaderPropertiesPanel(PropertyPage propertyPage, Composite parent, Map<String, Parameter> parameterMap, int visibleTableRows) {
    	super();
    	this.parameterMap = parameterMap;
    	this.visibleTableRows = visibleTableRows;
    	this.propertyPage = propertyPage;
    	createPanel(parent);
    }
    
    /*
     * create the panel
     * @param parent the parent composite
     */
	private void createPanel(Composite parent) {
    	Composite panel = WidgetFactory.createGroup(parent, StringConstants.EMPTY_STRING, SWT.FILL, 2, 1); 
    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    	gd.horizontalSpan = 2;
    	panel.setLayoutData(gd);
    	
        //
        // add toolbar above the table
        //
        
        Composite toolbarPanel = WidgetFactory.createPanel(panel, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 2);
        ((GridLayout)toolbarPanel.getLayout()).marginHeight = 0;
        
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

        this.propertiesViewer = new TableViewerBuilder(panel, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(propertiesViewer.getTableComposite());

        ColumnViewerToolTipSupport.enableFor(this.propertiesViewer.getTableViewer());
        this.propertiesViewer.setContentProvider(new IStructuredContentProvider() {
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
//            @Override
//            public Object[] getElements( Object inputElement ) {
//            	Properties props = profileProperties;
//
//                if (props.isEmpty()) {
//                    return new Object[0];
//                }
//                
//                List<Parameter> properties= new ArrayList<Parameter>();
//                for( Object key : props.keySet() ) {
//                	String keyStr = (String)key;
//                	boolean yes = keyStr.startsWith(Parameter.HEADER_PREFIX);
//                	if( keyStr.startsWith(Parameter.HEADER_PREFIX) ){
//                		properties.add((Parameter)props.get(key));
//                	}
//                }
//                return properties.toArray(new Parameter[0]);
//            }
            
            @Override
            public Object[] getElements( Object inputElement ) {

                if (parameterMap == null || parameterMap.isEmpty()) {
                    return new Object[0];
                }
          
                Map<String, Parameter> parameterValues = new LinkedHashMap<String, Parameter>();
                for (Parameter parameter: parameterMap.values()){
                	if (parameter.getType().equals(Parameter.Type.Header))
                		parameterValues.put(parameter.getPropertyKey(), parameter);
                }
                return parameterValues.values().toArray();
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
        this.propertiesViewer.setComparator(new ViewerComparator() {
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
            	Parameter prop1 = (Parameter)e1;
            	Parameter prop2 = (Parameter)e2;

                return super.compare(viewer, prop1.getName(), prop2.getName());
            }
        });

        Table table = this.propertiesViewer.getTable();
        ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * this.visibleTableRows;

        // create columns
        TableViewerColumn column = propertiesViewer.createColumn(SWT.LEFT, 50, 50, true);
        column.getColumn().setText(UTIL.getString("HeaderPropertiesPanel_name") + "                   ");  //$NON-NLS-1$ //$NON-NLS-2$
        column.setLabelProvider(new PropertyLabelProvider(0));
        //column.setEditingSupport(new PropertyNameEditingSupport(this.propertiesViewer, 0));

        column = propertiesViewer.createColumn(SWT.LEFT, 50, 50, true);
        column.getColumn().setText(UTIL.getString("HeaderPropertiesPanel_value"));  //$NON-NLS-1$
        column.setLabelProvider(new PropertyLabelProvider(1));
        column.setEditingSupport(new PropertyNameEditingSupport(this.propertiesViewer.getTableViewer(), 1));

        this.propertiesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
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
        
        this.propertiesViewer.setInput(this);
	}
	
	void handlePropertySelected() {
		boolean hasSelection = !this.propertiesViewer.getSelection().isEmpty();
		this.removePropertyButton.setEnabled(hasSelection);
	}
	
    private Parameter getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)this.propertiesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (Parameter)selection.getFirstElement();
    }
    
    /**
	 * 
	 */
	private void updateProperties() {
		if (this.wsProfileDetailsWizardPage!=null){
			for( Object key : this.parameterMap.keySet() )  {
				Parameter para = (Parameter)this.parameterMap.get(key);
				wsProfileDetailsWizardPage.getProfileProperties().put(para.getPropertyKey(), para.getPropertyValue());
			}
			wsProfileDetailsWizardPage.setParameterMap(this.parameterMap);
		}else{
			for( Object key : this.parameterMap.keySet() )  {
				Parameter para = (Parameter)this.parameterMap.get(key);
				propertyPage.getExtraProperties().put(para.getPropertyKey(), para.getPropertyValue());
			}
			propertyPage.setParameterMap(this.parameterMap);
		}
	}
	
    void handleAddProperty() {
    	 assert (!this.propertiesViewer.getSelection().isEmpty());
         if (this.parameterMap == null) this.parameterMap = new LinkedHashMap<String, Parameter>();
         Set<String> keys = new HashSet<String>();
         for( Object key : parameterMap.keySet() ) {
         	keys.add((String)key);
         }

         AddHeaderPropertyDialog dialog = new AddHeaderPropertyDialog(propertiesViewer.getControl().getShell(), keys);

         if (dialog.open() == Window.OK) {
             // update model
             String name = dialog.getName();
             String defaultValue = dialog.getValue();
             Parameter parameter = new Parameter(name, defaultValue, Parameter.Type.Header);

             this.parameterMap.put(Parameter.HEADER_PREFIX+name, parameter);
             
             // update UI from model
             this.propertiesViewer.refresh();

             // select the new property
             
             Parameter prop = null;
             
             for(TableItem item : this.propertiesViewer.getTable().getItems() ) {
             	if( item.getData() instanceof Parameter && ((Parameter)item.getData()).getName().equals(name) ) {
             		prop = (Parameter)item.getData();
             		break;
             	}
             }

             if( prop != null ) {
                 this.propertiesViewer.setSelection(new StructuredSelection(prop), true);
             }
         }
         
         updateProperties();
    }
    
    void handleRemoveProperty() {
    	Parameter selectedProperty = getSelectedProperty();
        assert (selectedProperty != null);

        // update model
        parameterMap.remove(Parameter.HEADER_PREFIX+selectedProperty.getName());

        // update UI
        this.propertiesViewer.refresh();
        
        updateProperties();
        
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
			if( element instanceof Parameter ) {
				if( columnID == 0 ) {
					return ((Parameter)element).getName();
				} else if( columnID == 1 ) {
					return ((Parameter)element).getDefaultValue();
				}
			}
			return super.getText(element);
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
			if( element instanceof Parameter ) {
				if( columnID == 0 ) {
					return ((Parameter)element).getName();
				} else if( columnID == 1 ) {
					return ((Parameter)element).getDefaultValue();
				}
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
//			if( element instanceof Parameter ) {
//				if( columnID == 0 ) {
//					String oldKey = ((Parameter)element).getName();
//					String oldValue = ((Parameter)element).getDefaultValue();
//					String newKey = (String)value;
//					if( newKey != null && newKey.length() > 0 && !newKey.equalsIgnoreCase(oldKey)) {
//						profileProperties.remove(Parameter.HEADER_PREFIX+oldKey);
//						profileProperties.put(Parameter.HEADER_PREFIX+newKey,oldValue);
//						propertiesViewer.refresh();
//					}
//				} else if( columnID == 1 ) {
//					String key = Parameter.HEADER_PREFIX+((Parameter)element).getName();
//					String oldValue = ((Parameter)element).getDefaultValue();
//					String newValue = (String)value;
//					if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
//						profileProperties.put(key,newValue);
//						propertiesViewer.refresh();
//					}
//				}
//
//			}
//		}
			if( element instanceof Parameter ) {
				String key = ((Parameter)element).getPropertyKey();
				if( columnID == 1 ) {
					String oldDefaultValue = StringConstants.EMPTY_STRING;
					if( ((Parameter)element).getDefaultValue() != null ) {
						oldDefaultValue = ((Parameter)element).getDefaultValue();
					}
					String newDefaultValue = (String)value;
					if( newDefaultValue != null && newDefaultValue.length() > 0 && !newDefaultValue.equalsIgnoreCase(oldDefaultValue)) {
						((Parameter)element).setDefaultValue(newDefaultValue);
						parameterMap.put(key, (Parameter)element);
						propertiesViewer.refresh(element);
					}
				}
				
			}
			
			if (wsProfileDetailsWizardPage!=null){
            	for( Object key : parameterMap.keySet() )  {
            		Parameter para = (Parameter)parameterMap.get(key);
            		wsProfileDetailsWizardPage.getProfileProperties().put(para.getPropertyKey(), para.getPropertyValue());
            	}
            	wsProfileDetailsWizardPage.setParameterMap(parameterMap);
            	wsProfileDetailsWizardPage.urlPreviewText.setText(wsProfileDetailsWizardPage.updateUrlPreview().toString());
            }else{
            	for( Object key : parameterMap.keySet() )  {
            		Parameter para = (Parameter)parameterMap.get(key);
            		propertyPage.getExtraProperties().put(para.getPropertyKey(), para.getPropertyValue());
            	}
            	propertyPage.setParameterMap(parameterMap);
            	propertyPage.urlPreviewText.setText(propertyPage.updateUrlPreview().toString());
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
