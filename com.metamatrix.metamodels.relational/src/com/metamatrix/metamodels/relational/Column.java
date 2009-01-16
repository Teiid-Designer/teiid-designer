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

package com.metamatrix.metamodels.relational;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Column</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getNativeType <em>Native Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getLength <em>Length</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#isFixedLength <em>Fixed Length</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getPrecision <em>Precision</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getScale <em>Scale</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getNullable <em>Nullable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#isAutoIncremented <em>Auto Incremented</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getMinimumValue <em>Minimum Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getMaximumValue <em>Maximum Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getFormat <em>Format</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getCharacterSetName <em>Character Set Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getCollationName <em>Collation Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#isSelectable <em>Selectable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#isUpdateable <em>Updateable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#isCaseSensitive <em>Case Sensitive</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getSearchability <em>Searchability</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#isCurrency <em>Currency</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getRadix <em>Radix</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#isSigned <em>Signed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getDistinctValueCount <em>Distinct Value Count</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getNullValueCount <em>Null Value Count</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getUniqueKeys <em>Unique Keys</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getIndexes <em>Indexes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getForeignKeys <em>Foreign Keys</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getAccessPatterns <em>Access Patterns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getOwner <em>Owner</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Column#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn()
 * @model
 * @generated
 */
public interface Column extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Native Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Native Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Native Type</em>' attribute.
     * @see #isSetNativeType()
     * @see #unsetNativeType()
     * @see #setNativeType(String)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_NativeType()
     * @model unsettable="true"
     * @generated
     */
    String getNativeType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getNativeType <em>Native Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Native Type</em>' attribute.
     * @see #isSetNativeType()
     * @see #unsetNativeType()
     * @see #getNativeType()
     * @generated
     */
    void setNativeType(String value);

    /**
     * Unsets the value of the '{@link com.metamatrix.metamodels.relational.Column#getNativeType <em>Native Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetNativeType()
     * @see #getNativeType()
     * @see #setNativeType(String)
     * @generated
     */
    void unsetNativeType();

    /**
     * Returns whether the value of the '{@link com.metamatrix.metamodels.relational.Column#getNativeType <em>Native Type</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Native Type</em>' attribute is set.
     * @see #unsetNativeType()
     * @see #getNativeType()
     * @see #setNativeType(String)
     * @generated
     */
    boolean isSetNativeType();

    /**
     * Returns the value of the '<em><b>Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Length</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Length</em>' attribute.
     * @see #setLength(int)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Length()
     * @model
     * @generated
     */
    int getLength();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getLength <em>Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Length</em>' attribute.
     * @see #getLength()
     * @generated
     */
    void setLength(int value);

    /**
     * Returns the value of the '<em><b>Fixed Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Fixed Length</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Fixed Length</em>' attribute.
     * @see #setFixedLength(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_FixedLength()
     * @model
     * @generated
     */
    boolean isFixedLength();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#isFixedLength <em>Fixed Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Fixed Length</em>' attribute.
     * @see #isFixedLength()
     * @generated
     */
    void setFixedLength(boolean value);

    /**
     * Returns the value of the '<em><b>Precision</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Precision</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Precision</em>' attribute.
     * @see #setPrecision(int)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Precision()
     * @model
     * @generated
     */
    int getPrecision();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getPrecision <em>Precision</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Precision</em>' attribute.
     * @see #getPrecision()
     * @generated
     */
    void setPrecision(int value);

    /**
     * Returns the value of the '<em><b>Scale</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Scale</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Scale</em>' attribute.
     * @see #setScale(int)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Scale()
     * @model
     * @generated
     */
    int getScale();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getScale <em>Scale</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Scale</em>' attribute.
     * @see #getScale()
     * @generated
     */
    void setScale(int value);

    /**
     * Returns the value of the '<em><b>Nullable</b></em>' attribute.
     * The default value is <code>"NULLABLE"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.relational.NullableType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Nullable</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Nullable</em>' attribute.
     * @see com.metamatrix.metamodels.relational.NullableType
     * @see #setNullable(NullableType)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Nullable()
     * @model default="NULLABLE"
     * @generated
     */
    NullableType getNullable();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getNullable <em>Nullable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Nullable</em>' attribute.
     * @see com.metamatrix.metamodels.relational.NullableType
     * @see #getNullable()
     * @generated
     */
    void setNullable(NullableType value);

    /**
     * Returns the value of the '<em><b>Auto Incremented</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Auto Incremented</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Auto Incremented</em>' attribute.
     * @see #setAutoIncremented(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_AutoIncremented()
     * @model default="false"
     * @generated
     */
    boolean isAutoIncremented();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#isAutoIncremented <em>Auto Incremented</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Auto Incremented</em>' attribute.
     * @see #isAutoIncremented()
     * @generated
     */
    void setAutoIncremented(boolean value);

    /**
     * Returns the value of the '<em><b>Default Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Default Value</em>' attribute.
     * @see #setDefaultValue(String)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_DefaultValue()
     * @model
     * @generated
     */
    String getDefaultValue();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getDefaultValue <em>Default Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Default Value</em>' attribute.
     * @see #getDefaultValue()
     * @generated
     */
    void setDefaultValue(String value);

    /**
     * Returns the value of the '<em><b>Minimum Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Minimum Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Minimum Value</em>' attribute.
     * @see #setMinimumValue(String)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_MinimumValue()
     * @model
     * @generated
     */
    String getMinimumValue();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getMinimumValue <em>Minimum Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Minimum Value</em>' attribute.
     * @see #getMinimumValue()
     * @generated
     */
    void setMinimumValue(String value);

    /**
     * Returns the value of the '<em><b>Maximum Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Maximum Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Maximum Value</em>' attribute.
     * @see #setMaximumValue(String)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_MaximumValue()
     * @model
     * @generated
     */
    String getMaximumValue();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getMaximumValue <em>Maximum Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Maximum Value</em>' attribute.
     * @see #getMaximumValue()
     * @generated
     */
    void setMaximumValue(String value);

    /**
     * Returns the value of the '<em><b>Format</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Format</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Format</em>' attribute.
     * @see #setFormat(String)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Format()
     * @model
     * @generated
     */
    String getFormat();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getFormat <em>Format</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Format</em>' attribute.
     * @see #getFormat()
     * @generated
     */
    void setFormat(String value);

    /**
     * Returns the value of the '<em><b>Character Set Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Character Set Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Character Set Name</em>' attribute.
     * @see #setCharacterSetName(String)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_CharacterSetName()
     * @model
     * @generated
     */
    String getCharacterSetName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getCharacterSetName <em>Character Set Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Character Set Name</em>' attribute.
     * @see #getCharacterSetName()
     * @generated
     */
    void setCharacterSetName(String value);

    /**
     * Returns the value of the '<em><b>Collation Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Collation Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Collation Name</em>' attribute.
     * @see #setCollationName(String)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_CollationName()
     * @model
     * @generated
     */
    String getCollationName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getCollationName <em>Collation Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Collation Name</em>' attribute.
     * @see #getCollationName()
     * @generated
     */
    void setCollationName(String value);

    /**
     * Returns the value of the '<em><b>Selectable</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Selectable</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Selectable</em>' attribute.
     * @see #setSelectable(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Selectable()
     * @model default="true"
     * @generated
     */
    boolean isSelectable();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#isSelectable <em>Selectable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Selectable</em>' attribute.
     * @see #isSelectable()
     * @generated
     */
    void setSelectable(boolean value);

    /**
     * Returns the value of the '<em><b>Updateable</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Updateable</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Updateable</em>' attribute.
     * @see #setUpdateable(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Updateable()
     * @model default="true"
     * @generated
     */
    boolean isUpdateable();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#isUpdateable <em>Updateable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Updateable</em>' attribute.
     * @see #isUpdateable()
     * @generated
     */
    void setUpdateable(boolean value);

    /**
     * Returns the value of the '<em><b>Case Sensitive</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Case Sensitive</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Case Sensitive</em>' attribute.
     * @see #setCaseSensitive(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_CaseSensitive()
     * @model default="true"
     * @generated
     */
    boolean isCaseSensitive();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#isCaseSensitive <em>Case Sensitive</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Case Sensitive</em>' attribute.
     * @see #isCaseSensitive()
     * @generated
     */
    void setCaseSensitive(boolean value);

    /**
     * Returns the value of the '<em><b>Searchability</b></em>' attribute.
     * The default value is <code>"SEARCHABLE"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.relational.SearchabilityType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Searchability</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Searchability</em>' attribute.
     * @see com.metamatrix.metamodels.relational.SearchabilityType
     * @see #setSearchability(SearchabilityType)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Searchability()
     * @model default="SEARCHABLE"
     * @generated
     */
    SearchabilityType getSearchability();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getSearchability <em>Searchability</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Searchability</em>' attribute.
     * @see com.metamatrix.metamodels.relational.SearchabilityType
     * @see #getSearchability()
     * @generated
     */
    void setSearchability(SearchabilityType value);

    /**
     * Returns the value of the '<em><b>Currency</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Currency</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Currency</em>' attribute.
     * @see #setCurrency(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Currency()
     * @model
     * @generated
     */
    boolean isCurrency();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#isCurrency <em>Currency</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Currency</em>' attribute.
     * @see #isCurrency()
     * @generated
     */
    void setCurrency(boolean value);

    /**
     * Returns the value of the '<em><b>Radix</b></em>' attribute.
     * The default value is <code>"10"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Radix</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Radix</em>' attribute.
     * @see #setRadix(int)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Radix()
     * @model default="10"
     * @generated
     */
    int getRadix();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getRadix <em>Radix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Radix</em>' attribute.
     * @see #getRadix()
     * @generated
     */
    void setRadix(int value);

    /**
     * Returns the value of the '<em><b>Signed</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Signed</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Signed</em>' attribute.
     * @see #setSigned(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Signed()
     * @model default="true"
     * @generated
     */
    boolean isSigned();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#isSigned <em>Signed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Signed</em>' attribute.
     * @see #isSigned()
     * @generated
     */
    void setSigned(boolean value);

    /**
     * Returns the value of the '<em><b>Distinct Value Count</b></em>' attribute.
     * The default value is <code>"-1"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Distinct Value Count</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Distinct Value Count</em>' attribute.
     * @see #setDistinctValueCount(int)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_DistinctValueCount()
     * @model default="-1"
     * @generated
     */
    int getDistinctValueCount();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getDistinctValueCount <em>Distinct Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Distinct Value Count</em>' attribute.
     * @see #getDistinctValueCount()
     * @generated
     */
    void setDistinctValueCount(int value);

    /**
     * Returns the value of the '<em><b>Null Value Count</b></em>' attribute.
     * The default value is <code>"-1"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Null Value Count</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Null Value Count</em>' attribute.
     * @see #setNullValueCount(int)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_NullValueCount()
     * @model default="-1"
     * @generated
     */
    int getNullValueCount();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getNullValueCount <em>Null Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Null Value Count</em>' attribute.
     * @see #getNullValueCount()
     * @generated
     */
    void setNullValueCount(int value);

    /**
     * Returns the value of the '<em><b>Unique Keys</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.UniqueKey}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.UniqueKey#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Unique Keys</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Unique Keys</em>' reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_UniqueKeys()
     * @see com.metamatrix.metamodels.relational.UniqueKey#getColumns
     * @model type="com.metamatrix.metamodels.relational.UniqueKey" opposite="columns"
     * @generated
     */
    EList getUniqueKeys();

    /**
     * Returns the value of the '<em><b>Indexes</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Index}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Index#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Indexes</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Indexes</em>' reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Indexes()
     * @see com.metamatrix.metamodels.relational.Index#getColumns
     * @model type="com.metamatrix.metamodels.relational.Index" opposite="columns"
     * @generated
     */
    EList getIndexes();

    /**
     * Returns the value of the '<em><b>Foreign Keys</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.ForeignKey}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.ForeignKey#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Foreign Keys</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Foreign Keys</em>' reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_ForeignKeys()
     * @see com.metamatrix.metamodels.relational.ForeignKey#getColumns
     * @model type="com.metamatrix.metamodels.relational.ForeignKey" opposite="columns"
     * @generated
     */
    EList getForeignKeys();

    /**
     * Returns the value of the '<em><b>Access Patterns</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.AccessPattern}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.AccessPattern#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Access Patterns</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Access Patterns</em>' reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_AccessPatterns()
     * @see com.metamatrix.metamodels.relational.AccessPattern#getColumns
     * @model type="com.metamatrix.metamodels.relational.AccessPattern" opposite="columns"
     * @generated
     */
    EList getAccessPatterns();

    /**
     * Returns the value of the '<em><b>Owner</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.ColumnSet#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owner</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owner</em>' container reference.
     * @see #setOwner(ColumnSet)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Owner()
     * @see com.metamatrix.metamodels.relational.ColumnSet#getColumns
     * @model opposite="columns"
     * @generated
     */
    ColumnSet getOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getOwner <em>Owner</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Owner</em>' container reference.
     * @see #getOwner()
     * @generated
     */
    void setOwner(ColumnSet value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' reference.
     * @see #isSetType()
     * @see #unsetType()
     * @see #setType(EObject)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumn_Type()
     * @model unsettable="true" required="true"
     * @generated
     */
    EObject getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Column#getType <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' reference.
     * @see #isSetType()
     * @see #unsetType()
     * @see #getType()
     * @generated
     */
    void setType(EObject value);

    /**
     * Unsets the value of the '{@link com.metamatrix.metamodels.relational.Column#getType <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetType()
     * @see #getType()
     * @see #setType(EObject)
     * @generated
     */
    void unsetType();

    /**
     * Returns whether the value of the '{@link com.metamatrix.metamodels.relational.Column#getType <em>Type</em>}' reference is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Type</em>' reference is set.
     * @see #unsetType()
     * @see #getType()
     * @see #setType(EObject)
     * @generated
     */
    boolean isSetType();

} // Column
