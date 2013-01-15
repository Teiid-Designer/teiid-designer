/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import java.net.ConnectException;
import org.junit.Before;
import org.junit.Test;
import org.komodo.common.util.StringUtil;
import org.overlord.sramp.client.SrampClientException;

/**
 * A test class for a {@link Repositories}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class RepositoriesTest {

    private Repositories repos;

    @Before
    public void beforeEach() {
        this.repos = new Repositories();
    }

    @Test
    public void afterRemovingShouldNotGetSameInstance() throws Exception {
        final String url = "http://localhost:8081";
        final RepositoryManager oldRepo = this.repos.get(url, false);
        this.repos.remove(url);

        final RepositoryManager newRepo = this.repos.get(url, false);
        assertThat(newRepo, is(not(sameInstance(oldRepo))));
    }

    @Test
    public void shouldBeSameInstanceIfUrlIsSame() throws Throwable {
        final String url = "http://localhost:8081";
        final RepositoryManager actual = this.repos.get(url, false);
        assertThat(actual, is(sameInstance(this.repos.get(url))));
    }

    @Test( expected = ConnectException.class )
    public void shouldFailWhenTryingToConnectUsingValidUrlButNotConnected() throws Throwable {
        try {
            this.repos.get("http://localhost:8081");
        } catch (final SrampClientException e) {
            throw e.getCause();
        }
    }

    @Test( expected = Exception.class )
    public void shouldNotAllowConnectingWithInvalidUrl() throws Throwable {
        this.repos.get("bogus", true);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowEmptyUrl() throws Exception {
        this.repos.get(StringUtil.EMPTY_STRING);
    }

    @Test( expected = Exception.class )
    public void shouldNotAllowInvalidUrl() throws Throwable {
        this.repos.get("bogus");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullUrl() throws Exception {
        this.repos.get(null);
    }

}
