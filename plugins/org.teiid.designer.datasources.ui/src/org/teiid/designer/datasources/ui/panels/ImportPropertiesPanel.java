/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datasources.ui.panels;

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
import org.eclipse.swt.widgets.TableItem;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.translators.SimpleProperty;
import org.teiid.designer.datasources.ui.Messages;
import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.UiPlugin;
import org.teiid.designer.datasources.ui.wizard.TeiidDataSourceManager;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;

/**
 * Panel for managing the Optional Import Properties
 */
public class ImportPropertiesPanel {
	static final String PREFIX = I18nUtil.getPropertyPrefix(ImportPropertiesPanel.class);
	
    TableViewerBuilder propertiesViewerBuilder;
	Button addPropertyButton;
	Button removePropertyButton;
	TeiidDataSourceManager importManager;
	
	/**
	 * Constructor
     * @param parent the parent Composite
     * @param importManager the TeiidImportManager
     */
    public ImportPropertiesPanel(Composite parent, TeiidDataSourceManager importManager) {
    	super();
    	this.importManager = importManager;
    	createPanel(parent);
    }
    
    /*
     * create the panel
     * @param parent the parent composite
     */
	private void createPanel(Composite parent) {
    	Composite panel = WidgetFactory.createGroup(parent, Messages.ImportPropertiesPanel_groupTitle, SWT.FILL, 1, 1); 
    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    	gd.heightHint = 150;
    	panel.setLayoutData(gd);

    	this.propertiesViewerBuilder = new TableViewerBuilder(panel, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        ColumnViewerToolTipSupport.enableFor(this.propertiesViewerBuilder.getTableViewer());
        this.propertiesViewerBuilder.setContentProvider(new IStructuredContentProvider() {
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
                
                List<SimpleProperty> properties= new ArrayList<SimpleProperty>();
                for( String key : props.keySet() ) {
                	properties.add(new SimpleProperty(key, props.get(key)));
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
        this.propertiesViewerBuilder.setComparator(new ViewerComparator() {
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

        // create columns
        TableViewerColumn column = this.propertiesViewerBuilder.createColumn(SWT.LEFT, 50, 50, true);
        column.getColumn().setText(Messages.ImportPropertiesPanel_name);
        column.setLabelProvider(new PropertyLabelProvider(0));

        column = this.propertiesViewerBuilder.createColumn(SWT.LEFT, 50, 50, true);
        column.getColumn().setText(Messages.ImportPropertiesPanel_value); 
        column.setLabelProvider(new PropertyLabelProvider(1));
        column.setEditingSupport(new PropertyNameEditingSupport(this.propertiesViewerBuilder.getTableViewer(), 1));

        this.propertiesViewerBuilder.addSelectionChangedListener(new ISelectionChangedListener() {
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
        this.addPropertyButton.setImage(UiPlugin.getDefault().getImage(UiConstants.IMAGES.ADD_PROPERTY)); 
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
        this.removePropertyButton.setImage(UiPlugin.getDefault().getImage(UiConstants.IMAGES.REMOVE_PROPERTY));
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
        
        this.propertiesViewerBuilder.setInput(this);
	}
	
	void handlePropertySelected() {
		boolean hasSelection = !this.propertiesViewerBuilder.getSelection().isEmpty();
		this.removePropertyButton.setEnabled(hasSelection);
	}
	
    private SimpleProperty getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)this.propertiesViewerBuilder.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (SimpleProperty)selection.getFirstElement();
    }
	
    void handleAddProperty() {
        assert (!this.propertiesViewerBuilder.getSelection().isEmpty());

        AddGeneralPropertyDialog dialog = 
        		new AddGeneralPropertyDialog(propertiesViewerBuilder.getControl().getShell(), 
        				importManager.getOptionalImportProps().keySet());

        if (dialog.open() == Window.OK) {
            // update model
            String name = dialog.getName();
            String value = dialog.getValue();
            importManager.addOptionalImportProperty(name, value);

            // update UI from model
            this.propertiesViewerBuilder.getTableViewer().refresh();

            // select the new property
            
            
            SimpleProperty prop = null;
            
            for(TableItem item : this.propertiesViewerBuilder.getTable().getItems() ) {
            	if( item.getData() instanceof SimpleProperty && ((SimpleProperty)item.getData()).getName().equals(name) ) {
            		prop = (SimpleProperty)item.getData();
            		break;
            	}
            }

            if( prop != null ) {
                this.propertiesViewerBuilder.getTableViewer().setSelection(new StructuredSelection(prop), true);
            }
        }
    }
    
    void handleRemoveProperty() {
        SimpleProperty selectedProperty = getSelectedProperty();
        assert (selectedProperty != null);

        // update model
        importManager.removeOptionalImportProperty(selectedProperty.getName());

        // update UI
        this.propertiesViewerBuilder.getTableViewer().refresh();
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
				        importManager.removeOptionalImportProperty(oldKey);
				        importManager.addOptionalImportProperty(newKey,oldValue);
						propertiesViewerBuilder.getTableViewer().refresh();
					}
				} else if( columnID == 1 ) {
					String key = ((SimpleProperty)element).getName();
					String oldValue = ((SimpleProperty)element).getValue();
					String newValue = (String)value;
					if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
				        importManager.addOptionalImportProperty(key,newValue);
						propertiesViewerBuilder.getTableViewer().refresh();
					}
				}

			}
		}

	}

}
