/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * DiagramEntityAdapter
 * This class provides transaction-based access to diagram entities for any "write" access methods,
 * as well as read access methods.
 */
public class DiagramEntityAdapter {
//    private static final int MAX_POS_INT = 99999;
    private static final int MAX_NEG_INT = -99999;

    // ---------
    // GETTER METHODS
    // ---------

    public static int getXPosition(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);

        return diagramEntity.getXPosition();
    }

    public static int getYPosition(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);
        int yPos = diagramEntity.getYPosition();
        if( yPos < MAX_NEG_INT ) {
            yPos = 10;
            diagramEntity.setYPosition(yPos);
        }
        return yPos;
    }

    public static Point getPosition(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);

        return new Point(diagramEntity.getXPosition(), diagramEntity.getYPosition());
    }

    public static String getName(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);

        return diagramEntity.getName();
    }

    public static int getWidth(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);

        return diagramEntity.getWidth();
    }

    public static int getHeight(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);

        return diagramEntity.getHeight();
    }

    public static Dimension getSize(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);

        return new Dimension(diagramEntity.getWidth(), diagramEntity.getHeight());
    }

    public static String getUserString(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);

        return diagramEntity.getUserString();
    }

    public static String getUserType(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);

        return diagramEntity.getUserType();
    }

    public static String getAlias(final DiagramEntity diagramEntity) {
        ArgCheck.isNotNull(diagramEntity);

        return diagramEntity.getAlias();
    }

    // -----------
    // SETTER METHODS
    // -----------
    public static void setXPosition(final DiagramEntity diagramEntity, final int iValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE X Position", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setXPosition(iValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setXPosition(
        final DiagramEntity diagramEntity,
        final int iValue,
        final boolean significance,
        final boolean undoable) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(significance, undoable, "Set DE X Position", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setXPosition(iValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setYPosition(final DiagramEntity diagramEntity, final int iValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE Y Position", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setYPosition(iValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setYPosition(
        final DiagramEntity diagramEntity,
        final int iValue,
        final boolean significance,
        final boolean undoable) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(significance, undoable, "Set DE Y Position", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setYPosition(iValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setPosition(final DiagramEntity diagramEntity, final int xValue, final int yValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE Position", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setXPosition(xValue);
            diagramEntity.setYPosition(yValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setPosition(
        final DiagramEntity diagramEntity,
        final int xValue,
        final int yValue,
        final boolean significance,
        final boolean undoable) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(significance, undoable, "Set DE Position", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setXPosition(xValue);
            diagramEntity.setYPosition(yValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setName(final DiagramEntity diagramEntity, final String sValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE Name", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setName(sValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setWidth(final DiagramEntity diagramEntity, final int iValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE Width", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setWidth(iValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setHeight(final DiagramEntity diagramEntity, final int iValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE Height", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setHeight(iValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setSize(final DiagramEntity diagramEntity, final int wValue, final int hValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE Size", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setWidth(wValue);
            diagramEntity.setHeight(hValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setUserString(final DiagramEntity diagramEntity, final String sValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE User String", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setUserString(sValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setUserType(final DiagramEntity diagramEntity, final String sValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE User Type", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setUserType(sValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }
    }

    public static void setAlias(final DiagramEntity diagramEntity, final String sValue) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set DE Alias", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setAlias(sValue);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }

    }
    

    public static void setModelObject(final DiagramEntity diagramEntity, final EObject eObject) {
        ArgCheck.isNotNull(diagramEntity);

        boolean requiredStart = ModelerCore.startTxn(false, false, "Set Model Object", diagramEntity); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            diagramEntity.setModelObject(eObject);
            succeeded = true;
        } finally {
            if (requiredStart) {
                if ( succeeded ) {
                    ModelerCore.commitTxn( );
                } else {
                    ModelerCore.rollbackTxn( );
                }
            }
        }

    }

}
