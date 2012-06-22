/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.teiid.designer.vdb.Vdb.Event.ENTRY_CHECKSUM;
import static org.teiid.designer.vdb.Vdb.Event.ENTRY_DESCRIPTION;
import static org.teiid.designer.vdb.Vdb.Event.ENTRY_SYNCHRONIZATION;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.teiid.core.designer.EclipseMock;
import org.teiid.designer.core.ModelResourceMockFactory;
import org.teiid.designer.vdb.VdbEntry.Synchronization;

/**
 * 
 */
public class VdbEntryTest {

    private EclipseMock eclipseMock;
    private VdbEntry entry;
    private Vdb vdb;

    @Before
    public void before() throws Exception {
        final VdbTest vdbTest = new VdbTest();
        vdbTest.before();
        eclipseMock = vdbTest.getEclipseMock();
        vdb = vdbTest.getVdb();
        entry = vdb.addEntry(mock(IPath.class), null);
    }

    @Test
    public void shouldHaveChecksumIfFileInWorkspace() throws Exception {
        // mock file and contents so that checksum can be computed
        File tempFile = ModelResourceMockFactory.createTempFile("temp1", "", null, "abcdef");
        FileInputStream fileInputStream = new FileInputStream(tempFile);
        
        final IPath name = mock(Path.class);
        final IFile iFile = mock(IFile.class);
        when(iFile.getLocation()).thenReturn(name);
        when(iFile.getLocation().toFile()).thenReturn(tempFile);
        when(iFile.getContents()).thenReturn(fileInputStream);

        // put file in workspace
        final IWorkspaceRoot mockRoot = eclipseMock.workspaceRoot();
        when(mockRoot.findMember(name)).thenReturn(iFile);

        // construct entry so that checksum will be computed
        
        
        entry = vdb.addEntry(name, null);
        assertThat(entry.getChecksum(), not(is(0L)));
    }

    @Test
    public void shouldHaveDifferentChecksumIfFileChanges() throws Exception {
        // mock file and contents so that checksum can be computed
        File tempFile1 = ModelResourceMockFactory.createTempFile("temp1", "", null, "abcdefxyz");
        File tempFile2 = ModelResourceMockFactory.createTempFile("temp2", "", null, "xyz");
        FileInputStream fis1 = new FileInputStream(tempFile1);
        FileInputStream fis2 = new FileInputStream(tempFile2);
        
        final IPath name = mock(Path.class);
        final IFile iFile = mock(IFile.class);
        when(iFile.getLocation()).thenReturn(name);
        when(iFile.getLocation().toFile()).thenReturn(tempFile1, tempFile2);
        
        // include values for first call and second call
        when(iFile.getContents()).thenReturn(fis1, fis2);

        // put file in workspace
        final IWorkspaceRoot mockRoot = eclipseMock.workspaceRoot();
        when(mockRoot.findMember(name)).thenReturn(iFile);

        // construct entry so that checksum will be computed
        entry = vdb.addEntry(name, null);
        final long originalChecksum = entry.getChecksum(); // will use first value of iFile.getContents()
        entry.setSynchronization(Synchronization.NotSynchronized); // so that checksum will be recalculated
        entry.synchronize(null); // will use second value of iFile.getContents()

        // test
        assertThat(entry.getChecksum(), not(is(originalChecksum)));
    }
//
//    @Test
//    public void shouldIndicateNotSynchronizedWhenFileIsChanged() throws Exception {
//        // create resource change
//        final IResourceDelta delta = mock(IResourceDelta.class);
//        when(delta.getKind()).thenReturn(IResourceDelta.CHANGED);
//        final IFile file = mock(IFile.class);
//        when(file.getContents()).thenReturn(new ByteArrayInputStream("abcdef".getBytes()));
//        when(delta.getResource()).thenReturn(file);
//
//        // handle event
//        entry.fileChanged(delta);
//
//        // test
//        assertThat(entry.getSynchronization(), is(Synchronization.NotSynchronized));
//    }

