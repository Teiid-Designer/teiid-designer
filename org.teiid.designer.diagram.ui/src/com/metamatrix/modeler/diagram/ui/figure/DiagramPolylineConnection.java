/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.figure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.draw2d.AnchorListener;
import org.eclipse.draw2d.ArrowLocator;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.DelegatingLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import com.metamatrix.modeler.diagram.ui.util.ToolTipUtil;

/**
 * Connection based on polyline. The layout of the connection is handled by routers.
 */
public class DiagramPolylineConnection extends Polyline implements Connection, AnchorListener {
	
	private Color defaultLineColor = ColorConstants.black;
    private ConnectionAnchor startAnchor, endAnchor;
    private ConnectionRouter connectionRouter = ConnectionRouter.NULL;
    private RotatableDecoration startArrow, endArrow;

    {
        setLayoutManager(new DelegatingLayout());
        addPoint(new Point(0, 0));
        addPoint(new Point(100, 100));
    }

    /**
     * Hooks the source and target anchors.
     * @see Figure#addNotify()
     */
    @Override
    public void addNotify() {
        super.addNotify();
        hookSourceAnchor();
        hookTargetAnchor();
    }

    /**
     * Called by the anchors of this connection when they have moved, revalidating this 
     * polyline connection.
     * @param anchor the anchor that moved
     */
    public void anchorMoved(ConnectionAnchor anchor) {
        revalidate();
    }

    /**
     * Returns the bounds which holds all the points in this polyline connection. Returns any 
     * previously existing bounds, else calculates by unioning all the children's
     * dimensions.
     * @return the bounds
     */
    @Override
    public Rectangle getBounds() {
        if (bounds == null) {
            super.getBounds();
            for (int i = 0; i < getChildren().size(); i++) {
                IFigure child = (IFigure)getChildren().get(i);
                bounds.union(child.getBounds());
            }
        }
        return bounds;
    }

    /**
     * Returns the <code>ConnectionRouter</code> used to layout this connection. Will not 
     * return <code>null</code>.
     * @return this connection's router
     */
    public ConnectionRouter getConnectionRouter() {
        return connectionRouter;
    }

    /**
     * Returns this connection's routing constraint from its connection router.  May return 
     * <code>null</code>.
     * @return the connection's routing constraint
     */
    public Object getRoutingConstraint() {
        if (getConnectionRouter() != null)
            return getConnectionRouter().getConstraint(this);
        return null;
    }

    /**
     * @return the anchor at the start of this polyline connection (may be null)
     */
    public ConnectionAnchor getSourceAnchor() {
        return startAnchor;
    }

    /**
     * @return the source decoration (may be null)
     */
    protected RotatableDecoration getSourceDecoration() {
        return startArrow;
    }

    /**
     * @return the anchor at the end of this polyline connection (may be null)
     */
    public ConnectionAnchor getTargetAnchor() {
        return endAnchor;
    }

    /**
     * @return the target decoration (may be null)
     * 
     * @since 2.0
     */
    protected RotatableDecoration getTargetDecoration() {
        return endArrow;
    }

    private void hookSourceAnchor() {
        if (getSourceAnchor() != null)
            getSourceAnchor().addAnchorListener(this);
    }

    private void hookTargetAnchor() {
        if (getTargetAnchor() != null)
            getTargetAnchor().addAnchorListener(this);
    }

    /**
     * Layouts this polyline. If the start and end anchors are present, the connection router 
     * is used to route this, after which it is laid out. It also fires a moved method.
     */
    @Override
    public void layout() {
        if (getSourceAnchor() != null && getTargetAnchor() != null)
            getConnectionRouter().route(this);
        super.layout();
        bounds = null;
        repaint();
        fireFigureMoved();
    }

    /**
     * Called just before the receiver is being removed from its parent. Results in removing 
     * itself from the connection router.
     * 
     * @since 2.0
     */
    @Override
    public void removeNotify() {
        unhookSourceAnchor();
        unhookTargetAnchor();
        getConnectionRouter().remove(this);
        super.removeNotify();
    }

