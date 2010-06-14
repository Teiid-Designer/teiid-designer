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
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.core.designer.EclipseMock;
import org.teiid.designer.vdb.VdbEntry.Synchronization;
import com.metamatrix.core.modeler.util.FileUtils;

/**
 * 
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( {VdbPlugin.class, ResourcesPlugin.class, FileUtils.class} )
public class VdbEntryTest {

    private EclipseMock eclipseMock;
    private VdbEntry entry;
    private Vdb vdb;

    @Before
    public void before() {
        eclipseMock = new EclipseMock();
        vdb = new VdbTest().createVdb();
        entry = vdb.addEntry(mock(IPath.class), null);
    }

    @Test
    public void shouldHaveChecksumIfFileInWorkspace() throws Exception {
        mockStatic(FileUtils.class); // so files aren't copied

        // mock file and contents so that checksum can be computed
        IPath name = mock(Path.class);
        IFile iFile = mock(IFile.class);
        when(iFile.getLocation()).thenReturn(name);
        when(iFile.getContents()).thenReturn(new ByteArrayInputStream("abcdef".getBytes()));

        // put file in workspace
        IWorkspaceRoot mockRoot = eclipseMock.getRoot();
        when(mockRoot.findMember(name)).thenReturn(iFile);

        // construct entry so that checksum will be computed
        entry = vdb.addEntry(name, null);
        assertThat(entry.getChecksum(), not(is(0L)));
    }

    @Test
    public void shouldHaveDifferentChecksumIfFileChanges() throws Exception {
        mockStatic(FileUtils.class); // so files aren't copied

        // mock file and contents so that checksum can be computed
        IPath name = mock(Path.class);
        IFile iFile = mock(IFile.class);
        when(iFile.getLocation()).thenReturn(name);

        // include values for first call and second call
        when(iFile.getContents()).thenReturn(new ByteArrayInputStream("abcdef".getBytes()),
                                             new ByteArrayInputStream("xyz".getBytes()));

        // put file in workspace
        IWorkspaceRoot mockRoot = eclipseMock.getRoot();
        when(mockRoot.findMember(name)).thenReturn(iFile);

        // construct entry so that checksum will be computed
        entry = vdb.addEntry(name, null);
        long originalChecksum = entry.getChecksum(); // will use first value of iFile.getContents()
        entry.setSynchronization(Synchronization.NotSynchronized); // so that checksum will be recalculated
        entry.synchronize(null); // will use second value of iFile.getContents()

        // test
        assertThat(entry.getChecksum(), not(is(originalChecksum)));
    }

    @Test
    public void shouldIndicateNotSynchronizedWhenFileIsChanged() throws Exception {
        // create resource change
        IResourceDelta delta = mock(IResourceDelta.class);
        when(delta.getKind()).thenReturn(IResourceDelta.CHANGED);
        IFile file = mock(IFile.class);
        when(file.getContents()).thenReturn(new ByteArrayInputStream("abcdef".getBytes()));
        when(delta.getResource()).thenReturn(file);

        // handle event
        entry.fileChanged(delta);

        // test
        assertThat(entry.getSynchronization(), is(Synchronization.NotSynchronized));
    }

    @Test
    public void shouldIndicateSynchronizationNotApplicableIfNotInWorkspace() throws Exception {
        assertThat(entry.getSynchronization(), is(Synchronization.NotApplicable));
    }

    @Test
    public void shouldNotChangeSynchronizationStateWhenDescriptionIsChanged() {
        Synchronization currentState = entry.getSynchronization();
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
        String oldDescription = "old description";
        entry.setDescription(oldDescription);
        
        // hookup listener
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);

        // change description
        String newDescription = "new description";
        entry.setDescription(newDescription);

        // tests
        ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(Vdb.ENTRY_DESCRIPTION));
        assertThat((String)arg.getValue().getOldValue(), is(oldDescription));
        assertThat((String)arg.getValue().getNewValue(), is(newDescription));        
    }

    @Test
    public void shouldNotifyAfterChecksumChanges() throws Exception {
        mockStatic(FileUtils.class); // so files aren't copied

        // change checksum by changing file contents and synchronizing
        IPath name = mock(Path.class);
        IFile iFile = mock(IFile.class);
        when(iFile.getLocation()).thenReturn(name);

        // include values for first call and second call
        when(iFile.getContents()).thenReturn(new ByteArrayInputStream("abcdef".getBytes()),
                                             new ByteArrayInputStream("xyz".getBytes()));

        // put file in workspace
        IWorkspaceRoot mockRoot = eclipseMock.getRoot();
        when(mockRoot.findMember(name)).thenReturn(iFile);

        // construct entry so that checksum will be computed
        entry = vdb.addEntry(name, null); // will have an original checksum based on first value of iFile.getContents()
        entry.setSynchronization(Synchronization.NotSynchronized); // so that checksum will be recalculated

        // add listener
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);

        // this will cause event to fire
        entry.synchronize(null); // will use second value of iFile.getContents() to compute checksum

        // tests
        ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener, times(2)).propertyChange(arg.capture());

        // expect 2 events
        List<PropertyChangeEvent> values = arg.getAllValues();
        assertThat(values.get(0).getPropertyName(), is(Vdb.ENTRY_CHECKSUM));
        assertThat(values.get(1).getPropertyName(), is(Vdb.ENTRY_SYNCHRONIZATION));
    }

    @Test
    public void shouldNotifyAfterSynchronizationChanges() throws Exception {
        PropertyChangeListener listener = mock(PropertyChangeListener.class);
        vdb.addChangeListener(listener);

        // change synchronization
        entry.setSynchronization(Synchronization.Synchronized);

        // tests
        ArgumentCaptor<PropertyChangeEvent> arg = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(arg.capture());
        assertThat(arg.getValue().getPropertyName(), is(Vdb.ENTRY_SYNCHRONIZATION));
    }

    @Test
    public void shouldSetDescriptionToNonNullValue() {
        String description = "new description";
        entry.setDescription(description);

        // test
        assertThat(entry.getDescription(), is(description));
    }

    @Test
    public void shouldVerifyEqualityWhenSamePath() {
        IPath path = new Path("/my/path/filename");
        VdbEntry thisEntry = new VdbEntry(vdb, path, null);
        VdbEntry thatEntry = new VdbEntry(vdb, path, null);
        assertThat(thisEntry.equals(thatEntry), is(true));
        assertThat(thisEntry.hashCode(), is(thatEntry.hashCode()));
    }

}
