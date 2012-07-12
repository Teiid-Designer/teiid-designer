package org.teiid.designer.transformation.ui.actions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


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
