/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.transformation.search.TransformationSearchHelper;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.editors.sqleditor.SqlTextViewer;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.common.graphics.ColorManager;
import org.teiid.designer.ui.common.tree.AbstractTreeContentProvider;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.viewsupport.SelectModelObjectLabelProvider;


/**
 * @since 8.0
 */
public class TransformationSearchPanel extends Composite implements ModifyListener {

    /** The size of the dialogs search history. */
    private static final int HISTORY_SIZE = 5;

    private static final String STAGING_TABLE_STR = "<Staging Table>"; //$NON-NLS-1$
    private static final String MAPPING_CLASS_STR = "<Mapping Class>"; //$NON-NLS-1$
    private static final String TABLE_STR = "<Table>"; //$NON-NLS-1$
    private static final String PROCEDURE_STR = "<Procedure>"; //$NON-NLS-1$

    private static final String SQL_MESSAGE_SUFFIX = CoreStringUtil.Constants.SPACE
                                                     + getString("TransformationSearchPanel.sqlMessageSuffix"); //$NON-NLS-1$
    private static final String SEARCH_STATUS = getString("TransformationSearchPanel.searchStatus"); //$NON-NLS-1$
    private static final String RESULTS_TITLE = getString("TransformationSearchPanel.resultsTitle"); //$NON-NLS-1$
    private static final String NO_SELECTION = getString("TransformationSearchPanel.noSelection"); //$NON-NLS-1$
    private static final String SQL_TEXT = getString("TransformationSearchPanel.sqlText"); //$NON-NLS-1$
    private static final String INPUT = getString("TransformationSearchPanel.input"); //$NON-NLS-1$
    private static final String FIND = getString("TransformationSearchPanel.find"); //$NON-NLS-1$
    private static final String CASE_SENSITIVE = getString("TransformationSearchPanel.caseSensitive"); //$NON-NLS-1$
    private static final String PERFORM_SEARCH = getString("TransformationSearchPanel.performSearch"); //$NON-NLS-1$
    private static final String NO_STRING_ENTERED = getString("TransformationSearchPanel.noStringEntered"); //$NON-NLS-1$

    CLabel messageLabel;
    CLabel sqlMessageLabel;

    private Combo fFindField;
    private List fFindHistory;
    Button fCaseCheckBox;
    private Button performSearchButton;
    private TreeViewer searchResultsTreeViewer;
    private TextViewer sqlTextViewer;
    Group sqlTextPanel;
    private IDocument sqlDocument;
    private ColorManager colorManager;

    TransformationSearchHelper searchHelper;
    TransformationSearchDialog parentDialog;

    private Collection lastResults = Collections.EMPTY_LIST;
    private String lastSearchString = CoreStringUtil.Constants.EMPTY_STRING;
    EObject lastSelectedTarget;

    private static String getString( String id ) {
        return UiConstants.Util.getString(id);
    }

    private static String getString( String id,
                                     Object value1 ) {
        return UiConstants.Util.getString(id, value1);
    }

    private static String getString( String id,
                                     Object value1,
                                     Object value2 ) {
        return UiConstants.Util.getString(id, value1, value2);
    }

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public TransformationSearchPanel( Composite parent,
                                      TransformationSearchDialog dialog ) {
        super(parent, SWT.NONE);
        parentDialog = dialog;
        init();
    }

