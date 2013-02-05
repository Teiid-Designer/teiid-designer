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
import static org.junit.Assert.assertThat;
import java.util.List;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komodo.common.util.CollectionUtil;
import org.komodo.repository.RepositoryTest;
import org.komodo.repository.SoaRepository;
import org.komodo.repository.artifact.Artifact;
import org.overlord.sramp.atom.providers.HttpResponseProvider;
import org.overlord.sramp.atom.providers.OntologyProvider;
import org.overlord.sramp.atom.providers.SrampAtomExceptionProvider;
import org.overlord.sramp.common.SrampModelUtils;
import org.overlord.sramp.repository.PersistenceFactory;
import org.overlord.sramp.repository.jcr.JCRRepository;
import org.overlord.sramp.server.atom.services.ArtifactResource;
import org.overlord.sramp.server.atom.services.BatchResource;
import org.overlord.sramp.server.atom.services.FeedResource;
import org.overlord.sramp.server.atom.services.OntologyResource;
import org.overlord.sramp.server.atom.services.QueryResource;
import org.overlord.sramp.server.atom.services.ServiceDocumentResource;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.ExtendedArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.Relationship;
import org.s_ramp.xmlns._2010.s_ramp.Target;

/**
 * The base class for Komodo repository artifact tests.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class SrampTest extends RepositoryTest implements SrampRepositoryConstants {

    @AfterClass
    public static void shutdownRepository() throws Exception {
        if (_repository != null) {
            EmbeddedContainer.stop();
            PersistenceFactory.newInstance().shutdown();
        }
    }

    @BeforeClass
    public static void startRepository() throws Exception {
        System.setProperty(SrampCleanableRepository.MODESHAPE_CONFIG_URL_SYS_PROP,
                           "classpath://" + JCRRepository.class.getName()
                           + "/META-INF/modeshape-configs/inmemory-sramp-config.json");

        if (System.getProperty(SoaRepository.SERVER_PORT_SYS_PROP) == null) {
            System.setProperty(SoaRepository.SERVER_PORT_SYS_PROP, Integer.toString(SrampCleanableRepository.DEFAULT_SERVER_PORT));
        }

        final ResteasyDeployment deployment = EmbeddedContainer.start();
        final Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(ServiceDocumentResource.class);
        registry.addPerRequestResource(ArtifactResource.class);
        registry.addPerRequestResource(FeedResource.class);
        registry.addPerRequestResource(QueryResource.class);
        registry.addPerRequestResource(BatchResource.class);
        registry.addPerRequestResource(OntologyResource.class);

        final ResteasyProviderFactory providerFactory = deployment.getProviderFactory();
        providerFactory.registerProvider(SrampAtomExceptionProvider.class);
        providerFactory.registerProvider(HttpResponseProvider.class);
        providerFactory.registerProvider(OntologyProvider.class);

        _repository = new SrampCleanableRepository();
    }

    /**
     * @param artifact the artifact whose number of derived artifacts is being checked (cannot be <code>null</code>)
     * @param expected the expected number of derived artifacts
     * @throws Exception if the test fails or there is a problem obtaining the number of derived artifacts
     */
    protected void assertNumberOfDerivedArtifacts(final BaseArtifactType artifact,
                                                  final int expected) throws Exception {
        final long actual = _repository.getDerivedArtifacts(artifact.getUuid()).size();

        if (actual != expected) {
            throw new AssertionError("Expected <" + expected + "> but got <" + actual + ">");
        }
    }

    /**
     * @param artifact the artifact whose custom property is being checked (cannot be <code>null</code>)
     * @param customPropertyName the custom property name whose value is being requested (cannot be <code>null</code> or empty)
     * @param expected the expected property value (can be <code>null</code> or empty)
     * @throws Exception if the test fails or there is a problem obtaining the property value
     */
    protected void assertPropertyValue(final BaseArtifactType artifact,
                                       final String customPropertyName,
                                       final String expected) throws Exception {
        assert (artifact != null);
        assert ((customPropertyName != null) && !customPropertyName.isEmpty());

        final String actual = SrampModelUtils.getCustomProperty(artifact, customPropertyName);
        assertThat(actual, is(expected));
    }

    protected void assertRelationshipTargetUuid(final BaseArtifactType artifact,
                                                final Artifact.RelationshipType relationshipType,
                                                final String targetArtifactUuid) {
        assert (artifact != null);
        assert (relationshipType != null);
        assert ((targetArtifactUuid != null) && !targetArtifactUuid.isEmpty());

        final List<Relationship> relationships = artifact.getRelationship();

        if (CollectionUtil.isEmpty(relationships)) {
            throw new AssertionError("No relationships found for artifact '" + artifact.getName() + '\'');
        }

        final String relationshipId = relationshipType.getId();
        boolean relationshipFound = false;
        boolean targetFound = false;

        for (final Relationship relationship : relationships) {
            if (relationship.getRelationshipType().equals(relationshipId)) {
                relationshipFound = true;
                final List<Target> targets = relationship.getRelationshipTarget();

                if (CollectionUtil.isEmpty(targets)) {
                    throw new AssertionError("No targets found for relationship '" + relationshipId + "' and artifact '"
                                             + artifact.getName() + '\'');
                }

                for (final Target target : targets) {
                    if (targetArtifactUuid.equals(target.getValue())) {
                        targetFound = true;
                        break;
                    }
                }
            }
        }

        if (!relationshipFound) {
            throw new AssertionError("Relationship '" + relationshipId + "' was not found for artifact '" + artifact.getName()
                                     + '\'');
        } else if (!targetFound) {
            throw new AssertionError("Target '" + targetArtifactUuid + ", for relationship '" + relationshipId
                                     + "' and artifact '" + artifact.getName() + "' was not found");
        }
    }

    protected void assertSrampArtifact(final Artifact artifact) {
        assert (artifact != null);
        assertThat(artifact, is(instanceOf(SrampArtifact.class)));
    }

    protected boolean isExtendedType(final SrampArtifact srampArtifact,
                                     final Artifact.Type extendedType) {
        assert (srampArtifact != null);
        assert (extendedType != null);

        if (srampArtifact.getDelegate() instanceof ExtendedArtifactType) {
            return extendedType.getId().equals(((ExtendedArtifactType)srampArtifact.getDelegate()).getExtendedType());
        }

        return false;
    }
}
