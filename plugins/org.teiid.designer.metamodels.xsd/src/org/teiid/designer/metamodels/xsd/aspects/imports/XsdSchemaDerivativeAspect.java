/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xsd.aspects.imports;

import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.ImportsAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.xsd.XsdPlugin;


/**
 * XsdSchemaDerivativeAspect
 *
 * @since 8.0
 */
public class XsdSchemaDerivativeAspect extends AbstractMetamodelAspect implements ImportsAspect,
                                                                                  FileUtils.Constants {

    //============================================================================================================================
    // Constants
    
    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.ID;

    protected XsdSchemaDerivativeAspect(MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.ImportsAspect#getModelLocation(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public String getModelLocation(EObject eObject) {
        CoreArgCheck.isInstanceOf(XSDSchemaDirective.class, eObject);
        XSDSchemaDirective xsdDerivative = (XSDSchemaDirective) eObject;
        return xsdDerivative.getSchemaLocation();
    }


    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#getModelImportPaths(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public IPath getModelPath(EObject eObject) {
        
        CoreArgCheck.isInstanceOf(XSDSchemaDirective.class, eObject);
        XSDSchemaDirective xsdDerivative = (XSDSchemaDirective) eObject;
        XSDSchema schema = xsdDerivative.getResolvedSchema();
        
        if(schema != null) {
            Resource schemaResource = schema.eResource();
            if(schemaResource != null) {
                // find the model resource for the schema               
                ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(schemaResource);
                if(modelResource != null) {
                    return modelResource.getPath();             
                }
            }
//          MyDefect : 16368 refactor
            else {
                return findModelPathBySchemaLocation(xsdDerivative);
            }
        }
//      MyDefect : 16368 refactor
        else {            
            return findModelPathBySchemaLocation(xsdDerivative);
        }

        return null;
    }
    /**
     * Added this method to resolve IPath, in two or more projects if searching for a resource 
     * which have the same name in other project, could return a wrong location of the resource.
     * Will create another defect to fix this problem. 
     * @param xsdDerivative
     * @return
     * @since 4.2
     */
    private IPath findModelPathBySchemaLocation(XSDSchemaDirective xsdDerivative) {
        
        String schemaLocation = xsdDerivative.getSchemaLocation();
        if(schemaLocation != null) {
            IPath schemaPath = new Path(schemaLocation);
            String resourceName = schemaPath.lastSegment();
            
            //MyDefect : 17327 Added for the defect to find the correct resource by name 
            IResource iResource = findResourceInProjectByName(resourceName, xsdDerivative);
            if(iResource != null) {                
                return iResource.getFullPath();      
            }
        }

        return null;
    }
    
    private IResource findResourceInProjectByName(final String name, XSDSchemaDirective xsdDerivative) {
        
        if(name == null) {
            return null;
        }
                
        IResource iResource = null;
        Collection<IFile> iResources = WorkspaceResourceFinderUtil.findIResourceByName(name);

        if (iResources.size() == 0) {
            return null;
        } else if (iResources.size() == 1) {
            iResource = iResources.iterator().next();
        } else {
            // Find the IResource with this name in the same IProject as the IResource being operated on 
            IResource iRes = WorkspaceResourceFinderUtil.findIResource(xsdDerivative.eResource().getURI());
            IProject project = iRes.getProject();
            for (IFile fileResource : iResources) {
                if (fileResource.getProject().equals(project)) {
                    iResource = fileResource;
                    break;
                }
            }
            // If no IResource exists in this project then pick the first on in the array
            if (iResource == null) {
                iResource = iResources.iterator().next();
            }
        } 
        
        return iResource;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#getModelType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getModelType(EObject eObject) {
        return ModelType.TYPE_LITERAL.getName();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#getModelUuid(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getModelUuid(EObject eObject) {
        CoreArgCheck.isInstanceOf(XSDSchemaDirective.class, eObject);
        XSDSchemaDirective xsdDerivative = (XSDSchemaDirective) eObject;
        XSDSchema schema = xsdDerivative.getResolvedSchema();
        if(schema != null) {
            Resource schemaResource = schema.eResource();
            if(schemaResource != null) {
                // find the model resource for the schema               
                ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(schemaResource);
                if(modelResource != null) {
                    try {
                        return modelResource.getUuid();
                    } catch(Exception e) {
                        XsdPlugin.Util.log(e);
                    }
                }
            }
        }

        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#getPrimaryMetaModelUri(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getPrimaryMetaModelUri(EObject eObject) {
        return XSDPackage.eNS_URI;
    }

//    /**
//     * Adjust path to refactored model in referencing schema.
//     * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#setModelPath(org.eclipse.emf.ecore.EObject, org.eclipse.core.runtime.IPath)
//     */
//    public void setModelPath(final EObject eObject,
//                             IPath modelPath) {
//        CoreArgCheck.isInstanceOf(XSDSchemaDirective.class, eObject);
//        CoreArgCheck.isNotNull(modelPath);
//
//        final XSDSchemaDirective derivative = (XSDSchemaDirective)eObject;
//        final ModelResource model = ModelerCore.getModelWorkspace().
//                                        findModelResource(derivative.eResource());
//        
//        final IPath srcModelPath = model.getPath();
//        
//        final int commonFolderCount = modelPath.matchingFirstSegments(srcModelPath);
//        modelPath = modelPath.removeFirstSegments(commonFolderCount);
//        final int srcFolderCount = srcModelPath.segmentCount() - 1;
//        
//        //MyDefect : 17364 added path in the for loop.
//        if (commonFolderCount < srcFolderCount) {
//            IPath path = new Path(PARENT_FOLDER_SYMBOL);
//            int newCounter = srcFolderCount - commonFolderCount - 1;
//
//            for (int count = newCounter; count > 0; count--) {
//                path = path.append(new Path(PARENT_FOLDER_SYMBOL));
//            }
//            
//            modelPath = path.append(modelPath);
//        }
//        
//        derivative.setSchemaLocation(modelPath.makeRelative().toString());
//    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.ImportsAspect#setModelLocation(org.eclipse.emf.ecore.EObject, org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    @Override
	public void setModelLocation(EObject eObject, URI uri) {
        CoreArgCheck.isInstanceOf(XSDSchemaDirective.class, eObject);
        CoreArgCheck.isNotNull(uri);

        final XSDSchemaDirective derivative = (XSDSchemaDirective)eObject;
        Resource eResource = derivative.eResource();
        if (eResource != null) {
            URI eResourceURI = eResource.getURI();
            URI importURI    = uri;
            String uriString = URI.decode(importURI.toString());
            
            if (uri.isFile()) {
                boolean deresolve = (eResourceURI != null && !eResourceURI.isRelative() && eResourceURI.isHierarchical());
                if (deresolve && !importURI.isRelative()) {
                    URI deresolvedURI = importURI.deresolve(eResourceURI, true, true, false);
                    if (deresolvedURI.hasRelativePath()) {
                        importURI = deresolvedURI;
                    }
                }
                derivative.setSchemaLocation(importURI.toString());
            } else {
                derivative.setSchemaLocation(uriString);
            }
        }
    }
    
}
