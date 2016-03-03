/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.pakkage;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.IDiagramProvider;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.diagram.PresentationEntity;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * <p>PackageDiagramContentProvider is the specific ContentProvider for all package diagram model
 *  objects inside ModelResources.</p>
 * <p>PackageDiagramContentProvider implements ITreeContentProvider because this interface has the right
 * methods for the functionality this class provides.</p>
 *
 * @since 8.0
 */
final public class PackageDiagramContentProvider implements ITreeContentProvider, IDiagramProvider, DiagramUiConstants {
    private IPackageDiagramProvider packageDiagramProvider;

    // ===========================================
    // Static

    private static final Object[] NO_CHILDREN = new Object[0];

    // ===========================================
    // Constructors

    /**
     * Construct an instance of ModelObjectContentProvider.
     */
    public PackageDiagramContentProvider() {
        packageDiagramProvider = new PackageDiagramProvider();
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

            } else if ( parentElement instanceof EObject ) {
                // get resource, and get Diagrams for resource.
                EObject eObject = (EObject) parentElement;
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
                if( modelResource != null ) {
	                List diagramList = new ArrayList();

	                ArrayList allChildren = new ArrayList();

	                MetamodelAspect aspect = ModelObjectUtilities.getUmlAspect(eObject);

	                if ( aspect instanceof UmlClassifier ) {
	                    return children;
	                } else if ( aspect instanceof UmlPackage ) {
	                    // make sure this object contains a Package Diagram
	                    diagramList = getPackageDiagrams(modelResource, eObject);
	                }

	                if ( !diagramList.isEmpty() ) {
	                    allChildren.addAll(diagramList);
	                    children = allChildren.toArray();
	                }
                }
            } else if ( parentElement instanceof ModelResource ) {

                ModelResource modelResource = (ModelResource) parentElement;

                List diagramList = getPackageDiagrams(modelResource, null);
                ArrayList allChildren = new ArrayList();

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
    	return hasChildren(element, false);
//        Object[] children= getChildren(element);
//        return (children != null) && children.length > 0;
    }

    private boolean hasChildren(Object parentElement, boolean dummyBoolean) {
		if ( parentElement instanceof PresentationEntity ) {
			return false;
		} else if ( parentElement instanceof EObject ) {
			// get resource, and get Diagrams for resource.
			EObject eObject = (EObject) parentElement;
			ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(eObject);
			if( modelResource != null ) {

				MetamodelAspect aspect = ModelObjectUtilities.getUmlAspect(eObject);

				if ( aspect instanceof UmlClassifier ) {
					return false;
				} else if ( aspect instanceof UmlPackage ) {
					// All packages have a package diagram, so we return true.
					// If they don't exist yet, we don't care, that will be handled
					// in getChildre()
					return true;
				}
			}
		} else if ( parentElement instanceof ModelResource ) {
			return hasPackageDiagram((ModelResource) parentElement, null);
		}
		return false;
    }

    public Diagram getPackageDiagram(ModelResource modelResource, EObject eObject) {
        Diagram packageDiagram = null;

        // if eObject == null, then it's a 'Model' node, then we don't ask for a contributed package diagram.
        if( eObject != null ) {
            // Here's where we ask the DiagramTypeManager for a package diagram for a specific target and resource
            // if it comes back null, then we go ahead with the default here....
            packageDiagram = DiagramUiPlugin.getDiagramTypeManager().getPackageDiagram(modelResource, eObject, false);
        }
        else {
			try {
				packageDiagram = DiagramUiPlugin.getDiagramTypeManager().getPackageDiagram(modelResource, modelResource.getModelAnnotation(), false);
			} catch (ModelWorkspaceException e) {
                if (!modelResource.hasErrors()) {
                    DiagramUiConstants.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                }
			}
        }

        if( packageDiagram == null ) {
            packageDiagram = packageDiagramProvider.getPackageDiagram(modelResource, eObject, false);
        }
        return packageDiagram;
    }

    private List getPackageDiagrams(ModelResource modelResource, EObject eObject) {
        List packageDiagrams = new ArrayList();

        Diagram packageDiagram = getPackageDiagram(modelResource, eObject);

        if( packageDiagram != null )
            packageDiagrams.add(packageDiagram);

        return packageDiagrams;
    }

	private boolean hasPackageDiagram(ModelResource modelResource, EObject eObject) {
		List diagramList = null;

		try {
			diagramList = new ArrayList(modelResource.getModelDiagrams().getDiagrams(eObject));
		} catch (ModelWorkspaceException e) {
			String message = DiagramUiConstants.Util.getString("Problem in hasPackageDiagram() call for resource = ", modelResource.toString());  //$NON-NLS-1$
			DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
		}
		if( diagramList != null && !diagramList.isEmpty())
			return true;

		return false;
	}

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.IDiagramProvider#canDelete(org.teiid.designer.metamodels.diagram.Diagram)
     */
    @Override
	public boolean canDelete(Diagram diagram) {
        return false;
    }

}

