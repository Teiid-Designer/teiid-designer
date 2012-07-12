/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.mapping.Mapping;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.index.IndexingContext;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationInfo;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.metamodels.transformation.TransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TreeMappingRoot;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.xml.aspects.sql.MappingContext;


/**
 * @
 */
public class TreeMappingRootSqlAspect extends TransformationMappingRootSqlAspect {

    private static final String[] TRANSFORMATION_TYPES = new String[] {SqlTransformationAspect.Types.MAPPING};

    private static final String SCHEMA_EXTENTION = ".XSD"; //$NON-NLS-1$

    protected TreeMappingRootSqlAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformedObject(org.eclipse.emf.ecore.EObject)
     */
    public Object getTransformedObject( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);
        final TreeMappingRoot root = (TreeMappingRoot)eObject;
        EObject targetEObj = root.getTarget();
        if (targetEObj == null) {
            TransformationPlugin.Util.log(IStatus.WARNING,
                                          TransformationPlugin.Util.getString("TreeMappingRootSqlAspect.0", ModelerCore.getObjectIdString(root))); //$NON-NLS-1$
        }
        return targetEObj;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getInputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        return root.getInputs();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getNestedInputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        if (root.getNested() == null) {
            return Collections.EMPTY_LIST;
        }
        final List result = new ArrayList();
        for (final Iterator iter = root.getNested().iterator(); iter.hasNext();) {
            final Mapping mapping = (Mapping)iter.next();
            if (mapping != null) {
                result.addAll(mapping.getInputs());
            }
        }
        return result;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getNestedOutputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        if (root.getNested() == null) {
            return Collections.EMPTY_LIST;
        }
        final List result = new ArrayList();
        for (final Iterator iter = root.getNested().iterator(); iter.hasNext();) {
            final Mapping mapping = (Mapping)iter.next();
            if (mapping != null) {
                result.addAll(mapping.getOutputs());
            }
        }
        return result;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputsForOutput(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getNestedInputsForOutput( EObject eObject,
                                          EObject output ) {
        CoreArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        if (root.getNested() != null) {
            for (final Iterator iter = root.getNested().iterator(); iter.hasNext();) {
                final Mapping mapping = (Mapping)iter.next();
                if (mapping != null && mapping.getOutputs().contains(output)) {
                    return mapping.getInputs();
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputsForInput(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getNestedOutputsForInput( EObject eObject,
                                          EObject input ) {
        CoreArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        if (root.getNested() != null) {
            for (final Iterator iter = root.getNested().iterator(); iter.hasNext();) {
                final Mapping mapping = (Mapping)iter.next();
                if (mapping != null && mapping.getInputs().contains(input)) {
                    return mapping.getOutputs();
                }
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getOutputObjects( EObject eObject ) {
        CoreArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);
        final TransformationMappingRoot root = (TransformationMappingRoot)eObject;
        return root.getOutputs();
    }

    /**
     * @see org.teiid.designer.transformation.aspects.sql.MappingClassObjectSqlAspect#isRecordType(char)
     */
    public boolean isRecordType( char recordType ) {
        return (recordType == IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable( final EObject eObject ) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName( EObject eObject ) {
        final EObject transformedObject = (EObject)getTransformedObject(eObject);
        if (transformedObject == null) {
            return null;
        }
        final SqlAspect sqlAspect = AspectManager.getSqlAspect(transformedObject);
        if (sqlAspect != null) {
            return sqlAspect.getName(transformedObject);
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName( EObject eObject ) {
        final EObject transformedObject = (EObject)getTransformedObject(eObject);
        if (transformedObject == null) {
            return null;
        }
        final SqlAspect sqlAspect = AspectManager.getSqlAspect(transformedObject);
        if (sqlAspect != null) {
            return sqlAspect.getFullName(transformedObject);
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource( EObject eObject ) {
        final EObject transformedObject = (EObject)getTransformedObject(eObject);
        if (transformedObject == null) {
            return null;
        }
        final SqlAspect sqlAspect = AspectManager.getSqlAspect(transformedObject);
        if (sqlAspect != null) {
            return sqlAspect.getNameInSource(transformedObject);
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationTypes(org.eclipse.emf.ecore.EObject)
     */
    public String[] getTransformationTypes( final EObject eObject ) {
        CoreArgCheck.isInstanceOf(TreeMappingRoot.class, eObject);
        return TRANSFORMATION_TYPES;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#isDeleteAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isDeleteAllowed( EObject eObject ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#isInsertAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isInsertAllowed( EObject eObject ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#isUpdateAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isUpdateAllowed( EObject eObject ) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformation(org.eclipse.emf.ecore.EObject,
     *      java.lang.String)
     */
    public String getTransformation( EObject eObject,
                                     String type ) {
        final XmlDocument xmlDoc = (XmlDocument)getTransformedObject(eObject);
        final MappingContext mappingContext = new MappingContext(this.getResourceSet(eObject));
        final MappingDocumentFormatter formatter = MappingDocumentFormatter.create(xmlDoc, mappingContext);
        if (formatter != null) {
            try {
                final String result = formatter.createMappingString();
                return result;
            } catch (Exception e) {
                TransformationPlugin.Util.log(e);
            }
        }
        return null;
    }

    private ResourceSet getResourceSet( final EObject eObject ) {
        final Resource eResource = eObject.eResource();
        if (eResource != null) {
            return eResource.getResourceSet();
        }
        try {
            return ModelerCore.getModelContainer();
        } catch (CoreException e) {
            TransformationPlugin.Util.log(e);
        }
        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationInfo(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext, java.lang.String)
     */
    public SqlTransformationInfo getTransformationInfo( final EObject eObject,
                                                        final IndexingContext context,
                                                        final String type ) {
        final XmlDocument xmlDoc = (XmlDocument)getTransformedObject(eObject);

        List schemaPaths = new ArrayList();

        // get the model relative paths of the schemas that the document resource depends upon
        final Resource eResource = xmlDoc.eResource();
        if (eResource != null && eResource instanceof EmfResource) {
            final List modelImports = ((EmfResource)eResource).getModelAnnotation().getModelImports();
            for (final Iterator iter = modelImports.iterator(); iter.hasNext();) {
                final ModelImport mdlImport = (ModelImport)iter.next();
                if (mdlImport != null) {
                    String modelPath = mdlImport.getModelLocation().toUpperCase();
                    // get the modeliports for schema
                    if (modelPath.endsWith(SCHEMA_EXTENTION)) {
                        schemaPaths.add(mdlImport.getModelLocation());
                    }
                }
            }

            final String result = getTransformation(eObject, type);
            SqlTransformationInfo info = new SqlTransformationInfo(result);
            info.setSchemaPaths(schemaPaths);
            return info;
        }

        final Object[] params = new Object[] {xmlDoc};
        final String msg = TransformationPlugin.Util.getString("TreeMappingRootSqlAspect.could_not_get_resource_for_xmldoc", params); //$NON-NLS-1$
        TransformationPlugin.Util.log(IStatus.ERROR, msg);

        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject( EObject targetObject,
                              EObject sourceObject ) {

    }

}
