/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;

/**
 * ScaledFontManager
 */
public class ScaledFontManager {
    
    private static final int DEFAULT_FONT_SIZE = 9;
    private static final int TITLE_FONT_SIZE = 14;
    private static final int MIN_FONT_SIZE = 4;
    private static final int MAX_FONT_SIZE = 40;
    private static boolean fontWasSet = false;
    
    private static int currentSize = DEFAULT_FONT_SIZE;
    private static String currentName = "Veranda";  //$NON-NLS-1$
    private static int currentStyle = ScaledFont.PLAIN_STYLE;
    
    private static Font currentFont = new Font(null, currentName, DEFAULT_FONT_SIZE, ScaledFont.PLAIN_STYLE);
	private static Font smallerFont = new Font(null, currentName, DEFAULT_FONT_SIZE-1, ScaledFont.PLAIN_STYLE);
    private static Font plainFont = new Font(null, currentName, DEFAULT_FONT_SIZE, ScaledFont.PLAIN_STYLE);
	private static Font boldFont = new Font(null, currentName, DEFAULT_FONT_SIZE, ScaledFont.BOLD_STYLE);
    private static Font titleFont = new Font(null, currentName, TITLE_FONT_SIZE, ScaledFont.BOLD_STYLE);
	private static Font italicsFont = new Font(null, currentName, DEFAULT_FONT_SIZE, ScaledFont.ITALICS_STYLE);
	private static Font boldItalicsFont = new Font(null, currentName, DEFAULT_FONT_SIZE, ScaledFont.BOLD_ITALICS_STYLE);
	
    /**
     * Construct an instance of ScaledFontManager.
     * 
     */
    public ScaledFontManager() {
        super();
        setFontFromPreferences();
    }
    
    private void setFontFromPreferences() {
    	if( !fontWasSet) {
	        IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();
	        FontData fontData = PreferenceConverter.getFontData(preferenceStore,
	                PluginConstants.Prefs.Appearance.FONT);
	        currentName = fontData.getName();
	        currentSize = fontData.getHeight();
	        currentStyle = fontData.getStyle();
			currentFont = new Font(null, currentName, currentSize, currentStyle);
			fontWasSet = true;
			
			resetSecondaryFonts();
		}
    }
    
	private static void saveFontPreference() {
		PreferenceConverter.setValue(DiagramUiPlugin.getDefault().getPreferenceStore(), 
						PluginConstants.Prefs.Appearance.FONT, getFont().getFontData()[0]);
		DiagramUiPlugin.getDefault().savePluginPreferences();
	}
    
    public static void increase() {
        int currSize = getSize();
        if (currSize < MAX_FONT_SIZE) {
            setSize(currSize + 1);
			saveFontPreference();
        }
    }

    public static void decrease() {
        int currSize = getSize();
        if (currSize > MIN_FONT_SIZE) {
            setSize(currSize - 1);
			saveFontPreference();
        }
    }

    public static boolean canIncrease() {
        int currSize = getSize();
        if (currSize < MAX_FONT_SIZE) {
            return true;
        }
        return false;
    }


    public static boolean canIncrease( int iSize ) {

        if (iSize < MAX_FONT_SIZE)
            return true;
        return false;
    }


    public static boolean canDecrease() {
        int currSize = getSize();
        if (currSize  > MIN_FONT_SIZE)
            return true;
        return false;
    }


    public static boolean canDecrease( int iSize ) {
 
        if (iSize > MIN_FONT_SIZE)
            return true;
        return false;
    }

    public static Font getFont() {
        return currentFont;
    }
    
	public static Font getFont(int style) {
		Font someFont = plainFont;
		switch(style) {
			case ScaledFont.BOLD_ITALICS_STYLE: {
				someFont = boldItalicsFont;
			} break;
			
			case ScaledFont.ITALICS_STYLE: {
				someFont = italicsFont;
			} break;
			
			case ScaledFont.BOLD_STYLE: {
				someFont = boldFont;
			} break;
			
			case ScaledFont.PLAIN_STYLE: {
				someFont = plainFont;
			} break;
			
			case ScaledFont.SMALLER_PLAIN_STYLE: {
				someFont = smallerFont;
			} break;
            
            case ScaledFont.TITLE_STYLE: {
                someFont = titleFont;
            } break;
            
			default:
			break;
		}
		return someFont;
	}

    public static String getName() {
        return currentName;
    }

    public static int getSize() {
        return currentSize;
    }

