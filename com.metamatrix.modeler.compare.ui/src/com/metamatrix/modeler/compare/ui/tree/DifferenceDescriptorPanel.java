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

package com.metamatrix.modeler.compare.ui.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.PropertyDifference;
import com.metamatrix.modeler.compare.ui.UiConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * DifferenceDescriptorPanel.
 */
public class DifferenceDescriptorPanel extends Composite implements UiConstants, StringUtil.Constants {

    private static final String PREFIX = "DifferenceDescriptorPanel."; //$NON-NLS-1$
    private static final String[] TBL_HDRS_OLDNEW = new String[] {StringUtil.Constants.EMPTY_STRING,
        Util.getString(PREFIX + "nameHdr"), //$NON-NLS-1$
        Util.getString(PREFIX + "oldValueHdr"), //$NON-NLS-1$
        Util.getString(PREFIX + "newValueHdr")}; //$NON-NLS-1$

    private static final String[] TBL_HDRS_FIRSTSECOND = new String[] {StringUtil.Constants.EMPTY_STRING,
        Util.getString(PREFIX + "nameHdr"), //$NON-NLS-1$
        Util.getString(PREFIX + "firstValueHdr"), //$NON-NLS-1$
        Util.getString(PREFIX + "secondValueHdr")}; //$NON-NLS-1$

    private static final String NO_SELECTION = Util.getString(PREFIX + "noSelection.text"); //$NON-NLS-1$
    private static final String SELECTION_IS_DELETION = Util.getString(PREFIX + "selectionIsDeletion.text"); //$NON-NLS-1$
    private static final String SELECTION_IS_ADDITION = Util.getString(PREFIX + "selectionIsAddition.text"); //$NON-NLS-1$
    private static final String SELECTION_IS_CHANGED = Util.getString(PREFIX + "selectionIsChanged.text"); //$NON-NLS-1$
    private static final String SELECTION_IS_UNCHANGED = Util.getString(PREFIX + "selectionIsUnchanged.text"); //$NON-NLS-1$
    private static final String SELECTION_IS_CHANGE_BELOW = Util.getString(PREFIX + "selectionIsChangeBelow.text"); //$NON-NLS-1$

    private CLabel label;
    private ILabelProvider labelProvider = DifferenceAnalysis.getMappingLabelProvider();
    private ILabelProvider propTableLabelProvider;

    private TableViewer tableViewer;
    private Table table;
    List<PropertyDifference> tableRowList;
    private String title;
    private boolean enablePropSelection;
    private boolean showCheckboxes;
    private int iTerminology = DifferenceReportsPanel.USE_OLD_NEW_TERMINOLOGY;

    /**
     * constructor
     * 
     * @param the parent composite
     * @param title the title for the composite
     */
    public DifferenceDescriptorPanel( Composite parent,
                                      String title,
                                      boolean enablePropertySelection,
                                      boolean showCheckboxes,
                                      int iTerminology ) {
        super(parent, SWT.NONE);
        this.title = title;
        this.enablePropSelection = enablePropertySelection;
        this.showCheckboxes = showCheckboxes;
        this.iTerminology = iTerminology;

        initialize();
    }

    /**
     * get the Composite's TableViewer
     * 
     * @return the TableViewer
     */
    public TableViewer getTableViewer() {
        return this.tableViewer;
    }

