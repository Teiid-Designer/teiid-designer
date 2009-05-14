/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.util;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.List;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * 
 * OrderableViewerSorter
 * 
 * Extension to {@link ViewerSorter} in which sorting can be turned off and on.  This was done to
 * accomodate the needs of a table in 
 * {@link com.metamatirx.ui.internal.widget.accumulator.AccumulatorPanel}.
 * 
 * User may call the setStringsOrder() method to set an order for the strings.  If called with a
 * list of strings, this list is expected to include all of the strings that may be compared for
 * collation, in the desired order.  "Sorting" two items then merely reflects their positions in
 * this list.  See examples in {@link com.metamatrix.ui.internal.widget.accumulator.AccumulatorPanel}.  
 * If setStringsOrder() is called with a null list, alphabetic sorting is reinstated.
 */
public class OrderableViewerSorter extends ViewerSorter {
	//==================================================
	// Instance variables
	//==================================================
	//Overridden collator
	private OrderableCollator collator;
	
	//==================================================
	// Constructors
	//==================================================
	
	/**
	 * Parameterless constructor.
	 */
	public OrderableViewerSorter() {
		super();
		try {
			this.collator = new OrderableCollator();
		} catch (Exception ex) {
			//cannot happen
		}
	}
	
	//===================================================
	// Instance methods
	//===================================================
	/**
	 * Get the collator
	 * @return  the Collator
	 */
	@Override
    public Collator getCollator() {
		return this.collator;
	}
	
	/**
	 * BWP 06/05/03 Have to include this method (copied from {@link ViewerSorter}) to change last
	 * statement from collator.compare(... to getCollator().compare(.  Overriding getCollator() was 
	 * causing nothing to happen because of this apparent mistake in the method.
	 * 
	 * Returns a negative, zero, or positive number depending on whether
	 * the first element is less than, equal to, or greater than
	 * the second element.
	 * <p>
	 * The default implementation of this method is based on
	 * comparing the elements' categories as computed by the <code>category</code>
	 * framework method. Elements within the same category are further 
	 * subjected to a case insensitive compare of their label strings, either
	 * as computed by the content viewer's label provider, or their 
	 * <code>toString</code> values in other cases. Subclasses may override.
	 * </p>
	 * 
	 * @param viewer the viewer
	 * @param e1 the first element
	 * @param e2 the second element
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	@Override
    public int compare(Viewer viewer, Object e1, Object e2) {
	
		int cat1 = category(e1);
		int cat2 = category(e2);
	
		if (cat1 != cat2)
			return cat1 - cat2;
	
		// cat1 == cat2
	
		String name1;
		String name2;
	
		if (viewer == null || !(viewer instanceof ContentViewer)) {
			name1 = e1.toString();
			name2 = e2.toString();
		} else {
			IBaseLabelProvider prov = ((ContentViewer) viewer).getLabelProvider();
			if (prov instanceof ILabelProvider) {
				ILabelProvider lprov = (ILabelProvider) prov;
				name1 = lprov.getText(e1);
				name2 = lprov.getText(e2);
			} else {
				name1 = e1.toString();
				name2 = e2.toString();
			}
		}
		if (name1 == null)
			name1 = "";//$NON-NLS-1$
		if (name2 == null)
			name2 = "";//$NON-NLS-1$
		return getCollator().compare(name1, name2);
	}
	
	/**
	 * Set an order of all Strings that may be collated.  If non-null, comparing two strings merely
	 * reflects their positions in this list.  So for instance, if an item is to be added to the
	 * end of a table, the list should reflect the current contents of the table with the new item
	 * included at the end.  See examples in {@link com.metamatrix.ui.internal.widget.accumulator.AccumulatorPanel}.
	 * If null, alphabetic sorting is reinstated.
	 * 
	 * @param order   Ordered list of strings, or null to reinstate alphabetic sorting
	 */
	public void setStringsOrder(List<String> order) {
		this.collator.setStringsOrder(order);
	}
}//end OrderableViewerSorter




/**
 * 
 * OrderableCollator
 * 
 * Extension to {@link RuleBasedCollator to override the compare() method.  A java.util.List of
 * Strings can be supplied.  If they are present, they are expected to represent all of the items
 * that may be compared for collation, in the desired order.  If this list is not present, then
 * the comparison is performed by the superclass, in other words alphabetic.  This was developed for 
 * a table shown in {@link com.metamatrix.ui.internal.widget.accumulator.AccumulatorPanel}, in which 
 * alphabetic sorting can be turned on and off, hence the sorter needs to be able to sort 
 * alphabetically or "sort" from an ordered list.
 */
class OrderableCollator extends RuleBasedCollator {
	//====================================================
	// Instance variables
	//====================================================
	private List<String> orderedStrings = null;
	
	//====================================================
	// Constructors
	//====================================================
	public OrderableCollator() throws Exception {
		super(((RuleBasedCollator)Collator.getInstance()).getRules());
	}
	
	//====================================================
	// Instance methods
	//====================================================
	
	/**
	 * Set the ordered list to replace the alphabetic sort.  If setting to null, alphabetic
	 * sorting is turned on.
	 * @param order		Ordered list of strings to replace alphabetic sorting, or null to 
	 * 					instate alphabetic sorting
	 */
	public void setStringsOrder(List<String> order) {
		this.orderedStrings = order;
	}
	
	/**
	 * Overridden compare() method.  If list is present, do "comparison" based on order in list,
	 * else have superclass do comparison.
	 * @param source   first string
	 * @param target   second string
	 * @return <0, 0, or >0, same as superclass
	 */
	@Override
    public int compare(String source, String target) {
		int val;
		if (orderedStrings == null) {
			val = super.compare(source, target);
		} else {
			int index1 = orderedStrings.indexOf(source);
			int index2 = orderedStrings.indexOf(target);
			if ((index1 < 0) || (index2 < 0)) {
				val = super.compare(source, target);
			} else {
				val = index1 - index2;
			}
		}
		return val;
	}
}//end OrderableCollator
