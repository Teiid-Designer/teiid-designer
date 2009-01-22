/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.layout;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;


/**
 * MmColumnLayout
 */
public class ColumnLayout {
    public static final int ERROR_1 = -1;
    public static final int ERROR_2 = -2;
    public static final int ERROR_3 = -3;
    private LayoutNode[] nodeArray;
    private int startX = 10;
    private int startY = 10;
    private int padding = 20;
    
    public ColumnLayout() {
        super();
    }
    
    public ColumnLayout(List newNodes, int startX, int startY) {
        super();
        this.startX = startX;
        this.startY = startY;

        nodeArray = LayoutUtilities.getLayoutNodeArray(newNodes);
    }
    
    public int run() {
        layoutInColumns();
        return 0;
    }
    
    public void layoutInColumns() {
        
        double totalArea = 0;
        double deltaArea = 0;
        int minNodeWidth = 9999;
        int minNodeHeight = 9999;
        int maxNodeWidth = 0;
        int maxNodeHeight = 0;

        int layoutWidth = 0;
        int layoutHeight = 0;
        int viewPortWidth = 0;
        int viewPortHeight = 0;

        LayoutNode nextNode = null;
        
        int nNodes = nodeArray.length;
      
        int nColumns = 0;
//        boolean hasLinks = false;

        // Go Through Each Component and add Area to total;

        for( int i=0; i<nodeArray.length; i++) {
            LayoutNode next = nodeArray[i];
            maxNodeWidth = Math.max( (int)next.getWidth(), maxNodeWidth );
            maxNodeHeight = Math.max( (int)next.getHeight(), maxNodeHeight );
            minNodeWidth = Math.min( (int)next.getWidth(), minNodeWidth );
            minNodeHeight = Math.min( (int)next.getHeight(), minNodeHeight );
            deltaArea = next.getHeight()*next.getWidth();
            totalArea += deltaArea;
        }


        layoutWidth = (int)(Math.sqrt(totalArea*2.5));
        layoutHeight = (int)(layoutWidth/1.0); //getDiagramView().getZoomFactor());
        layoutWidth = layoutHeight;

        viewPortWidth = 800; //getViewport().getWidth();
        viewPortHeight = 800; //getViewport().getSize().height;

        layoutWidth = Math.max( layoutWidth, viewPortWidth);
        layoutHeight = Math.max( layoutHeight, viewPortHeight);

//        if( hasLinks ) {
//            layoutWidth = (int)(layoutWidth*2.0);
//            layoutHeight = (int)(layoutHeight*.75);
//        }

        nColumns = (int)(layoutWidth/(maxNodeWidth + (double)getPadding()));
        
        if( nColumns < 1 ) {
        	if( nNodes < 2 )
        		return;
        	nColumns = 1;
        }

        //Set up Column array sizes

        int[] xpt = new int[nColumns];
        int[] ypt = new int[nColumns];
        int cid = 0; // ColumnID

        // Initialize column coordinates
        xpt[0] = startX;
        ypt[0] = startY;
        for( int j=1; j<nColumns; j++ ) {
            xpt[j] = xpt[j-1] + maxNodeWidth + getPadding();
            ypt[j] = startY;
        }
        int currentNodeID = 0;

        if( nColumns < 1 ) {

        } else if( nColumns == 1 ) {
            // now Set up a for loop for all components that are of diagram Components.
            // Walk accross the first row to start things off
            for( int i=0; i<nNodes; i++) {
                nextNode = nodeArray[i];

                nextNode.setPosition(new Point(xpt[cid], ypt[cid]) );
                // Reset ypt[] for next component in column.
                ypt[cid] = ypt[cid] + (int)nextNode.getHeight() + getPadding();
            }
        } else {
            currentNodeID = 0;
            // now Set up a for loop for all components that are of diagram Components.
            // Walk accross the first row to start things off
            for( int i=0; i<nNodes; i++) {
                nextNode = nodeArray[i];
                // Make sure that the component is actually a LayoutNode and not of type link
                nextNode.setPosition( new Point(xpt[cid], ypt[cid]) );
                // Reset ypt[] for next component in column.
                ypt[cid] = ypt[cid] + (int)nextNode.getHeight() + getPadding();
                currentNodeID = i;
                if( cid == (nColumns-1) )
                    break;
                cid++;
            }

            // now Set up a for loop for all components that are of diagram Components.

            cid = 0; // reset columnID to first column
            currentNodeID++;
            if( currentNodeID < nNodes ) {
                for( int i=currentNodeID; i<nNodes; i++) {
                    nextNode = nodeArray[i];
                    // Make sure that the component is actually a LayoutNode and not of type link

                    if( cid == 0 ) {
                        // Make sure that first column is always longer than the second.
                        if( ypt[cid] <= (ypt[cid + 1] + 1) ) {
                            nextNode.setPosition(new Point((double)xpt[cid], (double)ypt[cid]) );
                            // Reset ypt[] for next component in column.
                            ypt[cid] = ypt[cid] + (int)nextNode.getHeight() + getPadding();
                        } else {
                            cid++;
                            nextNode.setPosition(new Point((double)xpt[cid], (double)ypt[cid]) );
                            // Reset ypt[] for next component in column.
                            ypt[cid] = ypt[cid] + (int)nextNode.getHeight() + getPadding();
                        }
                    } else if( cid == (nColumns-1) ) {
                        // Make sure that last column is always longer than the previous.
                        // reset column to first column when it is.
                        if( ypt[cid] < (ypt[cid - 1] - 1) ) {
                            nextNode.setPosition(new Point((double)xpt[cid], (double)ypt[cid]) );
                            ypt[cid] = ypt[cid] + (int)nextNode.getHeight() + getPadding();
                        } else {
                            // Need to change to first column and add it there.
                            cid = 0;
                            nextNode.setPosition(new Point((double)xpt[cid], (double)ypt[cid]) );
                            ypt[cid] = ypt[cid] + (int)nextNode.getHeight() + getPadding();
                        }
                    } else {
                        // make sure the next column is always longer than the previous.
                        if( ypt[cid] <= (ypt[cid +1] + 1)  ) {
                            nextNode.setPosition(new Point((double)xpt[cid], (double)ypt[cid]) );
                            ypt[cid] = ypt[cid] + (int)nextNode.getHeight() + getPadding();
                        } else {
                            cid++;
                            nextNode.setPosition(new Point((double)xpt[cid], (double)ypt[cid]) );
                            // Reset ypt[] for next component in column.
                            ypt[cid] = ypt[cid] + (int)nextNode.getHeight() + getPadding();
                        }
                    }

                    if( cid == nColumns)
                        cid = 0;
                }
            }
        }


    }

