/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.layout;

/**
 * DiagramLayoutManager
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.geometry.Point;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.layout.spring.SpringLayout;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;


/**
 * @author blafond
 *
 * This class provides a wrapper to the JLoox layout managers.  It provides an easier way to 
 * maintain a temporary layout and use that layout in a larger layout. i.e. layout-layouts....
 * JLoox was having problems with this, so I wrote my own in <AbstractDiagram>
 */
public class DiagramLayoutManager implements DiagramUiConstants {
//    private static final String KEY_NOTATREE = "DiagramLayoutManager.treeLayout.notatree"; //$NON-NLS-1$
//    private static final String KEY_BADROOT = "DiagramLayoutManager.treeLayout.badroot"; //$NON-NLS-1$
//    private static final String KEY_NOROOT = "DiagramLayoutManager.treeLayout.noroot"; //$NON-NLS-1$
    
    
    public static final int UNKNOWN_LAYOUT      = -1;
    public static final int COLUMN_LAYOUT       = 0;
    public static final int TREE_LAYOUT         = 1;
    public static final int SPRING_LAYOUT       = 2;
    public static final int HIERARCHICAL_LAYOUT = 3;
    public static final int CIRCULAR_LAYOUT     = 4;
    public static final int TIER_LAYOUT         = 5;
    public static final int TABLE_LAYOUT        = 6;
    public static final int DECLUTTER_LAYOUT    = 7;
    public static final int ALIGN_LAYOUT        = 8;
    
    private DiagramLayout currentLayout;

    private int layoutType_ = -1;
    
    private double declutterPadding = 10;
    private double currentX = 0;
    private double currentY = 0;
    private boolean useLinksForLayout = false;
    private boolean useStartingBounds = false;
//    private double startingHeight = 10;
//    private double startingWidth = 10;
    
    private List allComponents;
    
    public DiagramLayoutManager(int layoutType) {
        createLayout(layoutType);
    }
        
    public DiagramLayoutManager(DiagramLayout someLayoutManager, List componentList) {
        currentLayout = someLayoutManager;
        setLayoutType();
        addComponents(componentList);
    }

    public DiagramLayoutManager(int layoutType, List componentList) {
        createLayout(layoutType);
        addComponents(componentList);
    }

    public void setLayout(DiagramLayout someLayout) {
        currentLayout = someLayout;
    }
    
    public DiagramLayout getLayout() {
        return currentLayout;
    }
    
    public void clear() {
        if( currentLayout != null )
            currentLayout.clear();
    }
    
    public void addComponents(List nodes) {
        allComponents = new ArrayList(nodes);
        if( currentLayout != null ) {
            Iterator iter = nodes.iterator();
            DiagramModelNode nextComp = null;
            
            while( iter.hasNext() ) {
                nextComp = (DiagramModelNode)iter.next();
                currentLayout.add(nextComp);
            }
        }
    }
    
    public void add(Object node) {
        if( allComponents == null )
            allComponents = new ArrayList(10);
//System.out.println(" DLM.add()  Node = " + node +  "  MO = " + ((DiagramModelNode)node).getModelObject());
        currentLayout.add(node);
    }
    
    public void remove(Object node) {
        currentLayout.remove(node);
    }
    
    public List getComponents() {
        if( currentLayout != null ) {
            return currentLayout.getComponents();
        }
        return Collections.EMPTY_LIST;
    }

    public double getWidth() {
        DiagramModelNode nextComp = null;
        double thisWidth = 10;
        double maxX = 0;
        double minX = 99999;

        Iterator iter = currentLayout.getComponents().iterator();
        while( iter.hasNext()) {
            nextComp = (DiagramModelNode)iter.next();
            maxX = Math.max(maxX, (nextComp.getPosition().x + nextComp.getSize().width) );
            minX = Math.min(minX, nextComp.getPosition().x );
        }
        if( maxX > minX )
            thisWidth = maxX - minX;
            
        return thisWidth;
    }
    

