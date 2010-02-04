/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.PresentationEntity;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.core.workspace.ModelDiagrams;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.IDiagramProvider;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * <p>CustomDiagramContentProvider is the specific ContentProvider for all package diagram model
 *  objects inside ModelResources.</p>
 * <p>CustomDiagramContentProvider implements ITreeContentProvider because this interface has the right
 * methods for the functionality this class provides.</p>
 */
final public class CustomDiagramContentProvider implements ITreeContentProvider, IDiagramProvider, DiagramUiConstants {

    // ===========================================
    // Static

    private static final Object[] NO_CHILDREN = new Object[0];

    // ===========================================
    // Constructors

    /**
     * Construct an instance of ModelObjectContentProvider.
     */
    public CustomDiagramContentProvider() {
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
	                    diagramList = getCustomDiagrams(modelResource, eObject);
	                }
	                
	                if ( !diagramList.isEmpty() ) {
	                    allChildren.addAll(diagramList);
	                    children = allChildren.toArray();
	                }
				}
            } else if ( parentElement instanceof ModelResource ) {
                
                ModelResource modelResource = (ModelResource) parentElement;

                List diagramList = getCustomDiagrams(modelResource, null);
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
    public boolean hasChildren(Object element) {
        Object[] children= getChildren(element);
        return (children != null) && children.length > 0;
    }

    
    private List getCustomDiagrams(ModelResource modelResource, EObject eObject) {
        List customDiagrams = new ArrayList();
        ModelDiagrams modelDiagrams = null;
        try {
            modelDiagrams = modelResource.getModelDiagrams();
            if ( modelDiagrams != null && eObject != null ) {
                List diagramList = new ArrayList(modelDiagrams.getDiagrams(eObject));
                Iterator iter = diagramList.iterator();
                Diagram nextDiagram = null;
                while( iter.hasNext() ) {
                    nextDiagram = (Diagram)iter.next();
                    if( nextDiagram.getType() != null ) {
                        if( nextDiagram.getType().equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID) )
							customDiagrams.add(nextDiagram);
                    } else {
                        String message = "Diagram has no type!! shouldn't happen. Target = " + eObject + "   Diagram = " + nextDiagram;  //$NON-NLS-1$ //$NON-NLS-2$
                        System.out.println(message);
                    }

                }
            } else if( modelDiagrams != null ) { // This means get custom diagrams below the model Resource
				List diagramList = new ArrayList(modelDiagrams.getDiagrams(null));
				Iterator iter = diagramList.iterator();
				Diagram nextDiagram = null;
				while( iter.hasNext() ) {
					nextDiagram = (Diagram)iter.next();
					if( nextDiagram.getType() != null ) {
						if( nextDiagram.getType().equals(PluginConstants.CUSTOM_DIAGRAM_TYPE_ID) )
							customDiagrams.add(nextDiagram);
					} else {
						String message = "Diagram has no type!! shouldn't happen. Target = " + eObject + "   Diagram = " + nextDiagram;  //$NON-NLS-1$ //$NON-NLS-2$
						System.out.println(message);
					}

				}
            }
        } catch (ModelWorkspaceException e) {
            if ( !modelResource.hasErrors() ) {
                // No errors, then it's unexpected, so log ...
                String message = Util.getString("CustomDiagramContentProvider.getCustomDiagramError", modelResource.toString());  //$NON-NLS-1$
                Util.log(IStatus.ERROR, e, message);
            }
        }
        
        
        return customDiagrams;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramProvider#canDelete(com.metamatrix.metamodels.diagram.Diagram)
     */
    public boolean canDelete(Diagram diagram) {
        return true;
    }

}


