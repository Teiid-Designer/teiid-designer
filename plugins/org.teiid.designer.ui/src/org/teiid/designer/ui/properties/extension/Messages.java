/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.ui.properties.extension;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    public static String errorCreatingPropertyDescriptors;
    public static String errorObtainingPropertyFromAssistant;
    public static String modelExtensionAssistantNotFound;
	public static String modelExtensionPropertyCategory;
	public static String namespacePrefixIsEmpty;
    public static String openEditorDialogMessage;
	public static String openEditorDialogTitle;
	public static String propertyDefinitiontNotFound;
	public static String unexpectedPropertySourceId;
	public static String unexpectedPropertyValueType;
	public static String valueIsNotAnAllowedValue;
	public static String workspaceFileNotFound;

    static {
        NLS.initializeMessages("org.teiid.designer.ui.properties.extension.messages", Messages.class); //$NON-NLS-1$
    }
}
