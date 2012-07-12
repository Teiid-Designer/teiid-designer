/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.properties;

import java.util.List;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.metamodels.relationship.RelationshipMetamodelPlugin;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.relationship.ui.UiPlugin;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;


public class RelationshipTypeProvider extends ModelExplorerContentProvider {

    Object builtInTypeFolder = new RelationshipTypeFolder();

    /**
     * Construct an instance of RelationshipTypeProvider.
     */
    public RelationshipTypeProvider() {
        super();
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements( Object inputElement ) {
        if (inputElement instanceof IWorkspaceRoot) {
            Object[] children = super.getElements(inputElement);
            Object[] result = new Object[children.length + 1];
            result[0] = builtInTypeFolder;
            for (int i = 0; i < children.length; ++i) {
                result[i + 1] = children[i];
            }
            return result;
        }

        return super.getElements(inputElement);
    }

    public ILabelProvider getLabelProvider() {
        return new TypeLabelProvider();
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren( Object parentElement ) {
        if (parentElement == builtInTypeFolder) {
            List builtInTypes = RelationshipMetamodelPlugin.getBuiltInRelationshipTypeManager().getAllBuiltInRelationshipTypes();
            return builtInTypes.toArray();
        }
        return super.getChildren(parentElement);
    }

    public class TypeLabelProvider extends ModelExplorerLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            if (element == builtInTypeFolder) {
                return UiPlugin.getDefault().getImage(UiConstants.Images.TYPE_FOLDER);
            }
            return super.getImage(element);
        }

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            if (element == builtInTypeFolder) {
                return UiConstants.Util.getString("RelationshipPropertyEditorFactory.builtInTypes"); //$NON-NLS-1$
            }
            return super.getText(element);
        }

    }
}
