/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.roles.Crud;
import org.teiid.designer.roles.Crud.Type;
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.transformation.util.TransformationHelper;

/**
 * @since 8.0
 */
public class PermissionHandler {

    private DataRolesModelTreeProvider tree;
    private Map<Object, Permission> objectsToPermissionsMap;

	public PermissionHandler(DataRolesModelTreeProvider tree) {
        super();
        this.tree = tree;
        this.objectsToPermissionsMap = new HashMap<Object, Permission>();
    }
	
	public void handlePermissionChanged(Permission permission) {
		Object targetObject = null;
		for( Object obj : objectsToPermissionsMap.keySet()) {
			Permission perm = objectsToPermissionsMap.get(obj);
			if( perm == permission) {
				targetObject = obj;
			}
		}
		if( targetObject != null) {
			cleanUpPermissions(targetObject);
		}
	}

    /*
     * Because child permission values can default to parent Crud values there may be instances where a permission's CRUD
     * either exactly matches values from parents, or the CRUD is all NULL. In both cases we need to remove these Permissions
     * from the cache.
     * 
     */
    private void cleanUpPermissions( Object changedElement ) {

        // walk the tree,
        Permission perm = objectsToPermissionsMap.get(changedElement);
        if (perm == null || perm.isPrimary() || perm.isAllowLanguage()) {
            return;
        }

        if (perm.isNullCrud()) {
        	if( perm.getCondition() == null && perm.getMask() == null ) {
	            // System.out.println(" Removing Stale Permission for: " + perm.getTargetName());
	            objectsToPermissionsMap.remove(changedElement);
        	}
        } else {
            boolean sameCreate = false;
            boolean sameRead = false;
            boolean sameUpdate = false;
            boolean sameDelete = false;
            boolean sameExecute = false;
            boolean sameAlter = false;
            
            // look at all 6 CRUD values and their Parent Perms and if they are the same as the CRUD value
            Permission parentPerm = getFirstParentPermission(changedElement, Crud.Type.CREATE);
            if (parentPerm != null) {
				if( (perm.getCRUDValue(Crud.Type.CREATE) == parentPerm.getCRUDValue(Crud.Type.CREATE)) ||
					(perm.getCRUDValue(Crud.Type.CREATE) == null && parentPerm.getCRUDValue(Crud.Type.CREATE) != null) ) {
                    sameCreate = true;
                }
            }
            if (!sameCreate) return;

            parentPerm = getFirstParentPermission(changedElement, Crud.Type.READ);
            if (parentPerm != null) {
				if( (perm.getCRUDValue(Crud.Type.READ) == parentPerm.getCRUDValue(Crud.Type.READ)) ||
					(perm.getCRUDValue(Crud.Type.READ) == null && parentPerm.getCRUDValue(Crud.Type.READ) != null) ) {
                    sameRead = true;
                }
            }
            if (!sameRead) return;

            parentPerm = getFirstParentPermission(changedElement, Crud.Type.UPDATE);
            if (parentPerm != null) {
				if( (perm.getCRUDValue(Crud.Type.UPDATE) == parentPerm.getCRUDValue(Crud.Type.UPDATE)) ||
					(perm.getCRUDValue(Crud.Type.UPDATE) == null && parentPerm.getCRUDValue(Crud.Type.UPDATE) != null) ) {
                    sameUpdate = true;
                }
            }
            if (!sameUpdate) return;

            parentPerm = getFirstParentPermission(changedElement, Crud.Type.DELETE);
            if (parentPerm != null) {
				if( (perm.getCRUDValue(Crud.Type.DELETE) == parentPerm.getCRUDValue(Crud.Type.DELETE)) ||
					(perm.getCRUDValue(Crud.Type.DELETE) == null && parentPerm.getCRUDValue(Crud.Type.DELETE) != null) ) {
                    sameDelete = true;
                }
            }
            
            parentPerm = getFirstParentPermission(changedElement, Crud.Type.EXECUTE);
            if (parentPerm != null) {
				if( (perm.getCRUDValue(Crud.Type.EXECUTE) == parentPerm.getCRUDValue(Crud.Type.EXECUTE)) ||
					(perm.getCRUDValue(Crud.Type.EXECUTE) == null && parentPerm.getCRUDValue(Crud.Type.EXECUTE) != null) ) {
                    sameExecute = true;
                }
            }
            parentPerm = getFirstParentPermission(changedElement, Crud.Type.ALTER);
            if (parentPerm != null) {
				if( (perm.getCRUDValue(Crud.Type.ALTER) == parentPerm.getCRUDValue(Crud.Type.ALTER)) ||
					(perm.getCRUDValue(Crud.Type.ALTER) == null && parentPerm.getCRUDValue(Crud.Type.ALTER) != null) ) {
                    sameAlter = true;
                }
            }

            
            if (sameCreate && sameRead && sameUpdate && sameDelete && sameExecute && sameAlter) {
            	if( perm.getCondition() != null || perm.getMask() != null ) {
            		// null out all crud values
            		perm.setCRUD(null,null,null,null,null,null);
            	} else {
            		// System.out.println(" Removing Stale Permission for: " + perm.getTargetName());
            		objectsToPermissionsMap.remove(changedElement);
            	}
            }
        }
    }

