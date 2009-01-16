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

package com.metamatrix.modeler.modelgenerator.uml2.processor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;

import com.metamatrix.core.selection.TreeSelection;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.modelgenerator.processor.RelationTracker;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2ModelGeneratorPlugin;
import com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectGenerator;
import com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectGeneratorImpl;
import com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategy;
import com.metamatrix.modeler.modelgenerator.uml2.util.RelationalObjectNamingStrategyImpl;
import com.metamatrix.modeler.modelgenerator.uml2.util.ValidatingRelationalObjectNameStrategy;
import com.metamatrix.modeler.modelgenerator.util.AnnotationHelper;
import com.metamatrix.modeler.modelgenerator.util.AnnotationHelperImpl;
import com.metamatrix.modeler.modelgenerator.util.EObjectUtil;
import com.metamatrix.modeler.modelgenerator.util.EObjectUtilImpl;
import com.metamatrix.modeler.modelgenerator.util.SimpleDatatypeUtil;
import com.metamatrix.modeler.modelgenerator.util.SimpleDatatypeUtilImpl;

/**
 * RelationalFragmentGeneratorImpl
 */
public class RelationalFragmentGeneratorImpl implements RelationalFragmentGenerator {

    public static final int UNABLE_TO_RETRIEVE_ROOT_OBJECTS_FROM_SELECTOR = 800001;
    public static final int UNABLE_TO_ADD_ROOT_OBJECTS_TO_SELECTOR = 800002;

    private static final int UNIT_OF_WORK_GRANULARITY = 1;

    private static final String TASK_NAME = "Generating Objects"; //$NON-NLS-1$

