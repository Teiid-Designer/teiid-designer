/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
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
import com.metamatrix.modeler.modelgenerator.wsdl.ModelGeneratorWsdlPlugin;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

public class DimensionExtensionOperation extends WorkspaceModifyOperation {

    protected static final String XML_HTTP_EXTENSION_NAME = "XMLHTTPExtension"; //$NON-NLS-1$
    private static final String HTTP_EXTENSION_MODEL = "XMLHttpConnectorExtensions.xmi"; //$NON-NLS-1$
    private static final String XML_EXTENSION_PROJECT = "XMLExtensionsProject"; //$NON-NLS-1$

    private Object m_internal;

    public DimensionExtensionOperation( Object vdbContext ) {
        m_internal = vdbContext;
    }

    @Override
    protected void execute( IProgressMonitor monitor ) throws CoreException, InvocationTargetException {
        IProject proj = null;
        try {
            Class util = Class.forName("com.metamatrix.modeler.vdbview.ui.views.VdbViewUtil"); //$NON-NLS-1$
            Method getWorker = util.getMethod("getVdbViewWorker"); //$NON-NLS-1$
            Object worker = getWorker.invoke(null);
            Method getProj = worker.getClass().getMethod("getVdbProject"); //$NON-NLS-1$
            proj = (IProject)getProj.invoke(worker);
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
        boolean extensionExists = checkExtensionExists(proj);
        IPath newPath = new Path(HTTP_EXTENSION_MODEL);
        IFile theFile = null;
        if (!extensionExists) {
            try {
                InputStream is = getExtensionAsStream();
                theFile = createNewFile(newPath, proj, is);
            } catch (IOException e) {
                throw new InvocationTargetException(e);
            }

            IPath extPath = theFile.getFullPath();
            addExtensionToVdb(m_internal, extPath, theFile);
        }
    }

    private IFile createNewFile( IPath path,
                                 IProject project,
                                 InputStream is ) throws CoreException {
        IFile file = project.getFile(path);
        file.create(is, true, null);
        return file;
    }

    private InputStream getExtensionAsStream() throws IOException {
        String extensionZip = deriveExtensionZipLocation(XML_EXTENSION_PROJECT + ".zip"); //$NON-NLS-1$
        ZipFile zipFile = new ZipFile(extensionZip);
        ZipEntry sourceEntry = zipFile.getEntry(HTTP_EXTENSION_MODEL);
        InputStream is = zipFile.getInputStream(sourceEntry);
        return is;
    }

    private boolean checkExtensionExists( IProject project ) {
        IFile contents = project.getFile(HTTP_EXTENSION_MODEL);
        return contents.exists();
    }

    private String deriveExtensionZipLocation( String zipName ) throws IOException {
        URL zipUrl = FileLocator.find(ModelGeneratorWsdlPlugin.getDefault().getBundle(), new Path(zipName), null);
        URL resolvedZip = FileLocator.resolve(zipUrl);
        return resolvedZip.getPath();
    }

    private void addExtensionToVdb( Object vdbContext,
                                    IPath modelPath,
                                    IFile theFile ) throws VdbEditException {
        Class objClass = vdbContext.getClass();
        boolean extensionModelsExist = false;
        try {
            Method meth = objClass.getMethod("getVirtualDatabase"); //$NON-NLS-1$
            VirtualDatabase vdb = (VirtualDatabase)meth.invoke(vdbContext);
            List modelList = vdb.getModels();
            for (Iterator iter = modelList.iterator(); iter.hasNext();) {
                ModelReference ref = (ModelReference)iter.next();
                if (HTTP_EXTENSION_MODEL.equals(ref.getName())) {
                    extensionModelsExist = true;
                    break;
                }
            }
        } catch (Exception nme) {
            // chances are someone changed the api again.
            throw new VdbEditException(nme);
        }
        if (!extensionModelsExist) {
            ModelReference[] ref = null;
            if (vdbContext instanceof InternalVdbEditingContext) {
                ref = ((InternalVdbEditingContext)vdbContext).addModel(null, modelPath, false);
            } else {
                try {
                    Method addModel = objClass.getMethod("addModel", new Class[] {IProgressMonitor.class, File.class, //$NON-NLS-1$
                        String.class, boolean.class});
                    String strPath = theFile.getFullPath().toString();
                    File modelFile = theFile.getLocation().toFile();
                    ref = (ModelReference[])addModel.invoke(vdbContext, new Object[] {null, modelFile, strPath, Boolean.FALSE});
                } catch (Exception ie) {
                    if (ie instanceof InvocationTargetException) {
                        throw new VdbEditException(ie.getCause());
                    }
                    throw new VdbEditException(ie);
                }
            }
            try {
                ref[0].eResource().save(java.util.Collections.EMPTY_MAP);
            } catch (IOException ioe) {
                throw new VdbEditException(ioe);
            }
            // save it

        }
    }

}