    public double getHeight() {
        DiagramModelNode nextComp = null;
        double thisHeight = 10;
        double maxY = 0;
        double minY = 99999;
        Iterator iter = currentLayout.getComponents().iterator();
        while( iter.hasNext()) {
            nextComp = (DiagramModelNode)iter.next();
            maxY = Math.max(maxY, (nextComp.getPosition().y + nextComp.getSize().height) );
            minY = Math.min(minY, nextComp.getPosition().y );
        }
        if( maxY > minY )
            thisHeight = maxY - minY;
            
        return thisHeight;
    }
    
    

    public double getMaxY() {
        DiagramModelNode nextComp = null;
        double maxY = 0;
        Iterator iter = currentLayout.getComponents().iterator();
        while( iter.hasNext()) {
            nextComp = (DiagramModelNode)iter.next();
            maxY = Math.max(maxY, (nextComp.getPosition().y + nextComp.getSize().height) );
        }
        return maxY;
    }
    

    public double getMaxX() {
        DiagramModelNode nextComp = null;
        double maxX = 0;
        Iterator iter = currentLayout.getComponents().iterator();
        while( iter.hasNext()) {
            nextComp = (DiagramModelNode)iter.next();
            maxX = Math.max(maxX, (nextComp.getPosition().x + nextComp.getSize().width) );
        }
        
        return maxX;
    }
    
    public int numberOfComponents() {
        if( currentLayout != null )
            return currentLayout.getComponentCount();
        return 0; 
    }
    
    public boolean run() {
        int returnValue = -99;
        boolean wasSuccessful = true;
        
        initializeLayout();
        
        if( numberOfComponents() > 1 ) {
            returnValue = currentLayout.run();
            processErrors(returnValue);
        }
        if( returnValue != 0 )
            wasSuccessful = false;
            
        return wasSuccessful;
    }
    
    public void setLocation( double newX, double newY ) {
        DiagramModelNode nextComp = null;
        currentX = newX;
        currentY = newY;
        Iterator iter = currentLayout.getComponents().iterator();
        while( iter.hasNext()) {
            nextComp = (DiagramModelNode)iter.next();
            nextComp.setPosition(
                new Point( nextComp.getPosition().x + newX, nextComp.getPosition().y + newY ));
        }
    }
    
    public double getY() {
        return currentY;
    }
    
    public double getX() {
        return currentX;
    }

    public void createLayout(int layoutType) {
        layoutType_ = layoutType;
        
        switch(layoutType) {
            case TREE_LAYOUT:           {   currentLayout = new MmTreeLayout();             } break;
            case COLUMN_LAYOUT:         {   currentLayout = new MmColumnLayout();           } break;            
//            case SPRING_LAYOUT:         {   currentLayout = new SpringLayout();           } break;
//            
//            case HIERARCHICAL_LAYOUT:   {   currentLayout = new MmHierarchicalLayout();     } break;
//            
//            case CIRCULAR_LAYOUT:       {   currentLayout = new MmCircularLayout();         } break;
//            
//            case TIER_LAYOUT:           {   currentLayout = new MmTierLayout();             } break;
//            
//            case TABLE_LAYOUT:          {   currentLayout = new MmTableLayout();            } break;
//            
//            case DECLUTTER_LAYOUT:      {   currentLayout = new MmDeclutterLayout();        } break;
//            
//            case ALIGN_LAYOUT:          {   currentLayout = new MmAlignLayout();            } break;
            
            default: {
                // Was not a JLoox error? or no layout manager set.
            }

        }
    }
    
    public void setLayoutType() {
        if( currentLayout instanceof MmTreeLayout )
            layoutType_ = TREE_LAYOUT;
        else if( currentLayout instanceof MmColumnLayout )
            layoutType_ = COLUMN_LAYOUT;
        else if( currentLayout instanceof SpringLayout )
            layoutType_ = SPRING_LAYOUT;
//        else if( currentLayout instanceof LxHierarchicalLayout )
//            layoutType_ = HIERARCHICAL_LAYOUT;
//        else if( currentLayout instanceof LxCircularLayout )
//            layoutType_ = CIRCULAR_LAYOUT;
//        else if( currentLayout instanceof LxTierLayout )
//            layoutType_ = TIER_LAYOUT;
//        else if( currentLayout instanceof LxTableLayout )
//            layoutType_ = TABLE_LAYOUT;
//        else if( currentLayout instanceof LxDeclutterLayout )
//            layoutType_ = DECLUTTER_LAYOUT;
//        else if( currentLayout instanceof LxAlignLayout )
//            layoutType_ = ALIGN_LAYOUT;
        else 
            layoutType_ = UNKNOWN_LAYOUT;
    }
    