    /**
     * Construct an instance of RelationalFragmentGeneratorImpl.
     * 
     */
    public RelationalFragmentGeneratorImpl() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2relational.RelationalFragmentGenerator#createModelFragments(com.metamatrix.modeler.modelgenerator.uml2relational.Uml2RelationalProcessor, java.util.List, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void createModelFragments( final Uml2RelationalProcessor processor, final List problems,
                                      final IProgressMonitor monitor) {

        // Create the naming strategy ...
        final Uml2RelationalOptions options = processor.getOptions();
        RelationalObjectNamingStrategy mainNamingStrategy = new RelationalObjectNamingStrategyImpl(options);
        // (wrap with a strategy that makes all names valid)
        RelationalObjectNamingStrategy namingStrategy = new ValidatingRelationalObjectNameStrategy(mainNamingStrategy);

        SimpleDatatypeUtil datatypeUtil = new SimpleDatatypeUtilImpl();
        AnnotationHelper annotationHelper =
            new AnnotationHelperImpl(processor.getRelationalOutputModelSelector());

        List selectors = processor.getInputModelSelectors();

        List rootEObjects = null;
        try {
            rootEObjects = getAllRootsFromSelectors(selectors);
        } catch (ModelerCoreException e) {
            IStatus status =
                new Status(
                    IStatus.ERROR,
                    Uml2ModelGeneratorPlugin.PLUGIN_ID,
                    UNABLE_TO_RETRIEVE_ROOT_OBJECTS_FROM_SELECTOR,
                    e.getMessage(),
                    e);
            problems.add(status);

        }

        monitor.beginTask(TASK_NAME, rootEObjects.size());

        try {
            processor.getRelationalOutputModelSelector().addRootObjects(
                processRootObjects(
                    rootEObjects,
                    monitor,
                    problems,
                    processor,
                    namingStrategy,
                    datatypeUtil,
                    annotationHelper));
        } catch (ModelerCoreException e1) {
            String message = Uml2ModelGeneratorPlugin.Util.getString("RelationalFragmentGeneratorImpl.Unable_to_add_the_Root_objects"); //$NON-NLS-1$
            IStatus status =
                new Status(
                    IStatus.ERROR,
                    Uml2ModelGeneratorPlugin.PLUGIN_ID,
                    UNABLE_TO_ADD_ROOT_OBJECTS_TO_SELECTOR,
                    message,
                    e1);
            problems.add(status);
        }

    }

    /**
     * This method is used to get the root EObjects from all fo the ModelSelectors that were passed in.
     * 
     * @param modelSelectors the ModelSelector instances to get the root EObjects from
     * @return A List of EObjects that represent all of the roots from all of the passed in ModelSelectors.
     * @throws ModelerCoreException if there is a problem getting the root objects from one of the input
     * ModelSelectors.
     */
    private List getAllRootsFromSelectors(List modelSelectors) throws ModelerCoreException {
        final List allRoots = new LinkedList();
        final Iterator iter = modelSelectors.iterator();

        while (iter.hasNext()) {
            final ModelSelector input = (ModelSelector)iter.next();
            allRoots.addAll(input.getRootObjects());

        }
        return allRoots;
    }

    /**
     * This method is used to process a List of root UML EObjects into a List of root Relational EObjects.  This
     * method will never return null.
     * 
     * @param objects the root UML Eobjects to be processed.
     * @param monitor the ProgressMonitor interface to be used to tell the client about the progress of the
     * processing job.
     * @param problems the List of IStatus instances that describe problems in the model generation process.
     * @param processor the ModelProcessor which has getters for all of the 'ancillary' classes necessary to
     * execute this method.
     * @param strategy the strategy instance to be used to determine whether or not a UML Property represents
     * a column in the Primary Key of a generated BaseTable.
     * @return The List of Relational root objects that were generated. Never null. Can be empty.
     */
    private List processRootObjects(
        final List objects,
        final IProgressMonitor monitor,
        final List problems,
        final Uml2RelationalProcessor processor,
        final RelationalObjectNamingStrategy namingStrategy,
        final SimpleDatatypeUtil datatypeUtil,
        AnnotationHelper annotationHelper) {

        final List outputFragments = new LinkedList();
        final Set associationsToBeProcessed = new HashSet();
        final EObjectUtil eObjectUtil = new EObjectUtilImpl();
        TreeSelection selection = processor.getUml2InputSelections();

        RelationalObjectGenerator generator =
            new RelationalObjectGeneratorImpl(
                processor.getRelationTracker(),
                processor.getDatatypeFinder(),
                processor.getOptions(),
                namingStrategy,
                eObjectUtil,
                RelationalFactory.eINSTANCE,
                selection,
                datatypeUtil,
                annotationHelper);

        final Iterator iter = objects.iterator();
        while (iter.hasNext()) {

            final EObject object = (EObject)iter.next();
            List objectOutputFragments =
                processRootEObject(object, problems, generator, processor, associationsToBeProcessed);
            monitor.worked(UNIT_OF_WORK_GRANULARITY);
            outputFragments.addAll(objectOutputFragments);
        }

        final Iterator iterator = associationsToBeProcessed.iterator();
        while (iterator.hasNext()) {
            final Association association = (Association)iterator.next();
            outputFragments.addAll(generator.createBaseTablesForAssociation(association, problems));
        }

        return outputFragments;

    }

    /**
     * This method is used to process a root UML EObject into a List of root Relational EObjects that represent
     * the input UML EObjects.  This method will never return null.
     * 
     * @param rootEObject the root UML Eobject to be processed.
     * @param problems the List of IStatus instances that describe problems in the model generation process.
     * @param processor the ModelProcessor which has getters for all of the 'ancillary' classes necessary to
     * execute this method.
     * @param strategy the strategy instance to be used to determine whether or not a UML Property represents
     * a column in the Primary Key of a generated BaseTable.
     * @return The List of Relational root objects that were generated. Never null. Can be empty.
     */
    private List processRootEObject(
        final EObject rootEObject,
        final List problems,
        final RelationalObjectGenerator generator,
        final Uml2RelationalProcessor processor,
        final Set associationsToBeProcessed) {

        if (rootEObject == null) {
            return null;
        }

        List outputRootEObjects = new LinkedList();

        outputRootEObjects.addAll(
            processEObject(
                rootEObject,
                problems,
                outputRootEObjects,
                generator,
                processor,
                associationsToBeProcessed));

        Iterator iterator = rootEObject.eAllContents();
        while (iterator.hasNext()) {
            EObject object = (EObject)iterator.next();
            outputRootEObjects.addAll(
                processEObject(
                    object,
                    problems,
                    outputRootEObjects,
                    generator,
                    processor,
                    associationsToBeProcessed));
        }
        return outputRootEObjects;

    }

    /**
     * This method will process a single UML EObject and will output a List of Relational EObjects that 
     * represent the input UML EObject.  This method will never return null, however the List returned
     * may be empty.
     * 
     * @param object the input UML EObject to be processed.
     * @param problems the List of problems that have occurred thus far in processing the intput UML EObjects.
     * @param rootEObjects this is the list of root Relational EObjects that have thus far been generated in the
     * processing of the input UML EObjects.
     * @return A List of Relational root EObjects representing the input UML EObjects.  The list will never
     * be null, but may be empty.
     */
    private List processEObject(
        final EObject object,
        final List problems,
        final List rootEObjects,
        final RelationalObjectGenerator generator,
        final Uml2RelationalProcessor processor,
        final Set associationsToBeProcessed) {

        RelationTracker tracker = processor.getRelationTracker();

        /*
         * if this object has already been processed as a result of there being a relationship between it
         * and another object, then there is no reason to process it again.
         */
        if (tracker.getGeneratedTo(object) != null) {
            return Collections.EMPTY_LIST;
        }
        
        /*
         * Skip Profiles (subtype of Package) and Stereotypes (subtype of Class)
         */
        if (object instanceof Profile) {
            return Collections.EMPTY_LIST;
        }
        if (object instanceof Stereotype) {
            return Collections.EMPTY_LIST;
        }

        if (object instanceof Package) {

            int selectionMode = processor.getUml2InputSelections().getSelectionMode(object);
            List packageList = new LinkedList();
            if (selectionMode == TreeSelection.PARTIALLY_SELECTED
                || selectionMode == TreeSelection.SELECTED) {

                //                    packageList.add(generator.createSchemaFromPackage((Package)object));
            }
            return packageList;
        } else if (object instanceof Class) {

            if (processor.getUml2InputSelections().getSelectionMode(object) == TreeSelection.SELECTED) {

                return generator.createBaseTablesForClass((Class)object, problems, associationsToBeProcessed);
            }

        } else if (object instanceof Association) {
            Association association = (Association)object;
            if (association.getOwnedEnds().size() == 2) {
                associationsToBeProcessed.add(association);
            }
        }
        return Collections.EMPTY_LIST;
    }

}
