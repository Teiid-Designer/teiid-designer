/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.ui.common.widget;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility dialog which includes a scrollable main panel
 * 
 * Implementers of this class will need to call sizeScrolledPanel() at the end creating the control for the dialog.
 * 
 * EXAMPLE:
 * 
 *      final Composite mainPanel = (Composite)super.createDialogArea(parent);
 *        
 * 		Button checkBox = new Button(mainPanel, SWT.CHECK);
 * 		checkBox.setText("Check Me");
 *      ...
 *      ...
 *                 
 *      sizeScrolledPanel();
 *
 */
public class ScrollableTitleAreaDialog extends TitleAreaDialog {
	
	DefaultScrolledComposite scrolledComposite;
	
	int numColumns;

	public ScrollableTitleAreaDialog(Shell parentShell) {
		super(parentShell);
		this.numColumns = 1;
	}
	
	/**
	 * Allow setting number of columns in the dialogs top-level Composite
	 * @param parentShell
	 * @param nColumns
	 */
	public ScrollableTitleAreaDialog(Shell parentShell, int nColumns) {
		this(parentShell);
		this.numColumns = nColumns;
	}


	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method creates
	 * and returns a new <code>Composite</code> with no margins and spacing.
	 * Subclasses should override.
	 * </p>
	 * 
	 * @param parent
	 *            The parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		// create the top level composite for the dialog area
		scrolledComposite = new DefaultScrolledComposite(parent);
		Composite composite = scrolledComposite.getPanel();
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.numColumns = numColumns;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		// Build the separator line
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData titleBarGD = new GridData(GridData.FILL_HORIZONTAL);
		titleBarGD.horizontalSpan = numColumns;
		titleBarSeparator.setLayoutData(titleBarGD);
		return composite;
	}
	
	public void sizeScrolledPanel() {
		scrolledComposite.sizeScrolledPanel();
	}
}
