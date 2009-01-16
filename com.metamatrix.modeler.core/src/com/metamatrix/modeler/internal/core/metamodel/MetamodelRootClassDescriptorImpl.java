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

package com.metamatrix.modeler.internal.core.metamodel;

import org.osgi.framework.Bundle;
import com.metamatrix.modeler.core.metamodel.MetamodelRootClassDescriptor;
import com.metamatrix.modeler.internal.core.ExtensionDescriptorImpl;

/**
 *
 */
public class MetamodelRootClassDescriptorImpl extends ExtensionDescriptorImpl implements MetamodelRootClassDescriptor {

    private int maxOccurs;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**
	 * Construct an instance of ExternalResourceSetDescriptorImpl.
	 * 
	 * @param id
	 * @param className
	 * @param bundle
	 */
    public MetamodelRootClassDescriptorImpl( final Object id,
	                                         final String className,
	                                         final Bundle bundle ) {
		super(id, className, bundle);
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRootClassDescriptor#getMaxOccurs()
     */
    public int getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @since 4.3
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MetamodelRootClassDescriptorImpl)) {
            return false;
        }
        MetamodelRootClassDescriptorImpl that = (MetamodelRootClassDescriptorImpl)obj;
        if (this.maxOccurs != that.maxOccurs) {
            return false;
        }
        return super.equals(obj);
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    public void setMaxOccurs(int value) {
        maxOccurs = value;
    }

    public void setMaxOccurs(final String valueString) {
        if (valueString == null || valueString.equals("*")) { //$NON-NLS-1$
            this.maxOccurs = -1;
        } else {
            this.maxOccurs = Integer.parseInt(valueString);
        }
    }

}
