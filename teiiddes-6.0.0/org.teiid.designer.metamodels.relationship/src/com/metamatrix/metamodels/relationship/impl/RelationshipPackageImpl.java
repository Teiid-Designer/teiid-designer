/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.impl;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;

import com.metamatrix.metamodels.relationship.FileReference;
import com.metamatrix.metamodels.relationship.PlaceholderReference;
import com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipContainer;
import com.metamatrix.metamodels.relationship.RelationshipEntity;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.RelationshipTypeStatus;
import com.metamatrix.metamodels.relationship.UriReference;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RelationshipPackageImpl extends EPackageImpl implements RelationshipPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass relationshipTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass relationshipEntityEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass relationshipEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass fileReferenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass relationshipRoleEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass placeholderReferenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass placeholderReferenceContainerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass uriReferenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass relationshipFolderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass relationshipContainerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum relationshipTypeStatusEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType iStatusEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType listEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private RelationshipPackageImpl() {
        super(eNS_URI, RelationshipFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static RelationshipPackage init() {
        if (isInited) return (RelationshipPackage)EPackage.Registry.INSTANCE.getEPackage(RelationshipPackage.eNS_URI);

        // Obtain or create and register package
        RelationshipPackageImpl theRelationshipPackage = (RelationshipPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof RelationshipPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new RelationshipPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackageImpl.init();

        // Create package meta-data objects
        theRelationshipPackage.createPackageContents();

        // Initialize created meta-data
        theRelationshipPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theRelationshipPackage.freeze();

        return theRelationshipPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRelationshipType() {
        return relationshipTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_Directed() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_Exclusive() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_CrossModel() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_Abstract() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_UserDefined() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_Status() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_Stereotype() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_Constraint() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_Label() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipType_OppositeLabel() {
        return (EAttribute)relationshipTypeEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipType_RelationshipFeatures() {
        return (EReference)relationshipTypeEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipType_SuperType() {
        return (EReference)relationshipTypeEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipType_SubType() {
        return (EReference)relationshipTypeEClass.getEStructuralFeatures().get(12);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipType_Roles() {
        return (EReference)relationshipTypeEClass.getEStructuralFeatures().get(13);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipType_Owner() {
        return (EReference)relationshipTypeEClass.getEStructuralFeatures().get(14);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRelationshipEntity() {
        return relationshipEntityEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipEntity_Name() {
        return (EAttribute)relationshipEntityEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRelationship() {
        return relationshipEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationship_FeatureValues() {
        return (EReference)relationshipEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationship_Targets() {
        return (EReference)relationshipEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationship_Sources() {
        return (EReference)relationshipEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationship_Type() {
        return (EReference)relationshipEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationship_RelationshipContainer() {
        return (EReference)relationshipEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getFileReference() {
        return fileReferenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFileReference_ToolName() {
        return (EAttribute)fileReferenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFileReference_ToolVersion() {
        return (EAttribute)fileReferenceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFileReference_FormatName() {
        return (EAttribute)fileReferenceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFileReference_FormatVersion() {
        return (EAttribute)fileReferenceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRelationshipRole() {
        return relationshipRoleEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipRole_Stereotype() {
        return (EAttribute)relationshipRoleEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipRole_Ordered() {
        return (EAttribute)relationshipRoleEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipRole_Unique() {
        return (EAttribute)relationshipRoleEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipRole_Navigable() {
        return (EAttribute)relationshipRoleEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipRole_LowerBound() {
        return (EAttribute)relationshipRoleEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipRole_UpperBound() {
        return (EAttribute)relationshipRoleEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationshipRole_Constraint() {
        return (EAttribute)relationshipRoleEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipRole_RelationshipType() {
        return (EReference)relationshipRoleEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipRole_OppositeRole() {
        return (EReference)relationshipRoleEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipRole_IncludeTypes() {
        return (EReference)relationshipRoleEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipRole_ExcludeTypes() {
        return (EReference)relationshipRoleEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getPlaceholderReference() {
        return placeholderReferenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getPlaceholderReference_PlaceholderReferenceContainer() {
        return (EReference)placeholderReferenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getPlaceholderReferenceContainer() {
        return placeholderReferenceContainerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getPlaceholderReferenceContainer_Placeholders() {
        return (EReference)placeholderReferenceContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getUriReference() {
        return uriReferenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getUriReference_Name() {
        return (EAttribute)uriReferenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getUriReference_Uri() {
        return (EAttribute)uriReferenceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getUriReference_Resolvable() {
        return (EAttribute)uriReferenceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getUriReference_Encoding() {
        return (EAttribute)uriReferenceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getUriReference_Abstract() {
        return (EAttribute)uriReferenceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getUriReference_Keywords() {
        return (EAttribute)uriReferenceEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getUriReference_RelatedUris() {
        return (EAttribute)uriReferenceEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getUriReference_Properties() {
        return (EReference)uriReferenceEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRelationshipFolder() {
        return relationshipFolderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipFolder_OwnedRelationshipTypes() {
        return (EReference)relationshipFolderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipFolder_OwnedRelationshipFolders() {
        return (EReference)relationshipFolderEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipFolder_Owner() {
        return (EReference)relationshipFolderEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRelationshipContainer() {
        return relationshipContainerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getRelationshipContainer_OwnedRelationships() {
        return (EReference)relationshipContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getRelationshipTypeStatus() {
        return relationshipTypeStatusEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getIStatus() {
        return iStatusEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getList() {
        return listEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipFactory getRelationshipFactory() {
        return (RelationshipFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        relationshipTypeEClass = createEClass(RELATIONSHIP_TYPE);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__DIRECTED);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__EXCLUSIVE);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__CROSS_MODEL);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__ABSTRACT);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__USER_DEFINED);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__STATUS);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__STEREOTYPE);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__CONSTRAINT);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__LABEL);
        createEAttribute(relationshipTypeEClass, RELATIONSHIP_TYPE__OPPOSITE_LABEL);
        createEReference(relationshipTypeEClass, RELATIONSHIP_TYPE__RELATIONSHIP_FEATURES);
        createEReference(relationshipTypeEClass, RELATIONSHIP_TYPE__SUPER_TYPE);
        createEReference(relationshipTypeEClass, RELATIONSHIP_TYPE__SUB_TYPE);
        createEReference(relationshipTypeEClass, RELATIONSHIP_TYPE__ROLES);
        createEReference(relationshipTypeEClass, RELATIONSHIP_TYPE__OWNER);

        relationshipEntityEClass = createEClass(RELATIONSHIP_ENTITY);
        createEAttribute(relationshipEntityEClass, RELATIONSHIP_ENTITY__NAME);

        relationshipEClass = createEClass(RELATIONSHIP);
        createEReference(relationshipEClass, RELATIONSHIP__FEATURE_VALUES);
        createEReference(relationshipEClass, RELATIONSHIP__TARGETS);
        createEReference(relationshipEClass, RELATIONSHIP__SOURCES);
        createEReference(relationshipEClass, RELATIONSHIP__TYPE);
        createEReference(relationshipEClass, RELATIONSHIP__RELATIONSHIP_CONTAINER);

        fileReferenceEClass = createEClass(FILE_REFERENCE);
        createEAttribute(fileReferenceEClass, FILE_REFERENCE__TOOL_NAME);
        createEAttribute(fileReferenceEClass, FILE_REFERENCE__TOOL_VERSION);
        createEAttribute(fileReferenceEClass, FILE_REFERENCE__FORMAT_NAME);
        createEAttribute(fileReferenceEClass, FILE_REFERENCE__FORMAT_VERSION);

        relationshipRoleEClass = createEClass(RELATIONSHIP_ROLE);
        createEAttribute(relationshipRoleEClass, RELATIONSHIP_ROLE__STEREOTYPE);
        createEAttribute(relationshipRoleEClass, RELATIONSHIP_ROLE__ORDERED);
        createEAttribute(relationshipRoleEClass, RELATIONSHIP_ROLE__UNIQUE);
        createEAttribute(relationshipRoleEClass, RELATIONSHIP_ROLE__NAVIGABLE);
        createEAttribute(relationshipRoleEClass, RELATIONSHIP_ROLE__LOWER_BOUND);
        createEAttribute(relationshipRoleEClass, RELATIONSHIP_ROLE__UPPER_BOUND);
        createEAttribute(relationshipRoleEClass, RELATIONSHIP_ROLE__CONSTRAINT);
        createEReference(relationshipRoleEClass, RELATIONSHIP_ROLE__RELATIONSHIP_TYPE);
        createEReference(relationshipRoleEClass, RELATIONSHIP_ROLE__OPPOSITE_ROLE);
        createEReference(relationshipRoleEClass, RELATIONSHIP_ROLE__INCLUDE_TYPES);
        createEReference(relationshipRoleEClass, RELATIONSHIP_ROLE__EXCLUDE_TYPES);

        placeholderReferenceEClass = createEClass(PLACEHOLDER_REFERENCE);
        createEReference(placeholderReferenceEClass, PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER);

        placeholderReferenceContainerEClass = createEClass(PLACEHOLDER_REFERENCE_CONTAINER);
        createEReference(placeholderReferenceContainerEClass, PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS);

        uriReferenceEClass = createEClass(URI_REFERENCE);
        createEAttribute(uriReferenceEClass, URI_REFERENCE__NAME);
        createEAttribute(uriReferenceEClass, URI_REFERENCE__URI);
        createEAttribute(uriReferenceEClass, URI_REFERENCE__RESOLVABLE);
        createEAttribute(uriReferenceEClass, URI_REFERENCE__ENCODING);
        createEAttribute(uriReferenceEClass, URI_REFERENCE__ABSTRACT);
        createEAttribute(uriReferenceEClass, URI_REFERENCE__KEYWORDS);
        createEAttribute(uriReferenceEClass, URI_REFERENCE__RELATED_URIS);
        createEReference(uriReferenceEClass, URI_REFERENCE__PROPERTIES);

        relationshipFolderEClass = createEClass(RELATIONSHIP_FOLDER);
        createEReference(relationshipFolderEClass, RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES);
        createEReference(relationshipFolderEClass, RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS);
        createEReference(relationshipFolderEClass, RELATIONSHIP_FOLDER__OWNER);

        relationshipContainerEClass = createEClass(RELATIONSHIP_CONTAINER);
        createEReference(relationshipContainerEClass, RELATIONSHIP_CONTAINER__OWNED_RELATIONSHIPS);

        // Create enums
        relationshipTypeStatusEEnum = createEEnum(RELATIONSHIP_TYPE_STATUS);

        // Create data types
        iStatusEDataType = createEDataType(ISTATUS);
        listEDataType = createEDataType(LIST);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        EcorePackageImpl theEcorePackage = (EcorePackageImpl)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

        // Add supertypes to classes
        relationshipTypeEClass.getESuperTypes().add(this.getRelationshipEntity());
        relationshipEClass.getESuperTypes().add(this.getRelationshipEntity());
        relationshipEClass.getESuperTypes().add(this.getRelationshipContainer());
        fileReferenceEClass.getESuperTypes().add(this.getUriReference());
        relationshipRoleEClass.getESuperTypes().add(this.getRelationshipEntity());
        uriReferenceEClass.getESuperTypes().add(this.getPlaceholderReference());
        relationshipFolderEClass.getESuperTypes().add(this.getRelationshipEntity());
        relationshipFolderEClass.getESuperTypes().add(this.getPlaceholderReferenceContainer());
        relationshipFolderEClass.getESuperTypes().add(this.getRelationshipContainer());

        // Initialize classes and features; add operations and parameters
        initEClass(relationshipTypeEClass, RelationshipType.class, "RelationshipType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getRelationshipType_Directed(), ecorePackage.getEBoolean(), "directed", "true", 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipType_Exclusive(), ecorePackage.getEBoolean(), "exclusive", "true", 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipType_CrossModel(), ecorePackage.getEBoolean(), "crossModel", "true", 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipType_Abstract(), ecorePackage.getEBoolean(), "abstract", "false", 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipType_UserDefined(), ecorePackage.getEBoolean(), "userDefined", "true", 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipType_Status(), this.getRelationshipTypeStatus(), "status", "STANDARD", 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipType_Stereotype(), ecorePackage.getEString(), "stereotype", null, 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getRelationshipType_Constraint(), ecorePackage.getEString(), "constraint", null, 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getRelationshipType_Label(), ecorePackage.getEString(), "label", null, 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getRelationshipType_OppositeLabel(), ecorePackage.getEString(), "oppositeLabel", null, 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipType_RelationshipFeatures(), theEcorePackage.getEStructuralFeature(), null, "relationshipFeatures", null, 0, -1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipType_SuperType(), this.getRelationshipType(), this.getRelationshipType_SubType(), "superType", null, 0, 1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipType_SubType(), this.getRelationshipType(), this.getRelationshipType_SuperType(), "subType", null, 0, -1, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipType_Roles(), this.getRelationshipRole(), this.getRelationshipRole_RelationshipType(), "roles", null, 2, 2, RelationshipType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipType_Owner(), this.getRelationshipFolder(), this.getRelationshipFolder_OwnedRelationshipTypes(), "owner", null, 0, 1, RelationshipType.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(relationshipTypeEClass, this.getRelationshipRole(), "getSourceRole"); //$NON-NLS-1$

        addEOperation(relationshipTypeEClass, this.getRelationshipRole(), "getTargetRole"); //$NON-NLS-1$

        addEOperation(relationshipTypeEClass, this.getList(), "getAllRelationshipFeatures"); //$NON-NLS-1$

        initEClass(relationshipEntityEClass, RelationshipEntity.class, "RelationshipEntity", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getRelationshipEntity_Name(), ecorePackage.getEString(), "name", null, 0, 1, RelationshipEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(relationshipEntityEClass, this.getIStatus(), "isValid"); //$NON-NLS-1$

        initEClass(relationshipEClass, Relationship.class, "Relationship", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getRelationship_FeatureValues(), theEcorePackage.getEStringToStringMapEntry(), null, "featureValues", null, 0, -1, Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationship_Targets(), theEcorePackage.getEObject(), null, "targets", null, 0, -1, Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationship_Sources(), theEcorePackage.getEObject(), null, "sources", null, 0, -1, Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationship_Type(), this.getRelationshipType(), null, "type", null, 0, 1, Relationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationship_RelationshipContainer(), this.getRelationshipContainer(), this.getRelationshipContainer_OwnedRelationships(), "relationshipContainer", null, 0, 1, Relationship.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(relationshipEClass, this.getRelationshipRole(), "getSourceRole"); //$NON-NLS-1$

        addEOperation(relationshipEClass, this.getRelationshipRole(), "getTargetRole"); //$NON-NLS-1$

        initEClass(fileReferenceEClass, FileReference.class, "FileReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getFileReference_ToolName(), ecorePackage.getEString(), "toolName", null, 0, 1, FileReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getFileReference_ToolVersion(), ecorePackage.getEString(), "toolVersion", null, 0, 1, FileReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getFileReference_FormatName(), ecorePackage.getEString(), "formatName", null, 0, 1, FileReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getFileReference_FormatVersion(), ecorePackage.getEString(), "formatVersion", null, 0, 1, FileReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(fileReferenceEClass, ecorePackage.getEString(), "getDisplayableName"); //$NON-NLS-1$

        initEClass(relationshipRoleEClass, RelationshipRole.class, "RelationshipRole", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getRelationshipRole_Stereotype(), ecorePackage.getEString(), "stereotype", null, 0, 1, RelationshipRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getRelationshipRole_Ordered(), ecorePackage.getEBoolean(), "ordered", "false", 0, 1, RelationshipRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipRole_Unique(), ecorePackage.getEBoolean(), "unique", "true", 0, 1, RelationshipRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipRole_Navigable(), ecorePackage.getEBoolean(), "navigable", "true", 0, 1, RelationshipRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipRole_LowerBound(), ecorePackage.getEInt(), "lowerBound", "1", 0, 1, RelationshipRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipRole_UpperBound(), ecorePackage.getEInt(), "upperBound", "-1", 0, 1, RelationshipRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getRelationshipRole_Constraint(), ecorePackage.getEString(), "constraint", null, 0, 1, RelationshipRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipRole_RelationshipType(), this.getRelationshipType(), this.getRelationshipType_Roles(), "relationshipType", null, 0, 1, RelationshipRole.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipRole_OppositeRole(), this.getRelationshipRole(), null, "oppositeRole", null, 1, 1, RelationshipRole.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipRole_IncludeTypes(), theEcorePackage.getEClass(), null, "includeTypes", null, 0, -1, RelationshipRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipRole_ExcludeTypes(), theEcorePackage.getEClass(), null, "excludeTypes", null, 0, -1, RelationshipRole.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(relationshipRoleEClass, ecorePackage.getEBoolean(), "isSourceRole"); //$NON-NLS-1$

        addEOperation(relationshipRoleEClass, ecorePackage.getEBoolean(), "isTargetRole"); //$NON-NLS-1$

        EOperation op = addEOperation(relationshipRoleEClass, this.getIStatus(), "isValidParticipant"); //$NON-NLS-1$
        addEParameter(op, theEcorePackage.getEObject(), "participant"); //$NON-NLS-1$

        op = addEOperation(relationshipRoleEClass, this.getIStatus(), "isValidParticipant"); //$NON-NLS-1$
        addEParameter(op, theEcorePackage.getEClassifier(), "participantType"); //$NON-NLS-1$

        op = addEOperation(relationshipRoleEClass, ecorePackage.getEBoolean(), "isAllowed"); //$NON-NLS-1$
        addEParameter(op, theEcorePackage.getEClassifier(), "type"); //$NON-NLS-1$

        op = addEOperation(relationshipRoleEClass, ecorePackage.getEBoolean(), "isAllowed"); //$NON-NLS-1$
        addEParameter(op, theEcorePackage.getEObject(), "particpant"); //$NON-NLS-1$

        addEOperation(relationshipRoleEClass, this.getRelationshipRole(), "getOverriddenRole"); //$NON-NLS-1$

        initEClass(placeholderReferenceEClass, PlaceholderReference.class, "PlaceholderReference", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getPlaceholderReference_PlaceholderReferenceContainer(), this.getPlaceholderReferenceContainer(), this.getPlaceholderReferenceContainer_Placeholders(), "PlaceholderReferenceContainer", null, 0, 1, PlaceholderReference.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(placeholderReferenceEClass, ecorePackage.getEString(), "getDisplayableName"); //$NON-NLS-1$

        initEClass(placeholderReferenceContainerEClass, PlaceholderReferenceContainer.class, "PlaceholderReferenceContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getPlaceholderReferenceContainer_Placeholders(), this.getPlaceholderReference(), this.getPlaceholderReference_PlaceholderReferenceContainer(), "placeholders", null, 0, -1, PlaceholderReferenceContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(uriReferenceEClass, UriReference.class, "UriReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getUriReference_Name(), ecorePackage.getEString(), "name", null, 0, 1, UriReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getUriReference_Uri(), ecorePackage.getEString(), "uri", null, 0, 1, UriReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getUriReference_Resolvable(), ecorePackage.getEBoolean(), "resolvable", "true", 0, 1, UriReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getUriReference_Encoding(), ecorePackage.getEString(), "encoding", null, 0, 1, UriReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getUriReference_Abstract(), ecorePackage.getEString(), "abstract", null, 0, 1, UriReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getUriReference_Keywords(), ecorePackage.getEString(), "keywords", null, 0, 1, UriReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getUriReference_RelatedUris(), ecorePackage.getEString(), "relatedUris", null, 0, -1, UriReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getUriReference_Properties(), theEcorePackage.getEStringToStringMapEntry(), null, "properties", null, 0, -1, UriReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(uriReferenceEClass, ecorePackage.getEString(), "getDisplayableName"); //$NON-NLS-1$

        initEClass(relationshipFolderEClass, RelationshipFolder.class, "RelationshipFolder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getRelationshipFolder_OwnedRelationshipTypes(), this.getRelationshipType(), this.getRelationshipType_Owner(), "ownedRelationshipTypes", null, 0, -1, RelationshipFolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipFolder_OwnedRelationshipFolders(), this.getRelationshipFolder(), this.getRelationshipFolder_Owner(), "ownedRelationshipFolders", null, 0, -1, RelationshipFolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRelationshipFolder_Owner(), this.getRelationshipFolder(), this.getRelationshipFolder_OwnedRelationshipFolders(), "owner", null, 0, 1, RelationshipFolder.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(relationshipContainerEClass, RelationshipContainer.class, "RelationshipContainer", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getRelationshipContainer_OwnedRelationships(), this.getRelationship(), this.getRelationship_RelationshipContainer(), "ownedRelationships", null, 0, -1, RelationshipContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(relationshipTypeStatusEEnum, RelationshipTypeStatus.class, "RelationshipTypeStatus"); //$NON-NLS-1$
        addEEnumLiteral(relationshipTypeStatusEEnum, RelationshipTypeStatus.PROTOTYPE_LITERAL);
        addEEnumLiteral(relationshipTypeStatusEEnum, RelationshipTypeStatus.STANDARD_LITERAL);
        addEEnumLiteral(relationshipTypeStatusEEnum, RelationshipTypeStatus.DEPRECATED_LITERAL);
        addEEnumLiteral(relationshipTypeStatusEEnum, RelationshipTypeStatus.INVALID_LITERAL);

        // Initialize data types
        initEDataType(iStatusEDataType, IStatus.class, "IStatus", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEDataType(listEDataType, List.class, "List", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //RelationshipPackageImpl