    public void initializeLayout() {
        
        switch(layoutType_) {
            case TREE_LAYOUT: {
                setStartingBounds();
            } break;
            
            case SPRING_LAYOUT: {

            } break;
            
            case HIERARCHICAL_LAYOUT: {

            } break;
            
            case CIRCULAR_LAYOUT: {

            } break;
            
            case TIER_LAYOUT: {

            } break;
            
            case TABLE_LAYOUT: {

            } break;
            
            case DECLUTTER_LAYOUT: {

            } break;
            
            case ALIGN_LAYOUT: {

            } break;
            
            default: {
                // Was not a JLoox error? or no layout manager set.
            }

        }
    }
    
    private boolean processErrors(int errorCode) {
        boolean returnOK = true;
        
        switch(layoutType_) {
            case TREE_LAYOUT: {
                switch(errorCode) {
                    case MmTreeLayout.ERROR_NOT_A_TREE: {
//                        ModelerCore.Util.log( IStatus.WARNING, "[DiagramLayout.runTreeLayout() " + Util.getString(KEY_NOTATREE)); //$NON-NLS-1$
                        returnOK = false;
                    } break;
                    
                    case MmTreeLayout.ERROR_ROOT_NOT_MANAGED: {
//                        ModelerCore.Util.log( IStatus.WARNING, "[DiagramLayout.runTreeLayout() " + Util.getString(KEY_BADROOT)); //$NON-NLS-1$
                        returnOK = false;
                    } break;
                    
                    case MmTreeLayout.ERROR_TREE_HAS_NO_ROOT: {
//                        ModelerCore.Util.log( IStatus.WARNING, "[DiagramLayout.runTreeLayout() " + Util.getString(KEY_NOROOT)); //$NON-NLS-1$
                        returnOK = false;
                    } break;
                    
                    default: {
                        // LAYOUT IS FINE
                    } break;
                }
            } break;
            
            case SPRING_LAYOUT: {
                switch(errorCode) {          
                    default: {
                        // LAYOUT IS FINE
                    } break;
                }
            } break;
            
            case HIERARCHICAL_LAYOUT: {
            } break;
            
            case CIRCULAR_LAYOUT: {
            } break;
            
            case TIER_LAYOUT: {
            } break;
            
            case TABLE_LAYOUT: {
            } break;
            
            case DECLUTTER_LAYOUT: {

            } break;
            
            case ALIGN_LAYOUT: {

            } break;
            
            default: {
                // Was not a JLoox error? or no layout manager set.
            }

        }
        
        return returnOK;
    }
    
    public void setDeclutterPadding(double newPadding) {
        if( layoutType_ == DECLUTTER_LAYOUT && currentLayout != null ) {
//            ((LxDeclutterLayout)currentLayout).setObjectPadding(newPadding);
            declutterPadding = newPadding;
        }
    }
    
    public double getDeclutterPadding() {
        return declutterPadding;
    }
    
    public void setUseLinks(boolean useLinks) {
        useLinksForLayout = useLinks;
    }
    
    
    public void setUseStartingBounds(boolean useBounds) {
        useStartingBounds = useBounds;
    }
    
    public boolean shouldUseStartingBounds() {
        return useStartingBounds;
    }
    
    public void setStartingBounds( double width, double height) {
//        startingWidth = width;
//        startingHeight = height;
    }
    
