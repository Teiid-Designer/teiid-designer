/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.core.status;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
/**
 * 
 */
public class AdvisorStatusExtensionManager {

    private static Collection statusProviders;
    private static boolean providersLoaded = false;
    private static IStatusContentProvider defaultStatusProvider = new DefaultStatusProvider();
    
    private static IStatusContentProvider currentStatusProvider = defaultStatusProvider;
    private static Object currentFocusedObject;

    public static Object getCurrentFocusedObject() {
		return currentFocusedObject;
	}

	public static Collection getProviders() {
        if (!providersLoaded) {
            loadExtensions();
        }
        return statusProviders;
    }

    private static void loadExtensions() {
        HashMap statusProvidersMap = new HashMap();
        providersLoaded = true;

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
                    statusProvidersMap.put(elements[j].getAttribute(className), helper);
                }

            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = "Problem Loading Designer Advisor Extension Point Data";  //$NON-NLS-1$
                                                            //extensions[i].getUniqueIdentifier());
                AdvisorUiConstants.UTIL.log(IStatus.ERROR, e, message);
            }
        }

        statusProviders = statusProvidersMap.values();
    }

    public static IStatusContentProvider getProvider( final Object target ) {
        if (!providersLoaded) {
            loadExtensions();
        }

        if (target != null) {
            String id = null;
            if (target instanceof IProject) {
                id = getPrimaryProjectScopeNature((IProject)target);
            } else if (target instanceof IResource && ModelUtilities.isModelFile((IResource)target)) {
                id = "Model";
            }

            // create a NewChildAction for every new child type
            Iterator iter = statusProviders.iterator();
            IStatusContentProvider nextProvider = null;

            while (iter.hasNext()) {
            	nextProvider = (IStatusContentProvider)iter.next();
                if (nextProvider.getId().equalsIgnoreCase(id)) {
                	currentFocusedObject = target;
                	currentStatusProvider = nextProvider;
                    return nextProvider;
                }
            }
        }

        currentStatusProvider = defaultStatusProvider;
        return defaultStatusProvider;
    }
    
    /**
     * Returns the current/active StatusProvider
     * 
     * @return
     */
    public static IStatusContentProvider getCurrentProvider() {
    	return currentStatusProvider;
    }

    private static String getPrimaryProjectScopeNature( IProject proj ) {

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
}
