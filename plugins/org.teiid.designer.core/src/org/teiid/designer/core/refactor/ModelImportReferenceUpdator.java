/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.refactor;

import java.util.Iterator;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.metamodels.core.ModelType;



/**
 * Updates the properties on the model import to poin to the newly copied model.
 * @since 8.0
 */
public class ModelImportReferenceUpdator implements ReferenceUpdator {

    /**
     * @see org.teiid.designer.core.refactor.ReferenceUpdator#updateEObject(org.eclipse.emf.ecore.EObject, java.util.Map)
     * @since 4.2
     */
    @Override
	public void updateEObject(final EObject eObject, final Map oldToNewObjects) {
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isNotNull(oldToNewObjects);

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
        CoreArgCheck.isNotNull(resource);

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
