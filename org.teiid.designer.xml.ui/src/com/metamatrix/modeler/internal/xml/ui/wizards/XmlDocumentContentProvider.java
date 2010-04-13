/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.teiid.designer.core.xmi.XMIHeader;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * @since 5.0
 */
public class XmlDocumentContentProvider extends ModelExplorerContentProvider {

    private Object root;

    public XmlDocumentContentProvider() {
        super.setShowModelContent(true);
    }

    public XmlDocumentContentProvider( final Object rootNode ) {
        this.root = rootNode;
        super.setShowModelContent(true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren( final Object parentElement ) {
        if (isXmlDocumentModel(parentElement)) {
            final Object[] allChildren = super.getChildren(parentElement);
            // Now only return XmlDocuments
            final List xmlDocNodes = new ArrayList();
            for (final Object element : allChildren)
                if (element instanceof XmlDocument) xmlDocNodes.add(element);

            if (xmlDocNodes.isEmpty()) return new Object[0];

            return xmlDocNodes.toArray();
        }

        return super.getChildren(parentElement);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent( final Object element ) {
        if (element instanceof IResource) return root;
        return super.getParent(element);
    }

    // /**
    // * @param theRoot The root to set.
    // * @since 5.0
    // */
    // public void setRoot(Object theRoot) {
    // this.root = theRoot;
    // }

    private boolean isXmlDocumentModel( final Object object ) {
        if (object instanceof IResource && ModelUtilities.isModelFile((IResource)object)) {
            final IResource iResource = (IResource)object;
            final XMIHeader header = ModelUtil.getXmiHeader(iResource);
            if (header != null) {
                final String mmURI = header.getPrimaryMetamodelURI();
                if (mmURI != null && mmURI.equals(XmlDocumentPackage.eNS_URI)) return true;
            }
        }

        return false;
    }

}
