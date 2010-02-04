/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.util.RelationshipTypeManager;
import com.metamatrix.metamodels.relationship.util.RelationshipUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.relationship.RelationshipTypeEditor;

/**
 * RelationshipTypeEditorImpl
 */
public class RelationshipTypeEditorImpl implements RelationshipTypeEditor {

    private final RelationshipType relationshipType;
//    private final boolean useTransactions;
    private final RelationshipFactory factory;

    /**
     * Construct an instance of RelationshipTypeEditorImpl.
     * @param relationshipType the relationship type to be edited; may not be null
     * @param useTransactions true if the move methods should be completed in a single transaction,
     * or false if implicit transactions should be used for all operations
     */
    public RelationshipTypeEditorImpl( final RelationshipType relationshipType, final boolean useTransactions ) {
        this(relationshipType,useTransactions,RelationshipFactory.eINSTANCE);
    }

    /**
     * Construct an instance of RelationshipTypeEditorImpl.
     * @param relationshipType the relationship type to be edited; may not be null
     * @param useTransactions true if the move methods should be completed in a single transaction,
     * or false if implicit transactions should be used for all operations
     */
    public RelationshipTypeEditorImpl( final RelationshipType relationshipType, final boolean useTransactions,
                                       final RelationshipFactory factory ) {
        super();
        ArgCheck.isNotNull(relationshipType);
        ArgCheck.isNotNull(factory);
        this.relationshipType = relationshipType;
//        this.useTransactions = useTransactions;
        this.factory = factory;
        
        // Make sure the type has a super type ...
        ensureSuperTypeIsSet();
    }
    
