/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal.ui.util;

import java.util.List;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.rose.internal.IMessage;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * ViewFormStatusTable
 */
public class MessageTableViewForm extends Composite implements IRoseUiConstants, IRoseUiConstants.Images {

    /** Properties key prefix. */
    static final String PREFIX = I18nUtil.getPropertyPrefix(MessageTableViewForm.class);

    /** Column headers for the table. */
    private static final String[] TBL_HDRS;

    /** Index of the icon column in the table. */
    static final int IMAGE_COLUMN;

    /** Index of the message column in the table. */
    static final int MSG_COLUMN;

    static {
        // set column indexes
        IMAGE_COLUMN = 0;
        MSG_COLUMN = 1;

        // set column headers
        TBL_HDRS = new String[2];
        TBL_HDRS[IMAGE_COLUMN] = UTIL.getString(PREFIX + "table.column.icon"); //$NON-NLS-1$
        TBL_HDRS[MSG_COLUMN] = UTIL.getString(PREFIX + "table.column.description"); //$NON-NLS-1$
    }

    private List messages;

    /** Viewer for table. */
    private TableViewer viewer;

    /**
     * @param theParent
     * @since 4.1
     */
    public MessageTableViewForm( Composite theParent ) {
        super(theParent, SWT.NONE);

        final int COLUMNS = 1;
        setLayout(new GridLayout(COLUMNS, false));

        ViewForm viewForm = WidgetFactory.createViewForm(this, SWT.BORDER, GridData.FILL_BOTH, COLUMNS);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, UTIL.getString(PREFIX + "label.viewForm"))); //$NON-NLS-1$
        createViewFormContents(viewForm, COLUMNS);
    }

    private void createViewFormContents( ViewForm theViewForm,
                                         int theColumns ) {
        // contents of view form is a panel
        final int COLUMNS = 2;
        Composite pnl = WidgetFactory.createPanel(theViewForm, SWT.NONE, GridData.FILL_BOTH, theColumns, COLUMNS);
        theViewForm.setContent(pnl);

        // contents of panel is a table and a button panel

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.SIMPLE | SWT.FULL_SELECTION;
        this.viewer = WidgetFactory.createTableViewer(pnl, style);

        Table tbl = this.viewer.getTable();
        tbl.setHeaderVisible(true);
        tbl.setLinesVisible(true);

        // create columns & pack columns
        for (int i = 0; i < TBL_HDRS.length; i++) {
            TableColumn col = new TableColumn(tbl, SWT.LEFT);
            col.setText(TBL_HDRS[i]);

            if (i == IMAGE_COLUMN) {
                col.setResizable(false);
                col.setImage(RoseImporterUiUtils.getProblemViewImage());
            }
        }

        setContentProvider(new MessageTableContentProvider());
        setLabelProvider(new MessageTableLabelProvider());
        packTableColumns();
    }

    /**
     * @return
     * @since 4.1
     */
    public IContentProvider getContentProvider() {
        return this.viewer.getContentProvider();
    }

    /**
     * @return
     * @since 4.1
     */
    public IBaseLabelProvider getLabelProvider() {
        return this.viewer.getLabelProvider();
    }

    /**
     * @return
     * @since 4.1
     */
    public List getStatusMessages() {
        return this.messages;
    }

    private void packTableColumns() {
        Table tbl = this.viewer.getTable();
        for (int i = 0; i < TBL_HDRS.length; tbl.getColumn(i++).pack()) {

        }
    }

    /**
     * @param theProvider
     * @since 4.1
     */
    public void setContentProvider( IStructuredContentProvider theProvider ) {
        this.viewer.setContentProvider(theProvider);
    }

    /**
     * @param theProvider
     * @since 4.1
     */
    public void setLabelProvider( IBaseLabelProvider theProvider ) {
        this.viewer.setLabelProvider(theProvider);
    }

    /**
     * @param theMessages
     * @since 4.1
     */
    public void setMessages( List theMessages ) {
        this.messages = theMessages;
        this.viewer.setInput(theMessages);
        packTableColumns();
    }

    class MessageTableContentProvider implements IStructuredContentProvider {
        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 4.1
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 4.1
         */
        public Object[] getElements( Object theInputElement ) {
            Object[] result = null;

            if ((theInputElement != null) && (theInputElement instanceof List)) {
                result = ((List)theInputElement).toArray();
            }

            return (result == null) ? new Object[0] : result;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.1
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
        }

    }

    class MessageTableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @since 4.1
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            return (theColumnIndex == IMAGE_COLUMN) ? RoseImporterUiUtils.getStatusImage(((IMessage)theElement).getType()) : null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @since 4.1
         */
        public String getColumnText( Object theElement,
                                     int theIndex ) {
            String result = null;
            IMessage msg = (IMessage)theElement;

            if (theIndex == IMAGE_COLUMN) {
                result = ""; //$NON-NLS-1$
            } else if (theIndex == MSG_COLUMN) {
                result = msg.getText();
            } else {
                Assertion.failed(UTIL.getString(PREFIX + "msg.unexpectedColumnIndex")); //$NON-NLS-1$
            }

            return result;
        }

    }

}
