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

import org.eclipse.core.runtime.IProgressMonitor;
import org.teiid.designer.roles.DataRole;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.vdb.Vdb.Event;
import org.teiid.designer.vdb.manifest.DataRoleElement;
import org.teiid.designer.vdb.manifest.PermissionElement;

import com.metamatrix.core.util.StringUtilities;

/**
 *
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class VdbDataRole {
	
    private final Vdb vdb;

    private final String name;
    
    final AtomicReference<String> description = new AtomicReference<String>();
    private List<Permission> permissions = new ArrayList<Permission>();
    
    private List<String> mappedRoleNames = new ArrayList<String>();

    
    VdbDataRole(final Vdb vdb,
    		final DataRole dataRole,
    		final IProgressMonitor monitor) {
    	 super();
    	 
    	 this.vdb = vdb;
    	 name = dataRole.getName();
    	 permissions = new ArrayList(dataRole.getPermissions());
    	 mappedRoleNames = new ArrayList(dataRole.getRoleNames());
         this.description.set(dataRole.getDescription() == null ? StringUtilities.EMPTY_STRING : dataRole.getDescription());
	}
    
    VdbDataRole(final Vdb vdb,
    		final DataRoleElement element) {
    	 super();
    	 this.vdb = vdb;
    	 this.name = element.getName();
    	 
         this.description.set(element.getDescription() == null ? StringUtilities.EMPTY_STRING : element.getDescription());
    	 
         for( PermissionElement perm : element.getPermissions()) {
    		 permissions.add(new Permission(perm.getResourceName(), perm.isCreate(), perm.isRead(), perm.isUpdate(), perm.isDelete()));
    	 }
    	 
    	 mappedRoleNames = new ArrayList(element.getMappedRoleNames());
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