    @Test
    public void shouldIndicateSynchronizationNotApplicableIfNotInWorkspace() throws Exception {
        assertThat(entry.getSynchronization(), is(Synchronization.NotApplicable));
    }

    @Test
    public void shouldNotChangeSynchronizationStateWhenDescriptionIsChanged() {
        final Synchronization currentState = entry.getSynchronization();
        assertThat(entry.getSynchronization(), is(Synchronization.NotApplicable)); // check initial state

        // change description
        entry.setDescription("new description");

        // test
        assertThat(entry.getSynchronization(), is(currentState));
    }

    @Test
    public void shouldNotHaveChecksumIfFileNotInWorkspace() {
        assertThat(entry.getChecksum(), is(0L));
    }

    @Test
    public void shouldNotifyAfterChangingEntryDescription() throws Exception {
        // set an initial description
        final String oldDescription = "old description";
        entry.setDescription(oldDescription);

        // hookup listener
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);

        // change description
        final String newDescription = "new description";
        entry.setDescription(newDescription);

        // tests
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(ENTRY_DESCRIPTION));
        assertThat((String)arg.getValue().getOldValue(), is(oldDescription));
        assertThat((String)arg.getValue().getNewValue(), is(newDescription));
    }

    @Test
    public void shouldNotifyAfterChecksumChanges() throws Exception {
        // change checksum by changing file contents and synchronizing
        
        File tempFile1 = ModelResourceMockFactory.createTempFile("temp1", "", null, "abcdef");
        File tempFile2 = ModelResourceMockFactory.createTempFile("temp2", "", null, "xyz");
        FileInputStream fis1 = new FileInputStream(tempFile1);
        FileInputStream fis2 = new FileInputStream(tempFile2);
        
        final IPath name = mock(Path.class);
        final IFile iFile = mock(IFile.class);
        when(iFile.getLocation()).thenReturn(name);
        when(iFile.getLocation().toFile()).thenReturn(tempFile1, tempFile2);
        
        // include values for first call and second call
        when(iFile.getContents()).thenReturn(fis1, fis2);

        // put file in workspace
        final IWorkspaceRoot mockRoot = eclipseMock.workspaceRoot();
        when(mockRoot.findMember(name)).thenReturn(iFile);

        // construct entry so that checksum will be computed
        entry = vdb.addEntry(name, null); // will have an original checksum based on first value of iFile.getContents()
        entry.setSynchronization(Synchronization.NotSynchronized); // so that checksum will be recalculated

        // add listener
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);

        // this will cause event to fire
        entry.synchronize(null); // will use second value of iFile.getContents() to compute checksum

        // tests
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener, times(2)).propertyChange(arg.capture());

        // expect 2 events
        final List<PropertyChangeEvent> values = arg.getAllValues();
        assertThat(values.get(0).getPropertyName(), is(ENTRY_CHECKSUM));
        assertThat(values.get(1).getPropertyName(), is(ENTRY_SYNCHRONIZATION));
    }

    @Test
    public void shouldNotifyAfterSynchronizationChanges() throws Exception {
        final PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);

        // change synchronization
        entry.setSynchronization(Synchronization.Synchronized);

        // tests
        final ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(ENTRY_SYNCHRONIZATION));
    }

    @Test
    public void shouldSetDescriptionToNonNullValue() {
        final String description = "new description";
        entry.setDescription(description);

        // test
        assertThat(entry.getDescription(), is(description));
    }
    
    @Test
    public void shouldSetDescriptionToNullWithEmptyString() {
        final String description = "";
        entry.setDescription(description);

        // test
        assertThat(entry.getDescription(), is(description));
    }

    @Test
    public void shouldVerifyEqualityWhenSamePath() {
        final IPath path = new Path("/my/path/filename");
        final VdbEntry thisEntry = new VdbEntry(vdb, path, null);
        final VdbEntry thatEntry = new VdbEntry(vdb, path, null);
        assertThat(thisEntry.equals(thatEntry), is(true));
        assertThat(thisEntry.hashCode(), is(thatEntry.hashCode()));
    }

}
