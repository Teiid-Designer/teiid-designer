/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Ray;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Provides a customized {@link Connection} with an orthogonal route between the connection's source and target anchors. This
 * class provides means to pad the offset of the routed lines outside of
 */
public final class BlockConnectionRouter extends AbstractRouter {

    private int offsetPadding = 10;

    private Map<Integer, Integer> usedRows = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> usedColumns = new HashMap<Integer, Integer>();

    private Map<Connection, ReservedInfo> reservedInfo = new HashMap<Connection, ReservedInfo>();

    class ReservedInfo {
        public List<Integer> reservedRows = new ArrayList<Integer>(2);
        public List<Integer> reservedColumns = new ArrayList<Integer>(2);
    }

    private static Ray DIR_UP = new Ray(0, -1), DIR_DOWN = new Ray(0, 1), DIR_LEFT = new Ray(-1, 0), DIR_RIGHT = new Ray(1, 0);

    /**
     * Create with initial offsetPadding
     * 
     * @param offsetPadding
     */
    public BlockConnectionRouter( int offsetPadding ) {
        super();
        this.offsetPadding = offsetPadding;
    }

    /**
     * @see ConnectionRouter#invalidate(Connection)
     */
    @Override
    public void invalidate( Connection connection ) {
        removeReservedLines(connection);
    }

    /**
     * Returns the direction of the point <i>p</i> in relation to the given rectangle. Possible values are DIR_LEFT (-1,0),
     * DIR_RIGHT (1,0), DIR_UP (0,-1) and DIR_DOWN (0,1).
     * 
     * @param r the rectangle
     * @param p the point
     * @return the direction from <i>r</i> to <i>p</i>
     */
    protected Ray getDirection( Rectangle r,
                                Point p ) {
        int i, distance = Math.abs(r.x - p.x);
        Ray direction;

        direction = DIR_LEFT;

        i = Math.abs(r.y - p.y);
        if (i <= distance) {
            distance = i;
            direction = DIR_UP;
        }

        i = Math.abs(r.bottom() - p.y);
        if (i <= distance) {
            distance = i;
            direction = DIR_DOWN;
        }

        i = Math.abs(r.right() - p.x);
        if (i < distance) {
            distance = i;
            direction = DIR_RIGHT;
        }

        return direction;
    }

    private int getNearestColumn( Connection connection,
                                  int r,
                                  int n,
                                  int x ) {
        int min = Math.min(n, x), max = Math.max(n, x);
        if (min > r) {
            max = min;
            min = r - (min - r);
        }
        if (max < r) {
            min = max;
            max = r + (r - max);
        }
        int proxVal = 0;
        int dirVal = -1;
        if (r % 2 == 1) r--;
        Integer i;
        while (proxVal < r) {
            i = new Integer(r + proxVal * dirVal);
            if (!usedColumns.containsKey(i)) {
                usedColumns.put(i, i);
                reserveColumn(connection, i);
                return i.intValue();
            }
            int j = i.intValue();
            if (j <= min) return j + 2;
            if (j >= max) return j - 2;
            if (dirVal == 1) dirVal = -1;
            else {
                dirVal = 1;
                proxVal += 2;
            }
        }
        return r;
    }

    protected Ray getEndDirection( Connection conn ) {
        ConnectionAnchor anchor = conn.getTargetAnchor();
        Point p = getEndPoint(conn);
        Rectangle rect;
        if (anchor.getOwner() == null) rect = new Rectangle(p.x - 1, p.y - 1, 2, 2);
        else {
            rect = conn.getTargetAnchor().getOwner().getBounds().getCopy();
            conn.getTargetAnchor().getOwner().translateToAbsolute(rect);
        }
        return getDirection(rect, p);
    }

