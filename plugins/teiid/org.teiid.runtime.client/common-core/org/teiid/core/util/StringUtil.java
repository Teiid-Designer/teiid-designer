/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
*
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved.
 * This code is made available under the terms of the Eclipse Public
 * License, version 1.0.
*/
package org.teiid.core.util;

/**
 * This is a common place to put String utility methods.
 */
public final class StringUtil {

    /*
     * Replace a single occurrence of the search string with the replace string
     * in the source string. If any of the strings is null or the search string
     * is zero length, the source string is returned.
     * @param source the source string whose contents will be altered
     * @param search the string to search for in source
     * @param replace the string to substitute for search if present
     * @return source string with the *first* occurrence of the search string
     * replaced with the replace string
     */
    public static String replace(String source, String search, String replace) {
        if (source != null && search != null && search.length() > 0 && replace != null) {
            int start = source.indexOf(search);
            if (start > -1) {
                return new StringBuffer(source).replace(start, start + search.length(), replace).toString();
            }
        }
        return source;    
    }

	/*
	 * Replace all occurrences of the search string with the replace string
	 * in the source string. If any of the strings is null or the search string
	 * is zero length, the source string is returned.
	 * @param source the source string whose contents will be altered
	 * @param search the string to search for in source
	 * @param replace the string to substitute for search if present
	 * @return source string with *all* occurrences of the search string
	 * replaced with the replace string
	 */
	public static String replaceAll(String source, String search, String replace) {
	    if (source == null || search == null || search.length() == 0 || replace == null) {
	    	return source;
	    }
        int start = source.indexOf(search);
        if (start > -1) {
	        StringBuffer newString = new StringBuffer(source);
	        while (start > -1) {
	            int end = start + search.length();
	            newString.replace(start, end, replace);
	            start = newString.indexOf(search, start + replace.length());
	        }
	        return newString.toString();
        }
	    return source;    
	}

	/**
     * Tests if the string starts with the specified prefix.
     *
     * @param   text     the string to test.
     * @param   prefix   the prefix.
     * @return  <code>true</code> if the character sequence represented by the
     *          argument is a prefix of the character sequence represented by
     *          this string; <code>false</code> otherwise.      
     *          Note also that <code>true</code> will be returned if the 
     *          prefix is an empty string or is equal to the text 
     *          <code>String</code> object as determined by the 
     *          {@link #equals(Object)} method. If the text or 
     *          prefix argument is null <code>false</code> is returned.
     * @since   JDK1. 0
     */
    public static boolean startsWithIgnoreCase(final String text, final String prefix) {
        if (text == null || prefix == null) {
            return false;
        }
        return text.regionMatches(true, 0, prefix, 0, prefix.length());
    }

	/**
     * Tests if the string ends with the specified suffix.
     *
     * @param   text     the string to test.
     * @param   suffix   the suffix.
     * @return  <code>true</code> if the character sequence represented by the
     *          argument is a suffix of the character sequence represented by
     *          this object; <code>false</code> otherwise. Note that the 
     *          result will be <code>true</code> if the suffix is the 
     *          empty string or is equal to this <code>String</code> object 
     *          as determined by the {@link #equals(Object)} method. If the text or 
     *          suffix argument is null <code>false</code> is returned.
     */
    public static boolean endsWithIgnoreCase(final String text, final String suffix) {
    	if (text == null || suffix == null) {
            return false;
        }
        return text.regionMatches(true, text.length() - suffix.length(), suffix, 0, suffix.length());
    }

	public static boolean isLetter(char c) {
        return isBasicLatinLetter(c) || Character.isLetter(c);
    }
	public static boolean isDigit(char c) {
        return isBasicLatinDigit(c) || Character.isDigit(c);
    }
    public static boolean isLetterOrDigit(char c) {
        return isBasicLatinLetter(c) || isBasicLatinDigit(c) || Character.isLetterOrDigit(c);
    }
    public static boolean isValid(String str) {
    	return (!(str == null || str.trim().length() == 0));
    }

	private static boolean isBasicLatinLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    private static boolean isBasicLatinDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static int parseNumericValue(CharSequence string, StringBuilder sb, int i, int value, int possibleDigits, int radixExp) {
        for (int j = 0; j < possibleDigits; j++) {
            if (i + 1 == string.length()) {
                break;
            }
            char digit = string.charAt(i + 1);
            int val = Character.digit(digit, 1 << radixExp);
            if (val == -1) {
                break;
            }
            i++;
            value = (value << radixExp) + val;
        }
        sb.append((char)value);
        return i;
    }

    /**
     * Unescape the given string
     * @param string
     * @param quoteChar
     * @param useAsciiExcapes
     * @param sb a scratch buffer to use
     * @return
     */
    public static String unescape(CharSequence string, int quoteChar, boolean useAsciiEscapes, StringBuilder sb) {
        boolean escaped = false;
        
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (escaped) {
                switch (c) {
                case 'b':
                    sb.append('\b');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 'u':
                    i = parseNumericValue(string, sb, i, 0, 4, 4);
                    //TODO: this should probably be strict about needing 4 digits
                    break;
                default:
                    if (c == quoteChar) {
                        sb.append(quoteChar);
                    } else if (useAsciiEscapes) {
                        int value = Character.digit(c, 8);
                        if (value == -1) {
                            sb.append(c);
                        } else {
                            int possibleDigits = value < 3 ? 2:1;
                            int radixExp = 3;
                            i = parseNumericValue(string, sb, i, value, possibleDigits, radixExp);
                        }
                    }
                }
                escaped = false;
            } else {
                if (c == '\\') {
                    escaped = true;
                } else if (c == quoteChar) {
                    break;
                } else {
                    sb.append(c);
                }
            }
        }
        //TODO: should this be strict?
        //if (escaped) {
            //throw new FunctionExecutionException();
        //}
        return sb.toString();
    }
}
