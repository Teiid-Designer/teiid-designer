/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClass;

/**
 * MetamodelEntity
 */
public interface MetamodelEntity {
    /**
     * Return the display name for the given entity
     * @return String name
     */
    String getDisplayName();
    
    /**
    * Return the plural display name for the given entity
    * @return String PluralDisplayName
    */
    String getPluralDisplayName();

    /**
    * Return the icon path for the given entity
    * @return String icon path
    */    
    String getIconPath();
    
    /**
    * Return the tooltip for the given entity
    * @return String tooltip
    */    
    String getTooltip();
    
    /**
    * Return the MetamodelAspect for the given id
    * @param String id
    * @return MetamodelAspect for the given id
    */    
    MetamodelAspect getMetamodelAspect(String id);
    
    /**
    * Return the collection of all Metamodel Aspects for the given entity
    * @return Collection Metamodel Aspects
    */    
    Collection getMetamodelAspects();
    
    /**
    * Return the EClass for the given metamodel entity
    * @return EClass
    */    
    EClass getEClass();

    /**
     * Return the list of all {@link EClass} instances that are
     * referenced, through non-containment references, by this class.
     * The list consists of all referenced classes both local and
     * inherited.
     * @return List
     */
    List getReferencedClasses();

	/**
	 * Return the list of all {@link EReference} instances for this
	 * MetamodelEntity that are uni directonal and non-containment,
	 * ie: each EReference does not have an opposite EReference and
	 * thus is uni directional and is non-containment.  
	 * @return List
	 */
	List getUniDirectionalReferences();

    /**
     * Return the list of all {@link EClass} instances that can
     * be contained by this class.  The list consists of 
     * all referenced classes both local and inherited.
     * @return List
     */
    List getContainedClasses();
    
    /**
     * Return the list of all {@link EAttribute} instances for 
     * this class.  The list consists of all attributes both
     * local and inherited.
     * @return List
     */
    List getAttributes();
        
}
