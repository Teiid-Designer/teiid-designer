/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.sql;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * Colors used in the SQL editor
 */
public class ColorManager {

    // Editor Valid Background - white
    public static final RGB BACKGROUND_VALID = new RGB(255, 255, 255);
    public static final RGB BACKGROUND_FOCUSED = new RGB(255, 255, 255);
    public static final RGB BACKGROUND_UNFOCUSED = new RGB(230, 230, 230);

    public static final RGB NON_RECD_UNION_QUERY_FOCUSED = new RGB(255, 235, 235);
    public static final RGB NON_RECD_UNION_QUERY_UNFOCUSED = new RGB(255, 220, 220);
    // Editor Invalid Background - pink
    // Color.pink = 255,175,175
    // modTODO: leave invalid sql background white for now - making different
    // color presents problems to the scanner - need to set background on detected keywords,etc.
    //public static final RGB BACKGROUND_INVALID = new RGB(245, 215, 230);
    public static final RGB BACKGROUND_INVALID = new RGB(255, 255, 255);

	public static final RGB MULTI_LINE_COMMENT = new RGB(64, 128, 128);
	public static final RGB SINGLE_LINE_COMMENT = new RGB(64, 128, 128);
	
    // Default Text - Black
	public static final RGB DEFAULT = new RGB(0, 0, 0);
    // Keyword Text - Blue
	public static final RGB KEYWORD = new RGB(0, 0, 255);
    // Datatype Text - dk purple
    public static final RGB DATATYPE = new RGB(120, 0, 120);
    // Function names - dk purple
    // Defect 10803 - function name color black for now, until we figure out how to make it smarter
    //public static final RGB FUNCTION = new RGB(180, 0, 180);
    public static final RGB FUNCTION = new RGB(0, 0, 0);
    // String Text - lt purple
	public static final RGB STRING = new RGB(160, 0, 160);

	public static final RGB SQL_CODE_DEFAULT = new RGB(63, 95, 191);
	public static final RGB SQL_CODE_KEYWORD = new RGB(100, 100, 100);
	public static final RGB SQL_CODE_TAG = new RGB(127, 159, 191);

//	protected Map fColorTable = new HashMap(10);

	/**
	 * Method disposes of the colors.
	 */
	public void dispose() {
		// NO OP since now delegating to ModelerUiColorManager
	}
	/**
	 * A getter method that returns a color.
	 * @param rgb
	 * @return Color
	 */
	public Color getColor(RGB rgb) {
		return GlobalUiColorManager.getColor(rgb);
	}
}
