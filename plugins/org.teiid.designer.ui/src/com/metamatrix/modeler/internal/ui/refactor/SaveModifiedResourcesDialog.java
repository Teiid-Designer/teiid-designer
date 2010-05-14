/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.SelectionDialog;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 * SaveModifiedResourcesDialog is a dialog that displays any dirty resources and 
 *  requests permission to close them
 *    
      sample of the dialog code:
     private void handleBrowseTypeButtonPressed_TestOfFileFolderMoveDialog() {
              
        
        // ========================================
        // launch Refactor Command Processor Dialog 
        // ========================================
        
        RefactorCommandProcessorDialog ffmdDialog 
            = new RefactorCommandProcessorDialog( UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell() );
 
        ffmdDialog.setAllowMultiple( false );
        ffmdDialog.setTitle( "Move this thing" );
        ffmdDialog.setMessage( "Select the move destination:" );
        ffmdDialog.setValidator( new RelationshipTypeSelectionValidator() );
        ffmdDialog.setResource( xxx);
        ffmdDialog.setCommand( xxx );
        
        ffmdDialog.open();
                
        if ( ffmdDialog.getReturnCode() == FileFolderMoveDialog.OK ) {
            Object[] oSelectedObjects = ffmdDialog.getResult();             
            ...
        }                        
    } 
      
    
 */
public class SaveModifiedResourcesDialog extends SelectionDialog {

    private static final String TITLE 
        = UiConstants.Util.getString("SaveModifiedResourcesDialog.dialogTitle.text"); //$NON-NLS-1$
    private static final String HEADER_MESSAGE 
        = UiConstants.Util.getString("SaveModifiedResourcesDialog.headerMessage.text"); //$NON-NLS-1$

    private static final String SAVE_ERROR_TITLE 
        = UiConstants.Util.getString("SaveModifiedResourcesDialog.saveError.title"); //$NON-NLS-1$
    private static final String SAVE_ERROR_MESSAGE 
        = UiConstants.Util.getString("SaveModifiedResourcesDialog.saveError.message"); //$NON-NLS-1$

    private Collection resourcesToIgnore;
    private Collection colResourcesToDisplay;  
    
    protected IBaseLabelProvider labelProvider;
    protected IContentProvider contentProvider;
    
    private SaveModifiedResourcesTablePanel pnlSaveModifiedResourcesTable;    
        
    /**
     * Construct an instance of SaveModifiedResourcesDialog.  
     * @param propertiedObject the EObject to display in this 
     * @param parent the shell
     * 
     * 
     */
    public SaveModifiedResourcesDialog( Shell parent ) {
        super( parent );
                
        setTitle( TITLE );       
//        System.out.println("[SaveModifiedResourcesDialog.ctor] HEADER_MESSAGE is: " + HEADER_MESSAGE ); //$NON-NLS-1$
        setMessage( HEADER_MESSAGE );              
    }

