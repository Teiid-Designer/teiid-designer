/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class FormUtil {

    //
    // Constants:
    //
    public static final String HTML_BEGIN = "<form><p>"; //$NON-NLS-1$
    public static final String HTML_END = "</p></form>"; //$NON-NLS-1$
    public static final String HTML_NEWLINE = "<br/>"; //$NON-NLS-1$

    //
    // Utility Methods:
    //
    public static boolean safeEquals(Object leftVal, Object rightVal) {
        if (leftVal == null) {
            return rightVal == null;
        } // endif
    
        return leftVal.equals(rightVal);
    }

    public static boolean safeEquals(String leftVal, String rightVal, boolean treatNullAsEmpty) {
        if (leftVal == null) {
            // left was null; right can be either null or (when treatNullAsEmpty) empty
            return rightVal == null
                || (treatNullAsEmpty && rightVal.length() == 0);
        } // endif
    
        if (leftVal.length() == 0) {
            // left was empty; right can be either empty or (when treatNullAsEmpty) null
            if (rightVal == null) {
                return treatNullAsEmpty;
            } // endif
    
            // right not null 
            return rightVal.length() == 0;
        } // endif
    
        // left not null or empty, so right can't be either; just eq:
        return leftVal.equals(rightVal);
    }

    public static ScrolledForm getScrolledForm(Control c) {
        Composite parent = c.getParent();
        
        while (parent != null) {
            if (parent instanceof ScrolledForm) {
                return (ScrolledForm) parent;
            } // endif
            parent = parent.getParent();
        } // endwhile
    
        // no scrolled form in hierarchy, return null:
        return null;
    }

    public static void tweakColors(FormToolkit ftk, Display display) {
        ftk.refreshHyperlinkColors();
        HyperlinkGroup hlg = ftk.getHyperlinkGroup();
        if (hlg.getActiveForeground() == null) {
            hlg.setActiveForeground(display.getSystemColor(SWT.COLOR_RED));
            hlg.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
        } // endif
    }

}
