/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.editor.panels;

import static org.teiid.core.designer.util.StringConstants.EMPTY_STRING;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.table.ResourceEditingSupport;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbIndexedEntry.Problem;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.connections.SourceHandlerExtensionManager;
import org.teiid.designer.vdb.ui.Messages;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

/**
 *
 */
public class ModelDetailsPanel {
    static final String DESCRIPTION_COLUMN_NAME = i18n("descriptionColumnName"); //$NON-NLS-1$;
    
    static String i18n( final String id ) {
        return VdbUiConstants.Util.getString(id);
    }
    
    Text modelNameText;
    Text modelLocationText;
    StyledTextEditor modelDescriptionEditor;
    Button multiSourceCB, addColumnCB, addButton, deleteButton;
    Text columnAliasText;
    Label columnAliaslabel;
    TableViewerBuilder bindingsViewer;
    TableViewerBuilder problemsViewer;
    VdbModelEntry selectedVdbModelEntry;
    
    Vdb vdb;


    /**
     * @param parent
     * @param vdb
     */
    public ModelDetailsPanel(Composite parent, Vdb vdb) {
    	super();
    	this.vdb = vdb;
    	
    	createPanel(parent);
    }
    
    @SuppressWarnings("unused")
	private void createPanel(Composite parent) {
		Composite mainPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1, 1);
		Composite headerPanel = WidgetFactory.createPanel(mainPanel, SWT.NONE, GridData.FILL_HORIZONTAL, 1, 2);
		headerPanel.setLayout(new GridLayout(2, false));
    	
		Label label = new Label(headerPanel, SWT.NONE);
		label.setText("Name"); //$NON-NLS-1$

		this.modelNameText = new Text(headerPanel, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		this.modelNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.modelNameText.setBackground(headerPanel.getBackground());
		this.modelNameText.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
		
		label = new Label(headerPanel, SWT.NONE);
		label.setText("Location"); //$NON-NLS-1$

		this.modelLocationText = new Text(headerPanel, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		this.modelLocationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.modelLocationText.setBackground(headerPanel.getBackground());
		this.modelLocationText.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
    	
    	
        CTabFolder tabFolder = WidgetFactory.createTabFolder(mainPanel);
        
        CTabItem bindingsTab = new CTabItem(tabFolder, SWT.NONE);
        bindingsTab.setText(Messages.modelDetailsPanel_sourceBindingDefinition);
        bindingsTab.setToolTipText(Messages.modelDetailsPanel_sourceBindingDefinitionTooltip);

		Composite sourceBindingsGroup = new Composite(tabFolder, SWT.NONE);
		sourceBindingsGroup.setLayout(new GridLayout(1, false));
		sourceBindingsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		bindingsTab.setControl(sourceBindingsGroup);
        
		SOURCE_BINDING_PANEL : {

			Composite subPanel_1 = WidgetFactory.createPanel(sourceBindingsGroup, SWT.NONE, GridData.FILL_HORIZONTAL, 1, 4);
			subPanel_1.setLayout(new GridLayout(4, false));
			// Options panel contains: multi-source check-box, add column checkbox and source name alias label + text entry box
			multiSourceCB = WidgetFactory.createButton(subPanel_1, Messages.modelDetailsPanel_multiSourceLabel, SWT.NONE, 1, SWT.CHECK);
			multiSourceCB.setToolTipText(Messages.modelDetailsPanel_multiSourceCheckBoxTooltip);
			multiSourceCB.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					if( selectedVdbModelEntry != null ) {
						selectedVdbModelEntry.getSourceInfo().setIsMultiSource(multiSourceCB.getSelection());
					}
					refreshModelDetails();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// DO NOTHING
				}
			});
			
