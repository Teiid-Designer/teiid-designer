/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.xml;

import org.jdom.Verifier;

import org.teiid.core.util.StringUtil;

/**
 * Utility class for XML documents and related items.
 */
public class XmlUtil {
    
    /**
     * Constructor for XMLUtil.
     */
    private XmlUtil() {
        super();
    }
    
    /**
     * This method helps determine whether a string value (stored as an element or attribute value) are
     * legal characters as defined by XML 1.0.
     * <p>
     * XML has a slightly smaller set of legal scalar values than Unicode: 
     * <p>
     * </p>
     * <code>    #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]</code>
     * @param str the string to be checked
     * @return a <code>String</code> with the reason the string is invalid, or
     *         <code>null</code> if name is OK.
     */
    public static String containsValidCharacters( final String str ) {
        return Verifier.checkCharacterData(str);
    }
    
    public static String removeInvalidCharacters( final String str ) {
        if (str != null ) {
            final char[] orig = str.toCharArray();
            final int origLength = orig.length;
            final StringBuffer sb = new StringBuffer(str.length());
            for ( int i=0; i!=origLength; ++i ) {
                final char c = orig[i];
                if ( Verifier.isXMLCharacter(c) ) {
                    sb.append(c);
                }
            }
            return sb.toString(); 
        }
        return str;
    }
    
    /**
     * This method will escape any and all characters in the passed in String so that the String can
     * be used as character data in an XML Document without affecting the markup.
     * 
     *  Essentially the code does the following replacements:
     *  
     *  > -- &gt;
     *  < -- &lt;
     *  & -- &amp;
     *  " -- &quot;
     *  
     *  The returned String will be fully escaped.  If the passed in String is null, null will be returned from this method.
     *  
     * @param text The string to be escaped.
     * @return the escaped String.
     *
     */
    public static String escapeCharacterData(String text) {
        if(text == null) {
            return null;
        }
        
        /*
         * We must do this one first so as not to disturb the other &s in the escaped string.
         */
        String escaped = StringUtil.replace(text, "&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
        
        escaped = StringUtil.replace(escaped, ">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
        escaped = StringUtil.replace(escaped, "<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
        escaped = StringUtil.replace(escaped, "\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
        escaped = StringUtil.replace(escaped, "'", "&apos;"); //$NON-NLS-1$ //$NON-NLS-2$
        
        return escaped;
    }

}
