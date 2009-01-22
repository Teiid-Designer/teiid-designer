/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.relationship.FileReference;
import com.metamatrix.metamodels.relationship.PlaceholderReference;
import com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipContainer;
import com.metamatrix.metamodels.relationship.RelationshipEntity;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.UriReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage
 * @generated
 */
public class RelationshipSwitch {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static RelationshipPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipSwitch() {
        if (modelPackage == null) {
            modelPackage = RelationshipPackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public Object doSwitch(EObject theEObject) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(EClass theEClass, EObject theEObject) {
        if (theEClass.eContainer() == modelPackage) {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        }
        List eSuperTypes = theEClass.getESuperTypes();
        return
            eSuperTypes.isEmpty() ?
                defaultCase(theEObject) :
                doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case RelationshipPackage.RELATIONSHIP_TYPE: {
                RelationshipType relationshipType = (RelationshipType)theEObject;
                Object result = caseRelationshipType(relationshipType);
                if (result == null) result = caseRelationshipEntity(relationshipType);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationshipPackage.RELATIONSHIP_ENTITY: {
                RelationshipEntity relationshipEntity = (RelationshipEntity)theEObject;
                Object result = caseRelationshipEntity(relationshipEntity);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationshipPackage.RELATIONSHIP: {
                Relationship relationship = (Relationship)theEObject;
                Object result = caseRelationship(relationship);
                if (result == null) result = caseRelationshipEntity(relationship);
                if (result == null) result = caseRelationshipContainer(relationship);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationshipPackage.FILE_REFERENCE: {
                FileReference fileReference = (FileReference)theEObject;
                Object result = caseFileReference(fileReference);
                if (result == null) result = caseUriReference(fileReference);
                if (result == null) result = casePlaceholderReference(fileReference);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationshipPackage.RELATIONSHIP_ROLE: {
                RelationshipRole relationshipRole = (RelationshipRole)theEObject;
                Object result = caseRelationshipRole(relationshipRole);
                if (result == null) result = caseRelationshipEntity(relationshipRole);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationshipPackage.PLACEHOLDER_REFERENCE: {
                PlaceholderReference placeholderReference = (PlaceholderReference)theEObject;
                Object result = casePlaceholderReference(placeholderReference);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER: {
                PlaceholderReferenceContainer placeholderReferenceContainer = (PlaceholderReferenceContainer)theEObject;
                Object result = casePlaceholderReferenceContainer(placeholderReferenceContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationshipPackage.URI_REFERENCE: {
                UriReference uriReference = (UriReference)theEObject;
                Object result = caseUriReference(uriReference);
                if (result == null) result = casePlaceholderReference(uriReference);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationshipPackage.RELATIONSHIP_FOLDER: {
                RelationshipFolder relationshipFolder = (RelationshipFolder)theEObject;
                Object result = caseRelationshipFolder(relationshipFolder);
                if (result == null) result = caseRelationshipEntity(relationshipFolder);
                if (result == null) result = casePlaceholderReferenceContainer(relationshipFolder);
                if (result == null) result = caseRelationshipContainer(relationshipFolder);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationshipPackage.RELATIONSHIP_CONTAINER: {
                RelationshipContainer relationshipContainer = (RelationshipContainer)theEObject;
                Object result = caseRelationshipContainer(relationshipContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Type</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Type</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRelationshipType(RelationshipType object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Entity</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Entity</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRelationshipEntity(RelationshipEntity object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Relationship</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Relationship</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRelationship(Relationship object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>File Reference</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>File Reference</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseFileReference(FileReference object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Role</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Role</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRelationshipRole(RelationshipRole object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Placeholder Reference</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Placeholder Reference</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object casePlaceholderReference(PlaceholderReference object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Placeholder Reference Container</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Placeholder Reference Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object casePlaceholderReferenceContainer(PlaceholderReferenceContainer object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Uri Reference</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Uri Reference</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseUriReference(UriReference object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Folder</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Folder</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRelationshipFolder(RelationshipFolder object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Container</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRelationshipContainer(RelationshipContainer object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public Object defaultCase(EObject object) {
        return null;
    }

} //RelationshipSwitch
