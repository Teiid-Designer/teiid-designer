/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.util;

public class NameUtil {

    public static String normalizeName( String nameIn ) {
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
        edit = edit.replace('/', '_');
        edit = edit.replace('\\', '_');
        edit = edit.replace(':', '_');
        edit = edit.replace('\'', '_');
        edit = edit.replace('-', '_');
        edit = edit.replace("%", "percentage");//$NON-NLS-1$ //$NON-NLS-2$
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
