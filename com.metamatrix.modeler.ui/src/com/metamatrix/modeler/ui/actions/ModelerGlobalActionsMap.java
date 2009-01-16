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

package com.metamatrix.modeler.ui.actions;

import java.util.Arrays;

import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.actions.GlobalActionsMap;

/**
 * The <code>ModelerGlobalActionsMap</code> class contains an entry for each of the global actions.
 * Initially each action is set to their default actions. Keys for the map is the global key of the
 * action. These can be found in {@link IModelerActionConstants}. The value can be one of the following:
 * <ul>
 * <li>an IAction object responsible for overriding the default action, 
 * <li>a value indicating the default action should be used (constant provided), and
 * <li>an IAction that will never be enabled indicating the action is not supported (constant provided).
 * </ul>
 * <p>
 * Sometimes an action is unsupported. An action which is never enabled will be used as the value
 * for that global action key.
 */
public class ModelerGlobalActionsMap extends GlobalActionsMap
                                     implements IModelerActionConstants,
                                                UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** All global actions of the modeler. Includes Eclipse global actions. */
    public static final String[] ALL_GLOBAL_ACTIONS;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    static {
        ALL_GLOBAL_ACTIONS = new String[ModelerGlobalActions.ALL_ACTIONS.length + EclipseGlobalActions.ALL_ACTIONS.length];
        System.arraycopy(ModelerGlobalActions.ALL_ACTIONS, 
                         0,
                         ALL_GLOBAL_ACTIONS, 
                         0, 
                         ModelerGlobalActions.ALL_ACTIONS.length);
        System.arraycopy(EclipseGlobalActions.ALL_ACTIONS, 
                         0,
                         ALL_GLOBAL_ACTIONS, 
                         ModelerGlobalActions.ALL_ACTIONS.length, 
                         EclipseGlobalActions.ALL_ACTIONS.length);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates if the given key represents a Modeler global action. This does not include Eclipse global
     * actions.
     * @param theKey the key being checked
     * @return <code>true</code> if a Modeler global action; <code>false</code> otherwise.
     */
    public static boolean isModelerGlobalAction(String theKey) {
        return Arrays.asList(ModelerGlobalActions.ALL_ACTIONS).contains(theKey);
    }
    
    /**
     * Indicates if the key is valid.
     * @param theKey the proposed key
     * @return <code>true</code> if key is valid; <code>false</code> otherwise.
     */
    @Override
    public boolean isValidKey(Object theKey) {
        boolean result = super.isValidKey(theKey);
        
        if (!result) {
            for (int i = 0; i < ModelerGlobalActions.ALL_ACTIONS.length; i++) {
                if (theKey.equals(ModelerGlobalActions.ALL_ACTIONS[i])) {
                    result = true;
                    break;
                }
            }
        }
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.ModelerGlobalActionsMap#reset()
     */
    @Override
    public void reset() {
        super.reset();

        for (int i = 0; i < ModelerGlobalActions.ALL_ACTIONS.length; ++i ) {
            put(ModelerGlobalActions.ALL_ACTIONS[i], DEFAULT_ACTION);            
        }

    }

}
