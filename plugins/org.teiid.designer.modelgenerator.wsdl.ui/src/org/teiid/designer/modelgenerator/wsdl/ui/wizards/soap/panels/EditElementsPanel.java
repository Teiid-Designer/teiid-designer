/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ColumnInfo;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ProcedureInfo;

import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;

public class EditElementsPanel {
	TableViewer columnsViewer;
	ProcedureInfo procedureInfo;
	int type;

	final OperationsDetailsPage detailsPage;

	private static final int NAME_PROP = 0;
	private static final String EMPTY = ""; //$NON-NLS-1$

	public EditElementsPanel(Composite parent, int style, int type, OperationsDetailsPage detailsPage) {
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
		refresh();
	}

	private void createPanel(Composite parent) {
		Table table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayout(new TableLayout());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 80;
		table.setLayoutData(gd);

		this.columnsViewer = new TableViewer(table);
		this.columnsViewer.getControl().setLayoutData(gd);

		// create columns
		TableViewerColumn column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
		column.getColumn().setText(Messages.Name + "                         "); //$NON-NLS-1$
		column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, NAME_PROP));
		column.setLabelProvider(new ColumnDataLabelProvider(0));
		column.getColumn().pack();
	}

	public void refresh() {
		this.columnsViewer.getTable().removeAll();
		if( this.type == ProcedureInfo.TYPE_BODY ) {
    		for (ColumnInfo row : procedureInfo.getBodyColumnInfoList()) {
    			this.columnsViewer.add(row);
    		}
		} else {
    		for (ColumnInfo row : procedureInfo.getHeaderColumnInfoList()) {
    			this.columnsViewer.add(row);
    		}
		}
	}

	public void refresh(Object element) {
		this.columnsViewer.refresh(element);
	}

	public void addSelectionListener(ISelectionChangedListener listener) {
		this.columnsViewer.addSelectionChangedListener(listener);
	}

	public ColumnInfo getSelectedColumn() {

		IStructuredSelection selection = (IStructuredSelection) this.columnsViewer.getSelection();
		for (Object obj : selection.toArray()) {
			if (obj instanceof ColumnInfo) {
				return (ColumnInfo) obj;
			}
		}

		return null;
	}

	public int getSelectedIndex() {
		return columnsViewer.getTable().getSelectionIndex();
	}

	public void selectRow(int index) {
		if (index > -1) {
			columnsViewer.getTable().select(index);
		} else {
			columnsViewer.setSelection(new StructuredSelection());
		}
	}

	private void notifyColumnDataChanged() {
		this.detailsPage.notifyColumnDataChanged();
	}
	
    public void setEnabled(boolean enable) {
    	columnsViewer.getTable().setEnabled(enable);
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
			if (element instanceof ColumnInfo) {
				switch (this.columnNumber) {
				case 0: {
					if (element instanceof ColumnInfo) {
						return ((ColumnInfo) element).getName();
					}
				}
				}
			}
			return EMPTY;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if (this.columnNumber == 0) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
			}
			return null;
		}
	}

	class ColumnInfoTextEditingSupport extends EditingSupport {

		private TextCellEditor editor;
		private int columnNumber;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer
		 */
		public ColumnInfoTextEditingSupport(ColumnViewer viewer, int type) {
			super(viewer);
			this.columnNumber = type;
			this.editor = new TextCellEditor((Composite) viewer.getControl());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.
		 * Object)
		 */
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		protected Object getValue(Object element) {
			if (element instanceof ColumnInfo) {
				switch (this.columnNumber) {
				case NAME_PROP: {
					return ((ColumnInfo) element).getName();
				}
				}
			}
			return EMPTY;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 * java.lang.Object)
		 */
		protected void setValue(Object element, Object value) {
			if (element instanceof ColumnInfo) {
				switch (this.columnNumber) {
				case NAME_PROP: {
					String oldValue = ((ColumnInfo) element).getName();
					String newValue = (String) value;
					if (newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
						((ColumnInfo) element).setName(newValue);
						notifyColumnDataChanged();
					}
				}
					break;
				}

			}
		}

	}
}