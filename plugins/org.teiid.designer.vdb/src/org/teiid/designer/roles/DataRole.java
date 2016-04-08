/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.vdb.VdbUnit;
import org.teiid.designer.vdb.manifest.ConditionElement;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.MaskElement;
import org.teiid.designer.vdb.manifest.PermissionElement;

/**
 *
 */
public class DataRole extends VdbUnit {

    /**
     * The default value for the <code>create temporary tables</code> property. Value is {@value} .
     */
    public boolean DEFAULT_ALLOW_CREATE_TEMP_TABLES = false;

    /**
     * The default value for the <code>any authenticated</code> property. Value is {@value} .
     */
    public boolean DEFAULT_ANY_AUTHENTICATED = false;

    /**
     * The default value for the <code>grant all</code> property. Value is {@value} .
     */
    public boolean DEFAULT_GRANT_ALL = false;
    
    @SuppressWarnings("javadoc")
	public static final String SYS_ADMIN_TABLE_TARGET = "sysadmin"; //$NON-NLS-1$
    @SuppressWarnings("javadoc")
	public static final String SYS_TABLE_TARGET = "sys"; //$NON-NLS-1$

    /**
     * An empty array of data roles.
     */
    DataRole[] NO_DATA_ROLES = new DataRole[0];

    /**
    <xs:element name="data-role" minOccurs="0" maxOccurs="unbounded">
             <xs:annotation>
                 <xs:documentation>This defines a data role. The "resource-name" element defines fully qualified 
                 name for a Schema, Table, Procedure, etc.  The "allows-*" elements define the security access, which are applied to the resource
                 and all child resources.  The "mapped-role-name" defines the "role" name that user must have before they have access to this data. 
                 The "role" name matches to the JAAS based role that user has.</xs:documentation>
             </xs:annotation>                 
         
            <xs:complexType>
                <xs:sequence>
                    <xs:element name="description" type="xs:string" minOccurs="0"/>
                     <xs:element name="permission" minOccurs="0" maxOccurs="unbounded"></xs:element>                                                                      
                     <xs:element name="mapped-role-name" type="xs:string" minOccurs="0" maxOccurs="unbounded"/>
                </xs:sequence>
                <xs:attribute name="name" type="xs:string" use="required"/>
                <xs:attribute name="any-authenticated" type="xs:boolean"/>
                <xs:attribute name="allow-create-temporary-tables" type="xs:boolean"/>
                <xs:attribute name="grant-all" type="xs:boolean"/>
            </xs:complexType>
       </xs:element>
     */

    // grant-all is an attribute associated with a data role in the vdb.xml definition
    private Boolean grantAll = Boolean.FALSE;

    private Set<String> roleNames;

    private Map<String, Permission> permissions;

    private Boolean anyAuthenticated;

    private Boolean allowCreateTempTables;

    /**
     * @param name
     */
    public DataRole(String name) {
        super();
        setName(name);
        this.roleNames = new LinkedHashSet<String>();
        this.permissions = new LinkedHashMap<String, Permission>();
    }

    /**
     * @param dataRole
     */
    public DataRole(DataRole dataRole) {
        super();
        setVdb(dataRole.getVdb());
        setName(dataRole.getName());
        setDescription(dataRole.getDescription());
        setGrantAll(dataRole.isGrantAll());
        setRoleNames(dataRole.getRoleNames());
        setPermissions(dataRole.getPermissions());
        setAnyAuthenticated(dataRole.isAnyAuthenticated());
        setAllowCreateTempTables(dataRole.isAllowCreateTempTables());
    }

    /**
     * @param element
     */
    public DataRole(final DataRoleElement element) {
        this(element.getName());
        setAnyAuthenticated(element.isAnyAuthenticated());
        setAllowCreateTempTables(element.allowCreateTempTables());
        setGrantAll(element.doGrantAll() == null ? Boolean.FALSE : element.doGrantAll());
        setDescription(element.getDescription() == null ? EMPTY_STRING : element.getDescription());
        setRoleNames(element.getMappedRoleNames());

        for (PermissionElement pe : element.getPermissions()) {
            boolean allow = false;

            if (pe != null) {
                allow = pe.isAllowLanguage();
            }

            Permission permission = new Permission(pe.getResourceName(), pe.isCreate(), pe.isRead(), pe.isUpdate(),
                                                   pe.isDelete(), pe.isExecute(), pe.isAlter());

            ConditionElement condition = pe.getCondition();
            if (condition != null) {
                permission.setCondition(condition.getSql());
                if (!condition.getConstraint()) {
                    permission.setConstraint(condition.getConstraint());
                }
            }

            MaskElement mask = pe.getMask();
            if (mask != null) {
                permission.setMask(mask.getSql());
                if (mask.getOrder() != null) {
                    permission.setOrder(Integer.valueOf(mask.getOrder()));
                }
            }

            if (allow) {
                permission.setAllowLanguage(true);
            }

            addPermission(permission);
        }
    }

