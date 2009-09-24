/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.ui.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.DifferenceType;
import com.metamatrix.modeler.compare.ui.PluginConstants;
import com.metamatrix.modeler.compare.ui.UiConstants;
import com.metamatrix.modeler.compare.ui.UiPlugin;
import com.metamatrix.ui.graphics.GlobalUiFontManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.DefaultTreeViewerController;

/**
 * Panel to show the tree of changes, allowing for selection / deselection of parts of them.
 */
public class CompareTreePanel extends ViewForm /* Composite */
implements UiConstants, PluginConstants {

    private Tree tree;
    // private CheckboxTreeViewer treeViewer;
    private TreeViewer treeViewer;
    private List<DifferenceReport> diffReports;

    // default this to false:
    private boolean bDisplayOnlyPrimaryMetamodelObjects = false;
    private String title;
    private boolean updateMapping = true;
    // private ITreeViewerController controller;
    private CompareTreeController controller;
    private boolean showCheckBoxes;
    private ITreeContentProvider contentProvider;
    private PrimaryMetamodelObjectViewerFilter pmofViewerFilter;
    private int iAdditions;
    private int iChanges;
    private int iDeletions;
    private ArrayList<TreeItem> arylTreeItemDepthFirstList;
    private TreeItem tiCurrentSelection;
    private boolean bUpdatingSelection = false;

    private Button btnSelectAll;
    private Button btnUnselectAll;

    private ToolBar tbToolBar;
    private ToolBarManager tbmToolBarMgr;
    private Action actUp;
    private Action actDown;

    private static final String SELECT_ALL_TEXT = UiConstants.Util.getString("CompareTreePanel.selectAll.text"); //$NON-NLS-1$   

    private static final String SELECT_ALL_TIP = UiConstants.Util.getString("CompareTreePanel.selectAll.tip"); //$NON-NLS-1$   

    private static final String UNSELECT_ALL_TEXT = UiConstants.Util.getString("CompareTreePanel.unselectAll.text"); //$NON-NLS-1$   

    private static final String UNSELECT_ALL_TIP = UiConstants.Util.getString("CompareTreePanel.unselectAll.tip"); //$NON-NLS-1$   

    private static final String NEXT_NAV_TIP = UiConstants.Util.getString("CompareTreePanel.nextNav.tip"); //$NON-NLS-1$   

    private static final String PREV_NAV_TIP = UiConstants.Util.getString("CompareTreePanel.prevNav.tip"); //$NON-NLS-1$   

    private static final String GO_TO_NEXT_TITLE = UiConstants.Util.getString("CompareTreePanel.goToNext.title"); //$NON-NLS-1$   

    private static final String END_OF_DOC_MSG = UiConstants.Util.getString("CompareTreePanel.endOfDoc.msg"); //$NON-NLS-1$   

    private static final String BEGINNING_OF_DOC_MSG = UiConstants.Util.getString("CompareTreePanel.beginningOfDoc.msg"); //$NON-NLS-1$

    public CompareTreePanel( Composite parent,
                             String title,
                             List<DifferenceReport> diffReports,
                             boolean showCheckBoxes,
                             int terminology ) {
        this(parent, title, showCheckBoxes, false, terminology);
        setDifferenceReports(diffReports);
    }

    public CompareTreePanel( Composite parent,
                             String title,
                             List<DifferenceReport> diffReports,
                             int terminology ) {
        this(parent, title, true, false, terminology);
        setDifferenceReports(diffReports);
    }

    public CompareTreePanel( Composite parent,
                             String title,
                             DifferenceReport diffReport,
                             int terminology ) {
        this(parent, title, Collections.singletonList(diffReport), terminology);
    }

    public CompareTreePanel( Composite theParent,
                             String theTitle,
                             boolean showCheckBoxes,
                             boolean bDisplayOnlyPrimaryMetamodelObjects,
                             int terminology ) {
        super(theParent, SWT.NULL);

        this.title = theTitle;
        this.showCheckBoxes = showCheckBoxes;
        this.bDisplayOnlyPrimaryMetamodelObjects = bDisplayOnlyPrimaryMetamodelObjects;

        initialize(terminology);
    }

    /**
     */
    public void clearSelection() {
        this.tree.deselectAll();
    }

    public void selectAll() {
        this.tree.selectAll();
    }

    public TreeViewer getTreeViewer() {
        return this.treeViewer;
    }

    private void initialize( int terminology ) {

        // create title and make it bold
        CLabel lblTitle = WidgetFactory.createLabel(this, this.title);

        Font fOld = lblTitle.getFont();
        FontData data = fOld.getFontData()[0];
        data.setStyle(SWT.BOLD);
        Font fNewFont = GlobalUiFontManager.getFont(data);
        lblTitle.setFont(fNewFont);

        this.setTopLeft(lblTitle);

        // tree
        this.controller = new CompareTreeController();

        if (showCheckBoxes) {
            this.treeViewer = WidgetFactory.createTreeViewer(this, SWT.SINGLE | SWT.CHECK, GridData.FILL_BOTH, this.controller);
        } else {
            // create the treeviewer without the SWT.CHECK style
            this.treeViewer = WidgetFactory.createTreeViewer(this, SWT.SINGLE, GridData.FILL_BOTH, this.controller);
        }

        this.tree = this.treeViewer.getTree();
        this.setContent(tree);

        contentProvider = new MappingTreeContentProvider();

        if (diffReports != null && diffReports.get(0) != null) {
            ((MappingTreeContentProvider)contentProvider).setDifferenceReports(diffReports);
        }

        treeViewer.setContentProvider(contentProvider);

        ILabelProvider labelProvider = DifferenceAnalysis.getMappingDecoratingLabelProvider(terminology);

        // decorator needs to have a reference to the content provider. this is because the
        // content provider keeps information about description differences. descriptions are
        // being shown to the user as if they were a feature/property of the EObject.
        if (labelProvider instanceof DecoratingLabelProvider) {
            ILabelDecorator decorator = ((DecoratingLabelProvider)labelProvider).getLabelDecorator();

            if (decorator instanceof MappingLabelDecorator) {
                ((MappingLabelDecorator)decorator).setMappingTreeContentProvider((MappingTreeContentProvider)contentProvider);
            }
        }

        treeViewer.setLabelProvider(labelProvider);

        if (bDisplayOnlyPrimaryMetamodelObjects) {
            pmofViewerFilter = new PrimaryMetamodelObjectViewerFilter();
            treeViewer.addFilter(pmofViewerFilter);
        }

        // button panel
        if (showCheckBoxes) {
            createSelectUnSelectButtonPanel(this);
        }

        // create the up/down toolbar
        createToolBar();

        this.tree.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleTreeNodeSelection();
            }
        });

    }

    private void createSelectUnSelectButtonPanel( Composite parent ) {

        // create panel to hold all the buttons
        Composite pnlButtons = WidgetFactory.createPanel(parent, GridData.HORIZONTAL_ALIGN_CENTER);
        GridLayout gridLayout = new GridLayout();
        pnlButtons.setLayout(gridLayout);
        gridLayout.numColumns = 2;
        GridData gridData = new GridData(GridData.CENTER/* GridData.FILL_HORIZONTAL */);
        pnlButtons.setLayoutData(gridData);

        // select all button
        this.btnSelectAll = WidgetFactory.createButton(pnlButtons, SELECT_ALL_TEXT, GridData.FILL_HORIZONTAL);
        this.btnSelectAll.setEnabled(true);
        this.btnSelectAll.setToolTipText(SELECT_ALL_TIP);
        this.btnSelectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleSelectAll();
            }
        });

        // unselect all button
        this.btnUnselectAll = WidgetFactory.createButton(pnlButtons, UNSELECT_ALL_TEXT, GridData.FILL_HORIZONTAL);
        this.btnUnselectAll.setEnabled(true);
        this.btnUnselectAll.setToolTipText(UNSELECT_ALL_TIP);
        this.btnUnselectAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleUnselectAll();
            }
        });

    }

    public void selectFirstDiff() {

        // (calling getNext with a null current will retrieve the FIRST diff)
        TreeItem tiFirst = getNextDiffItem(null);

        if (tiFirst != null) {
            TreeItem[] tiArray = {tiFirst};
            getTreeViewer().reveal(tiFirst);
            forceTreeSelection(tiArray);
            setCurrentTreeItem(tiFirst);
        }
    }

    protected void createToolBar() {

        tbToolBar = new ToolBar(this, SWT.FLAT | SWT.WRAP);
        this.setTopRight(tbToolBar);
        tbmToolBarMgr = new PaneToolBarManager(tbToolBar);

        // == Down (next) Arrow
        actDown = new Action() {

            @Override
            public void run() {
                handleDownArrow();
            }
        };

        actDown.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.NEXT_NAV));
        actDown.setToolTipText(NEXT_NAV_TIP);
        tbmToolBarMgr.add(actDown);

        // == Up (previous) Arrow
        actUp = new Action() {

            @Override
            public void run() {
                handleUpArrow();
            }
        };
        actUp.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.PREV_NAV));
        actUp.setToolTipText(PREV_NAV_TIP);
        tbmToolBarMgr.add(actUp);

        tbmToolBarMgr.update(true);
    }

    void handleSelectAll() {
        setAllNodesSelected(true);
    }

    void handleUnselectAll() {
        setAllNodesSelected(false);
    }

    public void setDifferenceReports( List<DifferenceReport> theDifferenceReports ) {
        this.diffReports = theDifferenceReports;

        if (contentProvider != null) {
            ((MappingTreeContentProvider)contentProvider).setDifferenceReports(this.diffReports);
        }

        treeViewer.setInput(this.diffReports);

        initTreeSelections();
        generateStats();

        if ((getAdditionCount() == 0) && (getChangeCount() == 0) && (getDeletionCount() == 0)) {
            this.actDown.setEnabled(false);
            this.actUp.setEnabled(false);
        }
    }

    public void initTreeSelections() {

        // init this list. it will be used by the up/down arrow change navigation.
        arylTreeItemDepthFirstList = new ArrayList<TreeItem>();

        TreeItem[] items = tree.getItems();
        for (int i = 0; i < items.length; i++) {
            selectItemAndChildren(items[i]);
        }
    }

    public void generateStats() {

        // init counts
        iAdditions = 0;
        iChanges = 0;
        iDeletions = 0;

        getTreeViewer().expandAll();
        TreeItem[] items = tree.getItems();
        for (int i = 0; i < items.length; i++) {
            calculateCounts(items[i]);
        }
        getTreeViewer().collapseAll();
        // report the counts
        // System.out.println("[CompareTreePanel.generateStats] iAdditions: " + iAdditions ); //$NON-NLS-1$
        // System.out.println("[CompareTreePanel.generateStats] iChanges: " + iChanges ); //$NON-NLS-1$
        // System.out.println("[CompareTreePanel.generateStats] iDeletions: " + iDeletions ); //$NON-NLS-1$
    }

    /*
     * The counts (see drDiffReport.getTotalAdditions()) that come with the DifferernceReport cannot be used because they include
     * objects that we will not be displaying (see PrimaryMetamodelObjectViewerFilter). So we'll have to count them again, using
     * the PrimaryMetamodelObjectViewerFilter to tell us which ones to include.
     */

    private void calculateCounts( TreeItem item ) {

        Object data = item.getData();
        if (data != null && data instanceof DifferenceReport) {
            Mapping mapping = ((DifferenceReport)data).getMapping();

            /*
             * Issue: what if the root 'mapping' is a ModelAnnotation, and it fails this first test?
             */
            if (mapping == null) {
                return;
            }

            if ((this.pmofViewerFilter != null) && !this.pmofViewerFilter.passesFilter(mapping)) {
                // no action if we fail the filter
            } else {
                DifferenceDescriptor desc = DifferenceAnalysis.getDifferenceDescriptor(mapping);
                if (desc != null) {
                    // System.out.println("[CompareTreePanel.calculateCounts] About to test for add/chg/del, item is: " +
                    // desc.getMapper() ); //$NON-NLS-1$
                    if (desc.isAddition()) {
                        iAdditions++;
                        // System.out.println("[CompareTreePanel.calculateCounts] incr iAdditions " ); //$NON-NLS-1$
                    } else if (desc.isChanged()) {
                        iChanges++;
                        // System.out.println("[CompareTreePanel.calculateCounts] incr iChanges " ); //$NON-NLS-1$
                    } else if (desc.isDeletion()) {
                        iDeletions++;
                        // System.out.println("[CompareTreePanel.calculateCounts] incr iDeletions " ); //$NON-NLS-1$
                    }
                }
            }
        } else if (data != null && data instanceof Mapping) {
            Mapping mapping = (Mapping)data;
            DifferenceDescriptor desc = DifferenceAnalysis.getDifferenceDescriptor(mapping);
            if (desc != null) {
                // System.out.println("[CompareTreePanel.calculateCounts] About to test for add/chg/del, item is: " +
                // desc.toString() ); //$NON-NLS-1$
                if (desc.isAddition()) {
                    iAdditions++;
                    // System.out.println("[CompareTreePanel.calculateCounts]2 incr iAdditions" ); //$NON-NLS-1$
                } else if (desc.isChanged()) {
                    iChanges++;
                    // System.out.println("[CompareTreePanel.calculateCounts]2 incr iChanges " ); //$NON-NLS-1$
                } else if (desc.isDeletion()) {
                    iDeletions++;
                    // System.out.println("[CompareTreePanel.calculateCounts]2 incr iDeletions" ); //$NON-NLS-1$
                }
            }
        }

        TreeItem[] children = item.getItems();
        for (int i = 0; i < children.length; i++) {

            calculateCounts(children[i]);
        }
    }

    /**
     * Indicates if the <code>Mapping</code>'s difference descriptor's skip property should be updated when tree item's check
     * state is changed.
     * 
     * @return <code>true</code> if updating; <code>false</code> otherwise.
     */
    public boolean isUpdatingMappingSkipFlag() {
        return this.updateMapping;
    }

    private void selectItemAndChildren( TreeItem item ) {

        arylTreeItemDepthFirstList.add(item);

        Object data = item.getData();
        if (data != null && data instanceof DifferenceReport) {
            Mapping mapping = ((DifferenceReport)data).getMapping();

            if (mapping == null) {
                return;
            }

            DifferenceDescriptor desc = DifferenceAnalysis.getDifferenceDescriptor(mapping);
            if (desc != null) {
                boolean skip = desc.isSkip();
                WidgetUtil.setChecked(item, !skip, false, this.controller);
            }
            // If the item is not checkable, gray it
            if (!controller.isItemCheckable(item)) {
                item.setGrayed(true);
                this.controller.update(item, false);
            }
        } else if (data != null && data instanceof Mapping) {
            Mapping mapping = (Mapping)data;
            DifferenceDescriptor desc = DifferenceAnalysis.getDifferenceDescriptor(mapping);
            if (desc != null) {
                boolean skip = desc.isSkip();
                WidgetUtil.setChecked(item, !skip, false, this.controller);
            }
            // If the item is not checkable, gray it
            if (!controller.isItemCheckable(item)) {
                item.setGrayed(true);
                this.controller.update(item, false);
            }
        }
        TreeItem[] children = item.getItems();
        for (int i = 0; i < children.length; i++) {
            selectItemAndChildren(children[i]);
        }
    }

    private void setAllNodesSelected( boolean bSelected ) {
        // System.out.println("[CompareTreePanel.setAllNodesSelected] bSelected: " + bSelected ); //$NON-NLS-1$

        TreeItem[] items = tree.getItems();
        for (int i = 0; i < items.length; i++) {
            setAllSelected(items[i], bSelected);
        }
    }

    private void setAllSelected( final TreeItem item,
                                 final boolean checked ) {
        final Object data = item.getData();
        Mapping mapping;
        if (data instanceof DifferenceReport) {
            mapping = ((DifferenceReport)data).getMapping();
        } else if (data instanceof Mapping) {
            mapping = (Mapping)data;
        } else {
            return;
        }
        DifferenceDescriptor desc = DifferenceAnalysis.getDifferenceDescriptor(mapping);
        if (desc != null) {
            WidgetUtil.setChecked(item, checked, false, this.controller);
        }
        // If the item is not checkable, gray it
        if (!controller.isItemCheckable(item)) {
            item.setGrayed(true);
            this.controller.update(item, false);
        }
        // Apply same checked state to any children
        final TreeItem[] children = item.getItems();
        for (int ndx = 0; ndx < children.length; ndx++) {
            setAllSelected(children[ndx], checked);
        }
    }

    /**
     * Sets the property which allows the automatic updating of the {@link DifferenceDescriptor} based on changes to the
     * corresponding tree item's check state.
     * 
     * @param theUpdateMappingFlag the flag indicating if automatic update should take place
     */
    public void setUpdateMappingSkipFlag( boolean theUpdateMappingFlag ) {
        this.updateMapping = theUpdateMappingFlag;
    }

    public int getAdditionCount() {
        // System.out.println("[CompareTreePanel.getAdditionCount] iAdditions: " + iAdditions ); //$NON-NLS-1$
        return this.iAdditions;
    }

    public int getChangeCount() {
        // System.out.println("[CompareTreePanel.getChangeCount] iChanges: " + iChanges ); //$NON-NLS-1$
        return this.iChanges;
    }

    public int getDeletionCount() {
        // System.out.println("[CompareTreePanel.getDeletionCount] iDeletions: " + iDeletions ); //$NON-NLS-1$
        return this.iDeletions;
    }

    public EObject getEObjectForMapping( Mapping mapping ) {
        EObject eoResult = null;

        MappingHelper helper = mapping.getHelper();
        if (helper instanceof DifferenceDescriptor) {
            final DifferenceDescriptor desc = (DifferenceDescriptor)helper;
            final DifferenceType type = desc.getType();
            if (type.getValue() == DifferenceType.DELETION) {
                final List<EObject> inputs = mapping.getInputs();
                final EObject input = inputs.isEmpty() ? null : inputs.get(0);

                if (input != null) {
                    eoResult = input;
                }
            } else {
                final List<EObject> outputs = mapping.getOutputs();
                final EObject output = outputs.isEmpty() ? null : outputs.get(0);
                final List<EObject> inputs = mapping.getInputs();
                final EObject input = inputs.isEmpty() ? null : inputs.get(0);

                if (output != null) {
                    eoResult = output;
                } else if (input != null) {
                    eoResult = input;
                }
            }
        }

        return eoResult;
    }

    void handleTreeNodeSelection() {
        // Do not proceed if we are programatically changing selection
        // (As would happen in handleDownArrow())
        if (bUpdatingSelection) {
            return;
        }

        TreeItem[] tiSelectedTreeItems = tree.getSelection();

        // JBEDSP-102 : Fixes NPE on linux
        if (tiSelectedTreeItems != null && tiSelectedTreeItems.length > 0) {
            TreeItem ti = tiSelectedTreeItems[0];
            setCurrentTreeItem(ti);
        }
    }

    void handleUpArrow() {
        bUpdatingSelection = true;
        TreeItem tiPrev = getPrevDiffItem(getCurrentTreeItem());

        if (tiPrev != null) {
            // System.out.println( "[CompareTreepanel.handleUpArrow] Prev is: " + tiPrev.getText() ); //$NON-NLS-1$
            TreeItem[] tiArray = {tiPrev};
            getTreeViewer().reveal(tiPrev);
            forceTreeSelection(tiArray);
            setCurrentTreeItem(tiPrev);
        } else {
            boolean bContinue = MessageDialog.openQuestion(null, GO_TO_NEXT_TITLE, BEGINNING_OF_DOC_MSG);

            // user wishes to continue at the last diff
            if (bContinue) {

                // (calling getPrev with a null current will retrieve the LAST diff)
                TreeItem tiLast = getPrevDiffItem(null);

                if (tiLast != null) {
                    TreeItem[] tiArray = {tiLast};
                    getTreeViewer().reveal(tiLast);
                    forceTreeSelection(tiArray);
                    setCurrentTreeItem(tiLast);
                }
            }
        }
        bUpdatingSelection = false;
    }

    void handleDownArrow() {
        bUpdatingSelection = true;

        TreeItem tiNext = getNextDiffItem(getCurrentTreeItem());

        if (tiNext != null) {
            // System.out.println( "[CompareTreepanel.handleDownArrow] Next Data is: " + tiNext.getData().getClass().getName() );
            // //$NON-NLS-1$
            TreeItem[] tiArray = {tiNext};
            forceTreeSelection(tiArray);
            setCurrentTreeItem(tiNext);
        } else {
            boolean bContinue = MessageDialog.openQuestion(null, GO_TO_NEXT_TITLE, END_OF_DOC_MSG);

            // user wishes to continue at the first diff
            if (bContinue) {

                // (calling getNext with a null current will retrieve the FIRST diff)
                TreeItem tiFirst = getNextDiffItem(null);

                if (tiFirst != null) {
                    TreeItem[] tiArray = {tiFirst};
                    getTreeViewer().reveal(tiFirst);
                    forceTreeSelection(tiArray);
                    setCurrentTreeItem(tiFirst);
                }
            }
        }
        // System.out.println( "[CompareTreepanel.handleDownArrow] BOT " ); //$NON-NLS-1$
        bUpdatingSelection = false;
    }

    private void forceTreeSelection( final TreeItem[] tiArray ) {

        // System.out.println( "[CompareTreepanel.forceTreeSelection] ti to force is: " + tiArray[0].getData() ); //$NON-NLS-1$
        if (tiArray == null || tiArray.length == 0 || tiArray[0] == null || tiArray[0].getData() == null) {
            return;
        }

        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                doTreeSelection(tiArray);

                // Now do it again, in case it was not visible the first time.
                // A weakness in SWT's tree is that when you try to select
                // and reveal a node in a branch that was not previously expanded,
                // it will be revealed, but not selected. Its parent will be
                // selected instead. When we do this the second time we are doing
                // it against a node whose branch has been expanded (our first pass
                // did that) so selection will work. (jh, 1/18/2005)
                forceTreeSelectionAgain(tiArray);
            }
        });
    }

    void forceTreeSelectionAgain( final TreeItem[] tiArray ) {

        // System.out.println( "[CompareTreepanel.forceTreeSelection] ti to force is: " + tiArray[0].getData() ); //$NON-NLS-1$
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                doTreeSelection(tiArray);
            }
        });
    }

    void doTreeSelection( final TreeItem[] tiArray ) {

        ISelection sel = new StructuredSelection(tiArray[0].getData());
        getTreeViewer().setSelection(sel, true);
        Event event = new Event();
        event.widget = tree;
        tree.notifyListeners(SWT.Selection, event);
    }

    private void setCurrentTreeItem( TreeItem ti ) {
        tiCurrentSelection = ti;
        // System.out.println( "[CompareTreepanel.setCurrentTreeItem] New Curr Item is: " + tiCurrentSelection.getText() );
        // //$NON-NLS-1$
    }

    private TreeItem getCurrentTreeItem() {

        // System.out.println( "[CompareTreepanel.getCurrentTreeItem] Curr Item is: " + tiCurrentSelection.getText() );
        // //$NON-NLS-1$
        return tiCurrentSelection;
    }

    private TreeItem getNextDiffItem( TreeItem tiCurrentNode ) {

        int iSize = arylTreeItemDepthFirstList.size();
        int iStartIndex = 0;
        TreeItem tiResult = null;

        if (tiCurrentNode != null) {
            iStartIndex = arylTreeItemDepthFirstList.indexOf(tiCurrentNode);
            iStartIndex++;

        } else {

            // if no node selected, default to zero
            iStartIndex = 0;
        }

        // walk the list FORWARD until you find the next diff node, or reach the end
        for (int i = iStartIndex; i < iSize; i++) {
            TreeItem tiTemp = arylTreeItemDepthFirstList.get(i);
            if (controller.isItemChanged(tiTemp)) {
                tiResult = tiTemp;
                break;
            }
        }

        // if ( tiResult != null ) {
        // System.out.println( "[CompareTreepanel.getNextDiffItem] About to return: " + tiResult.getText() ); //$NON-NLS-1$
        // } else {
        // System.out.println( "[CompareTreepanel.getNextDiffItem] About to return: NULL " ); //$NON-NLS-1$
        // }

        return tiResult;
    }

    private TreeItem getPrevDiffItem( TreeItem tiCurrentNode ) {

        int iSize = arylTreeItemDepthFirstList.size();
        int iStartIndex = iSize - 1;
        TreeItem tiResult = null;

        if (tiCurrentNode != null) {
            iStartIndex = arylTreeItemDepthFirstList.indexOf(tiCurrentNode);
            iStartIndex--;

        } else {

            // if no node selected, default to the end of the list
            iStartIndex = iSize - 1;
        }

        // walk the list BACKWARD until you find the next diff node, or reach the end
        for (int i = iStartIndex; i > -1; i--) {
            TreeItem tiTemp = arylTreeItemDepthFirstList.get(i);
            if (controller.isItemChanged(tiTemp)) {
                tiResult = tiTemp;
                break;
            }
        }

        return tiResult;
    }

    /**
     * Toolbar manager for theis panel's toolbar.
     */
    class PaneToolBarManager extends ToolBarManager {

        public PaneToolBarManager( ToolBar paneToolBar ) {
            super(paneToolBar);
        }

        @Override
        protected void relayout( ToolBar toolBar,
                                 int oldCount,
                                 int newCount ) {
            toolBar.layout();
            Composite parent = toolBar.getParent();
            parent.layout();
            if (parent.getParent() != null) parent.getParent().layout();
        }

    }

    class CompareTreeController extends DefaultTreeViewerController {

        /**
         * @see com.metamatrix.ui.internal.widget.DefaultTreeViewerController#checkedStateToggled(org.eclipse.swt.widgets.TreeItem)
         */

        @Override
        public boolean isItemCheckable( TreeItem theItem ) {

            /*
             * We are implementing a policy of protecting 'NO CHANGE' nodes from becoming unchecked here.
             */
            return isItemChanged(theItem);
        }

        public boolean isItemChanged( TreeItem theItem ) {

            /*
             * We are implementing a policy of protecting 'NO CHANGE' nodes from becoming unchecked here.
             */
            Object element = theItem.getData();
            MappingHelper helper = null;

            if (element instanceof DifferenceReport) {
                Mapping mapping = ((DifferenceReport)element).getMapping();
                helper = mapping.getHelper();
            } else if (element instanceof Mapping) {
                helper = ((Mapping)element).getHelper();
            }

            if (helper instanceof DifferenceDescriptor) {
                DifferenceDescriptor desc = (DifferenceDescriptor)helper;
                if (desc.isChangedBelow()) {
                    return false;
                } else if (desc.isAddition() || desc.isDeletion() || desc.isChanged()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void checkedStateToggled( TreeItem theItem ) {

            if (isUpdatingMappingSkipFlag()) {
                Object element = theItem.getData();
                MappingHelper helper = null;

                if (element instanceof DifferenceReport) {
                    Mapping mapping = ((DifferenceReport)element).getMapping();
                    helper = mapping.getHelper();
                } else if (element instanceof Mapping) {
                    helper = ((Mapping)element).getHelper();
                }

                if (helper instanceof DifferenceDescriptor) {
                    ((DifferenceDescriptor)helper).setSkip(!theItem.getChecked());
                }
            }
        }

        /**
         * @see com.metamatrix.ui.internal.widget.DefaultTreeViewerController#update(org.eclipse.swt.widgets.TreeItem, boolean)
         * @since 4.2
         */
        @Override
        public void update( final TreeItem item,
                            final boolean selected ) {
            super.update(item, selected);
            checkedStateToggled(item);
        }
    }

    public void setTerminology( int terminology ) {
        DecoratingLabelProvider labelProvider = (DecoratingLabelProvider)this.treeViewer.getLabelProvider();
        ((MappingLabelDecorator)labelProvider.getLabelDecorator()).setTerminology(terminology);
    }
}// end CompareTreePanel
