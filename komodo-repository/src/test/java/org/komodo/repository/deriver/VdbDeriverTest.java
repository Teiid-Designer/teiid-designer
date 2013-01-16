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
import static org.junit.Assert.fail;
import java.io.InputStream;
import org.junit.Test;
import org.komodo.repository.RepositoryTest;
import org.komodo.repository.artifact.Artifact;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Source;
import org.komodo.teiid.model.vdb.Translator;
import org.overlord.sramp.ArtifactType;
import org.overlord.sramp.SrampModelUtils;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
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
        final QueryResultSet results = _repoMgr.getDerivedArtifacts(vdbArtifact);
        assertThat(results.size(), is(4L));

        // verify derived artifacts
        boolean foundTranslator = false;
        boolean foundPhysicalModel = false;
        boolean foundViewModel = false;
        boolean foundDataSource = false;

        for (final ArtifactSummary summary : results) {
            final ArtifactType artifact = summary.getType(); // lightweight artifact
            final String userType = artifact.getUserType();
            final BaseArtifactType derivedArtifact = _repoMgr.get(summary.getUuid()); // materialize entire artifact
            final String artifactName = derivedArtifact.getName();

            if (!foundTranslator && Artifact.Type.TRANSLATOR.getName().equals(userType)) {
                foundTranslator = true;
                assertThat(artifactName, is("rest"));
                assertPropertyValue(derivedArtifact, Translator.PropertyName.TYPE, "ws");
                assertPropertyValue(derivedArtifact, "DefaultBinding", "HTTP");
                assertPropertyValue(derivedArtifact, "DefaultServiceMode", "MESSAGE");
            } else if (Artifact.Type.SCHEMA.getName().equals(userType)) {
                if (!foundPhysicalModel && "twitter".equals(artifactName)) {
                    foundPhysicalModel = true;
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.TYPE, Schema.Type.PHYSICAL.name());
                } else if (!foundViewModel && "twitterview".equals(artifactName)) {
                    foundViewModel = true;
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.TYPE, Schema.Type.VIRTUAL.name());
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.METADATA_TYPE, Schema.DEFAULT_METADATA_TYPE);
                    final String expected = "\n             CREATE VIRTUAL PROCEDURE getTweets(query varchar) RETURNS (created_on varchar(25), from_user varchar(25), to_user varchar(25),\n"
                                            + "                 profile_image_url varchar(25), source varchar(25), text varchar(140)) AS\n"
                                            + "                select tweet.* from\n"
                                            + "                    (call twitter.invokeHTTP(action => 'GET', endpoint =>querystring('',query as \"q\"))) w,\n"
                                            + "                    XMLTABLE('results' passing JSONTOXML('myxml', w.result) columns\n"
                                            + "                    created_on string PATH 'created_at',\n"
                                            + "                    from_user string PATH 'from_user',\n"
                                            + "                    to_user string PATH 'to_user',\n"
                                            + "                    profile_image_url string PATH 'profile_image_url',\n"
                                            + "                    source string PATH 'source',\n"
                                            + "                    text string PATH 'text') tweet;\n"
                                            + "                CREATE VIEW Tweet AS select * FROM twitterview.getTweets;\n"
                                            + "         ";
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.METADATA, expected);
                } else {
                    fail("unexpected schema artifact '" + artifactName + '\'');
                }
            } else if (!foundDataSource && Artifact.Type.SOURCE.getName().equals(userType)) {
                foundDataSource = true;
                assertThat(artifactName, is("twitter"));
                assertPropertyValue(derivedArtifact, Source.PropertyName.TRANSLATOR_NAME, "rest");
                assertPropertyValue(derivedArtifact, Source.PropertyName.JNDI_NAME, "java:/twitterDS");
            } else {
                fail("unexpected artifact type '" + userType + +'\'');
            }
        }

        assertThat((foundTranslator && foundPhysicalModel && foundViewModel && foundDataSource), is(true));
    }

}
