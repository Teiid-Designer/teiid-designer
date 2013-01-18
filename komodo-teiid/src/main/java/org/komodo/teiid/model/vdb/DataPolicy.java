/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.HashCode;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.teiid.model.Describable;

/**
 * The Teiid data policy business object.
 */
public class DataPolicy extends VdbObject implements Describable {

    /**
     * The VDB manifest (<code>vdb.xml</code>) identifiers related to data policy elements.
     */
    public interface ManifestId {

        /**
         * The VDB data policy element attribute identifiers.
         */
        interface Attributes {

            /**
             * The any authenticated attribute identifier.
             */
            String ANY_AUTHENTICATED = "any-authenticated"; //$NON-NLS-1$

            /**
             * The data policy name attribute identifier.
             */
            String NAME = "name"; //$NON-NLS-1$

            /**
             * The allow create temporary tables attribute identifier.
             */
            String TEMP_TABLE_CREATABLE = "allow-create-temporary-tables"; //$NON-NLS-1$
        }

        /**
         * The data policy description element identifier.
         */
        String DESCRIPTION = "description"; //$NON-NLS-1$

        /**
         * The data permission element identifier.
         */
        String PERMISSION = "permission"; //$NON-NLS-1$

        /**
         * The mapped role name element identifier.
         */
        String ROLE_NAME = "mapped-role-name"; //$NON-NLS-1$
    }

    /**
     * Data policy property names.
     */
    public interface PropertyName extends VdbObject.PropertyName {

        /**
         * Indicates if data policy has any authenticated. 
         */
        String ANY_AUTHENTICATED = DataPolicy.class.getSimpleName() + ".anyAuthenticated"; //$NON-NLS-1$

        /**
         * The Teiid data policy description.
         */
        String DESCRIPTION = Describable.DESCRIPTION;

        /**
         * A collection of data permisions.
         */
        String PERMISSIONS = DataPolicy.class.getSimpleName() + ".anyAuthenticated"; //$NON-NLS-1$

        /**
         * A collection of role names.
         */
        String ROLE_NAMES = DataPolicy.class.getSimpleName() + ".roleNames"; //$NON-NLS-1$

        /**
         * Indicates if data policy can create temp tables.
         */
        String TEMP_TABLE_CREATABLE = DataPolicy.class.getSimpleName() + ".tempTableCreatable"; //$NON-NLS-1$
    }

    private static final List<Permission> NO_PERMISSIONS = Collections.emptyList();
    private static final List<String> NO_ROLE_NAMES = Collections.emptyList();

    private boolean anyAuthenticated;
    private String description;
    private List<Permission> permissions;
    private List<String> roleNames;
    private boolean tempTableCreate;

    /**
     * Generates a property change event if the collection of data permissions is changed.
     * 
     * @param newPermission the data permission being added (cannot be <code>null</code>)
     */
    public void addPermission(final Permission newPermission) {
        Precondition.notNull(newPermission, "newPermission"); //$NON-NLS-1$

        if (this.permissions == null) {
            this.permissions = new ArrayList<Permission>();
        }

        final List<Permission> oldValue = getPermissions();

        if (this.permissions.add(newPermission)) {
            firePropertyChangeEvent(PropertyName.PERMISSIONS, oldValue, getPermissions());
        } else if (this.permissions.isEmpty()) {
            this.permissions = null;
        }
    }

    /**
     * Generates a property change event if the collection of mapped data roles is changed.
     * 
     * @param newRoleName the role name being added (cannot be <code>null</code>)
     */
    public void addRoleName(final String newRoleName) {
        Precondition.notNull(newRoleName, "newRoleName"); //$NON-NLS-1$

        if (this.roleNames == null) {
            this.roleNames = new ArrayList<String>();
        }

        final List<String> oldValue = getRoleNames();

        if (this.roleNames.add(newRoleName)) {
            firePropertyChangeEvent(PropertyName.ROLE_NAMES, oldValue, getRoleNames());
        } else if (this.roleNames.isEmpty()) {
            this.roleNames = null;
        }
    }

