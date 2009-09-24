/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.util.ModelImportComparator;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.refactor.ExternalReferenceVisitor.ExternalReferences;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * OrganizeImportCommand
 */
public class OrganizeImportCommandHelperNonXsd extends OrganizeImportCommandHelper {
        
    protected OrganizeImportCommandHelperNonXsd() {        
        super();
    }
    
    /**
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus execute( final IProgressMonitor monitor) {
        return process(monitor);
    }
    
    /**
     *  
     * @param monitor
     * @return IStatus
     * @since 4.3
     */
    private IStatus process(IProgressMonitor monitor) {
        // Find all the external references ...        
        final List problems = new LinkedList();
        
        // Resource whose imports and hrefs are being rebuilt
        final Resource eResource = getResource();

        // Get annotation ...
        final ModelAnnotation modelAnnot = getModelAnnotation(eResource);
        
        // Clear current state ...
        this.modelImports.clear();
//        // reset the model imports
//        this.modelImports = new ArrayList(modelAnnot.getModelImports());

        // collect new imports(should be no duplicates)
        Set newImports = new HashSet(this.modelImports.size());
        
        // Build all of the imports for the external references ...
        buildAllImportsForExternalRef(eResource, problems, newImports, monitor);
        
        this.modelImports.addAll(newImports);
        
        // Defect 23518 - improving checks so we don't change imports if nothing changed.
        //Update the imports on the model annotation ... if imports have changed.
        if( importsChanged(modelAnnot) ) {
            updateImport(modelAnnot);
        }
               
        // Put all of the problems into a single IStatus ...
        IStatus resultStatus = createFinalStatus(problems);
//      MyDefect : 16368 refactored the method end
        
        return resultStatus;
    }
    
