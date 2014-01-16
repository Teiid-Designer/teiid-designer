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
package org.teiid.runtime.client.util;

/**
 * This is a common place to put String utility methods.
 */
public final class StringUtil {

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
}
