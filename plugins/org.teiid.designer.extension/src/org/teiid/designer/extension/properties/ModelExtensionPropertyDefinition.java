/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.properties;

import java.beans.PropertyChangeListener;

import org.teiid.core.properties.PropertyDefinition;

import com.metamatrix.core.util.CoreArgCheck;

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
     * @param listener the listener being registered to receive property definition property change events (never <code>null</code>)
     * @return <code>true</code> if the listener was successfully added
     */
    boolean addListener( PropertyChangeListener listener );

    /**
     * @return the namespace prefix (cannot be <code>null</code> or empty)
     */
    String getNamespacePrefix();

    /**
     * @return the runtime type (cannot be <code>null</code> or empty)
     */
    String getRuntimeType();

    /**
     * A simple identifier does not include the namespace prefix.
     * 
     * @return the simple identifier (cannot be <code>null</code> or empty)
     */
    String getSimpleId();

    /**
     * @param listener the listener being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the listener was successfully removed
     */
    boolean removeListener( PropertyChangeListener listener );

    /**
     * @param newAllowedValues the new allowed values (can be <code>null</code> but cannot have <code>null</code> values)
     */
    void setAllowedValues( String[] values );

    /**
     * @param newDescription the new description (can be <code>null</code> or empty)
     */
    void setDescription( String newDescription );

    /**
     * @return <code>true</code> if this property should be indexed for use by the Teiid server
     */
    boolean shouldBeIndexed();

    /**
     * The property names that can be changed.
     */
    public enum PropertyName {
        /**
         * The allowed values property.
         */
        ALLOWED_VALUES,

        /**
         * The description property.
         */
        DESCRIPTION
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
         */
        Type( String type ) {
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
         * If the runtime type cannot be converted, then {@link ModelExtensionPropertyDefinitionImpl.Type#STRING string} is
         * returned.
         * 
         * @param runtimeType the Teiid runtime type being converted (cannot be <code>null</code> or empty)
         * @return the model extension property definition type (never <code>null</code>)
         */
        public static Type convertRuntimeType( String runtimeType ) {
            CoreArgCheck.isNotEmpty(runtimeType, "runtimeType is empty"); //$NON-NLS-1$

            for (Type type : Type.values()) {
                if (type.getRuntimeType().equals(runtimeType)) {
                    return type;
                }
            }

            return Type.STRING;
        }

        /**
         * @param propId the string being checked (cannot be <code>null</code> or empty)
         * @return the namespace prefix or <code>null</code> if not found
         */
        public static String getNamespacePrefix( String propId ) {
            CoreArgCheck.isNotEmpty(propId, "propId is empty"); //$NON-NLS-1$

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

        public static String getPropertyId( String namespacePrefix,
                                            String propertySimpleId ) {
            CoreArgCheck.isNotEmpty(namespacePrefix, "namespacePrefix is empty"); //$NON-NLS-1$
            CoreArgCheck.isNotEmpty(propertySimpleId, "propertySimpleId is empty"); //$NON-NLS-1$
            return namespacePrefix + ModelExtensionPropertyDefinition.ID_DELIM + propertySimpleId;
        }

        /**
         * @param id the identifier being checked (cannot be <code>null</code> or empty)
         * @param namespacePrefix the namespace prefix used to determine the result (cannot be <code>null</code> or empty)
         * @return <code>true</code> if the identifier is a property definition ID for the specified namespace prefix
         */
        public static boolean isExtensionPropertyId( String id,
                                                     String namespacePrefix ) {
            CoreArgCheck.isNotEmpty(id, "id is empty"); //$NON-NLS-1$
            CoreArgCheck.isNotEmpty(namespacePrefix, "namespacePrefix is empty"); //$NON-NLS-1$

            if (id.startsWith(namespacePrefix + ModelExtensionPropertyDefinition.ID_DELIM)) {
                return (id.length() > (namespacePrefix.length() + Character.toString(ModelExtensionPropertyDefinition.ID_DELIM)
                                                                           .length()));
            }

            return false;
        }
    }

}
