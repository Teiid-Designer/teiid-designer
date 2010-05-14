/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.util.BitSet;

/**
 * General purpose escaping and un-escaping utility methods. For "character encoding", The whole escaped characters must be done.
 * It's different between "character encoding" and "escaping of characters". NOTICE: These methods encode all characters,
 * including the ":", "/" and "." characters. So in order to escape a complete URI, the individual segments of the URI should be
 * escaped separately.
 * 
 * @see <a href=http://www.ietf.org/rfc/rfc2396.txt?number=2396>RFC 2396</a>
 */

public class UriUtil {

    /**
     * <p>
     * Prevents instantiation.
     * </p>
     * 
     * @since 4.0
     */
    private UriUtil() {
    }

    /**
     * Array containing the ASCII expression for hexadecimal.
     */
    private static final char[] hexadecimal = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Array containing the alphanum URI character set.
     */
    private static BitSet alphanum;

    /**
     * Array containing the reserved URI character set of the scheme part.
     */
    private static BitSet schemeReserved;

    /**
     * Array containing the reserved URI character set of the authority part.
     */
    private static BitSet authorityReserved;

    /**
     * Array containing the reserved URI character set of the userinfo part.
     */
    private static BitSet userinfoReserved;

    /**
     * Array containing the reserved URI character set of the host part.
     */
    private static BitSet hostReserved;

    /**
     * Array containing the reserved URI character set of the path part.
     */
    private static BitSet pathReserved;

    /**
     * Array containing the reserved URI character set of the query.
     */
    private static BitSet queryReserved;

    static {

        // Save the alphanum URI characters that is common to do URI escaping.
        alphanum = new BitSet(128);
        for (int i = 'a'; i <= 'z'; i++) {
            alphanum.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            alphanum.set(i);
        }
        for (int i = '0'; i <= '9'; i++) {
            alphanum.set(i);
        }

        // Save the reserved URI characters within the sheme component.
        schemeReserved = new BitSet(128);
        /**
         * Actually, this should be any combination of lower case letters, digits, plus ("+"), period ("."), or hyphen ("-"). The
         * upper case letters should be treated as equivalent to lower case in scheme names.
         */
        schemeReserved.set('+');
        schemeReserved.set('.');
        schemeReserved.set('-');

        // Save the reserved URI characters within the authority component.
        authorityReserved = new BitSet(128);
        authorityReserved.set(';');
        authorityReserved.set(':');
        authorityReserved.set('@');
        authorityReserved.set('?');
        authorityReserved.set('/');

        // Save the reserved URI characters within the userinfo component.
        userinfoReserved = new BitSet(128);
        userinfoReserved.set(';');
        userinfoReserved.set(':');
        userinfoReserved.set('&');
        userinfoReserved.set('=');
        userinfoReserved.set('+');
        userinfoReserved.set('$');
        userinfoReserved.set(',');

        // Save the reserved URI characters within the host component.
        hostReserved = new BitSet(128);
        hostReserved.set('.');
        hostReserved.set('-');

        // Save the reserved URI characters within the path component.
        pathReserved = new BitSet(128);
        pathReserved.set('/');
        pathReserved.set(';');
        pathReserved.set('=');
        pathReserved.set('?');

        // Save the reserved URI characters within the query component.
        queryReserved = new BitSet(128);
        queryReserved.set(';');
        queryReserved.set('/');
        queryReserved.set('?');
        queryReserved.set(':');
        queryReserved.set('@');
        queryReserved.set('&');
        queryReserved.set('=');
        queryReserved.set('+');
        queryReserved.set(',');
        queryReserved.set('$');

    }

    /**
     * Convert a byte character value to hexadecimal digit value.
     * 
     * @param b the character value byte
     */
    private static synchronized byte convertHexDigit( byte b ) {
        if ((b >= '0') && (b <= '9')) return (byte)(b - '0');
        if ((b >= 'a') && (b <= 'f')) return (byte)(b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F')) return (byte)(b - 'A' + 10);
        return 0;
    }

    /**
     * Unescape the escaped URI string.
     * 
     * @param str The URI-escaped string.
     * @exception IllegalArgumentException if a '%' character is not followed by a valid 2-digit hexadecimal number
     */
    public static String unescape( String str ) {
        return (str == null) ? null : unescape(str.getBytes());
    }

    /**
     * Unescape the escaped URI string.
     * 
     * @param bytes The URI-escaped byte array.
     * @exception IllegalArgumentException if a '%' character is not followed by a valid 2-digit hexadecimal number
     */
    public static synchronized String unescape( byte[] bytes ) {

        if (bytes == null) return (null);

        int len = bytes.length;
        int ix = 0;
        int ox = 0;
        while (ix < len) {
            byte b = bytes[ix++]; // Get byte to test
            if (b == '+') {
                b = (byte)' ';
            } else if (b == '%') {
                b = (byte)((convertHexDigit(bytes[ix++]) << 4) + convertHexDigit(bytes[ix++]));
            }
            bytes[ox++] = b;
        }

        return new String(bytes, 0, ox);
    }

    /**
     * Escape the unescaped URI string.
     * 
     * @param str The unescaped URI string which has to be rewritten.
     */
    public static String escape( String str ) {
        return escape(str, null);
    }

    /**
     * Escape the unescaped URI string.
     * 
     * @param str The unescaped URI string which has to be rewritten.
     * @param reserved The additional reserved URI character set.
     */
    public static String escape( String str,
                                 BitSet reserved ) {
        return (str == null) ? null : escape(str.getBytes(), reserved);
    }

    /**
     * Escape the unescaped URI byte array.
     * 
     * @param bytes The unescaped URI byte array which has to be rewritten.
     * @param reserved The additional reserved URI character set.
     */
    public static synchronized String escape( byte[] bytes,
                                              BitSet reserved ) {

        if (bytes == null) return (null);

        StringBuffer rewrittenStr = new StringBuffer(bytes.length);

        for (int i = 0; i < bytes.length; i++) {
            char c = (char)bytes[i];
            if (alphanum.get(c)) {
                rewrittenStr.append(c);
            } else if (reserved != null && reserved.get(c)) {
                rewrittenStr.append(c);
            } else {
                byte toEscape = bytes[i];
                rewrittenStr.append('%');
                int low = (toEscape & 0x0f);
                int high = ((toEscape & 0xf0) >> 4);
                rewrittenStr.append(hexadecimal[high]);
                rewrittenStr.append(hexadecimal[low]);
            }
        }

        return rewrittenStr.toString();
    }
}
