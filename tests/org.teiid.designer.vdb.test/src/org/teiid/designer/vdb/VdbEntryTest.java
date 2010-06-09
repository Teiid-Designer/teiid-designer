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
import static org.mockito.Mockito.mock;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.core.designer.EclipseMock;
import org.teiid.designer.vdb.VdbEntry.Synchronization;

/**
 * 
 */
@RunWith( PowerMockRunner.class ) @PrepareForTest( {VdbPlugin.class, ResourcesPlugin.class} ) public class VdbEntryTest {

    private Vdb vdb;

    @Before public void before() {
        new EclipseMock();
        vdb = new VdbTest().createVdb();
    }

    @Test public void shouldIndicateSynchronizationNotApplicableIfNotInWorkspace() throws Exception {
        final VdbEntry entry = vdb.addEntry(mock(IPath.class), null);
        assertThat(entry.getSynchronization(), is(Synchronization.NotApplicable));
    }

}
