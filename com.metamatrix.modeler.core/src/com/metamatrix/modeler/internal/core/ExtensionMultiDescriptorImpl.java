/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
