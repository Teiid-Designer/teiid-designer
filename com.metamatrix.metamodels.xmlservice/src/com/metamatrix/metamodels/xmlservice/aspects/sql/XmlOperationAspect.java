/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.aspects.sql;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.XmlServiceComponent;
import com.metamatrix.metamodels.xmlservice.XmlServiceMetamodelPlugin;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/** 
 * XmlOperationAspect
 */
public class XmlOperationAspect extends XmlServiceComponentAspect implements SqlProcedureAspect {

    protected XmlOperationAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public String getNameInSource(final EObject eObject) {
        ArgCheck.isInstanceOf(XmlServiceComponent.class, eObject); 
        XmlServiceComponent entity = (XmlServiceComponent) eObject;    
        String nameInSource = entity.getNameInSource();
        if (StringUtil.isEmpty(nameInSource)) {
            nameInSource =  super.getName(eObject);
        }
        return nameInSource;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isVirtual(final EObject eObject) {
        ArgCheck.isInstanceOf(XmlOperation.class, eObject);
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isFunction(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isFunction(final EObject eObject) {
        ArgCheck.isInstanceOf(XmlOperation.class, eObject);
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getParameters(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getParameters(final EObject eObject) {
        ArgCheck.isInstanceOf(XmlOperation.class, eObject);
        XmlOperation operation = (XmlOperation) eObject;
        List params = new ArrayList();
        if (!operation.getInputs().isEmpty()) {
            params.addAll(operation.getInputs());
        }
        return params;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getResult(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public Object getResult(final EObject eObject) {
        ArgCheck.isInstanceOf(XmlOperation.class, eObject);
        XmlOperation operation = (XmlOperation) eObject;
        return operation.getOutput();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getUpdateCount(org.eclipse.emf.ecore.EObject)
     * @since 5.5.3
     */
    public int getUpdateCount(EObject eObject) {
        ArgCheck.isInstanceOf(XmlOperation.class, eObject);
        return ((XmlOperation)eObject).getUpdateCount().getValue();
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     * @since 4.2
     */
    public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.CALLABLE);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    public boolean isMappable(final EObject eObject, final int mappingType) {
        return (mappingType == SqlProcedureAspect.MAPPINGS.SQL_TRANSFORM);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canAcceptTransformationSource(EObject target, EObject source) {
        ArgCheck.isInstanceOf(XmlOperation.class, target);
        ArgCheck.isNotNull(source);
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canBeTransformationSource(EObject source, EObject target) {
        ArgCheck.isInstanceOf(XmlOperation.class, source);
        ArgCheck.isNotNull(target);
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
