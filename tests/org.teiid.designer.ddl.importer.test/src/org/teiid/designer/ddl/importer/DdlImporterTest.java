/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.junit.Before;
import org.junit.Test;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.exception.EmptyArgumentException;

import com.metamatrix.core.util.SmartTestDesignerSuite;

public class DdlImporterTest {    
    private static final File TEST_DDL_FILE = SmartTestDesignerSuite.getTestDataFile(DdlImporterTest.class, "createTables.ddl");
    
    private EclipseMock eclipseMock;
    
    private DdlImporter importer;

    private DdlImporter createImporter(IProject[] projects) {
        return new DdlImporter(projects);
    }
    
    @Before
    public void setUp() {
        eclipseMock = new EclipseMock();
        importer = createImporter(null);
    }
    
    @Test
    public void shouldAcceptNewModel() {
        
        when(eclipseMock.workspace().validateName(anyString(), eq(IResource.FILE))).thenReturn(Status.OK_STATUS);
        
        final IFile model = mock(IFile.class);
        when(eclipseMock.workspaceRoot().getFile((IPath)anyObject())).thenReturn(model);
       
        importer.setModelName("model");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldFailIfDdlFileDoesNotExist() {
        importer.setDdlFileName("doesNotExist");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfDdlFileNameIsEmpty() {
        importer.setDdlFileName(" ");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfDdlFileNameIsNull() {
        importer.setDdlFileName(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldFailIfModelFolderInNonModelProject() {
        final IProject project = mock(IProject.class);
        when(eclipseMock.workspaceRoot().findMember("project")).thenReturn(project);
        
        final DdlImporter importer = createImporter(new IProject[] {project});
        importer.setModelFolder("project/folder");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldFailIfModelFolderIsFile() {
        final String folder = "project/file";
        final IPath path = Path.fromPortableString(folder).makeAbsolute();
        final IProject project = mock(IProject.class);
        final IWorkspaceRoot root = eclipseMock.workspaceRoot();
        final String projectName = path.segment(0);
        when(root.findMember(projectName)).thenReturn(project);
        when(project.getName()).thenReturn(projectName);
        when(eclipseMock.workspace().validatePath(path.toString(), IResource.PROJECT | IResource.FOLDER)).thenReturn(Status.OK_STATUS);
        final IFile file = mock(IFile.class);
        when(root.findMember(path)).thenReturn(file);
        
        final DdlImporter importer = createImporter(new IProject[] {project});
        importer.setModelFolder(folder);
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelFolderNameHasNoSegments() {
        importer.setModelFolder("/");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelFolderNameIsEmpty() {
        importer.setModelFolder(" ");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelFolderNameIsNull() {
        importer.setModelFolder((String)null);
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelNameIsEmpty() {
        importer.setModelName(" ");
    }

    @Test( expected = EmptyArgumentException.class )
    public void shouldFailIfModelNameIsNull() {
        importer.setModelName(null);
    }

    @Test
    public void shouldProvideDdlFile() {
        importer.setDdlFileName(TEST_DDL_FILE.getAbsolutePath());
        assertThat(importer.ddlFileName(), is(TEST_DDL_FILE.getAbsolutePath()));
    }

    @Test
    public void shouldProvideModel() {
        when(eclipseMock.workspace().validateName(anyString(), eq(IResource.FILE))).thenReturn(Status.OK_STATUS);
        final IFile modelFile = mock(IFile.class);
        final IPath modelPath = mock(IPath.class);
        when(modelPath.removeFileExtension()).thenReturn(modelPath);
        when(modelFile.getFullPath()).thenReturn(modelPath);
        when(eclipseMock.workspaceRoot().getFile((IPath)anyObject())).thenReturn(modelFile);
        final IFolder folder = mock(IFolder.class);
        final IPath folderPath = mock(IPath.class);
        when(folder.getFullPath()).thenReturn(folderPath);
        when(folderPath.append(anyString())).thenReturn(folderPath);

        importer.setModelFolder(folder);
        importer.setModelName("model");
        assertThat(importer.modelFile(), notNullValue());
    }

    @Test
    public void shouldProvideModelFolder() {
        when(eclipseMock.workspace().validatePath(anyString(), eq(IResource.PROJECT | IResource.FOLDER))).thenReturn(Status.OK_STATUS);
        final IFolder folder = mock(IFolder.class);
        when(eclipseMock.workspaceRoot().getFolder((IPath)anyObject())).thenReturn(folder);

        importer.setModelFolder("project/folder");
        assertThat(importer.modelFolder(), notNullValue());
        final IContainer container = mock(IContainer.class);
        importer.setModelFolder(container);
        assertThat(importer.modelFolder(), is(container));
    }
}
