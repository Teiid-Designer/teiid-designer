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
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.VerticalRuler;
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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlTextViewer;
import com.metamatrix.ui.graphics.ColorManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

public class TeiidMetadataImportOptionsPage  extends AbstractWizardPage implements UiConstants {
	// ===========================================================================================================================
	// Constants

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportOptionsPage.class);

	private static final String DELIMITED_TITLE = getString("delimitedColumnsTitle"); //$NON-NLS-1$
	private static final String FIXED_COLUMNS_WIDTH_TITLE = getString("fixedColumnsWidthTitle"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

	private final String EMPTY = StringUtilities.EMPTY_STRING;
	private final int GROUP_HEIGHT_190 = 190;
	private final int GROUP_HEIGHT_160 = 160;

	private static String getString(final String id) {
		return Util.getString(I18N_PREFIX + id);
	}

	private static String getString(final String id, final Object param) {
		return Util.getString(I18N_PREFIX + id, param);
	}

	private TeiidMetadataImportInfo info;

	private TeiidMetadataFileInfo dataFileInfo;
	
	
	// ====================================================
	// GENERAL WIDGETS
	Composite stackPanel;
	StackLayout stackLayout;
	
	Text selectedFileText;
	
	TextViewer sqlTextViewer;
	IDocument sqlDocument;
	
	// ====================================================
	// DELIMITED OPTION WIDGETS
	Composite delimitedColumnsPanel;
	ListViewer delimitedFileContentsViewer;
	TableViewer delimitedColumnsViewer;
	Label headerLineNumberLabel;
	Text headerLineNumberText, delimitedFirstDataRowText;
	Button useHeaderForColumnNamesCB;
	Button delimitedParseRowButton;
	Action delimitedParseRowAction;
	Button addColumnDelimitedButton, deleteColumnDelimitedButton, upColumnDelimitedButton, downColumnDelimitedButton;
	
	// ====================================================
	// FIXED COLUMN WIDTH OPTION WIDGETS
	Composite fixedWidthColumnsPanel;
	TextViewer fixedFileContentsViewer;
	TableViewer fixedColumnsViewer;
	Text fixedFirstDataRowText, cursorPositionText, selectedTextLengthText;
	Button addColumnFixedButton, deleteColumnFixedButton, upColumnFixedButton, downColumnFixedButton;
	
	Action createColumnAction;

	boolean creatingControl = false;

	boolean synchronizing = false;

	/**
	 * @since 4.0
	 */
	public TeiidMetadataImportOptionsPage(TeiidMetadataImportInfo info) {
		super(TeiidMetadataImportOptionsPage.class.getSimpleName(), DELIMITED_TITLE);
		this.info = info;
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(
				Images.IMPORT_TEIID_METADATA));
	}

	@Override
	public void createControl(Composite parent) {
		creatingControl = true;
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(2, false));
		mainPanel.setLayoutData(new GridData()); //GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);
		
		
        // Create Bottom Composite
        Composite upperPanel = WidgetFactory.createPanel(mainPanel, SWT.NONE, GridData.FILL_HORIZONTAL, 2, 2);
        upperPanel.setLayout(new GridLayout(2, false));

		setMessage(INITIAL_MESSAGE);
		
		Label selectedFileLabel = new Label(upperPanel, SWT.NONE);
		selectedFileLabel.setText(getString("selectedFile")); //$NON-NLS-1$
		
        selectedFileText = new Text(upperPanel, SWT.BORDER);//, SWT.BORDER | SWT.SINGLE);
        selectedFileText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        selectedFileText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
		selectedFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createStackLayout(mainPanel);
        
        createSqlGroup(mainPanel);
        
		creatingControl = false;

		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			TeiidMetadataFileInfo fileInfo = null;
			for (TeiidMetadataFileInfo theFileInfo : info.getFileInfos()) {
				if (theFileInfo.doProcess()) {
					fileInfo = theFileInfo;
					break;
				}
			}
			if (fileInfo != null) {
				this.dataFileInfo = fileInfo;

				loadFileContentsViewers();
			}
			synchronizeUI();
			
			validatePage();
		}
	}

	private boolean validatePage() {
		if( !dataFileInfo.getStatus().isOK() && !(dataFileInfo.getStatus().getSeverity() == IStatus.WARNING)  ) {
			setThisPageComplete(dataFileInfo.getStatus().getMessage(), IStatus.ERROR);
			return false;
		}
		
		setThisPageComplete(EMPTY, NONE);
		return true;
	}

	private void setThisPageComplete(String message, int severity) {
		WizardUtil.setPageComplete(this, message, severity);
	}

	private void synchronizeUI() {
		synchronizing = true;

		selectedFileText.setText(dataFileInfo.getDataFile().getName());

		boolean isDelimitedOption = this.dataFileInfo.doUseDelimitedColumns();
    	
    	this.useHeaderForColumnNamesCB.setSelection(this.dataFileInfo.doUseHeaderForColumnNames());
    	this.headerLineNumberText.setText(Integer.toString(this.dataFileInfo.getHeaderLineNumber()));
    	this.delimitedFirstDataRowText.setText(Integer.toString(dataFileInfo.getFirstDataRow()));
    	this.fixedFirstDataRowText.setText(Integer.toString(dataFileInfo.getFirstDataRow()));
    	
    	boolean enable = isDelimitedOption;
    	this.useHeaderForColumnNamesCB.setEnabled(enable);
    	this.headerLineNumberText.setEnabled(useHeaderForColumnNamesCB.getSelection());
    	
    	this.delimitedColumnsViewer.getTable().setEnabled(enable);
    	this.fixedColumnsViewer.getTable().setEnabled(!enable);

    	this.headerLineNumberLabel.setEnabled(this.dataFileInfo.doUseHeaderForColumnNames());
    	this.headerLineNumberText.setEnabled(this.dataFileInfo.doUseHeaderForColumnNames());

    	this.delimitedColumnsViewer.getTable().removeAll();
        for( TeiidColumnInfo row : dataFileInfo.getColumnInfoList() ) {
        	this.delimitedColumnsViewer.add(row);
        }
        
    	this.fixedColumnsViewer.getTable().removeAll();
        for( TeiidColumnInfo row : dataFileInfo.getColumnInfoList() ) {
        	this.fixedColumnsViewer.add(row);
        }
    	
    	updateSqlText();
    	
		
		if( isDelimitedOption ) {
			this.stackLayout.topControl = delimitedColumnsPanel;
			this.setTitle(DELIMITED_TITLE);
		} else {
			this.stackLayout.topControl = fixedWidthColumnsPanel;
			this.setTitle(FIXED_COLUMNS_WIDTH_TITLE);
		}
		
		this.stackPanel.layout();
    	
		synchronizing = false;
	}
	
    private void createStackLayout( Composite parent ) {

    	stackPanel = new Composite(parent, SWT.NONE | SWT.FILL);
    	stackLayout = new StackLayout();
    	stackLayout.marginWidth = 0;
    	stackLayout.marginHeight = 0;
    	stackPanel.setLayout(stackLayout);
    	stackPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	

    	createDelimitedColumnsPanel(stackPanel);
        createFixedWidthColumnsPanel(stackPanel);
    }
    
    private void createDelimitedColumnsPanel( Composite parentPanel ) {
        // Set overall grid layout
        GridLayout glOuterGridLayout = new GridLayout();
        glOuterGridLayout.marginHeight = 0;
        glOuterGridLayout.marginWidth = 0;
        delimitedColumnsPanel = new Composite(parentPanel, SWT.NONE);
        delimitedColumnsPanel.setLayout(glOuterGridLayout);
        delimitedColumnsPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        // Create Bottom Composite
        Composite topPanel = WidgetFactory.createPanel(delimitedColumnsPanel, SWT.NONE, GridData.FILL_HORIZONTAL, 2);
        GridLayout tpGL = new GridLayout(2, false);
        tpGL.marginHeight = 1;
        topPanel.setLayout(tpGL);
        
        createDelimitedFileOptionsGroup(topPanel);
        
        createDelimitedFileContentsGroup(topPanel);
        
        // Create Bottom Composite
        Composite bottomPanel = WidgetFactory.createPanel(delimitedColumnsPanel, SWT.NONE, GridData.FILL_HORIZONTAL, 2);
        GridLayout bpGL = new GridLayout(2, false);
        bpGL.marginHeight = 1;
        bottomPanel.setLayout(bpGL);
      
        createDelimitedColumnInfoGroup(bottomPanel);

    }
    
    private void createFixedWidthColumnsPanel( Composite parentPanel ) {
        // Set overall grid layout
        GridLayout glOuterGridLayout = new GridLayout();

        glOuterGridLayout.marginHeight = 0;
        glOuterGridLayout.marginWidth = 0;
        fixedWidthColumnsPanel = new Composite(parentPanel, SWT.NONE);
        fixedWidthColumnsPanel.setLayout(glOuterGridLayout);
        fixedWidthColumnsPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        // Create Bottom Composite
        Composite topPanel = WidgetFactory.createPanel(fixedWidthColumnsPanel, SWT.NONE, GridData.FILL_HORIZONTAL, 2);
        GridLayout tpGL = new GridLayout(2, false);
        tpGL.marginHeight = 1;
        topPanel.setLayout(tpGL);
        
        createFixedFileOptionsGroup(topPanel);
        
        createFixedFileContentsGroup(topPanel);
        
        // Create Bottom Composite
        Composite bottomPanel = WidgetFactory.createPanel(fixedWidthColumnsPanel, SWT.NONE, GridData.FILL_HORIZONTAL, 2);
        GridLayout bpGL = new GridLayout(2, false);
        bpGL.marginHeight = 1;
        bottomPanel.setLayout(bpGL);
      
        createFixedColumnsOptionsGroup(bottomPanel);
      
        createFixedColumnInfoGroup(bottomPanel);
    }
    
    private void createDelimitedFileOptionsGroup(Composite parent) {
    	Group theGroup = WidgetFactory.createGroup(parent, "Format Options", SWT.NONE, 1, 2); //$NON-NLS-1$
    	theGroup.setLayout(new GridLayout(2, false));
	  	GridData groupGD = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	  	groupGD.heightHint=GROUP_HEIGHT_190;
	  	theGroup.setLayoutData(groupGD);
    	
    	// Create Bottom Composite
        
        this.useHeaderForColumnNamesCB = WidgetFactory.createCheckBox(theGroup, getString("useHeaderForColumnNames"), 0, 2); //$NON-NLS-1$
        this.useHeaderForColumnNamesCB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing ) {
	            	dataFileInfo.setDoUseHeaderForColumnNames(useHeaderForColumnNamesCB.getSelection());
	            	handleInfoChanged(false);
            	}
            }
        });
        
        this.delimitedParseRowAction = new Action(getString("parseSelectedRow")) { //$NON-NLS-1$
            @Override
            public void run() {
            	parseDelimitedSelectedDataRow();
            }
		};
		//addSpacer(fileContentsGroup, 32);
        
        headerLineNumberLabel = new Label(theGroup, SWT.NONE);
    	headerLineNumberLabel.setText(getString("headerLineNumber")); //$NON-NLS-1$
    	this.headerLineNumberText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 40;
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
	        				if( lineNumber != dataFileInfo.getHeaderLineNumber() ) {
	        					dataFileInfo.setHeaderLineNumber(lineNumber);
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
    	
    	Label firstDataRowLabel = new Label(theGroup, SWT.NONE);
    	firstDataRowLabel.setText(getString("firstRowLineNumber")); //$NON-NLS-1$
    	firstDataRowLabel.setToolTipText(getString("firstDataRowTooltip")); //$NON-NLS-1$
    	this.delimitedFirstDataRowText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 20;
	    gd.horizontalSpan=1;
	    this.delimitedFirstDataRowText.setLayoutData(gd);
    	this.delimitedFirstDataRowText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	if( !synchronizing ) {
	            	if( !delimitedFirstDataRowText.getText().isEmpty()) {
	            		try {
	        				int nLines = Integer.parseInt(delimitedFirstDataRowText.getText());
	        				if( nLines < 1 ) {
	        					setErrorMessage(getString("firstDataRowCannotBeZeroOrNegative")); //$NON-NLS-1$
	        					return;
	        				}
	        				if( nLines != dataFileInfo.getFirstDataRow() ) {
	        					dataFileInfo.setFirstDataRow(nLines);
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
    	this.delimitedFirstDataRowText.setToolTipText(getString("firstDataRowTooltip")); //$NON-NLS-1$
    	
        this.delimitedParseRowButton = WidgetFactory.createButton(theGroup, SWT.PUSH);
        this.delimitedParseRowButton.setText(getString("parseSelectedRow")); //$NON-NLS-1$
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 120;
    	gd.horizontalSpan = 2;
    	this.delimitedParseRowButton.setLayoutData(gd);
        this.delimitedParseRowButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	parseDelimitedSelectedDataRow();
            }
        });
        
        Button editDelimiterButton = WidgetFactory.createButton(theGroup, SWT.PUSH);
        editDelimiterButton.setText(getString("editDelimiterButtonLabel")); //$NON-NLS-1$
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 120;
    	gd.horizontalSpan = 2;
        
        editDelimiterButton.setLayoutData(gd);
        editDelimiterButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	DelimiterOptionsDialog dialog = new DelimiterOptionsDialog(getShell(), dataFileInfo);
            	
            	dialog.open();
            	handleInfoChanged(false);
            }
        });
        editDelimiterButton.setEnabled(true);
        
        Button editTextTableOptionsButton = WidgetFactory.createButton(theGroup, SWT.PUSH);
        editTextTableOptionsButton.setText(getString("editTextTableOptionsButtonLabel")); //$NON-NLS-1$
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 120;
    	gd.horizontalSpan = 2;
        
    	editTextTableOptionsButton.setLayoutData(gd);
    	editTextTableOptionsButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	TextTableFunctionOptionsDialog dialog = new TextTableFunctionOptionsDialog(getShell(), dataFileInfo);
            	
            	dialog.open();
            	handleInfoChanged(false);
            }
        });
    	editTextTableOptionsButton.setEnabled(true);
    }
    

    
    private void createDelimitedFileContentsGroup(Composite parent) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("fileContentsGroup"), SWT.NONE, 1, 4); //$NON-NLS-1$
    	theGroup.setLayout(new GridLayout(4, false));
    	GridData groupGD = new GridData(GridData.FILL_BOTH);
    	groupGD.heightHint = GROUP_HEIGHT_190;
    	groupGD.widthHint = 400;
    	theGroup.setLayoutData(groupGD);
    	
    	this.delimitedFileContentsViewer = new ListViewer(theGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        this.delimitedFileContentsViewer.getControl().setFont(JFaceResources.getTextFont());
        this.delimitedFileContentsViewer.getControl().setLayoutData(data);
        
        if( this.dataFileInfo != null ) {
	        for( String row : this.dataFileInfo.getCachedFirstLines() ) {
	        	if( row != null ) {
	        		this.delimitedFileContentsViewer.add(row);
	        	}
	        }
        }
        
     // Add a Context Menu
        final MenuManager columnMenuManager = new MenuManager();
        this.delimitedFileContentsViewer.getControl().setMenu(columnMenuManager.createContextMenu(parent));
        this.delimitedFileContentsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
            	columnMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)delimitedFileContentsViewer.getSelection();
                if (sel.size() == 1) {
					columnMenuManager.add(delimitedParseRowAction);
					delimitedParseRowButton.setEnabled(true);
                } else {
                	delimitedParseRowButton.setEnabled(false);
                }

            }
        });
    	//LayoutDebugger.debugLayout(fileContentsGroup);
    }
    
    private void createDelimitedColumnInfoGroup(Composite parent) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("columnInfoGroup"), SWT.NONE, 2, 4); //$NON-NLS-1$
    	theGroup.setLayout(new GridLayout(4, true));
    	GridData groupGD = new GridData(GridData.FILL_BOTH);
    	groupGD.heightHint = 140;
    	theGroup.setLayoutData(groupGD);
    	
    	Table table = new Table(theGroup, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.delimitedColumnsViewer = new TableViewer(table);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 4;
        this.delimitedColumnsViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.delimitedColumnsViewer, SWT.LEFT);
        column.getColumn().setText(getString("columnName") + getSpaces(36)); //$NON-NLS-1$
        column.setEditingSupport(new ColumnNameEditingSupport(this.delimitedColumnsViewer));
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.delimitedColumnsViewer, SWT.LEFT);
        column.getColumn().setText(getString("datatype") + getSpaces(12)); //$NON-NLS-1$ 
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        column.setEditingSupport(new DatatypeEditingSupport(this.delimitedColumnsViewer));
        column.getColumn().pack();
    	
        if( this.dataFileInfo != null ) {
	        for( TeiidColumnInfo row : this.dataFileInfo.getColumnInfoList() ) {
	        	this.delimitedColumnsViewer.add(row);
	        }
        }
       
    	addColumnDelimitedButton = new Button(theGroup, SWT.PUSH);
    	addColumnDelimitedButton.setText(getString("addLabel")); //$NON-NLS-1$
    	addColumnDelimitedButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	addColumnDelimitedButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				createColumn();
			}
    		
		});
    	
    	deleteColumnDelimitedButton = new Button(theGroup, SWT.PUSH);
    	deleteColumnDelimitedButton.setText(getString("deleteLabel")); //$NON-NLS-1$
    	deleteColumnDelimitedButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	deleteColumnDelimitedButton.setEnabled(false);
    	deleteColumnDelimitedButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidColumnInfo info = null;
				
				IStructuredSelection selection = (IStructuredSelection)delimitedColumnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof TeiidColumnInfo ) {
						info =  (TeiidColumnInfo) obj;
						break;
					}
				}
				if( info != null ) {
					dataFileInfo.removeColumn(info);
					handleInfoChanged(false);
					deleteColumnDelimitedButton.setEnabled(false);
				}
			}
    		
		});
    	
    	upColumnDelimitedButton = new Button(theGroup, SWT.PUSH);
    	upColumnDelimitedButton.setText(getString("upLabel")); //$NON-NLS-1$
    	upColumnDelimitedButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	upColumnDelimitedButton.setEnabled(false);
    	upColumnDelimitedButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidColumnInfo info = null;
				
				IStructuredSelection selection = (IStructuredSelection)delimitedColumnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof TeiidColumnInfo ) {
						info =  (TeiidColumnInfo) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = delimitedColumnsViewer.getTable().getSelectionIndex();
					dataFileInfo.moveColumnUp(info);
					handleInfoChanged(false);
					delimitedColumnsViewer.getTable().select(selectedIndex-1);
					downColumnDelimitedButton.setEnabled(dataFileInfo.canMoveDown(info));
					upColumnDelimitedButton.setEnabled(dataFileInfo.canMoveUp(info));
				}
			}
    		
		});
    	
    	downColumnDelimitedButton = new Button(theGroup, SWT.PUSH);
    	downColumnDelimitedButton.setText(getString("downLabel")); //$NON-NLS-1$
    	downColumnDelimitedButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	downColumnDelimitedButton.setEnabled(false);
    	downColumnDelimitedButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidColumnInfo info = null;
				
				IStructuredSelection selection = (IStructuredSelection)delimitedColumnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof TeiidColumnInfo ) {
						info =  (TeiidColumnInfo) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = delimitedColumnsViewer.getTable().getSelectionIndex();
					dataFileInfo.moveColumnDown(info);
					handleInfoChanged(false);
					delimitedColumnsViewer.getTable().select(selectedIndex+1);
					downColumnDelimitedButton.setEnabled(dataFileInfo.canMoveDown(info));
					upColumnDelimitedButton.setEnabled(dataFileInfo.canMoveUp(info));
				}
			}
    		
		});
        
    	this.delimitedColumnsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				
				if( sel.isEmpty()) {
					deleteColumnDelimitedButton.setEnabled(false);
					upColumnDelimitedButton.setEnabled(false);
					downColumnDelimitedButton.setEnabled(false);
				} else {
					boolean enable = true;
					Object[] objs = sel.toArray();
					TeiidColumnInfo columnInfo = null;
					for( Object obj : objs) {
						if(  !(obj instanceof TeiidColumnInfo)) {
							enable = false;
							break;
						} else {
							columnInfo = (TeiidColumnInfo)obj;
						}
					} 
					if( objs.length == 0 ) {
						enable = false;
					}
					deleteColumnDelimitedButton.setEnabled(enable);
					if( enable ) {
						upColumnDelimitedButton.setEnabled(dataFileInfo.canMoveUp(columnInfo));
						downColumnDelimitedButton.setEnabled(dataFileInfo.canMoveDown(columnInfo));
					}
				}	
			}
		});
    }
    
    private void createFixedFileOptionsGroup(Composite parent) {
    	Group theGroup = WidgetFactory.createGroup(parent, "Format Options", SWT.NONE, 1, 2); //$NON-NLS-1$
    	theGroup.setLayout(new GridLayout(2, false));
	  	GridData groupGD = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	  	groupGD.heightHint=GROUP_HEIGHT_190;
	  	theGroup.setLayoutData(groupGD);
    	
    	Label firstDataRowLabel = new Label(theGroup, SWT.NONE);
    	firstDataRowLabel.setText(getString("firstRowLineNumber")); //$NON-NLS-1$
    	firstDataRowLabel.setToolTipText(getString("firstDataRowTooltip")); //$NON-NLS-1$
    	this.fixedFirstDataRowText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 20;
	    gd.horizontalSpan=1;
	    this.fixedFirstDataRowText.setLayoutData(gd);
    	this.fixedFirstDataRowText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	if( !synchronizing ) {
	            	if( !fixedFirstDataRowText.getText().isEmpty()) {
	            		try {
	        				int nLines = Integer.parseInt(fixedFirstDataRowText.getText());
	        				if( nLines < 1 ) {
	        					setErrorMessage(getString("firstDataRowCannotBeZeroOrNegative")); //$NON-NLS-1$
	        					return;
	        				}
	        				if( nLines != dataFileInfo.getFirstDataRow() ) {
	        					dataFileInfo.setFirstDataRow(nLines);
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
    	this.fixedFirstDataRowText.setToolTipText(getString("firstDataRowTooltip")); //$NON-NLS-1$
    	
    	Label curorPositionLabel = new Label(theGroup, SWT.NONE);
    	curorPositionLabel.setText("Cursor Position"); //$NON-NLS-1$
    	this.cursorPositionText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 20;
	    gd.horizontalSpan=1;
	    this.cursorPositionText.setLayoutData(gd);
	    this.cursorPositionText.setEditable(false);
	    this.cursorPositionText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
	    
	    Label selectedTextLengthLabel = new Label(theGroup, SWT.NONE);
	    selectedTextLengthLabel.setText("Text Length"); //$NON-NLS-1$
    	this.selectedTextLengthText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 20;
	    gd.horizontalSpan=1;
	    this.selectedTextLengthText.setLayoutData(gd);
	    this.selectedTextLengthText.setEditable(false);
	    this.selectedTextLengthText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
    }
    
    private void createFixedFileContentsGroup(Composite parent) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("fileContentsGroup"), SWT.NONE, 1, 4); //$NON-NLS-1$
    	theGroup.setLayout(new GridLayout(4, false));
    	GridData groupGD = new GridData(GridData.FILL_BOTH);
    	groupGD.heightHint = GROUP_HEIGHT_190;
    	groupGD.widthHint = 400;
    	theGroup.setLayoutData(groupGD);
    	
    	this.fixedFileContentsViewer = new TextViewer(theGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    	Document fileDocument = new Document();
    	fixedFileContentsViewer.setInput(fileDocument);
    	fixedFileContentsViewer.setEditable(false);
    	fileDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        fixedFileContentsViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        //data.heightHint = 130;
        this.fixedFileContentsViewer.getControl().setFont(JFaceResources.getTextFont());
        this.fixedFileContentsViewer.getControl().setLayoutData(data);
        
        if( this.dataFileInfo != null ) {
	        for( String row : this.dataFileInfo.getCachedFirstLines() ) {
	        	if( row != null ) {
	        		this.delimitedFileContentsViewer.add(row);
	        	}
	        }
        }
        final MenuManager fileContentsMenuManager = new MenuManager();
        this.fixedFileContentsViewer.getControl().setMenu(fileContentsMenuManager.createContextMenu(parent));
        this.fixedFileContentsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
            	fileContentsMenuManager.removeAll();
				if( event.getSelection() instanceof TextSelection) {
					TextSelection sel = (TextSelection)event.getSelection();
					if( sel.getLength() > 0 ) {
						
						fileContentsMenuManager.add(createColumnAction);
					}
				}

            }
        });
        
        this.fixedFileContentsViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if( event.getSelection() instanceof TextSelection) {
					TextSelection sel = (TextSelection)event.getSelection();
					int offset = fixedFileContentsViewer.getTextWidget().getCaretOffset();
		            int line = 0;
		            int lineOffset = 0;
		            try {
		                line = fixedFileContentsViewer.getDocument().getLineOfOffset(offset);
		                lineOffset = fixedFileContentsViewer.getDocument().getLineOffset(line);
		            } catch (BadLocationException exception) {
		            }
		            int column = offset - lineOffset;
					cursorPositionText.setText(Integer.toString(column));
					selectedTextLengthText.setText(Integer.toString(sel.getLength()));
				}
			}
		});
        
        this.createColumnAction = new Action(getString("createColumnActionLabel")) { //$NON-NLS-1$
            @Override
            public void run() {
            	createColumn();
            }
		};
    }
    
    private void createFixedColumnsOptionsGroup(Composite parent) {
	  	
	  	Group theGroup = WidgetFactory.createGroup(parent, getString("columnOptionsGroup"), SWT.NONE, 1, 1); //$NON-NLS-1$
	  	theGroup.setLayout(new GridLayout(1, false));
	  	GridData groupGD = new GridData();
	  	groupGD.heightHint=GROUP_HEIGHT_160;
	  	theGroup.setLayoutData(groupGD);
	  	
    	addColumnFixedButton = new Button(theGroup, SWT.PUSH);
    	addColumnFixedButton.setText(getString("addLabel")); //$NON-NLS-1$
    	addColumnFixedButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	addColumnFixedButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		createColumn();
				handleInfoChanged(false);
			}
    		
		});
    	
    	deleteColumnFixedButton = new Button(theGroup, SWT.PUSH);
    	deleteColumnFixedButton.setText(getString("deleteLabel")); //$NON-NLS-1$
    	deleteColumnFixedButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	deleteColumnFixedButton.setEnabled(false);
    	deleteColumnFixedButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidColumnInfo info = null;
				
				IStructuredSelection selection = (IStructuredSelection)fixedColumnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof TeiidColumnInfo ) {
						info =  (TeiidColumnInfo) obj;
						break;
					}
				}
				if( info != null ) {
					dataFileInfo.removeColumn(info);
					handleInfoChanged(false);
					deleteColumnFixedButton.setEnabled(false);
				}
			}
    		
		});
    	
    	upColumnFixedButton = new Button(theGroup, SWT.PUSH);
    	upColumnFixedButton.setText(getString("upLabel")); //$NON-NLS-1$
    	upColumnFixedButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	upColumnFixedButton.setEnabled(false);
    	upColumnFixedButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidColumnInfo info = null;
				
				IStructuredSelection selection = (IStructuredSelection)fixedColumnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof TeiidColumnInfo ) {
						info =  (TeiidColumnInfo) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = fixedColumnsViewer.getTable().getSelectionIndex();
					dataFileInfo.moveColumnUp(info);
					handleInfoChanged(false);
					fixedColumnsViewer.getTable().select(selectedIndex-1);
					downColumnFixedButton.setEnabled(dataFileInfo.canMoveDown(info));
					upColumnFixedButton.setEnabled(dataFileInfo.canMoveUp(info));
				}
			}
    		
		});
    	
    	downColumnFixedButton = new Button(theGroup, SWT.PUSH);
    	downColumnFixedButton.setText(getString("downLabel")); //$NON-NLS-1$
    	downColumnFixedButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	downColumnFixedButton.setEnabled(false);
    	downColumnFixedButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TeiidColumnInfo info = null;
				
				IStructuredSelection selection = (IStructuredSelection)fixedColumnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof TeiidColumnInfo ) {
						info =  (TeiidColumnInfo) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = fixedColumnsViewer.getTable().getSelectionIndex();
					dataFileInfo.moveColumnDown(info);
					handleInfoChanged(false);
					fixedColumnsViewer.getTable().select(selectedIndex+1);
					downColumnFixedButton.setEnabled(dataFileInfo.canMoveDown(info));
					upColumnFixedButton.setEnabled(dataFileInfo.canMoveUp(info));
				}
			}
    		
		});
    }
    
    private void createFixedColumnInfoGroup(Composite parent) {
    	Group columnInfoGroup = WidgetFactory.createGroup(parent, getString("columnInfoGroup"), SWT.NONE, 1, 1); //$NON-NLS-1$
    	columnInfoGroup.setLayout(new GridLayout(1, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = GROUP_HEIGHT_160;
    	columnInfoGroup.setLayoutData(gd);
    	
    	Table table = new Table(columnInfoGroup, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.fixedColumnsViewer = new TableViewer(table);
        
        GridData data = new GridData(GridData.FILL_BOTH);
        this.fixedColumnsViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.fixedColumnsViewer, SWT.LEFT);
        column.getColumn().setText(getString("columnName") + getSpaces(36)); //$NON-NLS-1$
        column.setEditingSupport(new ColumnNameEditingSupport(this.fixedColumnsViewer));
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.fixedColumnsViewer, SWT.LEFT);
        column.getColumn().setText(getString("datatype") + getSpaces(12)); //$NON-NLS-1$ 
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        column.setEditingSupport(new DatatypeEditingSupport(this.fixedColumnsViewer));
        column.getColumn().pack();
        
        column = new TableViewerColumn(this.fixedColumnsViewer, SWT.LEFT);
        column.getColumn().setText(getString("width") + getSpaces(12)); //$NON-NLS-1$ 
        column.setLabelProvider(new ColumnDataLabelProvider(2));
        column.setEditingSupport(new ColumnWidthEditingSupport(this.fixedColumnsViewer));
        column.getColumn().pack();
        
    	
        if( this.dataFileInfo != null ) {
	        for( TeiidColumnInfo row : this.dataFileInfo.getColumnInfoList() ) {
	        	this.fixedColumnsViewer.add(row);
	        }
        }
        
        this.fixedColumnsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				
				if( sel.isEmpty()) {
					deleteColumnFixedButton.setEnabled(false);
					upColumnFixedButton.setEnabled(false);
					downColumnFixedButton.setEnabled(false);
				} else {
					boolean enable = true;
					Object[] objs = sel.toArray();
					TeiidColumnInfo columnInfo = null;
					for( Object obj : objs) {
						if(  !(obj instanceof TeiidColumnInfo)) {
							enable = false;
							break;
						} else {
							columnInfo = (TeiidColumnInfo)obj;
						}
					} 
					if( objs.length == 0 ) {
						enable = false;
					}
					deleteColumnFixedButton.setEnabled(enable);
					if( enable ) {
						upColumnFixedButton.setEnabled(dataFileInfo.canMoveUp(columnInfo));
						downColumnFixedButton.setEnabled(dataFileInfo.canMoveDown(columnInfo));
					}
					
				}
				
			}
		});
       
    }
    
    private void createSqlGroup(Composite parent) {
    	Group textTableOptionsGroup = WidgetFactory.createGroup(parent, getString("textTableOptionsGroup"), SWT.NONE, 2, 1); //$NON-NLS-1$
    	textTableOptionsGroup.setLayout(new GridLayout(1, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 120;
    	gd.horizontalSpan = 2;
    	textTableOptionsGroup.setLayoutData(gd);
    	
    	ColorManager colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        sqlTextViewer = new SqlTextViewer(textTableOptionsGroup, new VerticalRuler(0), styles, colorManager);
        sqlDocument = new Document();
        sqlTextViewer.setInput(sqlDocument);
        sqlTextViewer.setEditable(false);
        sqlTextViewer.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        sqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        sqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        
        updateSqlText();
    }
    
//    private void createTextTableOptionsGroup(Composite parent) {
//    	Group theGroup = WidgetFactory.createGroup(parent, getString("teiidTextTableGroup"), SWT.NONE); //$NON-NLS-1$
//    	theGroup.setLayout(new GridLayout(7, false));
//    	GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
//    	groupGD.horizontalSpan = 2;
//    	theGroup.setLayoutData(groupGD);
//    	
//    	this.useHeaderInSQLCB = WidgetFactory.createCheckBox(theGroup, getString("includeHeader"), 0, 1); //$NON-NLS-1$
//        this.useHeaderInSQLCB.addSelectionListener(new SelectionAdapter() {
//
//            @Override
//            public void widgetSelected(final SelectionEvent event) {
//            	if( !synchronizing ) {
//	            	dataFileInfo.setIncludeHeader(useHeaderInSQLCB.getSelection());
//	            	handleInfoChanged(false);
//            	}
//            }
//        });
//        this.useHeaderInSQLCB.setToolTipText(getString("includeHeaderTooltip")); //$NON-NLS-1$
//        this.useHeaderInSQLCB.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//        
//        this.includeSkipCB = WidgetFactory.createCheckBox(theGroup, getString("includeSkip"), 0, 1); //$NON-NLS-1$
//        this.includeSkipCB.addSelectionListener(new SelectionAdapter() {
//
//            @Override
//            public void widgetSelected(final SelectionEvent event) {
//            	if( !synchronizing ) {
//	            	dataFileInfo.setIncludeSkip(includeSkipCB.getSelection());
//	            	handleInfoChanged(false);
//            	}
//            }
//        });
//        this.includeSkipCB.setToolTipText(getString("includeSkipTooltip")); //$NON-NLS-1$
//        this.includeSkipCB.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//        addSpacer(theGroup, 20);
//        
//        this.includeQuoteCB = WidgetFactory.createCheckBox(theGroup, getString("includeQuote"), 0, 1); //$NON-NLS-1$
//        this.includeQuoteCB.addSelectionListener(new SelectionAdapter() {
//
//            @Override
//            public void widgetSelected(final SelectionEvent event) {
//            	if( !synchronizing ) {
//	            	dataFileInfo.setIncludeQuote(includeQuoteCB.getSelection());
//	            	handleInfoChanged(false);
//            	}
//            }
//        });
//        
//        this.quoteText = WidgetFactory.createTextField(theGroup, SWT.NONE);
//    	GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
//    	gd.minimumWidth = 50;
//    	gd.horizontalSpan=1;
//    	this.quoteText.setLayoutData(gd);
//    	this.quoteText.setTextLimit(1);
//    	this.quoteText.addModifyListener(new ModifyListener() {
//
//            public void modifyText( final ModifyEvent event ) {
//            	if( !synchronizing ) {
//	            	if( !quoteText.getText().isEmpty()) {
//	            		if( quoteText.getText().charAt(0) != dataFileInfo.getQuote() ) {
//	            			dataFileInfo.setQuote(quoteText.getText().charAt(0));
//	            			handleInfoChanged(false);
//	            		}
//	            		setErrorMessage(null);
//	            	} else {
//	            		setErrorMessage(getString("quoteCannotBeNull")); //$NON-NLS-1$
//	            		return;
//	            	}
//            	}
//            }
//        });
//        
//        this.includeQuoteCB.setToolTipText(getString("includeQuoteTooltip")); //$NON-NLS-1$
//        
//        this.includeEscapeCB = WidgetFactory.createCheckBox(theGroup, getString("includeEscape"), 0, 1); //$NON-NLS-1$
//        this.includeEscapeCB.addSelectionListener(new SelectionAdapter() {
//
//            @Override
//            public void widgetSelected(final SelectionEvent event) {
//            	if( !synchronizing ) {
//	            	dataFileInfo.setIncludeEscape(includeEscapeCB.getSelection());
//	            	handleInfoChanged(false);
//            	}
//            }
//        });
//    	this.escapeText = WidgetFactory.createTextField(theGroup, SWT.NONE);
//    	gd.grabExcessHorizontalSpace = true;
//    	gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
//    	gd.minimumWidth = 50;
//    	gd.horizontalSpan=1;
//    	gd.grabExcessHorizontalSpace = true;
//    	this.escapeText.setLayoutData(gd);
//    	this.escapeText.setTextLimit(1);
//    	this.escapeText.addModifyListener(new ModifyListener() {
//
//            public void modifyText( final ModifyEvent event ) {
//            	if( !synchronizing ) {
//	            	if( !escapeText.getText().isEmpty()) {
//	            		if( escapeText.getText().charAt(0) != dataFileInfo.getEscape() ) {
//	            			dataFileInfo.setEscape(escapeText.getText().charAt(0));
//	            			handleInfoChanged(false);
//	            		}
//	            		setErrorMessage(null);
//	            	} else {
//	            		setErrorMessage(getString("escapeCannotBeNull")); //$NON-NLS-1$
//	            		return;
//	            	}
//            	}
//            }
//        });
//        
//        this.includeEscapeCB.setToolTipText(getString("includeEscapeTooltip")); //$NON-NLS-1$
//        
//    }
    
    private void createColumn() {
    	String newName = "column_" + (dataFileInfo.getColumnInfoList().length + 1); //$NON-NLS-1$
    	int length = 10;
    	if( selectedTextLengthText.getText().length() > 0 ) {
    		int textLength = Integer.parseInt(selectedTextLengthText.getText());
    		length = textLength > 0 ? textLength : length;
    	}
    	TeiidColumnInfo newColumn = new TeiidColumnInfo(newName, TeiidColumnInfo.DEFAULT_DATATYPE, length);
		dataFileInfo.addColumn(newColumn);
		
		handleInfoChanged(false);
    }
    
    void updateSqlText() {
    	if( this.dataFileInfo != null ) {
    		if( this.info.getSourceModelName() != null ) {
    			String modelName = this.dataFileInfo.getModelNameWithoutExtension(this.info.getSourceModelName());
    			sqlTextViewer.getDocument().set(dataFileInfo.getSqlString(modelName));
    		} else {
    			sqlTextViewer.getDocument().set(dataFileInfo.getSqlStringTemplate());
    		}
    	}
    }
    
    private String getSpaces(int nSpaces) {
    	StringBuffer sb = new StringBuffer(nSpaces);
    	for( int i=0; i<nSpaces; i++ ) {
    		sb.append(StringUtilities.SPACE);
    	}
    	return sb.toString();
    }
    
    private void parseDelimitedSelectedDataRow() {
    	IStructuredSelection selectedFile = (IStructuredSelection)this.delimitedFileContentsViewer.getSelection();
    	if( selectedFile.getFirstElement() != null && selectedFile.getFirstElement() instanceof String ) {
    		String dataRowStr = (String)selectedFile.getFirstElement();
    		ParsedDataRowDialog dialog = new ParsedDataRowDialog(getShell(), dataFileInfo, dataRowStr);
        	
        	dialog.open();
    	}
    }
    
    private void handleInfoChanged(boolean reloadFileContents) {
    	if( synchronizing ) return;
    	
    	synchronizeUI();
    	
    	if( reloadFileContents ) {
    		loadFileContentsViewers();
    	}

    	this.delimitedColumnsViewer.getTable().removeAll();
        for( TeiidColumnInfo row : dataFileInfo.getColumnInfoList() ) {
        	this.delimitedColumnsViewer.add(row);
        }
        
    	this.fixedColumnsViewer.getTable().removeAll();
        for( TeiidColumnInfo row : dataFileInfo.getColumnInfoList() ) {
        	this.fixedColumnsViewer.add(row);
        }
        
        validatePage();
    }
    
    private void loadFileContentsViewers() {
    	delimitedFileContentsViewer.getList().removeAll();
    	for( String row : this.dataFileInfo.getCachedFirstLines() ) {
        	if( row != null ) {
        		this.delimitedFileContentsViewer.add(row);
        	}
        }
    	
    	fixedFileContentsViewer.getDocument().set(EMPTY);
    	StringBuffer sb = new StringBuffer();
    	int i=0;
    	int nLines = this.dataFileInfo.getNumberOfCachedFileLines();
    	for( String row : this.dataFileInfo.getCachedFirstLines() ) {
        	if( row != null ) {
        		sb.append(row);
        		if( i< nLines) {
        			sb.append('\n');
        		}
        		i++;
        	}
        }
    	fixedFileContentsViewer.getDocument().set(sb.toString());
    }
    
    public TeiidMetadataFileInfo getFileInfo() {
    	return this.dataFileInfo;
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
					delimitedColumnsViewer.refresh(element);
					fixedColumnsViewer.refresh(element);
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
					fixedColumnsViewer.refresh(element);
				}
			}
		}

	}

}

