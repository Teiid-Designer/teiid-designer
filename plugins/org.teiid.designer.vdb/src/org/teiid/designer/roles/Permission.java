/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles;

import org.teiid.designer.vdb.VdbUnit;


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
public class Permission extends VdbUnit {

    /**
    <xs:element name="permission" minOccurs="0" maxOccurs="unbounded">
        <xs:complexType>
            <xs:sequence>                            
                <xs:element name="resource-name" type="xs:string"/>
                <xs:sequence>
                     <xs:element name="allow-create" type="xs:boolean" minOccurs="0"/>
                     <xs:element name="allow-read" type="xs:boolean" minOccurs="0"/>
                     <xs:element name="allow-update" type="xs:boolean" minOccurs="0"/>
                     <xs:element name="allow-delete" type="xs:boolean" minOccurs="0"/>
                     <xs:element name="allow-execute" type="xs:boolean" minOccurs="0"/>
                     <xs:element name="allow-alter" type="xs:boolean" minOccurs="0"/>   
                     <xs:element name="condition" minOccurs="0">
                        <xs:complexType>
                            <xs:simpleContent>
                                    <xs:extension base="xs:string">
                                    <xs:attribute name="constraint" type="xs:boolean" default="true"/>
                                </xs:extension>
                            </xs:simpleContent>
                        </xs:complexType>
                     </xs:element>
                     <xs:element name="mask" minOccurs="0">
                        <xs:complexType>
                            <xs:simpleContent>
                                    <xs:extension base="xs:string">
                                    <xs:attribute name="order" type="xs:string"/>
                                </xs:extension>
                            </xs:simpleContent>
                        </xs:complexType>
                     </xs:element>
                     <xs:element name="allow-language" type="xs:boolean" minOccurs="0"/>
                </xs:sequence>
           </xs:sequence>      
        </xs:complexType>
    </xs:element> 
     */

    /**
     * The default value indicating if this permission allows alter. Value is {@value} .
     */
    boolean DEFAULT_ALLOW_ALTER = false;

    /**
     * The default value indicating if this permission allows create. Value is {@value} .
     */
    boolean DEFAULT_ALLOW_CREATE = false;

    /**
     * The default value indicating if this permission allows delete. Value is {@value} .
     */
    boolean DEFAULT_ALLOW_DELETE = false;

    /**
     * The default value indicating if this permission allows execute. Value is {@value} .
     */
    boolean DEFAULT_ALLOW_EXECUTE = false;

    /**
     * The default value indicating if this permission allows language. Value is {@value} .
     */
    boolean DEFAULT_ALLOW_LANGUAGE = false;

    /**
     * The default value indicating if this permission allows read. Value is {@value} .
     */
    boolean DEFAULT_ALLOW_READ = false;

    /**
     * The default value indicating if this permission allows update. Value is {@value} .
     */
    boolean DEFAULT_ALLOW_UPDATE = false;

    /**
     * An empty array of permissions.
     */
    Permission[] NO_PERMISSIONS = new Permission[0];

	private Crud crud;
	private boolean primary;
	private boolean constraint;
	private String condition;
	private int order;
	private String mask;
	private boolean allowLanguage;
	private boolean canFilter;
	private boolean canMask;

	/**
	 * @param targetName
	 */
	public Permission(String targetName) {
		super();
		setName(targetName);
	}
	
	/**
	 * @param permission
	 */
	public Permission(Permission permission) {
		super();
		setName(permission.getName());
		this.crud = permission.getCRUD();
		this.allowLanguage = permission.isAllowLanguage();
		this.condition = permission.getCondition();
		this.mask = permission.getMask();
		setCanFilter(permission.canFilter());
		setCanMask(permission.canMask());
	}
	
	/**
	 * @param targetName
	 * @param createAllowed
	 * @param readAllowed
	 * @param updateAllowed
	 * @param deleteAllowed
	 * @param executeAllowed
	 * @param alterAllowed
	 */
	public Permission(
				String targetName, 
				Boolean createAllowed, 
				Boolean readAllowed, 
				Boolean updateAllowed, 
				Boolean deleteAllowed,
				Boolean executeAllowed,
				Boolean alterAllowed) {
		super();
		setName(targetName);
		this.crud = new Crud(createAllowed, readAllowed, updateAllowed, deleteAllowed, executeAllowed, alterAllowed);
	}
	
	/**
	 * @param targetName
	 * @param crud
	 */
	public Permission(String targetName, Crud crud ) {
		super();
		setName(targetName);
		this.crud = new Crud(crud.c, crud.r, crud.u, crud.d, crud.e, crud.a);
	}

	/**
	 * @return name of permission
	 */
	public String getTargetName() {
	    return getName();
	}

	/**
	 * @return value
	 */
	public Boolean isCreateAllowed() {
		return this.crud.c; // != null ? this.crud.c : Boolean.FALSE;
	}
	
