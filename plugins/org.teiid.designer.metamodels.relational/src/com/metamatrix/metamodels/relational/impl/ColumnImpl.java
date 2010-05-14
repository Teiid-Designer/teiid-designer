/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ColumnSet;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.metamodels.relational.UniqueKey;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Column</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getNativeType <em>Native Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getLength <em>Length</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#isFixedLength <em>Fixed Length</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getPrecision <em>Precision</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getScale <em>Scale</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getNullable <em>Nullable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#isAutoIncremented <em>Auto Incremented</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getMinimumValue <em>Minimum Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getMaximumValue <em>Maximum Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getFormat <em>Format</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getCharacterSetName <em>Character Set Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getCollationName <em>Collation Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#isSelectable <em>Selectable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#isUpdateable <em>Updateable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#isCaseSensitive <em>Case Sensitive</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getSearchability <em>Searchability</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#isCurrency <em>Currency</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getRadix <em>Radix</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#isSigned <em>Signed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getDistinctValueCount <em>Distinct Value Count</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getNullValueCount <em>Null Value Count</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getUniqueKeys <em>Unique Keys</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getIndexes <em>Indexes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getForeignKeys <em>Foreign Keys</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getAccessPatterns <em>Access Patterns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getOwner <em>Owner</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ColumnImpl#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ColumnImpl extends RelationalEntityImpl implements Column {
    /**
     * The default value of the '{@link #getNativeType() <em>Native Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNativeType()
     * @generated
     * @ordered
     */
    protected static final String NATIVE_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNativeType() <em>Native Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNativeType()
     * @generated
     * @ordered
     */
    protected String nativeType = NATIVE_TYPE_EDEFAULT;

    /**
     * This is true if the Native Type attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean nativeTypeESet = false;

    /**
     * The default value of the '{@link #getLength() <em>Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLength()
     * @generated
     * @ordered
     */
    protected static final int LENGTH_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getLength() <em>Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLength()
     * @generated
     * @ordered
     */
    protected int length = LENGTH_EDEFAULT;

    /**
     * The default value of the '{@link #isFixedLength() <em>Fixed Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isFixedLength()
     * @generated
     * @ordered
     */
    protected static final boolean FIXED_LENGTH_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isFixedLength() <em>Fixed Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isFixedLength()
     * @generated
     * @ordered
     */
    protected boolean fixedLength = FIXED_LENGTH_EDEFAULT;

    /**
     * The default value of the '{@link #getPrecision() <em>Precision</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPrecision()
     * @generated
     * @ordered
     */
    protected static final int PRECISION_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getPrecision() <em>Precision</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPrecision()
     * @generated
     * @ordered
     */
    protected int precision = PRECISION_EDEFAULT;

    /**
     * The default value of the '{@link #getScale() <em>Scale</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScale()
     * @generated
     * @ordered
     */
    protected static final int SCALE_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getScale() <em>Scale</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScale()
     * @generated
     * @ordered
     */
    protected int scale = SCALE_EDEFAULT;

    /**
     * The default value of the '{@link #getNullable() <em>Nullable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNullable()
     * @generated
     * @ordered
     */
    protected static final NullableType NULLABLE_EDEFAULT = NullableType.NULLABLE_LITERAL;

    /**
     * The cached value of the '{@link #getNullable() <em>Nullable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNullable()
     * @generated
     * @ordered
     */
    protected NullableType nullable = NULLABLE_EDEFAULT;

    /**
     * The default value of the '{@link #isAutoIncremented() <em>Auto Incremented</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAutoIncremented()
     * @generated
     * @ordered
     */
    protected static final boolean AUTO_INCREMENTED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isAutoIncremented() <em>Auto Incremented</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAutoIncremented()
     * @generated
     * @ordered
     */
    protected boolean autoIncremented = AUTO_INCREMENTED_EDEFAULT;

    /**
     * The default value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefaultValue()
     * @generated
     * @ordered
     */
    protected static final String DEFAULT_VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefaultValue()
     * @generated
     * @ordered
     */
    protected String defaultValue = DEFAULT_VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getMinimumValue() <em>Minimum Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMinimumValue()
     * @generated
     * @ordered
     */
    protected static final String MINIMUM_VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMinimumValue() <em>Minimum Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMinimumValue()
     * @generated
     * @ordered
     */
    protected String minimumValue = MINIMUM_VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getMaximumValue() <em>Maximum Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaximumValue()
     * @generated
     * @ordered
     */
    protected static final String MAXIMUM_VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMaximumValue() <em>Maximum Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaximumValue()
     * @generated
     * @ordered
     */
    protected String maximumValue = MAXIMUM_VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getFormat() <em>Format</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFormat()
     * @generated
     * @ordered
     */
    protected static final String FORMAT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFormat() <em>Format</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFormat()
     * @generated
     * @ordered
     */
    protected String format = FORMAT_EDEFAULT;

    /**
     * The default value of the '{@link #getCharacterSetName() <em>Character Set Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCharacterSetName()
     * @generated
     * @ordered
     */
    protected static final String CHARACTER_SET_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCharacterSetName() <em>Character Set Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCharacterSetName()
     * @generated
     * @ordered
     */
    protected String characterSetName = CHARACTER_SET_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getCollationName() <em>Collation Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCollationName()
     * @generated
     * @ordered
     */
    protected static final String COLLATION_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCollationName() <em>Collation Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCollationName()
     * @generated
     * @ordered
     */
    protected String collationName = COLLATION_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #isSelectable() <em>Selectable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSelectable()
     * @generated
     * @ordered
     */
    protected static final boolean SELECTABLE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isSelectable() <em>Selectable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSelectable()
     * @generated
     * @ordered
     */
    protected boolean selectable = SELECTABLE_EDEFAULT;

    /**
     * The default value of the '{@link #isUpdateable() <em>Updateable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUpdateable()
     * @generated
     * @ordered
     */
    protected static final boolean UPDATEABLE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isUpdateable() <em>Updateable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUpdateable()
     * @generated
     * @ordered
     */
    protected boolean updateable = UPDATEABLE_EDEFAULT;

    /**
     * The default value of the '{@link #isCaseSensitive() <em>Case Sensitive</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCaseSensitive()
     * @generated
     * @ordered
     */
    protected static final boolean CASE_SENSITIVE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isCaseSensitive() <em>Case Sensitive</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCaseSensitive()
     * @generated
     * @ordered
     */
    protected boolean caseSensitive = CASE_SENSITIVE_EDEFAULT;

    /**
     * The default value of the '{@link #getSearchability() <em>Searchability</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSearchability()
     * @generated
     * @ordered
     */
    protected static final SearchabilityType SEARCHABILITY_EDEFAULT = SearchabilityType.SEARCHABLE_LITERAL;

    /**
     * The cached value of the '{@link #getSearchability() <em>Searchability</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSearchability()
     * @generated
     * @ordered
     */
    protected SearchabilityType searchability = SEARCHABILITY_EDEFAULT;

    /**
     * The default value of the '{@link #isCurrency() <em>Currency</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCurrency()
     * @generated
     * @ordered
     */
    protected static final boolean CURRENCY_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isCurrency() <em>Currency</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCurrency()
     * @generated
     * @ordered
     */
    protected boolean currency = CURRENCY_EDEFAULT;

    /**
     * The default value of the '{@link #getRadix() <em>Radix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRadix()
     * @generated
     * @ordered
     */
    protected static final int RADIX_EDEFAULT = 10;

    /**
     * The cached value of the '{@link #getRadix() <em>Radix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRadix()
     * @generated
     * @ordered
     */
    protected int radix = RADIX_EDEFAULT;

    /**
     * The default value of the '{@link #isSigned() <em>Signed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSigned()
     * @generated
     * @ordered
     */
    protected static final boolean SIGNED_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isSigned() <em>Signed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSigned()
     * @generated
     * @ordered
     */
    protected boolean signed = SIGNED_EDEFAULT;

    /**
     * The default value of the '{@link #getDistinctValueCount() <em>Distinct Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDistinctValueCount()
     * @generated
     * @ordered
     */
    protected static final int DISTINCT_VALUE_COUNT_EDEFAULT = -1;

    /**
     * The cached value of the '{@link #getDistinctValueCount() <em>Distinct Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDistinctValueCount()
     * @generated
     * @ordered
     */
    protected int distinctValueCount = DISTINCT_VALUE_COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getNullValueCount() <em>Null Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNullValueCount()
     * @generated
     * @ordered
     */
    protected static final int NULL_VALUE_COUNT_EDEFAULT = -1;

    /**
     * The cached value of the '{@link #getNullValueCount() <em>Null Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNullValueCount()
     * @generated
     * @ordered
     */
    protected int nullValueCount = NULL_VALUE_COUNT_EDEFAULT;

    /**
     * The cached value of the '{@link #getUniqueKeys() <em>Unique Keys</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUniqueKeys()
     * @generated
     * @ordered
     */
    protected EList uniqueKeys = null;

    /**
     * The cached value of the '{@link #getIndexes() <em>Indexes</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIndexes()
     * @generated
     * @ordered
     */
    protected EList indexes = null;

    /**
     * The cached value of the '{@link #getForeignKeys() <em>Foreign Keys</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getForeignKeys()
     * @generated
     * @ordered
     */
    protected EList foreignKeys = null;

    /**
     * The cached value of the '{@link #getAccessPatterns() <em>Access Patterns</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAccessPatterns()
     * @generated
     * @ordered
     */
    protected EList accessPatterns = null;

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected EObject type = null;

    /**
     * This is true if the Type reference has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean typeESet = false;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ColumnImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getColumn();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getNativeType() {
        return nativeType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNativeType(String newNativeType) {
        String oldNativeType = nativeType;
        nativeType = newNativeType;
        boolean oldNativeTypeESet = nativeTypeESet;
        nativeTypeESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__NATIVE_TYPE, oldNativeType, nativeType, !oldNativeTypeESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetNativeType() {
        String oldNativeType = nativeType;
        boolean oldNativeTypeESet = nativeTypeESet;
        nativeType = NATIVE_TYPE_EDEFAULT;
        nativeTypeESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, RelationalPackage.COLUMN__NATIVE_TYPE, oldNativeType, NATIVE_TYPE_EDEFAULT, oldNativeTypeESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetNativeType() {
        return nativeTypeESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getLength() {
        return length;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLength(int newLength) {
        int oldLength = length;
        length = newLength;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__LENGTH, oldLength, length));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isFixedLength() {
        return fixedLength;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFixedLength(boolean newFixedLength) {
        boolean oldFixedLength = fixedLength;
        fixedLength = newFixedLength;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__FIXED_LENGTH, oldFixedLength, fixedLength));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPrecision(int newPrecision) {
        int oldPrecision = precision;
        precision = newPrecision;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__PRECISION, oldPrecision, precision));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getScale() {
        return scale;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setScale(int newScale) {
        int oldScale = scale;
        scale = newScale;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__SCALE, oldScale, scale));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NullableType getNullable() {
        return nullable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNullable(NullableType newNullable) {
        NullableType oldNullable = nullable;
        nullable = newNullable == null ? NULLABLE_EDEFAULT : newNullable;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__NULLABLE, oldNullable, nullable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAutoIncremented() {
        return autoIncremented;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAutoIncremented(boolean newAutoIncremented) {
        boolean oldAutoIncremented = autoIncremented;
        autoIncremented = newAutoIncremented;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__AUTO_INCREMENTED, oldAutoIncremented, autoIncremented));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDefaultValue(String newDefaultValue) {
        String oldDefaultValue = defaultValue;
        defaultValue = newDefaultValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__DEFAULT_VALUE, oldDefaultValue, defaultValue));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getMinimumValue() {
        return minimumValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMinimumValue(String newMinimumValue) {
        String oldMinimumValue = minimumValue;
        minimumValue = newMinimumValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__MINIMUM_VALUE, oldMinimumValue, minimumValue));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getMaximumValue() {
        return maximumValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMaximumValue(String newMaximumValue) {
        String oldMaximumValue = maximumValue;
        maximumValue = newMaximumValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__MAXIMUM_VALUE, oldMaximumValue, maximumValue));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getFormat() {
        return format;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFormat(String newFormat) {
        String oldFormat = format;
        format = newFormat;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__FORMAT, oldFormat, format));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getCharacterSetName() {
        return characterSetName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCharacterSetName(String newCharacterSetName) {
        String oldCharacterSetName = characterSetName;
        characterSetName = newCharacterSetName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__CHARACTER_SET_NAME, oldCharacterSetName, characterSetName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getCollationName() {
        return collationName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCollationName(String newCollationName) {
        String oldCollationName = collationName;
        collationName = newCollationName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__COLLATION_NAME, oldCollationName, collationName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSelectable(boolean newSelectable) {
        boolean oldSelectable = selectable;
        selectable = newSelectable;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__SELECTABLE, oldSelectable, selectable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isUpdateable() {
        return updateable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUpdateable(boolean newUpdateable) {
        boolean oldUpdateable = updateable;
        updateable = newUpdateable;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__UPDATEABLE, oldUpdateable, updateable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCaseSensitive(boolean newCaseSensitive) {
        boolean oldCaseSensitive = caseSensitive;
        caseSensitive = newCaseSensitive;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__CASE_SENSITIVE, oldCaseSensitive, caseSensitive));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SearchabilityType getSearchability() {
        return searchability;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSearchability(SearchabilityType newSearchability) {
        SearchabilityType oldSearchability = searchability;
        searchability = newSearchability == null ? SEARCHABILITY_EDEFAULT : newSearchability;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__SEARCHABILITY, oldSearchability, searchability));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isCurrency() {
        return currency;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCurrency(boolean newCurrency) {
        boolean oldCurrency = currency;
        currency = newCurrency;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__CURRENCY, oldCurrency, currency));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getRadix() {
        return radix;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRadix(int newRadix) {
        int oldRadix = radix;
        radix = newRadix;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__RADIX, oldRadix, radix));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSigned(boolean newSigned) {
        boolean oldSigned = signed;
        signed = newSigned;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__SIGNED, oldSigned, signed));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getDistinctValueCount() {
        return distinctValueCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDistinctValueCount(int newDistinctValueCount) {
        int oldDistinctValueCount = distinctValueCount;
        distinctValueCount = newDistinctValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__DISTINCT_VALUE_COUNT, oldDistinctValueCount, distinctValueCount));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getNullValueCount() {
        return nullValueCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNullValueCount(int newNullValueCount) {
        int oldNullValueCount = nullValueCount;
        nullValueCount = newNullValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__NULL_VALUE_COUNT, oldNullValueCount, nullValueCount));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getUniqueKeys() {
        if (uniqueKeys == null) {
            uniqueKeys = new EObjectWithInverseResolvingEList.ManyInverse(UniqueKey.class, this, RelationalPackage.COLUMN__UNIQUE_KEYS, RelationalPackage.UNIQUE_KEY__COLUMNS);
        }
        return uniqueKeys;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getIndexes() {
        if (indexes == null) {
            indexes = new EObjectWithInverseResolvingEList.ManyInverse(Index.class, this, RelationalPackage.COLUMN__INDEXES, RelationalPackage.INDEX__COLUMNS);
        }
        return indexes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getForeignKeys() {
        if (foreignKeys == null) {
            foreignKeys = new EObjectWithInverseResolvingEList.ManyInverse(ForeignKey.class, this, RelationalPackage.COLUMN__FOREIGN_KEYS, RelationalPackage.FOREIGN_KEY__COLUMNS);
        }
        return foreignKeys;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getAccessPatterns() {
        if (accessPatterns == null) {
            accessPatterns = new EObjectWithInverseResolvingEList.ManyInverse(AccessPattern.class, this, RelationalPackage.COLUMN__ACCESS_PATTERNS, RelationalPackage.ACCESS_PATTERN__COLUMNS);
        }
        return accessPatterns;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ColumnSet getOwner() {
        if (eContainerFeatureID != RelationalPackage.COLUMN__OWNER) return null;
        return (ColumnSet)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOwner(ColumnSet newOwner) {
        if (newOwner != eContainer || (eContainerFeatureID != RelationalPackage.COLUMN__OWNER && newOwner != null)) {
            if (EcoreUtil.isAncestor(this, newOwner))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOwner != null)
                msgs = ((InternalEObject)newOwner).eInverseAdd(this, RelationalPackage.COLUMN_SET__COLUMNS, ColumnSet.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newOwner, RelationalPackage.COLUMN__OWNER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__OWNER, newOwner, newOwner));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getType() {
        if (type != null && type.eIsProxy()) {
            EObject oldType = type;
            type = eResolveProxy((InternalEObject)type);
            if (type != oldType) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, RelationalPackage.COLUMN__TYPE, oldType, type));
            }
        }
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setType(EObject newType) {
        EObject oldType = type;
        type = newType;
        boolean oldTypeESet = typeESet;
        typeESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.COLUMN__TYPE, oldType, type, !oldTypeESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetType() {
        EObject oldType = type;
        boolean oldTypeESet = typeESet;
        type = null;
        typeESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, RelationalPackage.COLUMN__TYPE, oldType, null, oldTypeESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetType() {
        return typeESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case RelationalPackage.COLUMN__UNIQUE_KEYS:
                    return ((InternalEList)getUniqueKeys()).basicAdd(otherEnd, msgs);
                case RelationalPackage.COLUMN__INDEXES:
                    return ((InternalEList)getIndexes()).basicAdd(otherEnd, msgs);
                case RelationalPackage.COLUMN__FOREIGN_KEYS:
                    return ((InternalEList)getForeignKeys()).basicAdd(otherEnd, msgs);
                case RelationalPackage.COLUMN__ACCESS_PATTERNS:
                    return ((InternalEList)getAccessPatterns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.COLUMN__OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.COLUMN__OWNER, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
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
                case RelationalPackage.COLUMN__UNIQUE_KEYS:
                    return ((InternalEList)getUniqueKeys()).basicRemove(otherEnd, msgs);
                case RelationalPackage.COLUMN__INDEXES:
                    return ((InternalEList)getIndexes()).basicRemove(otherEnd, msgs);
                case RelationalPackage.COLUMN__FOREIGN_KEYS:
                    return ((InternalEList)getForeignKeys()).basicRemove(otherEnd, msgs);
                case RelationalPackage.COLUMN__ACCESS_PATTERNS:
                    return ((InternalEList)getAccessPatterns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.COLUMN__OWNER:
                    return eBasicSetContainer(null, RelationalPackage.COLUMN__OWNER, msgs);
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
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case RelationalPackage.COLUMN__OWNER:
                    return eContainer.eInverseRemove(this, RelationalPackage.COLUMN_SET__COLUMNS, ColumnSet.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.COLUMN__NAME:
                return getName();
            case RelationalPackage.COLUMN__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.COLUMN__NATIVE_TYPE:
                return getNativeType();
            case RelationalPackage.COLUMN__LENGTH:
                return new Integer(getLength());
            case RelationalPackage.COLUMN__FIXED_LENGTH:
                return isFixedLength() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.COLUMN__PRECISION:
                return new Integer(getPrecision());
            case RelationalPackage.COLUMN__SCALE:
                return new Integer(getScale());
            case RelationalPackage.COLUMN__NULLABLE:
                return getNullable();
            case RelationalPackage.COLUMN__AUTO_INCREMENTED:
                return isAutoIncremented() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.COLUMN__DEFAULT_VALUE:
                return getDefaultValue();
            case RelationalPackage.COLUMN__MINIMUM_VALUE:
                return getMinimumValue();
            case RelationalPackage.COLUMN__MAXIMUM_VALUE:
                return getMaximumValue();
            case RelationalPackage.COLUMN__FORMAT:
                return getFormat();
            case RelationalPackage.COLUMN__CHARACTER_SET_NAME:
                return getCharacterSetName();
            case RelationalPackage.COLUMN__COLLATION_NAME:
                return getCollationName();
            case RelationalPackage.COLUMN__SELECTABLE:
                return isSelectable() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.COLUMN__UPDATEABLE:
                return isUpdateable() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.COLUMN__CASE_SENSITIVE:
                return isCaseSensitive() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.COLUMN__SEARCHABILITY:
                return getSearchability();
            case RelationalPackage.COLUMN__CURRENCY:
                return isCurrency() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.COLUMN__RADIX:
                return new Integer(getRadix());
            case RelationalPackage.COLUMN__SIGNED:
                return isSigned() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.COLUMN__DISTINCT_VALUE_COUNT:
                return new Integer(getDistinctValueCount());
            case RelationalPackage.COLUMN__NULL_VALUE_COUNT:
                return new Integer(getNullValueCount());
            case RelationalPackage.COLUMN__UNIQUE_KEYS:
                return getUniqueKeys();
            case RelationalPackage.COLUMN__INDEXES:
                return getIndexes();
            case RelationalPackage.COLUMN__FOREIGN_KEYS:
                return getForeignKeys();
            case RelationalPackage.COLUMN__ACCESS_PATTERNS:
                return getAccessPatterns();
            case RelationalPackage.COLUMN__OWNER:
                return getOwner();
            case RelationalPackage.COLUMN__TYPE:
                if (resolve) return getType();
                return basicGetType();
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
            case RelationalPackage.COLUMN__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.COLUMN__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.COLUMN__NATIVE_TYPE:
                setNativeType((String)newValue);
                return;
            case RelationalPackage.COLUMN__LENGTH:
                setLength(((Integer)newValue).intValue());
                return;
            case RelationalPackage.COLUMN__FIXED_LENGTH:
                setFixedLength(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.COLUMN__PRECISION:
                setPrecision(((Integer)newValue).intValue());
                return;
            case RelationalPackage.COLUMN__SCALE:
                setScale(((Integer)newValue).intValue());
                return;
            case RelationalPackage.COLUMN__NULLABLE:
                setNullable((NullableType)newValue);
                return;
            case RelationalPackage.COLUMN__AUTO_INCREMENTED:
                setAutoIncremented(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.COLUMN__DEFAULT_VALUE:
                setDefaultValue((String)newValue);
                return;
            case RelationalPackage.COLUMN__MINIMUM_VALUE:
                setMinimumValue((String)newValue);
                return;
            case RelationalPackage.COLUMN__MAXIMUM_VALUE:
                setMaximumValue((String)newValue);
                return;
            case RelationalPackage.COLUMN__FORMAT:
                setFormat((String)newValue);
                return;
            case RelationalPackage.COLUMN__CHARACTER_SET_NAME:
                setCharacterSetName((String)newValue);
                return;
            case RelationalPackage.COLUMN__COLLATION_NAME:
                setCollationName((String)newValue);
                return;
            case RelationalPackage.COLUMN__SELECTABLE:
                setSelectable(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.COLUMN__UPDATEABLE:
                setUpdateable(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.COLUMN__CASE_SENSITIVE:
                setCaseSensitive(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.COLUMN__SEARCHABILITY:
                setSearchability((SearchabilityType)newValue);
                return;
            case RelationalPackage.COLUMN__CURRENCY:
                setCurrency(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.COLUMN__RADIX:
                setRadix(((Integer)newValue).intValue());
                return;
            case RelationalPackage.COLUMN__SIGNED:
                setSigned(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.COLUMN__DISTINCT_VALUE_COUNT:
                setDistinctValueCount(((Integer)newValue).intValue());
                return;
            case RelationalPackage.COLUMN__NULL_VALUE_COUNT:
                setNullValueCount(((Integer)newValue).intValue());
                return;
            case RelationalPackage.COLUMN__UNIQUE_KEYS:
                getUniqueKeys().clear();
                getUniqueKeys().addAll((Collection)newValue);
                return;
            case RelationalPackage.COLUMN__INDEXES:
                getIndexes().clear();
                getIndexes().addAll((Collection)newValue);
                return;
            case RelationalPackage.COLUMN__FOREIGN_KEYS:
                getForeignKeys().clear();
                getForeignKeys().addAll((Collection)newValue);
                return;
            case RelationalPackage.COLUMN__ACCESS_PATTERNS:
                getAccessPatterns().clear();
                getAccessPatterns().addAll((Collection)newValue);
                return;
            case RelationalPackage.COLUMN__OWNER:
                setOwner((ColumnSet)newValue);
                return;
            case RelationalPackage.COLUMN__TYPE:
                setType((EObject)newValue);
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
            case RelationalPackage.COLUMN__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__NATIVE_TYPE:
                unsetNativeType();
                return;
            case RelationalPackage.COLUMN__LENGTH:
                setLength(LENGTH_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__FIXED_LENGTH:
                setFixedLength(FIXED_LENGTH_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__PRECISION:
                setPrecision(PRECISION_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__SCALE:
                setScale(SCALE_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__NULLABLE:
                setNullable(NULLABLE_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__AUTO_INCREMENTED:
                setAutoIncremented(AUTO_INCREMENTED_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__DEFAULT_VALUE:
                setDefaultValue(DEFAULT_VALUE_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__MINIMUM_VALUE:
                setMinimumValue(MINIMUM_VALUE_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__MAXIMUM_VALUE:
                setMaximumValue(MAXIMUM_VALUE_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__FORMAT:
                setFormat(FORMAT_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__CHARACTER_SET_NAME:
                setCharacterSetName(CHARACTER_SET_NAME_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__COLLATION_NAME:
                setCollationName(COLLATION_NAME_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__SELECTABLE:
                setSelectable(SELECTABLE_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__UPDATEABLE:
                setUpdateable(UPDATEABLE_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__CASE_SENSITIVE:
                setCaseSensitive(CASE_SENSITIVE_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__SEARCHABILITY:
                setSearchability(SEARCHABILITY_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__CURRENCY:
                setCurrency(CURRENCY_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__RADIX:
                setRadix(RADIX_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__SIGNED:
                setSigned(SIGNED_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__DISTINCT_VALUE_COUNT:
                setDistinctValueCount(DISTINCT_VALUE_COUNT_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__NULL_VALUE_COUNT:
                setNullValueCount(NULL_VALUE_COUNT_EDEFAULT);
                return;
            case RelationalPackage.COLUMN__UNIQUE_KEYS:
                getUniqueKeys().clear();
                return;
            case RelationalPackage.COLUMN__INDEXES:
                getIndexes().clear();
                return;
            case RelationalPackage.COLUMN__FOREIGN_KEYS:
                getForeignKeys().clear();
                return;
            case RelationalPackage.COLUMN__ACCESS_PATTERNS:
                getAccessPatterns().clear();
                return;
            case RelationalPackage.COLUMN__OWNER:
                setOwner((ColumnSet)null);
                return;
            case RelationalPackage.COLUMN__TYPE:
                unsetType();
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
            case RelationalPackage.COLUMN__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.COLUMN__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.COLUMN__NATIVE_TYPE:
                return isSetNativeType();
            case RelationalPackage.COLUMN__LENGTH:
                return length != LENGTH_EDEFAULT;
            case RelationalPackage.COLUMN__FIXED_LENGTH:
                return fixedLength != FIXED_LENGTH_EDEFAULT;
            case RelationalPackage.COLUMN__PRECISION:
                return precision != PRECISION_EDEFAULT;
            case RelationalPackage.COLUMN__SCALE:
                return scale != SCALE_EDEFAULT;
            case RelationalPackage.COLUMN__NULLABLE:
                return nullable != NULLABLE_EDEFAULT;
            case RelationalPackage.COLUMN__AUTO_INCREMENTED:
                return autoIncremented != AUTO_INCREMENTED_EDEFAULT;
            case RelationalPackage.COLUMN__DEFAULT_VALUE:
                return DEFAULT_VALUE_EDEFAULT == null ? defaultValue != null : !DEFAULT_VALUE_EDEFAULT.equals(defaultValue);
            case RelationalPackage.COLUMN__MINIMUM_VALUE:
                return MINIMUM_VALUE_EDEFAULT == null ? minimumValue != null : !MINIMUM_VALUE_EDEFAULT.equals(minimumValue);
            case RelationalPackage.COLUMN__MAXIMUM_VALUE:
                return MAXIMUM_VALUE_EDEFAULT == null ? maximumValue != null : !MAXIMUM_VALUE_EDEFAULT.equals(maximumValue);
            case RelationalPackage.COLUMN__FORMAT:
                return FORMAT_EDEFAULT == null ? format != null : !FORMAT_EDEFAULT.equals(format);
            case RelationalPackage.COLUMN__CHARACTER_SET_NAME:
                return CHARACTER_SET_NAME_EDEFAULT == null ? characterSetName != null : !CHARACTER_SET_NAME_EDEFAULT.equals(characterSetName);
            case RelationalPackage.COLUMN__COLLATION_NAME:
                return COLLATION_NAME_EDEFAULT == null ? collationName != null : !COLLATION_NAME_EDEFAULT.equals(collationName);
            case RelationalPackage.COLUMN__SELECTABLE:
                return selectable != SELECTABLE_EDEFAULT;
            case RelationalPackage.COLUMN__UPDATEABLE:
                return updateable != UPDATEABLE_EDEFAULT;
            case RelationalPackage.COLUMN__CASE_SENSITIVE:
                return caseSensitive != CASE_SENSITIVE_EDEFAULT;
            case RelationalPackage.COLUMN__SEARCHABILITY:
                return searchability != SEARCHABILITY_EDEFAULT;
            case RelationalPackage.COLUMN__CURRENCY:
                return currency != CURRENCY_EDEFAULT;
            case RelationalPackage.COLUMN__RADIX:
                return radix != RADIX_EDEFAULT;
            case RelationalPackage.COLUMN__SIGNED:
                return signed != SIGNED_EDEFAULT;
            case RelationalPackage.COLUMN__DISTINCT_VALUE_COUNT:
                return distinctValueCount != DISTINCT_VALUE_COUNT_EDEFAULT;
            case RelationalPackage.COLUMN__NULL_VALUE_COUNT:
                return nullValueCount != NULL_VALUE_COUNT_EDEFAULT;
            case RelationalPackage.COLUMN__UNIQUE_KEYS:
                return uniqueKeys != null && !uniqueKeys.isEmpty();
            case RelationalPackage.COLUMN__INDEXES:
                return indexes != null && !indexes.isEmpty();
            case RelationalPackage.COLUMN__FOREIGN_KEYS:
                return foreignKeys != null && !foreignKeys.isEmpty();
            case RelationalPackage.COLUMN__ACCESS_PATTERNS:
                return accessPatterns != null && !accessPatterns.isEmpty();
            case RelationalPackage.COLUMN__OWNER:
                return getOwner() != null;
            case RelationalPackage.COLUMN__TYPE:
                return isSetType();
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
        result.append(" (nativeType: "); //$NON-NLS-1$
        if (nativeTypeESet) result.append(nativeType); else result.append("<unset>"); //$NON-NLS-1$
        result.append(", length: "); //$NON-NLS-1$
        result.append(length);
        result.append(", fixedLength: "); //$NON-NLS-1$
        result.append(fixedLength);
        result.append(", precision: "); //$NON-NLS-1$
        result.append(precision);
        result.append(", scale: "); //$NON-NLS-1$
        result.append(scale);
        result.append(", nullable: "); //$NON-NLS-1$
        result.append(nullable);
        result.append(", autoIncremented: "); //$NON-NLS-1$
        result.append(autoIncremented);
        result.append(", defaultValue: "); //$NON-NLS-1$
        result.append(defaultValue);
        result.append(", minimumValue: "); //$NON-NLS-1$
        result.append(minimumValue);
        result.append(", maximumValue: "); //$NON-NLS-1$
        result.append(maximumValue);
        result.append(", format: "); //$NON-NLS-1$
        result.append(format);
        result.append(", characterSetName: "); //$NON-NLS-1$
        result.append(characterSetName);
        result.append(", collationName: "); //$NON-NLS-1$
        result.append(collationName);
        result.append(", selectable: "); //$NON-NLS-1$
        result.append(selectable);
        result.append(", updateable: "); //$NON-NLS-1$
        result.append(updateable);
        result.append(", caseSensitive: "); //$NON-NLS-1$
        result.append(caseSensitive);
        result.append(", searchability: "); //$NON-NLS-1$
        result.append(searchability);
        result.append(", currency: "); //$NON-NLS-1$
        result.append(currency);
        result.append(", radix: "); //$NON-NLS-1$
        result.append(radix);
        result.append(", signed: "); //$NON-NLS-1$
        result.append(signed);
        result.append(", distinctValueCount: "); //$NON-NLS-1$
        result.append(distinctValueCount);
        result.append(", nullValueCount: "); //$NON-NLS-1$
        result.append(nullValueCount);
        result.append(')');
        return result.toString();
    }

} //ColumnImpl
