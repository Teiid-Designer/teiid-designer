/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ColumnInfo;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ProcedureInfo;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

public class ColumnsInfoPanel {
	private ProcedureInfo procedureInfo;
	private Button addButton, deleteButton, upButton, downButton;
	private Text rootPathText;
	
	TreeViewer schemaTreeViewer;
	

	EditColumnsPanel editColumnsPanel;
	private int type = -1;
	
	final OperationsDetailsPage detailsPage;
	
	boolean initializing = false;
	
	public ColumnsInfoPanel(Composite parent, int style, int type, OperationsDetailsPage detailsPage) {
		super();
		this.type = type;
		this.detailsPage = detailsPage;
		init(parent);
	}
	
	public ProcedureInfo getProcedureInfo() {
		return this.procedureInfo;
	}
	
	public void setProcedureInfo(ProcedureInfo info) {
		initializing = true;
		this.procedureInfo = info;
		editColumnsPanel.setProcedureInfo(info);
		editColumnsPanel.refresh();
		this.addButton.setEnabled(info != null);
		
		String rootPath = StringUtilities.EMPTY_STRING;
		if( this.getProcedureInfo().getRootPath() != null ) {
			rootPath = this.getProcedureInfo().getRootPath();
		}
		this.rootPathText.setText(rootPath);
		initializing = false;
	}
	
	public void refresh() {
		this.editColumnsPanel.refresh();
	}
	
	public Text getRootPathText() {
		return rootPathText;
	}
	
	private void init(Composite parent) {
    	Group columnInfoGroup = WidgetFactory.createGroup(parent, Messages.ColumnInfo, SWT.NONE, 2);
    	columnInfoGroup.setLayout(new GridLayout(2, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.horizontalSpan = 1;
    	columnInfoGroup.setLayoutData(gd);
    	
    	Label prefixLabel = new Label(columnInfoGroup, SWT.NONE);
    	prefixLabel.setText(Messages.RootPath);
        
    	rootPathText = WidgetFactory.createTextField(columnInfoGroup, SWT.NONE);
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 50;
    	gd.horizontalSpan=1;
    	gd.grabExcessHorizontalSpace = true;
    	rootPathText.setLayoutData(gd);
    	rootPathText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent event) {
    			notifyColumnDataChanged();
    		}
    	});

    	Composite leftToolbarPanel = new Composite(columnInfoGroup, SWT.NONE);
    	GridLayout tbGL = new GridLayout();
    	tbGL.marginHeight = 0;
    	tbGL.marginWidth = 0;
    	tbGL.verticalSpacing = 2;
    	leftToolbarPanel.setLayout(tbGL);
	GridData ltpGD = new GridData(GridData.FILL_VERTICAL);
	ltpGD.heightHint=120;
	leftToolbarPanel.setLayoutData(ltpGD);
    	
