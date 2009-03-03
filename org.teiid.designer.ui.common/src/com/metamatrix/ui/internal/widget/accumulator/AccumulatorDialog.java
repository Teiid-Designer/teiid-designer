/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget.accumulator;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * AccumulatorDialog
 */
public class AccumulatorDialog extends Dialog {
	//=============================================================
	// Instance variables
	//=============================================================
	private IAccumulatorSource source;
	private String title;
	private String leftSideLabelText;
	private String rightSideLabelText;
	private Collection /*<Object>*/ initialRightSideItems;
	private Collection /*<Object>*/ endingRightSideItems;
	private Collection /*<Object>*/ itemsMovedToRightSide;
	private Collection /*<Object>*/ itemsRemovedFromRightSide;
	private ILabelProvider labelProvider;
	private AccumulatorPanel panel;
	private Composite contents;
	private boolean cancelled = false;
	
	//=============================================================
	// Constructors
	//=============================================================
	/**
	 * AccumulatorDialog constructor.
	 * 
	 * @param parent   parent of this dialog
	 * @param source   handler of the Available (left-hand) side
	 * @param title    dialog display title
	 * @param initialRightSideItems  Collection (String) of items initially to be
	 * 				placed on the Selected (right-hand) side
	 * @param labelProvider  ILabelProvider for the right side list.  May be null, in
	 * 				which case the toString() of each object will be used, with no image.
	 * @leftSideLabelText	optional left side column label.  Default if null is "Available".
	 * @rightSideLabelText  optional right side column label.  Default if null is "Selected".
	 */
	public AccumulatorDialog(Shell parent, IAccumulatorSource source, String title,
			Collection /*<String>*/initialRightSideItems, ILabelProvider labelProvider,
			String leftSideLabelText, String rightSideLabelText) {
		super(parent);
		this.source = source;
		this.title = title;
		this.initialRightSideItems = initialRightSideItems;
		this.labelProvider = labelProvider;
		this.leftSideLabelText = leftSideLabelText;
		this.rightSideLabelText = rightSideLabelText;
    	setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}
		
	/**
	 * AccumulatorDialog constructor.
	 * 
	 * @param parent   parent of this dialog
	 * @param source   handler of the Available (left-hand) side
	 * @param title    dialog display title
	 * @param initialRightSideItems  Collection (String) of items initially to be
	 * 				placed on the Selected (right-hand) side
	 */
	public AccumulatorDialog(Shell parent, IAccumulatorSource source, String title,
			Collection /*<String>*/initialRightSideItems) {
		this(parent, source, title, initialRightSideItems, null, null, null);
	}

	//=============================================================
	// Instance methods
	//=============================================================
	@Override
    protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(this.title);
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		contents = (Composite)super.createDialogArea(parent);
	    panel = new AccumulatorPanel(contents, this.source, this.initialRightSideItems,
				labelProvider, leftSideLabelText, rightSideLabelText);
	    return contents;
	}
	
	@Override
    protected void okPressed() {
		endingRightSideItems = panel.getSelectedItems();
		itemsMovedToRightSide = panel.getItemsMovedToSelected();
		itemsRemovedFromRightSide = panel.getItemsRemovedFromSelected();
		super.okPressed();
	}
	
	@Override
    protected void cancelPressed() {
		cancelled = true;
		endingRightSideItems = initialRightSideItems;
		itemsMovedToRightSide = Collections.EMPTY_LIST;
		itemsRemovedFromRightSide = Collections.EMPTY_LIST;
		super.cancelPressed();
	}
	
	/**
	 * Return boolean indicating whether or not "Cancel" was pressed
	 * @return  true if "Cancel" was pressed
	 */
	public boolean wasCancelled() {
		return cancelled;
	}
	
	/**
	 * Return the items ending up on the selected (right-hand) side
	 * @return Collection (Object) of the items that ended up on the selected (right-hand) side
	 */
	public Collection /*<Object>*/ getSelectedItems() {
		return endingRightSideItems;
	}
	
	/**
	 * Return the items which started on the available side but ended on the selected side
	 * @return Collection (Object) of items which started on the available side but ended on the selected side
	 */
	public Collection /*<Object>*/ getItemsMovedToSelected() {
		return itemsMovedToRightSide;
	}
	
	/**
	 * Return the items which started on the selected side but ended on the available side
	 * @return Collection (Object) of items which started on the selected side but ended on the available side
	 */
	public Collection /*<Object>*/ getItemsRemovedFromSelected() {
		return itemsRemovedFromRightSide;
	}
}//end AccumulatorDialog
