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

/**
 * This manager maintains and gives access to a current map/list of any ModelResourceAction contributions.
 * These contributions are required to auto-wire themselves to the workspace for Selection, if needed, as well
 * as loading images and text.
 * @author BLaFond
 *
 */

public abstract class ModelResourceActionManager {
	/** Array of all extensions to the DiagramHelper extension point */
	private static final String MODELS_LABEL = UiConstants.Util.getString("ModelResourceActionManager.modelsLabel"); //$NON-NLS-1$
	private static Collection actions;
	private static boolean actionsLoaded = false;
	
	public static Collection getActions(Object selection) {
		if( !actionsLoaded ) {
			loadExtensions();
		}
		return actions;
	}
	
	private static void loadExtensions() {
		HashMap actionExtList = new HashMap();
		actionsLoaded = true;


		// get the NewChildAction extension point from the plugin class
		String id = UiConstants.ExtensionPoints.ModelResourceActionExtension.ID;
		String actionTag = UiConstants.ExtensionPoints.ModelResourceActionExtension.RESOURCE_ACTION;
		String className = UiConstants.ExtensionPoints.ModelResourceActionExtension.CLASSNAME;
		String labelTag = UiConstants.ExtensionPoints.ModelResourceActionExtension.LABEL;
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
						// Set the text label
						String label = elements[j].getAttribute(labelTag);
						if( label != null ) {
							((Action)helper).setText(label);
						}

						actionExtList.put(elements[j].getAttribute(className), helper);
					}
				}
            
			} catch (Exception e) {
				// catch any Exception that occurred obtaining the configuration and log it
				String message = UiConstants.Util.getString("ModelResourceActionManager.loadingExtensionsErrorMessage", //$NON-NLS-1$
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
	
    public static MenuManager getModelResourceActionMenu(final ISelection theSelection) {
        MenuManager menu = new MenuManager(MODELS_LABEL,
                "resourceActions"); //$NON-NLS-1$
        
        Collection modelResourceActions = getActions(theSelection);
        
        // create a NewChildAction for every new child type
        Iterator iter = modelResourceActions.iterator();
        Action nextAction = null;

        while (iter.hasNext()) {
        	nextAction = (Action)iter.next();
        	if( UiPlugin.getDefault().isProductContextValueSupported(IModelerProductContexts.Actions.MODEL_RESOURCE_ACTION_GROUP, nextAction.getClass().getName()) ) {
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
        if( menu.getItems().length == 0 )
        	return null;
        
        return menu;
    }
}