	/**
	 * @param createAllowed
	 */
	public void setCreateAllowed(Boolean createAllowed) {
		this.crud.c = createAllowed;
	}
	
	/**
	 * @return value
	 */
	public Boolean isReadAllowed() {
		return this.crud.r; // != null ? this.crud.r : Boolean.FALSE;
	}
	
	/**
	 * @param readAllowed
	 */
	public void setReadAllowed(Boolean readAllowed) {
		this.crud.r = readAllowed;
	}
	
	/**
	 * @return value
	 */
	public Boolean isUpdateAllowed() {
		return this.crud.u; // != null ? this.crud.u : Boolean.FALSE;
	}
	
	/**
	 * @param updateAllowed
	 */
	public void setUpdateAllowed(Boolean updateAllowed) {
		this.crud.u = updateAllowed;
	}
	
	/**
	 * @return value
	 */
	public Boolean isDeleteAllowed() {
		return this.crud.d; // != null ? this.crud.d : Boolean.FALSE;
	}
	
	/**
	 * @param deleteAllowed
	 */
	public void setDeleteAllowed(Boolean deleteAllowed) {
		this.crud.d = deleteAllowed;
	}
	
	/**
	 * @return value
	 */
	public Boolean isExecuteAllowed() {
		return this.crud.e; // != null ? this.crud.e : Boolean.FALSE;
	}
	
	/**
	 * @param executeAllowed
	 */
	public void setExecuteAllowed(Boolean executeAllowed) {
		this.crud.e = executeAllowed;
	}
	
	/**
	 * @return value
	 */
	public Boolean isAlterAllowed() {
		return this.crud.a; // != null ? this.crud.a : Boolean.FALSE;
	}
	
	/**
	 * @param alterAllowed
	 */
	public void setAlterAllowed(Boolean alterAllowed) {
		this.crud.a = alterAllowed;
	}
	
	/**
	 * @return value
	 */
	public Crud getCRUD() {
		return new Crud(this.crud);
	}
	
	/**
	 * @param c
	 * @param r
	 * @param u
	 * @param d
	 * @param e
	 * @param a
	 */
	public void setCRUD(Boolean c, Boolean r, Boolean u, Boolean d, Boolean e, Boolean a) {
		this.crud = new Crud(c,r,u,d,e,a);
	}
	
	/**
	 * @param value
	 * @param type
	 */
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
	
	/**
	 * @param type
	 * @return value
	 */
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
	
	/**
	 * @param type
	 */
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
	
	/**
	 * @param parentValue
	 * @param childValue
	 * @return value
	 */
	public boolean childCrudValueIsDifferent(Boolean parentValue, Boolean childValue) {
		if( parentValue == null ) return true;
		if( parentValue == Boolean.TRUE && childValue == null) return false;
		if( parentValue == Boolean.TRUE && childValue == Boolean.TRUE) return false;
		
		return true;
	}
	
	/**
	 * @param permission
	 * @return value
	 */
	public Boolean isEquivalentCRUD(Permission permission) {
		return this.crud.equivalent(permission.getCRUD());
	}
	
	/**
	 * @return value
	 */
	public boolean isNullCrud() {
		return this.crud.c == null && this.crud.r == null && this.crud.u == null && this.crud.d == null && this.crud.e == null && this.crud.a == null;
	}
	
	/**
	 * @return value
	 */
	public boolean isFalseCrud() {
		return this.crud.c == Boolean.FALSE && this.crud.r == Boolean.FALSE && 
			   this.crud.u == Boolean.FALSE && this.crud.d == Boolean.FALSE && 
			   this.crud.e == Boolean.FALSE && this.crud.a == Boolean.FALSE;
	}

	/** (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof Permission ) {
			Permission perm = (Permission)obj;
			if( perm.getName().equalsIgnoreCase(this.getName())) {
				return perm.getCRUD().isSameAs(this.getCRUD());
			}
		}
		return false;
	}
	
	
	/**
	 * @return value
	 */
	public boolean isPrimary() {
		return primary;
	}


	/**
	 * @param primary
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}




	/** (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Permission: ").append(getName()); //$NON-NLS-1$
		sb.append("\n\t").append("c = " + this.crud.c.booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("\n\t").append("r = " + this.crud.r.booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("\n\t").append("u = " + this.crud.u.booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("\n\t").append("d = " + this.crud.d.booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("\n\t").append("e = " + this.crud.e.booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("\n\t").append("a = " + this.crud.a.booleanValue()); //$NON-NLS-1$ //$NON-NLS-2$
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
	
	/**
	 * @return value
	 */
	public boolean canMask() {
		return canMask;
	}
	
	/**
	 * @param canMask
	 */
	public void setCanMask(boolean canMask) {
		this.canMask = canMask;
	}
	
	/**
	 * @return value
	 */
	public boolean canFilter() {
		return canFilter;
	}
	
	/**
	 * @param canFilter
	 */
	public void setCanFilter(boolean canFilter) {
		this.canFilter = canFilter;
	}
	
	
}
