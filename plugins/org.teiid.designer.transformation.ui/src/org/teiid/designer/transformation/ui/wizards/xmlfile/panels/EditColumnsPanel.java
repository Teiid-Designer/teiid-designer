/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.xmlfile.panels;

import org.eclipse.jface.viewers.ColumnLabelProvider;
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
import org.eclipse.jface.viewers.TreeViewerColumn;
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
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.query.proc.ITeiidXmlColumnInfo;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlColumnInfo;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlImportXmlConfigurationPage;

/**
 * @since 8.0
 */
public class EditColumnsPanel {
	private final Image XSD_ELEMENT_ICON_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.SCHEMA_ELEMENT);
	private final Image XSD_ATTRIBUTE_ICON_IMG = UiPlugin.getDefault().getImage(UiConstants.Images.SCHEMA_ATTRIBUTE);

	final TeiidXmlImportXmlConfigurationPage configPage;
	TreeViewer columnsViewer;

	int type;
	TreeEditor treeEditor;
	Text textEditor;
	Composite textEditorParent;
	TextActionHandler textActionHandler;
	Object selectedObject;
	TreeItem[] cachedSelection;

	public EditColumnsPanel(Composite parent,
			TeiidXmlImportXmlConfigurationPage configPage) {
		super();
		this.configPage = configPage;
		createPanel(parent);
	}

	private void createPanel(Composite parent) {

		Tree table = new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayout(new TableLayout());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 80;
		table.setLayoutData(gd);

		this.columnsViewer = new TreeViewer(table);
		this.columnsViewer.getControl().setLayoutData(gd);
		TreeViewerColumn name = new TreeViewerColumn(this.columnsViewer,
				SWT.LEFT);
		name.setLabelProvider(new ColumnDataLabelProvider(0));
		name.getColumn().setWidth(200);
		name.getColumn().setText(Messages.Name);
		TreeViewerColumn columnInfo = new TreeViewerColumn(this.columnsViewer,
				SWT.LEFT);
		columnInfo.setLabelProvider(new ColumnDataLabelProvider(1));
		columnInfo.getColumn().setWidth(200);
		columnInfo.getColumn().setText(Messages.ColumnInfo);
		treeEditor = new TreeEditor(columnsViewer.getTree());

		ColumnDataTreeProvider provider = new ColumnDataTreeProvider();
		this.columnsViewer.setContentProvider(provider);
		this.columnsViewer.setAutoExpandLevel(3);
		this.columnsViewer.setInput(this.configPage.getFileInfo());

		this.columnsViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				renameInline();
			}
		});

		this.columnsViewer.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent theEvent) {
				if (theEvent.button == 1) {
					if (isTextEditorActive() && selectedObject != null) {
						saveChangesAndDispose(selectedObject);
					}
				}
			}
		});

	}

	public void refresh() {
		this.columnsViewer.setInput(this.configPage);
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

		IStructuredSelection selection = (IStructuredSelection) this.columnsViewer
				.getSelection();
		for (Object obj : selection.toArray()) {
			return obj;
		}

		return null;
	}

	public TeiidXmlColumnInfo getSelectedColumn() {
		IStructuredSelection selection = (IStructuredSelection) this.columnsViewer
				.getSelection();
		for (Object obj : selection.toArray()) {
			if (obj instanceof TeiidXmlColumnInfo) {
				return (TeiidXmlColumnInfo) obj;
			}
		}

		return null;
	}

	public int getSelectedIndex() {
		TreeItem[] selectedItems = columnsViewer.getTree().getSelection();
		if (selectedItems.length > 0) {
			int i = 0;
			for (TreeItem item : columnsViewer.getTree().getItems()) {
				if (selectedItems[0] == item) {
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
		 this.columnsViewer.refresh();
	}

	public void setEnabled(boolean enable) {
		columnsViewer.getTree().setEnabled(enable);
	}
	class ColumnDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public ColumnDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof TeiidXmlColumnInfo) {
				switch (this.columnNumber) {
				case 0: {
					return ((TeiidXmlColumnInfo) element).getName();
				}
				case 1: {
					StringBuilder buf = new StringBuilder();
					buf.append(((TeiidXmlColumnInfo) element).toString().substring(((TeiidXmlColumnInfo) element).toString().indexOf(", ")+2));
					if (((TeiidXmlColumnInfo) element).getOrdinality()){
						buf = new StringBuilder(buf.toString().replace("ordinal = true", "ordinal"));
					}else{
						buf = new StringBuilder(buf.toString().replace("ordinal = false", "ordinal"));
					}
					return buf.toString().replace("datatype", "type");
				}
				}
			}
			return StringUtilities.EMPTY_STRING;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			switch (this.columnNumber) {
			case 0: {
				return "Tooltip 1"; //getString("columnNameColumnTooltip"); //$NON-NLS-1$
			}
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
			}
			}
			return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if (this.columnNumber == 0) {
				return UiPlugin.getDefault().getImage(
						UiConstants.Images.COLUMN_ICON);
			} else if (this.columnNumber == 1) {
				if (element instanceof ITeiidXmlColumnInfo) {
					if (((ITeiidXmlColumnInfo) element).getOrdinality()) {
						return UiPlugin.getDefault().getImage(
								PluginConstants.Images.CHECKED_BOX_ICON);
					} else {
						return UiPlugin.getDefault().getImage(
								PluginConstants.Images.UNCHECKED_BOX_ICON);
					}
				}
				return null;
			}
			return null;
		}
	}
	class ColumnDataTreeProvider implements ITreeContentProvider,
			ILabelProvider {

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// NO OP
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof TeiidXmlImportXmlConfigurationPage) {
				return ((TeiidXmlImportXmlConfigurationPage) inputElement).getFileInfo().getColumnInfoList().toArray();
			}
			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object parentElement) {

		//	if (parentElement instanceof TeiidXmlColumnInfo) {
		//		//return ((TeiidXmlColumnInfo) parentElement);
		//		return new String[] {"someValue"};
		//	}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof TeiidXmlColumnInfo) {
				return true;
			}
			return false;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// NO OP
			return false;
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof TeiidXmlColumnInfo) {
				return XSD_ELEMENT_ICON_IMG;
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof TeiidXmlColumnInfo) {
				return ((TeiidXmlColumnInfo) element).getName();
			}
			return null;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// NO OP
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
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
		if (obj instanceof TeiidXmlColumnInfo) {
			name = ((TeiidXmlColumnInfo) obj).getName();
		}
		if (name != null) {
			textEditor.setText(name);
		}

		// Open text editor with initial size.
		textEditorParent.setVisible(true);
		Point textSize = textEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		textSize.x += textSize.y; // Add extra space for new characters.
		Point parentSize = textEditorParent.getSize();
		textEditor.setBounds(2, 1, Math.min(textSize.x, parentSize.x - 4),
				parentSize.y - 2);
		textEditorParent.redraw();
		textEditor.selectAll();
		textEditor.setFocus();
	}

	private void createTextEditor(final Object obj) {
		// Create text editor parent. This draws a nice bounding rect.
		textEditorParent = createEditorParent();
		textEditorParent.setVisible(false);
		textEditorParent.addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Point textSize = textEditor.getSize();
				Point parentSize = textEditorParent.getSize();
				e.gc.drawRectangle(0, 0,
						Math.min(textSize.x + 4, parentSize.x - 1),
						parentSize.y - 1);
			}
		});

		// Create inner text editor.
		textEditor = new Text(textEditorParent, SWT.NONE);
		textEditorParent.setBackground(textEditor.getBackground());
		textEditor.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Point textSize = textEditor.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				textSize.x += textSize.y; // Add extra space for new characters.
				Point parentSize = textEditorParent.getSize();
				textEditor.setBounds(2, 1,
						Math.min(textSize.x, parentSize.x - 4),
						parentSize.y - 2);
				textEditorParent.redraw();
			}
		});
		textEditor.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event event) {

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
			public void focusLost(FocusEvent fe) {
				// saveChangesAndDispose(obj);
			}
		});

		if (textActionHandler != null)
			textActionHandler.addText(textEditor);
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
		if (textActionHandler != null)
			textActionHandler.removeText(textEditor);

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
	 * @param resource
	 *            - the resource to move.
	 */
	void saveChangesAndDispose(Object obj) {
		// Cache the resource to avoid selection loss since a selection of
		// another item can trigger this method
		final String newName = textEditor.getText();
		if (obj instanceof TeiidXmlColumnInfo) {
			((TeiidXmlColumnInfo) obj).setName(newName);
		} 

		Runnable query = new Runnable() {
			@Override
			public void run() {
				// Dispose the text widget regardless
				disposeTextWidget();
				notifyColumnDataChanged();
			}
		};

		this.columnsViewer.getTree().getShell().getDisplay().asyncExec(query);

	}

}
