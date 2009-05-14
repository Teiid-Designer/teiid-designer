/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;

import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.reader.StreamReader;
import com.metamatrix.modeler.core.writer.StreamWriter;

/**
 */
public class MetabaseToolKitPlugin /*extends Plugin*/ {
    
//    public static String PLUGIN_ID = ModelerCore.PLUGIN_ID;
    
//    private static Registry theRegistry;
    private static Map streamReaderExtensions;
    private static Map streamWriterExtensions;

//    static {
//        // Initialize the Registry with the default implementation
//        theRegistry = new FlatRegistry();
//    }

//    /**
//     * Constructor for MetabaseToolKitPlugin.
//     * @param arg0
//     */
//    public MetabaseToolKitPlugin(IPluginDescriptor arg0) {
//        super(arg0);
//    }
//    
//    /**
//     * Create a new Container instance by loading and activating the specified
//     * extension.  This method ensures that the Container instance has been
//     * registered with the Registry.
//     * @param containerExtensionID the complete ID of the extension that should
//     * be activated (e.g., "<code>com.mycompany.myPlugin.myContainer</code>");
//     * @return the new Container instance
//     * @throws ModelerCoreException if there is an error initializing the Container
//     * @throws CoreException if there is an error activating the specified
//     * extension.
//     */
//    public static Container createContainer( final String containerName, 
//                                              final String containerExtensionID ) 
//                                              throws ModelerCoreException, CoreException {
//        // Load the container with the extension ID ...
//        final Container container = (Container) PluginUtilities.createExecutableExtension(containerExtensionID,"class","name"); //$NON-NLS-1$ //$NON-NLS-2$
//        if ( container == null ) {
//            throw new ModelerCoreException(ModelerCore.Util.getString("MetabaseToolKitPlugin.Unable_to_find,_activate_and_load_container_extension___14") + containerExtensionID + "\""); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        // Set the name ...
//        container.setName(containerName);
//
//        // Ensure the container is registered with its name ...
//        final String name = container.getName();
//        if ( name != null && name.length() != 0 ) {
//            getRegistry().register(name,container);
//        }
//        
//        return container;
//    }
//    
//    /**
//     * Return the Registry.
//     * @return the Registry instance.
//     */
//    public static Registry getRegistry() {
//        return theRegistry;
//    }
    
    /**
     * Return an {@link MtkStreamReader} for the specified reader name.
     * @return the MtkStreamReader instance.
     */
    public static StreamReader createStreamReaderService(final String uniqueID) throws ModelerCoreException {
        if(uniqueID == null){
            ArgCheck.isNotNull(uniqueID,ModelerCore.Util.getString("MetabaseToolKitPlugin.The_unique_identifier_for_the_Service_may_not_be_null_24")); //$NON-NLS-1$
        }
        
        // Initialize the map of streamReader extensions
        if (streamReaderExtensions == null) {
            streamReaderExtensions = new HashMap();
            final String extensionID = ModelerCore.EXTENSION_POINT.STREAM_READER.UNIQUE_ID;
            loadExtensionMap(extensionID,streamReaderExtensions);
            
        }
        // Attempt to create an instance the streamReader extension by name
        return (StreamReader) createExtensionInstance(uniqueID,streamReaderExtensions);
    }
    
    /**
     * Return an {@link MtkStreamWriter} for the specified writer name.
     * @return the MtkStreamWriter instance.
     */
    public static StreamWriter createStreamWriterService(final String uniqueID) throws ModelerCoreException {
        if(uniqueID == null){
            ArgCheck.isNotNull(uniqueID,ModelerCore.Util.getString("MetabaseToolKitPlugin.The_unique_identifier_for_the_Service_may_not_be_null_26")); //$NON-NLS-1$
        }
        
        // Initialize the map of streamReader extensions
        if (streamWriterExtensions == null) {
            streamWriterExtensions = new HashMap();
            final String extensionID = ModelerCore.EXTENSION_POINT.STREAM_WRITER.UNIQUE_ID;
            loadExtensionMap(extensionID,streamWriterExtensions);
            
        }
        // Attempt to create an instance the streamReader extension by name
        return (StreamWriter) createExtensionInstance(uniqueID,streamWriterExtensions);
    }
    
    /**
     * Load the given Map with all extensions for the specified extension point.
     * Each extension will be loaded into the map using the extension label as
     * the key.
     * @param extensionPointID
     * @param extensionMap the map into which all extensions will be loaded
     */
    private static void loadExtensionMap(final String extensionPointID, final Map extensionMap) {
        if(extensionPointID == null){
            ArgCheck.isNotNull(extensionPointID, ModelerCore.Util.getString("MetabaseToolKitPlugin.The_extension_ID_may_not_be_null_28")); //$NON-NLS-1$
        }
        if(extensionMap == null){
            ArgCheck.isNotNull(extensionMap,ModelerCore.Util.getString("MetabaseToolKitPlugin.The_Map_reference_may_not_be_null_29")); //$NON-NLS-1$
        }
        
//System.err.println("loadExtensionMap for ID \""+extensionPointID+"\"");
        final IExtension[] extensions = PluginUtilities.getExtensions(extensionPointID);
        for (int i = 0; i < extensions.length; i++) {
            final IExtension extension = extensions[i];
            final String uniqueID = extension.getUniqueIdentifier();
//System.err.println("Loading extension with uniqueID \""+uniqueID+"\"");
            extensionMap.put(uniqueID,extension);
        }
    }
    