    /**
     * Initialize the Panel
     */
    private void initialize() {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        this.setLayoutData(new GridData(GridData.FILL_BOTH));

        Group group = WidgetFactory.createGroup(this, title, GridData.FILL_BOTH);

        label = WidgetFactory.createLabel(group, GridData.FILL_HORIZONTAL);
        label.setText(EMPTY_STRING);

        // CheckBox TableViewer
        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;

        if (showCheckboxes) {
            tableViewer = CheckboxTableViewer.newCheckList(group, style);
        } else {
            tableViewer = new TableViewer(group, style);
        }

        table = tableViewer.getTable();
        table.setLayout(new GridLayout());
        GridData gd = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        // create columns
        String[] sHeaders = new String[] {""}; //$NON-NLS-1$;

        if (iTerminology == DifferenceReportsPanel.USE_OLD_NEW_TERMINOLOGY) {
            sHeaders = TBL_HDRS_OLDNEW;
        } else if (iTerminology == DifferenceReportsPanel.USE_FIRST_SECOND_TERMINOLOGY) {
            sHeaders = TBL_HDRS_FIRSTSECOND;
        }

        for (int i = 0; i < sHeaders.length; i++) {
            TableColumn column = new TableColumn(table, SWT.LEFT);
            column.setText(sHeaders[i]);
            if (this.enablePropSelection == false) {
                column.setWidth(0);
            }
        }

        tableViewer.setContentProvider(new TableViewerContentProvider());
        this.propTableLabelProvider = new TableViewerLabelProvider();
        tableViewer.setLabelProvider(this.propTableLabelProvider);

        // populate table using empty list.
        this.tableRowList = new ArrayList<PropertyDifference>();
        tableViewer.setInput(tableRowList);
    }

