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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.ui.UiConstants.Images;
import org.teiid.designer.ui.common.util.WidgetFactory;


/**
 * @since 8.0
 */
public class TextTableFunctionOptionsDialog  extends TitleAreaDialog {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TextTableFunctionOptionsDialog.class);
	private final String TITLE = getString("title"); //$NON-NLS-1$

	private static String getString(final String id) {
		return UiConstants.Util.getString(I18N_PREFIX + id);
	}
	
    //=============================================================
    // Instance variables
    //=============================================================
    private TeiidMetadataFileInfo dataFileInfo;
    
	Text quoteText,	escapeText;
	Button useHeaderInSQLCB, includeQuoteCB, includeEscapeCB, includeSkipCB, includeNoTrimCB;

	boolean synchronizing = false;
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
    public TextTableFunctionOptionsDialog(Shell parent, TeiidMetadataFileInfo fileInfo) {
        super(parent);
        this.dataFileInfo = fileInfo;
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
        
        createOptionsGroup(composite);
        
        return composite;
    }
    
    @SuppressWarnings("unused")
	private void createOptionsGroup(Composite parent) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("teiidTextTableGroup"), SWT.NONE, 1, 2); //$NON-NLS-1$
    	GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
    	groupGD.horizontalSpan = 1;
    	theGroup.setLayoutData(groupGD);
    	
    	INCLUDE_HEADER : {
	    	this.useHeaderInSQLCB = WidgetFactory.createCheckBox(theGroup, getString("includeHeader"), 0, 2); //$NON-NLS-1$
	        this.useHeaderInSQLCB.addSelectionListener(new SelectionAdapter() {
	
	            @Override
	            public void widgetSelected(final SelectionEvent event) {
	            	if( !synchronizing ) {
		            	dataFileInfo.setIncludeHeader(useHeaderInSQLCB.getSelection());
		            	synchronizeUI();
	            	}
	            }
	        });
	        this.useHeaderInSQLCB.setToolTipText(getString("includeHeaderTooltip")); //$NON-NLS-1$
	        this.useHeaderInSQLCB.setLayoutData(new GridData()); //new GridData(GridData.HORIZONTAL_ALIGN_END));
	        
	        new Label(theGroup, SWT.NONE);
    	}
        
    	INCLUDE_SKIP : {
	        this.includeSkipCB = WidgetFactory.createCheckBox(theGroup, getString("includeSkip"), 0, 2); //$NON-NLS-1$
	        this.includeSkipCB.addSelectionListener(new SelectionAdapter() {
	
	            @Override
	            public void widgetSelected(final SelectionEvent event) {
	            	if( !synchronizing ) {
		            	dataFileInfo.setIncludeSkip(includeSkipCB.getSelection());
		            	synchronizeUI();
	            	}
	            }
	        });
	        this.includeSkipCB.setToolTipText(getString("includeSkipTooltip")); //$NON-NLS-1$
	        this.includeSkipCB.setLayoutData(new GridData()); //GridData.HORIZONTAL_ALIGN_END));
	        new Label(theGroup, SWT.NONE);
    	}
    	
    	INCLUDE_QUOTE : {
	        this.includeQuoteCB = WidgetFactory.createCheckBox(theGroup, getString("includeQuote"), 0, 1); //$NON-NLS-1$
	        this.includeQuoteCB.addSelectionListener(new SelectionAdapter() {
	
	            @Override
	            public void widgetSelected(final SelectionEvent event) {
	            	if( !synchronizing ) {
		            	dataFileInfo.setIncludeQuote(includeQuoteCB.getSelection());
		            	synchronizeUI();
	            	}
	            }
	        });
	        
	        this.quoteText = WidgetFactory.createTextField(theGroup, SWT.NONE);
	    	GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
	    	gd.minimumWidth = 50;
	    	gd.horizontalSpan=1;
	    	gd.grabExcessHorizontalSpace = true;
	    	this.quoteText.setLayoutData(gd);
	    	this.quoteText.setTextLimit(1);
	    	this.quoteText.addModifyListener(new ModifyListener() {
	
	            @Override
				public void modifyText( final ModifyEvent event ) {
	            	if( !synchronizing ) {
		            	if( !quoteText.getText().isEmpty()) {
		            		if( ! quoteText.getText().substring(0, 1).equals(dataFileInfo.getQuote()) ) {
		            			dataFileInfo.setQuote(quoteText.getText().substring(0, 1));
		            			synchronizeUI();
		            		}
		            		setErrorMessage(null);
		            	} else {
		            		setErrorMessage(getString("quoteCannotBeNull")); //$NON-NLS-1$
		            		return;
		            	}
	            	}
	            }
	        });
        
	    	this.includeQuoteCB.setToolTipText(getString("includeQuoteTooltip")); //$NON-NLS-1$
    	}
        INCLUDE_ESCAPTE : {
	        this.includeEscapeCB = WidgetFactory.createCheckBox(theGroup, getString("includeEscape"), 0, 1); //$NON-NLS-1$
	        this.includeEscapeCB.addSelectionListener(new SelectionAdapter() {
	
	            @Override
	            public void widgetSelected(final SelectionEvent event) {
	            	if( !synchronizing ) {
		            	dataFileInfo.setIncludeEscape(includeEscapeCB.getSelection());
		            	synchronizeUI();
	            	}
	            }
	        });
	    	this.escapeText = WidgetFactory.createTextField(theGroup, SWT.NONE);
	    	GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
	    	gd.minimumWidth = 50;
	    	gd.horizontalSpan=1;
	    	gd.grabExcessHorizontalSpace = true;
	    	this.escapeText.setLayoutData(gd);
	    	this.escapeText.setTextLimit(1);
	    	this.escapeText.addModifyListener(new ModifyListener() {
	
	            @Override
				public void modifyText( final ModifyEvent event ) {
	            	if( !synchronizing ) {
		            	if( !escapeText.getText().isEmpty()) {
		            		if( ! escapeText.getText().substring(0, 1).equals(dataFileInfo.getEscape()) ) {
		            			dataFileInfo.setEscape(escapeText.getText().substring(0, 1));
		            			synchronizeUI();
		            		}
		            		setErrorMessage(null);
		            	} else {
		            		setErrorMessage(getString("escapeCannotBeNull")); //$NON-NLS-1$
		            		return;
		            	}
	            	}
	            }
	        });
	        
	        this.includeEscapeCB.setToolTipText(getString("includeEscapeTooltip")); //$NON-NLS-1$
    	}
    	
    	INCLUDE_NO_TRIM : {
	    	this.includeNoTrimCB = WidgetFactory.createCheckBox(theGroup, getString("includeNoTrim"), 0, 2); //$NON-NLS-1$
	        this.includeNoTrimCB.addSelectionListener(new SelectionAdapter() {
	
	            @Override
	            public void widgetSelected(final SelectionEvent event) {
	            	if( !synchronizing ) {
		            	dataFileInfo.setIncludeNoTrim(includeNoTrimCB.getSelection());
		            	synchronizeUI();
	            	}
	            }
	        });
	        this.includeNoTrimCB.setToolTipText(getString("includeNoTrimTooltip")); //$NON-NLS-1$
	        this.includeNoTrimCB.setLayoutData(new GridData());
	        
	        new Label(theGroup, SWT.NONE);
    	}
    }
    

    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        
        synchronizeUI();
    }
    
    private void synchronizeUI() {
    	this.useHeaderInSQLCB.setSelection(this.dataFileInfo.doIncludeHeader());
    	this.includeSkipCB.setSelection(this.dataFileInfo.doIncludeSkip());
    	this.includeQuoteCB.setSelection(this.dataFileInfo.doIncludeQuote());
    	this.includeEscapeCB.setSelection(this.dataFileInfo.doIncludeEscape());
    	this.includeNoTrimCB.setSelection(this.dataFileInfo.doIncludeNoTrim());
    	
    	this.quoteText.setText(StringUtilities.EMPTY_STRING + this.dataFileInfo.getQuote());
    	this.escapeText.setText(StringUtilities.EMPTY_STRING + this.dataFileInfo.getEscape());
    	

    }
    
    @Override
    protected void okPressed() {
        super.okPressed();
    }

}
