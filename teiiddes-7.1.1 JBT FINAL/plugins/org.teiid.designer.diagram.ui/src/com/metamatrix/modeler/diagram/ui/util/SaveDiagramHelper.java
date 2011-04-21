/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.ui.IDiagramTypeEditPart;

/**
 * @since 5.0
 */
public class SaveDiagramHelper implements DiagramUiConstants {

    private static final String FILE_DIALOG_TITLE = "SaveDiagramDialog.title"; //$NON-NLS-1$
    private static final long MAX_SAVEABLE_DIAGRAM_SIZE = 9000L * 9000L;
    private static final int COLOR_DEPTH = 16; // 16 is plenty for our modest pallette of colors

    /**
     * @since 5.0
     */
    public SaveDiagramHelper() {
        super();
    }

    public void saveDiagramToFile( final DiagramEditor editor ) {
        // System.out.println("\n\n[SaveDiagramHelper.saveDiagramToFile] TOP" );

        if (editor != null) {

            // testImageCreateSizes();

            ImageLoader imageLoader = null;
            GraphicalViewer viewer = editor.getDiagramViewer();
            List lstEditParts = viewer.getSelectedEditParts();

            if (lstEditParts.size() > 0 && selectionIsValid(lstEditParts)) {
                imageLoader = createImageLoaderForSelection(editor, lstEditParts);
            } else {
                imageLoader = createImageLoaderForFullDiagram(editor);
            }

            if (imageLoader == null) {
                return;
            }

            // One more time, free all the memory and handles you can...
            System.gc();

            saveThisImageFile(imageLoader);
        }
    }

