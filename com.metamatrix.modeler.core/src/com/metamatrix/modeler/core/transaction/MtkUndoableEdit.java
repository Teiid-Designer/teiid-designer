/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.transaction;

import javax.swing.undo.UndoableEdit;

/**
 * @author Lance Phillips
 *
 * @since 3.1
 */
public interface MtkUndoableEdit extends UndoableEdit {
    
    /**
     * 
     * @return String - the name of the edit
     */
    String getName();
    
}
