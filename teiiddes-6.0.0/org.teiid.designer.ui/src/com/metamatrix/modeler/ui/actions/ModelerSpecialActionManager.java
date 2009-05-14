/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;

import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;

public abstract class ModelerSpecialActionManager {
	public static final String SPECIAL_LABEL = UiConstants.Util.getString("ModelerSpecialActionManager.specialLabel"); //$NON-NLS-1$
	private static Collection actions;
	private static boolean actionsLoaded = false;
	
	public ModelerSpecialActionManager() {
		super();
	}
	
	public static Collection getActions(Object selection) {
		if( !actionsLoaded ) {
			loadExtensions();
		}
		return actions;
	}
	
	private static void loadExtensions() {
		HashMap actionExtList = new HashMap();
		actionsLoaded = true;
		String id = UiConstants.ExtensionPoints.ModelerSpecialActionExtension.ID;
		String actionTag = UiConstants.ExtensionPoints.ModelerSpecialActionExtension.SPECIAL_ACTION;
		String className = UiConstants.ExtensionPoints.ModelerSpecialActionExtension.CLASSNAME;
		String labelTag = UiConstants.ExtensionPoints.ModelerSpecialActionExtension.LABEL;
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, id);
		
		// get the all extensions to the NewChildAction extension point
		IExtension[] extensions = extensionPoint.getExtensions();
		
		// walk through the extensions and find all INewChildAction implementations
		for ( int i=0 ; i<extensions.length ; ++i ) {
			IConfigurationElement[] elements = extensions[i].getConfigurationElements();
			try {

				// first, find the content provider instance and add it to the instance list
				for ( int j=0 ; j<elements.length ; ++j ) {
					if ( elements[j].getName().equals(actionTag)) {
						Object helper = elements[j].createExecutableExtension(className);

                        // Add an ID to provide getAction() method the ability to identify and return a specific action
                        String actionID = elements[j].getAttribute(className); 
                        
                        // Set the text label
                        String label = elements[j].getAttribute(labelTag);
                        ((Action)helper).setId(actionID);
						if( label != null ) {
							((Action)helper).setText(label);
						}


						actionExtList.put(elements[j].getAttribute(className), helper);
					}
				}
            
			} catch (Exception e) {
				// catch any Exception that occurred obtaining the configuration and log it
				String message = UiConstants.Util.getString("ModelerSpecialActionManager.loadingExtensionsErrorMessage", //$NON-NLS-1$
							extensions[i].getUniqueIdentifier()); 
				UiConstants.Util.log(IStatus.ERROR, e, message);
			}
		}
		
		
		Object[] actionsArray = actionExtList.values().toArray();
		Arrays.sort(actionsArray);
		// Now we need to set the actual map
		actions = new ArrayList(actionsArray.length);
		Collection keys = actionExtList.keySet();
		for(int i=0; i<actionsArray.length; i++ ) {
			Object key = null;
			for(Iterator iter = keys.iterator(); iter.hasNext(); ) {
				key = iter.next();
				if( actionExtList.get(key) == (actionsArray[i])) {
					actions.add(actionsArray[i]);
					break;
				}
			}
		}
	}

    /**
     * Returns a <code>MenuManager</code> containing actions relevant to the input selection.
     * @param theSelection
     * @return
     * @since 5.0
     */
    public static MenuManager getModeObjectSpecialActionMenu(final ISelection theSelection) {
        MenuManager menu = new MenuManager(SPECIAL_LABEL, ModelerActionBarIdManager.getModelingMenuId());
        
        Collection modelResourceActions = getActions(theSelection);

        Iterator iter = modelResourceActions.iterator();
        Action nextAction = null;

        while (iter.hasNext()) {
        	nextAction = (Action)iter.next();
        	if( UiPlugin.getDefault().isProductContextValueSupported(IModelerProductContexts.Actions.MODEL_OBJECT_SPECIAL_ACTION_GROUP, nextAction.getClass().getName()) ) {
        		if( nextAction instanceof ISelectionAction ) {
            		if( ((ISelectionAction)nextAction).isApplicable(theSelection) ) {
        	        	if( nextAction instanceof ISelectionListener) {
        	        		((ISelectionListener)nextAction).selectionChanged(null, theSelection);
        	        	}
        	            menu.add(nextAction);
            		}
        		} else {
                	if( nextAction instanceof ISelectionListener) {
                		((ISelectionListener)nextAction).selectionChanged(null, theSelection);
                	}

                    menu.add(nextAction);
        		}
        	}
        }
        
        return menu;
    }
    
    /**
     * Method which can be called to wire any loaded actions for workspace selection. This allows actions to exist in context
     * menus as well as toolbars and other static areas of the UI. 
     * @param service
     * @since 5.0
     */
    public static void wireActionsForSelection(ModelerActionService service ) {
        Collection specialActions = getActions(null);
        Iterator iter = specialActions.iterator();
        Action nextAction = null;

        while (iter.hasNext()) {
            nextAction = (Action)iter.next();
            if( nextAction instanceof SortableSelectionAction && ((SortableSelectionAction)nextAction).doWireForSelection()) {
                service.addWorkbenchSelectionListener((SortableSelectionAction)nextAction);
            }
        }
    }
    
    /**
     * Returns SortableSelectionAction who's ID matches actionId
     * @param actionId - the stringified action's ID
     * @return
     * @since 5.0
     */
    public static SortableSelectionAction getAction(String actionId) {
        Collection specialActions = getActions(null);
        Iterator iter = specialActions.iterator();
        Action nextAction = null;

        while (iter.hasNext()) {
            nextAction = (Action)iter.next();
            if( nextAction instanceof SortableSelectionAction &&
                            nextAction.getId() != null &&
                            nextAction.getId().equals(actionId)) {
                return (SortableSelectionAction)nextAction;
            }
        }
        
        return null;
    }
 
}