    private int getPadding() {
        return padding;
    }
    
    public void setPadding(int newPadding) {
        padding = newPadding;
    }
    
    public void setFinalNodePositions() {
        for( int i=0; i<nodeArray.length; i++ ) {
            nodeArray[i].setFinalPosition();
        }
    }
    
    public double getSizeEstimate() {
        double totalArea = 0.0;
        for( int i=0; i<nodeArray.length; i++ ) {
                totalArea += (nodeArray[i].getWidth() * nodeArray[i].getHeight());
        }
        totalArea = 5*totalArea;
        double length = Math.sqrt(totalArea);
        
        return length;
        
    }

    public int getCurrentWidth() {
        // Walk through springNodes and get the total width
        double currentWidth = 0;
        double nextXPlusW = 0;
        for( int i=0; i<nodeArray.length; i++ ) {
            nextXPlusW = (nodeArray[i].getCenterX() + nodeArray[i].getWidth()/2);
            currentWidth = Math.max(currentWidth, nextXPlusW);
        }
        
        return (int)currentWidth;
    }
    

    public int getCurrentHeight() {
        // Walk through springNodes and get the total width
        double currentHeight = 0;
        double nextYPlusH = 0;
        for( int i=0; i<nodeArray.length; i++ ) {
            nextYPlusH = (nodeArray[i].getCenterY() + nodeArray[i].getHeight()/2);
            currentHeight = Math.max(currentHeight, nextYPlusH);
        }
        
        return (int)currentHeight;
    }
}
