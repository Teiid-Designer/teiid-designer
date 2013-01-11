/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.deriver;

import org.komodo.common.util.Precondition;
import org.komodo.repository.RepositoryConstants;

/**
 * Utilities for use with derivers.
 */
public class DeriverUtil {

    private static final String ATTRIBUTE_QUERY_PATTERN = "string(./%s)"; //$NON-NLS-1$

    private static final String DERIVED_ARTIFACTS_PATTERN = RepositoryConstants.Sramp.USER_DEFINED_ARTIFACT_PATH
                                                            + "[relatedDocument[@uuid = '%s']]"; //$NON-NLS-1$

    private static final String ELEMENT_QUERY_PATTERN = "./%s"; //$NON-NLS-1$

    private static final String UUID_QUERY_PATTERN = RepositoryConstants.Sramp.ROOT_PATH + "[@uuid = '%s']"; //$NON-NLS-1$

    /**
     * @param qualifiedName the qualified name of the attribute whose query string is being requested (cannot be <code>null</code> 
     * or empty)
     * @return the query string (never <code>null</code>)
     */
    public static String getAttributeQueryString(final String qualifiedName) {
        Precondition.notEmpty(qualifiedName, "qualifiedName"); //$NON-NLS-1$
        System.err.println(String.format(ATTRIBUTE_QUERY_PATTERN, qualifiedName));
        return String.format(ATTRIBUTE_QUERY_PATTERN, qualifiedName);
    }

    /**
     * @param uuid the UUID of the artifact whose derived artifacts query string is being requested (cannot be <code>null</code> 
     * or empty)
     * @return the query string (never <code>null</code>)
     */
    public static String getDerivedArtifactsQueryString(final String uuid) {
        Precondition.notEmpty(uuid, "qualifiedName"); //$NON-NLS-1$
        return String.format(DERIVED_ARTIFACTS_PATTERN, uuid);
    }

    /**
     * @param qualifiedName the qualified name of the element whose query string is being requested (cannot be <code>null</code> 
     * or empty)
     * @return the query string (never <code>null</code>)
     */
    public static String getElementQueryString(final String qualifiedName) {
        Precondition.notEmpty(qualifiedName, "qualifiedName"); //$NON-NLS-1$
        return String.format(ELEMENT_QUERY_PATTERN, qualifiedName);
    }

    /**
     * @param uuid the UUID of the artifact whose query string is being requested (cannot be <code>null</code> or empty)
     * @return the query string (never <code>null</code>)
     */
    public static String getUuidQueryString(final String uuid) {
        Precondition.notEmpty(uuid, "uuid"); //$NON-NLS-1$
        return String.format(UUID_QUERY_PATTERN, uuid);
    }

    /**
     * Don't allow public construction.
     */
    private DeriverUtil() {
        // nothing to do
    }
}
