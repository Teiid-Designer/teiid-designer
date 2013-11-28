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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.AttributeInfo;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ColumnInfo;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ProcedureInfo;
import org.teiid.designer.query.proc.wsdl.IWsdlAttributeInfo;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * @since 8.0
 */
public class ElementsInfoPanel {
	private ProcedureInfo procedureInfo;
	private Button addButton, deleteButton, upButton, downButton;
	EditElementsPanel editElementsPanel;
	private int type = -1; 

	final OperationsDetailsPage detailsPage;

	public ElementsInfoPanel(Composite parent, int style, int type, OperationsDetailsPage detailsPage) {
		super();
		this.type = type;
		this.detailsPage = detailsPage;
		init(parent);
	}

	public ProcedureInfo getProcedureInfo() {
		return this.procedureInfo;
	}

	public void setProcedureInfo(ProcedureInfo info) {
		this.procedureInfo = info;
		editElementsPanel.setProcedureInfo(info);
		editElementsPanel.refresh();
		this.addButton.setEnabled(info != null);
	}

	public void refresh() {
		this.editElementsPanel.refresh();
	}

	private void init(Composite parent) {
		Group columnInfoGroup = WidgetFactory.createGroup(parent, Messages.ElementInfo, SWT.NONE, 2, 2);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		columnInfoGroup.setLayoutData(gd);

		Composite leftToolbarPanel = new Composite(columnInfoGroup, SWT.NONE);
		GridLayout tbGL = new GridLayout();
		tbGL.marginHeight = 0;
		tbGL.marginWidth = 0;
		tbGL.verticalSpacing = 2;
		leftToolbarPanel.setLayout(tbGL);
		GridData ltpGD = new GridData(GridData.FILL_VERTICAL);
		ltpGD.heightHint = 120;

		leftToolbarPanel.setLayoutData(ltpGD);

		addButton = new Button(leftToolbarPanel, SWT.PUSH);
		addButton.setText(Messages.Add);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setEnabled(false);
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Check Selection from tree
				String name = detailsPage.createRequestColumn(type);
				if (name != null) {
					boolean ok = MessageDialog.openQuestion(detailsPage.getShell(),
						Messages.InvalidSelectedSchemaObject,
						NLS.bind(Messages.InvalidSelectedSchemaObject_element_msg, name));
					
					if( ok ) {
						if( type == ProcedureInfo.TYPE_BODY ) {
							detailsPage.getProcedureGenerator().getRequestInfo().addBodyColumn(name, false, ColumnInfo.DEFAULT_DATATYPE, null, null, null);
						} else {
							detailsPage.getProcedureGenerator().getRequestInfo().addHeaderColumn(name, false, ColumnInfo.DEFAULT_DATATYPE, null, null, null);
						}
    					editElementsPanel.refresh();
    					notifyColumnDataChanged();
					}
				}
			}

		});

		deleteButton = new Button(leftToolbarPanel, SWT.PUSH);
		deleteButton.setText(Messages.Delete);
		deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		deleteButton.setEnabled(false);
		deleteButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object selectedObject = editElementsPanel.getSelectedObject();
				if( selectedObject instanceof ColumnInfo) {
					ColumnInfo info = (ColumnInfo)selectedObject;
					if (info != null) {
						if( type == ProcedureInfo.TYPE_BODY ) {
							detailsPage.getProcedureGenerator().getRequestInfo().removeBodyColumn(info);
						} else {
							detailsPage.getProcedureGenerator().getRequestInfo().removeHeaderColumn(info);
						}
	
						deleteButton.setEnabled(false);
						editElementsPanel.selectRow(-1);
						editElementsPanel.refresh();
						notifyColumnDataChanged();
					}
				} else if( selectedObject instanceof IWsdlAttributeInfo) {
					AttributeInfo info = (AttributeInfo)selectedObject;
					if (info != null) {
						ColumnInfo parentColumnInfo = info.getColumnInfo();
						parentColumnInfo.removeAttributeInfo(info);
	
						deleteButton.setEnabled(false);
						editElementsPanel.selectRow(-1);
						editElementsPanel.refresh();
						
						notifyColumnDataChanged();
					}
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
				ColumnInfo info = editElementsPanel.getSelectedColumn();
				if (info != null) {
					int selectedIndex = editElementsPanel.getSelectedIndex();
					if( type == ProcedureInfo.TYPE_BODY ) {
    					detailsPage.getProcedureGenerator().getRequestInfo().moveBodyColumnUp(info);
    					downButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveBodyColumnDown(info));
    					upButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveBodyColumnUp(info));
					} else {
						detailsPage.getProcedureGenerator().getRequestInfo().moveHeaderColumnUp(info);
    					downButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveHeaderColumnDown(info));
    					upButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveHeaderColumnUp(info));
					}
					editElementsPanel.refresh();
					notifyColumnDataChanged();

					editElementsPanel.selectRow(selectedIndex - 1);
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
				ColumnInfo info = editElementsPanel.getSelectedColumn();
				if (info != null) {
					int selectedIndex = editElementsPanel.getSelectedIndex();
					if( type == ProcedureInfo.TYPE_BODY ) {
    					detailsPage.getProcedureGenerator().getRequestInfo().moveBodyColumnDown(info);
    					downButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveBodyColumnDown(info));
    					upButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveBodyColumnUp(info));
					} else {
						detailsPage.getProcedureGenerator().getRequestInfo().moveHeaderColumnDown(info);
    					downButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveHeaderColumnDown(info));
    					upButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveHeaderColumnUp(info));
					}
					editElementsPanel.refresh();
					notifyColumnDataChanged();

					editElementsPanel.selectRow(selectedIndex + 1);
				}
			}

		});

		editElementsPanel = new EditElementsPanel(columnInfoGroup, SWT.NONE, this.type, this.detailsPage);

		editElementsPanel.addSelectionListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				deleteButton.setEnabled(false);
				upButton.setEnabled(false);
				downButton.setEnabled(false);
				
				if (! sel.isEmpty()) {
					
					Object[] objs = sel.toArray();

					if (objs[0] instanceof ColumnInfo) {
						boolean enable = true;
						ColumnInfo columnInfo = (ColumnInfo) objs[0];
						
						deleteButton.setEnabled(true);
						if (enable) {
							if( type == ProcedureInfo.TYPE_BODY ) {
	    						upButton.setEnabled(procedureInfo.canMoveBodyColumnUp(columnInfo));
	    						downButton.setEnabled(procedureInfo.canMoveBodyColumnDown(columnInfo));
							} else {
								upButton.setEnabled(procedureInfo.canMoveHeaderColumnUp(columnInfo));
	    						downButton.setEnabled(procedureInfo.canMoveHeaderColumnDown(columnInfo));
							}
						}
					} else if( objs[0] instanceof IWsdlAttributeInfo) {
						deleteButton.setEnabled(true);
					}

				}

			}
		});
	}

	private void notifyColumnDataChanged() {
		this.detailsPage.notifyColumnDataChanged();
	}
	
	public void disableButtons() {
		deleteButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
	}
	
	public void setEnabled(boolean enable ) {
		addButton.setEnabled(enable);
		disableButtons();
		editElementsPanel.setEnabled(enable);
	}

}