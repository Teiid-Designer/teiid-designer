/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import org.eclipse.draw2d.Graphics;

/**
 * AssociationConstants
 */
public interface AssociationConstants {
    /**
     * Constants related to AssociationTypes
     * @since 4.0
     */
    interface Type {
        int UNKNOWN         = -1;
        int ASSOCIATION     = 0;
        int COMPOSITION     = 1;
        int DEPENDENCY      = 2;
        int GENERALIZATION  = 3;
        int REALIZATION     = 4;
    }
    
    interface LineStyle {
        int UNKNOWN         = Graphics.LINE_SOLID;
        int ASSOCIATION     = Graphics.LINE_SOLID;
        int COMPOSITION     = Graphics.LINE_SOLID;
        int DEPENDENCY      = Graphics.LINE_DASH;
        int GENERALIZATION  = Graphics.LINE_SOLID;
        int REALIZATION     = Graphics.LINE_DASH;
    }
}
