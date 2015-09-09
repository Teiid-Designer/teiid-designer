package org.teiid.designer.vdb.manifest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.teiid.designer.comments.CommentSets;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class DataRoleElement implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private CommentSets comments;

    @XmlAttribute( name = "name", required = true )
    private String name;
    
    @XmlAttribute( name = "any-authenticated", required = false )
    private Boolean anyAuthenticated;
    
    @XmlAttribute( name = "allow-create-temporary-tables", required = false )
    private Boolean allowCreateTempTables;
    
    @XmlAttribute( name = "grant-all", required = false )
    private Boolean grantAll;
    
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

    	if (dataRole.isAnyAuthenticated() != null)
    	    anyAuthenticated = dataRole.isAnyAuthenticated();

    	if (dataRole.isAllowCreateTempTables() != null)
    	    allowCreateTempTables = dataRole.isAllowCreateTempTables();

    	if (dataRole.isGrantAll() != null)
    	    grantAll = dataRole.isGrantAll();

    	description = dataRole.getDescription();

    	getComments().add(dataRole.getComments());

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
    public Boolean allowCreateTempTables() {
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
    public Boolean isAnyAuthenticated() {
        return anyAuthenticated;
    }
    
    /**
     * @return grantAll
     */
    public Boolean doGrantAll() {
    	return grantAll;
    }

    /**
     * @param visitor
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return comments for this element
     */
    public CommentSets getComments() {
        if (this.comments == null)
            this.comments = new CommentSets();

        return this.comments;
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
