/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.TextActionHandler;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.AttributeInfo;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ColumnInfo;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ProcedureInfo;

import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.util.ModelGeneratorWsdlUiUtil;

public class EditElementsPanel {
	private final Image XSD_ELEMENT_ICON_IMG = 
			ModelGeneratorWsdlUiUtil.getImage(ModelGeneratorWsdlUiConstants.Images.XSD_ELEMENT_ICON);
	private final Image XSD_ATTRIBUTE_ICON_IMG = 
			ModelGeneratorWsdlUiUtil.getImage(ModelGeneratorWsdlUiConstants.Images.XSD_ATTRIBUTE_ICON);
	
	TreeViewer columnsViewer;
	ProcedureInfo procedureInfo;
	int type;
	
	TreeEditor treeEditor;
    Text textEditor;
    Composite textEditorParent;
    TextActionHandler textActionHandler;
    Object selectedObject;
    TreeItem[] cachedSelection;

	final OperationsDetailsPage detailsPage;

	public EditElementsPanel(Composite parent, int style, int type,
			OperationsDetailsPage detailsPage) {
		super();
		this.type = type;
		this.detailsPage = detailsPage;
		createPanel(parent);
	}

	public ProcedureInfo getProcedureInfo() {
		return this.procedureInfo;
	}

	public void setProcedureInfo(ProcedureInfo info) {
		this.procedureInfo = info;
		this.columnsViewer.setInput(this.procedureInfo);
		refresh();
	}

