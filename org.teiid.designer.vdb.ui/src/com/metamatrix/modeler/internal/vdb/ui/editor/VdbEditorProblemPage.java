/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.util.ArrayList;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.validation.ProblemMarker;
import com.metamatrix.modeler.ui.editors.IRevertable;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.UiConstants.Images;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.AbstractTableLabelProvider;
import com.metamatrix.ui.table.TableViewerSorter;

/**
 * @since 4.0
 */
public class VdbEditorProblemPage extends EditorPart
    implements Images, VdbUiConstants, StringUtil.Constants, VdbEditor.Constants, IRevertable, IGotoMarker {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbEditorProblemPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final int SEVERITY_COLUMN = 0;
    private static final int MESSAGE_COLUMN = 1;
    private static final int OBJECT_COLUMN = 2;

    private static final String MESSAGE_HEADER = getString("messageHeader"); //$NON-NLS-1$
    private static final String OBJECT_HEADER = getString("objectHeader"); //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    VdbEditor editor;
    TableViewer viewer;

    /**
     * @since 4.0
     */
    public VdbEditorProblemPage( final VdbEditor editor ) {
        this.editor = editor;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    public void createPartControl( final Composite parent ) {
        this.viewer = WidgetFactory.createTableViewer(parent, SWT.FULL_SELECTION);
        final Table table = this.viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        WidgetFactory.createTableColumn(table);
        WidgetFactory.createTableColumn(table, MESSAGE_HEADER);
        WidgetFactory.createTableColumn(table, OBJECT_HEADER);
        this.viewer.setContentProvider(new IStructuredContentProvider() {
            public void dispose() {
            }

            public Object[] getElements( final Object inputElement ) {
                final Vdb vdb = VdbEditorProblemPage.this.editor.getVdb();
                final ArrayList problems = new ArrayList();
                for (VdbModelEntry entry : vdb.getModelEntries()) {
                    problems.addAll(entry.getErrors());
                    problems.addAll(entry.getWarnings());
                }
                return problems.toArray();
            }

            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
            }
        });
        this.viewer.setLabelProvider(new AbstractTableLabelProvider() {
            @Override
            public Image getColumnImage( final Object element,
                                         final int column ) {
                if (column == SEVERITY_COLUMN) {
                    return VdbEditor.getStatusImage(((ProblemMarker)element).getSeverity());
                }
                return null;
            }

            public String getColumnText( final Object element,
                                         final int column ) {
                final ProblemMarker marker = (ProblemMarker)element;
                switch (column) {
                    case MESSAGE_COLUMN: {
                        final String message = marker.getMessage();
                        return message == null ? EMPTY_STRING : message;
                    }
                    case OBJECT_COLUMN: {
                        final String target = marker.getTarget();
                        return target == null ? EMPTY_STRING : target;
                    }
                }
                return EMPTY_STRING;
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( final DoubleClickEvent event ) {
                // final ProblemMarker marker = (ProblemMarker)((IStructuredSelection)event.getSelection()).getFirstElement();
            }
        });
        this.viewer.setSorter(new TableViewerSorter(this.viewer, SEVERITY_COLUMN, TableViewerSorter.ASCENDING) {
            @Override
            protected int compareColumn( final TableViewer viewer,
                                         final Object object1,
                                         final Object object2,
                                         final int column ) {
                if (column == SEVERITY_COLUMN) {
                    return (((ProblemMarker)object2).getSeverity().getValue() - ((ProblemMarker)object1).getSeverity().getValue());
                }
                return super.compareColumn(viewer, object1, object2, column);
            }
        });
        this.viewer.setInput(this);
        WidgetUtil.pack(table);
    }

    public void doRevertToSaved() {
        if (editor.getVdb() != null) refreshViewer(); // already runs in asyncExec
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.0
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
        refreshViewer();
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     * @since 4.0
     */
    @Override
    public void doSaveAs() {
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     * @since 4.0
     */
    public void gotoMarker( final IMarker marker ) {
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     * @since 4.0
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) throws PartInitException {
        if (input != null && !(input instanceof IFileEditorInput)) {
            throw new PartInitException(INVALID_INPUT_MESSAGE);
        }
        setSite(site);
        setInput(input);
        setPartName(TITLE);
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     * @since 4.0
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     * @since 4.0
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Cause the table viewer to refresh itself from its content provider
     */
    public void refreshViewer() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (viewer != null && !viewer.getControl().isDisposed()) {
                    viewer.refresh();
                    WidgetUtil.pack(viewer);
                }
            }
        });
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 4.0
     */
    @Override
    public void setFocus() {
    }
}
