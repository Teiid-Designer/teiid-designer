/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.query.internal.ui.builder.actions.DeleteViewerObjectAction;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener;
import com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent;
import com.metamatrix.query.internal.ui.builder.util.BuilderUtils;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.visitor.SQLStringVisitor;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.builder.ILanguageObjectEditor;
import com.metamatrix.query.ui.sqleditor.SqlDisplayPanel;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * AbstractLanguageObjectBuilder
 */
public abstract class AbstractLanguageObjectBuilder extends Dialog implements UiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(AbstractLanguageObjectBuilder.class);

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private IAction deleteAction;

    private ILanguageObjectEditor editor;

    private LanguageObject savedSelection;

    protected LanguageObject savedLangObj;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROLS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    protected Button btnSet;

    protected Button btnReset;

    private Label lblTitle;

    private Composite pnlEditor;

    private Composite pnlEditorDetail;

    LanguageObjectBuilderTreeViewer treeViewer;

    private SqlDisplayPanel currentSql;

    private SqlDisplayPanel originalSql;

    private CLabel originalLanguageObjLabel;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    protected AbstractLanguageObjectBuilder( Shell theParent,
                                             String theTitle ) {
        super(theParent, theTitle);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void create() {
        super.create();

        editor = createEditor(pnlEditorDetail);
        editor.getModel().addModelListener(new ILanguageObjectEditorModelListener() {
            public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
                handleModelChanged();
            }
        });

        lblTitle.setText(editor.getTitle());

        Composite pnlEditorButtons = new Composite(pnlEditor, SWT.NONE);
        pnlEditorButtons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
        pnlEditorButtons.setLayout(new GridLayout());

        //
        // pnlEditorButtons contents
        //

        btnSet = new Button(pnlEditorButtons, SWT.NONE);
        btnSet.setEnabled(false);
        btnSet.setText(Util.getString(PREFIX + "btnSet")); //$NON-NLS-1$
        btnSet.setToolTipText(Util.getString(PREFIX + "btnSet.tip")); //$NON-NLS-1$
        btnSet.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleSetSelected();
            }
        });

        btnReset = new Button(pnlEditorButtons, SWT.NONE);
        btnReset.setEnabled(false);
        btnReset.setText(Util.getString(PREFIX + "btnReset")); //$NON-NLS-1$
        btnReset.setToolTipText(Util.getString(PREFIX + "btnReset.tip")); //$NON-NLS-1$
        btnReset.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleResetSelected();
            }
        });

        setLanguageObject(null); // needed to establish the viewer input

        // select root tree node after all construction is finished
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                treeViewer.selectRoot();
            }
        });
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite theParent ) {
        originalLanguageObjLabel = WidgetFactory.createLabel(theParent, StringUtil.Constants.EMPTY_STRING);
        Composite pnlContents = (Composite)super.createDialogArea(theParent);

        //
        // main panel contents
        //

        CTabFolder tabFolder = WidgetFactory.createTabFolder(pnlContents);

        //
        // tabFolder contents - 2 tabs (Tree, SQL Text), each with a splitter
        //

        CTabItem treeTab = WidgetFactory.createTab(tabFolder, Util.getString(PREFIX + "treeTab")); //$NON-NLS-1$
        treeTab.setToolTipText(Util.getString(PREFIX + "treeTab.tip")); //$NON-NLS-1$

        SashForm treeTabSash = new SashForm(tabFolder, SWT.VERTICAL);
        treeTabSash.setLayoutData(new GridData(GridData.FILL_BOTH));
        treeTab.setControl(treeTabSash);

        CTabItem sqlTab = WidgetFactory.createTab(tabFolder, Util.getString(PREFIX + "sqlTab")); //$NON-NLS-1$
        sqlTab.setToolTipText(Util.getString(PREFIX + "sqlTab.tip")); //$NON-NLS-1$

        SashForm sqlTabSash = new SashForm(tabFolder, SWT.VERTICAL);
        sqlTabSash.setLayoutData(new GridData(GridData.FILL_BOTH));
        sqlTab.setControl(sqlTabSash);

        //
        // treeTab contents
        //

        ViewForm formTree = new ViewForm(treeTabSash, SWT.BORDER);
        Composite pnlTree = new Composite(formTree, SWT.NO_TRIM);
        formTree.setContent(pnlTree);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        pnlTree.setLayout(layout);
        pnlTree.setLayoutData(new GridData(GridData.FILL_BOTH));

        ViewForm formEditor = new ViewForm(treeTabSash, SWT.BORDER);
        pnlEditor = new Composite(formEditor, SWT.NO_TRIM);
        formEditor.setContent(pnlEditor);
        pnlEditor.setLayoutData(new GridData(GridData.FILL_BOTH));

        lblTitle = new Label(formEditor, SWT.CENTER);
        lblTitle.setBackground(BuilderUtils.COLOR_HIGHLIGHT);
        formEditor.setTopLeft(lblTitle);

        treeTabSash.setWeights(new int[] {30, 70});

        //
        // sqlTab contents
        //

        ViewForm formCurrentSql = new ViewForm(sqlTabSash, SWT.BORDER);
        ViewForm formOriginalSql = new ViewForm(sqlTabSash, SWT.BORDER);

        //
        // formCurrentSql contents
        //

        Composite pnlCurrentSql = new Composite(formCurrentSql, SWT.NONE);
        formCurrentSql.setContent(pnlCurrentSql);
        pnlCurrentSql.setLayout(new GridLayout());
        pnlCurrentSql.setLayoutData(new GridData(GridData.FILL_BOTH));

        currentSql = new SqlDisplayPanel(pnlCurrentSql);
        currentSql.setLayoutData(new GridData(GridData.FILL_BOTH));

        CLabel lblCurrent = new CLabel(formCurrentSql, SWT.NONE);
        lblCurrent.setBackground(BuilderUtils.COLOR_HIGHLIGHT);
        lblCurrent.setText(Util.getString(PREFIX + "lblCurrent")); //$NON-NLS-1$
        lblCurrent.setToolTipText(Util.getString(PREFIX + "lblCurrent.tip")); //$NON-NLS-1$
        formCurrentSql.setTopLeft(lblCurrent);

        //
        // formOriginalSql contents
        //

        Composite pnlOriginalSql = new Composite(formOriginalSql, SWT.NONE);
        formOriginalSql.setContent(pnlOriginalSql);
        pnlOriginalSql.setLayout(new GridLayout());
        pnlOriginalSql.setLayoutData(new GridData(GridData.FILL_BOTH));

        originalSql = new SqlDisplayPanel(pnlOriginalSql);
        originalSql.setLayoutData(new GridData(GridData.FILL_BOTH));

        CLabel lblOriginal = new CLabel(formOriginalSql, SWT.NONE);
        lblOriginal.setBackground(BuilderUtils.COLOR_HIGHLIGHT);
        lblOriginal.setText(Util.getString(PREFIX + "lblOriginal")); //$NON-NLS-1$
        lblOriginal.setToolTipText(Util.getString(PREFIX + "lblOriginal.tip")); //$NON-NLS-1$
        formOriginalSql.setTopLeft(lblOriginal);

        //
        // pnlTree contents - 2 columns (tree viewer, button panel)
        //

        layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        pnlTree.setLayout(layout);

        treeViewer = new LanguageObjectBuilderTreeViewer(pnlTree);
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleTreeSelection();
            }
        });

        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow( IMenuManager theMenuMgr ) {
                fillContextMenu(theMenuMgr);
            }
        });
        treeViewer.getTree().setMenu(menuMgr.createContextMenu(treeViewer.getTree()));

        Composite pnlButtons = new Composite(pnlTree, SWT.NONE);
        pnlButtons.setLayout(new GridLayout());

        createTreeButtons(pnlButtons);

        //
        // pnlEditor contents
        //

        layout = new GridLayout();
        layout.numColumns = 2;
        pnlEditor.setLayout(layout);

        pnlEditorDetail = new Composite(pnlEditor, SWT.NONE);
        pnlEditorDetail.setLayoutData(new GridData(GridData.FILL_BOTH));
        pnlEditorDetail.setLayout(new GridLayout());
        return pnlContents;
    }

    protected abstract ILanguageObjectEditor createEditor( Composite theParent );

    /**
     * Creates buttons that interact with the tree.
     * 
     * @param theParent the panel where the buttons are contained
     */
    protected void createTreeButtons( Composite theParent ) {
        Runnable deleteRunner = new Runnable() {
            public void run() {
                handleDeleteSelected();
            }
        };
        deleteAction = new DeleteViewerObjectAction(theParent, deleteRunner);
    }

    protected void fillContextMenu( IMenuManager theMenuMgr ) {
        theMenuMgr.add(deleteAction);
    }

    public ILanguageObjectEditor getEditor() {
        return editor;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.ILanguageObjectInputProvider#getLanguageObject()
     */
    public LanguageObject getLanguageObject() {
        return treeViewer.getLanguageObject();
    }

    /**
     * Gets the saved <code>LanguageObject</code>.
     * 
     * @return the saved <code>LanguageObject</code> or <code>null</code>
     */
    protected LanguageObject getSavedLanguageObject() {
        return savedLangObj;
    }

    /**
     * Gets a title for the builder. Title is appropriate for use as a dialog title.
     * 
     * @return the builder title
     */
    @Override
    public abstract String getTitle();

    protected LanguageObjectBuilderTreeViewer getTreeViewer() {
        return treeViewer;
    }

    protected void handleDeleteSelected() {
        editor.clear();
        treeViewer.deleteSelection();

        // update SQL text
        setCurrentSql(treeViewer.getLanguageObject());

        editor.acceptFocus();
    }

    void handleModelChanged() {
        boolean isEnabled = false;
        boolean isComplete = false;
        boolean hasChanged = false;
        isEnabled = editor.isEnabled();
        if (isEnabled) {
            isComplete = editor.isComplete();
        }
        if (isComplete) {
            hasChanged = editor.hasChanged();
        }
        boolean state = (isEnabled && isComplete && hasChanged);
        btnSet.setEnabled(state);
        btnReset.setEnabled(state);

        setCurrentSql(treeViewer.getLanguageObject());

        // set enable/disable status of buttons
        setEnabledStatus();
    }

    protected void handleResetSelected() {
        editor.reset();

        // put focus back on editor from the reset button
        editor.acceptFocus();
    }

    protected void handleSetSelected() {
        editor.save();

        // need the editor's language object in order to update tree.
        // the language object should never be null. if it was this handler should not have been called
        LanguageObject langObj = editor.getLanguageObject();
        if (langObj == null) {
            Assertion.isNotNull(langObj, Util.getString(PREFIX + "nullLangObj", //$NON-NLS-1$
                                                        new Object[] {"handleSetSelected"})); //$NON-NLS-1$
        }

        // update tree
        treeViewer.modifySelectedItem(langObj, false);

        // update SQL text
        setCurrentSql(treeViewer.getLanguageObject());

        // put focus back on editor from the set button
        editor.acceptFocus();
    }

    protected void handleTreeSelection() {
        IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
        Object selectedObj = selection.getFirstElement();

        if (selectedObj == null) {
            savedSelection = null;

            if (editor.isEnabled()) {
                editor.setEnabled(false);
            }
        } else {
            // selection with either be a LanguageObject or an Undefined object (String)
            savedSelection = (selectedObj instanceof LanguageObject) ? (LanguageObject)selectedObj : null;
        }

        setEditorLanguageObject(savedSelection);

        // set enable/disable status of buttons
        setEnabledStatus();
    }

    protected boolean isTreeSelectionRoot() {
        boolean isRoot = false;
        IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
        if (selection.size() > 0) {
            Tree tree = treeViewer.getTree();
            // We are assuming that the tree is single-selection.
            TreeItem treeItem = tree.getSelection()[0];
            TreeItem parentItem = treeItem.getParentItem();
            isRoot = (parentItem == null);
        }
        return isRoot;
    }

    protected void setCurrentSql( LanguageObject theLangObj ) {
        currentSql.setText(SQLStringVisitor.getSQLString(theLangObj));
    }

    protected void setEditorLanguageObject( LanguageObject theEditorLangObj ) {
        getEditor().setLanguageObject(savedSelection);

        if (!editor.isEnabled()) {
            editor.setEnabled(true);
        }
    }

    protected void setEnabledStatus() {
        //
        // set enabled status of delete button
        //

        boolean canDelete = treeViewer.canDeleteSelection();

        if (canDelete) {
            if (!deleteAction.isEnabled()) {
                deleteAction.setEnabled(true);
            }
        } else {
            if (deleteAction.isEnabled()) {
                deleteAction.setEnabled(false);
            }
        }

        //
        // set enabled status of OK button
        //

        Button btnOk = getButton(OK);
        boolean enable = treeViewer.isComplete();

        if (btnOk.isEnabled() != enable) {
            btnOk.setEnabled(enable);
        }
    }

    public void setLanguageObject( LanguageObject theLangObj ) {
        // language object must be cloned here so that the original isn't modified.
        // this prevents the original from being modified even if the user cancels out of the builder.
        LanguageObject langObj = (theLangObj == null) ? null : (LanguageObject)theLangObj.clone();

        savedLangObj = langObj;
        setOriginalSql(langObj);
        setCurrentSql(langObj);
        treeViewer.setLanguageObject(langObj);
        treeViewer.selectRoot();
        treeViewer.expandAll();

        // Defect 22003 - needed a context for the original expression
        // Providing a Lable at the top of the dialog.
        String labelText = Util.getString(PREFIX + "initialExpression") + //$NON-NLS-1$
                           StringUtil.Constants.DBL_SPACE + StringUtil.Constants.DBL_SPACE + Util.getString(PREFIX + "undefined"); //$NON-NLS-1$
        if (savedLangObj != null) {
            String loString = savedLangObj.toString();
            if (loString.length() > 50) {
                loString = loString.substring(0, 50) + "..."; //$NON-NLS-1$ 
            }
            labelText = Util.getString(PREFIX + "initialExpression") + //$NON-NLS-1$
                        StringUtil.Constants.DBL_SPACE + StringUtil.Constants.DBL_SPACE + loString;
        }
        if (originalLanguageObjLabel != null) {
            originalLanguageObjLabel.setText(labelText);
            originalLanguageObjLabel.getParent().layout();
        }
        // select root tree node
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                treeViewer.selectRoot();
            }
        });
    }

    protected void setOriginalSql( LanguageObject theLangObj ) {
        originalSql.setText(SQLStringVisitor.getSQLString(theLangObj));
    }

}
