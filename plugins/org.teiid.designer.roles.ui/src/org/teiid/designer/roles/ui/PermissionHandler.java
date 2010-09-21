/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.roles.Crud;
import org.teiid.designer.roles.Permission;

import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;

public class PermissionHandler {

	private DataRolesModelTreeProvider tree;
	private Map<Object, Permission> permissionsMap;
	
	public PermissionHandler(DataRolesModelTreeProvider tree, Map<Object, Permission> permissionsMap) {
		super();
		this.tree = tree;
		this.permissionsMap = permissionsMap;
	}
	
	/*
	 * Because child permission values can default to parent Crud values there may be instances where a permission's CRUD
	 * either exactly matches values from parents, or the CRUD is all NULL. In both cases we need to remove these Permissions
	 * from the cache.
	 * 
	 */
	private void cleanUpPermissions(Object changedElement) {
		
		// walk the tree, 
		Permission perm = permissionsMap.get(changedElement);
		if( perm == null || perm.isPrimary() ) {
			return;
		}
		
		if( perm.isNullCrud() ) {
			//System.out.println(" Removing Stale Permission for: " + perm.getTargetName());
			permissionsMap.remove(changedElement); //stalePermissionKeys.add(changedElement);
		} else {
			boolean sameCreate = false;
			boolean sameRead = false;
			boolean sameUpdate = false;
			boolean sameDelete = false;
			// look at all 4 CRUD values and their Parent Perms and if they are the same as the CRUD value
			Permission parentPerm = getFirstParentPermission(changedElement, Crud.Type.CREATE);
			if( parentPerm != null ) {
				if( (perm.getCRUDValue(Crud.Type.CREATE) == parentPerm.getCRUDValue(Crud.Type.CREATE)) ||
					(perm.getCRUDValue(Crud.Type.CREATE) == null && parentPerm.getCRUDValue(Crud.Type.CREATE) != null) ) {
					sameCreate = true;
				}
			}
			if( !sameCreate ) return;
			
			parentPerm = getFirstParentPermission(changedElement, Crud.Type.READ);
			if( parentPerm != null ) {
				if( (perm.getCRUDValue(Crud.Type.READ) == parentPerm.getCRUDValue(Crud.Type.READ)) ||
					(perm.getCRUDValue(Crud.Type.READ) == null && parentPerm.getCRUDValue(Crud.Type.READ) != null) ) {
					sameRead = true;
				}
			}
			if( !sameRead ) return;
			
			parentPerm = getFirstParentPermission(changedElement, Crud.Type.UPDATE);
			if( parentPerm != null ) {
				if( (perm.getCRUDValue(Crud.Type.UPDATE) == parentPerm.getCRUDValue(Crud.Type.UPDATE)) ||
					(perm.getCRUDValue(Crud.Type.UPDATE) == null && parentPerm.getCRUDValue(Crud.Type.UPDATE) != null) ) {
					sameUpdate = true;
				}
			}
			if( !sameUpdate ) return;
			
			parentPerm = getFirstParentPermission(changedElement, Crud.Type.DELETE);
			if( parentPerm != null ) {
				if( (perm.getCRUDValue(Crud.Type.DELETE) == parentPerm.getCRUDValue(Crud.Type.DELETE)) ||
					(perm.getCRUDValue(Crud.Type.DELETE) == null && parentPerm.getCRUDValue(Crud.Type.DELETE) != null) ) {
					sameDelete = true;
				}
			}
			
			if( sameCreate && sameRead && sameUpdate && sameDelete ) {
				//System.out.println(" Removing Stale Permission for: " + perm.getTargetName());
				permissionsMap.remove(changedElement);
				//stalePermissionKeys.add(changedElement);
			}
		}
	}
	
	/*
	 * Gather up all child and grand-child permissions
	 */
	private void getChildPermissions(Object parent, Collection<Permission> allChildPermissions) {

		for( Object child : tree.getChildren(parent) ) {
			Permission perm = this.permissionsMap.get(child);
			if( perm != null ) {
				allChildPermissions.add(perm);
			}
			getChildPermissions(child, allChildPermissions);
		}
	}
	
	/*
	 * Gather all children below the parent that have an associated permission.
	 * 
	 */
	private void getChildrenWithPermission(Object parent, Collection<Object> childrenWithPermission) {
		for( Object child : tree.getChildren(parent) ) {
			Permission perm = this.permissionsMap.get(child);
			if( perm != null ) {
				childrenWithPermission.add(child);
			}
			getChildrenWithPermission(child, childrenWithPermission);
		}
	}
	
	/**
	 * Finds the first parent permission which contains a non-null Boolean crud value
	 * @param element
	 * @param crudType
	 * @return
	 */
	public Permission getExistingPermission(Object element, Crud.Type crudType) {
		Permission perm = this.permissionsMap.get(element);
		if( perm == null || perm.getCRUDValue(crudType) == null ) {
			Object parent = tree.getParent(element);
			while( parent != null && (perm == null || perm.getCRUDValue(crudType) == null) ) {
				perm = this.permissionsMap.get(parent);
				parent = tree.getParent(parent);
			}
		}
		
		return perm;
	}
	
