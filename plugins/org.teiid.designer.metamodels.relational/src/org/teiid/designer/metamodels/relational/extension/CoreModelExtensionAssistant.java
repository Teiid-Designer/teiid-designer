/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.metamodels.relational.extension;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.metamodels.core.ModelAnnotation;

/**
 * @since 8.0
 *
 */
public class CoreModelExtensionAssistant  extends EmfModelObjectExtensionAssistant {

    private static String getPropertyId(final String propName) {
        return ModelExtensionPropertyDefinition.Utils.getPropertyId(CoreModelExtensionConstants.NAMESPACE_PROVIDER,
                                                                    propName);
    }

    private enum PropertyName {

        LOCKED(getPropertyId("locked")), //$NON-NLS-1$
        VDB_NAME(getPropertyId("vdb-name")), //$NON-NLS-1$
        VDB_VERSION(getPropertyId("vdb-version")), //$NON-NLS-1$
        DIAGRAM_LOCKED(getPropertyId("diagram-locked")); //$NON-NLS-1$

        public static boolean same(final PropertyName propName,
                                   final String value) {
            return propName.toString().equals(value);
        }

        private final String propName;

        private PropertyName(final String propName) {
            this.propName = propName;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return this.propName;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant#getPropertyDefinition(java.lang.Object, java.lang.String)
     */
    @Override
    protected ModelExtensionPropertyDefinition getPropertyDefinition(final Object modelObject,
                                                                     final String propId) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);

        // make sure there is a property definition first
        final ModelExtensionPropertyDefinition propDefn = super.getPropertyDefinition(modelObject, propId);

        if (propDefn != null) {
            // must be a model Annotation to have these properties
        	if (modelObject instanceof ModelAnnotation) {
	            if (PropertyName.same(PropertyName.LOCKED, propId) ||
	            	PropertyName.same(PropertyName.VDB_NAME, propId) ||
	            	PropertyName.same(PropertyName.VDB_VERSION, propId) ||
	            	PropertyName.same(PropertyName.DIAGRAM_LOCKED, propId)) {
	                return propDefn;
	            }

                // All other EObjects should not have the core properties
                return null;
            }
        }

        // property definition not found
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant#setPropertyValue(java.lang.Object, java.lang.String, java.lang.String)
     */
    @Override
    public void setPropertyValue(final Object modelObject,
                                 final String propId,
                                 final String newValue) throws Exception {
        super.setPropertyValue(modelObject, propId, newValue);
    }
    
    /**
     * @param modelResource the model resource
     * @return if locked property exists and is TRUE
     * @throws Exception if problem finding model annotation object
     */
    public boolean isModelLocked(final ModelResource modelResource) throws Exception {
    	if( modelResource.getModelAnnotation() != null) {
    		String locked = super.getPropertyValue(modelResource.getModelAnnotation(), PropertyName.LOCKED.toString());
    		return Boolean.parseBoolean(locked);
    	}
    	
    	return false;
    }
    
    /**
     * @param modelResource the model resource
     * @return vdb name property if exists
     * @throws Exception if problem finding model annotation object
     */
    public String getVdbName(final ModelResource modelResource) throws Exception {
    	if( modelResource.getModelAnnotation() != null) {
    		String name = super.getPropertyValue(modelResource.getModelAnnotation(), PropertyName.VDB_NAME.toString());
    		return name;
    	}
    	
    	return null;
    }
    
    /**
     * @param modelResource the model resource
     * @return vdb name property if exists
     * @throws Exception if problem finding model annotation object
     */
    public String getVdbVersion(final ModelResource modelResource) throws Exception {
    	if( modelResource.getModelAnnotation() != null) {
    		String version = super.getPropertyValue(modelResource.getModelAnnotation(), PropertyName.VDB_VERSION.toString());
    		return version;
    	}
    	
    	return null;
    }
    
    /**
     * @param modelResource the model resource
     * @return if vdb name property exists then TRUE
     * @throws Exception if problem finding model annotation object
     */
    public boolean isVdbSourceModel(final ModelResource modelResource) throws Exception {
    	if( modelResource.getModelAnnotation() != null) {
    		String name = super.getPropertyValue(modelResource.getModelAnnotation(), PropertyName.VDB_NAME.toString());
    		return name != null;
    	}
    	
    	return false;
    }
}