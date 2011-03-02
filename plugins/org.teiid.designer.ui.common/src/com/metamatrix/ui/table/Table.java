/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @param <T>
 */
public class Table<T> {

    final TableViewer viewer;
    final List<TableViewerColumn> columns;

    /**
     * @param <V>
     * @param parent
     * @param tableProvider
     * @param columnProviders
     */
    public <V> Table( final Composite parent,
                      final TableProvider<T> tableProvider,
                      final ColumnProvider<T, V>... columnProviders ) {
        // Create table viewer
        viewer = new TableViewer(parent);
        viewer.setContentProvider(new IStructuredContentProvider() {

            @Override
            public void dispose() {
            }

            @Override
            public Object[] getElements( final Object inputElement ) {
                return tableProvider.getElements();
            }

            @Override
            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
            }
        });
        // Enable tooltips
        ColumnViewerToolTipSupport.enableFor(viewer);

        // Create table
        final org.eclipse.swt.widgets.Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Create table columns
        this.columns = new ArrayList<TableViewerColumn>();

        for (final ColumnProvider<T, V> columnProvider : columnProviders) {
            final TableViewerColumn viewerCol = new TableViewerColumn(viewer, columnProvider.getAlignment());
            this.columns.add(viewerCol);
            viewerCol.setLabelProvider(new ColumnLabelProvider() {

                @SuppressWarnings( "unchecked" )
                @Override
                public Image getImage( final Object element ) {
                    if (columnProvider.getAlignment() != SWT.LEFT) return null;
                    return columnProvider.getImage((T)element);
                }

                @SuppressWarnings( "unchecked" )
                @Override
                public String getText( final Object element ) {
                    return columnProvider.getText((T)element);
                }

                @SuppressWarnings( "unchecked" )
                @Override
                public String getToolTipText( final Object element ) {
                    return columnProvider.getToolTip((T)element);
                }
            });
            viewerCol.setEditingSupport(new EditingSupport(viewer) {

                @SuppressWarnings( "unchecked" )
                @Override
                protected boolean canEdit( final Object element ) {
                    return columnProvider.isEditable((T)element);
                }

                @Override
                protected CellEditor getCellEditor( final Object element ) {
                    try {
                        return columnProvider.getEditorClass().getConstructor(Composite.class).newInstance(table);
                    } catch (final Exception error) {
                        throw new RuntimeException(error);
                    }
                }

                @SuppressWarnings( "unchecked" )
                @Override
                protected Object getValue( final Object element ) {
                    return columnProvider.getValue((T)element);
                }

                @SuppressWarnings( "unchecked" )
                @Override
                protected void setValue( final Object element,
                                         final Object value ) {
                    columnProvider.setValue((T)element, (V)value);
                    viewer.update(element, null);
                }
            });
            final TableColumn col = viewerCol.getColumn();
            if( columnProvider.getName() != null ) {
            	col.setText(columnProvider.getName());
            } else if( columnProvider.getImage() != null ) {
            	col.setImage(columnProvider.getImage());
            }
            col.setMoveable(false);
            col.setResizable(columnProvider.isResizable());
        }
        // Create listener to control position of images within cells
        final Listener paintListener = new Listener() {
            @Override
            @SuppressWarnings( "unchecked" )
            public void handleEvent( final Event event ) {
                final ColumnProvider<T, V> columnProvider = columnProviders[event.index];
                final Image image = (columnProvider.getAlignment() == SWT.LEFT ? null : columnProvider.getImage((T)event.item.getData()));
                if (image == null) return;
                final int cellWidth = table.getColumn(event.index).getWidth();
                switch (event.type) {
                    case SWT.MeasureItem: {
                        final Rectangle rect = image.getBounds();
                        event.width = cellWidth;
                        event.height = Math.max(event.height, rect.height);
                        break;
                    }
                    case SWT.PaintItem: {
                        final Rectangle rect = image.getBounds();
                        final int xOffset = (cellWidth - rect.width) / 2;
                        final int yOffset = (event.height - rect.height) / 2;
                        event.gc.drawImage(image, event.x + xOffset, event.y + yOffset);
                        break;
                    }
                }
            }
        };
        table.addListener(SWT.MeasureItem, paintListener);
        table.addListener(SWT.PaintItem, paintListener);
        // TODO: Sorting options regarding column order, priority, direction, and initial state
        viewer.setSorter(new ViewerSorter() {

            private final Integer[] indexes;

            {
                final List<Integer> indexes = new ArrayList<Integer>();
                for (int ndx = 0, len = columnProviders.length; ndx < len; ++ndx)
                    if (columnProviders[ndx].isSortable()) indexes.add(ndx);
                this.indexes = indexes.toArray(new Integer[indexes.size()]);
            }

            @SuppressWarnings( "unchecked" )
            @Override
            public int compare( final Viewer viewer,
                                final Object element1,
                                final Object element2 ) {
                for (final Integer ndx : indexes) {
                    final int result = columnProviders[ndx].compare((T)element1, (T)element2);
                    if (result != 0) return result;
                }
                return 0;
            }
        });
    }
    
    /**
     * @param index the index of the column being requested
     * @return the column
     * @throws IndexOutOfBoundsException if index is not valid
     */
    public TableViewerColumn getColumn( int index ) {
        return this.columns.get(index);
    }

    /**
     * @return viewer
     */
    public TableViewer getViewer() {
        return viewer;
    }

    void packColumns() {
        for (final TableColumn col : viewer.getTable().getColumns()) {
            final String title = col.getText();
            if (col.getAlignment() == SWT.CENTER) col.setText("M" + title + "M"); //$NON-NLS-1$ //$NON-NLS-2$ 
            if (col.getAlignment() == SWT.RIGHT) col.setText("MM" + title); //$NON-NLS-1$
            col.setText(title + "MM"); //$NON-NLS-1$
            col.pack();
            col.setText(title);
        }
    }

    /**
     * @param input
     */
    public void setInput( final Object input ) {
        viewer.setInput(input);
        packColumns();
    }
}
