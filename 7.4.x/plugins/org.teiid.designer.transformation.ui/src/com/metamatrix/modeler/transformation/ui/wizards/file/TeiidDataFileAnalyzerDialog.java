/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * Dialog to allow users to configure the parsing parameters of delimiter character and header line number, view resulting
 * column data and set column datatype values to be used in generating view tables.
 * 
 */
public class TeiidDataFileAnalyzerDialog extends TitleAreaDialog implements UiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidDataFileAnalyzerDialog.class);
    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String EMPTY = ""; //$NON-NLS-1$
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    private static String getString( final String id , Object param) {
        return Util.getString(I18N_PREFIX + id, param);
    }
    
	private final TeiidMetadataFileInfo fileInfo;
	private boolean infoChanged;
	
	Group headerGroup;
	ListViewer fileContentsViewer;
	TableViewer columnsViewer;
	Label headerLineNumberLabel, numberOfFixedWidthColumnsLabel;
	Text numberOfCachedLinesText, headerLineNumberText, delimiterText, quoteText, escapeText, firstDataRowText;
	Button useHeaderForColumnNamesCB;
	Button commaRB, spaceRB, tabRB, semicolonRB, barRB, otherDelimiterRB;
	Button delimitedColumnsRB, fixedWidthColumnsRB;
	Text otherDelimiterText, numberOfFixedWidthColumnsText;
	Button useHeaderInSQLCB, includeQuoteCB, includeEscapeCB, includeSkipCB;
	Button parseRowButton;
	Action parseRowAction;
	
	boolean synchronizing = false;

    
    /**
     * @param parent
     * @param title
     * @since 7.4
     */
    public TeiidDataFileAnalyzerDialog( Shell parent,
                                     TeiidMetadataFileInfo fileInfo) {

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
    
	@Override
    protected Control createDialogArea( Composite parent ) {
    	setTitleImage(UiPlugin.getDefault().getImage(Images.IMPORT_TEIID_METADATA));
    	
        Composite mainPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1);

        this.setTitle(getString("messageTitle",fileInfo.getDataFile().getName()) ); //$NON-NLS-1$
        this.setMessage(getString("initialMessage")); //$NON-NLS-1$
        
        mainPanel.setLayout(new GridLayout(1, false));
        mainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        
        createFileContentsGroup(mainPanel);

        
        createColumnsFormatGroup(mainPanel);

        // Create Bottom Composite
        Composite bottomPanel = WidgetFactory.createPanel(mainPanel, SWT.NONE, GridData.FILL_BOTH, 1);
        bottomPanel.setLayout(new GridLayout(2, false));
        
        createDelimitersGroup(bottomPanel);
        
        createColumnInfoGroup(bottomPanel);
        
        createTextTableOptionsGroup(mainPanel);
        
        synchronizeUI();
        
        validate();
        //LayoutDebugger.debugLayout(mainPanel);
        return mainPanel;
    }
    
    
    private void synchronizeUI() {
    	// This method takes the Business Object (TeiidMetadataFileInfo) and syncs all ui Objects
    	this.synchronizing = true;
    	
    	this.numberOfCachedLinesText.setText(Integer.toString(this.fileInfo.getNumberOfCachedFileLines()));
    	
    	this.delimitedColumnsRB.setSelection(this.fileInfo.doUseDelimitedColumns());
    	this.fixedWidthColumnsRB.setSelection(this.fileInfo.isFixedWidthColumns());
    	
    	this.useHeaderForColumnNamesCB.setSelection(this.fileInfo.doUseHeaderForColumnNames());
    	this.headerLineNumberText.setText(Integer.toString(this.fileInfo.getHeaderLineNumber()));
    	this.numberOfFixedWidthColumnsText.setText(Integer.toString(this.fileInfo.getNumberOfFixedWidthColumns()));
    	this.firstDataRowText.setText(Integer.toString(fileInfo.getFirstDataRow()));
    	
    	this.useHeaderInSQLCB.setSelection(this.fileInfo.doIncludeHeader());
    	this.includeSkipCB.setSelection(this.fileInfo.doIncludeSkip());
    	this.includeQuoteCB.setSelection(this.fileInfo.doIncludeQuote());
    	this.includeEscapeCB.setSelection(this.fileInfo.doIncludeEscape());
    	
    	this.quoteText.setText(EMPTY + this.fileInfo.getQuote());
    	this.escapeText.setText(EMPTY + this.fileInfo.getEscape());
    	
    	boolean enable = this.fileInfo.doUseDelimitedColumns();
    	this.useHeaderForColumnNamesCB.setEnabled(enable);
    	this.useHeaderInSQLCB.setEnabled(enable);
    	this.includeEscapeCB.setEnabled(enable);
    	this.includeQuoteCB.setEnabled(enable);
    	this.includeSkipCB.setEnabled(true);
		this.commaRB.setEnabled(enable);
		this.spaceRB.setEnabled(enable);
		this.tabRB.setEnabled(enable);
		this.semicolonRB.setEnabled(enable);
		this.barRB.setEnabled(enable);
    	this.otherDelimiterRB.setEnabled(enable);
    	this.otherDelimiterText.setEnabled(enable);
    	
    	this.numberOfFixedWidthColumnsLabel.setEnabled(!enable);
        this.numberOfFixedWidthColumnsText.setEnabled(!enable);
    	
    	this.headerLineNumberLabel.setEnabled(this.fileInfo.doUseHeaderForColumnNames());
    	this.headerLineNumberText.setEnabled(this.fileInfo.doUseHeaderForColumnNames());
    	
    	this.synchronizing = false;
    }
    
    private void createTextTableOptionsGroup(Composite parent) {
    	Group textTableOptionsGroup = WidgetFactory.createGroup(parent, getString("teiidTextTableGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	textTableOptionsGroup.setLayout(new GridLayout(7, false));
    	textTableOptionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
    	this.useHeaderInSQLCB = WidgetFactory.createCheckBox(textTableOptionsGroup, getString("includeHeader"), 0, 1); //$NON-NLS-1$
        this.useHeaderInSQLCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing ) {
	            	fileInfo.setIncludeHeader(useHeaderInSQLCB.getSelection());
	            	handleInfoChanged(false);
            	}
            }
        });
        this.useHeaderInSQLCB.setToolTipText(getString("useHeaderTooltip")); //$NON-NLS-1$
        this.useHeaderInSQLCB.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        
        this.includeSkipCB = WidgetFactory.createCheckBox(textTableOptionsGroup, getString("includeSkip"), 0, 1); //$NON-NLS-1$
        this.includeSkipCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing ) {
	            	fileInfo.setIncludeSkip(includeSkipCB.getSelection());
	            	handleInfoChanged(false);
            	}
            }
        });
        this.includeSkipCB.setToolTipText(getString("includeSkipTooltip")); //$NON-NLS-1$
        this.includeSkipCB.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        addSpacer(textTableOptionsGroup, 20);
        
        this.includeQuoteCB = WidgetFactory.createCheckBox(textTableOptionsGroup, getString("includeQuote"), 0, 1); //$NON-NLS-1$
        this.includeQuoteCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing ) {
	            	fileInfo.setIncludeQuote(includeQuoteCB.getSelection());
	            	handleInfoChanged(false);
            	}
            }
        });
        
        this.quoteText = WidgetFactory.createTextField(textTableOptionsGroup, SWT.NONE);
    	GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
    	gd.minimumWidth = 50;
    	gd.horizontalSpan=1;
    	this.quoteText.setLayoutData(gd);
    	this.quoteText.setTextLimit(1);
    	this.quoteText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	if( !synchronizing ) {
	            	if( !quoteText.getText().isEmpty()) {
	            		if( quoteText.getText().charAt(0) != fileInfo.getQuote() ) {
	            			fileInfo.setQuote(quoteText.getText().charAt(0));
	            			handleInfoChanged(false);
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
        
        this.includeEscapeCB = WidgetFactory.createCheckBox(textTableOptionsGroup, getString("includeEscape"), 0, 1); //$NON-NLS-1$
        this.includeEscapeCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing ) {
	            	fileInfo.setIncludeEscape(includeEscapeCB.getSelection());
	            	handleInfoChanged(false);
            	}
            }
        });
    	this.escapeText = WidgetFactory.createTextField(textTableOptionsGroup, SWT.NONE);
    	gd.grabExcessHorizontalSpace = true;
    	gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
    	gd.minimumWidth = 50;
    	gd.horizontalSpan=1;
    	gd.grabExcessHorizontalSpace = true;
    	this.escapeText.setLayoutData(gd);
    	this.escapeText.setTextLimit(1);
    	this.escapeText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	if( !synchronizing ) {
	            	if( !escapeText.getText().isEmpty()) {
	            		if( escapeText.getText().charAt(0) != fileInfo.getEscape() ) {
	            			fileInfo.setEscape(escapeText.getText().charAt(0));
	            			handleInfoChanged(false);
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
    
    private void createFileContentsGroup(Composite parent) {
    	Group fileContentsGroup = WidgetFactory.createGroup(parent, getString("fileContentsGroup"), SWT.NONE, 1, 4); //$NON-NLS-1$
    	fileContentsGroup.setLayout(new GridLayout(4, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 270;
    	gd.widthHint = 500;
    	fileContentsGroup.setLayoutData(gd);
    	
    	Composite topPanel = WidgetFactory.createPanel(fileContentsGroup);
    	topPanel.setLayout(new GridLayout(2, false));
        GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
        gd1.horizontalSpan=4;
        topPanel.setLayoutData(gd1);
        
    	Label prefixLabel = new Label(topPanel, SWT.NONE);
    	prefixLabel.setText(getString("numberOfLinesLabel",this.fileInfo.getNumberOfLinesInFile())); //$NON-NLS-1$
    	GridData lgd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        lgd.horizontalSpan=1;
        prefixLabel.setLayoutData(lgd);
        
    	this.numberOfCachedLinesText = WidgetFactory.createTextField(topPanel, SWT.NONE);
    	gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
    	gd.minimumWidth = 50;
    	gd.horizontalSpan=1;
    	gd.grabExcessHorizontalSpace = true;
    	this.numberOfCachedLinesText.setLayoutData(gd);
    	this.numberOfCachedLinesText.addModifyListener(new ModifyListener() {
    		public void modifyText( final ModifyEvent event ) {
    			if( !synchronizing ) {
	    			if( !numberOfCachedLinesText.getText().isEmpty()) {
	            		try {
	        				int nLines = Integer.parseInt(numberOfCachedLinesText.getText());
	        				if( nLines == 0 ) {
	        					setErrorMessage(getString("numberOfLinesCannotBeNullOrZero")); //$NON-NLS-1$
	        					return;
	        				}
	        				if( nLines != fileInfo.getNumberOfCachedFileLines() ) {
	        					fileInfo.setNumberOfCachedFileLines(nLines);
	        					handleInfoChanged(true);
	        				}
	        				setErrorMessage(null);
	        			} catch (NumberFormatException ex) {
	        				setErrorMessage(getString("numberOfLinesMustBeInteger", numberOfCachedLinesText.getText())); //$NON-NLS-1$
	        				return;
	        			}
	            	} else {
	            		setErrorMessage(getString("numberOfLinesCannotBeNullOrZero")); //$NON-NLS-1$
	            		return;
	            	}
    			}
    		}
    	});
    	
    	this.fileContentsViewer = new ListViewer(fileContentsGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        this.fileContentsViewer.getControl().setLayoutData(data);
        for( String row : this.fileInfo.getCachedFirstLines() ) {
        	if( row != null ) {
        		this.fileContentsViewer.add(row);
        	}
        }
        
     // Add a Context Menu
        final MenuManager columnMenuManager = new MenuManager();
        this.fileContentsViewer.getControl().setMenu(columnMenuManager.createContextMenu(parent));
        this.fileContentsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
            	columnMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)fileContentsViewer.getSelection();
                if (sel.size() == 1) {
					columnMenuManager.add(parseRowAction);
					parseRowButton.setEnabled(true);
                } else {
                	parseRowButton.setEnabled(false);
                }

            }
        });
        
    	
    	// Create Bottom Composite
        
        this.useHeaderForColumnNamesCB = WidgetFactory.createCheckBox(fileContentsGroup, getString("useHeaderForColumnNames"), 0, 2); //$NON-NLS-1$
        this.useHeaderForColumnNamesCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing ) {
	            	fileInfo.setDoUseHeaderForColumnNames(useHeaderForColumnNamesCB.getSelection());
	            	handleInfoChanged(false);
            	}
            }
        });
        
        this.parseRowAction = new Action(getString("parseSelectedRow")) { //$NON-NLS-1$
            @Override
            public void run() {
            	parseSelectedDataRow();
            }
		};
		addSpacer(fileContentsGroup, 32);
    	
        this.parseRowButton = WidgetFactory.createButton(fileContentsGroup, SWT.PUSH);
        this.parseRowButton.setText(getString("parseSelectedRow")); //$NON-NLS-1$
    	gd = new GridData(GridData.FILL_BOTH);
    	gd.minimumWidth = 100;
    	this.parseRowButton.setLayoutData(gd);
        this.parseRowButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	parseSelectedDataRow();
            }
        });
        this.parseRowButton.setEnabled(false);
        this.parseRowButton.setToolTipText(getString("parseSelectedTooltip")); //$NON-NLS-1$
        
        headerLineNumberLabel = new Label(fileContentsGroup, SWT.NONE);
    	headerLineNumberLabel.setText(getString("headerLineNumber")); //$NON-NLS-1$
    	this.headerLineNumberText = WidgetFactory.createTextField(fileContentsGroup, SWT.NONE);
    	gd = new GridData(GridData.FILL_BOTH);
    	gd.minimumWidth = 100;
    	gd.minimumHeight= 23;
    	this.headerLineNumberText.setLayoutData(gd);
    	this.headerLineNumberText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	if( !synchronizing ) {
	            	if( !headerLineNumberText.getText().isEmpty()) {
	            		try {
	        				int lineNumber = Integer.parseInt(headerLineNumberText.getText());
	        				if( lineNumber == 0 ) {
	        					setErrorMessage(getString("headerLineNumberCannotBeNullOrZero")); //$NON-NLS-1$
	        					return;
	        				}
	        				if( lineNumber != fileInfo.getHeaderLineNumber() ) {
	        					fileInfo.setHeaderLineNumber(lineNumber);
	        					handleInfoChanged(false);
	        				}
	        				setErrorMessage(null);
	        			} catch (NumberFormatException ex) {
	        				setErrorMessage(getString("headerLineNumberMustBeInteger", headerLineNumberText.getText())); //$NON-NLS-1$
	        				return;
	        			}
	            	} else {
	            		setErrorMessage(getString("headerLineNumberCannotBeNullOrZero")); //$NON-NLS-1$
	            		return;
            	}
            	}
            }
        });
    	this.headerLineNumberLabel.setEnabled(this.useHeaderForColumnNamesCB.getSelection());
    	this.headerLineNumberText.setEnabled(this.useHeaderForColumnNamesCB.getSelection());
    	addSpacer(fileContentsGroup, 32);
    	addSpacer(fileContentsGroup, 32);
    	
    	Label firstDataRowLabel = new Label(fileContentsGroup, SWT.NONE);
    	firstDataRowLabel.setText(getString("firstRowLineNumber")); //$NON-NLS-1$
    	firstDataRowLabel.setToolTipText(getString("firstDataRowTooltip")); //$NON-NLS-1$
    	this.firstDataRowText = WidgetFactory.createTextField(fileContentsGroup, SWT.NONE);
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 20;
	    gd.horizontalSpan=1;
	    this.firstDataRowText.setLayoutData(gd);
    	this.firstDataRowText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	if( !synchronizing ) {
	            	if( !firstDataRowText.getText().isEmpty()) {
	            		try {
	        				int nLines = Integer.parseInt(firstDataRowText.getText());
	        				if( nLines < 1 ) {
	        					setErrorMessage(getString("firstDataRowCannotBeZeroOrNegative")); //$NON-NLS-1$
	        					return;
	        				}
	        				if( nLines != fileInfo.getFirstDataRow() ) {
	        					fileInfo.setFirstDataRow(nLines);
	        					handleInfoChanged(false);
	        				}
	        				setErrorMessage(null);
	        			} catch (NumberFormatException ex) {
	        				setErrorMessage(getString("firstDataRowMustBeInteger", headerLineNumberText.getText())); //$NON-NLS-1$
	        				return;
	        			}
	            	} else {
	            		setErrorMessage(getString("firstDataRowCannotBeZeroOrNegative")); //$NON-NLS-1$
	            		return;
	            	}
            	}
            }
        });
    	this.firstDataRowText.setToolTipText(getString("firstDataRowTooltip")); //$NON-NLS-1$
    	addSpacer(fileContentsGroup, 32);
    	addSpacer(fileContentsGroup, 32);
    	
    	//LayoutDebugger.debugLayout(fileContentsGroup);
    }
    
    private void createColumnsFormatGroup(Composite parent) {
    	// Create Bottom Composite
        Composite topPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1);
        topPanel.setLayout(new GridLayout(2, false));
        
    	Group columnFormatGroup = WidgetFactory.createGroup(topPanel, getString("columnsFormatGroup"), SWT.NONE, 1, 3); //$NON-NLS-1$
    	columnFormatGroup.setLayout(new GridLayout(3, true));
    	columnFormatGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
    	// delimitedColumnsRB, fixedWidthColumnsRB;
    	delimitedColumnsRB = WidgetFactory.createRadioButton(columnFormatGroup, getString("characterDelimited")); //$NON-NLS-1$
    	
    	delimitedColumnsRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing ) {
	            	fileInfo.setUseDelimitedColumns(delimitedColumnsRB.getSelection());
	            	handleInfoChanged(false);
            	}
            }
        });
    	
    	this.fixedWidthColumnsRB = WidgetFactory.createRadioButton(columnFormatGroup, getString("fixedWidth")); //$NON-NLS-1$
    	
    	this.fixedWidthColumnsRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing ) {
	            	fileInfo.setFixedWidthColumns(fixedWidthColumnsRB.getSelection());
	            	handleInfoChanged(false);
            	}
            }
        });
    	
    	Group fixedWidthGroup = WidgetFactory.createGroup(topPanel, getString("fixedWidthOptions"), SWT.NONE, 1, 2); //$NON-NLS-1$
    	fixedWidthGroup.setLayout(new GridLayout(2, false));
    	fixedWidthGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
    	
    	numberOfFixedWidthColumnsLabel = new Label(fixedWidthGroup, SWT.NONE);
    	numberOfFixedWidthColumnsLabel.setText(getString("numberOfColumns")); //$NON-NLS-1$
    	this.numberOfFixedWidthColumnsText = WidgetFactory.createTextField(fixedWidthGroup, GridData.FILL_HORIZONTAL);

    	this.numberOfFixedWidthColumnsText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	if( !synchronizing ) {
	            	if( !numberOfFixedWidthColumnsText.getText().isEmpty()) {
	            		try {
	        				int nColumns = Integer.parseInt(numberOfFixedWidthColumnsText.getText());
	        				if( nColumns < 0 ) {
	        					setErrorMessage(getString("numberOfFixedWidthColumnsCannotBeNegative")); //$NON-NLS-1$
	        					return;
	        				}
	        				if( nColumns != fileInfo.getNumberOfFixedWidthColumns() ) {
		        				fileInfo.setNumberOfFixedWidthColumns(nColumns);
		        				handleInfoChanged(false);
	        				}
	        				setErrorMessage(null);
	        			} catch (NumberFormatException ex) {
	        				setErrorMessage(getString("numberOfFixedWidthColumnsMustBeInteger", numberOfFixedWidthColumnsText.getText())); //$NON-NLS-1$
	        				return;
	        			}
	            	} else {
	            		setErrorMessage(getString("numberOfFixedWidthColumnsCannotBeNullOrZero")); //$NON-NLS-1$
	            		return;
	            	}
            	}
            }
        });
    	
    	this.numberOfFixedWidthColumnsLabel.setEnabled(this.fileInfo.isFixedWidthColumns());
        this.numberOfFixedWidthColumnsText.setEnabled(this.fileInfo.isFixedWidthColumns());
    }
    
    private void createDelimitersGroup(Composite parent) {
        Composite subPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1);
        subPanel.setLayout(new GridLayout(1, false));
    	
    	Group delimitersGroup = WidgetFactory.createGroup(subPanel, "Delimeter Options"/*getString("fileFormatOptionsGroup")*/, SWT.NONE, 1, 2); //$NON-NLS-1$
    	delimitersGroup.setLayout(new GridLayout(2, false));
    	delimitersGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
    	//commaCB, spaceCB, tabCB, semicolonCB, barCB, otherDelimiterCB;
    	this.commaRB = WidgetFactory.createRadioButton(delimitersGroup, getString("commaLabel"), SWT.NONE, 2, true); //$NON-NLS-1$
    	this.commaRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleInfoChanged(false);
            }
        });
    	this.spaceRB = WidgetFactory.createRadioButton(delimitersGroup, getString("spaceLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.spaceRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleInfoChanged(false);
            }
        });
    	this.tabRB = WidgetFactory.createRadioButton(delimitersGroup, getString("tabLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.tabRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleInfoChanged(false);
            }
        });
    	this.semicolonRB = WidgetFactory.createRadioButton(delimitersGroup, getString("semicolonLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.semicolonRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleInfoChanged(false);
            }
        });
    	this.barRB = WidgetFactory.createRadioButton(delimitersGroup, getString("barLabel"), SWT.NONE, 2, false); //$NON-NLS-1$
    	this.barRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleInfoChanged(false);
            }
        });
    	this.otherDelimiterRB = WidgetFactory.createRadioButton(delimitersGroup, getString("otherLabel"), SWT.NONE, 1, false); //$NON-NLS-1$
    	this.otherDelimiterRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	handleInfoChanged(false);
            }
        });
    	this.otherDelimiterText = WidgetFactory.createTextField(delimitersGroup, SWT.NONE);
    	this.otherDelimiterText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	handleInfoChanged(false);
            }
        });

    }
    
    private void createColumnInfoGroup(Composite parent) {
    	Group columnInfoGroup = WidgetFactory.createGroup(parent, getString("columnInfoGroup"), SWT.NONE, 1, 1); //$NON-NLS-1$
    	columnInfoGroup.setLayout(new GridLayout(1, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 140;
    	columnInfoGroup.setLayoutData(gd);
    	
    	Table table = new Table(columnInfoGroup, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.columnsViewer = new TableViewer(table);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        this.columnsViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(getString("columnName") + getSpaces(36)); //$NON-NLS-1$
        column.setEditingSupport(new ColumnNameEditingSupport(this.columnsViewer));
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(getString("datatype") + getSpaces(12)); //$NON-NLS-1$ 
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        column.setEditingSupport(new DatatypeEditingSupport(this.columnsViewer));
        column.getColumn().pack();
        
        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(getString("width") + getSpaces(12)); //$NON-NLS-1$ 
        column.setLabelProvider(new ColumnDataLabelProvider(2));
        column.setEditingSupport(new ColumnWidthEditingSupport(this.columnsViewer));
        column.getColumn().pack();
        
    	

        for( TeiidColumnInfo row : this.fileInfo.getColumnInfoList() ) {
        	this.columnsViewer.add(row);
        }
        
       
    }
    
    private void addSpacer(Composite parent, int nSpaces) {
    	Label label = new Label(parent, SWT.NONE);
    	label.setText(getSpaces(nSpaces));
    }
    
    private String getSpaces(int nSpaces) {
    	StringBuffer sb = new StringBuffer(nSpaces);
    	for( int i=0; i<nSpaces; i++ ) {
    		sb.append(StringUtilities.SPACE);
    	}
    	return sb.toString();
    }
    
    private void setDelimiterValue() {
    	if( this.delimitedColumnsRB.getSelection()) {
    		setErrorMessage(null);
        	if( this.otherDelimiterRB.getSelection() ) {
	        	if( !this.otherDelimiterText.getText().isEmpty()) {
		    		this.fileInfo.setDelimiter(this.delimiterText.getText().charAt(0));
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
    	    		this.fileInfo.setDelimiter(this.delimiterText.getText().charAt(0));
    	    	}
        	}
    	}
    }
    
    private void parseSelectedDataRow() {
    	IStructuredSelection selectedFile = (IStructuredSelection)this.fileContentsViewer.getSelection();
    	if( selectedFile.getFirstElement() != null && selectedFile.getFirstElement() instanceof String ) {
    		String dataRowStr = (String)selectedFile.getFirstElement();
    		ParsedDataRowDialog dialog = new ParsedDataRowDialog(getShell(), fileInfo, dataRowStr);
        	
        	dialog.open();
    	}
    }
    
    private void handleInfoChanged(boolean reloadFileContents) {
    	if( synchronizing ) return;
    	
    	this.infoChanged = true;
    	
    	setDelimiterValue();
    	
    	synchronizeUI();
    	
    	if( reloadFileContents ) {
    		loadFileContentsViewer();
    	}

    	this.columnsViewer.getTable().removeAll();
        for( TeiidColumnInfo row : fileInfo.getColumnInfoList() ) {
        	this.columnsViewer.add(row);
        }
        
        validate();
    }
    
    private void loadFileContentsViewer() {
    	fileContentsViewer.getList().removeAll();
    	for( String row : this.fileInfo.getCachedFirstLines() ) {
        	if( row != null ) {
        		this.fileContentsViewer.add(row);
        	}
        }
    }
    
    public TeiidMetadataFileInfo getFileInfo() {
    	return this.fileInfo;
    }
    
    public boolean infoChanged() {
    	return this.infoChanged;
    }
    
    private void validate() {
    	if( fileInfo.getStatus().isOK() || fileInfo.getStatus().getSeverity() == IStatus.WARNING  ) {
    		setErrorMessage(null);
    		setMessage(getString("initialMessage")); //$NON-NLS-1$
    		return;
    	}
    	setErrorMessage(fileInfo.getStatus().getMessage());
    }

	class ColumnDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public ColumnDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof TeiidColumnInfo ) {
				switch (this.columnNumber) {
					case 0: {
						if(element instanceof TeiidColumnInfo) {
							return ((TeiidColumnInfo)element).getName();
						}
					}
					case 1: {
						if(element instanceof TeiidColumnInfo) {
							return ((TeiidColumnInfo)element).getDatatype();
						}
					}
					case 2: {
						if(element instanceof TeiidColumnInfo) {
							return Integer.toString(((TeiidColumnInfo)element).getWidth());
						}
					}
				}
			}
			return EMPTY;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			switch (this.columnNumber) {
			case 0: {
				return "Tooltip 1"; //getString("columnNameColumnTooltip"); //$NON-NLS-1$
			}
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
			}
		}
		return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if( this.columnNumber == 0 ) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);

			}
			return null;
		}
		
		
	}
    
    class ColumnNameEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer
		 */
		public ColumnNameEditingSupport(ColumnViewer viewer) {
			super(viewer);
			this.editor = new TextCellEditor((Composite) viewer.getControl());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		protected Object getValue(Object element) {
			if( element instanceof TeiidColumnInfo ) {
				return ((TeiidColumnInfo)element).getName();
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		protected void setValue(Object element, Object value) {
			if( element instanceof TeiidColumnInfo ) {
				String oldValue = ((TeiidColumnInfo)element).getName();
				String newValue = (String)value;
				if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
					((TeiidColumnInfo)element).setName(newValue);
					columnsViewer.refresh(element);
				}
			}
		}

	}
    
    class ColumnWidthEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer
		 */
		public ColumnWidthEditingSupport(ColumnViewer viewer) {
			super(viewer);
			this.editor = new TextCellEditor((Composite) viewer.getControl());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		protected Object getValue(Object element) {
			if( element instanceof TeiidColumnInfo ) {
				return Integer.toString(((TeiidColumnInfo)element).getWidth());
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		protected void setValue(Object element, Object value) {
			if( element instanceof TeiidColumnInfo ) {
				int oldValue = ((TeiidColumnInfo)element).getWidth();
				int newValue = oldValue;
				try {
					newValue = Integer.parseInt((String)value);
				} catch (NumberFormatException ex) {
					return;
				}
				if( newValue != oldValue ) {
					((TeiidColumnInfo)element).setWidth(newValue);
					columnsViewer.refresh(element);
				}
			}
		}

	}
}
