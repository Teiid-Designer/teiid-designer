/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.actions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IAction;

import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.UiPlugin;

/**
 * The <code>GlobalActionsMap</code> class contains an entry for each of the Eclipse global actions.
 * Initially each action is set to their default actions. Keys for the map is the global key of the
 * action. These can be found in {@link IActionConstants}. The value can be one of the following:
 * <ul>
 * <li>an IAction object responsible for overriding the default action, 
 * <li>a value indicating the default action should be used (constant provided), and
 * <li>an IAction that will never be enabled indicating the action is not supported (constant provided).
 * </ul>
 */
public class GlobalActionsMap implements Map,
                                         IActionConstants,
                                         UiConstants {
    
    /** Constant to use as value for global action when the action is not supported. */
    public static final IAction UNSUPPORTED_ACTION;
    
    /** Constant to use as value for global action when the default action should be used. */
    public static final Object DEFAULT_ACTION = null;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    static {
        UNSUPPORTED_ACTION = new AbstractAction(UiPlugin.getDefault()) 
        	{/*subclass needed but no impl*/
        		@Override
                protected void doRun() {}
                @Override
                public void setEnabled(boolean theEnabled) {
                    super.setEnabled(false);
                }
        	};
        UNSUPPORTED_ACTION.setEnabled(false);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Delegate map. */
    private Map map;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * Constructs a <code>GlobalActionsMap</code>. Each global action is set to a default action 
     * that is never enabled.
     */
    public GlobalActionsMap() {
        map = new HashMap();
        reset(); // sets initial values
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This operation is not supported.
     * @throws UnsupportedOperationException if called.
     */
    public void clear() {
        throw new UnsupportedOperationException(Util.getString("GlobalActionsMap.unsupportedOperation")); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object theKey) {
        return map.containsKey(theKey);
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object theValue) {
        return map.containsValue(theValue);
    }

    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        return map.entrySet();
    }

    /* (non-Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object theKey) {
        return map.get(theKey);
    }
    
    /**
     * Gets the action associated with the key.
     * @param theKey the key associated with the requested action
     * @return the action or <code>null</code>
     */
    public IAction getAction(String theKey) {
        return (IAction)get(theKey);
    }
    
    /**
     * Indicates if the value represented by the given key represents the default action. If the key is not
     * a valid key, <code>false</code> is returned.
     * @param theKey the key whose action value is being checked
     * @return <code>true</code> if action value represents the default action; <code>false</code> otherwise.
     */
    public boolean isDefaultAction(String theKey) {
        return (isValidKey(theKey) && (get(theKey) == DEFAULT_ACTION));
    }
    
    /**
     * This map is never empty as it always contains entries for each global action.
     * @return <code>false</code>
     */
    public boolean isEmpty() {
        return false;
    }
    
    /**
     * Indicates if the given key represents an Eclipse global action.
     * @param theKey the key being checked
     * @return <code>true</code> if an Eclipse global action; <code>false</code> otherwise.
     */
    public static boolean isEclipseGlobalAction(String theKey) {
        return Arrays.asList(EclipseGlobalActions.ALL_ACTIONS).contains(theKey);
    }
    
    /**
     * Indicates if the value represented by the given key represents an unsupported action.
     * @param theKey the key whose action value is being checked
     * @return <code>true</code> if action value represents an unsupported action; <code>false</code> otherwise.
     */
    public boolean isUnsupportedAction(Object theKey) {
        return (get(theKey) == UNSUPPORTED_ACTION);
    }
    
    /**
     * Indicates if the key is valid.
     * @param theKey the proposed key
     * @return <code>true</code> if key is valid; <code>false</code> otherwise.
     */
    public boolean isValidKey(Object theKey) {
        boolean result = false;
        
        if (theKey != null) {
            for (int i = 0; i < EclipseGlobalActions.ALL_ACTIONS.length; i++) {
                if (theKey.equals(EclipseGlobalActions.ALL_ACTIONS[i])) {
                    result = true;
                    break;
                }
            }
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        return map.keySet();
    }

    /**
     * If a valid key, the current key value is replaced. If the proposed value is <code>null</code> or
     * {@link #DEFAULT_ACTION}, the default action will be used. If the proposed value is {@link #UNSUPPORTED_ACTION},
     * an action that is never enabled will be used.
     * @param theKey the key whose value is being changed
     * @param theValue the new value or <code>null</code>
     * @return the old value or <code>null</code>
     * @throws IllegalArgumentException if key is invalid
     * @throws NullPointerException if key is <code>null</code>
     */
    public Object put(Object theKey,
                      Object theValue) {
        if (theKey == null) {
            throw new NullPointerException(Util.getString("GlobalActionsMap.nullKey")); //$NON-NLS-1$
        }

        if (!isValidKey(theKey)) {
            throw new IllegalArgumentException(Util.getString("GlobalActionsMap.invalidKey", new Object[] {theKey})); //$NON-NLS-1$
        }

        return map.put(theKey, theValue);
    }

    /**
     * Only valid map keys are changed.
     * @param theMap the map whose entries are used to modify this map
     * @throws IllegalArgumentException if key is invalid
     * @throws NullPointerException if key is <code>null</code>
     */
    public void putAll(Map theMap) {
        if (theMap !=  null) {
            Iterator itr = theMap.entrySet().iterator();
            
            while (itr.hasNext()) {
                Map.Entry entry = (Map.Entry)itr.next();
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * This operation is not supported.
     * @throws UnsupportedOperationException if called.
     */
    public Object remove(Object theKey) {
        throw new UnsupportedOperationException(Util.getString("GlobalActionsMap.unsupportedOperation")); //$NON-NLS-1$
    }

    /** Resets all actions to be the default action. */
    public void reset() {
        map.clear();

        for (int i = 0; i < EclipseGlobalActions.ALL_ACTIONS.length; i++) {
            put(EclipseGlobalActions.ALL_ACTIONS[i], DEFAULT_ACTION);
        }
    }

    /** Always returns the current number of global actions. */
    public int size() {
        return map.size();
    }
    
    /**
     * Gets a string representation of this map.
     * @return a string representation
     */
    @Override
    public String toString() {
        return map.toString();
    }
    
    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    public Collection values() {
        return map.values();
    }

    protected Map getMap() {
        return map;
    }
        
}
