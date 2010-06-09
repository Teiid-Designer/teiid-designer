/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.core.designer.EclipseMock;
import org.teiid.designer.core.ModelWorkspaceMock;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * 
 */
@RunWith( PowerMockRunner.class ) @PrepareForTest( {VdbPlugin.class, ResourcesPlugin.class, ModelerCore.class,
    ModelWorkspaceManager.class, IndexUtil.class} ) public class VdbTest {

    @Mock private IFile vdbFile;

    Vdb createVdb() {
        initMocks(this);
        final IPath vdbPath = mock(IPath.class);
        when(vdbFile.getLocation()).thenReturn(vdbPath);
        final File vdbFileAbsolute = mock(File.class);
        when(vdbPath.toFile()).thenReturn(vdbFileAbsolute);
        when(vdbFileAbsolute.length()).thenReturn(0L);

        mockStatic(VdbPlugin.class);
        final VdbPlugin plugin = PowerMockito.mock(VdbPlugin.class);
        when(VdbPlugin.singleton()).thenReturn(plugin);
        final IPath stateFolderPath = mock(IPath.class);
        when(plugin.getStateLocation()).thenReturn(stateFolderPath);
        final IPath vdbFolderPath = mock(IPath.class);
        when(stateFolderPath.append((IPath)anyObject())).thenReturn(vdbFolderPath);
        final File vdbFolderFile = new File(".");
        when(vdbFolderPath.toFile()).thenReturn(vdbFolderFile);
        return new Vdb(vdbFile, null);
    }

    @Test public void shouldBeSynchronizedAfterAddingEntry() throws Exception {
        final ModelWorkspaceMock modelWorkspaceMock = new ModelWorkspaceMock();
        final Vdb vdb = createVdb();
        vdb.addEntry(mock(IPath.class), null);
        assertThat(vdb.isSynchronized(), is(true));
        mockStatic(IndexUtil.class);
        when(IndexUtil.getRuntimeIndexFileName(isA(IResource.class))).thenReturn("indexName");
        final IPath modelPath = Path.fromPortableString("model.xmi");
        when(modelWorkspaceMock.getEclipseMock().getRootPath().append(modelPath)).thenReturn(modelPath);
        final EmfResource model = mock(EmfResource.class);
        when(modelWorkspaceMock.getFinder().findByURI(isA(URI.class), eq(false))).thenReturn(model);
        final ModelAnnotation annotation = mock(ModelAnnotation.class);
        when(model.getModelAnnotation()).thenReturn(annotation);
        vdb.addModelEntry(modelPath, null);
        assertThat(vdb.isSynchronized(), is(true));
    }

    @Test public void shouldExposeFile() throws Exception {
        assertThat(createVdb().getFile(), is(vdbFile));
    }

    @Test public void shouldExposeNameAsFileName() throws Exception {
        assertThat(createVdb().getName(), is(vdbFile.getFullPath()));
    }

    @Test public void shouldInitiallyBeSynchronized() throws Exception {
        assertThat(createVdb().isSynchronized(), is(true));
    }

    @Test public void shouldInitiallyBeUnmodified() throws Exception {
        assertThat(createVdb().isModified(), is(false));
    }

    @Test public void shouldNeverReturnNullForEntries() throws Exception {
        final Vdb vdb = createVdb();
        assertThat(vdb.getEntries(), notNullValue());
        assertThat(vdb.getModelEntries(), notNullValue());
    }

    @Test public void shouldNotifyAfterChangingDescription() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        final Vdb vdb = createVdb();
        vdb.addChangeListener(listener);
        vdb.setDescription("test");
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(Vdb.DESCRIPTION));
    }

    @Test public void shouldNotNotifyAfterRemovingListener() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        final Vdb vdb = createVdb();
        vdb.addChangeListener(listener);
        vdb.setDescription("test");
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(Vdb.DESCRIPTION));
        vdb.removeChangeListener(listener);
        vdb.setDescription("test1");
        verify(listener).propertyChange(isA(PropertyChangeEvent.class));
    }

    @Test public void shouldNotNotifyAfterSettingDescriptionToSameValue() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        final Vdb vdb = createVdb();
        vdb.addChangeListener(listener);
        vdb.setDescription("test");
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(Vdb.DESCRIPTION));
        vdb.setDescription("test");
        verify(listener).propertyChange(isA(PropertyChangeEvent.class));
    }

    @Test public void shouldNotNotifyIfAlreadySynchronized() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        final Vdb vdb = createVdb();
        vdb.addChangeListener(listener);
        vdb.synchronize(null);
        verify(listener, never()).propertyChange(isA(PropertyChangeEvent.class));
    }

    @Test public void shouldNotRequireMonitorToAddEntry() throws Exception {
        new EclipseMock();
        createVdb().addEntry(mock(IPath.class), null);
    }

    @Test public void shouldNotRequireMonitorToBeCreated() throws Exception {
        createVdb();
    }

    @Test public void shouldNotRequireMonitorToSynchronize() throws Exception {
        createVdb().synchronize(null);
    }

    @Test public void shouldReflectAddedAndRemovedEntries() throws Exception {
        new EclipseMock();
        final Vdb vdb = createVdb();
        final VdbEntry entry = vdb.addEntry(mock(IPath.class), null);
        assertThat(vdb.getEntries().size(), is(1));
        vdb.removeEntry(entry);
        assertThat(vdb.getEntries().isEmpty(), is(true));
    }

    @Test public void shouldReflectChangedDescription() throws Exception {
        final Vdb vdb = createVdb();
        vdb.setDescription("test");
        assertThat(vdb.getDescription(), is("test"));
    }
}
