/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    public static String descriptionColumnText;
    public static String extendedMetamodelUriColumnText;
    public static String namespacePrefixColumnText;
    public static String namespaceUriColumnText;
    public static String versionColumnText;

    public static String cloneMedActionText;
    public static String cloneMedActionToolTip;
    public static String findMedReferencesActionText;
    public static String findMedReferencesActionToolTip;
    public static String openMedActionText;
    public static String openMedActionToolTip;
    public static String registerMedActionText;
    public static String registerMedActionToolTip;
    public static String unregisterMedActionText;
    public static String unregisterMedActionToolTip;
    
    static {
        NLS.initializeMessages("org.teiid.designer.extension.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
