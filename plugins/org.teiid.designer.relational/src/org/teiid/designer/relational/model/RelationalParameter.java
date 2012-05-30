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
public class RelationalParameter extends RelationalReference {
    public static final String KEY_DATATYPE = "DATATYPE"; //$NON-NLS-1$
    public static final String KEY_NATIVE_TYPE = "NATIVETYPE"; //$NON-NLS-1$
    public static final String KEY_NULLABLE = "NULLABLE"; //$NON-NLS-1$
    public static final String KEY_DIRECTION = "DIRECTION"; //$NON-NLS-1$
    public static final String KEY_DEFAULT_VALUE = "DEFAULTVALUE"; //$NON-NLS-1$
    public static final String KEY_LENGTH = "LENGTH"; //$NON-NLS-1$
    public static final String KEY_PRECISION = "PRECISION"; //$NON-NLS-1$
    public static final String KEY_RADIX = "RADIX"; //$NON-NLS-1$
    public static final String KEY_SCALE = "SCALE"; //$NON-NLS-1$
    
    public static final String DEFAULT_DATATYPE = null;
    public static final String DEFAULT_NATIVE_TYPE = null;
    public static final String DEFAULT_NULLABLE = NULLABLE.NULLABLE;
    public static final String DEFAULT_DIRECTION = DIRECTION.IN;
    public static final String DEFAULT_DEFAULT_VALUE = null;
    public static final int DEFAULT_LENGTH = 0;
    public static final int DEFAULT_PRECISION = 0;
    public static final int DEFAULT_RADIX = 0;
    public static final int DEFAULT_SCALE = 10;
    
    public static final int DEFAULT_STRING_LENGTH = 4000;
    
    private String  datatype;
    private String  nativeType;
    private String  nullable;
    private String  direction = DEFAULT_DIRECTION;
    private String  defaultValue;
    private int length;
    private int precision;
    private int radix;
    private int scale;
    
    public RelationalParameter() {
        super();
        setType(TYPES.PARAMETER);
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    /**
     * @param name
     */
    public RelationalParameter( String name ) {
        super(name);
        setType(TYPES.PARAMETER);
        setNameValidator(new RelationalStringNameValidator(false, true));
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
     * @return direction
     */
    public String getDirection() {
        return direction;
    }
    /**
     * @param direction Sets direction to the specified value.
     */
    public void setDirection( String direction ) {
        this.direction = direction;
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
    
	@Override
	public void validate() {
		// Walk through the properties for the parameter and set the status
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
            } else if(keyStr.equalsIgnoreCase(KEY_LENGTH) ) {
                setLength(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_DATATYPE) ) {
                setDatatype(value);
            } else if(keyStr.equalsIgnoreCase(KEY_DEFAULT_VALUE) ) {
                setDefaultValue(value);
            } else if(keyStr.equalsIgnoreCase(KEY_DIRECTION) ) {
                setDirection(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NATIVE_TYPE) ) {
                setNativeType(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NULLABLE) ) {
                setNullable(value);
            } else if(keyStr.equalsIgnoreCase(KEY_PRECISION) ) {
                setPrecision(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_SCALE) ) {
                setScale(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_RADIX) ) {
                setRadix(Integer.parseInt(value));
            } 
        }
    }
}
