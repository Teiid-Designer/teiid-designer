/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.uml;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

/**
 * UmlDependency - A dependency signifies a supplier/client relationship between model 
 * elements where the modification of the supplier may impact the client model elements. 
 * A dependency implies the semantics of the client is not complete without the supplier.
 * The presence of dependency relationships in a model does not have any runtime semantics 
 * implications, it is all given in terms of the model-elements that participate in the 
 * relationship, not in terms of their instances.
 * <p>
 * A dependency is shown as a dashed arrow between two model elements. The model element 
 * at the tail of the arrow (the client) depends on the model element at the arrowhead 
 * (the supplier). The arrow may be labeled with an optional stereotype and an optional 
 * name. It is possible to have a set of elements for the client or supplier. In this case, 
 * one or more arrows with their tails on the clients are connected the tails of one or more 
 * arrows with their heads on the suppliers. A small dot can be placed on the junction if
 * desired. A note on the dependency should be attached at the junction point.
 * </p>
 */
public interface UmlDependency extends UmlRelationship {
    
    //Navigability Constants
    public final static int NAVIGABILITY_NONE = 501;
    public final static int NAVIGABILITY_UNKNOWN = 502;
    public final static int NAVIGABILITY_NAVIGABLE = 503;

    /**
     * Returns the value of the '<em><b>Source</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
     * @param relationship
     * @return the value of the '<em>Source</em>' reference list.
     */
    List getSource(Object relationship);

    /**
     * Returns the value of the '<em><b>Target</b></em>' reference list.
     * The target element is that affected by the supplier element. In some cases 
     * (such as a Trace Abstraction) the direction is unimportant and serves only 
     * to distinguish the two elements.
     * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
     * @param relationship
     * @return the value of the '<em>Target</em>' reference list.
     */
    List getTarget(Object relationship);

    /**
     * Return the list of values referenced at the given end.
     * @param relationship
     * @param end which end to return
     * @return
     */
    List getEndObjects(Object relationship, int end);
    
    /**
     * Return true if the relationship models an Abstraction. An abstraction is 
     * a relationship that relates two elements or sets of elements that represent 
     * the same concept at different levels of abstraction or from different viewpoints. 
     * (See also, the definition of abstration in the Glossary.) In the metamodel, 
     * an Abstraction is a Dependency in which there is a mapping between the supplier 
     * and the client.
     * @param relationship
     * @return
     */
    boolean isAbstraction(Object relationship);
    
    /**
     * Return true if the relationship models a Usage. A usage is a relationship in which 
     * one element requires another element (or set of elements) for its full implementation 
     * or operation. In the metamodel, a Usage is a Dependency in which the client requires 
     * the presence of the supplier.
     * @param relationship
     * @return
     */
    boolean isUsage(Object relationship);
    
    /**
     * Return true if the relationship models a Permission.  A Permission signifies 
     * granting of access rights from the supplier model element to a client model 
     * element. Or to put it another way, it signifies that the client requires access 
     * to some or all of the constituent elements of the supplier. The supplier element 
     * gives the client permission to access some or all of its constituents elements.
     * @param relationship
     * @return
     */
    boolean isPermission(Object relationship);
    
    /**
     * Return true if the relationship models a Realization. Realization is a specialized 
     * abstraction relationship between two sets of model elements, one representing a 
     * specification (the supplier) and the other represents an implementation of the 
     * latter (the client). Realization can be used to model stepwise refinement, 
     * optimizations, transformations, templates, model synthesis, framework composition, etc. 
     * Realization is a specialized abstraction relationship between two sets of model elements. 
     * One specifies the source (the supplier); the other implements the targer (the client). 
     * Realization can be used to model stepwise refinement, optimizations, transformations, 
     * templates, model synthesis, framework composition, etc.
     * @param relationship
     * @return
     */
    boolean isRealization(Object relationship);
    
    /**
     * Return true if the relationship models a Substitution. A substitution is a 
     * relationship between two classifiers signifies that the substitutingClassifier 
     * complies with the contract specified by the contract classifier. This implies 
     * that instances of the substitutingClassifier are runtime substitutable where 
     * instances of the contract classifier are expected.
     * @param relationship
     * @return
     */
    boolean isSubstitution(Object relationship);
    
    /**
     * Return the Sterotype string for the given eObject
     * @param eObject
     * @return the Sterotype string
     */
    String getStereotype(Object eObject);
    
    /**
     * Return the Signature string for the given eObject
     * @param eObject
     * @param showMask the mask for which attributes constitue the signature
     * @return the Signature string using the mask
     */
    String getSignature(Object eObject, int showMask);
    
    /**
     * Return the editable portion of the signature string for the given eObject
     * @param eObject
     * @return the editable portion of the signature string 
     */
    String getEditableSignature(Object eObject);
    
    /**
     * Set the Signature string for the given eObject
     * @param eObject
     * @param newSignature
     * @return an IStatus object with the results of the set operation
     */
    IStatus setSignature(Object eObject, String newSignature);
    
}
