/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.xsd.impl.XSDAttributeUseImpl;
import org.eclipse.xsd.impl.XSDElementDeclarationImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ImportWsdlSchemaHandler;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ProcedureInfo;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.SchemaTreeModel.SchemaNode;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * @since 8.0
 */
public class RequestSchemaContentsGroup {
	TreeViewer schemaTreeViewer;
	Action createElementAction;
	ElementsInfoPanel elementsInfoPanel;
	
	// TYPE either BODY or SOAP
	int type;

	final OperationsDetailsPage detailsPage;

	public RequestSchemaContentsGroup(Composite parent, int type, OperationsDetailsPage detailsPage) {
		super();
		this.type = type;
		this.detailsPage = detailsPage;
		createPanel(parent);
	}

	private void createPanel(Composite parent) {
		Group schemaContentsGroup = WidgetFactory.createGroup(parent, Messages.SchemaContents, SWT.NONE, 1, 4);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.heightHint = 120;
		schemaContentsGroup.setLayoutData(gd);

		this.schemaTreeViewer = new TreeViewer(schemaContentsGroup, SWT.MULTI);

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
				if( selection.size() == 1 && selection.getFirstElement() instanceof SchemaNode) {
					Object element = ((SchemaNode)selection.getFirstElement()).getElement();
					if (element instanceof XSDParticleImpl || 
							element instanceof XSDElementDeclarationImpl ||
							element instanceof XSDAttributeUseImpl ) {
						columnMenuManager.add(createElementAction);
					}
				} else {
					if( canAddAllSelected()) {
						columnMenuManager.add(createElementAction);
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
					if( ImportWsdlSchemaHandler.shouldCreateRequestColumn(element) ) {
						createRequestColumn();
					}
				}
			}
		});

		this.createElementAction = new Action(Messages.AddAsNewElement) {
			@Override
			public void run() {
				createRequestColumn();
			}
		};
	}
	
	public void setElementsInfoPanel(ElementsInfoPanel panel) {
		this.elementsInfoPanel = panel;
	}
	
	private boolean canAddAllSelected() {
		boolean result = true;
		IStructuredSelection selection = (IStructuredSelection) schemaTreeViewer.getSelection();
		if (selection.size() > 0 ) {
			for( Object obj : selection.toArray() ) {
				if( obj instanceof SchemaNode ) {
					Object element = ((SchemaNode)selection.getFirstElement()).getElement();
			    	if( !ImportWsdlSchemaHandler.shouldCreateRequestColumn(element) ) {
			    		result = false;
			    	}
				}
			}
		}
		
		return result;
	}
	
	
	public String[] createRequestColumn() {
		List<String> results = new ArrayList<String>();
		IStructuredSelection selection = (IStructuredSelection) schemaTreeViewer.getSelection();
		for( Object obj : selection.toArray() ) {
			IStructuredSelection sel = new StructuredSelection(obj);
			String value = this.detailsPage.getSchemaHandler().createRequestColumn(this.type, sel, getRequestInfo());
			if( value != null ) {
				results.add(value);
			}
		}
		return results.toArray(new String[results.size()]);
	}
	
	public void setInput(Object value) {
		schemaTreeViewer.setInput(value);
		// Update the columns view buttons
		elementsInfoPanel.disableButtons();
	}
	
	private ProcedureInfo getRequestInfo() {
		return this.detailsPage.getProcedureGenerator().getRequestInfo();
	}
	
	public void setEnabled(boolean enable) {
		this.schemaTreeViewer.getTree().setEnabled(enable);
		this.elementsInfoPanel.setEnabled(enable);
	}
}
