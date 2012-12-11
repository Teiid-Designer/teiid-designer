/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository;

import java.io.InputStream;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.overlord.sramp.repository.DerivedArtifactsFactory;
import org.overlord.sramp.repository.PersistenceFactory;
import org.overlord.sramp.repository.QueryManagerFactory;
import org.overlord.sramp.repository.jcr.JCRRepository;
import org.overlord.sramp.repository.jcr.JCRRepositoryCleaner;

/**
 * The base class for Komodo repository artifact tests.
 */
public abstract class RepositoryTest {

    protected static RepositoryManager _repoMgr;
    protected static JCRRepositoryCleaner _repoCleaner;

    /**
     * Sets up the in-memory S-RAMP repository.
     */
    @BeforeClass
    public static void startupRepository() {
        // use the in-memory config for unit tests
        System.setProperty("sramp.modeshape.config.url", "classpath://" + JCRRepository.class.getName() //$NON-NLS-1$ //$NON-NLS-2$
                                                         + "/META-INF/modeshape-configs/inmemory-sramp-config.json"); //$NON-NLS-1$

        _repoMgr = new RepositoryManager(DerivedArtifactsFactory.newInstance(), PersistenceFactory.newInstance(),
                                         QueryManagerFactory.newInstance());
        _repoCleaner = new JCRRepositoryCleaner();
    }

    /**
     * Shutdowns the S-RAMP in-memory repository.
     */
    @AfterClass
    public static void stopRepository() {
        _repoMgr.getPersistenceManager().shutdown();
    }

    /**
     * Performed before each test.
     */
    @Before
    public void beforeEach() {
        _repoCleaner.clean();
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
