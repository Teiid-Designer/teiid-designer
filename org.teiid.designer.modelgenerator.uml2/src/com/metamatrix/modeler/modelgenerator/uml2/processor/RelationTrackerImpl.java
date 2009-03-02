/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.UMLPackage;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipEntity;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.util.RelationshipTypeManager;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.modelgenerator.processor.TransientRelationTrackerImpl;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2ModelGeneratorPlugin;

/**
 * RelationTrackerImpl
 */
public class RelationTrackerImpl extends TransientRelationTrackerImpl {

    public static final int UNABLE_TO_ADD_RELATIONSHIP_CODE = 20001;

    private ModelSelector relationModelSelector;
    /**
     * Map keyed by input EObject and values of {@link LinkedList} that contain the list of {@link Relationship} instances in
     * which the supplied key is a participant on the "input" role.
     */
    private Map relationshipsByInput;
    /**
     * Map keyed by output EObject and values of {@link LinkedList} that contain the list of {@link Relationship} instances in
     * which the supplied key is a participant on the "output" role.
     */
    private Map relationshipsByOutput;

    private RelationshipFactory factory;

    private RelationshipType generatedRelationshipType;

    private int numGeneratedFrom;

    private String defaultGeneratedFromNamePrefix;

    /**
     * Construct an instance of RelationTrackerImpl.
     */
    public RelationTrackerImpl( final ModelSelector relationModelSelector,
                                final RelationshipFactory factory ) {
        super();
        ArgCheck.isNotNull(relationModelSelector);
        this.relationModelSelector = relationModelSelector;
        this.relationshipsByInput = new HashMap();
        this.relationshipsByOutput = new HashMap();
        this.factory = factory != null ? factory : RelationshipFactory.eINSTANCE;
        this.defaultGeneratedFromNamePrefix = Uml2ModelGeneratorPlugin.Util.getString("RelationTrackerImpl.Default_GeneratedFrom_relationship_name_prefix"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.RelationTracker#recordGeneratedFrom(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject)
     */
    @Override
    public void recordGeneratedFrom( final EObject input,
                                     final EObject output,
                                     final List problems ) {
        // Construct the relationship ...
        /*
         * if there is a selector, then we go ahead and create the relationship and subsequently add it to
         * that selector.  Otherwise we just do the mappings.
         */
        if (relationModelSelector != null) {
            final Relationship relationship = this.factory.createRelationship();
            relationship.getSources().add(input);
            relationship.getTargets().add(output);
            relationship.setType(this.getGeneratedRelationshipType());
            doSetName(relationship, input, output);

            // Register the relationship in the cache ...
            ++numGeneratedFrom;
            doRegister(relationship, problems);
        }

        super.recordGeneratedFrom(input, output, problems);
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.RelationTracker#recordGeneratedFrom(org.eclipse.emf.ecore.EObject,
     *      java.util.List)
     */
    @Override
    public void recordGeneratedFrom( final EObject input,
                                     final List outputs,
                                     final List problems ) {
        // Construct the relationship ...
        /*
         * if there is a selector, then we go ahead and create the relationship and subsequently add it to
         * that selector.  Otherwise we just do the mappings.
         */
        if (relationModelSelector != null) {
            final Relationship relationship = this.factory.createRelationship();
            relationship.getSources().add(input);
            relationship.getTargets().addAll(outputs);
            relationship.setType(this.getGeneratedRelationshipType());
            doSetName(relationship, input, outputs);

            // Register the relationship in the cache ...
            ++numGeneratedFrom;
            doRegister(relationship, problems);
        }

        super.recordGeneratedFrom(input, outputs, problems);
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.RelationTracker#recordGeneratedFrom(java.util.List, java.util.List)
     */
    @Override
    public void recordGeneratedFrom( final List inputs,
                                     final List outputs,
                                     final List problems ) {
        // Construct the relationship ...
        /*
         * if there is a selector, then we go ahead and create the relationship and subsequently add it to
         * that selector.  Otherwise we just do the mappings.
         */
        if (relationModelSelector != null) {
            final Relationship relationship = this.factory.createRelationship();
            relationship.getSources().addAll(inputs);
            relationship.getTargets().addAll(outputs);
            relationship.setType(this.getGeneratedRelationshipType());
            doSetName(relationship, inputs, outputs);

            // Register the relationship in the cache ...
            ++numGeneratedFrom;
            doRegister(relationship, problems);
        }

        super.recordGeneratedFrom(inputs, outputs, problems);
    }

    public RelationshipType getGeneratedRelationshipType() {
        if (this.generatedRelationshipType == null) {
            this.generatedRelationshipType = doCreateGeneratedRelationshipType();
            if (this.generatedRelationshipType != null) {
                doAdd(this.generatedRelationshipType, null);
            }
        }
        return this.generatedRelationshipType;
    }

    public void setGeneratedRelationshipType( final RelationshipType type ) {
        this.generatedRelationshipType = type;
    }

    protected RelationshipType doCreateGeneratedRelationshipType() {
        final RelationshipType type = this.factory.createRelationshipType();
        final RelationshipRole generatedTargetRole = this.factory.createRelationshipRole();
        final RelationshipRole generatedSourceRole = this.factory.createRelationshipRole();
        generatedSourceRole.setRelationshipType(type);
        generatedTargetRole.setRelationshipType(type);

        // Set the supertype ...
        final RelationshipTypeManager typeManager = RelationshipMetamodelPlugin.getBuiltInRelationshipTypeManager();
        final RelationshipType supertype = typeManager.getBuiltInRelationshipType(RelationshipTypeManager.Names.MANIFESTATION);
        if (supertype != null) {
            type.setSuperType(supertype);
        }

        // Set the names ...
        final String typeName = Uml2ModelGeneratorPlugin.Util.getString("RelationTrackerImpl.GeneratedRelationshipType_Name"); //$NON-NLS-1$
        final String typeLabel = Uml2ModelGeneratorPlugin.Util.getString("RelationTrackerImpl.GeneratedRelationshipType_Label"); //$NON-NLS-1$
        final String typeOppositeLabel = Uml2ModelGeneratorPlugin.Util.getString("RelationTrackerImpl.GeneratedRelationshipType_OppositeLabel"); //$NON-NLS-1$
        final String generatedTargetRoleName = Uml2ModelGeneratorPlugin.Util.getString("RelationTrackerImpl.GeneratedRelationshipTargetRole_Name"); //$NON-NLS-1$
        final String generatedSourceRoleName = Uml2ModelGeneratorPlugin.Util.getString("RelationTrackerImpl.GeneratedRelationshipSourceRole_Name"); //$NON-NLS-1$
        type.setName(typeName);
        type.setLabel(typeLabel);
        type.setOppositeLabel(typeOppositeLabel);
        generatedTargetRole.setName(generatedTargetRoleName);
        generatedSourceRole.setName(generatedSourceRoleName);

        // generatedRole.setLowerBound(1);
        // generatedRole.setUpperBound(-1);
        generatedTargetRole.getIncludeTypes().add(RelationalPackage.eINSTANCE.getRelationalEntity());
        // generatedFromRole.setLowerBound(1);
        // generatedFromRole.setUpperBound(-1);
        generatedSourceRole.getIncludeTypes().add(UMLPackage.eINSTANCE.getElement());

        return type;
    }

    protected void doRegister( final Relationship relationship,
                               final List problems ) {
        // Register by inputs ...
        final Iterator inputIter = relationship.getSources().iterator();
        while (inputIter.hasNext()) {
            final EObject input = (EObject)inputIter.next();
            LinkedList result = (LinkedList)this.relationshipsByInput.get(input);
            if (result == null) {
                result = new LinkedList();
                this.relationshipsByInput.put(input, result);
            }
            result.add(relationship);
        }

        // Register by inputs ...
        final Iterator outputIter = relationship.getSources().iterator();
        while (outputIter.hasNext()) {
            final EObject output = (EObject)outputIter.next();
            LinkedList result = (LinkedList)this.relationshipsByOutput.get(output);
            if (result == null) {
                result = new LinkedList();
                this.relationshipsByOutput.put(output, result);
            }
            result.add(relationship);
        }

        doAdd(relationship, problems);
    }

    protected void doAdd( final RelationshipEntity entity,
                          final List problems ) {
        try {
            relationModelSelector.getRootObjects().add(entity);
        } catch (ModelerCoreException e) {
            if (problems != null) {
                String message = Uml2ModelGeneratorPlugin.Util.getString("RelationTrackerImpl.Unable_to_add_a_Relationship_1"); //$NON-NLS-1$
                IStatus status = new Status(IStatus.WARNING, Uml2ModelGeneratorPlugin.PLUGIN_ID, UNABLE_TO_ADD_RELATIONSHIP_CODE,
                                            message, e);
                problems.add(status);
            }
        }
    }

    /**
     * @param relationship
     * @param input
     * @param output
     */
    protected void doSetName( Relationship relationship,
                              EObject input,
                              EObject output ) {
        relationship.setName(this.defaultGeneratedFromNamePrefix + this.numGeneratedFrom);
    }

    /**
     * @param relationship
     * @param input
     * @param outputs
     */
    protected void doSetName( Relationship relationship,
                              EObject input,
                              List outputs ) {
        relationship.setName(this.defaultGeneratedFromNamePrefix + this.numGeneratedFrom);
    }

    /**
     * @param relationship
     * @param inputs
     * @param outputs
     */
    protected void doSetName( Relationship relationship,
                              List inputs,
                              List outputs ) {
        relationship.setName(this.defaultGeneratedFromNamePrefix + this.numGeneratedFrom);
    }

}
