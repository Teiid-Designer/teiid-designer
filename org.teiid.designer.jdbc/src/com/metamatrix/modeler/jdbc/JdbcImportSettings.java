/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Import Settings</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getSource <em>Source</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getOptions <em>Options</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isCreateCatalogsInModel <em>Create Catalogs In Model</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isCreateSchemasInModel <em>Create Schemas In Model</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getConvertCaseInModel <em>Convert Case In Model</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getGenerateSourceNamesInModel <em>Generate Source Names In Model</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getIncludedCatalogPaths <em>Included Catalog Paths</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getIncludedSchemaPaths <em>Included Schema Paths</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getExcludedObjectPaths <em>Excluded Object Paths</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeForeignKeys <em>Include Foreign Keys</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeIndexes <em>Include Indexes</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeProcedures <em>Include Procedures</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeApproximateIndexes <em>Include Approximate Indexes</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeUniqueIndexes <em>Include Unique Indexes</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getIncludedTableTypes <em>Included Table Types</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings()
 * @model
 * @generated
 */
public interface JdbcImportSettings extends EObject{
    /**
     * Returns the value of the '<em><b>Create Catalogs In Model</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Create Catalogs In Model</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Create Catalogs In Model</em>' attribute.
     * @see #setCreateCatalogsInModel(boolean)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_CreateCatalogsInModel()
     * @model default="true"
     * @generated
     */
    boolean isCreateCatalogsInModel();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isCreateCatalogsInModel <em>Create Catalogs In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Create Catalogs In Model</em>' attribute.
     * @see #isCreateCatalogsInModel()
     * @generated
     */
    void setCreateCatalogsInModel(boolean value);

    /**
     * Returns the value of the '<em><b>Create Schemas In Model</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Create Schemas In Model</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Create Schemas In Model</em>' attribute.
     * @see #setCreateSchemasInModel(boolean)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_CreateSchemasInModel()
     * @model default="true"
     * @generated
     */
    boolean isCreateSchemasInModel();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isCreateSchemasInModel <em>Create Schemas In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Create Schemas In Model</em>' attribute.
     * @see #isCreateSchemasInModel()
     * @generated
     */
    void setCreateSchemasInModel(boolean value);

    /**
     * Returns the value of the '<em><b>Convert Case In Model</b></em>' attribute.
     * The literals are from the enumeration {@link com.metamatrix.modeler.jdbc.CaseConversion}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Convert Case In Model</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Convert Case In Model</em>' attribute.
     * @see com.metamatrix.modeler.jdbc.CaseConversion
     * @see #setConvertCaseInModel(CaseConversion)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_ConvertCaseInModel()
     * @model
     * @generated
     */
    CaseConversion getConvertCaseInModel();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getConvertCaseInModel <em>Convert Case In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Convert Case In Model</em>' attribute.
     * @see com.metamatrix.modeler.jdbc.CaseConversion
     * @see #getConvertCaseInModel()
     * @generated
     */
    void setConvertCaseInModel(CaseConversion value);

    /**
     * Returns the value of the '<em><b>Generate Source Names In Model</b></em>' attribute.
     * The default value is <code>"UNQUALIFIED"</code>.
     * The literals are from the enumeration {@link com.metamatrix.modeler.jdbc.SourceNames}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Generate Source Names In Model</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Generate Source Names In Model</em>' attribute.
     * @see com.metamatrix.modeler.jdbc.SourceNames
     * @see #setGenerateSourceNamesInModel(SourceNames)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_GenerateSourceNamesInModel()
     * @model default="UNQUALIFIED"
     * @generated
     */
    SourceNames getGenerateSourceNamesInModel();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getGenerateSourceNamesInModel <em>Generate Source Names In Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Generate Source Names In Model</em>' attribute.
     * @see com.metamatrix.modeler.jdbc.SourceNames
     * @see #getGenerateSourceNamesInModel()
     * @generated
     */
    void setGenerateSourceNamesInModel(SourceNames value);

    /**
     * Returns the value of the '<em><b>Included Catalog Paths</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Included Catalog Paths</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Included Catalog Paths</em>' attribute list.
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_IncludedCatalogPaths()
     * @model type="java.lang.String"
     * @generated
     */
    EList getIncludedCatalogPaths();

    /**
     * Returns the value of the '<em><b>Included Schema Paths</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Included Schema Paths</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Included Schema Paths</em>' attribute list.
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_IncludedSchemaPaths()
     * @model type="java.lang.String"
     * @generated
     */
    EList getIncludedSchemaPaths();

