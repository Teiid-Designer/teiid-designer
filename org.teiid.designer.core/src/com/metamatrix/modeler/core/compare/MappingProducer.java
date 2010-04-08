/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;

/**
 * MappingProducer
 */
public class MappingProducer {

    private final EObjectMatcherCache matchers;
    private final LinkedList unmappedObjects;
    private final Map inputsToOutputs;

    public MappingProducer() {
        this(new HashMap());
    }

    /**
     * Construct an instance of MappingProducer.
     */
    public MappingProducer( final HashMap inputsToOutputs ) {
        super();
        this.matchers = new EObjectMatcherCache();
        this.unmappedObjects = new LinkedList();
        this.inputsToOutputs = inputsToOutputs;
    }

    public EObjectMatcherCache getEObjectMatcherCache() {
        return this.matchers;
    }

    public Map getInputsToOutputs() {
        return this.inputsToOutputs;
    }

    /**
     * This method creates the mappings between the list of inputs and outputs.
     * <p>
     * Note that this method will call the {@link MappingFactory#createMappingRoot()} method to create the outer mapping object
     * that is returned by this method. The {@link #createMappings(List, List, boolean, Mapping, MappingFactory)} method allows
     * the caller to pass in the outer mapping object to which all the mappings will be added.
     * </p>
     * 
     * @param inputs the list of {@link org.eclipse.emf.ecore.EObject} instances that are to be the inputs of the computed
     *        mappings; may not be null but may be empty
     * @param outputs the list of {@link org.eclipse.emf.ecore.EObject} instances that are to be the outputs of the computed
     *        mappings; may not be null but may be empty
     * @param recursive true if this method should map not only the supplied inputs and outputs but it should also map their
     *        children recursively; or false otherwise.
     * @param factory the {@link MappingFactory} that should be used to create the nested {@link Mapping} objects.
     * @param monitor the progress monitor; may not be null (use {@link org.eclipse.core.runtime.NullProgressMonitor} if needed)
     * @return the nested mapping structure containing the mappings between the inputs and outputs; never null
     * @see #createMappings(List, List, boolean, Mapping, MappingFactory)
     */
    public Mapping createMappings( final List inputs,
                                   final List outputs,
                                   final boolean recursive,
                                   final MappingFactory factory,
                                   final IProgressMonitor monitor ) {
        CoreArgCheck.isNotNull(inputs);
        CoreArgCheck.isNotNull(outputs);
        CoreArgCheck.isNotNull(factory);

        // -------------------------------------------------------------
        // Perform phase 0 - initialization
        // -------------------------------------------------------------
        // Clear the mapping state ...
        this.unmappedObjects.clear();
        // this.inputsToOutputs.clear();

        // -------------------------------------------------------------
        // Perform phase 1 - Match without inputs-to-outputs
        // -------------------------------------------------------------
        // Create the outer Mapping object ...
        final Mapping mappingRoot = factory.createMapping();
        final List inputCopies = new LinkedList(inputs);
        final List outputCopies = new LinkedList(outputs);

        // Copy all of the inputs/outputs into the mapping root inputs/outputs
        mappingRoot.getInputs().addAll(inputs);
        mappingRoot.getOutputs().addAll(outputs);

        // Add the mappings for the roots-level objects ...
        final List matchers = this.matchers.getEObjectMatchersForRoots();
        final Iterator iter = matchers.iterator();
        while (iter.hasNext()) {
            final EObjectMatcher matcher = (EObjectMatcher)iter.next();
            matcher.addMappingsForRoots(inputCopies, outputCopies, mappingRoot, factory);
            if (inputCopies.isEmpty() || outputCopies.isEmpty()) {
                break;
            }
        }

        // If there are any left ...
        enqueueUnmappedMappings(null, inputCopies, outputCopies, mappingRoot, factory);

        // Go through all of the mappings that were generated ...
        doProcessNestedMappings(factory, mappingRoot);

        // -------------------------------------------------------------
        // Perform phase 2 - Match remaining using inputs-to-outputs
        // -------------------------------------------------------------
        // First, create the map of inputs to outputs ...
        initializeInputToOutputMapping(mappingRoot, inputsToOutputs);

        // This processes the queue and creates additional mappings ...
        while (this.unmappedObjects.size() != 0) {
            final UnmappedObjects uo = (UnmappedObjects)this.unmappedObjects.removeFirst();
            final Mapping parentMapping = uo.parentMapping;
            // Make a copy of the existing nested mappings ...
            final List existingNested = new ArrayList(parentMapping.getNested());

            final EReference ref = uo.reference;
            if (ref == null) {
                // root-level objects ...
                final List theMatchers = this.matchers.getEObjectMatchersForRoots();
                final Iterator matcherIter = theMatchers.iterator();
                while (matcherIter.hasNext()) {
                    final EObjectMatcher matcher = (EObjectMatcher)matcherIter.next();
                    if (matcher instanceof TwoPhaseEObjectMatcher) {
                        final TwoPhaseEObjectMatcher tpMatcher = (TwoPhaseEObjectMatcher)matcher;
                        tpMatcher.addMappingsForRoots(uo.unmappedInputs,
                                                      uo.unmappedOutputs,
                                                      this.inputsToOutputs,
                                                      parentMapping,
                                                      factory);
                        if (uo.unmappedInputs.isEmpty() || uo.unmappedOutputs.isEmpty()) {
                            break;
                        }
                    }
                }

                // If there are remaining inputs and outputs, put them
            } else {
                // It is not a root-level object ...
                final List theMatchers = this.matchers.getEObjectMatchers(ref);
                final Iterator matcherIter = theMatchers.iterator();
                while (matcherIter.hasNext()) {
                    final EObjectMatcher matcher = (EObjectMatcher)matcherIter.next();
                    if (matcher instanceof TwoPhaseEObjectMatcher) {
                        final TwoPhaseEObjectMatcher tpMatcher = (TwoPhaseEObjectMatcher)matcher;
                        tpMatcher.addMappings(ref,
                                              uo.unmappedInputs,
                                              uo.unmappedOutputs,
                                              this.inputsToOutputs,
                                              parentMapping,
                                              factory);
                        if (uo.unmappedInputs.isEmpty() || uo.unmappedOutputs.isEmpty()) {
                            break;
                        }
                    }
                }
            }

            // Find the nested mappings that were added by this second phase, and then
            // process them. Do this by removing the ones that existed above
            final List nested = parentMapping.getNested();
            if (nested.size() != existingNested.size()) {
                // Must have added at least one ...
                final List newNestedMappings = new ArrayList(parentMapping.getNested());
                newNestedMappings.removeAll(existingNested);
                final Iterator newNestedIter = newNestedMappings.iterator();
                while (newNestedIter.hasNext()) {
                    final Mapping newNested = (Mapping)newNestedIter.next();
                    // Go through all of the mappings under new mappings ...
                    doProcessMapping(factory, newNested);
                }
            }

            // Take anything that is left, and create the adds/deletes
            // (this may enqueue more unmatched)
            addUnmappedMappings(uo.unmappedInputs, uo.unmappedOutputs, parentMapping, factory);
        }
        return mappingRoot;
    }

