/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.legacy;

import org.eclipse.osgi.util.NLS;

/**
*
*/
public class Messages  extends NLS {

    public static String wrongFormat;

	public static String blockSizeExceeded;

    static {
        NLS.initializeMessages(Messages.class.getPackage().getName() + ".messages", Messages.class); //$NON-NLS-1$
    }


}
