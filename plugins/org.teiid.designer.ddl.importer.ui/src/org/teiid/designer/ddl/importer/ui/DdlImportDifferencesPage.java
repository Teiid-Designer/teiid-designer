/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.teiid.core.designer.I18n;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.compare.DifferenceReport;
import org.teiid.designer.relational.compare.OperationList;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.DefaultTreeViewerController;
import org.teiid.designer.ui.common.wizard.IPersistentWizardPage;

/**
 * DdlImportDifferencesPage - shows entities which will be changed in the target model and allows user to de-select if desired
 * 
 */
public class DdlImportDifferencesPage extends WizardPage implements IPersistentWizardPage {

    private static final int PANEL_GRID_SPAN = 3;
    		
    final DdlImporter importer;
	private Composite stackPanel;
	private Composite differencesPanel;
	private Composite parseErrorPanel;
	private StackLayout stackLayout;
    private CheckboxTreeController controller;
    private TreeViewer treeViewer;
    private Tree tree;
    boolean treeExpanded = false;
    private StyledTextEditor ddlContentsArea;
    private org.eclipse.swt.widgets.List messagesList;
    
    Properties options;
    
    /**
     * DdlImportDifferencesPage Constructor
     * @param importer the DdlImporter
     * @param options 
     */
    public DdlImportDifferencesPage( final DdlImporter importer, Properties options) {
        super(DdlImportDifferencesPage.class.getSimpleName(), DdlImporterUiI18n.DIFFERENCE_PAGE_TITLE, null);
        this.importer = importer;
        this.options = options;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( final Composite parent ) {
        //final IDialogSettings settings = getDialogSettings();

        final Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1, PANEL_GRID_SPAN);
        setControl(panel);
        
        // Stack Layout - swap between parse error page and differences page
    	stackPanel = new Composite(panel, SWT.NONE | SWT.FILL);
    	stackLayout = new StackLayout();
    	stackLayout.marginWidth = 0;
    	stackLayout.marginHeight = 0;
    	stackPanel.setLayout(stackLayout);
    	stackPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
        // Differences Panel 
    	createDifferencesPanel(stackPanel); 
    	
    	// Parse Error Panel
    	createParseErrorPanel(stackPanel);
    }
    
    /**
     * create the Differences tree Composite
     * 
     * @param parent the parent composite
     */
    private void createDifferencesPanel( Composite parent ) {
    	differencesPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
    	GridLayout layout = new GridLayout(1, false);
    	differencesPanel.setLayout(layout);

    	SashForm splitter = WidgetFactory.createSplitter(differencesPanel, SWT.VERTICAL);
    	GridData gid = new GridData();
    	gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
    	gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
    	splitter.setLayoutData(gid);


    	// --------------------------
    	// Group for checkbox tree
    	// --------------------------
    	Group treeGroup = WidgetFactory.createGroup(splitter, DdlImporterUiI18n.DIFFERENCE_PAGE_IMPORT_TREE_GROUP_TITLE, GridData.FILL_BOTH, 1, 1);

    	// ----------------------------
    	// TreeViewer
    	// ----------------------------
    	this.controller = new CheckboxTreeController();
    	this.treeViewer = WidgetFactory.createTreeViewer(treeGroup, SWT.SINGLE | SWT.CHECK, GridData.FILL_BOTH, controller);

    	this.tree = this.treeViewer.getTree();

    	tree.setLayoutData(new GridData(GridData.FILL_BOTH));

    	this.treeViewer.setContentProvider(new CheckboxTreeContentProvider());
    	this.treeViewer.setLabelProvider(new CheckboxTreeLabelProvider());

    	this.treeViewer.setInput(null);

    	// --------------------------
    	// Group for Import Messages
    	// --------------------------
    	Group messageGroup = WidgetFactory.createGroup(splitter, DdlImporterUiI18n.DIFFERENCE_PAGE_IMPORT_MESSAGES_GROUP_TITLE, GridData.FILL_BOTH, 1, 1);

    	messagesList = new org.eclipse.swt.widgets.List(messageGroup, SWT.BORDER | SWT.V_SCROLL	| SWT.H_SCROLL);
    	messagesList.setLayoutData(new GridData(GridData.FILL_BOTH));

    	// position the splitter
    	splitter.setWeights(new int[] {10, 3});
    	splitter.layout();
    }
    
