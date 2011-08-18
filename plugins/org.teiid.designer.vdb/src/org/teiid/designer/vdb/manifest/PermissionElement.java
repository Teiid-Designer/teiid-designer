/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.vdb.manifest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.teiid.designer.roles.Permission;

/**
 *
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class PermissionElement implements Serializable {
	
    private static final long serialVersionUID = 1L;

    @XmlElement( name = "resource-name", required = true)
    private String resource_name;
    
    @XmlElement( name = "allow-create")
    private Boolean create;
    
    @XmlElement( name = "allow-read")
    private Boolean read;
    
    @XmlElement( name = "allow-update")
    private Boolean update;
    
    @XmlElement( name = "allow-delete")
    private Boolean delete;
    
    @XmlElement( name = "allow-execute")
    private Boolean execute;
    
    @XmlElement( name = "allow-alter")
    private Boolean alter;
    
    /**
     * Used by JAXB when loading a VDB
     */
    public PermissionElement() {
    }
    
    /**
     * Used by JAXB when loading a VDB
     * @param permission s
     */
    public PermissionElement(Permission permission) {
        super();
        this.resource_name = permission.getTargetName();
        this.create = permission.isCreateAllowed();
        this.read = permission.isReadAllowed();
        this.update = permission.isUpdateAllowed();
        this.delete = permission.isDeleteAllowed();
        this.execute = permission.isExecuteAllowed();
        this.alter = permission.isAlterAllowed();
    }
    
    
    
    /**
     * @return the resource name
     */
    public String getResourceName() {
		return resource_name;
	}

	/**
	 * @return if permission allows create
	 */
	public Boolean isCreate() {
		return create;
	}

	/**
	 * @return if permission allows read
	 */
	public Boolean isRead() {
		return read;
	}

	/**
	 * @return if permission allows update
	 */
	public Boolean isUpdate() {
		return update;
	}

	/**
	 * @return if permission allows delete
	 */
	public Boolean isDelete() {
		return delete;
	}
	
	/**
	 * @return if permission allows execute
	 */
	public Boolean isExecute() {
		return execute;
	}
	
	/**
	 * @return if permission allows alter
	 */
	public Boolean isAlter() {
		return alter;
	}
}

/*
	<data-policy name="HR Model Data Policy">
		<description>Access to HR department to create, read, update and delete personnel records.</description>
		<permission>
		    <resource-name>HROracleAllView</resource-name>
		    <allow-create>TRUE</allow-create>
		    <allow-read>TRUE</allow-read>
		    <allow-update>TRUE</allow-update>
		    <allow-delete>TRUE</allow-delete>
		</permission>
		<permission>
		    <resource-name>HROracleAllView.Management.Payroll</resource-name>
		    <allow-create>FALSE</allow-create>
		    <allow-read>TRUE</allow-read>
		    <allow-update>FALSE</allow-update>
		    <allow-delete>FALSE</allow-delete>
		</permission>
		<permission>
		    <resource-name>HROracleAllView.Salary.Payroll</resource-name>
		    <allow-create>TRUE</allow-create>
		    <allow-read>TRUE</allow-read>
		    <allow-update>TRUE</allow-update>
		    <allow-delete>TRUE</allow-delete>
		</permission>
		<mapped-role-name>Full Personnel Records</mapped-role-name>
	</data-policy>
*/
