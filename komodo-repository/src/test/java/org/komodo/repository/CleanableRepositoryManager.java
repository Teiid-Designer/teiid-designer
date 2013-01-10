/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;


/**
 * A repository manager that can delete all content.
 */
public interface CleanableRepositoryManager extends RepositoryManager {

    /**
     *  Cleans/clears out repository content.
     *  
     * @throws Exception if there is a problem cleaning the repository
     */
    void clean() throws Exception;

}
