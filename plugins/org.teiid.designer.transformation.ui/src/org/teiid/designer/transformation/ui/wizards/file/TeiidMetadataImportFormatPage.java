/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.file;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;


/**
 * Page allows user to select Delimited or Fixed Width column formatted flat file
 *
 * @since 8.0
 */
public class TeiidMetadataImportFormatPage extends AbstractWizardPage implements UiConstants {
	// ===========================================================================================================================
	// Constants

	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataImportFormatPage.class);

	private static final String TITLE = getString("title"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

	private final String EMPTY = StringUtilities.EMPTY_STRING;
	private final int GROUP_HEIGHT_190 = 190;

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
	Text selectedFileText;
	Text numberPreviewLinesText;
	Text numberLinesInFileText;
	
	// ====================================================
	// DELIMITED OPTION WIDGETS
	ListViewer fileContentsViewer;
	Button delimitedColumnsRB;
	
	// ====================================================
	// FIXED COLUMN WIDTH OPTION WIDGETS
	Button fixedWidthColumnsRB;

	boolean creatingControl = false;

	boolean synchronizing = false;

	/**
     * @param info the import data (cannot be <code>null</code>)
	 * @since 4.0
	 */
	public TeiidMetadataImportFormatPage(TeiidMetadataImportInfo info) {
		super(TeiidMetadataImportFormatPage.class.getSimpleName(), TITLE);

        CoreArgCheck.isNotNull(info, "info"); //$NON-NLS-1$
        this.info = info;

		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(
				Images.IMPORT_TEIID_METADATA));
	}

	@Override
	public void createControl(Composite parent) {
		creatingControl = true;
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(1, false));
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
		selectedFileText.setEditable(false);
		
		createFilePreviewOptionsGroup(mainPanel);
		
		createColumnOptionsRadioGroup(mainPanel);

		createFileContentsGroup(mainPanel);
        
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
		if(dataFileInfo==null) return false;
		
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

		if(dataFileInfo==null) return;

		String charset = this.info.getFileInfo(this.dataFileInfo.getDataFile()).getCharset();
		if (!charset.equals(this.dataFileInfo.getCharset())) {
		    this.dataFileInfo = this.info.getFileInfo(this.dataFileInfo.getDataFile());
		    loadFileContentsViewers();
		}

		selectedFileText.setText(dataFileInfo.getDataFile().getName());

		boolean isDelimitedOption = this.dataFileInfo.doUseDelimitedColumns();

		{ // number of preview lines
		    final String numLines = Integer.toString(this.dataFileInfo.getNumberOfCachedFileLines());

		    if (!numLines.equals(this.numberPreviewLinesText.getText())) {
		        this.numberPreviewLinesText.setText(numLines);
		    }
		}
    	
    	this.delimitedColumnsRB.setSelection(isDelimitedOption);
    	this.fixedWidthColumnsRB.setSelection(!isDelimitedOption);

    	this.numberLinesInFileText.setText(Integer.toString(this.dataFileInfo.getNumberOfLinesInFile()));
    	
		synchronizing = false;
	}
    
    private void createColumnOptionsRadioGroup(Composite parent ) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("columnsFormatGroup"), SWT.NONE, 1, 2); //$NON-NLS-1$
    	theGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
    	// delimitedColumnsRB, fixedWidthColumnsRB;
    	this.delimitedColumnsRB = WidgetFactory.createRadioButton(theGroup, getString("characterDelimited")); //$NON-NLS-1$
    	
    	this.delimitedColumnsRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing && !creatingControl ) {
            		if( event.getSource() == delimitedColumnsRB ) {
            			if( delimitedColumnsRB.getSelection() != dataFileInfo.doUseDelimitedColumns() ) {
			            	dataFileInfo.setUseDelimitedColumns(delimitedColumnsRB.getSelection());
	            			dataFileInfo.setFixedWidthColumns(!delimitedColumnsRB.getSelection());
	            			if( dataFileInfo.getColumnInfoList().size() > 0 ) {
	            				boolean result = MessageDialog.openQuestion(getShell(),
		            					getString("formatChangedTitle"), //$NON-NLS-1$
		            					getString("formateChangedMessage")); //$NON-NLS-1$
	            				if( result ) {
	            					dataFileInfo.clearColumns();
	            				}
    						}
			            	handleInfoChanged(false);
            			}
            		}
            	}
            }
        });
    	
    	this.fixedWidthColumnsRB = WidgetFactory.createRadioButton(theGroup, getString("fixedWidth")); //$NON-NLS-1$
    	
    	this.fixedWidthColumnsRB.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent event) {
            	if( !synchronizing && !creatingControl ) {
            		if( event.getSource() == fixedWidthColumnsRB ) {
            			if( fixedWidthColumnsRB.getSelection() != dataFileInfo.isFixedWidthColumns() ) {
			            	dataFileInfo.setFixedWidthColumns(fixedWidthColumnsRB.getSelection());
			            	dataFileInfo.setUseDelimitedColumns(!fixedWidthColumnsRB.getSelection());
	            			if( dataFileInfo.getColumnInfoList().size() > 0 ) {
	            				boolean result = MessageDialog.openQuestion(getShell(), 
	            					getString("formatChangedTitle"), //$NON-NLS-1$
	            					getString("formateChangedMessage")); //$NON-NLS-1$
	            				if( result ) {
	            					dataFileInfo.clearColumns();
	            				}
    						}
			            	handleInfoChanged(false);
            			}
            		}
            	}
            }
        });
    	
    	
    	this.delimitedColumnsRB.setSelection(true);
    }
    
    private void createFilePreviewOptionsGroup(Composite parent ) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("filePreviewOptionsGroup"), SWT.NONE, 1, 5); //$NON-NLS-1$
    	theGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
    	Label numberLinesInFileLabel = new Label(theGroup, SWT.NONE);
    	numberLinesInFileLabel.setText(getString("numberOfLinesLabel")); //$NON-NLS-1$
    	
    	numberLinesInFileText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	numberLinesInFileText.setEditable(false);
    	numberLinesInFileText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
    	GridData gd = new GridData();
    	gd.minimumWidth = 50;
    	
    	Label spacer = new Label(theGroup, SWT.NONE);
    	spacer.setText("                    "); //$NON-NLS-1$
    	
    	Label prefixLabel = new Label(theGroup, SWT.NONE);
    	prefixLabel.setText(getString("numberOfPreviewLines")); //$NON-NLS-1$
    	prefixLabel.setLayoutData(new GridData()); // new GridData(GridData.FILL_HORIZONTAL));
        
    	this.numberPreviewLinesText = WidgetFactory.createTextField(theGroup, SWT.NONE);
    	gd = new GridData();
    	gd.minimumWidth = 50;

    	this.numberPreviewLinesText.setLayoutData(gd);
    	this.numberPreviewLinesText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			if( !synchronizing ) {
	    			if( !numberPreviewLinesText.getText().isEmpty()) {
	            		try {
	        				int nLines = Integer.parseInt(numberPreviewLinesText.getText());
	        				if( nLines == 0 ) {
	        					setErrorMessage(getString("numberOfLinesCannotBeNullOrZero")); //$NON-NLS-1$
	        					return;
	        				}
	        				if( nLines != dataFileInfo.getNumberOfCachedFileLines() ) {
	        					dataFileInfo.setNumberOfCachedFileLines(nLines);
	        					handleInfoChanged(true);
	        				}
	        				setErrorMessage(null);
	        			} catch (NumberFormatException ex) {
	        				setErrorMessage(getString("numberOfLinesMustBeInteger", numberPreviewLinesText.getText())); //$NON-NLS-1$
	        				return;
	        			}
	            	} else {
	            		setErrorMessage(getString("numberOfLinesCannotBeNullOrZero")); //$NON-NLS-1$
	            		return;
	            	}
    			}
    		}
    	});

    	
    }
    

    
    private void createFileContentsGroup(Composite parent) {
    	Group theGroup = WidgetFactory.createGroup(parent, getString("fileContentsGroup"), SWT.NONE, 1, 4); //$NON-NLS-1$
    	GridData groupGD = new GridData(GridData.FILL_BOTH);
    	groupGD.heightHint = GROUP_HEIGHT_190;
    	groupGD.widthHint = 400;
    	theGroup.setLayoutData(groupGD);
    	
    	this.fileContentsViewer = new ListViewer(theGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=4;
        this.fileContentsViewer.getControl().setFont(JFaceResources.getTextFont());
        this.fileContentsViewer.getControl().setLayoutData(data);
        
        if( this.dataFileInfo != null ) {
	        for( String row : this.dataFileInfo.getCachedFirstLines() ) {
	        	if( row != null ) {
	        		this.fileContentsViewer.add(row);
	        	}
	        }
        }
        
     // Add a Context Menu
        final MenuManager columnMenuManager = new MenuManager();
        this.fileContentsViewer.getControl().setMenu(columnMenuManager.createContextMenu(parent));
    }
    
    private void handleInfoChanged(boolean reloadFileContents) {
    	if( synchronizing ) return;
    	
    	synchronizeUI();
    	
    	if( reloadFileContents ) {
    		loadFileContentsViewers();
    	}
        
        validatePage();
    }
    
    private void loadFileContentsViewers() {
    	fileContentsViewer.getList().removeAll();
    	for( String row : this.dataFileInfo.getCachedFirstLines() ) {
        	if( row != null ) {
        		this.fileContentsViewer.add(row);
        	}
        }
    }
}
