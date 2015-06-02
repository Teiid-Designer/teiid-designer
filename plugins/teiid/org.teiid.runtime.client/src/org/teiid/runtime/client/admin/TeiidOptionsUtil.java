/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeiidOptionsUtil {

    private static final String PREFIX = "OPTIONS (";
    public static final String UUID = "UUID";
    private static final char S_QUOTE = '\'';
    private static final char D_QUOTE = '"';
    private static final List< Character > INVALID_ID_CHARS = Arrays.asList( new Character[] {'/', ':', '[', ']', '|', '*'} ); // from JCR spec

    private static boolean isValidIdentifierCharacter( final char c ) {
        return !INVALID_ID_CHARS.contains( c );
    }
    
    public static String filterUuidsFromOptions(String ddl) throws Exception {
    	// find first index
    	StringBuilder sb = new StringBuilder();
    	int indexOfOptions = ddl.indexOf(PREFIX);
    	String remainingDdl = ddl;
    	while (indexOfOptions > -1 ) {
    		// Add everything prior to "OPTIONS ("
    		sb.append(remainingDdl.substring(0, indexOfOptions));
    		remainingDdl = remainingDdl.substring(indexOfOptions);
    		// Now parse all Options
    		Map< String, String > options = new HashMap<String, String>();
    		remainingDdl = parse(remainingDdl, options, true);
    		
    		String filteredOptions = getOptionsClause(options, true);
    		if( filteredOptions != null ) {
    			sb.append(filteredOptions);
    		}
    		indexOfOptions = remainingDdl.indexOf(PREFIX);
    	}
    	
    	sb.append(remainingDdl);
    	
    	return sb.toString();
    }
    
    private static String getOptionsClause(Map<String, String> options, boolean removeUuid) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(PREFIX);
    	int nValues = options.size();
    	int count = 0;
    	boolean hasOptions = false;
    	for( String key : options.keySet() ) {
    		// Skip UUID property
    		if( key.equalsIgnoreCase(UUID) ) continue;
    		
    		sb.append(key).append(" ").append(options.get(key));
    		hasOptions = true;
    		count++;
    		
    		if( count < nValues-1 ) sb.append(", ");
    	}
    	
    	if( hasOptions ) return sb.toString();
    	
    	return null;
    }
    
    
    protected static String parse( final String inputString,
            final Map< String, String > options ) throws Exception {
 	   return parse( inputString, options, false);
    }

    /**
     * @param inputString
     *        the text being parsed (cannot be empty and must start with <code>OPTIONS (</code>
     * @param options
     *        an empty map where the option/value pairs will be returned
     * @return the remainder of the input string that is not part of the OPTIONS clause (can be empty)
     * @throws Exception
     *         if an error occurs
     */
    private static String parse( final String inputString,
                                 final Map< String, String > options,
                                 boolean keepQuotes ) throws Exception {
        if (( inputString == null ) || !inputString.startsWith( PREFIX )) {
            throw new Exception( "OPTIONS clause must start with " + PREFIX );
        }

        if (( options == null ) || !options.isEmpty()) {
            throw new Exception( "Options map is null or not empty" );
        }

        String text = inputString.trim();
        text = inputString.substring( PREFIX.length() );

        if (text.isEmpty()) {
            throw new Exception( "OPTIONS clause did not have closing paren" );
        }

        String[] optionResult = null;
        String[] valueResult = null;
        boolean keepGoing = true;
        int i = -1;

        do {
            i = -1;
            optionResult = parseIdentifier( text, keepQuotes );
            valueResult = parseValue( optionResult[1], keepQuotes );
            options.put( optionResult[0], valueResult[0] );
            text = valueResult[1];

            // remove whitespace and a comma in order to get to next option
            // options must be separated by a comma
            // end if find end of OPTIONS which is a right paren
            for (final char c : text.toCharArray()) {
                ++i;

                if (c == ')') {
                    keepGoing = false;
                    break;
                }

                if (c == ',') {
                    break;
                }

                if (!Character.isWhitespace( c )) {
                    throw new Exception( "OPTIONS clause need commas separating options" );
                }
            }

            if (i == text.length() - 1) {
                keepGoing = false;
            } else {
                text = text.substring( i + 1 );
            }
        } while (keepGoing);

        // remove the ending right paren
        if (text.charAt( i ) != ')') {
            throw new Exception( "OPTIONS clause does not have ending right paren" );
        }

        return text.substring( ++i );
    }
    

    protected static String[] parseIdentifier( final String inputString ) throws Exception {
 	   return parseIdentifier(inputString, false);
    }

    private static String[] parseIdentifier( final String inputString, boolean keepQuotes ) throws Exception {
        if (( inputString == null ) || inputString.trim().isEmpty()) {
            throw new Exception( "Cannot parse identifier when input string is empty" );
        }

        String remainder = inputString.trim();
        String id = StringConstants.EMPTY_STRING;

        boolean foundBeginningSingleQuote = false;
        boolean foundEndingSingleQuote = false;

        boolean foundBeginningDoubleQuote = false;
        boolean foundEndingDoubleQuote = false;

        int i = 0;

        PARSING: for (final char c : remainder.toCharArray()) {
            if (Character.isWhitespace( c )) {
                if (!id.isEmpty()) {
                    ++i;
                    break PARSING; // done parsing found identifier
                }
            } else if (!isValidIdentifierCharacter( c )) {
                throw new Exception( "Option identifier has invalid character: " + c );
            }

            switch (c) {
                case S_QUOTE:
                    if (foundBeginningSingleQuote) {
                        foundEndingSingleQuote = true;
                        id += c;
                        ++i;
                        break PARSING; // done parsing found single quoted identifier
                    }

                    if (id.isEmpty() && !foundBeginningSingleQuote) {
                        foundBeginningSingleQuote = true;
                        id += c;
                        break;
                    }

                    throw new Exception( "Found single quote embedded in identifier" );
                case D_QUOTE:
                    if (foundBeginningDoubleQuote) {
                        foundEndingDoubleQuote = true;
                        id += c;
                        ++i;
                        break PARSING; // done parsing found double quoted identifier
                    }

                    if (id.isEmpty() && !foundBeginningDoubleQuote) {
                        foundBeginningDoubleQuote = true;
                        id += c;
                        break;
                    }

                    throw new Exception( "Found double quote embedded in identifier" );
                default:
                    id += c;
                    break;
            }

            ++i;
        }

        if (id.isEmpty()) {
            throw new Exception( "No identifier found" );
        }

        if (foundBeginningSingleQuote && !foundEndingSingleQuote) {
            throw new Exception( "Identifier does not have an ending single quote" );
        }

        if (foundBeginningDoubleQuote && !foundEndingDoubleQuote) {
            throw new Exception( "Identifier does not have an ending double quote" );
        }
        
        if( keepQuotes ) {
     	   if( foundBeginningSingleQuote && foundEndingSingleQuote ) {
     		   return new String[] { S_QUOTE + id + S_QUOTE, remainder.substring( i )};
     	   } else if( foundBeginningDoubleQuote && foundEndingDoubleQuote ) {
     		   return new String[] { D_QUOTE + id + D_QUOTE, remainder.substring( i )};
     	   }
        }
        
        return new String[] {id, remainder.substring( i )};
    }
    

    private static String[] parseValue( final String inputString ) throws Exception {
 	   return parseValue( inputString, false );
    }

    private static String[] parseValue( final String inputString, boolean keepQuotes ) throws Exception {
        if (( inputString == null ) || inputString.isEmpty()) {
            throw new Exception( "Options value is null or empty" );
        }
        boolean foundSingleFirst = false;
        boolean foundDoubleFirst = false;
        
        // find first non-whitespace character
        int startIndex = 0;

        for (final char c : inputString.toCharArray()) {
            if (Character.isWhitespace( c )) {
                ++startIndex;
                continue;
            }

            break;
        }

        // value will either be single quoted or not have quotes
        if (inputString.charAt( startIndex ) == '\'') {
            String value = inputString.substring( startIndex ); // now find ending single quote

            // walk through string to figure out where value ends
            boolean foundDelimiter = false;
            int numSingle = 0;
            int numDouble = 0;
            int i = 0;

            for (final char c : value.toCharArray()) {
                // only look at quoting
                if (c == S_QUOTE) {
                    ++numSingle;
                    if( foundDoubleFirst == false && foundSingleFirst == false ) {
                 	   foundSingleFirst = true;
                    }

                    // value can only end in a single quote
                    // make sure double quotes are matched
                    // look at next char to see if end of value
                    if (( value.length() - 1 ) == i && ( i != 0 )) {
                        ++i;
                        break; // single quote is last character
                    }

                    final char nextChar = value.charAt( i + 1 );

                    if (Character.isWhitespace( nextChar ) || ( nextChar == ',' ) || ( nextChar == ')' )) {
                        if (( ( numSingle % 2 ) == 0 ) && ( ( numDouble % 2 ) == 0 )) {
                            ++i;
                            foundDelimiter = true;
                            break; // have even number single and double quotes so have value
                        }

                        // else keep looking for ending single quote
                    }
                } else if (c == D_QUOTE) {
                    ++numDouble;
                    if( foundDoubleFirst == false && foundSingleFirst == false ) {
                 	   foundDoubleFirst = true;
                    }
                }

                ++i;
            }

            int endIndex = ( i + startIndex );
            String leftOver = null;

            if (foundDelimiter) {
                leftOver = inputString.substring( endIndex );
            } else {
                leftOver = StringConstants.EMPTY_STRING;
            }

            return new String[] {inputString.substring( startIndex, endIndex ), leftOver};
        } else { // unquoted value
            boolean foundDelimiter = false;
            int endIndex = 0;

            for (int i = startIndex; i < inputString.length(); ++i) {
                char c = inputString.charAt( i );

                // find delimiter
                if (Character.isWhitespace( c ) || ( c == ',' ) || ( c == ')' )) {
                    endIndex = i;
                    foundDelimiter = true;
                    break; // found unquoted value
                }

                if (( c == '\'' ) || ( c == '"' )) {
                    throw new Exception( "Unquoted values cannot have quotes" );
                }

                endIndex = i;
            }

            String leftOver = null;

            if (foundDelimiter) {
                leftOver = inputString.substring( endIndex );
            } else {
                leftOver = StringConstants.EMPTY_STRING;
                ++endIndex;
            }
            
            if( keepQuotes ) {
         	   if( foundSingleFirst ) {
         		   return new String[] {S_QUOTE + inputString.substring( startIndex, endIndex ) + S_QUOTE, leftOver};
         	   }
         	   
         	   if( foundDoubleFirst ) {
         		   return new String[] {D_QUOTE + inputString.substring( startIndex, endIndex ) + D_QUOTE, leftOver};
         	   }
            }
            
            return new String[] {inputString.substring( startIndex, endIndex ), leftOver};
        }
    }

}