    private boolean checkDiagramSize( Rectangle rect ) {

        // check for excessively large diagram regions
        long lRectangleSize = rect.width * rect.height;
        // System.out.println("[SaveDiagramHelper.checkDiagramSize] About to test rectangle, size is: " + lRectangleSize );

        if (lRectangleSize > MAX_SAVEABLE_DIAGRAM_SIZE) {
            // System.out.println("[SaveDiagramHelper.checkDiagramSize] Rectangle TOO LARGE TO SAVE: " + lRectangleSize );
            String sMessage = Util.getString("SaveDiagramWarningDialog1.message", MAX_SAVEABLE_DIAGRAM_SIZE) //$NON-NLS-1$
                              + Util.getString("SaveDiagramWarningDialog2.message", lRectangleSize); //$NON-NLS-1$
            MessageDialog.openWarning(getShell(), Util.getString("SaveDiagramWarningDialog.title"), sMessage); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    protected Shell getShell() {
        return DiagramUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

    private boolean selectionIsValid( List lstEditParts ) {
        /*
         * By default, the diagram edit part itself is selected when the diagram is first presented.
         * A selection is only meaningful for our purpose if it is a classifier.
         */
        if (lstEditParts.size() == 1) {
            Object oEditPart = lstEditParts.iterator().next();

            if (oEditPart instanceof IDiagramTypeEditPart) {
                return false;
            }
        }
        return true;
    }

    private void saveThisImageFile( ImageLoader imageLoader ) {

        /*
            I tried all of the following:
                    SWT.IMAGE_BMP;
                    SWT.IMAGE_GIF;
                    SWT.IMAGE_JPEG;
                    SWT.IMAGE_PNG;
                    
            GIF and PNG threw exceptions, so they are not supported.
            bmp and jpg are supported, so I added bmp to the extensions filter.            

        */

        // create coordinated arrays for extension literals and save constants
        String[] aryDisplayExtensions = new String[] {"*.jpg", "*.bmp"}; //$NON-NLS-1$ //$NON-NLS-2$

        String[] aryExactExtensions = new String[] {".jpg", ".bmp"}; //$NON-NLS-1$ //$NON-NLS-2$ 

        int[] aryConstants = new int[] {SWT.IMAGE_JPEG, SWT.IMAGE_BMP};

        // Display file dialog for user to choose libraries
        Shell shell = DiagramUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        final FileDialog dlg = new FileDialog(shell, SWT.SAVE | SWT.SINGLE);
        dlg.setText(DiagramUiConstants.Util.getString(FILE_DIALOG_TITLE));
        dlg.setFilterExtensions(aryDisplayExtensions);

        // present the dialog
        final String file = dlg.open();

        if (file != null) {
            String finalFileName = file;

            int iSelectedExtension = -1;
            for (int i = 0; i < aryExactExtensions.length; i++) {
                if (file.endsWith(aryExactExtensions[i])) {
                    iSelectedExtension = i;
                    break;
                }
            }

            // default to jpg
            int iExtensionType = SWT.IMAGE_JPEG;

            if (iSelectedExtension != -1) {
                iExtensionType = aryConstants[iSelectedExtension];
            }

            // do the save
            final String sFileName = finalFileName;
            final int iSelectedExtensionType = iExtensionType;
            final ImageLoader finalImageLoader = imageLoader;
            final String sProgressMessage = Util.getString("SaveDiagramDialog.progressMessage"); //$NON-NLS-1$

            if (finalImageLoader != null) {
                // final String sProgressMessage = Util.getString( "SaveDiagramDialog.progressMessage" );

                final IRunnableWithProgress op = new IRunnableWithProgress() {
                    public void run( final IProgressMonitor theMonitor ) {
                        theMonitor.beginTask(sProgressMessage, 100);
                        theMonitor.worked(50);

                        // first free all the memory and handles you can...
                        System.gc();

                        // System.out.println("[SaveDiagramHelper.saveDiagramToFile] About to save...imageLoader.logicalScreenWidth / imageLoader.logicalScreenHeight "
                        // + finalImageLoader.logicalScreenWidth + " / " + finalImageLoader.logicalScreenHeight );
                        finalImageLoader.save(sFileName, iSelectedExtensionType);

                        theMonitor.done();
                    }
                };
                try {
                    new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true, op);
                } catch (InterruptedException e) {
                } catch (InvocationTargetException e) {
                    DiagramUiConstants.Util.log(e.getTargetException());
                } catch (Exception e) {
                    DiagramUiConstants.Util.log(e.getMessage());
                }
            }
        }
    }

    private ImageLoader createImageLoaderForSelection( DiagramEditor editor,
                                                       List lstEditParts ) {

        ImageLoader imageLoader = null;
        GraphicalViewer viewer = editor.getDiagramViewer();

        Rectangle rectSelection = createRectangleForSelectedEditParts(lstEditParts);
        // System.out.println("[SaveDiagramHelper.createImageLoaderForSelection] Selection Rectangle: " + rectSelection );

        LayerManager lm = (LayerManager)viewer.getEditPartRegistry().get(LayerManager.ID);
        IFigure f = lm.getLayer(LayerConstants.PRINTABLE_LAYERS);
        Rectangle rectFullDiagram = f.getBounds();
        Display display = Display.getDefault();

        // === 1. Create full sized startingImage
        Image startingImage = null;
        ImageData scaledIData = null;

        // first free all the memory and handles you can...
        System.gc();

        // Fresh idea: Let's try making the dim of startImage JUST LARGE ENOUGH
        // to start at 0, 0 and include the selection:
        int iStartDiagramWidth = rectSelection.x + rectSelection.width;
        int iStartDiagramHeight = rectSelection.y + rectSelection.height;

        Rectangle rectSmallestRect = new Rectangle(0, 0, iStartDiagramWidth, iStartDiagramHeight);
        // System.out.println("[SaveDiagramHelper.createImageLoaderForSelection] rectSmallestRect: (used to scaledTo) " +
        // rectSmallestRect );

        // check for oversize diagram and bail out
        boolean bContinue = checkDiagramSize(rectSmallestRect);

        if (!bContinue) {
            return imageLoader;
        }

        try {

            // the 'scaleTo' call is the workaround for Windows 16M image size limit problem
            startingImage = new Image(display, 10, 10);

            ImageData imageData = new ImageData(10, 10, COLOR_DEPTH, new PaletteData(0xFF, 0xFF00, 0xFF0000));
            scaledIData = imageData.scaledTo(iStartDiagramWidth, iStartDiagramHeight);

            startingImage = new Image(display, scaledIData);
        } catch (Exception e) {
            DiagramUiConstants.Util.log(e.getMessage());

        }

        // System.out.println("[SveDiagramHelper.createImageLoaderForSelection] startingImage.getImageData().depth = " +
        // startingImage.getImageData().depth );
        // System.out.println("[SveDiagramHelper.createImageLoaderForSelection] startingImage.getImageData().width = " +
        // startingImage.getImageData().width );
        // System.out.println("[SveDiagramHelper.createImageLoaderForSelection] startingImage.getImageData().height = " +
        // startingImage.getImageData().height );
        //
        // long lFullSize = startingImage.getImageData().depth
        // * startingImage.getImageData().width
        // * startingImage.getImageData().height;
        // System.out.println("[SveDiagramHelper.createImageLoaderForSelection] iFullSize = " + lFullSize );

        // === 2. do copyArea to copy the selected portion of the workImage
        // === to (0, 0) of the startingImage
        GC gcStarting = new GC(startingImage);
        SWTGraphics graphics = new SWTGraphics(gcStarting);

        // System.out.println("[SaveDiagramHelper.createImageLoaderForSelection] About to call f.paint(graphics)" );
        f.paint(graphics);

        // defeat any existing clipping by setting clipping on this GC to the full size of the diagram
        gcStarting.setClipping(rectFullDiagram.x, rectFullDiagram.y, rectFullDiagram.width, rectFullDiagram.height);

        // now copy the selection rectangle to the upper left corner of the image
        gcStarting.copyArea(rectSelection.x, rectSelection.y, rectSelection.width, rectSelection.height, 0, 0);

        // create an empty image that is the size of just the part we want
        Image finalImage = new Image(display, rectSelection.width, rectSelection.height);

        // Use drawImage to copy just what we want into the finalImage
        GC gcFinal = new GC(finalImage);
        gcFinal.drawImage(startingImage,
                          0,
                          0,
                          rectSelection.width,
                          rectSelection.height,
                          0,
                          0,
                          rectSelection.width,
                          rectSelection.height);

        // === 3. create imageLoader from the finalImage
        imageLoader = new ImageLoader();

        imageLoader.logicalScreenWidth = rectSelection.width;
        imageLoader.logicalScreenHeight = rectSelection.height;
        imageLoader.data = new ImageData[] {finalImage.getImageData()};

        // clean up
        gcStarting.dispose();
        gcFinal.dispose();
        graphics.dispose();
        startingImage.dispose();
        finalImage.dispose();

        // System.out.println("[SaveDiagramHelper.createImageLoaderForSelection] About to save...imageLoader.logicalScreenWidth / imageLoader.logicalScreenHeight "
        // + imageLoader.logicalScreenWidth + " / " + imageLoader.logicalScreenHeight );
        return imageLoader;
    }

    private Rectangle createRectangleForSelectedEditParts( List lstEditParts ) {

        /*
         * Calculate the minimum rectangle that contains the selected edit parts
         * 1. Walk the edit parts seeking:
         *    a) The smallest X
         *    b) The largest (x + its width)
         *    c) The smallest Y
         *    d) The largest (y + its height)
         */
        int iSmallestX = -1;
        int iLargestXPlusWidth = 0;
        int iSmallestY = -1;
        int iLargestYPlusHeight = 0;

        int iX = 0;
        int iWidth = 0;
        int iXPlusWidth = 0;
        int iY = 0;
        int iHeight = 0;
        int iYPlusHeight = 0;

        Iterator it = lstEditParts.iterator();

        while (it.hasNext()) {
            EditPart ep = (EditPart)it.next();

            if (ep instanceof DiagramEditPart) {

                DiagramEditPart dep = (DiagramEditPart)ep;
                DiagramModelNode dmNode = (DiagramModelNode)dep.getModel();

                iX = dmNode.getX();
                iWidth = dmNode.getWidth();
                iXPlusWidth = iX + iWidth;
                iY = dmNode.getY();
                iHeight = dmNode.getHeight();
                iYPlusHeight = iY + iHeight;

                // if new values are better, swap them in
                if (iSmallestX == -1 || iX < iSmallestX) {
                    iSmallestX = iX;
                }
                if (iXPlusWidth > iLargestXPlusWidth) {
                    iLargestXPlusWidth = iXPlusWidth;
                }
                if (iSmallestY == -1 || iY < iSmallestY) {
                    iSmallestY = iY;
                }
                if (iYPlusHeight > iLargestYPlusHeight) {
                    iLargestYPlusHeight = iYPlusHeight;
                }
            }
        }

        int iFullWidth = iLargestXPlusWidth - iSmallestX;
        int iFullHeight = iLargestYPlusHeight - iSmallestY;

        // create the rectangle that contains the selected edit parts
        Rectangle rect = new Rectangle(iSmallestX, iSmallestY, iFullWidth, iFullHeight);

        // System.out.println("[SaveDiagramHelper.createRectangleForSelectedEditParts] Rectangle: " + rect );
        return rect;
    }

    private ImageLoader createImageLoaderForFullDiagram( DiagramEditor editor ) {

        ImageLoader imageLoader = null;
        Image finalImage = null;
        GraphicalViewer viewer = editor.getDiagramViewer();

        LayerManager lm = (LayerManager)viewer.getEditPartRegistry().get(LayerManager.ID);
        IFigure f = lm.getLayer(LayerConstants.PRINTABLE_LAYERS);

        Rectangle rectFullDiagram = f.getBounds();

        // check for oversize diagram and bail out
        boolean bContinue = checkDiagramSize(rectFullDiagram);

        if (!bContinue) {
            return imageLoader;
        }

        Display display = Display.getDefault();

        // first free all the memory and handles you can...
        System.gc();

        // the 'scaleTo' call is the workaround for Windows 16M image size limit problem
        ImageData imageData = new ImageData(10, 10, COLOR_DEPTH, new PaletteData(0xFF, 0xFF00, 0xFF0000));
        ImageData scaledIData = imageData.scaledTo(rectFullDiagram.width, rectFullDiagram.height);

        finalImage = new Image(display, scaledIData);

        // System.out.println("[SveDiagramHelper.createImageLoaderForFullDiagram] startingImage.getImageData().depth = " +
        // finalImage.getImageData().depth );
        // System.out.println("[SveDiagramHelper.createImageLoaderForFullDiagram] startingImage.getImageData().width = " +
        // finalImage.getImageData().width );
        // System.out.println("[SveDiagramHelper.createImageLoaderForFullDiagram] startingImage.getImageData().height = " +
        // finalImage.getImageData().height );
        //
        // long lFullSize = finalImage.getImageData().depth
        // * finalImage.getImageData().width
        // * finalImage.getImageData().height;
        // System.out.println("[SveDiagramHelper.createImageLoaderForFullDiagram] iFullSize = " + lFullSize );

        GC graphicContext = new GC(finalImage);
        SWTGraphics graphics = new SWTGraphics(graphicContext);

        // System.out.println("[SveDiagramHelper.createImageLoaderForFullDiagram]...About to call f.paint()  " );
        f.paint(graphics);

        imageLoader = new ImageLoader();

        imageLoader.data = new ImageData[] {finalImage.getImageData()};
        imageLoader.logicalScreenHeight = rectFullDiagram.height;
        imageLoader.logicalScreenWidth = rectFullDiagram.width;

        // clean up
        graphicContext.dispose();
        graphics.dispose();
        finalImage.dispose();

        return imageLoader;
    }

    /*
     * jh Case 4514 test
     *   This method can be used to find the largest Image that can be created before
     *   the  OS returns 'NO HANDLE'.
     */

    // public void testImageCreateSizes() {
    //        
    // int TRIES = 100;
    // Display display = Display.getDefault();
    //        
    // Image startingImage = null;
    // ImageData scaledIData = null;
    // Image finalImage = null;
    //
    // int iStartWidth = 9000;
    // int iStartHeight = 9000;
    // int iWidth;
    // int iHeight;
    // int iFactor;
    //
    // for ( int i = 0; i < TRIES; i++ ) {
    //            
    // if ( startingImage != null ) {
    // startingImage.dispose();
    // }
    //            
    // if ( finalImage != null ) {
    // finalImage.dispose();
    // }
    //
    // startingImage = null;
    // scaledIData = null;
    // finalImage = null;
    //            
    // System.gc();
    //
    // iFactor = (i + 1) * 10;
    // iWidth = iStartWidth + iFactor;
    // iHeight = iStartHeight + iFactor;
    //
    // System.out.println( "\n[SaveDiagramHelper.testImageCreateSizes] About to try... " );
    // System.out.println( "\n[SaveDiagramHelper.testImageCreateSizes] iWidth: " + iWidth );
    // System.out.println( "[SaveDiagramHelper.testImageCreateSizes] iHeight: " + iHeight );
    //            
    // // the 'scaleTo' call is the workaround for Windows 16M image size limit problem
    // startingImage = new Image( display, 10, 10 );
    // scaledIData
    // = startingImage.getImageData().scaledTo( iWidth, iHeight );
    //            
    // finalImage = new Image( display, scaledIData );
    // System.out.println( "[SaveDiagramHelper.testImageCreateSizes] SUCCESS! " );
    // }
    // }

}