    /**
     * checks if the expected imports (see this.importList) are different than the current imports
     * This method allows the process() method to NOT update the imports if they are ok. This way we don't modify the model when
     * Update/Build imports is called on a resource and nothing has changed to affect the import list.
     * @param modelAnnot
     * @return true if missing import or stale import exists in the import list of the ModelAnnotation
     * @since 5.0.2
     */
    private boolean importsChanged(final ModelAnnotation modelAnnot ) {
        boolean result = false;
        
        result = modelAnnot.getModelImports().size() != this.modelImports.size();
        
        if( !result && ( hasStaleImports(modelAnnot) || isMissingImports(modelAnnot) ) ) {
            result = true;
        }
        
        return result;
    }
    /**
     *  
     * @param modelAnnot
     * @since 4.3
     */
    private boolean clearExistingImports(final ModelAnnotation modelAnnot) {
        if( modelAnnot.getModelImports().isEmpty() ) {
            return false;
        }
        
        List existingImportList = new ArrayList(modelAnnot.getModelImports());
        
        try {
            ModelerCore.getModelEditor().removeValue(modelAnnot, existingImportList, modelAnnot.getModelImports());
        } catch (ModelerCoreException err) {
            ModelerCore.Util.log(err);
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if any imports in the given ModelAnnotation contains any imports not in this class's import list
     * @param modelAnnot
     * @return true if an import exists and doesn't need to be there.
     * @since 5.0.2
     */
    private boolean hasStaleImports(final ModelAnnotation modelAnnot) {
        // If any existing import is not in the modelImports list, assume imports have changed
        List existingImportList = new ArrayList(modelAnnot.getModelImports());
        
        for( Iterator iter = existingImportList.iterator(); iter.hasNext(); ) {
            ModelImport nextImport = (ModelImport)iter.next();
            if( ! importInList(nextImport, this.modelImports)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if the import list for the give ModelAnnotation is missing any import in this class's importList 
     * @param modelAnnot
     * @return true if an expected import is not in the import list of the ModelAnnotation
     * @since 5.0.2
     */
    private boolean isMissingImports(final ModelAnnotation modelAnnot) {

        // If any imports are missing (i.e. existing Imports does not include any import from the modelImports list
        // then assume imports have changed
        List existingImportList = new ArrayList(modelAnnot.getModelImports());
        
        for( Iterator iter = modelImports.iterator(); iter.hasNext(); ) {
            ModelImport nextImport = (ModelImport)iter.next();
            if( ! importInList(nextImport, existingImportList)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks the modelLocation value for a given import versus the modelLocations in a list of imports 
     * @param someModelImport
     * @param importList
     * @return true if import is already in the list
     * @since 5.0.2
     */
    private boolean importInList(final ModelImport someModelImport, final List importList) {
        if( someModelImport.getModelLocation() == null || someModelImport.getModelLocation().trim().equals("")) { //$NON-NLS-1$
            return false;
        }
        String importLocation = someModelImport.getModelLocation();
        for( Iterator iter = importList.iterator(); iter.hasNext(); ) {
            ModelImport nextImport = (ModelImport)iter.next();
            if( nextImport.getModelLocation() != null && nextImport.getModelLocation().equalsIgnoreCase(importLocation) ) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     *  
     * @param importChanged
     * @param modelAnnot
     * @since 4.3
     */
    private void updateImport(final ModelAnnotation modelAnnot) {
        if ( modelAnnot != null ) {
            
            // Update the imports on the model annotation ...

            boolean importsChanged = clearExistingImports(modelAnnot);
            
            try {
                if( !modelImports.isEmpty() ) {
                    importsChanged = true;
                    Collections.sort(this.modelImports, new ModelImportComparator());
                    // Add all at once
                    ModelerCore.getModelEditor().addValue(modelAnnot, this.modelImports, modelAnnot.getModelImports() );
                }
            } catch (ModelerCoreException e1) {
                ModelerCore.Util.log(e1);
            } finally {
                if( importsChanged )  {
                    this.getResource().setModified(true);
                }
            }
        }
    }
    
    /**
     *  
     * @param visitor
     * @param problems
     * @param newImports
     * @param modelAnnot
     * @param monitor
     * @since 4.3
     */
    private void buildAllImportsForExternalRef(final Resource eResource,
                                               final List problems, 
                                               final Set newImports, 
                                               final IProgressMonitor monitor) {
        
        final ExternalReferenceVisitor visitor = processExternalResourcesReferences(eResource, problems);
        Collection externRefList = visitor.getExternalReferences();      
        
        // Create a set of the existing ModelImport location strings to be used when determining
        // if href need to be adjusted
        ModelAnnotation modelAnnot = getModelAnnotation(eResource);
        Collection existingImportLocations = new HashSet(modelAnnot.getModelImports().size());
        for (Iterator iterator = modelAnnot.getModelImports().iterator(); iterator.hasNext();) {
            ModelImport existingImport = (ModelImport)iterator.next();
            existingImportLocations.add(existingImport.getModelLocation());
        }
        
        
        for (Iterator iter = externRefList.iterator(); iter.hasNext();) {
            
            final ExternalReferenceVisitor.ExternalReferences externalRefs = (ExternalReferenceVisitor.ExternalReferences)iter.next();
            final URI externalUri = externalRefs.getResourceUri();
            Assertion.isNotNull(externalUri);

            // If the resourceURI for the external reference is to the same resource as the
            // one we are updating the imports for then this is really an internal reference.
            // A reference of this type may show up if an href appears in the model file
            // and cannot be resolved.  Unresolved references will be caught by the 
            // ResourceInScopeValidationRule.
            if (externalUri.equals(eResource.getURI())) {
                continue;
            }
            
            try {
                final ModelImport modelImport = createModelImport(eResource, externalRefs, problems, monitor);
                
                // If a non-null ModelImport is returned then we know that the URI for this
                // ExternalReferences was found in the selected resource's import list
                if ( modelImport != null) {                    
                    
                    // Add the import (even if it already existed in the original list) to the list of new imports
                    if ( !containsImport(newImports, modelImport) ) {
                        newImports.add(modelImport);
                    }
                                     
                    // adjust external reference for the new import
                    if ( !existingImportLocations.contains(modelImport.getModelLocation()) ) {
//System.out.println("\nOrganizeImportCommand on "+eResource.getURI().lastSegment());
//System.out.println("   adjusting references for import: "+modelImport.getModelLocation());
                        adjustReferences(eResource, externalRefs, modelImport);
                        eResource.setModified(true);                        
                    }
                }
                
            } catch (Throwable t) {
                final Object[] params = new Object[]{URI.decode(externalUri.toString())};
                final String msg = ModelerCore.Util.getString("OrganizeImportCommand.Error_while_building_import",params); //$NON-NLS-1$
                problems.add( new Status(IStatus.ERROR,PID,UNKNOWN_ERROR_BUILDING_IMPORT,msg,t) );
            }
        }
        
    }
        
    private boolean containsImport(final Collection importCollection, final ModelImport modelImport) {
        String modelImportUUID = modelImport.getUuid();
        String modelImportLoc  = modelImport.getModelLocation();
        for (Iterator i = importCollection.iterator(); i.hasNext();) {
            ModelImport mi = (ModelImport)i.next();
            if (modelImportUUID != null && modelImportUUID.equals(mi.getUuid())) {
                return true;
            }
            if (modelImportLoc != null && modelImportLoc.equals(mi.getModelLocation())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     *  
     * @param problems
     * @return ExternalReferenceVisitor
     * @since 4.3
     */
    private ExternalReferenceVisitor processExternalResourcesReferences(final Resource eResource, final List problems) {
        
        final ExternalReferenceVisitor visitor = new ExternalReferenceVisitor(eResource);
        visitor.setIncludeDiagramReferences(this.includeDiagramReferences);
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
                
        try {
            processor.walk(this.getResource(), ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (ModelerCoreException e) {
            final Object[] params = new Object[]{this.getResource().getURI(),e.getLocalizedMessage()};
            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.Error_while_organizing_imports",params); //$NON-NLS-1$
            problems.add( new Status(IStatus.ERROR,PID,ERROR_ORGANIZING_IMPORTS,msg,e) );
        } catch (Throwable t) {
            final Object[] params = new Object[]{this.getResource().getURI(),t.getLocalizedMessage()};
            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.Unknown_error_while_organizing_imports",params); //$NON-NLS-1$
            problems.add( new Status(IStatus.ERROR,PID,UNKNOWN_ERROR_ORGANIZING_IMPORTS,msg,t) );
        }
        
        return visitor;
    }
    
    /**
     *  
     * @param problems
     * @return IStatus
     * @since 4.3
     */
    private IStatus createFinalStatus(List problems) {
        
        IStatus resultStatus = null;        
        if ( problems.isEmpty() ) {
            final int code = EXECUTE_WITH_NO_PROBLEMS;
            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.complete"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.OK,PLUGINID,code,msg,null);
            resultStatus = status;
        } else if ( problems.size() == 1 ) {
            resultStatus = (IStatus) problems.get(0);
        } else {
            // There were problems, so determine whether there were warnings and errors ...
            resultStatus = createFinalResultStatus(problems);
        }
        
        return resultStatus;
    }
    
    /**
     *  
     * @param problems
     * @return IStatus
     * @since 4.3
     */
    private IStatus createFinalResultStatus(List problems) {
        
        IStatus resultStatus = null;               
        int numErrors = 0;
        int numWarnings = 0;
        final Iterator problemIter = problems.iterator();
        
        while (problemIter.hasNext()) {
            final IStatus aStatus = (IStatus)problemIter.next();
            if ( aStatus.getSeverity() == IStatus.WARNING ) {
                ++numWarnings;
            } else if ( aStatus.getSeverity() == IStatus.ERROR ) {
                ++numErrors;
            }
        }
        
        // Create the final status ...
        final IStatus[] statusArray = (IStatus[]) problems.toArray(new IStatus[problems.size()]);
        if ( numWarnings != 0 && numErrors == 0 ) {
            final int code = EXECUTE_WITH_WARNINGS;
            final Object[] params = new Object[]{new Integer(numWarnings)};
            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.warnings",params); //$NON-NLS-1$
            resultStatus = new MultiStatus(PLUGINID,code,statusArray,msg,null);
        } else if ( numWarnings == 0 && numErrors != 0 ) {
            final int code = EXECUTE_WITH_ERRORS;
            final Object[] params = new Object[]{new Integer(numErrors)};
            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.errors",params); //$NON-NLS-1$
            resultStatus = new MultiStatus(PLUGINID,code,statusArray,msg,null);
        } else if ( numWarnings != 0 && numErrors != 0 ) {
            final int code = EXECUTE_WITH_WARNINGS_AND_ERRORS;
            final Object[] params = new Object[]{new Integer(numWarnings),new Integer(numErrors)};
            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.warnings_and_errors",params); //$NON-NLS-1$
            resultStatus = new MultiStatus(PLUGINID,code,statusArray,msg,null);
        } else {
            final int code = EXECUTE_WITH_NO_WARNINGS_AND_ERRORS;
            final String msg = ModelerCore.Util.getString("OrganizeImportCommand.no_warnings_or_errors"); //$NON-NLS-1$
            resultStatus = new MultiStatus(PLUGINID,code,statusArray,msg,null);
        }
        
        return resultStatus;
    }
        
    /**
     * Create an appropriate {@link ModelImport} for the external resource.
     * @param uri the URI of the resource; never null
     * @param resource the reference to the Resource; null if there was at least one reference in 
     * {@link #getResource() this resource} that was resolved
     * @param resourceSet the ResourceSet to search for a referenced model
     * @param factory the factory that should be used; never null, and simply the value returned from 
     * {@link #getFactory()}.
     * @return the ModelImport object, or null if a ModelImport could not be created
     */
    private ModelImport createModelImport(final Resource eResource,
                                          final ExternalReferenceVisitor.ExternalReferences externalRefs,
                                          final List problems,
                                          final IProgressMonitor monitor ) {
        ModelImport theImport = null;

        // Get the external resource URI and possibly the external resource itself from the externalRefs
        final URI uri = externalRefs.getResourceUri();
        //System.out.println("    #### OrganizeImportCommand.createModelImport() externalRefs.URI = " + uri);
        // Defect 23340 - if the uri is relative, we DONT CARE because the resource is TEMPORARY. Just return
        if( uri.isRelative() ) {
            return null;
        }
        
        Resource theResource = externalRefs.getResource();  

        // If the resource is null, then look it up in the resource set ...
        ResourceSet eResourceSet = eResource.getResourceSet();
        if ( theResource == null ) {
            final boolean loadOnDemand = false;
            theResource = eResourceSet.getResource(uri,loadOnDemand);
        }
        
        // The resource is still null, so start searching for referenced objects ...
        if ( theResource == null ) {
            final Collection refs = externalRefs.getReferencedObjects();
            theResource = getHelper().findResourceWithObject(refs, problems, this.handler);
        }
        
        // If the resource is still null or the actual underlying resource does not exit
        if (theResource == null || resourceMightBeRefactored(theResource, uri) ) {
            // Attempt to find the EMF resource by matching the name of the resource to others in the resource set
            theResource = getHelper().findRefactoredResource(eResource, uri, monitor, problems);
        }
        
        // See if there is already an import with this URI ...
        // Do not use the ModelAnnotation.findModelImportByPath() method
        // because the map is never cleared or references to non-existent
        // ModelImport instances are never removed.
        final ModelImport existingImport = ModelerCore.getModelEditor().findModelImport((EmfResource)eResource, theResource);
        if ( existingImport != null ) {
            // Make sure the location information in the existing import is up-to-date
            ModelerCore.getModelEditor().updateModelImport(existingImport, theResource);
            theImport = existingImport;
        }
        
        if( theImport == null ) {
            // If the resource is non-null, then we know the resource exists, so just create a new import ...
            if ( theResource != null ) {
                try {
                    theImport = ModelerCore.getModelEditor().createModelImport((EmfResource)eResource,theResource);
                } catch (ModelerCoreException e) {
                    ModelerCore.Util.log(e);
                }
            }
        }

        return theImport;
    }
    
    /**
     *  
     * @param theResource
     * @param uri
     * @return boolean
     * @since 4.3
     */
    private boolean resourceMightBeRefactored(Resource theResource, URI uri) {
        if (uri.isFile()) {
            if (!new File(theResource.getURI().toFileString()).exists()) {
                return false;
            }
            IResource resource = WorkspaceResourceFinderUtil.findIResource(theResource);
            if (resource == null || !resource.exists()) {
                return true;
            }
        }
        return false;
    }    
    
    /**
     *  
     * @see com.metamatrix.modeler.core.refactor.OrganizeImportCommandHelper#setRefactoredPaths(java.util.Map)
     * @since 4.3
     */
    @Override
    protected void setRefactoredPaths(Map paths) {
        getHelper().setRefactoredPaths(paths);
    }
            
    /**
     *  
     * @param resource
     * @return ModelAnnotation
     * @since 4.3
     */
    private ModelAnnotation getModelAnnotation( final Resource resource ) {
        if ( resource instanceof EmfResource ) {
            final EmfResource emfResource = (EmfResource)resource;
            return emfResource.getModelAnnotation();
        }
        return null;
    }
    
    /**
     * Adjust the existing external references to reference the supplied model import.
     * @param externalRefs
     * @param modelImport
     */
    private void adjustReferences(final Resource eResource, 
                                  final ExternalReferences externalRefs, 
                                  final ModelImport modelImport) {
        
        Resource importedResource = null;
        final Container cntr = ModelerCore.getContainer(eResource);
        if (cntr != null) {
            final ResourceFinder finder = cntr.getResourceFinder();
            
            // Find the imported resource by its relative path ...
            final String modelLocation = modelImport.getModelLocation();
            if (modelImport.eResource() == null && !StringUtil.isEmpty(modelLocation)) {
                URI baseLocationURI  = eResource.getURI();
                final boolean isXSD = baseLocationURI.lastSegment().endsWith(ModelUtil.EXTENSION_XSD);
                
                // If the base resource URI was created as a file URI then it's path is encoded so before we
                // resolve the referenced resource we need to encode it's relative path
                URI modelLocationURI = (baseLocationURI.isFile() ? URI.createURI(modelLocation, false): URI.createURI(modelLocation));
                
                // Defect 23340 - if the resource is an XSD, treat as before and modify the modelLocationURI
                if( isXSD ) {
                    if (baseLocationURI.isHierarchical() && !baseLocationURI.isRelative() && modelLocationURI.isRelative()) {
                        modelLocationURI = modelLocationURI.resolve(baseLocationURI);
                    }
                }
                // Defect 23340 - if location is relative, then we just find the resource from the workspace (lighter weight check)
                // rather than the findByURI() call.
                if( modelLocationURI.isRelative() ) {
                    importedResource = finder.findByWorkspaceUri(modelLocationURI, eResource);
                } else {
                    importedResource = finder.findByURI(modelLocationURI, true);
                }
            }
            // If the imported resource was not found try the ResourceFinder which 
            // will search by UUID and name.  
            if (importedResource == null) {
                importedResource = finder.findByImport(modelImport, false);
            }
            
            if (importedResource != null) {
                final Collection refs = externalRefs.getReferencedObjects();
                final URI uri = importedResource.getURI();
                
                for (Iterator iter = refs.iterator(); iter.hasNext();) {
                    final Object obj = iter.next();
                    
                    if ( obj instanceof InternalEObject ) {
                        final InternalEObject iobj = (InternalEObject)obj;
                        if ( iobj.eIsProxy() ) {                        
                            final URI existingUri = iobj.eProxyURI();
                            final String frag = existingUri.fragment();
                            
                            final URI newUri = uri.appendFragment(frag);                    
                            iobj.eSetProxyURI(newUri);
                        }
                    }
                }
            }
        }
    }
    
//  /**
//  *  
//  * @param newImports
//  * @return boolean
//  * @since 4.3
//  */
// private boolean isImportChanged(Set newImports) {
//     boolean importChanged = false;
//     if(this.modelImports.size() != newImports.size()) {
//         this.modelImports.clear();
//         this.modelImports = new ArrayList(newImports);
//         importChanged = true;
//     } else {
//         for(Iterator importIter= newImports.iterator(); importIter.hasNext();) {
//             final ModelImport modelImport = (ModelImport) importIter.next();
//             if (ModelerCore.getModelEditor().findModelImport(this.modelImports, modelImport.getPath()) == null) {                
//                 importChanged = true;
//                 this.modelImports.clear();
//                 this.modelImports = new ArrayList(newImports);
//                 break;
//             }                
//         }            
//     }
//     
//     return importChanged;
// }

//  /**
//   *  
//   * @param resourceUri
//   * @return String
//   * @since 4.3
//   */
//  private String getModelName( final URI resourceUri ) {
//      final String modelNameWithExt = resourceUri.lastSegment();
//      final String extension = resourceUri.fileExtension();
//      if ( extension != null ) {
//          final int index = modelNameWithExt.indexOf(extension);
//          if ( index > 1 ) {
//              return modelNameWithExt.substring(0,index-1);   // also remove the "."
//          }
//      }
//      return modelNameWithExt;
//  }

//    /**
//     * Adjust the existing external references to reference the supplied IResource.
//     * @param externalRefs
//     * @param iResource
//     */
//    private void adjustReferences(final ExternalReferences externalRefs, final IResource iResource) {
//        
//        final String modelPath = (iResource != null ? iResource.getLocation().toString() : null);
//        if ( modelPath == null ) {
//            return;
//        }
//        
//        final Collection refs = externalRefs.getReferencedObjects();
//        final URI uri = URI.createFileURI(modelPath);
//        final Iterator iter = refs.iterator();
//        
//        while (iter.hasNext()) {
//            final Object obj = iter.next();
//            if ( obj instanceof InternalEObject ) {
//                final InternalEObject iobj = (InternalEObject)obj;
//                if ( iobj.eIsProxy() ) {                        
//                    final URI existingUri = iobj.eProxyURI();
//                    final String frag = existingUri.fragment();
//                    
//                    //MyDefect : 17647 
//                    final URI newUri = uri.appendFragment(frag);                    
//                    iobj.eSetProxyURI(newUri);
//                }
//            }
//        }
//    }     
}
