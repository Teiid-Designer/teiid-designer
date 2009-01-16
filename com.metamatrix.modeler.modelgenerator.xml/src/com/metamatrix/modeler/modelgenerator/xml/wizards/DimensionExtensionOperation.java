/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.modelgenerator.xml.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.core.log.Logger;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;

public class DimensionExtensionOperation extends WorkspaceModifyOperation {

    protected static final String XML_FILE_EXTENSION_NAME = "XMLFileExtension"; //$NON-NLS-1$
    protected static final String XML_HTTP_EXTENSION_NAME = "XMLHTTPExtension"; //$NON-NLS-1$
    protected static final String XML_ACS_EXTENSION_NAME = "XMLACSExtension"; //$NON-NLS-1$
    private static final String HTTP_EXTENSION_MODEL = "XMLHttpConnectorExtensions.xmi"; //$NON-NLS-1$
    private static final String FILE_EXTENSION_MODEL = "XMLFileConnectorExtensions.xmi"; //$NON-NLS-1$
    private static final String ACS_EXTENSION_MODEL = "XMLACSExtensions.xmi"; //$NON-NLS-1$
    private static final String XML_EXTENSION_PROJECT = "XMLExtensionsProject"; //$NON-NLS-1$
    private static final String ACS_EXTENSION_PROJECT = "ACSExtensionsProject"; //$NON-NLS-1$

    private String m_extensionName;

    public DimensionExtensionOperation( String extensionName,
                                        Object vdbContext,
                                        Logger logger ) {
        m_extensionName = extensionName;
    }

    @Override
    protected void execute( IProgressMonitor monitor ) throws CoreException, InvocationTargetException {

        String project = determineProject();
        String model = determineModel();
        ModelProject theProject = ModelerCore.getModelWorkspace().getModelProjects()[0];
        boolean extensionExists = checkExtensionExists(theProject.getProject(), model);
        IPath newPath = new Path(model);
        if (!extensionExists) {
            try {
                InputStream is = getExtensionAsStream(project, model);
                createNewFile(newPath, theProject.getProject(), is);
            } catch (IOException e) {
                throw new InvocationTargetException(e);
            }
        }
    }

    private IFile createNewFile( IPath path,
                                 IProject project,
                                 InputStream is ) throws CoreException {
        IFile file = project.getFile(path);
        file.create(is, true, null);
        return file;
    }

    private InputStream getExtensionAsStream( String project,
                                              String model ) throws IOException {
        String extensionZip = deriveExtensionZipLocation(project + ".zip"); //$NON-NLS-1$
        ZipFile zipFile = new ZipFile(extensionZip);
        ZipEntry sourceEntry = zipFile.getEntry(model);
        InputStream is = zipFile.getInputStream(sourceEntry);
        return is;
    }

    private boolean checkExtensionExists( IProject project,
                                          String model ) {
        IFile contents = project.getFile(model);
        return contents.exists();
    }

    private String determineModel() {
        if (m_extensionName.equals(XML_FILE_EXTENSION_NAME)) {
            return FILE_EXTENSION_MODEL;
        }
        if (m_extensionName.equals(XML_HTTP_EXTENSION_NAME)) {
            return HTTP_EXTENSION_MODEL;
        }
        if (m_extensionName.equals(XML_ACS_EXTENSION_NAME)) {
            return ACS_EXTENSION_MODEL;
        }
        return null;
    }

    private String determineProject() {
        if (m_extensionName.equals(XML_FILE_EXTENSION_NAME) || m_extensionName.equals(XML_HTTP_EXTENSION_NAME)) {
            return XML_EXTENSION_PROJECT;
        }
        if (m_extensionName.equals(XML_ACS_EXTENSION_NAME)) {
            return ACS_EXTENSION_PROJECT;
        }
        return null;
    }

    private String deriveExtensionZipLocation( String zipName ) throws IOException {
        URL zipUrl = FileLocator.find(XmlImporterUiPlugin.getDefault().getBundle(), new Path(zipName), null);
        URL resolvedZip = FileLocator.resolve(zipUrl);
        return resolvedZip.getPath();
    }
}
