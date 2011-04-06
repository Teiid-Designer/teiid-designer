/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.manager;

import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * Structural object to hold state of both a target {@link EObject} it's resource, it's extended properties
 * handler and it's extended properties.
 */
public class ExtendedModelObject {
	
	private Collection<ModelObjectExtendedProperty> properties;
	
	private EObject eObject;
	
	private String name;
	
	private IResource resource;
	
	private IExtensionPropertiesHandler extensionPropertiesHandler;

	/**
	 * Primary constructor for {@link ExtendedModelObject}
	 * 
	 * @param extensionPropertiesHandler the extension properties handler
	 * @param resource the file resource containing the {@link EObject}
	 * @param eObject the target {@link EObject} 
	 * @param name the name of the {@link EObject} for display purposes
	 * @param properties the list of property objects
	 */
	public ExtendedModelObject(IExtensionPropertiesHandler extensionPropertiesHandler, 
								IResource resource, 
								EObject eObject, 
								String name, 
								Collection<ModelObjectExtendedProperty> properties) {
		super();
		this.extensionPropertiesHandler = extensionPropertiesHandler;
		this.resource = resource;
		this.properties = properties;
		this.eObject = eObject;
		this.name = name;
	}
	
	/**
	 * 
	 * @return a {@link Collection} of {@link ModelObjectExtendedProperty}s
	 */
	public Collection<ModelObjectExtendedProperty> getProperties() {
		return this.properties;
	}

	/**
	 * @return the target {@link EObject}
	 */
	public EObject getModelObject() {
		return this.eObject;
	}

	/**
	 * 
	 * @return the target {@link EObject}'s name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * 
	 * @return the {@link IResource} containing the target {@link EObject}
	 */
	public IResource getResource() {
		return this.resource;
	}

	/**
	 * Convenience method used to save the current properties of the extended model object.
	 * 
	 * @throws ModelerCoreException
	 */
	public void saveChanges() throws ModelerCoreException {
		this.extensionPropertiesHandler.save(eObject, this.properties);
	}
	
	/**
	 * 
	 * @return the name of the extension properties handler for display purposes
	 */
	public String getHandlerName() {
		return this.extensionPropertiesHandler.getDisplayName();
	}
}
