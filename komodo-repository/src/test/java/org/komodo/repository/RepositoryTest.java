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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komodo.common.util.Precondition;
import org.overlord.sramp.SrampModelUtils;
import org.overlord.sramp.repository.jcr.JCRRepository;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;

/**
 * The base class for Komodo repository artifact tests.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class RepositoryTest implements RepositoryConstants {

    protected static RepositoryManager _repoMgr;
    private static boolean _started;

    @BeforeClass
    public static void setupEnvironment() throws Exception {
        System.setProperty(RepositoryManager.MODESHAPE_CONFIG_URL, "classpath://" + JCRRepository.class.getName()
                                                                   + "/META-INF/modeshape-configs/inmemory-sramp-config.json");
        _repoMgr = new CleanableRepositoryManager();
        _repoMgr.start();
        _started = true;
    }

    @AfterClass
    public static void shutdownRepository() throws Exception {
        if (_started) {
            _repoMgr.shutdown();
            _started = false;
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
