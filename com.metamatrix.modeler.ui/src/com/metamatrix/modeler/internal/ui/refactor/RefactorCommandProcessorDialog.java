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
package com.metamatrix.modeler.internal.ui.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.SelectionDialog;
import com.metamatrix.modeler.core.refactor.RefactorCommand;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * RefactorCommandProcessorDialog is a dialog that displays the workspace tree and allows selection sample of the dialog code:
 * private void handleBrowseTypeButtonPressed_TestOfFileFolderMoveDialog() { // ======================================== // launch
 * Refactor Command Processor Dialog // ======================================== RefactorCommandProcessorDialog ffmdDialog = new
 * RefactorCommandProcessorDialog( UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell() ); ffmdDialog.setAllowMultiple(
 * false ); ffmdDialog.setTitle( "Move this thing" ); ffmdDialog.setMessage( "Select the move destination:" );
 * ffmdDialog.setValidator( new RelationshipTypeSelectionValidator() ); ffmdDialog.setResource( xxx); ffmdDialog.setCommand( xxx
 * ); ffmdDialog.open(); if ( ffmdDialog.getReturnCode() == FileFolderMoveDialog.OK ) { Object[] oSelectedObjects =
 * ffmdDialog.getResult(); ... } }
 */
public class RefactorCommandProcessorDialog extends SelectionDialog implements PluginConstants {

    private final String TITLE = UiConstants.Util.getString("RefactorCommandProcessorDialog.problemDialog.title"); //$NON-NLS-1$       
    private static final String HEADER_ERROR_MESSAGE = UiConstants.Util.getString("RefactorCommandProcessorDialog.headerErrorMessage.text"); //$NON-NLS-1$
    private static final String HEADER_WARNING_MESSAGE = UiConstants.Util.getString("RefactorCommandProcessorDialog.headerWarningMessage.text"); //$NON-NLS-1$

    private static final int WIDTH = 600;
    private static final int HEIGHT = 150;

    private RefactorCommand command;
    private ProblemTablePanel pnlProblemTable;
    TableColumn column1;
    TableColumn column2;

    /**
     * Construct an instance of FileFolderMoveDialog. This constructor defaults to the resource root.
     * 
     * @param propertiedObject the EObject to display in this
     * @param parent the shell
     */
    public RefactorCommandProcessorDialog( Shell parent,
                                           RefactorCommand command ) {
        super(parent);

        this.command = command;

        init();
    }

    private void init() {

        // set the title
        setTitle(TITLE);

        // set the message
        if (problemSetContainsErrors()) {
            setMessage(HEADER_ERROR_MESSAGE);
        } else {
            setMessage(HEADER_WARNING_MESSAGE);
        }
    }

    private boolean problemSetContainsErrors() {
        boolean bHasError = false;

        // check all of the problems for any that are errors
        Collection colMessages = command.getPostExecuteMessages();

        if ((colMessages != null) && !colMessages.isEmpty()) {

            Iterator it = colMessages.iterator();

            while (it.hasNext()) {
                IStatus stTemp = (IStatus)it.next();
                if (stTemp.getSeverity() < IStatus.ERROR) {
                    // no action
                } else {
                    bHasError = true;
                    break;
                }
            }
        }

        return bHasError;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite container ) {

        // create parent
        Composite parent = (Composite)super.createDialogArea(container);

        GridLayout gridLayout = new GridLayout();
        parent.setLayout(gridLayout);
        gridLayout.numColumns = 1;

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = WIDTH;
        gd.heightHint = HEIGHT;
        parent.setLayoutData(gd);

        // establish the message
        // it would be nice to lighten the background of this label...
        createMessageArea(parent);

        // create the table
        pnlProblemTable = new ProblemTablePanel(parent, command);
        GridData gd2 = new GridData(GridData.FILL_BOTH);
        gd2.widthHint = WIDTH - 30;
        gd2.heightHint = HEIGHT - 50;
        pnlProblemTable.setLayoutData(gd2);

        return parent;
    }

