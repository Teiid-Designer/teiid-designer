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
 * 
 *
 */

public interface IExtensionPropertiesHandler {
	static final String EXTENSION_ID_PREFIX = "ext-id:"; //$NON-NLS-1$
	static final String EXTENSION_NAMEPSACE_PREFIX = "ext-namespace:"; //$NON-NLS-1$
	static final String EXTENSION_CND_PREFIX = "ext-cnd:"; //$NON-NLS-1$
	static final String PROPERTY_KEY_NAMESPACE_PREFIX = "ext-"; //$NON-NLS-1$
	
	/**
	 * Returns a list of {@link Properties} defined as model extensions for the target {@link EObject}
	 * 
	 * @param target the {@link EObject} target of the annotation containing the extended model properties.
	 * @return the extended model object properties
	 * @throws ModelerCoreException
	 */
	Properties getExtendedProperties(final EObject target)throws ModelerCoreException;
	
	boolean hasExtendedProperties(final EObject target)throws ModelerCoreException;
	
	/**
	 * 
	 * @param target
	 * @param props
	 * @return
	 */
	Properties getMissingDefaultProperties(final EObject target, final Properties props);
	
	/**
	 * 
	 * @param target
	 * @return
	 * @throws ModelWorkspaceException
	 */
	boolean isApplicable(final EObject target);
	
	boolean isApplicable(IResource resource);
	
	
	ExtendedModelObject getExtendedModelObject(final EObject target)throws ModelerCoreException;
	/**
	 * 
	 * @param key
	 * @return
	 */
	boolean isExtendedKey(final String key);
	
	String getID();
	
	String getDisplayName();
	
	void save(final EObject target, Collection<ModelObjectExtendedProperty> properties) throws ModelerCoreException;
}
