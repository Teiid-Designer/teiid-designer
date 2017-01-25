/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.file;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.query.proc.ITeiidMetadataFileInfo;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiConstants.Images;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * @since 8.0
 */
public class DelimiterOptionsDialog  extends TitleAreaDialog {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(DelimiterOptionsDialog.class);
	private final String TITLE = getString("title"); //$NON-NLS-1$
	private final String SPACES_16 = "                "; //$NON-NLS-1$

	private static String getString(final String id) {
		return UiConstants.Util.getString(I18N_PREFIX + id);
	}
	
    //=============================================================
    // Instance variables
    //=============================================================
    private TeiidMetadataFileInfo fileInfo;
    
    
	Button defaultDelimiterRB, spaceRB, tabRB, semicolonRB, barRB, otherDelimiterRB;
	Text otherDelimiterText;
	Button defaultRowDelimiterCB;
	Button customRowDelimiterRB, noRowDelimiterRB;
	Text customRowDelimiterText;
	
	boolean synching = false;
        
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
        
    	if( fileInfo.doUseDelimitedColumns() ) {
            createDelimitedColumnsOptionsGroup(composite);
    	} else {
    		createRowDelimitedOptionsGroup(composite);
    	}

        
        return composite;
    }
    
    private void createRowDelimitedOptionsGroup(Composite parent) {

    	Group theGroup = WidgetFactory.createGroup(parent, getString("rowDelimeter"), SWT.NONE, 1, 3); //$NON-NLS-1$
    	theGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
    	this.defaultRowDelimiterCB  = WidgetFactory.createCheckBox(theGroup, getString("useDefaultText"), SWT.NONE, 3, true); //$NON-NLS-1$
    	this.defaultRowDelimiterCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleOptionChange();
            }
        });

    	WidgetFactory.createLabel(theGroup, SPACES_16);
    	
    	this.noRowDelimiterRB = WidgetFactory.createRadioButton(theGroup, getString("none"), SWT.NONE, 2, true); //$NON-NLS-1$
    	this.noRowDelimiterRB.setToolTipText(getString("noRowDelimeterTooltip"));  //$NON-NLS-1$
    	this.noRowDelimiterRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleOptionChange();
            }
        });
    	
    	WidgetFactory.createLabel(theGroup, SPACES_16);
    	this.customRowDelimiterRB = WidgetFactory.createRadioButton(theGroup, getString("customCharacter") + "  ", SWT.NONE, 1, false); //$NON-NLS-1$  //$NON-NLS-2$
    	this.customRowDelimiterRB.setToolTipText(getString("customCharacterTooltip")); //$NON-NLS-1$
    	this.customRowDelimiterRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleOptionChange();
            }
        });
    	
    	this.customRowDelimiterText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	GridDataFactory.fillDefaults().span(1, 1).applyTo(this.customRowDelimiterText);
    	this.customRowDelimiterText.addModifyListener(new ModifyListener() {

            @Override
			public void modifyText( final ModifyEvent event ) {
            	handleOptionChange();
            }
        });
    	this.customRowDelimiterText.setEnabled(false);
    	
    	this.customRowDelimiterRB.setEnabled(!defaultRowDelimiterCB.getSelection());
    	this.customRowDelimiterText.setEnabled(!defaultRowDelimiterCB.getSelection());
    	this.noRowDelimiterRB.setEnabled(!defaultRowDelimiterCB.getSelection());
    }
    
    private void createDelimitedColumnsOptionsGroup(Composite parent) {
    	
    	Group theGroup = WidgetFactory.createGroup(parent, getString("fieldDelimeter"), SWT.NONE, 1, 3); //$NON-NLS-1$
    	theGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
    	//commaCB, spaceCB, tabCB, semicolonCB, barCB, otherDelimiterCB;
    	this.defaultDelimiterRB = WidgetFactory.createRadioButton(theGroup, getString("commaLabel"), SWT.NONE, 3, true); //$NON-NLS-1$
    	this.defaultDelimiterRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleOptionChange();
            }
        });
    	
    	WidgetFactory.createLabel(theGroup, SPACES_16);
    	this.spaceRB = WidgetFactory.createRadioButton(theGroup, getString("spaceLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.spaceRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleOptionChange();
            }
        });
    	
    	WidgetFactory.createLabel(theGroup, SPACES_16);
    	this.tabRB = WidgetFactory.createRadioButton(theGroup, getString("tabLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.tabRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleOptionChange();
            }
        });
    	
    	WidgetFactory.createLabel(theGroup, SPACES_16);
    	this.semicolonRB = WidgetFactory.createRadioButton(theGroup, getString("semicolonLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.semicolonRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleOptionChange();
            }
        });
    	
    	WidgetFactory.createLabel(theGroup, SPACES_16);
    	this.barRB = WidgetFactory.createRadioButton(theGroup, getString("barLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.barRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleOptionChange();
            }
        });
    	
    	WidgetFactory.createLabel(theGroup, SPACES_16);
    	this.otherDelimiterRB = WidgetFactory.createRadioButton(theGroup, getString("otherLabel"), SWT.NONE, 1, false); //$NON-NLS-1$
    	this.otherDelimiterRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	otherDelimiterText.setEnabled(otherDelimiterRB.getSelection());
            	handleOptionChange();
            }
        });
    	
    	this.otherDelimiterText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	GridDataFactory.fillDefaults().span(1, 1).applyTo(this.otherDelimiterText);
    	this.otherDelimiterText.addModifyListener(new ModifyListener() {

            @Override
			public void modifyText( final ModifyEvent event ) {
            	handleOptionChange();
            }
        });
    	this.otherDelimiterText.setEnabled(false);
    }
    
    private void handleOptionChange() {
    	if( synching ) return;
    	
    	synching = true;
		setErrorMessage(null);
    	if( this.fileInfo.doUseDelimitedColumns()) {
    		setErrorMessage(null);
        	
        	if( this.defaultDelimiterRB.getSelection() ) {
        		fileInfo.setDelimiter(ITeiidMetadataFileInfo.COMMA);
        	} else if( this.spaceRB.getSelection() ) {
        		fileInfo.setDelimiter(ITeiidMetadataFileInfo.SPACE);
        	} else if ( this.tabRB.getSelection() ) {
        		fileInfo.setDelimiter(ITeiidMetadataFileInfo.TAB);
        	} else if ( this.semicolonRB.getSelection() ) {
        		fileInfo.setDelimiter(ITeiidMetadataFileInfo.SEMI_COLON);
        	} else if ( this.barRB.getSelection()  ) {
        		fileInfo.setDelimiter(ITeiidMetadataFileInfo.BAR);
        	} else {
        		if( !this.otherDelimiterText.getText().isEmpty()) {
    	    		this.fileInfo.setDelimiter(this.otherDelimiterText.getText().trim());
    	    	} else {
		    		setErrorMessage(getString("delimiterCannotBeNull")); //$NON-NLS-1$
		    		enableOk(false);
		        	synching = false;
		    		return;
		    	}
        	}
    	} else {
	    	// Row Delimiter
	    	this.customRowDelimiterText.setEnabled(this.customRowDelimiterRB.getSelection());
	
	    	if( this.defaultRowDelimiterCB.getSelection() ) {
	    		fileInfo.setRowDelimiterOption(ISQLConstants.ROW_DELIMETER_OPTIONS.DEFAULT);
	    		// If default is selected.. then disable radio buttons and text field
	    		this.customRowDelimiterRB.setEnabled(false);
	    		this.customRowDelimiterText.setEnabled(false);
	    		this.noRowDelimiterRB.setEnabled(false);
	    	} else if( this.customRowDelimiterRB.getSelection() ) {
	    		fileInfo.setRowDelimiterOption(ISQLConstants.ROW_DELIMETER_OPTIONS.CUSTOM);
	    		String value = this.customRowDelimiterText.getText().trim();
	    		if( value.length() == 0 ) {
		    		setErrorMessage(getString("customCharacterUndefined")); //$NON-NLS-1$
		    		enableOk(false);
		        	synching = false;
		    		return;
	    		} else if( value.length() > 1 ) {
		    		setErrorMessage(getString("customCharacterLengthError")); //$NON-NLS-1$
		    		enableOk(false);
		        	synching = false;
		    		return;
	    		}
	    		fileInfo.setRowDelimiter(value);
	    	} else {
	    		fileInfo.setRowDelimiterOption(ISQLConstants.ROW_DELIMETER_OPTIONS.NONE);
	    	}    	
	    	
	    	this.customRowDelimiterRB.setEnabled(!defaultRowDelimiterCB.getSelection());
	    	this.customRowDelimiterText.setEnabled(!defaultRowDelimiterCB.getSelection() && !this.noRowDelimiterRB.getSelection());
	    	this.noRowDelimiterRB.setEnabled(!defaultRowDelimiterCB.getSelection());
    	}
    	
    	enableOk(true);
    	synching = false;
    }
    
    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        
        synchronizeUI();
    }
    
    private void synchronizeUI() {
    	synching = true;
    	
    	if( this.fileInfo.doUseDelimitedColumns()) {
			this.defaultDelimiterRB.setSelection(false);
			this.spaceRB.setSelection(false);
			this.tabRB.setSelection(false);
			this.semicolonRB.setSelection(false);
			this.barRB.setSelection(false);
	    	this.otherDelimiterRB.setSelection(false);
	    	this.otherDelimiterText.setEnabled(false);
	    	
	    	String delimiter = fileInfo.getDelimiter();
	    	if( ITeiidMetadataFileInfo.COMMA.equals(delimiter)) {
	    		this.defaultDelimiterRB.setSelection(true);
	    	} else if( ITeiidMetadataFileInfo.SEMI_COLON.equals(delimiter)) {
	    		this.semicolonRB.setSelection(true);
	    	} else if( ITeiidMetadataFileInfo.BAR.equals(delimiter)) {
	    		this.barRB.setSelection(true);
	    	} else if( ITeiidMetadataFileInfo.SPACE.equals(delimiter)) {
	    		this.spaceRB.setSelection(true);
	    	} else if( ITeiidMetadataFileInfo.TAB.equals(delimiter)) {
	    		this.tabRB.setSelection(true);
	    	} else {
	    		// Assume OTHER
	    		this.otherDelimiterRB.setSelection(true);
	    		String charStr = StringConstants.EMPTY_STRING + delimiter;
	    		this.otherDelimiterText.setText(charStr);
	    		this.otherDelimiterText.setEnabled(true);
	    	}
    	} else {
    	
    	// Row Delimiter
    	int option = fileInfo.getRowDelimeterOption();
	    	if( option == ISQLConstants.ROW_DELIMETER_OPTIONS.DEFAULT ) {
	    		this.defaultRowDelimiterCB.setSelection(true);
	    		this.noRowDelimiterRB.setSelection(true);
	    		this.noRowDelimiterRB.setEnabled(false);
	    		this.customRowDelimiterRB.setEnabled(false);
	    		this.customRowDelimiterText.setEnabled(false);
	    	} else if( option == ISQLConstants.ROW_DELIMETER_OPTIONS.NONE ) {
	    		this.defaultRowDelimiterCB.setSelection(false);
	    		this.noRowDelimiterRB.setEnabled(true);
	    		this.noRowDelimiterRB.setSelection(true);
	    		this.customRowDelimiterRB.setEnabled(true);
	    		this.customRowDelimiterText.setEnabled(false);
	    	} else {
	    		this.customRowDelimiterText.setEnabled(true);
	    		String rowChar = this.fileInfo.getRowDelimiter();
	    		if( !StringUtilities.isEmpty(rowChar)) {
	    			this.customRowDelimiterText.setText(rowChar);
	    		}
	    		this.defaultRowDelimiterCB.setSelection(false);
	    		this.noRowDelimiterRB.setSelection(false);
	    		this.customRowDelimiterRB.setEnabled(true);
	    		this.customRowDelimiterRB.setSelection(true);
	    	}
    	}
    	synching = false;
    }
    
    @Override
    protected void okPressed() {
        super.okPressed();
    }
    
    private void enableOk(boolean value) {
    	getButton(IDialogConstants.OK_ID).setEnabled(value);
    }

}
