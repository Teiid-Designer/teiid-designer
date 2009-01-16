/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.compare.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.mapping.Mapping;

import com.metamatrix.modeler.compare.ComparePackage;
import com.metamatrix.modeler.compare.DifferenceReport;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Difference Report</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceReportImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceReportImpl#getTotalAdditions <em>Total Additions</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceReportImpl#getTotalDeletions <em>Total Deletions</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceReportImpl#getTotalChanges <em>Total Changes</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceReportImpl#getAnalysisTime <em>Analysis Time</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceReportImpl#getSourceUri <em>Source Uri</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceReportImpl#getResultUri <em>Result Uri</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceReportImpl#getMapping <em>Mapping</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DifferenceReportImpl extends EObjectImpl implements DifferenceReport {
    /**
     * The default value of the '{@link #getTitle() <em>Title</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTitle()
     * @generated
     * @ordered
     */
    protected static final String TITLE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTitle()
     * @generated
     * @ordered
     */
    protected String title = TITLE_EDEFAULT;

    /**
     * The default value of the '{@link #getTotalAdditions() <em>Total Additions</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTotalAdditions()
     * @generated
     * @ordered
     */
    protected static final int TOTAL_ADDITIONS_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getTotalAdditions() <em>Total Additions</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTotalAdditions()
     * @generated
     * @ordered
     */
    protected int totalAdditions = TOTAL_ADDITIONS_EDEFAULT;

    /**
     * The default value of the '{@link #getTotalDeletions() <em>Total Deletions</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTotalDeletions()
     * @generated
     * @ordered
     */
    protected static final int TOTAL_DELETIONS_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getTotalDeletions() <em>Total Deletions</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTotalDeletions()
     * @generated
     * @ordered
     */
    protected int totalDeletions = TOTAL_DELETIONS_EDEFAULT;

    /**
     * The default value of the '{@link #getTotalChanges() <em>Total Changes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTotalChanges()
     * @generated
     * @ordered
     */
    protected static final int TOTAL_CHANGES_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getTotalChanges() <em>Total Changes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTotalChanges()
     * @generated
     * @ordered
     */
    protected int totalChanges = TOTAL_CHANGES_EDEFAULT;

    /**
     * The default value of the '{@link #getAnalysisTime() <em>Analysis Time</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAnalysisTime()
     * @generated
     * @ordered
     */
    protected static final long ANALYSIS_TIME_EDEFAULT = 0L;

    /**
     * The cached value of the '{@link #getAnalysisTime() <em>Analysis Time</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAnalysisTime()
     * @generated
     * @ordered
     */
    protected long analysisTime = ANALYSIS_TIME_EDEFAULT;

    /**
     * The default value of the '{@link #getSourceUri() <em>Source Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSourceUri()
     * @generated
     * @ordered
     */
    protected static final String SOURCE_URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getSourceUri() <em>Source Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSourceUri()
     * @generated
     * @ordered
     */
    protected String sourceUri = SOURCE_URI_EDEFAULT;

    /**
     * The default value of the '{@link #getResultUri() <em>Result Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResultUri()
     * @generated
     * @ordered
     */
    protected static final String RESULT_URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getResultUri() <em>Result Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResultUri()
     * @generated
     * @ordered
     */
    protected String resultUri = RESULT_URI_EDEFAULT;

    /**
     * The cached value of the '{@link #getMapping() <em>Mapping</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMapping()
     * @generated
     * @ordered
     */
    protected Mapping mapping = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DifferenceReportImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ComparePackage.eINSTANCE.getDifferenceReport();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTitle() {
        return title;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTitle(String newTitle) {
        String oldTitle = title;
        title = newTitle;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_REPORT__TITLE, oldTitle, title));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getTotalAdditions() {
        return totalAdditions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTotalAdditions(int newTotalAdditions) {
        int oldTotalAdditions = totalAdditions;
        totalAdditions = newTotalAdditions;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_REPORT__TOTAL_ADDITIONS, oldTotalAdditions, totalAdditions));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getTotalDeletions() {
        return totalDeletions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTotalDeletions(int newTotalDeletions) {
        int oldTotalDeletions = totalDeletions;
        totalDeletions = newTotalDeletions;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_REPORT__TOTAL_DELETIONS, oldTotalDeletions, totalDeletions));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getTotalChanges() {
        return totalChanges;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTotalChanges(int newTotalChanges) {
        int oldTotalChanges = totalChanges;
        totalChanges = newTotalChanges;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_REPORT__TOTAL_CHANGES, oldTotalChanges, totalChanges));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public long getAnalysisTime() {
        return analysisTime;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAnalysisTime(long newAnalysisTime) {
        long oldAnalysisTime = analysisTime;
        analysisTime = newAnalysisTime;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_REPORT__ANALYSIS_TIME, oldAnalysisTime, analysisTime));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getSourceUri() {
        return sourceUri;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSourceUri(String newSourceUri) {
        String oldSourceUri = sourceUri;
        sourceUri = newSourceUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_REPORT__SOURCE_URI, oldSourceUri, sourceUri));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getResultUri() {
        return resultUri;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setResultUri(String newResultUri) {
        String oldResultUri = resultUri;
        resultUri = newResultUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_REPORT__RESULT_URI, oldResultUri, resultUri));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Mapping getMapping() {
        return mapping;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetMapping(Mapping newMapping, NotificationChain msgs) {
        Mapping oldMapping = mapping;
        mapping = newMapping;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_REPORT__MAPPING, oldMapping, newMapping);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMapping(Mapping newMapping) {
        if (newMapping != mapping) {
            NotificationChain msgs = null;
            if (mapping != null)
                msgs = ((InternalEObject)mapping).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ComparePackage.DIFFERENCE_REPORT__MAPPING, null, msgs);
            if (newMapping != null)
                msgs = ((InternalEObject)newMapping).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ComparePackage.DIFFERENCE_REPORT__MAPPING, null, msgs);
            msgs = basicSetMapping(newMapping, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_REPORT__MAPPING, newMapping, newMapping));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case ComparePackage.DIFFERENCE_REPORT__MAPPING:
                    return basicSetMapping(null, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ComparePackage.DIFFERENCE_REPORT__TITLE:
                return getTitle();
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_ADDITIONS:
                return new Integer(getTotalAdditions());
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_DELETIONS:
                return new Integer(getTotalDeletions());
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_CHANGES:
                return new Integer(getTotalChanges());
            case ComparePackage.DIFFERENCE_REPORT__ANALYSIS_TIME:
                return new Long(getAnalysisTime());
            case ComparePackage.DIFFERENCE_REPORT__SOURCE_URI:
                return getSourceUri();
            case ComparePackage.DIFFERENCE_REPORT__RESULT_URI:
                return getResultUri();
            case ComparePackage.DIFFERENCE_REPORT__MAPPING:
                return getMapping();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ComparePackage.DIFFERENCE_REPORT__TITLE:
                setTitle((String)newValue);
                return;
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_ADDITIONS:
                setTotalAdditions(((Integer)newValue).intValue());
                return;
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_DELETIONS:
                setTotalDeletions(((Integer)newValue).intValue());
                return;
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_CHANGES:
                setTotalChanges(((Integer)newValue).intValue());
                return;
            case ComparePackage.DIFFERENCE_REPORT__ANALYSIS_TIME:
                setAnalysisTime(((Long)newValue).longValue());
                return;
            case ComparePackage.DIFFERENCE_REPORT__SOURCE_URI:
                setSourceUri((String)newValue);
                return;
            case ComparePackage.DIFFERENCE_REPORT__RESULT_URI:
                setResultUri((String)newValue);
                return;
            case ComparePackage.DIFFERENCE_REPORT__MAPPING:
                setMapping((Mapping)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ComparePackage.DIFFERENCE_REPORT__TITLE:
                setTitle(TITLE_EDEFAULT);
                return;
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_ADDITIONS:
                setTotalAdditions(TOTAL_ADDITIONS_EDEFAULT);
                return;
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_DELETIONS:
                setTotalDeletions(TOTAL_DELETIONS_EDEFAULT);
                return;
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_CHANGES:
                setTotalChanges(TOTAL_CHANGES_EDEFAULT);
                return;
            case ComparePackage.DIFFERENCE_REPORT__ANALYSIS_TIME:
                setAnalysisTime(ANALYSIS_TIME_EDEFAULT);
                return;
            case ComparePackage.DIFFERENCE_REPORT__SOURCE_URI:
                setSourceUri(SOURCE_URI_EDEFAULT);
                return;
            case ComparePackage.DIFFERENCE_REPORT__RESULT_URI:
                setResultUri(RESULT_URI_EDEFAULT);
                return;
            case ComparePackage.DIFFERENCE_REPORT__MAPPING:
                setMapping((Mapping)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ComparePackage.DIFFERENCE_REPORT__TITLE:
                return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_ADDITIONS:
                return totalAdditions != TOTAL_ADDITIONS_EDEFAULT;
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_DELETIONS:
                return totalDeletions != TOTAL_DELETIONS_EDEFAULT;
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_CHANGES:
                return totalChanges != TOTAL_CHANGES_EDEFAULT;
            case ComparePackage.DIFFERENCE_REPORT__ANALYSIS_TIME:
                return analysisTime != ANALYSIS_TIME_EDEFAULT;
            case ComparePackage.DIFFERENCE_REPORT__SOURCE_URI:
                return SOURCE_URI_EDEFAULT == null ? sourceUri != null : !SOURCE_URI_EDEFAULT.equals(sourceUri);
            case ComparePackage.DIFFERENCE_REPORT__RESULT_URI:
                return RESULT_URI_EDEFAULT == null ? resultUri != null : !RESULT_URI_EDEFAULT.equals(resultUri);
            case ComparePackage.DIFFERENCE_REPORT__MAPPING:
                return mapping != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (title: "); //$NON-NLS-1$
        result.append(title);
        result.append(", totalAdditions: "); //$NON-NLS-1$
        result.append(totalAdditions);
        result.append(", totalDeletions: "); //$NON-NLS-1$
        result.append(totalDeletions);
        result.append(", totalChanges: "); //$NON-NLS-1$
        result.append(totalChanges);
        result.append(", analysisTime: "); //$NON-NLS-1$
        result.append(analysisTime);
        result.append(", sourceUri: "); //$NON-NLS-1$
        result.append(sourceUri);
        result.append(", resultUri: "); //$NON-NLS-1$
        result.append(resultUri);
        result.append(')');
        return result.toString();
    }

} //DifferenceReportImpl
