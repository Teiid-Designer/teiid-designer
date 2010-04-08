/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.metadata;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.internal.core.index.ModelResourceIndexSelector;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.query.metadata.QueryMetadataInterface;

/**
 * TransformationMetadataFactory
 */
public class TransformationMetadataFactory extends ServerMetadataFactory {

    private static final TransformationMetadataFactory INSTANCE = new TransformationMetadataFactory();

    public static TransformationMetadataFactory getInstance() {
        return INSTANCE;
    }

	/**
	 * Return a reference to a {@link QueryMetadataInterface} implementation given the
	 * context for metadata.
     * @param context Object containing the info needed to lookup metadta.
	 * @return the QueryMetadataInterface implementation; never null
	 */
	public QueryMetadataInterface getModelerMetadata(final QueryMetadataContext context, Container container) {
        // Create the QueryMetadataInterface implementation to use
        // for query validation and resolution
		return new ModelerMetadata(context, container);
	}

    /**
     * Return a reference to a {@link QueryMetadataInterface} implementation that for the given
     * eObject , its resourceand the resources that depend ont it.
     * @param eObject The eObject for which metadata instance is returned
     * @return the QueryMetadataInterface implementation; never null
     */
    public QueryMetadataInterface getModelerMetadata(final EObject eObject) {
        CoreArgCheck.isNotNull(eObject);
        return getModelerMetadata(eObject, false);
    }
    
    /**
     * Return a reference to a {@link QueryMetadataInterface} implementation that for the given
     * eObject , its resourceand the resources that depend ont it. This assumes the Eobject is in
	 * a modelContainer, should be used only for workspace validation.
     * @param eObject The eObject for which metadata instance is returned
     * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
     * or if the whole workspace needs to be searched
     * @return the QueryMetadataInterface implementation; never null
     */
    public QueryMetadataInterface getModelerMetadata(final EObject eObject, final boolean restrictSearch) {
		CoreArgCheck.isNotNull(eObject);
		QueryMetadataContext context = buildQueryMetadataContext(eObject, restrictSearch);
		Container container = null;
        try {
        	container = ModelerCore.getModelContainer();
		} catch (CoreException e) {
			TransformationPlugin.Util.log(e);
		}
		return getModelerMetadata(context, container);
    }
    
    public static QueryMetadataContext buildQueryMetadataContext(final EObject eObject, final boolean restrictSearch) {
        Collection resources = null;
        Container container = null;
        try {        
	        // assume model container...modler metadata is for workspace  
	        container = ModelerCore.getModelContainer();
            ModelWorkspace workspace = ModelerCore.getModelWorkspace();
            if(workspace.isOpen()) {
                resources = Arrays.asList(workspace.getEmfResources());
            }else {
                resources = container.getResources();
            }
        } catch(CoreException e) {
            TransformationPlugin.Util.log(e);
        }

        // find the resoucre for the eObject in the container
        Resource resource = ModelerCore.getModelEditor().findResource(container, eObject);
        // create a indexSelector for this resource and instantiate transformation validator
        ModelResourceIndexSelector  selector = new ModelResourceIndexSelector(resource);
        QueryMetadataContext context = new QueryMetadataContext(selector);
		// set the resource scope (all model resources in open model projects)
        context.setResources(resources);
        // set the restrict search flag
        context.setRestrictedSearch(restrictSearch);
        return context;
    }

    /**
     * Return a reference to a {@link QueryMetadataInterface} implementation, the metadata
     * is assumed not to change.
     * @param context Object containing the info needed to lookup metadta.
     * @return the QueryMetadataInterface implementation; never null
     */
    public QueryMetadataInterface getVdbMetadata(final QueryMetadataContext context, Container container) {
        CoreArgCheck.isNotNull(context);
        // Create the QueryMetadataInterface implementation to use
        // for query validation and resolution
        return new TransformationMetadataFacade(new VdbMetadata(context, container));
//        return new VdbMetadata(context);
    }
}
