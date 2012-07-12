/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.metamodels.relationship.RelationshipFactory;
import org.teiid.designer.metamodels.relationship.RelationshipMetamodelPlugin;
import org.teiid.designer.metamodels.relationship.RelationshipRole;
import org.teiid.designer.metamodels.relationship.RelationshipType;
import org.teiid.designer.metamodels.relationship.util.RelationshipTypeManager;
import org.teiid.designer.metamodels.relationship.util.RelationshipUtil;


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
        CoreArgCheck.isNotNull(relationshipType);
        CoreArgCheck.isNotNull(factory);
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
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getRelationshipType()
     */
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#validate()
     */
    public IStatus validate() {
        return this.relationshipType.isValid();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getName()
     */
    public String getName() {
        return this.relationshipType.getName();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setName(java.lang.String)
     */
    public void setName(final String name) {
        this.relationshipType.setName(name);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getLabel()
     */
    public String getLabel() {
        return this.relationshipType.getLabel();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setLabel(java.lang.String)
     */
    public void setLabel(final String label) {
        this.relationshipType.setLabel(label);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getOppositeName()
     */
    public String getOppositeLabel() {
        return this.relationshipType.getOppositeLabel();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setOppositeLabel(java.lang.String)
     */
    public void setOppositeLabel(final String oppositeLabel) {
        this.relationshipType.setOppositeLabel(oppositeLabel);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getStereotype()
     */
    public String getStereotype() {
        return this.relationshipType.getStereotype();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setStereotype(java.lang.String)
     */
    public void setStereotype(final String stereotype) {
        this.relationshipType.setStereotype(stereotype);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getSourceRoleName()
     */
    public String getSourceRoleName() {
        return this.getSourceRole().getName();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getTargetRoleName()
     */
    public String getTargetRoleName() {
        return this.getTargetRole().getName();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getFeatures()
     */
    public List getFeatures() {
        return this.relationshipType.getRelationshipFeatures();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#isDirected()
     */
    public boolean isDirected() {
        return this.relationshipType.isDirected();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setDirected(boolean)
     */
    public void setDirected(final boolean directed) {
        this.relationshipType.setDirected(directed);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#isExclusive()
     */
    public boolean isExclusive() {
        return this.relationshipType.isExclusive();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setExclusive(boolean)
     */
    public void setExclusive(final boolean exclusive) {
        this.relationshipType.setExclusive(exclusive);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#isCrossModel()
     */
    public boolean isCrossModel() {
        return this.relationshipType.isCrossModel();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setCrossModel(boolean)
     */
    public void setCrossModel(final boolean crossModel) {
        this.relationshipType.setCrossModel(crossModel);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#isAbstract()
     */
    public boolean isAbstract() {
        return this.relationshipType.isAbstract();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setAbstract(boolean)
     */
    public void setAbstract(final boolean abstractState) {
        this.relationshipType.setAbstract(abstractState);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getSupertype()
     */
    public RelationshipType getSupertype() {
        return this.relationshipType.getSuperType();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setSupertype(org.teiid.designer.metamodels.relationship.RelationshipType)
     */
    public void setSupertype(final RelationshipType supertype) {
        this.relationshipType.setSuperType(supertype);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#canSetSupertype(org.teiid.designer.metamodels.relationship.RelationshipType)
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
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getSubtypes()
     */
    public List getSubtypes() {
        return this.relationshipType.getSubType();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#canAddSubtype(org.teiid.designer.metamodels.relationship.RelationshipType)
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
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getSourceRole()
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
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getTargetRole()
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
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getRoleName(org.teiid.designer.metamodels.relationship.RelationshipRole)
     */
    public String getRoleName(final RelationshipRole role) {
        CoreArgCheck.isNotNull(role);
        return role.getName();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setRoleName(org.teiid.designer.metamodels.relationship.RelationshipRole,java.lang.String)
     */
    public void setRoleName(final RelationshipRole role, final String name) {
        CoreArgCheck.isNotNull(role);
        role.setName(name);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#isNavigable(org.teiid.designer.metamodels.relationship.RelationshipRole)
     */
    public boolean isNavigable(final RelationshipRole role) {
        CoreArgCheck.isNotNull(role);
        return role.isNavigable();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setNavigable(org.teiid.designer.metamodels.relationship.RelationshipRole, boolean)
     */
    public void setNavigable(final RelationshipRole role, final boolean navigable) {
        CoreArgCheck.isNotNull(role);
        role.setNavigable(navigable);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#isOrdered(org.teiid.designer.metamodels.relationship.RelationshipRole)
     */
    public boolean isOrdered(final RelationshipRole role) {
        CoreArgCheck.isNotNull(role);
        return role.isOrdered();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setOrdered(org.teiid.designer.metamodels.relationship.RelationshipRole, boolean)
     */
    public void setOrdered(final RelationshipRole role, final boolean ordered) {
        CoreArgCheck.isNotNull(role);
        role.setOrdered(ordered);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#isUnique(org.teiid.designer.metamodels.relationship.RelationshipRole)
     */
    public boolean isUnique(final RelationshipRole role) {
        CoreArgCheck.isNotNull(role);
        return role.isUnique();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setUnique(org.teiid.designer.metamodels.relationship.RelationshipRole, boolean)
     */
    public void setUnique(final RelationshipRole role, final boolean unique) {
        CoreArgCheck.isNotNull(role);
        role.setUnique(unique);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getLowerBound(org.teiid.designer.metamodels.relationship.RelationshipRole)
     */
    public int getLowerBound(final RelationshipRole role) {
        CoreArgCheck.isNotNull(role);
        return role.getLowerBound();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setLowerBound(org.teiid.designer.metamodels.relationship.RelationshipRole, int)
     */
    public void setLowerBound(final RelationshipRole role, final int lowerBound) {
        CoreArgCheck.isNotNull(role);
        role.setLowerBound(lowerBound);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getUpperBound(org.teiid.designer.metamodels.relationship.RelationshipRole)
     */
    public int getUpperBound(final RelationshipRole role) {
        CoreArgCheck.isNotNull(role);
        return role.getUpperBound();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#setUpperBound(org.teiid.designer.metamodels.relationship.RelationshipRole, int)
     */
    public void setUpperBound(final RelationshipRole role, final int upperBound) {
        CoreArgCheck.isNotNull(role);
        role.setUpperBound(upperBound);
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getIncludedMetaclasses(org.teiid.designer.metamodels.relationship.RelationshipRole)
     */
    public List getIncludedMetaclasses(final RelationshipRole role) {
        CoreArgCheck.isNotNull(role);
        return role.getIncludeTypes();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#canAddIncludedMetaclass(org.teiid.designer.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public boolean canAddIncludedMetaclass(final RelationshipRole role, final EClass metaclass) {
        CoreArgCheck.isNotNull(role);
        CoreArgCheck.isNotNull(metaclass);
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
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#addIncludedMetaclass(org.teiid.designer.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public void addIncludedMetaclass(final RelationshipRole role, final EClass metaclass) throws ModelerCoreException {
        CoreArgCheck.isNotNull(role);
        CoreArgCheck.isNotNull(metaclass);
        ModelerCore.getModelEditor().addValue(role,metaclass,role.getIncludeTypes());
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#removeIncludedMetaclass(org.teiid.designer.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public boolean removeIncludedMetaclass(final RelationshipRole role, final EClass metaclass) throws ModelerCoreException {
        CoreArgCheck.isNotNull(role);
        CoreArgCheck.isNotNull(metaclass);
        if ( !role.getIncludeTypes().contains(metaclass) ) {
            return false;
        }
        ModelerCore.getModelEditor().removeValue(role,metaclass,role.getIncludeTypes());
        return true;
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#getExcludedMetaclasses(org.teiid.designer.metamodels.relationship.RelationshipRole)
     */
    public List getExcludedMetaclasses(final RelationshipRole role) {
        CoreArgCheck.isNotNull(role);
        return role.getExcludeTypes();
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#canAddExcludedMetaclass(org.teiid.designer.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public boolean canAddExcludedMetaclass(final RelationshipRole role, final EClass metaclass) {
        CoreArgCheck.isNotNull(role);
        CoreArgCheck.isNotNull(metaclass);
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
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#addExcludedMetaclass(org.teiid.designer.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public void addExcludedMetaclass(final RelationshipRole role, final EClass metaclass) throws ModelerCoreException {
        CoreArgCheck.isNotNull(role);
        CoreArgCheck.isNotNull(metaclass);
        ModelerCore.getModelEditor().addValue(role,metaclass,role.getExcludeTypes());
    }

    /**
     * @see org.teiid.designer.relationship.RelationshipTypeEditor#removeExcludedMetaclass(org.teiid.designer.metamodels.relationship.RelationshipRole, org.eclipse.emf.ecore.EClass)
     */
    public boolean removeExcludedMetaclass(final RelationshipRole role, final EClass metaclass) throws ModelerCoreException {
        CoreArgCheck.isNotNull(role);
        CoreArgCheck.isNotNull(metaclass);
        if ( !role.getExcludeTypes().contains(metaclass) ) {
            return false;
        }
        ModelerCore.getModelEditor().removeValue(role,metaclass,role.getExcludeTypes());
        return true;
    }

}
