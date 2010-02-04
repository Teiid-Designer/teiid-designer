/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.resource;

import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.util.ModelContents;

/**
 * MTK implementation of a Resource
 */
public interface EResource extends Resource, Resource.Internal {

    /**
     * Return the {@link com.metamatrix.metamodels.core.ModelType} for this
     * resource.  The model type information is obtained by either reading
     * the model file header information or by examining the model annotation 
     * node depending on whether the resource has been loaded. If the type 
     * cannot be determined then null will be returned.
     * @return
     */
    ModelType getModelType();
    
    /**
     * Return the description string defined for this resource or null
     * if one does not exist. The description information is obtained by either
     * reading the model file header information or by examining the model 
     * annotation node depending on whether the resource has been loaded. If
     * no description exists then null will be returned.
     * @return
     */
    String getDescription();
    
    /**
     * Return the UUID defined for this resource or null
     * if one does not exist. The UUID is obtained by either
     * reading the model file header information or by examining the model 
     * annotation node depending on whether the resource has been loaded. 
     * @return
     */
    ObjectID getUuid();

    /**
     * Return the URI for the primary metamodel in this model
     * @return the URI; may be null if the primary metamodel is not registered
     */
    URI getPrimaryMetamodelUri();
    
    /**
     * Obtain the map of namespace prefixes to namespace URI strings.
     * @return
     */
    Map getNamespacePrefixToUrisMap();
    
    /**
     * Obtain the helper for the model contents.
     * @return the content helper; may be null if this resource is not loaded
     */
    ModelContents getModelContents();
    
    /**
     * Return a count for the number of times this resource has been loaded since it's
     * inception.  If 0 is returned this indicates that this resource has never been loaded.
     * @return count for the number of times the resource has been loaded since it was 
     * first instantiated.
     */
    int getLoadedCount();
    
//    /**
//     * Returns an array of ResourceReference instances representing those resources 
//     * externally referenced by objects within this resource.  The resource does not
//     * have to be loaded to retrieve the up-to-date list of references.  However, if 
//     * this resource has never been loaded before calling this method will force a 
//     * load in order to initially populate the cross-document references. 
//     * @return
//     */
//    ResourceReference[] getECrossReferences();

}
