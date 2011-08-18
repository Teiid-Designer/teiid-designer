/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles;


/**
 * Permission for a given relational model object reflects its data access entitlements
 * 
 * Each object can have Create, Read, Update and Delete boolean values.
 * 
 * None of the values need to be present, so Boolean is used to handle the "null" state.
 *
 *
 */
public class Permission {

	private String targetName;
	private Crud crud;
	private boolean primary;

	public Permission(String targetName) {
		super();
		this.targetName = targetName;
	}
	
	public Permission(Permission permission) {
		super();
		this.targetName = permission.getTargetName();
		this.crud = permission.getCRUD();
	}
	
	public Permission(
				String targetName, 
				Boolean createAllowed, 
				Boolean readAllowed, 
				Boolean updateAllowed, 
				Boolean deleteAllowed,
				Boolean executeAllowed,
				Boolean alterAllowed) {
		super();
		this.targetName = targetName;
		this.crud = new Crud(createAllowed, readAllowed, updateAllowed, deleteAllowed, executeAllowed, alterAllowed);
	}
	
	public Permission(String targetName, Crud crud ) {
		super();
		this.targetName = targetName;
		this.crud = new Crud(crud.c, crud.r, crud.u, crud.d, crud.e, crud.a);
	}


	
	public String getTargetName() {
		return this.targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Boolean isCreateAllowed() {
		return this.crud.c;
	}
	
	public void setCreateAllowed(Boolean createAllowed) {
		this.crud.c = createAllowed;
	}
	
	public Boolean isReadAllowed() {
		return this.crud.r;
	}
	
	public void setReadAllowed(Boolean readAllowed) {
		this.crud.r = readAllowed;
	}
	
	public Boolean isUpdateAllowed() {
		return this.crud.u;
	}
	
	public void setUpdateAllowed(Boolean updateAllowed) {
		this.crud.u = updateAllowed;
	}
	
	public Boolean isDeleteAllowed() {
		return this.crud.d;
	}
	
	public void setDeleteAllowed(Boolean deleteAllowed) {
		this.crud.d = deleteAllowed;
	}
	
	public Boolean isExecuteAllowed() {
		return this.crud.e;
	}
	
	public void setExecuteAllowed(Boolean executeAllowed) {
		this.crud.e = executeAllowed;
	}
	
	public Boolean isAlterAllowed() {
		return this.crud.a;
	}
	
	public void setAlterAllowed(Boolean alterAllowed) {
		this.crud.a = alterAllowed;
	}
	
	public Crud getCRUD() {
		return new Crud(this.crud);
	}
	
	public void setCRUD(Boolean c, Boolean r, Boolean u, Boolean d, Boolean e, Boolean a) {
		this.crud = new Crud(c,r,u,d,e,a);
	}
	
	public void setCRUDValue(Boolean value, Crud.Type type) {
		switch(type) {
			case CREATE: setCreateAllowed(value); break;
			case READ: setReadAllowed(value); break;
			case UPDATE: setUpdateAllowed(value); break;
			case DELETE: setDeleteAllowed(value); break;
			case EXECUTE: setExecuteAllowed(value); break;
			case ALTER: setAlterAllowed(value); break;
		}
	}
	
	public Boolean getCRUDValue(Crud.Type  type) {
		switch(type) {
			case CREATE: return isCreateAllowed();
			case READ: return isReadAllowed();
			case UPDATE: return isUpdateAllowed();
			case DELETE: return isDeleteAllowed();
			case EXECUTE: return isExecuteAllowed();
			case ALTER: return isAlterAllowed();
		}
		
		return null;
	}
	
	public void toggleCRUDValue(Crud.Type type) {
		Boolean currentValue = getCRUDValue(type);
		if( currentValue == null || currentValue == Boolean.FALSE ) {
			//System.out.println(" Permission() Target = " + getTargetName() + "  changing TYPE = " + type + "  [" + currentValue + "]  to [" + true + "]");
			setCRUDValue(Boolean.TRUE, type);
		} else {
			//System.out.println(" Permission() Target = " + getTargetName() + "  changing TYPE = " + type + "  [" + currentValue + "]  to [" + false + "]");
			setCRUDValue(Boolean.FALSE, type);
		}
	}
	
	public Boolean isEquivalentCRUD(Permission permission) {
		return this.crud.equivalent(permission.getCRUD());
	}
	
	public boolean isNullCrud() {
		return this.crud.c == null && this.crud.r == null && this.crud.u == null && this.crud.d == null && this.crud.e == null && this.crud.a == null;
	}
	
	public boolean isFalseCrud() {
		return this.crud.c == Boolean.FALSE && this.crud.r == Boolean.FALSE && 
			   this.crud.u == Boolean.FALSE && this.crud.d == Boolean.FALSE && 
			   this.crud.e == Boolean.FALSE && this.crud.a == Boolean.FALSE;
	}

	@Override
	public boolean equals(Object obj) {
		if( obj instanceof Permission ) {
			Permission perm = (Permission)obj;
			if( perm.getTargetName().equalsIgnoreCase(this.getTargetName())) {
				return perm.getCRUD().isSameAs(this.getCRUD());
			}
		}
		return false;
	}
	
	
	public boolean isPrimary() {
		return primary;
	}


	public void setPrimary(boolean primary) {
		this.primary = primary;
	}




	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Permission: ").append(this.targetName); //$NON-NLS-1$
		sb.append("\n\t").append(this.crud.c.booleanValue()); //$NON-NLS-1$
		sb.append("\n\t").append(this.crud.r.booleanValue()); //$NON-NLS-1$
		sb.append("\n\t").append(this.crud.u.booleanValue()); //$NON-NLS-1$
		sb.append("\n\t").append(this.crud.d.booleanValue()); //$NON-NLS-1$
		sb.append("\n\t").append(this.crud.e.booleanValue()); //$NON-NLS-1$
		sb.append("\n\t").append(this.crud.a.booleanValue()); //$NON-NLS-1$
		return super.toString();
	}

	
}
