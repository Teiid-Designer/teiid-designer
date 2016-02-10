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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.ui.common.table.ComboBoxEditingSupport;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 * @since 8.6
 */
public class ParameterPanel implements DatatoolsUiConstants {
	static final String PREFIX = I18nUtil.getPropertyPrefix(ParameterPanel.class);
	
    TableViewerBuilder propertiesViewer;
	Button addPropertyButton;
	Button removePropertyButton;
	Button upPropertyButton, downPropertyButton;
	private Map<String, Parameter> parameterMap;
	private int visibleTableRows;
	private WSProfileDetailsWizardPage wsProfileDetailsWizardPage;
	private PropertyPage propertyPage;
	
	/**
	 * Constructor
	 * @param wsProfileDetailsWizardPage 
     * @param parent the parent Composite
	 * @param parameterMap 
     * @param propertiesManager the TeiidpropertiesManager
     * @param visibleTableRows the number of visible rows to be shown in the table
     */
    public ParameterPanel(WSProfileDetailsWizardPage wsProfileDetailsWizardPage, Composite parent, Map<String, Parameter> parameterMap, int visibleTableRows) {
    	super();
    	this.parameterMap = parameterMap;
    	this.visibleTableRows = visibleTableRows;
    	this.wsProfileDetailsWizardPage = wsProfileDetailsWizardPage;
    	createPanel(parent);
    }
    /**
	 * Constructor
     * @param propertyPage 
     * @param parent the parent Composite
     * @param parameterMap 
     * @param propertiesManager the TeiidpropertiesManager
     * @param visibleTableRows the number of visible rows to be shown in the table
     */
    public ParameterPanel(PropertyPage propertyPage, Composite parent, Map<String, Parameter> parameterMap, int visibleTableRows) {
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
		
    	Composite panel = WidgetFactory.createGroup(parent, StringConstants.EMPTY_STRING, SWT.FILL, 2, 1);  //$NON-NLS-1$
    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    	gd.horizontalSpan = 2;
    	panel.setLayoutData(gd);
    	
    	//
        // add toolbar above the table
        //
        
        Composite toolbarPanel = WidgetFactory.createPanel(panel, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 4);
        ((GridLayout)toolbarPanel.getLayout()).marginHeight = 0;
        
        this.addPropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.addPropertyButton.setImage(DatatoolsUiPlugin.getDefault().getImage(Images.ADD_PROPERTY_ICON)); 
        this.addPropertyButton.setToolTipText(UTIL.getString("ParametersPanel_addNewParameterButton_tooltip")); //$NON-NLS-1$
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
        this.removePropertyButton.setToolTipText(UTIL.getString("ParametersPanel_removeParameterButton_tooltip"));  //$NON-NLS-1$
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
        
        this.upPropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.upPropertyButton.setImage(DatatoolsUiPlugin.getDefault().getImage(Images.UP_PROPERTY_ICON));
        this.upPropertyButton.setToolTipText(UTIL.getString("ParametersPanel_upParameterButton_tooltip"));  //$NON-NLS-1$
        this.upPropertyButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				handleMoveColumnUp();
			}
    		
		});
        
        this.upPropertyButton.setEnabled(false);
        
        this.downPropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.downPropertyButton.setImage(DatatoolsUiPlugin.getDefault().getImage(Images.DOWN_PROPERTY_ICON));
        this.downPropertyButton.setToolTipText(UTIL.getString("ParametersPanel_downParameterButton_tooltip"));  //$NON-NLS-1$
        this.downPropertyButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleMoveColumnDown();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        this.downPropertyButton.setEnabled(false);

        this.propertiesViewer = new TableViewerBuilder(panel, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
        GridDataFactory.fillDefaults().grab(true, true).span(3, 1).hint(360,  100).applyTo(propertiesViewer.getTableComposite());

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
            @Override
            public Object[] getElements( Object inputElement ) {

                if (parameterMap == null || parameterMap.isEmpty()) {
                    return new Object[0];
                }
          
                Map<String, Parameter> parameterValues = new LinkedHashMap<String, Parameter>();
                for (Parameter parameter: parameterMap.values()){
                	if (!parameter.getType().equals(Parameter.Type.Header))
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

                return 0;
            }
        });

        Table table = this.propertiesViewer.getTable();
        ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * this.visibleTableRows;

        // create columns
        TableViewerColumn column = propertiesViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(UTIL.getString("ParametersPanel_name") + "                   ");  //$NON-NLS-1$ //$NON-NLS-2$
        column.setLabelProvider(new PropertyLabelProvider(0));
        //column.setEditingSupport(new PropertyNameEditingSupport(this.propertiesViewer, 0));

        column = propertiesViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(UTIL.getString("ParametersPanel_type"));  //$NON-NLS-1$
        column.getColumn().setToolTipText(UTIL.getString("AddParameterDialog_txtType_toolTip"));
        column.setLabelProvider(new PropertyLabelProvider(1));
        column.setEditingSupport(new ParameterTypeComboEditingSupport(this.propertiesViewer.getTableViewer()));
        
        column = propertiesViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(UTIL.getString("ParametersPanel_default_value"));  //$NON-NLS-1$
        column.getColumn().setToolTipText(UTIL.getString("AddParameterDialog_txtDefaultValue_toolTip"));
        column.setLabelProvider(new PropertyLabelProvider(2));
        column.setEditingSupport(new PropertyNameEditingSupport(this.propertiesViewer.getTableViewer(), 2));
        
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
		boolean  sel = this.propertiesViewer.getTable().getSelectionIndex()>0;
		this.upPropertyButton.setEnabled(sel);
		this.downPropertyButton.setEnabled(this.propertiesViewer.getTable().getSelectionIndex()+1<this.propertiesViewer.getTable().getItemCount());
	}
	
	public void handleMoveColumnUp() {
		int startIndex = this.propertiesViewer.getTable().getSelectionIndex();
		if( startIndex > 0 ) {
			// Make Copy of List & get columnInfo of startIndex-1
			TableItem[] existingColumns = this.propertiesViewer.getTable().getItems();
			TableItem priorColumn = existingColumns[startIndex-1];
			existingColumns[startIndex-1] = this.propertiesViewer.getTable().getItem(startIndex);
			existingColumns[startIndex] = priorColumn;
			
			List<TableItem> newColumns = new ArrayList<TableItem>(existingColumns.length);
			Map<String, Parameter> oldParameterMap = new LinkedHashMap<String, Parameter>(parameterMap);
			this.parameterMap.clear();
			for( TableItem info : existingColumns) {
				newColumns.add(info);
				this.parameterMap.put(Parameter.PREFIX+((Parameter)info.getData()).getName(), oldParameterMap.get(Parameter.PREFIX+((Parameter)info.getData()).getName()));
			}
			
			this.propertiesViewer.getTable().setData(newColumns);
			this.propertiesViewer.refresh();
			updatePreviewUrl();
			handlePropertySelected();
			
		}
	}
	
	public void handleMoveColumnDown() {
		int startIndex = this.propertiesViewer.getTable().getSelectionIndex();
		if( startIndex < this.propertiesViewer.getTable().getItems().length) {
			// Make Copy of List & get columnInfo of startIndex+1
			TableItem[] existingColumns = this.propertiesViewer.getTable().getItems();
			TableItem afterColumn = existingColumns[startIndex+1];
			existingColumns[startIndex+1] = this.propertiesViewer.getTable().getItem(startIndex);
			existingColumns[startIndex] = afterColumn;
			
			List<TableItem> newColumns = new ArrayList<TableItem>(existingColumns.length);
			Map<String, Parameter> oldParameterMap = new LinkedHashMap<String, Parameter>(parameterMap);
			this.parameterMap.clear();
			for( TableItem info : existingColumns) {
				newColumns.add(info);
				this.parameterMap.put(Parameter.PREFIX+((Parameter)info.getData()).getName(), oldParameterMap.get(Parameter.PREFIX+((Parameter)info.getData()).getName()));
			}
			
			this.propertiesViewer.getTable().setData(newColumns);
			this.propertiesViewer.refresh();
			updatePreviewUrl();
			handlePropertySelected();
			
		}
	}
	
    private Parameter getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)this.propertiesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (Parameter)selection.getFirstElement();
    }
	
    void handleAddProperty() {
        assert (!this.propertiesViewer.getSelection().isEmpty());
        if (this.parameterMap == null) this.parameterMap = new LinkedHashMap<String, Parameter>();
        Set<String> keys = new HashSet<String>();
        for( Object key : parameterMap.keySet() ) {
        	keys.add((String)key);
        }


        AddParameterDialog dialog = new AddParameterDialog(propertiesViewer.getControl().getShell(), keys);

        if (dialog.open() == Window.OK) {
            // update model
            String name = dialog.getName();
            String type = dialog.getType() != null ? dialog.getType() : IWSProfileConstants.QUERY_STRING;
            String defaultValue = dialog.getDefaultValue();
            Parameter parameter = new Parameter(name, defaultValue, Parameter.Type.fromValue(type));

            this.parameterMap.put(Parameter.PREFIX+name, parameter);
            
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
            
            updatePreviewUrl();
        }
    }
	/**
	 * 
	 */
	private void updatePreviewUrl() {
		if (this.wsProfileDetailsWizardPage!=null){
			for( Object key : this.parameterMap.keySet() )  {
				Parameter para = (Parameter)this.parameterMap.get(key);
				wsProfileDetailsWizardPage.getProfileProperties().put(para.getPropertyKey(), para.getPropertyValue());
			}
			wsProfileDetailsWizardPage.setParameterMap(this.parameterMap);
			wsProfileDetailsWizardPage.urlPreviewText.setText(wsProfileDetailsWizardPage.updateUrlPreview().toString());
		}else{
			for( Object key : this.parameterMap.keySet() )  {
				Parameter para = (Parameter)this.parameterMap.get(key);
				propertyPage.getExtraProperties().put(para.getPropertyKey(), para.getPropertyValue());
			}
			propertyPage.setParameterMap(this.parameterMap);
			propertyPage.urlPreviewText.setText(propertyPage.updateUrlPreview().toString());
		}
	}
    
    void handleRemoveProperty() {
    	Parameter selectedProperty = getSelectedProperty();
        assert (selectedProperty != null);

        // update model
        parameterMap.remove(Parameter.PREFIX+selectedProperty.getName());

        // update UI
        this.propertiesViewer.refresh();
        
        updatePreviewUrl();
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
					return ((Parameter)element).getType().toString();
				} else if( columnID == 2 ) {
					return ((Parameter)element).getDefaultValue();
				}
			}
			return super.getText(element);
		}
	}
	
    class ParameterTypeComboEditingSupport extends ComboBoxEditingSupport {
    	
    	private String[] datatypes = { IWSProfileConstants.QUERY_STRING, IWSProfileConstants.URI };
        /**
         * @param viewer
         */
        public ParameterTypeComboEditingSupport( ColumnViewer viewer ) {
            super(viewer);
        }

        @Override
        protected String getElementValue( Object element ) {
        	return ((Parameter)element).getType().toString();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element, String newValue ) {
        	Parameter param = (Parameter)element;
			String key = param.getPropertyKey();
			String oldType = param.getType().toString();

			String newType = (String)newValue;
			if( newType != null && newType.length() > 0 && !newType.equalsIgnoreCase(oldType)) {
				param.setType(Parameter.Type.fromValue(newType));
				parameterMap.put(key,param);
				propertiesViewer.refresh(param);
				if( wsProfileDetailsWizardPage != null) {
					wsProfileDetailsWizardPage.setParameterMap(parameterMap);
				} else {
					propertyPage.setParameterMap(parameterMap);
				}
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
			if( element instanceof Parameter ) {
				if( columnID == 0 ) {
					return ((Parameter)element).getName();
				} else if( columnID == 1 ) {
					return ((Parameter)element).getType().toString();
				} else if( columnID == 2 ) {
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
			if( element instanceof Parameter ) {
				String key = ((Parameter)element).getPropertyKey();
				if( columnID == 2 ) {
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
    class AddParameterDialog  extends MessageDialog {

        private Button btnOk;
        private final Set<String> existingNames;
        private String name;
        private String type;
        private String defaultValue;

        /**
         * @param parentShell the parent shell (may be <code>null</code>)
         * @param existingPropertyNames the existing property names (can be <code>null</code>)
         */
        public AddParameterDialog( Shell parentShell,
                                  Set<String> existingPropertyNames ) {
            super(parentShell, UTIL.getString("AddParameterDialog_title"), null,   //$NON-NLS-1$
            		UTIL.getString("AddParameterDialog_message"), MessageDialog.INFORMATION,  //$NON-NLS-1$
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
            lblName.setText(UTIL.getString("AddParameterDialog_lblName_text"));  //$NON-NLS-1$

            Text txtName = new Text(pnl, SWT.BORDER);
            txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtName.setToolTipText(UTIL.getString("AddParameterDialog_txtName_toolTip")); //$NON-NLS-1$
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
            lblValue.setText(UTIL.getString("AddParameterDialog_lblType_text"));  //$NON-NLS-1$

            Combo txtValue = WidgetFactory.createCombo(pnl,
                    SWT.SIMPLE,
                    GridData.FILL_HORIZONTAL);
            GridData gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.verticalAlignment = GridData.BEGINNING;
            gd.grabExcessHorizontalSpace = true;
            txtValue.setLayoutData(gd);
            txtValue.add(IWSProfileConstants.QUERY_STRING);
            txtValue.add(IWSProfileConstants.URI);
            txtValue.select(0);
            txtValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtValue.setToolTipText(UTIL.getString("AddParameterDialog_txtType_toolTip"));  //$NON-NLS-1$
            txtValue.addSelectionListener(new SelectionListener() {

    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleTypeChanged(((Combo)e.widget).getText());
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    			}
    		});

            txtValue.setVisibleItemCount(2);
            
            Label lblDefaultValue = new Label(pnl, SWT.NONE);
            lblDefaultValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            lblDefaultValue.setText(UTIL.getString("AddParameterDialog_lblDefaultValue_text"));  //$NON-NLS-1$

            Text txtDefaultValue = new Text(pnl, SWT.BORDER);
            txtDefaultValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtDefaultValue.setToolTipText(UTIL.getString("AddParameterDialog_txtDefaultValue_toolTip")); //$NON-NLS-1$
            txtDefaultValue.addModifyListener(new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleDefaultValueChanged((((Text)e.widget).getText()));
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
        public String getType() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return type;
        }

        /**
		 * @return the defaultValue
		 */
		public String getDefaultValue() {
			return defaultValue;
		}

		/**
		 * @param defaultValue the defaultValue to set
		 */
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
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
        
        void handleDefaultValueChanged( String newDefaultValue ) {
            this.setDefaultValue(newDefaultValue);
            updateState();
        }

        void handleTypeChanged(Object type) {
            this.type = (String)type;
            updateState();
            updatePreviewUrl();
        }

        private void updateState() {
            // check to see if new name is valid
            String msg = validateName();
            
            if (StringUtilities.isEmpty(msg)) {
	            // check to see if new default value is valid
	            msg = StringUtilities.isEmpty(this.defaultValue) ? UTIL.getString("AddParameterDialog_emptyDefaultValue") : msg;  //$NON-NLS-1$
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
            } else {
                // value is not valid
                if (this.btnOk.isEnabled()) {
                    this.btnOk.setEnabled(false);
                }

                this.imageLabel.setImage(getErrorImage());
            }

            if (!StringUtilities.isEmpty(msg)) {
            	this.messageLabel.setText(msg);
            }
            this.messageLabel.pack();
        }

        private String validateName() {
            String errorMsg = validateName(this.name);

            if (errorMsg == null) {
                // make sure property ID doesn't already exist
                for (String existingName : this.existingNames) {
                    if (existingName.equals(this.name)) {
                        errorMsg = UTIL.getString("AddParameterDialog_customParameterAlreadyExists", this.name); //$NON-NLS-1$
                        break;
                    }
                }
            }

            return errorMsg;
        }

        /**
         * @param proposedName the proposed property name
         * @return an error message or <code>null</code> if name is valid
         */
        public String validateName( String proposedName ) {
            // must have a name
            if (StringUtilities.isEmpty(proposedName)) {
                return UTIL.getString("AddParameterDialog_emptyParameterName");  //$NON-NLS-1$
            }

            // make sure only letters
            for (char c : proposedName.toCharArray()) {
                if ( ! isValidChar(c)) {
                    return UTIL.getString("AddParameterDialog_invalidParameterName");  //$NON-NLS-1$
                }
            }

            // valid name
            return null;
        }
        
        private boolean isValidChar(char c) {
        	if((Character.isLetter(c) || Character.isDigit(c))) return true;
        	
        	return false;
        }
        
    }

}
