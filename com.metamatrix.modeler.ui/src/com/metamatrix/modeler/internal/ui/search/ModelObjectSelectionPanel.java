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
package com.metamatrix.modeler.internal.ui.search;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.FilteredList;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * Provides model object selection functionality, including object name matching as user types.
 * 
 * @since 4.2
 */
public class ModelObjectSelectionPanel extends Composite implements IFinderPanel {

    private static final String TITLE = UiConstants.Util.getString("ModelObjectSelectionPanel.title"); //$NON-NLS-1$

    private static final String TEXT_ENTRY_LABEL_TEXT = UiConstants.Util.getString("ModelObjectSelectionPanel.textEntryLabel.text"); //$NON-NLS-1$
    private static final String UPPER_TABLE_LABEL_TEXT = UiConstants.Util.getString("ModelObjectSelectionPanel.upperTableLabel.text"); //$NON-NLS-1$
    private static final String LOWER_TABLE_LABEL_TEXT = UiConstants.Util.getString("ModelObjectSelectionPanel.lowerTableLabel.text"); //$NON-NLS-1$

    private HashMap hmUniqueNames;
    private String sSelectedObject;
    private String sSelectedPath;

    private String fUpperListLabel;
    private String fLowerListLabel;
    ILabelProvider fQualifierRenderer;
    private ILabelProvider fFilterRenderer;
    private Object[] fElements = new Object[0];

    private Table fLowerList;
    private Object[] fQualifierElements;
    protected FilteredList fFilteredList;
    private String fFilter;
    Text fFilterText;
    private ISelectionStatusValidator fValidator;

    private String fEmptyListMessage = ""; //$NON-NLS-1$
    private String fEmptySelectionMessage = ""; //$NON-NLS-1$

    private Object[] result;

    private boolean fIgnoreCase = true;
    private boolean fAllowDuplicates = false;
    private boolean fMatchEmptyString = true;

    private Object[] fSelection = new Object[0];
    private Label fMessage;
    // a collection of the initially-selected elements
    private List initialSelections = new ArrayList();
    private IFinderHostDialog fhpHostDialog;
    private IStatus stCurrentStatus;
    private boolean bUserCancelledDuringLoad = false;
    private boolean bEnableProgressCancel = true; // default to TRUE

    /**
     * @param parent
     * @since 4.2
     */
    public ModelObjectSelectionPanel( Composite parent,
                                      IFinderHostDialog fhpHostDialog ) {
        super(parent, SWT.NONE);
        this.fhpHostDialog = fhpHostDialog;

        fQualifierRenderer = new FindObjectLabelProvider(FindObjectLabelProvider.CONTAINER);
        fFilterRenderer = new FindObjectLabelProvider(FindObjectLabelProvider.OBJECT);
        init();
    }

    public ModelObjectSelectionPanel( Composite parent,
                                      IFinderHostDialog fhpHostDialog,
                                      boolean bEnableProgressCancel ) {
        super(parent, SWT.NONE);
        this.fhpHostDialog = fhpHostDialog;
        this.bEnableProgressCancel = bEnableProgressCancel;

        fQualifierRenderer = new FindObjectLabelProvider(FindObjectLabelProvider.CONTAINER);
        fFilterRenderer = new FindObjectLabelProvider(FindObjectLabelProvider.OBJECT);
        init();
    }

    private IFinderHostDialog getHost() {
        return fhpHostDialog;
    }

