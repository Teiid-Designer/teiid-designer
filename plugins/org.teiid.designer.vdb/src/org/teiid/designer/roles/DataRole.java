/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.teiid.core.designer.util.CoreArgCheck;


/**
 * @since 8.0
 */
public class DataRole {
	
	private String name;
	private boolean anyAuthenticated;
	private boolean allowCreateTempTables;
	
	// grant-all is an attribute associated with a data role in the vdb.xml definition
	private boolean grantAll;
	private String description;
	private Set<String> roleNames;
	private Map<String, Permission> permissionsMap;
	
	public DataRole(String name) {
		super();
		this.name = name;
		this.anyAuthenticated = false;
		this.allowCreateTempTables = false;
		this.grantAll = false;
		this.roleNames = new HashSet<String>();
		this.permissionsMap = new HashMap<String, Permission>();
	}

	public DataRole(String name, String description, boolean anyAuthenticated, 
			boolean allowCreateTempTables, boolean grantAll, Collection<String> roleNames,
			Collection<Permission> permissions) {
		super();
		this.name = name;
		this.anyAuthenticated = anyAuthenticated;
		this.allowCreateTempTables = allowCreateTempTables;
		this.grantAll = grantAll;
		this.description = description;
		this.roleNames = new HashSet<String>(roleNames);
		this.permissionsMap = new HashMap<String, Permission>();

		setPermissions(permissions);
	}
	
	public DataRole(DataRole dataRole) {
		super();
		this.name = dataRole.getName();
		this.anyAuthenticated = dataRole.isAnyAuthenticated();
		this.allowCreateTempTables = dataRole.allowCreateTempTables();
		this.grantAll = dataRole.doGrantAll();
		this.description = dataRole.getDescription();
		this.roleNames = new HashSet<String>(dataRole.getRoleNames());
		this.permissionsMap = new HashMap<String, Permission>();
		
		setPermissions(dataRole.getPermissions());
	}
	
	public Map<String, Permission> getPermissionsMap() {
		return this.permissionsMap;
	}

	public boolean allowCreateTempTables() {
		return this.allowCreateTempTables;
	}
	
	public void setAllowCreateTempTables(boolean value) {
		this.allowCreateTempTables = value;
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
	
	public void removePermission(Permission permission) {
		CoreArgCheck.isNotNull(permission, "permission"); //$NON-NLS-1$
		this.permissionsMap.remove(permission);
	}
	
	public boolean isAnyAuthenticated() {
		return this.anyAuthenticated;
	}
	
	public void setAnyAuthenticated(boolean value) {
		this.anyAuthenticated = value;
	}
	
	public boolean doGrantAll() {
		return this.grantAll;
	}
	
	public void setGrantAll(boolean value) {
		this.grantAll = value;
	}
	
	public List<String> getAllowedLanguages() {
		List<String> allowedLanguages = new ArrayList<String>(10);
		
		for( Permission perm : getPermissions()) {
			if( perm.isAllowLanguage() ) {
				allowedLanguages.add(perm.getTargetName());
			}
		}
		
		return allowedLanguages;
	}
	
	public void addAllowedLanguage(String language) {
		Permission perm = new Permission(language, false, false, false, false, false, false);
		perm.setAllowLanguage(true);
		addPermission(perm);
	}
	
	public void removeAllowedLanguage(String language) {
		this.permissionsMap.remove(language);
	}
	
	public List<Permission> getPermissionsWithRowBasedSecurity() {
		List<Permission> perms = new ArrayList<Permission>(10);
		
		for( Permission perm : getPermissions()) {
			if( perm.getCondition() != null ) {
				perms.add(perm);
			}
		}
		
		return perms;
	}
	
	public Permission getPermission(String targetName) {
		return this.permissionsMap.get(targetName);
	}
	
	public List<Permission> getPermissionsWithColumnMasking() {
		List<Permission> perms = new ArrayList<Permission>(10);
		
		for( Permission perm : getPermissions()) {
			if( perm.getMask() != null ) {
				perms.add(perm);
			}
		}
		
		return perms;
	}
	
	public void removeRowBasedSecurity(Permission permission) {
		permission.setCondition(null);
		permission.setConstraint(true);
	}
	
	public void removeColumnMask(Permission permission) {
		permission.setMask(null);
		permission.setOrder(0);
	}
	
	public void removeColumnMask(String targetName) {
		Permission existingPerm = this.permissionsMap.get(targetName);
		if( existingPerm != null ) {
			existingPerm.setMask(null);
			existingPerm.setOrder(0);
		}
	}
	
	public void setColumnMask(String targetName, String mask, int order) {
		Permission existingPerm = this.permissionsMap.get(targetName);
		if( existingPerm == null ) {
			existingPerm = new Permission(
					targetName, 
					new Crud(Boolean.FALSE, 
							Boolean.FALSE, 
							Boolean.FALSE, 
							Boolean.FALSE, 
							Boolean.FALSE,
							Boolean.FALSE));
			this.permissionsMap.put(targetName, existingPerm);
		}
		existingPerm.setMask(mask);
		existingPerm.setOrder(order);
	}
	
	public void setRowsBasedSecurity(String targetName, String condition, boolean constraint) {
		Permission existingPerm = this.permissionsMap.get(targetName);
		if( existingPerm == null ) {
			existingPerm = new Permission(
					targetName, 
					new Crud(Boolean.FALSE, 
							Boolean.FALSE, 
							Boolean.FALSE, 
							Boolean.FALSE, 
							Boolean.FALSE,
							Boolean.FALSE));
			this.permissionsMap.put(targetName, existingPerm);
		}
		existingPerm.setCondition(condition);
		existingPerm.setConstraint(constraint);
	}
}
