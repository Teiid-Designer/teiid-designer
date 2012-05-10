/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.aspects.sql;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;


/** 
 * OperationAspect
 */
public class OperationAspect extends WebServiceComponentAspect implements SqlProcedureAspect {

    protected OperationAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isVirtual(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Operation.class, eObject);
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isFunction(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isFunction(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Operation.class, eObject);
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getParameters(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getParameters(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Operation.class, eObject);
        Operation opearation = (Operation) eObject;
        List params = new ArrayList(1);
        params.add(opearation.getInput());
        return params;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getResult(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public Object getResult(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Operation.class, eObject);
        Operation opearation = (Operation) eObject;
        return opearation.getOutput();
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getUpdateCount(org.eclipse.emf.ecore.EObject)
     * @since 5.5.3
     */
    public int getUpdateCount(EObject eObject) {
        CoreArgCheck.isInstanceOf(Operation.class, eObject);
        return ((Operation)eObject).getUpdateCount().getValue();
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
        CoreArgCheck.isInstanceOf(Operation.class, target);
        CoreArgCheck.isNotNull(source);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        if(isVirtual(target)) {
            // source cannot be an operation
            if(source instanceof Operation) {
                return canBeTransformationSource(source, target);
            }
            SqlAspect sourceAspect = SqlAspectHelper.getSqlAspect(source);
            if(sourceAspect instanceof SqlTableAspect || sourceAspect instanceof SqlProcedureAspect) {
                return true;
            }
        }
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canBeTransformationSource(EObject source, EObject target) {
        CoreArgCheck.isInstanceOf(Operation.class, source);
        return false;
    }
}
