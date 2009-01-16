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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.AbstractElementListSelectionDialog;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * ModelObjectSelectionDialog Adapted from the eclipse class TwoPaneElementSelector, because that class had the functionality but
 * lacked extensibility.
 * 
 * @since 4.2
 */
public class ModelObjectSelectionDialog extends AbstractElementListSelectionDialog {

    private static final String TITLE = UiConstants.Util.getString("ModelObjectSelectionDialog.title"); //$NON-NLS-1$

    private static final String TEXT_ENTRY_LABEL_TEXT = UiConstants.Util.getString("ModelObjectSelectionDialog.textEntryLabel.text"); //$NON-NLS-1$
    private static final String UPPER_TABLE_LABEL_TEXT = UiConstants.Util.getString("ModelObjectSelectionDialog.upperTableLabel.text"); //$NON-NLS-1$
    private static final String LOWER_TABLE_LABEL_TEXT = UiConstants.Util.getString("ModelObjectSelectionDialog.lowerTableLabel.text"); //$NON-NLS-1$    
    private static final String DOT = "."; //$NON-NLS-1$ 

    private HashMap hmUniqueNames;
    private String sSelectedObject;
    private String sSelectedPath;

    private String fUpperListLabel;
    private String fLowerListLabel;
    ILabelProvider fQualifierRenderer;
    private Object[] fElements = new Object[0];

    private Table fLowerList;
    private Object[] fQualifierElements;
    private boolean bUserCancelledDuringLoad = false;

    /**
     * @param parent
     * @since 4.2
     */
    public ModelObjectSelectionDialog( Shell parent ) {
        super(parent, new FindObjectLabelProvider(FindObjectLabelProvider.OBJECT));

        fQualifierRenderer = new FindObjectLabelProvider(FindObjectLabelProvider.CONTAINER);
        setSize(50, 15);
        setAllowDuplicates(false);

        setTitle(TITLE);

        setMessage(TEXT_ENTRY_LABEL_TEXT);
        setUpperListLabel(UPPER_TABLE_LABEL_TEXT);
        setLowerListLabel(LOWER_TABLE_LABEL_TEXT);

        doLoadObjects();

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
            dlg.run(true, true, op);

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

        if (colRecordObjects == null) {
            return;
        }

        // create a data structure to relate the names to their URI(s) (could be dupes).
        hmUniqueNames = new HashMap();

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

    public EObject getSelectedEObject() {

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

            if (sSelectedPath.equals(getFullPathForRecord(rorTemp))) {
                rorSelected = rorTemp;
                break;
            }
        }

        return getEObjectForRecord(rorSelected);
    }

    public EObject getEObjectForRecord( ResourceObjectRecord ror ) {

        EObject eObj = null;
        try {
            URI uri = URI.createURI(ror.getObjectURI());
            if (uri.fragment() != null) eObj = ModelerCore.getModelContainer().getEObject(uri, true);

        } catch (CoreException ce) {
            ModelerCore.Util.log(IStatus.ERROR, ce, ce.getMessage());
        }

        return eObj;
    }

    @Override
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
    @Override
    public Control createDialogArea( final Composite parent ) {
        // add the rest of the components:
        Composite contents = (Composite)super.createDialogArea(parent);

        createMessageArea(contents);
        createFilterText(contents);
        createLabel(contents, fUpperListLabel);
        Control listCtrl = createFilteredList(contents);
        final GridData gdlc = (GridData)listCtrl.getLayoutData();
        gdlc.heightHint = convertHeightInCharsToPixels(8);
        gdlc.widthHint = SWT.DEFAULT;

        createLabel(contents, fLowerListLabel);
        createLowerList(contents);

        setListElements(fElements);

        List initialSelections = getInitialElementSelections();
        if (!initialSelections.isEmpty()) {
            Object element = initialSelections.get(0);

            setSelection(new Object[] {element});
            setLowerSelectedElement(element);
        }

        return contents;
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
        // data.widthHint= convertWidthInCharsToPixels(50);
        data.heightHint = convertHeightInCharsToPixels(8);
        data.grabExcessVerticalSpace = true;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        list.setLayoutData(data);
        list.setFont(parent.getFont());

        fLowerList = list;

        return list;
    }

    /**
     * @see SelectionStatusDialog#computeResult()
     */
    @Override
    protected void computeResult() {
        Object[] results = new Object[] {getLowerSelectedElement()};
        setResult(Arrays.asList(results));
    }

    /**
     * @see AbstractElementListSelectionDialog#handleDefaultSelected()
     */
    @Override
    protected void handleDefaultSelected() {
        handleUpperSelectionChanged();

        if (validateCurrentSelection() && (getLowerSelectedElement() != null)) buttonPressed(IDialogConstants.OK_ID);
    }

    /**
     * @see AbstractElementListSelectionDialog#handleSelectionChanged()
     */
    @Override
    protected void handleSelectionChanged() {
        super.handleSelectionChanged();
        handleUpperSelectionChanged();
        handleLowerSelectionChanged();
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

    void handleLowerSelectionChanged() {
        updateOkState();

        // capture the selection in the lower list
        if (getLowerSelectedElement() != null) {

            ResourceObjectRecord rorSelected = (ResourceObjectRecord)getLowerSelectedElement();
            sSelectedPath = getFullPathForRecord(rorSelected);
        }
    }

    private String getFullPathForRecord( ResourceObjectRecord ror ) {
        String sPath = ""; //$NON-NLS-1$

        EObject eo = getEObjectForRecord(ror);
        if (eo != null) {
            IPath path = ModelerCore.getModelEditor().getModelRelativePath(eo);
            sPath = ror.getResourcePath() + path.makeAbsolute().toString();
        } else {
            sPath = ror.getResourcePath();
        }
        return sPath;
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

    /*
     * @see AbstractElementListSelectionDialog#handleEmptyList()
     */
    @Override
    protected void handleEmptyList() {
        super.handleEmptyList();
        fLowerList.setEnabled(false);
    }

    @Override
    protected void updateOkState() {
        super.updateOkState();

        // if we pass other OK tests, check to see that the Container does not end
        // in 'xmi', which indicates an incomplete path. In that case disable OK.
        if (getOkButton().isEnabled()) {
            Object oSelected = getLowerSelectedElement();
            String sContainerLabel = fQualifierRenderer.getText(oSelected);
            String sContainerLabelLowerCase = sContainerLabel.toLowerCase();

            if (sContainerLabelLowerCase.endsWith(DOT + ModelUtil.EXTENSION_XMI)
                || sContainerLabelLowerCase.endsWith(DOT + ModelUtil.EXTENSION_XML)
                || sContainerLabelLowerCase.endsWith(DOT + ModelUtil.EXTENSION_XSD)
                || sContainerLabelLowerCase.endsWith(DOT + ModelUtil.EXTENSION_VDB)) {

                getOkButton().setEnabled(false);
            }
        }
    }

}
