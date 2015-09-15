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
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ContainerImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.function.ScalarFunction;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.metamodels.webservice.Operation;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.roles.Crud;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.RolesUiConstants;
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public class DataRolesModelTreeProvider implements ITreeContentProvider, ITableLabelProvider {
    private static final Object[] NO_CHILDREN = new Object[0];
    private ITreeContentProvider modelProvider = ModelUtilities.getModelContentProvider();
    private static final char DELIM = CoreStringUtil.Constants.DOT_CHAR;
    private static final char B_SLASH = '/';

    private static final Image CHECKED_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.CHECKED_BOX_ICON);
    private static final Image UNCHECKED_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.UNCHECKED_BOX_ICON);
    private static final Image GRAY_CHECKED_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.GRAY_CHECKED_BOX_ICON);
    private static final Image GRAY_UNCHECKED_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.GRAY_UNCHECKED_BOX_ICON);
    
    private static final Image GRAY_STATUS_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.GRAY_BALL_ICON);
    private static final Image WHITE_STATUS_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.WHITE_BALL_ICON);
    private static final Image BLUE_STATUS_BOX = RolesUiPlugin.getInstance().getAnImage(RolesUiConstants.Images.BLUE_BALL_ICON);
    
    private static final int CHECKED = 0;
    private static final int UNCHECKED = 1;
    private static final int GRAY_CHECKED = 2;
    private static final int GRAY_UNCHECKED = 3;
    

    // private Map<Object, Permission> permissionsMap;
    private PermissionHandler handler;

    private Resource[] resources;

    public DataRolesModelTreeProvider( ) {
        super();
        handler = new PermissionHandler(this);
    }

    @Override
    public void addListener( ILabelProviderListener listener ) {
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
	private Image getCheckBoxImage(Object element, int columnIndex, Boolean value, boolean isParentValue, boolean hasDifferentChildValue) {
        // First case where the actual node does not have a CRUD value, so the first parent with a Value determines the state
        // of the check-box along with the boolean indicating that a child below it has a different value
		int imageType = CHECKED;
		
        if (isParentValue) {
            if (value == Boolean.FALSE) imageType = GRAY_UNCHECKED;
            if (hasDifferentChildValue) imageType = GRAY_CHECKED;
        }

        // The case where the actual node HAS a Permission with a non-null CRUD value
        if (value == Boolean.FALSE) imageType = UNCHECKED;
        if (hasDifferentChildValue) imageType = GRAY_CHECKED;
        
        switch(imageType) {
	        case CHECKED: {
	        	return CHECKED_BOX;
	        }
	        case UNCHECKED: {
	        	return UNCHECKED_BOX;
	        }
	        case GRAY_CHECKED: {
	        	return GRAY_CHECKED_BOX;
	        }
	        case GRAY_UNCHECKED: {
	        	return GRAY_UNCHECKED_BOX;
	        }
	        
	        default: return null;
        }

    }

    @Override
    public Object[] getChildren( Object parentElement ) {
        Object[] children = NO_CHILDREN;

        if (parentElement instanceof EObject) {
            children = getFilteredModelContents(modelProvider.getChildren(parentElement));
        } else if (parentElement instanceof Resource) {
            children = getFilteredModelContents(((Resource)parentElement).getContents());
        }

        return children;
    }
    
    
    @Override
	public boolean hasChildren( Object parentElement) {
    	return getChildren(parentElement).length > 0;
    }

    @Override
	public Image getColumnImage(Object element, int columnIndex) {


        if (columnIndex == 0) {
            return getImage(element);
        } else if( columnIndex == 1 ) {
        	return getSecurityStatusImage(element);
        }

        Crud.Type crudType = Crud.getCrudType(columnIndex);
        boolean supportsUpdate = handler.supportsUpdates(element, crudType);

        if (!supportsUpdate) {
            return GRAY_UNCHECKED_BOX;
        }

        // Should ALWAYS FIND ONE if it's a RESOURCE (i.e. Model)
        Permission perm = handler.getPermission(element);
        boolean isParent = false;

		// If no permission exists for this element OR if the element's permission contains a NULL boolean value for this crud type
        // then we walk up the tree to find the first one that is NON-NULL
        if (perm == null || perm.getCRUDValue(crudType) == null) {

            perm = handler.getExistingPermission(element, crudType);
            isParent = true;
        }

        if (perm != null) {
            Boolean booleanValue = perm.getCRUDValue(crudType);
            isParent = perm.isPrimary();
            boolean hasDifferentChildValue = false;
			// If the permission is defined by a parent value, then we assume that this element has a NULL value and we need to determine
            // if any children below the element has a permission crud value not equal to the parent.
            if (isParent || hasChildren(element) ) {
                hasDifferentChildValue = handler.hasChildWithDifferentCrudValue(perm, element, crudType);
            }

            return getCheckBoxImage(element, columnIndex, booleanValue, isParent, hasDifferentChildValue);
        }

        return GRAY_UNCHECKED_BOX;
    }

    @Override
	public String getColumnText(Object element, int columnIndex) {
        if (columnIndex == 0) {
            return getText(element);
        }
        return null;
    }

    @Override
    public Object[] getElements( Object inputElement ) {
        if (resources == null) {
        	// Filter out XSD models
        	List<Resource> allVdbResources = ((ContainerImpl)inputElement).getResources();
        	List<Resource> filteredResources = new ArrayList<Resource>();
        	for( Resource res : allVdbResources ) {
        		if( ! res.getURI().toFileString().toUpperCase().endsWith(".XSD") ) { //$NON-NLS-1$
        			filteredResources.add(res);
        		}
        	}
            resources = filteredResources.toArray( new Resource[0]); 
        }

        reInitializePermissions();
        return resources;
    }

    /*
     * return an array of objects that are filtered for only Relational Model object types.
     */
    private Object[] getFilteredModelContents( List<EObject> eObjs ) {
        Collection<EObject> relObjects = new ArrayList<EObject>();
        for (EObject eObj : eObjs) {
			if( eObj instanceof Table || 
				eObj instanceof View ||
				eObj instanceof Procedure ||
				eObj instanceof ProcedureResult ||
				eObj instanceof Schema ||
				eObj instanceof Catalog ||
				eObj instanceof Column ||
				eObj instanceof ProcedureParameter ||
				eObj instanceof Interface ||
				eObj instanceof Operation ||
				eObj instanceof XmlDocument ||
				eObj instanceof ScalarFunction) {
                relObjects.add(eObj);
            }
        }

        return relObjects.toArray();
    }

    /*
     * return an array of objects that are filtered for only Relational Model object types.
     */
    private Object[] getFilteredModelContents( Object[] eObjs ) {
        Collection<EObject> relObjects = new ArrayList<EObject>();
        for (Object eObj : eObjs) {
			if( eObj instanceof Table || 
				eObj instanceof View ||
				eObj instanceof Procedure ||
				eObj instanceof ProcedureResult ||
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
    private Image getImage( Object element ) {
        if (element instanceof EObject) {
            return ModelUtilities.getEMFLabelProvider().getImage(element);

        } else if (element instanceof Resource) {
            try {
                EObject firstEObj = ((Resource)element).getContents().get(0);
                ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(firstEObj);
                ModelType mType = ma.getModelType();
                if (ModelType.PHYSICAL_LITERAL == mType) {
                    return ModelIdentifier.getImage(ModelIdentifier.RELATIONAL_SOURCE_MODEL_ID);
                }
                if( ModelIdentifier.isFunctionModelUri(ma.getPrimaryMetamodelUri())) {
                	return ModelIdentifier.getImage(ModelIdentifier.FUNCTION_MODEL_ID);
                }
                if( ModelIdentifier.isXmlViewModel(ma.getPrimaryMetamodelUri())) {
                	return ModelIdentifier.getImage(ModelIdentifier.XML_VIEW_MODEL_ID);
                }
                if( ModelIdentifier.isWebServicesViewModel(ma.getPrimaryMetamodelUri())) {
                	return ModelIdentifier.getImage(ModelIdentifier.WEB_SERVICES_VIEW_MODEL_ID);
                }
                return ModelIdentifier.getImage(ModelIdentifier.RELATIONAL_VIEW_MODEL_ID);
            } catch (ModelerCoreException e) {
                RolesUiPlugin.UTIL.log(IStatus.ERROR, RolesUiPlugin.UTIL.getString("errorFindingImageForObject", element, e)); //$NON-NLS-1$
            }
        }

        return null;
    }

    @Override
    public Object getParent( Object element ) {
        if (element instanceof EObject) {
            return ModelUtilities.getModelContentProvider().getParent(element);
        } else if (element instanceof Resource) {
            return ((Resource)element).getResourceSet();
        }
        return null;
    }
    
    private Image getSecurityStatusImage(Object target) {
    	Permission perm = this.handler.getPermission(target);
    	if( perm != null ) {
    		if( allowsRowFilter(target) || allowsColumnMask(target) ) {
    			if( perm.getCondition() != null || perm.getMask() != null ) {
    				return BLUE_STATUS_BOX;
    			}
	    		return WHITE_STATUS_BOX;
	    	} else {
	    		return GRAY_STATUS_BOX;
	    	}
    	} else if( allowsRowFilter(target) || allowsColumnMask(target) ){
    		return WHITE_STATUS_BOX;
    	}
    	
    	return GRAY_STATUS_BOX;
    	
    }

    /**
     * Returns a string target name for the specified element
	 * 
     */
    public String getTargetName( Object element ) {
        String targetName = null;
        if (element instanceof Resource) {
            return getResourceName((Resource)element);
        } else if (element instanceof EObject) {
            EObject eObj = (EObject)element;
            targetName = getResourceName(eObj.eResource()) + '/' + ModelerCore.getModelEditor().getModelRelativePath(eObj);
        }

        if(targetName!=null) {
            targetName = targetName.replace(B_SLASH, DELIM);
        }
        
        return targetName;
    }

    /**
     * Returns an actual Model Object (EObject or Resource) for the specified Permission
     * 
	 * 
     * @param perm
     * @return
     */
    public Object getPermissionTargetObject( Permission perm ) {
        String targetName = perm.getTargetName();
        for (Resource res : resources) {
            if (getTargetName(res).equals(targetName)) {
                return res;
            }

            for (Object child : getChildren(res)) {
                if (getTargetName(child).equals(targetName)) {
                    return child;
                }
                Object target = getTargetObjectInChildren(child, targetName);
                if (target != null) {
                    return target;
                }
            }
        }

        return null;
    }

    /*
     * Returns the file name only minus the xmi file extension
     */
    private String getResourceName( Resource res ) {

        if (res.getURI().path().endsWith(".xmi")) { //$NON-NLS-1$
            Path path = new Path(res.getURI().path());
            return path.removeFileExtension().lastSegment();
        }
        return res.getURI().path();
    }

    /*
     * Finds the target object under the specified parent. May return null.
     * 
     */
	private Object getTargetObjectInChildren(Object parent, String targetName) {

        for (Object child : getChildren(parent)) {
            if (getTargetName(child).equals(targetName)) {
                return child;
            }
            Object target = getTargetObjectInChildren(child, targetName);
            if (target != null) {
                return target;
            }
        }

        return null;
    }

    /*
     * Helper method to retrieve the text value for an EMF object (EObject or Resource)
     * 
     */
    private String getText( Object element ) {
        if (element instanceof EObject) {
            ILabelProvider p = ModelUtilities.getEMFLabelProvider();
            return p.getText(element);
        } else if (element instanceof Resource) {
            return ((Resource)element).getURI().lastSegment();
        }

        return StringConstants.EMPTY_STRING;
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

        if (handler.hasPermissions()) {
            return;
        }

        for (Resource res : resources) {
            String resPath = getTargetName(res);
            Permission perm = new Permission(resPath, false, true, false, false, false, false);
            perm.setPrimary(true);
            handler.addPermission(res, perm);
        }
    }

    @Override
    public void removeListener( ILabelProviderListener listener ) {
        // NO OPisRowFilter
    }

    /**
	 * Performs the necessary permission CRUD value changes based on the target element and the CRUD type.
	 * This method is targeted for use by a single-click editor changing ONE CRUD boolean value for one object.
     * 
     * @param element
     * @param crudType
     */
	public void togglePermission( Object element, Crud.Type crudType ) {
        handler.toggleElementPermission(element, crudType);
    }

    /**
     * Get any warnings which we may need to show the user, before toggling.
     * 
     * @param element
     * @param crudType
     * @return IStatus status toggle warnings to display, if any
     */
    public IStatus getToggleStatus( Object element,
                                    Crud.Type crudType ) {
        return handler.getToggleStatus(element, crudType);
    }
    
    public void loadPermissions(Collection<Permission> permissions) {
        this.handler.loadPermissions(permissions);
    }
    
    public Collection<Permission> getPermissions() {
    	return this.handler.getPermissions();
    }
    
	public List<Permission> getPermissionsWithRowBasedSecurity() {
		return this.handler.getPermissionsWithRowBasedSecurity();
	}
	
	public Permission getPermission(String targetName) {
		return this.handler.getPermission(targetName);
	}
	
	public Permission getPermission(Object element) {
		return this.handler.getPermission(element);
	}
	
	public List<Permission> getPermissionsWithColumnMasking() {
		return handler.getPermissionsWithColumnMasking();
	}
	
	public void removeRowBasedSecurity(Permission permission) {
		this.handler.removeRowBasedSecurity(permission);
	}
	
	public void setRowsBasedSecurity(String targetName, String condition, boolean constraint) {
		this.handler.setRowsBasedSecurity(targetName, condition, constraint);
	}
	
	public void removeColumnMask(Permission permission) {
		this.handler.removeColumnMask(permission);
	}
	
	public void removeColumnMask(String targetName) {
		this.handler.removeColumnMask(targetName);
	}
	
	public void setColumnMask(String targetName, String condition, String mask, int order) {
		this.handler.setColumnMask(targetName, condition, mask, order);
	}
	
	public List<String> getAllowedLanguages() {
		return this.handler.getAllowedLanguages();
	}
	
	public void addAllowedLanguage(String language) {
		this.handler.addAllowedLanguage(language);
	}
	
	public void removeAllowedLanguage(String language) {
		this.handler.removeAllowedLanguage(language);
	}
	
	public Permission createPermission(Object target) {
		String targetName = getTargetName(target);
		Permission perm = new Permission(targetName, new Crud(null, null, null, null, null, null));
		perm.setCanFilter(allowsRowFilter(target));
		perm.setCanMask(allowsColumnMask(target));
		this.handler.addPermission(target, perm);
		return perm;
	}
	
	public boolean allowsRowFilter(Object target) {
		return (target instanceof Table || target instanceof View || target instanceof Procedure);
	}
	
	public boolean allowsColumnMask(Object target) {
		return target instanceof Column;
	}
	
	public boolean allowsCondition(Object target) {
		return allowsColumnMask(target) || allowsRowFilter(target);
	}
	
	public String getSecurityDialogMessage(Object target) {
		if( target instanceof Column ) {
			return NLS.bind(Messages.setSecurityValuesFor_0_message, Messages.column);
		} else if( target instanceof Table ) {
			return NLS.bind(Messages.setSecurityValuesFor_0_message, Messages.table);
		} else if( target instanceof View ) {
			return NLS.bind(Messages.setSecurityValuesFor_0_message, Messages.view);
		} else if( target instanceof Procedure ) {
			return NLS.bind(Messages.setSecurityValuesFor_0_message, Messages.procedure);
		}
		return StringConstants.EMPTY_STRING;
	}
	
	public void handlePermissionChanged(Permission permission) {
		this.handler.handlePermissionChanged(permission);
	}

}
