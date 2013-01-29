/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import java.io.InputStream;
import java.util.HashMap;
import org.junit.Test;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.TranslatorArtifact;
import org.komodo.repository.artifact.VdbArtifact;
import org.overlord.sramp.common.SrampModelUtils;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.ExtendedArtifactType;

/**
 * A test class of a {@link AtomRepositoryManager}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class AtomRepositoryManagerTest extends RepositoryTest {

    private void addArtifacts() throws Exception {
        { // add parser-test-vdb.xml
            final InputStream vdbStream = getResourceAsStream("vdb/parser-test-vdb.xml");
            _repoMgr.addVdb(vdbStream);
        }

        { // twitterVdb.xml
            final InputStream vdbStream = getResourceAsStream("vdb/twitterVdb.xml");
            _repoMgr.addVdb(vdbStream);
        }
    }

    @Test
    public void shouldAddParserTestVdb() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/parser-test-vdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // verify VDB artifact exists and has correct properties
        final BaseArtifactType vdbArtifact = _repoMgr.addVdb(vdbStream);
        assertThat(vdbArtifact, is(instanceOf(ExtendedArtifactType.class)));
        assertThat(((ExtendedArtifactType)vdbArtifact).getExtendedType(), is(VdbArtifact.TYPE.getId()));
        assertThat(vdbArtifact.getName(), is("myVDB"));
        assertThat(vdbArtifact.getVersion(), is("1"));
        assertThat(vdbArtifact.getDescription(), is("vdb description"));
        assertThat(SrampModelUtils.getCustomProperty(vdbArtifact, "vdb-property2"), is("vdb-value2"));
        assertThat(SrampModelUtils.getCustomProperty(vdbArtifact, "vdb-property"), is("vdb-value"));
    }

    @Test
    public void shouldAddTwitterVdb() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/twitterVdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // verify VDB artifact exists and has correct properties
        final BaseArtifactType vdbArtifact = _repoMgr.addVdb(vdbStream);
        assertThat(vdbArtifact, is(instanceOf(ExtendedArtifactType.class)));
        assertThat(((ExtendedArtifactType)vdbArtifact).getExtendedType(), is(VdbArtifact.TYPE.getId()));
        assertThat(vdbArtifact.getName(), is("twitter"));
        assertThat(vdbArtifact.getVersion(), is("1"));
        assertThat(vdbArtifact.getDescription(), is("Shows how to call Web Services"));
        assertThat(SrampModelUtils.getCustomProperty(vdbArtifact, "UseConnectorMetadata"), is("cached"));
    }

    @Test
    public void shouldGetTwitterVdbWhenNameVersionParamsSet() throws Exception {
        addArtifacts();
        final RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        settings.params = new HashMap<String, String>();
        settings.params.put(Artifact.Property.VERSION, "1");
        settings.params.put(Artifact.Property.NAME, "twitter");
        assertThat(_repoMgr.query(settings).size(), is(1L));
    }

    @Test
    public void shouldNotGetResultsWhenNoMatchingVdb() throws Exception {
        addArtifacts();
        final RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        settings.params = new HashMap<String, String>();
        settings.params.put(Artifact.Property.VERSION, "2"); // wrong version
        settings.params.put(Artifact.Property.NAME, "twitter");
        assertThat(_repoMgr.query(settings).size(), is(0L));
    }

    @Test
    public void shouldQueryByVersion() throws Exception {
        addArtifacts();
        final RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        settings.params = new HashMap<String, String>();
        settings.params.put(Artifact.Property.VERSION, "1");
        assertThat(_repoMgr.query(settings).size(), is(2L));
    }

    @Test
    public void shouldQueryWithCustomProperty() throws Exception {
        addArtifacts();
        final RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = TranslatorArtifact.TYPE;
        settings.params = new HashMap<String, String>();
        settings.params.put(Artifact.Property.NAME, "rest");
        settings.params.put("DefaultBinding", "HTTP"); // wrong version
        assertThat(_repoMgr.query(settings).size(), is(1L));
    }

    @Test
    public void whenNoParamsShouldGetAllVdbs() throws Exception {
        addArtifacts();
        final RepositoryManager.QuerySettings settings = new RepositoryManager.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        assertThat(_repoMgr.query(settings).size(), is(2L));
    }
}
