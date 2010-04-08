/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.DifferenceType;
import com.metamatrix.modeler.compare.PropertyDifference;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * Utility class for the {@link ModelerComparePlugin}.
 */
public class CompareUtil {

    /**
     * Prevent instantiation.
     */
    private CompareUtil() {
        super();
    }

    /**
     * Process all of the changes that are specified in the supplied {@link DifferenceReport}, and
     * {@link com.metamatrix.modeler.compare.DifferenceDescriptor#setSkip(boolean) skip} all changes <i>from</i> a default value
     * <i>to</i> a non-default value.
     * 
     * @param report
     */
    public static void skipChangesFromDefault( final DifferenceReport report ) {
        final Mapping rootMapping = report.getMapping();
        if (rootMapping != null) {
            skipChangesFromDefault(rootMapping, true);
        }
    }

    /**
     * Process all of the changes that are specified in the supplied {@link Mapping}, and
     * {@link com.metamatrix.modeler.compare.DifferenceDescriptor#setSkip(boolean) skip} all changes <i>from</i> a default value
     * <i>to</i> a non-default value.
     * 
     * @param differenceNode the node in a difference report; may not be null
     * @param recursive true if the same logic should also be applied to {@link Mapping#getNested() nested} mappings, or false if
     *        only the logic should only be applied to the supplied node.
     * @return the number of {@link PropertyDifference} instances that were changed to skip
     */
    public static int skipChangesFromDefault( final Mapping differenceNode,
                                              final boolean recursive ) {
        int count = 0;

        // Get the helper ...
        final DifferenceDescriptor desc = getDifferenceDescriptor(differenceNode);
        if (desc != null) {
            count += skipChangesFromDefault(desc);
        }

        if (recursive) {
            final Iterator iter = differenceNode.getNested().iterator();
            while (iter.hasNext()) {
                final Mapping nestedNode = (Mapping)iter.next();
                count += skipChangesFromDefault(nestedNode, recursive);
            }
        }
        return count;
    }

