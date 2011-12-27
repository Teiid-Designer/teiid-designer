/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	public static String ExtensionPropertiesManager_loadExtensionsErrorMessage;
    public static String InvalidPropertyValue;
    public static String InvalidPropertyEditorConstrainedValue;
    public static String InvalidPropertyEditorValue;
    public static String InvalidNullPropertyValue;
    public static String MissingPropertyDefinition;
    public static String PrefixedName_invalidPrefix;
    public static String UnknownPropertyType;

    static {
        NLS.initializeMessages("org.teiid.designer.extension.messages", Messages.class); //$NON-NLS-1$
    }
}
