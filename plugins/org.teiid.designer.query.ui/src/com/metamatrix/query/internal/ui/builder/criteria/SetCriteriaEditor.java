/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.ExpressionBuilder;
import com.metamatrix.query.internal.ui.builder.actions.AddSetCriteriaItemAction;
import com.metamatrix.query.internal.ui.builder.actions.DeleteSetCriteriaItemAction;
import com.metamatrix.query.internal.ui.builder.actions.EditSetCriteriaItemAction;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener;
import com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent;
import com.metamatrix.query.internal.ui.builder.model.SetCriteriaEditorModel;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.AbstractSetCriteria;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.lang.SetCriteria;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.visitor.SQLStringVisitor;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;

public class SetCriteriaEditor extends AbstractPredicateCriteriaTypeEditor {
    private final static String PREFIX = I18nUtil.getPropertyPrefix(SetCriteriaEditor.class);
    private final static int HORIZONTAL_GAP_BETWEEN_BUTTONS = 7;

    private SetCriteria setCriteria;
    private CriteriaExpressionEditor editor;
    private Control component;
    SetCriteriaEditorModel theModel;
    private ViewController viewController;
    Button listButton;
    private MenuManager listButtonMenuManager;
    private AddSetCriteriaItemAction addAction;
    private EditSetCriteriaItemAction editAction;
    private DeleteSetCriteriaItemAction deleteAction;
    Button subqueryButton;
    private Composite stackedComposite;
    private Composite listComposite;
    private SashForm subquerySashForm;
    private Text subquerySQLText;
    private TreeViewer subqueryTreeViewer;
    private Tree subqueryTree;
    private StackLayout stackLayout;
    private List curItemsList;
    private Map listItemToLangObjMap = new HashMap();
    private LanguageObject[] objectsToSelect = new LanguageObject[] {};
    private int curType = -1; // SetCriteriaEditorModel.LIST or

    // SetCriteriaEditorModel.SUBQUERY.
    // -1 is initial value.

    public SetCriteriaEditor( Composite parent,
                              SetCriteriaEditorModel model ) {
        super(parent, AbstractSetCriteria.class, model);
        this.theModel = model;
        this.viewController = new ViewController();
        theModel.addModelListener(viewController);
        this.viewController.initialize();
    }

    @Override
    public String getToolTipText() {
        String tip = Util.getString(PREFIX + "toolTipText"); //$NON-NLS-1$
        return tip;
    }

    @Override
    public String getTitle() {
        String title = Util.getString(PREFIX + "title"); //$NON-NLS-1$
        return title;
    }

    public Control createLeftComponent( Composite parent ) {
        editor = new CriteriaExpressionEditor(parent, theModel.getExpressionModel());
        component = editor.getUi();
        return component;
    }

