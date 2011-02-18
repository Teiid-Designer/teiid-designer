/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.vdb.edit.VdbGenerationContext;
import com.metamatrix.vdb.edit.VdbGenerationContext.ModelType;

/**
 * @since 4.2
 */
public class VdbGenerationModelHelper implements VdbGenerationContext.ModelHelper {

    private final Map modelNameByResource;
    private final Map modelPathByResource;
    private final Map modelVisibilityByResource;

    public VdbGenerationModelHelper( final Map modelNameByResource,
                                     final Map modelPathByResource,
                                     final Map modelVisibilityByResource ) {
        ArgCheck.isNotNull(modelNameByResource);
        ArgCheck.isNotNull(modelPathByResource);
        this.modelNameByResource = modelNameByResource;
        this.modelPathByResource = modelPathByResource;
        this.modelVisibilityByResource = modelVisibilityByResource;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelObjectHelper#getUuid(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public String getUuid( Resource model ) {
        ArgCheck.isNotNull(model);
        final ObjectID id = getObjectId(model);
        return id.toString();
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelObjectHelper#getUuid(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public ObjectID getObjectId( Resource model ) {
        if (model instanceof EmfResource) {
            final EmfResource emfResource = (EmfResource)model;
            final ModelAnnotation modelAnn = emfResource.getModelAnnotation();
            if (modelAnn != null) {
                return ModelerCore.getObjectId(modelAnn);
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelHelper#getName(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public String getName( Resource model ) {
        return (String)this.modelNameByResource.get(model);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelHelper#getPath(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public String getPath( Resource model ) {
        return (String)this.modelPathByResource.get(model);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelHelper#getDescription(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public String getDescription( Resource model ) {
        if (model instanceof EmfResource) {
            final EmfResource emfResource = (EmfResource)model;
            final ModelAnnotation modelAnn = emfResource.getModelAnnotation();
            if (modelAnn != null) {
                return modelAnn.getDescription();
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelHelper#getPrimaryMetamodelUri(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public String getPrimaryMetamodelUri( Resource model ) {
        // Look for model resources ...
        if (model instanceof EmfResource) {
            final EmfResource emfResource = (EmfResource)model;
            final ModelContents contents = emfResource.getModelContents();
            if (contents != null) {
                final ModelAnnotation modelAnnotation = contents.getModelAnnotation();
                if (modelAnnotation != null) {
                    return modelAnnotation.getPrimaryMetamodelUri();
                }
            }
        }

        // Look for XSD resources ...
        if (model instanceof XSDResourceImpl) {
            return XSDPackage.eNS_URI;
        }

        // Don't know what it is, so return null ...
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelHelper#getTargetNamespaceUri(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public String getTargetNamespaceUri( Resource model ) {
        if (model instanceof EmfResource) {
            final EmfResource emfResource = (EmfResource)model;
            final ModelAnnotation modelAnn = emfResource.getModelAnnotation();
            if (modelAnn != null) {
                // return modelAnn.getTargetNamespace();
            }
        } else if (model instanceof XSDResourceImpl) {
            final XSDSchema schema = ((XSDResourceImpl)model).getSchema();
            if (schema != null) {
                return schema.getTargetNamespace();
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelObjectHelper#getProperties(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public Properties getProperties( Resource model ) {
        ArgCheck.isNotNull(model);
        final Properties properties = new Properties();
        // Look for model resources ...
        if (model instanceof EmfResource) {
            final EmfResource emfResource = (EmfResource)model;
            final ModelContents contents = emfResource.getModelContents();
            if (contents != null) {
                final ModelAnnotation modelAnnotation = contents.getModelAnnotation();
                if (modelAnnotation != null) {
                    final EMap props = modelAnnotation.getTags();
                    final Iterator iter = props.iterator();
                    while (iter.hasNext()) {
                        final Map.Entry entry = (Map.Entry)iter.next();
                        final Object key = entry.getKey();
                        final Object value = entry.getValue();
                        properties.put(key, value);
                    }
                }
            }
        }
        return properties;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelHelper#getModelType(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public ModelType getModelType( Resource model ) {
        ArgCheck.isNotNull(model);

        com.metamatrix.metamodels.core.ModelType type = null;

        // Look for model resources ...
        if (model instanceof EmfResource) {
            final EmfResource emfResource = (EmfResource)model;
            final ModelContents contents = emfResource.getModelContents();
            if (contents != null) {
                final ModelAnnotation modelAnnotation = contents.getModelAnnotation();
                if (modelAnnotation != null) {
                    type = modelAnnotation.getModelType();
                }
            }
        }

        // Look for XSD resources ...
        if (model instanceof XSDResourceImpl) {
            type = com.metamatrix.metamodels.core.ModelType.TYPE_LITERAL;
        }

        if (type == null) {
            return ModelType.UNKNOWN;
        }
        return ModelType.getModelType(type);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelHelper#isVisible(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public boolean isVisible( Resource model ) {
        final Boolean isVisible = (Boolean)this.modelVisibilityByResource.get(model);
        return (isVisible != null ? isVisible.booleanValue() : true);
    }

}
