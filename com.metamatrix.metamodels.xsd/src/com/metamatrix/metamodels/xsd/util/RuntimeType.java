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

package com.metamatrix.metamodels.xsd.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Runtime Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.sdt.SdtPackage#getRuntimeType()
 * @model
 * @generated
 */
public final class RuntimeType extends AbstractEnumerator {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The '<em><b>String</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #STRING_LITERAL
     * @model name="string"
     * @generated
     * @ordered
     */
    public static final int STRING = 0;

    /**
     * The '<em><b>Boolean</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #BOOLEAN_LITERAL
     * @model name="boolean"
     * @generated
     * @ordered
     */
    public static final int BOOLEAN = 1;

    /**
     * The '<em><b>Byte</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #BYTE_LITERAL
     * @model name="byte"
     * @generated
     * @ordered
     */
    public static final int BYTE = 2;

    /**
     * The '<em><b>Short</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SHORT_LITERAL
     * @model name="short"
     * @generated
     * @ordered
     */
    public static final int SHORT = 3;

    /**
     * The '<em><b>Char</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CHAR_LITERAL
     * @model name="char"
     * @generated
     * @ordered
     */
    public static final int CHAR = 4;

    /**
     * The '<em><b>Integer</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INTEGER_LITERAL
     * @model name="integer"
     * @generated
     * @ordered
     */
    public static final int INTEGER = 5;

    /**
     * The '<em><b>Long</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #LONG_LITERAL
     * @model name="long"
     * @generated
     * @ordered
     */
    public static final int LONG = 6;

    /**
     * The '<em><b>Biginteger</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #BIGINTEGER_LITERAL
     * @model name="biginteger"
     * @generated
     * @ordered
     */
    public static final int BIGINTEGER = 7;

    /**
     * The '<em><b>Float</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FLOAT_LITERAL
     * @model name="float"
     * @generated
     * @ordered
     */
    public static final int FLOAT = 8;

    /**
     * The '<em><b>Double</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DOUBLE_LITERAL
     * @model name="double"
     * @generated
     * @ordered
     */
    public static final int DOUBLE = 9;

    /**
     * The '<em><b>Bigdecimal</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #BIGDECIMAL_LITERAL
     * @model name="bigdecimal"
     * @generated
     * @ordered
     */
    public static final int BIGDECIMAL = 10;

    /**
     * The '<em><b>Date</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DATE_LITERAL
     * @model name="date"
     * @generated
     * @ordered
     */
    public static final int DATE = 11;

    /**
     * The '<em><b>Time</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #TIME_LITERAL
     * @model name="time"
     * @generated
     * @ordered
     */
    public static final int TIME = 12;

    /**
     * The '<em><b>Timestamp</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #TIMESTAMP_LITERAL
     * @model name="timestamp"
     * @generated
     * @ordered
     */
    public static final int TIMESTAMP = 13;

    /**
     * The '<em><b>Object</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OBJECT_LITERAL
     * @model name="object"
     * @generated
     * @ordered
     */
    public static final int OBJECT = 14;

    /**
     * The '<em><b>Null</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NULL_LITERAL
     * @model name="null"
     * @generated
     * @ordered
     */
    public static final int NULL = 15;

    /**
     * The '<em><b>Blob</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #BLOB_LITERAL
     * @model name="blob"
     * @generated
     * @ordered
     */
    public static final int BLOB = 16;

    /**
     * The '<em><b>Clob</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CLOB_LITERAL
     * @model name="clob"
     * @generated
     * @ordered
     */
    public static final int CLOB = 17;

    /**
     * The '<em><b>Xml</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #XML_LITERAL
     * @model name="xml"
     * @generated
     * @ordered
     */
    public static final int XML = 18;

    /**
     * The '<em><b>String</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>String</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #STRING
     * @generated
     * @ordered
     */
    public static final RuntimeType STRING_LITERAL = new RuntimeType(STRING, "string"); //$NON-NLS-1$

    /**
     * The '<em><b>Boolean</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>BOOLEAN</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #BOOLEAN
     * @generated
     * @ordered
     */
    public static final RuntimeType BOOLEAN_LITERAL = new RuntimeType(BOOLEAN, "boolean"); //$NON-NLS-1$

    /**
     * The '<em><b>Byte</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>BYTE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #BYTE
     * @generated
     * @ordered
     */
    public static final RuntimeType BYTE_LITERAL = new RuntimeType(BYTE, "byte"); //$NON-NLS-1$

    /**
     * The '<em><b>Short</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>SHORT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #SHORT
     * @generated
     * @ordered
     */
    public static final RuntimeType SHORT_LITERAL = new RuntimeType(SHORT, "short"); //$NON-NLS-1$

    /**
     * The '<em><b>Char</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>CHAR</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CHAR
     * @generated
     * @ordered
     */
    public static final RuntimeType CHAR_LITERAL = new RuntimeType(CHAR, "char"); //$NON-NLS-1$

    /**
     * The '<em><b>Integer</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INTEGER</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INTEGER
     * @generated
     * @ordered
     */
    public static final RuntimeType INTEGER_LITERAL = new RuntimeType(INTEGER, "integer"); //$NON-NLS-1$

