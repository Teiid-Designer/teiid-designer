/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository.artifact;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.io.InputStream;
import org.junit.Test;
import org.teiid.komodo.repository.RepositoryTest;

/**
 * VDB artifact tests.
 */
public class VdbArtifactTest extends RepositoryTest {

    @SuppressWarnings( "javadoc" )
    @Test
    public void shouldAddVdbWithNoParentPathToRepository() throws Exception {
        final String vdbName = "twitter.vdb"; //$NON-NLS-1$
        final VdbArtifact vdbArtifact = new VdbArtifact(vdbName, null);

        // load VDB
        final String path = ("vdb/" + vdbName); //$NON-NLS-1$
        final InputStream content = getResourceAsStream(path);
        assertNotNull(content);

        // add to the repo
        _repoMgr.persist(vdbArtifact, content);

        // test
        assertThat(_repoMgr.exists(vdbArtifact), is(true));
    }

    @SuppressWarnings( "javadoc" )
    @Test
    public void shouldAddVdbWithParentPathToRepository() throws Exception {
        final String vdbName = "twitter.vdb"; //$NON-NLS-1$
        final VdbArtifact vdbArtifact = new VdbArtifact(vdbName, "MyProject/MyFolder"); //$NON-NLS-1$

        // load VDB
        final String path = ("vdb/" + vdbName); //$NON-NLS-1$
        final InputStream content = getResourceAsStream(path);
        assertNotNull(content);

        // add to the repo
        _repoMgr.persist(vdbArtifact, content);

        // test
        assertThat(_repoMgr.exists(vdbArtifact), is(true));
    }
}
