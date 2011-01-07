package com.metamatrix.modeler.internal.jdbc.relational.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.metamodels.relational.util.RelationalTypeMappingImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.relational.JdbcRelationalPlugin;
import com.metamatrix.modeler.jdbc.relational.RelationalModelProcessor;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * This class provides a static way to manage the extension contributions for the <code>RelationalModelProcessor</code>.
 * 
 * This includes utility methods for storing the list of display labels for the contributions for the UI and methods
 * to return a model processor based on the extension's display label as well as processor type (i.e. translator)
 * 
 *
 */

public class JdbcModelProcessorManager {
	
    /**
     * The identifiers for all ModelerCore extension points
     */
    public static class EXTENSION_POINT {
        /** Extension point for the model validation service implementation */
        public static class MODEL_PROCESSOR {
            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
            public static class ELEMENTS {
                public static final String PROCESSOR_CLASS = "processorClass"; //$NON-NLS-1$
                public static final String PROCESSOR_TYPE = "processorType"; //$NON-NLS-1$
            }

            public static final String ID = "modelProcessor"; //$NON-NLS-1$

            public static final String UNIQUE_ID = JdbcRelationalPlugin.PLUGIN_ID + DELIMITER + ID;
        }
    }
    
    /**
     * Delimiter used by extension/extension point declarations
     */
    public static final String DELIMITER = "."; //$NON-NLS-1$
    
    public static final String JDBC_DEFAULT = "JDBC (default)"; //$NON-NLS-1$

    public static final String JDBC_TYPE = "JDBC"; //$NON-NLS-1$
    
    private static Map<String, IExtension> processorExtensionMap;
    
    private static Map<String, String> processorNameMap;
    
    private static boolean processorsLoaded = false;
    
    /**
     * Create a new {@link RelationalModelProcessor Relational model processor} that can transform
     * {@link com.metamatrix.modeler.jdbc.metadata.JdbcDatabase JDBC metadata} into a
     * {@link com.metamatrix.metamodels.relational.RelationalPackage Relational} model.
     * <p>
     * This method attempts to find the model processor that is best suited for the supplied source. It does so by searching for
     * the first <code>com.metamatrix.modeler.jdbc.relational.modelProcessor</code> extension that is defined to work with the
     * JdbcSource's {@link JdbcSource#getDriverClass()}
     * </p>
     * 
     * @param source the JdbcSource; may be null if the default processor should be used
     * @return the new model processor
     */
    public static RelationalModelProcessor createRelationalModelProcessor( final JdbcSource source) {
        return createRelationalModelProcessor(source, RelationalTypeMappingImpl.getInstance(), JDBC_TYPE);
    }
    
    /**
     * Create a new {@link RelationalModelProcessor Relational model processor} that can transform
     * {@link com.metamatrix.modeler.jdbc.metadata.JdbcDatabase JDBC metadata} into a
     * {@link com.metamatrix.metamodels.relational.RelationalPackage Relational} model.
     * <p>
     * This method attempts to find the model processor that is best suited for the supplied source. This method requires
     * a processor type that matches one of the contribution extensions and is akin to translator type. These include
     * types like "oracle", "sybase", etc.  The helper method JdbcTranslatorHelper.getModelProcessorType() can be 
     * used to discover the processor type given a Connection Profile. 
     * </p>
     * 
     * @param source the JdbcSource; may be null if the default processor should be used
     * @param mapping the RelationalTypeMapping that should be used
     * @param processorType
     * @return the new model processor
     */
    public static RelationalModelProcessor createRelationalModelProcessor( final JdbcSource source,
                                                                           final RelationalTypeMapping mapping,
                                                                           final String processorType) {
        RelationalModelProcessor processor = getProcessor(processorType);

        // Attempt to set the type mapping
        if (mapping != null && processor instanceof RelationalModelProcessorImpl) {
            ((RelationalModelProcessorImpl)processor).setTypeMapping(mapping);
        }
        
        return processor;
    }

    /**
     * Create a new {@link RelationalModelProcessor Relational model processor} that can transform
     * {@link com.metamatrix.modeler.jdbc.metadata.JdbcDatabase JDBC metadata} into a
     * {@link com.metamatrix.metamodels.relational.RelationalPackage Relational} model.
     * <p>
     * @param source
     * @param processorType
     * @return the new model processor
     */
    public static RelationalModelProcessor createRelationalModelProcessor( final JdbcSource source, final String processorType) {
        return createRelationalModelProcessor(source, RelationalTypeMappingImpl.getInstance(), processorType);
    }
    
