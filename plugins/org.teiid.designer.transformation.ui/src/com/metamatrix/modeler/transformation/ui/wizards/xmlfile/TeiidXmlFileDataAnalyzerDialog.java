/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.types.DataTypeManager;
import org.xml.sax.SAXException;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlTextViewer;
import com.metamatrix.modeler.transformation.ui.wizards.file.TeiidColumnInfo;
import com.metamatrix.query.internal.ui.sqleditor.sql.ColorManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.table.CheckBoxEditingSupport;
import com.metamatrix.ui.table.ComboBoxEditingSupport;

public class TeiidXmlFileDataAnalyzerDialog  extends TitleAreaDialog implements UiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidXmlFileDataAnalyzerDialog.class);
    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String EMPTY = ""; //$NON-NLS-1$
    public static final int NAME_PROP = 0;
    public static final int DEFAULT_VALUE_PROP = 1;
    public static final int XML_PATH_PROP = 2;
    
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
    private static String getString( final String id , Object param) {
        return Util.getString(I18N_PREFIX + id, param);
    }
    
	private final TeiidXmlFileInfo fileInfo;
	private boolean infoChanged;
	
	Group headerGroup;
	ListViewer fileContentsViewer;
	TextViewer sqlTextViewer;
	IDocument sqlDocument;
	Label numberOfFixedWidthColumnsLabel;
	Text numberOfCachedLinesText, headerLineNumberText;
	Button parseRowButton;
	Action parseRowAction;
	Button deleteButton;
	
	EditColumnsPanel columnsPanel;
	
	boolean synchronizing = false;

    
    /**
     * @param parent
     * @param title
     * @since 7.4
     */
    public TeiidXmlFileDataAnalyzerDialog( Shell parent,
                                     TeiidXmlFileInfo fileInfo) {

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

        
        createXQueryOptionsGroup(mainPanel);

        // Create Bottom Composite
        createColumnInfoGroup(mainPanel);
        
        createXmlTableSqlGroup(mainPanel);
        
        synchronizeUI();
        
        validate();
        //LayoutDebugger.debugLayout(mainPanel);
        return mainPanel;
    }
    
    
    private void synchronizeUI() {
    	// This method takes the Business Object (TeiidMetadataFileInfo) and syncs all ui Objects
    	this.synchronizing = true;
    	
    	this.numberOfCachedLinesText.setText(Integer.toString(this.fileInfo.getNumberOfCachedFileLines()));
    	
    	this.synchronizing = false;
    }
    
    private void createXQueryOptionsGroup(Composite parent) {
    	Group xQueryOptionsGroup = WidgetFactory.createGroup(parent, getString("xQueryOptionsGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	xQueryOptionsGroup.setLayout(new GridLayout(2, false));
    	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    	xQueryOptionsGroup.setLayoutData(gd);
    	
    	Label prefixLabel = new Label(xQueryOptionsGroup, SWT.NONE);
    	prefixLabel.setText(getString("xQueryExpressionLabel")); //$NON-NLS-1$
        
    	final Text xQueryText = WidgetFactory.createTextField(xQueryOptionsGroup, SWT.NONE);
    	gd = new GridData(GridData.FILL_HORIZONTAL);
    	gd.minimumWidth = 50;
    	gd.horizontalSpan=1;
    	gd.grabExcessHorizontalSpace = true;
    	xQueryText.setLayoutData(gd);
    	xQueryText.addModifyListener(new ModifyListener() {
    		public void modifyText( final ModifyEvent event ) {
    			if( !synchronizing ) {
	    			if( !xQueryText.getText().isEmpty()) {
        				fileInfo.setXQueryExpression(xQueryText.getText());
        				setErrorMessage(null);
        				handleInfoChanged(false);
	            	} else {
	            		setErrorMessage(getString("expressionCannotBeEmpty")); //$NON-NLS-1$
	            	}
    			}
    		}
    	});
    }
    
    private void createXmlTableSqlGroup(Composite parent) {
    	Group xmlTableOptionsGroup = WidgetFactory.createGroup(parent, getString("teiidXMLTableGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	xmlTableOptionsGroup.setLayout(new GridLayout(1, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 120;
    	xmlTableOptionsGroup.setLayoutData(gd);
    	
    	ColorManager colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        sqlTextViewer = new SqlTextViewer(xmlTableOptionsGroup, new VerticalRuler(0), styles, colorManager);
        sqlDocument = new Document();
        sqlTextViewer.setInput(sqlDocument);
        sqlTextViewer.setEditable(false);
        sqlTextViewer.getTextWidget().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        sqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        sqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
//        String sqlText = "SELECT \n\ttitle.pmid AS pmid, title.journal AS journal, title.title AS title\n" + 
//        			"FROM \n\t(EXEC getMeds.getTextFiles('medsamp2011.xml')) AS f," +  
//        			"XMLTABLE('$d/MedlineCitationSet/MedlineCitation' PASSING " +
//        			"XMLPARSE(DOCUMENT f.file) AS d " + 
//        			"COLUMNS pmid biginteger PATH 'PMID', journal string PATH 'Article/Journal/Title', title string PATH 'Article/ArticleTitle') AS title";
        updateSqlText();
    }
    
    private void createFileContentsGroup(Composite parent) {
    	Group fileContentsGroup = WidgetFactory.createGroup(parent, getString("fileContentsGroup"), SWT.NONE, 1, 4); //$NON-NLS-1$
    	fileContentsGroup.setLayout(new GridLayout(4, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 350;
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
//        this.fileContentsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
//            /**
//             * {@inheritDoc}
//             * 
//             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
//             */
//            @Override
//            public void selectionChanged( final SelectionChangedEvent event ) {
//            	columnMenuManager.removeAll();
//                IStructuredSelection sel = (IStructuredSelection)fileContentsViewer.getSelection();
//                if (sel.size() == 1) {
//					columnMenuManager.add(parseRowAction);
//					parseRowButton.setEnabled(true);
//                } else {
//                	parseRowButton.setEnabled(false);
//                }
//
//            }
//        });
        
        
        this.parseRowAction = new Action(getString("parseSelectedRow")) { //$NON-NLS-1$
            @Override
            public void run() {
            	parseSelectedDataRow();
            }
		};
    	
        this.parseRowButton = WidgetFactory.createButton(fileContentsGroup, SWT.PUSH);
        this.parseRowButton.setText(getString("parseSelectedRow")); //$NON-NLS-1$
    	gd = new GridData(GridData.FILL_BOTH);
    	gd.minimumWidth = 100;
    	gd.minimumHeight = 25;
    	this.parseRowButton.setLayoutData(gd);
        this.parseRowButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
            	parseSelectedDataRow();
            }
        });
        this.parseRowButton.setEnabled(true);
        this.parseRowButton.setToolTipText(getString("parseSelectedTooltip")); //$NON-NLS-1$
        

    	//LayoutDebugger.debugLayout(fileContentsGroup);
    }
    
    

    
    private void createColumnInfoGroup(Composite parent) {
    	Group columnInfoGroup = WidgetFactory.createGroup(parent, getString("columnInfoGroup"), SWT.NONE, 1); //$NON-NLS-1$
    	columnInfoGroup.setLayout(new GridLayout(2, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 160;
    	columnInfoGroup.setLayoutData(gd);
    	

    	Composite leftToolbarPanel = new Composite(columnInfoGroup, SWT.NONE);
    	leftToolbarPanel.setLayout(new GridLayout());
    	gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    	gd.horizontalIndent = 1;
    	gd.verticalIndent = 1;
    	leftToolbarPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    	
    	Button addButton = new Button(leftToolbarPanel, SWT.PUSH);
    	addButton.setText(getString("addLabel")); //$NON-NLS-1$
    	addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String newName = "column_" + (fileInfo.getColumnInfoList().length + 1); //$NON-NLS-1$
				fileInfo.addColumn(newName, false, TeiidColumnInfo.DEFAULT_DATATYPE, null, null);
				handleInfoChanged(false);
			}
    		
		});
    	
    	deleteButton = new Button(leftToolbarPanel, SWT.PUSH);
    	deleteButton.setText(getString("deleteLabel")); //$NON-NLS-1$
    	deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	deleteButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if( !columnsPanel.getSelectedColumns().isEmpty() ) {
					for( TeiidColumnInfo info : columnsPanel.getSelectedColumns()) {
						fileInfo.removeColumn(info);
					}
					handleInfoChanged(false);
					deleteButton.setEnabled(false);
				}
			}
    		
		});
    	
    	columnsPanel = new EditColumnsPanel(columnInfoGroup, SWT.NONE, this.fileInfo);
    	
    	columnsPanel.addSelectionListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				
				if( sel.isEmpty()) {
					deleteButton.setEnabled(false);
				} else {
					boolean enable = true;
					Object[] objs = sel.toArray();
					for( Object obj : objs) {
						if(  !(obj instanceof TeiidColumnInfo)) {
							enable = false;
							break;
						}
					}
					deleteButton.setEnabled(enable);
				}
				
			}
		});
    	
    	
    	//LayoutDebugger.debugLayout(columnInfoGroup);
       
    }
    
    private String getSpaces(int nSpaces) {
    	StringBuffer sb = new StringBuffer(nSpaces);
    	for( int i=0; i<nSpaces; i++ ) {
    		sb.append(StringUtilities.SPACE);
    	}
    	return sb.toString();
    }
    
    private void parseSelectedDataRow() {
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	//Using factory get an instance of document builder
        try {
			DocumentBuilder db = factory.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			@SuppressWarnings("unused")
			org.w3c.dom.Document document = db.parse(fileInfo.getDataFile().getAbsolutePath());
			
			//get the root element
            //Element root = document.getDocumentElement();
            
            String title = getString("xmlDataFileXmlParsingResultOK.title"); //$NON-NLS-1$
            String message = getString("xmlDataFileXmlParsingResultOK.message", fileInfo.getDataFile().getName()); //$NON-NLS-1$
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), title, message);
            
		} catch (ParserConfigurationException ex) {
			String title = getString("xmlDataFileXmlParsingError.title"); //$NON-NLS-1$
            String message = getString("xmlDataFileXmlParsingError", fileInfo.getDataFile().getName()) + ex.getMessage(); //$NON-NLS-1$
            MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
		} catch (SAXException ex) {
			String title = getString("xmlDataFileXmlParsingError.title"); //$NON-NLS-1$
            String message = getString("xmlDataFileXmlParsingError", fileInfo.getDataFile().getName()) + ex.getMessage(); //$NON-NLS-1$
            MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
		} catch (IOException ex) {
			String title = getString("xmlDataFileXmlParsingError.title"); //$NON-NLS-1$
            String message = getString("xmlDataFileXmlParsingError", fileInfo.getDataFile().getName()) + ex.getMessage(); //$NON-NLS-1$
            MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
		}
    }
    
    private void handleInfoChanged(boolean reloadFileContents) {
    	if( synchronizing ) return;
    	
    	this.infoChanged = true;
    	
    	synchronizeUI();
    	
    	if( reloadFileContents ) {
    		loadFileContentsViewer();
    	}

    	this.columnsPanel.refresh();
    	
    	updateSqlText();
        
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
    
    public TeiidXmlFileInfo getFileInfo() {
    	return this.fileInfo;
    }
    
    public boolean infoChanged() {
    	return this.infoChanged;
    }
    
    void updateSqlText() {
        sqlTextViewer.getDocument().set(fileInfo.getSqlStringTemplate());
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
						return EMPTY;
					}
					case 2: {
						if(element instanceof TeiidColumnInfo) {
							return ((TeiidColumnInfo)element).getDatatype();
						}
					}
					case 3: {
						if(element instanceof TeiidColumnInfo) {
							return ((TeiidColumnInfo)element).getDefaultValue();
						}
					}
					case 4: {
						if(element instanceof TeiidColumnInfo) {
							return ((TeiidColumnInfo)element).getXmlPath();
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
			} else if( this.columnNumber == 1 ) {
				if(element instanceof TeiidColumnInfo) {
					if( ((TeiidColumnInfo)element).getOrdinality() ) {
						return UiPlugin.getDefault().getImage(PluginConstants.Images.CHECKED_BOX_ICON);
					} else {
						return UiPlugin.getDefault().getImage(PluginConstants.Images.UNCHECKED_BOX_ICON);
					}
				}
				return null;
			}
			return null;
		}
		
		
	}
    
    class ColumnInfoTextEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;
		private int type;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer
		 */
		public ColumnInfoTextEditingSupport(ColumnViewer viewer, int type) {
			super(viewer);
			this.type = type;
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
				switch(this.type) {
					case NAME_PROP: {
						return ((TeiidColumnInfo)element).getName();
					}
					case DEFAULT_VALUE_PROP: {
						return ((TeiidColumnInfo)element).getDefaultValue();
					}
					case XML_PATH_PROP: {
						return ((TeiidColumnInfo)element).getXmlPath();
					}
				}
			}
			return EMPTY;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		protected void setValue(Object element, Object value) {
			if( element instanceof TeiidColumnInfo ) {
				switch(this.type) {
					case NAME_PROP: {
						String oldValue = ((TeiidColumnInfo)element).getName();
						String newValue = (String)value;
						if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((TeiidColumnInfo)element).setName(newValue);
							columnsPanel.refresh(element);
							handleInfoChanged(false);
						}
					} break;
					case DEFAULT_VALUE_PROP: {
						String oldValue = ((TeiidColumnInfo)element).getDefaultValue();
						String newValue = (String)value;
						if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((TeiidColumnInfo)element).setDefaultValue(newValue);
							columnsPanel.refresh(element);
							handleInfoChanged(false);
						}
					} break;
					case XML_PATH_PROP: {
						String oldValue = ((TeiidColumnInfo)element).getXmlPath();
						String newValue = (String)value;
						if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
							((TeiidColumnInfo)element).setXmlPath(newValue);
							columnsPanel.refresh(element);
							handleInfoChanged(false);
						}
					} break;
				}
				
			}
		}

	}
    
    class DatatypeComboEditingSupport extends ComboBoxEditingSupport {
    	
    	private String[] datatypes;
        /**
         * @param viewer
         */
        public DatatypeComboEditingSupport( ColumnViewer viewer ) {
            super(viewer);
    		Set<String> unsortedDatatypes = DataTypeManager.getAllDataTypeNames();
    		Collection<String> dTypes = new ArrayList<String>();
    		
    		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
    		Arrays.sort(sortedStrings);
    		for( String dType : sortedStrings ) {
    			dTypes.add(dType);
    		}
    		
    		datatypes = dTypes.toArray(new String[dTypes.size()]);
    		
        }


        @Override
        protected String getElementValue( Object element ) {
        	return ((TeiidColumnInfo)element).getDatatype();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
        	if( !((TeiidColumnInfo)element).getOrdinality() ) {
	            ((TeiidColumnInfo)element).setDatatype(newValue);
	            handleInfoChanged(false);
        	}
        }
    }
        
    class EditColumnsPanel  {
    	TableViewer columnsViewer;
    	
    	TeiidXmlFileInfo fileInfo;
    	
		public EditColumnsPanel(Composite parent, int style, TeiidXmlFileInfo fileInfo) {
			super();
			
			this.fileInfo = fileInfo;
			createPanel(parent);
		}
		
		private void createPanel(Composite parent) {
	    	
	    	Table table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
	        table.setHeaderVisible(true);
	        table.setLinesVisible(true);
	        table.setLayout(new TableLayout());
	        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	        this.columnsViewer = new TableViewer(table);
	        
	        GridData data = new GridData(GridData.FILL_BOTH);
	        this.columnsViewer.getControl().setLayoutData(data);
	        
	        // create columns
	        TableViewerColumn column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText(getString("columnName") + getSpaces(30)); //$NON-NLS-1$
	        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, NAME_PROP));
	        column.setLabelProvider(new ColumnDataLabelProvider(0));
	        column.getColumn().pack();
	        
	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText(getString("forOrdinality")); //$NON-NLS-1$ 
	        column.setLabelProvider(new ColumnDataLabelProvider(1));
	        column.setEditingSupport(new OrdinalityEditingSupport(this.columnsViewer));
	        column.getColumn().pack();

	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText(getString("datatype") + getSpaces(6)); //$NON-NLS-1$ 
	        column.setLabelProvider(new ColumnDataLabelProvider(2));
	        column.setEditingSupport(new DatatypeComboEditingSupport(this.columnsViewer));
	        column.getColumn().pack();
	        
	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText(getString("defaultValue") + getSpaces(6)); //$NON-NLS-1$ 
	        column.setLabelProvider(new ColumnDataLabelProvider(3));
	        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, DEFAULT_VALUE_PROP));
	        column.getColumn().pack();
	        
	        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
	        column.getColumn().setText(getString("path") + getSpaces(30)); //$NON-NLS-1$ 
	        column.setLabelProvider(new ColumnDataLabelProvider(4));
	        column.setEditingSupport(new ColumnInfoTextEditingSupport(this.columnsViewer, XML_PATH_PROP));
	        column.getColumn().pack();
	        

	        for( TeiidColumnInfo row : this.fileInfo.getColumnInfoList() ) {
	        	this.columnsViewer.add(row);
	        }
		}
        
		public void refresh() {
	    	this.columnsViewer.getTable().removeAll();
	        for( TeiidColumnInfo row : fileInfo.getColumnInfoList() ) {
	        	this.columnsViewer.add(row);
	        }
		}
		
		public void refresh(Object element) {
			this.columnsViewer.refresh(element);
		}
		
		public void addSelectionListener(ISelectionChangedListener listener) {
			this.columnsViewer.addSelectionChangedListener(listener);
		}
		
		public Collection<TeiidColumnInfo> getSelectedColumns() {
			Collection<TeiidColumnInfo> columns = new ArrayList<TeiidColumnInfo>();
			
			IStructuredSelection selection = (IStructuredSelection)this.columnsViewer.getSelection();
			for( Object obj : selection.toArray()) {
				if( obj instanceof TeiidColumnInfo ) {
					columns.add((TeiidColumnInfo)obj);
				}
			}
			
			return columns;
		}
		
    	
    }
    
    class OrdinalityEditingSupport extends CheckBoxEditingSupport {

		public OrdinalityEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setElementValue(Object element, Object newValue) {
			if( element instanceof TeiidColumnInfo && newValue instanceof Boolean) {
				TeiidColumnInfo info = (TeiidColumnInfo)element;
				if(info.getOrdinality() ) {
					fileInfo.setOrdinality(info, false);
					handleInfoChanged(false);
				} else {
					fileInfo.setOrdinality(info, true);
					handleInfoChanged(false);
				}
			}
		}
    	
    }
}

