/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.IWebServiceResource;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * The <code>ShowDependenciesDialog</code> shows a table identifying for each file their dependencies.
 * 
 * @since 4.2
 */
public final class ShowDependenciesDialog extends Dialog implements IInternalUiConstants {

    /** Properties key prefix. */
    static final String PREFIX = I18nUtil.getPropertyPrefix(ShowDependenciesDialog.class);

    /** Column headers for the table. */
    private static final String[] TBL_HDRS;

    /** Index of the referencer column in the table. */
    static final int NAMESPACE_COLUMN;

    /** Index of the references column in the table. */
    static final int USES_COLUMN;

    /** Setup for constants used in creating and referencing the table. */
    static {
        // set column indexes
        NAMESPACE_COLUMN = 0;
        USES_COLUMN = 1;

        // set column headers
        TBL_HDRS = new String[2];
        TBL_HDRS[NAMESPACE_COLUMN] = getString("tableColumn.namespace"); //$NON-NLS-1$
        TBL_HDRS[USES_COLUMN] = getString("tableColumn.uses"); //$NON-NLS-1$
    }

    /** The model builder. */
    private IWebServiceModelBuilder builder;

    /**
     * Constructs a <code>ShowDependenciesDialog</code>.
     * 
     * @param theShell the dialog's parent
     * @param theBuilder the model builder
     */
    public ShowDependenciesDialog( Shell theShell,
                                   IWebServiceModelBuilder theBuilder ) {
        super(theShell, getString("title")); //$NON-NLS-1$
        setDefaultImage(WebServiceUiUtil.getImage(Images.SHOW_DEPENDENCIES));
        setCenterOnDisplay(true);
        this.builder = theBuilder;
    }

    /**
     * Overriden so that the cancel button is not displayed.
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     * @since 4.2
     */
    @Override
    protected Button createButton( Composite theParent,
                                   int theId,
                                   String theLabel,
                                   boolean theDefaultButton ) {
        // don't include the cancel button
        return (theId == IDialogConstants.CANCEL_ID) ? null : super.createButton(theParent, theId, theLabel, theDefaultButton);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    protected Control createDialogArea( Composite theParent ) {
        ViewForm viewForm = WidgetFactory.createViewForm(theParent, SWT.BORDER, GridData.FILL_BOTH, 1);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, getString("label.tableViewForm"))); //$NON-NLS-1$

        Composite pnl = WidgetFactory.createPanel(viewForm);
        viewForm.setContent(pnl);

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION;
        TableViewer viewer = WidgetFactory.createTableViewer(pnl, style);
        viewer.setContentProvider(new TableContentProvider());
        viewer.setLabelProvider(new TableLabelProvider());

        Table tbl = viewer.getTable();
        tbl.setHeaderVisible(true);
        tbl.setLinesVisible(true);

        // create columns
        for (int i = 0; i < TBL_HDRS.length; i++) {
            TableColumn col = new TableColumn(tbl, SWT.LEFT);
            col.setText(TBL_HDRS[i]);
        }

        // populate the table
        viewer.setInput(this.builder);

        // pack columns
        for (int i = 0; i < TBL_HDRS.length; i++) {
            viewer.getTable().getColumn(i).pack();
        }
        return viewForm.getContent();
    }

    /**
     * Convenience method to access model builder from inside inner classes.
     * 
     * @return the model builder
     * @since 4.2
     */
    IWebServiceModelBuilder getBuilder() {
        return this.builder;
    }

    /**
     * Convenience method to retrieve localized text.
     * 
     * @param theKey the properties key whose localized value is being requested
     * @return the localized value
     * @since 4.2
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }

    /** The table content provider. */
    class TableContentProvider implements IStructuredContentProvider {

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
            List result = new ArrayList();
            IWebServiceModelBuilder builder = getBuilder();
            Iterator itr = builder.getResources().iterator();

            while (itr.hasNext()) {
                IWebServiceResource resource = (IWebServiceResource)itr.next();
                Collection references = resource.getReferencedResources();

                if ((references != null) && !references.isEmpty()) {
                    Iterator itrRefs = references.iterator();

                    while (itrRefs.hasNext()) {
                        result.add(new TableRow(resource, (IWebServiceResource)itrRefs.next()));
                    }
                }
            }

            return result.toArray();
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

    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @since 4.1
         */
        public Image getColumnImage( Object theElement,
                                     int theIndex ) {
            Image result = null;

            if (theElement instanceof TableRow) {
                TableRow row = (TableRow)theElement;

                if (theIndex == NAMESPACE_COLUMN) {
                    if (!row.namespace.isResolved()) {
                        result = WebServiceUiUtil.getStatusImage(row.namespace.getStatus());
                    }
                } else if (theIndex == USES_COLUMN) {
                    if (!row.uses.isResolved()) {
                        result = WebServiceUiUtil.getStatusImage(row.uses.getStatus());
                    }
                } else {
                    // should not happen
                    CoreArgCheck.isTrue(false, UTIL.getString(PREFIX + "msg.unknownTableColumn", //$NON-NLS-1$
                                                              new Object[] {Integer.toString(theIndex)}));
                }
            } else {
                // should not happen
                CoreArgCheck.isTrue(false, UTIL.getString(PREFIX + "msg.unknownObjectType", //$NON-NLS-1$
                                                          new Object[] {theElement.getClass().getName()}));
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @since 4.1
         */
        public String getColumnText( Object theElement,
                                     int theIndex ) {
            String result = null;

            if (theElement instanceof TableRow) {
                if (theIndex == NAMESPACE_COLUMN) {
                    result = WebServiceUiUtil.getText(((TableRow)theElement).namespace);
                } else if (theIndex == USES_COLUMN) {
                    result = WebServiceUiUtil.getText(((TableRow)theElement).uses);
                } else {
                    // should not happen
                    CoreArgCheck.isTrue(false, UTIL.getString(PREFIX + "msg.unknownTableColumn", //$NON-NLS-1$
                                                              new Object[] {Integer.toString(theIndex)}));
                }
            } else {
                // should not happen
                CoreArgCheck.isTrue(false, UTIL.getString(PREFIX + "msg.unknownObjectType", //$NON-NLS-1$
                                                          new Object[] {theElement.getClass().getName()}));
            }

            return result;
        }

    }

    /**
     * The <code>TableRow</code> class is used for the table row model object.
     * 
     * @since 4.2
     */
    private class TableRow {
        //
        // FIELDS
        //

        public IWebServiceResource namespace;
        public IWebServiceResource uses;

        //
        // CONSTRUCTORS
        //

        public TableRow( IWebServiceResource theNamespace,
                         IWebServiceResource theUses ) {
            this.namespace = theNamespace;
            this.uses = theUses;
        }
    }
}