    private void init() {

        setUpperListLabel(UPPER_TABLE_LABEL_TEXT);
        setLowerListLabel(LOWER_TABLE_LABEL_TEXT);

        doLoadObjects();

        createControl(this);
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    private void doLoadObjects() {

        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( IProgressMonitor theMonitor ) {

                loadObjects(theMonitor);
                theMonitor.done();
            }
        };
        ProgressMonitorDialog dlg = null;
        try {

            dlg = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());

            dlg.run(true, bEnableProgressCancel, op);

            if (dlg.getProgressMonitor().isCanceled()) {
                bUserCancelledDuringLoad = true;
            } else {
                // finish it all at the end
                dlg.getProgressMonitor().worked(1);
            }

        } catch (InterruptedException e) {
            bUserCancelledDuringLoad = true;
        } catch (InvocationTargetException e) {
            UiConstants.Util.log(e.getTargetException());
        }

    }

    public boolean userCancelledDuringLoad() {
        return bUserCancelledDuringLoad;
    }

    void loadObjects( IProgressMonitor monitor ) {

        ModelWorkspaceSearch mwssSearch = new ModelWorkspaceSearch(monitor);
        Collection colRecordObjects = mwssSearch.getAllModelObjectRecords();

        // create a data structure to relate the names to their URI(s) (could be dupes).
        hmUniqueNames = new HashMap();

        // the collection will be null if user cancelled the workspace search
        if (colRecordObjects == null) {
            return;
        }

        Iterator it = colRecordObjects.iterator();

        while (it.hasNext()) {
            ResourceObjectRecord ror = (ResourceObjectRecord)it.next();

            // skip entries that have no name
            if (ror.getName().trim().equals("")) { //$NON-NLS-1$
                continue;
            }

            // add named entries
            if (hmUniqueNames.containsKey(ror.getName())) {
                // if key already exists, add to its ArrayList
                ArrayList aryl = (ArrayList)hmUniqueNames.get(ror.getName());
                aryl.add(ror);
            } else {
                // if key does not exist, create new ArrayList and add ror to it
                ArrayList aryl2 = new ArrayList();
                aryl2.add(ror);
                hmUniqueNames.put(ror.getName(), aryl2);
            }
        }

        // load the dialog
        setElements(getElementsFromSource());
    }

    private Object[] getElementsFromSource() {

        // The elements are the first entries in each ArrayList stored as a value
        // in the HashMap. They are ResourceObjectRecord s.
        ArrayList arylElements = new ArrayList(hmUniqueNames.values().size());
        Iterator it = hmUniqueNames.values().iterator();

        while (it.hasNext()) {
            ArrayList arylTemp = (ArrayList)it.next();
            arylElements.add(arylTemp.get(0));
        }

        return arylElements.toArray();
    }

    public Object[] getFoldedElements( int index ) {

        // 1. get the string in the selected upper table row
        String sName = null;
        Object[] oArySelection = fFilteredList.getSelection();

        if (oArySelection.length > 0) {
            sName = ((ResourceObjectRecord)oArySelection[0]).getName();
        }

        // 2. use the string to get the 'dupes' from the hashmap
        ArrayList aryl = (ArrayList)hmUniqueNames.get(sName);

        Object[] oAry = new Object[aryl.size()];
        Iterator it = aryl.iterator();
        int ix = 0;

        while (it.hasNext()) {
            ResourceObjectRecord rorTemp = (ResourceObjectRecord)it.next();

            oAry[ix++] = rorTemp;
        }

        return oAry;
    }

    /**
     * Sets the upper list label. If the label is <code>null</code> (default), no label is created.
     */
    public void setUpperListLabel( String label ) {
        fUpperListLabel = label;
    }

    /**
     * Sets the lower list label. If the label is <code>null</code> (default), no label is created.
     */
    public void setLowerListLabel( String label ) {
        fLowerListLabel = label;
    }

    /**
     * Sets the elements to be displayed.
     * 
     * @param elements the elements to be displayed.
     */
    public void setElements( Object[] elements ) {
        fElements = elements;
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    public Control createControl( Composite parent ) {
        Composite contents = parent;

        createMessageArea(contents);
        createFilterText(contents);
        createLabel(contents, fUpperListLabel);
        createFilteredList(contents);
        createLabel(contents, fLowerListLabel);
        createLowerList(contents);

        setListElements(fElements);

        List initialSelections = getInitialElementSelections(); // jhTODO: from SelectionDialog
        if (!initialSelections.isEmpty()) {
            Object element = initialSelections.get(0);

            setSelection(new Object[] {element});
            setLowerSelectedElement(element);
        }

        return contents;
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
     * Sets the selection referenced by an array of elements. Empty or null array removes selection. To be called within open().
     * 
     * @param selection the indices of the selection.
     */
    protected void setSelection( Object[] selection ) {
        Assert.isNotNull(fFilteredList);
        fFilteredList.setSelection(selection);
    }

    /**
     * Sets the elements of the list (widget). To be called within open().
     * 
     * @param elements the elements of the list.
     */
    protected void setListElements( Object[] elements ) {
        Assert.isNotNull(fFilteredList);
        fFilteredList.setElements(elements);
    }

    /**
     * Creates the message text widget and sets layout data.
     * 
     * @param composite the parent composite of the message area.
     */
    protected Label createMessageArea( Composite composite ) {
        Label label = new Label(composite, SWT.NONE);

        if (TEXT_ENTRY_LABEL_TEXT != null) {
            label.setText(TEXT_ENTRY_LABEL_TEXT);
        }

        label.setFont(composite.getFont());

        GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        label.setLayoutData(data);

        fMessage = label;

        return label;
    }

    /**
     * Creates a label if name was not <code>null</code>.
     * 
     * @param parent the parent composite.
     * @param name the name of the label.
     * @return returns a label if a name was given, <code>null</code> otherwise.
     */
    protected Label createLabel( Composite parent,
                                 String name ) {
        if (name == null) return null;

        Label label = new Label(parent, SWT.NONE);
        label.setText(name);
        label.setFont(parent.getFont());

        return label;
    }

    protected Text createFilterText( Composite parent ) {
        Text text = new Text(parent, SWT.BORDER);

        GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        text.setLayoutData(data);
        text.setFont(parent.getFont());

        text.setText((fFilter == null ? "" : fFilter)); //$NON-NLS-1$

        Listener listener = new Listener() {
            public void handleEvent( Event e ) {
                fFilteredList.setFilter(fFilterText.getText());
            }
        };
        text.addListener(SWT.Modify, listener);

        text.addKeyListener(new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if (e.keyCode == SWT.ARROW_DOWN) fFilteredList.setFocus();
            }

            public void keyReleased( KeyEvent e ) {
            }
        });

        fFilterText = text;

        return text;
    }

    /**
     * Creates a filtered list.
     * 
     * @param parent the parent composite.
     * @return returns the filtered list widget.
     */
    protected FilteredList createFilteredList( Composite parent ) {
        int flags = SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE;

        FilteredList list = new FilteredList(parent, flags, fFilterRenderer, fIgnoreCase, fAllowDuplicates, fMatchEmptyString);

        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        list.setLayoutData(data);
        list.setFont(parent.getFont());
        list.setFilter((fFilter == null ? "" : fFilter)); //$NON-NLS-1$

        list.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent e ) {
                handleDefaultSelected();
            }

            public void widgetSelected( SelectionEvent e ) {
                handleWidgetSelected();
            }
        });

        fFilteredList = list;

        return list;
    }

    void handleWidgetSelected() {
        Object[] newSelection = fFilteredList.getSelection();

        if (newSelection.length != fSelection.length) {
            fSelection = newSelection;
            handleSelectionChanged();
        } else {
            for (int i = 0; i != newSelection.length; i++) {
                if (!newSelection[i].equals(fSelection[i])) {
                    fSelection = newSelection;
                    handleSelectionChanged();
                    break;
                }
            }
        }
    }

    /**
     * Creates the list widget and sets layout data.
     * 
     * @param parent the parent composite.
     * @return returns the list table widget.
     */
    protected Table createLowerList( Composite parent ) {
        Table list = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        list.addListener(SWT.Selection, new Listener() {
            public void handleEvent( Event evt ) {
                handleLowerSelectionChanged();
            }
        });

        list.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent( Event evt ) {
                handleDefaultSelected();
            }
        });

        list.addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent e ) {
                fQualifierRenderer.dispose();
            }
        });

        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        list.setLayoutData(data);
        list.setFont(parent.getFont());

        fLowerList = list;

        return list;
    }

    public static int convertWidthInCharsToPixels( FontMetrics fontMetrics,
                                                   int chars ) {
        return fontMetrics.getAverageCharWidth() * chars;
    }

    /**
     * @see SelectionStatusDialog#computeResult()
     */
    protected void computeResult() {
        Object[] results = new Object[] {getLowerSelectedElement()};
        setResult(Arrays.asList(results)); // jhTODO: this method is in SelectionDialog
    }

    /**
     * Set the selections made by the user, or <code>null</code> if the selection was canceled.
     * 
     * @param the list of selected elements, or <code>null</code> if Cancel was pressed
     */
    protected void setResult( List newResult ) {
        if (newResult == null) {
            result = null;
        } else {
            result = new Object[newResult.size()];
            newResult.toArray(result);
        }
    }

    /**
     * @see AbstractElementListSelectionDialog#handleDefaultSelected()
     */
    protected void handleDefaultSelected() {
        handleUpperSelectionChanged();

        if (validateCurrentSelection() && (getLowerSelectedElement() != null)) {
            getHost().okPressed();
        }
    }

    /**
     * @see AbstractElementListSelectionDialog#handleSelectionChanged()
     */
    protected void handleSelectionChanged() {
        validateCurrentSelection();
        handleUpperSelectionChanged();
        handleLowerSelectionChanged();
    }

    /**
     * Validates the current selection and updates the status line accordingly.
     */
    protected boolean validateCurrentSelection() {
        Assert.isNotNull(fFilteredList);

        IStatus status;

        // must first convert the 'record object' into an EObject
        EObject eoSelection = getSelectedEObject();
        Object[] elements = new Object[] {eoSelection};

        // now validate
        if (eoSelection == null) {
            status = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.ERROR, fEmptySelectionMessage, null);

        } else if (elements.length > 0) {
            if (fValidator != null) {
                status = fValidator.validate(elements);
            } else {
                status = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", //$NON-NLS-1$
                                    null);
            }
        } else {
            if (fFilteredList.isEmpty()) {
                status = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.ERROR, fEmptyListMessage, null);
            } else {
                status = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.ERROR, fEmptySelectionMessage, null);
            }
        }

        stCurrentStatus = status;
        getHost().updateTheStatus(stCurrentStatus);

        return status.isOK();
    }

    public Object[] getResult() {
        return new Object[] {getSelectedEObject()};
    }

    /**
     * Returns an array of the currently selected elements. To be called within or after open().
     * 
     * @return returns an array of the currently selected elements.
     */
    protected Object[] getSelectedElements() {
        Assert.isNotNull(fFilteredList);
        return fFilteredList.getSelection();
    }

    void handleUpperSelectionChanged() {
        int index = getSelectionIndex();

        fLowerList.removeAll();

        if (index < 0) return;

        fQualifierElements = getFoldedElements(index);

        if (fQualifierElements == null) updateLowerListWidget(new Object[] {});
        else updateLowerListWidget(fQualifierElements);

        updateOkState();

        // capture the selection in the upper list
        if (getSelectedElements()[0] != null) {

            Object[] oArySelection = fFilteredList.getSelection();

            if (oArySelection.length > 0) {
                sSelectedObject = ((ResourceObjectRecord)oArySelection[0]).getName();
            }
        }

    }

    /**
     * Returns an index referring the first current selection. To be called within open().
     * 
     * @return returns the indices of the current selection.
     */
    protected int getSelectionIndex() {
        Assert.isNotNull(fFilteredList);
        return fFilteredList.getSelectionIndex();
    }

    void handleLowerSelectionChanged() {
        updateOkState();

        // capture the selection in the lower list
        if (getLowerSelectedElement() != null) {
            sSelectedPath = ((ResourceObjectRecord)getLowerSelectedElement()).getResourcePath();
        }
    }

    public void updateOkState() {

    }

    /**
     * Selects an element in the lower pane.
     */
    protected void setLowerSelectedElement( Object element ) {
        if (fQualifierElements == null) return;

        // find matching index
        int i;
        for (i = 0; i != fQualifierElements.length; i++)
            if (fQualifierElements[i].equals(element)) break;

        // set selection
        if (i != fQualifierElements.length) fLowerList.setSelection(i);
    }

    /**
     * Returns the selected element from the lower pane.
     */
    protected Object getLowerSelectedElement() {
        int index = fLowerList.getSelectionIndex();

        if (index >= 0) return fQualifierElements[index];

        return null;
    }

    private void updateLowerListWidget( Object[] elements ) {
        int length = elements.length;

        String[] qualifiers = new String[length];
        for (int i = 0; i != length; i++)
            qualifiers[i] = fQualifierRenderer.getText(elements[i]);

        TwoArrayQuickSorter sorter = new TwoArrayQuickSorter(isCaseIgnored());
        sorter.sort(qualifiers, elements);

        for (int i = 0; i != length; i++) {
            TableItem item = new TableItem(fLowerList, SWT.NONE);
            item.setText(qualifiers[i]);
            item.setImage(fQualifierRenderer.getImage(elements[i]));
        }

        if (fLowerList.getItemCount() > 0) fLowerList.setSelection(0);
    }

    /**
     * Returns if sorting, filtering and folding is case sensitive.
     */
    public boolean isCaseIgnored() {
        return fIgnoreCase;
    }

    /**
     * Handles empty list by disabling widgets.
     */
    protected void handleEmptyList() {
        fMessage.setEnabled(false);
        fFilterText.setEnabled(false);
        fFilteredList.setEnabled(false);
        fLowerList.setEnabled(false);
        updateOkState();
    }

    // =================================
    // interface: IFinderPanel
    // =================================

    public void createButtonsForButtonBar( Composite parent ) {
        // no action
    }

    public void handleOkPressed() {

    }

    public void handleCancelPressed() {

    }

    public void updateOKStatus() {
        /*
         * always update with an OK status, because there is no way to pick the 'wrong' thing
         * in this panel.
         */

        IStatus fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", //$NON-NLS-1$
                                         null);
        getHost().updateTheStatus(fCurrStatus);
    }

    public EObject getSelectedEObject() {
        if (sSelectedObject == null) {
            return null;
        }

        // 1. get the string in the selected upper table row
        String sName = sSelectedObject;

        // 2. use the string to get the 'dupes' from the hashmap
        ArrayList aryl = (ArrayList)hmUniqueNames.get(sName);

        Iterator it = aryl.iterator();
        ResourceObjectRecord rorSelected = null;

        // walk the 'folded' entries, looking for a match with the pathname
        // the user selected in the 'qualifier' table (lower table)
        while (it.hasNext()) {
            ResourceObjectRecord rorTemp = (ResourceObjectRecord)it.next();

            if (sSelectedPath.equals(rorTemp.getResourcePath())) {
                rorSelected = rorTemp;
                break;
            }
        }

        EObject eObj = null;
        try {
            URI uri = URI.createURI(rorSelected.getObjectURI());

            eObj = ModelerCore.getModelContainer().getEObject(uri, true);

        } catch (CoreException ce) {
            ModelerCore.Util.log(IStatus.ERROR, ce, ce.getMessage());
        }

        return eObj;
    }

    public String getTitle() {
        return TITLE;
    }

    /**
     * Sets an optional validator to check if the selection is valid. The validator is invoked whenever the selection changes.
     * 
     * @param validator the validator to validate the selection.
     */
    public void setValidator( ISelectionStatusValidator validator ) {
        fValidator = validator;
    }

}
