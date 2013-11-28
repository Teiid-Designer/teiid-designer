/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.editor.panels;

import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.ADD;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Images.REMOVE;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiPlugin;
import org.teiid.designer.vdb.ui.editor.VdbEditor;

/**
 *
 */
public class PropertiesPanel {
	static final String PREFIX = I18nUtil.getPropertyPrefix(PropertiesPanel.class);
	
    static final String INVALID_INTEGER_INPUT_TITLE = i18n("invalidQueryTimeoutValueTitle"); //$NON-NLS-1$
    static final String INVALID_INTEGER_INPUT_MESSAGE = i18n("invalidQueryTimeoutValueMessage"); //$NON-NLS-1$

    
	VdbEditor vdbEditor;
    

    ListViewer allowedLanguagesViewer;
	Button addLanguageButton;
	Button removeLanguageButton;
	
    TableViewer propertiesViewer;
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
    public PropertiesPanel(Composite parent, VdbEditor editor) {
    	super();
    	this.vdbEditor = editor;
    	
    	createPanel(parent);
    }
    
	private void createPanel(Composite parent) {
    	Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1, 2);
    	panel.setLayout(new GridLayout(2, false));

		Group propertiesGroup = WidgetFactory.createGroup(panel, prefixedI18n("teiid"), SWT.FILL, 1, 2);  //$NON-NLS-1$
		GridData gd_1 = new GridData(GridData.FILL_VERTICAL);
		gd_1.widthHint = 240;
		propertiesGroup.setLayoutData(gd_1);

		Label label = new Label(propertiesGroup, SWT.NONE);
		label.setText(i18n("queryTimeoutLabel")); //$NON-NLS-1$

