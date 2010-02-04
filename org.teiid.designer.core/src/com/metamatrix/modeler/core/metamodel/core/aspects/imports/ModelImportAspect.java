/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.imports;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * ModelAnnotationAspect
 */
public class ModelImportAspect extends AbstractMetamodelAspect implements ImportsAspect {

	public ModelImportAspect(MetamodelEntity entity) {
		super.setMetamodelEntity(entity);
		super.setID(ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.ID);
	}

	/** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect#getModelLocation(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public String getModelLocation(EObject eObject) {
        ArgCheck.isInstanceOf(ModelImport.class, eObject);
        // the modelImport object
        ModelImport modelImport = (ModelImport) eObject;
        return modelImport.getModelLocation();
    }

    /* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect#getModelImportPaths(org.eclipse.emf.ecore.EObject)
	 */
	public IPath getModelPath(EObject eObject) {
		ArgCheck.isInstanceOf(ModelImport.class, eObject);
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
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect#getImportedModelObjectID(org.eclipse.emf.ecore.EObject)
	 */
	public String getModelUuid(EObject eObject) {
		ArgCheck.isInstanceOf(ModelImport.class, eObject);
		// the modelImport object
		return ((ModelImport)eObject).getUuid();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect#getModelType(org.eclipse.emf.ecore.EObject)
	 */
	public String getModelType(EObject eObject) {
		ArgCheck.isInstanceOf(ModelImport.class, eObject);
		// the modelImport object
		ModelImport modelImport = (ModelImport) eObject;
		return modelImport.getModelType().getName();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect#getPrimaryMetaModelUri(org.eclipse.emf.ecore.EObject)
	 */
	public String getPrimaryMetaModelUri(EObject eObject) {
		ArgCheck.isInstanceOf(ModelImport.class, eObject);
		// the modelImport object
		ModelImport modelImport = (ModelImport) eObject;
		return modelImport.getPrimaryMetamodelUri();
	}

//	/* (non-Javadoc)
//	 * @see com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect#setModelPath(org.eclipse.emf.ecore.EObject, org.eclipse.core.runtime.IPath)
//	 */
//	public void setModelPath(EObject eObject, IPath modelPath) {
//		ArgCheck.isInstanceOf(ModelImport.class, eObject);
//		ArgCheck.isNotNull(modelPath);
//		// the modelImport object
//		ModelImport modelImport = (ModelImport) eObject;
//		modelImport.setPath(modelPath.toString());
//	}

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect#setModelLocation(org.eclipse.emf.ecore.EObject, org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    public void setModelLocation(EObject eObject, URI uri) {
        ArgCheck.isInstanceOf(ModelImport.class, eObject);
        ArgCheck.isNotNull(uri);
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
