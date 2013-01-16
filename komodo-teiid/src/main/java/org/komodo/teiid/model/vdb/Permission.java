/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import org.komodo.common.util.HashCode;
import org.komodo.common.util.StringUtil;

/**
 * The Teiid data policy permission business object.
 */
public class Permission extends VdbObject {

    /**
     * The VDB manifest (<code>vdb.xml</code>) identifiers related to data policy permission elements.
     */
    public interface ManifestId {

        /**
         * The allow alter element identifier.
         */
        String ALTERABLE = "allow-alter"; //$NON-NLS-1$

        /**
         * The condition element identifier.
         */
        String CONDITION = "condition"; //$NON-NLS-1$

        /**
         * The allow create element identifier.
         */
        String CREATABLE = "allow-create"; //$NON-NLS-1$

        /**
         * The allow delete element identifier.
         */
        String DELETABLE = "allow-delete"; //$NON-NLS-1$

        /**
         * The allow execute element identifier.
         */
        String EXECUTABLE = "allow-execute"; //$NON-NLS-1$

        /**
         * The allow language element identifier.
         */
        String LANGUAGABLE = "allow-language"; //$NON-NLS-1$

        /**
         * The allow read element identifier.
         */
        String READABLE = "allow-read"; //$NON-NLS-1$

        /**
         * The resource name element identifier.
         */
        String RESOURCE_NAME = "resource-name"; //$NON-NLS-1$

        /**
         * The allow update element identifier.
         */
        String UPDATABLE = "allow-update"; //$NON-NLS-1$
    }

    /**
     * Data permission property names.
     */
    public interface PropertyName extends VdbObject.PropertyName {

        /**
         * Indicates if the permission resource is alterable.
         */
        String ALTERABLE = Permission.class.getSimpleName() + ".alterable"; //$NON-NLS-1$

        /**
         * The optional permission condition.
         */
        String CONDITION = Permission.class.getSimpleName() + ".condition"; //$NON-NLS-1$

        /**
         * Indicates if the permission resource is creatable.
         */
        String CREATABLE = Permission.class.getSimpleName() + ".creatable"; //$NON-NLS-1$

        /**
         * Indicates if the permission resource is deletable.
         */
        String DELETABLE = Permission.class.getSimpleName() + ".deletable"; //$NON-NLS-1$

        /**
         * Indicates if the permission resource is executable.
         */
        String EXECUTABLE = Permission.class.getSimpleName() + ".executable"; //$NON-NLS-1$

        /**
         * Indicates if the permission resource allows language.
         */
        String LANGUAGABLE = Permission.class.getSimpleName() + ".languagable"; //$NON-NLS-1$

        /**
         * Indicates if the permission resource is readable.
         */
        String READABLE = Permission.class.getSimpleName() + ".readable"; //$NON-NLS-1$

        /**
         * Indicates if the permission resource is updatable.
         */
        String UPDATABLE = Permission.class.getSimpleName() + ".updatable"; //$NON-NLS-1$
    }

    private boolean alterable;
    private String condition;
    private boolean creatable;
    private boolean deletable;
    private boolean executable;
    private boolean languagable;
    private boolean readable;
    private boolean updatable;

    /**
     * @return <code>true</code> if permission resource is alterable
     */
    public boolean alterable() {
        return this.alterable;
    }

    /**
     * @return <code>true</code> if permission resource is creatable
     */
    public boolean creatable() {
        return this.creatable;
    }

    /**
     * @return <code>true</code> if permission resource is deletable
     */
    public boolean deletable() {
        return this.deletable;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object that) {
        if (super.equals(that)) {
            final Permission thatPermission = (Permission)that;

            // @formatter:off
            return ((this.alterable == thatPermission.alterable)
                   && (StringUtil.matches(this.condition, thatPermission.condition))
                   && (this.creatable == thatPermission.creatable)
                   && (this.deletable == thatPermission.deletable)
                   && (this.executable == thatPermission.executable)
                   && (this.languagable == thatPermission.languagable)
                   && (this.readable == thatPermission.readable)
                   && (this.updatable == thatPermission.updatable));
            // @formatter:on
        }

        return false;
    }

    /**
     * @return <code>true</code> if permission resource is executable
     */
    public boolean executable() {
        return this.executable;
    }

    /**
     * @return the condition (can be <code>null</code> or empty)
     */
    public String getCondition() {
        return this.condition;
    }

    /**
     * @return the resource name (which is the identifier)
     * @see VdbObject#getId()
     */
    public String getResourceName() {
        return getId();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(super.hashCode(),
                                this.alterable,
                                this.condition,
                                this.creatable,
                                this.deletable,
                                this.languagable,
                                this.executable,
                                this.readable,
                                this.updatable);
    }

