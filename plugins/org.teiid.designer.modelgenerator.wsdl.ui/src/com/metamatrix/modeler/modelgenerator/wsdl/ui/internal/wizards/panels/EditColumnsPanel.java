/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

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
import org.teiid.core.types.DataTypeManager;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.Messages;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.ColumnInfo;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.OperationsDetailsPage;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.ProcedureInfo;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.table.CheckBoxEditingSupport;
import com.metamatrix.ui.table.ComboBoxEditingSupport;

public class EditColumnsPanel {
	TableViewer columnsViewer;
	ProcedureInfo procedureInfo;
	int type;

	final OperationsDetailsPage detailsPage;

    private final int NAME_PROP = 0;
    private final int DEFAULT_VALUE_PROP = 1;
    private final int XML_PATH_PROP = 2;
	private static final String EMPTY = ""; //$NON-NLS-1$

	public EditColumnsPanel(Composite parent, int style, int type, OperationsDetailsPage detailsPage) {
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
    	Table table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
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
        column.getColumn().setText(Messages.Name + getSpaces(25));
        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, NAME_PROP));
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();
        
        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.Ordinality);
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        column.setEditingSupport(new OrdinalityEditingSupport(this.columnsViewer));
        column.getColumn().pack();

        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.DataType + getSpaces(2));
        column.setLabelProvider(new ColumnDataLabelProvider(2));
        column.setEditingSupport(new DatatypeComboEditingSupport(this.columnsViewer));
        column.getColumn().pack();
        
        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.DefaultValue + getSpaces(2)); 
        column.setLabelProvider(new ColumnDataLabelProvider(3));
        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, DEFAULT_VALUE_PROP));
        column.getColumn().pack();
        
        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.Path);
        column.setLabelProvider(new ColumnDataLabelProvider(4));
        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, XML_PATH_PROP));
        column.getColumn().pack();
	}

	public void refresh() {
		this.columnsViewer.getTable().removeAll();
		for (ColumnInfo row : procedureInfo.getColumnInfoList()) {
			this.columnsViewer.add(row);
		}
	}

	public void refresh(Object element) {
		this.columnsViewer.refresh(element);
	}

	public void addSelectionListener(ISelectionChangedListener listener) {
		this.columnsViewer.addSelectionChangedListener(listener);
	}

	public ColumnInfo getSelectedColumn() {

		IStructuredSelection selection = (IStructuredSelection) this.columnsViewer
				.getSelection();
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

    private String getSpaces(int nSpaces) {
    	StringBuffer sb = new StringBuffer(nSpaces);
    	for( int i=0; i<nSpaces; i++ ) {
    		sb.append(StringUtilities.SPACE);
    	}
    	return sb.toString();
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
				switch(this.columnNumber) {
					case 0: {
						if(element instanceof ColumnInfo) {
							return ((ColumnInfo)element).getName();
						}
					}
					case 1: {
						return EMPTY;
					}
					case 2: {
						if(element instanceof ColumnInfo) {
							return ((ColumnInfo)element).getDatatype();
						}
					}
					case 3: {
						if(element instanceof ColumnInfo) {
							return ((ColumnInfo)element).getDefaultValue();
						}
					}
					case 4: {
						if(element instanceof ColumnInfo) {
							return ((ColumnInfo)element).getRelativePath();
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
				return UiPlugin.getDefault().getImage(
						UiConstants.Images.COLUMN_ICON);
			}
			return null;
		}
	}

	class ColumnInfoTextEditingSupport extends EditingSupport {

		private TextCellEditor editor;
		private int type;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer
		 */
		public ColumnInfoTextEditingSupport(ColumnViewer viewer, int type) {
			super(viewer);
			this.type = type;
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
				switch (this.type) {
				case NAME_PROP: {
					return ((ColumnInfo) element).getName();
				}
				
				case DEFAULT_VALUE_PROP: {
					return ((ColumnInfo)element).getDefaultValue();
				}
				case XML_PATH_PROP: {
					return ((ColumnInfo) element).getRelativePath();
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
				switch (this.type) {
				case NAME_PROP: {
					String oldValue = ((ColumnInfo) element).getName();
					String newValue = (String) value;
					if (newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
						((ColumnInfo) element).setName(newValue);
						notifyColumnDataChanged();
					}
				} break;
				case DEFAULT_VALUE_PROP: {
					String oldValue = ((ColumnInfo)element).getDefaultValue();
					String newValue = (String)value;
					if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
						((ColumnInfo)element).setDefaultValue(newValue);
						procedureInfo.columnChanged((ColumnInfo)element);
						notifyColumnDataChanged();
					}
				} break;
				case XML_PATH_PROP: {
						String oldValue = ((ColumnInfo) element).getRelativePath();
						String newValue = (String) value;
						if (newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((ColumnInfo) element).setRelativePath(newValue);
							notifyColumnDataChanged();
						}
					} break;
				}

			}
		}

	}
	
    class OrdinalityEditingSupport extends CheckBoxEditingSupport {

		public OrdinalityEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setElementValue(Object element, Object newValue) {
			if( element instanceof ColumnInfo && newValue instanceof Boolean) {
				ColumnInfo info = (ColumnInfo)element;
				if(info.getOrdinality() ) {
					procedureInfo.setOrdinality(info, false);
					notifyColumnDataChanged();
				} else {
					procedureInfo.setOrdinality(info, true);
					notifyColumnDataChanged();
				}
			}
		}
    }
    
    class DatatypeComboEditingSupport extends ComboBoxEditingSupport {
    	
    	private String[] datatypes;
        /**
         * @param viewer
         */
        public DatatypeComboEditingSupport( ColumnViewer viewer ) {
            super(viewer);
    		Set<String> unsortedDatatypes = DataTypeManager.getAllDataTypeNames();
    		Collection<String> dTypes = new ArrayList<String>();
    		
    		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
    		Arrays.sort(sortedStrings);
    		for( String dType : sortedStrings ) {
    			dTypes.add(dType);
    		}
    		
    		datatypes = dTypes.toArray(new String[dTypes.size()]);
    		
        }


        @Override
        protected String getElementValue( Object element ) {
        	return ((ColumnInfo)element).getDatatype();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
        	if( !((ColumnInfo)element).getOrdinality() ) {
	            ((ColumnInfo)element).setDatatype(newValue);
	            notifyColumnDataChanged();
        	}
        }
    }
}