    /**
     * create the Panel to display parse errors
     * 
     * @param parent the parent composite
     */
    private void createParseErrorPanel( Composite parent ) {
    	parseErrorPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
    	GridLayout layout = new GridLayout(1, false);
    	parseErrorPanel.setLayout(layout);

    	Group ddlGroup = WidgetFactory.createGroup(parseErrorPanel, DdlImporterUiI18n.DIFFERENCE_PAGE_PARSE_ERROR_DDL_GROUP_TITLE, GridData.FILL_BOTH, 1, 1);

    	Composite ddlPanel = WidgetFactory.createPanel(ddlGroup, SWT.NONE, GridData.FILL_BOTH);
    	ddlPanel.setLayout(layout);

    	// --------------------------
        // Group for Parse Errors
        // --------------------------
        int messageAreaStyle = SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        ddlContentsArea = StyledTextEditor.createReadOnlyEditor(ddlPanel, messageAreaStyle);
        ddlContentsArea.setLayoutData(gridData);
        ddlContentsArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.wizard.IPersistentWizardPage#saveSettings()
     */
    @Override
    public void saveSettings() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.WizardPage#setErrorMessage(java.lang.String)
     */
    @Override
    public void setErrorMessage( final String message ) {
        if (message == null || getErrorMessage() == null) super.setErrorMessage(message);
        setPageComplete(message == null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( final boolean visible ) {
        super.setVisible(visible);
        
        // If the page is being shown, import the DDL and generate the difference report 
        if(! visible)
            return;

        // Perform the DDL Import
        final Exception importException[] = new Exception[1];
        try {
            new ProgressMonitorDialog(getShell()).run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask(DdlImporterUiI18n.IMPORTING_DDL_MSG, 100);
                    try {
                        importer.importDdl(monitor, 100, options);
                    } catch (Exception ex) {
                        importException[0] = ex;
                    }
                    monitor.done();
                }
            });

            if (importException[0] != null)
                throw importException[0];

        } catch (Exception ex) {
            String msg = I18n.format(DdlImporterUiI18n.DIFFERENCE_PAGE_DDLIMPORT_ERROR_MSG, ex.getLocalizedMessage());
            DdlImporterUiPlugin.UTIL.log(IStatus.ERROR, ex, msg);
            importer.addProgressMessage(msg);
        }

        // Hard Failure (eg parse error) will show the error page
        if (importException[0] != null || importer.hasParseError()) {
            String title = null;
            String msg = null;
            this.stackLayout.topControl = parseErrorPanel;
            this.ddlContentsArea.setText(importer.getDdlString());

            if (importException[0] != null) {
                title = DdlImporterUiI18n.DIFFERENCE_PAGE_GENERAL_ERROR_TITLE;
                msg = I18n.format(DdlImporterUiI18n.DIFFERENCE_PAGE_DDLIMPORT_ERROR_MSG, importException[0].getLocalizedMessage());

            } else if (importer.hasParseError()) {
                title = DdlImporterUiI18n.DIFFERENCE_PAGE_PARSE_ERROR_TITLE;
                String parseErrorMessage = importer.getParseErrorMessage();
                // Get the offSet of the error if set
                int offset = importer.getParseErrorIndex();
                // Highlight the problem line if possible
                if (offset > -1) {
                    StyledText styledText = ddlContentsArea.getTextWidget();
                    int line = styledText.getLineAtOffset(offset);
                    if (line > -1 && styledText.getLineCount() > 1) {
                        int startIndx = styledText.getOffsetAtLine(line);
                        int endIndx = styledText.getOffsetAtLine(line + 1);
                        styledText.setSelection(startIndx, endIndx);
                    }
                }
                msg = I18n.format(DdlImporterUiI18n.DIFFERENCE_PAGE_PARSE_ERROR_MSG, parseErrorMessage);
            }

            setErrorMessage(msg);
            importer.undoImport();
            this.setTitle(title);
            // Show differences page
        } else {
            this.stackLayout.topControl = differencesPanel;
            this.setTitle(DdlImporterUiI18n.DIFFERENCE_PAGE_TITLE);

            // Set the checkbox tree roots (create,delete,update)
            List<OperationList> rootList = new ArrayList<OperationList>();
            DifferenceReport diffReport = this.importer.getDifferenceReport();
            if (diffReport != null) {
                OperationList createObjs = this.importer.getDifferenceReport().getObjectsToCreate();
                OperationList deleteObjs = this.importer.getDifferenceReport().getObjectsToDelete();
                OperationList updateObjs = this.importer.getDifferenceReport().getObjectsToUpdate();
                if (!createObjs.getList().isEmpty())
                    rootList.add(createObjs);
                if (!deleteObjs.getList().isEmpty())
                    rootList.add(deleteObjs);
                if (!updateObjs.getList().isEmpty())
                    rootList.add(updateObjs);
            }
            this.treeViewer.setInput(rootList);
            this.treeViewer.expandToLevel(2);
            this.setAllNodesSelected(true);

            List<String> progressMessages = this.importer.getAllMessages();
            messagesList.setItems(progressMessages.toArray(new String[progressMessages.size()]));

            validate();
        }

        this.stackPanel.layout();
    }

