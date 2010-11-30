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
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.roles.Crud;
import org.teiid.designer.roles.Permission;

import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.View;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

public class DataRolesModelTreeProvider implements ITreeContentProvider, ITableLabelProvider {
	private static final Object[] NO_CHILDREN = new Object[0];
	private ITreeContentProvider modelProvider = ModelUtilities.getModelContentProvider();
	private static final char DELIM = StringUtil.Constants.DOT_CHAR;
	private static final char B_SLASH = '/';
	
	private static final Image CHECKED_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.CHECKED_BOX_ICON);
	private static final Image UNCHECKED_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.UNCHECKED_BOX_ICON);
	private static final Image GRAY_CHECKED_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.GRAY_CHECKED_BOX_ICON);
	private static final Image GRAY_UNCHECKED_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.GRAY_UNCHECKED_BOX_ICON);
	
//	private Map<Object, Permission> permissionsMap;
	private PermissionHandler handler;
	
	private Resource[] resources;
	
	public DataRolesModelTreeProvider(Map<Object, Permission> permissionsMap) {
		super();
		handler = new PermissionHandler(this, permissionsMap);
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// NO OP
	}
	
	@Override
	public void dispose() {
		// NO OP
	}

	/*
	 * Helper method to find a check-box image based on boolean value, if it is a parent or not and if it has at lease one
	 * child (or grand-child, etc.) that has a different boolean value.
	 */
	private Image getCheckBoxImage(Boolean value, boolean isParentValue, boolean hasDifferentChildValue) {
		// First case where the actual node does not have a CRUD value, so the first parent with a Value determines the state
		// of the check-box along with the boolean indicating that a child below it has a different value
		if( isParentValue ) {
			if( value == Boolean.FALSE) return GRAY_UNCHECKED_BOX;
			if( hasDifferentChildValue ) return GRAY_CHECKED_BOX;
			return CHECKED_BOX;
		}
		
		// The case where the actual node HAS a Permission with a non-null CRUD value
		if( value == Boolean.FALSE) return UNCHECKED_BOX;
		if( hasDifferentChildValue ) return GRAY_CHECKED_BOX;
		return CHECKED_BOX;

	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
        Object[] children = NO_CHILDREN;
        
        if (parentElement instanceof EObject) {
            children = getFilteredModelContents(modelProvider.getChildren(parentElement));
        } else if( parentElement instanceof Resource ) {
        	children = getFilteredModelContents(((Resource)parentElement).getContents());
        }

		return children;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		
		if( columnIndex == 0 ) {
			return getImage(element);
		}

		Crud.Type crudType = Crud.getCrudType(columnIndex);
		boolean supportsUpdate = handler.supportsUpdates(element, crudType);
		
		if( !supportsUpdate ) {
			return GRAY_UNCHECKED_BOX;
		}
		
		// Should ALWAYS FIND ONE if it's a RESOURCE (i.e. Model)
		Permission perm = handler.getPermission(element);
		boolean isParent = false;
		
		// If no permission exists for this element OR if the element's permission contains a NULL boolean value for this crud type
		// then we walk up the tree to find the first one that is NON-NULL
		if( perm == null || perm.getCRUDValue(crudType) == null ) {
			
			perm = handler.getExistingPermission(element, crudType);
			isParent = true;
		}

		if( perm != null ) {
			Boolean booleanValue = perm.getCRUDValue(crudType);
			isParent = perm.isPrimary();
			boolean hasDifferentChildValue = false;
			// If the permission is defined by a parent value, then we assume that this element has a NULL value and we need to determine
			// if any children below the element has a permission crud value not equal to the parent.
			if( isParent ) {
				hasDifferentChildValue = handler.hasChildWithDifferentCrudValue(perm, element, crudType);
			}
			
			return getCheckBoxImage(booleanValue, isParent, hasDifferentChildValue);
		}		
		
		return GRAY_UNCHECKED_BOX;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if( columnIndex == 0 ) {
	        return getText(element);
		}
		return null;
	}


	@Override
	public Object[] getElements(Object inputElement) {
		if( resources == null ) {
			resources = (Resource[])((ContainerImpl)inputElement).getResources().toArray();
		}
		
		reInitializePermissions();
		return resources;
	}

	/*
	 * return an array of objects that are filtered for only Relational Model object types.
	 */
	private Object[] getFilteredModelContents(List<EObject> eObjs) {
		Collection<EObject> relObjects = new ArrayList<EObject>();
		for( EObject eObj : eObjs ) {
			if( eObj instanceof Table || 
				eObj instanceof View ||
				eObj instanceof Procedure ||
				eObj instanceof Schema ||
				eObj instanceof Catalog ||
				eObj instanceof Column ||
				eObj instanceof ProcedureParameter ||
				eObj instanceof Interface ||
				eObj instanceof Operation ||
				eObj instanceof XmlDocument) {
				relObjects.add(eObj);
			}
		}
		
		return relObjects.toArray();
	}
	
	/*
	 * return an array of objects that are filtered for only Relational Model object types.
	 */
	private Object[] getFilteredModelContents(Object[] eObjs) {
		Collection<EObject> relObjects = new ArrayList<EObject>();
		for( Object eObj : eObjs ) {
			if( eObj instanceof Table || 
				eObj instanceof View ||
				eObj instanceof Procedure ||
				eObj instanceof Schema ||
				eObj instanceof Catalog ||
				eObj instanceof Column ||
				eObj instanceof ProcedureParameter ||
				eObj instanceof Interface ||
				eObj instanceof Operation ||
				eObj instanceof XmlDocument) {
				relObjects.add((EObject)eObj);
			}
		}
		
		return relObjects.toArray();
	}
	
	/*
	 * Returns an image for the element. Targeted for Column 1 in the Tree corresponding to the Model and it's children.
	 * 
	 */
	private Image getImage(Object element) {
        if (element instanceof EObject) {
            return ModelUtilities.getEMFLabelProvider().getImage(element);

        } else if (element instanceof Resource) {
        	try {
				EObject firstEObj = ((Resource)element).getContents().get(0);
				ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
				ModelType mType = ma.getModelType();
				if( ModelType.PHYSICAL_LITERAL == mType ) {
					return ModelIdentifier.getImage(ModelIdentifier.RELATIONAL_SOURCE_MODEL_ID);
				} 
				return ModelIdentifier.getImage(ModelIdentifier.RELATIONAL_VIEW_MODEL_ID);
			} catch (ModelerCoreException e) {
				RolesUiPlugin.UTIL.log(IStatus.ERROR, RolesUiPlugin.UTIL.getString("errorFindingImageForObject", element, e)); //$NON-NLS-1$
			}
        }
        
        return null;
	}
	
	@Override
	public Object getParent(Object element) {
		if( element instanceof EObject ) {
			return ModelUtilities.getModelContentProvider().getParent(element);
		} else if( element instanceof Resource ) {
			return ((Resource)element).getResourceSet();
		}
		return null;
	}
	
	/**
	 * Returns a string target name for the specified element
	 * 
	 */
	public String getTargetName(Object element) {
		String targetName = null;
		if( element instanceof Resource ) {
			return getResourceName((Resource)element);
		} else if( element instanceof EObject ) {
			EObject eObj = (EObject)element;
			targetName = getResourceName(eObj.eResource()) + '/' + ModelerCore.getModelEditor().getModelRelativePath(eObj);
		}
		
		targetName = targetName.replace(B_SLASH, DELIM);
		
		return targetName;
	}
	
	/**
	 * Returns an actual Model Object (EObject or Resource) for the specified Permission
	 * 
	 * 
	 * @param perm
	 * @return
	 */
	public Object getPermissionTargetObject(Permission perm) {
		String targetName = perm.getTargetName();
		for( Resource res : resources ) {
			if( getTargetName(res).equals(targetName) ) {
				return res;
			}

			for( Object child : getChildren(res) ) {
				if( getTargetName(child).equals(targetName)) {
					return child;
				}
				Object target = getTargetObjectInChildren(child, targetName);
				if( target != null ) {
					return target;
				}
			}
		}
		
		return null;
	}

	/*
	 * Returns the file name only minus the xmi file extension
	 */
	private String getResourceName(Resource res) {
		
		if( res.getURI().path().endsWith(".xmi")) { //$NON-NLS-1$
			Path path = new Path(res.getURI().path());
			return path.removeFileExtension().lastSegment();
		} else {
			return res.getURI().path();
		}
	}

	/*
	 * Finds the target object under the specified parent. May return null.
	 * 
	 */
	private Object getTargetObjectInChildren(Object parent, String targetName) {

		for( Object child : getChildren(parent) ) {
			if( getTargetName(child).equals(targetName) ) {
				return child;
			}
			Object target = getTargetObjectInChildren(child, targetName);
			if( target != null ) {
				return target;
			}
		}
		
		return null;
	}

	/*
	 * Helper method to retrieve the text value for an EMF object (EObject or Resource)
	 * 
	 */
	private String getText(Object element) {
		if (element instanceof EObject) {
            ILabelProvider p = ModelUtilities.getEMFLabelProvider();
            return p.getText(element);
        } else if (element instanceof Resource) {
        	return ((Resource)element).getURI().lastSegment();
        }
		
		return StringUtilities.EMPTY_STRING;
	}
	
	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// NO OP
		
	}
	
	@Override
	public boolean isLabelProperty(Object element, String property) {
		// NO OP
		return false;
	}
	
	/*
	 * Make sure that ALL resources have a permissions object??
	 */
	private void reInitializePermissions() {
		
		if( handler.hasPermissions() ) {
			return;
		}
		
		for( Resource res : resources ) {
			String resPath = getTargetName(res);
			Permission perm = new Permission(resPath, false, true, false, false);
			perm.setPrimary(true);
			handler.addPermission(res, perm);
		}
	}
	
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// NO OP
	}
	
	/**
	 * Performs the necessary permission CRUD value changes based on the target element and the CRUD type.
	 * This method is targeted for use by a single-click editor changing ONE CRUD boolean value for one object.
	 * 
	 * @param element
	 * @param crudType
	 */
	public void togglePermission( Object element, Crud.Type crudType ) {
		handler.togglePermission(element, crudType);
	}
	

}
