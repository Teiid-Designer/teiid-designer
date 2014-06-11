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
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.metamodels.core.ModelType;

/**
 *
 */
public class MockFileBuilder implements StringConstants {

    private final File realFile;
    private final IPath path;
    private final IFile resourceFile;
    private URI uri;
    private EmfResource emfModel;

    /**
     * @param realFile
     * @throws Exception
     */
    public MockFileBuilder(final File realFile) throws Exception {
        this.realFile = realFile;

        path = mock(IPath.class);
        resourceFile = mock(IFile.class);

        when(path.getFileExtension()).thenReturn(getExtension());
        when(path.toFile()).thenReturn(realFile);
        when(path.toString()).thenReturn(realFile.getAbsolutePath());
        when(path.toOSString()).thenReturn(realFile.getAbsolutePath());
        when(path.toPortableString()).thenReturn(realFile.getAbsolutePath());
        when(path.lastSegment()).thenReturn(realFile.getName());

        when(resourceFile.getName()).thenReturn(realFile.getName());
        when(resourceFile.getLocation()).thenReturn(path);
        when(resourceFile.getFullPath()).thenReturn(path);
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
        when(modelWorkspace.getEclipseMock().workspaceRoot().findMember(path)).thenReturn(resourceFile);
        when(modelWorkspace.getFinder().findByURI(getURI(), false)).thenReturn(emfModel);
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.realFile.getName();
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        String fileName = getName();
        if(fileName.lastIndexOf(DOT) > 0)
            return fileName.substring(fileName.lastIndexOf(DOT) + 1);

        return EMPTY_STRING;
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
        return this.resourceFile;
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
            uri = URI.createFileURI(resourceFile.getLocation().toString());

        return uri;
    }
}
