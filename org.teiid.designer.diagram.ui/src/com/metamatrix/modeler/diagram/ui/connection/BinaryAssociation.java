/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.Graphics;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship;

/**
 * BinaryAssocation
 */
public interface BinaryAssociation {
	// Source/Target Constants
	public static final int SOURCE_END = 0;
	public static final int TARGET_END = 1;

	public static final int TYPE_UNKNOWN_RELATIONSHIP = -1;
	public static final int TYPE_UML_ASSOCIATION = 0;
	public static final int TYPE_UML_DEPENDENCY = 1;
	public static final int TYPE_UML_GENERALIZATION = 2;

	// Visibility Constants
	public final static int VISIBILITY_PUBLIC = 100;
	public final static int VISIBILITY_PROTECTED = 101;
	public final static int VISIBILITY_PRIVATE = 102;
	public final static int VISIBILITY_DEFAULT = 103;

	// Show Mask Constants    
	public final static int SIGNATURE_NAME = 1;
	public final static int SIGNATURE_STEROTYPE = 2;
	public final static int SIGNATURE_PROPERTIES = 4;

	// Aggregation Constants
	public final static int AGGREGATION_NONE = 301;
	public final static int AGGREGATION_SHARED = 302;
	public final static int AGGREGATION_COMPOSITE = 303;

	// Navigability Constants
	public final static int NAVIGABILITY_NONE = 501;
	public final static int NAVIGABILITY_UNKNOWN = 502;
	public final static int NAVIGABILITY_NAVIGABLE = 503;
	
	public final static int LINE_SOLID   		= Graphics.LINE_SOLID;
	public final static int LINE_DASH   		= Graphics.LINE_DASH;
	public final static int LINE_DASHDOT   		= Graphics.LINE_DASHDOT;
	public final static int LINE_DOT   			= Graphics.LINE_DOT;
	public final static int LINE_DASHDOTDOT  	= Graphics.LINE_DASHDOTDOT;

	public final static int DECORATOR_NONE   			= 0;
	public final static int DECORATOR_ARROW_OPEN   		= 1;
	public final static int DECORATOR_ARROW_CLOSED 		= 2;
	public final static int DECORATOR_ARROW_FILLED 		= 3;
	public final static int DECORATOR_DIAMOND_OPEN  	= 4;
	public final static int DECORATOR_DIAMOND_FILLED  	= 5;
	public final static int DECORATOR_NON_NAVIGABLE  	= 6;

	/**
	 * Set the association that the other methods will use
	 * @param assoc
	 * @return the end count
	 */
	void setUmlAspect(UmlRelationship relationship);
	
	/**
	 * get the current relationship aspect
	 * @return the aspect
	 */
	UmlRelationship getRelationshipAspect();

	/**
	 * Get the end count for the given association
	 * @param assoc
	 * @return the end count
	 */
	int getEndCount();

	/**
	 * Get the role name for the given association end
	 * @param assoc
	 * @param end
	 * @return role name
	 */
	String getRoleName(int end);

	/**
	 * Get the multiplicity string for the given association end
	 * @param assoc
	 * @param end
	 * @return multiplicity string
	 */
	String getMultiplicity(int end);

	/**
	 * Get the aggreation constant for the given association end
	 * @param assoc
	 * @param end
	 * @return the aggregation constant
	 */
	int getAggregation(int end);

	/**
	 * Return the properties for the given association end
	 * @param assoc
	 * @param end
	 * @return the properties as a string
	 */
	String[] getProperties(int end);

	/**
	 * Return the navigability for the given association end
	 * @param assoc
	 * @param end
	 * @return the navigability constant
	 */
	int getNavigability(int end);

	/**
	 * Return the Assoiciation End for the given end inidicator.
	 * This should be, for instance, the FK or PK/UK
	 * @param assoc
	 * @param end which end to return
	 * @return
	 */
	EObject getEnd(int end);

	/**
	 * Return the Assoiciation End Target for the given end indicator.
	 * This should be the "Table"
	 * @param assoc
	 * @param end which end to return
	 * @return
	 */
	EObject getEndTarget(int end);

	/**
	 * Set the role name for the given association end
	 * @param assoc
	 * @param end
	 * @param name
	 * @return IStatus result of the operation
	 */
	IStatus setRoleName(int end, String name);

	/**
	 * Set the Mulitplicity for the given association end
	 * @param assoc
	 * @param end
	 * @param mult
	 * @return IStatus result of the operation
	 */
	IStatus setMultiplicity(int end, String mult);

	/**
	 * Set the Propertyies for the given association end
	 * @param assoc
	 * @param end
	 * @param props
	 * @return IStatus result of the operation
	 */
	IStatus setProperties(int end, String[] props);

	/**
	 * Set the Navigability for the given association end
	 * @param assoc
	 * @param end
	 * @param navigability
	 * @return IStatus result of the operation
	 */
	IStatus setNavigability(int end, int navigability);

	// ===========================================
	//  Visibility
	// ===========================================

	/**
	 * Return the Visibility constant for the given eObject
	 * @param eObject
	 * @return the visibility int constant
	 */
	int getVisibility();

	/**
	 * Return the Sterotype string for the given eObject
	 * @param eObject
	 * @return the Sterotype string
	 */
	String getStereotype();

	/**
	 * Return the Signature string for the given eObject
	 * @param eObject
	 * @param showMask the mask for which attributes constitue the signature
	 * @return the Signature string using the mask
	 */
	String getSignature(int showMask);

	/**
	 * Return the editable portion of the signature string for the given eObject
	 * @param eObject
	 * @return the editable portion of the signature string 
	 */
	String getEditableSignature();

	/**
	 * Set the Signature string for the given eObject
	 * @param eObject
	 * @param newSignature
	 * @return an IStatus object with the results of the set operation
	 */
	IStatus setSignature(String newSignature);

	/**
	 * Get's the relationship type for the association
	 * @return type;
	 */
	int getRelationshipType();
	
	/**
	 * Get's the relationship's reference EObject
	 * @return eObject;
	 */
	EObject getReference();
}