    /**
     * @return <code>true</code> if data policy allows any authenticated
     */
    public boolean anyAuthenticated() {
        return this.anyAuthenticated;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object that) {
        if (super.equals(that)) {
            final DataPolicy thatDataPolicy = (DataPolicy)that;

            return ((this.anyAuthenticated == thatDataPolicy.anyAuthenticated)
                    && (this.tempTableCreate == thatDataPolicy.tempTableCreate)
                    && StringUtil.matches(this.description, thatDataPolicy.description)
                    && CollectionUtil.matches(this.permissions, thatDataPolicy.permissions) && CollectionUtil.matches(this.roleNames,
                                                                                                                      thatDataPolicy.roleNames));
        }

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Describable#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * @return an unmodifiable collection of data permissions (never <code>null</code> but can be empty)
     */
    public List<Permission> getPermissions() {
        if (this.permissions == null) {
            return NO_PERMISSIONS;
        }

        return Collections.unmodifiableList(this.permissions);
    }

    /**
     * @return an unmodifiable collection of mapped role names (never <code>null</code> but can be empty)
     */
    public List<String> getRoleNames() {
        if (this.roleNames == null) {
            return NO_ROLE_NAMES;
        }

        return Collections.unmodifiableList(this.roleNames);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(super.hashCode(),
                                this.anyAuthenticated,
                                this.description,
                                this.permissions,
                                this.roleNames,
                                this.tempTableCreate);
    }

    /**
     * Generates a property change event if the collection of data permissions is changed.
     * 
     * @param permissionToDelete the data permission being deleted (cannot be <code>null</code>)
     */
    public void removePermission(final Permission permissionToDelete) {
        Precondition.notNull(permissionToDelete, "permissionToDelete"); //$NON-NLS-1$

        if (this.permissions != null) {
            final List<Permission> oldValue = getPermissions();

            if (this.permissions.remove(permissionToDelete)) {
                if (this.permissions.isEmpty()) {
                    this.permissions = null;
                }

                firePropertyChangeEvent(PropertyName.PERMISSIONS, oldValue, getPermissions());
            }
        }
    }

    /**
     * Generates a property change event if the collection of mapped role name is changed.
     * 
     * @param roleNameToDelete the mapped role name being deleted (cannot be <code>null</code>)
     */
    public void removeRoleName(final String roleNameToDelete) {
        Precondition.notNull(roleNameToDelete, "dataPolicyToDelete"); //$NON-NLS-1$

        if (this.roleNames != null) {
            final List<String> oldValue = getRoleNames();

            if (this.roleNames.remove(roleNameToDelete)) {
                if (this.roleNames.isEmpty()) {
                    this.roleNames = null;
                }

                firePropertyChangeEvent(PropertyName.ROLE_NAMES, oldValue, getRoleNames());
            }
        }
    }

    /**
     * Generates a property change event if the any authenticated flag is changed.
     * 
     * @param newAnyAuthenticated the new allows any authenticated value
     */
    public void setAnyAuthenticated(final boolean newAnyAuthenticated) {
        if (this.anyAuthenticated != newAnyAuthenticated) {
            final boolean oldValue = this.anyAuthenticated;
            this.anyAuthenticated = newAnyAuthenticated;
            firePropertyChangeEvent(PropertyName.ANY_AUTHENTICATED, oldValue, this.anyAuthenticated);

            assert (this.anyAuthenticated == newAnyAuthenticated);
            assert (this.anyAuthenticated != oldValue);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Describable#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(final String newDescription) {
        if (!StringUtil.matches(this.description, newDescription)) {
            final String oldValue = this.description;
            this.description = newDescription;
            firePropertyChangeEvent(PropertyName.DESCRIPTION, oldValue, newDescription);

            assert StringUtil.matches(this.description, newDescription);
            assert !StringUtil.matches(this.description, oldValue);
        }
    }

    /**
     * Generates a property change event if the create temporary tables flag is changed.
     * 
     * @param newTempTableCreatable the new create temporary tables value
     */
    public void setTempTableCreatable(final boolean newTempTableCreatable) {
        if (this.tempTableCreate != newTempTableCreatable) {
            final boolean oldValue = this.tempTableCreate;
            this.tempTableCreate = newTempTableCreatable;
            firePropertyChangeEvent(PropertyName.TEMP_TABLE_CREATABLE, oldValue, this.tempTableCreate);

            assert (this.tempTableCreate == newTempTableCreatable);
            assert (this.tempTableCreate != oldValue);
        }
    }

    /**
     * @return <code>true</code> if data policy can create temporary tables
     */
    public boolean tempTableCreatable() {
        return this.tempTableCreate;
    }

}
