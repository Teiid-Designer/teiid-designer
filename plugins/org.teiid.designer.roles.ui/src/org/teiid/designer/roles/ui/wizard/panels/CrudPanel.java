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
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.teiid.designer.roles.Crud;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.roles.ui.wizard.dialogs.SecurityDefinitionDialog;
import org.teiid.designer.ui.UiPlugin;

/**
 *
 */
public class CrudPanel extends DataRolePanel {
    
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
        if (column == 1 && getTreeProvider().allowsSecurity(target) ) {
        	Permission perm = getTreeProvider().getPermission(target);
        	boolean existingPerm = perm != null;
        	if( perm == null ) {
        		perm = getTreeProvider().createPermission(target);
        	}
        	boolean allowsCondition = getTreeProvider().allowsCondition(target);
        	boolean allowsMask = getTreeProvider().allowsMasking(target);
        	
        	String message = getTreeProvider().getSecurityDialogMessage(target);
        	SecurityDefinitionDialog dialog = 
        			new SecurityDefinitionDialog(getShell(), 
        					Messages.setSecurityValuesTitle, message, perm, 
        					allowsCondition, allowsMask, existingPerm);
        	
        	if( dialog.open() == Window.OK) {
        		if( allowsCondition) {
        			perm.setCondition(dialog.getCondition());
                    perm.setConstraint(dialog.getConstraintValue());
        		}
        		if( allowsMask ) {
	                perm.setMask(dialog.getMask());
	                perm.setOrder(dialog.getOrder());
        		}
        		
    	        getWizard().refreshAllTabs();
    	    	
    	        validateInputs();
        	}
        }
    }
    
    @Override
    public void refresh() {
    	treeViewer.refresh();
    }

}
