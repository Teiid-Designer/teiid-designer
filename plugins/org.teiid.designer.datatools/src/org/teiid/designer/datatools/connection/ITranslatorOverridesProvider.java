package org.teiid.designer.datatools.connection;

import java.util.Properties;

import org.teiid.designer.core.workspace.ModelResource;

/**
 * Interface to provide source-persisted properties for Translator Overrides
 * 
 * @author blafond
 *
 */
public interface ITranslatorOverridesProvider {
    
	/**
	 * Replace all translator override properties for the given <code>ModelResource</code>
	 * 
	 * @param modelResource the model
	 * @param properties the properties
	 */
    public abstract void replaceTranlatorOverrideProperties( ModelResource modelResource, Properties properties);
    
    /**
     * Clear all translator override properties for the given <code>ModelResource</code>
     * 
     * @param modelResource the model
     */
    public abstract void clearTranslatorOverrideProperties(ModelResource modelResource);
    
    /**
     * Return all translator override properties for the given <code>ModelResource</code>
     * 
     * @param modelResource model
     * @return the properties
     */
    public abstract Properties getTranslatorOverrideProperties( ModelResource modelResource);
}
