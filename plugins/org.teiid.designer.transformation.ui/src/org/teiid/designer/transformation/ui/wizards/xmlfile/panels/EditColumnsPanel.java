/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.xmlfile.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.proc.ITeiidXmlColumnInfo;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlColumnInfo;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlFileInfo;
import org.teiid.designer.transformation.ui.wizards.xmlfile.TeiidXmlImportXmlConfigurationPage;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.ui.common.table.CheckBoxEditingSupport;
import org.teiid.designer.ui.common.table.ComboBoxEditingSupport;
import org.teiid.designer.ui.common.table.TableViewerBuilder;


/**
 * @since 8.0
 */
public class EditColumnsPanel {
    private final String EMPTY = ""; //$NON-NLS-1$
    private final int NAME_PROP = 0;
    private final int DEFAULT_VALUE_PROP = 1;
    private final int XML_PATH_PROP = 2;
    
	
	TableViewerBuilder columnsViewer;
	final TeiidXmlImportXmlConfigurationPage configPage;
	
	public EditColumnsPanel(Composite parent, TeiidXmlImportXmlConfigurationPage configPage) {
		super();
		this.configPage = configPage;
		createPanel(parent);
	}
	
	private void createPanel(Composite parent) {
        this.columnsViewer = new TableViewerBuilder(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 80).applyTo(this.columnsViewer.getControl());
        
        // create columns
        TableViewerColumn column = columnsViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(Messages.ColumnName + getSpaces(25));
        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer.getTableViewer(), NAME_PROP));
        column.setLabelProvider(new ColumnDataLabelProvider(0));

        
        column = columnsViewer.createColumn(SWT.LEFT, 10, 30, true);
        column.getColumn().setText(Messages.ForOrdinality);
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        column.setEditingSupport(new OrdinalityEditingSupport(this.columnsViewer.getTableViewer()));


        column = columnsViewer.createColumn(SWT.LEFT, 20, 30, true);
        column.getColumn().setText(Messages.dataTypeLabel + getSpaces(2));
        column.setLabelProvider(new ColumnDataLabelProvider(2));
        column.setEditingSupport(new DatatypeComboEditingSupport(this.columnsViewer.getTableViewer()));

        
        column = columnsViewer.createColumn(SWT.LEFT, 20, 30, true);
        column.getColumn().setText(Messages.DefaultValue + getSpaces(2));
        column.setLabelProvider(new ColumnDataLabelProvider(3));
        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer.getTableViewer(), DEFAULT_VALUE_PROP));

        
        column = columnsViewer.createColumn(SWT.LEFT, 20, 30, true);
        column.getColumn().setText(Messages.Path);
        column.setLabelProvider(new ColumnDataLabelProvider(4));
        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer.getTableViewer(), XML_PATH_PROP));

        
        if( getFileInfo() != null ) {
	        for( ITeiidXmlColumnInfo row : getFileInfo().getColumnInfoList() ) {
	        	this.columnsViewer.add(row);
	        }
        }
	}
	
    private String getSpaces(int nSpaces) {
    	StringBuffer sb = new StringBuffer(nSpaces);
    	for( int i=0; i<nSpaces; i++ ) {
    		sb.append(StringUtilities.SPACE);
    	}
    	return sb.toString();
    }

	private TeiidXmlFileInfo getFileInfo() {
		return this.configPage.getFileInfo();
	}
    
	public void refresh() {
    	this.columnsViewer.getTable().removeAll();
        for( ITeiidXmlColumnInfo row : getFileInfo().getColumnInfoList() ) {
        	this.columnsViewer.add(row);
        }
	}
	
	public void refresh(Object element) {
		this.columnsViewer.refresh(element);
	}
	
	public void addSelectionListener(ISelectionChangedListener listener) {
		this.columnsViewer.addSelectionChangedListener(listener);
	}
	
	public TeiidXmlColumnInfo getSelectedColumn() {
		
		IStructuredSelection selection = (IStructuredSelection)this.columnsViewer.getSelection();
		for( Object obj : selection.toArray()) {
			if( obj instanceof ITeiidXmlColumnInfo ) {
				return (TeiidXmlColumnInfo) obj;
			}
		}
		
		return null;
	}
	
	public int getSelectedIndex() {
		return columnsViewer.getTable().getSelectionIndex();
	}
	
	public void selectRow(int index) {
		if( index > -1 ) {
			columnsViewer.getTable().select(index);
		} else {
			columnsViewer.setSelection(new StructuredSelection());
		}
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
			if( element instanceof ITeiidXmlColumnInfo ) {
				switch (this.columnNumber) {
					case 0: {
						return ((ITeiidXmlColumnInfo)element).getName();
					}
					case 1: {
						return EMPTY;
					}
					case 2: {
						return ((ITeiidXmlColumnInfo)element).getDatatype();
					}
					case 3: {
						return ((ITeiidXmlColumnInfo)element).getDefaultValue();
					}
					case 4: {
						return ((ITeiidXmlColumnInfo)element).getRelativePath();
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
			if( this.columnNumber == 0 ) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
			} else if( this.columnNumber == 1 ) {
				if(element instanceof ITeiidXmlColumnInfo) {
					if( ((ITeiidXmlColumnInfo)element).getOrdinality() ) {
						return UiPlugin.getDefault().getImage(PluginConstants.Images.CHECKED_BOX_ICON);
					} else {
						return UiPlugin.getDefault().getImage(PluginConstants.Images.UNCHECKED_BOX_ICON);
					}
				}
				return null;
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
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override
		protected Object getValue(Object element) {
			if( element instanceof ITeiidXmlColumnInfo ) {
				switch(this.type) {
					case NAME_PROP: {
						return ((ITeiidXmlColumnInfo)element).getName();
					}
					case DEFAULT_VALUE_PROP: {
						return ((ITeiidXmlColumnInfo)element).getDefaultValue();
					}
					case XML_PATH_PROP: {
						return ((ITeiidXmlColumnInfo)element).getRelativePath();
					}
				}
			}
			return EMPTY;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			if( element instanceof ITeiidXmlColumnInfo ) {
				switch(this.type) {
					case NAME_PROP: {
						String oldValue = ((ITeiidXmlColumnInfo)element).getName();
						String newValue = (String)value;
						if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((TeiidXmlColumnInfo)element).setName(newValue);
							refresh(element);
					    	getFileInfo().columnChanged((ITeiidXmlColumnInfo)element);
							configPage.handleInfoChanged(false);
						}
					} break;
					case DEFAULT_VALUE_PROP: {
						String oldValue = ((ITeiidXmlColumnInfo)element).getDefaultValue();
						String newValue = (String)value;
						if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((TeiidXmlColumnInfo)element).setDefaultValue(newValue);
							refresh(element);
							getFileInfo().columnChanged((ITeiidXmlColumnInfo)element);
							configPage.handleInfoChanged(false);
						}
					} break;
					case XML_PATH_PROP: {
						String oldValue = ((ITeiidXmlColumnInfo)element).getRelativePath();
						String newValue = (String)value;
						if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((TeiidXmlColumnInfo)element).setRelativePath(newValue);
							refresh(element);
							getFileInfo().columnChanged((ITeiidXmlColumnInfo)element);
							configPage.handleInfoChanged(false);
						}
					} break;
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
            IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
    		Set<String> unsortedDatatypes = service.getAllDataTypeNames();
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
        	return ((ITeiidXmlColumnInfo)element).getDatatype();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
        	if( !((ITeiidXmlColumnInfo)element).getOrdinality() ) {
	            ((TeiidXmlColumnInfo)element).setDatatype(newValue);
	            configPage.handleInfoChanged(false);
        	}
        }
    }
	class OrdinalityEditingSupport extends CheckBoxEditingSupport {

		public OrdinalityEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setElementValue(Object element, Object newValue) {
			if (element instanceof ITeiidXmlColumnInfo
					&& newValue instanceof Boolean) {
				TeiidXmlColumnInfo info = (TeiidXmlColumnInfo) element;
				if (info.getOrdinality()) {
					getFileInfo().setOrdinality(info, false);
					configPage.handleInfoChanged(false);
				} else {
					getFileInfo().setOrdinality(info, true);
					configPage.handleInfoChanged(false);
				}
			}
		}

	}
    
}
