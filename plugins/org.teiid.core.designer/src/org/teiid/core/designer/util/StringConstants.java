/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.util;

/**
 * 
 *
 * @since 8.0
 */
public interface StringConstants {

    /**
     * An empty string
     */
    String EMPTY_STRING = ""; //$NON-NLS-1$

    /**
     * A space.
     */
    String SPACE = " "; //$NON-NLS-1$

    /**
     * A star.
     */
    String STAR = "*"; //$NON-NLS-1$

    /**
     * An underscore.
     */
    String UNDERSCORE = "_"; //$NON-NLS-1$

    /**
     * The String "\n"
     */
    String NEW_LINE = "\n"; //$NON-NLS-1$
    
    /**
     * A Comma.
     */
    String COMMA = ","; //$NON-NLS-1$

    /**
     * A Dot.
     */
    String DOT = "."; //$NON-NLS-1$

    /**
     * class extension
     */
    String CLASS = "class"; //$NON-NLS-1$

    /**
     * xml extension
     */
    String XML = "xml"; //$NON-NLS-1$

    /**
     * The name of the System property that specifies the string that should be used to separate lines. This property is a standard
     * environment property that is usually set automatically.
     */
    String LINE_SEPARATOR_PROPERTY_NAME = "line.separator"; //$NON-NLS-1$

    /**
     * The String that should be used to separate lines; defaults to {@link #NEW_LINE}
     */
    String LINE_SEPARATOR = System.getProperty(LINE_SEPARATOR_PROPERTY_NAME, NEW_LINE);

    /**
     * Forward slash
     */
    String FORWARD_SLASH = "/"; //$NON-NLS-1$

}
