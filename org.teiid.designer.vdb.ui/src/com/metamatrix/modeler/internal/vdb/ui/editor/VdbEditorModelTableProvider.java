/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

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
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbEntry.SyncState;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelLabelProvider;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;

/**
 * VdbEditorModelTableProvider is the LabelProvider and ContentProvider for the TableViewer in VdbEditorModelComposite.
 * 
 * @since 4.2
 */
public class VdbEditorModelTableProvider extends ModelLabelProvider implements IStructuredContentProvider, ITableLabelProvider {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private static final String VISIBLE_COLUMN_NAME = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.visibleColumnName"); //$NON-NLS-1$;
    private static final String MODEL_COLUMN_NAME = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.modelColumnName"); //$NON-NLS-1$
    private static final String DATE_COLUMN_NAME = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.dateColumnName"); //$NON-NLS-1$
    private static final String STATUS_COLUMN_NAME = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.statusColumnName"); //$NON-NLS-1$
    private static final String STATUS_OK = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.statusOk"); //$NON-NLS-1$
    private static final String STATUS_STALE = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.statusStale"); //$NON-NLS-1$
    private static final String STATUS_UNKNOWN = VdbUiConstants.Util.getString("VdbEditorModelTableProvider.statusUnknown"); //$NON-NLS-1$
    private static final String[] COLUMN_HEADERS = new String[] {VISIBLE_COLUMN_NAME, MODEL_COLUMN_NAME, DATE_COLUMN_NAME,
        STATUS_COLUMN_NAME};

    private static final Image WARNING_ICON = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);

    private static final int COLUMN_VISIBILITY = 0;
    private static final int COLUMN_MODEL = 1;
    private static final int COLUMN_LAST_SYNC = 2;
    private static final int COLUMN_STATUS_IMAGE = 3;
    private static final int COLUMN_STATUS_TEXT = 4;

    private final VdbEditor editor;

    /**
     * @since 4.2
     */
    public VdbEditorModelTableProvider( final VdbEditor editor ) {
        super();
        this.editor = editor;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    @Override
    public void addListener( final ILabelProviderListener listener ) {
    }

    void buildTableColumns( final TableViewer tableViewer ) {
        final TableLayout layout = new TableLayout();
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

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
    }

    boolean existsInWorkspace( final VdbEntry entry ) {
        final IResource localFile = entry.findFile();
        return (localFile != null && localFile.exists());
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     * @since 4.2
     */
    public Image getColumnImage( final Object element,
                                 final int columnIndex ) {
        if (columnIndex == COLUMN_MODEL) return getImage(element);
        else if (columnIndex == COLUMN_STATUS_IMAGE) if (((VdbModelEntry)element).getSyncState() == SyncState.OutOfSync) return WARNING_ICON;
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     * @since 4.2
     */
    public String getColumnText( final Object element,
                                 final int columnIndex ) {
        String result = EMPTY_STRING;
        switch (columnIndex) {
            case COLUMN_VISIBILITY:
                break;
            case COLUMN_MODEL:
                result = getText(element);
                break;
            case COLUMN_STATUS_IMAGE:
                break;
            case COLUMN_STATUS_TEXT:
                if (existsInWorkspace(((VdbEntry)element))) {
                    if (((VdbModelEntry)element).getSyncState() == SyncState.OutOfSync) result = STATUS_STALE;
                    else result = STATUS_OK;
                } else result = STATUS_UNKNOWN;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    public Object[] getElements( final Object inputElement ) {
        return editor.getVdb().getModelEntries().toArray();
    }

    @Override
    public Image getImage( final Object element ) {
        if (element instanceof VdbEntry) {
            final String path = ((VdbEntry)element).getName().toString();
            final Path p = new Path(path);
            return super.getImage(ResourcesPlugin.getWorkspace().getRoot().getFile(p));
        }
        return super.getImage(element);
    }

    @Override
    public String getText( final Object element ) {
        if (element instanceof VdbEntry) {
            final String pathString = ((VdbEntry)element).getName().toString();
            final Path path = new Path(pathString);
            final String result = path.makeRelative().toString();
            return result;
        }
        return super.getText(element);
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     * @since 4.2
     */
    public void inputChanged( final Viewer viewer,
                              final Object oldInput,
                              final Object newInput ) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    @Override
    public boolean isLabelProperty( final Object element,
                                    final String property ) {
        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    @Override
    public void removeListener( final ILabelProviderListener listener ) {
    }

    public void resetColumnWidths( final TableViewer viewer ) {
        final Table table = viewer.getTable();
        final int colCount = table.getColumnCount();

        for (int i = 0; i < colCount; i++) {
            final TableColumn column = table.getColumn(i);

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
}