    /**
     * Sets the connection router which handles the layout of this polyline. Generally set by 
     * the parent handling the polyline connection.
     * @param cr the connection router
     */
    public void setConnectionRouter(ConnectionRouter cr) {
        if (cr == null)
            cr = ConnectionRouter.NULL;
        if (connectionRouter != cr) {
            connectionRouter.remove(this);
            Object old = connectionRouter;
            connectionRouter = cr;
            firePropertyChange(Connection.PROPERTY_CONNECTION_ROUTER, old, cr);
            revalidate();
        }
    }

    /**
     * Sets the routing constraint for this connection.
     * @param cons the constraint
     */
    public void setRoutingConstraint(Object cons) {
        if (getConnectionRouter() != null)
            getConnectionRouter().setConstraint(this, cons);
        revalidate();
    }

    /**
     * Sets the anchor to be used at the start of this polyline connection.
     * @param anchor the new source anchor
     */
    public void setSourceAnchor(ConnectionAnchor anchor) {
        unhookSourceAnchor();
        getConnectionRouter().invalidate(this);
        startAnchor = anchor;
        if (getParent() != null)
            hookSourceAnchor();
        revalidate();
    }

    /**
     * Sets the decoration to be used at the start of the {@link Connection}.
     * @param dec the new source decoration
     * @since 2.0
     */
    public void setSourceDecoration(RotatableDecoration dec) {
        if (getSourceDecoration() != null)
            remove(getSourceDecoration());
        startArrow = dec;
        if (dec != null)
            add(dec, new ArrowLocator(this, ConnectionLocator.SOURCE));
    }

    /**
     * Sets the anchor to be used at the end of the polyline connection. Removes this listener 
     * from the old anchor and adds it to the new anchor.
     * @param anchor the new target anchor
     */
    public void setTargetAnchor(ConnectionAnchor anchor) {
        unhookTargetAnchor();
        getConnectionRouter().invalidate(this);
        endAnchor = anchor;
        if (getParent() != null)
            hookTargetAnchor();
        revalidate();
    }

    /**
     * Sets the decoration to be used at the end of the {@link Connection}.
     * @param dec the new target decoration
     */
    public void setTargetDecoration(RotatableDecoration dec) {
        if (getTargetDecoration() != null)
            remove(getTargetDecoration());
        endArrow = dec;
        if (dec != null)
            add(dec, new ArrowLocator(this, ConnectionLocator.TARGET));
    }

    private void unhookSourceAnchor() {
        if (getSourceAnchor() != null)
            getSourceAnchor().removeAnchorListener(this);
    }

    private void unhookTargetAnchor() {
        if (getTargetAnchor() != null)
            getTargetAnchor().removeAnchorListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
     */
    @Override
    protected boolean useLocalCoordinates() {
        return false;
    }

	/**
	 * @see org.eclipse.draw2d.IFigure#setToolTip(IFigure)
	 */
	public void setToolTip(String toolTipString) {
		super.setToolTip(createToolTip(toolTipString));
	}
	
	
	public IFigure createToolTip(List toolTipStrings) {
		return ToolTipUtil.createToolTip(toolTipStrings);
	}
	
	public IFigure createToolTip(String toolTipString) {
		return ToolTipUtil.createToolTip(toolTipString);
	}

	public void hilite(boolean value) {
		if( value ) {
			super.setForegroundColor(ColorConstants.darkBlue);
			setLineWidth(3);
		} else {
			super.setForegroundColor(defaultLineColor);
			setLineWidth(1);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.IFigure#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	@Override
    public void setForegroundColor(Color bg) {
		defaultLineColor = bg;
		super.setForegroundColor(bg);
	}
    
    public List getInternalPoints() {
        if( getPoints().size() > 0 ) {
            List ptList = new ArrayList(getPoints().size()/2);
            int[] points = getPoints().toIntArray();
            for(int i=2; i<points.length-2; i++) {
                ptList.add( new Point(points[i], points[i+1]));
                i++;
            }
            return ptList;
        }
        
        return Collections.EMPTY_LIST;
    }

}
