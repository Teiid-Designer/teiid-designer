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

package com.metamatrix.modeler.internal.dqp.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * The <code>ConnectorSelectionPage</code> class is the second page of the connector importer. The user chooses the connector
 * bindings they want to import. This page will not be shown if there is no available connector bindings to import.
 * 
 * @since 5.5.3
 */
public final class ConnectorSelectionPage extends WizardPage {

    /**
     * Table column indexes.
     * 
     * @since 5.5.3
     */
    private interface ColumnIndexes {
        int NAME = 0;
        int STATUS = 1;
        int TYPE = 2;
    }

    /**
     * Table headers used to create the table columns.
     * 
     * @since 5.5.3
     */
    private static final String[] HEADERS;

    static {
        HEADERS = new String[3];
        HEADERS[ColumnIndexes.NAME] = I18n.NameHeader;
        HEADERS[ColumnIndexes.STATUS] = I18n.StatusHeader;
        HEADERS[ColumnIndexes.TYPE] = I18n.TypeHeader;
    }

    /**
     * The area where the selected row details is shown.
     * 
     * @since 5.5.3
     */
    private DetailsAreaPanel detailsArea;

    /**
     * The business object used by the wizard.
     * 
     * @since 5.5.3
     */
    private final ConnectorImportHelper helper;

    /**
     * The table containing the available connector bindings for import.
     * 
     * @since 5.5.3
     */
    private TableViewer viewer;

    /**
     * @param helper the business object
     * @since 5.5.3
     */
    public ConnectorSelectionPage( ConnectorImportHelper helper ) {
        super(ConnectorSelectionPage.class.getSimpleName());
        this.helper = helper;
        setTitle(I18n.ConnectorSelectionPageTitle);
    }

    /**
     * A way for inner classes to get to the business object.
     * 
     * @since 5.5.3
     */
    ConnectorImportHelper accessHelper() {
        return this.helper;
    }

    /**
     * Constructs the table containing the connector bindings available for import.
     * 
     * @param parent the UI parent of the table panel
     * @since 5.5.3
     */
    private void constructTablePanel( Composite parent ) {
        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK;
        this.viewer = WidgetFactory.createTableViewer(parent, style);
        this.viewer.setContentProvider(new BindingContentProvider());
        this.viewer.setLabelProvider(new BindingLabelProvider());

        Table table = this.viewer.getTable();
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleConnectorSelected(event);
            }
        });

        // create columns
        WidgetFactory.createTableColumns(table, HEADERS, SWT.LEFT);
        table.getColumn(ColumnIndexes.STATUS).setResizable(false);

        // populate the table
        this.viewer.setInput(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {
        Composite mainControl = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH);
        setControl(mainControl);

        // put a splitter between the table and the import details
        SashForm splitter = WidgetFactory.createSplitter(mainControl, SWT.VERTICAL, GridData.FILL_BOTH);

        // construct table
        constructTablePanel(splitter);

        // create details control that displays the selected connector type's import details
        this.detailsArea = new DetailsAreaPanel(splitter);

        // position the splitter
        splitter.setWeights(new int[] {7, 3});
        splitter.layout();

        // set initial state
        refresh();
    }

    /**
     * Handler for when a table row is selected.
     * 
     * @since 5.5.3
     */
    void handleConnectorSelected( SelectionEvent event ) {
        // user checked/unchecked connector binding so update import status in business object
        TableItem tableItem = (TableItem)event.item;
        if (event.detail == SWT.CHECK) {
            ConnectorBinding binding = (ConnectorBinding)tableItem.getData();
            this.helper.setImportStatus(binding, tableItem.getChecked());
        }

        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        ConnectorBinding selectedBinding = (ConnectorBinding)selection.getFirstElement();
        // update details panel and page state
        this.detailsArea.setStatus(this.helper.getStatus(selectedBinding));
        updateState();
    }

    private void refresh() {
        this.viewer.refresh();
        WidgetUtil.pack(this.viewer.getTable(), 10);

        // update checkboxes
        for (TableItem item : this.viewer.getTable().getItems()) {
            ConnectorBinding binding = (ConnectorBinding)item.getData();
            item.setChecked(this.helper.isSelectedForImport(binding));
        }

        // auto-select first row
        Table table = this.viewer.getTable();

        if (this.viewer.getSelection().isEmpty() && (table.getItemCount() != 0)) {
            table.select(0);
            Event event = new Event();
            event.widget = table;
            table.notifyListeners(SWT.Selection, event);
        }

        // update page state
        updateState();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     * @since 5.5.3
     */
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);

        // next or previous button was pressed to show this page so update UI
        if (visible) {
            refresh();
        }
    }

    /**
     * Update page messages and button state.
     * 
     * @since 5.5.3
     */
    public void updateState() {
        // page is complete if (1) at least one binding is being imported, or
        // (2) and at least one type is being imported
        int numTypesBeingImported = this.helper.getSelectedImportFileConnectorTypes().size();
        int numBindingsBeingImported = this.helper.getSelectedImportFileConnectors().size();
        boolean complete = ((numTypesBeingImported != 0) || (numBindingsBeingImported != 0));

        setErrorMessage(null);
        if (complete) {
            setMessage(NLS.bind(I18n.BindingsAndTypesBeingImportedMsg, numTypesBeingImported, numBindingsBeingImported));
        } else {
            setMessage(I18n.NoTypesNoBindingsBeingImportedMsg, IMessageProvider.ERROR);
        }

        setPageComplete(complete);
    }

    /**
     * The <code>BindingContentProvider</code> provides the available connector bindings as content.
     * 
     * @since 5.5.3
     */
    class BindingContentProvider implements IStructuredContentProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object inputElement ) {
            return accessHelper().getAllImportFileConnectors().toArray();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
            // nothing to do
        }
    }

    /**
     * @since 5.5.3
     */
    class BindingLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object element,
                                     int columnIndex ) {
            if (columnIndex == ColumnIndexes.STATUS) {
                ConnectorBinding binding = (ConnectorBinding)element;
                IStatus status = accessHelper().getStatus(binding);
                return WidgetUtil.getStatusImage(status);
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object element,
                                     int columnIndex ) {
            ConnectorBinding binding = (ConnectorBinding)element;

            if (columnIndex == ColumnIndexes.NAME) {
                return binding.getFullName();
            }

            if (columnIndex == ColumnIndexes.TYPE) {
                return binding.getComponentTypeID().getFullName();
            }

            return StringUtil.Constants.EMPTY_STRING;
        }
    }
}
