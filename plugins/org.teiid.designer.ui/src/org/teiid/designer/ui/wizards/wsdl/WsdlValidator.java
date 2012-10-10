/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.wizards.wsdl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.webservice.WebServicePlugin;


 /**
 * @since 8.0
 */
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
