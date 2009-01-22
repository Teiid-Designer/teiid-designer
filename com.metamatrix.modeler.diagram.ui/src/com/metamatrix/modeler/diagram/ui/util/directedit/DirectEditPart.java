/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util.directedit;

import com.metamatrix.modeler.diagram.ui.part.EditableEditPart;

/**
 * DirectEditPart
 */
public interface DirectEditPart extends EditableEditPart {
    String getText();
    
    void setText(String newText);
    
    String getEditString();
    
    void performDirectEdit();
    
    DirectEditPartManager getEditManager();
}