    /**
     * Initialize the panel.
     */
    private void init() {
        fFindHistory = new ArrayList(HISTORY_SIZE - 1);

        searchHelper = new TransformationSearchHelper();

        // ------------------------------
        // Set layout for the Composite
        // ------------------------------

        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        this.setLayoutData(gridData);

        createInputPanel(this);

        // Create SEARCH STATUS GROUP ---------------------------------------
        Composite searchMessagePanel = WidgetFactory.createGroup(this, SEARCH_STATUS, SWT.NULL | SWT.FILL);
        GridData panelGD = new GridData(GridData.FILL_HORIZONTAL);
        panelGD.grabExcessHorizontalSpace = true;
        // panelGD.grabExcessVerticalSpace = true;
        searchMessagePanel.setLayoutData(panelGD);

        this.messageLabel = WidgetFactory.createLabel(searchMessagePanel, SWT.NULL | SWT.FILL);

        this.messageLabel.setText(" "); //$NON-NLS-1$
        GridData messageLabelGridData = new GridData();
        messageLabelGridData.horizontalAlignment = GridData.FILL;
        messageLabelGridData.verticalAlignment = GridData.FILL;
        messageLabelGridData.grabExcessHorizontalSpace = true;
        this.messageLabel.setLayoutData(messageLabelGridData);

        // Create SEARCH RESULTS GROUP ---------------------------------------
        Composite searchResultsPanel = WidgetFactory.createGroup(this, RESULTS_TITLE, SWT.NULL | SWT.FILL);
        searchResultsPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        this.sqlMessageLabel = WidgetFactory.createLabel(searchResultsPanel, SWT.NULL | SWT.FILL);
        this.sqlMessageLabel.setText("No Search Results"); //$NON-NLS-1$
        GridData sqlMessageLabelGridData = new GridData();
        sqlMessageLabelGridData.horizontalAlignment = GridData.FILL;
        sqlMessageLabelGridData.verticalAlignment = GridData.FILL;
        sqlMessageLabelGridData.grabExcessHorizontalSpace = true;
        this.sqlMessageLabel.setLayoutData(sqlMessageLabelGridData);

        searchResultsTreeViewer = createResultsTreeViewer(searchResultsPanel);

        searchResultsTreeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        searchResultsTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
			public void selectionChanged( SelectionChangedEvent theEvent ) {
                StructuredSelection selection = (StructuredSelection)theEvent.getSelection();
                if (selection.getFirstElement() instanceof EObject || selection.getFirstElement() instanceof SUIDObject) {
                    setSqlText(selection.getFirstElement());
                } else {
                    setSqlText(null);
                }

            }
        });

        // fill in combo contents
        fFindField.removeModifyListener(this);
        updateCombo(fFindField, fFindHistory);
        fFindField.addModifyListener(this);

        sqlTextPanel = WidgetFactory.createGroup(this, SQL_TEXT, SWT.NULL | SWT.FILL);
        GridData pnlGD = new GridData(GridData.FILL_BOTH);
        pnlGD.grabExcessHorizontalSpace = true;
        sqlTextPanel.setLayoutData(pnlGD);

        colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        sqlTextViewer = new SqlTextViewer(sqlTextPanel, new VerticalRuler(0), styles, colorManager);
        sqlDocument = new Document();
        sqlTextViewer.setInput(sqlDocument);
        sqlTextViewer.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        sqlTextViewer.setEditable(false);
        sqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        sqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

    }

    void setSqlText( Object obj ) {
        String text = CoreStringUtil.Constants.EMPTY_STRING;
        String sqlMessage = CoreStringUtil.Constants.EMPTY_STRING;

        if (obj != null) {
            if (obj instanceof EObject) {
                SqlTransformationMappingRoot tRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot((EObject)obj);
                if (tRoot != null) {
                    text = TransformationSearchHelper.getUserSql(tRoot, QueryValidator.SELECT_TRNS);
                    sqlMessage = SUIDObject.SELECT_STR + SQL_MESSAGE_SUFFIX;
                }

            } else if (obj instanceof SUIDObject) {
                SUIDObject suidObj = (SUIDObject)obj;
                SqlTransformationMappingRoot tRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(suidObj.getParent());
                if (tRoot != null) {
                    text = TransformationSearchHelper.getUserSql(tRoot, suidObj.getSqlType());
                    sqlMessage = suidObj.getLabel() + SQL_MESSAGE_SUFFIX;
                }
            }
        } else {
            sqlMessage = NO_SELECTION;
        }
        sqlTextViewer.getDocument().set(text);

        setSqlGroupMessage(sqlMessage);

    }

    /**
     * Creates the panel where the user specifies the text to search for and the optional replacement text.
     * 
     * @param parent the parent composite
     * @return the input panel
     */
    private Composite createInputPanel( Composite parent ) {

        Composite panel = WidgetFactory.createGroup(parent, INPUT, SWT.NULL | SWT.FILL, 1, 2);
        panel.setSize(300, 400);
        GridData panelGD = new GridData(GridData.FILL_HORIZONTAL);
        panelGD.grabExcessHorizontalSpace = true;
        // panelGD.grabExcessVerticalSpace = true;
        panel.setLayoutData(panelGD);

        Label findLabel = new Label(panel, SWT.LEFT);
        findLabel.setText(FIND);
        setGridData(findLabel, GridData.BEGINNING, false, GridData.CENTER, false);

        fFindField = new Combo(panel, SWT.DROP_DOWN | SWT.BORDER);
        setGridData(fFindField, GridData.FILL, true, GridData.CENTER, false);
        fFindField.addModifyListener(this);

        SelectionListener selectionListener = new SelectionListener() {
            @Override
			public void widgetSelected( SelectionEvent e ) {
                searchHelper.setCaseSensitive(fCaseCheckBox.getSelection());
            }

            @Override
			public void widgetDefaultSelected( SelectionEvent e ) {
                searchHelper.setCaseSensitive(fCaseCheckBox.getSelection());
            }
        };

        fCaseCheckBox = WidgetFactory.createButton(panel, CASE_SENSITIVE, GridData.BEGINNING, 2, SWT.CHECK | SWT.LEFT);
        // fCaseCheckBox.setText("Case Sensitive");
        // setGridData(fCaseCheckBox, GridData.BEGINNING, false, GridData.CENTER, false);
        fCaseCheckBox.setSelection(false);
        fCaseCheckBox.addSelectionListener(selectionListener);

        performSearchButton = WidgetFactory.createButton(parent, PERFORM_SEARCH, GridData.BEGINNING, 1, SWT.NONE);
        SelectionListener searchButtonListener = new SelectionListener() {
            @Override
			public void widgetSelected( SelectionEvent e ) {
                performSearch();
                updateFindHistory();
            }

            @Override
			public void widgetDefaultSelected( SelectionEvent e ) {
            }
        };
        performSearchButton.addSelectionListener(searchButtonListener);

        updateFindHistory();

        return panel;
    }

    public TreeViewer createResultsTreeViewer( Composite parent ) {
        TreeViewer viewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

        viewer.setContentProvider(new AbstractTreeContentProvider() {

            @Override
            public Object[] getChildren( Object parentElement ) {
                if (parentElement instanceof EObject) {
                    // Get T-Root and check for SUID
                    // Create SUIDObjects for each sql type
                    EObject mRootTarget = (EObject)parentElement;

                    SqlTransformationMappingRoot tRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(mRootTarget);
                    boolean supportsUpdates = false;

                    SqlAspect sqlAspect = org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(mRootTarget);
                    if (sqlAspect != null && (sqlAspect instanceof SqlTableAspect)) {
                        supportsUpdates = ((SqlTableAspect)sqlAspect).supportsUpdate(mRootTarget);
                    }
                    List suidList = new ArrayList(4);
                    suidList.add(new SUIDObject(mRootTarget, QueryValidator.SELECT_TRNS));

                    if (supportsUpdates) {
                        if (TransformationSearchHelper.hasUserSql(tRoot, QueryValidator.INSERT_TRNS)) {
                            suidList.add(new SUIDObject(mRootTarget, QueryValidator.INSERT_TRNS));
                        }

                        if (TransformationSearchHelper.hasUserSql(tRoot, QueryValidator.UPDATE_TRNS)) {
                            suidList.add(new SUIDObject(mRootTarget, QueryValidator.UPDATE_TRNS));
                        }

                        if (TransformationSearchHelper.hasUserSql(tRoot, QueryValidator.DELETE_TRNS)) {
                            suidList.add(new SUIDObject(mRootTarget, QueryValidator.DELETE_TRNS));
                        }
                    }
                    return suidList.toArray();
                }

                return new Object[0];
            }

            /**
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             * @since 5.0
             */
            @Override
            public Object[] getElements( Object theInputElement ) {
                return (Object[])theInputElement;
            }

            /**
             * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
             * @since 5.0
             */
            @Override
			public Object getParent( Object element ) {
                if (element instanceof SUIDObject) {
                    return ((SUIDObject)element).getParent();
                }
                return null;
            }

            /**
             * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
             * @since 5.0
             */
            @Override
            public boolean hasChildren( Object element ) {
                if (element instanceof EObject) {
                    return true;
                }

                return false;
            }

            /**
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             * @since 5.0
             */
            @Override
            public void inputChanged( Viewer v,
                                      Object oldInput,
                                      Object newInput ) {
            }

            /**
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             * @since 5.0
             */
            @Override
            public void dispose() {
            }
        });

        viewer.getTree().addTraverseListener(new TraverseListener() {

            @Override
			public void keyTraversed( TraverseEvent event ) {
                if (event.keyCode == SWT.ESC) {
                    event.doit = false;
                    ((Tree)event.widget).getShell().setVisible(false);
                }
            }

        });

        viewer.setLabelProvider(new MyLabelProvider());

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
			public void selectionChanged( SelectionChangedEvent theEvent ) {
                StructuredSelection selection = (StructuredSelection)theEvent.getSelection();
                if (selection.getFirstElement() instanceof EObject) {
                    parentDialog.setEditEnabled(true);
                    lastSelectedTarget = (EObject)selection.getFirstElement();
                } else if (selection.getFirstElement() instanceof SUIDObject) {
                    parentDialog.setEditEnabled(true);
                    lastSelectedTarget = ((SUIDObject)selection.getFirstElement()).getParent();
                } else {
                    parentDialog.setEditEnabled(false);
                    lastSelectedTarget = null;
                }

            }
        });
        return viewer;
    }

    public EObject getLastSelectedTarget() {
        return lastSelectedTarget;
    }

    /**
     * Updates the given combo with the given content.
     * 
     * @param combo combo to be updated
     * @param content to be put into the combo
     */
    private void updateCombo( Combo combo,
                              List content ) {
        combo.removeAll();
        for (int i = 0; i < content.size(); i++) {
            combo.add(content.get(i).toString());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     */
    @Override
	public void modifyText( ModifyEvent e ) {

        if (fFindField.getText().equals(CoreStringUtil.Constants.EMPTY_STRING)) { 
            // empty selection
            // CLEAR
        } else {
            // performSearch();
        }

        if (getFindString() == null || getFindString().length() == 0) {
            setMessage(NO_STRING_ENTERED);
            performSearchButton.setEnabled(false);
        } else {
            performSearchButton.setEnabled(true);
            setMessage(getString("TransformationSearchPanel.changedString", getFindString())); //$NON-NLS-1$
        }
    }

    void performSearch() {
        final String findString = getFindString();
        lastResults = Collections.EMPTY_LIST;

        if (findString != null && findString.length() > 0) {
            Collection results = Collections.EMPTY_LIST;

            try {
                results = searchHelper.findTransformationTargets(findString);
            } catch (CoreException theException) {
                UiConstants.Util.log(IStatus.ERROR, theException.getMessage());
            }

            searchResultsTreeViewer.setInput(results.toArray());

            lastResults = new ArrayList(results);
            lastSearchString = findString;

            updateState();

            setMessage(getString("TransformationSearchPanel.searchComplete", lastSearchString)); //$NON-NLS-1$

            if (lastResults.isEmpty()) {
                setSqlTextMessage(getString("TransformationSearchPanel.noMatches", lastSearchString)); //$NON-NLS-1$
            } else {
                String sizeStr = CoreStringUtil.Constants.EMPTY_STRING + lastResults.size();
                setSqlTextMessage(getString("TransformationSearchPanel.matchesFound", sizeStr, lastSearchString)); //$NON-NLS-1$
            }

            parentDialog.setEditEnabled(false);
        }
    }

    public ISelection getViewerSelection() {
        return searchResultsTreeViewer.getSelection();
    }

    /**
     * Retrieves the string to search for from the appropriate text input field and returns it.
     * 
     * @return the search string
     */
    private String getFindString() {
        return fFindField.getText();
    }

    private void updateState() {
        performSearchButton.setEnabled(true);
        // Check for string == NULL or EMPTY
        if (getFindString() == null || getFindString().length() == 0) {
            setMessage(NO_STRING_ENTERED);
            performSearchButton.setEnabled(false);
        } else {
            setMessage(CoreStringUtil.Constants.EMPTY_STRING);
        }

    }

    private void setSqlTextMessage( final String message ) {

        Display.getCurrent().asyncExec(new Runnable() {
            @Override
			public void run() {
                if (sqlMessageLabel != null && !sqlMessageLabel.isDisposed()) {
                    sqlMessageLabel.setText(message);
                    sqlMessageLabel.redraw();
                }
            }
        });
    }

    private void setSqlGroupMessage( final String message ) {

        Display.getCurrent().asyncExec(new Runnable() {
            @Override
			public void run() {
                if (sqlTextPanel != null && !sqlTextPanel.isDisposed()) {
                    sqlTextPanel.setText(message);
                    sqlTextPanel.redraw();
                }
            }
        });
    }

    private void setMessage( final String message ) {

        Display.getCurrent().asyncExec(new Runnable() {
            @Override
			public void run() {
                if (messageLabel != null && !messageLabel.isDisposed()) {
                    if (message == null) {
                        messageLabel.setImage(null);
                    }
                    messageLabel.setText(message);
                    messageLabel.redraw();
                }
            }
        });
    }

    /**
     * Attaches the given layout specification to the <code>component</code>.
     * 
     * @param component the component
     * @param horizontalAlignment horizontal alignment
     * @param grabExcessHorizontalSpace grab excess horizontal space
     * @param verticalAlignment vertical alignment
     * @param grabExcessVerticalSpace grab excess vertical space
     */
    private void setGridData( Control component,
                              int horizontalAlignment,
                              boolean grabExcessHorizontalSpace,
                              int verticalAlignment,
                              boolean grabExcessVerticalSpace ) {
        GridData gd = new GridData();
        gd.horizontalAlignment = horizontalAlignment;
        gd.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
        gd.verticalAlignment = verticalAlignment;
        gd.grabExcessVerticalSpace = grabExcessVerticalSpace;
        component.setLayoutData(gd);
    }

    /**
     * Called after executed find action to update the history.
     */
    void updateFindHistory() {
        fFindField.removeModifyListener(this);
        updateHistory(fFindField, fFindHistory);
        fFindField.addModifyListener(this);
    }

    /**
     * Updates the combo with the history.
     * 
     * @param combo to be updated
     * @param history to be put into the combo
     */
    private void updateHistory( Combo combo,
                                List history ) {
        String findString = combo.getText();
        int index = history.indexOf(findString);
        if (index != 0) {
            if (index != -1) {
                history.remove(index);
            }
            history.add(0, findString);
            updateCombo(combo, history);
            combo.setText(findString);
        }
    }

    private class SUIDObject {
        public static final String SELECT_STR = "SELECT"; //$NON-NLS-1$
        public static final String INSERT_STR = "INSERT"; //$NON-NLS-1$
        public static final String UPDATE_STR = "UPDATE"; //$NON-NLS-1$
        public static final String DELETE_STR = "DELETE"; //$NON-NLS-1$

        private int suidValue = QueryValidator.SELECT_TRNS;
        private String suidLabel = SELECT_STR;

        private EObject parentEObject;

        public SUIDObject( EObject eObject,
                           int suidID ) {
            super();

            parentEObject = eObject;

            suidValue = suidID;

            // Set the suid STRING to be used by label provider
            switch (suidID) {
                case QueryValidator.SELECT_TRNS: {
                    suidLabel = SELECT_STR;
                }
                    break;

                case QueryValidator.INSERT_TRNS: {
                    suidLabel = INSERT_STR;
                }
                    break;

                case QueryValidator.UPDATE_TRNS: {
                    suidLabel = UPDATE_STR;
                }
                    break;

                case QueryValidator.DELETE_TRNS: {
                    suidLabel = DELETE_STR;
                }
                    break;

                default: {
                    suidLabel = SELECT_STR;
                }
                    break;
            }
        }

        public String getLabel() {
            return suidLabel;
        }

        public EObject getParent() {
            return parentEObject;
        }

        public int getSqlType() {
            return suidValue;
        }
    }

    class MyLabelProvider extends SelectModelObjectLabelProvider {

        public MyLabelProvider() {
            super();
        }

        @Override
        public String getText( Object theElement ) {
            if (theElement instanceof EObject) {
                EObject eo = (EObject)theElement;
                String sText = ModelerCore.getModelEditor().getName(eo);
                if (showPath) {
                    String type = getType(eo);
                    if (type != null) {
                        sText += CoreStringUtil.Constants.SPACE + type + CoreStringUtil.Constants.SPACE;
                    }
                    String path = getAppendedPath(eo);
                    if (path != null) {
                        sText += " : " + getAppendedPath(eo); //$NON-NLS-1$
                    }
                }
                return sText;
            } else if (theElement instanceof SUIDObject) {
                return ((SUIDObject)theElement).getLabel();
            }
            return super.getText(theElement);
        }

        private String getType( EObject eObj ) {
            if (TransformationHelper.isStagingTable(eObj)) {
                return STAGING_TABLE_STR;
            } else if (TransformationHelper.isMappingClass(eObj)) {
                return MAPPING_CLASS_STR;
            } else if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(eObj)) {
                return PROCEDURE_STR;
            } else if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isTable(eObj)) {
                return TABLE_STR;
            }
            return null;
        }

        private String getAppendedPath( EObject eObj ) {
            if (TransformationHelper.isMappingClass(eObj)) {
                MappingClass mc = (MappingClass)eObj;
                EObject doc = mc.getMappingClassSet().getTarget();
                IPath pathToDoc = ModelerCore.getModelEditor().getFullPathToParent(doc);
                pathToDoc = pathToDoc.append(ModelerCore.getModelEditor().getName(doc));
                return pathToDoc.toString();
            }

            return ModelerCore.getModelEditor().getFullPathToParent(eObj).toString();
        }

    }
}
