/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget.accumulator;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * IAccumulatorSource
 */
public interface IAccumulatorSource {
	//=======================================================
	// Instance methods
	//=======================================================
	
    /**
     * Called by {@link AccumulatorPanel} to determine if this source supports the Add All button.
     * @return true if this panel supports Add All, false if it does not.
     */
    boolean supportsAddAll();
    
	/**
	 * Called by {@link AccumulatorPanel} to notify this source that accumulated values have
	 * been removed.  The source implementation must then add these values to its collection
	 * of available values.
	 * @param values   Collection of Objects that have been removed from the accumulated side, 
	 * 					which the implementer should add to the available side.
	 */
	void accumulatedValuesRemoved(Collection values);
	
	/**
	 * Called by {@link AccumulatorPanel} to notify this source that accumulated values have
	 * been added.  The source implementation may then remove these values from its collection
	 * of available values.
	 * @param values   Collection of Objects that have been added to the accumulated side, which 
	 * 					the implementer should then remove from the available side
	 */
	void accumulatedValuesAdded(Collection values);
	
	/**
	 * Called by {@link AccumulatorPanel} to obtain the collection of available values.
	 * @return Non-null Collection of Objects which are the currently available values
	 */
	Collection getAvailableValues();

	/**
	 * Called by {@link AccumulatorPanel} to obtain the count of available values.
	 * @return Count of available values
	 */
	int getAvailableValuesCount();
	
	/**
	 * Called by {@link AccumulatorPanel} to obtain the collection of available values that are
	 * currently selected.
	 * @return Non-null Collection of Objects which are the currently selected available values
	 */
	Collection getSelectedAvailableValues();
	
    /**
     * Return an IStatus reflecting the current Selection.  If the IStatus is OK, then the Add
     * button will be enabled.  If the IStatus severity is ERROR, then the Add button will be disabled
     * and the Error message displayed to the user. 
     * @return
     */
    IStatus getSelectionStatus();
    
	/**
	 * Called by {@link AccumulatorPanel} to obtain the count of available values that are
	 * currently selected.
	 * @return Count of available values currently selected
	 */
	int getSelectedAvailableValuesCount();
		
	/**
	 * Called by {@link AccumulatorPanel} during construction to force creation of the Control 
	 * used to display the available source values.  NOTE-- The implementor of this interface 
	 * should not provide layout data for this control (i.e. call setLayoutData()).  The
	 * {@link AccumulatorPanel} will do so.  Constructor parameters for the {@link AccumulatorPanel}
	 * exist for giving height and width hints for this control (or default values may be used).  
	 * The {@link GridData} option FILL_BOTH will also be used.
	 * @param parent  Control being returned must be created with this argument as its parent
	 * @return The Control used to display the available source values
	 */
	Control createControl(Composite parent);
	
	/**
	 * Called by {@link AccumulatorPanel} to add itself as a SelectionListener to the Control 
	 * used to display the available source values.  (This call will be made after calling
	 * createControl().)  This implies that the Control being used must be one which supports 
	 * addSelectionListener(), such as List, Table, or Tree.  This is done so
	 * that the {@link AccumulatorPanel} can at any time know whether or not any items are
	 * selected, and enable/disable its move-left-to-right button accordingly.
	 * @param listener The selection listener to be added to the Control
	 */
	void addSelectionListener(SelectionListener listener);
}