    protected void doProcessNestedMappings( final MappingFactory factory,
                                            final Mapping mappingRoot ) {
        // Go through all of the mappings that were generated ...
        final Iterator nestedIter = mappingRoot.getNested().iterator();
        while (nestedIter.hasNext()) {
            final Mapping nestedMapping = (Mapping)nestedIter.next();
            doProcessMapping(factory, nestedMapping);
        }
    }

    protected void doProcessMapping( final MappingFactory factory,
                                     final Mapping mapping ) {
        final List nestedInputs = mapping.getInputs();
        final List nestedOutputs = mapping.getOutputs();
        if (nestedInputs.size() == 1 && nestedOutputs.size() == 1) {
            // The nested mapping has exactly 1 input and 1 output
            final EObject inputObj = (EObject)nestedInputs.get(0);
            final EObject outputObj = (EObject)nestedOutputs.get(0);
            final EClass inputEClass = inputObj.eClass();
            final EClass outputEClass = outputObj.eClass();
            // Only compare if the metaclasses are the same
            if (inputEClass.equals(outputEClass)) {
                // Iterate over all of the containment references ...
                final List refs = inputEClass.getEAllContainments();
                final Iterator refIter = refs.iterator();
                while (refIter.hasNext()) {
                    final EReference ref = (EReference)refIter.next();
                    // Get the values for this ref from the input and output object ...
                    final List inputValues = new LinkedList();
                    final List outputValues = new LinkedList();
                    if (ref.isMany()) {
                        inputValues.addAll((List)inputObj.eGet(ref));
                        outputValues.addAll((List)outputObj.eGet(ref));
                    } else {
                        final Object inputValue = inputObj.eGet(ref);
                        final Object outputValue = outputObj.eGet(ref);
                        if (inputValue != null) {
                            inputValues.add(inputValue);
                        }
                        if (outputValue != null) {
                            outputValues.add(outputValue);
                        }
                    }
                    if (inputValues.size() != 0 && outputValues.size() != 0) {
                        // There are both input values and output values, so run through the matchers ...
                        final List refMatchers = this.matchers.getEObjectMatchers(ref);
                        final Iterator refMatcherIter = refMatchers.iterator();
                        while (refMatcherIter.hasNext()) {
                            final EObjectMatcher refMatcher = (EObjectMatcher)refMatcherIter.next();
                            refMatcher.addMappings(ref, inputValues, outputValues, mapping, factory);
                            if (inputValues.isEmpty() || outputValues.isEmpty()) {
                                break;
                            }
                        }
                    }

                    // If there are any left ...
                    if (inputValues.size() != 0 || outputValues.size() != 0) {
                        enqueueUnmappedMappings(ref, inputValues, outputValues, mapping, factory);
                    }
                }
            }
        }
        // Go through all of the mappings that were generated ...
        doProcessNestedMappings(factory, mapping);
    }

