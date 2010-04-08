/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.geometry.Point;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.diagram.DiagramLink;
import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.metamodels.diagram.DiagramPosition;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * @since 4.2
 */
public class DiagramLinkAdapter extends DiagramEntityAdapter {

    public static List getBendpoints( DiagramLink diagramLink ) {
        CoreArgCheck.isNotNull(diagramLink);

        List positions = diagramLink.getRoutePoints();
        if (positions != null && !positions.isEmpty()) {
            List bendpoints = new ArrayList(positions.size());
            Iterator iter = positions.iterator();
            DiagramPosition position = null;
            while (iter.hasNext()) {
                position = (DiagramPosition)iter.next();
                bendpoints.add(new Point(position.getXPosition(), position.getYPosition()));
            }
            return bendpoints;
        }

        return Collections.EMPTY_LIST;
    }

    public static void setBendpoints( DiagramLink diagramLink,
                                      List bendpoints ) {
        CoreArgCheck.isNotNull(diagramLink);
        boolean requiredStart = ModelerCore.startTxn(false, false, "Set Link Bendpoints", diagramLink); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Remove all current point
            List currentPoints = new ArrayList(diagramLink.getRoutePoints());
            if (!currentPoints.isEmpty()) {
                ModelObjectUtilities.delete(currentPoints, false, false, diagramLink);
            }
            DiagramUiUtilities.createDiagramPositions(diagramLink, bendpoints);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    public static void addBendpoint( DiagramLink diagramLink,
                                     int index,
                                     Point newBendpoint ) {
        CoreArgCheck.isNotNull(diagramLink);
        boolean requiredStart = ModelerCore.startTxn(false, false, "Add Link Bendpoint", diagramLink); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            DiagramPosition newPos = DiagramUiUtilities.createDiagramPosition(diagramLink, newBendpoint);
            diagramLink.getRoutePoints().move(index, newPos);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    public static void removeBendpoint( DiagramLink diagramLink,
                                        int index ) {
        CoreArgCheck.isNotNull(diagramLink);
        boolean requiredStart = ModelerCore.startTxn(false, false, "Remove Link Bendpoint", diagramLink); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Remove all current point
            List currentPoints = diagramLink.getRoutePoints();
            currentPoints.remove(index);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    public static void clearBendpoints( DiagramLink diagramLink ) {
        CoreArgCheck.isNotNull(diagramLink);
        boolean requiredStart = ModelerCore.startTxn(false, false, "Remove Link Bendpoint", diagramLink); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Remove all current point
            // List currentPoints = diagramLink.getRoutePoints();
            int nPoints = diagramLink.getRoutePoints().size();
            for (int i = nPoints - 1; i >= 0; i--) {
                removeBendpoint(diagramLink, i);
            }
            // currentPoints.clear();
            succeeded = true;
        } finally {
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    public static void setBendpoint( DiagramLink diagramLink,
                                     int index,
                                     Point newLocation ) {
        CoreArgCheck.isNotNull(diagramLink);
        boolean requiredStart = ModelerCore.startTxn(false, false, "Add Link Bendpoint", diagramLink); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Remove all current point
            List currentPoints = diagramLink.getRoutePoints();
            ((DiagramPosition)currentPoints.get(index)).setXPosition(newLocation.x);
            ((DiagramPosition)currentPoints.get(index)).setYPosition(newLocation.y);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    public static int getType( DiagramLink diagramLink ) {
        int type = DiagramLinkType.ORTHOGONAL;

        type = diagramLink.getType().getValue();

        return type;
    }

    public static void setType( DiagramLink diagramLink,
                                int newType ) {
        CoreArgCheck.isNotNull(diagramLink);
        boolean requiredStart = ModelerCore.startTxn(false, false, "Set Link Type", diagramLink); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Remove all current point
            DiagramLinkType theType = DiagramLinkType.get(newType);
            diagramLink.setType(theType);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
}
