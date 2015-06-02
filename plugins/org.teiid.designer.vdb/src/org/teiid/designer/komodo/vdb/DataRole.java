/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Represents a VDB data role.
 */
public class DataRole extends VdbObject {
	Map<String, Permission> permissions;
	Set<String> mappedRoleNames;
	boolean anyAuthenticated;
	boolean allowCreateTemporaryTables;
	boolean grantAll;
	
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

    /**
     * The type identifier.
     */
    int TYPE_ID = DataRole.class.hashCode();

    /**
     * Identifier of this object
     */
    TeiidType IDENTIFIER = TeiidType.VDB_DATA_ROLE;

    /**
     * The default value for the <code>create temporary tables</code> property. Value is {@value} .
     */
    boolean DEFAULT_ALLOW_CREATE_TEMP_TABLES = false;

    /**
     * The default value for the <code>any authenticated</code> property. Value is {@value} .
     */
    boolean DEFAULT_ANY_AUTHENTICATED = false;

    /**
     * The default value for the <code>grant all</code> property. Value is {@value} .
     */
    boolean DEFAULT_GRANT_ALL = false;

    /**
     * An empty array of data roles.
     */
    DataRole[] NO_DATA_ROLES = new DataRole[0];

    /**
     * 
     */
    public DataRole () {
    	super();
    	permissions = new HashMap<String, Permission>();
    	mappedRoleNames = new HashSet<String>();
    }
    
    /**
     * @param roleName
     *        the name of the mapped role being added (cannot be empty)
     */
    public void addMappedRole( final String roleName ) {
    	if( ! mappedRoleNames.contains(roleName) ) {
    		mappedRoleNames.add(roleName);
    		setChanged(true);
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
    public Permission createPermission(
			final String targetName, 
			final Boolean createAllowed, 
			final Boolean readAllowed, 
			final Boolean updateAllowed, 
			final Boolean deleteAllowed,
			final Boolean executeAllowed,
			final Boolean alterAllowed ) {
    	
    	Permission perm = new Permission(
    			targetName, 
    			createAllowed, 
    			readAllowed, 
    			updateAllowed, 
    			deleteAllowed,
    			executeAllowed,
    			alterAllowed);
    	
    	addPermission(perm);

    	return perm;
    }
    
    /**
     * @param permission 
     *        the name of the permission being added (cannot be empty)
     */
    public void addPermission( final Permission permission ) {
    	permissions.put(permission.getName(), permission);
    	setChanged(true);
    }


    /**
     * @return the mapped role names (never <code>null</code> but can be empty)
     */
    public String[] getMappedRoles() {
    	return (String[])mappedRoleNames.toArray(new String[mappedRoleNames.size()]);
    }

    /**
     * @return the permissions (never <code>null</code> but can be empty)
     */
    public Permission[] getPermissions() {
    	return (Permission[])permissions.values().toArray(new Permission[permissions.size()]);
    }

    /**
     * @return <code>true</code> if allows creating temporary tables
     * @see #DEFAULT_ALLOW_CREATE_TEMP_TABLES
     */
    public boolean isAllowCreateTempTables() {
    	return this.allowCreateTemporaryTables;
    }

    /**
     * @return <code>true</code> if any authenticated
     * @see #DEFAULT_ANY_AUTHENTICATED
     */
    public boolean isAnyAuthenticated() {
    	return this.anyAuthenticated;
	}

    /**
     * @return <code>true</code> if grant all
     * @see #DEFAULT_GRANT_ALL
     */
    public boolean isGrantAll() {
    	return this.grantAll;
    }

    /**
     * @param roleNameToRemove
     *        the name of the role being removed (cannot be empty)
     */
    public void removeMappedRole( final String roleNameToRemove ) {
    	if( mappedRoleNames.remove(roleNameToRemove) ) {
    		setChanged(true);
    	}
    }

    /**
     * @param permissionToRemove
     *        the name of the permission being removed (cannot be empty)
     */
    public void removePermission( final String permissionToRemove ) {
    	if( permissions.remove(permissionToRemove) != null ) {
    		setChanged(true);
    	}
    }
    

    /**
     * @param newAllowCreateTempTables
     *        the new value for the <code>allow creating temporary tables</code> property
     * @see #DEFAULT_ALLOW_CREATE_TEMP_TABLES
     */
    public void setAllowCreateTempTables( final boolean newAllowCreateTempTables ) {
    	if( this.allowCreateTemporaryTables != newAllowCreateTempTables) {
    		this.allowCreateTemporaryTables = newAllowCreateTempTables;
    		setChanged(true);
    	}
    }

    /**
     * @param newAnyAuthenticated
     *        the new value for the <code>any authenticated</code> property
     * @see #DEFAULT_ANY_AUTHENTICATED
     */
    public void setAnyAuthenticated( final boolean newAnyAuthenticated ) {
    	if( this.anyAuthenticated != newAnyAuthenticated) {
    		this.anyAuthenticated = newAnyAuthenticated;
    		setChanged(true);
    	}
    }
    
    /**
     * @param newGrantAll
     *        the new value for the <code>grant all</code> property
     * @see #DEFAULT_GRANT_ALL
     */
    public void setGrantAll( final boolean newGrantAll ) {
    	if( this.grantAll != newGrantAll) {
    		this.grantAll = newGrantAll;
    		setChanged(true);
    	}
    }

}
