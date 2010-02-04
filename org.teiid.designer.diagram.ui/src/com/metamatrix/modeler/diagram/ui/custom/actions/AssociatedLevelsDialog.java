/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom.actions;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.ui.internal.widget.Dialog;

public class AssociatedLevelsDialog extends Dialog {

    //=============================================================
    // Instance variables
    //=============================================================
    private AssociatedLevelsPanel panel;
    private int levels = 0;

    //=============================================================
    // Constructors
    //=============================================================
    /**
     * AliasEntryDialog constructor.
     * 
     * @param parent
     *            parent of this dialog
     * @param transObj
     *            the transformation EObject
     * @param title
     *            dialog display title
     */
    public AssociatedLevelsDialog(Shell parent,
                                  String title) {
        super(parent, title);
    }

    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        panel = new AssociatedLevelsPanel(composite);
        return composite;
    }

    @Override
    protected void okPressed() {
        levels = panel.getLevels();
        super.okPressed();
    }

    /**
     * Get the alias name entry
     * 
     * @return the desired alias name
     */
    public int getLevels() {
        return levels;
    }

}