	private void createPanel(Composite parent) {
		Tree table = new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayout(new TableLayout());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 80;
		table.setLayoutData(gd);

		this.columnsViewer = new TreeViewer(table);
		this.columnsViewer.getControl().setLayoutData(gd);
		treeEditor = new TreeEditor(columnsViewer.getTree());

		ColumnDataTreeProvider provider = new ColumnDataTreeProvider();
		this.columnsViewer.setContentProvider(provider);
		this.columnsViewer.setLabelProvider(provider);
		this.columnsViewer.setAutoExpandLevel(3);

		this.columnsViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				renameInline();
			}
		});
		
		this.columnsViewer.getTree().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown( MouseEvent theEvent ) {
                if (theEvent.button == 1 ) {
                    if (isTextEditorActive() && selectedObject != null) {
                        saveChangesAndDispose((AttributeInfo)selectedObject);
                    }
                }
            }
        });
	}

	public void refresh() {
		this.columnsViewer.refresh();
		this.columnsViewer.expandAll();
	}

	public void refresh(Object element) {
		this.columnsViewer.refresh(element);
	}

	public void addSelectionListener(ISelectionChangedListener listener) {
		this.columnsViewer.addSelectionChangedListener(listener);
	}

	public Object getSelectedObject() {

		IStructuredSelection selection = (IStructuredSelection) this.columnsViewer.getSelection();
		for (Object obj : selection.toArray()) {
			return obj;
		}

		return null;
	}
	
	public ColumnInfo getSelectedColumn() {
		IStructuredSelection selection = (IStructuredSelection) this.columnsViewer.getSelection();
		for (Object obj : selection.toArray()) {
			if( obj instanceof ColumnInfo ) {
				return (ColumnInfo)obj;
			}
		}
		
		return null;
	}

	public int getSelectedIndex() {
		TreeItem[] selectedItems = columnsViewer.getTree().getSelection();
		if( selectedItems.length > 0 ) {
			int i=0;
			for( TreeItem item : columnsViewer.getTree().getItems()) {
				if( selectedItems[0] == item ) {
					return i;
				}
			}
		}
		return -1;
	}

	public void selectRow(int index) {
		if (index > -1) {
			TreeItem item = columnsViewer.getTree().getItem(index);
			columnsViewer.getTree().select(item);
		} else {
			columnsViewer.setSelection(new StructuredSelection());
		}
	}

	private void notifyColumnDataChanged() {
		this.detailsPage.notifyColumnDataChanged();
	}

	public void setEnabled(boolean enable) {
		columnsViewer.getTree().setEnabled(enable);
	}

	class ColumnDataTreeProvider implements ITreeContentProvider, ILabelProvider {
		

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// NO OP
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if( inputElement instanceof ProcedureInfo ) {
				if (type == ProcedureInfo.TYPE_BODY) {
					return procedureInfo.getBodyColumnInfoList();
				} else {
					return procedureInfo.getHeaderColumnInfoList();
				}
			}
			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			
			if( parentElement instanceof ColumnInfo ) {
				return ((ColumnInfo)parentElement).getAttributeInfoArray();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if( element instanceof ProcedureInfo ) {
				if (type == ProcedureInfo.TYPE_BODY) {
					return procedureInfo.getBodyColumnInfoList().length > 0;
				} else {
					return procedureInfo.getHeaderColumnInfoList().length > 0;
				}
			} else if( element instanceof ColumnInfo ) {
				return ((ColumnInfo)element).getAttributeInfoArray().length > 0;
			}
			return false;
		}
		
	    @Override
	    public Object getParent( Object element ) {
	        return null;
	    }

	    @Override
		public boolean isLabelProperty(Object element, String property) {
	        // NO OP
	        return false;
	    }

		@Override
		public Image getImage(Object element) {
			if (element instanceof ColumnInfo) {
				return XSD_ELEMENT_ICON_IMG;
			} else if( element instanceof AttributeInfo ) {
				return XSD_ATTRIBUTE_ICON_IMG;
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof ColumnInfo) {
				return ((ColumnInfo) element).getName();
			} else if( element instanceof AttributeInfo ) {
				return ((AttributeInfo) element).getSignature();
			}
			return null;
		}
		
	    @Override
	    public void addListener( ILabelProviderListener listener ) {
	        // NO OP
	    }
	    
	    @Override
	    public void removeListener( ILabelProviderListener listener ) {
	        // NO OP
	    }

	    @Override
	    public void dispose() {
	        // NO OP
	    }

	}

	private void renameInline() {
		Object obj = getSelectedObject();
		selectedObject = obj;
		cachedSelection = columnsViewer.getTree().getSelection();
        // Make sure text editor is created only once. Simply reset text
        // editor when action is executed more than once. Fixes bug 22269.
        if (textEditorParent == null) {
            createTextEditor(obj);
        }
        String name = null;
        if( obj instanceof ColumnInfo ) {
        	name = ((ColumnInfo)obj).getName();
        } else if( obj instanceof AttributeInfo ) {
        	name = ((AttributeInfo)obj).getAlias();
        }
        if (name != null) {
            textEditor.setText(name);
        }

        // Open text editor with initial size.
        textEditorParent.setVisible(true);
        Point textSize = textEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        textSize.x += textSize.y; // Add extra space for new characters.
        Point parentSize = textEditorParent.getSize();
        textEditor.setBounds(2, 1, Math.min(textSize.x, parentSize.x - 4), parentSize.y - 2);
        textEditorParent.redraw();
        textEditor.selectAll();
        textEditor.setFocus();
	}
	
	 private void createTextEditor( final Object obj ) {
        // Create text editor parent. This draws a nice bounding rect.
        textEditorParent = createEditorParent();
        textEditorParent.setVisible(false);
        textEditorParent.addListener(SWT.Paint, new Listener() {
            public void handleEvent( Event e ) {
                Point textSize = textEditor.getSize();
                Point parentSize = textEditorParent.getSize();
                e.gc.drawRectangle(0, 0, Math.min(textSize.x + 4, parentSize.x - 1), parentSize.y - 1);
            }
        });

        // Create inner text editor.
        textEditor = new Text(textEditorParent, SWT.NONE);
        textEditorParent.setBackground(textEditor.getBackground());
        textEditor.addListener(SWT.Modify, new Listener() {
            public void handleEvent( Event e ) {
                Point textSize = textEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                textSize.x += textSize.y; // Add extra space for new characters.
                Point parentSize = textEditorParent.getSize();
                textEditor.setBounds(2, 1, Math.min(textSize.x, parentSize.x - 4), parentSize.y - 2);
                textEditorParent.redraw();
            }
        });
        textEditor.addListener(SWT.Traverse, new Listener() {
            public void handleEvent( Event event ) {

                // Workaround for Bug 20214 due to extra
                // traverse events
                switch (event.detail) {
                    case SWT.TRAVERSE_ESCAPE:
                        // Do nothing in this case
                        disposeTextWidget();
                        event.doit = true;
                        event.detail = SWT.TRAVERSE_NONE;
                        break;
                    case SWT.TRAVERSE_RETURN:
                        saveChangesAndDispose(obj);
                        event.doit = true;
                        event.detail = SWT.TRAVERSE_NONE;
                        break;
                }
            }
        });
        textEditor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost( FocusEvent fe ) {
                //saveChangesAndDispose(obj);
            }
        });

        if (textActionHandler != null) textActionHandler.addText(textEditor);
    }

    Composite createEditorParent() {
        Tree tree = columnsViewer.getTree();
        Composite result = new Composite(tree, SWT.NONE);
        // Now let's make sure the target eObject is selected
        TreeItem[] selectedItems = cachedSelection; // tree.getSelection();
        if (selectedItems.length > 0) {
            treeEditor.horizontalAlignment = SWT.LEFT;
            treeEditor.grabHorizontal = true;
            treeEditor.setEditor(result, selectedItems[0]);
        }
        return result;
    }
    
    /**
     * Indicates if the text editor is currently active and not disposed.
     * 
     * @return <code>true</code>if active; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean isTextEditorActive() {
        return (this.textEditor != null);
    }

    /**
     * Close the text widget and reset the editorText field.
     */
    void disposeTextWidget() {
        if (textActionHandler != null) textActionHandler.removeText(textEditor);

        if (textEditorParent != null) {
            textEditorParent.dispose();
            textEditorParent = null;
            textEditor = null;
            treeEditor.setEditor(null, null);
        }
        
    }
    
    /**
     * Save the changes and dispose of the text widget.
     * 
     * @param resource - the resource to move.
     */
    void saveChangesAndDispose( Object obj ) {
        // Cache the resource to avoid selection loss since a selection of
        // another item can trigger this method
        final String newName = textEditor.getText();
        if( obj instanceof ColumnInfo) {
        	((ColumnInfo)obj).setName(newName);
        } else if( obj instanceof AttributeInfo) {
        	((AttributeInfo)obj).setAlias(newName);
        }
        
        Runnable query = new Runnable() {
            public void run() {
                // Dispose the text widget regardless
                disposeTextWidget();
                notifyColumnDataChanged();
            }
        };
        
        this.columnsViewer.getTree().getShell().getDisplay().asyncExec(query);
        
        
    }
}