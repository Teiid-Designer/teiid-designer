/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import java.io.InputStream;
import org.junit.Test;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.teiid.TranslatorArtifact;
import org.komodo.repository.artifact.teiid.VdbArtifact;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.ExtendedArtifactType;

/**
 * A test class of a {@link SrampRepository}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class SrampRepositoryTest extends SrampTest {

    private void addArtifacts() throws Exception {
        { // add parser-test-vdb.xml
            final InputStream vdbStream = getResourceAsStream("vdb/parser-test-vdb.xml");
            _repository.add(vdbStream, VdbArtifact.TYPE);
        }

        { // twitterVdb.xml
            final InputStream vdbStream = getResourceAsStream("vdb/twitterVdb.xml");
            _repository.add(vdbStream, VdbArtifact.TYPE);
        }
    }

    @Test
    public void shouldAddParserTestVdb() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/parser-test-vdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // verify VDB artifact exists and has correct properties
        final Artifact artifact = _repository.add(vdbStream, VdbArtifact.TYPE);
        assertThat(artifact, is(instanceOf(VdbArtifact.class)));

        final VdbArtifact vdbArtifact = (VdbArtifact)artifact;
        assertSrampArtifact(vdbArtifact);

        final BaseArtifactType srampArtifact = ((SrampArtifact)vdbArtifact).getDelegate();
        assertThat(srampArtifact, is(instanceOf(ExtendedArtifactType.class)));

        assertThat(((ExtendedArtifactType)srampArtifact).getExtendedType(), is(VdbArtifact.TYPE.getId()));
        assertThat(vdbArtifact.getArtifactName(), is("myVDB"));
        assertThat(vdbArtifact.getArtifactVersion(), is("1"));
        assertThat(vdbArtifact.getDescription(), is("vdb description"));
        assertThat(vdbArtifact.getProperty("vdb-property2"), is("vdb-value2"));
        assertThat(vdbArtifact.getProperty("vdb-property"), is("vdb-value"));
    }

    @Test
    public void shouldAddTwitterVdb() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/twitterVdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // verify VDB artifact exists and has correct properties
        final Artifact artifact = _repository.add(vdbStream, VdbArtifact.TYPE);
        assertThat(artifact, is(instanceOf(VdbArtifact.class)));

        final VdbArtifact vdbArtifact = (VdbArtifact)artifact;
        assertSrampArtifact(vdbArtifact);

        final BaseArtifactType srampArtifact = ((SrampArtifact)vdbArtifact).getDelegate();
        assertThat(srampArtifact, is(instanceOf(ExtendedArtifactType.class)));

        assertThat(((ExtendedArtifactType)srampArtifact).getExtendedType(), is(VdbArtifact.TYPE.getId()));
        assertThat(vdbArtifact.getArtifactName(), is("twitter"));
        assertThat(vdbArtifact.getArtifactVersion(), is("1"));
        assertThat(vdbArtifact.getDescription(), is("Shows how to call Web Services"));
        assertThat(vdbArtifact.getProperty("UseConnectorMetadata"), is("cached"));
    }

    @Test
    public void shouldGetTwitterVdbWhenNameVersionParamsSet() throws Exception {
        addArtifacts();
        final SrampRepository.QuerySettings settings = new SrampRepository.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        settings.params.put(Artifact.Property.VERSION, "1");
        settings.params.put(Artifact.Property.NAME, "twitter");
        assertThat(_repository.query(settings).size(), is(1));
    }

    @Test
    public void shouldNotGetResultsWhenNoMatchingVdb() throws Exception {
        addArtifacts();
        final SrampRepository.QuerySettings settings = new SrampRepository.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        settings.params.put(Artifact.Property.VERSION, "2"); // wrong version
        settings.params.put(Artifact.Property.NAME, "twitter");
        assertThat(_repository.query(settings).size(), is(0));
    }

    @Test
    public void shouldQueryByVersion() throws Exception {
        addArtifacts();
        final SrampRepository.QuerySettings settings = new SrampRepository.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        settings.params.put(Artifact.Property.VERSION, "1");
        assertThat(_repository.query(settings).size(), is(2));
    }

    @Test
    public void shouldQueryWithCustomProperty() throws Exception {
        addArtifacts();
        final SrampRepository.QuerySettings settings = new SrampRepository.QuerySettings();
        settings.artifactType = TranslatorArtifact.TYPE;
        settings.params.put(Artifact.Property.NAME, "rest");
        settings.params.put("DefaultBinding", "HTTP"); // wrong version
        assertThat(_repository.query(settings).size(), is(1));
    }

    @Test
    public void whenNoParamsShouldGetAllVdbs() throws Exception {
        addArtifacts();
        final SrampRepository.QuerySettings settings = new SrampRepository.QuerySettings();
        settings.artifactType = VdbArtifact.TYPE;
        assertThat(_repository.query(settings).size(), is(2));
    }
}
