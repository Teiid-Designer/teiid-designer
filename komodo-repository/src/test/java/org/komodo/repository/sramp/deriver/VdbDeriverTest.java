/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp.deriver;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.ArtifactResultSet;
import org.komodo.repository.artifact.teiid.DataPolicyArtifact;
import org.komodo.repository.artifact.teiid.EntryArtifact;
import org.komodo.repository.artifact.teiid.ImportVdbArtifact;
import org.komodo.repository.artifact.teiid.PermissionArtifact;
import org.komodo.repository.artifact.teiid.SchemaArtifact;
import org.komodo.repository.artifact.teiid.SourceArtifact;
import org.komodo.repository.artifact.teiid.TranslatorArtifact;
import org.komodo.repository.artifact.teiid.VdbArtifact;
import org.komodo.repository.sramp.SrampArtifact;
import org.komodo.repository.sramp.SrampTest;
import org.komodo.teiid.model.vdb.DataPolicy;
import org.komodo.teiid.model.vdb.ImportVdb;
import org.komodo.teiid.model.vdb.Permission;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Source;
import org.komodo.teiid.model.vdb.Translator;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.Relationship;

/**
 * A test class for a {@link VdbDeriver}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class VdbDeriverTest extends SrampTest {

    @Test
    public void shouldDeriveParserTestVdbArtifacts() throws Exception {
        final InputStream vdbStream = getResourceAsStream("vdb/parser-test-vdb.xml");
        assertThat(vdbStream, is(not(nullValue())));

        // add VDB
        final Artifact artifact = _repository.add(vdbStream, VdbArtifact.TYPE);
        assertThat(artifact, is(instanceOf(VdbArtifact.class)));

        final VdbArtifact vdbArtifact = (VdbArtifact)artifact;
        assertSrampArtifact(vdbArtifact);

        final SrampArtifact vdbSrampArtifact = (SrampArtifact)vdbArtifact;

        // verify derived artifacts
        final ArtifactResultSet results = _repository.getDerivedArtifacts(vdbArtifact.getArtifactUuid());
        assertThat(results.size(), is(13));

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

        for (final Artifact derivedArtifact : results) {
            assertSrampArtifact(derivedArtifact);

            final SrampArtifact srampArtifact = (SrampArtifact)derivedArtifact;
            final String artifactName = derivedArtifact.getArtifactName();

            if (!foundImportVdb && isExtendedType(srampArtifact, ImportVdbArtifact.TYPE)) {
                foundImportVdb = true;
                assertThat(artifactName, is("x"));
                assertThat(derivedArtifact.getArtifactVersion(), is("2"));
                assertPropertyValue(derivedArtifact, ImportVdb.PropertyName.IMPORT_DATA_POLICIES, "true");
                assertThat(derivedArtifact.getProperties().size(), is(1));

                // related documents relationship
                final List<Relationship> relationships = srampArtifact.getDelegate().getRelationship();
                assertThat(relationships.size(), is(1));

                // make sure VDB contains import VDB
                assertRelationshipTargetUuid(vdbSrampArtifact.getDelegate(),
                                             VdbArtifact.DERIVED_RELATIONSHIP,
                                             derivedArtifact.getArtifactUuid());
            } else if (!foundTranslator && isExtendedType(srampArtifact, TranslatorArtifact.TYPE)) {
                foundTranslator = true;
                assertThat(artifactName, is("oracleOverride"));
                assertThat(derivedArtifact.getDescription(), is("hello world"));
                assertThat(derivedArtifact.getProperties().size(), is(2));
                assertPropertyValue(derivedArtifact, Translator.PropertyName.TYPE, "oracle");
                assertPropertyValue(derivedArtifact, "my-property", "my-value");

                // related documents relationship
                final List<Relationship> relationships = srampArtifact.getDelegate().getRelationship();
                assertThat(relationships.size(), is(1));

                // make sure VDB contains translator
                assertRelationshipTargetUuid(vdbSrampArtifact.getDelegate(),
                                             VdbArtifact.DERIVED_RELATIONSHIP,
                                             derivedArtifact.getArtifactUuid());
            } else if (isExtendedType(srampArtifact, EntryArtifact.TYPE)) {
                if (!foundEntry1 && "/path-one".equals(artifactName)) {
                    foundEntry1 = true;
                    assertThat(derivedArtifact.getDescription(), is("path one description"));
                    assertPropertyValue(derivedArtifact, "entryone", "1");
                    assertThat(derivedArtifact.getProperties().size(), is(1));

                    // related documents relationship
                    final List<Relationship> relationships = srampArtifact.getDelegate().getRelationship();
                    assertThat(relationships.size(), is(1));

                    // make sure VDB contains entry
                    assertRelationshipTargetUuid(vdbSrampArtifact.getDelegate(),
                                                 VdbArtifact.DERIVED_RELATIONSHIP,
                                                 derivedArtifact.getArtifactUuid());
                } else if (!foundEntry2 && "/path-two".equals(artifactName)) {
                    foundEntry2 = true;
                    assertThat(derivedArtifact.getProperties().isEmpty(), is(true));

                    // related documents relationship
                    final List<Relationship> relationships = srampArtifact.getDelegate().getRelationship();
                    assertThat(relationships.size(), is(1));

                    // make sure VDB contains entry
                    assertRelationshipTargetUuid(vdbSrampArtifact.getDelegate(),
                                                 VdbArtifact.DERIVED_RELATIONSHIP,
                                                 derivedArtifact.getArtifactUuid());
                } else {
                    fail("unexpected entry: " + artifactName);
                }
            } else if (isExtendedType(srampArtifact, SchemaArtifact.TYPE)) {
                if (!foundPhysicalModel && "model-one".equals(artifactName)) {
                    foundPhysicalModel = true;
                    physicalModel = srampArtifact.getDelegate();

                    assertPropertyValue(physicalModel, Schema.PropertyName.TYPE, Schema.Type.PHYSICAL.name());
                    assertPropertyValue(physicalModel, Schema.PropertyName.VISIBLE, "false");
                    assertThat(physicalModel.getDescription(), is("model description"));
                    assertPropertyValue(physicalModel, "model-prop", "model-value-override");

                    // sources and related documents relationships
                    final List<Relationship> relationships = physicalModel.getRelationship();
                    assertThat(relationships.size(), is(2));

                    // make sure VDB contains physical model
                    assertRelationshipTargetUuid(vdbSrampArtifact.getDelegate(),
                                                 VdbArtifact.DERIVED_RELATIONSHIP,
                                                 physicalModel.getUuid());
                } else if (!foundVirtualModel && "model-two".equals(artifactName)) {
                    foundVirtualModel = true;
                    virtualModel = srampArtifact.getDelegate();

                    assertPropertyValue(virtualModel, Schema.PropertyName.TYPE, Schema.Type.VIRTUAL.name());
                    assertPropertyValue(virtualModel, Schema.PropertyName.VISIBLE, "true");
                    assertPropertyValue(virtualModel, "model-prop", "model-value");
                    assertPropertyValue(virtualModel, Schema.PropertyName.METADATA_TYPE, Schema.DEFAULT_METADATA_TYPE.name());
                    assertPropertyValue(virtualModel, Schema.PropertyName.METADATA, "DDL Here");

                    // sources and related document relationships
                    final List<Relationship> relationships = virtualModel.getRelationship();
                    assertThat(relationships.size(), is(2));

                    // make sure VDB contains virtual model
                    assertRelationshipTargetUuid(vdbSrampArtifact.getDelegate(),
                                                 VdbArtifact.DERIVED_RELATIONSHIP,
                                                 virtualModel.getUuid());
                } else {
                    fail("unexpected model: " + artifactName);
                }
            } else if (isExtendedType(srampArtifact, SourceArtifact.TYPE)) {
                if (!foundSource1 && "s1".equals(artifactName)) {
                    foundSource1 = true;
                    source1 = srampArtifact.getDelegate();

                    assertPropertyValue(source1, Source.PropertyName.TRANSLATOR_NAME, "translator");
                    assertPropertyValue(source1, Source.PropertyName.JNDI_NAME, "java:binding-one");
                    assertThat(source1.getProperty().size(), is(2));

                    // schema and related document relationships
                    final List<Relationship> relationships = source1.getRelationship();
                    assertThat(relationships.size(), is(2));
                } else if (!foundSource2 && "s2".equals(artifactName)) {
                    foundSource2 = true;
                    source2 = srampArtifact.getDelegate();

                    assertPropertyValue(source2, Source.PropertyName.TRANSLATOR_NAME, "translator");
                    assertPropertyValue(source2, Source.PropertyName.JNDI_NAME, "java:binding-two");
                    assertThat(source2.getProperty().size(), is(2));

                    // schema and related document relationships
                    final List<Relationship> relationships = source2.getRelationship();
                    assertThat(relationships.size(), is(2));
                } else if (!foundSource3 && "s3".equals(artifactName)) {
                    foundSource3 = true;
                    source3 = srampArtifact.getDelegate();

                    assertPropertyValue(source3, Source.PropertyName.TRANSLATOR_NAME, "translator");
                    assertPropertyValue(source3, Source.PropertyName.JNDI_NAME, "java:mybinding");
                    assertThat(source3.getProperty().size(), is(2));

                    // schema and related document relationships
                    final List<Relationship> relationships = source3.getRelationship();
                    assertThat(relationships.size(), is(2));
                } else {
                    fail("unexpected source: " + artifactName);
                }
            } else if (!foundDataRole && isExtendedType(srampArtifact, DataPolicyArtifact.TYPE)) {
                foundDataRole = true;
                dataPolicy = srampArtifact.getDelegate();

                assertThat(artifactName, is("roleOne"));
                assertThat(dataPolicy.getDescription(), is("roleOne described"));
                assertPropertyValue(dataPolicy, DataPolicy.PropertyName.ANY_AUTHENTICATED, "false");
                assertPropertyValue(dataPolicy, DataPolicy.PropertyName.TEMP_TABLE_CREATABLE, "true");
                assertPropertyValue(dataPolicy, DataPolicy.PropertyName.ROLE_NAMES, "ROLE1,ROLE2");
                assertThat(dataPolicy.getProperty().size(), is(3));
                assertRelationshipTargetUuid(vdbSrampArtifact.getDelegate(),
                                             VdbArtifact.DERIVED_RELATIONSHIP,
                                             dataPolicy.getUuid());

                // permissions and related document relationships
                final List<Relationship> relationships = dataPolicy.getRelationship();
                assertThat(relationships.size(), is(2));

                // make sure VDB contains virtual data policy
                assertRelationshipTargetUuid(vdbSrampArtifact.getDelegate(),
                                             VdbArtifact.DERIVED_RELATIONSHIP,
                                             dataPolicy.getUuid());
            } else if (isExtendedType(srampArtifact, PermissionArtifact.TYPE)) {
                if (!foundPermission1 && "myTable.T1".equals(artifactName)) {
                    foundPermission1 = true;
                    permission1 = srampArtifact.getDelegate();

                    assertPropertyValue(permission1, Permission.PropertyName.READABLE, "true");
                    assertThat(permission1.getProperty().size(), is(1));

                    // data policy and related document relationships
                    final List<Relationship> relationships = permission1.getRelationship();
                    assertThat(relationships.size(), is(2));
                } else if (!foundPermission2 && "myTable.T2".equals(artifactName)) {
                    foundPermission2 = true;
                    permission2 = srampArtifact.getDelegate();

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
                    permission3 = srampArtifact.getDelegate();

                    assertPropertyValue(permission3, Permission.PropertyName.LANGUAGABLE, "true");
                    assertThat(derivedArtifact.getProperties().size(), is(1));

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
        final Artifact artifact = _repository.add(vdbStream, VdbArtifact.TYPE);

        // verify derived artifacts
        final ArtifactResultSet results = _repository.getDerivedArtifacts(artifact.getArtifactUuid());
        assertThat(results.size(), is(4));

        assertSrampArtifact(artifact);
        final SrampArtifact vdbArtifact = (SrampArtifact)artifact;

        // verify derived artifacts
        boolean foundTranslator = false;
        boolean foundPhysicalModel = false;
        boolean foundViewModel = false;
        boolean foundDataSource = false;

        BaseArtifactType sourceArtifact = null;
        BaseArtifactType translatorArtifact = null;

        for (final Artifact derivedArtifact : results) {
            assertSrampArtifact(derivedArtifact);

            final SrampArtifact srampArtifact = (SrampArtifact)derivedArtifact;
            final String artifactName = derivedArtifact.getArtifactName();

            if (!foundTranslator && isExtendedType(srampArtifact, TranslatorArtifact.TYPE)) {
                foundTranslator = true;
                translatorArtifact = srampArtifact.getDelegate();

                assertThat(artifactName, is("rest"));
                assertPropertyValue(derivedArtifact, Translator.PropertyName.TYPE, "ws");
                assertPropertyValue(derivedArtifact, "DefaultBinding", "HTTP");
                assertPropertyValue(derivedArtifact, "DefaultServiceMode", "MESSAGE");
                assertRelationshipTargetUuid(vdbArtifact.getDelegate(),
                                             VdbArtifact.DERIVED_RELATIONSHIP,
                                             derivedArtifact.getArtifactUuid());
            } else if (isExtendedType(srampArtifact, SchemaArtifact.TYPE)) {
                if (!foundPhysicalModel && "twitter".equals(artifactName)) {
                    foundPhysicalModel = true;
                    assertPropertyValue(derivedArtifact, Schema.PropertyName.TYPE, Schema.Type.PHYSICAL.name());
                    assertRelationshipTargetUuid(vdbArtifact.getDelegate(),
                                                 VdbArtifact.DERIVED_RELATIONSHIP,
                                                 derivedArtifact.getArtifactUuid());
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
                    assertRelationshipTargetUuid(vdbArtifact.getDelegate(),
                                                 VdbArtifact.DERIVED_RELATIONSHIP,
                                                 derivedArtifact.getArtifactUuid());
                } else {
                    fail("unexpected schema artifact '" + artifactName + '\'');
                }
            } else if (!foundDataSource && isExtendedType(srampArtifact, SourceArtifact.TYPE)) {
                foundDataSource = true;
                sourceArtifact = srampArtifact.getDelegate();
                assertThat(artifactName, is("twitter"));
                assertPropertyValue(derivedArtifact, Source.PropertyName.TRANSLATOR_NAME, "rest");
                assertPropertyValue(derivedArtifact, Source.PropertyName.JNDI_NAME, "java:/twitterDS");
            } else {
                fail("unexpected artifact '" + derivedArtifact.getArtifactName() + +'\'');
            }
        }

        assertThat((foundTranslator && foundPhysicalModel && foundViewModel && foundDataSource), is(true));
        assertRelationshipTargetUuid(sourceArtifact, SourceArtifact.TRANSLATOR_RELATIONSHIP, translatorArtifact.getUuid());
        assertRelationshipTargetUuid(translatorArtifact, TranslatorArtifact.SOURCES_RELATIONSHIP, sourceArtifact.getUuid());
    }

}