    protected void enqueueUnmappedMappings( final EReference reference,
                                            final List inputs,
                                            final List outputs,
                                            final Mapping mapping,
                                            final MappingFactory factory ) {
        // If there is anything unmapped (on each side), put it into the queue ...
        if (inputs.size() != 0 || outputs.size() != 0) {
            final UnmappedObjects uo = new UnmappedObjects(reference, inputs, outputs, mapping);
            this.unmappedObjects.add(uo);
        }
    }

    protected void addUnmappedMappings( final List inputs,
                                        final List outputs,
                                        final Mapping mapping,
                                        final MappingFactory factory ) {

        // Any remaining inputs and outputs are extras, so create single-mappings for them ...
        final Iterator inputIter = inputs.iterator();
        while (inputIter.hasNext()) {
            final EObject input = (EObject)inputIter.next();
            final Mapping deletionMapping = factory.createMapping();
            deletionMapping.getInputs().add(input);
            mapping.getNested().add(deletionMapping);
        }

        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final EObject output = (EObject)outputIter.next();
            final Mapping additionMapping = factory.createMapping();
            additionMapping.getOutputs().add(output);
            mapping.getNested().add(additionMapping);
        }

    }

    protected void initializeInputToOutputMapping( final Mapping mapping,
                                                   final Map inputsToOutputsMap ) {
        // Walk through the mappings and build up the object equivalence maps ...
        final ModelVisitor visitor = new ModelVisitor() {
            public boolean visit( final EObject object ) {
                if (object instanceof Mapping) {
                    final Mapping mapping = (Mapping)object;
                    final List inputs = mapping.getInputs();
                    final List outputs = mapping.getOutputs();
                    if (inputs.size() == 1 && outputs.size() == 1) {
                        final EObject input = (EObject)inputs.get(0);
                        final EObject output = (EObject)outputs.get(0);
                        inputsToOutputsMap.put(input, output);
                    }
                    return true;
                }
                return false;
            }

            public boolean visit( Resource resource ) {
                return true;
            }
        };
        final ModelVisitorProcessor visitorProcessor = new ModelVisitorProcessor(visitor);
        try {
            visitorProcessor.walk(mapping, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    protected class UnmappedObjects {
        protected final List unmappedInputs;
        protected final List unmappedOutputs;
        protected final Mapping parentMapping;
        protected final EReference reference;

        public UnmappedObjects( final EReference reference,
                                final List unmappedInputs,
                                final List unmappedOutputs,
                                final Mapping parentMapping ) {
            this.unmappedInputs = unmappedInputs;
            this.unmappedOutputs = unmappedOutputs;
            this.parentMapping = parentMapping;
            this.reference = reference;
        }
    }
}
