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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.teiid.designer.vdb.Vdb.Event.DESCRIPTION;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.hamcrest.core.IsSame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.designer.util.FileUtils;

/**
 * 
 */
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
        
        /*
         * Vdb uses this tempDir for creating temp archive then uses
        * File.renameTo which seems to require that both files are in
        * the same directory.
        */
        File tempDir = VdbPlugin.singleton().getStateLocation().toFile();
        final File vdbFileAbsolute = File.createTempFile(VdbTest.class.getSimpleName(), ".vdb", tempDir);
        vdbFileAbsolute.deleteOnExit();
        when(vdbPathAbsolute.toFile()).thenReturn(vdbFileAbsolute);
        
        final IPath vdbName = mock(IPath.class);
        when(vdbName.toString()).thenReturn(vdbFileAbsolute.getName());
        when(vdbName.lastSegment()).thenReturn(FileUtils.getFilenameWithoutExtension(vdbFileAbsolute.getName()));
        
        final IPath vdbPath = mock(IPath.class);
        when(vdbPath.getFileExtension()).thenReturn("vdb");
        when(vdbPath.removeFileExtension()).thenReturn(vdbName);
        
        when(vdbFile.getFullPath()).thenReturn(vdbPath);
        when(vdbFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)).thenReturn(new IMarker[0]);

        eclipseMock = new EclipseMock();
        when(eclipseMock.workspaceRoot().findMember(vdbPath)).thenReturn(vdbFile);

        vdb = new Vdb(vdbFile, null);
    }
    
    @After
    public void after() {
        eclipseMock.dispose();
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
        assertThat(arg.getValue().getPropertyName(), is(DESCRIPTION));
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
        assertThat(arg.getValue().getPropertyName(), is(DESCRIPTION));
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
        assertThat(arg.getValue().getPropertyName(), is(DESCRIPTION));
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
    
    @Test
    public void testAddingAndRemovingImportVdbEntry() throws Exception {
    	String entryName = "testImportVdbEntry";
    	
    	vdb.addImportVdb(entryName);
    	
    	Collection<VdbImportVdbEntry> entries = vdb.getImportVdbEntries();
    	assertEquals(1, entries.size());
    	
    	VdbImportVdbEntry entry = entries.iterator().next();
		assertEquals(entryName, entry.getName());
    	
    	vdb.removeImportVdb(entry, null);
    	assertEquals(0, vdb.getImportVdbEntries().size());
    }
}
