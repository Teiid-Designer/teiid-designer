package com.metamatrix.modeler.internal.ui.actions;

import java.util.List;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.actions.workers.CloneWorker;
import com.metamatrix.modeler.internal.ui.viewsupport.DiagramHelperManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.actions.IPasteSpecialContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

public final class DesignerUiPropertyTester extends PropertyTester {

    /**
     * The toString() values of these enum values match entries in plugin.xml.
     */
    private enum Tests {
        CLONE_ALLOWED,
        EDITABLE_OBJECT,
        MODEL_PROJECT_OBJECT,
        READONLY_MODEL_OBJECT,
        RENAME_ALLOWED,
        PASTE_SPECIAL_ALLOWED
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
     *      java.lang.Object)
     */
    @Override
    public boolean test( Object receiver,
                         String property,
                         Object[] args,
                         Object expectedValue ) {
        if (Tests.CLONE_ALLOWED.toString().equals(property)) {
            return isCloneAllowed(receiver);
        }

        if (Tests.EDITABLE_OBJECT.toString().equals(property)) {
            return isEditableObject(receiver);
        }

        if (Tests.MODEL_PROJECT_OBJECT.toString().equals(property)) {
            return isModelProjectObject(receiver);
        }

        if (Tests.PASTE_SPECIAL_ALLOWED.toString().equals(property)) {
            return isPasteSpecialAllowed(receiver);
        }

        if (Tests.READONLY_MODEL_OBJECT.toString().equals(property)) {
            return isReadOnlyModelObject(receiver);
        }

        if (Tests.RENAME_ALLOWED.toString().equals(property)) {
            return isRenameAllowed(receiver);
        }

        // make sure all properties have been handled
        assert false : "Did not test all properties"; //$NON-NLS-1$

        return false;
    }

    private boolean isCloneAllowed( Object receiver ) {
        ISelection selection = null;

        if (receiver instanceof List<?>) {
            List<?> items = (List<?>)receiver;

            if (items.isEmpty()) {
                return false;
            }

            selection = new StructuredSelection(items);
        } else {
            selection = new StructuredSelection(receiver);
        }

        // let work determine enablement
        CloneWorker cloneWorker = new CloneWorker(true);
        return cloneWorker.selectionChanged(selection);
    }

    private boolean isEditableObject( Object receiver ) {
        assert (receiver instanceof EObject) : "Receiver is not an EObject. Check handler's enabledWhen logic in plugin.xml."; //$NON-NLS-1$
        return ModelEditorManager.canEdit((EObject)receiver);
    }

    private boolean isModelProjectObject( Object receiver ) {
        if (receiver instanceof IFile) {
            return ModelUtilities.isModelFile((IFile)receiver);
        }

        if (receiver instanceof EObject) {
            return (ModelerCore.getModelWorkspace().findModelResource((EObject)receiver) != null);
        }

        return false;
    }

    private boolean isPasteSpecialAllowed( Object receiver ) {
        for (IPasteSpecialContributor contributor : PasteSpecialAction.getPasteSpecialContributors()) {
            if (contributor.canPaste()) {
                return true;
            }
        }

        return false;
    }
    
    private boolean isReadOnlyModelObject( Object receiver) {
        if (receiver instanceof EObject) {
            return ModelObjectUtilities.isReadOnly((EObject)receiver);
        }

        assert false : "Receiver is not an EObject. Check handler's enabledWhen logic in plugin.xml"; //$NON-NLS-1$
        return false;
    }

    private boolean isRenameAllowed( Object receiver ) {
        assert (receiver instanceof EObject) : "Receiver is not an EObject. Check handler's enabledWhen logic in plugin.xml."; //$NON-NLS-1$
        EObject eObj = (EObject)receiver;

        if (isReadOnlyModelObject(eObj)) {
            return false;
        }

        if (eObj instanceof Diagram) {
            return DiagramHelperManager.canRename((Diagram)eObj);
        }

        if (eObj instanceof ModelImport) {
            return false;
        }

        return ModelerCore.getModelEditor().hasName(eObj);
    }

}
