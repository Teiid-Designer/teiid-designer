/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.teiid.designer.messages"; //$NON-NLS-1$
    
    public static String valueCannotBeNull;
    
    public static String invalidTargetTypeForGetServerMethod;
    public static String invalidTargetTypeForGetDataSourceMethod;
    public static String invalidTargetTypeForGetTranslatorMethod;
    public static String invalidTargetTypeForGetUpdatedServerMethod;

    public static String NoExecutionAdminFactory;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
