/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.processor;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * RelationTracker
 */
public interface RelationTracker {

    /**
     * Record that the supplied input object was used to generate the one supplied output object.
     * The result will be a 1-to-1 "generated" relationship between the supplied input and output.
     * @param input the input object; may not be null
     * @param output the output object; may not be null
     * @param the List of problems to add a problem to if any issues occur while trying to relate the given
     * objects.
     */
    public void recordGeneratedFrom(EObject input, EObject output, List problems);

    /**
     * Record that the supplied input object was used to generate the multiple supplied output objects.
     * The result will be a 1-to-n "generated" relationship (where "n" is <code>outputs.size()</code>)
     * between the supplied input and outputs.
     * <p>
     * The order of the <code>outputs</code> list should also have the "primary" output object
     * (if there is one) as the first object in the list.  This is used by
     * {@link #getGeneratedFrom(EObject)} to return a single input object when only one is
     * desired.
     * </p>
     * @param input the input object; may not be null
     * @param outputs the list of output objects; may not be null, and may not be empty
     * @param the List of problems to add a problem to if any issues occur while trying to relate the given
     * objects.
     */
    public void recordGeneratedFrom(EObject input, List outputs, List problems);

    /**
     * Record that the supplied input objects were all used to generate the multiple supplied output objects.
     * The result will be a m-to-n "generated" relationship (where "m" is <code>inputs.size()</code>,
     * and where "n" is <code>outputs.size()</code>) between the supplied inputs and outputs.
     * <p>
     * The order of the <code>outputs</code> and <code>inputs</code> lists should also have the 
     * "primary" object (if there is one) as the first object in the corresponding list.  This is used by
     * {@link #getGeneratedFrom(EObject)} to return a single input object when only one is
     * desired.
     * </p>
     * @param input the list of input objects; may not be null, and may not be empty
     * @param outputs the list of output objects; may not be null, and may not be empty
     * @param the List of problems to add a problem to if any issues occur while trying to relate the given
     * objects.
     */
    public void recordGeneratedFrom(List umlInputs, List outputs, List problems);

    /**
     * Find the "primary" input object that 
     * @param output
     * @return
     */
    public EObject getGeneratedFrom(final EObject output);

    /**
     * This method can be used to retrieve the EObject that the given input EObject was generated
     * to.  
     * 
     * @param input the EObject to look for representative objects in the output for.
     * @return the EObject in the output that was generated from the given input EObject. Null if none found.
     */
    public EObject getGeneratedTo(final EObject input);

}
