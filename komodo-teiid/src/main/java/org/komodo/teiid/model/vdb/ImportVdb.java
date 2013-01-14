/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import org.komodo.common.util.HashCode;

/**
 * The Teiid Import VDB business object.
 */
public class ImportVdb extends VdbObject {

    /**
     * The VDB manifest (<code>vdb.xml</code>) identifiers related to translator elements.
     */
    public interface ManifestId {

        /**
         * The VDB translator element attribute identifiers.
         */
        interface Attributes {

            /**
             * The translator name and property name attribute identifier.
             */
            String NAME = "name"; //$NON-NLS-1$

            /**
             * The import VDB import data policies attribute identifier.
             */
            String IMPORT_DATA_POLICIES = "import-data-policies"; //$NON-NLS-1$

            /**
             * The Import VDB version attribute identifier.
             */
            String VERSION = "version"; //$NON-NLS-1$
        }
    }

    /**
     * Import VDB property names.
     */
    public interface PropertyName extends VdbObject.PropertyName {

        /**
         * Indicates if data policies should be imported.
         */
        String IMPORT_DATA_POLICIES = ImportVdb.class.getSimpleName() + ".importDataPolicies"; //$NON-NLS-1$

        /**
         * The Import VDB version.
         */
        String VERSION = ImportVdb.class.getSimpleName() + ".version"; //$NON-NLS-1$
    }

    /**
     * The default import data policies setting. Value is {@value}.
     */
    public static final boolean DEFAULT_IMPORT_DATA_POLICIES = true;

    private boolean importDataPolicies = DEFAULT_IMPORT_DATA_POLICIES;
    private int version = 1;

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object that) {
        if (super.equals(that)) {
            final ImportVdb thatImportVdb = (ImportVdb)that;
            return ((this.importDataPolicies == thatImportVdb.importDataPolicies) && (this.version == thatImportVdb.version));
        }

        return false;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(super.hashCode(), this.id, this.version);
    }

    /**
     * Generates a property change event if the import data policies flag is changed.
     * 
     * @param newImportDataPolicies the new import data policies value
     */
    public void setImportDataPolicies(final boolean newImportDataPolicies) {
        if (this.importDataPolicies != newImportDataPolicies) {
            final boolean oldValue = this.importDataPolicies;
            this.importDataPolicies = newImportDataPolicies;
            firePropertyChangeEvent(PropertyName.IMPORT_DATA_POLICIES, oldValue, this.importDataPolicies);

            assert (this.importDataPolicies == newImportDataPolicies);
            assert (this.importDataPolicies != oldValue);
        }
    }

    /**
     * Generates a property change event if the version is changed.
     * 
     * @param newVersion the new version
     */
    public void setVersion(final int newVersion) {
        if (this.version != newVersion) {
            final int oldValue = this.version;
            this.version = newVersion;
            firePropertyChangeEvent(PropertyName.VERSION, oldValue, this.version);

            assert (this.version == newVersion);
            assert (this.version != oldValue);
        }
    }

    /**
     * @return <code>true</code> if data policies should be imported by the VDB importing this VDB
     */
    public boolean shouldDataPoliciesBeImported() {
        return this.importDataPolicies;
    }

}
