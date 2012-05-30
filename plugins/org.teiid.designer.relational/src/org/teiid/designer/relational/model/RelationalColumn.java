/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import java.util.Properties;

import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;


/**
 * 
 */
public class RelationalColumn extends RelationalReference {
    public static final String KEY_DISTINCT_VALUE_COUNT = "DISTINCTVALUECOUNT"; //$NON-NLS-1$
    public static final String KEY_NULL_VALUE_COUNT = "NULLVALUECOUNT"; //$NON-NLS-1$
    public static final String KEY_DATATYPE = "DATATYPE"; //$NON-NLS-1$
    public static final String KEY_NATIVE_TYPE = "NATIVETYPE"; //$NON-NLS-1$
    public static final String KEY_NULLABLE = "NULLABLE"; //$NON-NLS-1$
    public static final String KEY_AUTO_INCREMENTED = "AUTOINCREMENTED"; //$NON-NLS-1$
    public static final String KEY_CASE_SENSITIVE = "CASESENSITIVE"; //$NON-NLS-1$
    public static final String KEY_CHARACTER_SET_NAME = "CHARACTERSETNAME"; //$NON-NLS-1$
    public static final String KEY_COLLATION_NAME = "COLLATIONNAME"; //$NON-NLS-1$
    public static final String KEY_CURRENCY = "CURRENCY"; //$NON-NLS-1$
    public static final String KEY_DEFAULT_VALUE = "DEFAULTVALUE"; //$NON-NLS-1$
    public static final String KEY_FORMAT = "FORMAT"; //$NON-NLS-1$
    public static final String KEY_LENGTH = "LENGTH"; //$NON-NLS-1$
    public static final String KEY_LENGTH_FIXED = "LENGTHFIXED"; //$NON-NLS-1$
    public static final String KEY_MAXIMUM_VALUE = "MAXIMUMVALUE"; //$NON-NLS-1$
    public static final String KEY_MINIMUM_VALUE = "MINIMUMVALUE"; //$NON-NLS-1$
    public static final String KEY_PRECISION = "PRECISION"; //$NON-NLS-1$
    public static final String KEY_SCALE = "SCALE"; //$NON-NLS-1$
    public static final String KEY_RADIX = "RADIX"; //$NON-NLS-1$
    public static final String KEY_SIGNED = "SIGNED"; //$NON-NLS-1$
    public static final String KEY_SEARCHABILITY = "SEARCHABILITY"; //$NON-NLS-1$
    public static final String KEY_SELECTABLE = "SELECTABLE"; //$NON-NLS-1$
    public static final String KEY_UPDATEABLE = "UPDATEABLE"; //$NON-NLS-1$
    
    public static final int DEFAULT_DISTINCT_VALUE_COUNT = -1;
    public static final int DEFAULT_NULL_VALUE_COUNT = -1;
    public static final String DEFAULT_DATATYPE = null;
    public static final String DEFAULT_NATIVE_TYPE = null;
    public static final String DEFAULT_NULLABLE = NULLABLE.NULLABLE;
    public static final boolean DEFAULT_AUTO_INCREMENTED = false;
    public static final boolean DEFAULT_CASE_SENSITIVE = true;
    public static final String DEFAULT_CHARACTER_SET_NAME = null;
    public static final String DEFAULT_COLLATION_NAME = null;
    public static final boolean DEFAULT_CURRENCY = false;
    public static final String DEFAULT_DEFAULT_VALUE = null;
    public static final String DEFAULT_FORMAT = null;
    public static final int DEFAULT_LENGTH = 0;
    public static final boolean DEFAULT_LENGTH_FIXED = false;
    public static final String DEFAULT_MAXIMUM_VALUE = null;
    public static final String DEFAULT_MINIMUM_VALUE = null;
    public static final int DEFAULT_PRECISION = 0;
    public static final int DEFAULT_RADIX = 0;
    public static final int DEFAULT_SCALE = 0;
    public static final boolean DEFAULT_SIGNED = true;
    public static final String DEFAULT_SEARCHABILITY = SEARCHABILITY.SEARCHABLE;
    public static final boolean DEFAULT_SELECTABLE = true;
    public static final boolean DEFAULT_UPDATEABLE = true;
    
    public static final int DEFAULT_STRING_LENGTH = 4000;
    
