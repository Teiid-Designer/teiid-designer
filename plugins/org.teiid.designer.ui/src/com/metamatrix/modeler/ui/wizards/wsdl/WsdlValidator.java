/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards.wsdl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/** Validator that makes sure the selection contains all WSDL files. */
public class WsdlValidator implements ISelectionStatusValidator, UiConstants {

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil
            .getPropertyPrefix(WsdlValidator.class);
    
    @Override
    public IStatus validate(Object[] theSelection) {
        IStatus result = null;
        boolean valid = true;

        if ((theSelection != null) && (theSelection.length > 0)) {
            for (int i = 0; i < theSelection.length; i++) {
                if ((!(theSelection[i] instanceof IFile))
                        || !WebServicePlugin.isWsdlFile((IFile) theSelection[i])) {
                    valid = false;
                    break;
                }
            }
        } else {
            valid = false;
        }

        if (valid) {
            result = new StatusInfo(PLUGIN_ID);
        } else {
            result = new StatusInfo(PLUGIN_ID, IStatus.ERROR, Util.getString(PREFIX + "msg.selectionIsNotWsdl")); //$NON-NLS-1$
        }

        return result;
    }
}
