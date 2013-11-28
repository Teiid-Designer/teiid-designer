/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.teiidimporter.ui.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.teiidimporter.ui.Activator;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.wizard.TeiidImportManager;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 * Panel for managing the Optional Import Properties
 */
public class ImportPropertiesPanel {
	static final String PREFIX = I18nUtil.getPropertyPrefix(ImportPropertiesPanel.class);
	
    TableViewer propertiesViewer;
	Button addPropertyButton;
	Button removePropertyButton;
	TeiidImportManager importManager;
	private int visibleTableRows;
	
	/**
	 * Constructor
     * @param parent the parent Composite
     * @param importManager the TeiidImportManager
     * @param visibleTableRows the number of visible rows to be shown in the table
     */
    public ImportPropertiesPanel(Composite parent, TeiidImportManager importManager, int visibleTableRows) {
    	super();
    	this.importManager = importManager;
    	this.visibleTableRows = visibleTableRows;
    	createPanel(parent);
    }
    
    /*
     * create the panel
     * @param parent the parent composite
     */
	private void createPanel(Composite parent) {
    	Composite panel = WidgetFactory.createGroup(parent, Messages.ImportPropertiesPanel_groupTitle, SWT.FILL, 1, 1); 
    	panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        this.propertiesViewer = new TableViewer(panel, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
        ColumnViewerToolTipSupport.enableFor(this.propertiesViewer);
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
                Map<String, String> props =  importManager.getOptionalImportProps();

                if (props.isEmpty()) {
                    return new Object[0];
                }
                
                List<ImportPropertiesPanel.Property> properties= new ArrayList<ImportPropertiesPanel.Property>();
                for( String key : props.keySet() ) {
                	properties.add(new Property(key, props.get(key)));
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
                Property prop1 = (Property)e1;
                Property prop2 = (Property)e2;

                return super.compare(viewer, prop1.getName(), prop2.getName());
            }
        });

        Table table = this.propertiesViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData)table.getLayoutData()).horizontalSpan = 2;
        ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * this.visibleTableRows;

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
        column.getColumn().setText(Messages.ImportPropertiesPanel_name + "                   ");  //$NON-NLS-1$
        column.setLabelProvider(new PropertyLabelProvider(0));
        //column.setEditingSupport(new PropertyNameEditingSupport(this.propertiesViewer, 0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
        column.getColumn().setText(Messages.ImportPropertiesPanel_value); 
        column.setLabelProvider(new PropertyLabelProvider(1));
        column.setEditingSupport(new PropertyNameEditingSupport(this.propertiesViewer, 1));
        column.getColumn().pack();

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

        //
        // add toolbar below the table
        //
        
        Composite toolbarPanel = WidgetFactory.createPanel(panel, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 2);
        
        this.addPropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.addPropertyButton.setImage(Activator.getDefault().getImage(UiConstants.ImageIds.ADD_PROPERTY)); 
        this.addPropertyButton.setToolTipText(Messages.ImportPropertiesPanel_addNewPropertyButton_tooltip);
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
        this.removePropertyButton.setImage(Activator.getDefault().getImage(UiConstants.ImageIds.REMOVE_PROPERTY));
        this.removePropertyButton.setToolTipText(Messages.ImportPropertiesPanel_removePropertyButton_tooltip); 
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
        
        this.propertiesViewer.setInput(this);
	}
	
	void handlePropertySelected() {
		boolean hasSelection = !this.propertiesViewer.getSelection().isEmpty();
		this.removePropertyButton.setEnabled(hasSelection);
	}
	
    private Property getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)this.propertiesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (Property)selection.getFirstElement();
    }
	
    void handleAddProperty() {
        assert (!this.propertiesViewer.getSelection().isEmpty());

        AddGeneralPropertyDialog dialog = 
        		new AddGeneralPropertyDialog(propertiesViewer.getControl().getShell(), 
        				importManager.getOptionalImportProps().keySet());

        if (dialog.open() == Window.OK) {
            // update model
            String name = dialog.getName();
            String value = dialog.getValue();
            importManager.addOptionalImportProperty(name, value);

            // update UI from model
            this.propertiesViewer.refresh();

            // select the new property
            
            
            Property prop = null;
            
            for(TableItem item : this.propertiesViewer.getTable().getItems() ) {
            	if( item.getData() instanceof Property && ((Property)item.getData()).getName().equals(name) ) {
            		prop = (Property)item.getData();
            		break;
            	}
            }

            if( prop != null ) {
                this.propertiesViewer.setSelection(new StructuredSelection(prop), true);
            }
        }
    }
    
    void handleRemoveProperty() {
        Property selectedProperty = getSelectedProperty();
        assert (selectedProperty != null);

        // update model
        importManager.removeOptionalImportProperty(selectedProperty.getName());

        // update UI
        this.propertiesViewer.refresh();
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
				        importManager.removeOptionalImportProperty(oldKey);
				        importManager.addOptionalImportProperty(newKey,oldValue);
						propertiesViewer.refresh();
					}
				} else if( columnID == 1 ) {
					String key = ((Property)element).getName();
					String oldValue = ((Property)element).getValue();
					String newValue = (String)value;
					if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
				        importManager.addOptionalImportProperty(key,newValue);
						propertiesViewer.refresh();
					}
				}

			}
		}

	}

}