    /*
     * Gather up all child and grand-child permissions
     */
	private void getChildPermissions(Object parent, Collection<Permission> allChildPermissions) {

        for (Object child : tree.getChildren(parent)) {
            Permission perm = this.objectsToPermissionsMap.get(child);
            if (perm != null) {
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
        for (Object child : tree.getChildren(parent)) {
            Permission perm = this.objectsToPermissionsMap.get(child);
            if (perm != null) {
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
        Permission perm = this.objectsToPermissionsMap.get(element);
        if (perm == null || perm.getCRUDValue(crudType) == null) {
            Object parent = tree.getParent(element);
            while (parent != null && (perm == null || perm.getCRUDValue(crudType) == null)) {
                perm = this.objectsToPermissionsMap.get(parent);
                parent = tree.getParent(parent);
            }
        }

        return perm;
    }
	
    /**
     * Finds the parent permission which contains a non-null Boolean crud value
     * @param element
     * @param crudType
     * @return parent permission. may be null
     */
	public Permission getParentPermission(Object element, Crud.Type crudType) {
		Permission perm = this.objectsToPermissionsMap.get(element);
		if (perm != null &&  perm.getCRUDValue(crudType) != null) {
			return perm;
		}
		
		return null;
    }
	
	private Permission getModelPermssion(Object element) {
		if( element instanceof Resource ) {
			return this.objectsToPermissionsMap.get(element);
		}
		
        Object parent = tree.getParent(element);
        while (parent != null ) {
        	if( parent instanceof Resource ) {
        		return this.objectsToPermissionsMap.get(parent);
        	}
            parent = tree.getParent(parent);
        }

		return null;
	}
	
	private Object getModelElement(Object element) {
        Object parent = element;
        while (parent != null ) {
        	Object theParent = parent;
        	if( theParent instanceof Resource ) {
        		return theParent;
        	}
            parent = tree.getParent(theParent);
        }
        
        return null;
	}

    /*
     * Find the first parent above the element with an NON-NULL boolean value for the specified CRUD type
     * 
     */
	private Permission getFirstParentPermission(Object element, Crud.Type crudType) {
        Permission perm = null;
        Object parent = tree.getParent(element);
        while (parent != null && (perm == null || perm.getCRUDValue(crudType) == null)) {
            perm = this.objectsToPermissionsMap.get(parent);
            parent = tree.getParent(parent);
        }
        return perm;
    }

    public Permission getPermission( Object element ) {
        return this.objectsToPermissionsMap.get(element);
    }

    /**
     * Helper method to determine if any child below a parent contains a CRUD value different than the parent.
	 * 
     */
	public boolean hasChildWithDifferentCrudValue(Permission parentPermission, Object parent, Crud.Type type) {
        Boolean parentValue = parentPermission.getCRUDValue(type);

        Collection<Permission> childPermissions = new ArrayList<Permission>();

        getChildPermissions(parent, childPermissions);

        for (Permission perm : childPermissions) {
            Boolean childValue = perm.getCRUDValue(type);
            if( childValue != null && parentPermission.childCrudValueIsDifferent(parentValue, childValue)) {
            	return true;
            }
        }
        return false;
    }

    public boolean hasPermissions() {
        return !this.objectsToPermissionsMap.isEmpty();
    }

	public void addPermission(Object key, Permission perm) {
        if (!this.objectsToPermissionsMap.containsKey(key)) {
            this.objectsToPermissionsMap.put(key, perm);
        }
    }
	
	private void disableNearestParentPermission(Object element, Crud.Type crudType) {
		Object firstParent = tree.getParent(element);
		Permission perm = this.objectsToPermissionsMap.get(firstParent);
		
		if( perm != null && perm.getCRUDValue(crudType) != null && perm.getCRUDValue(crudType) == Boolean.FALSE ) {
			perm.setCRUDValue(true, crudType);
		} else {
			Permission permiss = null;
            Object parent = tree.getParent(firstParent);
            
            while (parent != null && !(parent instanceof Resource) && permiss == null) {
            	permiss = this.objectsToPermissionsMap.get(parent);
                parent = tree.getParent(parent);
                if( permiss != null && permiss.getCRUDValue(crudType) == Boolean.FALSE ) {
                	perm.setCRUDValue(true, crudType);
                	return;
                }
            }
		}
	}
	
	private boolean hasParentPermission(Object element, Crud.Type crudType) {
		Object firstParent = tree.getParent(element);
		Permission perm = this.objectsToPermissionsMap.get(firstParent);
		
		if( (perm == null || perm.getCRUDValue(crudType) == null)) {
			return false;
		} else {
			Permission permiss = perm;
            if( permiss.getCRUDValue(crudType) == Boolean.FALSE ) {
            	return true;
            }
            Object parent = tree.getParent(firstParent);
            
            while (parent != null && !(parent instanceof Resource) && (permiss == null || permiss.getCRUDValue(crudType) == null)) {
            	permiss = this.objectsToPermissionsMap.get(parent);
                parent = tree.getParent(parent);
                if( permiss != null && permiss.getCRUDValue(crudType) == Boolean.FALSE ) {
                	return true;
                }
            }
		}
		
		return false;
	}
	
	@SuppressWarnings("unused")
	private Permission togglePermissionValue(Object element, boolean newValue, Crud.Type crudType) {
		Permission permission = this.objectsToPermissionsMap.get(element);
		if( permission == null ) {
			permission = new Permission(tree.getTargetName(element), new Crud(null, null, null, null, null, null));
			this.objectsToPermissionsMap.put(element, permission);
			permission.setCRUDValue(newValue, crudType);
		} else {
			permission.toggleCRUDValue(crudType);
		}
		
		// If toggling permission value to TRUE, then need to set all Children to TRUE if perm
		SET_CHILD_PERMISSIONS : {
	        Boolean parentValue = permission.getCRUDValue(crudType);
	
	        Collection<Permission> childPermissions = new ArrayList<Permission>();
	
	        getChildPermissions(element, childPermissions);
	
	        for (Permission perm : childPermissions) {
	        	if( perm.getCRUDValue(crudType) != null ) {
	        		perm.setCRUDValue(parentValue, crudType);
	        	}
	        }
		}
		
		return permission;
	}
	
    /**
	 * Performs the necessary permission CRUD value changes based on the target element and the CRUD type.
	 * This method is targeted for use by a single-click editor changing ONE CRUD boolean value for one object.
     * 
     * @param element
     * @param crudType
     */
	public void toggleElementPermission( Object element, Crud.Type crudType ) {
        if (!supportsUpdates(element, crudType)) {
            return;
        }

    	// See if model permission is TRUE or FALSE
    	
    	Permission modelPermission = getModelPermssion(element);
    	Object modelElement = getModelElement(element);
    	
    	boolean firstTimePermission = false;
    	if( modelPermission == null || modelPermission.getCRUDValue(crudType) == Boolean.FALSE) {
    		// Model needs to get turned ON because this is the FIRST time this crud type is enabled
    		
    		if( modelPermission == null ) {
    			Crud targetCrud = new Crud(null, null, null, null, null, null);
    			
    			modelPermission = new Permission(tree.getTargetName(modelElement), targetCrud);
    			modelPermission.setCRUDValue(true, crudType);
                this.objectsToPermissionsMap.put(modelElement, modelPermission);
    		} else {
    			modelPermission.setCRUDValue(true, crudType);
    		}
    		firstTimePermission = true;
    	}
    	
    	// if firstTimePermission == TRUE
    	// Then we just need to set all non-checked siblings with PERMISSION = FALSE and all parent's siblings to FALSE
    	// etc... until we reach the model
    	
    	if( firstTimePermission ) {
    		disableAllSiblingPermissions(element, crudType);
    		
    		Object parent = this.tree.getParent(element);
    		while( parent != null && parent != modelElement ) {
    			Object theParent = parent;
    			disableAllSiblingPermissions(parent, crudType);
    			parent = tree.getParent(theParent);
    		}
    	} else {
    		// Now we have an enabled Model Permission and need to look only at the selected object and it's siblings
    		// toggle existing permission (or create new with false)

    		
    		boolean hasParentPermissionFalse = hasParentPermission(element, crudType);
    		
    		if( hasParentPermissionFalse ) {
    			disableNearestParentPermission(element, crudType);
    			disableAllSiblingPermissions(element, crudType);
    		}
    		
    		
    		togglePermissionValue(element, hasParentPermissionFalse, crudType);
    	}
        
        // Now check if we need to remove any permissions based on the selected element.
        cleanUpPermissions(element);

        // Check on all other permissions and clean them up if necessary
        Collection<Object> childPermissions = new ArrayList<Object>();
        getChildrenWithPermission(element, childPermissions);
        for (Object childWithPerm : childPermissions) {
            cleanUpPermissions(childWithPerm);
        }

        // If all of this node's siblings have same status - which is different than parent - change parent status,
        // then do a clean-up
        Object workingElem = element;
        while (!isPrimary(workingElem) && allSiblingsHaveSameStatus(workingElem, crudType)) {
            Object parent = tree.getParent(workingElem);
            if (parent != null && !haveSameStatus(parent, workingElem, crudType)) {
                // Get parent permission directly
                Permission parentPerm = this.objectsToPermissionsMap.get(parent);
                Permission elementPerm = getExistingPermission(workingElem, crudType);
                Boolean elementStatus = elementPerm.getCRUDValue(crudType);
                // Paren permission is null means the crud was inherited, so add an override
                if (parentPerm == null) {
                    Crud targetCrud = new Crud(null, null, null, null, null, null);
                    Permission newParentPermission = new Permission(tree.getTargetName(parent), targetCrud);
                    newParentPermission.setCRUDValue(elementStatus, crudType);
                    this.objectsToPermissionsMap.put(parent, newParentPermission);
                } else {
                    parentPerm.setCRUDValue(elementStatus, crudType);
                }
                // Now check if we need to remove any permissions based on the selected element.
                cleanUpPermissions(parent);

                // Check on all other permissions and clean them up if necessary
                Collection<Object> pchildPermissions = new ArrayList<Object>();
                getChildrenWithPermission(parent, pchildPermissions);
                for (Object childWithPerm : pchildPermissions) {
                    cleanUpPermissions(childWithPerm);
                }
            }
            workingElem = parent;
        }
	}



	
	private void disableAllSiblingPermissions(Object element, Crud.Type crudType) {
		Object parent = tree.getParent(element);
		
		if( parent != null ) {
			for( Object child : tree.getChildren(parent) ) {
				if( child == element ) {
					continue;
				}
				Permission existingPermission = this.objectsToPermissionsMap.get(child);
				if( existingPermission == null ) {
					Crud childCrud = new Crud(null, null, null, null, null, null);
					Permission newPermission = new Permission(tree.getTargetName(child), childCrud);
					newPermission.setCRUDValue(false, crudType);
					this.objectsToPermissionsMap.put(child, newPermission);
				} else {
					existingPermission.setCRUDValue(false, crudType);
				}
			}
		}
	}

    /**
     * Check the 'toggle-ability' of the crud type of the supplied element. The status may return INFO - means a message should be
     * displayed to the user before toggling WARNING - means the user should be shown a confirmation message before toggling OK -
     * OK to toggle without showing a message or getting user confirmation
     * 
     * @param element the element to toggle
     * @param crudType the crud type to toggle
     * @return the status of the toggle request
     */
    public IStatus getToggleStatus( Object element,
                                    Crud.Type crudType ) {

        // Return INFO - if element does not support updates
        if (!supportsUpdates(element, crudType)) {
            return createStatus(IStatus.INFO, RolesUiPlugin.UTIL.getString("PermissionHandler.infoNotUpdatable")); //$NON-NLS-1$
        }

        // Return WARNING - if element can be toggled, but doing so will toggle all its children
        Object[] children = tree.getChildren(element);
        if (children.length > 0) {
            String msg = null;
            if (crudType == Crud.Type.READ) {
                msg = RolesUiPlugin.UTIL.getString("PermissionHandler.warningWillToggleChildren"); //$NON-NLS-1$
            } else {
                msg = RolesUiPlugin.UTIL.getString("PermissionHandler.warningWillToggleChildrenIfUpdatable"); //$NON-NLS-1$
            }
            return createStatus(IStatus.WARNING, msg);
        }

        // OK status - no message
        return createStatus(IStatus.OK, ""); //$NON-NLS-1$
    }

    /**
     * Create a Status object with the given status and message
     * 
     * @param statusFlag the desired status
     * @param msg the desired message
     * @return the status object
     */
    private IStatus createStatus( int statusFlag,
                                  String msg ) {
        final IStatus status = new Status(statusFlag, RolesUiPlugin.PLUGIN_ID, msg);
        return status;
    }

    /**
     * Determine if all siblings of the supplied element have the same state for the specified crudType
     * 
     * @param element the element to check
     * @param crudType the crud type to check
     * @return 'true' if all siblings have the same status
     */
    private boolean allSiblingsHaveSameStatus( Object element,
                                               Crud.Type crudType ) {
        boolean allSame = true;

        // Get parent of the provided element
        Object parent = tree.getParent(element);
        // Compare status of all the children
        if (parent != null) {
            int i = 0;
            Boolean firstStatus = null;
            // Accumulate all of the sibling states
            for (Object child : tree.getChildren(parent)) {
                // Current child's permission. If it does not have a permission, it inherits the parents.
                Permission perm = getExistingPermission(child, crudType);
                Boolean currentStatus = null;
                if (perm != null) {
                    currentStatus = perm.getCRUDValue(crudType);
                }
                if (i == 0) {
                    firstStatus = currentStatus;
                } else {
                    if (currentStatus != firstStatus) {
                        allSame = false;
                        break;
                    }
                }
                i++;
            }
        }
        return allSame;
    }

    /**
     * Determine if element 'isPrimary'
     */
    private boolean isPrimary( Object element ) {
        Permission perm = objectsToPermissionsMap.get(element);
        if (perm != null && perm.isPrimary()) {
            return true;
        }
        return false;
    }

    /**
     * Helper method - determine if the two supplied elements have the same status for the specified crud type.
     */
    private boolean haveSameStatus( Object elem1,
                                    Object elem2,
                                    Crud.Type crudType ) {
        Permission elem1Perm = getExistingPermission(elem1, crudType);
        Permission elem2Perm = getExistingPermission(elem2, crudType);

        Boolean elem1Status = elem1Perm.getCRUDValue(crudType);
        Boolean elem2Status = elem2Perm.getCRUDValue(crudType);

        if (elem1Status != null && elem2Status != null && elem1Status == elem2Status) {
            return true;
        }
        return false;
    }

    public boolean supportsUpdates( Object element,
                                    Crud.Type crudType ) {
        Object targetObj = element;
        if (TransformationHelper.isSqlColumn(element) || element instanceof ProcedureParameter) {
            targetObj = ((EObject)element).eContainer();
            if (crudType == Type.DELETE) {
                return false;
            }
        }

        boolean isVirtualTable = TransformationHelper.isVirtualSqlTable(targetObj);

        if (TransformationHelper.isSqlTable(targetObj) && !TransformationHelper.isXmlDocument(targetObj)) {
            SqlTableAspect tableAspect = (SqlTableAspect)org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)targetObj);
            if (tableAspect != null) {
                if (isVirtualTable) {
                    EObject transMappingRoot = TransformationHelper.getTransformationMappingRoot((EObject)targetObj);
                    switch (crudType) {
	                	case CREATE: return TransformationHelper.isInsertAllowed(transMappingRoot);
	                	case READ: return true;
	                	case UPDATE: return TransformationHelper.isUpdateAllowed(transMappingRoot);
	                	case DELETE: return TransformationHelper.isDeleteAllowed(transMappingRoot);
	                	default:
	                		return false;
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
    
    public void loadPermissions(Collection<Permission> permissions) {
        for (Permission perm : permissions) {
        	if( perm.isAllowLanguage() ) {
        		this.objectsToPermissionsMap.put(perm.getTargetName(), perm);
        	} else {
	            Object obj = tree.getPermissionTargetObject(perm);
	            if (obj != null) {
	                if (obj instanceof Resource) {
	                    perm.setPrimary(true);
	                }
	                // load the actual object to permission into map
	                this.objectsToPermissionsMap.put(obj, perm);
	            }
        	}
        }
    }
    
    public Collection<Permission> getPermissions() {
    	return this.objectsToPermissionsMap.values();
    }
    
	public List<Permission> getPermissionsWithRowBasedSecurity() {
		List<Permission> perms = new ArrayList<Permission>(10);
		
		for( Permission perm : getPermissions()) {
			if( perm.getCondition() != null ) {
				perms.add(perm);
			}
		}
		
		return perms;
	}
	
	public Permission getPermission(String targetName) {
		for( Permission perm : objectsToPermissionsMap.values()) {
			if( perm.getTargetName().equals(targetName) ) {
				return perm;
			}
		}
		return null;
	}
	
	public List<Permission> getPermissionsWithColumnMasking() {
		List<Permission> perms = new ArrayList<Permission>(10);
		
		for( Permission perm : getPermissions()) {
			if( perm.getMask() != null ) {
				perms.add(perm);
			}
		}
		
		return perms;
	}
	
	public void removeRowBasedSecurity(Permission permission) {
		permission.setCondition(null);
		permission.setConstraint(true);
	}
	
	public void removeColumnMask(Permission permission) {
		permission.setMask(null);
		permission.setOrder(0);
	}
	
	public void removeColumnMask(String targetName) {
		Permission existingPerm = getPermission(targetName);
		if( existingPerm != null ) {
			existingPerm.setMask(null);
			existingPerm.setOrder(0);
		}
	}
	
	public void setColumnMask(String targetName, String mask, int order) {
		Permission existingPerm = getPermission(targetName);
		if( existingPerm == null ) {
			existingPerm = new Permission(targetName, new Crud(null, null, null, null, null, null));
			this.objectsToPermissionsMap.put(targetName, existingPerm);
		}
		existingPerm.setMask(mask);
		existingPerm.setOrder(order);
	}
	
	public void setRowsBasedSecurity(String targetName, String condition, boolean constraint) {
		Permission existingPerm = getPermission(targetName);
		if( existingPerm == null ) {
			existingPerm = new Permission(targetName, new Crud(null, null, null, null, null, null));

			this.objectsToPermissionsMap.put(targetName, existingPerm);
		}
		existingPerm.setCondition(condition);
		existingPerm.setConstraint(constraint);
	}
	
	public List<String> getAllowedLanguages() {
		List<String> allowedLanguages = new ArrayList<String>(10);
		
		for( Permission perm : getPermissions()) {
			if( perm.isAllowLanguage() ) {
				allowedLanguages.add(perm.getTargetName());
			}
		}
		
		return allowedLanguages;
	}
	
	public void addAllowedLanguage(String language) {
		Permission perm = new Permission(language, false, false, false, false, false, false);
		perm.setAllowLanguage(true);
		this.objectsToPermissionsMap.put(language, perm);
	}
	
	public void removeAllowedLanguage(String language) {
		this.objectsToPermissionsMap.remove(language);
	}

}