    private void setStartingBounds( ) {
        if( useLinksForLayout ) {
            double totalArea = 0;
            double deltaArea = 0;
            int minCompWidth = 9999;
            int minCompHeight = 9999;
            int maxCompWidth = 0;
            int maxCompHeight = 0;
            DiagramModelNode firstComp = null;
    
            double areaFactor = 1.0;
            double heightRatio = 1.0;
            double widthRatio = 1.0;
            double layoutWidth = 1.0;
            double layoutHeight = 1.0;
            
            int nLinks = 0; //DiagramUtilities.getLinksForComps(diagram, (Collection)allComponents).size();
            
            int numComps = currentLayout.getComponentCount();
            
            DiagramModelNode nextComp = null;
            Iterator iter = allComponents.iterator();
            
            // Go Through Each Component and add Area to total;
            while( iter.hasNext() ) {
                nextComp = (DiagramModelNode)iter.next();
                if( nextComp != null ) {

                    if( firstComp == null )
                        firstComp = nextComp;
                    maxCompWidth = Math.max( nextComp.getSize().width, maxCompWidth );
                    maxCompHeight = Math.max( nextComp.getSize().height, maxCompHeight );
                    minCompWidth = Math.min( nextComp.getSize().width, minCompWidth );
                    minCompHeight = Math.min( nextComp.getSize().height, minCompHeight );
                    deltaArea = nextComp.getSize().height*nextComp.getSize().width;
                    totalArea += deltaArea;
                } 
            }
            
            maxCompHeight = maxCompHeight + 20;
            maxCompWidth = maxCompWidth + 20;
    
            heightRatio = (maxCompHeight/minCompHeight);
            widthRatio =  (maxCompWidth/minCompWidth);
    
            if( maxCompHeight > maxCompWidth ) {
                if( heightRatio < 2.0 && widthRatio < 2.0 )
                    areaFactor = 2.5;
                else areaFactor = Math.max( heightRatio, widthRatio );
            } else areaFactor = 2.5;
            
            layoutWidth = Math.sqrt(areaFactor*totalArea);
            layoutHeight = layoutWidth;
            
            double adjFactor = 1.2;
            
            if( nLinks > 5  && nLinks < 10) {
                adjFactor = Math.sqrt(Math.sqrt(nLinks - 3));
            } else if( nLinks >= 10 ) {
                adjFactor = Math.sqrt(Math.sqrt(nLinks + 15));
            }
            adjFactor = Math.max(1.4, adjFactor);
            
            layoutWidth = layoutWidth*adjFactor;
            layoutHeight = layoutHeight*adjFactor;
            
            if( numComps < 10 ) {
                layoutHeight = layoutHeight*1.4;
                layoutWidth = layoutWidth*1.4;
                
                double linkCompRatio = nLinks/numComps;
                if( linkCompRatio > .5 )
                    layoutWidth = layoutWidth*1.4;
            }
            if( nLinks > 10 )
                layoutWidth = layoutWidth*1.3;
                
            setStartingBounds(layoutWidth, layoutHeight);
        } else {
            
        }
        
    }
    
    public void justifyAllToCorner() {
        // Check to see that minimum initial X,Y of all components is < 11
        // else move all components to fill up left/top of window.
        
        Iterator iter = getComponents().iterator();
        double minX = 9999;
        double minY = 9999;
        DiagramModelNode nextComp = null;
        double deltaX = 0;
        double deltaY = 0;
        
        while( iter.hasNext() ) {
            nextComp = (DiagramModelNode)iter.next();
            minX = Math.min( nextComp.getX(), minX );
            minY = Math.min( nextComp.getY(), minY );
        }
        
        deltaX = minX - 20;
        deltaY = minY - 20;
        
        if( minX > 20 && minY > 20 ) {
            iter = getComponents().iterator();
            while( iter.hasNext() ) {
                nextComp = (DiagramModelNode)iter.next();
                nextComp.setPosition( new Point(nextComp.getX() - deltaX, nextComp.getY() - deltaY) );
            }    
        } else if( minX > 20 && minY < 20 ) {
            iter = getComponents().iterator();
            while( iter.hasNext() ) {
                nextComp = (DiagramModelNode)iter.next();
                nextComp.setPosition( new Point(nextComp.getX() - deltaX, nextComp.getY()) );
            }           
        } else if( minX < 20 && minY > 20 ) {
            iter = getComponents().iterator();
            while( iter.hasNext() ) {
                nextComp = (DiagramModelNode)iter.next();
                 nextComp.setPosition( new Point(nextComp.getX(), nextComp.getY() - deltaY ) );
            }
        }
    }

}

