/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.teiid.core.designer.TeiidDesignerRuntimeException;

/**
 * This class contains static utilities that return strings that are the result of manipulating other strings or objects.
 * 
 * @since 8.0
 */
public class StringUtilities {

    /**
     * An empty string.
     */
    public static String EMPTY_STRING = ""; //$NON-NLS-1$
    
    /**
     * A space.
     */
    public static String SPACE = " "; //$NON-NLS-1$
    
    /**
     * An underscore.
     */
    public static String UNDERSCORE = "_"; //$NON-NLS-1$

    /**
     * The String "\n"
     */
    public static final String NEW_LINE = "\n"; //$NON-NLS-1$

    /**
     * The name of the System property that specifies the string that should be used to separate lines. This property is a standard
     * environment property that is usually set automatically.
     */
    public static final String LINE_SEPARATOR_PROPERTY_NAME = "line.separator"; //$NON-NLS-1$

    /**
     * The String that should be used to separate lines; defaults to {@link #NEW_LINE}
     */
    public static final String LINE_SEPARATOR = System.getProperty(LINE_SEPARATOR_PROPERTY_NAME, NEW_LINE);

    /**
     * Returns the path representing the concatenation of the specified path prefix and suffix. The resulting path is guaranteed to
     * have exactly one file separator between the prefix and suffix.
     * 
     * @param prefix The path prefix
     * @param suffix The path suffix
     * @return The concatenated path prefix and suffix
     * @since 3.1
     */
    public static String buildPath( final String prefix,
                                    final String suffix ) {
        final StringBuffer path = new StringBuffer(prefix);
        if (!prefix.endsWith(File.separator)) path.append(File.separator);
        if (suffix.startsWith(File.separator)) path.append(suffix.substring(File.separator.length()));
        else path.append(suffix);
        return path.toString();
    }

    /**
     * @param originalString
     * @param maxLength
     * @param endLength
     * @param middleString
     * @return
     * @since 5.0
     */
    public static String condenseToLength( final String originalString,
                                           final int maxLength,
                                           final int endLength,
                                           final String middleString ) {
        if (originalString.length() <= maxLength) return originalString;
        final int originalLength = originalString.length();
        final StringBuffer sb = new StringBuffer(maxLength);
        sb.append(originalString.substring(0, maxLength - endLength - middleString.length()));
        sb.append(middleString);
        sb.append(originalString.substring(originalLength - endLength, originalLength));

        return sb.toString();
    }

    /**
     * @param string1 may be <code>null</code>
     * @param string2 may be <code>null</code>
     * @return <code>true</code> if the supplied strings are equal.
     */
    public static boolean equals( final String string1,
                                  final String string2 ) {
        if (string1 == null) return string2 == null;
        return string1.equals(string2);
    }

    /**
     * @param string1 may be <code>null</code>
     * @param string2 may be <code>null</code>
     * @return <code>true</code> if the supplied strings are equal, ignoring case.
     */
    public static boolean equalsIgnoreCase( final String string1,
                                            final String string2 ) {
        if (string1 == null) return string2 == null;
        return string1.equalsIgnoreCase(string2);
    }

    /**
     * Returns a new string that represents the last fragment of the original string that begins with an uppercase char. Ex:
     * "getSuperTypes" would return "Types".
     * 
     * @param value
     * @return String
     */
    public static String getLastUpperCharToken( final String value ) {
        if (value == null) return null;

        final StringBuffer result = new StringBuffer();
        for (int i = value.length() - 1; i >= 0; i--) {
            result.insert(0, value.charAt(i));
            if (Character.isUpperCase(value.charAt(i))) return result.toString();
        }

        return result.toString();
    }

