/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.tree;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SetQuery;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * QueryTreeSelectionDialog is an ElementTreeSelectionDialog for displaying the SELECT, UNION, FROM, and WHERE nodes of a query.
 * Subselects in the FROM and WHERE clauses are supported. Usage: <code>
        QueryTreeSelectionDialog d = new QueryTreeSelectionDialog(getShell(), getCommand());
        d.setValidator(new AddGroupSelectionValidator());
        d.setTitle(AddGroupSelectionValidator.DIALOG_TITLE);
        d.setMessage(AddGroupSelectionValidator.DIALOG_MESSAGE);
        d.open();
   </code>
 * 
 * @since 4.2
 */
public class QueryTreeSelectionDialog extends ElementTreeSelectionDialog implements UiConstants {

    private static final String UNIONALL_CHECKBOX_TEXT = Util.getString("QueryTreeSelectionDialog.unionAllCheckbox.text"); //$NON-NLS-1$
    private static final String UNIONALL_CHECKBOX_TIP = Util.getString("QueryTreeSelectionDialog.unionAllCheckbox.toolTip"); //$NON-NLS-1$

    private Command root;
    private Button unionAllCheckbox;
    private boolean unionAll = false;

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for displaying Metadata labels and icons in the TreeViewer. Labels and icons for
     *        LanguageObjects in the tree are handled by this class.
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public QueryTreeSelectionDialog( Shell parent,
                                     Command root ) {
        super(parent, new QueryTreeLabelProvider(), QueryTreeContentProvider.getInstance());
        this.root = root;
        init();
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for displaying Metadata labels and icons in the TreeViewer. Labels and icons for
     *        LanguageObjects in the tree are handled by this class.
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public QueryTreeSelectionDialog( Shell parent,
                                     Command root,
                                     ILabelProvider labelProvider ) {
        super(parent, new QueryTreeLabelProvider(labelProvider), QueryTreeContentProvider.getInstance());
        this.root = root;
        init();
    }

    private void init() {
        ArrayList inputList = new ArrayList(1);
        inputList.add(root);
        setInput(inputList);
        setAllowMultiple(false);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        // Add a UseAll checkbox to the DialogArea
        Composite overallComposite = new Composite(parent, SWT.NONE);

        // Set the layout
        overallComposite.setLayout(new GridLayout());

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        overallComposite.setLayoutData(gridData);

        super.createDialogArea(overallComposite);

        unionAllCheckbox = WidgetFactory.createCheckBox(overallComposite, UNIONALL_CHECKBOX_TEXT);
        unionAllCheckbox.setToolTipText(UNIONALL_CHECKBOX_TIP);
        unionAllCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                updateUnionAll();
            }
        });

        return overallComposite;
    }

    void setUnionAllCheckbox( int indx ) {
        if (this.root != null && this.root instanceof SetQuery) {
            SetQuery union = (SetQuery)this.root;
            unionAllCheckbox.setSelection(union.isAll());
            this.unionAll = union.isAll();
        }
    }

    void updateUnionAll() {
        this.unionAll = this.unionAllCheckbox.getSelection();
    }

    /**
     * Adds a ViewerFilter to this dialog's TreeViewer
     * 
     * @param filter
     */
    public void addViewerFilter( ViewerFilter filter ) {
        super.getTreeViewer().addFilter(filter);
    }

    /**
     * Get the index of the query within the UNION that is selected. If a query command is not selected, -1 is returned.
     * 
     * @return the index of the selected query, -1 if none selected
     */
    public int getSelectedUnionSegmentIndex() {
        int index = -1;
        // make sure the base query is a union
        if (this.root instanceof SetQuery) {
            // Selected objects
            Object[] results = getResult();
            if (results.length == 1) {
                // Selected object is a Command
                if (results[0] instanceof Command) {
                    // Get all root children (Commands), figure out the index of the selected object
                    Object[] children = QueryTreeContentProvider.getInstance().getChildren(this.root);
                    for (int i = 0; i < children.length; i++) {
                        if (children[i].equals(results[0])) {
                            index = i;
                            break;
                        }
                    }
                }
            }
        }
        return index;
    }

    /**
     * Determine if a UNION query segment was selected
     * 
     * @return 'true' if a query segment is selected, 'false' if not.
     */
    public boolean unionQuerySegmentSelected() {
        int index = getSelectedUnionSegmentIndex();
        return (index == -1) ? false : true;
    }

    /**
     * Determine if a FROM clause in one of the UNION query segments was selected
     * 
     * @return 'true' if a query segment FROM clause is selected, 'false' if not.
     */
    public boolean unionQueryFromClauseSelected() {
        boolean fromSelected = false;
        // make sure the base query is a union
        if (this.root instanceof SetQuery) {
            // Selected objects
            Object[] results = getResult();
            if (results.length == 1) {
                if (results[0] instanceof From) {
                    fromSelected = true;
                }
            }
        }
        return fromSelected;
    }

    /**
     * Get the index of the Union query segment, when a FROM clause is selected.
     * 
     * @return the index of the query that the selected FROM clause belongs to, -1 if failure.
     */
    public int getUnionQuerySegmentIndexForFromClause() {
        int segmentIndex = -1;
        // make sure the base query is a union
        if (this.root instanceof SetQuery) {
            // Selected objects
            Object[] results = getResult();
            if (results.length == 1) {
                // Selected Object is a FROM clause
                if (results[0] instanceof From) {
                    // Get all the root Commands
                    Object[] children = QueryTreeContentProvider.getInstance().getChildren(this.root);
                    // Look through the root commands, find one that has FROM clause we're looking for.
                    for (int i = 0; i < children.length; i++) {
                        if (children[i] instanceof QueryCommand) {
                            Object[] queryChildren = QueryTreeContentProvider.getInstance().getChildren(children[i]);
                            for (int iClause = 0; iClause < queryChildren.length; iClause++) {
                                if ((queryChildren[iClause] instanceof From) && queryChildren[iClause].equals(results[0])) {
                                    segmentIndex = i;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return segmentIndex;
    }

    public void setInitialUnionSelection( int selectedUnionIndex ) {
        // Get all the root Commands
        Object[] children = QueryTreeContentProvider.getInstance().getChildren(this.root);

        // Set selection to the desired child
        List selectedChild = new ArrayList(1);
        selectedChild.add(children[selectedUnionIndex]);

        setInitialElementSelections(selectedChild);
    }

    public boolean isUnionAllSelected() {
        return this.unionAll;
    }

    /**
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected TreeViewer createTreeViewer( Composite parent ) {
        TreeViewer result = super.createTreeViewer(parent);
        // add a filter to remove closed projects
        result.addFilter(new ViewerFilter() {
            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                if (element instanceof IProject) {
                    return ((IProject)element).isOpen();
                }
                return true;
            }

        });
        result.expandAll();
        result.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                if (unionQuerySegmentSelected()) {
                    int indx = getSelectedUnionSegmentIndex();
                    setUnionAllCheckbox(indx);
                }
            }
        });

        return result;
    }

    /**
     * Method declared on Dialog.
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        super.createButtonsForButtonBar(parent);
    }

}
