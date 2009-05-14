/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * NodeConnectionAnchor
 */
public class NodeConnectionAnchor extends AbstractConnectionAnchor {
    public static final boolean IS_SOURCE = true;
    public static final boolean IS_TARGET = false;
    private boolean leftToRight = true;
    private int offsetH;
    private int offsetV;
    private boolean topDown = true;
    private int direction;
    private boolean isSourceAnchor = true;

    public NodeConnectionAnchor(IFigure iOwner) {
        super(iOwner);
    }

    public NodeConnectionAnchor(IFigure iOwner, boolean isSource) {
        super(iOwner);
        this.isSourceAnchor = isSource;
    }

    /**
     * This method is taken from FixedConnectionAnchor in Logical Diagram Editor
     * example.
    **/
    public Point getLocation(Point iReference) {

        Rectangle r = getOwner().getBounds();
        int x, y;
        if (topDown)
            y = r.y + offsetV;
        else
            y = r.y + r.height - offsetV;

        if (leftToRight)
            x = r.x + offsetH;
        else
            x = r.x + r.width - offsetH;

        Point p = new Point(x, y);
        getOwner().translateToAbsolute(p);

        return p;
    }
    
    public void setHOffset(int newHOffset) {
        this.offsetH = newHOffset;
    }

    public void setAsSource(boolean isSource) {
        this.isSourceAnchor = isSource;
    }
    
    public boolean isSource() {
        return isSourceAnchor;
    }
    /**
     * @return
     */
    public int getOffsetH() {
        return offsetH;
    }

    /**
     * @return
     */
    public int getOffsetV() {
        return offsetV;
    }

    /**
     * @return
     */
    public boolean isTopDown() {
        return topDown;
    }

    /**
     * @param i
     */
    public void setOffsetH(int i) {
        offsetH = i;
    }

    /**
     * @param i
     */
    public void setOffsetV(int i) {
        offsetV = i;
    }

    /**
     * @param b
     */
    public void setTopDown(boolean b) {
        topDown = b;
    }

    /**
     * @return
     */
    public int getDirection() {
        return direction;
    }

    /**
     * @param i
     */
    public void setDirection(int i) {
        direction = i;
    }

    @Override
    public boolean equals( Object oNodeConnectionAnchor ) {

        if ( oNodeConnectionAnchor instanceof NodeConnectionAnchor ) {
            NodeConnectionAnchor ncaTarget = (NodeConnectionAnchor)oNodeConnectionAnchor;
            if ( this.getOffsetH() == ncaTarget.getOffsetH()
              && this.getOffsetV() == ncaTarget.getOffsetV()                             
              && this.getDirection() == ncaTarget.getDirection()                             
              && this.isTopDown() == ncaTarget.isTopDown()                             
              && this.isSource() == ncaTarget.isSource()                             
               ) {
                return true;
            }
        }
        
        return false;
    }
}