    public void ensureSuperTypeIsSet() {
        if ( this.relationshipType.getSuperType() == null ) {
            final RelationshipTypeManager mgr = RelationshipMetamodelPlugin.getBuiltInRelationshipTypeManager();
            final RelationshipType anyType = mgr.getAnyRelationshipType();
            this.relationshipType.setSuperType(anyType);
        }
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getRelationshipType()
     */
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#validate()
     */
    public IStatus validate() {
        return this.relationshipType.isValid();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getName()
     */
    public String getName() {
        return this.relationshipType.getName();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setName(java.lang.String)
     */
    public void setName(final String name) {
        this.relationshipType.setName(name);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getLabel()
     */
    public String getLabel() {
        return this.relationshipType.getLabel();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setLabel(java.lang.String)
     */
    public void setLabel(final String label) {
        this.relationshipType.setLabel(label);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getOppositeName()
     */
    public String getOppositeLabel() {
        return this.relationshipType.getOppositeLabel();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setOppositeLabel(java.lang.String)
     */
    public void setOppositeLabel(final String oppositeLabel) {
        this.relationshipType.setOppositeLabel(oppositeLabel);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getStereotype()
     */
    public String getStereotype() {
        return this.relationshipType.getStereotype();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setStereotype(java.lang.String)
     */
    public void setStereotype(final String stereotype) {
        this.relationshipType.setStereotype(stereotype);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getSourceRoleName()
     */
    public String getSourceRoleName() {
        return this.getSourceRole().getName();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getTargetRoleName()
     */
    public String getTargetRoleName() {
        return this.getTargetRole().getName();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getFeatures()
     */
    public List getFeatures() {
        return this.relationshipType.getRelationshipFeatures();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#isDirected()
     */
    public boolean isDirected() {
        return this.relationshipType.isDirected();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setDirected(boolean)
     */
    public void setDirected(final boolean directed) {
        this.relationshipType.setDirected(directed);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#isExclusive()
     */
    public boolean isExclusive() {
        return this.relationshipType.isExclusive();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setExclusive(boolean)
     */
    public void setExclusive(final boolean exclusive) {
        this.relationshipType.setExclusive(exclusive);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#isCrossModel()
     */
    public boolean isCrossModel() {
        return this.relationshipType.isCrossModel();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setCrossModel(boolean)
     */
    public void setCrossModel(final boolean crossModel) {
        this.relationshipType.setCrossModel(crossModel);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#isAbstract()
     */
    public boolean isAbstract() {
        return this.relationshipType.isAbstract();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setAbstract(boolean)
     */
    public void setAbstract(final boolean abstractState) {
        this.relationshipType.setAbstract(abstractState);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getSupertype()
     */
    public RelationshipType getSupertype() {
        return this.relationshipType.getSuperType();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setSupertype(com.metamatrix.metamodels.relationship.RelationshipType)
     */
    public void setSupertype(final RelationshipType supertype) {
        this.relationshipType.setSuperType(supertype);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#canSetSupertype(com.metamatrix.metamodels.relationship.RelationshipType)
     */
    public boolean canSetSupertype(final RelationshipType supertype) {
        return canSetSupertype(this.relationshipType,supertype);
    }

    protected boolean canSetSupertype(final RelationshipType subtype, final RelationshipType supertype) {
        if ( supertype == null ) {
            return true;
        }
        // Walk up supertype's supertype path to see if we are an existing supertype of it ...
        RelationshipType baseType = supertype;
        while ( baseType != null ) {
            if ( baseType.equals(subtype) ) {
                return false;
            }
            baseType = baseType.getSuperType();
        }
        return true;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getSubtypes()
     */
    public List getSubtypes() {
        return this.relationshipType.getSubType();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#canAddSubtype(com.metamatrix.metamodels.relationship.RelationshipType)
     */
    public boolean canAddSubtype(final RelationshipType subtype) {
        if ( ! canSetSupertype(subtype,this.relationshipType) ) {
            return false;
        }
        // Check to see whether the subtype is already defined ...
        final boolean alreadySubtype = this.getSubtypes().contains(subtype);
        if ( alreadySubtype ) {
            return false;
        }
        return true;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getSourceRole()
     */
    public RelationshipRole getSourceRole() {
        RelationshipRole role = this.relationshipType.getSourceRole();
        while ( role == null ) {
            role = this.factory.createRelationshipRole();
            role.setRelationshipType(this.relationshipType);
            role = this.relationshipType.getSourceRole();
        }
        return role;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getTargetRole()
     */
    public RelationshipRole getTargetRole() {
        RelationshipRole role = this.relationshipType.getTargetRole();
        while ( role == null ) {
            role = this.factory.createRelationshipRole();
            role.setRelationshipType(this.relationshipType);
            role = this.relationshipType.getTargetRole();
        }
        return role;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getRoleName(com.metamatrix.metamodels.relationship.RelationshipRole)
     */
    public String getRoleName(final RelationshipRole role) {
        ArgCheck.isNotNull(role);
        return role.getName();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setRoleName(com.metamatrix.metamodels.relationship.RelationshipRole,java.lang.String)
     */
    public void setRoleName(final RelationshipRole role, final String name) {
        ArgCheck.isNotNull(role);
        role.setName(name);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#isNavigable(com.metamatrix.metamodels.relationship.RelationshipRole)
     */
    public boolean isNavigable(final RelationshipRole role) {
        ArgCheck.isNotNull(role);
        return role.isNavigable();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setNavigable(com.metamatrix.metamodels.relationship.RelationshipRole, boolean)
     */
    public void setNavigable(final RelationshipRole role, final boolean navigable) {
        ArgCheck.isNotNull(role);
        role.setNavigable(navigable);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#isOrdered(com.metamatrix.metamodels.relationship.RelationshipRole)
     */
    public boolean isOrdered(final RelationshipRole role) {
        ArgCheck.isNotNull(role);
        return role.isOrdered();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setOrdered(com.metamatrix.metamodels.relationship.RelationshipRole, boolean)
     */
    public void setOrdered(final RelationshipRole role, final boolean ordered) {
        ArgCheck.isNotNull(role);
        role.setOrdered(ordered);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#isUnique(com.metamatrix.metamodels.relationship.RelationshipRole)
     */
    public boolean isUnique(final RelationshipRole role) {
        ArgCheck.isNotNull(role);
        return role.isUnique();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setUnique(com.metamatrix.metamodels.relationship.RelationshipRole, boolean)
     */
    public void setUnique(final RelationshipRole role, final boolean unique) {
        ArgCheck.isNotNull(role);
        role.setUnique(unique);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getLowerBound(com.metamatrix.metamodels.relationship.RelationshipRole)
     */
    public int getLowerBound(final RelationshipRole role) {
        ArgCheck.isNotNull(role);
        return role.getLowerBound();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setLowerBound(com.metamatrix.metamodels.relationship.RelationshipRole, int)
     */
    public void setLowerBound(final RelationshipRole role, final int lowerBound) {
        ArgCheck.isNotNull(role);
        role.setLowerBound(lowerBound);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getUpperBound(com.metamatrix.metamodels.relationship.RelationshipRole)
     */
    public int getUpperBound(final RelationshipRole role) {
        ArgCheck.isNotNull(role);
        return role.getUpperBound();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#setUpperBound(com.metamatrix.metamodels.relationship.RelationshipRole, int)
     */
    public void setUpperBound(final RelationshipRole role, final int upperBound) {
        ArgCheck.isNotNull(role);
        role.setUpperBound(upperBound);
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getIncludedMetaclasses(com.metamatrix.metamodels.relationship.RelationshipRole)
     */
    public List getIncludedMetaclasses(final RelationshipRole role) {
        ArgCheck.isNotNull(role);
        return role.getIncludeTypes();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#canAddIncludedMetaclass(com.metamatrix.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public boolean canAddIncludedMetaclass(final RelationshipRole role, final EClass metaclass) {
        ArgCheck.isNotNull(role);
        ArgCheck.isNotNull(metaclass);
        final List includedTypes = role.getIncludeTypes();
        if ( includedTypes.contains(metaclass) ) {
            return false;
        }
        final List excludedTypes = role.getExcludeTypes();
        if ( excludedTypes.contains(metaclass) ) {
            return false;
        }
        final Iterator iter = includedTypes.iterator();
        while (iter.hasNext()) {
            final EClass existingIncludedEClass = (EClass)iter.next();
            if ( RelationshipUtil.isAncestor(metaclass,existingIncludedEClass) ) {
                // The metaclass is a subtype of an existing included metaclass, so no need to add
                return false;
            }
        }
        return true;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#addIncludedMetaclass(com.metamatrix.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public void addIncludedMetaclass(final RelationshipRole role, final EClass metaclass) throws ModelerCoreException {
        ArgCheck.isNotNull(role);
        ArgCheck.isNotNull(metaclass);
        ModelerCore.getModelEditor().addValue(role,metaclass,role.getIncludeTypes());
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#removeIncludedMetaclass(com.metamatrix.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public boolean removeIncludedMetaclass(final RelationshipRole role, final EClass metaclass) throws ModelerCoreException {
        ArgCheck.isNotNull(role);
        ArgCheck.isNotNull(metaclass);
        if ( !role.getIncludeTypes().contains(metaclass) ) {
            return false;
        }
        ModelerCore.getModelEditor().removeValue(role,metaclass,role.getIncludeTypes());
        return true;
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#getExcludedMetaclasses(com.metamatrix.metamodels.relationship.RelationshipRole)
     */
    public List getExcludedMetaclasses(final RelationshipRole role) {
        ArgCheck.isNotNull(role);
        return role.getExcludeTypes();
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#canAddExcludedMetaclass(com.metamatrix.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public boolean canAddExcludedMetaclass(final RelationshipRole role, final EClass metaclass) {
        ArgCheck.isNotNull(role);
        ArgCheck.isNotNull(metaclass);
        final List excludedTypes = role.getExcludeTypes();
        if ( excludedTypes.contains(metaclass) ) {
            return false;
        }
        final List includedTypes = role.getIncludeTypes();
        if ( includedTypes.contains(metaclass) ) {
            return false;
        }
        final Iterator iter = excludedTypes.iterator();
        while (iter.hasNext()) {
            final EClass existingExcludedEClass = (EClass)iter.next();
            if ( RelationshipUtil.isAncestor(metaclass,existingExcludedEClass) ) {
                // The metaclass is a subtype of an existing excluded metaclass, so no need to add
                return false;
            }
        }
        return true;
    }
    
    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#addExcludedMetaclass(com.metamatrix.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public void addExcludedMetaclass(final RelationshipRole role, final EClass metaclass) throws ModelerCoreException {
        ArgCheck.isNotNull(role);
        ArgCheck.isNotNull(metaclass);
        ModelerCore.getModelEditor().addValue(role,metaclass,role.getExcludeTypes());
    }

    /**
     * @see com.metamatrix.modeler.relationship.RelationshipTypeEditor#removeExcludedMetaclass(com.metamatrix.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public boolean removeExcludedMetaclass(final RelationshipRole role, final EClass metaclass) throws ModelerCoreException {
        ArgCheck.isNotNull(role);
        ArgCheck.isNotNull(metaclass);
        if ( !role.getExcludeTypes().contains(metaclass) ) {
            return false;
        }
        ModelerCore.getModelEditor().removeValue(role,metaclass,role.getExcludeTypes());
        return true;
    }

}
