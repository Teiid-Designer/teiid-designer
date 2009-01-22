/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.printing.DiagramPrintSummaryDialog;
import com.metamatrix.modeler.diagram.ui.printing.DiagramPrintingAnalyzer;
import com.metamatrix.modeler.diagram.ui.printing.DiagramPrintingOperation;
import com.metamatrix.modeler.diagram.ui.printing.PrintSettings;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.ui.print.IPrintable;



/**
 * PrintWrapper
 */
public class PrintWrapper extends DiagramAction
                       implements DiagramUiConstants,
                                  IDiagramActionConstants,
                                  IPartListener,
                                  ActionWrapper {

    /**
     * Construct an instance of PrintWrapper.
     * 
     */
    public PrintWrapper() {
        super();
        initialize();
    }

    /**
     * Construct an instance of PrintWrapper.
     * @param theStyle
     */
    public PrintWrapper(int theStyle) {
        super(theStyle);
        initialize();
        
    }

    public void initialize() {
        
//        System.out.println("[PrintWrapper.initialize] TOP"); //$NON-NLS-1$
        addAsPartListener();   
        this.setToolTipText( "GEF PrintWrapper");  //$NON-NLS-1$  
     
    }

 
    @Override
    protected void doRun() {
        /*
         * jh: most of this method was reimplemented from the GEF PrintAction class' run() method.
         */

        IEditorPart editor = getActiveEditor();
        
        if ( editor != null ) {
           
            IPrintable printable = (IPrintable)editor.getAdapter( IPrintable.class );

            if ( printable != null ) {

                Object oViewer = printable.getObject();
                
                if ( oViewer != null && oViewer instanceof GraphicalViewer ) {

                    GraphicalViewer viewer = (GraphicalViewer)oViewer;
                    Shell shell = viewer.getControl().getShell();
                    
                    PrintDialog dialog = new PrintDialog( shell, SWT.NULL );
                    PrinterData data = dialog.open();                    
                    
                    
                    if (data != null) {
                        // save printerdata with the analyzer
                        DiagramPrintingAnalyzer.setPrinterData( data );

                        PrintSettings psSettings = new PrintSettings( data );
//                        System.out.println("\n[PrintWrapper.doRun] ... Start..." ); //$NON-NLS-1$
                        
                        DiagramPrintingAnalyzer analyzer = new DiagramPrintingAnalyzer( new Printer(data), viewer ); 
                        DiagramPrintSummaryDialog dlgSummary 
                            = new DiagramPrintSummaryDialog( shell, psSettings, analyzer );
                        
                        int iContinue = dlgSummary.open();
                        
                        if ( iContinue == Window.OK ) {
                            data = dlgSummary.getSettings().getPrinterData();
                            DiagramPrintingOperation op 
                                = new DiagramPrintingOperation( new Printer(data), viewer );
                            
                            op.run( editor.getTitle() );
                        }
                    }   
                    setEnabled( true );                     
                }
            }
        }
//        System.out.println("[PrintWrapper.doRun] ...Done..." ); //$NON-NLS-1$
    }
         
    private void addAsPartListener() {
        getPlugin().getCurrentWorkbenchWindow().getPartService().addPartListener( this );
    }
    
    /**
     *  
     * @see com.metamatrix.ui.actions.AbstractAction#dispose()
     * @since 5.0
     */
    @Override
    public void dispose() {
        getPlugin().getCurrentWorkbenchWindow().getPartService().removePartListener( this );
        super.dispose();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
       
        // jh 11/7: I think we want this new code; but am commenting out to see if that
        //  cures some righteous exceptions we are getting (RunTime on getAction in action services)
        // compare thePart to ModelEditor.getCurrentPage
        
        boolean bResultState = false;
        
        IEditorPart editor = getActiveEditor();
        
        if ( editor != null ) {
           
            if ( thePart instanceof ModelEditor && editor instanceof ModelEditor ) {
                IPrintable printable = (IPrintable)editor.getAdapter( IPrintable.class );
    
                if ( printable != null ) {
                    Object oViewer = printable.getObject();
                    
                    if ( oViewer != null && oViewer instanceof GraphicalViewer ) {
                        bResultState = true;
                    }
                }
            }
        }
//        System.out.println("[PrintWrapper.selectionChanged] About to set enabled state to: " + bResultState ); //$NON-NLS-1$
        setEnabled( bResultState );
    }    
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    public void setEnableState() {

        IEditorPart editor = getActiveEditor();
        
        if ( editor != null ) {             
            setEnabled( true );             
        } else {
            setEnabled( false );                     
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partActivated(IWorkbenchPart part) {
        setEnableState();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     */
    public void partBroughtToTop(IWorkbenchPart part) {
        setEnableState();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     */
    public void partClosed(IWorkbenchPart part) {
        setEnabled( false );
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     */
    public void partDeactivated(IWorkbenchPart part) {
        setEnabled( false );
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     */
    public void partOpened(IWorkbenchPart part) {
        setEnableState();
    }
}


