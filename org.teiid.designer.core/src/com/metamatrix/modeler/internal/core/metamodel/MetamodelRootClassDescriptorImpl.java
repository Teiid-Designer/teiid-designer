/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
