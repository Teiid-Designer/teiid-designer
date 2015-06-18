package org.teiid.designer.vdb.manifest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class DataRoleElement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @XmlAttribute( name = "name", required = true )
    private String name;
    
    @XmlAttribute( name = "any-authenticated", required = false )
    private boolean anyAuthenticated;
    
    @XmlAttribute( name = "allow-create-temporary-tables", required = false )
    private boolean allowCreateTempTables;
    
    @XmlAttribute( name = "grant-all", required = false )
    private boolean grantAll;
    
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

    /**
     * @param dataRole
     */
    public DataRoleElement(DataRole dataRole) {
    	super();
    	name = dataRole.getName();
    	anyAuthenticated = dataRole.isAnyAuthenticated();
    	allowCreateTempTables = dataRole.isAllowCreateTempTables();
    	grantAll = dataRole.isGrantAll();
    	description = dataRole.getDescription();
    	for( Permission permission : dataRole.getPermissions() ) {
    		getPermissions().add(new PermissionElement(permission));
    	}
    	mappedRoleNames = new ArrayList<String>(dataRole.getRoleNames().size());
    	for( String name : dataRole.getRoleNames() ) {
    		mappedRoleNames.add(name);
    	}
    }
    
    /**
     * @return anyAuthenticated
     */
    public boolean allowCreateTempTables() {
        return allowCreateTempTables;
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
    
    /**
     * @return anyAuthenticated
     */
    public boolean isAnyAuthenticated() {
        return anyAuthenticated;
    }
    
    /**
     * @return grantAll
     */
    public boolean doGrantAll() {
    	return grantAll;
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