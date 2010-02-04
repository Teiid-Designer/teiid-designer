/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;


/** 
 * @since 4.2
 */
public class VdbEditingContextImplValidationHelper {
    
    private String MSGKEY = "VdbEditingContextImpl.The_model_{0}_is_dependent_on_{1}_which_is_not_part_of_the_vdb._1"; //$NON-NLS-1$
    
    private VdbEditingContextImpl vdbEditingContextImpl;
    private Collection eResources;
    private boolean partialValidation;
    private List problemMarker;
    private List problems;
    private Map problemsMarkerInfo;
    
    private IPath modelPath;
    private ModelReference modelRef;
    private String modelPathString;
    
    /**
     *  
     * @param vdbEditingContextImpl
     * @param eResources
     * @since 4.2
     */
    public VdbEditingContextImplValidationHelper(final VdbEditingContextImpl vdbEditingContextImpl,
                                                 final Collection eResources, 
                                                 final boolean partialValidation) {
        this.vdbEditingContextImpl = vdbEditingContextImpl;
        this.eResources = eResources;
        this.partialValidation = partialValidation;
    }
    
    /**
     *  
     * @return
     * @since 4.2
     */
    protected IStatus getVdbStatus() {
        
        IStatus status = null;
        problems = new ArrayList();
        problemMarker = new ArrayList();
        problemsMarkerInfo = new HashMap();
        
        for (final Iterator iter = eResources.iterator(); iter.hasNext();) {            
            final Resource eResource = (Resource)iter.next();
           
            if( !isValidVdbInitial(eResource) ) {
                continue;
            }
            
            if (ModelerCore.getPlugin() != null) {
                isStale();
            }
            
            validateModels(eResource);                      
        }

        if (!partialValidation) {
            validateVdbWsdlGenerationOptions();
        }

        if (!problems.isEmpty()) {
            final String desc = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errors_validating_vdb"); //$NON-NLS-1$
            status = this.getVdbEditingContextImpl().createSingleIStatus(problems, desc);
        }

        return status;
    }
    
    private void validateModels(Resource eResource) {

        // Check the import list for missing dependent models ...
        String[] unresolvedLocations = getVdbEditingContextImpl().getVdbContainer().getResourceFinder().findUnresolvedResourceLocations(eResource);

        if (unresolvedLocations.length > 0) {
            for (int i = 0; i != unresolvedLocations.length; ++i) {
                String location = unresolvedLocations[i];
                if (!location.startsWith("http")) { //$NON-NLS-1$
                    location = URI.createURI(location).lastSegment();
                }
                setProblems(location, IStatus.ERROR, IStatus.ERROR, problemMarker, problems);
            }
        }
    }
    
    private String getMsgDescription(String path) {
        final Object[] params = new Object[] {modelPath, path};
        return VdbEditPlugin.Util.getString(MSGKEY, params); 
    }
    
    private void setProblems(String path, int markerStatus, int problemStatus, 
                             List problemMarker, List problems) {
        String msg = getMsgDescription(path);
        problemMarker.add(new VdbEditingContextImplProblemMarker(modelRef, markerStatus, msg, null));
        problems.add(new Status(problemStatus, VdbEditPlugin.PLUGIN_ID, 0, msg, null));
    }
    
//    /**
//     * @param eResource
//     * @param importUri
//     * @since 4.2
//     */
//    private void validateMissingModels(Resource eResource, URI importUri) {
//
//        // get normalized URI and path, for XSDs the uri may have been
//        // modified during save operation(due to webservices feature)
//        final URIConverter converter = this.getVdbEditingContextImpl().getVdbContainer().getURIConverter();
//        String importPath = importUri.path();
//        if (converter != null) {
//            importUri = converter.normalize(importUri);
//        }
//
//        MissingModelsValidator missingModelsValidator = new MissingModelsValidator(this.getVdbEditingContextImpl(), eResource,
//                                                                                   modelPath, modelRef);
//        missingModelsValidator.validateMissingModels(importUri, importPath, problemMarker, problems);
//    }

//    /**
//     *  
//     * @return
//     * @since 4.2
//     */
//    protected IStatus getVdbStatus() {
//        
//        IStatus status = null;
//        problems = new ArrayList();
//        problemMarker = new ArrayList();
//        problemsMarkerInfo = new HashMap();
//        
//        for (final Iterator iter = eResources.iterator(); iter.hasNext();) {            
//            final Resource eResource = (Resource)iter.next();
//            final URI resourceURI = eResource.getURI();
//           
//            if( !isValidVdbInitial(eResource) ) {
//                continue;
//            }
//            
//            if (ModelerCore.getPlugin() != null) {
//                isStale();
//            }
//            
//            validateModels(resourceURI, eResource);                      
//        }
//
//        if (!partialValidation) {
//            validateVdbWsdlGenerationOptions();
//        }
//
//        if (!problems.isEmpty()) {
//            final String desc = VdbEditPlugin.Util.getString("VdbEditingContextImpl.errors_validating_vdb"); //$NON-NLS-1$
//            status = this.getVdbEditingContextImpl().createSingleIStatus(problems, desc);
//        }
//
//        return status;
//    }
//    
//    private void validateModels(URI resourceURI, Resource eResource) {
//        
//        // Get any imports associated with the resource ...
//        final Collection modelImports = this.getVdbEditingContextImpl().getModelImportUris(resourceURI);
//
//        // Check the import list for missing dependent models ...
//        for (Iterator importIter = modelImports.iterator(); importIter.hasNext();) {
//            URI importUri = (URI)importIter.next();
//
//            // Check if the referenced model is one of the global resources
//            if (this.getVdbEditingContextImpl().isGlobalResource(importUri.toString())) {
//                continue;
//            }
//            
//            validateMissingModels(eResource, importUri);
//        }
//    }
//    
//    /**
//     *  
//     * @param eResource
//     * @param importUri
//     * @since 4.2
//     */
//    private void validateMissingModels(Resource eResource, URI importUri) {
//                
//        // get normalized URI and path, for XSDs the uri may have been
//        // modified during save operation(due to webservices feature)
//        final URIConverter converter = this.getVdbEditingContextImpl().getVdbContainer().getURIConverter();
//        String importPath = importUri.path();
//        if (converter != null) {
//            importUri = converter.normalize(importUri);
//        }
//
//        MissingModelsValidator missingModelsValidator = 
//            new MissingModelsValidator(this.getVdbEditingContextImpl(), eResource, modelPath, modelRef);
//        missingModelsValidator.validateMissingModels(importUri, importPath, problemMarker, problems);
//    }
    
