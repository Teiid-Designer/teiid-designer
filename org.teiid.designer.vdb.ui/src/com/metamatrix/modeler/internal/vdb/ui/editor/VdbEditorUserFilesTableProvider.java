/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.io.File;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelLabelProvider;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;


/** 
 * VdbEditorUserFilesTableProvider is the LabelProvider and ContentProvider for the TableViewer
 * in VdbEditorUserFilesComposite.
 * @since 5.3.3
 */
public class VdbEditorUserFilesTableProvider extends ModelLabelProvider 
        implements IStructuredContentProvider, ITableLabelProvider {
    //
    // Class constants:
    //
    private static final String EMPTY_STRING = "";  //$NON-NLS-1$
    private static final String FILE_COLUMN_NAME = VdbUiConstants.Util.getString("VdbEditorUserFilesTableProvider.fileColumnName");  //$NON-NLS-1$
    private static final String[] COLUMN_HEADERS = new String[] {FILE_COLUMN_NAME};

    private static final int COLUMN_FILE = 0;
    
    //
    // Instance variables:
    //
    private VdbEditor editor;
    private ISharedImages imgs;
    //
    // Constructors:
    //

    /** 
     * @since 5.3.3
     */
    public VdbEditorUserFilesTableProvider(VdbEditor editor) {
        super();
        this.editor = editor;
        final IWorkbench workbench = PlatformUI.getWorkbench();
        imgs = workbench.getSharedImages();
    }

    /** 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 5.3.3
     */
    public Object[] getElements(Object inputElement) {
        return editor.getContext().getUserFileNames().toArray();
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @since 5.3.3
     */
    @Override
    public void dispose() {
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 5.3.3
     */
    public void inputChanged(Viewer viewer,
                             Object oldInput,
                             Object newInput) {
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 5.3.3
     */
    @Override
    public void addListener(ILabelProviderListener listener) {
    }
    /** 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     * @since 5.3.3
     */
    public Image getColumnImage(Object element,
                                int columnIndex) {
        if ( columnIndex == COLUMN_FILE ) {
            return imgs.getImage(ISharedImages.IMG_OBJ_FILE);
        } 
        return null;
    }
    /** 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     * @since 5.3.3
     */
    public String getColumnText(Object element,
                                int columnIndex) {
        String result = EMPTY_STRING;
        switch (columnIndex) {
            case COLUMN_FILE:
                result = getText(element);
                break;
            default:
                break;
        }
        return result;
    }
    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     * @since 5.3.3
     */
    @Override
    public boolean isLabelProperty(Object element,
                                   String property) {
        return false;
    }
    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 5.3.3
     */
    @Override
    public void removeListener(ILabelProviderListener listener) {
    }
    
    @Override
    public Image getImage(Object element) {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final ISharedImages imgs = workbench.getSharedImages();
        if (element instanceof File) {
        	return imgs.getImage(ISharedImages.IMG_OBJ_FILE);
        }
        return super.getImage(element);
    }

    @Override
    public String getText(final Object element) {
        if (element instanceof File) {
            String name = ((File)element).getName();
            return name;
        }
        return super.getText(element);
    }
    
    void buildTableColumns(TableViewer tableViewer) {
        TableLayout layout = new TableLayout();
        tableViewer.getTable().setLayout(layout);
        
        TableColumn column = new TableColumn(tableViewer.getTable(), SWT.NONE);
        column.setText(FILE_COLUMN_NAME); 
        column.setWidth(300);
        
		layout.addColumnData(new ColumnPixelData(50, false));
		layout.addColumnData(new ColumnWeightData(200));
		layout.addColumnData(new ColumnWeightData(80));
        layout.addColumnData(new ColumnPixelData(17, false));
        layout.addColumnData(new ColumnWeightData(80));
        
        tableViewer.setColumnProperties(COLUMN_HEADERS);
    }
    
    public void resetColumnWidths(TableViewer viewer) {
        Table table = viewer.getTable();
        int colCount = table.getColumnCount();

        for (int i = 0; i < colCount; i++) {
            TableColumn column = table.getColumn(i);

            switch (i) {
                case COLUMN_FILE:
                    column.setWidth(400);
                    break;
                default:
                    break;
            } // endswitch
        } // endfor
    }
    
}
