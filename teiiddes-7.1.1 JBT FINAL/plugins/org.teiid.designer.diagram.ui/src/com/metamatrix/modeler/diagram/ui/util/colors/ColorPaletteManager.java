/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util.colors;

import org.eclipse.emf.ecore.EObject;

/**
 * ColorPaletteManager
 */
public interface ColorPaletteManager {

    ColorPalette getColorPalette(EObject eObject);
	ColorPalette getColorPalette(Object eObject);
}
