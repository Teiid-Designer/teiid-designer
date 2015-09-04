package org.teiid.designer.core.workspace;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stubVoid;
import static org.mockito.Mockito.when;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.teiid.core.designer.EclipseMock;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.ModelerCore;

public final class WorkspaceResourceFinderUtilTest {

    private static final String NAME = "MyModel";

    private IResource model;
    private IProject modelProject;
    private ModelWorkspaceMock modelWorkspaceMock;
    private List<IResource> resources = new ArrayList<IResource>();

    @After
    public void afterEach() throws Exception {
        this.modelWorkspaceMock.dispose();
        this.resources.clear();
    }

    @SuppressWarnings( "deprecation" )
    @Before
    public void beforeEach() throws Exception {
        final EclipseMock eclipseMock = new EclipseMock();
        this.modelWorkspaceMock = new ModelWorkspaceMock(eclipseMock);

        this.modelProject = mock(IProject.class);
        eclipseMock.addProject(this.modelProject);

        when(this.modelProject.isOpen()).thenReturn(true);
        when(this.modelProject.getProject()).thenReturn(this.modelProject);
        when(this.modelProject.hasNature(ModelerCore.NATURE_ID)).thenReturn(true);
        when(this.modelProject.members()).thenReturn(new IResource[0]);

        // add the workspace resources to the visitor
        stubVoid(this.modelProject).toAnswer(new Answer<Void>() {

            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                final IResourceVisitor visitor = (IResourceVisitor)args[0];

                // add each resource to the visitor
                for (final IResource resource : getResources()) {
                    visitor.visit(resource);
                }

                return null;
            }
        }).on().accept(Matchers.<IResourceVisitor>anyVararg());

        { // .project file
            final File dotFile = File.createTempFile("dotFile", ".project");
            dotFile.deleteOnExit();

            final IPath path = new Path(dotFile.getAbsolutePath());
            final IFile dotIfile = mock(IFile.class);
            when(dotIfile.getName()).thenReturn(".project");
            when(dotIfile.getType()).thenReturn(IResource.FILE);
            when(dotIfile.isAccessible()).thenReturn(true);
            when(dotIfile.getLocation()).thenReturn(path);
            when(dotIfile.getProject()).thenReturn(this.modelProject);

            when(this.modelProject.getFile(DotProjectUtils.DOT_PROJECT)).thenReturn(dotIfile);
        }

        this.model = createFile(NAME);
    }

    List<IResource> getResources() {
        return this.resources;
    }

    IWorkspaceRoot getWorkspaceRoot() {
        return this.modelWorkspaceMock.getEclipseMock().workspaceRoot();
    }

    @Test
    public void shouldNotHaveCircularDependenciesWhenNoDependents() {
        assertThat(WorkspaceResourceFinderUtil.getFirstResourceHavingCircularDependency(this.model), is(nullValue()));
    }

    @Test
    public void shouldHaveCircularDependencies() throws Exception {
        // this.model depends on aaa, aaa depends on bbb, bbb depends on this.model
        final IFile aaa = createFile("aaa");
        final IFile bbb = createFile("bbb");

        final String modelHeader = createXmiHeader(aaa.getName());
        writeFile(this.model.getRawLocation().toFile(), modelHeader);

        final String aaaHeader = createXmiHeader(bbb.getName());
        writeFile(aaa.getRawLocation().toFile(), aaaHeader);

        final String bbbHeader = createXmiHeader(this.model.getName());
        writeFile(bbb.getRawLocation().toFile(), bbbHeader);

        assertThat(WorkspaceResourceFinderUtil.getFirstResourceHavingCircularDependency(this.model), is(notNullValue()));
    }

    @Test
    public void shouldNotHaveCircularDependencies() throws Exception {
        // this.model depends on aaa
        final IFile aaa = createFile("aaa");

        final String modelHeader = createXmiHeader(aaa.getName());
        writeFile(this.model.getRawLocation().toFile(), modelHeader);

        assertThat(WorkspaceResourceFinderUtil.getFirstResourceHavingCircularDependency(this.model), is(nullValue()));
    }

    private static void writeFile(final File file,
                                  final String content) throws Exception {
        Files.write(Paths.get(file.getPath()), content.getBytes());
    }

    private IFile createFile(final String name) throws Exception {
        final File file = File.createTempFile(name, ".xmi");
        file.deleteOnExit();

        final IPath path = new Path(file.getAbsolutePath());
        final IFile iFile = mock(IFile.class);
        when(iFile.getName()).thenReturn(file.getName());
        when(iFile.getLocation()).thenReturn(path);
        when(iFile.getRawLocation()).thenReturn(path);
        when(iFile.getFullPath()).thenReturn(path);
        when(iFile.exists()).thenReturn(true);
        when(iFile.getType()).thenReturn(IResource.FILE);
        when(iFile.getProject()).thenReturn(this.modelProject);
        this.resources.add(iFile);

        when(this.modelProject.members()).thenReturn(this.resources.toArray(new IResource[this.resources.size()]));
        when(this.modelProject.findMember(path)).thenReturn(iFile);
        when(this.modelProject.findMember(path.toString())).thenReturn(iFile);
        return iFile;
    }

    private String createXmiHeader(final String... modelsToImport) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"ASCII\"?> <xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:diagram=\"http://www.metamatrix.com/metamodels/Diagram\" xmlns:mmcore=\"http://www.metamatrix.com/metamodels/Core\" xmlns:mmws=\"http://www.metamatrix.com/metamodels/WebService\" xmlns:transformation=\"http://www.metamatrix.com/metamodels/Transformation\">");
        builder.append("<mmcore:ModelAnnotation xmi:uuid=\"mmuuid:ae435d62-1cff-4038-ae14-4a5fdf5fd417\" primaryMetamodelUri=\"http://www.metamatrix.com/metamodels/WebService\" modelType=\"VIRTUAL\" ProducerName=\"Teiid Designer\" ProducerVersion=\"9.2.0\">");

        int i = 0;

        if ((modelsToImport != null) && (modelsToImport.length != 0)) {
            for (final String model : modelsToImport) {
                final String modelName = model.substring(0, model.indexOf(".xmi"));

                builder.append("<modelImports xmi:uuid=\"mmuuid:96eda359-0832-46e7-9e33-c878a3603dc").append(i).append("\" ");
                builder.append("name=\"").append(modelName).append("\" modelLocation=\"").append(model).append("\" uuid=\"mmuuid:88b08082-c021-4c3a-a4a3-148f5e58dc3").append(i).append("\" ");
                builder.append("modelType=\"VIRTUAL\" primaryMetamodelUri=\"http://www.metamatrix.com/metamodels/XmlDocument\"/>");
                ++i;
            }
        }

        builder.append("</mmcore:ModelAnnotation>");
        builder.append("</xmi:XMI>");

        return builder.toString();
    }

}