    /**
     * The '<em><b>Long</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>LONG</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #LONG
     * @generated
     * @ordered
     */
    public static final RuntimeType LONG_LITERAL = new RuntimeType(LONG, "long"); //$NON-NLS-1$

    /**
     * The '<em><b>Biginteger</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>BIGINTEGER</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #BIGINTEGER
     * @generated
     * @ordered
     */
    public static final RuntimeType BIGINTEGER_LITERAL = new RuntimeType(BIGINTEGER, "biginteger"); //$NON-NLS-1$

    /**
     * The '<em><b>Float</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>FLOAT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #FLOAT
     * @generated
     * @ordered
     */
    public static final RuntimeType FLOAT_LITERAL = new RuntimeType(FLOAT, "float"); //$NON-NLS-1$

    /**
     * The '<em><b>Double</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DOUBLE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DOUBLE
     * @generated
     * @ordered
     */
    public static final RuntimeType DOUBLE_LITERAL = new RuntimeType(DOUBLE, "double"); //$NON-NLS-1$

    /**
     * The '<em><b>Bigdecimal</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>BIGDECIMAL</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #BIGDECIMAL
     * @generated
     * @ordered
     */
    public static final RuntimeType BIGDECIMAL_LITERAL = new RuntimeType(BIGDECIMAL, "bigdecimal"); //$NON-NLS-1$

    /**
     * The '<em><b>Date</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DATE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DATE
     * @generated
     * @ordered
     */
    public static final RuntimeType DATE_LITERAL = new RuntimeType(DATE, "date"); //$NON-NLS-1$

    /**
     * The '<em><b>Time</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>TIME</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #TIME
     * @generated
     * @ordered
     */
    public static final RuntimeType TIME_LITERAL = new RuntimeType(TIME, "time"); //$NON-NLS-1$

    /**
     * The '<em><b>Timestamp</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>TIMESTAMP</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #TIMESTAMP
     * @generated
     * @ordered
     */
    public static final RuntimeType TIMESTAMP_LITERAL = new RuntimeType(TIMESTAMP, "timestamp"); //$NON-NLS-1$

    /**
     * The '<em><b>Object</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>OBJECT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #OBJECT
     * @generated
     * @ordered
     */
    public static final RuntimeType OBJECT_LITERAL = new RuntimeType(OBJECT, "object"); //$NON-NLS-1$

    /**
     * The '<em><b>Null</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NULL</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #NULL
     * @generated
     * @ordered
     */
    public static final RuntimeType NULL_LITERAL = new RuntimeType(NULL, "null"); //$NON-NLS-1$

    /**
     * The '<em><b>Blob</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>BLOB</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #BLOB
     * @generated
     * @ordered
     */
    public static final RuntimeType BLOB_LITERAL = new RuntimeType(BLOB, "blob"); //$NON-NLS-1$

    /**
     * The '<em><b>Clob</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>CLOB</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CLOB
     * @generated
     * @ordered
     */
    public static final RuntimeType CLOB_LITERAL = new RuntimeType(CLOB, "clob"); //$NON-NLS-1$

    /**
     * The '<em><b>Xml</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>XML</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #XML
     * @generated
     * @ordered
     */
    public static final RuntimeType XML_LITERAL = new RuntimeType(XML, "xml"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Runtime Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final RuntimeType[] VALUES_ARRAY =
        new RuntimeType[] {
            STRING_LITERAL,
            BOOLEAN_LITERAL,
            BYTE_LITERAL,
            SHORT_LITERAL,
            CHAR_LITERAL,
            INTEGER_LITERAL,
            LONG_LITERAL,
            BIGINTEGER_LITERAL,
            FLOAT_LITERAL,
            DOUBLE_LITERAL,
            BIGDECIMAL_LITERAL,
            DATE_LITERAL,
            TIME_LITERAL,
            TIMESTAMP_LITERAL,
            OBJECT_LITERAL,
            NULL_LITERAL,
            BLOB_LITERAL,
            CLOB_LITERAL,
            XML_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Runtime Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Runtime Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static RuntimeType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            RuntimeType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Runtime Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static RuntimeType get(int value) {
        switch (value) {
            case STRING: return STRING_LITERAL;
            case BOOLEAN: return BOOLEAN_LITERAL;
            case BYTE: return BYTE_LITERAL;
            case SHORT: return SHORT_LITERAL;
            case CHAR: return CHAR_LITERAL;
            case INTEGER: return INTEGER_LITERAL;
            case LONG: return LONG_LITERAL;
            case BIGINTEGER: return BIGINTEGER_LITERAL;
            case FLOAT: return FLOAT_LITERAL;
            case DOUBLE: return DOUBLE_LITERAL;
            case BIGDECIMAL: return BIGDECIMAL_LITERAL;
            case DATE: return DATE_LITERAL;
            case TIME: return TIME_LITERAL;
            case TIMESTAMP: return TIMESTAMP_LITERAL;
            case OBJECT: return OBJECT_LITERAL;
            case NULL: return NULL_LITERAL;
            case BLOB: return BLOB_LITERAL;
            case CLOB: return CLOB_LITERAL;
            case XML: return XML_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private RuntimeType(int value, String name) {
        super(value, name);
    }

} //RuntimeType
