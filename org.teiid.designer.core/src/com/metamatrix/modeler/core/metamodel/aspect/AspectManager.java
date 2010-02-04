/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect;

/**
 * This class looks up aspects and caches them.
 */
public class AspectManager { 

	private static Class sqlAspectId = SqlAspect.class;
	private static Class validationAspectId = ValidationAspect.class;
	private static Class umlDiagramAspectId = UmlDiagramAspect.class;
    private static Class relationshipAspectId = RelationshipMetamodelAspect.class;
	private static Class importsAspectId = ImportsAspect.class;
    
    // --------------------------------------------------------
    // Modified 3/20/07 - BML (reviewed by John V.)
    // Defect 23839 - the call to the MetamodelRegistry's MetamodelAspect factory is expensive.
    // So, let's cache the aspects in this manager
    private static Map  sqlAspectMap = new HashMap();
    private static Map  validationAspectMap = new HashMap();
    private static Map  umlDiagramAspectMap = new HashMap();
    private static Map  relationshipsAspectMap = new HashMap();
    private static Map  modelImportsAspectMap = new HashMap();
    // --------------------------------------------------------
    
    
	/**
	 * Get the SqlAspect given an EObject
	 * @param eObject the EObject
	 * @return the SqlAspect for the supplied EObject
	 */
	public static SqlAspect getSqlAspect(final EObject eObject) {
        SqlAspect existingAspect = null;
		if(eObject != null) {
            EClass eClass = (eObject instanceof EClass ? (EClass)eObject : eObject.eClass());
            existingAspect = (SqlAspect)sqlAspectMap.get(eClass.getName());
            if( existingAspect == null ) {
                existingAspect = (SqlAspect) ModelerCore.getMetamodelRegistry().getMetamodelAspect( eClass, sqlAspectId );
                if( existingAspect != null ) {
                    sqlAspectMap.put(eClass.getName(), existingAspect);
                }
            }
		}
		return existingAspect;
	}

	/**
	 * Get the SqlAspect given an EObject
	 * @param eObject the EObject
	 * @return the SqlAspect for the supplied EObject
	 */
	public static ValidationAspect getValidationAspect(EObject eObject) {
        ValidationAspect existingAspect = null;
        if(eObject != null) {
            EClass eClass = (eObject instanceof EClass ? (EClass)eObject : eObject.eClass());
            existingAspect = (ValidationAspect)validationAspectMap.get(eClass.getName());
            if( existingAspect == null ) {
                existingAspect = (ValidationAspect) ModelerCore.getMetamodelRegistry().getMetamodelAspect( eClass, validationAspectId );
                if( existingAspect != null ) {
                    validationAspectMap.put(eClass.getName(), existingAspect);
                }
            }
        }
        return existingAspect;
	}

	/**
	 * Get the SqlAspect given an EObject
	 * @param eObject the EObject
	 * @return the SqlAspect for the supplied EObject
	 */
	public static UmlDiagramAspect getUmlDiagramAspect(EObject eObject) {
        UmlDiagramAspect existingAspect = null;
        if(eObject != null) {
            EClass eClass = (eObject instanceof EClass ? (EClass)eObject : eObject.eClass());
            existingAspect = (UmlDiagramAspect)umlDiagramAspectMap.get(eClass.getName());
            if( existingAspect == null ) {
                existingAspect = (UmlDiagramAspect) ModelerCore.getMetamodelRegistry().getMetamodelAspect( eClass, umlDiagramAspectId );
                if( existingAspect != null ) {
                    umlDiagramAspectMap.put(eClass.getName(), existingAspect);
                }
            }
        }
        return existingAspect;
	}

    /**
     * Get the RelationshipAspect given an EObject
     * @param eObject the EObject
     * @return the RelationshipAspect for the supplied EObject
     */
    public static RelationshipMetamodelAspect getRelationshipAspect(EObject eObject) {
        RelationshipMetamodelAspect existingAspect = null;
        if(eObject != null) {
            EClass eClass = (eObject instanceof EClass ? (EClass)eObject : eObject.eClass());
            existingAspect = (RelationshipMetamodelAspect)relationshipsAspectMap.get(eClass.getName());
            if( existingAspect == null ) {
                existingAspect = (RelationshipMetamodelAspect) ModelerCore.getMetamodelRegistry().getMetamodelAspect( eClass, relationshipAspectId );
                if( existingAspect != null ) {
                    relationshipsAspectMap.put(eClass.getName(), existingAspect);
                }
            }
        }
        return existingAspect;
    }

	/**
	 * Get the ImportsAspect given an EObject
	 * @param eObject the EObject
	 * @return the ImportsAspect for the supplied EObject
	 */
	public static ImportsAspect getModelImportsAspect(EObject eObject) {
        ImportsAspect existingAspect = null;
        if(eObject != null) {
            EClass eClass = (eObject instanceof EClass ? (EClass)eObject : eObject.eClass());
            existingAspect = (ImportsAspect)modelImportsAspectMap.get(eClass.getName());
            if( existingAspect == null ) {
                existingAspect = (ImportsAspect) ModelerCore.getMetamodelRegistry().getMetamodelAspect( eClass, importsAspectId );
                if( existingAspect != null ) {
                    modelImportsAspectMap.put(eClass.getName(), existingAspect);
                }
            }
        }
        return existingAspect;
	}
}
