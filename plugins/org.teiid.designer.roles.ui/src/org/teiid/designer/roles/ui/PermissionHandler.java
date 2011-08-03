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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.roles.Crud;
import org.teiid.designer.roles.Crud.Type;
import org.teiid.designer.roles.Permission;
import com.metamatrix.metamodels.relational.ProcedureParameter;
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
    private void cleanUpPermissions( Object changedElement ) {

        // walk the tree,
        Permission perm = permissionsMap.get(changedElement);
        if (perm == null || perm.isPrimary()) {
            return;
        }

        if (perm.isNullCrud()) {
            // System.out.println(" Removing Stale Permission for: " + perm.getTargetName());
            permissionsMap.remove(changedElement); // stalePermissionKeys.add(changedElement);
        } else {
            boolean sameCreate = false;
            boolean sameRead = false;
            boolean sameUpdate = false;
            boolean sameDelete = false;
            // look at all 4 CRUD values and their Parent Perms and if they are the same as the CRUD value
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

            if (sameCreate && sameRead && sameUpdate && sameDelete) {
                // System.out.println(" Removing Stale Permission for: " + perm.getTargetName());
                permissionsMap.remove(changedElement);
                // stalePermissionKeys.add(changedElement);
            }
        }
    }

    /*
     * Gather up all child and grand-child permissions
     */
	private void getChildPermissions(Object parent, Collection<Permission> allChildPermissions) {

        for (Object child : tree.getChildren(parent)) {
            Permission perm = this.permissionsMap.get(child);
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
            Permission perm = this.permissionsMap.get(child);
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
        Permission perm = this.permissionsMap.get(element);
        if (perm == null || perm.getCRUDValue(crudType) == null) {
            Object parent = tree.getParent(element);
            while (parent != null && (perm == null || perm.getCRUDValue(crudType) == null)) {
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
        while (parent != null && (perm == null || perm.getCRUDValue(crudType) == null)) {
            perm = this.permissionsMap.get(parent);
            parent = tree.getParent(parent);
        }
        return perm;
    }

    public Permission getPermission( Object element ) {
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

        for (Permission perm : childPermissions) {
            Boolean value = perm.getCRUDValue(type);
            if (value != null && value != parentValue) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermissions() {
        return !this.permissionsMap.isEmpty();
    }

	public void addPermission(Object key, Permission perm) {
        if (!this.permissionsMap.containsKey(key)) {
            this.permissionsMap.put(key, perm);
        }
    }

    /**
	 * Performs the necessary permission CRUDDELETE value changes based on the target element and the CRUD type.
	 * This method is targeted for use by a single-click editor changing ONE CRUD boolean value for one object.
     * 
     * @param element
     * @param crudType
     */
	public void togglePermission( Object element, Crud.Type crudType ) {
        if (!supportsUpdates(element, crudType)) {
            return;
        }

        // The current element permission
        Permission targetPermission = this.permissionsMap.get(element);

        // Current element permission is null - inherits from parent
        if (targetPermission == null) {
            Permission parentPermission = getExistingPermission(element, crudType);

            // Now create New permission with NULL values
            Crud targetCrud = new Crud(null, null, null, null);
            targetPermission = new Permission(tree.getTargetName(element), targetCrud);

            // Set the target permission crud value to parent permission crud value
            // ONLY if the parent crud value == TRUE, else we can't override the parent.
            if (parentPermission.getCRUDValue(crudType) == Boolean.TRUE) {
                targetPermission.setCRUDValue(parentPermission.getCRUDValue(crudType), crudType);
                permissionsMap.put(element, targetPermission);
                targetPermission.toggleCRUDValue(crudType);
            }
            // Current element permission non null - some portion already contains override.
        } else {
            // if the targetPermission's value == NULL, then we should treat this like a "new Permission"
            if (targetPermission.getCRUDValue(crudType) == null) {
                Permission parentPermission = getExistingPermission(element, crudType);
                // Set the target permission crud value to parent permission crud value
                // ONLY if the parent crud value == TRUE, else we can't override the parent.
                if (parentPermission.getCRUDValue(crudType) == Boolean.TRUE) {
                    targetPermission.setCRUDValue(parentPermission.getCRUDValue(crudType), crudType);
                    targetPermission.toggleCRUDValue(crudType);
                }
            } else if (targetPermission.getCRUDValue(crudType) == Boolean.FALSE) {
                if (targetPermission.isPrimary()) {
                    targetPermission.toggleCRUDValue(crudType);
                } else {
                    Permission parentPermission = getFirstParentPermission(element, crudType);
                    // Set the target permission crud value to parent permission crud value
                    // ONLY if the parent crud value == TRUE, else we can't override the parent.
                    if (parentPermission.getCRUDValue(crudType) == Boolean.TRUE) {
                        targetPermission.setCRUDValue(null, crudType); // parentPermission.getCRUDValue(type), type);
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
                Permission parentPerm = this.permissionsMap.get(parent);
                Permission elementPerm = getExistingPermission(workingElem, crudType);
                Boolean elementStatus = elementPerm.getCRUDValue(crudType);
                // Paren permission is null means the crud was inherited, so add an override
                if (parentPerm == null) {
                    Crud targetCrud = new Crud(null, null, null, null);
                    Permission newParentPermission = new Permission(tree.getTargetName(parent), targetCrud);
                    newParentPermission.setCRUDValue(elementStatus, crudType);
                    this.permissionsMap.put(parent, newParentPermission);
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
        return;
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

        // Check for conditions where parent does not allow toggle
        boolean parentAllowsToggle = true;
        // The current element permission
        Permission targetPermission = this.permissionsMap.get(element);
        // Target Permission null, check existing permission from parent
        if (targetPermission == null) {
            Permission parentPermission = getExistingPermission(element, crudType);
            if (parentPermission.getCRUDValue(crudType) != Boolean.TRUE) parentAllowsToggle = false;
        } else {
            if (targetPermission.getCRUDValue(crudType) == null) {
                Permission parentPermission = getExistingPermission(element, crudType);
                if (parentPermission.getCRUDValue(crudType) != Boolean.TRUE) parentAllowsToggle = false;
            } else if (targetPermission.getCRUDValue(crudType) == Boolean.FALSE) {
                if (!targetPermission.isPrimary()) {
                    Permission parentPermission = getFirstParentPermission(element, crudType);
                    if (parentPermission.getCRUDValue(crudType) != Boolean.TRUE) parentAllowsToggle = false;
                }
            }
        }
        // Return INFO - if parent state disallows toggle
        if (!parentAllowsToggle) {
            return createStatus(IStatus.INFO, RolesUiPlugin.UTIL.getString("PermissionHandler.infoParentNotEnabled")); //$NON-NLS-1$
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
        Permission perm = permissionsMap.get(element);
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
            SqlTableAspect tableAspect = (SqlTableAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)targetObj);
            if (tableAspect != null) {
                if (isVirtualTable) {
                    EObject transMappingRoot = TransformationHelper.getTransformationMappingRoot((EObject)targetObj);
                    switch (crudType) {
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
