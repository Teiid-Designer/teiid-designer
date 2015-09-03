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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.teiid.core.designer.ModelerCoreException;
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
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *
 */
public class PermissionTreeProvider implements ILabelProvider, ITreeContentProvider {
    private static final Object[] NO_CHILDREN = new Object[0];
    private ITreeContentProvider modelProvider = ModelUtilities.getModelContentProvider();
    
    private Resource[] resources;
    
    public PermissionTreeProvider() {
        super();

    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
        if (element instanceof EObject) {
            ILabelProvider p = ModelUtilities.getEMFLabelProvider();
            return p.getText(element);
        } else if (element instanceof Resource) {
            return ((Resource)element).getURI().lastSegment();
        }

        return StringConstants.EMPTY_STRING;
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
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

        return resources;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
        Object[] children = NO_CHILDREN;

        if (parentElement instanceof EObject) {
            children = getFilteredModelContents(modelProvider.getChildren(parentElement));
        } else if (parentElement instanceof Resource) {
            children = getFilteredModelContents(((Resource)parentElement).getContents());
        }

        return children;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
        if (element instanceof EObject) {
            return ModelUtilities.getModelContentProvider().getParent(element);
        } else if (element instanceof Resource) {
            return ((Resource)element).getResourceSet();
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
        return true; //getChildren(element).length > 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

}
