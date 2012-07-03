/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.util.INewModelObjectHelper;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;

/**
 * @since 4.3
 */
public class TransformationNewModelObjectHelper implements INewModelObjectHelper {

    public static final String VIRTUAL_TABLE_CLEAR_SUPPORTS_UPDATE = "clearSupportsUpdate"; //$NON-NLS-1$
    public static final String VIRTUAL_PROCEDURE_TEMPLATE_SQL = "CREATE VIRTUAL PROCEDURE\nBEGIN\n <--insert SQL here-->;\nEND"; //$NON-NLS-1$

    /**
     * @since 4.3
     */
    public TransformationNewModelObjectHelper() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#canHelpCreate(java.lang.Object)
     */
    public boolean canHelpCreate( Object newObject ) {
        CoreArgCheck.isNotNull(newObject);
        // First case is a standard virtual table
        // If the createdObject is VirtualTable, set supportsUpdate to false
        if (newObject instanceof EObject) {
            EObject newEObject = (EObject)newObject;
            if (TransformationHelper.isVirtual(newEObject)) {
                // If the createdObject is VirtualTable, set supportsUpdate to false & create T-Root
                if (TransformationHelper.isSqlTable(newEObject) && !TransformationHelper.isXmlDocument(newEObject)) {
                    return true;
                } else if (TransformationHelper.isSqlProcedure(newEObject)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     */
    public Object getTransactionSetting() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.metamatrix.modeler.core.util.INewModelObjectHelper#helpCreate(java.lang.Object, java.util.Map)
     */
    public boolean helpCreate( Object newObject,
                               Map properties ) {
        CoreArgCheck.isNotNull(newObject);

        if (newObject instanceof EObject) {
            EObject newTarget = (EObject)newObject;
            if (TransformationHelper.isVirtual(newTarget)  ){
                // If the createdObject is VirtualTable, set supportsUpdate to false & create T-Root if it doesn't exist
                MetamodelAspect aspect = AspectManager.getSqlAspect(newTarget);
                if (aspect != null && aspect instanceof SqlTableAspect) {
                    // Add T-Root
                	if( !TransformationHelper.hasSqlTransformationMappingRoot(newTarget) ) {
	                    EObject newRoot = ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(newTarget, false, this);
	                    // Add Sql Mapping Helper under it.
	                    ModelResourceContainerFactory.addMappingHelper(newRoot);
	                    // defect 19675 - allow a straight-up copy of a table if desired
	                    if (isMapValueTrue(VIRTUAL_TABLE_CLEAR_SUPPORTS_UPDATE, properties, true)) { // default to clear
	                        SqlTableAspect tableAspect = (SqlTableAspect)aspect;
	                        tableAspect.setSupportsUpdate(newTarget, false);
	                    } // endif

	                    return true;
                	}
                } else if (TransformationHelper.isSqlProcedure(newTarget) && !TransformationHelper.isOperation(newTarget)) {
                	// Add T-Root
                	if( !TransformationHelper.hasSqlTransformationMappingRoot(newTarget) ) {
	                    EObject newRoot = ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(newTarget, false, this);
	                    // Add Sql Mapping Helper under it.
	                    ModelResourceContainerFactory.addMappingHelper(newRoot);

                	}
                	EObject tRoot = TransformationHelper.getMappingRoot(newTarget);
                    TransformationHelper.setSelectSqlString(tRoot, VIRTUAL_PROCEDURE_TEMPLATE_SQL, false, this);
                    return true;
                }
            }            
        }
        return false;
    }

    private static boolean isMapValueTrue( String propertyName,
                                           Map properties,
                                           boolean defaultValue ) {
        if (properties == null) {
            return defaultValue;
        } // endif
        Boolean bool = (Boolean)properties.get(propertyName);
        if (bool == null) {
            return defaultValue;
        } // endif
        return bool.booleanValue();
    }

}
