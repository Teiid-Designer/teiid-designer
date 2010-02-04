/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest;

import java.util.Date;
import org.eclipse.emf.common.util.EList;
import com.metamatrix.metamodels.core.ModelImport;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getVersion <em>Version</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getUri <em>Uri</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#isVisible <em>Visible</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getAccessibility <em>Accessibility</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getTimeLastSynchronized <em>Time Last Synchronized</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getTimeLastSynchronizedAsDate <em>Time Last Synchronized As Date</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getChecksum <em>Checksum</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getVirtualDatabase <em>Virtual Database</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getUses <em>Uses</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getUsedBy <em>Used By</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelReference#getModelSource <em>Model Source</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference()
 * @model
 * @generated
 */
public interface ModelReference extends ModelImport, ProblemMarkerContainer{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Version</em>' attribute.
     * @see #setVersion(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_Version()
     * @model
     * @generated
     */
    String getVersion();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     * @generated
     */
    void setVersion(String value);

    /**
     * Returns the value of the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Uri</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Uri</em>' attribute.
     * @see #setUri(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_Uri()
     * @model
     * @generated
     */
    String getUri();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getUri <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Uri</em>' attribute.
     * @see #getUri()
     * @generated
     */
    void setUri(String value);

    /**
     * Returns the value of the '<em><b>Visible</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Visible</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Visible</em>' attribute.
     * @see #setVisible(boolean)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_Visible()
     * @model default="true"
     * @generated
     */
	boolean isVisible();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelReference#isVisible <em>Visible</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Visible</em>' attribute.
     * @see #isVisible()
     * @generated
     */
	void setVisible(boolean value);

    /**
     * Returns the value of the '<em><b>Accessibility</b></em>' attribute.
     * The default value is <code>"PUBLIC"</code>.
     * The literals are from the enumeration {@link com.metamatrix.vdb.edit.manifest.ModelAccessibility}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Accessibility</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Accessibility</em>' attribute.
     * @see com.metamatrix.vdb.edit.manifest.ModelAccessibility
     * @see #setAccessibility(ModelAccessibility)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_Accessibility()
     * @model default="PUBLIC"
     * @generated
     */
	ModelAccessibility getAccessibility();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getAccessibility <em>Accessibility</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Accessibility</em>' attribute.
     * @see com.metamatrix.vdb.edit.manifest.ModelAccessibility
     * @see #getAccessibility()
     * @generated
     */
	void setAccessibility(ModelAccessibility value);

    /**
     * Returns the value of the '<em><b>Time Last Synchronized</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time Last Synchronized</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Time Last Synchronized</em>' attribute.
     * @see #setTimeLastSynchronized(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_TimeLastSynchronized()
     * @model
     * @generated
     */
	String getTimeLastSynchronized();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getTimeLastSynchronized <em>Time Last Synchronized</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time Last Synchronized</em>' attribute.
     * @see #getTimeLastSynchronized()
     * @generated
     */
	void setTimeLastSynchronized(String value);

    /**
     * Returns the value of the '<em><b>Time Last Synchronized As Date</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time Last Synchronized As Date</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Time Last Synchronized As Date</em>' attribute.
     * @see #setTimeLastSynchronizedAsDate(Date)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_TimeLastSynchronizedAsDate()
     * @model dataType="com.metamatrix.vdb.edit.manifest.JavaDate" volatile="true"
     * @generated
     */
	Date getTimeLastSynchronizedAsDate();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getTimeLastSynchronizedAsDate <em>Time Last Synchronized As Date</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time Last Synchronized As Date</em>' attribute.
     * @see #getTimeLastSynchronizedAsDate()
     * @generated
     */
	void setTimeLastSynchronizedAsDate(Date value);

    /**
     * Returns the value of the '<em><b>Checksum</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Checksum</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Checksum</em>' attribute.
     * @see #setChecksum(long)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_Checksum()
     * @model
     * @generated
     */
	long getChecksum();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getChecksum <em>Checksum</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Checksum</em>' attribute.
     * @see #getChecksum()
     * @generated
     */
	void setChecksum(long value);

    /**
     * Returns the value of the '<em><b>Virtual Database</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getModels <em>Models</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Virtual Database</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Virtual Database</em>' container reference.
     * @see #setVirtualDatabase(VirtualDatabase)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_VirtualDatabase()
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getModels
     * @model opposite="models" required="true"
     * @generated
     */
    VirtualDatabase getVirtualDatabase();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getVirtualDatabase <em>Virtual Database</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Virtual Database</em>' container reference.
     * @see #getVirtualDatabase()
     * @generated
     */
    void setVirtualDatabase(VirtualDatabase value);

    /**
     * Returns the value of the '<em><b>Uses</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.vdb.edit.manifest.ModelReference}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getUsedBy <em>Used By</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Uses</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Uses</em>' reference list.
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_Uses()
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getUsedBy
     * @model type="com.metamatrix.vdb.edit.manifest.ModelReference" opposite="usedBy"
     * @generated
     */
    EList getUses();

    /**
     * Returns the value of the '<em><b>Used By</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.vdb.edit.manifest.ModelReference}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getUses <em>Uses</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Used By</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Used By</em>' reference list.
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_UsedBy()
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getUses
     * @model type="com.metamatrix.vdb.edit.manifest.ModelReference" opposite="uses"
     * @generated
     */
    EList getUsedBy();

    /**
     * Returns the value of the '<em><b>Model Source</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ModelSource#getModel <em>Model</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model Source</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Model Source</em>' containment reference.
     * @see #setModelSource(ModelSource)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelReference_ModelSource()
     * @see com.metamatrix.vdb.edit.manifest.ModelSource#getModel
     * @model opposite="model" containment="true"
     * @generated
     */
    ModelSource getModelSource();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getModelSource <em>Model Source</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Model Source</em>' containment reference.
     * @see #getModelSource()
     * @generated
     */
    void setModelSource(ModelSource value);

} // ModelReference
