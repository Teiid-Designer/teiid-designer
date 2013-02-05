/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.artifact;

import java.util.Iterator;

/**
 * A collection of artifacts that are a result of a query.
 */
public interface ArtifactResultSet extends Iterable<Artifact>, Iterator<Artifact> {

    /**
     * @return the number of artifacts in the result
     */
    int size();

}