	/*
	 * Find the first parent above the element with an NON-NULL boolean value for the specified CRUD type
	 * 
	 */
	private Permission getFirstParentPermission(Object element, Crud.Type crudType) {
		Permission perm = null;
		Object parent = tree.getParent(element);
		while( parent != null && (perm == null || perm.getCRUDValue(crudType) == null) ) {
			perm = this.permissionsMap.get(parent);
			parent = tree.getParent(parent);
		}
		return perm;
	}
	
	public Permission getPermission(Object element) {
		return this.permissionsMap.get(element);
	}
	
	/**
	 * Helper method to determine if any child below a parent contains a CRUD value different than the parent. 
	 * 
	 */
	public boolean hasChildWithDifferentCrudValue(Permission parentPermission, Object parent, Crud.Type type) {
		Boolean parentValue = parentPermission.getCRUDValue(type);

		Collection<Permission> childPermissions = new ArrayList<Permission>();
		
		getChildPermissions(parent, childPermissions);
		
		for( Permission perm : childPermissions ) {
			Boolean value = perm.getCRUDValue(type);
			if( value != null && value != parentValue ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPermissions() {
		return !this.permissionsMap.isEmpty();
	}
	
	public void addPermission(Object key, Permission perm) {
		if( !this.permissionsMap.containsKey(key)) {
			this.permissionsMap.put(key, perm);
		}
	}
	
	/**
	 * Performs the necessary permission CRUD value changes based on the target element and the CRUD type.
	 * This method is targeted for use by a single-click editor changing ONE CRUD boolean value for one object.
	 * 
	 * @param element
	 * @param crudType
	 */
	public void togglePermission( Object element, Crud.Type crudType ) {
		if( ! supportsUpdates(element, crudType) ) {
			return;
		}
		Permission targetPermission = this.permissionsMap.get(element);

		if( targetPermission == null ) {
			Permission parentPermission = getExistingPermission(element, crudType);
			
			// Now create New permission with NULL values
			Crud targetCrud = new Crud(null, null, null, null);
			targetPermission = new Permission(tree.getTargetName(element), targetCrud);
			// Set the target permission crud value to parent permission crud value
			// ONLY if the parent crud value == TRUE, else we can't override the parent.
			if( parentPermission.getCRUDValue(crudType) == Boolean.TRUE) {
				targetPermission.setCRUDValue(parentPermission.getCRUDValue(crudType), crudType);
				permissionsMap.put(element, targetPermission);
				targetPermission.toggleCRUDValue(crudType);
			}
		} else {
			// if the targetPermission's value == NULL, then we should treat this like a "new Permission"
			if( targetPermission.getCRUDValue(crudType) == null ) {
				Permission parentPermission = getExistingPermission(element, crudType);
				// Set the target permission crud value to parent permission crud value
				// ONLY if the parent crud value == TRUE, else we can't override the parent.
				if( parentPermission.getCRUDValue(crudType) == Boolean.TRUE) {
					targetPermission.setCRUDValue(parentPermission.getCRUDValue(crudType), crudType);
					targetPermission.toggleCRUDValue(crudType);
				}
			} else if( targetPermission.getCRUDValue(crudType) == Boolean.FALSE ) {
				if( targetPermission.isPrimary() ) {
					targetPermission.toggleCRUDValue(crudType);
				} else {
					Permission parentPermission = getFirstParentPermission(element, crudType);
					// Set the target permission crud value to parent permission crud value
					// ONLY if the parent crud value == TRUE, else we can't override the parent.
					if( parentPermission.getCRUDValue(crudType) == Boolean.TRUE) {
						targetPermission.setCRUDValue(null, crudType); //parentPermission.getCRUDValue(type), type);
					}
				}
			} else {
				targetPermission.toggleCRUDValue(crudType);
			}
			
		}
		
		// Now check if we need to remove any permissions based on the selected element.
		cleanUpPermissions(element);
		
		// Check on all other permissions and clean them up if necessary
		Collection<Object> childPermissions = new ArrayList<Object>();
		getChildrenWithPermission(element, childPermissions);
		for( Object childWithPerm : childPermissions ) {
			cleanUpPermissions(childWithPerm);
		}
		

	}
	
	public boolean supportsUpdates(Object element, Crud.Type crudType) {
		Object targetObj = element;
		if( TransformationHelper.isSqlColumn(element) ) {
			targetObj = ((EObject)element).eContainer();
		}
		
		boolean isVirtualTable = TransformationHelper.isVirtualSqlTable(targetObj);

		if (TransformationHelper.isSqlTable(targetObj) && !TransformationHelper.isXmlDocument(targetObj)) {
            SqlTableAspect tableAspect = (SqlTableAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)targetObj);
            if (tableAspect != null) {
                if( isVirtualTable ) {
                	EObject transMappingRoot = TransformationHelper.getTransformationMappingRoot((EObject)targetObj);
                	switch( crudType ) {
	                	case CREATE: return TransformationHelper.isInsertAllowed(transMappingRoot);
	                	case READ: return true;
	                	case UPDATE: return TransformationHelper.isUpdateAllowed(transMappingRoot);
	                	case DELETE: return TransformationHelper.isDeleteAllowed(transMappingRoot);
                	}
                } else {
                	return tableAspect.supportsUpdate((EObject)targetObj);
                }
            }
        } else {
        	return true;
        }

		
		return false;
	}
}