    protected int getNearestRow( Connection connection,
                                 int r,
                                 int n,
                                 int x ) {
        int min = Math.min(n, x), max = Math.max(n, x);
        if (min > r) {
            max = min;
            min = r - (min - r);
        }
        if (max < r) {
            min = max;
            max = r + (r - max);
        }

        int proxVal = 0;
        int dirVal = -1;
        if (r % 2 == 1) r--;
        Integer i;
        while (proxVal < r) {
            i = new Integer(r + proxVal * dirVal);
            if (!usedRows.containsKey(i)) {
                usedRows.put(i, i);
                reserveRow(connection, i);
                return i.intValue();
            }
            int j = i.intValue();
            if (j <= min) return j + 2;
            if (j >= max) return j - 2;
            if (dirVal == 1) dirVal = -1;
            else {
                dirVal = 1;
                proxVal += 2;
            }
        }
        return r;
    }

    protected Ray getStartDirection( Connection conn ) {
        ConnectionAnchor anchor = conn.getSourceAnchor();
        Point p = getStartPoint(conn);
        Rectangle rect;
        if (anchor.getOwner() == null) rect = new Rectangle(p.x - 1, p.y - 1, 2, 2);
        else {
            rect = conn.getSourceAnchor().getOwner().getBounds().getCopy();
            conn.getSourceAnchor().getOwner().translateToAbsolute(rect);
        }
        return getDirection(rect, p);
    }

    /**
     * Process all positions and reset routing points
     * 
     * @param start
     * @param end
     * @param positions
     * @param horizontalState
     * @param conn
     */
    protected void processFinalPositions( Ray start,
                                          Ray end,
                                          List positions,
                                          boolean horizontalState,
                                          Connection conn ) {
        removeReservedLines(conn);

        int pos[] = new int[positions.size() + 2];
        int i;

        if (horizontalState) pos[0] = start.x;
        else pos[0] = start.y;

        for (i = 0; i < positions.size(); i++) {
            pos[i + 1] = ((Integer)positions.get(i)).intValue();
        }

        if (horizontalState == (positions.size() % 2 == 1)) {
            pos[++i] = end.x;
        } else {
            pos[++i] = end.y;
        }

        PointList routerPoints = new PointList();
        Point newPoint;
        int currentPosition, previousPosition, minPosition, maxPosition;
        boolean adjust;

        routerPoints.addPoint(new Point(start.x, start.y));

        for (i = 2; i < pos.length - 1; i++) {
            horizontalState = !horizontalState;
            previousPosition = pos[i - 1];
            currentPosition = pos[i];

            adjust = (i != pos.length - 2);
            if (horizontalState) {
                if (adjust) {
                    minPosition = pos[i - 2];
                    maxPosition = pos[i + 2];
                    pos[i] = currentPosition = getNearestRow(conn, currentPosition, minPosition, maxPosition);
                }
                newPoint = new Point(previousPosition, currentPosition);
            } else {
                if (adjust) {
                    minPosition = pos[i - 2];
                    maxPosition = pos[i + 2];
                    pos[i] = currentPosition = getNearestColumn(conn, currentPosition, minPosition, maxPosition);
                }
                newPoint = new Point(currentPosition, previousPosition);
            }
            routerPoints.addPoint(newPoint);
        }
        routerPoints.addPoint(new Point(end.x, end.y));
        conn.setPoints(routerPoints);
    }

    /**
     * @see ConnectionRouter#remove(Connection)
     */
    @Override
    public void remove( Connection connection ) {
        removeReservedLines(connection);
    }

    protected void reserveColumn( Connection connection,
                                  Integer column ) {
        ReservedInfo info = reservedInfo.get(connection);
        if (info == null) {
            info = new ReservedInfo();
            reservedInfo.put(connection, info);
        }
        info.reservedColumns.add(column);
    }

    protected void reserveRow( Connection connection,
                               Integer row ) {
        ReservedInfo info = reservedInfo.get(connection);
        if (info == null) {
            info = new ReservedInfo();
            reservedInfo.put(connection, info);
        }
        info.reservedRows.add(row);
    }

