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
import java.util.List;
import org.junit.Test;
import org.komodo.repository.RepositoryTest;
import org.komodo.repository.artifact.Artifact;
import org.komodo.teiid.model.vdb.DataPolicy;
import org.komodo.teiid.model.vdb.ImportVdb;
import org.komodo.teiid.model.vdb.Permission;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Source;
import org.komodo.teiid.model.vdb.Translator;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.overlord.sramp.common.SrampModelUtils;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.ExtendedArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.Relationship;

/**
 * A test class for a {@link VdbDeriver}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class VdbDeriverTest extends RepositoryTest {

    @Test
    public void shouldAddParserTestVdb() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/parser-test-vdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // verify VDB artifact exists and has correct properties
        final BaseArtifactType vdbArtifact = _repoMgr.addVdb(vdbStream, "parser-test-vdb.xml");
        assertThat(vdbArtifact, is(instanceOf(ExtendedArtifactType.class)));
        assertThat(((ExtendedArtifactType)vdbArtifact).getExtendedType(), is(Artifact.Type.VDB.getName()));
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
        final BaseArtifactType vdbArtifact = _repoMgr.addVdb(vdbStream, "twitterVdb.xml");
        assertThat(vdbArtifact, is(instanceOf(ExtendedArtifactType.class)));
        assertThat(((ExtendedArtifactType)vdbArtifact).getExtendedType(), is(Artifact.Type.VDB.getName()));
        assertThat(vdbArtifact.getName(), is("twitter"));
        assertThat(vdbArtifact.getVersion(), is("1"));
        assertThat(vdbArtifact.getDescription(), is("Shows how to call Web Services"));
        assertThat(SrampModelUtils.getCustomProperty(vdbArtifact, "UseConnectorMetadata"), is("cached"));
    }

    @Test
    public void shouldDeriveParserTestVdbArtifacts() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/parser-test-vdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // add VDB
        final BaseArtifactType vdbArtifact = _repoMgr.addVdb(vdbStream, "parser-test-vdb.xml");

        // verify derived artifacts
        final QueryResultSet results = _repoMgr.getDerivedArtifacts(vdbArtifact);
        assertThat(results.size(), is(13L));

        // verify derived artifacts
        boolean foundImportVdb = false;
        boolean foundPhysicalModel = false;
        boolean foundSource1 = false;
        boolean foundVirtualModel = false;
        boolean foundSource2 = false;
        boolean foundSource3 = false;
        boolean foundEntry1 = false;
        boolean foundEntry2 = false;
        boolean foundTranslator = false;
        boolean foundDataRole = false;
        boolean foundPermission1 = false;
        boolean foundPermission2 = false;
        boolean foundPermission3 = false;

        String physicalModelUuid = null;
        String physicalSourceTargetUuid = null;

        String virtualModelUuid = null;
        String virtualSource1TargetUuid = null;
        String virtualSource2TargetUuid = null;

        String source1Uuid = null;
        String source1SchemaUuid = null;

        String source2Uuid = null;
        String source2SchemaUuid = null;

        String source3Uuid = null;
        String source3SchemaUuid = null;

        String dataPolicyUuid = null;
        String dataPolicyTarget1Uuid = null;
        String dataPolicyTarget2Uuid = null;
        String dataPolicyTarget3Uuid = null;

        String permission1Uuid = null;
        String permission1TargetUuid = null;

        String permission2Uuid = null;
        String permission2TargetUuid = null;

        String permission3Uuid = null;
        String permission3TargetUuid = null;

        for (final ArtifactSummary summary : results) {
            final ArtifactType artifact = summary.getType(); // lightweight artifact
            final String userType = artifact.getExtendedType();
            final BaseArtifactType derivedArtifact = _repoMgr.get(summary.getUuid()); // materialize entire artifact
            final String artifactName = derivedArtifact.getName();

            if (!foundImportVdb && Artifact.Type.IMPORT_VDB.getName().equals(userType)) {
                foundImportVdb = true;
                assertThat(artifactName, is("x"));
                assertThat(derivedArtifact.getVersion(), is("2"));
                assertPropertyValue(derivedArtifact, ImportVdb.PropertyName.IMPORT_DATA_POLICIES, "true");
                assertThat(derivedArtifact.getProperty().size(), is(1));
            } else if (!foundTranslator && Artifact.Type.TRANSLATOR.getName().equals(userType)) {
                foundTranslator = true;
                assertThat(artifactName, is("oracleOverride"));
                assertThat(derivedArtifact.getDescription(), is("hello world"));
                assertPropertyValue(derivedArtifact, Translator.PropertyName.TYPE, "oracle");
                assertPropertyValue(derivedArtifact, "my-property", "my-value");
                assertThat(derivedArtifact.getProperty().size(), is(2));
            } else if (Artifact.Type.ENTRY.getName().equals(userType)) {
                if (!foundEntry1 && "/path-one".equals(artifactName)) {
                    foundEntry1 = true;
                    assertThat(derivedArtifact.getDescription(), is("path one description"));
                    assertPropertyValue(derivedArtifact, "entryone", "1");
                    assertThat(derivedArtifact.getProperty().size(), is(1));
                } else if (!foundEntry2 && "/path-two".equals(artifactName)) {
                    foundEntry2 = true;
                    assertThat(derivedArtifact.getProperty().isEmpty(), is(true));
                } else {
                    fail("unexpected entry: " + artifactName);
                }
            } else if (Artifact.Type.SCHEMA.getName().equals(userType)) {
                if (!foundPhysicalModel && "model-one".equals(artifactName)) {
                    foundPhysicalModel = true;
                    physicalModelUuid = derivedArtifact.getUuid();

                    assertPropertyValue(derivedArtifact, Schema.PropertyName.TYPE, Schema.Type.PHYSICAL.name());
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.VISIBLE, "false");
                    assertThat(derivedArtifact.getDescription(), is("model description"));
                    assertPropertyValue(derivedArtifact, "model-prop", "model-value-override");

                    // sources and related documents relationships
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(2));

                    if (RelationshipType.SCHEMA_SOURCES.getName().equals(relationships.get(0).getRelationshipType())) {
                        physicalSourceTargetUuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                    } else {
                        physicalSourceTargetUuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                    }
                } else if (!foundVirtualModel && "model-two".equals(artifactName)) {
                    foundVirtualModel = true;
                    virtualModelUuid = derivedArtifact.getUuid();

                    assertPropertyValue(derivedArtifact, Schema.PropertyName.TYPE, Schema.Type.VIRTUAL.name());
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.VISIBLE, "true");
                    assertPropertyValue(derivedArtifact, "model-prop", "model-value");
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.METADATA_TYPE, Schema.DEFAULT_METADATA_TYPE);
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.METADATA, "DDL Here");

                    // sources and related document relationships
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(2));

                    if (RelationshipType.SCHEMA_SOURCES.getName().equals(relationships.get(0).getRelationshipType())) {
                        virtualSource1TargetUuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                        virtualSource2TargetUuid = relationships.get(0).getRelationshipTarget().get(1).getValue();
                    } else {
                        virtualSource1TargetUuid = relationships.get(1).getRelationshipTarget().get(0).getValue();
                        virtualSource2TargetUuid = relationships.get(1).getRelationshipTarget().get(1).getValue();
                    }
                } else {
                    fail("unexpected model: " + artifactName);
                }
            } else if (Artifact.Type.SOURCE.getName().equals(userType)) {
                if (!foundSource1 && "s1".equals(artifactName)) {
                    foundSource1 = true;
                    source1Uuid = derivedArtifact.getUuid();

                    assertPropertyValue(derivedArtifact, Source.PropertyName.TRANSLATOR_NAME, "translator");
                    assertPropertyValue(derivedArtifact, Source.PropertyName.JNDI_NAME, "java:binding-one");
                    assertThat(derivedArtifact.getProperty().size(), is(2));

                    // schema and related document relationships
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(2));

                    if (RelationshipType.SOURCE_SCHEMA.getName().equals(relationships.get(0).getRelationshipType())) {
                        source1SchemaUuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                    } else {
                        source1SchemaUuid = relationships.get(1).getRelationshipTarget().get(0).getValue();
                    }
                } else if (!foundSource2 && "s2".equals(artifactName)) {
                    foundSource2 = true;
                    source2Uuid = derivedArtifact.getUuid();

                    assertPropertyValue(derivedArtifact, Source.PropertyName.TRANSLATOR_NAME, "translator");
                    assertPropertyValue(derivedArtifact, Source.PropertyName.JNDI_NAME, "java:binding-two");
                    assertThat(derivedArtifact.getProperty().size(), is(2));

                    // schema and related document relationships
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(2));

                    if (RelationshipType.SOURCE_SCHEMA.getName().equals(relationships.get(0).getRelationshipType())) {
                        source2SchemaUuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                    } else {
                        source2SchemaUuid = relationships.get(1).getRelationshipTarget().get(0).getValue();
                    }
                } else if (!foundSource3 && "s3".equals(artifactName)) {
                    foundSource3 = true;
                    source3Uuid = derivedArtifact.getUuid();

                    assertPropertyValue(derivedArtifact, Source.PropertyName.TRANSLATOR_NAME, "translator");
                    assertPropertyValue(derivedArtifact, Source.PropertyName.JNDI_NAME, "java:mybinding");
                    assertThat(derivedArtifact.getProperty().size(), is(2));

                    // schema and related document relationships
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(2));

                    if (RelationshipType.SOURCE_SCHEMA.getName().equals(relationships.get(0).getRelationshipType())) {
                        source3SchemaUuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                    } else {
                        source3SchemaUuid = relationships.get(1).getRelationshipTarget().get(0).getValue();
                    }
                } else {
                    fail("unexpected source: " + artifactName);
                }
            } else if (!foundDataRole && Artifact.Type.DATA_POLICY.getName().equals(userType)) {
                foundDataRole = true;
                dataPolicyUuid = derivedArtifact.getUuid();

                assertThat(artifactName, is("roleOne"));
                assertThat(derivedArtifact.getDescription(), is("roleOne described"));
                assertPropertyValue(derivedArtifact, DataPolicy.PropertyName.ANY_AUTHENTICATED, "false");
                assertPropertyValue(derivedArtifact, DataPolicy.PropertyName.TEMP_TABLE_CREATABLE, "true");
                assertPropertyValue(derivedArtifact, DataPolicy.PropertyName.ROLE_NAMES, "ROLE1,ROLE2");
                assertThat(derivedArtifact.getProperty().size(), is(3));

                // permissions and related document relationship
                final List<Relationship> relationships = derivedArtifact.getRelationship();
                assertThat(relationships.size(), is(2));

                if (RelationshipType.DATA_POLICY_PERMISSIONS.getName().equals(relationships.get(0).getRelationshipType())) {
                    dataPolicyTarget1Uuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                    dataPolicyTarget2Uuid = relationships.get(0).getRelationshipTarget().get(1).getValue();
                    dataPolicyTarget3Uuid = relationships.get(0).getRelationshipTarget().get(2).getValue();
                } else {
                    dataPolicyTarget1Uuid = relationships.get(1).getRelationshipTarget().get(0).getValue();
                    dataPolicyTarget2Uuid = relationships.get(1).getRelationshipTarget().get(1).getValue();
                    dataPolicyTarget3Uuid = relationships.get(1).getRelationshipTarget().get(2).getValue();
                }
            } else if (Artifact.Type.PERMISSION.getName().equals(userType)) {
                if (!foundPermission1 && "myTable.T1".equals(artifactName)) {
                    foundPermission1 = true;
                    permission1Uuid = derivedArtifact.getUuid();

                    assertPropertyValue(derivedArtifact, Permission.PropertyName.READABLE, "true");
                    assertThat(derivedArtifact.getProperty().size(), is(1));

                    // data policy and related document relationships
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(2));

                    if (RelationshipType.PERMISSION_DATA_POLICY.getName().equals(relationships.get(0).getRelationshipType())) {
                        permission1TargetUuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                    } else {
                        permission1TargetUuid = relationships.get(1).getRelationshipTarget().get(0).getValue();
                    }
                } else if (!foundPermission2 && "myTable.T2".equals(artifactName)) {
                    foundPermission2 = true;
                    permission2Uuid = derivedArtifact.getUuid();

                    assertPropertyValue(derivedArtifact, Permission.PropertyName.CREATABLE, "true");
                    assertPropertyValue(derivedArtifact, Permission.PropertyName.READABLE, "false");
                    assertPropertyValue(derivedArtifact, Permission.PropertyName.UPDATABLE, "true");
                    assertPropertyValue(derivedArtifact, Permission.PropertyName.DELETABLE, "true");
                    assertPropertyValue(derivedArtifact, Permission.PropertyName.EXECUTABLE, "true");
                    assertPropertyValue(derivedArtifact, Permission.PropertyName.ALTERABLE, "true");
                    assertPropertyValue(derivedArtifact, Permission.PropertyName.CONDITION, "col1 = user()");
                    assertThat(derivedArtifact.getProperty().size(), is(7));

                    // data policy and related document relationships
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(2));

                    if (RelationshipType.PERMISSION_DATA_POLICY.getName().equals(relationships.get(0).getRelationshipType())) {
                        permission2TargetUuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                    } else {
                        permission2TargetUuid = relationships.get(1).getRelationshipTarget().get(0).getValue();
                    }
                } else if (!foundPermission3 && "javascript".equals(artifactName)) {
                    foundPermission3 = true;
                    permission3Uuid = derivedArtifact.getUuid();

                    assertPropertyValue(derivedArtifact, Permission.PropertyName.LANGUAGABLE, "true");
                    assertThat(derivedArtifact.getProperty().size(), is(1));

                    // data policy and related document relationships
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(2));

                    if (RelationshipType.PERMISSION_DATA_POLICY.getName().equals(relationships.get(0).getRelationshipType())) {
                        permission3TargetUuid = relationships.get(0).getRelationshipTarget().get(0).getValue();
                    } else {
                        permission3TargetUuid = relationships.get(1).getRelationshipTarget().get(0).getValue();
                    }
                } else {
                    fail("unexpected permission: " + artifactName);
                }
            } else {
                fail("unexpected artifact: " + artifactName);
            }
        }

        assertThat("foundImportVdb=" + foundImportVdb + ", foundPhysicalModel=" + foundPhysicalModel + ", foundSource1="
                   + foundSource1 + ", foundVirtualModel=" + foundVirtualModel + ", foundSource2=" + foundSource2
                   + ", foundSource3=" + foundSource3 + ", foundEntry1" + foundEntry1 + ", foundEntry2=" + foundEntry2
                   + ", foundTranslator=" + foundTranslator + ", foundDataRole=" + foundDataRole + ", foundPermission1="
                   + foundPermission1 + ", foundPermission2=" + foundPermission2 + ", foundPermission3=" + foundPermission3,
                   (foundImportVdb && foundPhysicalModel && foundSource1 && foundVirtualModel && foundSource2 && foundSource3
                    && foundEntry1 && foundEntry2 && foundTranslator && foundDataRole && foundPermission1 && foundPermission2 && foundPermission3),
                   is(true));

        // relationships
        assertThat(physicalSourceTargetUuid, is(source3Uuid));
        assertThat(virtualSource1TargetUuid, is(source1Uuid));
        assertThat(virtualSource2TargetUuid, is(source2Uuid));
        assertThat(source3SchemaUuid, is(physicalModelUuid));
        assertThat(source1SchemaUuid, is(virtualModelUuid));
        assertThat(source2SchemaUuid, is(virtualModelUuid));
        assertThat(permission1TargetUuid, is(dataPolicyUuid));
        assertThat(permission2TargetUuid, is(dataPolicyUuid));
        assertThat(permission3TargetUuid, is(dataPolicyUuid));
        assertThat(dataPolicyTarget1Uuid, is(permission1Uuid));
        assertThat(dataPolicyTarget2Uuid, is(permission2Uuid));
        assertThat(dataPolicyTarget3Uuid, is(permission3Uuid));
    }

    @Test
    public void shouldDeriveTwitterVdbArtifacts() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/twitterVdb.xml");
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
            final String userType = artifact.getExtendedType();
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
