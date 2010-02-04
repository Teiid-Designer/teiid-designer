/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.util;

import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelStatusImpl;

/**
 * Util
 */
public class Util {

    public static CoreException newCoreException( final int errorCode, final String msg ) {
        return new ModelWorkspaceException(new ModelStatusImpl(errorCode,msg));
    }
    
    public static CoreException newCoreException( final int errorCode, final Throwable t, final String msg ) {
        return new ModelWorkspaceException(new ModelStatusImpl(errorCode,t,msg));
    }
    
    /**
     * Load the given Map with all extensions for the specified extension point.
     * Each extension will be loaded into the map using the extension label as
     * the key.
     * @param extensionPointID
     * @param extensionMap the map into which all extensions will be loaded
     */
    public static void loadExtensionMap(final String extensionPointID, final Map extensionMap) {
        if (extensionPointID == null) {
            final String msg = ModelerCore.Util.getString("Util.The_extension_ID_may_not_be_null_1"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (extensionMap == null) {
            final String msg = ModelerCore.Util.getString("Util.The_Map_reference_may_not_be_null_2"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
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
    public static Object createExtensionInstance(final String uniqueID, final Map extensionMap) throws CoreException {
        if (uniqueID == null) {
            final String msg = ModelerCore.Util.getString("Util.The_extension_uniqueID_may_not_be_null_3"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (extensionMap == null) {
            final String msg = ModelerCore.Util.getString("Util.The_Map_reference_may_not_be_null_4"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        
        // Attempt to lookup the extension by name
        IExtension extension = (IExtension) extensionMap.get(uniqueID);
        if (extension == null) {
            throw newCoreException(1,ModelerCore.Util.getString("Util.Unable_to_find_an_extension_with_unique_ID___5")+uniqueID+"\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Activate the extension ...
        return createExecutableExtension(extension);
    }
    
    public static Object createExecutableExtension(IExtension extension) throws CoreException {
        if (extension == null) {
            final String msg = ModelerCore.Util.getString("Util.The_IExtension_reference_may_not_be_null_7"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        
        try {
            return PluginUtilities.createExecutableExtension(extension,"class","name"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (CoreException e) {
            throw newCoreException(1,ModelerCore.Util.getString("Util.Error_creating_instance_of_extension_with_ID___10")+extension.getUniqueIdentifier()+"\""); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
	/**
	 * Return the ModelAnnotation for the provided Resource
	 * @param resource the provided resource
	 * @return the modelAnnotation - null if not found.
	 */
	public static ModelAnnotation getModelAnnotation(final Resource resource) {
		ModelAnnotation modelAnnotation = null;
		if(resource instanceof MtkXmiResourceImpl){
			modelAnnotation = ((MtkXmiResourceImpl)resource).getModelAnnotation();
		}else{
			final Iterator roots = resource.getContents().iterator();
			while (roots.hasNext()) {
				Object next = roots.next();
				if(next instanceof ModelAnnotation){
					modelAnnotation = (ModelAnnotation)next;
					break;
				}				
			}
		}
		return modelAnnotation;
	}

}