    public Control createRightComponent( Composite parent ) {
        ViewForm rightComponentViewForm = new ViewForm(parent, SWT.BORDER);
        Composite rightComponent = new Composite(rightComponentViewForm, SWT.NONE);
        rightComponentViewForm.setContent(rightComponent);
        GridLayout layout = new GridLayout();
        rightComponent.setLayout(layout);
        Composite topButtonPanel = new Composite(rightComponent, SWT.NONE);
        RowLayout topButtonPanelLayout = new RowLayout();
        topButtonPanelLayout.wrap = false;
        topButtonPanelLayout.pack = true;
        topButtonPanelLayout.justify = false;
        topButtonPanelLayout.type = SWT.HORIZONTAL;
        topButtonPanelLayout.spacing = HORIZONTAL_GAP_BETWEEN_BUTTONS;
        topButtonPanel.setLayout(topButtonPanelLayout);
        GridData topButtonPanelGridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        topButtonPanel.setLayoutData(topButtonPanelGridData);
        listButton = new Button(topButtonPanel, SWT.RADIO);
        String listButtonText = Util.getString(PREFIX + "list"); //$NON-NLS-1$		
        listButton.setText(listButtonText);
        String listButtonToolTipText = Util.getString(PREFIX + "listToolTipText"); //$NON-NLS-1$
        listButton.setToolTipText(listButtonToolTipText);
        listButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                if (listButton.getSelection()) {
                    theModel.setCurType(SetCriteriaEditorModel.LIST);
                }
            }
        });
        subqueryButton = new Button(topButtonPanel, SWT.RADIO);
        String subqueryButtonText = Util.getString(PREFIX + "subquery"); //$NON-NLS-1$
        subqueryButton.setText(subqueryButtonText);
        String subqueryButtonToolTipText = Util.getString(PREFIX + "subqueryToolTipText"); //$NON-NLS-1$
        subqueryButton.setToolTipText(subqueryButtonToolTipText);
        subqueryButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                if (subqueryButton.getSelection()) {
                    theModel.setCurType(SetCriteriaEditorModel.SUBQUERY);
                }
            }
        });
        stackedComposite = new Composite(rightComponent, SWT.NONE);
        GridData stackedCompositeGridData = new GridData(GridData.FILL_BOTH);
        stackedComposite.setLayoutData(stackedCompositeGridData);
        stackLayout = new StackLayout();
        stackedComposite.setLayout(stackLayout);
        listComposite = new Composite(stackedComposite, SWT.NONE);
        subquerySashForm = new SashForm(stackedComposite, SWT.VERTICAL);
        GridLayout listCompositeLayout = new GridLayout();
        listComposite.setLayout(listCompositeLayout);
        curItemsList = new List(listComposite, SWT.MULTI);
        GridData curItemsListGridData = new GridData(GridData.FILL_BOTH);
        curItemsList.setLayoutData(curItemsListGridData);
        curItemsList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                curItemsListChanged();
            }
        });
        // Override setEnabled() to prevent problems of WidgetUtil.setEnabled() disabling these
        // buttons.
        Composite buttonsPanel = new Composite(listComposite, SWT.NONE) {
            @Override
            public void setEnabled( boolean enabled ) {
                super.setEnabled(enabled);
                curItemsListChanged();
            }
        };

        RowLayout buttonsPanelLayout = new RowLayout();
        buttonsPanelLayout.wrap = false;
        buttonsPanelLayout.pack = false;
        buttonsPanelLayout.justify = false;
        buttonsPanelLayout.type = SWT.HORIZONTAL;
        buttonsPanelLayout.spacing = HORIZONTAL_GAP_BETWEEN_BUTTONS;
        buttonsPanel.setLayout(buttonsPanelLayout);
        GridData buttonsPanelGridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        buttonsPanel.setLayoutData(buttonsPanelGridData);
        Runnable addRunnable = new Runnable() {
            public void run() {
                addButtonPressed();
            }
        };
        addAction = new AddSetCriteriaItemAction(buttonsPanel, addRunnable);
        addAction.setEnabled(true);
        Runnable editRunnable = new Runnable() {
            public void run() {
                editButtonPressed();
            }
        };
        editAction = new EditSetCriteriaItemAction(buttonsPanel, editRunnable);
        editAction.setEnabled(false);
        Runnable deleteRunnable = new Runnable() {
            public void run() {
                deleteButtonPressed();
            }
        };
        deleteAction = new DeleteSetCriteriaItemAction(buttonsPanel, deleteRunnable);
        deleteAction.setEnabled(false);

        listButtonMenuManager = new MenuManager();
        listButtonMenuManager.setRemoveAllWhenShown(true);
        listButtonMenuManager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow( IMenuManager menuMgr ) {
                fillListButtonMenu();
            }
        });

        listButton.setMenu(listButtonMenuManager.createContextMenu(listButton));

        subquerySQLText = new Text(subquerySashForm, SWT.MULTI | SWT.WRAP | SWT.BORDER);
        GridData sqlTextData = new GridData(GridData.FILL_HORIZONTAL);
        subquerySQLText.setBackground(UiUtil.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        subquerySQLText.setForeground(UiUtil.getSystemColor(SWT.COLOR_INFO_FOREGROUND));

        subquerySQLText.setLayoutData(sqlTextData);
        subqueryTreeViewer = ElementViewerFactory.createElementViewer(subquerySashForm);
        subqueryTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent theEvent ) {
                handleDoubleClick();
            }
        });
        theModel.setViewer(subqueryTreeViewer);

        subqueryTree = subqueryTreeViewer.getTree();
        subqueryTree.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent theEvent ) {
                handleTreeSelection();
            }

            public void widgetSelected( SelectionEvent theEvent ) {
                handleTreeSelection();
            }

        });
        GridData treeData = new GridData(GridData.FILL_BOTH);
        subqueryTree.setLayoutData(treeData);

        subquerySashForm.setWeights(new int[] {1, 3});

        return rightComponentViewForm;
    }

    void fillListButtonMenu() {
        listButtonMenuManager.add(addAction);
        listButtonMenuManager.add(editAction);
        listButtonMenuManager.add(deleteAction);
    }

    private void listTypeSelected() {
        if (listButton.getSelection()) {
            if (subqueryButton.getSelection()) {
                subqueryButton.setSelection(false);
            }
            stackLayout.topControl = listComposite;
            stackedComposite.layout();
            theModel.setCurType(SetCriteriaEditorModel.LIST);
        } else if (!subqueryButton.getSelection()) {
            // Do not allow user to unset list button if subquery button not set
            listButton.setSelection(true);
        }
    }

    private void subqueryTypeSelected() {
        if (subqueryButton.getSelection()) {
            if (listButton.getSelection()) {
                listButton.setSelection(false);
            }
            stackLayout.topControl = subquerySashForm;
            stackedComposite.layout();
            theModel.setCurType(SetCriteriaEditorModel.SUBQUERY);
        } else if (!listButton.getSelection()) {
            // Do not allow user to unset subquery button is list button not set
            subqueryButton.setSelection(true);
        }
    }

    void curItemsListChanged() {
        // Add button always stays enabled
        // Edit button enabled iff. exactly one item selected
        // Delete button enabled iff. one or more items selected
        int selectionCount = curItemsList.getSelectionCount();
        editAction.setEnabled((selectionCount == 1));
        deleteAction.setEnabled((selectionCount > 0));
    }

    void addButtonPressed() {
        Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        ExpressionBuilder expBld = new ExpressionBuilder(shell);
        int returnCode = expBld.open();
        if (returnCode == Window.OK) {
            // Do not need to change either curItemsList or listItemToLangObjMap. We will do this when
            // we receive an event telling us that the values have been changed.
            LanguageObject langObj = expBld.getLanguageObject();
            objectsToSelect = new LanguageObject[] {langObj};
            theModel.addValue(langObj);
        }
    }

    void editButtonPressed() {
        // Can only be one item selected or edit button is disabled.
        String listItem = curItemsList.getSelection()[0];
        LanguageObject obj = (LanguageObject)listItemToLangObjMap.get(listItem);
        Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        ExpressionBuilder expBld = new ExpressionBuilder(shell);

        // Not certain why a create() call is necessary here but it seems to be. BWP 08/26/03
        expBld.create();
        expBld.setLanguageObject(obj);
        int returnCode = expBld.open();
        if (returnCode == Window.OK) {
            LanguageObject newObj = expBld.getLanguageObject();
            if (!obj.equals(newObj)) {
                // Do not need to change either curItemsList or
                // listItemToLangObjMap. We will do this when we receive an event
                // telling us that the values have been changed.
                objectsToSelect = new LanguageObject[] {newObj};
                theModel.replaceValue(obj, newObj);
            }
        }
    }

    void deleteButtonPressed() {
        // We will replace the entire set of values in the model, with those not selected in curItemsList
        int[] selectedIndices = curItemsList.getSelectionIndices();
        int itemCount = curItemsList.getItemCount();
        boolean[] stillIncludedMask = new boolean[itemCount];
        for (int i = 0; i < itemCount; i++) {
            stillIncludedMask[i] = true;
        }
        for (int i = 0; i < selectedIndices.length; i++) {
            stillIncludedMask[selectedIndices[i]] = false;
        }
        // Collect up only those items that were not selected in curItemsList
        Collection newValues = new ArrayList(itemCount);
        for (int i = 0; i < itemCount; i++) {
            if (stillIncludedMask[i]) {
                String listItem = curItemsList.getItem(i);
                LanguageObject obj = (LanguageObject)listItemToLangObjMap.get(listItem);
                newValues.add(obj);
            }
        }
        // Do not need to change either curItemsList or listItemToLangObjMap. We will do this when
        // we receive an event telling us that the values have been changed.
        objectsToSelect = new LanguageObject[] {};
        theModel.setValues(newValues);
    }

    public Expression getLeftExpression() {
        Expression leftExpression = null;
        if (setCriteria != null) {
            leftExpression = setCriteria.getExpression();
        }
        return leftExpression;
    }

    public Expression getRightExpression() {
        // Unused
        return null;
    }

    @Override
    public void setLanguageObject( LanguageObject obj ) {
        CoreArgCheck.isInstanceOf(SetCriteria.class, obj);
        setCriteria = (SetCriteria)obj;
        editor.setLanguageObject(getLeftExpression());
    }

    public String[] getOperators() {
        return theModel.getOperators();
    }

    @Override
    public void acceptFocus() {
        editor.acceptFocus();
    }

    public void setOperator( String op ) {
        theModel.setCurrentOperator(op);
    }

    public String getCurrentOperator() {
        return theModel.getCurrentOperator();
    }

    void handleDoubleClick() {
        // Is there anything to do here?
    }

    void handleTreeSelection() {
        IStructuredSelection selection = (IStructuredSelection)subqueryTreeViewer.getSelection();
        Object firstSelection = selection.getFirstElement();
        theModel.setSubquerySelection(firstSelection);
    }

    void displayExpression() {
    }

    void displayValues() {
        if (curType == SetCriteriaEditorModel.LIST) {
            curItemsList.removeAll();
            listItemToLangObjMap.clear();
            SetCriteria setCriteria = (SetCriteria)theModel.getLanguageObject();
            Collection newValues = setCriteria.getValues();
            // Put the list of values into curItemsList and into listItemToLangObjMap
            Iterator it = newValues.iterator();
            while (it.hasNext()) {
                LanguageObject langObj = (LanguageObject)it.next();
                String itemName = langObj.toString();
                curItemsList.add(itemName);
                listItemToLangObjMap.put(itemName, langObj);
            }
            // Now select those items in the list that we have already flagged
            Collection selectionLocs = new ArrayList();
            for (int i = 0; i < objectsToSelect.length; i++) {
                int index = curItemsList.indexOf(objectsToSelect[i].toString());
                if (index >= 0) {
                    selectionLocs.add(new Integer(index));
                }
            }
            int[] selectionLocsArray = new int[selectionLocs.size()];
            it = selectionLocs.iterator();
            for (int i = 0; it.hasNext(); i++) {
                Integer tempInt = (Integer)it.next();
                selectionLocsArray[i] = tempInt.intValue();
            }
            curItemsList.select(selectionLocsArray);
            curItemsListChanged();
        }
    }

    void displayType() {
        int newType = theModel.getCurType();
        if (newType != curType) {
            curType = newType;
            if (curType == SetCriteriaEditorModel.LIST) {
                if (!listButton.getSelection()) {
                    listButton.setSelection(true);
                }
                listTypeSelected();
            } else { // must be SUBQUERY
                if (!subqueryButton.getSelection()) {
                    subqueryButton.setSelection(true);
                }
                subqueryTypeSelected();
            }
        }
    }

    void displayCommand() {
        Command command = theModel.getCommand();
        String displayText;
        if (command == null) {
            Object selection = theModel.getSubquerySelection();
            if (selection == null) {
                displayText = Util.getString(PREFIX + "noSelectionMsg"); //$NON-NLS-1$
            } else {
                displayText = theModel.getInvalidSelectionMessage();
            }
        } else {
            displayText = SQLStringVisitor.getSQLString(command);
        }
        subquerySQLText.setText(displayText);
    }

    void displayLanguageObjectChange() {
        displayType();
        displayExpression();
        displayValues();
        displayCommand();
    }

    /**
     * The <code>ViewController</code> class is a view controller for the <code>FunctionEditor</code>.
     */
    private class ViewController implements ILanguageObjectEditorModelListener {
        public ViewController() {
            super();
        }

        public void initialize() {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    modelChanged(new LanguageObjectEditorModelEvent(theModel, LanguageObjectEditorModelEvent.SAVED));
                }
            });
        }

        /**
         * @see com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModelListener#modelChanged(com.metamatrix.query.internal.ui.builder.model.LanguageObjectEditorModelEvent)
         */
        public void modelChanged( LanguageObjectEditorModelEvent theEvent ) {
            String type = theEvent.getType();
            if (type.equals(SetCriteriaEditorModel.EXPRESSION)) {
                displayExpression();
            } else if (type.equals(SetCriteriaEditorModel.VALUES)) {
                displayValues();
            } else if (type.equals(SetCriteriaEditorModel.COMMAND)) {
                displayCommand();
            } else if (type.equals(SetCriteriaEditorModel.SUBTYPE_CHANGED)) {
                displayType();
            } else if (type.equals(LanguageObjectEditorModelEvent.SAVED)) {
                displayLanguageObjectChange();
            }
        }
    }
}// end SetCriteriaEditor
