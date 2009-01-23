/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.history.HistoryFactory
 * @generated
 */
public interface HistoryPackage extends EPackage{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "history"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/History"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "history"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    HistoryPackage eINSTANCE = com.metamatrix.metamodels.history.impl.HistoryPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.history.impl.HistoryLogImpl <em>Log</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.history.impl.HistoryLogImpl
     * @see com.metamatrix.metamodels.history.impl.HistoryPackageImpl#getHistoryLog()
     * @generated
     */
    int HISTORY_LOG = 0;

    /**
     * The feature id for the '<em><b>History Criteria</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_LOG__HISTORY_CRITERIA = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_LOG__NAME = 1;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_LOG__URI = 2;

    /**
     * The number of structural features of the the '<em>Log</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_LOG_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.history.impl.HistoryLogEntryImpl <em>Log Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.history.impl.HistoryLogEntryImpl
     * @see com.metamatrix.metamodels.history.impl.HistoryPackageImpl#getHistoryLogEntry()
     * @generated
     */
    int HISTORY_LOG_ENTRY = 3;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_LOG_ENTRY__TIMESTAMP = 0;

    /**
     * The feature id for the '<em><b>User</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_LOG_ENTRY__USER = 1;

    /**
     * The number of structural features of the the '<em>Log Entry</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_LOG_ENTRY_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.history.impl.RevisionImpl <em>Revision</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.history.impl.RevisionImpl
     * @see com.metamatrix.metamodels.history.impl.HistoryPackageImpl#getRevision()
     * @generated
     */
    int REVISION = 1;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION__TIMESTAMP = HISTORY_LOG_ENTRY__TIMESTAMP;

    /**
     * The feature id for the '<em><b>User</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION__USER = HISTORY_LOG_ENTRY__USER;

    /**
     * The feature id for the '<em><b>History Log</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION__HISTORY_LOG = HISTORY_LOG_ENTRY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Branch</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION__BRANCH = HISTORY_LOG_ENTRY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Label</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION__LABEL = HISTORY_LOG_ENTRY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Next Revision</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION__NEXT_REVISION = HISTORY_LOG_ENTRY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Previous Revision</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION__PREVIOUS_REVISION = HISTORY_LOG_ENTRY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Comment</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION__COMMENT = HISTORY_LOG_ENTRY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION__VERSION = HISTORY_LOG_ENTRY_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the the '<em>Revision</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION_FEATURE_COUNT = HISTORY_LOG_ENTRY_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.history.impl.LabelImpl <em>Label</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.history.impl.LabelImpl
     * @see com.metamatrix.metamodels.history.impl.HistoryPackageImpl#getLabel()
     * @generated
     */
    int LABEL = 2;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL__TIMESTAMP = HISTORY_LOG_ENTRY__TIMESTAMP;

    /**
     * The feature id for the '<em><b>User</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL__USER = HISTORY_LOG_ENTRY__USER;

    /**
     * The feature id for the '<em><b>Revision</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL__REVISION = HISTORY_LOG_ENTRY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>History Log</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL__HISTORY_LOG = HISTORY_LOG_ENTRY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Tag</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL__TAG = HISTORY_LOG_ENTRY_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Label</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_FEATURE_COUNT = HISTORY_LOG_ENTRY_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.history.impl.BranchImpl <em>Branch</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.history.impl.BranchImpl
     * @see com.metamatrix.metamodels.history.impl.HistoryPackageImpl#getBranch()
     * @generated
     */
    int BRANCH = 4;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRANCH__TIMESTAMP = HISTORY_LOG_ENTRY__TIMESTAMP;

    /**
     * The feature id for the '<em><b>User</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRANCH__USER = HISTORY_LOG_ENTRY__USER;

    /**
     * The feature id for the '<em><b>Previous Revision</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRANCH__PREVIOUS_REVISION = HISTORY_LOG_ENTRY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRANCH__VERSION = HISTORY_LOG_ENTRY_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Branch</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BRANCH_FEATURE_COUNT = HISTORY_LOG_ENTRY_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.history.impl.HistoryCriteriaImpl <em>Criteria</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.history.impl.HistoryCriteriaImpl
     * @see com.metamatrix.metamodels.history.impl.HistoryPackageImpl#getHistoryCriteria()
     * @generated
     */
    int HISTORY_CRITERIA = 5;

    /**
     * The feature id for the '<em><b>History Log</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_CRITERIA__HISTORY_LOG = 0;

    /**
     * The feature id for the '<em><b>From Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_CRITERIA__FROM_DATE = 1;

    /**
     * The feature id for the '<em><b>To Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_CRITERIA__TO_DATE = 2;

    /**
     * The feature id for the '<em><b>User</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_CRITERIA__USER = 3;

    /**
     * The feature id for the '<em><b>Include Labels</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_CRITERIA__INCLUDE_LABELS = 4;

    /**
     * The feature id for the '<em><b>Only Labels</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_CRITERIA__ONLY_LABELS = 5;

    /**
     * The number of structural features of the the '<em>Criteria</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HISTORY_CRITERIA_FEATURE_COUNT = 6;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.history.impl.RevisionLogImpl <em>Revision Log</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.history.impl.RevisionLogImpl
     * @see com.metamatrix.metamodels.history.impl.HistoryPackageImpl#getRevisionLog()
     * @generated
     */
    int REVISION_LOG = 6;

