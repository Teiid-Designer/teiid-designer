/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.properties;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Set;

import org.eclipse.osgi.util.NLS;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.extension.Messages;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * A <code>ModelExtensionPropertyDefinition</code> is the property definition of all extension properties.
 */
public interface ModelExtensionPropertyDefinition extends PropertyDefinition {

    /**
     * The allowed boolean values.
     */
    String[] BOOLEAN_ALLOWED_VALUES = new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() };

    /**
     * The delimiter character between the namespace prefix and the simple identifier.
     */
    char ID_DELIM = ':';

    /**
     * @param newAllowedValue the allowed value being added (cannot be <code>null</code>)
     * @return <code>true</code> if the allowed value was added
     */
    boolean addAllowedValue( String newAllowedValue );

    /**
     * @param newDescription the translated description being added (cannot be <code>null</code>)
     * @return <code>true</code> if the description was added
     */
    boolean addDescription( Translation newDescription );

    /**
     * @param newDisplayName the translated display name being added (cannot be <code>null</code>)
     * @return <code>true</code> if the display name was added
     */
    boolean addDisplayName( Translation newDisplayName );

    /**
     * @param listener the listener being registered to receive property definition property change events (never <code>null</code>)
     * @return <code>true</code> if the listener was successfully added
     */
    boolean addListener( PropertyChangeListener listener );

    /**
     * The collection returned can be manipulated without affecting this property definition.
     * 
     * @return the allowed values (never <code>null</code> but can be empty)
     */
    Set<String> allowedValues();

    /**
     * The collection returned can be manipulated without affecting this property definition.
     * 
     * @return the descriptions (never <code>null</code> but can be empty)
     */
    Set<Translation> getDescriptions();

    /**
     * The collection returned can be manipulated without affecting this property definition.
     * 
     * @return the display names (never <code>null</code> but can be empty)
     */
    Set<Translation> getDisplayNames();

    /**
     * @return the fixed value (a non-<code>null</code> value means the value is unmodifiable)
     */
    String getFixedValue();

    /**
     * @return the namespace prefix (can be <code>null</code> or empty)
     */
    String getNamespacePrefix();

    /**
     * @return the runtime type (can be <code>null</code> or empty)
     */
    String getRuntimeType();

    /**
     * A simple identifier does not include the namespace prefix.
     * 
     * @return the simple identifier (can be <code>null</code> or empty)
     */
    String getSimpleId();

    /**
     * @return the type (can be <code>null</code> or empty)
     */
    Type getType();

    /**
     * @param allowedValue the allowed value being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the allowed value was removed
     */
    boolean removeAllowedValue( String allowedValue );

    /**
     * @param description the description being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the description was removed
     */
    boolean removeDescription( Translation description );

    /**
     * @param displayName the display name being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the display name was removed
     */
    boolean removeDisplayName( Translation displayName );

    /**
     * @param listener the listener being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the listener was successfully removed
     */
    boolean removeListener( PropertyChangeListener listener );

    /**
     * @param newAdvanced the new advanced value
     */
    void setAdvanced( boolean newAdvanced );

    /**
     * @param newAllowedValues the new allowed values (can be <code>null</code> but cannot have <code>null</code> values)
     */
    void setAllowedValues( Set<String> newAllowedValues );

    /**
     * @param newDefaultValue the new default value (can be <code>null</code> or empty)
     */
    void setDefaultValue( String newDefaultValue );

    /**
     * @param newDescriptions the new descriptions (can be <code>null</code> or empty)
     */
    void setDescriptions( Set<Translation> newDescriptions );

    /**
     * @param newDisplayNames the new display names (can be <code>null</code> or empty)
     */
    void setDisplayNames( Set<Translation> newDisplayNames );

    /**
     * @param newFixedValue the new fixed value (can be <code>null</code> or empty)
     */
    void setFixedValue( String newFixedValue );

    /**
     * @param newIndex the new index value
     */
    void setIndex( boolean newIndex );

    /**
     * @param newMasked the new masked value
     */
    void setMasked( boolean newMasked );

    /**
     * @param newNamespacePrefix the new namespace prefix (can be <code>null</code> or empty)
     */
    void setNamespacePrefix( String newNamespacePrefix );

    /**
     * @param newRequired the new required value
     */
    void setRequired( boolean newRequired );

    /**
     * @param newSimpleId the new simpleId (can be <code>null</code> or empty)
     */
    void setSimpleId( String newSimpleId );

    /**
     * @param runtimeType the Teiid runtime type (can be <code>null</code>)
     */
    void setType( Type runtimeType );

    /**
     * @return <code>true</code> if this property should be indexed for use by the Teiid server
     */
    boolean shouldBeIndexed();

    /**
     * The property names that can be changed.
     */
    public enum PropertyName {
        /**
         * Indicates if the property should only be modified by advanced users.
         */
        ADVANCED,

        /**
         * The allowed values property.
         */
        ALLOWED_VALUES,

        /**
         * The default value of the property (used when user has not entered a value).
         */
        DEFAULT_VALUE,

        /**
         * The description property.
         */
        DESCRIPTION,

        /**
         * The property name to display to the user.
         */
        DISPLAY_NAME,

        /**
         * The fixed property value.
         */
        FIXED_VALUE,

        /**
         * Indicates if the property should be indexed for use in the Teiid runtime.
         */
        INDEX,

        /**
         * Indicates if the property should be masked when displayed to the user.
         */
        MASKED,

        /**
         * The namespace prefix where the extension property is defined.
         */
        NAMESPACE_PREFIX,

        /**
         * Indicates if the property is required to have a value.
         */
        REQUIRED,

        /**
         * The property identifier without the namespace prefix.
         */
        SIMPLE_ID,

        /**
         * The Teiid runtime data type.
         */
        TYPE
    }

    /**
     * These runtime types <strong>MUST</strong>> match those listed in the model extension XSD.
     */
    public enum Type {
        BIG_DECIMAL("bigdecimal"), //$NON-NLS-1$
        BIG_INTEGER("biginteger"), //$NON-NLS-1$
        BLOB("blob"), //$NON-NLS-1$
        BOOLEAN("boolean"), //$NON-NLS-1$
        BYTE("byte"), //$NON-NLS-1$
        CHAR("char"), //$NON-NLS-1$
        CLOB("clob"), //$NON-NLS-1$
        DATE("date"), //$NON-NLS-1$
        DOUBLE("double"), //$NON-NLS-1$
        FLOAT("float"), //$NON-NLS-1$
        INTEGER("integer"), //$NON-NLS-1$
        LONG("long"), //$NON-NLS-1$
        OBJECT("object"), //$NON-NLS-1$
        SHORT("short"), //$NON-NLS-1$
        STRING("string"), //$NON-NLS-1$
        TIME("time"), //$NON-NLS-1$
        TIMESTAMP("timestamp"), //$NON-NLS-1$
        XML("xml"); //$NON-NLS-1$

        /**
         * The Teiid runtime type (never <code>null</code> or empty).
         */
        private final String runtimeType;

        /**
         * @param type the Teiid runtime type (cannot be <code>null</code>)
         * @throws IllegalArgumentException if the param is not valid
         */
        private Type( String type ) {
            assert type != null : "runtime type is null"; //$NON-NLS-1$
            this.runtimeType = type;
        }

        /**
         * @return the Teiid runtime type (never <code>null</code> or empty)
         */
        public String getRuntimeType() {
            return this.runtimeType;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return getRuntimeType();
        }
    }

    class Utils {

        /**
         * @param runtimeType the Teiid runtime type being converted (never <code>null</code> or empty)
         * @return the model extension property definition type (newver <code>null</code>)
         * @throws IllegalArgumentException if argument cannot be converted to a valid type
         */
        public static Type convertRuntimeType( String runtimeType ) {
            CoreArgCheck.isNotEmpty(runtimeType, "runtimeType is empty"); //$NON-NLS-1$

            for (Type type : Type.values()) {
                if (type.getRuntimeType().equals(runtimeType)) {
                    return type;
                }
            }

            throw new IllegalArgumentException(NLS.bind(Messages.invalidRuntimeType, runtimeType));
        }

        /**
         * @param thisValue the first value being compared (can be <code>null</code> or empty)
         * @param thatValue the other value being compared (can be <code>null</code> or empty)
         * @return <code>true</code> if values are equal or both values are empty
         */
        public static boolean valuesAreEqual( String thisValue,
                                              String thatValue ) {
            if (CoreStringUtil.isEmpty(thisValue) && CoreStringUtil.isEmpty(thatValue)) {
                return true;
            }

            return CoreStringUtil.equals(thisValue, thatValue);
        }

        /**
         * @param propId the string being checked (can be <code>null</code> or empty)
         * @return the namespace prefix or <code>null</code> if not found
         */
        public static String getNamespacePrefix( String propId ) {
            if (CoreStringUtil.isEmpty(propId)) {
                return null;
            }

            int index = propId.indexOf(ID_DELIM);

            if (index != -1) {
                // delimiter is first character or there are no characters after delimiter
                if ((index == 0) || (propId.length() == (index + 1))) {
                    return null;
                }

                return propId.substring(0, index);
            }

            return null;
        }

        /**
         * @param namespacePrefix the namespace prefix (can be <code>null</code> or empty)
         * @param propertySimpleId the simple identifier (can be <code>null</code> or empty)
         * @return the property ID or <code>null</code> if either the namespace prefix or simple identifier is empty
         */
        public static String getPropertyId( String namespacePrefix,
                                            String propertySimpleId ) {
            if (CoreStringUtil.isEmpty(namespacePrefix) || CoreStringUtil.isEmpty(propertySimpleId)) {
                return null;
            }

            return namespacePrefix + ModelExtensionPropertyDefinition.ID_DELIM + propertySimpleId;
        }

        /**
         * @param id the identifier being checked (can be <code>null</code> or empty)
         * @param namespacePrefix the namespace prefix used to determine the result (cannot be <code>null</code> or empty)
         * @return <code>true</code> if the identifier is a property definition ID for the specified namespace prefix
         */
        public static boolean isExtensionPropertyId( String id,
                                                     String namespacePrefix ) {
            CoreArgCheck.isNotEmpty(namespacePrefix, "namespacePrefix is empty"); //$NON-NLS-1$

            if ((id != null) && id.startsWith(namespacePrefix + ModelExtensionPropertyDefinition.ID_DELIM)) {
                return (id.length() > (namespacePrefix.length() + Character.toString(ModelExtensionPropertyDefinition.ID_DELIM)
                                                                           .length()));
            }

            return false;
        }

        /**
         * @param runtimeType the runtime type (can be <code>null</code>)
         * @param proposedValue the proposed value (can be <code>null</code> or empty)
         * @param required indicates if the property requires a value
         * @param allowedValues the allowed values (can be <code>null</code> or empty)
         * @return the error message or <code>null</code>
         */
        public static String isValidValue( Type runtimeType,
                                           String proposedValue,
                                           boolean required,
                                           String[] allowedValues ) {
            // must have a runtime type
            if (runtimeType == null) {
                return Messages.missingRuntimeTypeValidationMsg;
            }

            // must have a value
            if (CoreStringUtil.isEmpty(proposedValue)) {
                if (required) {
                    return Messages.emptyPropertyValue;
                }
            }

            // validate against allowed values first
            if ((allowedValues != null) && (allowedValues.length != 0)) {
                for (String allowedValue : allowedValues) {
                    if (allowedValue.equals(proposedValue)) {
                        // valid
                        return null;
                    }
                }

                // must match an allowed value
                return Messages.valueDoesNotMatchAnAllowedValue;
            }

            // no validation done on these types
            if ((Type.STRING == runtimeType) || (Type.BLOB == runtimeType) || (Type.CLOB == runtimeType)
                    || (Type.OBJECT == runtimeType) || (Type.XML == runtimeType)) {
                return null; // valid
            }

            if (Type.BOOLEAN == runtimeType) {
                if (!proposedValue.equalsIgnoreCase(Boolean.TRUE.toString())
                        && !proposedValue.equalsIgnoreCase(Boolean.FALSE.toString())) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.CHAR == runtimeType) {
                if (proposedValue.length() != 1) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.BYTE == runtimeType) {
                try {
                    Byte.parseByte(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.SHORT == runtimeType) {
                try {
                    Short.parseShort(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.INTEGER == runtimeType) {
                try {
                    Integer.parseInt(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.LONG == runtimeType) {
                try {
                    Long.parseLong(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.FLOAT == runtimeType) {
                try {
                    Float.parseFloat(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.DOUBLE == runtimeType) {
                try {
                    Double.parseDouble(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.BIG_INTEGER == runtimeType) {
                try {
                    new BigInteger(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.BIG_DECIMAL == runtimeType) {
                try {
                    new BigDecimal(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.DATE == runtimeType) {
                try {
                    Date.valueOf(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.TIME == runtimeType) {
                try {
                    Time.valueOf(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else if (Type.TIMESTAMP == runtimeType) {
                try {
                    Timestamp.valueOf(proposedValue);
                } catch (Exception e) {
                    return NLS.bind(Messages.invalidPropertyValueForType, proposedValue, runtimeType);
                }
            } else {
                // unknown property type
                return NLS.bind(Messages.unknownPropertyType, runtimeType);
            }

            // valid
            return null;
        }
    }

}
