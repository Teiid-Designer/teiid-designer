package com.metamatrix.modeler.internal.core.workspace;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.emf.common.util.EMap;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;


/**
 * This class is intended to provide a method of injecting additional info and properties into a Model.
 * 
 * Basically it provides access to an Annotation whose reference is the ModelAnnotation EObject.
 * Annotations provide for a "tag" map, equivalent to a EObject Properties() object.
 * 
 * Users of this class can add specific name-space key'd Properties to the model for persistence and retrieval at a later time.
 * 
 *
 */


public class ResourceAnnotationHelper {
	
	

	public ResourceAnnotationHelper() {
		super();
	}

	private static String getNamespace(final String str) {
		CoreArgCheck.isNotNull(str, "str"); //$NON-NLS-1$
		
		int semiColonIndex = str.indexOf(':') + 1;
		if( semiColonIndex > 0 ) {
			return str.substring(0, semiColonIndex);
		}
		
		return null;
	}
	
	/**
	 * Retrieves the tagged resource <code>Annotation</code> object referenced to a <code>ModelResource</code>'s <code>ModelAnnotation</code>
	 * @param modelResource the <code>ModelResource</code>. may not be null
	 * @param forceCreate forces creation of the annotation if it does not exist.
	 * @return the <code>Annotation</code>
	 * @throws ModelWorkspaceException
	 */
	public Annotation getResourceAnnotation(final ModelResource modelResource, final boolean forceCreate) throws ModelWorkspaceException {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		Annotation annotation = null;
		
		if( modelResource.getAnnotations() != null && modelResource.getModelAnnotation() != null ) {
			annotation = modelResource.getAnnotations().getAnnotation(modelResource.getModelAnnotation());
			if( annotation == null && forceCreate ) {
				annotation = ModelResourceContainerFactory.createNewAnnotation(modelResource.getModelAnnotation(), modelResource.getEmfResource());
		        
				ModelContents contents = ModelerCore.getModelEditor().getModelContents(modelResource);
				AnnotationContainer ac = contents.getAnnotationContainer(false);
		        if( ac != null )
		            annotation.setAnnotationContainer(ac);
			}
		}
		return annotation;
	}
	
	/**
	 * Retrieves the value of an object stored on an <code>Annotation</code> in the tags map based on the input key
	 * 
	 * @param modelResource the Model Resource. may not be null;
	 * @param key the key for the mapped value. may not be null;
	 * @return the object value stored in the annotation's tags
	 * @throws ModelWorkspaceException
	 */
	public Object getPropertyValue(final ModelResource modelResource, final String key) throws ModelWorkspaceException {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(key, "key"); //$NON-NLS-1$
		Annotation annotation = getResourceAnnotation(modelResource, false);
		if( annotation != null ) {
			return annotation.getTags().get(key);
		}
		return null;
	}
	

	/**
	 * Returns all properties who's keys are prefixed with the given name-space prefix
	 * 
	 * @param modelResource
	 * @param namespacePrefix
	 * @return the properties (never <code>null</code>)
	 * @throws ModelWorkspaceException
	 */
	public Properties getProperties(final ModelResource modelResource, final String namespacePrefix) throws ModelWorkspaceException {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(namespacePrefix, "namespacePrefix"); //$NON-NLS-1$
		Properties props = new Properties();

		Annotation annotation = getResourceAnnotation(modelResource, false);
		
		if( annotation != null ) {
			EMap tags = annotation.getTags();
			Set<Object> keys = tags.keySet();
			for(Object  nextKey : keys ) {
				String namespace = getNamespace((String)nextKey);
				if( namespace != null && namespace.equals(namespacePrefix)) {
					props.put(nextKey, tags.get(nextKey));
				}
			}
		}
		
		return props;
	}
	
	public Properties getAllProperties(final ModelResource modelResource) throws ModelWorkspaceException {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		Properties props = new Properties();

		Annotation annotation = getResourceAnnotation(modelResource, false);
		if( annotation != null ) {
			EMap tags = annotation.getTags();
			Set<Object> keys = tags.keySet();
			for(Object  nextKey : keys ) {
				props.put(nextKey, tags.get(nextKey));
			}
		}
		return props;
	}
	

	public void removeProperties(final ModelResource modelResource, final String namespacePrefix) throws ModelWorkspaceException {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(namespacePrefix, "namespacePrefix"); //$NON-NLS-1$

		Annotation annotation = getResourceAnnotation(modelResource, false);
		
		if( annotation != null ) {
			EMap tags = annotation.getTags();
			Set<Object> keys = new HashSet(tags.keySet());
			for(Object  nextKey : keys ) {
				String namespace = getNamespace((String)nextKey);
				if( namespace != null && namespace.equals(namespacePrefix)) {
					tags.remove(nextKey);
				}
			}
		}
	}
	
	/**
	 * Sets the value of an object stored on an <code>Annotation</code> in the tags map based on the input key
	 * 
	 * @param modelResource the Model Resource. may not be null;
	 * @param key the key for the mapped value. may not be null;
	 * @param value the object value to store in the annotation
	 * @throws ModelWorkspaceException
	 */
	public void setProperty(final ModelResource modelResource, final String key, final Object value) throws ModelWorkspaceException {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(key, "key"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(value, "value"); //$NON-NLS-1$
		Annotation annotation = getResourceAnnotation(modelResource, true);
		
		annotation.getTags().put(key, value);
	}
	
	public void setProperties(final ModelResource modelResource, final Properties props) throws ModelWorkspaceException {
		CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(props, "props"); //$NON-NLS-1$
		
		Annotation annotation = getResourceAnnotation(modelResource, true);
		Set<Object> keys = props.keySet();
		for(Object  nextKey : keys ) {
			annotation.getTags().put(nextKey, props.get(nextKey));
		}
	}
}
