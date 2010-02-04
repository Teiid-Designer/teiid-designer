/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource;

/**
 * SqlAliasAccumulatorSource
 */
public class SqlAliasAccumulatorSource implements IAccumulatorSource {

    private static final IStatus OK_STATUS = new StatusInfo(UiConstants.PLUGIN_ID);
    
    //We will be using a Table to show the data.  However, we will NOT be using a TableViewer with
    //the table.  The reason is this:  Whenever an item is removed from this table (moved to the
    //"selected" side in the accumulator), then later reinserted into this side, we would like to be
    //able to reinsert it into its original position.  That is not feasible with a TableViewer.  
    //With a TableViewer one must either supply a sorter, which is not what we want, or always 
    //insert the items at the end of the table, which is likewise not what we want.
    //
    //But, seeing as SWT provides no means to correlate an Object itself with a row in the
    //Table, we will keep an up-to-date list of Objects represented in the table (currentValues).  
    //Table only provides means to get Strings and Images pertaining to rows in a table, but not 
    //Objects that they are supposed to represent, which I find very strange.  BWP.
     
    private ILabelProvider labelProvider;
    private java.util.List /*<Object>*/ initialValues;
    private java.util.List /*<Object>*/ currentValues = new ArrayList();
    private Table table;
            
    public SqlAliasAccumulatorSource(ILabelProvider labelProvider,
            java.util.List /*<Object>*/ initialValues) {
        super();
        this.labelProvider = labelProvider;
        this.initialValues = initialValues;
    }
    
    public void accumulatedValuesRemoved(Collection values) {
        //Any items that were originally in our table we will reinsert into the same relative 
        //location.  Any ones that did not start here we will insert at the end.
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            int index = indexForValueInserting(obj);
            TableItem tableItem = new TableItem(table, 0, index);
            Image image = labelProvider.getImage(obj);
            String text = labelProvider.getText(obj);
            tableItem.setImage(image);
            tableItem.setText(text);
            currentValues.add(index, obj);
        }
    }
    
    public void accumulatedValuesAdded(Collection values) {
        Iterator it = values.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            int index = indexForValueRemoving(value);
            table.remove(index);
            currentValues.remove(index);
        }
    }
    
    public Collection getAvailableValues() {
        Collection itemsColl = new ArrayList(currentValues.size());
        Iterator it = currentValues.iterator();
        while (it.hasNext()) {
            itemsColl.add(it.next());
        }
        return itemsColl;
    }
    
    public int getAvailableValuesCount() {
        int count = table.getItemCount();
        return count;
    }
    
    public Collection getSelectedAvailableValues() {
        int[] itemIndices = table.getSelectionIndices();
        Collection itemsColl = new ArrayList(itemIndices.length);
        for (int i = 0; i < itemIndices.length; i++) {
            Object obj = currentValues.get(itemIndices[i]);
            itemsColl.add(obj);
        }
        return itemsColl;
    }
    
    public int getSelectedAvailableValuesCount() {
        int count = table.getSelectionCount();
        return count;
    }
    
    public Control createControl(Composite parent) {
        //Create the table
        table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData tableGridData = new GridData();
        tableGridData.widthHint = 200;
        tableGridData.verticalAlignment = GridData.FILL;
        tableGridData.horizontalAlignment = GridData.FILL;
        tableGridData.grabExcessHorizontalSpace= true;
        tableGridData.grabExcessVerticalSpace= true;
        table.setLayoutData(tableGridData);
        
        //Populate the table
        int loc = 0;
        Iterator it = this.initialValues.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            TableItem tableItem = new TableItem(table, 0, loc);
            Image image = labelProvider.getImage(obj);
            String text = labelProvider.getText(obj);
            tableItem.setImage(image);
            tableItem.setText(text);
            currentValues.add(obj);
            loc++;
        }
        
        return table;
    }
    
    public void addSelectionListener(SelectionListener listener) {
        table.addSelectionListener(listener);
    }
    
    private int indexForValueRemoving(Object value) {
        int index = currentValues.indexOf(value);
        return index;
    }
    
    private int indexForValueInserting(Object value) {
        int index = -1;
        int originalIndex = initialValues.indexOf(value);
        if (originalIndex < 0) {
            index = currentValues.size();
        } else {
            //We will attempt to find in the current list the object that was just before this one
            //in the original list.  If found, we know that this one should be inserted right after
            //it.  If not found, we will look for the object that was right before that object, etc.
            //If no object that was before this one in the original list was found in the current
            //list, then we will return 0, to insert the object at the beginning of the table.
            boolean found = false;
            int loc = originalIndex - 1;
            while ((!found) && (loc >= 0)) {
                Object objectLookingFor = initialValues.get(loc);
                int curIndexOfObject = currentValues.indexOf(objectLookingFor);
                if (curIndexOfObject >= 0) {
                    found = true;
                    index = curIndexOfObject + 1;
                } else {
                    loc--;
                }
            }
            if (!found) {
                index = 0;
            }
        }
        return index;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#supportsAddAll()
     */
    public boolean supportsAddAll() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getSelectionStatus()
     */
    public IStatus getSelectionStatus() {
        return OK_STATUS;
    }

}//end ModelObjectAccumulatorSourceImpl