    /**
     * Overridden to make the shell resizable.
     * 
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        setShellStyle(getShellStyle() | SWT.RESIZE);
        super.create();
    }

    /* Overridden to limit this dialog to just an OK button
     *  (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {

        super.createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    class ProblemTablePanel extends Composite implements SelectionListener {

        private RefactorCommand mrcCommand;

        private final String FOUND_PROBLEMS_HEADING = UiConstants.Util.getString("RefactorCommandProcessorDialog.foundProblems.title"); //$NON-NLS-1$

        private Composite pnlTableStuff;

        private Table tblProblemTable;
        private String[] columnNames = new String[] {
        /*
         * Extremely weird:  When I put them in BACKWARDS, they appear in the desired order...
         */
        FOUND_PROBLEMS_HEADING, "" //$NON-NLS-1$
        };

        private TableViewer tvRoleTableViewer;
        private ProblemTableContentProvider cpProblemContentProvider;
        private ProblemTableLabelProvider lpProblemLabelProvider;

        public ProblemTablePanel( Composite parent,
                                  RefactorCommand mrcCommand ) {

            super(parent, SWT.NONE);
            // this.parent = parent;
            this.mrcCommand = mrcCommand;

            createControl(this);
        }

        @Override
        public void setVisible( boolean b ) {
            column1.pack();
            column2.pack();
            super.setVisible(b);
        }

        /**
         * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
         */
        public void createControl( Composite parent ) {

            // 0. Set layout for the SashForm
            GridLayout gridLayout = new GridLayout();
            this.setLayout(gridLayout);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridLayout.marginWidth = gridLayout.marginHeight = 0;

            this.setLayoutData(gridData);

            // 2. Create the table
            createTableStuffPanel(parent);

        }

        private void createTableStuffPanel( Composite parent ) {

            pnlTableStuff = new Composite(parent, SWT.NONE);

            GridLayout gridLayout = new GridLayout();
            gridLayout.marginWidth = gridLayout.marginHeight = 0;
            pnlTableStuff.setLayout(gridLayout);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            pnlTableStuff.setLayoutData(gridData);

            // 1. Create the table
            createTableViewerPanel(pnlTableStuff);

        }

        /*
         * Create the TableViewerPanel 
         */
        private void createTableViewerPanel( Composite parent ) {
            // Create the table
            createTable(parent);

            // Create and setup the TableViewer
            createTableViewer();
            cpProblemContentProvider = new ProblemTableContentProvider(mrcCommand);
            lpProblemLabelProvider = new ProblemTableLabelProvider();

            tvRoleTableViewer.setContentProvider(cpProblemContentProvider);
            tvRoleTableViewer.setLabelProvider(lpProblemLabelProvider);

            if (mrcCommand != null) {
                tvRoleTableViewer.setInput(mrcCommand);
            }

        }

        /**
         * Create the Table
         */
        private void createTable( Composite parent ) {
            int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

            tblProblemTable = new Table(parent, style);
            TableLayout layout = new TableLayout();
            tblProblemTable.setLayout(layout);

            GridData gridData = new GridData(GridData.FILL_BOTH);
            tblProblemTable.setLayoutData(gridData);

            tblProblemTable.setLinesVisible(true);
            tblProblemTable.setHeaderVisible(true);

            // 1st column
            column1 = new TableColumn(tblProblemTable, SWT.LEFT);
            column1.setText(columnNames[1]);
            column1.setResizable(false);
            column1.setImage(UiPlugin.getDefault().getImage(Images.ERROR_WARNING_ICON));
            column1.setText(""); //$NON-NLS-1$
            column1.pack();

            ColumnWeightData weight = new ColumnWeightData(1);
            layout.addColumnData(weight);

            // 2nd column
            column2 = new TableColumn(tblProblemTable, SWT.LEFT);
            column2.setText(columnNames[0]);
            column2.pack();
            ColumnWeightData weight2 = new ColumnWeightData(12, 50, true);
            layout.addColumnData(weight2);

        }

        /**
         * Create the TableViewer
         */
        private void createTableViewer() {

            tvRoleTableViewer = new TableViewer(tblProblemTable);
            tvRoleTableViewer.setUseHashlookup(true);

            tvRoleTableViewer.setColumnProperties(columnNames);

            // Create the cell editors
            CellEditor[] editors = new CellEditor[columnNames.length];

            // Column 1 : Attribute not editable
            editors[0] = null;

            // Column 1 : Attribute not editable
            editors[1] = null;

            // Assign the cell editors to the viewer
            tvRoleTableViewer.setCellEditors(editors);
        }

        public void widgetSelected( SelectionEvent e ) {
        }

        public void widgetDefaultSelected( SelectionEvent e ) {
        }

    }

    class ProblemTableRow {

        private Object oObject;

        public ProblemTableRow( Object oObject ) {

            this.oObject = oObject;
        }

        public Object getObject() {
            return oObject;
        }

        public String getColumnText( int iColumnIndex ) {
            String sResult = ""; //$NON-NLS-1$
            IStatus status;

            if (oObject instanceof IStatus) {

                status = (IStatus)oObject;

                switch (iColumnIndex) {
                    case 0:
                        // no text, only image in col 1:
                        sResult = ""; //$NON-NLS-1$
                        break;

                    case 1:
                        sResult = status.getMessage();
                        break;
                }

            } else {
                switch (iColumnIndex) {
                    case 0:
                        sResult = oObject.toString();
                        break;

                    case 1:
                        sResult = oObject.toString();
                        break;
                }
            }

            return sResult;
        }

        public Object getValue( int theIndex ) {
            Object oResult = null;

            return oResult;
        }
    }

    class ProblemTableContentProvider implements IStructuredContentProvider {

        RefactorCommand mrcCommand;

        public ProblemTableContentProvider( RefactorCommand mrcCommand ) {
            // need to know what role and what metaclass type;
            // the rest we work out calling editor methods (getEditor() is globally visible).

            this.mrcCommand = mrcCommand;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object theInputElement ) {

            Object[] result = null;

            Collection colMessages = new ArrayList();

            if (mrcCommand != null) {
                colMessages = mrcCommand.getPostExecuteMessages();
            }

            if ((colMessages != null) && !colMessages.isEmpty()) {

                int numRows = colMessages.size();
                result = new Object[numRows];

                Iterator it = colMessages.iterator();
                for (int i = 0; i < numRows; i++) {
                    Object oObject = it.next();

                    result[i] = new ProblemTableRow(oObject);
                }
            }

            return ((colMessages == null) || colMessages.isEmpty()) ? new Object[0] : result;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {

            if (theOldInput != null) {
                // do any required cleanup
            }

            mrcCommand = (RefactorCommand)theNewInput;
            if (theNewInput != null) {
                theViewer.refresh();
            }
        }

    }

    class ProblemTableLabelProvider extends LabelProvider implements ITableLabelProvider {

        public Image getColumnImage( Object theElement,
                                     int iColumnIndex ) {
            Object oRealObject = ((ProblemTableRow)theElement).getObject();

            Image imgResult = null;

            if (oRealObject instanceof IStatus) {
                IStatus status = (IStatus)oRealObject;

                switch (iColumnIndex) {
                    case 0:
                        // get the problem icon from eclipse code and always use it
                        if (status.getSeverity() == IStatus.ERROR) {
                            imgResult = UiPlugin.getDefault().getImage(PluginConstants.Images.ERROR_ICON);
                        } else if (status.getSeverity() == IStatus.WARNING) {
                            imgResult = UiPlugin.getDefault().getImage(PluginConstants.Images.WARNING_ICON);
                        }

                        break;
                    case 1:
                        // no image
                        break;
                }
            }
            return imgResult;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int iColumnIndex ) {
            ProblemTableRow row = (ProblemTableRow)theElement;
            return row.getColumnText(iColumnIndex);

        }

    }
}