    protected void removeReservedLines( Connection connection ) {
        ReservedInfo rInfo = reservedInfo.get(connection);
        if (rInfo == null) return;

        for (int i = 0; i < rInfo.reservedRows.size(); i++) {
            usedRows.remove(rInfo.reservedRows.get(i));
        }
        for (int i = 0; i < rInfo.reservedColumns.size(); i++) {
            usedColumns.remove(rInfo.reservedColumns.get(i));
        }
        reservedInfo.remove(connection);
    }

    /**
     * Create all route points.
     * 
     * @see ConnectionRouter#route(Connection)
     */
    public void route( Connection conn ) {
        if ((conn.getTargetAnchor() == null) || (conn.getSourceAnchor() == null)) {
            return;
        }

        int i;

        Point startingPt = getStartPoint(conn);
        conn.translateToRelative(startingPt);
        Point endPt = getEndPoint(conn);
        conn.translateToRelative(endPt);

        Ray startRay = new Ray(startingPt);
        Ray endRay = new Ray(endPt);
        Ray averageRay = startRay.getAveraged(endRay);

        Ray direction = new Ray(startRay, endRay);
        Ray startingNormal = getStartDirection(conn);
        Ray endingNormal = getEndDirection(conn);

        Vector<Integer> positions = new Vector<Integer>(5);

        boolean horizontalState = startingNormal.isHorizontal();

        if (horizontalState) {
            positions.add(new Integer(startRay.y));
        } else {
            positions.add(new Integer(startRay.x));
        }

        horizontalState = !horizontalState;

        if (startingNormal.dotProduct(endingNormal) == 0) {
            if ((startingNormal.dotProduct(direction) >= 0) && (endingNormal.dotProduct(direction) <= 0)) {
                // 0 only
                // Do nothing
            } else {
                // 2 only
                if (startingNormal.dotProduct(direction) < 0) i = startingNormal.similarity(startRay.getAdded(startingNormal.getScaled(offsetPadding)));
                else {
                    if (horizontalState) i = averageRay.y;
                    else i = averageRay.x;
                }

                positions.add(new Integer(i));
                horizontalState = !horizontalState;

                if (endingNormal.dotProduct(direction) > 0) {
                    i = endingNormal.similarity(endRay.getAdded(endingNormal.getScaled(offsetPadding)));
                } else {
                    if (horizontalState) {
                        i = averageRay.y;
                    } else {
                        i = averageRay.x;
                    }
                }
                positions.add(new Integer(i));
                horizontalState = !horizontalState;
            }
        } else {
            if (startingNormal.dotProduct(endingNormal) > 0) {
                // 1 only
                if (startingNormal.dotProduct(direction) >= 0) {
                    i = startingNormal.similarity(startRay.getAdded(startingNormal.getScaled(offsetPadding)));
                } else {
                    i = endingNormal.similarity(endRay.getAdded(endingNormal.getScaled(offsetPadding)));
                }
                positions.add(new Integer(i));
                horizontalState = !horizontalState;
            } else {
                // 3 or 1
                if (startingNormal.dotProduct(direction) < 0) {
                    i = startingNormal.similarity(startRay.getAdded(startingNormal.getScaled(offsetPadding)));
                    positions.add(new Integer(i));
                    horizontalState = !horizontalState;
                }

                if (horizontalState) {
                    i = averageRay.y;
                } else {
                    i = averageRay.x;
                }

                positions.add(new Integer(i));
                horizontalState = !horizontalState;

                if (startingNormal.dotProduct(direction) < 0) {
                    i = endingNormal.similarity(endRay.getAdded(endingNormal.getScaled(offsetPadding)));
                    positions.add(new Integer(i));
                    horizontalState = !horizontalState;
                }
            }
        }
        if (horizontalState) {
            positions.add(new Integer(endRay.y));
        } else {
            positions.add(new Integer(endRay.x));
        }

        processFinalPositions(startRay, endRay, positions, startingNormal.isHorizontal(), conn);
    }

}
