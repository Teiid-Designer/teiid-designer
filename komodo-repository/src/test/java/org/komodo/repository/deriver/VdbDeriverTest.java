/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.deriver;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;
import org.komodo.repository.RepositoryTest;
import org.komodo.repository.artifact.DataPolicyArtifact;
import org.komodo.repository.artifact.EntryArtifact;
import org.komodo.repository.artifact.ImportVdbArtifact;
import org.komodo.repository.artifact.PermissionArtifact;
import org.komodo.repository.artifact.SchemaArtifact;
import org.komodo.repository.artifact.SourceArtifact;
import org.komodo.repository.artifact.TranslatorArtifact;
import org.komodo.repository.artifact.VdbArtifact;
import org.komodo.teiid.model.vdb.DataPolicy;
import org.komodo.teiid.model.vdb.ImportVdb;
import org.komodo.teiid.model.vdb.Permission;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Source;
import org.komodo.teiid.model.vdb.Translator;
import org.overlord.sramp.client.query.ArtifactSummary;
import org.overlord.sramp.client.query.QueryResultSet;
import org.overlord.sramp.common.ArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.Relationship;

/**
 * A test class for a {@link VdbDeriver}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class VdbDeriverTest extends RepositoryTest {

    @Test
    public void shouldDeriveParserTestVdbArtifacts() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/parser-test-vdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // add VDB
        final BaseArtifactType vdbArtifact = _repoMgr.addVdb(vdbStream);

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

        // hold on to artifacts to test relationships
        BaseArtifactType physicalModel = null;
        BaseArtifactType virtualModel = null;
        BaseArtifactType source1 = null;
        BaseArtifactType source2 = null;
        BaseArtifactType source3 = null;
        BaseArtifactType dataPolicy = null;
        BaseArtifactType permission1 = null;
        BaseArtifactType permission2 = null;
        BaseArtifactType permission3 = null;

        for (final ArtifactSummary summary : results) {
            final ArtifactType artifact = summary.getType(); // lightweight artifact
            final String userType = artifact.getExtendedType();
            final BaseArtifactType derivedArtifact = _repoMgr.get(summary.getUuid()); // materialize entire artifact
            final String artifactName = derivedArtifact.getName();

            if (!foundImportVdb && ImportVdbArtifact.TYPE.getId().equals(userType)) {
                foundImportVdb = true;
                assertThat(artifactName, is("x"));
                assertThat(derivedArtifact.getVersion(), is("2"));
                assertPropertyValue(derivedArtifact, ImportVdb.PropertyName.IMPORT_DATA_POLICIES, "true");
                assertThat(derivedArtifact.getProperty().size(), is(1));

                // related documents relationship
                final List<Relationship> relationships = derivedArtifact.getRelationship();
                assertThat(relationships.size(), is(1));

                // make sure VDB contains import VDB
                assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, derivedArtifact.getUuid());
            } else if (!foundTranslator && TranslatorArtifact.TYPE.getId().equals(userType)) {
                foundTranslator = true;
                assertThat(artifactName, is("oracleOverride"));
                assertThat(derivedArtifact.getDescription(), is("hello world"));
                assertThat(derivedArtifact.getProperty().size(), is(2));
                assertPropertyValue(derivedArtifact, Translator.PropertyName.TYPE, "oracle");
                assertPropertyValue(derivedArtifact, "my-property", "my-value");

                // related documents relationship
                final List<Relationship> relationships = derivedArtifact.getRelationship();
                assertThat(relationships.size(), is(1));

                // make sure VDB contains translator
                assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, derivedArtifact.getUuid());
            } else if (EntryArtifact.TYPE.getId().equals(userType)) {
                if (!foundEntry1 && "/path-one".equals(artifactName)) {
                    foundEntry1 = true;
                    assertThat(derivedArtifact.getDescription(), is("path one description"));
                    assertPropertyValue(derivedArtifact, "entryone", "1");
                    assertThat(derivedArtifact.getProperty().size(), is(1));

                    // related documents relationship
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(1));

                    // make sure VDB contains entry
                    assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, derivedArtifact.getUuid());
                } else if (!foundEntry2 && "/path-two".equals(artifactName)) {
                    foundEntry2 = true;
                    assertThat(derivedArtifact.getProperty().isEmpty(), is(true));

                    // related documents relationship
                    final List<Relationship> relationships = derivedArtifact.getRelationship();
                    assertThat(relationships.size(), is(1));

                    // make sure VDB contains entry
                    assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, derivedArtifact.getUuid());
                } else {
                    fail("unexpected entry: " + artifactName);
                }
            } else if (SchemaArtifact.TYPE.getId().equals(userType)) {
                if (!foundPhysicalModel && "model-one".equals(artifactName)) {
                    foundPhysicalModel = true;
                    physicalModel = derivedArtifact;

                    assertPropertyValue(physicalModel, Schema.PropertyName.TYPE, Schema.Type.PHYSICAL.name());
                    assertPropertyValue(physicalModel, Schema.PropertyName.VISIBLE, "false");
                    assertThat(physicalModel.getDescription(), is("model description"));
                    assertPropertyValue(physicalModel, "model-prop", "model-value-override");

                    // sources and related documents relationships
                    final List<Relationship> relationships = physicalModel.getRelationship();
                    assertThat(relationships.size(), is(2));

                    // make sure VDB contains physical model
                    assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, physicalModel.getUuid());
                } else if (!foundVirtualModel && "model-two".equals(artifactName)) {
                    foundVirtualModel = true;
                    virtualModel = derivedArtifact;

                    assertPropertyValue(virtualModel, Schema.PropertyName.TYPE, Schema.Type.VIRTUAL.name());
                    assertPropertyValue(virtualModel, Schema.PropertyName.VISIBLE, "true");
                    assertPropertyValue(virtualModel, "model-prop", "model-value");
                    assertPropertyValue(virtualModel, Schema.PropertyName.METADATA_TYPE, Schema.DEFAULT_METADATA_TYPE.name());
                    assertPropertyValue(virtualModel, Schema.PropertyName.METADATA, "DDL Here");

                    // sources and related document relationships
                    final List<Relationship> relationships = virtualModel.getRelationship();
                    assertThat(relationships.size(), is(2));

                    // make sure VDB contains virtual model
                    assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, virtualModel.getUuid());
                } else {
                    fail("unexpected model: " + artifactName);
                }
            } else if (SourceArtifact.TYPE.getId().equals(userType)) {
                if (!foundSource1 && "s1".equals(artifactName)) {
                    foundSource1 = true;
                    source1 = derivedArtifact;

                    assertPropertyValue(source1, Source.PropertyName.TRANSLATOR_NAME, "translator");
                    assertPropertyValue(source1, Source.PropertyName.JNDI_NAME, "java:binding-one");
                    assertThat(source1.getProperty().size(), is(2));

                    // schema and related document relationships
                    final List<Relationship> relationships = source1.getRelationship();
                    assertThat(relationships.size(), is(2));
                } else if (!foundSource2 && "s2".equals(artifactName)) {
                    foundSource2 = true;
                    source2 = derivedArtifact;

                    assertPropertyValue(source2, Source.PropertyName.TRANSLATOR_NAME, "translator");
                    assertPropertyValue(source2, Source.PropertyName.JNDI_NAME, "java:binding-two");
                    assertThat(source2.getProperty().size(), is(2));

                    // schema and related document relationships
                    final List<Relationship> relationships = source2.getRelationship();
                    assertThat(relationships.size(), is(2));
                } else if (!foundSource3 && "s3".equals(artifactName)) {
                    foundSource3 = true;
                    source3 = derivedArtifact;

                    assertPropertyValue(source3, Source.PropertyName.TRANSLATOR_NAME, "translator");
                    assertPropertyValue(source3, Source.PropertyName.JNDI_NAME, "java:mybinding");
                    assertThat(source3.getProperty().size(), is(2));

                    // schema and related document relationships
                    final List<Relationship> relationships = source3.getRelationship();
                    assertThat(relationships.size(), is(2));
                } else {
                    fail("unexpected source: " + artifactName);
                }
            } else if (!foundDataRole && DataPolicyArtifact.TYPE.getId().equals(userType)) {
                foundDataRole = true;
                dataPolicy = derivedArtifact;

                assertThat(artifactName, is("roleOne"));
                assertThat(dataPolicy.getDescription(), is("roleOne described"));
                assertPropertyValue(dataPolicy, DataPolicy.PropertyName.ANY_AUTHENTICATED, "false");
                assertPropertyValue(dataPolicy, DataPolicy.PropertyName.TEMP_TABLE_CREATABLE, "true");
                assertPropertyValue(dataPolicy, DataPolicy.PropertyName.ROLE_NAMES, "ROLE1,ROLE2");
                assertThat(dataPolicy.getProperty().size(), is(3));
                assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, dataPolicy.getUuid());

                // permissions and related document relationships
                final List<Relationship> relationships = dataPolicy.getRelationship();
                assertThat(relationships.size(), is(2));

                // make sure VDB contains virtual data policy
                assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, dataPolicy.getUuid());
            } else if (PermissionArtifact.TYPE.getId().equals(userType)) {
                if (!foundPermission1 && "myTable.T1".equals(artifactName)) {
                    foundPermission1 = true;
                    permission1 = derivedArtifact;

                    assertPropertyValue(permission1, Permission.PropertyName.READABLE, "true");
                    assertThat(permission1.getProperty().size(), is(1));

                    // data policy and related document relationships
                    final List<Relationship> relationships = permission1.getRelationship();
                    assertThat(relationships.size(), is(2));
                } else if (!foundPermission2 && "myTable.T2".equals(artifactName)) {
                    foundPermission2 = true;
                    permission2 = derivedArtifact;

                    assertPropertyValue(permission2, Permission.PropertyName.CREATABLE, "true");
                    assertPropertyValue(permission2, Permission.PropertyName.READABLE, "false");
                    assertPropertyValue(permission2, Permission.PropertyName.UPDATABLE, "true");
                    assertPropertyValue(permission2, Permission.PropertyName.DELETABLE, "true");
                    assertPropertyValue(permission2, Permission.PropertyName.EXECUTABLE, "true");
                    assertPropertyValue(permission2, Permission.PropertyName.ALTERABLE, "true");
                    assertPropertyValue(permission2, Permission.PropertyName.CONDITION, "col1 = user()");
                    assertThat(permission2.getProperty().size(), is(7));

                    // data policy and related document relationships
                    final List<Relationship> relationships = permission2.getRelationship();
                    assertThat(relationships.size(), is(2));
                } else if (!foundPermission3 && "javascript".equals(artifactName)) {
                    foundPermission3 = true;
                    permission3 = derivedArtifact;

                    assertPropertyValue(permission3, Permission.PropertyName.LANGUAGABLE, "true");
                    assertThat(derivedArtifact.getProperty().size(), is(1));

                    // data policy and related document relationships
                    final List<Relationship> relationships = permission3.getRelationship();
                    assertThat(relationships.size(), is(2));
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
        assertRelationshipTargetUuid(physicalModel, SchemaArtifact.SOURCES_RELATIONSHIP, source3.getUuid());
        assertRelationshipTargetUuid(virtualModel, SchemaArtifact.SOURCES_RELATIONSHIP, source1.getUuid());
        assertRelationshipTargetUuid(virtualModel, SchemaArtifact.SOURCES_RELATIONSHIP, source2.getUuid());
        assertRelationshipTargetUuid(source3, SourceArtifact.SCHEMA_RELATIONSHIP, physicalModel.getUuid());
        assertRelationshipTargetUuid(source1, SourceArtifact.SCHEMA_RELATIONSHIP, virtualModel.getUuid());
        assertRelationshipTargetUuid(source2, SourceArtifact.SCHEMA_RELATIONSHIP, virtualModel.getUuid());
        assertRelationshipTargetUuid(permission1, PermissionArtifact.DATA_POLICY_RELATIONSHIP, dataPolicy.getUuid());
        assertRelationshipTargetUuid(permission2, PermissionArtifact.DATA_POLICY_RELATIONSHIP, dataPolicy.getUuid());
        assertRelationshipTargetUuid(permission3, PermissionArtifact.DATA_POLICY_RELATIONSHIP, dataPolicy.getUuid());
        assertRelationshipTargetUuid(dataPolicy, DataPolicyArtifact.PERMISSIONS_RELATIONSHIP, permission1.getUuid());
        assertRelationshipTargetUuid(dataPolicy, DataPolicyArtifact.PERMISSIONS_RELATIONSHIP, permission2.getUuid());
        assertRelationshipTargetUuid(dataPolicy, DataPolicyArtifact.PERMISSIONS_RELATIONSHIP, permission3.getUuid());
    }

    @Test
    public void shouldDeriveTwitterVdbArtifacts() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/twitterVdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // add VDB
        final BaseArtifactType vdbArtifact = _repoMgr.addVdb(vdbStream);

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

            if (!foundTranslator && TranslatorArtifact.TYPE.getId().equals(userType)) {
                foundTranslator = true;
                assertThat(artifactName, is("rest"));
                assertPropertyValue(derivedArtifact, Translator.PropertyName.TYPE, "ws");
                assertPropertyValue(derivedArtifact, "DefaultBinding", "HTTP");
                assertPropertyValue(derivedArtifact, "DefaultServiceMode", "MESSAGE");
                assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, summary.getUuid());
            } else if (SchemaArtifact.TYPE.getId().equals(userType)) {
                if (!foundPhysicalModel && "twitter".equals(artifactName)) {
                    foundPhysicalModel = true;
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.TYPE, Schema.Type.PHYSICAL.name());
                    assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, summary.getUuid());
                } else if (!foundViewModel && "twitterview".equals(artifactName)) {
                    foundViewModel = true;
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.TYPE, Schema.Type.VIRTUAL.name());
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.METADATA_TYPE, Schema.DEFAULT_METADATA_TYPE.name());
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
                    assertRelationshipTargetUuid(vdbArtifact, VdbArtifact.DERIVED_RELATIONSHIP, summary.getUuid());
                } else {
                    fail("unexpected schema artifact '" + artifactName + '\'');
                }
            } else if (!foundDataSource && SourceArtifact.TYPE.getId().equals(userType)) {
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
