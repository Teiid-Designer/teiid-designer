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
 *
 * @since 8.0
 */
public class Permission {

	private String targetName;
	private Crud crud;
	private boolean primary;
	private boolean constraint;
	private String condition;
	private int order;
	private String mask;
	private boolean allowLanguage;
	private boolean canFilter;
	private boolean canMask;
	
	private String NULL = "null"; //$NON-NLS-1$

	public Permission(String targetName) {
		super();
		this.targetName = targetName;
	}
	
	public Permission(Permission permission) {
		super();
		this.targetName = permission.getTargetName();
		this.crud = permission.getCRUD();
		this.constraint = permission.isConstraint();
		this.order = permission.getOrder();
		this.allowLanguage = permission.isAllowLanguage();
		this.condition = permission.getCondition();
		this.mask = permission.getMask();
		setCanFilter(permission.canFilter());
		setCanMask(permission.canMask());
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
		this.constraint = true;
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
		return this.crud.c; // != null ? this.crud.c : Boolean.FALSE;
	}
	
	public void setCreateAllowed(Boolean createAllowed) {
		this.crud.c = createAllowed;
	}
	
	public Boolean isReadAllowed() {
		return this.crud.r; // != null ? this.crud.r : Boolean.FALSE;
	}
	
	public void setReadAllowed(Boolean readAllowed) {
		this.crud.r = readAllowed;
	}
	
	public Boolean isUpdateAllowed() {
		return this.crud.u; // != null ? this.crud.u : Boolean.FALSE;
	}
	
	public void setUpdateAllowed(Boolean updateAllowed) {
		this.crud.u = updateAllowed;
	}
	
	public Boolean isDeleteAllowed() {
		return this.crud.d; // != null ? this.crud.d : Boolean.FALSE;
	}
	
	public void setDeleteAllowed(Boolean deleteAllowed) {
		this.crud.d = deleteAllowed;
	}
	
	public Boolean isExecuteAllowed() {
		return this.crud.e; // != null ? this.crud.e : Boolean.FALSE;
	}
	
	public void setExecuteAllowed(Boolean executeAllowed) {
		this.crud.e = executeAllowed;
	}
	
	public Boolean isAlterAllowed() {
		return this.crud.a; // != null ? this.crud.a : Boolean.FALSE;
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
			case CREATE: 	return isCreateAllowed(); //if( this.crud.c == null ) { return false; } else { return isCreateAllowed(); }
			case READ: 		return isReadAllowed(); //if( this.crud.r == null ) { return false; } else { return isReadAllowed(); }
			case UPDATE: 	return isUpdateAllowed(); //if( this.crud.u == null ) { return false; } else { return isReadAllowed(); }
			case DELETE: 	return isDeleteAllowed(); //if( this.crud.d == null ) { return false; } else { return isDeleteAllowed(); }
			case EXECUTE: 	return isExecuteAllowed(); //if( this.crud.e == null ) { return false; } else { return isExecuteAllowed(); }
			case ALTER: 	return isAlterAllowed(); //if( this.crud.a == null ) { return false; } else { return isAlterAllowed(); }
		}
		
		return null;
	}
	
//	public Boolean getActualCRUDValue(Crud.Type  type) {
//		switch(type) {
//			case CREATE: 	return this.crud.c;
//			case READ: 		return this.crud.r;
//			case UPDATE: 	return this.crud.u;
//			case DELETE: 	return this.crud.d;
//			case EXECUTE: 	return this.crud.e;
//			case ALTER: 	return this.crud.a;
//		}
//		
//		return null;
//	}
	
	public void toggleCRUDValue(Crud.Type type) {
		Boolean currentValue = getCRUDValue(type);
		if( currentValue == Boolean.FALSE ) { //currentValue == null || 
			//System.out.println(" Permission() Target = " + getTargetName() + "  changing TYPE = " + type + "  [" + currentValue + "]  to [" + true + "]");
			setCRUDValue(Boolean.TRUE, type);
		} else {
			//System.out.println(" Permission() Target = " + getTargetName() + "  changing TYPE = " + type + "  [" + currentValue + "]  to [" + false + "]");
			setCRUDValue(Boolean.FALSE, type);
		}
	}
	
	public boolean childCrudValueIsDifferent(Boolean parentValue, Boolean childValue) {
		if( parentValue == null ) return true;
		if( parentValue == Boolean.TRUE && childValue == null) return false;
		if( parentValue == Boolean.TRUE && childValue == Boolean.TRUE) return false;
		if( parentValue == Boolean.FALSE && childValue == Boolean.FALSE) return false; 
		
		return true;
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
		String value = NULL;
		if( this.crud.c != null ) {
			value = Boolean.toString(this.crud.c.booleanValue());
		}
		sb.append("\n\t").append("c = " + value); //$NON-NLS-1$ //$NON-NLS-2$
		value = NULL;
		if( this.crud.r != null ) {
			value = Boolean.toString(this.crud.r.booleanValue());
		}
		sb.append("\n\t").append("r = " + value); //$NON-NLS-1$ //$NON-NLS-2$
		value = NULL;
		if( this.crud.u != null ) {
			value = Boolean.toString(this.crud.u.booleanValue());
		}
		sb.append("\n\t").append("u = " + value); //$NON-NLS-1$ //$NON-NLS-2$
		value = NULL;
		if( this.crud.d != null ) {
			value = Boolean.toString(this.crud.d.booleanValue());
		}
		sb.append("\n\t").append("d = " + value); //$NON-NLS-1$ //$NON-NLS-2$
		value = NULL;
		if( this.crud.e != null ) {
			value = Boolean.toString(this.crud.e.booleanValue());
		}
		sb.append("\n\t").append("e = " + value); //$NON-NLS-1$ //$NON-NLS-2$
		value = NULL;
		if( this.crud.a != null ) {
			value = Boolean.toString(this.crud.a.booleanValue());
		}
		sb.append("\n\t").append("a = " + value); //$NON-NLS-1$ //$NON-NLS-2$

		sb.append("\n\t").append("allow-language = " + allowLanguage); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}
	
	/**
	 * @return the constraint
	 */
	public boolean isConstraint() {
		return this.constraint;
	}

	/**
	 * @param constraint the constraint to set
	 */
	public void setConstraint(boolean constraint) {
		this.constraint = constraint;
	}

	/**
	 * @return the condition
	 */
	public String getCondition() {
		return this.condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	/**
	 * @return the order
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * @return the mask
	 */
	public String getMask() {
		return this.mask;
	}

	/**
	 * @param mask the mask to set
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}
	
	/**
	 * @return allowLanguage
	 */
	public boolean isAllowLanguage() {
		return this.allowLanguage;
	}

	/**
	 * @param allowLanguage the allowLanguage to set
	 */
	public void setAllowLanguage(boolean allowLanguage) {
		this.allowLanguage = allowLanguage;
	}
	
	public boolean canMask() {
		return canMask;
	}
	
	public void setCanMask(boolean canMask) {
		this.canMask = canMask;
	}
	
	public boolean canFilter() {
		return canFilter;
	}
	
	public void setCanFilter(boolean canFilter) {
		this.canFilter = canFilter;
	}
	
	
}
