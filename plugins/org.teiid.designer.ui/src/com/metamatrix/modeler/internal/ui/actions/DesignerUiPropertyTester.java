package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

public final class DesignerUiPropertyTester extends PropertyTester {

    private enum Tests {
        EDITABLE_OBJECT
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
        if (Tests.EDITABLE_OBJECT.toString().equals(property)) {
            assert (receiver instanceof EObject) : "Receiver is not an EObject. Check EditHandler's enabledWhen logic in plugin.xml."; //$NON-NLS-1$
            return ModelEditorManager.canEdit((EObject)receiver);
        }

        return false;
    }

}