    /**
     * @param name
     * @param description
     * @param anyAuthenticated
     * @param allowCreateTempTables
     * @param grantAll
     * @param roleNames
     * @param permissions
     */
    public DataRole(String name, String description, Boolean anyAuthenticated, Boolean allowCreateTempTables, Boolean grantAll, Collection<String> roleNames, Collection<Permission> permissions) {
        super();
        setName(name);
        setDescription(description);
        this.anyAuthenticated = anyAuthenticated;
        this.allowCreateTempTables = allowCreateTempTables;
        this.grantAll = grantAll;
        this.roleNames = new HashSet<String>(roleNames);
        setPermissions(permissions);
    }

    /**
     * @return permissions
     */
    public Collection<Permission> getPermissions() {
        return Collections.unmodifiableCollection(permissions.values());
    }

    /**
     * @param name name of permission
     * @return permission with given name
     */
    public Permission getPermission(String name) {
        return this.permissions.get(name);
    }

    /**
     * @param permissions
     */
    public void setPermissions(Collection<Permission> permissions) {
        if (this.permissions == null)
            this.permissions = new HashMap<String, Permission>();
        else
            this.permissions.clear();

        if (permissions != null) {
            for (Permission next : permissions) {
                // Need to create new instance of this permission
                Permission perm = next.clone();
                this.permissions.put(perm.getTargetName(), perm);
            }
        }
    }

    /**
     * @param targetName
     * @param createAllowed
     * @param readAllowed
     * @param updateAllowed
     * @param deleteAllowed
     * @param executeAllowed
     * @param alterAllowed
     * @return new permission
     */
    public Permission createPermission(final String targetName, final Boolean createAllowed,
                                                                   final Boolean readAllowed, final Boolean updateAllowed,
                                                                   final Boolean deleteAllowed, final Boolean executeAllowed,
                                                                   final Boolean alterAllowed) {

        Permission perm = new Permission(targetName, createAllowed, readAllowed,
                                                                      updateAllowed, deleteAllowed, executeAllowed,
                                                                      alterAllowed);

        addPermission(perm);
        return perm;
    }

    /**
     * @param permission
     */
    public void addPermission(Permission permission) {
        CoreArgCheck.isNotNull(permission, "permission"); //$NON-NLS-1$
        this.permissions.put(permission.getTargetName(), permission);
        setChanged(true);
    }

    /**
     * @param permission
     */
    public void removePermission(Permission permission) {
        CoreArgCheck.isNotNull(permission, "permission"); //$NON-NLS-1$
        if( permissions.remove(permission) != null ) {
            setChanged(true);
        }
    }

    /**
     * @return grant all
     */
    public Boolean isGrantAll() {
        return this.grantAll;
    }

    /**
     * @param newGrantAll
     */
    public void setGrantAll(Boolean newGrantAll) {
        if( this.grantAll != newGrantAll) {
            this.grantAll = newGrantAll;
            setChanged(true);
        }
    }

    /**
     * @return is any authenticated
     */
    public Boolean isAnyAuthenticated() {
        return this.anyAuthenticated;
    }

    /**
     * @param newAnyAuthenticated
     */
    public void setAnyAuthenticated(Boolean newAnyAuthenticated) {
        if( this.anyAuthenticated != newAnyAuthenticated) {
            this.anyAuthenticated = newAnyAuthenticated;
            setChanged(true);
        }
    }

    /**
     * @return role name
     */
    public Collection<String> getRoleNames() {
        return roleNames;
    }

    /**
     * @param roleNames
     */
    public void setRoleNames(Collection<String> roleNames) {
        CoreArgCheck.isNotNull(roleNames, "roleNames"); //$NON-NLS-1$
        this.roleNames = new HashSet<String>(roleNames);
    }

    /**
     * @param roleName
     */
    public void addRoleName(String roleName) {
        if( ! roleNames.contains(roleName) ) {
            roleNames.add(roleName);
            setChanged(true);
        }
    }

    /**
     * @param roleNameToRemove
     *        the name of the role being removed (cannot be empty)
     */
    public void removeRoleName( final String roleNameToRemove ) {
        if( roleNames.remove(roleNameToRemove) ) {
            setChanged(true);
        }
    }

    /**
     * @return allow create temp tables
     */
    public Boolean isAllowCreateTempTables() {
        return this.allowCreateTempTables;
    }

    /**
     * @param newAllowCreateTempTables
     */
    public void setAllowCreateTempTables(Boolean newAllowCreateTempTables) {
        if( this.allowCreateTempTables != newAllowCreateTempTables) {
            this.allowCreateTempTables = newAllowCreateTempTables;
            setChanged(true);
        }
    }


    /** 
     * @see java.lang.Object#clone()
     */
    @Override
    public DataRole clone() {
        DataRole clone = new DataRole(getName());
        cloneVdbObject(clone);
        clone.setAnyAuthenticated(isAnyAuthenticated());
        clone.setAllowCreateTempTables(isAllowCreateTempTables());
        clone.setGrantAll(isGrantAll());
        if( getDescription() != null ) {
        	clone.setDescription(getDescription());
        }

        for (String roleName : getRoleNames()) {
            clone.addRoleName(roleName);
        }

        for (Permission permission : getPermissions()) {
            clone.addPermission(permission.clone());
        }

        return clone;
    }
}