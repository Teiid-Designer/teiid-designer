/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.view;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.ui.common.util.LayoutDebugger;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.ScrollableTitleAreaDialog;

public class CreateViewOptionsDialog extends ScrollableTitleAreaDialog {
	private final String TITLE = "Create Virtual Table Options";
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$'
    
    //=============================================================
    // Instance variables
    //=============================================================
    private ViewBuilderManager builder;
    
	Button basicRB, buildFromSourceTableRB;
	
	boolean basicEditor = true;
    
	public CreateViewOptionsDialog(Shell parent,  ViewBuilderManager builder) {
        super(parent);
        this.builder = builder;
	}
    
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(TITLE);
    }
    
    /* (non-Javadoc)
    * @see org.eclipse.jface.window.Window#setShellStyle(int)
    */
    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);

    }
        
    //=============================================================
    // Instance methods
    //=============================================================

    @SuppressWarnings("unused")
	@Override
    protected Control createDialogArea(Composite parent) {
    	setTitle("Select option to create virtual table");
    	
        Composite dialogArea = (Composite)super.createDialogArea(parent);
        ((GridData)dialogArea.getLayoutData()).grabExcessHorizontalSpace = true;
        ((GridData)dialogArea.getLayoutData()).widthHint = 600;
        ((GridData)dialogArea.getLayoutData()).heightHint = 300;
        
        //------------------------------        
        // Set layout for the Composite
        //------------------------------
        Composite composite = WidgetFactory.createPanel(dialogArea);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(composite);
		GridDataFactory.fillDefaults().grab(true, true).hint(500, 300).applyTo(composite);
    	
    	this.basicRB = WidgetFactory.createRadioButton(composite, "Option 1: Build with new table wizard", SWT.NONE, 2, true); //$NON-NLS-1$
//    	this.basicRB.setToolTipText(getString("noRowDelimeterTooltip"));  //$NON-NLS-1$
    	this.basicRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	basicEditor = basicRB.getSelection();
//            	handleOptionChange();
            }
        });
    	
    	this.buildFromSourceTableRB = WidgetFactory.createRadioButton(composite, "Option 2: Build using columns from source table", SWT.NONE, 1, false); //$NON-NLS-1$  //$NON-NLS-2$
//    	this.buildFromSourceTableRB.setToolTipText(getString("customCharacterTooltip")); //$NON-NLS-1$
    	this.buildFromSourceTableRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	basicEditor = basicRB.getSelection();
//            	handleOptionChange();
            }
        });
    	
    	sizeScrolledPanel();
		
		return composite;
    }
    
    public boolean useBasicEditor() {
    	return basicEditor;
    }
}