    /**
     * Process all of the changes that are specified in the supplied {@link DifferenceDescriptor}, and
     * {@link com.metamatrix.modeler.compare.DifferenceDescriptor#setSkip(boolean) skip} all changes <i>from</i> a default value
     * <i>to</i> a non-default value, or if there is no default value but the new value is null and the old value is not null.
     * 
     * @param differenceDescriptor the descriptor for a node in a difference report; may not be null
     * @return the number of {@link PropertyDifference} instances that were changed to skip
     */
    public static int skipChangesFromDefault( final DifferenceDescriptor differenceDescriptor ) {
        // If the difference is an add or a delete, then skip ...
        final DifferenceType diffType = differenceDescriptor.getType();
        if (diffType.getValue() == DifferenceType.ADDITION) {
            return 0;
        }
        if (diffType.getValue() == DifferenceType.DELETION) {
            return 0;
        }

        // Go through the changed features ...
        int count = 0;
        final List propertyDifferences = differenceDescriptor.getPropertyDifferences();
        final Iterator iter = propertyDifferences.iterator();
        while (iter.hasNext()) {
            final PropertyDifference propDiff = (PropertyDifference)iter.next();
            // Only need to process property diffs that are not already skipped ...
            if (!propDiff.isSkip()) {
                final EStructuralFeature feature = propDiff.getAffectedFeature();

                // If there is a default value on this feature ...
                final Object featureDefaultValue = feature.getDefaultValue();
                if (featureDefaultValue != null) {
                    // Then look at the new value to see if the value is going to be the default ...
                    final Object newValue = propDiff.getNewValue();
                    if (featureDefaultValue.equals(newValue)) {
                        // The new value is the default, so mark this as skipped ...
                        propDiff.setSkip(true);
                        ++count;
                    }
                } else {
                    // There is no default value, so skip if the new value is null but the old value is not
                    if (propDiff.getNewValue() == null && propDiff.getOldValue() != null) {
                        propDiff.setSkip(true);
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Obtain the {@link DifferenceDescriptor} at the supplied node, if there is such a descriptor.
     * 
     * @param mappingNode the mapping node; may not be null
     * @return the descriptor, or null if there is no descriptor at the supplied node.
     */
    public static DifferenceDescriptor getDifferenceDescriptor( final Mapping mappingNode ) {
        final MappingHelper helper = mappingNode.getHelper();
        if (helper instanceof DifferenceDescriptor) {
            return (DifferenceDescriptor)helper;
        }
        return null;
    }

    /**
     * Determine whether the supplied mapping node has any {@link Mapping#getInputs() inputs} or {@link Mapping#getOutputs()
     * outputs} that are instances of the supplied Class.
     * 
     * @param mappingNode the mapping node.
     * @param c the class; may not be null
     * @return true if there is at least one input or output that is an instance of <code>c</code>
     */
    public static boolean hasInstanceof( final Mapping mappingNode,
                                         final Class c ) {
        // Check the inputs ...
        final Iterator inputIter = mappingNode.getInputs().iterator();
        while (inputIter.hasNext()) {
            final Object obj = inputIter.next();
            if (c.isInstance(obj)) {
                return true;
            }
        }

        // Check the outputs ...
        final Iterator outputIter = mappingNode.getOutputs().iterator();
        while (outputIter.hasNext()) {
            final Object obj = outputIter.next();
            if (c.isInstance(obj)) {
                return true;
            }
        }

        // None found ...
        return false;
    }

    /**
     * Process the difference report and skip all changes, additions and deletions that involve features that cannot be changed
     * (see {@link EStructuralFeature#isChangeable()}).
     * 
     * @param differences
     */
    public static int skipUnchangeableFeatures( final DifferenceReport differences ) {
        // Iterate through the mappings of root-level objects
        final Mapping rootMapping = differences.getMapping();
        int count = 0;

        // Skip the root mapping, and go right to the nested of the root ...
        final List nestedMappings = rootMapping.getNested();
        final Iterator iter = nestedMappings.iterator();
        while (iter.hasNext()) {
            final Mapping nestedMapping = (Mapping)iter.next();
            count += skipUnchangeableFeatures(nestedMapping, true);
        }
        return count;
    }

    /**
     * Process the difference report and skip all changes, additions and deletions that involve features that cannot be changed
     * (see {@link EStructuralFeature#isChangeable()}). Non-changeable features are sometimes not useful, especially in cases when
     * some features are derived from other features.
     * 
     * @param differenceNode the node in a difference report; may not be null
     * @param recursive true if the same logic should also be applied to {@link Mapping#getNested() nested} mappings, or false if
     *        only the logic should only be applied to the supplied node.
     * @return the number of differences (changed properties, additions, deletions) that were changed to skip
     */
    public static int skipUnchangeableFeatures( final Mapping differenceNode,
                                                final boolean recursive ) {
        int count = 0;

        // Get the helper ...
        final DifferenceDescriptor desc = getDifferenceDescriptor(differenceNode);
        if (desc != null && !desc.isSkip()) { // don't need to check if already skipped
            final int type = desc.getType().getValue();
            switch (type) {
                case DifferenceType.ADDITION:
                case DifferenceType.DELETION:
                    // Determine if the added/deleted object is contained by a non-changeable feature ...
                    final List inputs = differenceNode.getInputs();
                    final List outputs = differenceNode.getOutputs();
                    final EObject input = inputs.isEmpty() ? null : (EObject)inputs.get(0);
                    final EObject output = outputs.isEmpty() ? null : (EObject)outputs.get(0);
                    final EObject obj = input != null ? input : output;
                    final EObject parent = obj.eContainer();
                    if (parent != null) {
                        // Iterate through the features ...
                        final EClass parentEClass = parent.eClass();
                        final Iterator iter = parentEClass.getEAllContainments().iterator();
                        while (iter.hasNext()) {
                            final EReference ref = (EReference)iter.next();
                            if (!ref.isChangeable()) {
                                if (ref.isMany()) {
                                    final List values = (List)parent.eGet(ref);
                                    if (values.contains(obj)) {
                                        desc.setSkip(true);
                                        ++count;
                                        continue; // obj may be in values for multiple EReferences
                                    }
                                } else {
                                    final EObject value = (EObject)parent.eGet(ref);
                                    if (obj.equals(value)) {
                                        desc.setSkip(true);
                                        ++count;
                                        continue; // obj may be in values for multiple EReferences
                                    }
                                }
                            }
                        }
                    }
                    break;
                case DifferenceType.CHANGE:
                    // Go through the affected properties ...
                    final Iterator iter = desc.getPropertyDifferences().iterator();
                    while (iter.hasNext()) {
                        final PropertyDifference propDiff = (PropertyDifference)iter.next();
                        if (!propDiff.isSkip()) {
                            final EStructuralFeature feature = propDiff.getAffectedFeature();
                            final boolean changeable = feature.isChangeable();
                            // Change to skip anything that is not changeable or a reference that is containment
                            if (!changeable || (feature instanceof EReference && ((EReference)feature).isContainment())) {
                                propDiff.setSkip(true);
                                ++count;
                            }
                        }
                    }
                    break;
            }
        }

        // Recurse ...
        if (recursive) {
            final Iterator iter = differenceNode.getNested().iterator();
            while (iter.hasNext()) {
                final Mapping nestedNode = (Mapping)iter.next();
                count += skipUnchangeableFeatures(nestedNode, recursive);
            }
        }
        return count;
    }

    public static void skipDeletesOfStandardContainers( final DifferenceReport differences ) {
        // Iterate through the mappings of root-level objects
        final Mapping rootMapping = differences.getMapping();
        final List nestedMappings = rootMapping.getNested();
        final Iterator iter = nestedMappings.iterator();
        while (iter.hasNext()) {
            final Mapping nestedMapping = (Mapping)iter.next();
            if (CompareUtil.hasInstanceof(nestedMapping, ModelAnnotation.class)) {
                // Don't delete an existing ModelAnnotation ...
                final DifferenceDescriptor desc = CompareUtil.getDifferenceDescriptor(nestedMapping);
                if (desc != null && desc.isDeletion()) {
                    desc.setSkip(true);
                }

                // Skip any change that would change a non-default value to a default (or null) value
                CompareUtil.skipChangesFromDefault(nestedMapping, true);
            } else if (CompareUtil.hasInstanceof(nestedMapping, AnnotationContainer.class)) {
                // Don't delete an existing AnnotationContainer ...
                final DifferenceDescriptor desc = CompareUtil.getDifferenceDescriptor(nestedMapping);
                if (desc != null && desc.isDeletion()) {
                    desc.setSkip(true);
                }
            } else if (CompareUtil.hasInstanceof(nestedMapping, TransformationContainer.class)) {
                // Always skip anything to do with a transformaiton container ...
                final DifferenceDescriptor desc = CompareUtil.getDifferenceDescriptor(nestedMapping);
                desc.setSkip(true);
            } else if (CompareUtil.hasInstanceof(nestedMapping, DiagramContainer.class)) {
                // Always skip anything to do with a diagram container ...
                final DifferenceDescriptor desc = CompareUtil.getDifferenceDescriptor(nestedMapping);
                desc.setSkip(true);
            }
        }
    }

    public static void skipDeletesOfModelImports( final DifferenceReport differences ) {
        // Iterate through the mappings of root-level objects
        final Mapping rootMapping = differences.getMapping();
        final List nestedMappings = rootMapping.getNested();
        final Iterator iter = nestedMappings.iterator();
        while (iter.hasNext()) {
            final Mapping nestedMapping = (Mapping)iter.next();
            if (CompareUtil.hasInstanceof(nestedMapping, ModelAnnotation.class)) {
                // Get the nested mappings ...
                final Iterator nestedIter = nestedMapping.getNested().iterator();
                while (nestedIter.hasNext()) {
                    final Mapping nestedNested = (Mapping)nestedIter.next();
                    if (CompareUtil.hasInstanceof(nestedNested, ModelImport.class)) {
                        // Don't delete an existing ModelImport ...
                        final DifferenceDescriptor desc = CompareUtil.getDifferenceDescriptor(nestedMapping);
                        if (desc != null && desc.isDeletion()) {
                            desc.setSkip(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Utility method to print a difference report in a semi-readable fashion.
     * 
     * @param report
     * @param stream
     */
    public static void print( final DifferenceReport report,
                              final PrintStream stream ) {
        print(report, stream, false);
    }

    /**
     * Utility method to print a difference report in a semi-readable fashion.
     * 
     * @param report
     * @param stream
     */
    public static void print( final DifferenceReport report,
                              final PrintStream stream,
                              final boolean showSkips ) {
        CoreArgCheck.isNotNull(report);
        CoreArgCheck.isNotNull(stream);
        stream.println(""); //$NON-NLS-1$
        if (report.getTitle() != null && report.getTitle().trim().length() != 0) {
            stream.println("DifferenceReport: " + report.getTitle()); //$NON-NLS-1$
        } else {
            stream.println("DifferenceReport"); //$NON-NLS-1$
        }
        printMapping(report.getMapping(), "   ", stream, showSkips); //$NON-NLS-1$
    }

    protected static void printMapping( final Mapping mapping,
                                        final String prefix,
                                        final PrintStream stream,
                                        final boolean showSkips ) {
        String msg = prefix;
        List submsgs = null;
        final List inputs = mapping.getInputs();
        final List outputs = mapping.getOutputs();
        if (mapping.getNestedIn() == null /*inputs.size() > 1 || outputs.size() > 1*/) {
            msg = msg + " Difference Report Root Mapping"; //$NON-NLS-1$
        } else {
            if (inputs.size() == 1 || outputs.size() == 1) {
                final EObject output = outputs.isEmpty() ? null : (EObject)outputs.get(0);
                final EObject input = inputs.isEmpty() ? null : (EObject)inputs.get(0);
                if (output != null) {
                    msg = msg + ModelerCore.getModelEditor().getModelRelativePath(output);
                } else if (input != null) {
                    msg = msg + ModelerCore.getModelEditor().getModelRelativePath(input);
                }
                final MappingHelper helper = mapping.getHelper();
                if (helper != null && helper instanceof DifferenceDescriptor) {
                    final DifferenceDescriptor desc = (DifferenceDescriptor)helper;
                    if (showSkips || !desc.isSkip()) {
                        final DifferenceType type = desc.getType();
                        if (type.getValue() == DifferenceType.ADDITION) {
                            msg = msg + " (Added)"; //$NON-NLS-1$
                        } else if (type.getValue() == DifferenceType.DELETION) {
                            msg = msg + " (Deleted)"; //$NON-NLS-1$
                        } else if (type.getValue() == DifferenceType.CHANGE) {
                            msg = msg + " (Changed)"; //$NON-NLS-1$
                            submsgs = new ArrayList(desc.getPropertyDifferences().size());
                            final Iterator iter = desc.getPropertyDifferences().iterator();
                            while (iter.hasNext()) {
                                final PropertyDifference propDiff = (PropertyDifference)iter.next();
                                if (showSkips || !propDiff.isSkip()) {
                                    String featureName = propDiff.getAffectedFeature().getName();
                                    if (propDiff.isSkip()) {
                                        featureName = featureName + " [skip]"; //$NON-NLS-1$
                                    }
                                    submsgs.add(featureName + " changed from " + propDiff.getOldValue()); //$NON-NLS-1$
                                    final String slot = getString(' ', featureName.length());
                                    submsgs.add(slot + "           to " + propDiff.getNewValue()); //$NON-NLS-1$
                                }
                            }
                        }
                        if (desc.isSkip()) {
                            msg = msg + " [skip]"; //$NON-NLS-1$
                        }
                    }
                }
            }
        }
        stream.println(msg);
        if (submsgs != null) {
            final Iterator iter = submsgs.iterator();
            while (iter.hasNext()) {
                final String submsg = (String)iter.next();
                stream.println(prefix + "    (" + submsg + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        // stream.println(prefix + mapping.toString());
        final Iterator iter = mapping.getNested().iterator();
        while (iter.hasNext()) {
            final Mapping nested = (Mapping)iter.next();
            printMapping(nested, "  " + prefix, stream, showSkips); //$NON-NLS-1$
        }
    }

    protected static String getString( char c,
                                       int length ) {
        final StringBuffer sb = new StringBuffer(length);
        for (int i = 0; i < length; ++i) {
            sb.append(c);
        }
        return sb.toString();
    }

}
