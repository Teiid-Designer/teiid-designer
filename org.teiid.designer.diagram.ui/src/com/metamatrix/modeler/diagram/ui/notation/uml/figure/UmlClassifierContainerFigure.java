/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.figure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigure;
import com.metamatrix.modeler.diagram.ui.figure.ContainerFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;

/**
 * UmlClassifierContainerFigure
 */
public class UmlClassifierContainerFigure extends AbstractDiagramFigure implements ContainerFigure {
    private int numContainedItems = 0;
    private static int ySpacing = 2;
    private static int leftMargin = 5;
    private Vector containedItems;
    private int initialY = 0;
    private ScrollPane scrollpane;
    
    private IFigure pane;
    
    private int stackOrderValue = 0;
    
    /**
     * Construct an instance of UmlClassifierContainerFigure.
     * 
     */
    public UmlClassifierContainerFigure(DiagramModelNode diagramNode, ColorPalette colorPalette) {
        super(colorPalette);
        setDiagramModelNode(diagramNode);
        init();
        
        createComponent();
    }
    private void init() {
        this.setBackgroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));
        
        initialY = ySpacing*2;
        
        containedItems = new Vector();

        scrollpane = new ScrollPane();
        pane = new FreeformLayer();
        pane.setLayoutManager(new FreeformLayout());
        setLayoutManager(new StackLayout());
        add(scrollpane);
        scrollpane.setViewport(new FreeformViewport());
        scrollpane.setContents(pane);

        setOpaque(true);
    }
    
    private void createComponent() {
        int maxWidth = 10;
        int rectHeight = 10;
        
        maxWidth = getMinimumWidth() + 20;
        rectHeight = getMinimumHeight() + 20;
        
       
        this.setSize(maxWidth, rectHeight);
        
        layoutFigure();
    }
    
    public IFigure getContentsPane() {
        return pane;
    }
    
    @Override
    public void layoutFigure() {
        numContainedItems = 0;
        Collection items = new HashSet();
        
        // This constainer should have attribute type children
        // Reconcile 
        int currentY = initialY;
        Figure nextLabel = null;
        
        List childFigures = getContentsPane().getChildren();
        Iterator iter = childFigures.iterator();
        
        Object nextObject = null;
        
        while( iter.hasNext() ) {
            nextObject = iter.next();
            if( !items.contains(nextObject) ) {
                nextLabel = (Figure)nextObject;
                nextLabel.setLocation(new Point(leftMargin, currentY) );
                currentY += nextLabel.getBounds().height;
                
                items.add(nextObject);
                numContainedItems++;
            }
        }
        
        containedItems = new Vector(items);
        int minHeight = getMinimumHeight();
        Dimension dim = new Dimension(getMinimumWidth(), minHeight);

        if( getDiagramModelNode().isHeightFixed() ) {
            dim.height = getDiagramModelNode().getFixedHeight(); //DiagramModelNode.DEFAULT_FIXED_HEIGHT;
        }
        this.setSize(dim.width, dim.height);
    }
    
    
    public int getMinimumWidth() {
        int minimumWidth = 20;
        Figure nextLabel = null;
        for( int i=0; i<numContainedItems; i++ ) {
            nextLabel = (Figure)containedItems.get(i);
            minimumWidth = Math.max(minimumWidth, (nextLabel.getSize().width ) );
        }
        minimumWidth += leftMargin*3;
        return minimumWidth;
    }
    
    public int getMinimumHeight() {
        int minimumHeight = 10;
        Figure nextLabel = null;
        if(numContainedItems > 0 )
            minimumHeight = initialY;
        for( int i=0; i<numContainedItems; i++ ) {
            nextLabel = (Figure)containedItems.get(i);
            minimumHeight += nextLabel.getBounds().height;
        }
        
        minimumHeight += ySpacing*4;
        return minimumHeight;
    }
    
    @Override
    public void refreshFont() {
        layoutFigure();
    }
    /**
     * @return
     */
    public int getStackOrderValue() {
        return stackOrderValue;
    }

    /**
     * @param i
     */
    public void setStackOrderValue(int i) {
        stackOrderValue = i;
    }

}
