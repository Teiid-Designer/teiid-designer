/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.ui.IFileEditorInput;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelLabelProvider;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.modeler.vdb.ui.util.VdbEditUtil;
import com.metamatrix.vdb.edit.manifest.ModelReference;


/**
 * VdbEditorModelTableProvider is the LabelProvider and ContentProvider for the TableViewer
 * in VdbEditorModelComposite.
 * @since 4.2
 */
public class VdbEditorModelTableProvider extends ModelLabelProvider
        implements IStructuredContentProvider, ITableLabelProvider {
    //
    // Class constants:
    //
    private static final String EMPTY_STRING = "";  //$NON-NLS-1$
    private static final String VISIBLE_COLUMN_NAME = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.visibleColumnName");  //$NON-NLS-1$;
    private static final String MODEL_COLUMN_NAME = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.modelColumnName");  //$NON-NLS-1$
    private static final String DATE_COLUMN_NAME = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.dateColumnName");  //$NON-NLS-1$
    private static final String STATUS_COLUMN_NAME = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.statusColumnName");  //$NON-NLS-1$
    private static final String STATUS_OK = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.statusOk");  //$NON-NLS-1$
    private static final String STATUS_STALE = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.statusStale");  //$NON-NLS-1$
    private static final String STATUS_UNKNOWN = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.statusUnknown");  //$NON-NLS-1$
    private static final String DATE_UNKNOWN = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.dateUnknown");  //$NON-NLS-1$
    private static final String[] COLUMN_HEADERS = new String[] { VISIBLE_COLUMN_NAME, MODEL_COLUMN_NAME, DATE_COLUMN_NAME, STATUS_COLUMN_NAME};

    private static final Image WARNING_ICON = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);

    private static final int COLUMN_VISIBILITY = 0;
    private static final int COLUMN_MODEL = 1;
    private static final int COLUMN_LAST_SYNC = 2;
    private static final int COLUMN_STATUS_IMAGE = 3;
    private static final int COLUMN_STATUS_TEXT = 4;

    /** Date formatter. */
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(VdbUiConstants.Util.getString("VdbEditorModelTableProvider.dateFormatPattern")); //$NON-NLS-1$

    //
    // Instance variables:
    //
    private VdbEditor editor;
    private IResource vdbResource;

    //
    // Constructors:
    //

    /**
     * @since 4.2
     */
    public VdbEditorModelTableProvider(VdbEditor editor) {
        super();
        this.editor = editor;
        this.vdbResource = ((IFileEditorInput) editor.getEditorInput()).getFile();
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    public Object[] getElements(Object inputElement) {
        return editor.getContext().getVirtualDatabase().getModels().toArray();
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void inputChanged(Viewer viewer,
                             Object oldInput,
                             Object newInput) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    @Override
    public void addListener(ILabelProviderListener listener) {
    }
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     * @since 4.2
     */
    public Image getColumnImage(Object element,
                                int columnIndex) {
        if ( columnIndex == COLUMN_MODEL ) {
            return getImage(element);
        } else if ( columnIndex == COLUMN_STATUS_IMAGE ) {
            if ( editor.getContext().isStale((ModelReference) element) ) {
                return WARNING_ICON;
            } // endif
        }
        return null;
    }
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     * @since 4.2
     */
    public String getColumnText(Object element,
                                int columnIndex) {
        String result = EMPTY_STRING;
        switch (columnIndex) {
            case COLUMN_VISIBILITY:
                break;
            case COLUMN_MODEL:
                result = getText(element);
                break;
            case COLUMN_LAST_SYNC:
                Date date = ((ModelReference) element).getTimeLastSynchronizedAsDate();
                if ( date == null ) {
                    result = DATE_UNKNOWN;
                } else {
                    result = DATE_FORMATTER.format(date);
                }
                break;
            case COLUMN_STATUS_IMAGE:
                break;
            case COLUMN_STATUS_TEXT:
                if ( existsInWorkspace(((ModelReference) element)) ) {
	                if ( editor.getContext().isStale((ModelReference) element) ) {
	                    result = STATUS_STALE;
	                } else {
	                    result = STATUS_OK;
	                }
                } else {
                    result = STATUS_UNKNOWN;
                }
                break;
            default:
                break;
        }
        return result;
    }
    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    @Override
    public boolean isLabelProperty(Object element,
                                   String property) {
        return false;
    }
    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    @Override
    public void removeListener(ILabelProviderListener listener) {
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof ModelReference) {
            String path = ((ModelReference)element).getModelLocation();
            Path p = new Path(path);
            return super.getImage(ResourcesPlugin.getWorkspace().getRoot().getFile(p));
        }
        return super.getImage(element);
    }

    @Override
    public String getText(final Object element) {
        if (element instanceof ModelReference) {
            String pathString = ((ModelReference)element).getModelLocation();
            Path path = new Path(pathString);
            String result = path.makeRelative().toString();
            return result;
        }
        return super.getText(element);
    }

    void buildTableColumns(TableViewer tableViewer) {
        TableLayout layout = new TableLayout();
        tableViewer.getTable().setLayout(layout);
        TableColumn column = new TableColumn(tableViewer.getTable(), SWT.NONE);
        column.setText(VISIBLE_COLUMN_NAME);
        column.setWidth(20);
        column.setResizable(false);

        column = new TableColumn(tableViewer.getTable(), SWT.NONE);
        column.setText(MODEL_COLUMN_NAME);
        column.setWidth(300);

        column = new TableColumn(tableViewer.getTable(), SWT.NONE);
        column.setText(DATE_COLUMN_NAME);
        column.setWidth(100);

        column = new TableColumn(tableViewer.getTable(), SWT.CENTER);
        column.setWidth(17);
        column.setResizable(false);

        column = new TableColumn(tableViewer.getTable(), SWT.NONE);
        column.setText(STATUS_COLUMN_NAME);
        column.setWidth(180);

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
                case COLUMN_VISIBILITY:
                    column.pack();
                    column.setWidth(column.getWidth() + 4);
                    break;
                case COLUMN_MODEL:
                    column.setWidth(200);
                    break;
                case COLUMN_LAST_SYNC:
                    column.setWidth(80);
                    break;
                case COLUMN_STATUS_IMAGE:
                    column.setWidth(17);
                    break;
                case COLUMN_STATUS_TEXT:
                    column.setWidth(80);
                    break;

                default:
                    break;
            } // endswitch
        } // endfor
    }

    boolean existsInWorkspace(final ModelReference reference) {
        IResource localFile = VdbEditUtil.getFile(reference, this.vdbResource.getProject());
        return (localFile != null && localFile.exists());
    }
}
