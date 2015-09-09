/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership. Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.teiid.runtime.client.admin;

/**
 *
 *
 *
 */
public interface StringConstants {

    /**
     * An empty string array.
     */
    String[] EMPTY_ARRAY = new String[0];

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
     * An underscore character.
     */
    char UNDERSCORE_CHAR = UNDERSCORE.charAt(0);

    /**
     * The String "\n"
     */
    String NEW_LINE = "\n"; //$NON-NLS-1$

    /**
     * The String "\t"
     */
    String TAB = "\t"; //$NON-NLS-1$

    /**
     * AT sign.
     */
    String AT= "@"; //$NON-NLS-1$

    /**
     * A Comma.
     */
    String COMMA = ","; //$NON-NLS-1$

    /**
     * A Colon.
     */
    String COLON = ":"; //$NON-NLS-1$

    /**
     * A Semi Colon.
     */
    String SEMI_COLON = ";"; //$NON-NLS-1$

    /**
     * A Hyphen.
     */
    String HYPHEN = "-"; //$NON-NLS-1$

    /**
     * A Dot.
     */
    String DOT = "."; //$NON-NLS-1$

    /**
     * A dot character.
     */
    char DOT_CHAR = DOT.charAt(0);

    /**
     * A Speech mark.
     */
    String SPEECH_MARK = "\""; //$NON-NLS-1$

    /**
     * A Quote mark.
     */
    String QUOTE_MARK = "'"; //$NON-NLS-1$

    /**
     * A Question mark.
     */
    String QUESTION_MARK = "?"; //$NON-NLS-1$

    /**
     * Two Dots
     */
    String DOT_DOT = ".."; //$NON-NLS-1$

    /**
     * class
     */
    String CLASS = "class"; //$NON-NLS-1$

    /**
     * interface
     */
    String INTERFACE = "interface"; //$NON-NLS-1$

    /**
     * enum
     */
    String ENUM = "enum"; //$NON-NLS-1$

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

    /**
     * Back slash used in regular expressions
     */
    String DOUBLE_BACK_SLASH = "\\"; //$NON-NLS-1$

    /**
     * Equals
     */
    String EQUALS = "="; //$NON-NLS-1$

    /**
     * Open Bracket
     */
    String OPEN_BRACKET = "("; //$NON-NLS-1$

    /**
     * Close Bracket
     */
    String CLOSE_BRACKET = ")"; //$NON-NLS-1$

    /**
     * Hash Symbol
     */
    String HASH = "#"; //$NON-NLS-1$

    /**
     * Ampersand Symbol
     */
    String AMPERSAND = "@"; //$NON-NLS-1$

    /**
     * Open Angle Bracket
     */
	String OPEN_ANGLE_BRACKET = "<"; //$NON-NLS-1$

	/**
	 * Close Angle Bracket
	 */
	String CLOSE_ANGLE_BRACKET = ">"; //$NON-NLS-1$

    /**
     * Open Square Bracket
     */
    String OPEN_SQUARE_BRACKET = "["; //$NON-NLS-1$

    /**
     * Close Square Bracket
     */
    String CLOSE_SQUARE_BRACKET = "]"; //$NON-NLS-1$

    /**
     * Open Brace
     */
    String OPEN_BRACE = "{"; //$NON-NLS-1$

    /**
     * Close Brace
     */
    String CLOSE_BRACE = "}"; //$NON-NLS-1$

    /**
     * Minus Sign
     */
    String MINUS = "-"; //$NON-NLS-1$

    /**
     * Plus Sign
     */
    String PLUS = "+"; //$NON-NLS-1$

    /**
     * Multiple Sign
     */
    String MULTIPLY = STAR;

    /**
     * Divide Sign
     */
    String DIVIDE = FORWARD_SLASH;

    /**
     * Pipe Sign
     */
    String PIPE = "|"; //$NON-NLS-1$

    /**
     * The String "\n"
     */
    String CLOSE_ELEMENT = "/>"; //$NON-NLS-1$

    /**
     * log
     */
    String LOG = "log"; //$NON-NLS-1$

	/**
	 * Current Folder Symbol
	 */
	String CURRENT_FOLDER_SYMBOL = "."; //$NON-NLS-1$

	/**
	 * Parent Folder Symbol
	 */
	String PARENT_FOLDER_SYMBOL = ".."; //$NON-NLS-1$

	/**
	 * Drive Separator
	 */
	String DRIVE_SEPARATOR = ":"; //$NON-NLS-1$

	/**
	 * File Extension separator
	 */
	String FILE_EXTENSION_SEPARATOR = "."; //$NON-NLS-1$

	/**
	 * File name wildcard
	 */
	String FILE_NAME_WILDCARD = "*"; //$NON-NLS-1$

  /**
   * Target directory
   */
  String TARGET = "target"; //$NON-NLS-1$

  /**
   * sources jar component
   */
  String SOURCES = "sources"; //$NON-NLS-1$

  /**
   * JAR File Extension
   */
  String JAR = "jar"; //$NON-NLS-1$

  /**
   * JAVA File Extension
   */
  String JAVA = "java"; //$NON-NLS-1$

  /**
   * DDL File Extension
   */
  String FILE_DDL = "ddl"; //$NON-NLS-1$

  /**
   * src directory
   */
  String SRC = "src"; //$NON-NLS-1$

  /**
   * public
   */
  String PUBLIC = "public"; //$NON-NLS-1$

  /**
   * static
   */
  String STATIC = "static"; //$NON-NLS-1$

  /**
   * final
   */
  String FINAL = "final"; //$NON-NLS-1$

  /**
   * private
   */
  String PRIVATE = "private"; //$NON-NLS-1$
}

