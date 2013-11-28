/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.xmlfile.panels;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.teiid.designer.query.proc.ITeiidXmlFileInfo;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlImportXmlConfigurationPage;
import org.teiid.designer.transformation.ui.wizards.xmlfile.XmlAttribute;
import org.teiid.designer.transformation.ui.wizards.xmlfile.XmlElement;
import org.teiid.designer.ui.common.tree.AbstractTreeContentProvider;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * @since 8.0
 */
public class XmlFileContentsGroup {
	
	TreeViewer xmlTreeViewer;
	Action createColumnAction, setRootPathAction;
	ColumnsInfoPanel columnsInfoPanel;
	
	final TeiidXmlImportXmlConfigurationPage configPage;

	public XmlFileContentsGroup(Composite parent, TeiidXmlImportXmlConfigurationPage configPage) {
		super();
		this.configPage = configPage;
		createPanel(parent);
	}
	
	private TeiidXmlFileInfo getFileInfo() {
		return configPage.getFileInfo();
	}
	
	public void setColumnsInfoPanel(ColumnsInfoPanel columnsInfoPanel) {
		this.columnsInfoPanel = columnsInfoPanel;
	}
	
    public void loadFileContentsViewer() {
    	this.xmlTreeViewer.setInput(this.configPage.getFileInfo());
    	this.columnsInfoPanel.refresh();
    }
	
	private void createPanel(Composite parent) {
    	Group fileContentsGroup = WidgetFactory.createGroup(parent, Messages.XmlFileContents, SWT.NONE, 1, 4);
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 400;
    	fileContentsGroup.setLayoutData(gd);
    	
    	this.xmlTreeViewer = new TreeViewer(fileContentsGroup, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        this.xmlTreeViewer.getControl().setLayoutData(data);
        this.xmlTreeViewer.setContentProvider(new AbstractTreeContentProvider() {
        	
            @Override
            public Object[] getChildren( Object element ) {
                return getNodeChildren(element);
            }

            @Override
			public Object getParent( Object element ) {
                return getNodeParent(element);
            }

            @Override
            public boolean hasChildren( Object element ) {
                return getNodeHasChildren(element);
            }

        });
    	
        this.xmlTreeViewer.setLabelProvider(new LabelProvider() {

            @Override
            public Image getImage( Object element ) {
                return getNodeImage(element);
            }

            @Override
            public String getText( Object element ) {
                return getNodeName(element);
            }
        });
        
     // Add a Context Menu
        final MenuManager columnMenuManager = new MenuManager();
        this.xmlTreeViewer.getControl().setMenu(columnMenuManager.createContextMenu(parent));
        this.xmlTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see oblafond@redhat.comrg.eclipse.jface.viewers.IcreateColumnSelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
            	columnMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
                if (sel.size() == 1) {
					columnMenuManager.add(createColumnAction);
					columnMenuManager.add(setRootPathAction);
					columnsInfoPanel.notifySelection(true);
                } else {
                	columnsInfoPanel.notifySelection(false);
                }

            }
        });
        
		this.xmlTreeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection != null && !selection.isEmpty() ) {
					createColumn();
				}
			}
		});
        
        this.createColumnAction = new Action(Messages.CreateColumn) {
            @Override
            public void run() {
            	createColumn();
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
    	IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
    	Object obj = sel.getFirstElement();
    	if( obj instanceof XmlElement ) {
    		String pathValue = ((XmlElement)obj).getFullPath();
    		getFileInfo().setRootPath(pathValue);
    		configPage.handleInfoChanged(false);
    	}
    }
    
    public void createColumn() {
    	IStructuredSelection sel = (IStructuredSelection)xmlTreeViewer.getSelection();
    	Object obj = sel.getFirstElement();
    	if( obj instanceof XmlElement || obj instanceof XmlAttribute ) {
    		getFileInfo().addNewColumn(obj);
    		this.configPage.handleInfoChanged(false);
    	}
    }
	
    Object[] getNodeChildren( Object element ) {
        if (element instanceof ITeiidXmlFileInfo) {
            return new Object[] {getFileInfo().getRootNode()};
        }

        Collection<Object> children = new ArrayList<Object>();
        for( Object attr : ((XmlElement)element).getAttributes()) {
        	children.add(attr);
        }
        for( Object elem : ((XmlElement)element).getChildrenDTDElements()) {
        	children.add(elem);
        }
        
        return children.toArray(new Object[0]);
    }
    
    boolean getNodeHasChildren( Object element ) {
    	if( element instanceof XmlElement) {
	        XmlElement node = (XmlElement)element;
	        Object[] children = node.getChildrenDTDElements();
	        Object[] attributes = node.getAttributes();
	
	        return (children.length + attributes.length) > 0;
    	}
    	
    	return false;
    }
    
    Image getNodeImage( Object element ) {
    	if( element instanceof XmlAttribute) {
    		return UiPlugin.getDefault().getImage(UiConstants.Images.SCHEMA_ATTRIBUTE);
    	}
    	if( element instanceof XmlElement ) {
    		return UiPlugin.getDefault().getImage(UiConstants.Images.SCHEMA_ELEMENT);
    	}
    	return null;
    }
    
    String getNodeName( Object element ) {
    	if( element instanceof XmlElement ) {
	        XmlElement node = (XmlElement)element;
	        return node.getName();
    	}
    	
    	if( element instanceof XmlAttribute) {
    		XmlAttribute node = (XmlAttribute)element;
    		return node.getName();
    	}
    	
    	return ""; //$NON-NLS-1$
    }

    Object getNodeParent( Object element ) {
        return ((XmlElement)element).getParent();
    }
}