		final Text queryTimeoutText = new Text(propertiesGroup, SWT.BORDER | SWT.SINGLE);
		queryTimeoutText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		queryTimeoutText.setText(Integer.toString(vdbEditor.getVdb().getQueryTimeout()));
    	queryTimeoutText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				try {
                    int valueInSecs = Integer.parseInt(queryTimeoutText.getText());
                    if (valueInSecs > -1) {
                        vdbEditor.getVdb().setQueryTimeout(valueInSecs);
					}
				} catch (NumberFormatException ex) {
					MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
                            INVALID_INTEGER_INPUT_TITLE,
                            INVALID_INTEGER_INPUT_MESSAGE);
					queryTimeoutText.setText(Integer.toString(vdbEditor.getVdb().getQueryTimeout()));
				}
				
			}
		});
    	
    	Label autGenRESTLabel = new Label(propertiesGroup, SWT.NONE);
    	autGenRESTLabel.setText(i18n("autoGenerateRESTWAR")); //$NON-NLS-1$
		final Button autoGenRESTCheckbox =  WidgetFactory.createCheckBox(propertiesGroup); 
		autoGenRESTCheckbox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
            	vdbEditor.getVdb().setAutoGenerateRESTWAR(((Button)e.getSource()).getSelection());
            }

            @Override
            public void widgetSelected( SelectionEvent e ) {
            	vdbEditor.getVdb().setAutoGenerateRESTWAR(((Button)e.getSource()).getSelection());
            }
        });
		
    	{
    		Group languageGroup = WidgetFactory.createGroup(propertiesGroup, prefixedI18n("allowedLanguages"), SWT.FILL, 2, 1);  //$NON-NLS-1$
    		GridData gd_2 = new GridData(GridData.FILL_VERTICAL);
    		gd_2.widthHint = 220;
    		gd_2.horizontalSpan = 2;
    		languageGroup.setLayoutData(gd_2);
    		// Add a simple list box entry form with String contents
        	this.allowedLanguagesViewer = new ListViewer(languageGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
            GridData data = new GridData(GridData.FILL_BOTH);
            data.horizontalSpan=2;
            this.allowedLanguagesViewer.getControl().setLayoutData(data);
            
            for( String value : vdbEditor.getVdb().getAllowedLanguages() ) {
            		this.allowedLanguagesViewer.add(value);
            }
            
            this.allowedLanguagesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
                 */
                @Override
                public void selectionChanged( SelectionChangedEvent event ) {
                    handleLanguageSelected();
                }
            });
            
            Composite toolbarPanel = WidgetFactory.createPanel(languageGroup, SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 2);
            
            this.addLanguageButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.addLanguageButton.setImage(VdbUiPlugin.singleton.getImage(ADD));
            this.addLanguageButton.setToolTipText(prefixedI18n("addLanguageButton.tooltip")); //$NON-NLS-1$
            this.addLanguageButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleAddLanguage();
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    			}
    		});
            
            this.removeLanguageButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
            this.removeLanguageButton.setImage(VdbUiPlugin.singleton.getImage(REMOVE));
            this.removeLanguageButton.setToolTipText(prefixedI18n("removeLangueButton.tooltip")); //$NON-NLS-1$
            this.removeLanguageButton.addSelectionListener(new SelectionListener() {
    			
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				handleRemoveLanguage();
    			}

    			@Override
    			public void widgetDefaultSelected(SelectionEvent e) {
    			}
    		});
            
            this.removeLanguageButton.setEnabled(false);
    	}
    	
        Composite pnlUserProperties = WidgetFactory.createGroup(panel, prefixedI18n("userDefined"), SWT.FILL, 1, 1);  //$NON-NLS-1$
        pnlUserProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.propertiesViewer = new TableViewer(pnlUserProperties, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));
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
                Map<String, String> props =  vdbEditor.getVdb().getGeneralProperties();

                if (props.isEmpty()) {
                    return new Object[0];
                }
                
                List<PropertiesPanel.Property> properties= new ArrayList<PropertiesPanel.Property>();
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

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
        column.getColumn().setText(prefixedI18n("name") + "                   ");  //$NON-NLS-1$//$NON-NLS-2$
        column.setLabelProvider(new PropertyLabelProvider(0));
        //column.setEditingSupport(new PropertyNameEditingSupport(this.propertiesViewer, 0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.propertiesViewer, SWT.LEFT);
        column.getColumn().setText(prefixedI18n("value")); //$NON-NLS-1$
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
	
	void handleLanguageSelected() {
		boolean hasSelection = !this.allowedLanguagesViewer.getSelection().isEmpty();
		this.removeLanguageButton.setEnabled(hasSelection);
	}

	void handlePropertySelected() {
		boolean hasSelection = !this.propertiesViewer.getSelection().isEmpty();
		this.removePropertyButton.setEnabled(hasSelection);
	}
	
    private String getSelectedLanguage() {
        IStructuredSelection selection = (IStructuredSelection)this.allowedLanguagesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (String)selection.getFirstElement();
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
        				vdbEditor.getVdb().getGeneralProperties().keySet());

        if (dialog.open() == Window.OK) {
            // update model
            String name = dialog.getName();
            String value = dialog.getValue();
            vdbEditor.getVdb().setGeneralProperty(name, value);

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
        this.vdbEditor.getVdb().removeGeneralProperty(selectedProperty.getName(), selectedProperty.getValue());

        // update UI
        this.propertiesViewer.refresh();
    }
    
    void handleAddLanguage() {
        assert (!this.propertiesViewer.getSelection().isEmpty());

        AddLanguagePropertyDialog dialog = 
        		new AddLanguagePropertyDialog(propertiesViewer.getControl().getShell(), 
        				vdbEditor.getVdb().getAllowedLanguages());


        if (dialog.open() == Window.OK) {
            // update model
            String language = dialog.getLanguage();

            vdbEditor.getVdb().addAllowedLanguage(language);

            // update UI from model
            this.allowedLanguagesViewer.add(language);
            
            this.propertiesViewer.refresh();

            // select the new property
            
            
            Property prop = null;
            
            for(TableItem item : this.propertiesViewer.getTable().getItems() ) {
            	if( item.getData() instanceof Property && ((Property)item.getData()).getName().equals(language) ) {
            		prop = (Property)item.getData();
            		break;
            	}
            }

            if( prop != null ) {
                this.propertiesViewer.setSelection(new StructuredSelection(prop), true);
            }
        }
    }
    
    void handleRemoveLanguage() {
        String selectedLanguage = getSelectedLanguage();
        assert (selectedLanguage != null);

        // update model
        this.vdbEditor.getVdb().removeAllowedLanguage(selectedLanguage);
        
        this.allowedLanguagesViewer.remove(selectedLanguage);
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
						vdbEditor.getVdb().removeGeneralProperty(oldKey, oldValue);
						vdbEditor.getVdb().setGeneralProperty(newKey, oldValue);
						propertiesViewer.refresh();
					}
				} else if( columnID == 1 ) {
					String key = ((Property)element).getName();
					String oldValue = ((Property)element).getValue();
					String newValue = (String)value;
					if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
						vdbEditor.getVdb().setGeneralProperty(key, newValue);
						propertiesViewer.refresh();
					}
				}

			}
		}

	}

}
