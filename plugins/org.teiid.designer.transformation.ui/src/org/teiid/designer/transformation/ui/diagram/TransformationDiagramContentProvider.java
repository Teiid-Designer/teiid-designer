/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.diagram;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.diagram.ui.IDiagramProvider;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.diagram.PresentationEntity;
import org.teiid.designer.metamodels.relational.aspects.uml.IndexAspect;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.util.TransformationDiagramUtil;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * <p>TransformationDiagramContentProvider is the specific ContentProvider for all package diagram model
 *  objects inside ModelResources.</p>
 * <p>TransformationDiagramContentProvider implements ITreeContentProvider because this interface has the right
 * methods for the functionality this class provides.</p>
 *
 * @since 8.0
 */
final public class TransformationDiagramContentProvider implements ITreeContentProvider, IDiagramProvider, UiConstants {

    // ===========================================
    // Static

    private static final Object[] NO_CHILDREN = new Object[0];
    private static boolean PERSIST_DIAGRAMS = true;

    // ===========================================
    // Constructors

    /**
     * Construct an instance of ModelObjectContentProvider.
     */
    public TransformationDiagramContentProvider() {
    }
    
    // ===========================================
    // Methods

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
	public void dispose() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
	public synchronized Object[] getChildren(Object parentElement) {

        Object[] children = NO_CHILDREN;
            
        if ( parentElement instanceof PresentationEntity ) {
            // then NO_CHILDREN is the right answer

        } else if ( parentElement instanceof EObject && 
                    ModelObjectUtilities.isVirtual((EObject)parentElement) ) {
                        
            // get resource, and get Diagrams for resource.
            EObject eObject = (EObject) parentElement;
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
            
            List diagramList = new ArrayList();
            
            ArrayList allChildren = new ArrayList();
            
            MetamodelAspect aspect = ModelObjectUtilities.getUmlAspect(eObject);
            
            if( aspect instanceof UmlPackage ) {
                return children;
            } else if ( aspect instanceof UmlClassifier && !(aspect instanceof IndexAspect) ) {
                // We should only find/make t-diagrams for classifiers that arent nested. So we check parent.
                Object parentParentObject = ((EObject)parentElement).eContainer();
                if( parentParentObject instanceof EObject ) {
                    MetamodelAspect parentAspect = ModelObjectUtilities.getUmlAspect((EObject)parentParentObject);
                    if( !(parentAspect instanceof UmlClassifier) || !TransformationHelper.isSqlProcedure(parentParentObject) ) {
                        diagramList = TransformationDiagramUtil.getTransformationDiagrams(modelResource, eObject, false, PERSIST_DIAGRAMS);
                    }
                } else {
                    // case where parent is probably the model resource.
                    diagramList = TransformationDiagramUtil.getTransformationDiagrams(modelResource, eObject, false, PERSIST_DIAGRAMS);
                }
            }
            
            if ( !diagramList.isEmpty() ) {
                allChildren.addAll(diagramList);
                children = allChildren.toArray();
            }
        }

        return children;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
	public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
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
    @Override
	public boolean hasChildren(Object element) {
        Object[] children= getChildren(element);
        return (children != null) && children.length > 0;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramProvider#canDelete(org.teiid.designer.metamodels.diagram.Diagram)
     */
    @Override
	public boolean canDelete(Diagram diagram) {
        return false;
    }
}

