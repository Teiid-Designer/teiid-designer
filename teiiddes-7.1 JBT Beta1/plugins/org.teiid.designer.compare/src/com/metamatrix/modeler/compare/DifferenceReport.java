/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Difference Report</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.compare.DifferenceReport#getTitle <em>Title</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.DifferenceReport#getTotalAdditions <em>Total Additions</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.DifferenceReport#getTotalDeletions <em>Total Deletions</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.DifferenceReport#getTotalChanges <em>Total Changes</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.DifferenceReport#getAnalysisTime <em>Analysis Time</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.DifferenceReport#getSourceUri <em>Source Uri</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.DifferenceReport#getResultUri <em>Result Uri</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.DifferenceReport#getMapping <em>Mapping</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceReport()
 * @model
 * @generated
 */
public interface DifferenceReport extends EObject{
    /**
     * Returns the value of the '<em><b>Title</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Title</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Title</em>' attribute.
     * @see #setTitle(String)
     * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceReport_Title()
     * @model
     * @generated
     */
    String getTitle();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.DifferenceReport#getTitle <em>Title</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Title</em>' attribute.
     * @see #getTitle()
     * @generated
     */
    void setTitle(String value);

    /**
     * Returns the value of the '<em><b>Total Additions</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Total Additions</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Total Additions</em>' attribute.
     * @see #setTotalAdditions(int)
     * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceReport_TotalAdditions()
     * @model
     * @generated
     */
    int getTotalAdditions();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.DifferenceReport#getTotalAdditions <em>Total Additions</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Total Additions</em>' attribute.
     * @see #getTotalAdditions()
     * @generated
     */
    void setTotalAdditions(int value);

    /**
     * Returns the value of the '<em><b>Total Deletions</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Total Deletions</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Total Deletions</em>' attribute.
     * @see #setTotalDeletions(int)
     * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceReport_TotalDeletions()
     * @model
     * @generated
     */
    int getTotalDeletions();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.DifferenceReport#getTotalDeletions <em>Total Deletions</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Total Deletions</em>' attribute.
     * @see #getTotalDeletions()
     * @generated
     */
    void setTotalDeletions(int value);

    /**
     * Returns the value of the '<em><b>Total Changes</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Total Changes</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Total Changes</em>' attribute.
     * @see #setTotalChanges(int)
     * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceReport_TotalChanges()
     * @model
     * @generated
     */
    int getTotalChanges();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.DifferenceReport#getTotalChanges <em>Total Changes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Total Changes</em>' attribute.
     * @see #getTotalChanges()
     * @generated
     */
    void setTotalChanges(int value);

    /**
     * Returns the value of the '<em><b>Analysis Time</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Analysis Time</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Analysis Time</em>' attribute.
     * @see #setAnalysisTime(long)
     * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceReport_AnalysisTime()
     * @model
     * @generated
     */
    long getAnalysisTime();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.DifferenceReport#getAnalysisTime <em>Analysis Time</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Analysis Time</em>' attribute.
     * @see #getAnalysisTime()
     * @generated
     */
    void setAnalysisTime(long value);

    /**
     * Returns the value of the '<em><b>Source Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Source Uri</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Source Uri</em>' attribute.
     * @see #setSourceUri(String)
     * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceReport_SourceUri()
     * @model
     * @generated
     */
    String getSourceUri();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.DifferenceReport#getSourceUri <em>Source Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source Uri</em>' attribute.
     * @see #getSourceUri()
     * @generated
     */
    void setSourceUri(String value);

    /**
     * Returns the value of the '<em><b>Result Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Result Uri</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Result Uri</em>' attribute.
     * @see #setResultUri(String)
     * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceReport_ResultUri()
     * @model
     * @generated
     */
    String getResultUri();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.DifferenceReport#getResultUri <em>Result Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Result Uri</em>' attribute.
     * @see #getResultUri()
     * @generated
     */
    void setResultUri(String value);

    /**
     * Returns the value of the '<em><b>Mapping</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mapping</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mapping</em>' containment reference.
     * @see #setMapping(Mapping)
     * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceReport_Mapping()
     * @model containment="true" required="true"
     * @generated
     */
    Mapping getMapping();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.DifferenceReport#getMapping <em>Mapping</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mapping</em>' containment reference.
     * @see #getMapping()
     * @generated
     */
    void setMapping(Mapping value);

} // DifferenceReport
