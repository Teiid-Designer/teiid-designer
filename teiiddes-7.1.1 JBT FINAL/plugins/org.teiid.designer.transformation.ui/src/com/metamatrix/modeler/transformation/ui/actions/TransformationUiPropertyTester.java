package com.metamatrix.modeler.transformation.ui.actions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;

public final class TransformationUiPropertyTester extends PropertyTester {

    private enum Tests {
        VIRTUAL_MODEL
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
        assert (receiver instanceof IFile) : "Receiver was not an IFile. Check plugin.xml extension."; //$NON-NLS-1$
        IFile file = (IFile)receiver;

        if (Tests.VIRTUAL_MODEL.toString().equals(property)) {
            if (ModelUtilities.isModelFile(file)) {
                try {
                    ModelResource modelResource = ModelUtil.getModelResource(file, false);

                    if (ModelUtilities.isVirtual(modelResource)) {
                        return true;
                    }
                } catch (ModelWorkspaceException e) {
                    UiConstants.Util.log(e);
                }
            }
        }

        return false;
    }

}