    /**
     * validate the Differences Page
     */
    void validate( ) {
        setErrorMessage(null);

        StringBuffer errMessageBuffer = new StringBuffer();
        
        // Determine if there are any importer messages to show
        boolean hasImportMessages = importer.getAllMessages().isEmpty() ? false : true;
        
        DifferenceReport diffReport = importer.getDifferenceReport();
        
        // No DifferenceReport to show
        if(diffReport==null) {
        	errMessageBuffer.append(DdlImporterUiI18n.DIFFERENCE_PAGE_NO_DIFFERENCE_REPORT_MSG);
        } else {
        	if( importer.noDdlImported() ) {
        		// do nothing
        	} else if(!diffReport.hasOperations()) { // DifferenceReport has no operations
            	errMessageBuffer.append(DdlImporterUiI18n.DIFFERENCE_PAGE_NO_DIFFERENCE_OPERATIONS_MSG);
            // DifferenceReport has nothing selected
        	} else if(!diffReport.hasSelectedOperations()) {
            	errMessageBuffer.append(DdlImporterUiI18n.DIFFERENCE_PAGE_NO_DIFFERENCE_OPERATIONS_SELECTED_MSG);
        	}
        }
        
        // Set error message if an error was found
        if(errMessageBuffer.length()>0) {
        	if(hasImportMessages) {
        		errMessageBuffer.append('\n'+DdlImporterUiI18n.DIFFERENCE_PAGE_SEE_IMPORT_MESSAGES_MSG);
        	}
        	setErrorMessage(errMessageBuffer.toString());
        // Set information message if only import messages are found
        } else if(hasImportMessages) {
        	setMessage(DdlImporterUiI18n.DIFFERENCE_PAGE_IMPORT_COMPLETED_WITH_MESSAGES_MSG);
        // Set description to finish
        } else {
        	if( importer.noDdlImported() ) {
        		setMessage(DdlImporterUiI18n.DIFFERENCE_PAGE_NO_DDL_IMPORTED_MSG);
        	} else {
        		setMessage(null);
        	}
            setDescription(DdlImporterUiI18n.DIFFERENCE_PAGE_DESCRIPTION);
        }
    }
    
	/**
	 * @return the options
	 */
	public Properties getOptions() {
		return options;
	}

	/**
	 * @param options
	 */
	public void setOptions(Properties options) {
		this.options = options;
	}
    
