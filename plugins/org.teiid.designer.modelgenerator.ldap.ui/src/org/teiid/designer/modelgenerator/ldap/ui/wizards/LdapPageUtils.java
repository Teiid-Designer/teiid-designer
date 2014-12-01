/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.ldap.ui.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Random utility methods for use with LDAP Definition Wizard pages
 */
public class LdapPageUtils {

    /**
     * Set the background colour of the control to the same as the base
     *
     * @param control
     * @param base
     */
    public static void setBackground(Control control, Composite base) {
        if (control == null || base == null)
            return;

        control.setBackground(base.getBackground());
    }

    /**
     * Set the background colour of the control to grey
     *
     * @param control
     */
    public static void greyBackground(Control control) {
        if (control == null)
            return;

        control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
    }

    /**
     * Set the foreground colour of the control to blue
     *
     * @param control
     */
    public static void blueForeground(Control control) {
        if (control == null)
            return;

        control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
    }

}
