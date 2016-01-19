/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.ui.common.table.ComboBoxEditingSupport;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbImportVdbEntry;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

/**
 * @since 8.0
 *
 */
public class ShowImportVdbsDialog extends Dialog {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ShowImportVdbsDialog.class);
    private static final String TITLE =getString("title"); //$NON-NLS-1$
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    static String[] TRUE_FALSE_ARRAY = {"true", "false"}; //$NON-NLS-1$ //$NON-NLS-2$
    
	private static String getString(final String id) {
		return VdbUiConstants.Util.getString(I18N_PREFIX + id);
	}
    private Vdb vdb;
    
    TableViewer importVdbViewer;
    /**
     * 
     * @param parent
     * @param vdb the VDB
     */
    public ShowImportVdbsDialog( Shell parent, Vdb vdb) {
        super(parent, TITLE);
        this.vdb = vdb;
    }
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
    	Composite mainPanel = (Composite)super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        mainPanel.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        
    	Group theGroup = WidgetFactory.createGroup(mainPanel, getString("vdbNames"), SWT.NONE, 1, 4); //$NON-NLS-1$
    	GridData groupGD = new GridData(GridData.FILL_BOTH);
    	groupGD.heightHint = 160;
    	groupGD.widthHint = 400;
    	theGroup.setLayoutData(groupGD);
    	
    	this.importVdbViewer = new TableViewer(theGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

    	
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        this.importVdbViewer.getControl().setLayoutData(data);
        
        int visibleTableRows = 4;
        Table table = this.importVdbViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData)table.getLayoutData()).horizontalSpan = 2;
        ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * visibleTableRows;

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.importVdbViewer, SWT.LEFT);
        column.getColumn().setText(getString("name")); //$NON-NLS-1$
        column.setLabelProvider(new ImportVdbLabelProvider(0));
        column.getColumn().setWidth(200);

        column = new TableViewerColumn(this.importVdbViewer, SWT.LEFT);
        column.getColumn().setText(getString("version")); //$NON-NLS-1$
        column.setLabelProvider(new ImportVdbLabelProvider(1));
        column.setEditingSupport(new VdbVersionEditingSupport(importVdbViewer));
        column.getColumn().setWidth(60);
        
        column = new TableViewerColumn(this.importVdbViewer, SWT.LEFT);
        column.getColumn().setText(getString("importDataPolicies")); //$NON-NLS-1$
        column.setLabelProvider(new ImportVdbLabelProvider(2));
        column.setEditingSupport(new ImportDataPoliciesEditingSupport(importVdbViewer));
        column.getColumn().setWidth(60);

        
        if( this.vdb != null ) {
	        for( VdbImportVdbEntry row : this.vdb.getImports() ) {
	        	if( row != null ) {
	        		this.importVdbViewer.add(row);
	        	}
	        }
        }
        
        return mainPanel;
    }
    
    class ImportVdbLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public ImportVdbLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof VdbImportVdbEntry ) {
				switch (this.columnNumber) {
					case 0: {
						return ((VdbImportVdbEntry)element).getName();
					}
					case 1: {
						return Integer.toString(((VdbImportVdbEntry)element).getVersion());
					}
					case 2: {
						return Boolean.toString(((VdbImportVdbEntry)element).isImportDataPolicies());
					}
				}
			}
			return EMPTY_STRING;
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
			if( element instanceof VdbImportVdbEntry) {
				switch (this.columnNumber) {
					case 0: {
						return VdbUiPlugin.singleton.getImage(VdbUiConstants.Images.VDB_ICON);
					}
					case 1: {
						return null;
					}
				}
				return null;
			}
			return null;
		}
		
		
	}
    
    class VdbVersionEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer the column viewer
		 */
		public VdbVersionEditingSupport(ColumnViewer viewer) {
			super(viewer);
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
			if( element instanceof VdbImportVdbEntry ) {
				return Integer.toString(((VdbImportVdbEntry)element).getVersion());
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			if( element instanceof VdbImportVdbEntry ) {
				int oldValue = ((VdbImportVdbEntry)element).getVersion();
				int newValue = oldValue;
				try {
					newValue = Integer.parseInt((String)value);
				} catch (NumberFormatException ex) {
					return;
				}
				if( newValue != oldValue ) {
					((VdbImportVdbEntry)element).setVersion(newValue);
					importVdbViewer.refresh(element);
				}
			}
		}

	}
    
    class ImportDataPoliciesEditingSupport extends ComboBoxEditingSupport {
    	
    	public ImportDataPoliciesEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}


        @Override
        protected String getElementValue( Object element ) {
        	return Boolean.toString(((VdbImportVdbEntry)element).isImportDataPolicies());
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return TRUE_FALSE_ARRAY;
        }

		@Override
		protected void setElementValue(Object element, String newValue) {

			if( element instanceof VdbImportVdbEntry) {
				VdbImportVdbEntry info = (VdbImportVdbEntry)element;
				if(info.isImportDataPolicies() ) {
					info.setImportDataPolicies(false);
					importVdbViewer.refresh(element);
				} else {
					info.setImportDataPolicies(true);
					importVdbViewer.refresh(element);
				}
			}
		}
    }
}
