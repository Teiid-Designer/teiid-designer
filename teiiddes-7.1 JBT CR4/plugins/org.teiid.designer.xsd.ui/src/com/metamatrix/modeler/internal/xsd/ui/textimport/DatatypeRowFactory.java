/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.textimport;

import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.tools.textimport.ui.wizards.IRowObject;

/**
 * @since 4.2
 */
public class DatatypeRowFactory {

    public static final int TYPE = 15;
    public static final int ENUM = 16;
    public static final String TYPE_STRING = "TYPE"; //$NON-NLS-1$
    public static final String ENUM_STRING = "ENUM"; //$NON-NLS-1$

    private static final char COMMA = IRowObject.COMMA;
    private static final StringNameValidator nameValidator = new StringNameValidator();

    /**
     * @since 4.2
     */
    public DatatypeRowFactory() {
        super();
    }

    public IRowObject createRowObject( String rawString ) {
        IRowObject newRowObject = null;

        // Given a row object, let's create a row for it.
        // Let's parse the initial string and check object type.
        int index = rawString.indexOf(COMMA); // Name should not have double quotes

        if (index == -1 || index >= rawString.length()) return null;

        String typeString = rawString.substring(0, index).trim();
        String restOfRow = rawString.substring(index + 1).trim();

        int objectType = getObjectType(typeString);

        switch (objectType) {
            case TYPE: {
                newRowObject = new DatatypeAtomicRowObject(restOfRow);
                break;
            }
            case ENUM: {
                newRowObject = new DatatypeEnumRowObject(restOfRow);
                break;
            }
        }

        if (newRowObject != null) {
            newRowObject.setRawString(rawString);
            newRowObject.parseRow();
        }

        return newRowObject;
    }

    private int getObjectType( String type ) {
        if (type.equals(TYPE_STRING)) {
            return TYPE;
        } else if (type.equals(ENUM_STRING)) {
            return ENUM;
        }

        return IRowObject.UNKNOWN;
    }

    public static String createValidName( final String input, // NO_UCD
                                          final boolean performValidityCheck ) {
        String validName = nameValidator.createValidName(input, performValidityCheck);
        if (validName != null) return validName;

        return input;
    }

}