    private int distinctValueCount;
    private int nullValueCount;
    private String  datatype;
    private String  nativeType;
    private String  nullable;
    private boolean autoIncremented;
    private boolean caseSensitive;
    private String  characterSetName;
    private String  collationName;
    private boolean currency;
    private String  defaultValue;
    private String  format;
    private int length;
    private boolean lengthFixed;
    private String  maximumValue;
    private String  minimumValue;
    private int precision;
    private int scale;
    private int radix;
    private boolean signed;
    private String  searchability = DEFAULT_SEARCHABILITY;
    private boolean selectable = DEFAULT_SELECTABLE;
    private boolean updateable = DEFAULT_UPDATEABLE;
    
    public RelationalColumn() {
        super();
        setType(TYPES.COLUMN);
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    /**
     * @param name
     */
    public RelationalColumn( String name ) {
        super(name);
        setType(TYPES.COLUMN);
        setNameValidator(new RelationalStringNameValidator(false, true));
    }

    /**
     * @return distinctValueCount
     */
    public int getDistinctValueCount() {
        return distinctValueCount;
    }
    /**
     * @param distinctValueCount Sets distinctValueCount to the specified value.
     */
    public void setDistinctValueCount( int distinctValueCount ) {
        this.distinctValueCount = distinctValueCount;
    }
    /**
     * @return nullValueCount
     */
    public int getNullValueCount() {
        return nullValueCount;
    }
    /**
     * @param nullValueCount Sets nullValueCount to the specified value.
     */
    public void setNullValueCount( int nullValueCount ) {
        this.nullValueCount = nullValueCount;
    }
    /**
     * @return datatype
     */
    public String getDatatype() {
        return datatype;
    }
    /**
     * @param datatype Sets datatype to the specified value.
     */
    public void setDatatype( String datatype ) {
        this.datatype = datatype;
    }
    /**
     * @return nativeType
     */
    public String getNativeType() {
        return nativeType;
    }
    /**
     * @param nativeType Sets nativeType to the specified value.
     */
    public void setNativeType( String nativeType ) {
        this.nativeType = nativeType;
    }
    /**
     * @return nullable
     */
    public String getNullable() {
        return nullable;
    }
    /**
     * @param nullable Sets nullable to the specified value.
     */
    public void setNullable( String nullable ) {
        this.nullable = nullable;
    }
    /**
     * @return autoIncremented
     */
    public boolean isAutoIncremented() {
        return autoIncremented;
    }
    /**
     * @param autoIncremented Sets autoIncremented to the specified value.
     */
    public void setAutoIncremented( boolean autoIncremented ) {
        this.autoIncremented = autoIncremented;
    }
    /**
     * @return caseSensitive
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }
    /**
     * @param caseSensitive Sets caseSensitive to the specified value.
     */
    public void setCaseSensitive( boolean caseSensitive ) {
        this.caseSensitive = caseSensitive;
    }
    /**
     * @return characterSetName
     */
    public String getCharacterSetName() {
        return characterSetName;
    }
    /**
     * @param characterSetName Sets characterSetName to the specified value.
     */
    public void setCharacterSetName( String characterSetName ) {
        this.characterSetName = characterSetName;
    }
    /**
     * @return collationName
     */
    public String getCollationName() {
        return collationName;
    }
    /**
     * @param collationName Sets collationName to the specified value.
     */
    public void setCollationName( String collationName ) {
        this.collationName = collationName;
    }
    /**
     * @return currency
     */
    public boolean isCurrency() {
        return currency;
    }
    /**
     * @param currency Sets currency to the specified value.
     */
    public void setCurrency( boolean currency ) {
        this.currency = currency;
    }
    /**
     * @return defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }
    /**
     * @param defaultValue Sets defaultValue to the specified value.
     */
    public void setDefaultValue( String defaultValue ) {
        this.defaultValue = defaultValue;
    }
    /**
     * @return format
     */
    public String getFormat() {
        return format;
    }
    /**
     * @param format Sets format to the specified value.
     */
    public void setFormat( String format ) {
        this.format = format;
    }
    /**
     * @return length
     */
    public int getLength() {
        return length;
    }
    /**
     * @param length Sets length to the specified value.
     */
    public void setLength( int length ) {
        this.length = length;
    }
    /**
     * @return lengthFixed
     */
    public boolean isLengthFixed() {
        return lengthFixed;
    }
    /**
     * @param lengthFixed Sets lengthFixed to the specified value.
     */
    public void setLengthFixed( boolean lengthFixed ) {
        this.lengthFixed = lengthFixed;
    }
    /**
     * @return maximumValue
     */
    public String getMaximumValue() {
        return maximumValue;
    }
    /**
     * @param maximumValue Sets maximumValue to the specified value.
     */
    public void setMaximumValue( String maximumValue ) {
        this.maximumValue = maximumValue;
    }
    /**
     * @return minimumValue
     */
    public String getMinimumValue() {
        return minimumValue;
    }
    /**
     * @param minimumValue Sets minimumValue to the specified value.
     */
    public void setMinimumValue( String minimumValue ) {
        this.minimumValue = minimumValue;
    }
    /**
     * @return precision
     */
    public int getPrecision() {
        return precision;
    }
    /**
     * @param precision Sets precision to the specified value.
     */
    public void setPrecision( int precision ) {
        this.precision = precision;
    }
    /**
     * @return scale
     */
    public int getScale() {
        return scale;
    }
    /**
     * @param scale Sets scale to the specified value.
     */
    public void setScale( int scale ) {
        this.scale = scale;
    }
    /**
     * @return radix
     */
    public int getRadix() {
        return radix;
    }
    /**
     * @param radix Sets radix to the specified value.
     */
    public void setRadix( int radix ) {
        this.radix = radix;
    }
    /**
     * @return signed
     */
    public boolean isSigned() {
        return signed;
    }
    /**
     * @param signed Sets signed to the specified value.
     */
    public void setSigned( boolean signed ) {
        this.signed = signed;
    }
    /**
     * @return searchability
     */
    public String getSearchability() {
        return searchability;
    }
    /**
     * @param searchability Sets searchability to the specified value.
     */
    public void setSearchability( String searchability ) {
        this.searchability = searchability;
    }
    /**
     * @return selectable
     */
    public boolean isSelectable() {
        return selectable;
    }
    /**
     * @param selectable Sets selectable to the specified value.
     */
    public void setSelectable( boolean selectable ) {
        this.selectable = selectable;
    }
    /**
     * @return updateable
     */
    public boolean isUpdateable() {
        return updateable;
    }
    /**
     * @param updateable Sets updateable to the specified value.
     */
    public void setUpdateable( boolean updateable ) {
        this.updateable = updateable;
    }
    
	@Override
	public void validate() {
		// Walk through the properties for the table and set the status
		super.validate();
	}
    
    public void setProperties(Properties props) {
        for( Object key : props.keySet() ) {
            String keyStr = (String)key;
            String value = props.getProperty(keyStr);
            
            if( value != null && value.length() == 0 ) {
                continue;
            }
            
            if( keyStr.equalsIgnoreCase(KEY_NAME) ) {
                setName(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NAME_IN_SOURCE) ) {
                setNameInSource(value);
            } else if(keyStr.equalsIgnoreCase(KEY_DESCRIPTION) ) {
                setDescription(value);
            } else if(keyStr.equalsIgnoreCase(KEY_AUTO_INCREMENTED) ) {
                setAutoIncremented(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_LENGTH) ) {
                setLength(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_CASE_SENSITIVE) ) {
                setCaseSensitive(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_CHARACTER_SET_NAME) ) {
                setCharacterSetName(value);
            } else if(keyStr.equalsIgnoreCase(KEY_COLLATION_NAME) ) {
                setCollationName(value);
            } else if(keyStr.equalsIgnoreCase(KEY_CURRENCY) ) {
                setCurrency(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_DATATYPE) ) {
                setDatatype(value);
            } else if(keyStr.equalsIgnoreCase(KEY_DEFAULT_VALUE) ) {
                setDefaultValue(value);
            } else if(keyStr.equalsIgnoreCase(KEY_DISTINCT_VALUE_COUNT) ) {
                setDistinctValueCount(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_FORMAT) ) {
                setFormat(value);
            } else if(keyStr.equalsIgnoreCase(KEY_LENGTH_FIXED) ) {
                setLengthFixed(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_MAXIMUM_VALUE) ) {
                setMaximumValue(value);
            } else if(keyStr.equalsIgnoreCase(KEY_MINIMUM_VALUE) ) {
                setMinimumValue(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NATIVE_TYPE) ) {
                setNativeType(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NULLABLE) ) {
                setNullable(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NULL_VALUE_COUNT) ) {
                setNullValueCount(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_PRECISION) ) {
                setPrecision(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_SCALE) ) {
                setScale(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_RADIX) ) {
                setRadix(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_SEARCHABILITY) ) {
                setSearchability(value);
            } else if(keyStr.equalsIgnoreCase(KEY_SELECTABLE) ) {
                setSelectable(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_SIGNED) ) {
                setSigned(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_UPDATEABLE) ) {
                setUpdateable(Boolean.parseBoolean(value));
            }
        }
    }
}
