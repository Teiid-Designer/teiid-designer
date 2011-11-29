/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.file;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants.Images;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;

public class DelimiterOptionsDialog  extends TitleAreaDialog {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(DelimiterOptionsDialog.class);
	private final String TITLE = getString("title"); //$NON-NLS-1$

	private static String getString(final String id) {
		return UiConstants.Util.getString(I18N_PREFIX + id);
	}
	
    //=============================================================
    // Instance variables
    //=============================================================
    private TeiidMetadataFileInfo fileInfo;
    
	Button commaRB, spaceRB, tabRB, semicolonRB, barRB, otherDelimiterRB;
	Text otherDelimiterText;
        
    //=============================================================
    // Constructors
    //=============================================================
    /**
     * ParsedDataRowDialog constructor.
     * 
     * @param parent   parent of this dialog
     * @param fileInfo the flat file business object
     * @param stringToParse the data string to parse
     */
    public DelimiterOptionsDialog(Shell parent, TeiidMetadataFileInfo fileInfo) {
        super(parent);
        this.fileInfo = fileInfo;
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

    @Override
    protected Control createDialogArea(Composite parent) {
    	setTitle(TITLE);
    	setTitleImage(UiPlugin.getDefault().getImage(Images.IMPORT_TEIID_METADATA));
    	setMessage(getString("titleMessage")); //$NON-NLS-1$
    	
        Composite composite = (Composite)super.createDialogArea(parent);
        //------------------------------        
        // Set layout for the Composite
        //------------------------------        
        GridLayout gridLayout = new GridLayout();
        composite.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 500;
        composite.setLayoutData(gridData);
        
        createDelimitedColumnsOptionsGroup(composite);
        
        return composite;
    }
    
    private void createDelimitedColumnsOptionsGroup(Composite parent) {
    	
    	Group theGroup = WidgetFactory.createGroup(parent, "Delimeter Options"/*getString("fileFormatOptionsGroup")*/, SWT.NONE, 1, 2); //$NON-NLS-1$
    	theGroup.setLayout(new GridLayout(2, false));
    	theGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
    	//commaCB, spaceCB, tabCB, semicolonCB, barCB, otherDelimiterCB;
    	this.commaRB = WidgetFactory.createRadioButton(theGroup, getString("commaLabel"), SWT.NONE, 2, true); //$NON-NLS-1$
    	this.commaRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	setDelimiterValue();
            }
        });
    	this.spaceRB = WidgetFactory.createRadioButton(theGroup, getString("spaceLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.spaceRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	setDelimiterValue();
            }
        });
    	this.tabRB = WidgetFactory.createRadioButton(theGroup, getString("tabLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.tabRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	setDelimiterValue();
            }
        });
    	this.semicolonRB = WidgetFactory.createRadioButton(theGroup, getString("semicolonLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.semicolonRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	setDelimiterValue();
            }
        });
    	this.barRB = WidgetFactory.createRadioButton(theGroup, getString("barLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.barRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	setDelimiterValue();
            }
        });
    	this.otherDelimiterRB = WidgetFactory.createRadioButton(theGroup, getString("otherLabel"), SWT.NONE, 1, false); //$NON-NLS-1$
    	this.otherDelimiterRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	otherDelimiterText.setEnabled(otherDelimiterRB.getSelection());
            	setDelimiterValue();
            }
        });
    	this.otherDelimiterText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	this.otherDelimiterText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	setDelimiterValue();
            }
        });
    	this.otherDelimiterText.setEnabled(false);
    }
    
    private void setDelimiterValue() {
    	if( this.fileInfo.doUseDelimitedColumns()) {
    		setErrorMessage(null);
        	if( this.otherDelimiterRB.getSelection() ) {
	        	if( !this.otherDelimiterText.getText().isEmpty()) {
		    		this.fileInfo.setDelimiter(this.otherDelimiterText.getText().charAt(0));
		    	} else {
		    		setErrorMessage(getString("delimiterCannotBeNull")); //$NON-NLS-1$
		    		return;
		    	}
        	}
        	
        	if( this.commaRB.getSelection() ) {
        		fileInfo.setDelimiter(TeiidMetadataFileInfo.COMMA);
        	} else if( this.spaceRB.getSelection() ) {
        		fileInfo.setDelimiter(TeiidMetadataFileInfo.SPACE);
        	} else if ( this.tabRB.getSelection() ) {
        		fileInfo.setDelimiter(TeiidMetadataFileInfo.TAB);
        	} else if ( this.semicolonRB.getSelection() ) {
        		fileInfo.setDelimiter(TeiidMetadataFileInfo.SEMICOLON);
        	} else if ( this.barRB.getSelection()  ) {
        		fileInfo.setDelimiter(TeiidMetadataFileInfo.BAR);
        	} else {
        		if( !this.otherDelimiterText.getText().isEmpty()) {
    	    		this.fileInfo.setDelimiter(this.otherDelimiterText.getText().charAt(0));
    	    	}
        	}
    	}
    }
    
    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        
        synchronizeUI();
    }
    
    private void synchronizeUI() {
		this.commaRB.setSelection(false);
		this.spaceRB.setSelection(false);
		this.tabRB.setSelection(false);
		this.semicolonRB.setSelection(false);
		this.barRB.setSelection(false);
    	this.otherDelimiterRB.setSelection(false);
    	this.otherDelimiterText.setEnabled(false);
    	
    	char delimiter = fileInfo.getDelimiter();
    	if( delimiter == TeiidMetadataFileInfo.COMMA) {
    		this.commaRB.setSelection(true);
    	} else if( delimiter == TeiidMetadataFileInfo.SEMICOLON) {
    		this.semicolonRB.setSelection(true);
    	} else if( delimiter == TeiidMetadataFileInfo.BAR) {
    		this.barRB.setSelection(true);
    	} else if( delimiter == TeiidMetadataFileInfo.SPACE) {
    		this.spaceRB.setSelection(true);
    	} else if( delimiter == TeiidMetadataFileInfo.TAB) {
    		this.tabRB.setSelection(true);
    	} else {
    		// Assume OTHER
    		this.otherDelimiterRB.setSelection(true);
    		String charStr = StringUtilities.EMPTY_STRING + delimiter;
    		this.otherDelimiterText.setText(charStr);
    		this.otherDelimiterText.setEnabled(true);
    	}
    }
    
    @Override
    protected void okPressed() {
        super.okPressed();
    }

}
