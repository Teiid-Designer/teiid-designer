package org.teiid.designer.runtime.connection;

import java.util.Properties;
import java.util.Set;

import org.teiid.designer.datatools.connection.IConnectionInfoHelper;

import com.metamatrix.core.util.CoreArgCheck;

public class TranslatorProfileFactory {
	
	public TranslatorProfileFactory() {
		super();
	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @param id
	 * @param props
	 * @return
	 */
	public TranslatorProfile createTeiidTranslator(String name, String type, Properties props) {
		CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(type, "type"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(props, "props"); //$NON-NLS-1$
		
		TranslatorProfile profile = new TranslatorProfile(name, type, props);
		return profile;
	}
	
	
	/**
	 * Returns the collective properties of a <code>ConnectionProfile</code> to include name, description and provider id
	 * in addition to it's base properties. These properties are also prefixed with a custom namespace for storage in
	 * a model resource "annotation"
	 * 
	 * @param connectionProfile the connection profile
	 * @return the name-spaced properties for the translator profile
	 */
	public Properties getNamespacedProperties(TranslatorProfile translatorProfile) {
		CoreArgCheck.isNotNull(translatorProfile, "translatorProfile"); //$NON-NLS-1$
		
		Properties translatorProps = translatorProfile.getProperties();
		Properties allProps = new Properties();
		allProps.put(IConnectionInfoHelper.TRANSLATOR_NAMESPACE + IConnectionInfoHelper.TRANSLATOR_NAME_KEY, translatorProfile.getName());
		allProps.put(IConnectionInfoHelper.TRANSLATOR_NAMESPACE + IConnectionInfoHelper.TRANSLATOR_TYPE_KEY, translatorProfile.getType());
		
		Set<Object> keys = translatorProps.keySet();
		for(Object  nextKey : keys ) {
			allProps.put(IConnectionInfoHelper.TRANSLATOR_NAMESPACE + nextKey, translatorProps.get(nextKey));
		}
		return allProps;
	}
}
