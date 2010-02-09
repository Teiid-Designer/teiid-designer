/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.VisibilityKind;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.uml2.Uml2MetamodelConstants;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;

public class Uml2AssociationUmlAspect extends AbstractUml2UmlAspect implements UmlAssociation {

    /**
	 * 
	 */
    public Uml2AssociationUmlAspect( MetamodelEntity entity ) {
        super();
        setMetamodelEntity(entity);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#getEndCount(java.lang.Object)
     */
    public int getEndCount( Object assoc ) {
        return 2; // associations are always binary
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#getRoleName(java.lang.Object, int)
     */
    public String getRoleName( final Object assoc,
                               final int end ) {
        final Association a = assertAssociation(assoc);
        final Property aEnd = getOppositeEnd(a, end);
        final String roleName = aEnd.getName();
        if (roleName == null || roleName.length() == 0) {
            return StringUtil.Constants.EMPTY_STRING;
        }
        return getVisibilityString(aEnd) + roleName;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#getMultiplicity(java.lang.Object, int)
     */
    public String getMultiplicity( Object assoc,
                                   int end ) {
        final Association a = assertAssociation(assoc);
        final Property p = getOppositeEnd(a, end);
        if (p != null) {
            final int lower = p.getLower();
            final int upper = p.getUpper();
            final StringBuffer sb = new StringBuffer(100);
            sb.append(StringUtil.Constants.EMPTY_STRING);
            sb.append(lower);
            if (upper != lower) {
                sb.append(".."); //$NON-NLS-1$
                if (upper == -1) {
                    sb.append('*');
                } else {
                    sb.append(upper);
                }
            }
            return sb.toString();
        }
        return StringUtil.Constants.EMPTY_STRING;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#getAggregation(java.lang.Object, int)
     */
    public int getAggregation( final Object assoc,
                               final int end ) {
        final Association a = assertAssociation(assoc);
        final Property aEnd = getAssociationEnd(a, end);
        final AggregationKind kind = aEnd.getAggregation();
        if (kind == AggregationKind.NONE_LITERAL) {
            return UmlAssociation.AGGREGATION_NONE;
        } else if (kind == AggregationKind.SHARED_LITERAL) {
            return UmlAssociation.AGGREGATION_SHARED;
        } else {
            return UmlAssociation.AGGREGATION_COMPOSITE;
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#getProperties(java.lang.Object, int)
     */
    public String[] getProperties( Object assoc,
                                   int end ) {
        final Association a = assertAssociation(assoc);
        final Property p = getOppositeEnd(a, end);
        return this.getPropertyStrings(p);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#getNavigability(java.lang.Object, int)
     */
    public int getNavigability( final Object assoc,
                                final int end ) {
        final Association a = assertAssociation(assoc);
        final List ownedEnds = a.getOwnedEnds();
        final Property aEnd = getOppositeEnd(a, end);

        if (ownedEnds.contains(aEnd)) {
            return UmlAssociation.NAVIGABILITY_NONE;
        }
        if (aEnd.getOwner() instanceof Classifier) {
            return UmlAssociation.NAVIGABILITY_NAVIGABLE;
        }

        return UmlAssociation.NAVIGABILITY_NAVIGABLE;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#getEnd(java.lang.Object, int)
     */
    public EObject getEnd( final Object assoc,
                           final int end ) {
        final Association a = assertAssociation(assoc);
        final Property aEnd = getOppositeEnd(a, end);
        // final Property aEnd = getAssociationEnd(a, end);
        return aEnd;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#getEndTarget(java.lang.Object, int)
     */
    public EObject getEndTarget( final Object assoc,
                                 final int end ) {
        final Association a = assertAssociation(assoc);
        final Property aEnd = getOppositeEnd(a, end);
        return aEnd.getType();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#setRoleName(java.lang.Object, int, java.lang.String)
     */
    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#setRoleName(java.lang.Object, int, java.lang.String)
     */
    public IStatus setRoleName( Object assoc,
                                int end,
                                String name ) {
        throw new UnsupportedOperationException(
                                                Uml2Plugin.Util.getString("Uml2AssociationUmlAspect.Signature_may_not_be_set_on_a_1", getStereotype(assoc))); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#setMultiplicity(java.lang.Object, int, java.lang.String)
     */
    public IStatus setMultiplicity( Object assoc,
                                    int end,
                                    String mult ) {
        // final Association a = assertAssociation(assoc);
        // if(end == 0) {
        // a.setForeignKeyMultiplicity(mult);
        // return new Status(IStatus.OK, Uml2MetamodelConstants.PLUGIN_ID, 0, "OK", null);
        // }else if(end == 1){
        // a.setPrimaryKeyMultiplicity(mult);
        // return new Status(IStatus.OK, Uml2MetamodelConstants.PLUGIN_ID, 0, "OK", null);
        // }
        return new Status(IStatus.ERROR, Uml2MetamodelConstants.PLUGIN_ID, 0,
                          Uml2Plugin.Util.getString("Uml2AssociationUmlAspect.Invalid_end_11"), null); //$NON-NLS-1$

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#setProperties(java.lang.Object, int, java.lang.String)
     */
    public IStatus setProperties( Object assoc,
                                  int end,
                                  String[] props ) {
        throw new UnsupportedOperationException(
                                                Uml2Plugin.Util.getString("Uml2AssociationUmlAspect.Properties_may_not_be_set_on_a__12", getStereotype(assoc))); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation#setNavigability(java.lang.Object, int, int)
     */
    public IStatus setNavigability( Object assoc,
                                    int end,
                                    int navigability ) {
        throw new UnsupportedOperationException(
                                                Uml2Plugin.Util.getString("Uml2AssociationUmlAspect.Navigability_may_not_be_set_on_a__13", getStereotype(assoc))); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getIconPath(java.lang.Object)
     */
    public String getIconPath( Object eObject ) { // NO_UCD
        return "icons"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getVisibility(java.lang.Object)
     */
    @Override
    public int getVisibility( Object eObject ) {
        final Association a = assertAssociation(eObject);
        return getVisibility(a);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype( Object eObject ) {
        return Uml2Plugin.getPluginResourceLocator().getString("_UI_Association_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
    public String getSignature( Object eObject,
                                int showMask ) {
        // return getSignature(eObject, UmlAssociation.SIGNATURE_NAME);
        final Association assoc = assertAssociation(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1:
                // Name
                result.append(assoc.getName());
                break;
            case 2:
                // Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject));
                result.append(">>"); //$NON-NLS-1$
                break;
            case 3:
                // Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject));
                result.append(">> "); //$NON-NLS-1$
                result.append(assoc.getName());
                break;
            case 4:
                // Return properties
                break;
            case 5:
                // Name and properties
                result.append(assoc.getName());
                break;
            case 6:
                // Return Properties and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject));
                result.append(">> "); //$NON-NLS-1$
                break;
            case 7:
                // Name, Stereotype and Properties
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject));
                result.append(">> "); //$NON-NLS-1$
                result.append(assoc.getName());
                break;
            default:
                throw new MetaMatrixRuntimeException("Invalid showMask for getSignature" + showMask); //$NON-NLS-1$
        }
        return result.toString();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     */
    public String getEditableSignature( Object eObject ) {
        return getSignature(eObject, UmlAssociation.SIGNATURE_NAME);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature( Object eObject,
                                 String newSignature ) {
        try {
            final Association assoc = assertAssociation(eObject);
            assoc.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, Uml2MetamodelConstants.PLUGIN_ID, 0, e.getMessage(), e);
        }

        return new Status(IStatus.OK, Uml2MetamodelConstants.PLUGIN_ID, 0,
                          Uml2Plugin.Util.getString("Uml2AssociationUmlAspect.OK_15"), null); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getName(java.lang.Object)
     */
    public String getName( Object eObject ) {
        final Association assoc = assertAssociation(eObject);
        return assoc.getName();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getToolTip(java.lang.Object)
     */
    public String getToolTip( Object eObject ) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append(this.getStereotype(eObject));
        sb.append(' ');
        Classifier classifier = (Classifier)this.getEndTarget(eObject, 0);
        sb.append(classifier.getName());
        sb.append(" --- "); //$NON-NLS-1$
        classifier = (Classifier)this.getEndTarget(eObject, 1);
        sb.append(classifier.getName());
        return sb.toString();
    }

    public String toString( Object eObject ) { // NO_UCD
        final Association a = assertAssociation(eObject);
        final StringBuffer sb = new StringBuffer(200);
        sb.append("Association "); //$NON-NLS-1$
        sb.append(a.getName());
        sb.append(' ');
        Property end1 = this.getAssociationEnd(a, 0);
        sb.append(end1.getName());
        sb.append(":"); //$NON-NLS-1$
        sb.append(end1.getAggregation().getName());
        sb.append(" ("); //$NON-NLS-1$
        Classifier type1 = (Classifier)this.getAssociationEnd(a, 1).getType();
        sb.append(type1.getName());
        sb.append(") --> "); //$NON-NLS-1$

        Property end2 = this.getAssociationEnd(a, 1);
        sb.append(end2.getName());
        sb.append(":"); //$NON-NLS-1$
        sb.append(end2.getAggregation().getName());
        sb.append(" ("); //$NON-NLS-1$
        Classifier type2 = (Classifier)this.getAssociationEnd(a, 0).getType();
        sb.append(type2.getName());
        sb.append(")"); //$NON-NLS-1$

        return sb.toString();
    }

    protected Association assertAssociation( Object eObject ) {
        ArgCheck.isInstanceOf(Association.class, eObject);
        return (Association)eObject;
    }

    private Property getOppositeEnd( final Association assoc,
                                     final int end ) {
        final int oppositeEnd = (end == 0 ? 1 : 0);
        return this.getAssociationEnd(assoc, oppositeEnd);
    }

    private Property getAssociationEnd( final Association assoc,
                                        final int end ) {

        // Log error if the binary relationship does not reference at least two ends
        if (assoc.getMemberEnds().size() < 2) {
            final String msg = Uml2Plugin.Util.getString("Uml2AssociationUmlAspect.Association_found_referencing_more_that_two_association_ends_1"); //$NON-NLS-1$
            Uml2Plugin.Util.log(IStatus.ERROR, msg);
            return null;
        }
        // Return the assocation end for the specified index
        return assoc.getMemberEnds().get(end);
    }

    private String getVisibilityString( Property property ) {
        String result = " "; //$NON-NLS-1$
        VisibilityKind vk = property.getVisibility();
        if (vk.getValue() == VisibilityKind.PUBLIC) {
            result = "+"; //$NON-NLS-1$
        } else if (vk.getValue() == VisibilityKind.PRIVATE) {
            result = "-"; //$NON-NLS-1$
        } else if (vk.getValue() == VisibilityKind.PROTECTED) {
            result = "#"; //$NON-NLS-1$
        } else if (vk.getValue() == VisibilityKind.PACKAGE) {
            result = " "; //$NON-NLS-1$
        }
        return result;
    }

    /**
     * If the property represents an association end The following property strings can be applied: <li>{subsets <property-name>}
     * to show that the end is a subset of the property called <property-name>. {redefined <end-name>} to show that the end
     * redefines the one named <end-name>. {union} to show that the end is derived by being the union of its subsets. {ordered} to
     * show that the end represents an ordered set. {bag} to show that the end represents a collection that permits the same
     * element to appear more than once. {sequence} or {seq} to show that the end represents a sequence (an ordered bag).</li>
     * </p>
     * 
     * @param p
     * @param result
     */
    protected void appendProperties( final Property p, // NO_UCD
                                     final StringBuffer result ) {
        ArgCheck.isNotNull(p);
        if (p.isOrdered()) {
            result.append("{ordered}"); //$NON-NLS-1$
        }
        if (!p.isUnique() && p.isMultivalued()) {
            if (p.isOrdered()) {
                result.append("{bag}"); //$NON-NLS-1$
            } else {
                result.append("{seq}"); //$NON-NLS-1$
            }
        }
        final List redefinedProps = p.getRedefinedProperties();
        if (redefinedProps != null && !redefinedProps.isEmpty()) {
            result.append('{');
            appendListAsCommaDelimitedString(redefinedProps, "redefines ", result); //$NON-NLS-1$
            result.append('}');
        }
        final List subsettedProps = p.getSubsettedProperties();
        if (subsettedProps != null && !subsettedProps.isEmpty()) {
            result.append('{');
            appendListAsCommaDelimitedString(subsettedProps, "subsets ", result); //$NON-NLS-1$
            result.append('}');
        }
    }

    /**
     * If the property represents an association end The following property strings can be applied: <li>{subsets <property-name>}
     * to show that the end is a subset of the property called <property-name>. {redefined <end-name>} to show that the end
     * redefines the one named <end-name>. {union} to show that the end is derived by being the union of its subsets. {ordered} to
     * show that the end represents an ordered set. {bag} to show that the end represents a collection that permits the same
     * element to appear more than once. {sequence} or {seq} to show that the end represents a sequence (an ordered bag).</li>
     * </p>
     * 
     * @param p
     * @param result
     */
    protected String[] getPropertyStrings( final Property p ) {
        ArgCheck.isNotNull(p);
        final List result = new ArrayList();
        if (p.isOrdered()) {
            result.add("{ordered}"); //$NON-NLS-1$
        }
        if (!p.isUnique() && p.isMultivalued()) {
            if (p.isOrdered()) {
                result.add("{bag}"); //$NON-NLS-1$
            } else {
                result.add("{seq}"); //$NON-NLS-1$
            }
        }
        final List redefinedProps = p.getRedefinedProperties();
        if (redefinedProps != null && !redefinedProps.isEmpty()) {
            final StringBuffer sb = new StringBuffer(100);
            sb.append('{');
            appendListAsCommaDelimitedString(redefinedProps, "redefines ", sb); //$NON-NLS-1$
            sb.append('}');
            result.add(sb.toString());
        }
        final List subsettedProps = p.getSubsettedProperties();
        if (subsettedProps != null && !subsettedProps.isEmpty()) {
            final StringBuffer sb = new StringBuffer(100);
            sb.append('{');
            appendListAsCommaDelimitedString(subsettedProps, "subsets ", sb); //$NON-NLS-1$
            sb.append('}');
            result.add(sb.toString());
        }
        return (result.isEmpty() ? StringUtil.Constants.EMPTY_STRING_ARRAY : (String[])result.toArray(new String[result.size()]));
    }

    protected void appendListAsCommaDelimitedString( final List values,
                                                     final String prefix,
                                                     final StringBuffer result ) {
        if (values == null || values.size() == 0) {
            return;
        }
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            NamedElement ne = (NamedElement)iter.next();
            if (prefix != null) {
                result.append(prefix);
            }
            result.append(ne.getName());
            if (iter.hasNext()) {
                result.append(',');
            }
        }
    }

}