    class CheckboxTreeLabelProvider extends LabelProvider {
        private final Image TABLE_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.TABLE_ICON);
        private final Image COLUMN_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
        private final Image VIEW_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.TABLE_ICON);
        private final Image PROCEDURE_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.PROCEDURE_ICON);
        private final Image INDEX_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.INDEX_ICON);
        private final Image VIEW_TABLE_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_TABLE_ICON);
        private final Image VIRTUAL_PROCEDURE_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_PROCEDURE_ICON);

        @Override
        public Image getImage( final Object node ) {
        	if(node instanceof RelationalReference) {
        		int type = ((RelationalReference)node).getType();
        		if(type==RelationalConstants.TYPES.TABLE) {
        			if( importer.modelType() == ModelType.VIRTUAL_LITERAL) {
        				return VIEW_TABLE_IMG;
        			}
        			return TABLE_IMG;
        		} else if(type==RelationalConstants.TYPES.COLUMN) {
        			return COLUMN_IMG;
        		} else if(type==RelationalConstants.TYPES.VIEW) {
        			if( importer.modelType() == ModelType.VIRTUAL_LITERAL) {
        				return VIEW_TABLE_IMG;
        			}
        			return VIEW_IMG;
        		} else if(type==RelationalConstants.TYPES.PROCEDURE) {
        			if( importer.modelType() == ModelType.VIRTUAL_LITERAL) {
        				return VIRTUAL_PROCEDURE_IMG;
        			}
        			return PROCEDURE_IMG;
        		} else if(type==RelationalConstants.TYPES.INDEX) {
        			return INDEX_IMG;
        		}
        	}
            return null;
        }

        @Override
        public String getText( final Object node ) {
        	if(node instanceof OperationList) {
        		OperationList opList = (OperationList)node;
        		if(opList.getOperationType()==OperationList.OperationType.CREATE) {
        			return DdlImporterUiI18n.DIFFERENCE_PAGE_CREATELIST_LABEL;
        		} else if(opList.getOperationType()==OperationList.OperationType.DELETE) {
        			return DdlImporterUiI18n.DIFFERENCE_PAGE_DELETELIST_LABEL;
        		} else if(opList.getOperationType()==OperationList.OperationType.UPDATE) {
        			return DdlImporterUiI18n.DIFFERENCE_PAGE_UPDATELIST_LABEL;
        		}
        	} else if(node instanceof RelationalReference) {
        		return ((RelationalReference)node).getName();
        	}
    		return DdlImporterUiI18n.DIFFERENCE_PAGE_UNKNOWN_LABEL;
        }
    }

    class CheckboxTreeContentProvider implements ITreeContentProvider {
        String[] EMPTY_STRING_ARRAY = new String[0];

        @Override
		public void dispose() {
        }

        @Override
		public Object[] getChildren( final Object node ) {
            if (node instanceof List) {
            	return ((List<?>)node).toArray();
            } else if (node instanceof OperationList) {
                return ((OperationList)node).getList().toArray();
            } 
            return EMPTY_STRING_ARRAY;
        }

        @Override
		public Object[] getElements( final Object inputElement ) {
            return getChildren(inputElement);
        }

        @Override
		public Object getParent( final Object node ) {
            return null;
        }

        @Override
		public boolean hasChildren( final Object node ) {
            if (node instanceof OperationList) {
                return !((OperationList)node).getList().isEmpty();
            } 
            return false;
        }

        @Override
		public void inputChanged( final Viewer viewer,
                                  final Object oldInput,
                                  final Object newInput ) {
        }
    }

    class CheckboxTreeController extends DefaultTreeViewerController {
        /**
         * @see org.teiid.designer.ui.common.widget.DefaultTreeViewerController#checkedStateToggled(org.eclipse.swt.widgets.TreeItem)
         */
        @Override
        public void checkedStateToggled( TreeItem item ) {
        	Object dataObj = item.getData();
        	if(dataObj instanceof RelationalReference) {
        		boolean isChecked = item.getChecked();
        		((RelationalReference)dataObj).setChecked(isChecked);
        	}
        	validate();
        }

        /**
         * @see org.teiid.designer.ui.common.widget.ITreeViewerController#isItemCheckable(org.eclipse.swt.widgets.TreeItem)
         */
        @Override
        public boolean isItemCheckable( final TreeItem item ) {
        	return true;
        }

        /**
         * <p>
         * </p>
         * 
         * @see org.teiid.designer.ui.common.widget.ITreeViewerController#update(org.eclipse.swt.widgets.TreeItem, boolean)
         * @since 4.0
         */
        @Override
        public void update( final TreeItem item,
                            final boolean selected ) {
            Object dataObj = item.getData();
            if (dataObj != null) {
                final boolean checked = !WidgetUtil.isUnchecked(item);
                if (isItemCheckable(item)) {
                    item.setChecked(checked);
                }
                item.setGrayed(WidgetUtil.isPartiallyChecked(item));

                if (selected) {
                    updateChildren(item, checked);
                    for (TreeItem parent = item.getParentItem(); parent != null; parent = parent.getParentItem()) {
                        int state = PARTIALLY_CHECKED;
                        final TreeItem[] children = parent.getItems();
                        for (int ndx = children.length; --ndx >= 0;) {
                            final TreeItem child = children[ndx];
                            if (WidgetUtil.isPartiallyChecked(child)) {
                                state = PARTIALLY_CHECKED;
                                break;
                            }
                            final int childState = WidgetUtil.getCheckedState(child);
                            if (state == PARTIALLY_CHECKED) {
                                state = childState;
                            } else if (state != childState) {
                                state = PARTIALLY_CHECKED;
                                break;
                            }
                        }
                        if (state != WidgetUtil.getCheckedState(parent)) {
                            WidgetUtil.setCheckedState(parent, state, false, this);
                        }
                    }
                }
            	if(dataObj instanceof RelationalReference) {
            		boolean isChecked = item.getChecked();
            		((RelationalReference)dataObj).setChecked(isChecked);
            	}
                if (!isItemCheckable(item)) {
                    item.setGrayed(true);
                    item.setChecked(false);
                } else {
                    item.setGrayed(false);
                }
            }
        }

        /**
         * @param item 
         * @param checked 
         * @since 4.0
         */
        private void updateChildren( final TreeItem item,
                                     final boolean checked ) {
            final TreeItem[] children = item.getItems();
            for (int ndx = children.length; --ndx >= 0;) {
                final TreeItem child = children[ndx];
                if (child.getData() != null) {
                    updateChildren(child, checked);
                    WidgetUtil.setChecked(child, checked, false, this);
                }
            }
        }

        /**
         * @see org.teiid.designer.ui.common.widget.ITreeViewerController#itemExpanded(org.eclipse.jface.viewers.TreeExpansionEvent)
         * @since 4.0
         */
        @Override
        public void itemExpanded( final TreeExpansionEvent event ) {
        	// Do a length check on selection to avoid Array Index OOB exception
        	if( ((TreeViewer)event.getTreeViewer()).getTree().getSelection().length > 0 ) {
        		final TreeItem item = ((TreeViewer)event.getTreeViewer()).getTree().getSelection()[0];
        		if (item.getData() != null) {
        			checkRequiredChildren(item);
        		}
        	}
        }
        
        /**
         * Set the checked states of the tree nodes based on data checked state
         * @param item the tree item
         */
        public void checkRequiredChildren( TreeItem item ) {
        	final TreeItem[] children = item.getItems();
        	for (int ndx = children.length; --ndx >= 0;) {
        		final TreeItem child = children[ndx];
        		Object childData = child.getData();
        		if (childData != null && childData instanceof RelationalReference) {
        			boolean isChecked = ((RelationalReference)childData).isChecked();
        			WidgetUtil.setChecked(child, isChecked, false, this);
        		}
        	}
        }

        @Override
        public void itemCollapsed( final TreeExpansionEvent event ) {
            super.itemCollapsed(event);
        }

    }

    private void setAllNodesSelected( boolean bSelected ) {
        TreeItem[] items = tree.getItems();
        for (int i = 0; i < items.length; i++) {
            setAllSelected(items[i], bSelected);
        }
    }

    private void setAllSelected( final TreeItem item,
                                 final boolean checked ) {
        WidgetUtil.setChecked(item, checked, false, this.controller);

        // Apply same checked state to any children
        final TreeItem[] children = item.getItems();
        for (int ndx = 0; ndx < children.length; ndx++) {
            setAllSelected(children[ndx], checked);
        }
    }
    
}
