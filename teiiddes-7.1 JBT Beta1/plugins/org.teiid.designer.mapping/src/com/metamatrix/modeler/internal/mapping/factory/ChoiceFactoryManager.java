/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.mapping.PluginConstants;
import com.metamatrix.modeler.mapping.factory.IChoiceFactory;

/**
 * ChoiceFactoryManager :: point="com.metamatrix.modeler.mapping.choiceObjectHandler">
 */
public class ChoiceFactoryManager implements PluginConstants, PluginConstants.ExtensionPoints.ChoiceObjectHandler {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Used in logging debug messages. */
    private static final Class CLASS = ChoiceFactoryManager.class;

    /** Localization prefix found in properties file key words. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(CLASS);

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    // /////////////////////////////////////////////////////////////////////////////////////////////

    static {
        buildChoiceHanderMaps();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Map used to create a new instance of a Choice Factory upon request. */
    private static Map choiceHandlerMap; // key=factory class (as string); value=factory class (as executable)

    /** Don't allow construction. */
    private ChoiceFactoryManager() {
    }

    private static void buildChoiceHanderMaps() {
        choiceHandlerMap = new HashMap();

        // get the ModelObjectActionContributor extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, ID);

        // get the all extensions to the ModelObjectActionContributor extension point
        IExtension[] extensions = extensionPoint.getExtensions();

        if (extensions.length > 0) {
            // for each extension get their contributor
            for (int i = 0; i < extensions.length; i++) {
                IConfigurationElement[] elements = extensions[i].getConfigurationElements();
                Object extension = null;
                String sFactoryClass = null;

                for (int j = 0; j < elements.length; j++) {
                    try {

                        extension = elements[j].createExecutableExtension(FACTORY_CLASS);

                        if (extension instanceof IChoiceFactory) {

                            choiceHandlerMap.put(sFactoryClass, extension);

                        } else {
                            Util.log(IStatus.ERROR, Util.getString(PREFIX + "invalidChoiceHandlerMapperClass", //$NON-NLS-1$
                                                                   new Object[] {extension.getClass().getName()}));
                        }
                    } catch (Exception theException) {
                        Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "loadingChoiceHandlerProblem", //$NON-NLS-1$
                                                                             new Object[] {elements[j].getAttribute(CLASSNAME)}));
                    }
                }
            }
        }
    }

    /**
     * Retrieves a choice factory for the given object, if there is one.
     * 
     * @param theMetamodelUri the metamodel URI whose mapper is being requested
     * @return the appropriate choice factory
     */
    public static IChoiceFactory getChoiceFactory( EObject eo ) {
        //        System.out.println( "[ChoiceFactoryManager.getChoiceFactory] TOP" ); //$NON-NLS-1$  
        //
        //        System.out.println( "[ChoiceFactoryManager.getChoiceFactory] choiceHandlerMap.size() is: " + choiceHandlerMap.size() ); //$NON-NLS-1$  

        CoreArgCheck.isNotNull(eo);

        Iterator it = choiceHandlerMap.values().iterator();

        while (it.hasNext()) {
            IChoiceFactory icf = (IChoiceFactory)it.next();

            if (icf.supports(eo)) {
                return icf;
            }
        }

        return null;
    }

}
