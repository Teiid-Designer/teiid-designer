/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;

/**
 *
 */
public interface MxdConstants extends StringConstants {

    /**
     * Extension metadata annotation property
     */
    String EXTENSION_METADATA_PROP = "ExtensionMetadataProperty"; //$NON-NLS-1$

    /**
     * Package used for teiid applicable classes
     */
    String TEIID_PACKAGE = "org.teiid.metadata"; //$NON-NLS-1$

    /**
     * Package used for applicable classes
     */
    String DESIGNER_PACKAGE = "org.teiid.designer.metamodels.relational.impl"; //$NON-NLS-1$

    /**
     * IMPL keyword
     */
    String IMPL = "Impl"; //$NON-NLS-1$

    /**
     * Attributes used by Extension metadata annotation
     */
    enum AnnotationProperties {
        APPLICABLE,

        ADVANCED,

        DATATYPE,

        DISPLAY,

        DESCRIPTION,

        REQUIRED;

        /**
         * @return id
         */
        public Object getId() {
            return name().toLowerCase();
        }

        /**
         * @param id
         * @return annotation with id
         */
        public static AnnotationProperties findKey(String id) {
            for (AnnotationProperties property : AnnotationProperties.values()) {
                if (property.getId().equals(id))
                    return property;
            }

            throw new IllegalStateException("Annotation property with id " + id + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Mappings for the target object type referenced in the
     * teiid annotation and contained in the owner property
     * of {@link TeiidPropertyDefinition}
     */
    enum TargetObjectMappings {

        /**
         *
         */
        TABLE,

        /**
         *
         */
        PROCEDURE,

        /**
         *
         */
        COLUMN,
    	
    	/**
        *
        */
    	PROCEDUREPARAMETER;

        private final String className;

        private TargetObjectMappings() {
        	String name = name();
        	if( name.toUpperCase().endsWith("PROCEDUREPARAMETER")) {
        		this.className = "ProcedureParameter"; //$NON-NLS-1$
        	} else {
        		this.className = StringUtilities.upperCaseFirstChar(name().toLowerCase());
        	}
        }

        public String getDesignerClass() {
            if (TABLE.name().equals(name()))
                return DESIGNER_PACKAGE + DOT + "Base" + className + IMPL; //$NON-NLS-1$

            return DESIGNER_PACKAGE + DOT + className + IMPL;
        }

        public String getTeiidClass() {
            return TEIID_PACKAGE + DOT + className;
        }

        public String getAnnotationClass() {
            return className + DOT + CLASS;
        }
    }

    /**
     * Valid data types used in mxd files
     */
    enum ValidDataTypes {

        BIG_INTEGER(BigInteger.class),

        BIG_DECIMAL(BigDecimal.class),

        BLOB(Blob.class),

        BOOLEAN(Boolean.class),

        BYTE(Byte.class),

        CHAR(char.class),

        CLOB(Clob.class),

        DATE(Date.class),

        DOUBLE(Double.class),

        FLOAT(Float.class),

        INTEGER(Integer.class),

        LONG(Long.class),

        OBJECT(Object.class),

        SHORT(short.class),

        STRING(String.class),

        TIME(Time.class),

        TIMESTAMP(Timestamp.class),

        XML(StringConstants.XML);

        private final String id;

        /**
         *
         */
        private ValidDataTypes(String id) {
            this.id = id.toLowerCase();
        }

        /**
         *
         */
        private ValidDataTypes(Class<?> dataClass) {
            this(dataClass.getSimpleName());
        }

        /**
         * @return the id
         */
        public String id() {
            return this.id;
        }

        public static boolean validateDataType(String dataType) {
            for (ValidDataTypes validDataType : ValidDataTypes.values()) {
                if (validDataType.id().equals(dataType))
                    return true;
            }

            throw new IllegalStateException("The data type " + dataType + " is not recognised"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
