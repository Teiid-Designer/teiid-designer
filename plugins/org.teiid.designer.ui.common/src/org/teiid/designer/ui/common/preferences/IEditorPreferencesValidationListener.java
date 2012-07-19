/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.preferences;

/**
 * @author SDelap
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @since 8.0
 */
public interface IEditorPreferencesValidationListener {
    /**
     * This is called when validation has changed.
     * @return
     */    
    public void validationStatus(boolean status, String message);
}
