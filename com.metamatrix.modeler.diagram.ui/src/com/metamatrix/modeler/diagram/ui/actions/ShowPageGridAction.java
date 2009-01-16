/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.gef.RootEditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.CustomScalableFreeformRootEditPart;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.printing.DiagramPrintingAnalyzer;

/**
 * DiagramPageSetupAction
 */
public class ShowPageGridAction extends DiagramAction 
                             implements DiagramUiConstants {
    

    private static final String NATIVE_PRINT_DIALOG_WARNING_DIALOG_TITLE 
        = Util.getString("com.metamatrix.modeler.diagram.ui.actions.ShowPageGridAction.nativePrintDialogWarningDialog.title"); //$NON-NLS-1$
    private static final String NATIVE_PRINT_DIALOG_WARNING_DIALOG_TEXT 
        = Util.getString("com.metamatrix.modeler.diagram.ui.actions.ShowPageGridAction.nativePrintDialogWarningDialog.text"); //$NON-NLS-1$

    
    private static final String PRINT_DIALOG_TEXT 
        = Util.getString("com.metamatrix.modeler.diagram.ui.actions.ShowPageGridAction.printDialog.text"); //$NON-NLS-1$
    
    private static final String SHOW_PRINT_GRID_TEXT 
        = Util.getString("com.metamatrix.modeler.diagram.ui.actions.ShowPageGridAction.show.text"); //$NON-NLS-1$
    private static final String HIDE_PRINT_GRID_TEXT 
        = Util.getString("com.metamatrix.modeler.diagram.ui.actions.ShowPageGridAction.hide.text"); //$NON-NLS-1$
    
    private static final String SHOW_PRINT_GRID_TOOLTIP 
        = Util.getString("com.metamatrix.modeler.diagram.ui.actions.ShowPageGridAction.show.toolTip"); //$NON-NLS-1$
    private static final String HIDE_PRINT_GRID_TOOLTIP 
        = Util.getString("com.metamatrix.modeler.diagram.ui.actions.ShowPageGridAction.hide.toolTip"); //$NON-NLS-1$


    ImageDescriptor SHOW_PAGE_GRID 
        = DiagramUiPlugin.getDefault().getImageDescriptor( DiagramUiConstants.Images.SHOW_PAGE_GRID );
    
    ImageDescriptor HIDE_PAGE_GRID 
        = DiagramUiPlugin.getDefault().getImageDescriptor( DiagramUiConstants.Images.HIDE_PAGE_GRID );
    
    private DiagramEditor editor;
    private boolean bShowGrid = false;

    /**
     * Construct an instance of SaveDiagramAction.
     * 
     */
    public ShowPageGridAction(DiagramEditor editor) {
        super();
//        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.SAVE_DIAGRAM));
        this.editor = editor;
        refreshImage();
        setText( SHOW_PRINT_GRID_TEXT );
        setToolTipText( SHOW_PRINT_GRID_TOOLTIP );
    }

    /**
     * Construct an instance of SaveDiagramAction.
     * @param theStyle
     */
    public ShowPageGridAction(int theStyle) {
        super(theStyle);
        setImageDescriptor( SHOW_PAGE_GRID );
        setText( SHOW_PRINT_GRID_TEXT );
        setToolTipText( SHOW_PRINT_GRID_TOOLTIP );
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);

        // do we care about selection? Don't we always enable this action?
        setEnabled(true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        // get the active diagram editor and through it get to the PageBoundaryGridLayer
        // get the PrintSettings preferences (is it all static so it can stand alone?)
        // create a DiagramPrintingAnalyzer with the settings and a temporary printer object??
        // get the dimensions (w * h) from the analyzer
        // set the dimensions here and repaint
        // flip this action's icon to the 'off' icon (if it was 'on', else flip to 'on')
        
        
        // Get current DiagramEditor
        if( editor != null ) {

            RootEditPart rep = editor.getDiagramViewer().getRootEditPart();
            
            if ( rep instanceof CustomScalableFreeformRootEditPart ) {
                // 1. if we do not have printer data, throw up the Print Dialog so user
                //      can select the printer; then we'll have it to use to calculate the
                //      print page grid.
                if ( DiagramPrintingAnalyzer.getPrinterData() == null ) {
        
                    Shell shell = editor.getDiagramViewer().getControl().getShell();
                    
                    // explain to the user what we are going to 
                    MessageDialog.openInformation( shell, 
                                                   NATIVE_PRINT_DIALOG_WARNING_DIALOG_TITLE,
                                                   NATIVE_PRINT_DIALOG_WARNING_DIALOG_TEXT );
                    
                    PrintDialog dialog = new PrintDialog( shell, SWT.NULL );
                    dialog.setText( PRINT_DIALOG_TEXT );
                    PrinterData data = dialog.open();                    
                    
                    if (data != null) {
                        // save printerdata with the analyzer
                        DiagramPrintingAnalyzer.setPrinterData( data );
                        
                        // refresh the grid
                        editor.getDiagramViewer().updateForPrintPreferences();
                    }
                }
            
                // 2. flip the visibility of the grid 
                CustomScalableFreeformRootEditPart repPageGrid
                    = (CustomScalableFreeformRootEditPart)rep;
                
                // flip the visible state boolean
                bShowGrid = !bShowGrid;
                
                // apply it to the page grid layer
                repPageGrid.getPageGridLayer().setVisible( bShowGrid );
                
                // flip the toolbar button image
                refreshImage();
            }
        }
    }
    
    private void refreshImage() {
        if ( bShowGrid ) {
            setImageDescriptor( HIDE_PAGE_GRID );
            setText( HIDE_PRINT_GRID_TEXT );
            setToolTipText( HIDE_PRINT_GRID_TOOLTIP );
        } else {
            setImageDescriptor( SHOW_PAGE_GRID );            
            setText( SHOW_PRINT_GRID_TEXT );
            setToolTipText( SHOW_PRINT_GRID_TOOLTIP );
        }
    }

}
