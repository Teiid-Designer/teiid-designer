/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.diagram;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.PresentationEntity;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.diagram.ui.IDiagramProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.UiConstants;

/**
 * <p>TransformationDiagramContentProvider is the specific ContentProvider for all package diagram model
 *  objects inside ModelResources.</p>
 * <p>TransformationDiagramContentProvider implements ITreeContentProvider because this interface has the right
 * methods for the functionality this class provides.</p>
 */
final public class MappingDiagramContentProvider implements ITreeContentProvider, IDiagramProvider, UiConstants {

    // ===========================================
    // Static

    private static final Object[] NO_CHILDREN = new Object[0];

    // ===========================================
    // Constructors

    /**
     * Construct an instance of ModelObjectContentProvider.
     */
    public MappingDiagramContentProvider() {
    }
    
    // ===========================================
    // Methods

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public synchronized Object[] getChildren(Object parentElement) {

        Object[] children = NO_CHILDREN;
            
        if ( parentElement instanceof PresentationEntity ) {
            // then NO_CHILDREN is the right answer

        } else if ( parentElement instanceof EObject && 
                    ModelObjectUtilities.isVirtual((EObject)parentElement) ) {
                        
            // get resource, and get Diagrams for resource.
            EObject eObject = (EObject) parentElement;
            
            MetamodelAspect aspect = ModelObjectUtilities.getUmlAspect(eObject);
            
            if( aspect instanceof UmlPackage ) {
                return children;
            }
        }

        return children;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        Object result = null;
        if ( element instanceof Diagram ) {
            Object target = ((Diagram) element).getTarget();
            if ( target instanceof EObject ) {
                result = target;
            } else if ( target instanceof ModelAnnotation ) {
                // the diagram is underneath the model file node - return the IResource
                result = ModelUtilities.getModelResourceForModelObject((ModelAnnotation) target);
            }
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object parentElement) {
//        Object[] children= getChildren(parentElement);
//        return (children != null) && children.length > 0;
		if ( parentElement instanceof PresentationEntity ) {
			return false;
		} else if ( parentElement instanceof EObject && 
					ModelObjectUtilities.isVirtual((EObject)parentElement) ) {
                        
			// get resource, and get Diagrams for resource.
			EObject eObject = (EObject) parentElement;
            
			MetamodelAspect aspect = ModelObjectUtilities.getUmlAspect(eObject);
            
			if( aspect instanceof UmlPackage ) {
				return true;
			}
		}
		return false;
    }
    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramProvider#canDelete(com.metamatrix.metamodels.diagram.Diagram)
     */
    public boolean canDelete(Diagram diagram) {
        return false;
    }
}

