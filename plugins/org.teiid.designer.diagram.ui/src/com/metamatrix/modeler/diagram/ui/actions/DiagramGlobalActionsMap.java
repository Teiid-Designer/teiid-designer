/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import com.metamatrix.modeler.ui.actions.ModelerGlobalActionsMap;

/**
 * DiagramGlobalActionsMap
 */
public class DiagramGlobalActionsMap extends ModelerGlobalActionsMap                                  
                                  implements IDiagramActionConstants {

    /**
     * Construct an instance of DiagramGlobalActionsMap.
     * 
     */
    public DiagramGlobalActionsMap() {
        super();
    }
    
    
    // ===================================================
    // FIELDS
    // ===================================================
    
    /** All global actions.                                  */
    public static final String[] ALL_DIAGRAM_GLOBAL_ACTIONS;
        
    //===================================================
    // INITIALIZER
    // ===================================================
        
    static {
        ALL_DIAGRAM_GLOBAL_ACTIONS = new String[ModelerGlobalActions.ALL_ACTIONS.length 
                                              + EclipseGlobalActions.ALL_ACTIONS.length
                                              + DiagramGlobalActions.ALL_ACTIONS.length ];
        System.arraycopy(ALL_GLOBAL_ACTIONS, 
                         0,
                         ALL_DIAGRAM_GLOBAL_ACTIONS, 
                         0, 
                         ALL_GLOBAL_ACTIONS.length);
                         
        System.arraycopy(DiagramGlobalActions.ALL_ACTIONS, 
                         0,
                         ALL_DIAGRAM_GLOBAL_ACTIONS, 
                         ALL_GLOBAL_ACTIONS.length, 
                         DiagramGlobalActions.ALL_ACTIONS.length);
    }
        
    // ===================================================
    // METHODS
    // ===================================================
    
    /**
     * Indicates if the key is valid.
     * @param theKey the proposed key
     * @return <code>true</code> if key is valid; <code>false</code> otherwise.
     */
    @Override
    public boolean isValidKey(Object theKey) {
        // rewrite to use both the modeler's and the local actions (local first)
        
        boolean result = super.isValidKey(theKey);
            
        if (!result) {
            for (int i = 0; i < DiagramGlobalActions.ALL_ACTIONS.length; i++) {
                if (theKey.equals(DiagramGlobalActions.ALL_ACTIONS[i])) {
                    result = true;
                    break;
                }
            }
        }
                  
        return result;
    }
        
    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.DiagramGlobalActionsMap#reset()
     */
    @Override
    public void reset() {
        super.reset();
    
        for (int i = 0;
             i < DiagramGlobalActions.ALL_ACTIONS.length;
             put(DiagramGlobalActions.ALL_ACTIONS[i++], DEFAULT_ACTION)) {
            
        }
    }


}
