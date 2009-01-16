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
package com.metamatrix.modeler.schema.tools;

public class NameUtil {
    public static String normalizeNameForRelationalTable( String nameIn ) {
        String normal = nameIn.trim();
        normal = removeDuplicate(normal);
        normal = removeSpaces(normal);
        normal = removeIllegalChars(normal);
        normal = removeTrailingUnderscore(normal);
        return normal;

    }

    private static String removeTrailingUnderscore( String normal ) {
        if (normal.endsWith("_")) { //$NON-NLS-1$
            return normal.substring(0, normal.lastIndexOf('_'));
        }
        return normal;
    }

    private static String removeIllegalChars( String normal ) {
        String edit = normal;
        edit = edit.replace('.', '_');
        edit = edit.replace('(', '_');
        edit = edit.replace(')', '_');
        return edit;
    }

    private static String removeSpaces( String normal ) {
        return normal.replace(' ', '_');
    }

    private static String removeDuplicate( String normal ) {
        if (normal.indexOf('(') < 0 || normal.indexOf(')') != normal.length() - 1) return normal;
        String firstPart = normal.substring(0, normal.indexOf('(')).trim();
        String secondPart = normal.substring(normal.indexOf('(') + 1, normal.length() - 1).trim();
        if (firstPart.equals(secondPart) || secondPart.equals("null")) return firstPart; //$NON-NLS-1$
        return normal;
    }
}
