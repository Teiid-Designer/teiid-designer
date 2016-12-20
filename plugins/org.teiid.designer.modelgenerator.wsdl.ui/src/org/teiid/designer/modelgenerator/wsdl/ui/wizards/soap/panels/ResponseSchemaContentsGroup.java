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
import org.eclipse.xsd.XSDComplexTypeDefinition;
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
public class ResponseSchemaContentsGroup {
	TreeViewer schemaTreeViewer;
	Action createColumnAction, setRootPathAction;
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
			 * @see org.eclipse.jface.viewers.
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
					} else if (element instanceof XSDElementDeclarationImpl ) {
						Object type = ((XSDElementDeclarationImpl) element).getTypeDefinition();
						if( type instanceof XSDComplexTypeDefinition) {
							columnMenuManager.add(setRootPathAction);
						}
					} else if (element instanceof XSDParticleImpl ) {
						columnMenuManager.add(setRootPathAction);
					}
				} else {
					if( canAddAllSelected()) {
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
		 this.setRootPathAction = new Action(Messages.SetAsRootPath) {
	            @Override
	            public void run() {
	            	setRootPath();
	            }
			};
		}
		
	    private void setRootPath() {
	    	IStructuredSelection selection = (IStructuredSelection) schemaTreeViewer.getSelection();
	    	Object obj = selection.getFirstElement();
    		String pathValue = ((SchemaNode)obj).getChildren().iterator().next().getFullPath();
    		if( pathValue.endsWith("/")) { //$NON-NLS-1$
    			pathValue = pathValue.substring(0, pathValue.length()-1);
			}
    		this.columnsInfoPanel.getRootPathText().setText(pathValue);
    		getResponseInfo().setRootPath(pathValue);
    		this.columnsInfoPanel.refresh();
	    }
	
	private boolean canAddAllSelected() {
		boolean result = true;
		IStructuredSelection selection = (IStructuredSelection) schemaTreeViewer.getSelection();
		if (selection.size() > 0 ) {
			for( Object obj : selection.toArray() ) {
				if( obj instanceof SchemaNode ) {
					Object element = ((SchemaNode)selection.getFirstElement()).getElement();
				    if( element instanceof XSDElementDeclarationImpl ) {
				    	result = false;
				    } else {
				    	if( !ImportWsdlSchemaHandler.shouldCreateResponseColumn(element) ) {
				    		result = false;
				    	}
				    }
				}
			}
		}
		
		return result;
	}
	
	public void setColumnsInfoPanel(ColumnsInfoPanel panel) {
		this.columnsInfoPanel = panel;
	}
	
	public String[] createResponseColumn() {
		List<String> results = new ArrayList<String>();
		IStructuredSelection selection = (IStructuredSelection) schemaTreeViewer.getSelection();
		for( Object obj : selection.toArray() ) {
			IStructuredSelection sel = new StructuredSelection(obj);
			String value = this.detailsPage.getSchemaHandler().createResponseColumn(this.type, sel, getResponseInfo());
			
			if( value != null ) {
				results.add(value);
			}
		}
		return results.toArray(new String[results.size()]);
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