    /**
     *  
     * 
     * @since 4.2
     */
    private void validateVdbWsdlGenerationOptions() {
    
        VdbWsdlGenerationOptionsValidator vdbWsdlGenerationOptionsValidator = 
            new VdbWsdlGenerationOptionsValidator(this.getVdbEditingContextImpl());
        
        vdbWsdlGenerationOptionsValidator.validateVdbWsdlGenerationOptions(problemMarker, problems);
    }
    
    /**
     *  
     * 
     * @since 4.2
     */   
    protected void markProblems() {
        if( problemMarker == null || problemMarker.size() == 0 ) return;
        
        for (Iterator problemMarkerIter = problemMarker.iterator(); problemMarkerIter.hasNext();) {
            VdbEditingContextImplProblemMarker marker = 
                (VdbEditingContextImplProblemMarker)problemMarkerIter.next();
            
            marker.markProblem();
        }
    }
    
    /**
     *  
     * @param eResource
     * @return boolean
     * @since 4.2
     */
    private boolean isValidVdbInitial(Resource eResource) {
        
        if ( !isValidModelPathString(eResource) ) {
            return false;
        }
        
        setPath();

        // If the resource is the Vdb manifest model or wsdl file then there is no corresponding ModelReference
        if (VdbEditingContext.MANIFEST_MODEL_NAME.equals(modelPathString) || 
                        VdbEditingContext.GENERATED_WSDL_FILENAME.equals(modelPathString)) {
            return false;
        }

        if ( !isValidModelReference(eResource) ) {
            return false;
        }

        //isStale();
        
        return true;
    }
    
    /**
     *  
     * @param eResource
     * @return boolean
     * @since 4.2
     */
    private boolean isValidModelPathString(Resource eResource) {
        modelPathString = this.getVdbEditingContextImpl().getResourcePath(eResource);
        
        if ( modelPathString == null ) {
            final URI uri = eResource.getURI();
            final String location = (uri.isFile() ? uri.toFileString() : URI.decode(uri.toString()));
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.finding_model_reference", location); //$NON-NLS-1$            
            problemMarker.add(new VdbEditingContextImplProblemMarker(vdbEditingContextImpl.getVirtualDatabase(), IStatus.ERROR, msg, null));
            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, null));            
            
            return false;
        }
        
        return true;
    }
    
    /**
     *  
     * @param eResource
     * @return boolean
     * @since 4.2
     */
    private boolean isValidModelReference(Resource eResource) {
    //  Get the ModelReference associated with this EMF resource
        modelRef = this.getVdbEditingContextImpl().getModelReferenceByPath(modelPath);
        
        if (modelRef == null) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.finding_model_reference", modelPath); //$NON-NLS-1$    
            
            problemMarker.add(new VdbEditingContextImplProblemMarker(vdbEditingContextImpl.getVirtualDatabase(), IStatus.ERROR, msg, null));
            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, 0, msg, null));
            return false;
        }
        
        return true;
    }
    
    /**
     *  
     * 
     * @since 4.2
     */
    private void isStale() {
//      Ensure the modelRef is not stale
        final boolean isStale = this.getVdbEditingContextImpl().isStale(modelRef);
        if (isStale) {
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.staleModel", modelRef.getName()); //$NON-NLS-1$
            if( !containsMessage(msg) ) {
                problemMarker.add(new VdbEditingContextImplProblemMarker(modelRef, IStatus.WARNING, msg, null));
            }
        }
    }
    
    private boolean containsMessage(String msg) {
        return problemsMarkerInfo.containsKey(msg);
    }
    
    /**
     *  
     * 
     * @since 4.2
     */
    private void setPath() {
        modelPath = new Path(modelPathString);        
    }
    
    /**
     *  
     * @return VdbEditingContextImpl
     * @since 4.2
     */
    private VdbEditingContextImpl getVdbEditingContextImpl() {
        return vdbEditingContextImpl;
    }
    
}