    public static int getStyle() {
        return currentStyle;
    }
    
//    private static void disposeFonts(boolean allFonts) {
//    	if( currentFont != null && allFonts )
//    		currentFont.dispose();
//    		
//
//		if( italicsFont != null )
//			italicsFont.dispose();
//		if( boldFont != null )
//			boldFont.dispose();
//		if( boldItalicsFont != null )
//			boldItalicsFont.dispose();
//			
//		if( smallerFont != null )
//			smallerFont.dispose();
//    }

    public static void setFont(String typeName, int size, int style) {
    	// Only set new font if it changed.
		if( currentSize != size ||
			!currentName.equalsIgnoreCase(typeName) ||
			currentStyle != style ) {
	    	currentName = typeName;
	    	currentSize = size;
	    	currentStyle = style;
	    	
	    	currentFont.dispose();
	    	
	    	currentFont = new Font(null, currentName, getSize(), getStyle());
	        
			resetSecondaryFonts();
		}
    }
    
    private static void resetSecondaryFonts() {
    	// Only reset these fonts if they change...
		if( plainFont != null ) {
			if(  fontIsChanged(plainFont, currentName, currentSize, ScaledFont.PLAIN_STYLE) ) {
				plainFont.dispose();
				plainFont = new Font(null, currentName, currentSize, ScaledFont.PLAIN_STYLE);
			}
		} else {
			plainFont = new Font(null, currentName, currentSize, ScaledFont.PLAIN_STYLE);
		}
		
		if( boldFont != null ) {
			if( fontIsChanged(boldFont, currentName, currentSize, ScaledFont.BOLD_STYLE) ) {
				boldFont.dispose();
				boldFont = new Font(null, currentName, currentSize, ScaledFont.BOLD_STYLE);
			}
		} else {
			boldFont = new Font(null, currentName, currentSize, ScaledFont.BOLD_STYLE);
		}
		
		if( italicsFont != null ) {
			if( fontIsChanged(italicsFont, currentName, currentSize, ScaledFont.ITALICS_STYLE) ) {
				italicsFont.dispose();
				italicsFont = new Font(null, currentName, currentSize, ScaledFont.ITALICS_STYLE);
			}
		} else {
			italicsFont = new Font(null, currentName, currentSize, ScaledFont.ITALICS_STYLE);
		}
		
		if( boldItalicsFont != null ) {
			if( fontIsChanged(boldItalicsFont, currentName, currentSize, ScaledFont.BOLD_ITALICS_STYLE) ) {
				boldItalicsFont.dispose();
				boldItalicsFont = new Font(null, currentName, currentSize, ScaledFont.BOLD_ITALICS_STYLE);
			}
		} else {
			boldItalicsFont = new Font(null, currentName, currentSize, ScaledFont.BOLD_ITALICS_STYLE);
		}
		
		if( smallerFont != null ) {
			if( fontIsChanged(smallerFont, currentName, currentSize-1, ScaledFont.PLAIN_STYLE) ) {
				smallerFont.dispose();
				smallerFont = new Font(null, currentName, currentSize-1, ScaledFont.PLAIN_STYLE);
			}
		} else {
			smallerFont = new Font(null, currentName, currentSize-1, ScaledFont.PLAIN_STYLE);
		}
    }
    
    private static boolean fontIsChanged(final Font oldFont, String newName, int newHeight, int newStyle ) {
		FontData data = oldFont.getFontData()[0];
		if( data.getHeight() != newHeight ||
			data.getStyle() != newStyle ||
			!data.getName().equalsIgnoreCase(newName) )
			return true;
			
		return false;
    }
    
    public static boolean fontsAreDifferent(final Font font1, final Font font2 ) {
    	if( font2.isDisposed() )
    		return true;
    	
		FontData data1 = font1.getFontData()[0];
		FontData data2 = font2.getFontData()[0];
		if( data1.getHeight() != data2.getHeight() ||
			data1.getStyle() != data2.getStyle() ||
			!data1.getName().equalsIgnoreCase(data2.getName()) )
			return true;
		
		return false;
    }

    public static void setFont(Font newFont) {
		currentFont.dispose();
		
		currentFont = newFont;
		FontData data = currentFont.getFontData()[0];
		currentName = data.getName();
		currentSize = data.getHeight();
		currentStyle = data.getStyle();
		
		resetSecondaryFonts();
    }

    public static void setName(String newName) {
        currentName = newName;
    }

    public static void setStyle(int newStyle) {
        currentStyle = newStyle;
        resetFont();
    }

    public static void setSize(int newSize) {
        currentSize = newSize;
        resetFont();
    }
    
    public static void resetFont() {
        setFont( new Font(null, getName(), getSize(), getStyle()) );
    }
}
