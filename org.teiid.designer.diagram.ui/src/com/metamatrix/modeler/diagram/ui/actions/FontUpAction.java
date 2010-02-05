/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

class FontUpAction extends DiagramAction {
    private ScaledFont scaledFontManager;

    /**
     * Construct an instance of FontUpAction.
     */
    public FontUpAction( ScaledFont fontManager ) {
        super();
        this.scaledFontManager = fontManager;
    }

    @Override
    protected void doRun() {
        scaledFontManager.increase();
    }
}
