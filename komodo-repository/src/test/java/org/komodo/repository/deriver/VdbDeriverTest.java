/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.deriver;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import java.io.InputStream;
import org.junit.Test;
import org.komodo.repository.RepositoryTest;
import org.komodo.repository.artifact.Artifact;
import org.komodo.teiid.model.vdb.Translator;
import org.overlord.sramp.ArtifactType;
import org.overlord.sramp.SrampModelUtils;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.UserDefinedArtifactType;

/**
 * A test class for a {@link VdbDeriver}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class VdbDeriverTest extends RepositoryTest {

    @Test
    public void shouldAddTwitterVdb() throws Exception {
        final InputStream vdbStream = VdbDeriverTest.class.getClassLoader().getResourceAsStream("vdb/twitterVdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // verify VDB artifact exists and has correct properties
        final BaseArtifactType vdbArtifact = _repoMgr.addVdb(vdbStream, "twitterVdb.xml");
        assertThat(vdbArtifact, is(instanceOf(UserDefinedArtifactType.class)));
        assertThat(((UserDefinedArtifactType)vdbArtifact).getUserType(), is(Artifact.Type.VDB.getName()));
        assertThat(vdbArtifact.getName(), is("twitter"));
        assertThat(vdbArtifact.getVersion(), is("1"));
        assertThat(vdbArtifact.getDescription(), is("Shows how to call Web Services"));
        assertThat(SrampModelUtils.getCustomProperty(vdbArtifact, "UseConnectorMetadata"), is("cached"));
    }

    @Test
    public void shouldDeriveTwitterVdbArtifacts() throws Exception {
        final InputStream vdbStream = VdbDeriverTest.class.getClassLoader().getResourceAsStream("vdb/twitterVdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // add VDB
        final BaseArtifactType vdbArtifact = _repoMgr.addVdb(vdbStream, "twitterVdb.xml");

        // verify derived artifacts
        assertNumberOfDerivedArtifacts(vdbArtifact, 1);

        // get derived artifacts
        for (ArtifactSummary summary : _repoMgr.getDerivedArtifacts(vdbArtifact)) {
            // these are lightweight artifacts
            ArtifactType artifact = summary.getType();
            assertThat(artifact.getType(), is(Artifact.Type.TRANSLATOR.getName()));

            // materialize entire artifact
            BaseArtifactType translatorArtifact = _repoMgr.get(summary.getUuid());
            assertThat(translatorArtifact.getName(), is("rest"));
            assertThat(SrampModelUtils.getCustomProperty(translatorArtifact, Translator.PropertyName.TYPE), is("ws"));
            assertThat(SrampModelUtils.getCustomProperty(translatorArtifact, "DefaultBinding"), is("HTTP"));
            assertThat(SrampModelUtils.getCustomProperty(translatorArtifact, "DefaultServiceMode"), is("MESSAGE"));
        }
    }

}
