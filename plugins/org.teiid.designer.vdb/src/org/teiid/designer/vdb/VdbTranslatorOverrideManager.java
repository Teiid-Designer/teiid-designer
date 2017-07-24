/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.util.Collection;
import java.util.Properties;

/**
 * @author blafond
 *
 */
public class VdbTranslatorOverrideManager {
	
	Vdb vdb;

	/**
	 * @param vdb
	 */
	public VdbTranslatorOverrideManager(Vdb vdb) {
		this.vdb = vdb;
	}
	
	/**
	 * @param entry
	 * @param props
	 */
	public void entryAdded(VdbModelEntry entry, Properties props) {
		// Need to do 2 things
		if( props == null || props.isEmpty() ) {
			return;
		}
		// 1) If the properties isn't empty, then we assume that a source model was added or synchronized
		// 2) Get the translator name from the source
		TranslatorOverride override = entry.getTranslatorOverride();
		if( override == null ) {
			override = getTranslatorOverride(entry.getSourceInfo());
		}
		String translatorName = null;
		
		if( override != null ) {
			translatorName = override.getName();
		}
		
		// 3) Get an existing translator overrides
		// 4) See if one matches
		// 4) Check if overrides exist for the sources translator type.
	}

    /**
     * Returns the current <code>TranslatorOverride</code> for this model
     * @return translator override. May be null.
     */
    public final TranslatorOverride getTranslatorOverride(VdbSourceInfo sourceInfo) {
    	if( !sourceInfo.isEmpty() ) {
        	Collection<TranslatorOverride> overrides = this.vdb.getTranslators();
        	for( TranslatorOverride to : overrides) {
        		for( VdbSource source : sourceInfo.getSources() ) {
        			String translatorName = source.getTranslatorName();
	        		if( translatorName != null && translatorName.toString().equalsIgnoreCase(to.getType()) ) {
	        			return to;
	        		}
        		}
        	}
    	}
    	
    	return null;
    }
    
    /*
     * Only called by synchronize method or vdb creation. Intent is to update the TO for a given model based on 
     * injected translator properties.
     * 
     * 1) if matching property found, set the new value
     * 2) if no matching property found, add a new one
     * 3) No way to tell if an OLD property needs to get removed though
     */
    String updateTranslatorOverrides(Properties props) {
    	// TODO: Update for Multi-Sources bindings
//    	if( this.sourceInfo.isMultiSource() ) {
//    		return null;
//    	}
//    	
//        // If only ONE property and it's "name", then ignore
//
//        if( props.size() == 1 && ((String)props.keySet().toArray()[0]).equalsIgnoreCase(VdbConstants.TRANSLATOR_NAME_KEY) ) {
//            return null;
//        }
//    	TranslatorOverride to = getTranslatorOverride();
//    	String oldTranslator = this.sourceInfo.getSource(0).getTranslatorName();
    	String newTranslator = null;
//    	if( to == null ) {
//    		String toName = null;
//    		if( !oldTranslator.startsWith(this.sourceInfo.getSource(0).getName()) ) {
//    			toName = this.sourceInfo.getSource(0).getName() + '_' + oldTranslator;
//    		}
//    		to = new TranslatorOverride(getVdb(), toName, oldTranslator, null);
//    		newTranslator = toName;
//    		this.sourceInfo.getSource(0).setTranslatorName(toName);
//    		getVdb().addTranslator(to);
//    	} else {
//    		newTranslator = to.getName();
//    	}
//    	
//    	TranslatorOverrideProperty[] toProps = to.getOverrideProperties();
//    	
//        Set<Object> keys = props.keySet();
//        for (Object nextKey : keys) {
//        	boolean existing = "name".equals(nextKey); //$NON-NLS-1$
//        	// Look through current TO props to see if already defined
//    		for( TranslatorOverrideProperty toProp : toProps ) {
//    			if( toProp.getDefinition().getId().equals(nextKey) ) {
//
//    				// This is an override case
//    				toProp.setValue(props.getProperty((String)nextKey));
//    				existing = true;
//    				break;
//    			}
//    		}
//
//    		if( !existing ) {
//    			to.addProperty(new TranslatorOverrideProperty(new TranslatorPropertyDefinition((String) nextKey, "dummy"), props.getProperty((String)nextKey))); //$NON-NLS-1$
//    		}
//    	}
//        
//        getVdb().setModified(this, MODEL_TRANSLATOR, oldTranslator, newTranslator);
        
        return newTranslator;
    }
}