    /**
     * Load the given Map with all extensions for the specified extension point.
     * Each extension will be loaded into the map using the extension label as
     * the key.
     * @param extensionPointID
     * @param extensionMap the map into which all extensions will be loaded
     */
    private static Object createExtensionInstance(final String uniqueID, final Map extensionMap) throws ModelerCoreException {
        if(uniqueID == null){
            ArgCheck.isNotNull(uniqueID, ModelerCore.Util.getString("MetabaseToolKitPlugin.The_extension_uniqueID_may_not_be_null_30")); //$NON-NLS-1$
        }
        if(extensionMap == null){
            ArgCheck.isNotNull(extensionMap,ModelerCore.Util.getString("MetabaseToolKitPlugin.The_Map_reference_may_not_be_null_31")); //$NON-NLS-1$
        }
        
        // Attempt to lookup the extension by name
        IExtension extension = (IExtension) extensionMap.get(uniqueID);
        if (extension == null) {
            throw new ModelerCoreException(ModelerCore.Util.getString("MetabaseToolKitPlugin.Unable_to_find_an_extension_with_unique_ID___32")+uniqueID+"\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Activate the extension ...
        return createExecutableExtension(extension);
    }
    
    private static Object createExecutableExtension(IExtension extension) throws ModelerCoreException {
        if(extension == null){
            ArgCheck.isNotNull(extension,ModelerCore.Util.getString("MetabaseToolKitPlugin.The_IExtension_reference_may_not_be_null_34")); //$NON-NLS-1$
        }
        
        try {
            return PluginUtilities.createExecutableExtension(extension,"class","name"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (CoreException e) {
            throw new ModelerCoreException(e,ModelerCore.Util.getString("MetabaseToolKitPlugin.Error_creating_instance_of_extension_with_ID___37")+extension.getUniqueIdentifier()+"\""); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

//    /**
//     * Starts up this plug-in.
//     * <p>
//     * This method should be overridden in subclasses that need to do something
//     * when this plug-in is started.  Implementors should call the inherited method
//     * to ensure that any system requirements can be met.
//     * </p>
//     * <p>
//     * If this method throws an exception, it is taken as an indication that
//     * plug-in initialization has failed; as a result, the plug-in will not
//     * be activated; moreover, the plug-in will be marked as disabled and 
//     * ineligible for activation for the duration.
//     * </p>
//     * <p>
//     * Plug-in startup code should be robust. In the event of a startup failure,
//     * the plug-in's <code>shutdown</code> method will be invoked automatically,
//     * in an attempt to close open files, etc.
//     * </p>
//     * <p>
//     * Note 1: This method is automatically invoked by the platform 
//     * the first time any code in the plug-in is executed.
//     * </p>
//     * <p>
//     * Note 2: This method is intended to perform simple initialization 
//     * of the plug-in environment. The platform may terminate initializers 
//     * that do not complete in a timely fashion.
//     * </p>
//     * <p>
//     * Note 3: The class loader typically has monitors acquired during invocation of this method.  It is 
//     * strongly recommended that this method avoid synchronized blocks or other thread locking mechanisms,
//     * as this would lead to deadlock vulnerability.
//     * </p>
//     * <b>Clients must never explicitly call this method.</b>
//     *
//     * @exception CoreException if this plug-in did not start up properly
//     * @see org.eclipse.core.runtime.Plugin#startup()
//     */
//    public void startup() throws CoreException {
//        super.startup();
//        
//        // Look up the DEFAULT configuration to use ...
//        final IExtension configExtension = Platform.getPluginRegistry().getExtension(PLUGIN_ID, ModelerCore.EXTENSION_POINT.CONFIGURATION.ID, ModelerCore.EXTENSION.DEFAULT_CONFIG_ID);
//        final IConfigurationElement[] elements = configExtension.getConfigurationElements();
//
//        // Extract the information for the configuration ...
//        String registryExtensionID = ModelerCore.EXTENSION.FLAT_REGISTRY_ID;
//        for (int i = 0; i < elements.length; i++) {
//            final IConfigurationElement iConfigurationElement = elements[i];
//            if ( iConfigurationElement.getName().equals(ModelerCore.EXTENSION_POINT.REGISTRY.ID) ) { //$NON-NLS-1$
//                registryExtensionID = iConfigurationElement.getAttribute("id"); //$NON-NLS-1$
//            }
//        }
//        
//        // Activate the Registry extension ...
//        final Registry registry = (Registry) PluginUtilities.createExecutableExtension(registryExtensionID, "class", "name"); //$NON-NLS-1$ //$NON-NLS-2$
//        theRegistry = registry;
//
//    }
    
}
