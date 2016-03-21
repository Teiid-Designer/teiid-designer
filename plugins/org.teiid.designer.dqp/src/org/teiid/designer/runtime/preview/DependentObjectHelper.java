/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.preview;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.MappingRoot;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.common.xmi.XMIHeader;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.workspace.ModelFileUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.importer.Messages;
import org.teiid.designer.transformation.util.TransformationHelper;


/**
 * @since 8.0
 */
public class DependentObjectHelper {
    private EObject targetObject;
    private Set<EObject> sourceTables = new HashSet<EObject>();

    boolean includeIntermediates = true;

    public static final int SEARCHABLE = 0;
    public static final int ALL_EXCEPT_LIKE = 1;
    public static final int LIKE_ONLY = 2;
    public static final int UNSEARCHABLE = 3;
    public static final String SEARCHABLE_STRING = "SEARCHABLE"; //$NON-NLS-1$
    public static final String ALL_EXCEPT_LIKE_STRING = "ALL_EXCEPT_LIKE"; //$NON-NLS-1$
    public static final String LIKE_ONLY_STRING = "LIKE_ONLY"; //$NON-NLS-1$
    public static final String UNSEARCHABLE_STRING = "UNSEARCHABLE"; //$NON-NLS-1$
    public static final String UNKNOWN_STRING = "UNKNOWN"; //$NON-NLS-1$
    public static final String NULL_STRING = "NULL"; //$NON-NLS-1$

    
    /**
     * @throws ModelWorkspaceException 
     * @since 4.2
     */
    public DependentObjectHelper( EObject targetGroup) throws ModelWorkspaceException {
        super();

        CoreArgCheck.isNotNull(targetGroup, "DependentObjectHelper has NULL group. Expected Non-Null"); //$NON-NLS-1$

        this.targetObject = targetGroup;
        this.includeIntermediates = true;
        calculateDependentObjects();
    }

    /**
     * @return Returns the vGroup.
     * @since 4.2
     */
    public EObject getVGroup() {
        return this.targetObject;
    }
    
    private void calculateDependentObjects() throws ModelWorkspaceException {
    	findSourceTables();
    }
    
    public Set<EObject> getDependentObjects() {
    	return sourceTables;
    }

    private void findSourceTables() throws ModelWorkspaceException {
        // get transformation object
    	ModelResource targetMR = ModelerCore.getModelEditor().findModelResource(targetObject);
    	boolean isVirtual = ModelType.VIRTUAL_LITERAL.equals(((EmfResource)targetMR.getEmfResource()).getModelAnnotation().getModelType());
    	if( ! isVirtual ) return;
    	
        EObject transformationEObject = getTransformation(targetObject);
        // get sources
        Iterator<EObject> sourceIter = getSourceEObjects(transformationEObject).iterator();
        EObject nextSourceEObject = null;
        // walk through sources and add dependencies if "virtual"
        while (sourceIter.hasNext()) {
            nextSourceEObject = (EObject)sourceIter.next();
            if( nextSourceEObject.eIsProxy() ) {
                nextSourceEObject = getRealEObjectFromProxy(nextSourceEObject);
            } 
            if(nextSourceEObject!=null) {
                addSourceTable(nextSourceEObject);
                if (isVirtual(nextSourceEObject)) addDependencies(nextSourceEObject);
            }
        }
    }
 
    /*
     * If the EObject is a proxy, use the uuid to lookup the real EObject
     * @param proxyEObj the proxied EObject
     * @return the real EObject, 'null' if not found
     */
    private EObject getRealEObjectFromProxy(EObject proxyEObj) {
        EObject eObjectResult = null;
        if(proxyEObj.eIsProxy() && proxyEObj instanceof EObjectImpl) {
            try {
                String sUUIDFrag = ((EObjectImpl)proxyEObj).eProxyURI().fragment();
                eObjectResult = (EObject)ModelerCore.getModelContainer().getEObjectFinder().find(sUUIDFrag);
            } catch (CoreException e) {
                DqpPlugin.Util.log(IStatus.ERROR, e, Messages.DependentObjectHelper_getRealEObjectFromProxyError);
            }
        }
        return eObjectResult;
    }
        
    private void addSourceTable( EObject sourceTable ) {
            sourceTables.add(sourceTable); //$NON-NLS-1$
    }

    private void addDependencies( EObject virtualSource ) {
        List<EObject> virtualSources = new ArrayList<EObject>();

        EObject transformationEObject = getTransformation(virtualSource);

        if (transformationEObject != null) {
            // Get Source Tables for this transformation
            Iterator<EObject> sourceIter = getSourceEObjects(transformationEObject).iterator();
            EObject nextSourceEObject = null;
            while (sourceIter.hasNext()) {
                nextSourceEObject = (EObject)sourceIter.next();
                if( nextSourceEObject.eIsProxy() ) {
                    nextSourceEObject = getRealEObjectFromProxy(nextSourceEObject);
                } 
                if(nextSourceEObject!=null) {
                    if (isVirtual(nextSourceEObject)) virtualSources.add(nextSourceEObject);
                    addSourceTable(nextSourceEObject);
                }
            }
        }

        if (!virtualSources.isEmpty()) {
            Iterator<EObject> vIter = virtualSources.iterator();
            while (vIter.hasNext()) {
                addDependencies((EObject)vIter.next());
            }

        }
    }

    private EObject getTransformation( EObject targetVirtualGroupEObject ) {
        return TransformationHelper.getTransformationMappingRoot(targetVirtualGroupEObject);
    }

    private List<EObject> getSourceEObjects( final EObject transformationEObject ) {
        if (transformationEObject instanceof SqlTransformationMappingRoot) {
            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)transformationEObject;

            // Let's get current Input's
            List<EObject> inputEObjects = ((MappingRoot)mappingRoot).getInputs();
            // Now let's check to see if any exist as current inputs

            if (inputEObjects != null) return inputEObjects;
        }

        return Collections.emptyList();
    }
    
    /**
     * Return the virtual model state of the specified model object.
     * 
     * @param eObject
     * @return true if model object is in virtual model.
     */
    public boolean isVirtual( EObject eObject ) {
        final Resource resource = eObject.eResource();
        if (resource instanceof EmfResource) {
            return ModelType.VIRTUAL_LITERAL.equals(((EmfResource)resource).getModelAnnotation().getModelType());
        } else if (resource == null && eObject.eIsProxy()) {
            URI theUri = ((InternalEObject)eObject).eProxyURI().trimFragment();
            if (theUri.isFile()) {
                File newFile = new File(theUri.toFileString());
                XMIHeader header = ModelFileUtil.getXmiHeader(newFile);
                if (header != null && ModelType.VIRTUAL_LITERAL.equals(ModelType.get(header.getModelType()))) return true;
            }
        }

        return false;
    }
}
