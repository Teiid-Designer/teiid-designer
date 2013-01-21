/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.io.InputStream;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.komodo.repository.artifact.Artifact;

/**
 * A test class of a {@link AtomRepositoryManager}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class AtomRepositoryManagerTest extends RepositoryTest {

    @Before
    public void addArtifacts() throws Exception {
        { // add parser-test-vdb.xml
            final InputStream vdbStream = getResourceAsStream("vdb/parser-test-vdb.xml");
            _repoMgr.addVdb(vdbStream, "parser-test-vdb.xml");
        }

        { // twitterVdb.xml
            final InputStream vdbStream = getResourceAsStream("vdb/twitterVdb.xml");
            _repoMgr.addVdb(vdbStream, "twitterVdb.xml");
        }
    }

    @Test
    public void shouldQueryByVersion() throws Exception {
        RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = Artifact.Type.VDB;
        settings.params = new HashMap<String, String>();
        settings.params.put("version", "1");
        assertThat(_repoMgr.query(settings).size(), is(2));
    }

    @Test
    public void whenNoParamsShouldGetAllVdbs() throws Exception {
        RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = Artifact.Type.VDB;
        assertThat(_repoMgr.query(settings).size(), is(2));
    }

    @Test
    public void shouldGetTwitterVdbWhenNameVersionParamsSet() throws Exception {
        RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = Artifact.Type.VDB;
        settings.params = new HashMap<String, String>();
        settings.params.put("version", "1");
        settings.params.put("name", "twitter");
        assertThat(_repoMgr.query(settings).size(), is(1));
    }

    @Test
    public void shouldNotGetResultsWhenNoMatchingVdb() throws Exception {
        RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = Artifact.Type.VDB;
        settings.params = new HashMap<String, String>();
        settings.params.put("version", "2"); // wrong version
        settings.params.put("name", "twitter");
        assertThat(_repoMgr.query(settings).size(), is(0));
    }

    @Test
    public void shouldQueryWithCustomProperty() throws Exception {
        RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = Artifact.Type.TRANSLATOR;
        settings.params = new HashMap<String, String>();
        settings.params.put("name", "rest");
        settings.params.put("DefaultBinding", "HTTP"); // wrong version
        assertThat(_repoMgr.query(settings).size(), is(1));
    }
}
