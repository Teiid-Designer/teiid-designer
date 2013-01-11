/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.test;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.komodo.repository.CleanableRepositoryManager;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;

/**
 * A unit test matcher for artifacts.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class NumberOfDerivedArtifactsMatcher extends TypeSafeMatcher<Integer> {

    @Factory
    public static NumberOfDerivedArtifactsMatcher numberOfDerivedArtifacts(BaseArtifactType artifact,
                                                                           CleanableRepositoryManager repoMgr) {
        return new NumberOfDerivedArtifactsMatcher(artifact, repoMgr);
    }

    private final BaseArtifactType artifact;
    private final CleanableRepositoryManager repoMgr;

    public NumberOfDerivedArtifactsMatcher(BaseArtifactType artifact,
                                           CleanableRepositoryManager repoMgr) {
        this.artifact = artifact;
        this.repoMgr = repoMgr;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
     */
    @Override
    public void describeTo(Description description) {
        description.appendText("number of derived artifacts");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.junit.internal.matchers.TypeSafeMatcher#matchesSafely(java.lang.Object)
     */
    @Override
    public boolean matchesSafely(Integer expectedNumberOfDerivedArtifacts) {
        try {
            return (this.repoMgr.getDerivedArtifacts(this.artifact).size() == expectedNumberOfDerivedArtifacts);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

}
