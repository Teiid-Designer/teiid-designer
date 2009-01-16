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
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.search.IFinderHostDialog;
import com.metamatrix.modeler.internal.ui.search.IFinderPanel;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * ModelWorkspacePanel is a panel that displays the workspace tree and allows selection
 */
public class ModelWorkspacePanel extends Composite implements IFinderPanel, ISelectionChangedListener {

    private static final String TITLE = UiConstants.Util.getString("ModelWorkspacePanel.title"); //$NON-NLS-1$

    private IWorkspaceRoot root;
    private String sMessage;
    private Button btnProperties;
    private boolean bShowPropertiesButton = true;
    private EObject selectedEObject;

    TreeViewer fViewer;
    private ILabelProvider fLabelProvider;
    private ITreeContentProvider fContentProvider;

    private ISelectionStatusValidator fValidator = null;
    private ViewerSorter fSorter;
    private boolean fAllowMultiple = true;
    boolean fDoubleClickSelects = true;
    private String fEmptyListMessage = UiConstants.Util.getString("ElementTreeSelectionDialog.nothing_available"); //$NON-NLS-1$   

    IStatus fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
    private List fFilters;
    private Object fInput;
    private boolean fIsEmpty;

    // the final collection of selected elements, or null if this dialog was canceled
    private Object[] result;

    // a collection of the initially-selected elements
    private List initialSelections = new ArrayList();

    private IFinderHostDialog fhpHostDialog;

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     */
    public ModelWorkspacePanel( Composite parent,
                                IFinderHostDialog fhpHostDialog ) {
        this(parent, fhpHostDialog, new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());
    }

    /**
     * Construct an instance of ModelWorkspaceDialog. This constructor defaults to the resource root.
     * 
     * @param parent
     * @param labelProvider an ILabelProvider for the tree
     * @param contentProvider an ITreeContentProvider for the tree
     */
    public ModelWorkspacePanel( Composite parent,
                                IFinderHostDialog fhpHostDialog,
                                ILabelProvider labelProvider,
                                ITreeContentProvider contentProvider ) {
        super(parent, SWT.NONE);
        this.fhpHostDialog = fhpHostDialog;
        this.fLabelProvider = labelProvider;
        this.fContentProvider = contentProvider;

        init();
    }

    IFinderHostDialog getHost() {
        return fhpHostDialog;
    }

