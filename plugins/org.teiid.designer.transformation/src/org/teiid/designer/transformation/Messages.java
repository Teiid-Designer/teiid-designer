/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation;

import org.eclipse.osgi.util.NLS;

/**
 * @since 8.0
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

	public static String validate_categoryUndefinedForUDF;
	public static String validate_javaClassUndefinedForUDF;
	public static String validate_javaMethodUndefinedForUDF;
	
    
    public static String virtualSchemaUnsupportedMessage;
    public static String virtualCatalogUnsupportedMessage;
    public static String virtualViewUnsupportedMessage;

    static {
        NLS.initializeMessages("org.teiid.designer.transformation.messages", Messages.class); //$NON-NLS-1$
    }
}