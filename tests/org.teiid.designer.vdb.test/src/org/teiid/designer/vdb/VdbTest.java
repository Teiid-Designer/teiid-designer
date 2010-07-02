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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.hamcrest.core.IsSame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.core.designer.EclipseMock;

/**
 * 
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( {JAXBContext.class, ResourcesPlugin.class, VdbPlugin.class} )
public class VdbTest {

    private Vdb vdb;
    private EclipseMock eclipseMock;
    @Mock
    private IFile vdbFile;

    @Before
    public void before() throws Exception {
        initMocks(this);

        final IPath vdbPathAbsolute = mock(IPath.class);
        when(vdbFile.getLocation()).thenReturn(vdbPathAbsolute);
        final File vdbFileAbsolute = File.createTempFile(VdbTest.class.getSimpleName(), ".vdb");
        vdbFileAbsolute.deleteOnExit();
        when(vdbPathAbsolute.toFile()).thenReturn(vdbFileAbsolute);
        final IPath vdbPath = mock(IPath.class);
        when(vdbFile.getFullPath()).thenReturn(vdbPath);
        when(vdbFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)).thenReturn(new IMarker[0]);
        final IPath vdbName = mock(IPath.class);
        when(vdbPath.removeFileExtension()).thenReturn(vdbName);

        eclipseMock = new EclipseMock();
        when(eclipseMock.getRoot().findMember(vdbPath)).thenReturn(vdbFile);

        mockStatic(JAXBContext.class);
        final JAXBContext context = PowerMockito.mock(JAXBContext.class);
        when(JAXBContext.newInstance((Class[])anyObject())).thenReturn(context);
        final Marshaller marshaller = mock(Marshaller.class);
        when(context.createMarshaller()).thenReturn(marshaller);

        mockStatic(VdbPlugin.class);
        final VdbPlugin plugin = PowerMockito.mock(VdbPlugin.class);
        when(VdbPlugin.singleton()).thenReturn(plugin);
        final IPath stateFolderPath = mock(IPath.class);
        when(plugin.getStateLocation()).thenReturn(stateFolderPath);
        final IPath vdbFolderPath = mock(IPath.class);
        when(stateFolderPath.append((IPath)anyObject())).thenReturn(vdbFolderPath);
        final File vdbFolderFile = new File(".");
        when(vdbFolderPath.toFile()).thenReturn(vdbFolderFile);
        vdb = new Vdb(vdbFile, null);
    }

    /**
     * @return eclipseMock
     */
    EclipseMock getEclipseMock() {
        return eclipseMock;
    }

    /**
     * @return vdb
     */
    Vdb getVdb() {
        return vdb;
    }

    @Test
    public void shouldBeModifiedWhenDescriptionChanges() throws Exception {
        vdb.setDescription("new description");
        assertThat(vdb.isModified(), is(true));
    }

    @Test
    public void shouldBeModifiedWhenEntryIsAdded() throws Exception {
        vdb.addEntry(mock(IPath.class), null);
        assertThat(vdb.isModified(), is(true));
    }

    @Test
    public void shouldBeSynchronizedAfterAddingEntry() throws Exception {
        vdb.addEntry(mock(IPath.class), null);
        assertThat(vdb.isSynchronized(), is(true));
    }

    @Test
    public void shouldBeUnmodifiedAfterDescriptionChangesFromNullToEmpty() throws Exception {
        vdb.setDescription(" ");
        assertThat(vdb.isModified(), is(false));
    }

    @Test
    public void shouldBeUnmodifiedAfterSave() throws Exception {
        vdb.setDescription("new description");
        vdb.save(null);
        assertThat(vdb.isModified(), is(false));
    }

    @Test
    public void shouldExposeFile() throws Exception {
        assertThat(vdb.getFile(), is(vdbFile));
    }

    @Test
    public void shouldExposeNameAsFileName() throws Exception {
        assertThat(vdb.getName(), is(vdbFile.getFullPath()));
    }

    @Test
    public void shouldInitiallyBeSynchronized() throws Exception {
        assertThat(vdb.isSynchronized(), is(true));
    }

    @Test
    public void shouldInitiallyBeUnmodified() throws Exception {
        assertThat(vdb.isModified(), is(false));
    }

    @Test
    public void shouldNeverReturnNullForEntries() throws Exception {
        assertThat(vdb.getEntries(), notNullValue());
        assertThat(vdb.getModelEntries(), notNullValue());
    }

    @Test
    public void shouldNotifyAfterChangingDescription() throws Exception {
        // set an initial description
        final String oldDescription = "oldDescription";
        vdb.setDescription(oldDescription);

        // hookup listener
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);

        // change description
        final String newDescription = "newDescription";
        vdb.setDescription("newDescription");

        // tests
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(Vdb.DESCRIPTION));
        assertThat((String)arg.getValue().getOldValue(), is(oldDescription));
        assertThat((String)arg.getValue().getNewValue(), is(newDescription));
    }

    @Test
    public void shouldNotNotifyAfterRemovingListener() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);
        vdb.setDescription("test");
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(Vdb.DESCRIPTION));
        vdb.removeChangeListener(listener);
        vdb.setDescription("test1");
        verify(listener).propertyChange(isA(PropertyChangeEvent.class));
    }

    @Test
    public void shouldNotNotifyAfterSettingDescriptionToSameValue() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);
        vdb.setDescription("test");
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(Vdb.DESCRIPTION));
        vdb.setDescription("test");
        verify(listener).propertyChange(isA(PropertyChangeEvent.class));
    }

    @Test
    public void shouldNotNotifyIfAlreadySynchronized() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);
        vdb.synchronize(null);
        verify(listener, never()).propertyChange(isA(PropertyChangeEvent.class));
    }

    @Test
    public void shouldNotRequireMonitorToAddEntry() throws Exception {
        vdb.addEntry(mock(IPath.class), null);
    }

    @Test
    public void shouldNotRequireMonitorToSynchronize() throws Exception {
        vdb.synchronize(null);
    }

    @Test
    public void shouldReflectAddedAndRemovedEntries() throws Exception {
        final VdbEntry entry = vdb.addEntry(mock(IPath.class), null);
        assertThat(vdb.getEntries().size(), is(1));
        vdb.removeEntry(entry);
        assertThat(vdb.getEntries().isEmpty(), is(true));
    }

    @Test
    public void shouldReflectChangedDescription() throws Exception {
        vdb.setDescription("test");
        assertThat(vdb.getDescription(), is("test"));
    }

    @Test
    public void shouldReturnExistingEntryWhenAddingDuplicateEntry() throws Exception {
        final IPath path = new Path("/my/full/path");
        final VdbEntry thisEntry = vdb.addEntry(path, null);
        final VdbEntry thatEntry = vdb.addEntry(path, null);
        assertThat(thatEntry, IsSame.sameInstance(thisEntry));
    }
}
