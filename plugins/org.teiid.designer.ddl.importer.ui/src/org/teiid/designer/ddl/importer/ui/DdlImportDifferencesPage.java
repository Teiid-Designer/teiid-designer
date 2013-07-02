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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.compare.DifferenceReport;
import org.teiid.designer.relational.compare.OperationList;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
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
    		
    private final DdlImporter importer;
    private CheckboxTreeController controller;
    private TreeViewer treeViewer;
    private Tree tree;
    boolean treeExpanded = false;
    
    /**
     * DdlImportDifferencesPage Constructor
     * @param importer the DdlImporter
     */
    public DdlImportDifferencesPage( final DdlImporter importer ) {
        super(DdlImportDifferencesPage.class.getSimpleName(), DdlImporterUiI18n.DIFFERENCE_PAGE_TITLE, null);
        this.importer = importer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl( final Composite parent ) {
        final IDialogSettings settings = getDialogSettings();

        final Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1, PANEL_GRID_SPAN);
        setControl(panel);
        
        // Checkbox tree - shows proposed changes
        createCheckboxTreeComposite(panel, DdlImporterUiI18n.DIFFERENCE_PAGE_IMPORT_SELECTIONS_LABEL); 
    }

    /**
     * create the checkbox tree Composite
     * 
     * @param parent the parent composite
     * @param title the group title
     */
    private void createCheckboxTreeComposite( Composite parent,
                                              String title ) {
        Composite checkBoxTreeComposite = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        GridLayout layout = new GridLayout(1, false);
        checkBoxTreeComposite.setLayout(layout);

        // --------------------------
        // Group for checkbox tree
        // --------------------------
        Group group = WidgetFactory.createGroup(checkBoxTreeComposite, title, GridData.FILL_BOTH, 1, 2);

        // ----------------------------
        // TreeViewer
        // ----------------------------
        this.controller = new CheckboxTreeController();
        this.treeViewer = WidgetFactory.createTreeViewer(group, SWT.SINGLE | SWT.CHECK, GridData.FILL_BOTH, controller);

        this.tree = this.treeViewer.getTree();
        //tree.addListener(SWT.Selection, this);

        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        this.treeViewer.setContentProvider(new CheckboxTreeContentProvider());
        this.treeViewer.setLabelProvider(new CheckboxTreeLabelProvider());

        this.treeViewer.setInput(null);
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
        if(visible) {
        	// Perform the DDL Import
        	final List<String> msgs = new ArrayList<String>();
        	try {
        		new ProgressMonitorDialog(getShell()).run(true, true, new IRunnableWithProgress() {

        			@Override
        			public void run( final IProgressMonitor monitor ) {
        				monitor.beginTask(DdlImporterUiI18n.IMPORTING_DDL_MSG, 100);
        				importer.importDdl(msgs, monitor, 100);
        				monitor.done();
        			}
        		});
        	} catch (Exception ex) {
        		DdlImporterUiPlugin.UTIL.log(IStatus.ERROR,ex,DdlImporterUiI18n.DIFFERENCE_PAGE_DDLIMPORT_ERROR_MSG);
        		msgs.add(DdlImporterUiI18n.DIFFERENCE_PAGE_DDLIMPORT_ERROR_MSG);
        	}
        	
            // Errors Encountered - confirm with user whether to continue
        	boolean importCancelled = false;
            if (!msgs.isEmpty()
                && new MessageDialog(getShell(), DdlImporterUiI18n.DIFFERENCE_PAGE_CONFIRM_DIALOG_TITLE, null, DdlImporterUiI18n.DIFFERENCE_PAGE_CONTINUE_IMPORT_MSG,
                                     MessageDialog.CONFIRM, new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL},
                                     SWT.NONE) {

                    @Override
                    protected Control createCustomArea( final Composite parent ) {
                        final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(parent, SWT.BORDER | SWT.V_SCROLL
                                                                                                           | SWT.H_SCROLL);
                        list.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                        list.setItems(msgs.toArray(new String[msgs.size()]));
                        return list;
                    }

                    @Override
                    protected final int getShellStyle() {
                        return SWT.SHEET;
                    }
                }.open() != Window.OK) {
            	importer.undoImport();
            	importCancelled=true;
            }
        	
            if(!importCancelled) {
            	// Set the checkbox tree roots (create,delete,update)
            	List<OperationList> rootList = new ArrayList<OperationList>();
            	DifferenceReport diffReport = this.importer.getDifferenceReport();
            	if(diffReport!=null) {
            		OperationList createObjs = this.importer.getDifferenceReport().getObjectsToCreate();
            		OperationList deleteObjs = this.importer.getDifferenceReport().getObjectsToDelete();
            		OperationList updateObjs = this.importer.getDifferenceReport().getObjectsToUpdate();
            		if(!createObjs.getList().isEmpty()) rootList.add(createObjs);
            		if(!deleteObjs.getList().isEmpty()) rootList.add(deleteObjs);
            		if(!updateObjs.getList().isEmpty()) rootList.add(updateObjs);
            	} 
            	this.treeViewer.setInput(rootList);
            	this.treeViewer.expandToLevel(2);
            	this.setAllNodesSelected(true);
            }
        	validate();
        }
    }

    void validate() {
        setErrorMessage(null);
        
        // Potentially set an error message here - if nothing is selected
        DifferenceReport diffReport = importer.getDifferenceReport();
        if(diffReport==null) {
        	setErrorMessage(DdlImporterUiI18n.DIFFERENCE_PAGE_NO_DIFFERENCE_REPORT_MSG);
        } else {
        	if(!diffReport.hasOperations()) {
            	setErrorMessage(DdlImporterUiI18n.DIFFERENCE_PAGE_NO_DIFFERENCE_OPERATIONS_MSG);
        	} else if(!diffReport.hasSelectedOperations()) {
            	setErrorMessage(DdlImporterUiI18n.DIFFERENCE_PAGE_NO_DIFFERENCE_OPERATIONS_SELECTED_MSG);
        	}
        }
        
        // Set the 'ok' message
        if (getErrorMessage() == null) {
            setDescription(DdlImporterUiI18n.DIFFERENCE_PAGE_DESCRIPTION);
        }
    }
    
    class CheckboxTreeLabelProvider extends LabelProvider {
        private final Image TABLE_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.TABLE_ICON);
        private final Image COLUMN_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
        private final Image VIEW_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.TABLE_ICON);
        private final Image PROCEDURE_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.PROCEDURE_ICON);
        private final Image INDEX_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.INDEX_ICON);
        

        @Override
        public Image getImage( final Object node ) {
        	if(node instanceof RelationalReference) {
        		int type = ((RelationalReference)node).getType();
        		if(type==RelationalConstants.TYPES.TABLE) {
        			return TABLE_IMG;
        		} else if(type==RelationalConstants.TYPES.COLUMN) {
        			return COLUMN_IMG;
        		} else if(type==RelationalConstants.TYPES.VIEW) {
        			return VIEW_IMG;
        		} else if(type==RelationalConstants.TYPES.PROCEDURE) {
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
            if (treeExpanded) {
                super.itemExpanded(event);
            } else {
            	// Do a length check on selection to avoid Array Index OOB exception
            	if( ((TreeViewer)event.getTreeViewer()).getTree().getSelection().length > 0 ) {
	                final TreeItem item = ((TreeViewer)event.getTreeViewer()).getTree().getSelection()[0];
	                if (item.getData() != null) {
	                    updateChildren(item, false);
	                }
	                treeExpanded = true;
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