    /**
     * Construct an instance of SaveModifiedResourcesDialog.  This constructor also takes an array of IResource objects to ignore
     * 
     * @param parent the shell
     * @param resourcesToIgnore resources to skip in the save process
     * 
     */
    public SaveModifiedResourcesDialog( Shell parent, Collection resourcesToIgnore ) {
        super( parent );
        this.resourcesToIgnore = resourcesToIgnore;
                        
        setTitle( TITLE );       
//        System.out.println("[SaveModifiedResourcesDialog.ctor] HEADER_MESSAGE is: " + HEADER_MESSAGE ); //$NON-NLS-1$
        setMessage( HEADER_MESSAGE );              
    }   
         
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite container) {
        
        // create parent
        Composite parent = (Composite) super.createDialogArea(container);

        GridLayout gridLayout = new GridLayout();
        parent.setLayout( gridLayout );
        gridLayout.numColumns = 1;

        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.heightHint = 400;
        gd.widthHint = 400;
        parent.setLayoutData( gd );
        
        // establish the message
        createMessageArea( parent );

        // create the table
        pnlSaveModifiedResourcesTable = new SaveModifiedResourcesTablePanel( parent ); 
        pnlSaveModifiedResourcesTable.refresh();
        
        return parent;
    }

    /* (non-Javadoc)
     * Overridden to make the shell resizable.
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        setShellStyle(getShellStyle() | SWT.RESIZE);
        super.create();
    }

    @Override
    protected void okPressed() {

        // close the editors for the displayed resources
        Iterator it = getResourcesToDisplay().iterator();
        
        // collect the files we cannot close (because they are not ModelResources, usually))
        ArrayList arylUnsavedFiles = new ArrayList();
        
        while( it.hasNext() ) {
            IResource resTemp = (IResource)it.next();
            
            if ( resTemp instanceof IFile ) {                        
                boolean bDone = ModelEditorManager.save( (IFile)resTemp );   
                if ( !bDone ) {
                    arylUnsavedFiles.add( resTemp );                    
                }
            }
        }
        
        if ( !arylUnsavedFiles.isEmpty() ) {
            String sList = arylUnsavedFiles.toString();
    
            // report the problem to the user
            MessageDialog.openError( this.getParentShell(), SAVE_ERROR_TITLE, SAVE_ERROR_MESSAGE + sList ); 
        }
        
        // let nature take its course        
        super.okPressed();        
    }


    public Collection getResourcesToDisplay() {
        
        if ( colResourcesToDisplay == null ) {
            
            Collection colDirtyFiles = ModelEditorManager.getDirtyResources();
            colResourcesToDisplay = new ArrayList();
            
            if ( resourcesToIgnore != null 
              && !resourcesToIgnore.isEmpty()
              && colDirtyFiles != null
              && !colDirtyFiles.isEmpty() ) {
    
                /*
                 * if we have both a set of dirty files, and a set of resources to ignore,
                 *  we need to remove the 'ignores' from the set of dirty files.
                 */
                Iterator itDirty = colDirtyFiles.iterator();                
                Object[] ignores = resourcesToIgnore.toArray();
                
                while( itDirty.hasNext() ) {
                    IFile dirtyFile = (IFile)itDirty.next();
                    
                    if ( dirtyFile.getType() == IResource.FILE ) {
                        for ( int i = 0; i < ignores.length; i++ ) {
                            ModelResource mrIgnore = (ModelResource)ignores[ i ];
                            ModelResource mrDirtyFile = null;
                            try {
                            
                                mrDirtyFile = ModelUtilities.getModelResource( dirtyFile, false );
                            } catch ( ModelWorkspaceException mwe ) {
                                ModelerCore.Util.log(IStatus.ERROR, mwe, mwe.getMessage());                                                                                  
                            }
                            
                            if ( mrDirtyFile == mrIgnore ) {
                                
                                // remove the current one from dirty set, if match
                                itDirty.remove();  
                            }
                        }
                    }
                    
                }
            }
    
            colResourcesToDisplay = colDirtyFiles;        
        }
                               
        return colResourcesToDisplay;
    }
    
    protected IContentProvider getContentProvider() {
        return this.contentProvider;
    }
    
    protected void setContentProvider(IContentProvider theContentProvider) {
        this.contentProvider = theContentProvider;
    }

    protected IBaseLabelProvider getLabelProvider() {
        return this.labelProvider;
    }
    
    protected void setLabelProvider(IBaseLabelProvider theLabelProvider) {
        this.labelProvider = theLabelProvider;
    }

    
    // =========================
    //  inner classes
    // =========================
    

        // =========================================
        // Inner class: SaveModifiedResources Table Panel
        // =========================================

    class SaveModifiedResourcesTablePanel extends Composite {
        // ===================================================
        //    UI Components (indentation shows structure)
        // ===================================================
            
        private Composite pnlTableStuff;
                
            private Table tblSaveModifiedResourcesTable;
                private String[] columnNames = new String[] {
                        ""                           //$NON-NLS-1$
                        };     
                 
            private TableViewer tvSaveModifiedTableViewer; 
    
        // ===================================================
        //    Constructor
        // ===================================================

        public SaveModifiedResourcesTablePanel( Composite parent ) {
            super( parent, SWT.NONE ); 
//            this.parent         = parent;

            createControl( this );
        }

        public void refresh() {
            tvSaveModifiedTableViewer.refresh();
        }
    
        /* (non-Javadoc)
         * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
         */
        public void createControl( Composite parent ) {
                     
            // 0. Set layout for the SashForm                     
            GridLayout gridLayout = new GridLayout();
            this.setLayout(gridLayout);
            gridLayout.numColumns = 1;
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridLayout.marginWidth = gridLayout.marginHeight = 0;

            this.setLayoutData(gridData);         

            //  2. Create the table
            createTableStuffPanel( parent );        
            
        }        


        private void createTableStuffPanel( Composite parent ) {
        
            pnlTableStuff = new Composite( parent, SWT.NONE );
                        
            GridLayout gridLayout = new GridLayout();
            gridLayout.marginWidth = gridLayout.marginHeight = 0;
            pnlTableStuff.setLayout( gridLayout );
            GridData gridData = new GridData( GridData.FILL_BOTH );
            pnlTableStuff.setLayoutData( gridData );
                
            // 1. Create the table
            createTableViewerPanel( pnlTableStuff );
            
        }
       
          /*
           * Create the TableViewerPanel 
           */
        private void createTableViewerPanel( Composite parent ) {
              // Create the table 
            createTable( parent );

              // Create and setup the TableViewer
            createTableViewer();
            
            if (getContentProvider() == null) {
                setContentProvider(new SaveModifiedResourcesTableContentProvider());
            }
            
            if (getLabelProvider() == null) {
                setLabelProvider(new SaveModifiedResourcesTableLabelProvider());
            }

            tvSaveModifiedTableViewer.setContentProvider(getContentProvider());
            tvSaveModifiedTableViewer.setLabelProvider(getLabelProvider());
            
            tvSaveModifiedTableViewer.setInput( new Object() );                     
        }


        /**
         * Create the Table
         */
        private void createTable( Composite parent ) {
            int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION ;                        

            tblSaveModifiedResourcesTable = new Table(parent, style);
            TableLayout layout = new TableLayout();
            tblSaveModifiedResourcesTable.setLayout(layout);
    
            GridData gridData = new GridData(GridData.FILL_BOTH);
            tblSaveModifiedResourcesTable.setLayoutData(gridData);      
                
            tblSaveModifiedResourcesTable.setLinesVisible(true);
            tblSaveModifiedResourcesTable.setHeaderVisible(true);

            // 1st column 
            TableColumn column1 = new TableColumn(tblSaveModifiedResourcesTable, SWT.LEFT, 0);     
            column1.setText( columnNames[0] );
            ColumnWeightData weight = new ColumnWeightData(1);
            layout.addColumnData(weight);

        }
     
     
         /**
          * Create the TableViewer
          */
          private void createTableViewer() {
          
            tvSaveModifiedTableViewer = new TableViewer(tblSaveModifiedResourcesTable);
            tvSaveModifiedTableViewer.setUseHashlookup(true);

            tvSaveModifiedTableViewer.setColumnProperties(columnNames);

            // Create the cell editors
            CellEditor[] editors = new CellEditor[columnNames.length];

            // Column 1 : Attribute not editable
            editors[0] = null;

            // Assign the cell editors to the viewer
            tvSaveModifiedTableViewer.setCellEditors(editors);
        }
    } 
    
               

        // =========================================
        // Inner class: SaveModifiedResourcesTableRow
        // =========================================
    
        class SaveModifiedResourcesTableRow {

            // ===================
            // Fields
            // ===================
        
            private Object oObject;            
//            private String EMPTY_STRING = "";  //$NON-NLS-1$
        
            // ===================
            // Constructors
            // ===================
        
            public SaveModifiedResourcesTableRow( Object oObject ) {
                                 
                this.oObject        = oObject;
            }

            // ===================
            // Methods
            // ===================                         
        
            public Object getObject() {
                return oObject;
            }
            
            public String getColumnText( int iColumnIndex ) {
                String sResult = "<unknown>";  //$NON-NLS-1$
                IResource resource;

                if ( oObject instanceof IResource ) {        
                    resource = (IResource)oObject;
                    
                    switch( iColumnIndex ) {
                        case 0:

                            sResult = resource.getFullPath().makeRelative().toString();                                                                         
                            break;    
                    }
                }
                
                return sResult;
            }

            public Object getValue( int theIndex ) {
//                String result = "unknown"; //$NON-NLS-1$
                Object oResult = null;                
            
                return oResult;
            }
        }


        // =========================================
        // Inner class: SaveModifiedResourcesTableContentProvider
        // =========================================
    
        protected class SaveModifiedResourcesTableContentProvider implements IStructuredContentProvider {
            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            public void dispose() {
            }

            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            public Object[] getElements( Object theInputElement ) {
                Object[] result = null;
                Collection colDirtyFiles = getResourcesToDisplay();
           
                if ( ( colDirtyFiles != null) && !colDirtyFiles.isEmpty()) {
                    int numRows = colDirtyFiles.size();
                    result = new Object[ numRows ];
                    Iterator it = colDirtyFiles.iterator();
                    
                    for (int i = 0; i < numRows; i++) {
                        Object oObject = it.next();
                        result[i] = new SaveModifiedResourcesTableRow( oObject );                             
                    }
                }
            
                return ( ( colDirtyFiles == null ) || colDirtyFiles.isEmpty() ) ? new Object[0]
                                                                                : result;
            }

            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
             */
            public void inputChanged(Viewer theViewer, Object theOldInput, Object theNewInput) {
                if (theNewInput != null) {
                    theViewer.refresh();
                }
            }

        }

        // ======================================================
        // Inner class: SaveModifiedResourcesTableLabelProvider
        // ======================================================
     
        protected class SaveModifiedResourcesTableLabelProvider extends LabelProvider implements ITableLabelProvider {
            public Image getColumnImage( Object theElement,
                                         int iColumnIndex ) {
                return null;                
            }
 
            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
             */
            public String getColumnText(Object theElement,
                                        int iColumnIndex) {
                SaveModifiedResourcesTableRow row = (SaveModifiedResourcesTableRow)theElement;
                return row.getColumnText( iColumnIndex );
            }

        }



}
