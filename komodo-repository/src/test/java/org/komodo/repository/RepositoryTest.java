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
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komodo.common.util.Precondition;
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

/**
 * The base class for Komodo repository artifact tests.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class RepositoryTest implements RepositoryConstants {

    protected static RepositoryManager _repoMgr;

    @BeforeClass
    public static void startRepository() throws Exception {
        System.setProperty(CleanableRepositoryManager.MODESHAPE_CONFIG_URL_SYS_PROP,
                           "classpath://" + JCRRepository.class.getName()
                           + "/META-INF/modeshape-configs/inmemory-sramp-config.json");

        if (System.getProperty(CleanableRepositoryManager.SERVER_PORT_SYS_PROP) == null) {
            System.setProperty(CleanableRepositoryManager.SERVER_PORT_SYS_PROP,
                               Integer.toString(CleanableRepositoryManager.DEFAULT_SERVER_PORT));
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

        _repoMgr = new CleanableRepositoryManager();
    }

    @AfterClass
    public static void shutdownRepository() throws Exception {
        if (_repoMgr != null) {
            EmbeddedContainer.stop();
            PersistenceFactory.newInstance().shutdown();
        }
    }

    /**
     * @param artifact the artifact whose number of derived artifacts is being checked (cannot be <code>null</code>)
     * @param expected the expected number of derived artifacts
     * @throws Exception if the test fails or there is a problem obtaining the number of derived artifacts
     */
    protected void assertNumberOfDerivedArtifacts(final BaseArtifactType artifact,
                                                  final int expected) throws Exception {
        final long actual = _repoMgr.getDerivedArtifacts(artifact).size();

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

    /**
     * Clean repo after each test method.
     */
    @After
    public final void cleanRepository() throws Exception {
        Precondition.notNull(_repoMgr, "_repoMgr");

        if (_repoMgr instanceof Cleanable) {
            ((Cleanable)_repoMgr).clean();
        }
    }

    /**
     * Obtains the content of a file resource.
     * 
     * @param fileName the file name relative to the calling class (cannot be <code>null</code> or empty)
     * @return the input stream to the content; may be <code>null</code> if the resource does not exist
     */
    protected InputStream getResourceAsStream(final String fileName) {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }

}
