/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.util.ArrayList;
import org.osgi.framework.Bundle;
import com.metamatrix.modeler.core.ExtensionDescriptor;

/**
 *
 */
public class ExtensionMultiDescriptorImpl extends ExtensionDescriptorImpl {

    private ArrayList children;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    public ExtensionMultiDescriptorImpl( final Object id,
                                         final String className,
                                         final Bundle bundle ) {
        super(id, className, bundle);
        this.children = new ArrayList();
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ExtensionDescriptor#getChildren()
     */
    @Override
    public ExtensionDescriptor[] getChildren() {
        if (children == null || children.size() == 0) {
            return EMPTY_ARRAY;
        }
        ExtensionDescriptor[] result = new ExtensionDescriptor[children.size()];
        children.toArray(result);
        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ExtensionDescriptor#isMultiDescriptor()
     */
    @Override
    public boolean isMultiDescriptor() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ExtensionDescriptor#getChildDescriptor(java.lang.Object)
     */
    @Override
    public ExtensionDescriptor getChildDescriptor( final Object id ) {
        if (children == null || children.size() == 0) {
            return null;
        }
        for (int i = 0, n = children.size(); i < n; i++) {
            final ExtensionDescriptor descriptor = (ExtensionDescriptor)children.get(i);
            final Object descriptorId = descriptor.getId();
            if (descriptorId != null && descriptorId.equals(id)) {
                return descriptor;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.validation.ValidationRuleSet#addRule(com.metamatrix.modeler.core.validation.ValidationRule)
     */
    public void addDescriptor( final ExtensionDescriptor descriptor ) {
        if (children == null) {
            children = new ArrayList();
        }
        final Object descriptorId = descriptor.getId();
        if (descriptorId != null && getChildDescriptor(descriptorId) != null) {
            children.remove(getChildDescriptor(descriptorId));
        }
        children.add(descriptor);
    }

}
