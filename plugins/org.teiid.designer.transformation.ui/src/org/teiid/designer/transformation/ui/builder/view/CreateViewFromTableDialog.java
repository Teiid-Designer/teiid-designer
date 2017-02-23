package org.teiid.designer.transformation.ui.builder.view;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.ScrollableTitleAreaDialog;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelResourceSelectionValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceViewerFilter;

public class CreateViewFromTableDialog extends /*Scrollable*/TitleAreaDialog {
	private final String TITLE = "Select Columns for View";
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
    //=============================================================
    // Instance variables
    //=============================================================
    private ViewBuilderManager builder;

//    TableViewerBuilder columnDataViewer;
    TableViewer columnDataViewer;
    
    private Text virtualModelNameText;
    private Text sourceTableNameAndPathText;
    private Text viewNameText;
    
    private Button browseVirtualModelButton;
    private Button browseSourceTableButton;

    private Button selectAllButton;

    private Button deselectAllButton;
    
    boolean buttonActionPressed;
    
    IStatus currentStatus;
    
    //=============================================================
    // Constructors
    //=============================================================
    /**
     * @param parent the parent shell
     * @param theTable the relational table
     * @param isPrimaryKeyColumns the primary key columns
     * 
     */
    public CreateViewFromTableDialog(Shell parent,  ViewBuilderManager builder) {
        super(parent);
        this.builder = builder;
    }
    
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(TITLE);
    }
    
    /* (non-Javadoc)
    * @see org.eclipse.jface.window.Window#setShellStyle(int)
    */
    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);

    }
        
    //=============================================================
    // Instance methods
    //=============================================================

    @SuppressWarnings("unused")
	@Override
    protected Control createDialogArea(Composite parent) {
    	setTitle("Select Columns dialog subtitle");
    	
        Composite dialogArea = (Composite)super.createDialogArea(parent);
        ((GridData)dialogArea.getLayoutData()).grabExcessHorizontalSpace = true;
        ((GridData)dialogArea.getLayoutData()).widthHint = 600;
        ((GridData)dialogArea.getLayoutData()).heightHint = 600;
        
        //------------------------------        
        // Set layout for the Composite
        //------------------------------
        Composite composite = WidgetFactory.createPanel(dialogArea);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(composite);
		GridDataFactory.fillDefaults().grab(true, true).hint(500, 500).applyTo(composite);
		
    	Composite namePanel = WidgetFactory.createPanel(composite, SWT.FILL, GridData.FILL_HORIZONTAL);
    	GridLayoutFactory.fillDefaults().margins(5,  5).numColumns(3).applyTo(namePanel);
    	GridDataFactory.fillDefaults().grab(true,  false).span(2,  1).applyTo(namePanel);
    	
    	SOURCE_TABLE : { // SOURCE TABLE PATH 
	    	Label sourceTableNameAndPath = new Label(namePanel, SWT.NONE);
	    	sourceTableNameAndPath.setText("Source Table");
	    	
	    	sourceTableNameAndPathText = new Text(namePanel, SWT.BORDER);
	    	GridDataFactory.fillDefaults().grab(true,  false).applyTo(sourceTableNameAndPathText);
	    	if( builder.getSourceModel() != null ) {
		    	String path = ModelUtil.getName(builder.getSourceModel()) + "/" + builder.getSourceTableName();
		    	sourceTableNameAndPathText.setText(path);
	    	}
	    	sourceTableNameAndPathText.setEditable(false);
	    	sourceTableNameAndPathText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	    	
	    	
	        // container browse button
	    	browseSourceTableButton = new Button(namePanel, SWT.PUSH);
	    	browseSourceTableButton.setText("Browse...");
	    	browseSourceTableButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
	    	browseSourceTableButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					handleBrowseSourceTable();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
    	}
    	
    	TARGET_MODEL : {
	    	Label modelNameLabel = new Label(namePanel, SWT.NONE);
	    	modelNameLabel.setText("Target View Model");
	    	
	    	virtualModelNameText = new Text(namePanel, SWT.BORDER);
	    	GridDataFactory.fillDefaults().grab(true,  false).applyTo(virtualModelNameText);
	    	if( builder.getTargetModel() != null ) {
		    	String name = ModelUtil.getName(builder.getTargetModel());
		    	virtualModelNameText.setText(name);
	    	}
	    	virtualModelNameText.setEditable(false);
	    	virtualModelNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
	    	
	        // container browse button
	    	browseVirtualModelButton = new Button(namePanel, SWT.PUSH);
	    	browseVirtualModelButton.setText("Browse...");
	    	browseVirtualModelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
	    	browseVirtualModelButton.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					handleBrowseVirtualModel();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
    	}
    	
    	Label viewNameLabel = new Label(namePanel, SWT.NONE);
    	viewNameLabel.setText("View Name");
    	
    	viewNameText = new Text(namePanel, SWT.BORDER);
    	GridDataFactory.fillDefaults().grab(true,  false).span(2, 1).applyTo(viewNameText);
    	if( builder.getView() != null && builder.getView().getName() != null ) {
	    	String name = builder.getView().getName();
	    	viewNameText.setText(name);
    	}
    	viewNameText.setEditable(true);
    	viewNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	
    	viewNameText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String name = viewNameText.getText();
				builder.getView().setName(name);
				validatePage();
			}
		});

    	Group columnsGroup = WidgetFactory.createGroup(composite, "Select Columns", SWT.NONE, 1, 2);
    	GridDataFactory.fillDefaults().grab(true, true).applyTo(columnsGroup);

    	Composite buttonsPanel = WidgetFactory.createPanel(columnsGroup, SWT.FILL, GridData.FILL_HORIZONTAL);
    	GridLayoutFactory.fillDefaults().numColumns(2).applyTo(buttonsPanel);
    	GridDataFactory.fillDefaults().grab(true,  false).span(2,  1).applyTo(buttonsPanel);
        
        this.selectAllButton = WidgetFactory.createButton(buttonsPanel, "Select All", GridData.FILL_HORIZONTAL); //$NON-NLS-1$
		this.deselectAllButton = WidgetFactory.createButton(buttonsPanel,
		                      "Deselect All", GridData.FILL_HORIZONTAL); //$NON-NLS-1$
		
		this.selectAllButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectAllNodes();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		this.deselectAllButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				deselectAllNodes();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

    	org.eclipse.swt.widgets.Table table = new org.eclipse.swt.widgets.Table(columnsGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		table.setLayout(new TableLayout());

//		this.columnDataViewer = new TableViewerBuilder(table, (SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER));
		this.columnDataViewer = new TableViewer(table);
		GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 300).span(2, 1).applyTo(this.columnDataViewer.getControl());
		
        // create columns
