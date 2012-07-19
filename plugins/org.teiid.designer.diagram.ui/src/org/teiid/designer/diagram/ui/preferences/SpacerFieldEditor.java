/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.preferences;

import org.eclipse.swt.widgets.Composite;

/**
 * A field editor for adding space to a preference page.
 *
 * @since 8.0
 */
public class SpacerFieldEditor extends LabelFieldEditor {
    // Implemented as an empty label field editor.
    public SpacerFieldEditor(Composite parent) {
        super("", parent); //$NON-NLS-1$
    }
}
