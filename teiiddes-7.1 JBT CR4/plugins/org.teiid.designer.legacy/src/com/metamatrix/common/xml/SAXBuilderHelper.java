package com.metamatrix.common.xml;

import org.jdom.input.SAXBuilder;

/**
 * Utility class used to create a SAXBuilder
 */
public class SAXBuilderHelper {

	/** System property name used to get parser class */
    public static final String PARSER_PROPERTY_NAME = "metamatrix.xmlparser.class"; //$NON-NLS-1$
    
    private static String PARSER_NAME;

    public static String getParserClassName() {

        if (PARSER_NAME == null) {
            PARSER_NAME = System.getProperty(PARSER_PROPERTY_NAME);
        }       
        return PARSER_NAME;
    }

	/**
	 * Returns a SAXBuilder using the Parser class defined by the metamatrix.xmlparser.class
	 *         System property. If the System property does not exist, returns a SAXBuilder using
	 *          the org.apache.xerces.parsers.SAXParser.
	 * @param boolean validate
	 * @return org.jdom.input.SAXBuilder
	 */
	public static SAXBuilder createSAXBuilder() {
		return createSAXBuilder(false);
	}

	/**
	 * Returns a SAXBuilder using the Parser class defined by the metamatrix.xmlparser.class
	 *         System property. If the System property does not exist, returns a SAXBuilder using
	 *          the org.apache.xerces.parsers.SAXParser.
	 * @param boolean validate
	 * @return org.jdom.input.SAXBuilder
	 */
	public static SAXBuilder createSAXBuilder(boolean validate) {
		return new SAXBuilder(getParserClassName(), validate);
	}

	/**
	 * Returns a SAXBuilder
	 * @param boolean validate
	 * @return org.jdom.input.SAXBuilder
	 */
	public static SAXBuilder createSAXBuilder(String saxDriverClass, boolean validate) {
		return new SAXBuilder(saxDriverClass, validate);
	}

}
