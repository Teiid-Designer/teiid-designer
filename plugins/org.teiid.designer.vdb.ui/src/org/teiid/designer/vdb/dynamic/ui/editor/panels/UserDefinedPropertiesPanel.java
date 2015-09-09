package org.teiid.designer.vdb.dynamic.ui.editor.panels;

import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.ADD;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.REMOVE;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.translators.SimpleProperty;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.dynamic.ui.editor.dialogs.AddGeneralPropertyDialog;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

/**
 * @author blafond
 *
 */
public class UserDefinedPropertiesPanel {
	static final String PREFIX = I18nUtil.getPropertyPrefix(UserDefinedPropertiesPanel.class);

    
	Vdb vdb;
	
    TableViewerBuilder propertiesViewer;
	Button addPropertyButton;
	Button removePropertyButton;

	
    static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }
    
    static String prefixedI18n( final String id ) {
        return VdbUiConstants.Util.getString(PREFIX + id);
    }
	
	/**
     * @param parent
     * @param editor
     */
    public UserDefinedPropertiesPanel(Composite parent, Vdb vdb) {
    	super();
    	this.vdb = vdb;
    	
    	createPanel(parent);
    }
    
	private void createPanel(Composite parent) {
        Composite pnlUserProperties = WidgetFactory.createGroup(parent, null, SWT.FILL, 1, 1);
        pnlUserProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.propertiesViewer = new TableViewerBuilder(pnlUserProperties, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
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
                Properties props=  vdb.getProperties();

                if (props.isEmpty()) {
                    return new Object[0];
                }
                
                List<SimpleProperty> properties= new ArrayList<SimpleProperty>();
                for( Object key : props.keySet() ) {
                	String keyStr = (String)key;
                	String value = props.getProperty(keyStr);
                	properties.add(new SimpleProperty(keyStr, value));
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
                SimpleProperty prop1 = (SimpleProperty)e1;
                SimpleProperty prop2 = (SimpleProperty)e2;

                return super.compare(viewer, prop1.getName(), prop2.getName());
            }
        });

        GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(propertiesViewer.getTableComposite());

        // create columns
        TableViewerColumn column = propertiesViewer.createColumn(SWT.LEFT, 50, 40, true);
        column.getColumn().setText(prefixedI18n("name") + "                                                ");  //$NON-NLS-1$//$NON-NLS-2$
        column.setLabelProvider(new PropertyLabelProvider(0));
        //column.setEditingSupport(new PropertyNameEditingSupport(this.propertiesViewer, 0));


        column = propertiesViewer.createColumn(SWT.LEFT, 50, 40, true);
        column.getColumn().setText(prefixedI18n("value")); //$NON-NLS-1$
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

        //
        // add toolbar below the table
        //
        
        Composite toolbarPanel = WidgetFactory.createPanel(pnlUserProperties, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 2);
        
        this.addPropertyButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.addPropertyButton.setImage(VdbUiPlugin.singleton.getImage(ADD));
        this.addPropertyButton.setToolTipText(prefixedI18n("addNewPropertyButton.tooltip")); //$NON-NLS-1$
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
        this.removePropertyButton.setImage(VdbUiPlugin.singleton.getImage(REMOVE));
        this.removePropertyButton.setToolTipText(prefixedI18n("removePropertyButton.tooltip")); //$NON-NLS-1$
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
	
    private SimpleProperty getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)this.propertiesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (SimpleProperty)selection.getFirstElement();
    }
	
    void handleAddProperty() {
        assert (!this.propertiesViewer.getSelection().isEmpty());

        Set<String> existingPropNames = new HashSet<String>();
        for( Object key : vdb.getProperties().keySet() ) {
        	existingPropNames.add((String)key);
        }
        
        AddGeneralPropertyDialog dialog = new AddGeneralPropertyDialog(propertiesViewer.getControl().getShell(), existingPropNames);

        if (dialog.open() == Window.OK) {
            // update model
            String name = dialog.getName();
            String value = dialog.getValue();
            vdb.setProperty(name, value);

            // update UI from model
            this.propertiesViewer.refresh();

            // select the new property
            
            
            SimpleProperty prop = null;
            
            for(TableItem item : this.propertiesViewer.getTable().getItems() ) {
            	if( item.getData() instanceof SimpleProperty && ((SimpleProperty)item.getData()).getName().equals(name) ) {
            		prop = (SimpleProperty)item.getData();
            		break;
            	}
            }

            if( prop != null ) {
                this.propertiesViewer.setSelection(new StructuredSelection(prop), true);
            }
        }
    }
    
    void handleRemoveProperty() {
        SimpleProperty selectedProperty = getSelectedProperty();
        assert (selectedProperty != null);

        // update model
        this.vdb.removeProperty(selectedProperty.getName());

        // update UI
        this.propertiesViewer.refresh();
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
		 * @param columnID 
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
						vdb.removeProperty(oldKey);
						vdb.setProperty(newKey, oldValue);
						propertiesViewer.refresh();
					}
				} else if( columnID == 1 ) {
					String key = ((SimpleProperty)element).getName();
					String oldValue = ((SimpleProperty)element).getValue();
					String newValue = (String)value;
					if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
						vdb.setProperty(key, newValue);
						propertiesViewer.refresh();
					}
				}

			}
		}

	}

}