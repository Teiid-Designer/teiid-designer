package org.teiid.adminapi;
/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */


import java.util.List;
import org.teiid.designer.annotation.Since;

public interface DataPolicy {
	
	public enum Context {
		CREATE,
		DROP,
		QUERY,
		INSERT,
		@Since("8.0.0")
		MERGE,
		UPDATE,
		DELETE,
		FUNCTION,
		ALTER,
		STORED_PROCEDURE;
    }
	
	public enum PermissionType {
		CREATE,
		READ,
		UPDATE,
		DELETE,
		ALTER,
		EXECUTE,
		DROP,
		@Since("8.0.0")
		LANGUAGE};
	
	
	/**
	 * Get the Name of the Data Policy
	 * @return
	 */
	String getName();
	
	/**
	 * Get the description of the Data Policy
	 * @return
	 */
	String getDescription();
	
	/**
	 * Get the List of Permissions for this Data Policy.
	 * @return
	 */
	List<DataPermission> getPermissions();
	
	/**
	 * Mapped Container Role names for this Data Policy
	 * @return
	 */
	List<String> getMappedRoleNames();
	
	/**
	 * If the policy applies to any authenticated user
	 * @return
	 */
	boolean isAnyAuthenticated();
	
	/**
	 * If the policy grants all permissions
	 * @return
	 */
	@Since("8.7.0")
	boolean isGrantAll();

	/**
	 * If the policy allows for temporary table usage
	 * @return
	 */
	Boolean isAllowCreateTemporaryTables();
	
	interface DataPermission {
		/**
		 * Get the Resource Name that Data Permission representing
		 * @return
		 */
		String getResourceName();
		
		/**
		 * Is "CREATE" allowed?
		 * @return
		 */
		Boolean getAllowCreate();
		
		/**
		 * Is "SELECT" allowed?
		 * @return
		 */
		Boolean getAllowRead();
		
		/**
		 * Is "INSERT/UPDATE" allowed?
		 * @return
		 */
		Boolean getAllowUpdate();
		
		/**
		 * Is "DELETE" allowed?
		 * @return
		 */
		Boolean getAllowDelete();

		/**
		 * Is "ALTER" allowed?
		 * @return
		 */
		Boolean getAllowAlter();

		/**
		 * Is "EXECUTE" allowed?
		 * @return
		 */
		Boolean getAllowExecute();
		
		/**
		 * Is "LANGUAGE" allowed?
		 * @return
		 */
		@Since("8.0.0")
		Boolean getAllowLanguage();

		/**
		 * The condition string
		 */
		@Since("8.0.0")
		String getCondition();
		
		/**
		 * The column mask string
		 */
		@Since("8.0.0")
		String getMask();

		/**
		 * The column mask order
		 */
		@Since("8.0.0")
		Integer getOrder();
		
		/**
		 * If the condition acts as a constraint.
		 */
		@Since("8.0.0")
		Boolean getConstraint();
		
	}
}
