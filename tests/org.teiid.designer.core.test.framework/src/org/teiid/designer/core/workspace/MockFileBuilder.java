/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.workspace;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.function.extension.FunctionModelExtensionConstants;
import org.teiid.designer.metamodels.relational.extension.CoreModelExtensionConstants;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;

/**
 *
 */
public class MockFileBuilder implements StringConstants {

    private String baseName;
    private String extension;
    private File realFile;
    private IPath location;
    private IResource resource;
    private URI uri;

    private IProject project;
    private List<IResource> projectChildren = new ArrayList<IResource>();

    // Not initialised unless we need it
    private ModelExtensionRegistry extensionRegistry = null;

    /**
     * Prefixes for model extension assistants
     */
    private static final String[] EXTENSION_PREFIXES = {
        CoreModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix(),
        FunctionModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix(),
        RelationalModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix(),
        RestModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix()
    };

    /**
     * @param realFile
     * @throws Exception
     */
    public MockFileBuilder(final File realFile) throws Exception {
        this.realFile = realFile;
        this.location = new Path(realFile.getAbsolutePath());

        if (realFile.isDirectory())
            mockDirectory();
        else
            mockFile();

        mockProject();
    }

    /**
     * Enable extension registry support on resource change
     */
    public void enableExtensionRegistry() {
        extensionRegistry = ExtensionPlugin.getInstance().getRegistry();
    }

    /**
     * Enable the mocking of the parent of this file as a project
     */
    public void enableProject() {
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

        location = new Path(realFile.getAbsolutePath());
    }

    private void mockFileProperties(IResource resource, File realFile, IPath wkspPath, int resourceType) {
        when(resource.getName()).thenReturn(realFile.getName());
        when(resource.getLocation()).thenReturn(new Path(realFile.getAbsolutePath()));
        when(resource.getFullPath()).thenReturn(wkspPath);
        when(resource.getFileExtension()).thenReturn(wkspPath.getFileExtension());
        when(resource.getType()).thenReturn(resourceType);
    }

    private void addProjectChild(IFile file) throws Exception {
        projectChildren.add(file);
    }

    private IFile mockProjectFile(IProject project, IPath projectPath) throws CoreException, Exception {
        File realFile;
        IPath wkspProjectLocation = project.getFullPath();
        final IFile file = mock(IFile.class);

        if (project.getLocation().isPrefixOf(projectPath))
            realFile = new File(projectPath.toOSString());
        else
            realFile = new File(project.getLocation().append(projectPath).toOSString());

        IPath wkspPath;
        if (project.getFullPath().isPrefixOf(projectPath))
            wkspPath = projectPath;
        else
            wkspPath = project.getFullPath().append(projectPath);

        mockFileProperties(file, realFile, wkspPath, IResource.FILE);
        when(file.getParent()).thenReturn(project);
        when(file.getProject()).thenReturn(project);
        when(file.getProjectRelativePath()).thenReturn(wkspPath.makeRelativeTo(wkspProjectLocation));
        when(file.exists()).thenReturn(realFile.exists());

        //
        // Allow writing of contents to IFile to be sent to the real file
        //
        final File theRealFile = realFile;
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                InputStream stream = (InputStream) args[0];
                try {
                    FileUtils.write(stream, theRealFile);

                    if (extensionRegistry != null) {
                        for (String prefix : EXTENSION_PREFIXES) {
                            final ModelExtensionAssistant assistant = extensionRegistry.getModelExtensionAssistant(prefix);
                            assistant.applyMedIfNecessary(file);
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    fail(ex.getMessage());
                } finally {
                    if (theRealFile.exists())
                        theRealFile.deleteOnExit();
                }

                return null;
            }
        }).when(file).create(isA(InputStream.class), anyInt(), any(IProgressMonitor.class));

        addProjectChild(file);
        return file;
    }

    private void mockProject() throws Exception {
        //
        // Makes real files parent a project
        //
        File parentFile = realFile.getParentFile();
        final Path wkspProjectPath = new Path(File.separator + parentFile.getName());
        project = mock(IProject.class);

        // Tie up resource to project
        when(resource.getParent()).thenReturn(project);
        when(resource.getProject()).thenReturn(project);

        // project's attributes
        mockFileProperties(project, parentFile, wkspProjectPath, IResource.PROJECT);

        // project returns itself
        when(project.getProject()).thenReturn(project);
        when(project.exists()).thenReturn(parentFile.exists());

        // attributes for checking its a modeller project
        when(project.isOpen()).thenReturn(true);
        when(project.hasNature(ModelerCore.NATURE_ID)).thenReturn(true);

        // set up all child created files to return parent
        when(project.getFile(isA(IPath.class))).thenAnswer(new Answer<IFile>() {

            @Override
            public IFile answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                assertNotNull(args[0]);
                IPath path = (IPath) args[0];

                return mockProjectFile(project, path);
            }
        });

        when(project.members()).thenReturn(projectChildren.toArray(new IResource[0]));

        // The .project file        
        File dotProjectFile = new File(parentFile, DotProjectUtils.DOT_PROJECT);
        dotProjectFile.createNewFile();
        dotProjectFile.deleteOnExit();
        IPath dotPath = new Path(dotProjectFile.getAbsolutePath());
        IPath wkspDotPath = project.getFullPath().append(dotPath);

        IFile dotProject = mock(IFile.class);
        mockFileProperties(dotProject, dotProjectFile, wkspDotPath, IResource.FILE);
        when(dotProject.getParent()).thenReturn(project);
        when(dotProject.getProject()).thenReturn(project);

        when(project.getFile(DotProjectUtils.DOT_PROJECT)).thenReturn(dotProject);
        when(dotProject.isAccessible()).thenReturn(true);
        projectChildren.add(dotProject);
    }

    private void mockDirectory() throws Exception {
        resource = mock(IFolder.class);
        mockFileProperties(resource, realFile, location, IResource.FOLDER);
    }

    private void mockFile() throws Exception {
        resource = mock(IFile.class);
        mockFileProperties(resource, realFile, location, IResource.FILE);

        IFile resourceFile = getResourceFile();
        when(resourceFile.getContents()).thenReturn(new FileInputStream(realFile));
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
        when(modelWorkspace.getEclipseMock().workspaceRoot().findMember(location)).thenReturn(resource);
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
        return this.location;
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

    /**
     * @return project
     */
    public IProject getProject() {
        return project;
    }
}
