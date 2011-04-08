/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.manager;

import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.ModelerCoreException;


/**
 * This interface defined the methods required to handle the new Model Extension properties framework.
 *
 */

public interface IExtensionPropertiesHandler {
	static final String EXTENSION_CND_PREFIX 			= "ext-cnd:"; //$NON-NLS-1$
	/**
	 * The following three constants should be used to prepend the keys for Model Extension Definitions tags
	 * 
	 * Example:
	 * 
	 *      <tags xmi:uuid="mmuuid:f502798d-9b75-49ff-99fe-e54e66f7c085" key="ext-id:salesforce" value="org.teiid.designer.model.extension.salesforce"/>
     * 		<tags xmi:uuid="mmuuid:54887ab5-6c9d-4a1e-9af7-bc9843290bc4" key="ext-namespace:salesforce" value="http://org.teiid.designer/metamodels/Salesforce"/>
     * 		<tags xmi:uuid="mmuuid:db555742-0930-42cd-b7d1-3b8e5e806893" key="ext-cnd:salesforce" value="&lt;salesforce='http://org.teiid.designer/metamodels/Salesforce'>. . . . - salesforce:picklistValues (string) multiple&#xa;"/>
	 */
	static final String EXTENSION_ID_PREFIX 			= "ext-id:"; //$NON-NLS-1$
	static final String EXTENSION_NAMESPACE_PREFIX 		= "ext-namespace:"; //$NON-NLS-1$
	
	/**
	 * This prefix should be prepended to ANY model extension property key
	 * 
	 * Example Tag entry: <tags xmi:uuid="mmuuid:e55d5200-41f6-4f65-9269-1a282603effc" key="ext-salesforce:Supports Query" value="true"/>
	 *   
	 */
	static final String PROPERTY_KEY_NAMESPACE_PREFIX 	= "ext-"; //$NON-NLS-1$
	
	/**
	 * Simple getter for UI display name/label for the handler
	 * @return the handler's display name
	 */
	String getDisplayName();
	
	/**
	 * Returns an {@link ExtendedModelObject} based on input target {@link EObject}. The {@link ExtendedModelObject} contains
	 * a list of extended {@link ModelObjectExtendedProperty}'s
	 * 
	 * @param target the {@link EObject} target
	 * @return the {@link ExtendedModelObject}
	 * @throws ModelerCoreException
	 */
	ExtendedModelObject getExtendedModelObject(final EObject target)throws ModelerCoreException;
	
	/**
	 * Returns a list of {@link Properties} defined as model extensions for the target {@link EObject}
	 * 
	 * @param target the {@link EObject} target of the annotation containing the extended model properties.
	 * @return the extended model object  {@link Properties}
	 * @throws ModelerCoreException
	 */
	Properties getExtendedProperties(final EObject target)throws ModelerCoreException;
	
	/**
	 * Simple getter of the handler ID string value
	 * @return the handler ID
	 */
	String getID();
	
	
	/**
	 * Returns a list of default {@link Properties} defined for a target and NOT included in the incoming props list.
	 * 
	 * @param target the {@link EObject} target of the annotation containing the extended model properties
	 * @param props the existing properties
	 * 
	 * @return the list of missing default extended model object {@link Properties}
	 */
	Properties getMissingDefaultProperties(final EObject target, final Properties props);
	
	/**
	 * Simple method to determine of a handler exists that is applicable to the input target {@link EObject}
	 * 
	 * @param target the {@link EObject} target
	 * @return true if a handler exists that is applicable to the input target {@link EObject}
	 * 
	 * @throws ModelWorkspaceException
	 */
	boolean isApplicable(final EObject target);
	
	/**
	 * Simple method to determine of a handler exists that is applicable to the input target {@link IResource}
	 * 
	 * @param target the {@link IResource} target
	 * @return true if a handler exists that is applicable to the input target {@link IResource}
	 * 
	 * @throws ModelWorkspaceException
	 */
	boolean isApplicable(IResource resource);
	
	/**
	 * Simple method determines if a tag's key value is a valid key known by at least one extension handler
	 * 
	 * @param key the key value
	 * @return true if handler exists that recognizes the key. false otherwise
	 */
	boolean isExtendedKey(final String key);
	
	/**
	 * For the given {@link EObject} target, clear current tags and store/save given {@link ModelObjectExtendedProperty}'s 
	 * as tags for the target.
	 * 
	 * @param target
	 * @param properties
	 * @throws ModelerCoreException
	 */
	void save(final EObject target, Collection<ModelObjectExtendedProperty> properties) throws ModelerCoreException;
}
