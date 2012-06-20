/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.expression;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectEditor;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.ui.builder.model.FunctionEditorModel;
import org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener;
import org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent;
import org.teiid.query.ui.builder.util.BuilderUtils;

/**
 * FunctionEditor
 */
public class FunctionEditor extends AbstractLanguageObjectEditor {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(FunctionEditor.class);

    private static final String[] TBL_HDRS = new String[] {Util.getString(PREFIX + "argColumnHdr"), //$NON-NLS-1$
        Util.getString(PREFIX + "valueColumnHdr")}; //$NON-NLS-1$

    /** Function argument name table column index. */
    private static final int ARG_INDEX = 0;

    /** Function argumen value table column index. */
    private static final int VALUE_INDEX = 1;

    private ViewController controller;

    FunctionEditorModel model;

    private boolean processingEvents = true;

    private Combo cbxCategory;

    private Combo cbxFunction;

    private Composite pnlContent;

    private Table table;

    private TableViewer viewer;

    /**
     * Constructs a <code>FunctionEditor</code> using the given model.
     * 
     * @param theParent the parent container
     * @param theModel the editor's model
     * @throws IllegalArgumentException if any of the parameters are <code>null</code>
     */
    public FunctionEditor( Composite theParent,
                           FunctionEditorModel theModel ) {
        super(theParent, Function.class, theModel);
        // Note: the call to super calls createUi

        controller = new ViewController();
        model = theModel;
        model.addModelListener(controller);

        viewer.setInput(model);
        cbxCategory.setItems(model.getCategories());

        // start the controller
        controller.initialize();
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.ILanguageObjectEditor#acceptFocus()
     */
    @Override
    public void acceptFocus() {
        cbxFunction.setFocus();
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectEditor#createUi(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createUi( Composite theParent ) {
        pnlContent = new Composite(theParent, SWT.NONE);
        pnlContent.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        pnlContent.setLayout(layout);

        //
        // pnlContent contents
        //

        Label lblCategory = new Label(pnlContent, SWT.NONE);
        lblCategory.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        lblCategory.setText(Util.getString(PREFIX + "lblCategory")); //$NON-NLS-1$

        cbxCategory = new Combo(pnlContent, SWT.BORDER | SWT.READ_ONLY);
        cbxCategory.setToolTipText(Util.getString(PREFIX + "cbxCategory.tip")); //$NON-NLS-1$
        cbxCategory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbxCategory.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleCategorySelected();
            }
        });

        Label lblFunction = new Label(pnlContent, SWT.NONE);
        lblFunction.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        lblFunction.setText(Util.getString(PREFIX + "lblFunction")); //$NON-NLS-1$

        cbxFunction = new Combo(pnlContent, SWT.BORDER | SWT.READ_ONLY);
        cbxFunction.setToolTipText(Util.getString(PREFIX + "cbxFunction.tip")); //$NON-NLS-1$
        cbxFunction.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbxFunction.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleFunctionSelected();
            }
        });

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.READ_ONLY;
        viewer = new TableViewer(pnlContent, style);
        viewer.setContentProvider(new TableContentProvider());
        viewer.setLabelProvider(new TableLabelProvider());

        table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        table.setLayoutData(gd);

        // create columns
        for (int i = 0; i < TBL_HDRS.length; i++) {
            TableColumn argCol = new TableColumn(table, SWT.CENTER);
            argCol.setText(TBL_HDRS[i]);
        }
    }

    void displayCategoryChange() {
        String category = model.getCategory();

        if (category == null) {
            model.setCategory(model.getDefaultCategory());
        } else {
            if ((cbxCategory.getSelectionIndex() == -1) || !category.equals(cbxCategory.getText())) {
                cbxCategory.setText(category);
            }

            //
            // make sure items in combobox are correct
            //

            String[] functions = model.getFunctions();
            String[] items = cbxFunction.getItems();
            boolean loadItems = true;

            if (functions.length == items.length) {
                for (int i = 0; i < functions.length; i++) {
                    if (!functions[i].equals(items[i])) {
                        loadItems = true;
                        break;
                    }
                }
            }

            if (loadItems) {
                processingEvents = false;
                cbxFunction.setItems(functions);
                processingEvents = true;
            }

            //
            // make sure appropriate function is correct
            //

            String function = model.getFunctionName();

            if (function == null) {
                if (cbxFunction.getItemCount() > 0) {
                    // select first function if no function is set
                    model.setFunctionName(cbxFunction.getItem(0));
                } else {
                    // no functions found for category
                    Util.log(IStatus.WARNING, Util.getString(PREFIX + "noFunctionsFound", new Object[] {category})); //$NON-NLS-1$
                }
            } else {
                if ((cbxFunction.getSelectionIndex() == -1) || !cbxFunction.getText().equals(function)) {
                    processingEvents = false;
                    model.setFunctionName(function);
                    processingEvents = true;
                }
            }
        }
    }

    void displayFunctionChange() {
        // only update UI if different function selected
        if ((cbxFunction.getSelectionIndex() == -1) || !cbxFunction.getText().equals(model.getFunction().toString())) {
            cbxFunction.setText(model.getFunctionName());
            cbxFunction.setToolTipText(model.getFunctionDescription());
            viewer.refresh();

            for (int i = 0; i < TBL_HDRS.length; table.getColumn(i++).pack()) {

            }
        }
    }

    void displayLanguageObjectChange() {
        displayCategoryChange();
        displayFunctionChange();
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.ILanguageObjectEditor#getTitle()
     */
    @Override
    public String getTitle() {
        return Util.getString(PREFIX + "title"); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.AbstractLanguageObjectEditor#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        return Util.getString(PREFIX + "tip"); //$NON-NLS-1$
    }

    /** Handler for function category combobox change. */
    void handleCategorySelected() {
        model.setCategory(cbxCategory.getText());
    }

    /** Handler for function combobox change. */
    void handleFunctionSelected() {
        if (processingEvents) {
            model.setFunctionName(cbxFunction.getText());
        }
    }

    /**
     * @see org.teiid.designer.transformation.ui.builder.ILanguageObjectEditor#setLanguageObject(org.teiid.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLanguageObject ) {
        if (theLanguageObject == null) {
            clear();
        } else {
            if (!(theLanguageObject instanceof Function)) {
                CoreArgCheck.isTrue((theLanguageObject instanceof Function),
                                    Util.getString(PREFIX + "invalidLanguageObject", //$NON-NLS-1$
                                                   new Object[] {theLanguageObject.getClass().getName()}));
            }

            model.setLanguageObject(theLanguageObject);
        }
    }

    /**
     * The <code>ViewController</code> class is a view controller for the <code>FunctionEditor</code>.
     */
    class ViewController implements ILanguageObjectEditorModelListener {

        public void initialize() {
            // set first selection
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    modelChanged(new LanguageObjectEditorModelEvent(model, FunctionEditorModel.CATEGORY));
                }
            });
        }

        /**
         * @see org.teiid.query.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(org.teiid.query.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            String type = theEvent.getType();

            if (type.equals(FunctionEditorModel.CATEGORY)) {
                displayCategoryChange();
            } else if (type.equals(FunctionEditorModel.SELECTED_FUNCTION)) {
                displayFunctionChange();
            } else if (type.equals(LanguageObjectEditorModelEvent.SAVED)) {
                displayLanguageObjectChange();
            }
        }

    }

    class TableContentProvider implements IStructuredContentProvider {

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
            List args = model.getFunctionArgNames();

            if ((args != null) && !args.isEmpty()) {
                List argValues = model.getFunctionArgValues();
                int numRows = args.size();
                result = new Object[numRows];

                for (int i = 0; i < numRows; i++) {
                    result[i] = new TableRow(args.get(i), argValues.get(i));
                }
            }

            return ((args == null) || args.isEmpty()) ? new Object[0] : result;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
        }

    }

    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            TableRow row = (TableRow)theElement;
            return row.getColumnText(theColumnIndex);
        }

    }

    class TableRow {

        private Object arg;
        private Object value;

        public TableRow( Object theArgName,
                         Object theArgValue ) {
            arg = theArgName;
            value = theArgValue;
        }

        public String getColumnText( int theIndex ) {
            String result = "unknown"; //$NON-NLS-1$
            if (theIndex == ARG_INDEX) {
                result = arg.toString();
            } else if (theIndex == VALUE_INDEX) {
                result = (value == null) ? BuilderUtils.UNDEFINED : value.toString();
            }

            return result;
        }
    }
}
