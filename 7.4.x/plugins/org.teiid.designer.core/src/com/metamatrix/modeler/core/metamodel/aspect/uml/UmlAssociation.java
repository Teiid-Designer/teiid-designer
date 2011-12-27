/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

/**
 * UmlAssociation
 */
public interface UmlAssociation extends UmlRelationship {
    //Show Mask Constants    
    public final static int SIGNATURE_NAME = 1;
    public final static int SIGNATURE_STEROTYPE = 2;
    public final static int SIGNATURE_PROPERTIES = 4;
    
    //Aggregation Constants
    public final static int AGGREGATION_NONE = 301;
    public final static int AGGREGATION_SHARED = 302;
    public final static int AGGREGATION_COMPOSITE = 303;
    
    //Navigability Constants
    public final static int NAVIGABILITY_NONE = 501;
    public final static int NAVIGABILITY_UNKNOWN = 502;
    public final static int NAVIGABILITY_NAVIGABLE = 503;
    
    /**
     * Get the end count for the given association
     * @param assoc
     * @return the end count
     */
    int getEndCount(Object assoc);
    
    /**
     * Get the role name for the given association end
     * @param assoc
     * @param end
     * @return role name
     */
    String getRoleName(Object assoc, int end);
    
    /**
     * Get the multiplicity string for the given association end
     * @param assoc
     * @param end
     * @return multiplicity string
     */
    String getMultiplicity(Object assoc, int end);
    
    /**
     * Get the aggreation constant for the given association end
     * @param assoc
     * @param end
     * @return the aggregation constant
     */
    int getAggregation(Object assoc, int end);
    
    /**
     * Return the properties for the given association end
     * @param assoc
     * @param end
     * @return the properties as a string
     */
    String[] getProperties(Object assoc, int end);
    
    /**
     * Return the navigability for the given association end
     * @param assoc
     * @param end
     * @return the navigability constant
     */
    int getNavigability(Object assoc, int end);
    
    /**
     * Return the Assoiciation End for the given end.
     * Ends are the actual objects referenced in the association
     * @param assoc
     * @param end which end to return
     * @return
     */
    EObject getEnd(Object assoc, int end);

    /**
     * Return the Assoiciation End Target for the given end.
     * End Targets are the objects to draw the association to.
     * @param assoc
     * @param end which end to return
     * @return
     */
    EObject getEndTarget(Object assoc, int end);
    
    /**
     * Set the role name for the given association end
     * @param assoc
     * @param end
     * @param name
     * @return IStatus result of the operation
     */
    IStatus setRoleName(Object assoc, int end, String name);
    
    /**
     * Set the Mulitplicity for the given association end
     * @param assoc
     * @param end
     * @param mult
     * @return IStatus result of the operation
     */
    IStatus setMultiplicity(Object assoc, int end, String mult);
    
    /**
     * Set the Propertyies for the given association end
     * @param assoc
     * @param end
     * @param props
     * @return IStatus result of the operation
     */
    IStatus setProperties(Object assoc, int end, String[] props);
    
    /**
     * Set the Navigability for the given association end
     * @param assoc
     * @param end
     * @param navigability
     * @return IStatus result of the operation
     */
    IStatus setNavigability(Object assoc, int end, int navigability);
}
