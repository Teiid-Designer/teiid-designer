/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ImportWsdlSchemaHandler;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ProcedureInfo;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.SchemaTreeModel.SchemaNode;

import com.metamatrix.ui.internal.util.WidgetFactory;

public class ResponseSchemaContentsGroup {
	TreeViewer schemaTreeViewer;
	Action createColumnAction;
	ColumnsInfoPanel columnsInfoPanel;
	
	// TYPE either BODY or SOAP
	int type;

	final OperationsDetailsPage detailsPage;

	public ResponseSchemaContentsGroup(Composite parent, int type, OperationsDetailsPage detailsPage) {
		super();
		this.type = type;
		this.detailsPage = detailsPage;
		createPanel(parent);
	}

	private void createPanel(Composite parent) {
		Group schemaContentsGroup = WidgetFactory.createGroup(parent, Messages.SchemaContents, SWT.NONE, 1, 4);
		schemaContentsGroup.setLayout(new GridLayout(4, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.heightHint = 120;
		schemaContentsGroup.setLayoutData(gd);

		this.schemaTreeViewer = new TreeViewer(schemaContentsGroup, SWT.SINGLE);

		this.schemaTreeViewer.setContentProvider(this.detailsPage.getSchemaContentProvider());
		this.schemaTreeViewer.setLabelProvider(this.detailsPage.getSchemaLabelProvider());
		this.schemaTreeViewer.setAutoExpandLevel(3);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		this.schemaTreeViewer.getControl().setLayoutData(data);
		this.schemaTreeViewer.setInput(null);

		// Add a Context Menu
		final MenuManager columnMenuManager = new MenuManager();
		this.schemaTreeViewer.getControl().setMenu(columnMenuManager.createContextMenu(parent));
		this.schemaTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			/**
			 * {@inheritDoc}
			 * 
			 * @see oblafond@redhat.comrg.eclipse.jface.viewers.
			 *      ISelectionChangedListener #selectionChanged(org.eclipse
			 *      .jface.viewers.SelectionChangedEvent)
			 */
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				columnMenuManager.removeAll();
				IStructuredSelection selection = (IStructuredSelection) schemaTreeViewer.getSelection();
				if (selection.size() == 1 && selection.getFirstElement() instanceof SchemaNode) {
					Object element = ((SchemaNode)selection.getFirstElement()).getElement();
					if( ImportWsdlSchemaHandler.shouldCreateResponseColumn(element) ) {
						columnMenuManager.add(createColumnAction);
					}
				}

			}
		});

		this.schemaTreeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection != null && !selection.isEmpty() && selection.getFirstElement() instanceof SchemaNode) {
					Object element = ((SchemaNode)selection.getFirstElement()).getElement();
					if( ImportWsdlSchemaHandler.shouldCreateResponseColumn(element) ) {
						createResponseColumn();
					}
				}
			}
		});

		this.createColumnAction = new Action(Messages.AddAsNewElement) {
			@Override
			public void run() {
				createResponseColumn();
			}
		};
	}
	
	public void setColumnsInfoPanel(ColumnsInfoPanel panel) {
		this.columnsInfoPanel = panel;
	}
	
	public String createResponseColumn() {
		return this.detailsPage.getSchemaHandler()
			.createResponseColumn(this.type, (IStructuredSelection) schemaTreeViewer.getSelection(), getResponseInfo());
	}
	
	public void setInput(Object value) {
		schemaTreeViewer.setInput(value);
		// Update the columns view buttons
		columnsInfoPanel.disableButtons();
	}
	
	private ProcedureInfo getResponseInfo() {
		return this.detailsPage.getProcedureGenerator().getResponseInfo();
	}
	
	public void setEnabled(boolean enable) {
		this.schemaTreeViewer.getTree().setEnabled(enable);
		this.columnsInfoPanel.setEnabled(enable);
	}
	
}