    /**
     * Return the cached list of Names for the contributed  {@link RelationalModelProcessor Relational model processor}.
     * @return the list of processor names
     */
    public static Collection<String> getMetadataProcessorNames() {
    	loadProcessors();
    	
    	return processorNameMap.values();
    }
    
    /**
     * Return the cached list of valid types for the contributed  {@link RelationalModelProcessor Relational model processor}.
     * @return the list of processor types
     */
    public static Collection<String> getMetadataProcessorTypes() {
    	loadProcessors();
    	
    	return processorExtensionMap.keySet();
    }
    
    private static RelationalModelProcessor getProcessor(IExtension extension) {
        final IConfigurationElement[] elems = extension.getConfigurationElements();

    	Object result = null;
    	
        for (int j = 0; j < elems.length; j++) {
            final IConfigurationElement elem = elems[j];
            final String elemName = elem.getName();
            if (elemName == null) {
                continue;
            }
            if (elemName.equals(JdbcModelProcessorManager.EXTENSION_POINT.MODEL_PROCESSOR.ELEMENTS.PROCESSOR_CLASS)) {
                final String attribName = JdbcModelProcessorManager.EXTENSION_POINT.MODEL_PROCESSOR.ATTRIBUTES.NAME;
                try {
                	result = elem.createExecutableExtension(attribName);
                } catch (Throwable e) {
                    JdbcRelationalPlugin.Util.log(e);
                }
            }
        }
        if( result instanceof RelationalModelProcessor ) {
        	return (RelationalModelProcessor)result;
        }
        return null;
    }
    
    private static RelationalModelProcessor getProcessor(String key) {
    	loadProcessors();
    	
    	IExtension extension = processorExtensionMap.get(key);
    	
    	if( extension != null ) {
	    	RelationalModelProcessor processor = getProcessor(extension);
	    	
	    	if( processor != null) {
	    		return (RelationalModelProcessor)processor;
	    	}
    	}
    	
    	return new RelationalModelProcessorImpl();
    }
    
    /**
     * Returns a valid processor name given a processor type. If no unique name is found, it will return the default
     * JDBC type
     * 
     * @param type
     * @return the processor's display name
     */
    public static String getProcessorNameWithType(String type) {
    	loadProcessors();
    	
    	for( String nextKey : processorNameMap.keySet() ) {
    		String nextValue = (String)processorNameMap.get(nextKey);
    		if( nextKey.equalsIgnoreCase(type) ) {
    			return nextValue;
    		}
    	}
    	
    	return JDBC_DEFAULT;
    }
    
    /**
     * Returns a valid processor type given the processor's display name
     * 
     * @param nameKey
     * @return the processors type
     */
    public static String getProcessorTypeWithName(String nameKey) {
    	loadProcessors();
    	
    	for( String nextKey : processorNameMap.keySet() ) {
    		String nextValue = (String)processorNameMap.get(nextKey);
    		if( nextValue.equalsIgnoreCase(nameKey) ) {
    			return nextKey;
    		}
    	}
    	
    	return JDBC_TYPE;
    }

    private static void loadProcessors() {
    	if( !processorsLoaded ) {
    		processorExtensionMap = new HashMap<String, IExtension>();
    		processorNameMap = new HashMap<String, String>();
    		
	        final IExtension[] extensions = PluginUtilities.getExtensions(JdbcModelProcessorManager.EXTENSION_POINT.MODEL_PROCESSOR.UNIQUE_ID);
	        for (int i = 0; i < extensions.length; i++) {
	            final IExtension extension = extensions[i];
	            
	            final IConfigurationElement[] elems = extension.getConfigurationElements();
            	String processorType = null;

            	String processorName = extension.getLabel();
            	
	            for (int j = 0; j < elems.length; j++) {
	                final IConfigurationElement elem = elems[j];
	                final String elemName = elem.getName();
	                if (elemName == null) {
	                    continue;
	                }
	                if (elemName.equals(JdbcModelProcessorManager.EXTENSION_POINT.MODEL_PROCESSOR.ELEMENTS.PROCESSOR_TYPE)) {
	                     processorType = elem.getAttribute(ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER.ATTRIBUTES.NAME);
	                }
	            }
	            
                if( processorName != null && processorType != null ) {
                	processorExtensionMap.put(processorType, extension);
                	processorNameMap.put(processorType, processorName);
                }
                
                
	        }
	        
	        processorNameMap.put(JDBC_TYPE, JDBC_DEFAULT);
	        		
	        processorsLoaded = true;
    	}

    }

}