//        TableViewerColumn column = this.columnDataViewer.createColumn(SWT.LEFT, 30, 40, true);
//        column.getColumn().setText(Messages.columnNameLabel);
//        column.setLabelProvider(new ColumnDataLabelProvider(0));
//
//        column = this.columnDataViewer.createColumn(SWT.LEFT, 30, 40, true);
//        column.getColumn().setText(Messages.dataTypeLabel);
//        column.setLabelProvider(new ColumnDataLabelProvider(1));
		this.columnDataViewer.setLabelProvider(new ColumnDataLabelProvider(0));
		
		this.columnDataViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public boolean hasChildren(Object element) {
				return !builder.getView().getColumns().isEmpty();
			}
			
			@Override
			public Object getParent(Object element) {
				return null;
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				if( inputElement instanceof RelationalTable ) {
					return builder.getView().getColumns().toArray(new Object[0]);
				}
				return new Object[0];
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				// TODO Auto-generated method stub
				return new Object[0];
			}
		});
		
//		this.columnDataViewer.setLabelProvider(new ColumnDataLabelProvider(0));
		
		this.columnDataViewer.setInput(this.builder.getView());
		
		this.columnDataViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// synch the selected columns list with the tree
				if( ! buttonActionPressed ) {
//					updaetSelectedColumns();
				}
			}
		});
        
        setMessage("Select columns for view");
        
        //sizeScrolledPanel();
        
        return composite;
    }
    
    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(true);
    }
    @Override
    protected void okPressed() {
    	updateSelectedColumns();
        super.okPressed();
    }
    
    private void updateSelectedColumns() {
    	builder.getSelectedColumns().clear();
    	
    	for( TableItem item : columnDataViewer.getTable().getItems() ) {
    		if( item.getChecked() ) {
    			builder.addSelectedColumn((RelationalColumn)item.getData());
    		}
    	}
    }
    
    private void selectAllNodes() {
    	buttonActionPressed = true;
    	for( TableItem item : columnDataViewer.getTable().getItems() ) {
    		item.setChecked(true);
    	}
    	buttonActionPressed = false;
    }
    
    private void deselectAllNodes() {
    	buttonActionPressed = true;
    	for( TableItem item : columnDataViewer.getTable().getItems() ) {
    		item.setChecked(false);
    	}
    	buttonActionPressed = false;
    }
    
    /**
     * Opens a container selection dialog and displays the user's subsequent container resource selection in this page's container
     * name field.
     */
    protected void handleBrowseVirtualModel() {
    	
		final Object[] selections = WidgetUtil.showWorkspaceObjectSelectionDialog(
						"Select View Model",
						"Select view model as the target for your new view",
						false, null, modelFilter,
						new ModelResourceSelectionValidator(false),
						new ModelExplorerLabelProvider(),
						new ModelExplorerContentProvider());

		if (selections != null && selections.length == 1 && virtualModelNameText != null) {
			if (selections[0] instanceof IFile) {
				IFile modelFile = (IFile) selections[0];
				ModelResource viewModelResource = ModelUtilities.getModelResource(modelFile);
				builder.setTargetModel(viewModelResource);
				String name = ModelUtil.getName(builder.getTargetModel());
				this.virtualModelNameText.setText(name);
			} else {
				
			}
			
		}
		
		validatePage();

    }
    
    /**
     * Opens selection dialog 
     */
    protected void handleBrowseSourceTable() {
    	
		final Object[] selections = WidgetUtil.showWorkspaceObjectSelectionDialog(
						"Select View Model",
						"Select view model as the target for your new view",
						false, null, tableOrViewFilter,
						new TableOrViewSelectionValidator(),
						new ModelExplorerLabelProvider(),
						new ModelExplorerContentProvider());

		if (selections != null && selections.length == 1 ) {
			if (selections[0] instanceof EObject) {
				EObject sourceTable = (EObject) selections[0];
				builder.setSourceTable(sourceTable);
				String path = ModelUtil.getName(builder.getSourceModel()) + "/" + builder.getSourceTableName();
				this.sourceTableNameAndPathText.setText(path);
				this.columnDataViewer.setInput(this.builder.getView());
				if( builder.getView().getName() != null ) {
					this.viewNameText.setText(builder.getView().getName());
				}
			} else {
				
			}
			
		}
		
		validatePage();

    }
    
	private boolean validatePage() {
		builder.getView().validate();
		currentStatus = builder.getView().getStatus();
		
		// Target Model my not null
		// One ore more columns must be selected
		
		if( currentStatus.isOK()) {
			setErrorMessage(null);
			setMessage("Click Finish to create your view");
			return true;
		} 
		return true;
	}

	final ViewerFilter modelFilter = new ModelWorkspaceViewerFilter(true) {

		@Override
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject().isOpen();
				String projectName = ((IResource) element).getProject().getName();
				boolean isSingleProject = false;
				if( projectName != null ) {
					isSingleProject = projectName.equals(builder.getModelProject().getName());
				}
				if (projectOpen && isSingleProject) {
					// Show open projects
					if (element instanceof IProject) {
		                IProject project = (IProject)element;
						try {
		                	doSelect = project.hasNature(ModelerCore.NATURE_ID);
		                } catch (CoreException e) {
		                	ModelerCore.Util.log(e);
		                }
					} else if (element instanceof IContainer) {
						doSelect = true;
						// Show webservice model files, and not .xsd files
					} else if (element instanceof IFile && ModelUtil.isModelFile((IFile) element)) {
						ModelResource theModel = null;
						try {
							theModel = ModelUtil.getModelResource((IFile) element, true);
						} catch (Exception ex) {
							ModelerCore.Util.log(ex);
						}
						if (theModel != null && ModelIdentifier.isRelationalViewModel(theModel) ) {
							doSelect = true;
						}
					}
				}
			} else if (element instanceof IContainer) {
				doSelect = true;
			}

			return doSelect;
		}
	};
	
	final ViewerFilter tableOrViewFilter = new ModelWorkspaceViewerFilter(true, true, false) {

		@Override
		public boolean select(final Viewer viewer, final Object parent,
				final Object element) {
			boolean doSelect = false;
			if (element instanceof IResource) {
				// If the project is closed, dont show
				boolean projectOpen = ((IResource) element).getProject().isOpen();
				String projectName = ((IResource) element).getProject().getName();
				boolean isSingleProject = false;
				if( projectName != null ) {
					isSingleProject = projectName.equals(builder.getModelProject().getName());
				}
				if (projectOpen && isSingleProject) {
					// Show open projects
					if (element instanceof IProject) {
		                IProject project = (IProject)element;
						try {
		                	doSelect = project.hasNature(ModelerCore.NATURE_ID);
		                } catch (CoreException e) {
		                	ModelerCore.Util.log(e);
		                }
					} else if (element instanceof IContainer) {
						doSelect = true;
						// Show webservice model files, and not .xsd files
					} else if (element instanceof IFile && ModelUtil.isModelFile((IFile) element)) {
						ModelResource theModel = null;
						try {
							theModel = ModelUtil.getModelResource((IFile) element, true);
						} catch (Exception ex) {
							ModelerCore.Util.log(ex);
						}
						if( theModel != null) return true;
					} else if( element instanceof EObject) {
						doSelect = true;
					}
				}
			} else if (element instanceof IContainer) {
				doSelect = true;
			} else if( element instanceof EObject ) {
				return true;
			}

			return doSelect;
		}
	};

}

class ColumnDataLabelProvider extends ColumnLabelProvider {

	private final int columnNumber;

	public ColumnDataLabelProvider(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if( element instanceof RelationalColumn ) {
			switch (this.columnNumber) {
				case 0: {
					return ((RelationalColumn)element).getName() + " : " + ((RelationalColumn)element).getDatatype();
				}
				case 1: {
					return ((RelationalColumn)element).getDatatype();
				}
				case 2: {
					return Integer.toString(((RelationalColumn)element).getLength());
				}
			}
		}
		return "";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
	 */
	@Override
	public String getToolTipText(Object element) {
		switch (this.columnNumber) {
		case 0: {
			return "Tooltip 1"; //getString("columnNameColumnTooltip"); //$NON-NLS-1$
		}
		case 1: {
			return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
		}
	}
	return "unknown tooltip"; //$NON-NLS-1$
	}

	@Override
	public Image getImage(Object element) {
		if( this.columnNumber == 0 ) {
			return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
		}
		return null;
	}

}
