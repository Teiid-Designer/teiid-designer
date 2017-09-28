/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard.panels;

import static org.teiid.designer.ui.PluginConstants.Prefs.General.AUTO_WILL_TOGGLE_WITH_CHILDREN;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.roles.Crud;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.RolesUiConstants;
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.roles.ui.wizard.dialogs.ColumnMaskingDialog;
import org.teiid.designer.roles.ui.wizard.dialogs.RowBasedSecurityDialog;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Label;
/**
 *
 */
public class CrudPanel extends DataRolePanel {
    static final String ALL = "ALL"; //$NON-NLS-1$
    static final String SOURCE = "SOURCE"; //$NON-NLS-1$
    static final String VIEW = "VIEW"; //$NON-NLS-1$
    static final String FILTER = "Filter"; //$NON-NLS-1$
    
    // Model Filter Variables

    Combo modelTypeCombo;
    Button clearFilterButton;
    
	private TreeViewer treeViewer;
	
	/**
     * @param parent
     * @param wizard
     */
    public CrudPanel(Composite parent, DataRoleWizard wizard) {
    	super(parent, wizard);
    }

	/* (non-Javadoc)
	 * @see org.teiid.designer.roles.ui.wizard.panels.DataRolePanel#createControl()
	 */
	@Override
	void createControl() {
		createModelFilterGroup(getPrimaryPanel());
		
        treeViewer = new TreeViewer(getPrimaryPanel(), SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        Tree tree = treeViewer.getTree();

        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        treeViewer.getControl().setLayoutData(gridData);

        tree.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp( MouseEvent e ) {
                // NO OP
            }

            @Override
            public void mouseDown( MouseEvent e ) {
                Point pt = new Point(e.x, e.y);
				if( treeViewer.getCell(pt) != null && 
					treeViewer.getCell(pt).getViewerRow() != null &&
					treeViewer.getCell(pt).getViewerRow().getItem() != null ) {
					handleSelection((treeViewer.getCell(pt).getColumnIndex()), treeViewer.getCell(pt).getViewerRow().getItem().getData());
                }
            }

            @Override
            public void mouseDoubleClick( MouseEvent e ) {
                Point pt = new Point(e.x, e.y);
				if( treeViewer.getCell(pt) != null && 
					treeViewer.getCell(pt).getViewerRow() != null &&
					treeViewer.getCell(pt).getViewerRow().getItem() != null ) {
					handleDoubleClick((treeViewer.getCell(pt).getColumnIndex()), treeViewer.getCell(pt).getViewerRow().getItem().getData());
                }
            }
        });

        treeViewer.setUseHashlookup(true);

        /*** Tree table specific code starts ***/

        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        TreeColumn treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText(Messages.model);
        
        treeViewer.getTree().setSortColumn(treeColumn);
        treeViewer.getTree().setSortDirection(SWT.UP);
        treeViewer.setComparator(getTreeProvider().getComparator(treeViewer, treeColumn));

        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
        treeViewerColumn.getColumn().setText(Messages.security);
        treeViewerColumn.getColumn().setToolTipText(Messages.securityTooltip);

        treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(Messages.create);
        treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(Messages.read);
        treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(Messages.update);
        treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(Messages.delete);
        treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(Messages.execute);
        treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.LEFT | SWT.CHECK);
        treeViewerColumn.getColumn().setText(Messages.alter);

        TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(60));
        layout.addColumnData(new ColumnWeightData(10));
        layout.addColumnData(new ColumnWeightData(10));
        layout.addColumnData(new ColumnWeightData(8));
        layout.addColumnData(new ColumnWeightData(10));
        layout.addColumnData(new ColumnWeightData(10));
        layout.addColumnData(new ColumnWeightData(11));
        layout.addColumnData(new ColumnWeightData(8));

        tree.setLayout(layout);

        treeViewer.setContentProvider(getTreeProvider());
        treeViewer.setLabelProvider(getTreeProvider());

        treeViewer.setInput(getWizard().getTempContainer());
		
	}
	
    /*
     * This method tells the Tree Provider that 
     */
    private void handleSelection(int column, Object rowData) {
        if (column > 1) {
            Crud.Type crudType = Crud.getCrudType(column);
            // Determine any info or warning messages that should be issued before toggling
            boolean doToggle = true;
            IStatus toggleStatus = getTreeProvider().getToggleStatus(rowData, crudType);
            // If info is returned, the element cannot be toggled - display info dialog
            String autoToggle = UiPlugin.getDefault().getPreferenceStore().getString(AUTO_WILL_TOGGLE_WITH_CHILDREN);
            if( !MessageDialogWithToggle.ALWAYS.equals(autoToggle) ) {
	            if (toggleStatus.getSeverity() == IStatus.INFO) {
	            	doToggle = false;
	                
	                MessageDialogWithToggle.openOkCancelConfirm(
	                		getShell(), Messages.state_change_info, toggleStatus.getMessage(), Messages.alwaysToggleAllChildrenMessage, false,
	                		UiPlugin.getDefault().getPreferenceStore(), AUTO_WILL_TOGGLE_WITH_CHILDREN);
	            } else if (toggleStatus.getSeverity() == IStatus.WARNING) {
	                MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(getShell(), Messages.confirm_state_change, toggleStatus.getMessage(),
	                		Messages.alwaysToggleAllChildrenMessage, false,
	                        UiPlugin.getDefault().getPreferenceStore(), AUTO_WILL_TOGGLE_WITH_CHILDREN);
	                doToggle = dialog.getReturnCode() == Window.OK;
	            }
	        }
            // Toggle the state
            if (doToggle) {
                getTreeProvider().togglePermission(rowData, crudType);
            }
            treeViewer.refresh();

            validateInputs();
        }
    }
    
    /*
     * This method tells the Tree Provider that 
     */
    private void handleDoubleClick(int column, Object target) {
        if (column == 1 ) {
        	if( getTreeProvider().allowsRowFilter(target) ) {
	        	Permission perm = getTreeProvider().getPermission(target);
	        	boolean existingPerm = perm != null;
	        	if( perm == null ) {
	        		perm = getTreeProvider().createPermission(target);
	        	}
	        	
	        	String message = getTreeProvider().getSecurityDialogMessage(target);
	        	RowBasedSecurityDialog dialog = 
	        			new RowBasedSecurityDialog(getShell(), 
	        					Messages.setSecurityValuesTitle, message, perm, existingPerm);
	        	
	        	if( dialog.open() == Window.OK) {
	        		if( dialog.hasCondition()) {
	        			perm.setCondition(dialog.getCondition());
	                    perm.setConstraint(dialog.getConstraintValue());
	        		}
	        		
	    	        getWizard().refreshAllTabs();
	    	    	
	    	        validateInputs();
	        	}
        	} else if( getTreeProvider().allowsColumnMask(target) ) {
	        	Permission perm = getTreeProvider().getPermission(target);
	        	boolean existingPerm = perm != null;
	        	if( perm == null ) {
	        		perm = getTreeProvider().createPermission(target);
	        	}
	        	
	        	String message = getTreeProvider().getSecurityDialogMessage(target);
	        	ColumnMaskingDialog dialog = 
	        			new ColumnMaskingDialog(getShell(), 
	        					Messages.setSecurityValuesTitle, message, perm, existingPerm);
	        	
	        	if( dialog.open() == Window.OK) {
	        		if( dialog.hasCondition()) {
	        			perm.setCondition(dialog.getCondition());
	        		}
	        		if( dialog.hasMask()) {
		                perm.setMask(dialog.getMask());
		                perm.setOrder(dialog.getOrder());
	        		}
	        		
	    	        getWizard().refreshAllTabs();
	    	    	
	    	        validateInputs();
	        	}
        	}
        }
    }
    
    @Override
    public void refresh() {
    	treeViewer.refresh();
    }
    
    private void createModelFilterGroup(Composite parent) {
    	Composite filterGroup = WidgetFactory.createGroup(parent, SWT.BORDER);
    	GridDataFactory.fillDefaults().grab(true, false).applyTo(filterGroup);
    	GridLayoutFactory.fillDefaults().numColumns(4).margins(3, 3).spacing(3, 3).applyTo(filterGroup);
    	
    	// Filter Label
    	Label tempLabel = new Label(filterGroup, SWT.NONE);
    	tempLabel.setText(FILTER);
    	
    	// Filter Text Field
    	final Text text = new Text(filterGroup, SWT.BORDER | SWT.FILL);
    	GridDataFactory.fillDefaults().grab(true, false).applyTo(text);

    	getTreeProvider().setModelFilter(StringConstants.EMPTY_STRING, ALL);
    	
    	text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				getTreeProvider().setModelFilter(text.getText(), modelTypeCombo.getText());
				treeViewer.refresh();
			}
		});
    	
    	
    	
    	// Model Type Combo
    	// Model Types = ALL, VIEW and SOURCE
    	modelTypeCombo = new Combo(filterGroup,  SWT.NONE);
    	modelTypeCombo.setItems(new String[] {ALL, SOURCE, VIEW} );
    	modelTypeCombo.select(0);
    	GridDataFactory.fillDefaults().grab(false, false).hint(90, 10).applyTo(modelTypeCombo);
    	
    	modelTypeCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				getTreeProvider().setModelFilter(text.getText(), modelTypeCombo.getText());
				treeViewer.refresh();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
    	
    	// Clear Filter Button
    	// Set filter text to "", type to ALL
    	clearFilterButton = WidgetFactory.createButton(filterGroup, SWT.PUSH);
    	clearFilterButton.setImage(RolesUiPlugin.getInstance().getImage(RolesUiConstants.Images.CLEAR));
    	clearFilterButton.setToolTipText("Clear Filter");  //$NON-NLS-1$
    	clearFilterButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				modelTypeCombo.select(0);
				text.setText(StringConstants.EMPTY_STRING);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
    }

}
