/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.part;

import java.util.List;
import org.eclipse.draw2d.geometry.Point;


/** 
 * @since 8.0
 */
public abstract class DropEditPartHelper implements
                                        DropEditPart {

    /** 
     * 
     * @since 4.3
     */
    public DropEditPartHelper() {
        super();
    }

    /** 
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#drop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.3
     */
    @Override
	public void drop(Point dropPoint,
                     List dropList) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#hilite(boolean)
     * @since 4.3
     */
    @Override
	public void hilite(boolean hilite) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#getLastHoverPoint()
     * @since 4.3
     */
    @Override
	public Point getLastHoverPoint() {
        return null;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#setLastHoverPoint(org.eclipse.draw2d.geometry.Point)
     * @since 4.3
     */
    @Override
	public void setLastHoverPoint(Point lastHoverPoint) {
    }

}
