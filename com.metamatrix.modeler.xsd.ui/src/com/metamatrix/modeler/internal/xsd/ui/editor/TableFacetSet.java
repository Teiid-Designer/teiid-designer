/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.internal.ui.actions.ClipboardActionHandler;
import com.metamatrix.modeler.internal.ui.actions.ClipboardActionsAdapter;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetEvent;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetMonitor;
import com.metamatrix.modeler.internal.ui.forms.DialogProvider;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.SystemClipboardUtilities;
import com.metamatrix.ui.internal.widget.AbstractTableLabelProvider;
import com.metamatrix.ui.internal.widget.DefaultContentProvider;

public class TableFacetSet extends AbstractFacetSet implements MultiFacetSet {

    private static final String COL_SEP = "\t"; //$NON-NLS-1$
    static final String LINE_SEP = System.getProperty("line.separator"); //$NON-NLS-1$
    static final String TRANSACTION_PASTE = GUIFacetHelper.getString("TableFacetSet.transactionPaste"); //$NON-NLS-1$
    private static final String TRANSACTION_DELETE = GUIFacetHelper.getString("TableFacetSet.transactionDelete"); //$NON-NLS-1$

    private final DialogProvider provider;
    Object transactionSource;
    TableViewer viewer;
    private List dataList;
    private MyListener myList;
    private ComponentSetMonitor mon;

    public TableFacetSet( String id,
                          String labelName,
                          DialogProvider dlp ) {
        super(id, labelName, false, false);
        provider = dlp;
    }

    public void setTransactionSource( Object transactionSource ) {
        this.transactionSource = transactionSource;
    }