    /**
     * @return <code>true</code> if permission resource allows language
     */
    public boolean languagable() {
        return this.languagable;
    }

    /**
     * @return <code>true</code> if permission resource is readable
     */
    public boolean readable() {
        return this.readable;
    }

    /**
     * Generates a property change event if the alterable flag is changed.
     * 
     * @param newAlterable the new alterable value
     */
    public void setAlterable(final boolean newAlterable) {
        if (this.alterable != newAlterable) {
            final boolean oldValue = this.alterable;
            this.alterable = newAlterable;
            firePropertyChangeEvent(PropertyName.ALTERABLE, oldValue, this.alterable);

            assert (this.alterable == newAlterable);
            assert (this.alterable != oldValue);
        }
    }

    /**
     * Generates a property change event if the condition is changed.
     * 
     * @param newCondition the new condition (can be <code>null</code> or empty)
     */
    public void setCondition(final String newCondition) {
        if (!StringUtil.matches(this.condition, newCondition)) {
            final String oldValue = this.condition;
            this.condition = newCondition;
            firePropertyChangeEvent(PropertyName.CONDITION, oldValue, this.condition);

            assert StringUtil.matches(this.condition, newCondition);
            assert !StringUtil.matches(this.condition, oldValue);
        }
    }

    /**
     * Generates a property change event if the alterable flag is changed.
     * 
     * @param newCreatable the new creatable value
     */
    public void setCreatable(final boolean newCreatable) {
        if (this.creatable != newCreatable) {
            final boolean oldValue = this.creatable;
            this.creatable = newCreatable;
            firePropertyChangeEvent(PropertyName.CREATABLE, oldValue, this.creatable);

            assert (this.creatable == newCreatable);
            assert (this.creatable != oldValue);
        }
    }

    /**
     * Generates a property change event if the deletable flag is changed.
     * 
     * @param newDeletable the new deletable value
     */
    public void setDeletable(final boolean newDeletable) {
        if (this.deletable != newDeletable) {
            final boolean oldValue = this.deletable;
            this.deletable = newDeletable;
            firePropertyChangeEvent(PropertyName.DELETABLE, oldValue, this.deletable);

            assert (this.deletable == newDeletable);
            assert (this.deletable != oldValue);
        }
    }

    /**
     * Generates a property change event if the executable flag is changed.
     * 
     * @param newExecutable the new executable value
     */
    public void setExecutable(final boolean newExecutable) {
        if (this.executable != newExecutable) {
            final boolean oldValue = this.executable;
            this.executable = newExecutable;
            firePropertyChangeEvent(PropertyName.EXECUTABLE, oldValue, this.executable);

            assert (this.executable == newExecutable);
            assert (this.executable != oldValue);
        }
    }

    /**
     * Generates a property change event if the allows language flag is changed.
     * 
     * @param newLanguagable the new allows language value
     */
    public void setLanguagable(final boolean newLanguagable) {
        if (this.languagable != newLanguagable) {
            final boolean oldValue = this.languagable;
            this.languagable = newLanguagable;
            firePropertyChangeEvent(PropertyName.LANGUAGABLE, oldValue, this.languagable);

            assert (this.languagable == newLanguagable);
            assert (this.languagable != oldValue);
        }
    }

    /**
     * Generates a property change event if the readable flag is changed.
     * 
     * @param newReadable the new readable value
     */
    public void setReadable(final boolean newReadable) {
        if (this.readable != newReadable) {
            final boolean oldValue = this.readable;
            this.readable = newReadable;
            firePropertyChangeEvent(PropertyName.READABLE, oldValue, this.readable);

            assert (this.readable == newReadable);
            assert (this.readable != oldValue);
        }
    }

    /**
     * Generates a property change event of type {@link VdbObject.PropertyName#ID} if the resource name is changed.
     * 
     * @param newResourceName the new resource name (which is the identifier)
     * @see VdbObject#setId(String)
     */
    public void setResourceName(final String newResourceName) {
        setId(newResourceName);
    }

    /**
     * Generates a property change event if the updatable flag is changed.
     * 
     * @param newUpdatable the new updatable value
     */
    public void setUpdatable(final boolean newUpdatable) {
        if (this.updatable != newUpdatable) {
            final boolean oldValue = this.updatable;
            this.updatable = newUpdatable;
            firePropertyChangeEvent(PropertyName.UPDATABLE, oldValue, this.updatable);

            assert (this.updatable == newUpdatable);
            assert (this.updatable != oldValue);
        }
    }

    /**
     * @return <code>true</code> if permission resource is updatable
     */
    public boolean updatable() {
        return this.updatable;
    }

}
