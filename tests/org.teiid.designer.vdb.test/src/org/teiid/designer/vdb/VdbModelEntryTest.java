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
import java.io.File;
import java.io.FileInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelResourceMockFactory;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ResourceFinder;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.spi.RegistrySPI;
import org.teiid.designer.core.workspace.ModelObjectAnnotations;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;

/**
 * 
 */
@SuppressWarnings( "javadoc" )
public class VdbModelEntryTest {

    private VdbModelEntry entry;
    private String modelResourceFileName;
    private VdbTest vdbTest;
    private ModelWorkspaceMock modelWorkspaceMock;

    @Before
    public void before() throws Exception {
        vdbTest = new VdbTest();
        vdbTest.before();

        Vdb vdb = vdbTest.getVdb();
        EclipseMock eclipseMock = vdbTest.getEclipseMock();
        File tempDir = VdbPlugin.singleton().getStateLocation().toFile();
        tempDir = new File(tempDir, "project" + File.separator + "folder");
        tempDir.mkdirs();
        
        String suffix = ".xmi";
        File tempFile = ModelResourceMockFactory.createTempFile("test", suffix, tempDir, "abcdef");
        FileInputStream fileInputStream = new FileInputStream(tempFile);

        modelResourceFileName = FileUtils.getFilenameWithoutExtension(tempFile.getName());

        final IPath nonExtModelPathName = mock(IPath.class);
        when(nonExtModelPathName.toString()).thenReturn(modelResourceFileName);
        when(nonExtModelPathName.lastSegment()).thenReturn(modelResourceFileName);

        final IPath modelPath = mock(IPath.class);
        when(modelPath.toFile()).thenReturn(tempFile);
        when(modelPath.getFileExtension()).thenReturn("xmi");
        when(modelPath.toString()).thenReturn(tempFile.getCanonicalPath());
        when(modelPath.toOSString()).thenReturn(tempFile.getCanonicalPath());
        when(modelPath.lastSegment()).thenReturn(modelResourceFileName);
        when(modelPath.removeFileExtension()).thenReturn(nonExtModelPathName);

        IFile file = mock(IFile.class);
        when(file.getLocation()).thenReturn(modelPath);
        when(file.findMarkers(anyString(), anyBoolean(), anyInt())).thenReturn(new IMarker[0]);
        when(file.getContents()).thenReturn(fileInputStream);
        when(file.getFullPath()).thenReturn(modelPath);

        modelWorkspaceMock = new ModelWorkspaceMock(eclipseMock);
        when(eclipseMock.workspaceRootLocation().append(modelPath)).thenReturn(modelPath);
        when(eclipseMock.workspaceRoot().findMember(modelPath)).thenReturn(file);

        final ModelAnnotation annotation = mock(ModelAnnotation.class);
        when(annotation.getModelType()).thenReturn(ModelType.PHYSICAL_LITERAL);
        
        final EmfResource model = mock(EmfResource.class);
        ResourceFinder finder = mock(ResourceFinder.class);
        when(finder.findByURI(isA(URI.class), eq(false))).thenReturn(model);
        modelWorkspaceMock.setFinder(finder);

        when(model.getModelType()).thenReturn(ModelType.PHYSICAL_LITERAL);
        when(model.getModelAnnotation()).thenReturn(annotation);
        
        final ModelResource modelResource = mock(ModelResource.class);
        when(modelResource.getPrimaryMetamodelUri()).thenReturn(ModelUtil.URI_XML_SCHEMA_MODEL);
     
        final ModelEditor me = ModelResourceMockFactory.getModelerEditor();
        ((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.MODEL_EDITOR_KEY, me);
        when(me.findModelResource(model)).thenReturn(modelResource);
        when(me.findModelResource(file)).thenReturn(modelResource);
        
        final ModelObjectAnnotations annotations = mock(ModelObjectAnnotations.class);
        when(modelResource.getAnnotations()).thenReturn(annotations);
        entry = vdb.addEntry(modelPath);
        
        fileInputStream.close();
    }
    
    @After
    public void afterEach() throws Exception {
        ((RegistrySPI) ModelerCore.getRegistry()).unregister(ModelerCore.MODEL_EDITOR_KEY);
    	modelWorkspaceMock.dispose();
        vdbTest.after();
    }

    @Test
    public void shouldReflectJndiNameAsSimpleModelName() throws Exception {
        assertThat(entry.getSourceInfo().getSource(0).getJndiName(), is(modelResourceFileName));
    }

    @Test
    public void shouldReflectSourceNameAsSimpleModelName() throws Exception {
        assertThat(entry.getSourceInfo().getSource(0).getName(), is(modelResourceFileName));
    }
}