    @Override
    protected void addMainControl( Composite parent,
                                   FormToolkit ftk,
                                   ComponentSetMonitor mon ) {
        this.mon = mon;
        // init:
        init();

        // set up viewer:
        viewer = new TableViewer(ftk.createTable(parent, SWT.MULTI | SWT.BORDER | SWT.FLAT | SWT.FULL_SELECTION));
        viewer.setContentProvider(new MyContentProvider());
        viewer.setLabelProvider(new MyTableLabelProvider());
        viewer.setSorter(new ViewerSorter());

        // set up table:
        Table table = viewer.getTable();
        table.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        // defect 18041 -- support copy/paste
        new ClipboardActionsAdapter(table, new MyClipActionHandler());

        // set up columns:
        TableColumn col = new TableColumn(table, SWT.LEFT);
        col.setText(LABEL_VALUE);
        col = new TableColumn(table, SWT.LEFT);
        col.setText(LABEL_DESCRIPTION);

        // pack the columns to get them to show up:
        packColumns();

        // hook it all together and add listener:
        viewer.setInput(dataList);
        viewer.addDoubleClickListener(myList);

        // button panel:
        Composite c = ftk.createComposite(parent);
        c.setLayout(new FillLayout(SWT.VERTICAL));
        // add:
        final Button add = ftk.createButton(c, LABEL_ADD, SWT.NONE);
        add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                addRowObject();
            }
        });
        // edit:
        final Button edit = ftk.createButton(c, provider.getLaunchButtonText(), SWT.NONE);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                edit.setEnabled(SelectionUtilities.isSingleSelection(event.getSelection()));// !event.getSelection().isEmpty());
            }
        });
        edit.setEnabled(false);
        edit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                editRowObject(SelectionUtilities.getSelectedObject(viewer.getSelection()));
            }
        });
        // remove:
        final Button remove = ftk.createButton(c, LABEL_REMOVE, SWT.NONE);
        remove.setEnabled(false);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                remove.setEnabled(!event.getSelection().isEmpty());
            }
        });
        remove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                removeSelectedRows();
            }
        });
    }

    @Override
    protected void setMainValue( Object value ) {
        if (value instanceof List) {
            init();

            // copy this list to our own:
            List l = (List)value;
            dataList.clear();
            dataList.addAll(l);

            // update gui, if present:
            if (viewer != null) {
                viewer.refresh();
                reflow();
            } // endif
        } else if (value == null) {
            clear();
        } // endif
    }

    public void addValue( FacetValue fv,
                          boolean reflow ) {
        init();

        dataList.add(fv);

        if (viewer != null) {
            viewer.add(fv);
            if (reflow) reflow();
        } // endif
    }

    public void reflow() {
        packColumns();
        getCategory().reflowForm();
    }

    public void clear() {
        init();

        dataList.clear();

        if (viewer != null) {
            viewer.refresh();
            reflow();
        } // endif
    }

    //
    // Overrides:
    //

    //
    // Utility methods:
    //
    private void init() {
        if (dataList == null) {
            dataList = new ArrayList();
            myList = new MyListener();
        } // endif
    }

    private void packColumns() {
        Table table = viewer.getTable();
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn col = table.getColumn(i);
            col.pack();
            if (col.getWidth() > 200) {
                col.setWidth(200);
            } // endif
        } // endfor
    }

    void addRowObject() {
        provider.showDialog(viewer.getTable().getShell(), new FacetValue());
        if (!provider.wasCancelled()) {
            FacetValue newVal = (FacetValue)provider.getValue();
            addValue(newVal, true);
            fireChange(newVal, false);
        } // endif
    }

    void editRowObject( Object selected ) {
        provider.showDialog(viewer.getTable().getShell(), selected);
        if (!provider.wasCancelled()) {
            viewer.refresh(provider.getValue());
            packColumns();
            fireChange((FacetValue)selected, false);
        } // endif
    }

    private void removeRowObjects( final List selected ) {
        dataList.removeAll(selected);
        viewer.remove(selected.toArray());
        reflow();

        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) {
                Iterator itor = selected.iterator();
                while (itor.hasNext()) {
                    FacetValue fv = (FacetValue)itor.next();
                    fv.value = null;
                    fireChange(fv, true);
                } // endwhile
                return null;
            }
        }; // endanon transaction

        try {
            ModelerCore.getModelEditor().executeAsTransaction(runnable, TRANSACTION_DELETE, true, true, transactionSource);
        } catch (ModelerCoreException mce) {
            ModelerXsdUiConstants.Util.log(mce);
        } // endtry

    }

    void fireChange( FacetValue newValue,
                     boolean delete ) {
        mon.update(new ComponentSetEvent(this, delete, newValue));
    }

    void removeSelectedRows() {
        removeRowObjects(SelectionUtilities.getSelectedObjects(viewer.getSelection()));
    }

    class MyListener implements IDoubleClickListener {
        public void doubleClick( DoubleClickEvent event ) {
            editRowObject(((IStructuredSelection)event.getSelection()).getFirstElement());
        }
    }

    static class MyContentProvider extends DefaultContentProvider {
        private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

        @Override
        public Object[] getElements( Object inputElement ) {
            if (inputElement instanceof List) {
                List l = (List)inputElement;
                if (l.size() > 0) {
                    return l.toArray();
                } // endif
            } // endif

            return EMPTY_OBJECT_ARRAY;
        }
    }

    static class MyTableLabelProvider extends AbstractTableLabelProvider implements ILabelProvider {
        private static final int VALUE_COLUMN = 0;
        private static final int DESC_COLUMN = 1;

        public String getColumnText( Object element,
                                     int columnIndex ) {
            if (element instanceof FacetValue) {
                FacetValue fv = (FacetValue)element;
                switch (columnIndex) {
                    case VALUE_COLUMN:
                        // defect 18304 - we were getting a classCastEx here on occasion:
                        return (fv.value != null) ? fv.value.toString() : null;

                    case DESC_COLUMN:
                        return fv.description;

                    default:
                        break;
                } // endswitch
            } // endif

            return null;
        }

        public Image getImage( Object element ) {
            return null;
        }

        public String getText( Object element ) {
            if (element instanceof FacetValue) {
                FacetValue fv = (FacetValue)element;
                return (fv.value != null) ? fv.value.toString() : fv.toString();
            } // endif

            return (element != null) ? element.toString() : null;
        }

    }

    class MyClipActionHandler implements ClipboardActionHandler {
        public void cut() {
            copy();
            delete();
        }

        public void copy() {
            StringBuffer toClip = new StringBuffer();
            // TODO much of this code should be placed in SystemClipboardUtils...
            List l = ((IStructuredSelection)viewer.getSelection()).toList();
            int size = l.size();
            for (int i = 0; i < size; i++) {
                FacetValue fv = (FacetValue)l.get(i);

                if (i > 0) {
                    // not first time, add CRLF:
                    toClip.append(LINE_SEP);
                } // endif

                toClip.append(fv.value).append(COL_SEP);

                if (fv.description != null && fv.description.length() > 0) {
                    toClip.append(fv.description);
                } // endif
            } // endfor

            SystemClipboardUtilities.setContents(toClip.toString());
        }

        public void paste() {
            final TransactionRunnable runnable = new TransactionRunnable() {
                public Object run( final UnitOfWork uow ) {
                    String contents = SystemClipboardUtilities.getContents();
                    if (contents != null) {
                        List toInsert = SystemClipboardUtilities.convertTableData(contents);

                        for (int i = 0; i < toInsert.size(); i++) {
                            List columns = (List)toInsert.get(i);
                            int size = columns.size();
                            // check format:
                            if (size != 1 && size != 2) {
                                ModelerXsdUiConstants.Util.log(ModelerXsdUiConstants.Util.getString("TableFacetSet.badRowFormat", columns.toString())); //$NON-NLS-1$
                                // don't add anything for this row.
                                continue;
                            } // endif

                            FacetValue fv = new FacetValue();
                            fv.value = columns.get(0);
                            // set description if available:
                            if (size == 2) {
                                fv.description = (String)columns.get(1);
                            } // endif

                            addValue(fv, false);
                            fireChange(fv, false);
                        } // endfor
                    } // endif
                    return null;
                }
            }; // endanon transaction

            try {
                ModelerCore.getModelEditor().executeAsTransaction(runnable, TRANSACTION_PASTE, true, true, transactionSource);
            } catch (ModelerCoreException mce) {
                ModelerXsdUiConstants.Util.log(mce);
            } // endtry

            reflow();
        }

        public void delete() {
            removeSelectedRows();
        }

        public void selectAll() {
            viewer.getTable().selectAll();
        }

        public void addSelectionChangedListener( ISelectionChangedListener listener ) {
            viewer.addSelectionChangedListener(listener);
        }
    } // endclass MyClipActionHandler

    // //
    // // Testing methods:
    // //
    // public static void main(String[] args) {
    // Display display = new Display();
    // Shell shell = new Shell(display);
    // shell.setText("Simple Datatypes Editor as a Form");
    // shell.setLayout(new FillLayout());
    //
    // TableFacetSet tfs = new TableFacetSet("testid", "labelname", new FacetHelper.NullDialogProvider("launch"));
    // tfs.addFormControls(shell, new FormToolkit(Display.getDefault()), 10);
    // LayoutDebugger.debugLayout(shell);
    //
    // shell.setBounds(100, 100, 650, 550);
    // shell.open();
    // while (!shell.isDisposed()) {
    // if (!display.readAndDispatch())
    // display.sleep();
    // }
    // display.dispose();
    // }
}
