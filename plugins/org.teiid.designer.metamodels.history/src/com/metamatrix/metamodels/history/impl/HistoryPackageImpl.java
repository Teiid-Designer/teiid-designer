/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import com.metamatrix.metamodels.history.Branch;
import com.metamatrix.metamodels.history.HistoryCriteria;
import com.metamatrix.metamodels.history.HistoryFactory;
import com.metamatrix.metamodels.history.HistoryLog;
import com.metamatrix.metamodels.history.HistoryLogEntry;
import com.metamatrix.metamodels.history.HistoryPackage;
import com.metamatrix.metamodels.history.Label;
import com.metamatrix.metamodels.history.LabelLog;
import com.metamatrix.metamodels.history.Revision;
import com.metamatrix.metamodels.history.RevisionLog;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class HistoryPackageImpl extends EPackageImpl implements HistoryPackage {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass historyLogEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass revisionEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass labelEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass historyLogEntryEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass branchEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass historyCriteriaEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass revisionLogEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass labelLogEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
     * EPackage.Registry} by the package package URI value.
     * <p>
     * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package, if one already exists. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see com.metamatrix.metamodels.history.HistoryPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private HistoryPackageImpl() {
        super(eNS_URI, HistoryFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends. Simple
     * dependencies are satisfied by calling this method on all dependent packages before doing anything else. This method drives
     * initialization for interdependent packages directly, in parallel with this package, itself.
     * <p>
     * Of this package and its interdependencies, all packages which have not yet been registered by their URI values are first
     * created and registered. The packages are then initialized in two steps: meta-model objects for all of the packages are
     * created before any are initialized, since one package's meta-model objects may refer to those of another.
     * <p>
     * Invocation of this method will not affect any packages that have already been initialized. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static HistoryPackage init() {
        if (isInited) return (HistoryPackage)EPackage.Registry.INSTANCE.getEPackage(HistoryPackage.eNS_URI);

        // Obtain or create and register package
        HistoryPackageImpl theHistoryPackage = (HistoryPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof HistoryPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new HistoryPackageImpl());

        isInited = true;

        // Create package meta-data objects
        theHistoryPackage.createPackageContents();

        // Initialize created meta-data
        theHistoryPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theHistoryPackage.freeze();

        return theHistoryPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getHistoryLog() {
        return historyLogEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getHistoryLog_Name() {
        return (EAttribute)historyLogEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getHistoryLog_Uri() {
        return (EAttribute)historyLogEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getHistoryLog_HistoryCriteria() {
        return (EReference)historyLogEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getRevision() {
        return revisionEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getRevision_Comment() {
        return (EAttribute)revisionEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getRevision_Version() {
        return (EAttribute)revisionEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getRevision_HistoryLog() {
        return (EReference)revisionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getRevision_Branch() {
        return (EReference)revisionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getRevision_Label() {
        return (EReference)revisionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getRevision_NextRevision() {
        return (EReference)revisionEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getRevision_PreviousRevision() {
        return (EReference)revisionEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getLabel() {
        return labelEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getLabel_Tag() {
        return (EAttribute)labelEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getLabel_Revision() {
        return (EReference)labelEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getLabel_HistoryLog() {
        return (EReference)labelEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getHistoryLogEntry() {
        return historyLogEntryEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getHistoryLogEntry_Timestamp() {
        return (EAttribute)historyLogEntryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getHistoryLogEntry_User() {
        return (EAttribute)historyLogEntryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getBranch() {
        return branchEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getBranch_Version() {
        return (EAttribute)branchEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getBranch_PreviousRevision() {
        return (EReference)branchEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getHistoryCriteria() {
        return historyCriteriaEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getHistoryCriteria_FromDate() {
        return (EAttribute)historyCriteriaEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getHistoryCriteria_ToDate() {
        return (EAttribute)historyCriteriaEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getHistoryCriteria_User() {
        return (EAttribute)historyCriteriaEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getHistoryCriteria_IncludeLabels() {
        return (EAttribute)historyCriteriaEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getHistoryCriteria_OnlyLabels() {
        return (EAttribute)historyCriteriaEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getHistoryCriteria_HistoryLog() {
        return (EReference)historyCriteriaEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getRevisionLog() {
        return revisionLogEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getRevisionLog_FirstRevision() {
        return (EReference)revisionLogEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getLabelLog() {
        return labelLogEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getLabelLog_Labels() {
        return (EReference)labelLogEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public HistoryFactory getHistoryFactory() {
        return (HistoryFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        historyLogEClass = createEClass(HISTORY_LOG);
        createEReference(historyLogEClass, HISTORY_LOG__HISTORY_CRITERIA);
        createEAttribute(historyLogEClass, HISTORY_LOG__NAME);
        createEAttribute(historyLogEClass, HISTORY_LOG__URI);

        revisionEClass = createEClass(REVISION);
        createEReference(revisionEClass, REVISION__HISTORY_LOG);
        createEReference(revisionEClass, REVISION__BRANCH);
        createEReference(revisionEClass, REVISION__LABEL);
        createEReference(revisionEClass, REVISION__NEXT_REVISION);
        createEReference(revisionEClass, REVISION__PREVIOUS_REVISION);
        createEAttribute(revisionEClass, REVISION__COMMENT);
        createEAttribute(revisionEClass, REVISION__VERSION);

        labelEClass = createEClass(LABEL);
        createEReference(labelEClass, LABEL__REVISION);
        createEReference(labelEClass, LABEL__HISTORY_LOG);
        createEAttribute(labelEClass, LABEL__TAG);

        historyLogEntryEClass = createEClass(HISTORY_LOG_ENTRY);
        createEAttribute(historyLogEntryEClass, HISTORY_LOG_ENTRY__TIMESTAMP);
        createEAttribute(historyLogEntryEClass, HISTORY_LOG_ENTRY__USER);

        branchEClass = createEClass(BRANCH);
        createEReference(branchEClass, BRANCH__PREVIOUS_REVISION);
        createEAttribute(branchEClass, BRANCH__VERSION);

        historyCriteriaEClass = createEClass(HISTORY_CRITERIA);
        createEReference(historyCriteriaEClass, HISTORY_CRITERIA__HISTORY_LOG);
        createEAttribute(historyCriteriaEClass, HISTORY_CRITERIA__FROM_DATE);
        createEAttribute(historyCriteriaEClass, HISTORY_CRITERIA__TO_DATE);
        createEAttribute(historyCriteriaEClass, HISTORY_CRITERIA__USER);
        createEAttribute(historyCriteriaEClass, HISTORY_CRITERIA__INCLUDE_LABELS);
        createEAttribute(historyCriteriaEClass, HISTORY_CRITERIA__ONLY_LABELS);

        revisionLogEClass = createEClass(REVISION_LOG);
        createEReference(revisionLogEClass, REVISION_LOG__FIRST_REVISION);

        labelLogEClass = createEClass(LABEL_LOG);
        createEReference(labelLogEClass, LABEL_LOG__LABELS);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any invocation
     * but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Add supertypes to classes
        revisionEClass.getESuperTypes().add(this.getHistoryLogEntry());
        labelEClass.getESuperTypes().add(this.getHistoryLogEntry());
        branchEClass.getESuperTypes().add(this.getHistoryLogEntry());
        revisionLogEClass.getESuperTypes().add(this.getHistoryLog());
        labelLogEClass.getESuperTypes().add(this.getHistoryLog());

        // Initialize classes and features; add operations and parameters
        initEClass(historyLogEClass, HistoryLog.class, "HistoryLog", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getHistoryLog_HistoryCriteria(),
                       this.getHistoryCriteria(),
                       this.getHistoryCriteria_HistoryLog(),
                       "historyCriteria", null, 0, 1, HistoryLog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHistoryLog_Name(),
                       ecorePackage.getEString(),
                       "name", null, 0, 1, HistoryLog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHistoryLog_Uri(),
                       ecorePackage.getEString(),
                       "uri", null, 0, 1, HistoryLog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(revisionEClass, Revision.class, "Revision", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getRevision_HistoryLog(),
                       this.getRevisionLog(),
                       this.getRevisionLog_FirstRevision(),
                       "historyLog", null, 1, 1, Revision.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRevision_Branch(),
                       this.getBranch(),
                       this.getBranch_PreviousRevision(),
                       "branch", null, 0, -1, Revision.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRevision_Label(),
                       this.getLabel(),
                       this.getLabel_Revision(),
                       "label", null, 0, -1, Revision.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRevision_NextRevision(),
                       this.getRevision(),
                       this.getRevision_PreviousRevision(),
                       "nextRevision", null, 1, 1, Revision.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getRevision_PreviousRevision(),
                       this.getRevision(),
                       this.getRevision_NextRevision(),
                       "previousRevision", null, 0, 1, Revision.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getRevision_Comment(),
                       ecorePackage.getEString(),
                       "comment", null, 0, 1, Revision.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getRevision_Version(),
                       ecorePackage.getEString(),
                       "version", null, 0, 1, Revision.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(labelEClass, Label.class, "Label", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getLabel_Revision(),
                       this.getRevision(),
                       this.getRevision_Label(),
                       "revision", null, 1, 1, Label.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getLabel_HistoryLog(),
                       this.getLabelLog(),
                       this.getLabelLog_Labels(),
                       "historyLog", null, 1, 1, Label.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getLabel_Tag(),
                       ecorePackage.getEString(),
                       "tag", null, 0, 1, Label.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(historyLogEntryEClass,
                   HistoryLogEntry.class,
                   "HistoryLogEntry", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getHistoryLogEntry_Timestamp(),
                       ecorePackage.getEString(),
                       "timestamp", null, 0, 1, HistoryLogEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHistoryLogEntry_User(),
                       ecorePackage.getEString(),
                       "user", null, 0, 1, HistoryLogEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(branchEClass, Branch.class, "Branch", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getBranch_PreviousRevision(),
                       this.getRevision(),
                       this.getRevision_Branch(),
                       "previousRevision", null, 1, 1, Branch.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getBranch_Version(),
                       ecorePackage.getEString(),
                       "version", null, 0, 1, Branch.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(historyCriteriaEClass,
                   HistoryCriteria.class,
                   "HistoryCriteria", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getHistoryCriteria_HistoryLog(),
                       this.getHistoryLog(),
                       this.getHistoryLog_HistoryCriteria(),
                       "historyLog", null, 1, 1, HistoryCriteria.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHistoryCriteria_FromDate(),
                       ecorePackage.getEString(),
                       "fromDate", null, 0, 1, HistoryCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHistoryCriteria_ToDate(),
                       ecorePackage.getEString(),
                       "toDate", null, 0, 1, HistoryCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHistoryCriteria_User(),
                       ecorePackage.getEString(),
                       "user", null, 0, 1, HistoryCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHistoryCriteria_IncludeLabels(),
                       ecorePackage.getEBoolean(),
                       "includeLabels", null, 0, 1, HistoryCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHistoryCriteria_OnlyLabels(),
                       ecorePackage.getEBoolean(),
                       "onlyLabels", null, 0, 1, HistoryCriteria.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(revisionLogEClass, RevisionLog.class, "RevisionLog", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getRevisionLog_FirstRevision(),
                       this.getRevision(),
                       this.getRevision_HistoryLog(),
                       "firstRevision", null, 1, 1, RevisionLog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(labelLogEClass, LabelLog.class, "LabelLog", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getLabelLog_Labels(),
                       this.getLabel(),
                       this.getLabel_HistoryLog(),
                       "labels", null, 1, -1, LabelLog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} // HistoryPackageImpl
