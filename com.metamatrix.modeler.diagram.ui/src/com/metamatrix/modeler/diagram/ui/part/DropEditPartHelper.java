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

package com.metamatrix.modeler.diagram.ui.part;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;


/** 
 * @since 4.3
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
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#drop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.3
     */
    public void drop(Point dropPoint,
                     List dropList) {
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#hilite(boolean)
     * @since 4.3
     */
    public void hilite(boolean hilite) {
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#getLastHoverPoint()
     * @since 4.3
     */
    public Point getLastHoverPoint() {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#setLastHoverPoint(org.eclipse.draw2d.geometry.Point)
     * @since 4.3
     */
    public void setLastHoverPoint(Point lastHoverPoint) {
    }

}
