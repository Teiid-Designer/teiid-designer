/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.table;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.ui.table.DoubleClickTableViewer;
import com.metamatrix.ui.table.TableViewerSorter;

/**
 * @author SDelap
 *
 * This viewer contains an optimized indexForElement method that better handles
 * when all rows are unsorted.
 */
public class ModelTableEditorViewer extends DoubleClickTableViewer {
    public ModelTableEditorViewer(Composite parent, int style) {
        super(parent, style);
        //this.setUseHashlookup(true);
    }
    
    /**
	 * In the event a table is unsorted all rows are added to the bottom
     * Otherwise they are added in the first position that is found where they are equal 
     * that rows sort value.
	**/
    @Override
    protected int indexForElement(Object element) {
        ViewerSorter sorter = getSorter();
        if (sorter == null || !(sorter instanceof TableViewerSorter) || ((TableViewerSorter) sorter).isUnsorted()) {
            return getTable().getItemCount();
        }
  
        int count = getTable().getItemCount();
        int min = 0, max = count - 1;
        while (min <= max) {
        	int mid = (min + max) / 2;
        	Object data = getTable().getItem(mid).getData();
        	int compare = sorter.compare(this, data, element);
        	if (compare == 0) {
        	    return mid;
        	}
        	if (compare < 0)
        		min = mid + 1;
        	else
        		max = mid - 1;
        }
        return min;
    }
}

