/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.internal.ui.actions.ModelObjectAction;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;

/**
 * TransformationAction provides transformation-specific helper methods.
 */
public class TransformationAction extends ModelObjectAction {
//    private static int nAction = -1;
    private EObject transformationEObject;
    private Diagram currentDiagram;
//    private int iAction = 0;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct an instance of TransformationAction.
     */
    public TransformationAction(Diagram diagram) {
        super(UiPlugin.getDefault());
        this.currentDiagram = diagram;
//        nAction++;
//        iAction = nAction;
    }

    /**
     * Construct an instance of TransformationAction.
     */
    public TransformationAction(EObject transformationEObject,
                                Diagram diagram) {
        super(UiPlugin.getDefault());
        this.transformationEObject = transformationEObject;
        this.currentDiagram = diagram;
//        nAction++;
//        iAction = nAction;
    }
    
//    /* (non-Javadoc)
//     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
//     */
//    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
//        super.selectionChanged(thePart, theSelection);
//        System.out.println(" TransformationAction[" + iAction + "] = " + this.getId());
//    }

    public EObject getTransformation() {
        if (transformationEObject != null) {
            if (transformationEObject.eResource() == null) {
                // Need to log this case
                // Defect 18880 looks like some other problem/error resulted in this action not being disposed and thusly
                // unregistered for selection. It appears that the t-root for this action became stale, (eResource == null)
                // and the enablement call will result in a NULL eResource() error.
//                String message = "[TransformationAction.getTransformation()] The following transformation root has become stale: " + transformationEObject; //$NON-NLS-1$
//                UiConstants.Util.log(IStatus.WARNING, message);
                return null;
            }
            return transformationEObject;
        }
        return null;
    }

    public void setTransformation(EObject transformationEObject) {
        this.transformationEObject = transformationEObject;
    }

    public void setDiagram(Diagram diagram) {
        this.currentDiagram = diagram;
    }

    @Override
    protected void doRun() {
    }

    protected boolean isDependencyDiagram() {
        if (currentDiagram != null
            && currentDiagram.getType() != null
            && currentDiagram.getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID))
            return true;

        return false;
    }

    protected boolean isWritable(EObject eObject) {
        return !ModelObjectUtilities.isReadOnly(eObject);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return false;
    }

    public Diagram getCurrentDiagram() {
        return this.currentDiagram;
    }
}
