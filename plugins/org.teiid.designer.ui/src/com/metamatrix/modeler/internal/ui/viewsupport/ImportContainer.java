/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.HashCodeUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * ImportContainer is the import declaration node beneath the Model node in the ModelOutline tree.
 * It wrapps a ModelAnnotation so that the NewChild/NewSibling action won't see the ModelAnnotation
 * as an EObject in the tree.
 */
public class ImportContainer
implements UiConstants, IAdaptable {

    private ModelAnnotation modelAnnotation;
    private Resource resource;
    
    public ImportContainer(ModelAnnotation modelAnnotation, Resource resource) {
        this.modelAnnotation = modelAnnotation;
        this.resource = resource;
    }

    @Override
    public String toString() {
        final int size = this.modelAnnotation == null ? 0 : this.modelAnnotation.getModelImports().size();
        return Util.getString("ImportContainer.importDeclaration", size); //$NON-NLS-1$
    }

    public ModelAnnotation getModelAnnotation() {
        return this.modelAnnotation;
    }

    public Resource getResource() {
        return this.resource;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        return null;
    }
    
    
    /** 
     * @see java.lang.Object#equals(java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean equals(Object theObject) {
        boolean result = super.equals(theObject);
        
        if (!result) {
            if ((theObject != null) && (theObject instanceof ImportContainer)) {
                result = getResource().equals(((ImportContainer)theObject).getResource());
            }
        }
        
        return result;
    }
    
    /** 
     * @see java.lang.Object#hashCode()
     * @since 4.2
     */
    @Override
    public int hashCode() {
        return HashCodeUtil.hashCode(super.hashCode(), getResource());
    }

}
