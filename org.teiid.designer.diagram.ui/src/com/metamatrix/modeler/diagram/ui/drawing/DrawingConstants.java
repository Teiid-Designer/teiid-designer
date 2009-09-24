/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing;

/**
 * DrawingConstants
 */
public interface DrawingConstants {
    /**
     * Constants related to Drawing objects
     * @since 4.0
     */
    interface TypeId {
        int NOTE        = 0;
        int RECTANGLE   = 1;
        int ELLIPSE     = 2;
        int TEXT        = 3;
    }
    
    interface Types {
        String NOTE         = "note"; //$NON-NLS-1$
        String RECTANGLE    = "rectangle"; //$NON-NLS-1$
        String ELLIPSE      = "ellipse"; //$NON-NLS-1$
        String TEXT         = "text"; //$NON-NLS-1$
    }
}