    	addButton = new Button(leftToolbarPanel, SWT.PUSH);
    	addButton.setText(Messages.Add);
    	addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	addButton.setEnabled(false);
    	addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
                UiBusyIndicator.showWhile(addButton.getDisplay(),
                        new Runnable() {
                            @Override
                            public void run() {
                                String name = null;

                                try {
                                    /*
                                     * Stop the rootPathText modify listener
                                     * from firing since it is notifying the
                                     * panels to refresh which is done anyway.
                                     */
                                    initializing = true;

                                    name = detailsPage
                                            .createResponseColumn(type);
                                }
                                finally {
                                    initializing = false;
                                }

                                if (name == null) {
                                    return;
                                }

                                boolean ok = MessageDialog.openQuestion(
                                        detailsPage.getShell(),
                                        Messages.InvalidSelectedSchemaObject,
                                        NLS.bind(
                                                Messages.InvalidSelectedSchemaObject_column_msg,
                                                name));
                                if (!ok) {
                                    return;
                                }

                                if (type == ProcedureInfo.TYPE_BODY) {
                                    detailsPage
                                            .getProcedureGenerator()
                                            .getResponseInfo()
                                            .addBodyColumn(
                                                    name,
                                                    false,
                                                    ColumnInfo.DEFAULT_DATATYPE,
                                                    null, null);
                                }
                                else {
                                    detailsPage
                                            .getProcedureGenerator()
                                            .getResponseInfo()
                                            .addHeaderColumn(
                                                    name,
                                                    false,
                                                    ColumnInfo.DEFAULT_DATATYPE,
                                                    null, null);
                                }
                                editColumnsPanel.refresh();
                                notifyColumnDataChanged();
                            }
                        });
            }
        });
    	
    	deleteButton = new Button(leftToolbarPanel, SWT.PUSH);
    	deleteButton.setText(Messages.Delete);
    	deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	deleteButton.setEnabled(false);
    	deleteButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColumnInfo info = editColumnsPanel.getSelectedColumn();
				if( info != null ) {
					if( type == ProcedureInfo.TYPE_BODY ) {
						detailsPage.getProcedureGenerator().getResponseInfo().removeBodyColumn(info);
					} else {
						detailsPage.getProcedureGenerator().getResponseInfo().removeHeaderColumn(info);
					}
					
					deleteButton.setEnabled(false);
					editColumnsPanel.selectRow(-1);
					editColumnsPanel.refresh();
					notifyColumnDataChanged();
				}
			}
    		
		});
    	
    	upButton = new Button(leftToolbarPanel, SWT.PUSH);
    	upButton.setText(Messages.Up);
    	upButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	upButton.setEnabled(false);
    	upButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColumnInfo info = editColumnsPanel.getSelectedColumn();
				if( info != null ) {
					int selectedIndex = editColumnsPanel.getSelectedIndex();
					if( type == ProcedureInfo.TYPE_BODY ) {
    					detailsPage.getProcedureGenerator().getResponseInfo().moveBodyColumnUp(info);
    					downButton.setEnabled(detailsPage.getProcedureGenerator().getResponseInfo().canMoveBodyColumnDown(info));
    					upButton.setEnabled(detailsPage.getProcedureGenerator().getResponseInfo().canMoveBodyColumnUp(info));
					} else {
						detailsPage.getProcedureGenerator().getResponseInfo().moveHeaderColumnUp(info);
    					downButton.setEnabled(detailsPage.getProcedureGenerator().getResponseInfo().canMoveHeaderColumnDown(info));
    					upButton.setEnabled(detailsPage.getProcedureGenerator().getResponseInfo().canMoveHeaderColumnUp(info));
					}
					editColumnsPanel.refresh();
					notifyColumnDataChanged();
					
					editColumnsPanel.selectRow(selectedIndex-1);
				}
			}
    		
		});
    	
    	downButton = new Button(leftToolbarPanel, SWT.PUSH);
    	downButton.setText(Messages.Down);
    	downButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	downButton.setEnabled(false);
    	downButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColumnInfo info = editColumnsPanel.getSelectedColumn();
				if( info != null ) {
					int selectedIndex = editColumnsPanel.getSelectedIndex();
					if( type == ProcedureInfo.TYPE_BODY ) {
    					detailsPage.getProcedureGenerator().getResponseInfo().moveBodyColumnDown(info);
    					downButton.setEnabled(detailsPage.getProcedureGenerator().getResponseInfo().canMoveBodyColumnDown(info));
    					upButton.setEnabled(detailsPage.getProcedureGenerator().getResponseInfo().canMoveBodyColumnUp(info));
					} else {
						detailsPage.getProcedureGenerator().getResponseInfo().moveHeaderColumnDown(info);
    					downButton.setEnabled(detailsPage.getProcedureGenerator().getResponseInfo().canMoveHeaderColumnDown(info));
    					upButton.setEnabled(detailsPage.getProcedureGenerator().getResponseInfo().canMoveHeaderColumnUp(info));
					}					editColumnsPanel.refresh();
					notifyColumnDataChanged();
					
					editColumnsPanel.selectRow(selectedIndex+1);
				}
			}
    		
		});
    	
    	editColumnsPanel = new EditColumnsPanel(columnInfoGroup, SWT.NONE, this.type, this.detailsPage);
    	
    	editColumnsPanel.addSelectionListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				
				if( sel.isEmpty()) {
					deleteButton.setEnabled(false);
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				} else {
					boolean enable = true;
					Object[] objs = sel.toArray();
					ColumnInfo columnInfo = null;
					for( Object obj : objs) {
						if(  !(obj instanceof ColumnInfo)) {
							enable = false;
							break;
						} else {
							columnInfo = (ColumnInfo)obj;
						}
					} 
					if( objs.length == 0 ) {
						enable = false;
					}
					deleteButton.setEnabled(enable);
					if( enable ) {
						if( type == ProcedureInfo.TYPE_BODY ) {
    						upButton.setEnabled(procedureInfo.canMoveBodyColumnUp(columnInfo));
    						downButton.setEnabled(procedureInfo.canMoveBodyColumnDown(columnInfo));
						} else {
							upButton.setEnabled(procedureInfo.canMoveHeaderColumnUp(columnInfo));
    						downButton.setEnabled(procedureInfo.canMoveHeaderColumnDown(columnInfo));
						}
					}
					
				}
				
			}
		});
    }
	
	private void notifyColumnDataChanged() {
		if( ! initializing ) {
			this.detailsPage.notifyColumnDataChanged();
		}
	}
	
	public void disableButtons() {
		deleteButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
	}
	
	public void setEnabled(boolean enable ) {
		addButton.setEnabled(enable);
		disableButtons();
		rootPathText.setEnabled(enable);
		editColumnsPanel.setEnabled(enable);
	}

}