    public void init() {

        // default to EObject validator
        setValidator(new EObjectSelectionValidator());

        // set input
        if (root != null) {
            setInput(root);
        } else {
            // use default root
            setInput(ResourcesPlugin.getWorkspace().getRoot());
        }

        createControl(this);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    private Composite createControl( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        GridData data1 = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(data1);

        Label messageLabel = createMessageArea(composite);
        TreeViewer treeViewer = createTreeViewer(composite);

        GridData data = new GridData(GridData.FILL_BOTH);

        // these methods are in Dialog
        // data.widthHint = fWidth * 8; //= convertWidthInCharsToPixels(fWidth);
        // data.heightHint = fHeight * 4; //convertHeightInCharsToPixels(fHeight);

        Tree treeWidget = treeViewer.getTree();
        treeWidget.setLayout(new GridLayout());
        treeWidget.setLayoutData(data);
        treeWidget.setFont(parent.getFont());

        if (fIsEmpty) {
            messageLabel.setEnabled(false);
            treeWidget.setEnabled(false);
        }

        return composite;
    }

    /**
     * Adds a ViewerFilter to this dialog's TreeViewer
     * 
     * @param filter
     */
    public void addViewerFilter( ViewerFilter filter ) {
        getTreeViewer().addFilter(filter);
    }

    public void setShowPropertiesButton( boolean b ) {
        bShowPropertiesButton = b;
        btnProperties.setVisible(bShowPropertiesButton);
    }

    /* (non-Javadoc)
     * Method declared on Dialog.
     */

    public void selectionChanged( SelectionChangedEvent event ) {
        IStructuredSelection sel = (IStructuredSelection)getTreeViewer().getSelection();

        if (sel.getFirstElement() instanceof EObject) {
            selectedEObject = (EObject)sel.getFirstElement();
        } else {
            selectedEObject = null;
        }
        getSelectedEObject();
    }

    private EObject getSelectedEObject() {
        return selectedEObject;
    }

    /**
     * Constructs an instance of <code>ElementTreeSelectionDialog</code>.
     * 
     * @param labelProvider the label provider to render the entries
     * @param contentProvider the content provider to evaluate the tree structure
     */
    public void initializeElementTreeSelectionDialog( Shell parent,
                                                      ILabelProvider labelProvider,
                                                      ITreeContentProvider contentProvider ) {

        fLabelProvider = labelProvider;
        fContentProvider = contentProvider;

        setResult(new ArrayList(0));
    }

    protected void setResult( List newResult ) {
        if (newResult == null) {
            result = null;
        } else {
            result = new Object[newResult.size()];
            newResult.toArray(result);
        }
    }

    /**
     * Sets the initial selection. Convenience method.
     * 
     * @param selection the initial selection.
     */
    public void setInitialSelection( Object selection ) {
        setInitialSelections(new Object[] {selection});
    }

    /**
     * Sets the initial selection in this selection dialog to the given elements.
     * 
     * @param selectedElements the array of elements to select
     */
    public void setInitialSelections( Object[] selectedElements ) {
        initialSelections = new ArrayList(selectedElements.length);
        for (int i = 0; i < selectedElements.length; i++)
            initialSelections.add(selectedElements[i]);
    }

    /**
     * Sets the message to be displayed if the list is empty.
     * 
     * @param message the message to be displayed.
     */
    public void setEmptyListMessage( String message ) {
        fEmptyListMessage = message;
    }

    /**
     * Specifies if multiple selection is allowed.
     */
    public void setAllowMultiple( boolean allowMultiple ) {
        fAllowMultiple = allowMultiple;
    }

    /**
     * Specifies if default selected events (double click) are created.
     */
    public void setDoubleClickSelects( boolean doubleClickSelects ) {
        fDoubleClickSelects = doubleClickSelects;
    }

    /**
     * Sets the sorter used by the tree viewer.
     */
    public void setSorter( ViewerSorter sorter ) {
        fSorter = sorter;
    }

    /**
     * Adds a filter to the tree viewer.
     * 
     * @param filter a filter.
     */
    public void addFilter( ViewerFilter filter ) {
        if (fFilters == null) fFilters = new ArrayList(4);

        fFilters.add(filter);
    }

    /**
     * Sets an optional validator to check if the selection is valid. The validator is invoked whenever the selection changes.
     * 
     * @param validator the validator to validate the selection.
     */
    public void setValidator( ISelectionStatusValidator validator ) {
        fValidator = validator;
    }

    /**
     * Sets the tree input.
     * 
     * @param input the tree input.
     */
    public void setInput( Object input ) {
        fInput = input;
    }

    /**
     * Handles cancel button pressed event.
     */
    protected void cancelPressed() {
        setResult(null);
    }

    /*
     * @see SelectionStatusDialog#computeResult()
     */
    protected void computeResult() {
        setResult(((IStructuredSelection)fViewer.getSelection()).toList());
    }

    /*
     * @see Window#create()
     */
    public void create() {
        BusyIndicator.showWhile(null, new Runnable() {
            public void run() {
                fViewer.setSelection(new StructuredSelection(getInitialElementSelections()), true);
                updateOKStatus();
            }
        });
    }

    protected Label createMessageArea( Composite composite ) {
        Label label = new Label(composite, SWT.NONE);
        if (sMessage != null) {
            label.setText(sMessage);
        }
        label.setFont(composite.getFont());
        return label;
    }

    /**
     * Returns the initial selection in this selection dialog.
     * 
     * @deprecated use getInitialElementSelections() instead
     * @return the list of initial selected elements or null
     */
    @Deprecated
    protected List getInitialSelections() {
        if (initialSelections.isEmpty()) {
            return null;
        }
        return getInitialElementSelections();
    }

    /**
     * Returns the list of initial element selections.
     * 
     * @return List
     */
    protected List getInitialElementSelections() {
        return initialSelections;
    }

    /**
     * Creates the tree viewer.
     * 
     * @param parent the parent composite
     * @return the tree viewer
     */
    protected TreeViewer createTreeViewer( Composite parent ) {
        int style = SWT.BORDER | (fAllowMultiple ? SWT.MULTI : SWT.SINGLE);

        fViewer = new TreeViewer(new Tree(parent, style));
        fViewer.setContentProvider(fContentProvider);
        fViewer.setLabelProvider(fLabelProvider);
        fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent event ) {
                access$setResult(((IStructuredSelection)event.getSelection()).toList());
                updateOKStatus();
            }
        });

        fViewer.setSorter(fSorter);
        if (fFilters != null) {
            for (int i = 0; i != fFilters.size(); i++)
                fViewer.addFilter((ViewerFilter)fFilters.get(i));
        }

        if (fDoubleClickSelects) {
            Tree tree = fViewer.getTree();
            tree.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetDefaultSelected( SelectionEvent e ) {
                    updateOKStatus();
                    if (fCurrStatus.isOK()) {
                        getHost().okPressed();
                    }
                }
            });
        }
        fViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent event ) {
                updateOKStatus();

                // If it is not OK or if double click does not
                // select then expand
                if (!(fDoubleClickSelects && fCurrStatus.isOK())) {
                    ISelection selection = event.getSelection();
                    if (selection instanceof IStructuredSelection) {
                        Object item = ((IStructuredSelection)selection).getFirstElement();
                        if (fViewer.getExpandedState(item)) fViewer.collapseToLevel(item, 1);
                        else fViewer.expandToLevel(item, 1);
                    }
                }
            }
        });

        fViewer.setInput(fInput);

        // listen to selection in the tree
        if (getTreeViewer() != null) {
            getTreeViewer().addSelectionChangedListener(this);
        }

        return fViewer;
    }

    /**
     * Returns the tree viewer.
     * 
     * @return the tree viewer
     */
    protected TreeViewer getTreeViewer() {
        return fViewer;
    }

    protected void access$setResult( List result ) {
        setResult(result);
    }

    public void createButtonsForButtonBar( Composite parent ) {

        // listen to selection in the tree
        if (getTreeViewer() != null) {
            getTreeViewer().addSelectionChangedListener(this);
        }
    }

    public void handleOkPressed() {

    }

    public void handleCancelPressed() {

    }

    public void updateOKStatus() {
        //        System.out.println("[ModelWOrkspacePanel.updateOkStatus] TOP"); //$NON-NLS-1$
        if (!fIsEmpty) {
            if (fValidator != null) {
                fCurrStatus = fValidator.validate(getResult());
                getHost().updateTheStatus(fCurrStatus);
            } else {
                fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", //$NON-NLS-1$
                                         null);
            }
        } else {
            fCurrStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.ERROR, fEmptyListMessage, null);
        }

        getHost().updateTheStatus(fCurrStatus);
    }

    public String getTitle() {
        return TITLE;
    }

    public Object[] getResult() {
        return result;
    }
}
