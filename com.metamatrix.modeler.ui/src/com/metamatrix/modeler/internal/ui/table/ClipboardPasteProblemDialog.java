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

package com.metamatrix.modeler.internal.ui.table;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;

/**
 * ClipboardPasteProblemDialog
 */
public class ClipboardPasteProblemDialog extends ExtendedTitleAreaDialog
    implements UiConstants, com.metamatrix.ui.UiConstants.Images, PluginConstants.Images {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ClipboardPasteProblemDialog.class);

    private static final String[] MESSAGE_TBL_HDRS = new String[] {StringUtil.Constants.EMPTY_STRING,
        Util.getString(PREFIX + "pasteColumnHdr"), //$NON-NLS-1$
        Util.getString(PREFIX + "pasteDataHdr"), //$NON-NLS-1$
        Util.getString(PREFIX + "descriptionHdr")}; //$NON-NLS-1$

    private static final int ICON_INDEX = 0;

    private static final int COLUMN_INDEX = 1;

    private static final int DATA_INDEX = 2;

    private static final int DESCRIPTION_INDEX = 3;

    private static final Image BLANK_IMAGE;

    private static final Image ERROR_IMAGE;

    private static final Image INFO_IMAGE;

    private static final Image WARNING_IMAGE;

    public static final String TRUNCATED_COLUMN_TITLE = Util.getString(PREFIX + "truncatedColumnHdr"); //$NON-NLS-1$

    static {
        BLANK_IMAGE = UiPlugin.getDefault().getImage(BLANK_ICON);
        ERROR_IMAGE = com.metamatrix.ui.UiPlugin.getDefault().getImage(TASK_ERROR);
        WARNING_IMAGE = com.metamatrix.ui.UiPlugin.getDefault().getImage(TASK_WARNING);
        INFO_IMAGE = com.metamatrix.ui.UiPlugin.getDefault().getImage(TASK_INFO);
    }

    private CLabel lblMessagesTitle;

    private SashForm sash;

    private TableEditor dataTableEditor;

    TableViewer dataViewer;

    private DataViewerContentProvider dataViewerContentProvider;

    // private String dialogTitle;

    TableViewer messageViewer;

    private MessageViewerContentProvider messageViewerContentProvider;

    int selectedColumn; // selected column index in data table

    List statusRecords;

    ITablePasteValidator validator;

    private int errorCount;

    private int infoCount;

    private int validCount;

    private int warningCount;

    private int truncatedColumnCount;

    private int truncatedRowCount;

    public ClipboardPasteProblemDialog( Shell theParent,
                                        List theRecords ) {
        super(theParent, UiPlugin.getDefault());
        statusRecords = theRecords;
    }

    public ClipboardPasteProblemDialog( Shell theParent,
                                        List theRecords,
                                        ITablePasteValidator theValidator ) {
        super(theParent, UiPlugin.getDefault());
        statusRecords = theRecords;
        validator = theValidator;
    }

    /**
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        super.create();

        //
        // Data Table setup
        //

        // create columns using first row
        Table table = dataViewer.getTable();
        List firstRow = (List)statusRecords.get(0);

        for (int size = firstRow.size(), i = 0; i < size; i++) {
            TableColumn column = new TableColumn(table, SWT.LEFT);
            ClipboardPasteStatusRecord record = (ClipboardPasteStatusRecord)firstRow.get(i);
            column.setText(record.getColumnName());
        }

        // populate data table
        dataViewer.setInput(statusRecords);

        // pack data table columns
        TableColumn[] columns = dataViewer.getTable().getColumns();
        for (int i = 0; i < columns.length; ++i) {
            columns[i].pack();
        }

        //
        // Message Table setup
        //

        // populate message table
        messageViewer.setInput(statusRecords);

        // pack message table columns
        columns = messageViewer.getTable().getColumns();
        for (int i = 0; i < columns.length; ++i) {
            columns[i].pack();
        }

        // now that table is populated set the sash weights
        sash.setWeights(new int[] {60, 40});

        // set window message and message table view form title
        setDynamicMessages();

        // get the OK button to enable correctly when first displayed.
        setOkEnabledState(true);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite theParent ) {
        Composite pnlContents = (Composite)super.createDialogArea(theParent);
        pnlContents.setLayout(new GridLayout());

        sash = new SashForm(pnlContents, SWT.VERTICAL);
        sash.setLayoutData(new GridData(GridData.FILL_BOTH));

        createPasteDataArea(sash);
        createMessageArea(sash);

        setDialogTitle(Util.getString(PREFIX + "windowTitle")); //$NON-NLS-1$
        setTitle(Util.getString(PREFIX + "title")); //$NON-NLS-1$;
        // setTitleImage(UiPlugin.getDefault().getImage(TABLE_PASTE_WINDOW_ICON));

        return pnlContents;
    }

    private Composite createMessageArea( Composite theParent ) {
        ViewForm viewForm = new ViewForm(theParent, SWT.BORDER);
        lblMessagesTitle = WidgetFactory.createLabel(viewForm);
        viewForm.setTopLeft(lblMessagesTitle);

        // this composite is needed in order to size the table correctly.
        // the viewform doesn't look at it's content's layout data.
        Composite pnlContents = new Composite(viewForm, SWT.NONE) {
            // need to override and set height based on a minimum of 5 visible rows in the table.
            @Override
            public Point computeSize( int wHint,
                                      int hHint,
                                      boolean changed ) {
                Table table = (Table)getChildren()[0];
                return super.computeSize(wHint, table.getItemHeight() * 5 + table.getHeaderHeight(), changed);
            }
        };
        final GridLayout layout = new GridLayout();
        layout.marginWidth = layout.marginHeight = 0;
        pnlContents.setLayout(layout);
        pnlContents.setLayoutData(new GridData(GridData.FILL_BOTH));
        viewForm.setContent(pnlContents);

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.READ_ONLY;
        messageViewer = new TableViewer(pnlContents, style);
        messageViewerContentProvider = new MessageViewerContentProvider();
        messageViewer.setContentProvider(messageViewerContentProvider);
        messageViewer.setLabelProvider(new MessageViewerLabelProvider());

        Table table = messageViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        // create columns
        for (int i = 0; i < MESSAGE_TBL_HDRS.length; i++) {
            TableColumn column = new TableColumn(table, SWT.LEFT);
            column.setText(MESSAGE_TBL_HDRS[i]);
        }

        return viewForm;
    }

    private Composite createPasteDataArea( Composite theParent ) {
        ViewForm viewForm = new ViewForm(theParent, SWT.BORDER);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, Util.getString(PREFIX + "pasteDataTitle"))); //$NON-NLS-1$

        // this composite is needed in order to size the table correctly.
        // the viewform doesn't look at it's content's layout data.
        Composite pnlContents = new Composite(viewForm, SWT.NONE) {
            // need to override and set height based on a minimum visible rows in the table.
            @Override
            public Point computeSize( int wHint,
                                      int hHint,
                                      boolean changed ) {
                Table table = (Table)getChildren()[0];
                return super.computeSize(wHint, table.getItemHeight() * 10 + table.getHeaderHeight(), changed);
            }
        };
        final GridLayout layout = new GridLayout();
        layout.marginWidth = layout.marginHeight = 0;
        pnlContents.setLayout(layout);
        pnlContents.setLayoutData(new GridData(GridData.FILL_BOTH));
        viewForm.setContent(pnlContents);

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.READ_ONLY;
        dataViewer = new TableViewer(pnlContents, style);
        dataViewerContentProvider = new DataViewerContentProvider();
        dataViewer.setContentProvider(dataViewerContentProvider);
        dataViewer.setLabelProvider(new DataViewerLabelProvider());

        Table table = dataViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown( MouseEvent theEvent ) {
                handleDataViewerMouseEvent(theEvent);
            }
        });
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleDataViewerSelectionEvent();
            }
        });

        // The table text editor must have the same size as the cell and must not be smaller than 50 pixels.
        dataTableEditor = new TableEditor(table);
        dataTableEditor.horizontalAlignment = SWT.LEFT;
        dataTableEditor.grabHorizontal = true;
        dataTableEditor.minimumWidth = 50;

        return viewForm;
    }

    String getPasteData( int theRow,
                         int theColumn ) {
        List row = (List)statusRecords.get(theRow);
        ClipboardPasteStatusRecord record = (ClipboardPasteStatusRecord)row.get(theColumn);
        return record.getPasteData();
    }

    private String getDialogMessage() {
        String msg = null;

        if (errorCount > 0) {
            msg = Util.getString(PREFIX + "errorsExist.msg", new Object[] {new Integer(errorCount)}); //$NON-NLS-1$
        } else if (validCount == 0) {
            msg = Util.getString(PREFIX + "noPasteData.msg"); //$NON-NLS-1$
        } else if (warningCount > 0) {
            if (truncatedRowCount > 0) {
                if (truncatedColumnCount > 0) {
                    msg = Util.getString(PREFIX + "rowsAndColumnsTruncated.msg", //$NON-NLS-1$
                                         new Object[] {new Integer(truncatedRowCount), new Integer(truncatedColumnCount)});
                } else {
                    msg = Util.getString(PREFIX + "rowsTruncated.msg", new Object[] {new Integer(truncatedRowCount)}); //$NON-NLS-1$
                }
            } else if (truncatedColumnCount > 0) {
                msg = Util.getString(PREFIX + "columnsTruncated.msg", new Object[] {new Integer(truncatedColumnCount)}); //$NON-NLS-1$
            } else {
                msg = Util.getString(PREFIX + "warningsExist.msg"); //$NON-NLS-1$
            }
        } else if (infoCount > 0) {
            msg = Util.getString(PREFIX + "infosExist.msg"); //$NON-NLS-1$
        } else {
            msg = Util.getString(PREFIX + "validData.msg"); //$NON-NLS-1$
        }

        return msg;
    }

    private int getDialogMessageType() {
        int errorType = IMessageProvider.NONE;

        if (errorCount > 0) {
            errorType = IMessageProvider.ERROR;
        } else if (validCount == 0) {
            errorType = IMessageProvider.INFORMATION;
        } else if (warningCount > 0) {
            errorType = IMessageProvider.WARNING;
        } else if (infoCount > 0) {
            errorType = IMessageProvider.INFORMATION;
        }

        return errorType;
    }

    Image getStatusImage( ClipboardPasteStatusRecord theRecord ) {
        Image result = null;

        if (theRecord.isValid()) {
            result = BLANK_IMAGE;
        } else if (theRecord.isError()) {
            result = ERROR_IMAGE;
        } else if (theRecord.isRowTruncated() || theRecord.isColumnTruncated() || theRecord.isProtectedColumn()
                   || theRecord.isWarning()) {
            result = WARNING_IMAGE;
        } else if (theRecord.isInfo()) {
            result = INFO_IMAGE;
        }

        return result;
    }

    public List getStatusRecords() {
        return statusRecords;
    }

    void handleDataViewerMouseEvent( MouseEvent theEvent ) {
        /* ----- DESIGN NOTE -----
         * This mouse event happens before the selection event.
         * The selection event depends on the selectedColumn being set.
         */

        Table table = dataViewer.getTable();
        TableItem[] selectedItems = table.getSelection();
        selectedColumn = -1;

        if (selectedItems.length > 0) {
            for (int numCols = table.getColumnCount(), i = 0; i < numCols; i++) {
                Rectangle bounds = selectedItems[0].getBounds(i);

                if (bounds.contains(theEvent.x, theEvent.y)) {
                    selectedColumn = i;
                    break;
                }
            }

            if (selectedColumn != -1) {
                int index = table.getSelectionIndex();
                List row = (List)statusRecords.get(index);
                ClipboardPasteStatusRecord record = (ClipboardPasteStatusRecord)row.get(selectedColumn);
                StructuredSelection selection = new StructuredSelection(record);
                messageViewer.setSelection(selection, true);
            }
        }
    }

    void handleDataViewerSelectionEvent() {
        /* ----- DESIGN NOTE -----
         * The mouse event happens before this selection event.
         * This selection event depends on the selectedColumn being set by the mouse event.
         */

        Table table = dataViewer.getTable();
        final int selectedRow = table.getSelectionIndex();

        // only edit if row and column has selection
        if ((selectedRow != -1) && (selectedColumn != -1)) {
            final List row = (List)statusRecords.get(selectedRow);
            ClipboardPasteStatusRecord record = (ClipboardPasteStatusRecord)row.get(selectedColumn);

            // allow editing if row is not being truncated
            if (!record.isRowTruncated() /*&& !record.isColumnTruncated()*/) {
                // The control that will be the editor must be a child of the table

                if (!record.isColumnTruncated() && !record.isProtectedColumn()) {
                    final Text textWidget = new Text(table, SWT.NONE);
                    textWidget.setText(record.getPasteData());
                    textWidget.setSelection(0, textWidget.getText().length());

                    textWidget.addFocusListener(new FocusListener() {
                        public void focusGained( FocusEvent theEvent ) {
                            setOkEnabledState(false);
                        }

                        public void focusLost( FocusEvent theEvent ) {
                            String oldText = getPasteData(selectedRow, selectedColumn);
                            String newText = textWidget.getText();
                            boolean changed = false;

                            if (newText == null) {
                                changed = (oldText != null);
                            } else {
                                changed = (oldText == null) || !oldText.equals(newText);
                            }

                            if (changed) {
                                // the validator needs to know the selected table row and column
                                int[] tableSelectedRowColumn = validator.getSelectedRowAndColumn();
                                ClipboardPasteStatusRecord newRecord = validator.constructPasteStatusRecord(newText,
                                                                                                            selectedRow
                                                                                                            + tableSelectedRowColumn[0],
                                                                                                            selectedColumn
                                                                                                            + tableSelectedRowColumn[1]);
                                setRecord(newRecord, selectedRow, selectedColumn);
                                dataViewer.refresh(row, true);
                                messageViewer.setInput(statusRecords);
                            }

                            textWidget.dispose();
                            setOkEnabledState(true);
                        }
                    });

                    // Open the text editor in the selected column of the selected row.
                    dataTableEditor.setEditor(textWidget, table.getItem(selectedRow), selectedColumn);
                    textWidget.setFocus();
                }

            }
        }
    }

    void resetCounts() {
        errorCount = infoCount = truncatedColumnCount = truncatedRowCount = validCount = warningCount = 0;
    }

    public void setDialogTitle( String theTitle ) {
        ArgCheck.isNotNull(theTitle);

        Shell shell = getShell();

        if (shell == null) {
            // dialogTitle = theTitle;
        } else {
            shell.setText(theTitle);
        }
    }

    private void setDynamicMessages() {
        lblMessagesTitle.setText(Util.getString(PREFIX + "messagesTitle", //$NON-NLS-1$
                                                new Object[] {new Integer(errorCount), new Integer(warningCount),
                                                    new Integer(infoCount)}));
        setMessage(getDialogMessage(), getDialogMessageType());
    }

    void setOkEnabledState( boolean theEnableState ) {
        boolean enable = theEnableState;

        if (theEnableState) {
            // no errors and something to paste
            enable = ((errorCount == 0) && (validCount > 0));
        }

        getButton(IDialogConstants.OK_ID).setEnabled(enable);
    }

    void setRecord( ClipboardPasteStatusRecord theRecord,
                    int theRow,
                    int theColumn ) {
        List row = (List)statusRecords.get(theRow);
        ClipboardPasteStatusRecord oldRecord = (ClipboardPasteStatusRecord)row.get(theColumn);

        // update type counts
        updateCounts(oldRecord, false);
        updateCounts(theRecord, true);

        // set data, description, and type. data and column should be the same.
        oldRecord.setPasteData(theRecord.getPasteData());
        oldRecord.setDescription(theRecord.getDescription());
        oldRecord.setType(theRecord.getType());

        // update dialog message
        setDynamicMessages();

        // enable/disable OK button
        getButton(IDialogConstants.OK_ID).setEnabled(errorCount == 0);
    }

    void updateCounts( ClipboardPasteStatusRecord theRecord,
                       boolean theIncreaseFlag ) {
        // an easy way to know what count to update is to see what image would be used.
        // this way only the logic to determine the type is only in the getStatusImage() method.
        // this method is called by the content provider.
        int addend = (theIncreaseFlag) ? 1 : -1;
        Image image = getStatusImage(theRecord);

        if (image == BLANK_IMAGE) validCount += addend;
        else if (image == ERROR_IMAGE) errorCount += addend;
        else if (image == WARNING_IMAGE) warningCount += addend;
        else if (image == INFO_IMAGE) infoCount += addend;
    }

    /**
     * Update table cell background colors if cell is being truncated.
     */
    void updateTableCellBackground( List theRow,
                                    int theColumnIndex ) {
        ClipboardPasteStatusRecord record = (ClipboardPasteStatusRecord)theRow.get(theColumnIndex);

        // currently SWT has no way of setting background color of a column cell. only can set row background.
        if (record.isRowTruncated() /*|| record.isColumnTruncated()*/) {
            int index = statusRecords.indexOf(theRow);

            if (index != -1) {
                TableItem item = dataViewer.getTable().getItem(index);
                item.setBackground(UiUtil.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
            }
        }
    }

    class DataViewerContentProvider implements IStructuredContentProvider {
        Object[] rows = null;

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object theInputElement ) {
            return (rows == null) ? new Object[0] : rows;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
            boolean validInput = false;
            List result = null;
            resetCounts();

            if ((theNewInput != null) && (theNewInput instanceof List) && !(((List)theNewInput).isEmpty())) {
                result = (List)theNewInput;

                // set initial counts
                for (int numRows = result.size(), i = 0; i < numRows; i++) {
                    List row = (List)result.get(i);

                    for (int numCols = row.size(), j = 0; j < numCols; j++) {
                        updateCounts((ClipboardPasteStatusRecord)row.get(j), true);
                    }
                }

                validInput = true;
            }

            if (validInput) {
                rows = result.toArray();
            } else {
                rows = new Object[0];
            }
        }

    }

    class DataViewerLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            Image result = null;

            if (theElement instanceof List) {
                Object obj = ((List)theElement).get(theColumnIndex);

                if (obj instanceof ClipboardPasteStatusRecord) {
                    result = getStatusImage((ClipboardPasteStatusRecord)obj);
                }
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            String result = null;

            if (theElement instanceof List) {
                Object obj = ((List)theElement).get(theColumnIndex);

                if (obj instanceof ClipboardPasteStatusRecord) {
                    ClipboardPasteStatusRecord record = (ClipboardPasteStatusRecord)obj;
                    result = record.getPasteData();
                    updateTableCellBackground((List)theElement, theColumnIndex);
                } else {
                    result = super.getText(theElement);
                }
            }

            return (result == null) ? "" : result; //$NON-NLS-1$
        }
    }

    class MessageViewerContentProvider implements IStructuredContentProvider {
        Object[] rows = null;

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object theInputElement ) {
            return (rows == null) ? new Object[0] : rows;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
            boolean validInput = true;
            List result = null;

            if ((theNewInput != null) && (theNewInput instanceof List) && !(((List)theNewInput).isEmpty())) {
                List input = (List)theNewInput;
                result = new ArrayList(input.size());

                ROW_LOOP: for (int numRows = input.size(), i = 0; i < numRows; i++) {
                    Object objRow = input.get(i);

                    if (objRow instanceof List) {
                        List row = (List)objRow;

                        // COLUMN_LOOP:
                        for (int numColumns = row.size(), j = 0; j < numColumns; j++) {
                            Object objColumn = row.get(j);

                            if (objColumn instanceof ClipboardPasteStatusRecord) {
                                if (!((ClipboardPasteStatusRecord)objColumn).isValid()) {
                                    result.add(objColumn);
                                }
                            } else {
                                validInput = false;
                                break ROW_LOOP;
                            }
                        }
                    } else {
                        validInput = false;
                        break ROW_LOOP;
                    }
                }
            } else {
                validInput = false;
            }

            if (validInput) {
                rows = result.toArray();
            } else {
                rows = new Object[0];
            }
        }

    }

    class MessageViewerLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            Image result = null;

            if ((theColumnIndex == ICON_INDEX) && (theElement instanceof ClipboardPasteStatusRecord)) {
                result = getStatusImage((ClipboardPasteStatusRecord)theElement);
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            String result = null;

            if (theElement instanceof ClipboardPasteStatusRecord) {
                ClipboardPasteStatusRecord row = (ClipboardPasteStatusRecord)theElement;

                switch (theColumnIndex) {
                    case COLUMN_INDEX:
                        result = row.getColumnName();
                        break;
                    case DATA_INDEX:
                        result = row.getPasteData();
                        break;
                    case DESCRIPTION_INDEX:
                        result = row.getDescription();
                        break;
                    default:
                        result = ""; //$NON-NLS-1$
                        break;
                }
            } else {
                result = super.getText(theElement);
            }

            return result;
        }
    }

}