    /**
     * The feature id for the '<em><b>History Criteria</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION_LOG__HISTORY_CRITERIA = HISTORY_LOG__HISTORY_CRITERIA;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION_LOG__NAME = HISTORY_LOG__NAME;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION_LOG__URI = HISTORY_LOG__URI;

    /**
     * The feature id for the '<em><b>First Revision</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION_LOG__FIRST_REVISION = HISTORY_LOG_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Revision Log</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int REVISION_LOG_FEATURE_COUNT = HISTORY_LOG_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.history.impl.LabelLogImpl <em>Label Log</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.history.impl.LabelLogImpl
     * @see com.metamatrix.metamodels.history.impl.HistoryPackageImpl#getLabelLog()
     * @generated
     */
    int LABEL_LOG = 7;

    /**
     * The feature id for the '<em><b>History Criteria</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_LOG__HISTORY_CRITERIA = HISTORY_LOG__HISTORY_CRITERIA;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_LOG__NAME = HISTORY_LOG__NAME;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_LOG__URI = HISTORY_LOG__URI;

    /**
     * The feature id for the '<em><b>Labels</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_LOG__LABELS = HISTORY_LOG_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Label Log</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LABEL_LOG_FEATURE_COUNT = HISTORY_LOG_FEATURE_COUNT + 1;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.history.HistoryLog <em>Log</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Log</em>'.
     * @see com.metamatrix.metamodels.history.HistoryLog
     * @generated
     */
    EClass getHistoryLog();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.HistoryLog#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.history.HistoryLog#getName()
     * @see #getHistoryLog()
     * @generated
     */
    EAttribute getHistoryLog_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.HistoryLog#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @see com.metamatrix.metamodels.history.HistoryLog#getUri()
     * @see #getHistoryLog()
     * @generated
     */
    EAttribute getHistoryLog_Uri();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.history.HistoryLog#getHistoryCriteria <em>History Criteria</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>History Criteria</em>'.
     * @see com.metamatrix.metamodels.history.HistoryLog#getHistoryCriteria()
     * @see #getHistoryLog()
     * @generated
     */
    EReference getHistoryLog_HistoryCriteria();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.history.Revision <em>Revision</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Revision</em>'.
     * @see com.metamatrix.metamodels.history.Revision
     * @generated
     */
    EClass getRevision();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.Revision#getComment <em>Comment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Comment</em>'.
     * @see com.metamatrix.metamodels.history.Revision#getComment()
     * @see #getRevision()
     * @generated
     */
    EAttribute getRevision_Comment();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.Revision#getVersion <em>Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Version</em>'.
     * @see com.metamatrix.metamodels.history.Revision#getVersion()
     * @see #getRevision()
     * @generated
     */
    EAttribute getRevision_Version();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.history.Revision#getHistoryLog <em>History Log</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>History Log</em>'.
     * @see com.metamatrix.metamodels.history.Revision#getHistoryLog()
     * @see #getRevision()
     * @generated
     */
    EReference getRevision_HistoryLog();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.history.Revision#getBranch <em>Branch</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Branch</em>'.
     * @see com.metamatrix.metamodels.history.Revision#getBranch()
     * @see #getRevision()
     * @generated
     */
    EReference getRevision_Branch();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.history.Revision#getLabel <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Label</em>'.
     * @see com.metamatrix.metamodels.history.Revision#getLabel()
     * @see #getRevision()
     * @generated
     */
    EReference getRevision_Label();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.history.Revision#getNextRevision <em>Next Revision</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Next Revision</em>'.
     * @see com.metamatrix.metamodels.history.Revision#getNextRevision()
     * @see #getRevision()
     * @generated
     */
    EReference getRevision_NextRevision();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.history.Revision#getPreviousRevision <em>Previous Revision</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Previous Revision</em>'.
     * @see com.metamatrix.metamodels.history.Revision#getPreviousRevision()
     * @see #getRevision()
     * @generated
     */
    EReference getRevision_PreviousRevision();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.history.Label <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Label</em>'.
     * @see com.metamatrix.metamodels.history.Label
     * @generated
     */
    EClass getLabel();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.Label#getTag <em>Tag</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Tag</em>'.
     * @see com.metamatrix.metamodels.history.Label#getTag()
     * @see #getLabel()
     * @generated
     */
    EAttribute getLabel_Tag();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.history.Label#getRevision <em>Revision</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Revision</em>'.
     * @see com.metamatrix.metamodels.history.Label#getRevision()
     * @see #getLabel()
     * @generated
     */
    EReference getLabel_Revision();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.history.Label#getHistoryLog <em>History Log</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>History Log</em>'.
     * @see com.metamatrix.metamodels.history.Label#getHistoryLog()
     * @see #getLabel()
     * @generated
     */
    EReference getLabel_HistoryLog();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.history.HistoryLogEntry <em>Log Entry</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Log Entry</em>'.
     * @see com.metamatrix.metamodels.history.HistoryLogEntry
     * @generated
     */
    EClass getHistoryLogEntry();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.HistoryLogEntry#getTimestamp <em>Timestamp</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Timestamp</em>'.
     * @see com.metamatrix.metamodels.history.HistoryLogEntry#getTimestamp()
     * @see #getHistoryLogEntry()
     * @generated
     */
    EAttribute getHistoryLogEntry_Timestamp();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.HistoryLogEntry#getUser <em>User</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>User</em>'.
     * @see com.metamatrix.metamodels.history.HistoryLogEntry#getUser()
     * @see #getHistoryLogEntry()
     * @generated
     */
    EAttribute getHistoryLogEntry_User();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.history.Branch <em>Branch</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Branch</em>'.
     * @see com.metamatrix.metamodels.history.Branch
     * @generated
     */
    EClass getBranch();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.Branch#getVersion <em>Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Version</em>'.
     * @see com.metamatrix.metamodels.history.Branch#getVersion()
     * @see #getBranch()
     * @generated
     */
    EAttribute getBranch_Version();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.history.Branch#getPreviousRevision <em>Previous Revision</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Previous Revision</em>'.
     * @see com.metamatrix.metamodels.history.Branch#getPreviousRevision()
     * @see #getBranch()
     * @generated
     */
    EReference getBranch_PreviousRevision();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.history.HistoryCriteria <em>Criteria</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Criteria</em>'.
     * @see com.metamatrix.metamodels.history.HistoryCriteria
     * @generated
     */
    EClass getHistoryCriteria();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.HistoryCriteria#getFromDate <em>From Date</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>From Date</em>'.
     * @see com.metamatrix.metamodels.history.HistoryCriteria#getFromDate()
     * @see #getHistoryCriteria()
     * @generated
     */
    EAttribute getHistoryCriteria_FromDate();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.HistoryCriteria#getToDate <em>To Date</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>To Date</em>'.
     * @see com.metamatrix.metamodels.history.HistoryCriteria#getToDate()
     * @see #getHistoryCriteria()
     * @generated
     */
    EAttribute getHistoryCriteria_ToDate();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.HistoryCriteria#getUser <em>User</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>User</em>'.
     * @see com.metamatrix.metamodels.history.HistoryCriteria#getUser()
     * @see #getHistoryCriteria()
     * @generated
     */
    EAttribute getHistoryCriteria_User();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.HistoryCriteria#isIncludeLabels <em>Include Labels</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Include Labels</em>'.
     * @see com.metamatrix.metamodels.history.HistoryCriteria#isIncludeLabels()
     * @see #getHistoryCriteria()
     * @generated
     */
    EAttribute getHistoryCriteria_IncludeLabels();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.history.HistoryCriteria#isOnlyLabels <em>Only Labels</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Only Labels</em>'.
     * @see com.metamatrix.metamodels.history.HistoryCriteria#isOnlyLabels()
     * @see #getHistoryCriteria()
     * @generated
     */
    EAttribute getHistoryCriteria_OnlyLabels();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.history.HistoryCriteria#getHistoryLog <em>History Log</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>History Log</em>'.
     * @see com.metamatrix.metamodels.history.HistoryCriteria#getHistoryLog()
     * @see #getHistoryCriteria()
     * @generated
     */
    EReference getHistoryCriteria_HistoryLog();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.history.RevisionLog <em>Revision Log</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Revision Log</em>'.
     * @see com.metamatrix.metamodels.history.RevisionLog
     * @generated
     */
    EClass getRevisionLog();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.history.RevisionLog#getFirstRevision <em>First Revision</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>First Revision</em>'.
     * @see com.metamatrix.metamodels.history.RevisionLog#getFirstRevision()
     * @see #getRevisionLog()
     * @generated
     */
    EReference getRevisionLog_FirstRevision();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.history.LabelLog <em>Label Log</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Label Log</em>'.
     * @see com.metamatrix.metamodels.history.LabelLog
     * @generated
     */
    EClass getLabelLog();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.history.LabelLog#getLabels <em>Labels</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Labels</em>'.
     * @see com.metamatrix.metamodels.history.LabelLog#getLabels()
     * @see #getLabelLog()
     * @generated
     */
    EReference getLabelLog_Labels();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    HistoryFactory getHistoryFactory();

} //HistoryPackage
