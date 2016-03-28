/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.teiid.core.designer.TeiidDesignerRuntimeException;

/**
 * This class contains static utilities that return strings that are the result of manipulating other strings or objects.
 * 
 * @since 8.0
 */
public class StringUtilities implements StringConstants {

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
     * @return <code>true</code> if the supplied strings are different.
     */
    public static boolean areDifferent( final String string1,
                                     final String string2 ) {
    	if (string1 == null) return string2 != null;
        return !string1.equals(string2);
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
        final ArrayList<String> result = new ArrayList<String>();
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
     * Indicates if the specified text is not empty or <code>null</code>.
     * 
     * @param text the text being checked (may be not be <code>null</code>)
     * @return <code>true</code> if the specified text is not empty or <code>null</code>
     */
    public static boolean isNotEmpty( final String text ) {
        return ((text != null) && (text.trim().length() > 0));
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
    
    /**
     * Parses a comma-separated list into an array of strings into an array of strings
     * Values can contain whitespace, but whitespace at the beginning and end of each value is trimmed.
     * @return array of Strings
     * @param csvList a string of comma separated values
     */
    public static String[] parseCommaDelimitedString(String csvString) {
        String[] result = parseList(csvString, COMMA);
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].trim();
        }
        
        return result;
    }

    /**
     * Parses a delimited string using the specified delimiter.
     * @param list a string of token separated values
     * @param delimiter the delimiter character(s).  Each character in the string is a single delimiter.
     * @return an array of strings
     */
    public static String[] parseList(String delimitedString, String delimiter) {
        List<String> result = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(delimitedString, delimiter);
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        
        return result.toArray(new String[0]);
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

    /**
     * @param value
     * @return CamelCase version of the given string, ie. converting '_' to capital letters as well
     *                 as capitalising the first letter.
     */
    public static String toCamelCase( final String value ) {
        StringBuffer sb = new StringBuffer();
        for (String s : value.split(UNDERSCORE)) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1, s.length()).toLowerCase());
            }
        }

        return sb.toString();
    }

    /**
     * Converts camel case string into 'normal' words-and-spaces, eg. BasicVdb -> Basic Vdb
     * <p>
     * <li>lowercase -> lowercase
     * <li>Class -> Class
     * <li>MyClass -> My Class
     * <li>HTML -> HTML
     * <li>PDFLoader -> PDF Loader
     * <li>AString -> A String
     * <li>SimpleXMLParser -> Simple XML Parser
     * <li>GL11Version -> GL 11 Version
     * <li>99Bottles -> 99 Bottles
     * <li>May5 -> May 5
     * <li>BFG9000 -> BFG 9000
     *</p>
     *
     * @param value
     * @return spaced version of camel case word
     */
    public static String fromCamelCase(final String value) {
        if (value == null)
            return EMPTY_STRING;

        String regexp = String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])",  //$NON-NLS-1$//$NON-NLS-2$
                                                                                   "(?<=[^A-Z])(?=[A-Z])", //$NON-NLS-1$
                                                                                   "(?<=[A-Za-z])(?=[^A-Za-z])"); //$NON-NLS-1$
        return value.replaceAll(regexp, SPACE);
    }
    
    public static String getUniqueName(String baseName, Set<String> otherNames, boolean appendInteger, boolean appendWithSpace, int countLimit) {
		int count = 1;
		// Set the newName to baseName
		String newName = baseName;
		
		// append count to baseName
		if( appendWithSpace ) {
			 newName = baseName + StringConstants.SPACE + count;
		} else {
			newName = baseName + count;
		}
		
		
		if( otherNames.contains(newName)) {
			if( appendWithSpace ) {
				newName = baseName + StringConstants.SPACE + count;
			} else {
				newName = baseName + count;
			}
			
			while( count < countLimit) {
				if(!otherNames.contains(newName)){
					return newName;
				} else {
					count++;
					newName = baseName + StringConstants.SPACE + count;
				}
			}
		} else {
			return newName;
		}
		
		return baseName;
    }

    /**
     * @param c the character being checked
     * @return <code>true</code> if the character is a letter
     */
    public static boolean isLetter( char c ) {
        return isBasicLatinLetter(c) || Character.isLetter(c);
    }

    /**
     * @param c the character being checked
     * @return <code>true</code> if the character is a letter or digit
     */
    public static boolean isLetterOrDigit( char c ) {
        return isBasicLatinLetter(c) || isBasicLatinDigit(c) || Character.isLetterOrDigit(c);
    }

    /**
     * @param text
     * @return text is either null or empty
     */
    public static boolean isBlank(final String text) {
        return ((text == null) || (text.trim().length() == 0));
    }

    /**
     * @param text the text being checked (can be empty)
     * @return <code>true</code> if the text can be converted to a number
     */
    public static boolean isNumber( final String text ) {
        return ( !isBlank( text ) && text.matches( "-?\\d+(\\.\\d+)?" ) ); //$NON-NLS-1$
    }

    private static boolean isBasicLatinLetter( char c ) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isBasicLatinDigit( char c ) {
        return c >= '0' && c <= '9';
    }

    /**
     * Replace all occurrences of the search string with the replace string
     * in the source string. If any of the strings is null or the search string
     * is zero length, the source string is returned.
     * @param source the source string whose contents will be altered
     * @param search the string to search for in source
     * @param replace the string to substitute for search if present
     * @return source string with *all* occurrences of the search string
     * replaced with the replace string
     */
    public static String replaceAll( String source,
                                     String search,
                                     String replace ) {
        if (source != null && search != null && search.length() > 0 && replace != null) {
            int start = source.indexOf(search);
            if (start > -1) {
                StringBuffer newString = new StringBuffer(source);
                replaceAll(newString, search, replace);
                return newString.toString();
            }
        }
        return source;
    }

    /**
     * @param source the source string whose contents will be altered
     * @param search the string to search for in source
     * @param replace the string to substitute for search if present
     */
    public static void replaceAll( StringBuffer source,
                                   String search,
                                   String replace ) {
        if (source != null && search != null && search.length() > 0 && replace != null) {
            int start = source.toString().indexOf(search);
            while (start > -1) {
                int end = start + search.length();
                source.replace(start, end, replace);
                start = source.toString().indexOf(search, start + replace.length());
            }
        }
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub

    	String startName = "XXXXX";
    	Set<String> otherNames = new HashSet<String>();
    	otherNames.add("XXXXX");
    	otherNames.add("YYYYY");
    	
    	String newName = StringUtilities.getUniqueName(startName, otherNames, true, true, 1000);
    	System.out.println("  START NAME = " + startName + " UNIQUE NAME = " + newName);
    	
    	otherNames.add("XXXXX 1");
    	
    	newName = StringUtilities.getUniqueName(startName, otherNames, true, true, 1000);
    	System.out.println("  START NAME = " + startName + " UNIQUE NAME = " + newName);
    	
    	otherNames.add("XXXXX 2");
    	
    	newName = StringUtilities.getUniqueName(startName, otherNames, true, true, 1000);
    	System.out.println("  START NAME = " + startName + " UNIQUE NAME = " + newName);
    }

    /**
        * Compares two strings lexicographically. 
        * The comparison is based on the Unicode value of each character in
        * the strings. 
     * @param str1
     * @param str2 
        *
        * @return  the value <code>0</code> if the str1 is equal to str2;
        *          a value less than <code>0</code> if str1
        *          is lexicographically less than str2; 
        *          and a value greater than <code>0</code> if str1 is
        *          lexicographically greater than str2.
        */
    public static int compare(char[] str1, char[] str2) {
        int len1 = str1.length;
        int len2 = str2.length;
        int n = Math.min(len1, len2);
        int i = 0;
        while (n-- != 0) {
            char c1 = str1[i];
            char c2 = str2[i++];
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return len1 - len2;
    }

    /**
        * Returns the length of the common prefix between s1 and s2.
     * @param s1
     * @param s2 
     * @return length of the prefix
        */
    public static int prefixLength(char[] s1, char[] s2) {
        int len = 0;
        int max = Math.min(s1.length, s2.length);
        for (int i = 0; i < max && s1[i] == s2[i]; ++i)
            ++len;
        return len;
    }

    /**
     * Returns the length of the common prefix between s1 and s2.
     * @param s1 
     * @param s2 
     * @return length of the prefix
     */
    public static int prefixLength(String s1, String s2) {
        int len = 0;
        int max = Math.min(s1.length(), s2.length());
        for (int i = 0; i < max && s1.charAt(i) == s2.charAt(i); ++i)
            ++len;
        return len;
    } 
    
    public static String removeXmiExtension(String input) {
    	if( input.toUpperCase().endsWith(".XMI") ) {
    		return input.substring(0, input.length() - 4);
    	}
    	
    	return input;
    }

    private StringUtilities() {
    }
}
