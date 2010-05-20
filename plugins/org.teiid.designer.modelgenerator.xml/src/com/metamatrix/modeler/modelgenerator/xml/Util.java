/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml;

import java.io.File;
import java.util.ResourceBundle;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.teiid.core.util.FileUtils;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelFileUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

public class Util extends PluginUtilImpl {
    /**
     * XSD extensions prefixed with the file wildcard and extension separator. Suitable for use in the
     * {@link org.eclipse.swt.widgets.FileDialog}.
     * 
     * @since 4.2
     */
    public static final String[] FILE_DIALOG_XSD_EXTENSIONS;
    static {
        // create XSD file dialog extension array
        FILE_DIALOG_XSD_EXTENSIONS = new String[] {createFileDialogExtension(ModelUtil.EXTENSION_XSD)};
    }

    public Util() {
        super(IUiConstants.PLUGIN_ID, IUiConstants.PLUGIN_ID + "." + PluginUtilImpl.RESOURCE_FILE_ROOT, //$NON-NLS-1$
              ResourceBundle.getBundle(IUiConstants.I18N_NAME));
    }

    /**
     * Convenience method to retrieve workbench shared image descriptors.
     * 
     * @param theImageName the name of the image descriptor being requested
     * @return the image descriptor or <code>null</code> if not found
     * @since 4.2
     */
    public static ImageDescriptor getSharedImageDescriptor( String theImageName ) {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(theImageName);
    }

    /**
     * Creates an extension which can be used in a {@link org.eclipse.swt.widgets.FileDialog}. Prefixes the specified extension
     * with the file name wildcard and the extension separator character.
     * 
     * @param theExtension the extension being used
     * @since 4.2
     */
    public static String createFileDialogExtension( String theExtension ) {
        return new StringBuffer().append(FileUtils.Constants.FILE_NAME_WILDCARD).append(FileUtils.Constants.FILE_EXTENSION_SEPARATOR_CHAR).append(theExtension).toString();
    }

    /**
     * Indicates if the specified file system resource is an XSD.
     * 
     * @param theFile the file being checked
     * @return <code>true</code>if an XSD file; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isXsdFile( File theFile ) {
        return ModelFileUtil.isXsdFile(theFile);
    }

    /**
     * Indicates if the specified workspace resource is an XSD.
     * 
     * @param theFile the file being checked
     * @return <code>true</code>if an XSD file; <code>false</code> otherwise.
     * @since 4.2
     */
    public static boolean isXsdFile( IFile theFile ) {
        return ModelUtil.isXsdFile(theFile);
    }
}
