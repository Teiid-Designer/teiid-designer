/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.metamatrix.core.util.CoreArgCheck;

public class DataRole {
	
	private String name;
	private boolean anyAuthenticated;
	private String description;
	private Set<String> roleNames;
	private Map<String, Permission> permissionsMap;
	
	public DataRole(String name) {
		super();
		this.name = name;
		this.anyAuthenticated = false;
		this.roleNames = new HashSet<String>();
		this.permissionsMap = new HashMap<String, Permission>();
	}

	public DataRole(String name, String description, boolean anyAuthenticated, Collection<String> roleNames, Collection<Permission> permissions) {
		super();
		this.name = name;
		this.anyAuthenticated = anyAuthenticated;
		this.description = description;
		this.roleNames = new HashSet<String>(roleNames);
		this.permissionsMap = new HashMap<String, Permission>();
		
		setPermissions(permissions);
	}
	
	public DataRole(DataRole dataRole) {
		super();
		this.name = dataRole.getName();
		this.anyAuthenticated = dataRole.isAnyAuthenticated();
		this.description = dataRole.getDescription();
		this.roleNames = new HashSet<String>(dataRole.getRoleNames());
		this.permissionsMap = new HashMap<String, Permission>();
		
		setPermissions(dataRole.getPermissions());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
		this.name = name;
	}
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(Collection<String> roleNames) {
		CoreArgCheck.isNotEmpty(roleNames, "roleNames"); //$NON-NLS-1$
		this.roleNames = new HashSet<String>(roleNames);
	}
	
	public void addRoleName(String roleName) {
		this.roleNames.add(roleName);
	}

	public Collection<Permission> getPermissions() {
		return permissionsMap.values();
	}

	public void setPermissions(Collection<Permission> permissions) {
		this.permissionsMap.clear();
		
		if( permissions != null ) {
			for( Permission next : permissions ) {
				// Need to create new instance of this permission
				Permission perm = new Permission(next);
				this.permissionsMap.put(perm.getTargetName(), perm);
			}
		}
	}
	
	public void addPermission(Permission permission) {
		CoreArgCheck.isNotNull(permission, "permission"); //$NON-NLS-1$
		this.permissionsMap.put(permission.getTargetName(), permission);
	}
	
	public boolean isAnyAuthenticated() {
		return this.anyAuthenticated;
	}
	
	public void setAnyAuthenticated(boolean value) {
		this.anyAuthenticated = value;
	}
}
