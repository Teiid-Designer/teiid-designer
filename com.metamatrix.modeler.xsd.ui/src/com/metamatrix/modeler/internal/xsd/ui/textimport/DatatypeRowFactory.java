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

    public static String createValidName( final String input,
                                          final boolean performValidityCheck ) {
        String validName = nameValidator.createValidName(input, performValidityCheck);
        if (validName != null) return validName;

        return input;
    }

}
