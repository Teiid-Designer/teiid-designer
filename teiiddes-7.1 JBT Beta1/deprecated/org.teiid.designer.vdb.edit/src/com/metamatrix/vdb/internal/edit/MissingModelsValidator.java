/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.manifest.ModelReference;


/** 
 * @since 4.2
 */
public class MissingModelsValidator {

    private VdbEditingContextImpl vdbEditingContextImpl;
    private Resource eResource;
    private IPath modelPath;
    private ModelReference modelRef;
    private String MSGKEY = "VdbEditingContextImpl.The_model_{0}_is_dependent_on_{1}_which_is_not_part_of_the_vdb._1"; //$NON-NLS-1$
    
    /**
     *  
     * @param vdbEditingContextImpl
     * @param eResource
     * @param modelPath
     * @param modelRef
     * @since 4.2
     */
    public MissingModelsValidator(Resource eResource,
                                  IPath modelPath,
                                  ModelReference modelRef) {
        
        this.eResource = eResource; 
        this.modelPath = modelPath;
        this.modelRef = modelRef;
    }
    
    /**
     *  
     * @param importUri
     * @param importPath
     * @param problemMarker
     * @param problems
     * @since 4.2
     */
    protected void validateMissingModels(URI importUri, String importPath, List problemMarker, List problems) {
                        
        if (importPath != null) {
            if (this.vdbEditingContextImpl.getVdbContainer().getResource(importUri, false) != null) {
                return;
            }
            if (importUri.isRelative()) {
                // check if the import references a model already in the VDB. If
                // the referenced model is not in the VDB then record an error.
                validateImportPathRelative(importPath, problemMarker, problems);
            } else if (importUri.isFile()) {
                // check if the import references a model already in the VDB. If
                // the referenced model is not in the VDB then record an error.
                validateImportPathFile(importPath, problemMarker, problems);
            } else {
                // the resource is either something in the workspce that is missing in
                // the vdb or outside the workspace
                validateImportPathOther(importPath, importUri, problemMarker, problems);
            }
        }
    }
    
    /**
     *  
     * @param importPath
     * @param problemMarker
     * @param problems
     * @since 4.2
     */
    private void validateImportPathRelative(String importPath, List problemMarker, List problems) {
        
        if (this.vdbEditingContextImpl.getModelReferenceByPath(new Path(importPath)) == null) {

            // Double check that this eResource has unresolvable external references ...
            final Collection unresolvedRefs = this.vdbEditingContextImpl.getUnresolvedExternalReferencePaths(eResource);
            if (!unresolvedRefs.isEmpty()) {

                setProblems(importPath, IStatus.ERROR, IStatus.ERROR, problemMarker, problems);
                            
                for (final Iterator iterator = unresolvedRefs.iterator(); iterator.hasNext();) {
                    final String refPath = (String)iterator.next();
                    if (refPath.equalsIgnoreCase(importPath)) {
                        continue;
                    }
                    
                    // Add a problem marker for any additional unresolved references
                    setProblems(refPath, IStatus.ERROR, IStatus.ERROR, problemMarker, problems);
                }
            }
        }        
    }
    
    /**
     *  
     * @param importPath
     * @param problemMarker
     * @param problems
     * @since 4.2
     */
    private void validateImportPathFile(String importPath, List problemMarker, List problems) {
        
        if (this.vdbEditingContextImpl.getModelReferenceByPartialPath(new Path(importPath)) == null) {
            // Double check that this eResource has unresolvable external references ...
            final Collection unresolvedRefs = this.vdbEditingContextImpl.getUnresolvedExternalReferencePaths(eResource);
            if (!unresolvedRefs.isEmpty()) {
                setProblems(importPath, IStatus.ERROR, IStatus.ERROR, problemMarker, problems);
                // Add a problem marker for any additional unresolved references
                for (final Iterator iterator = unresolvedRefs.iterator(); iterator.hasNext();) {
                    final String refPath = (String)iterator.next();
                    if (refPath.equalsIgnoreCase(importPath)) {
                        continue;
                    }

                    setProblems(refPath, IStatus.ERROR, IStatus.ERROR, problemMarker, problems);
                }
            }
        }
    }        
    
    /**
     *  
     * @param importPath
     * @param importUri
     * @param problemMarker
     * @param problems
     * @since 4.2
     */
    private void validateImportPathOther(String importPath, URI importUri, List problemMarker, List problems) {
        
        if (this.vdbEditingContextImpl.getVdbContainer().getResource(importUri, false) == null) {
            IPath iImportPart = new Path(importPath);
            // if the imported model is at a location relative to the
            // model that imports it, then the model is likely in the
            // workspce in the same project and missing from the vdb
            if (modelPath.matchingFirstSegments(iImportPart) > 0) {
                // Add a problem marker for any additional unresolved references
                setProblems(importPath, IStatus.ERROR, IStatus.ERROR, problemMarker, problems);
            } else {
                // the imported resource may still be in the workspce in a differrent
                // project, no way to catch this situation so just warn
                setProblems(importPath, IStatus.ERROR, IStatus.WARNING, problemMarker, problems);
            }
        }
    }
    
    /**
     *  
     * @param path
     * @return String
     * @since 4.2
     */
    private String getMsgDescription(String path) {
        final Object[] params = new Object[] {modelPath, path};
        return VdbEditPlugin.Util.getString(MSGKEY, params); 
    }
    
    /**
     *  
     * @param path
     * @param markerStatus
     * @param problemStatus
     * @param problemMarker
     * @param problems
     * @since 4.2
     */
    private void setProblems(String path, int markerStatus, int problemStatus, 
                             List problemMarker, List problems) {
        String msg = getMsgDescription(path);
        problemMarker.add(new VdbEditingContextImplProblemMarker(modelRef, markerStatus, msg, null));
        problems.add(new Status(problemStatus, VdbEditPlugin.PLUGIN_ID, 0, msg, null));
    }
}
