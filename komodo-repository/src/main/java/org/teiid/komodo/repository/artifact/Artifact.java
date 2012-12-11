/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository.artifact;

import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.teiid.komodo.repository.util.Precondition;
import org.teiid.komodo.repository.util.StringUtil;

/**
 * Represents a Komodo artifact.
 */
public interface Artifact {

    /**
     * Utilities for use with artifacts.
     */
    public class Utils {

        private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

        /**
         * @param simpleName the artifact's simple name (cannot be <code>null</code> or empty)
         * @param parentPath the artifact's parent path (can be <code>null</code> or empty)
         * @return the artifact's full name/path (never <code>null</code> or empty)
         */
        public static String constructFullName(final String simpleName,
                                               final String parentPath) {
            Precondition.notEmpty(simpleName, "simpleName"); //$NON-NLS-1$

            if (!StringUtil.isEmpty(parentPath)) {
                String fullName = parentPath;

                if (!fullName.endsWith(PATH_SEPARATOR)) {
                    fullName += PATH_SEPARATOR;
                }

                fullName += simpleName;
                return fullName;
            }

            return simpleName;
        }

        /**
         * @param fullName the name including the parent path (cannot be <code>null</code> ore empty)
         * @return the parent path or <code>null</code> if doesn't exist
         */
        public static String getParentPath(final String fullName) {
            Precondition.notEmpty(fullName, "fullName"); //$NON-NLS-1$
            final int index = fullName.lastIndexOf(PATH_SEPARATOR);
            return ((index == -1) ? null : fullName.substring(0, index));
        }

        /**
         * @param fullName the name including the parent path (cannot be <code>null</code> ore empty)
         * @return the simple name (never <code>null</code> or empty)
         * @throws IllegalArgumentException if a simple name cannot be found
         */
        public static String getSimpleName(final String fullName) {
            Precondition.notEmpty(fullName, "fullName"); //$NON-NLS-1$
            final int index = fullName.lastIndexOf(PATH_SEPARATOR);

            if (index == -1) {
                return fullName;
            }

            if (fullName.endsWith(PATH_SEPARATOR)) {
                throw new IllegalArgumentException("Artifact name of '" + fullName + "' does not have a simple name"); // TODO i18n
            }

            return fullName.substring(index + 1);
        }
    }

    /**
     * @return the S-RAMP artifact (never <code>null</code>)
     */
    BaseArtifactType getDelegate();

    /**
     * @return the artifact name including the parent path (never <code>null</code> or empty)
     */
    String getFullName();

    /**
     * @return the parent path (can be <code>null</code> or empty)
     */
    String getParentPath();

    /**
     * @return the name without the parent path (never <code>null</code>)
     */
    String getSimpleName();

    /**
     * @return the artifact type (never <code>null</code> or empty)
     */
    String getType();
}
