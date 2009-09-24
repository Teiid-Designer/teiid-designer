/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.refactor;

import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.ReferenceUpdator;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;


/**
 * Updates the properties on the model import to poin to the newly copied model.
 * @since 4.2
 */
public class ModelImportReferenceUpdator implements ReferenceUpdator {

    /**
     * @see com.metamatrix.modeler.core.refactor.ReferenceUpdator#updateEObject(org.eclipse.emf.ecore.EObject, java.util.Map)
     * @since 4.2
     */
    public void updateEObject(final EObject eObject, final Map oldToNewObjects) {
        ArgCheck.isNotNull(eObject);
        ArgCheck.isNotNull(oldToNewObjects);

        if(eObject instanceof ModelImport) {
            // model import that needs to be updated
            ModelImport modelImport = (ModelImport) eObject;

            // new reource model annotation
            ModelAnnotation newModel = getNewResourceModelAnnotation(modelImport, oldToNewObjects);

            if(newModel != null) {
	            // resource for the new model annotation
	            Resource newResource = newModel.eResource();
	            // find properties on these and update the modelImport
	            String resourcePath = WorkspaceResourceFinderUtil.getWorkspaceUri(newResource);
	            if (resourcePath == null) {
	                return;
	            }

	            Resource eResource = eObject.eResource();
                URI resourceURI    = eResource.getURI();
                URI importURI      = newResource.getURI();
                String uriString   = URI.decode(importURI.toString());
                if (importURI.isFile()) {
                    boolean deresolve = (resourceURI != null && !resourceURI.isRelative() && resourceURI.isHierarchical());
                    if (deresolve && !importURI.isRelative()) {
                        URI deresolvedURI = importURI.deresolve(resourceURI, true, true, false);
                        if (deresolvedURI.hasRelativePath()) {
                            importURI = deresolvedURI;
                            uriString = URI.decode(importURI.toString());
                        }
                    }
                    modelImport.setModelLocation(uriString);
                } else {
                    modelImport.setModelLocation(uriString);
                }

	            //modelImport.setPath(resourcePath);
	            modelImport.setName(this.getResourceName(newResource));
                modelImport.setModelType(newModel.getModelType());
                modelImport.setPrimaryMetamodelUri(newModel.getPrimaryMetamodelUri());
                modelImport.setUuid(ModelerCore.getObjectIdString(newModel));
            }
        }
    }

    /**
     * Get the modelannotation for the new/copied resource for whose old resource there is a model import.
     * @param modelImport ModelImport to the old resource
     * @param oldToNewObjects Map containig old new model annotation object
     * @return The model annotation to the new resource.
     * @since 4.2
     */
    private ModelAnnotation getNewResourceModelAnnotation(final ModelImport modelImport, final Map oldToNewObjects) {

        // properties on the model import to be matched against the old modelResource
        String primaryMetamodelUri = modelImport.getPrimaryMetamodelUri();
        String resourcePath = modelImport.getPath();
        ModelType modelType = modelImport.getModelType();

        for(final Iterator keyIter = oldToNewObjects.keySet().iterator(); keyIter.hasNext();) {
            Object keyObj = keyIter.next();
            if(keyObj instanceof ModelAnnotation) {
                ModelAnnotation modelAnnotation = (ModelAnnotation) keyObj;
                if(primaryMetamodelUri != null) {
                    if(!primaryMetamodelUri.equals(modelAnnotation.getPrimaryMetamodelUri())) {
                        continue;
                    }
                }

                if(modelType != null) {
                    int type = modelType.getValue();
                    ModelType oldModelType = modelAnnotation .getModelType();
                    if(oldModelType != null) {
                        int oldType = oldModelType.getValue();
                        if(type != oldType) {
                            continue;
                        }
                    }
                }

                if(resourcePath != null) {
                    Resource oldModelResource = modelAnnotation.eResource();
    	            String modelPath = WorkspaceResourceFinderUtil.getWorkspaceUri(oldModelResource);
                    if(!resourcePath.equals(modelPath)) {
                        continue;
                    }
                }


                // all the properties match find the new modelAnnotation
                return (ModelAnnotation) oldToNewObjects.get(keyObj);
            }
        }

        return null;
    }

    /**
     * Return the name of the specified resource removing any file
     * extension if one exists.
     * @param resource
     * @return
     */
    private String getResourceName(final Resource resource) {
        ArgCheck.isNotNull(resource);

        final URI resourceUri = resource.getURI();
        final String modelNameWithExt = resourceUri.lastSegment();
        final String extension = resourceUri.fileExtension();
        if (extension != null) {
            final int index = modelNameWithExt.indexOf(extension);
            if (index > 1) {
                return modelNameWithExt.substring(0, index - 1); // also remove the "."
            }
        }
        return modelNameWithExt;
    }

}
