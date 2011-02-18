/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import javax.xml.bind.JAXBContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.core.designer.EclipseMock;
import org.teiid.designer.core.ModelWorkspaceMock;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.modeler.util.OperationUtil;
import com.metamatrix.core.modeler.util.OperationUtil.ReturningUnreliable;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelObjectAnnotations;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * 
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( {FileUtils.class, IndexUtil.class, JAXBContext.class, ModelerCore.class, ModelWorkspaceManager.class,
    ModelBuildUtil.class, ModelUtil.class, OperationUtil.class, ResourcesPlugin.class, VdbPlugin.class} )
public class VdbModelEntryTest {

    private VdbModelEntry entry;
    private Vdb vdb;
    private EclipseMock eclipseMock;

    @Before
    public void before() throws Exception {
        final VdbTest vdbTest = new VdbTest();
        vdbTest.before();
        vdb = vdbTest.getVdb();
        mockStatic(IndexUtil.class);
        final IPath modelPath = Path.fromPortableString("project/folder/test.xmi");
        eclipseMock = vdbTest.getEclipseMock();
        final ModelWorkspaceMock modelWorkspaceMock = new ModelWorkspaceMock(eclipseMock);
        when(eclipseMock.workspaceRootLocation().append(modelPath)).thenReturn(modelPath);

        IFile file = mock(IFile.class);
        when(file.getLocation()).thenReturn(modelPath);
        when(file.findMarkers(anyString(), anyBoolean(), anyInt())).thenReturn(new IMarker[0]);
        when(eclipseMock.workspaceRoot().findMember(modelPath)).thenReturn(file);
        mockStatic(FileUtils.class);
        mockStatic(OperationUtil.class);
        when(OperationUtil.perform(isA(ReturningUnreliable.class))).thenReturn(new Long(1));
        mockStatic(ModelBuildUtil.class);
        mockStatic(IndexUtil.class);
        Index index = mock(Index.class);
        when(IndexUtil.getIndexFile(anyString(), anyString(), anyString())).thenReturn(index);

        final EmfResource model = mock(EmfResource.class);
        when(modelWorkspaceMock.getFinder().findByURI(isA(URI.class), eq(false))).thenReturn(model);
        final ModelAnnotation annotation = mock(ModelAnnotation.class);
        when(model.getModelAnnotation()).thenReturn(annotation);
        mockStatic(ModelUtil.class);
        when(ModelUtil.isXmiFile(model)).thenReturn(true);
        when(ModelUtil.isPhysical(model)).thenReturn(true);
        final ModelResource modelResource = mock(ModelResource.class);
        when(ModelerCore.getModelEditor().findModelResource(model)).thenReturn(modelResource);
        final ModelObjectAnnotations annotations = mock(ModelObjectAnnotations.class);
        when(modelResource.getAnnotations()).thenReturn(annotations);
        entry = vdb.addModelEntry(modelPath, null);
    }

    @Test
    public void shouldReflectJndiNameAsSimpleModelName() throws Exception {
        assertThat(entry.getJndiName(), is("test"));
    }

    @Test
    public void shouldReflectSourceNameAsSimpleModelName() throws Exception {
        assertThat(entry.getSourceName(), is("test"));
    }
}
