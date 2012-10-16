/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.imports;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.ImportsAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.core.ModelImport;


/**
 * ModelAnnotationAspect
 *
 * @since 8.0
 */
public class ModelImportAspect extends AbstractMetamodelAspect implements ImportsAspect {

	public ModelImportAspect(MetamodelEntity entity) {
		super.setMetamodelEntity(entity);
		super.setID(ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.ID);
	}

	/** 
     * @see org.teiid.designer.core.metamodel.aspect.ImportsAspect#getModelLocation(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public String getModelLocation(EObject eObject) {
        CoreArgCheck.isInstanceOf(ModelImport.class, eObject);
        // the modelImport object
        ModelImport modelImport = (ModelImport) eObject;
        return modelImport.getModelLocation();
    }

    /* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#getModelImportPaths(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public IPath getModelPath(EObject eObject) {
		CoreArgCheck.isInstanceOf(ModelImport.class, eObject);
		// the modelImport object
		ModelImport modelImport = (ModelImport) eObject;
		String importPath = modelImport.getPath();
		if(importPath != null) {
			// return the IPath
			return new Path(modelImport.getPath());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#getImportedModelObjectID(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getModelUuid(EObject eObject) {
		CoreArgCheck.isInstanceOf(ModelImport.class, eObject);
		// the modelImport object
		return ((ModelImport)eObject).getUuid();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#getModelType(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getModelType(EObject eObject) {
		CoreArgCheck.isInstanceOf(ModelImport.class, eObject);
		// the modelImport object
		ModelImport modelImport = (ModelImport) eObject;
		return modelImport.getModelType().getName();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#getPrimaryMetaModelUri(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getPrimaryMetaModelUri(EObject eObject) {
		CoreArgCheck.isInstanceOf(ModelImport.class, eObject);
		// the modelImport object
		ModelImport modelImport = (ModelImport) eObject;
		return modelImport.getPrimaryMetamodelUri();
	}

//	/* (non-Javadoc)
//	 * @See org.teiid.designer.core.metamodel.aspect.ImportsAspect#setModelPath(org.eclipse.emf.ecore.EObject, org.eclipse.core.runtime.IPath)
//	 */
//	public void setModelPath(EObject eObject, IPath modelPath) {
//		CoreArgCheck.isInstanceOf(ModelImport.class, eObject);
//		CoreArgCheck.isNotNull(modelPath);
//		// the modelImport object
//		ModelImport modelImport = (ModelImport) eObject;
//		modelImport.setPath(modelPath.toString());
//	}

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.ImportsAspect#setModelLocation(org.eclipse.emf.ecore.EObject, org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    @Override
	public void setModelLocation(EObject eObject, URI uri) {
        CoreArgCheck.isInstanceOf(ModelImport.class, eObject);
        CoreArgCheck.isNotNull(uri);
        // the modelImport object
        ModelImport modelImport = (ModelImport) eObject;
        
        Resource eResource = modelImport.eResource();
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
                        uriString = URI.decode(importURI.toString());
                    }
                }
                modelImport.setModelLocation(uriString);
            } else {
                modelImport.setModelLocation(uriString);
            }
        }
    }
    
}