    /**
     * Returns the value of the '<em><b>Excluded Object Paths</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Excluded Object Paths</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Excluded Object Paths</em>' attribute list.
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_ExcludedObjectPaths()
     * @model type="java.lang.String"
     * @generated
     */
    EList getExcludedObjectPaths();

    /**
     * Returns the value of the '<em><b>Include Foreign Keys</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Include Foreign Keys</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Include Foreign Keys</em>' attribute.
     * @see #setIncludeForeignKeys(boolean)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_IncludeForeignKeys()
     * @model default="true"
     * @generated
     */
    boolean isIncludeForeignKeys();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeForeignKeys <em>Include Foreign Keys</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Include Foreign Keys</em>' attribute.
     * @see #isIncludeForeignKeys()
     * @generated
     */
    void setIncludeForeignKeys(boolean value);

    /**
     * Returns the value of the '<em><b>Include Indexes</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Include Indexes</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Include Indexes</em>' attribute.
     * @see #setIncludeIndexes(boolean)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_IncludeIndexes()
     * @model default="true"
     * @generated
     */
    boolean isIncludeIndexes();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeIndexes <em>Include Indexes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Include Indexes</em>' attribute.
     * @see #isIncludeIndexes()
     * @generated
     */
    void setIncludeIndexes(boolean value);

    /**
     * Returns the value of the '<em><b>Include Procedures</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Include Procedures</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Include Procedures</em>' attribute.
     * @see #setIncludeProcedures(boolean)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_IncludeProcedures()
     * @model default="false"
     * @generated
     */
    boolean isIncludeProcedures();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeProcedures <em>Include Procedures</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Include Procedures</em>' attribute.
     * @see #isIncludeProcedures()
     * @generated
     */
    void setIncludeProcedures(boolean value);

    /**
     * Returns the value of the '<em><b>Include Approximate Indexes</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Include Approximate Indexes</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Include Approximate Indexes</em>' attribute.
     * @see #setIncludeApproximateIndexes(boolean)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_IncludeApproximateIndexes()
     * @model default="true"
     * @generated
     */
    boolean isIncludeApproximateIndexes();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeApproximateIndexes <em>Include Approximate Indexes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Include Approximate Indexes</em>' attribute.
     * @see #isIncludeApproximateIndexes()
     * @generated
     */
    void setIncludeApproximateIndexes(boolean value);

    /**
     * Returns the value of the '<em><b>Include Unique Indexes</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Include Unique Indexes</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Include Unique Indexes</em>' attribute.
     * @see #setIncludeUniqueIndexes(boolean)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_IncludeUniqueIndexes()
     * @model default="false"
     * @generated
     */
    boolean isIncludeUniqueIndexes();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#isIncludeUniqueIndexes <em>Include Unique Indexes</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Include Unique Indexes</em>' attribute.
     * @see #isIncludeUniqueIndexes()
     * @generated
     */
    void setIncludeUniqueIndexes(boolean value);

    /**
     * Returns the value of the '<em><b>Included Table Types</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Included Table Types</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Included Table Types</em>' attribute list.
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_IncludedTableTypes()
     * @model type="java.lang.String"
     * @generated
     */
    EList getIncludedTableTypes();

    /**
     * Returns the value of the '<em><b>Source</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.modeler.jdbc.JdbcSource#getImportSettings <em>Import Settings</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Source</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Source</em>' container reference.
     * @see #setSource(JdbcSource)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_Source()
     * @see com.metamatrix.modeler.jdbc.JdbcSource#getImportSettings
     * @model opposite="importSettings"
     * @generated
     */
    JdbcSource getSource();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getSource <em>Source</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Source</em>' container reference.
     * @see #getSource()
     * @generated
     */
    void setSource(JdbcSource value);

    /**
     * Returns the value of the '<em><b>Options</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.modeler.jdbc.JdbcImportOptions}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.modeler.jdbc.JdbcImportOptions#getImportSettings <em>Import Settings</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Options</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Options</em>' containment reference list.
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportSettings_Options()
     * @see com.metamatrix.modeler.jdbc.JdbcImportOptions#getImportSettings
     * @model type="com.metamatrix.modeler.jdbc.JdbcImportOptions" opposite="importSettings" containment="true"
     * @generated
     */
    EList getOptions();

} // JdbcImportSettings
