/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.functions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.relational.model.RelationalViewProcedure;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.viewsupport.ModelSelectorDialog;
import org.teiid.designer.ui.viewsupport.ModelSelectorInfo;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceViewerFilter;

public class ConvertFunctionModelDialog   extends TitleAreaDialog {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ConvertFunctionModelDialog.class);
	private final String TITLE = getString("title"); //$NON-NLS-1$

	private static String getString(final String id) {
		return UiConstants.Util.getString(I18N_PREFIX + id);
	}
	
    //=============================================================
    // Instance variables
    //=============================================================
    private ConvertFunctionModelHelper helper;
    
    private TableViewer listViewer;
	private Button browseModelButton;
	private Text modelNameText;

        
    //=============================================================
    // Constructors
    //=============================================================
    /**
     * ParsedDataRowDialog constructor.
     * 
     * @param parent   parent of this dialog
     * @param fileInfo the flat file business object
     * @param stringToParse the data string to parse
     */
    public ConvertFunctionModelDialog(Shell parent, ConvertFunctionModelHelper helper) {
        super(parent);
        this.helper = helper;
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

    @Override
    protected Control createDialogArea(Composite parent) {
    	setTitle(TITLE);
    	setMessage(getString("titleMessage")); //$NON-NLS-1$
    	
        Composite composite = (Composite)super.createDialogArea(parent);
        //------------------------------        
        // Set layout for the Composite
        //------------------------------        
        GridLayout gridLayout = new GridLayout();
        composite.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 500;
        composite.setLayoutData(gridData);
        
        createFunctionListViewerGroup(composite);
        
        createTargetModelGroup(composite);
        
        return composite;
    }
    
    private void createFunctionListViewerGroup(Composite parent) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("selectFunctions"), SWT.NONE, 1, 2); //$NON-NLS-1$
    	GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
    	groupGD.horizontalSpan = 1;
    	theGroup.setLayoutData(groupGD);
    	
		Table table = new Table(theGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		table.setLayout(new TableLayout());

		this.listViewer = new TableViewer(table);
		GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 100).span(2, 1).applyTo(this.listViewer.getControl());
		
		this.listViewer.setContentProvider(new ITreeContentProvider() {

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
				return ! (helper.getVirtualFunctions().length > 0);
			}
			
			@Override
			public Object getParent(Object element) {
				return null;
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				if( inputElement instanceof ConvertFunctionModelHelper ) {
					return helper.getVirtualFunctions();
				}
				return new Object[0];
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				// TODO Auto-generated method stub
				return new Object[0];
			}
		});
		
		FunctionProvider provider = new FunctionProvider();
		listViewer.setContentProvider(provider);
		listViewer.setLabelProvider(provider);
		listViewer.setInput(helper);
		
		listViewer.getTable().addSelectionListener(
				new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						if( e.detail != SWT.CHECK ) return;
						
						helper.clearFunctions();
			        	for( TableItem item : listViewer.getTable().getItems() ) {
			        		
			        		if( item.getChecked() ) {
			        			helper.selectFunction((RelationalViewProcedure)item.getData());
			        		}
			        	}
			        	
						setDialogStatus();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
    }
    
    private void createTargetModelGroup(Composite parent) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("targetModelGroupName"), SWT.NONE, 1, 3); //$NON-NLS-1$
    	GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
    	groupGD.horizontalSpan = 1;
    	theGroup.setLayoutData(groupGD);
    	
    	
		Label selectedFileLabel = new Label(theGroup, SWT.NONE);
		selectedFileLabel.setText(getString("name")); //$NON-NLS-1$

		modelNameText = new Text(theGroup, SWT.BORDER | SWT.SINGLE);
		modelNameText.setEditable(false);
		modelNameText.setBackground(GlobalUiColorManager.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(modelNameText);
		
        // source browse button
    	browseModelButton = new Button(theGroup, SWT.PUSH);
    	browseModelButton.setText("..."); //$NON-NLS-1$
        browseModelButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleModelFolderBrowseButtonPressed();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
        
        browseModelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        browseModelButton.setFont(parent.getFont());
        GridDataFactory.fillDefaults().minSize(30, 16).applyTo(browseModelButton);

    }
    
    /**
     * Opens a container selection dialog and displays the user's subsequent container resource selection in this page's container
     * name field.
     */
    protected void handleModelFolderBrowseButtonPressed() {

        // ==================================
        // launch Location chooser
        // ==================================
    	ModelSelectorInfo newModelInfo = new ModelSelectorInfo(
    			getString("modelNameDialogTitle"),  //$NON-NLS-1$
    			ModelType.VIRTUAL_LITERAL, 
    			RelationalPackage.eNS_URI,
                getString("modelNameDialogNewName"), //$NON-NLS-1$
                getString("modelNameDialogMessage"));//$NON-NLS-1$
        ModelSelectorDialog mwdDialog = new ModelSelectorDialog(this.getShell(), newModelInfo);
        mwdDialog.addFilter(new ModelWorkspaceViewerFilter(true));
        mwdDialog.setValidator(new ISelectionStatusValidator() {
            @Override
			public IStatus validate( Object[] selection ) {
                if (selection == null) {
                    return new StatusInfo(UiPlugin.PLUGIN_ID, IStatus.ERROR, getString("modelNameDialogNoModelSelected"));
                } else if (selection.length != 1) {
                    return new StatusInfo(UiPlugin.PLUGIN_ID, IStatus.ERROR, getString("modelNameDialogMultipleSelection"));
                } else if (!(selection[0] instanceof IFile)) {
                    return new StatusInfo(UiPlugin.PLUGIN_ID, IStatus.ERROR, getString("modelNameDialogNoModelSelected"));
                } else {
                    return new StatusInfo(UiPlugin.PLUGIN_ID);
                }
            }
        });
        mwdDialog.setAllowMultiple(false);
        mwdDialog.open();

        if (mwdDialog.getReturnCode() == Window.OK) {
            Object[] selectedObjects = mwdDialog.getResult();

            if (selectedObjects.length > 0 && selectedObjects[0] instanceof IFile) {
                IFile theFile = (IFile)selectedObjects[0];
            	ModelResource targetModel = null;
            	
                if (theFile != null) {

                    try {
                    	targetModel = ModelUtil.getModelResource(theFile, false);
                    } catch (ModelWorkspaceException theException) {
                        UiConstants.Util.log(theException);
                    }
                }
                if (targetModel != null) {
                	helper.setTargetModel(targetModel);
                	modelNameText.setText(targetModel.getPath().toOSString());
                }
            }
        }
        
        setDialogStatus();
    }
    
    private void setDialogStatus() {
    	IStatus status = helper.getStatus();
    	if( status.getSeverity() == IStatus.ERROR) {
    		setErrorMessage(status.getMessage());
    		getButton(OK).setEnabled(false);
    	} else {
    		setErrorMessage(null);
    		setMessage(status.getMessage(), status.getSeverity());
    		getButton(OK).setEnabled(true);
    	}
    	
    }
    
    class FunctionProvider extends ColumnLabelProvider implements ITreeContentProvider {
    	
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
			return ! (helper.getVirtualFunctions().length > 0);
		}
		
		@Override
		public Object getParent(Object element) {
			return null;
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			if( inputElement instanceof ConvertFunctionModelHelper ) {
				return helper.getVirtualFunctions();
			}
			return new Object[0];
		}
		
		@Override
		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			return new Object[0];
		}

		@Override
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getText(Object element) {
			if( element instanceof RelationalViewProcedure ) {
				return ((RelationalViewProcedure)element).getName();
			}
			return null;
		}

    	
    }
}