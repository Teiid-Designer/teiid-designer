/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xmlservice.aspects.sql;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.xmlservice.XmlOperation;
import org.teiid.designer.metamodels.xmlservice.XmlServiceComponent;
import org.teiid.designer.metamodels.xmlservice.XmlServiceMetamodelPlugin;


/** 
 * XmlOperationAspect
 */
public class XmlOperationAspect extends XmlServiceComponentAspect implements SqlProcedureAspect {

    protected XmlOperationAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public String getNameInSource(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlServiceComponent.class, eObject); 
        XmlServiceComponent entity = (XmlServiceComponent) eObject;    
        String nameInSource = entity.getNameInSource();
        if (CoreStringUtil.isEmpty(nameInSource)) {
            nameInSource =  super.getName(eObject);
        }
        return nameInSource;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isVirtual(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlOperation.class, eObject);
        XmlOperation operation = (XmlOperation) eObject;    
        try {    
            Resource eResource = operation.eResource();
            if (eResource != null && eResource instanceof EmfResource) {
                return (((EmfResource)eResource).getModelType() == ModelType.VIRTUAL_LITERAL);
            }
        } catch(Exception e) {
            XmlServiceMetamodelPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#isFunction(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isFunction(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlOperation.class, eObject);
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#getParameters(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getParameters(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlOperation.class, eObject);
        XmlOperation operation = (XmlOperation) eObject;
        List params = new ArrayList();
        if (!operation.getInputs().isEmpty()) {
            params.addAll(operation.getInputs());
        }
        return params;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#getResult(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public Object getResult(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlOperation.class, eObject);
        XmlOperation operation = (XmlOperation) eObject;
        return operation.getOutput();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#getUpdateCount(org.eclipse.emf.ecore.EObject)
     * @since 5.5.3
     */
    public int getUpdateCount(EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlOperation.class, eObject);
        return ((XmlOperation)eObject).getUpdateCount().getValue();
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     * @since 4.2
     */
    public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.CALLABLE);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    public boolean isMappable(final EObject eObject, final int mappingType) {
        return (mappingType == SqlProcedureAspect.MAPPINGS.SQL_TRANSFORM);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canAcceptTransformationSource(EObject target, EObject source) {
        CoreArgCheck.isInstanceOf(XmlOperation.class, target);
        CoreArgCheck.isNotNull(source);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        // source can be another xml operation
        if(source instanceof XmlOperation) {
            return true;
        }
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canBeTransformationSource(EObject source, EObject target) {
        CoreArgCheck.isInstanceOf(XmlOperation.class, source);
        CoreArgCheck.isNotNull(target);
        // no object should be target of itself
        if(source == target) {
            return false;
        }
        // target can be another xml operation
        if(target instanceof XmlOperation) {
            return true;
        }
        return false;
    }
}
