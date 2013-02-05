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
import org.komodo.common.util.Precondition;
import org.komodo.repository.artifact.Artifact;

/**
 * The base class for Komodo repository artifact tests. The static SOA repository must be set by the subclass.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class RepositoryTest {

    protected static SoaRepository _repository;

    /**
     * @param artifact the artifact whose property is being checked (cannot be <code>null</code>)
     * @param propertyName the property name whose value is being tested (cannot be <code>null</code> or empty)
     * @param expected the expected property value (can be <code>null</code> or empty)
     * @throws Exception if the test fails or there is a problem obtaining the property value
     */
    protected void assertPropertyValue(final Artifact artifact,
                                       final String propertyName,
                                       final String expected) throws Exception {
        assert (artifact != null);
        assert ((propertyName != null) && !propertyName.isEmpty());

        final String actual = artifact.getProperty(propertyName);
        assertThat(actual, is(expected));
    }

    /**
     * Clean repo after each test method.
     */
    @After
    public final void cleanRepository() throws Exception {
        Precondition.notNull(_repository, "_repoMgr");

        if (_repository instanceof Cleanable) {
            ((Cleanable)_repository).clean();
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
