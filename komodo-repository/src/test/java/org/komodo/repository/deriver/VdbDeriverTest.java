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
import org.komodo.repository.AtomRepositoryTest;
import org.komodo.repository.artifact.Artifact;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.UserDefinedArtifactType;

/**
 * A test class for a {@link VdbDeriver}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class VdbDeriverTest extends AtomRepositoryTest {

    @Test
    public void shouldDeriveTwitterVdb() throws Exception {
        final InputStream vdbStream = VdbDeriverTest.class.getClassLoader().getResourceAsStream("vdb/twitterVdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        BaseArtifactType artifact = getRepositoryManager().addVdb(vdbStream, "twitterVdb.xml");
        assertThat(artifact, is(instanceOf(UserDefinedArtifactType.class)));
        assertThat(((UserDefinedArtifactType)artifact).getUserType(), is(Artifact.Type.VDB.getName()));
    }
    //
    //    private static void queryForDerivedArtifacts(final SrampAtomApiClient client,
    //                                                 final String vdbArtifactUuid) throws Exception {
    //        // Check that we can query for the VDB artifact
    //        String query = String.format("/s-ramp/user/" + VdbArtifact.ARTIFACT_TYPE + "[@uuid = '%1$s']", vdbArtifactUuid);
    //        QueryResultSet resultSet = client.query(query);
    //
    //        if (resultSet.size() != 1) {
    //            throw new Exception("Error querying for vdb/twitterVdb.xml artifact!");
    //        }
    //
    //        // Find all derived artifacts by querying by the relatedDocument relationship
    //        query = String.format("/s-ramp/user[relatedDocument[@uuid = '%1$s']]", vdbArtifactUuid);
    //        resultSet = client.query(query);
    //
    //        if (resultSet.size() != 12) {
    //            throw new Exception("Expected 12 derived artifacts but found only " + resultSet.size() + "!");
    //        }
    //
    //        for (final ArtifactSummary arty : resultSet) {
    //            final ArtifactType type = arty.getType();
    //            System.out.println("\t" + type.getType() + " (" + arty.getName() + ")");
    //        }
    //    }
    //
    //    @Test
    //    public void testDeriver() throws Exception {
    //        final SrampAtomApiClient client = new SrampAtomApiClient(generateURL("/s-ramp"));
    //
    //        // First, add the demo/sample web.xml to the repository.
    //        final String uuid = addVdbToRepository(client);
    //
    //        // Now, do some queries to make sure that the derived artifacts were properly
    //        // created and linked (via S-RAMP relationships) to the original artifact.
    //        queryForDerivedArtifacts(client, uuid);
    //    }

}
