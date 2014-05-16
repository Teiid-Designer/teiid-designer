/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 *
 * @since 8.0
 */
public final class ModelType implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6569679435239246363L;

    /**
     *
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$ // NO_UCD

    /**
     * Enumerator versions of the ModelType literals
     */
    public static enum Type {
        /**
         * Enum version of {@link ModelType} literal
         */
        PHYSICAL(true),

        /**
         * Enum version of {@link ModelType} literal
         */
        VIRTUAL(true),

        /**
         * Enum version of {@link ModelType} literal
         */
        TYPE(true),

        /**
         * Enum version of {@link ModelType} literal
         */
        VDB_ARCHIVE(false),

        /**
         * Enum version of {@link ModelType} literal
         */
        UNKNOWN(false),

        /**
         * Enum version of {@link ModelType} literal
         */
        FUNCTION(false),

        /**
         * Enum version of {@link ModelType} literal
         */
        CONFIGURATION(false),

        /**
         * Enum version of {@link ModelType} literal
         */
        METAMODEL(true),

        /**
         * Enum version of {@link ModelType} literal
         */
        EXTENSION(true),

        /**
         * Enum version of {@link ModelType} literal
         */
        LOGICAL(true),

        /**
         * Enum version of {@link ModelType} literal
         */
        MATERIALIZATION(false);

        private final boolean sheddable;

        private static List<String> nameCache;

        /**
         * private constructor
         */
        private Type(boolean sheddable) {
            this.sheddable = sheddable;
        }

        /**
         * @return the sheddable
         */
        public boolean isSheddable() {
            return this.sheddable;
        }

        /**
         * @return the name.
         */
        public final String getName() {
            return name();
        }

        /**
         * @return CamelCase version of name
         */
        public final String getCamelCaseName() {
            return StringUtilities.toCamelCase(getName());
        }

        /**
         * @return the value.
         */
        public final int getValue() {
            return ordinal();
        }

        /**
         * @return the literal.
         */
        public final String getLiteral() {
            return name();
        }

        /**
         * @return the literal.
         */
        @Override
        public final String toString() {
            return name();
        }

        /**
         * @return array of the type names
         */
        public static List<String> getNames() {
            if (nameCache == null) {
                nameCache = new ArrayList<String>();
                for (Type type : Type.values()) {
                    nameCache.add(type.getName());
                }
            }

            return Collections.unmodifiableList(nameCache);
        }

        /**
         * @param s
         * @return index of the given string once successfully parsed
         */
        public static int parseString(String s) {
            if (! getNames().contains(s))
                throw new IllegalArgumentException("Unknown model type"); //$NON-NLS-1$

            Type type = Type.valueOf(s);
            return type.getValue();
        }

        /**
         * @param index
         * @return literal for the given index
         */
        public static String getString(int index) {
            Type[] enumValues = values();
            if (index < 0 || index >= enumValues.length)
                throw new IllegalArgumentException("Unknown model type"); //$NON-NLS-1$

            return enumValues[index].getLiteral();
        }

        /**
         * @param index
         * @return type for given index
         */
        public static Type getType(int index) {
            Type[] enumValues = values();
            if (index < 0 || index >= enumValues.length)
                throw new IllegalArgumentException("Unknown model type"); //$NON-NLS-1$

            return enumValues[index];
        }
    }

    /**
     * The '<em><b>PHYSICAL</b></em>' literal value.
     */
    public static final int PHYSICAL = Type.PHYSICAL.getValue();

    /**
     * The '<em><b>VIRTUAL</b></em>' literal value.
     */
    public static final int VIRTUAL = Type.VIRTUAL.getValue();

    /**
     * The '<em><b>TYPE</b></em>' literal value.
     */
    public static final int TYPE = Type.TYPE.getValue();

    /**
     * The '<em><b>VDB ARCHIVE</b></em>' literal value.
     */
    public static final int VDB_ARCHIVE = Type.VDB_ARCHIVE.getValue();

    /**
     * The '<em><b>UNKNOWN</b></em>' literal value.
     */
    public static final int UNKNOWN = Type.UNKNOWN.getValue();

    /**
     * The '<em><b>FUNCTION</b></em>' literal value.
     */
    public static final int FUNCTION = Type.FUNCTION.getValue();

    /**
     * The '<em><b>CONFIGURATION</b></em>' literal value.
     */
    public static final int CONFIGURATION = Type.CONFIGURATION.getValue();

    /**
     * The '<em><b>METAMODEL</b></em>' literal value.
     */
    public static final int METAMODEL = Type.METAMODEL.getValue();

    /**
     * The '<em><b>EXTENSION</b></em>' literal value.
     */
    public static final int EXTENSION = Type.EXTENSION.getValue();

    /**
     * The '<em><b>LOGICAL</b></em>' literal value.
     */
    public static final int LOGICAL = Type.LOGICAL.getValue();

    /**
     * The '<em><b>MATERIALIZATION</b></em>' literal value.
     */
    public static final int MATERIALIZATION = Type.MATERIALIZATION.getValue();

    /**
     * @param itemType
     * @return whether given item type is shreddable
     */
    public static boolean isShredable(final int itemType) { // NO_UCD
        Type[] enumValues = Type.values();
        if (itemType < 0 || itemType >= enumValues.length)
            throw new IllegalArgumentException("Unknown model type"); //$NON-NLS-1$

        return enumValues[itemType].isSheddable();
    }
}
