/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.workspace;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.FileInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.metamodels.core.ModelType;

/**
 *
 */
public class MockFileBuilder implements StringConstants {

    private String baseName;
    private String extension;
    private File realFile;
    private IPath path;
    private IResource resource;
    private URI uri;
    private EmfResource emfModel;

    /**
     * @param realFile
     * @throws Exception
     */
    public MockFileBuilder(final File realFile) throws Exception {
        this.realFile = realFile;
        if (realFile.isDirectory())
            mockDirectory();
        else
            mockFile();
    }

    private void mockPath() {
        String fileName = realFile.getName();
        IPath absFileNameNoExt = new Path(realFile.getAbsolutePath());

        if(fileName.lastIndexOf(DOT) > 0) {
            this.extension = fileName.substring(fileName.lastIndexOf(DOT) + 1);
            this.baseName = fileName.substring(0, fileName.lastIndexOf(DOT));
            absFileNameNoExt = absFileNameNoExt.removeFileExtension();
        } else {
            this.extension = EMPTY_STRING;
            this.baseName = fileName;
        }

        path = new Path(realFile.getAbsolutePath());
    }

    private void mockResource() {
        when(resource.getName()).thenReturn(realFile.getName());
        when(resource.getLocation()).thenReturn(path);
        when(resource.getFullPath()).thenReturn(path);
    }

    private void mockDirectory() throws Exception {
        mockPath();

        resource = mock(IFolder.class);
        mockResource();
    }

    private void mockFile() throws Exception {
        mockPath();

        resource = mock(IFile.class);
        mockResource();

        IFile resourceFile = getResourceFile();
        when(resourceFile.getContents()).thenReturn(new FileInputStream(realFile));

        if (ModelUtil.isModelFile(path)) {
            emfModel = mock(EmfResource.class);
            when(emfModel.getModelType()).thenReturn(ModelType.PHYSICAL_LITERAL);
        }
    }

    /**
     * @param name
     * @param extension
     * @throws Exception
     */
    public MockFileBuilder(String name, String extension) throws Exception {
        this(File.createTempFile(name, DOT + extension));
        // Tidy up the temporary file
        realFile.deleteOnExit();
    }

    /**
     * Add this builder's components to a mocked model workspace
     *
     * @param modelWorkspace
     */
    public void addToModelWorkspace(ModelWorkspaceMock modelWorkspace) {
        when(modelWorkspace.getEclipseMock().workspaceRoot().findMember(path)).thenReturn(resource);
        when(modelWorkspace.getFinder().findByURI(getURI(), false)).thenReturn(emfModel);
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.realFile.getName();
    }

    /**
     * @return base name, ie. no extension
     */
    public String getBaseName() {
        return baseName;
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @return the path
     */
    public IPath getPath() {
        return this.path;
    }

    /**
     * @return the resourceFile
     */
    public IFile getResourceFile() {
        if (this.resource instanceof IFile)
            return (IFile) this.resource;

        return null;
    }

    /**
     * @return the resourceFolder
     */
    public IFolder getResourceFolder() {
        if (this.resource instanceof IFolder)
            return (IFolder) this.resource;

        return null;
    }

    /**
     * @return the realFile
     */
    public File getRealFile() {
        return this.realFile;
    }

    /**
     * @return uri of the resource file
     */
    public URI getURI() {
        if (uri == null)
            uri = URI.createFileURI(resource.getLocation().toString());

        return uri;
    }
}