    /**
     * Returns a new string that represents the last fragment of the original string that begins with an uppercase char. Ex:
     * "getSuperTypes" would return "Types".
     * 
     * @param value
     * @param lastToken - the last token tried... if not null will look backwards from the last token instead of the end of the
     *        value param
     * @return String
     */
    public static String getLastUpperCharToken( final String value,
                                                final String lastToken ) {
        if (value == null || lastToken == null) return value;

        final int index = value.lastIndexOf(lastToken);
        if (index == -1) return null;

        final StringBuffer result = new StringBuffer();
        for (int i = index - 1; i >= 0; i--) {
            result.insert(0, value.charAt(i));
            if (Character.isUpperCase(value.charAt(i))) return result.toString() + lastToken;
        }

        return result.toString() + lastToken;
    }

    public static String[] getLines( final String value ) {
        final StringReader stringReader = new StringReader(value);
        final BufferedReader reader = new BufferedReader(stringReader);
        final ArrayList result = new ArrayList();
        try {
            String line = reader.readLine();
            while (line != null) {
                result.add(line);
                line = reader.readLine();
            }
        } catch (final IOException e) {
            throw new TeiidDesignerRuntimeException(e);
        }
        return (String[])result.toArray(new String[result.size()]);
    }

    public static String getLineSeparator() {
        return LINE_SEPARATOR;
    }

    /**
     * Indicates if the specified text is either empty or <code>null</code>.
     * 
     * @param text the text being checked (may be <code>null</code>)
     * @return <code>true</code> if the specified text is either empty or <code>null</code>
     */
    public static boolean isEmpty( final String text ) {
        return ((text == null) || (text.trim().length() == 0));
    }

    /**
     * Returns a new string that lowercases the first character in the passed in value String
     * 
     * @param value
     * @return String
     */
    public static String lowerCaseFirstChar( final String value ) {
        if (value == null) return null;

        // Lower case the first char and try to look-up the SF
        String firstChar = new Character(value.charAt(0)).toString();
        firstChar = firstChar.toLowerCase();
        return (firstChar + value.substring(1));
    }

    public static String removeChars( final String value,
                                      final char[] chars ) {
        final StringBuffer result = new StringBuffer();
        if (value != null && chars != null && chars.length > 0) {
            final String removeChars = String.valueOf(chars);
            for (int i = 0; i < value.length(); i++) {
                final String character = value.substring(i, i + 1);
                if (removeChars.indexOf(character) == -1) result.append(character);
            }
        } else result.append(value);
        return result.toString();
    }

    /**
     * Replaces multiple sequential "whitespace" characters from the specified string with a single space character, where
     * whitespace includes \r\t\n and other characters
     * 
     * @param value the string to work with
     * @see java.util.regex.Pattern
     */
    public static String removeExtraWhitespace( final String value ) {
        return value.replaceAll("\\s\\s+", " "); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Replaces all "whitespace" characters from the specified string with space characters, where whitespace includes \r\t\n and
     * other characters
     * 
     * @param value the string to work with
     * @param stripExtras if true, replace multiple whitespace characters with a single character.
     * @see java.util.regex.Pattern
     */
    public static String replaceWhitespace( final String value,
                                            final boolean stripExtras ) {
        return replaceWhitespace(value, " ", stripExtras); //$NON-NLS-1$
    }

    /**
     * Replaces all "whitespace" characters from the specified string with space characters, where whitespace includes \r\t\n and
     * other characters
     * 
     * @param value the string to work with
     * @param replaceWith the character to replace with
     * @param stripExtras if true, replace multiple whitespace characters with a single character.
     * @see java.util.regex.Pattern
     */
    public static String replaceWhitespace( final String value,
                                            final String replaceWith,
                                            final boolean stripExtras ) {
        String rv = value.replaceAll("\\s+", replaceWith); //$NON-NLS-1$

        if (stripExtras) rv = removeExtraWhitespace(rv);

        return rv;
    }

    /**
     * Returns a new string that uppercases the first character in the passed in value String
     * 
     * @param value
     * @return String
     */
    public static String upperCaseFirstChar( final String value ) {
        if (value == null) return null;

        // Lower case the first char and try to look-up the SF
        String firstChar = new Character(value.charAt(0)).toString();
        firstChar = firstChar.toUpperCase();
        return (firstChar + value.substring(1));
    }

    private StringUtilities() {
    }
}