			addColumnCB = WidgetFactory.createButton(subPanel_1, Messages.modelDetailsPanel_addColumnLabel + "      ", SWT.NONE, 1, SWT.CHECK); //$NON-NLS-1$
			addColumnCB.setToolTipText(Messages.modelDetailsPanel_addColumnCheckBoxTooltip);
			addColumnCB.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					if( selectedVdbModelEntry != null ) {
						selectedVdbModelEntry.getSourceInfo().setAddColumn(addColumnCB.getSelection());
					}
					refreshModelDetails();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// DO NOTHING
				}
			});
			columnAliaslabel = new Label(subPanel_1, SWT.NONE);
			columnAliaslabel.setText("Column Alias"); //$NON-NLS-1$
			columnAliaslabel.setToolTipText(Messages.modelDetailsPanel_columnAliaslabelTooltip);
			columnAliasText = new Text(subPanel_1, SWT.BORDER | SWT.SINGLE);
			columnAliasText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			columnAliasText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					selectedVdbModelEntry.getSourceInfo().setColumnAlias(columnAliasText.getText());
				}
			});
			
			Composite lowerPanel = WidgetFactory.createPanel(sourceBindingsGroup, SWT.NONE, GridData.FILL_BOTH, 2, 1);
			lowerPanel.setLayout(new GridLayout(2, false));
	        GridData gridData = new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING); 
	        gridData.grabExcessHorizontalSpace = true;
	        gridData.grabExcessVerticalSpace = true;
	        lowerPanel.setLayoutData(gridData);

	        // Vertical Buttons panel for ADD and DELETE buttons
			Composite leftToolbarPanel = WidgetFactory.createPanel(lowerPanel, SWT.NONE, GridData.FILL_VERTICAL);
			GridLayout tbGL = new GridLayout();
			tbGL.marginHeight = 0;
			tbGL.marginWidth = 0;
			tbGL.verticalSpacing = 2;
			leftToolbarPanel.setLayout(tbGL);

			addButton = new Button(leftToolbarPanel, SWT.PUSH);
			addButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
			addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			addButton.setEnabled(false);
			addButton.setToolTipText(Messages.modelDetailsPanel_addButtonTooltip);
			addButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					int numSources = selectedVdbModelEntry.getSourceInfo().getSources().size();
					boolean added = false;
					while(!added) {
						String newName = "Name_" + numSources; //$NON-NLS-1$
						added = selectedVdbModelEntry.getSourceInfo().add(newName, "JNDI_NAME", "TRANSLATOR_NAME"); //$NON-NLS-1$ //$NON-NLS-2$
						if(!added) numSources++;
					}
					refreshModelDetails();
				}

			});

			deleteButton = new Button(leftToolbarPanel, SWT.PUSH);
			deleteButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
			deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			deleteButton.setEnabled(false);
			deleteButton.setToolTipText(Messages.modelDetailsPanel_deleteButtonTooltip);
			deleteButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					IStructuredSelection selection = (IStructuredSelection)bindingsViewer.getSelection();
					for( Object obj : selection.toArray()) {
						if( obj instanceof VdbSource ) {
							selectedVdbModelEntry.getSourceInfo().removeSource((VdbSource) obj);
							refreshModelDetails();
						}
					}
				}

			});
			
			// Table containing Source  binding NAME, TRANSLATOR NAME, JNDI NAME
			BINDING_TABLE : {
		        // Create Table Viewer
		        int tableStyle = SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
		        bindingsViewer = new TableViewerBuilder(lowerPanel, tableStyle);        

		        // create columns
		        TableViewerColumn column = bindingsViewer.createColumn(SWT.LEFT, 30, 30, true);
		        column.getColumn().setText(Messages.modelDetailsPanel_sourceNameLabel + "                      "); //$NON-NLS-1$
		        column.setEditingSupport(new SourceNameTextEditingSupport(bindingsViewer.getTableViewer()));
		        column.setLabelProvider(new BindingDataLabelProvider(0));

		        
		        column = bindingsViewer.createColumn(SWT.LEFT, 30, 30, true);
		        column.getColumn().setText(Messages.modelDetailsPanel_translatorNameLabel + "            "); //$NON-NLS-1$
		        column.setEditingSupport(new TranslatorEditingSupport(bindingsViewer.getTableViewer(), vdb.getSourceFile()));
		        column.setLabelProvider(new BindingDataLabelProvider(1));


		        column = bindingsViewer.createColumn(SWT.LEFT, 30, 30, true);
		        column.getColumn().setText(Messages.modelDetailsPanel_jndiNameLabel + "          "); //$NON-NLS-1$
		        column.setEditingSupport(new JndiEditingSupport(bindingsViewer.getTableViewer(), vdb.getSourceFile()));
		        column.setLabelProvider(new BindingDataLabelProvider(2));

		        
		        bindingsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
					
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection sel = (IStructuredSelection)event.getSelection();
						
						if( sel.isEmpty()) {
							deleteButton.setEnabled(false);
						} else if( selectedVdbModelEntry.getSourceInfo().isMultiSource()) {
							boolean enable = true;
							Object[] objs = sel.toArray();
							for( Object obj : objs) {
								if(  !(obj instanceof VdbSource)) {
									enable = false;
									break;
								}
							} 
							if( objs.length == 0 ) {
								enable = false;
							}
							deleteButton.setEnabled(enable);
						}
					}
				});
			}
		}
		
        // model details tab

        CTabItem modelsTab = new CTabItem(tabFolder, SWT.NONE);
        modelsTab.setText(i18n("description")); //$NON-NLS-1$
        modelsTab.setToolTipText(Messages.modelDetailsPanel_modelDetailsTooltip);

        Composite modelDetailsPanel = new Composite(tabFolder, SWT.NONE);
        modelDetailsPanel.setLayout(new GridLayout(1, false));
        modelDetailsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		((GridData)modelDetailsPanel.getLayoutData()).widthHint = 200;
		modelsTab.setControl(modelDetailsPanel);
        
		NAME_LOCATION_DESCRIPTION_PANEL : {
	    	
	        this.modelDescriptionEditor = new StyledTextEditor(modelDetailsPanel, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
	        final GridData gridData = new GridData(GridData.FILL_BOTH);
	        gridData.horizontalSpan = 1;

	        this.modelDescriptionEditor.setLayoutData(gridData);
	        //this.modelDescriptionEditor.setText(vdb.getDescription());
	        this.modelDescriptionEditor.getDocument().addDocumentListener(new IDocumentListener() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	             */
	            @Override
	            public void documentAboutToBeChanged( final DocumentEvent event ) {
	                // nothing to do
	            }

	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
	             */
	            @Override
	            public void documentChanged( final DocumentEvent event ) {
	            	if( selectedVdbModelEntry != null ) {
	            		selectedVdbModelEntry.setDescription(modelDescriptionEditor.getText());
	            	}
	            }

	        });
	        
        	selectedVdbModelEntry = null;
        	modelNameText.setText(Messages.noSelection);
        	modelLocationText.setText(Messages.noSelection);
        	modelDescriptionEditor.setText(EMPTY_STRING);
        	modelDescriptionEditor.getTextViewer().setEditable(false);
		}
        
		
        CTabItem problemsTab = new CTabItem(tabFolder, SWT.NONE);
        problemsTab.setText(Messages.modelDetailsPanel_problemsTabLabel);
        problemsTab.setToolTipText(Messages.modelDetailsPanel_problemsTabTooltip);
        
        Composite modelProblemsPanel = new Composite(tabFolder, SWT.NONE);
        modelProblemsPanel.setLayout(new GridLayout(1, false));
        modelProblemsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		((GridData)modelProblemsPanel.getLayoutData()).widthHint = 200;
		problemsTab.setControl(modelProblemsPanel);
		
		MODEL_PROBLEMS_PANEL : {
			/*
	<validation-error path=""PARTSSUPPLIER.PERSON"/AGE" severity="WARNING">Integer datatype would result in a bigInteger runtimetype.</validation-error>
	<validation-error path=""PARTSSUPPLIER.PARTS" severity="ERROR">The name ("PARTSSUPPLIER.PARTS) contains the '.' delimiter. This name is required to be wrapped in double-quotes. </validation-error>
			*/
			// Need a table to display "PATH, SEVERITY, MESSAGE"
			Composite subPanel_1 = WidgetFactory.createPanel(modelProblemsPanel, SWT.NONE, GridData.FILL_BOTH, 1, 1);
			subPanel_1.setLayout(new GridLayout(1, false));
			
			PROBLEMS_TABLE : {
		        // Create Table Viewer
		        int tableStyle = SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
		        problemsViewer = new TableViewerBuilder(subPanel_1, tableStyle);

		        // create columns
		        TableViewerColumn column = problemsViewer.createColumn(SWT.LEFT, 30, 40, true);
		        column.getColumn().setText(Messages.modelDetailsPanel_problemPathLabel + "                                    "); //$NON-NLS-1$
		        column.setLabelProvider(new ProblemMarkerLabelProvider(0));

		        
		        column = problemsViewer.createColumn(SWT.LEFT, 30, 40, true);
		        column.getColumn().setText("      "); //$NON-NLS-1$
		        column.setLabelProvider(new ProblemMarkerLabelProvider(1));

		        
		        column = problemsViewer.createColumn(SWT.LEFT, 30, 40, true);
		        column.getColumn().setText(Messages.modelDetailsPanel_problemDescriptionLabel + "            "); //$NON-NLS-1$
		        column.setLabelProvider(new ProblemMarkerLabelProvider(2));

			}

		}
		
		tabFolder.setSelection(0);
		
		multiSourceCB.setEnabled(false);
		addColumnCB.setEnabled(false);
		columnAliasText.setEnabled(false);
		addButton.setEnabled(false);
		deleteButton.setEnabled(false);
		columnAliaslabel.setEnabled(false);
		columnAliasText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
    }
    
    /**
     * 
     */
    public void refreshModelDetails() {
        if (selectedVdbModelEntry == null) {
        	modelNameText.setText(Messages.noSelection);
        	modelLocationText.setText(Messages.noSelection);
        	modelDescriptionEditor.setText(EMPTY_STRING);
        	bindingsViewer.getTable().removeAll();
    		multiSourceCB.setEnabled(false);
    		addColumnCB.setEnabled(false);
    		columnAliasText.setEnabled(false);
    		addButton.setEnabled(false);
    		deleteButton.setEnabled(false);
    		columnAliaslabel.setEnabled(false);
    		columnAliasText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
    		problemsViewer.getTable().removeAll();
    		modelDescriptionEditor.getTextViewer().setEditable(false);
        } else {
        	modelNameText.setText(selectedVdbModelEntry.getName().lastSegment());
        	modelLocationText.setText(selectedVdbModelEntry.getName().removeLastSegments(1).toString());
        	modelDescriptionEditor.setText(selectedVdbModelEntry.getDescription());
        	modelDescriptionEditor.getTextViewer().setEditable(true);
        	bindingsViewer.getTable().removeAll();
        	for( VdbSource vdbSource : selectedVdbModelEntry.getSourceInfo().getSources() ) {
        		bindingsViewer.add(vdbSource);
        	}
        	problemsViewer.getTable().removeAll();
        	for( Problem prob : selectedVdbModelEntry.getProblems() ) {
        		problemsViewer.add(prob);
        	}
        	
        	boolean enable = selectedVdbModelEntry.getSourceInfo().isMultiSource();
        	multiSourceCB.setEnabled(true);
    		multiSourceCB.setSelection(enable);
    		addColumnCB.setEnabled(enable);
    		columnAliasText.setEnabled(enable);
    		addButton.setEnabled(enable);
    		deleteButton.setEnabled(selectedVdbModelEntry.getSourceInfo().getSourceCount() > 1 && !bindingsViewer.getSelection().isEmpty());
    		columnAliaslabel.setEnabled(enable);
    		if( enable ) {
    			columnAliasText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    		} else {
    			columnAliasText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
    		}
        }
    	
    }

	/**
	 * @param selection the selected VdbModelEntry to set
	 */
	public void setSelectedVdbModelEntry(VdbModelEntry selection) {
		this.selectedVdbModelEntry = selection;
		refreshModelDetails();
	}

	class BindingDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public BindingDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof VdbSource ) {
				switch (this.columnNumber) {
					case 0: {
						return ((VdbSource)element).getName();
					}
					case 1: {
						return ((VdbSource)element).getTranslatorName();
					}
					case 2: {
						return ((VdbSource)element).getJndiName();
					}
				}
			}
			return EMPTY_STRING;
		}
	}
	
	class ProblemMarkerLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public ProblemMarkerLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof Problem ) {
				switch (this.columnNumber) {
					case 0: {
						return ((Problem)element).getLocation();
					}
					case 2: {
						return ((Problem)element).getMessage();
					}
				}
			}
			return EMPTY_STRING;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element) {
			if( element instanceof Problem ) {
				switch (this.columnNumber) {
					case 1: {
						int severity = ((Problem)element).getSeverity();
			            if (severity == IMarker.SEVERITY_ERROR) {
			                return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.ERROR_ICON);
			            }
			            return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.WARNING_ICON);
					}
				}
			}
			
			return null;
		}
		
		
	}

    class TranslatorEditingSupport extends ResourceEditingSupport {

        /**
         * @param viewer
         * @param vdb
         */
        public TranslatorEditingSupport( ColumnViewer viewer,
                                         IResource vdb ) {
            super(viewer, vdb);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ResourceEditingSupport#canAddNewValue(java.lang.Object)
         */
        @Override
        protected boolean canAddNewValue( Object element ) {
            return true;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ResourceEditingSupport#getElementValue(java.lang.Object)
         */
        @Override
        protected String getElementValue( Object element ) {
            return ((VdbSource)element).getTranslatorName();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ResourceEditingSupport#refreshItems(java.lang.Object)
         */
        @Override
        protected String[] refreshItems( Object element ) {
            List<String> translators = new ArrayList<String>();
            // get the available translators from the server
            String[] serverTypes = SourceHandlerExtensionManager.getVdbConnectionFinder().getTranslatorTypes();

            if (serverTypes != null) {
                translators.addAll(Arrays.asList(serverTypes));
            }

            // add in the translator overrides from the VDB
            for (TranslatorOverride translator : vdb.getTranslators()) {
                translators.add(translator.getName());
            }

            Collections.sort(translators);
            return translators.toArray(new String[translators.size()]);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ResourceEditingSupport#setElementValue(java.lang.Object, java.lang.String)
         */
        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            if (newValue == null) {
                newValue = ""; //$NON-NLS-1$
            }

            ((VdbSource)element).setTranslatorName(newValue);
                        
            refreshModelDetails();
            
            // cause a selection event to be fired so that actions can set their enablement
            bindingsViewer.setSelection(new StructuredSelection(element));
        }
    }

    class JndiEditingSupport extends ResourceEditingSupport {

        /**
         * @param viewer
         * @param vdb
         */
        public JndiEditingSupport( ColumnViewer viewer,
                                   IResource vdb ) {
            super(viewer, vdb);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ResourceEditingSupport#canAddNewValue(java.lang.Object)
         */
        @Override
        protected boolean canAddNewValue( Object element ) {
            return true;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ResourceEditingSupport#getElementValue(java.lang.Object)
         */
        @Override
        protected String getElementValue( Object element ) {
            return ((VdbSource)element).getJndiName();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ResourceEditingSupport#refreshItems(java.lang.Object)
         */
        @Override
        protected String[] refreshItems( Object element ) {
            List<String> dsJndiNameList = new ArrayList<String>();
            
            // get the available datasource JNDI names from the server
            String[] dsJndiNames = SourceHandlerExtensionManager.getVdbConnectionFinder().getDataSourceJndiNames();


            // Copy dsJndiNames into List for sorting
            if (dsJndiNames != null) {
                dsJndiNameList.addAll(Arrays.asList(dsJndiNames));
            }

            // Sort names in alpha order
            Collections.sort(dsJndiNameList);
            
            return dsJndiNameList.toArray(new String[dsJndiNameList.size()]);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.ui.common.table.ResourceEditingSupport#setElementValue(java.lang.Object, java.lang.String)
         */
        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            if (newValue == null) {
                newValue = ""; //$NON-NLS-1$
            }
            ((VdbSource)element).setJndiName(newValue);
            
            refreshModelDetails();
            
            // cause a selection event to be fired so that actions can set their enablement
            bindingsViewer.setSelection(new StructuredSelection(element));
        }
    }
    
    class SourceNameTextEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer
		 */
		public SourceNameTextEditingSupport(ColumnViewer viewer) {
			super(viewer);
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
			if( element instanceof VdbSource ) {
				return ((VdbSource)element).getName();
			}
			return EMPTY_STRING;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			if( element instanceof VdbSource ) {
				String oldValue = ((VdbSource)element).getName();
				String newValue = (String)value;
				if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
					// Ensure the name is unique
					String newName = ensureUniqueName(newValue, (VdbSource)element);
					
					((VdbSource)element).setName(newName);
					refreshModelDetails();
		            
		            // cause a selection event to be fired so that actions can set their enablement
		            bindingsViewer.setSelection(new StructuredSelection(element));
				}
			}
		}
		
		/*
		 * Cannot have duplicate names.  If user tries duplicate, add a suffix
		 */
		private String ensureUniqueName(String name, VdbSource currentSource) {
			String uniqueName = name;
			List<String> otherSourceNames = new ArrayList<String>();
        	for( VdbSource vdbSource : selectedVdbModelEntry.getSourceInfo().getSources() ) {
        		if(!vdbSource.equals(currentSource)) {
        			otherSourceNames.add(vdbSource.getName().toLowerCase());
        		}
        	}
        	boolean isUnique = false;
        	int i = 1;  //Suffix added to make unique
        	while(!isUnique) {
        		isUnique = !otherSourceNames.contains(uniqueName.toLowerCase());
        		if(!isUnique) {
        			uniqueName += i;
        		}
        	}
			return uniqueName;
		}

	}
}