    private void setColumnHeaderText() {
        String[] sHeaders = new String[] {""}; //$NON-NLS-1$;

        if (iTerminology == DifferenceReportsPanel.USE_OLD_NEW_TERMINOLOGY) {
            sHeaders = TBL_HDRS_OLDNEW;
        } else if (iTerminology == DifferenceReportsPanel.USE_FIRST_SECOND_TERMINOLOGY) {
            sHeaders = TBL_HDRS_FIRSTSECOND;
        }

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumn(i);
            column.setText(sHeaders[i]);
            if (this.enablePropSelection == false) {
                column.setWidth(0);
            }
        }
    }

    public void setTerminologyStyle( int iTerminology ) {
        this.iTerminology = iTerminology;
        setColumnHeaderText();
    }

    /**
     * Initialize the checkboxtable checkbox states using the PropertyDifference skip flag
     */
    public void initCheckStates() {
        TableItem[] tableItems = tableViewer.getTable().getItems();
        for (int i = 0; i < tableItems.length; i++) {
            TableItem item = tableItems[i];
            Object itemData = item.getData();
            if (itemData instanceof PropertyDifference) {
                PropertyDifference propDiff = (PropertyDifference)itemData;
                if (propDiff.isSkip()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
            }
        }
    }

    /**
     * Clear
     */
    public void clear() {
        setDescriptor(null);
    }

    /**
     * set a new DifferenceDescriptor for the panel. Updates the label and the table contents.
     * 
     * @param descriptor the DifferenceDescriptor to use
     */
    public void setDescriptor( DifferenceDescriptor descriptor ) {
        // Set the label text depending on the type of descriptor
        if (descriptor != null) {
            Mapping mapping = descriptor.getMapper();
            StringBuffer sb = new StringBuffer();
            sb.append(this.labelProvider.getText(mapping));
            if (DifferenceAnalysis.isAdd(mapping)) {
                sb.append(" - " + SELECTION_IS_ADDITION); //$NON-NLS-1$
            } else if (DifferenceAnalysis.isDelete(mapping)) {
                sb.append(" - " + SELECTION_IS_DELETION); //$NON-NLS-1$
            } else if (DifferenceAnalysis.isChange(mapping)) {
                sb.append(" - " + SELECTION_IS_CHANGED); //$NON-NLS-1$
            } else if (DifferenceAnalysis.isUnchanged(mapping)) {
                sb.append(" - " + SELECTION_IS_UNCHANGED); //$NON-NLS-1$
            } else if (DifferenceAnalysis.isChangeBelow(mapping)) {
                sb.append(" - " + SELECTION_IS_CHANGE_BELOW); //$NON-NLS-1$
            }

            label.setText(sb.toString());
        } else {
            label.setText(NO_SELECTION);
        }
        // Update the table contents
        updateTableRows(descriptor);
    }

    /**
     * Update the tableContents for the provided DifferenceDescriptor
     * 
     * @param descriptor the DifferenceDescriptor
     */
    private void updateTableRows( DifferenceDescriptor descriptor ) {
        Mapping mapping = null;
        if (descriptor != null) {
            // Get the descriptor mapping
            mapping = descriptor.getMapper();
            // If the DifferenceDescriptor is 'Change' type, set the table contents
            if (mapping != null && DifferenceAnalysis.isChange(mapping)) {
                // Clear current contents
                this.tableRowList.clear();
                // Add new PropertyDifferences to the table
                Iterator iter = descriptor.getPropertyDifferences().iterator();
                while (iter.hasNext()) {
                    PropertyDifference propDiff = (PropertyDifference)iter.next();
                    this.tableRowList.add(propDiff);
                }
                this.tableViewer.refresh();
                // Initialize the Checkbox states
                initCheckStates();
                // Pack Columns
                final TableColumn[] cols = table.getColumns();
                for (int ndx = 0; ndx < cols.length; ++ndx) {
                    if (ndx == 0 && this.enablePropSelection == false) {
                        cols[ndx].setWidth(0);
                    } else {
                        cols[ndx].pack();
                    }
                }
                // show the table
                this.table.setVisible(true);
            } else {
                // Clear contents , dont show table
                this.tableRowList.clear();
                this.tableViewer.refresh();
                this.table.setVisible(false);
            }
        } else {
            this.tableRowList.clear();
            this.tableViewer.refresh();
            this.table.setVisible(false);
        }
    }

    class TableViewerContentProvider implements IStructuredContentProvider {

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object theInputElement ) {
            return tableRowList.toArray();
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
        }

    }

    class TableViewerLabelProvider extends LabelProvider implements ITableLabelProvider {

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            Image result = null;
            switch (theColumnIndex) {
                case 2:
                case 3:
                    IPropertySource source = ModelObjectUtilities.getEmfPropertySourceProvider().getPropertySource(theElement);
                    IPropertyDescriptor[] descs = source.getPropertyDescriptors();
                    ILabelProvider propertyLabelProvider = descs[1].getLabelProvider();
                    if (propertyLabelProvider != null) {
                        result = propertyLabelProvider.getImage(theElement);
                    }
                    break;
                default:
                    break;
            }
            return result;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            String result = null;

            if (theElement instanceof PropertyDifference) {
                PropertyDifference row = (PropertyDifference)theElement;

                IPropertySource source = ModelObjectUtilities.getEmfPropertySourceProvider().getPropertySource(row);
                IPropertyDescriptor[] descs = source.getPropertyDescriptors();
                ILabelProvider propertyLabelProvider = descs[1].getLabelProvider();
                switch (theColumnIndex) {
                    case 1:
                        EStructuralFeature feature = row.getAffectedFeature();
                        if (feature != null) {
                            result = feature.getName();
                        }
                        break;
                    case 2:
                        Object old = row.getOldValue();
                        result = getValueString(old, propertyLabelProvider);
                        break;
                    case 3:
                        Object newVal = row.getNewValue();
                        result = getValueString(newVal, propertyLabelProvider);
                        break;
                    default:
                        result = ""; //$NON-NLS-1$
                        break;
                }
            } else {
                result = super.getText(theElement);
            }

            return result;
        }
    }

    String getValueString( Object value,
                           ILabelProvider propertyLabelProvider ) {
        String result = null;
        if (propertyLabelProvider != null) {
            if (value instanceof List) {
                String str = new String();
                for (Iterator iter = ((List)value).iterator(); iter.hasNext();) {
                    Object o = iter.next();
                    if (o instanceof EObject) {
                        str += ModelUtilities.getEMFLabelProvider().getText(o);
                    } else {
                        str += propertyLabelProvider.getText(o);
                    }
                    if (iter.hasNext()) {
                        str += "; "; //$NON-NLS-1$
                    }
                }
                result = str;
            } else {
                result = propertyLabelProvider.getText(value);
            }
        } else if (value != null) {
            result = value.toString();
        }
        return result;
    }

}// end DifferenceDescriptorPanel
