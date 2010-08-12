package org.teiid.designer.vdb.manifest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.teiid.designer.roles.Permission;
import org.teiid.designer.vdb.VdbDataRole;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class DataRoleElement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @XmlAttribute( name = "name", required = true )
    private String name;
    
    @XmlElement( name = "description" )
    private String description;
	
    @XmlElement( name = "permission", required = true, type = PermissionElement.class )
    private List<PermissionElement> permissions;
    
    @XmlElement( name = "mapped-role-name" )
    private List<String> mappedRoleNames;
    
    /**
     * Used by JAXB when loading a VDB
     */
    public DataRoleElement() {
    }
    
    DataRoleElement(VdbDataRole dataRole) {
    	super();
    	name = dataRole.getName();
    	description = dataRole.getDescription();
    	for( Permission perm : dataRole.getPermissions() ) {
    		getPermissions().add(new PermissionElement(perm));
    	}
    	mappedRoleNames = new ArrayList(dataRole.getMappedRoleNames());
    }
    
    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return mappedRoleNames
     */
    public List<String> getMappedRoleNames() {
        if (mappedRoleNames == null) mappedRoleNames = new ArrayList<String>();
        return mappedRoleNames;
    }
    
    /**
     * @return path
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return permissions
     */
    public List<PermissionElement> getPermissions() {
        if (permissions == null) permissions = new ArrayList<PermissionElement>();
        return permissions;
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