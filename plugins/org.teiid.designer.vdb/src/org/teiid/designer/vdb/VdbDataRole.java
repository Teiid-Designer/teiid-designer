/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.vdb.Vdb.Event;
import org.teiid.designer.vdb.manifest.ConditionElement;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.MaskElement;
import org.teiid.designer.vdb.manifest.PermissionElement;


/**
 *
 *
 * @since 8.0
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class VdbDataRole {
	
    private final Vdb vdb;

    private final String name;
    
    private final boolean anyAuthenticated;
    
    private final boolean allowCreateTempTables;
    
    private final boolean grantAll;
    
    final AtomicReference<String> description = new AtomicReference<String>();
    private List<Permission> permissions = new ArrayList<Permission>();
    
    private List<String> mappedRoleNames = new ArrayList<String>();

    
    /**
     * @param vdb
     * @param dataRole
     */
    public VdbDataRole(final Vdb vdb,
    		final DataRole dataRole) {
    	 super();
    	 
    	 this.vdb = vdb;
    	 name = dataRole.getName();
    	 anyAuthenticated = dataRole.isAnyAuthenticated();
    	 allowCreateTempTables = dataRole.allowCreateTempTables();
    	 grantAll = dataRole.doGrantAll();
    	 permissions = new ArrayList<Permission>(dataRole.getPermissions());
    	 mappedRoleNames = new ArrayList<String>(dataRole.getRoleNames());
         this.description.set(dataRole.getDescription() == null ? StringUtilities.EMPTY_STRING : dataRole.getDescription());
	}
    
    /**
     * @param vdb
     * @param element
     */
    public VdbDataRole(final Vdb vdb,
    		final DataRoleElement element) {
    	 super();
    	 this.vdb = vdb;
    	 this.name = element.getName();
    	 this.anyAuthenticated = element.isAnyAuthenticated();
    	 this.allowCreateTempTables = element.allowCreateTempTables();
    	 this.grantAll = element.doGrantAll();
    	 
         this.description.set(element.getDescription() == null ? StringUtilities.EMPTY_STRING : element.getDescription());
    	 
         for( PermissionElement pe : element.getPermissions()) {
        	 boolean allow = false;
        	 
        	 if( pe != null ) {
        		 allow = pe.isAllowLanguage();
        	 }
        	 
        	 Permission permission = new Permission(pe.getResourceName(), 
        			 pe.isCreate(), pe.isRead(), pe.isUpdate(), pe.isDelete(), pe.isExecute(), pe.isAlter());
 				 
        	 ConditionElement condition = pe.getCondition();
        	 if( condition != null )  {
        		 permission.setCondition(condition.getSql());
        		 if( !condition.getConstraint()) {
        			 permission.setConstraint(condition.getConstraint());
        		 }
        	 }
        	 
        	 MaskElement mask = pe.getMask();
        	 if( mask != null )  {
        		 permission.setMask(mask.getSql());
        		 if( mask.getOrder() != null) {
        			 permission.setOrder(Integer.valueOf(mask.getOrder()));
        		 }
        	 }
        	 
        	 if( allow ) {
        		 permission.setAllowLanguage(true);
        	 }
        	 
    		 permissions.add(permission);
    	 }
    	 
    	 mappedRoleNames = new ArrayList<String>(element.getMappedRoleNames());
	}
    
    /**
     * @return the any-authenticated value
     */
    public boolean allowCreateTempTables() {
    	return this.allowCreateTempTables;
    }
    
    /**
     * @return description
     */
    public final String getDescription() {
        return description.get();
    }
    
    /**
     * @return immutable set of mapped role names associated with this data policy
     */
    public List<String> getMappedRoleNames() {
		return Collections.unmodifiableList(mappedRoleNames);
	}

	/**
     * @return the data policy name
     */
    public String getName() {
    	return this.name;
    }
    
    /**
     * @return the any-authenticated value
     */
    public boolean isAnyAuthenticated() {
    	return this.anyAuthenticated;
    }
    
    /**
     * @return the grant-all value
     */
    public boolean doGrantAll() {
    	return this.grantAll;
    }
    
    /**
     * @return the immutable set of problems associated with this model entry
     */
    public final List<Permission> getPermissions() {
        return Collections.unmodifiableList(permissions);
    }
    
    /**
     * @param description (never <code>null</code>)
     */
    public final void setDescription( String description ) {
        final String oldDescription = this.description.get();
        if (StringUtilities.equals(description, oldDescription)) return;
        this.description.set(description);
        vdb.setModified(this, Event.ENTRY_DESCRIPTION, oldDescription, description);
    }
}
