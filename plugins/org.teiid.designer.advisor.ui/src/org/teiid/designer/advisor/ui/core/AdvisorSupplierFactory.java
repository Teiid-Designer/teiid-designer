package org.teiid.designer.advisor.ui.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.scope.RelationalModelingNature;
import org.teiid.designer.advisor.ui.scope.VdbNature;
import org.teiid.designer.advisor.ui.scope.WebServicesModelingNature;
import org.teiid.designer.advisor.ui.scope.XmlModelingNature;


/**
 * The purpose of the factory class is to load, distribute and maintain a set of <code>AdvisorSupplier</code> contributions
 * designed to provide content management for the <code>AdvisorPanel</code>
 * 
 * The <code>AdvisorPanel</code> contains 2 sections. The top section requires both a GUI component, <code>StatusProvider</code>
 * and a Status component, <code>StatusManager</code>. The bottom section contains simple links to cheat sheet help via a
 * <code>CheatSheetProvider</code>.
 * 
 *
 */

public class AdvisorSupplierFactory {

    private static Collection suppliers;
    private static boolean suppliersLoaded = false;
    private static Object currentFocusedObject;
    
    private static IAdvisorSupplier currentSupplier;
   
    private static IAdvisorSupplier defaultSupplier = new DefaultAdvisorSupplier();

    /**
     * The Advisor is designed to provide visual feedback to the user based on a single user-defined object of some kind.
     * 
     * This object can be a Project, a File, a Model or an object within a Model or File.
     * 
     * This method returns the current "focused" object.
     * 
     * @return
     */
    public static Object getCurrentFocusedObject() {
		return currentFocusedObject;
	}

    private static void loadExtensions() {
        HashMap suppliersMap = new HashMap();
        suppliersLoaded = true;

        // get the NewChildAction extension point from the plugin class
        String id = AdvisorUiConstants.ExtensionPoints.AdvisorStatusManagerExtension.ID;
        String className = AdvisorUiConstants.ExtensionPoints.AdvisorStatusManagerExtension.CLASSNAME;
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(AdvisorUiConstants.PLUGIN_ID, id);

        // get the all extensions to the AdvisorStatusProvider extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        for (int i = 0; i < extensions.length; ++i) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            try {

                // first, find the provider instance and add it to the instance list
                for (int j = 0; j < elements.length; ++j) {
                    Object helper = elements[j].createExecutableExtension(className);
                    suppliersMap.put(elements[j].getAttribute(className), helper);
                }

            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = "Problem Loading Designer Advisor Extension Point Data";  //$NON-NLS-1$
                                                            //extensions[i].getUniqueIdentifier());
                AdvisorUiConstants.UTIL.log(IStatus.ERROR, e, message);
            }
        }

        suppliers = suppliersMap.values();
    }

    public static IAdvisorSupplier getSupplier( final Object target ) {
        if (!suppliersLoaded) {
            loadExtensions();
        }

        if (target != null ) {
        	if( target == currentFocusedObject ) {
        		return currentSupplier;
        	}
        	
//            String id = null;
//            if (target instanceof IProject) {
//                id = getPrimaryProjectScopeNature((IProject)target);
//            } else if (target instanceof IResource && ModelUtil.isModelFile((IResource)target)) {
//                id = "Model";
//            }

            // create a NewChildAction for every new child type
            Iterator iter = suppliers.iterator();
            IAdvisorSupplier nextSupplier = null;

            while (iter.hasNext()) {
            	nextSupplier = (IAdvisorSupplier)iter.next();
                if (nextSupplier.isApplicable(target)) {
                	currentFocusedObject = target;
                	if( currentSupplier != null ) {
                		currentSupplier.shutdown();
                	}
                	currentSupplier = nextSupplier;
                	
                	currentSupplier.startup();
                    return nextSupplier;
                }
            }
        }
        currentFocusedObject = target;
        currentSupplier = defaultSupplier;
        return defaultSupplier;
    }
    
    /**
     * Returns the current/active StatusProvider
     * 
     * @return
     */
    public static IAdvisorSupplier getCurrentSupplier() {
        if (!suppliersLoaded) {
            loadExtensions();
        }
    	if( currentSupplier != null ) {
    		return currentSupplier;
    	}
    	return defaultSupplier;
    }

    public static String getPrimaryProjectScopeNature( IProject proj ) {

        try {
            if (proj.hasNature(VdbNature.NATURE_ID)) {
                return VdbNature.NATURE_ID;
            }

            if (proj.hasNature(WebServicesModelingNature.NATURE_ID)) {
                return WebServicesModelingNature.NATURE_ID;
            }

            if (proj.hasNature(XmlModelingNature.NATURE_ID)) {
                return XmlModelingNature.NATURE_ID;
            }

            if (proj.hasNature(RelationalModelingNature.NATURE_ID)) {
                return RelationalModelingNature.NATURE_ID;
            }
        } catch (CoreException e) {
        	AdvisorUiConstants.UTIL.log(e);
        }

        return "NoNature";
    }
    
	public static boolean hasPrimaryProjectScopeNature(IProject proj, String id) {

		try {
			if (proj.hasNature(VdbNature.NATURE_ID)
					&& id.equalsIgnoreCase(VdbNature.NATURE_ID)) {
				return true;
			}

			if (proj.hasNature(WebServicesModelingNature.NATURE_ID)
					&& id.equalsIgnoreCase(WebServicesModelingNature.NATURE_ID)) {
				return true;
			}

			if (proj.hasNature(XmlModelingNature.NATURE_ID)
					&& id.equalsIgnoreCase(XmlModelingNature.NATURE_ID)) {
				return true;
			}

			if (proj.hasNature(RelationalModelingNature.NATURE_ID)
					&& id.equalsIgnoreCase(RelationalModelingNature.NATURE_ID)) {
				return true;
			}
		} catch (CoreException e) {
			AdvisorUiConstants.UTIL.log(e);
		}

		return false;
	}
}