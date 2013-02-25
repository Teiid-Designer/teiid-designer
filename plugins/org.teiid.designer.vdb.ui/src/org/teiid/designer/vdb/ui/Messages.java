/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
@SuppressWarnings("javadoc")
public class Messages  extends NLS {

	public static String vdbBuildTaskName;
	public static String vdbFilesBuildProblemMsg;
    public static String vdblFileBuildErrorMsg;
    public static String vdbBuildSubTaskName;
    public static String refactorModelVdbDependencyTitle;
    public static String refactorModelVdbDependencyMessage_noOpenEditors;
    public static String refactorModelVdbDependencyMessage_openEditors;
    public static String fixVdbPath_OpenEditorTitle;
    public static String fixVdbPath_OpenEditorMessage;

	public static String synchronizeVdbLabel;
    
    static {
        NLS.initializeMessages("org.teiid.designer.vdb.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
