/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
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
    
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    
	private final TeiidMetadataFileInfo fileInfo;
	private boolean infoChanged;
	
	ListViewer fileContentsViewer;
	TableViewer columnsViewer;
	Text headerLineNumberText;
	Text delimiterText;

    
    /**
     * @param parent
     * @param title
     * @since 7.4
     */
    public TeiidDataFileAnalyzerDialog( Shell parent,
                                     TeiidMetadataFileInfo fileInfo) {

        super(parent);
        this.fileInfo = fileInfo;
        setDefaultImage(UiPlugin.getDefault().getImage(Images.IMPORT_TEIID_METADATA));
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
        Composite mainPanel = WidgetFactory.createPanel(parent, SWT.NONE, GridData.FILL_BOTH, 1);

        this.setTitle(getString("messageTitle")); //$NON-NLS-1$
        this.setMessage(getString("initialMessage")); //$NON-NLS-1$
//        this.setTitleImage(UiPlugin.getDefault().getImage(UiConstants.Images.MANAGE_EXTENDED_PROPERTIES_ICON));
        mainPanel.setLayout(new GridLayout(1, false));
        mainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        createFileContentsGroup(mainPanel);
        
        createOptionsGroup(mainPanel);
        
        createColumnInfoGroup(mainPanel);
        
        validate();
        
        return mainPanel;
    }
    
    private void createFileContentsGroup(Composite parent) {
    	Group fileContentsGroup = WidgetFactory.createGroup(parent, getString("fileContentsGroup"), SWT.NONE, 1, 1); //$NON-NLS-1$
    	fileContentsGroup.setLayout(new GridLayout(1, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 140;
    	gd.widthHint = 500;
    	fileContentsGroup.setLayoutData(gd);
    	Label fileLabel = new Label(fileContentsGroup, SWT.NONE);
    	fileLabel.setText(Util.getString(I18N_PREFIX + "location", fileInfo.getDataFile().getName())); //$NON-NLS-1$
    	
    	fileContentsViewer = new ListViewer(fileContentsGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        fileContentsViewer.getControl().setLayoutData(data);
        for( String row : this.fileInfo.getFirstSixLines() ) {
        	fileContentsViewer.add(row);
        }
    }
    
    private void createOptionsGroup(Composite parent) {
    	Group fileOptionsGroup = WidgetFactory.createGroup(parent, getString("fileFormatOptionsGroup"), SWT.NONE, 1, 2); //$NON-NLS-1$
    	fileOptionsGroup.setLayout(new GridLayout(2, false));
    	fileOptionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	
    	Label delimiterLabel = new Label(fileOptionsGroup, SWT.NONE);
    	delimiterLabel.setText(getString("delimiter")); //$NON-NLS-1$
    	delimiterText = WidgetFactory.createTextField(fileOptionsGroup, SWT.NONE);
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.minimumWidth = 100;
    	delimiterText.setLayoutData(gd);
    	delimiterText.setText("" + this.fileInfo.getDelimiter()); //$NON-NLS-1$
    	delimiterText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	handleInfoChanged();
            }
        });
    	
    	Label headerLineNumberLabel = new Label(fileOptionsGroup, SWT.NONE);
    	headerLineNumberLabel.setText(getString("headerLineNumber")); //$NON-NLS-1$
    	headerLineNumberText = WidgetFactory.createTextField(fileOptionsGroup, SWT.NONE);
    	gd = new GridData(GridData.FILL_BOTH);
    	gd.minimumWidth = 100;
    	headerLineNumberText.setLayoutData(gd);
    	headerLineNumberText.setText(Integer.toString(fileInfo.getHeaderLineNumber()));
    	headerLineNumberText.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
            	handleInfoChanged();
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

    	columnsViewer = new TableViewer(table);
        GridData data = new GridData(GridData.FILL_BOTH);
        columnsViewer.getControl().setLayoutData(data);
        
        // create columns
        TableViewerColumn column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText("ColumnName"); //getString("dataFileNameColumn")); //$NON-NLS-1$
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText("Datatype"); //getString("viewFileNameColumn")); //$NON-NLS-1$
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        column.setEditingSupport(new DatatypeEditingSupport(this.columnsViewer));
        column.getColumn().pack();
        
    	

        for( TeiidColumnInfo row : this.fileInfo.getColumnInfoList() ) {
        	columnsViewer.add(row);
        }
        
       
    }
    
    private void handleInfoChanged() {
    	this.infoChanged = true;
    	if( !delimiterText.getText().isEmpty()) {
    		fileInfo.setDelimiter(delimiterText.getText().charAt(0));
    	} else {
    		setErrorMessage(getString("delimiterCannotBeNull")); //$NON-NLS-1$
    		return;
    	}
    	
    	if( !headerLineNumberText.getText().isEmpty()) {
    		try {
				int lineNumber = Integer.parseInt(headerLineNumberText.getText());
				if( lineNumber == 0 ) {
					setErrorMessage(getString("headerLineNumberCannotBeNullOrZero")); //$NON-NLS-1$
					return;
				}
				fileInfo.setHeaderLineNumber(lineNumber);
			} catch (NumberFormatException ex) {
				setErrorMessage(Util.getString(I18N_PREFIX + "headerLineNumberMustBeInteger", headerLineNumberText.getText())); //$NON-NLS-1$
				return;
			}
    	} else {
    		setErrorMessage(getString("headerLineNumberCannotBeNullOrZero")); //$NON-NLS-1$
    		return;
    	}

        columnsViewer.getTable().removeAll();
        for( TeiidColumnInfo row : fileInfo.getColumnInfoList() ) {
        	columnsViewer.add(row);
        }
        
        validate();
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
			// Element should be a "File"
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
				}
			}
			return ""; //$NON-NLS-1$
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
				return UiPlugin.getDefault().getImage(PluginConstants.Images.WARNING_ICON);

			}
			return null;
		}
		
		
	}
}
