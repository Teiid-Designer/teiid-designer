/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.LogicalRelationshipEnd;
import com.metamatrix.metamodels.relational.MultiplicityKind;
import com.metamatrix.metamodels.relational.RelationalMetamodelConstants;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;


/**
 * ForeignKeyAspect
 */
public class LogicalRelationshipAspect extends RelationalEntityAspect implements UmlAssociation  {
    /**
     * Construct an instance of ForeignKeyAspect.
     * @param entity
     */
    public LogicalRelationshipAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#getAggregation(java.lang.Object, int)
     */
    public int getAggregation(Object assoc, int end) {
        return AGGREGATION_NONE;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_LogicalRelationship_type"); //$NON-NLS-1$
    }

    public int getEndCount(Object assoc) {
        final LogicalRelationship lr = assertLogicalRelationship(assoc);
        return lr.getEnds().size();
    }

    public EObject getEndTarget(Object assoc, int end){
        final LogicalRelationship lr = assertLogicalRelationship(assoc);
        if (end < 0 || end >= lr.getEnds().size()) {
            return null;
        }
        final LogicalRelationshipEnd lre = (LogicalRelationshipEnd)lr.getEnds().get(end);
        return lre.getTable();
    }

    public EObject getEnd(Object assoc, int end){
        final LogicalRelationship lr = assertLogicalRelationship(assoc);
        if (end < 0 || end >= lr.getEnds().size()) {
            return null;
        }
        final LogicalRelationshipEnd lre = (LogicalRelationshipEnd)lr.getEnds().get(end);
        return lre;
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlAssociation.SIGNATURE_NAME);
    }

    public String getSignature(Object eObject, int showMask) {
        final LogicalRelationship lr = assertLogicalRelationship(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(lr.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">>"); //$NON-NLS-1$
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$
                result.append(lr.getName() );                       
                break;
            case 4 : 
                //Return properties
                result.append( super.getArrayAsString(getProperties(lr, 0)) );
                break;
            case 5 :
                //Name and properties
                result.append(lr.getName() );
                result.append( super.getArrayAsString(getProperties(lr, 0)) );
                break;
            case 6 :
                //Return Properties and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$
                result.append( super.getArrayAsString(getProperties(lr, 0)) );
                break;
            case 7 :
                //Name, Stereotype and Properties
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$
                result.append(lr.getName() );
                result.append( super.getArrayAsString(getProperties(lr, 0)) );
                break;
            default :
                throw new MetaMatrixRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            final LogicalRelationship lr = assertLogicalRelationship(eObject);
            lr.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, RelationalMetamodelConstants.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
    }
         
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#getNavigability(java.lang.Object, int)
     */
    public int getNavigability(Object assoc, int end) {
        assertLogicalRelationship(assoc);
        return NAVIGABILITY_NAVIGABLE;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setNavigability(java.lang.Object, int, int)
     */
    public IStatus setNavigability(Object assoc, int end, int navigability) {
        throw new UnsupportedOperationException(); 
    }

    public String getRoleName(Object assoc, int end) {
        final LogicalRelationship lr = assertLogicalRelationship(assoc);
        if (end < 0 || end >= lr.getEnds().size()) {
            return null;
        }
        
        LogicalRelationshipEnd lrEnd = (LogicalRelationshipEnd)lr.getEnds().get(end);
        return lrEnd.getName();
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setRoleName(java.lang.Object, int, java.lang.String)
     */
    public IStatus setRoleName(Object assoc, int end, String name) {
        throw new UnsupportedOperationException(); 
    }

    public String getMultiplicity(Object eObject, int end) {
        final LogicalRelationship lr = assertLogicalRelationship(eObject);
        if (end < 0 || end >= lr.getEnds().size()) {
            return null;
        }
        final LogicalRelationshipEnd lre = (LogicalRelationshipEnd)lr.getEnds().get(end);
        return lre.getMultiplicity().getName();
    }


    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setMultiplicity(java.lang.Object, int, java.lang.String)
     */
    public IStatus setMultiplicity(Object assoc, int end, String mult) {
        final LogicalRelationship lr = assertLogicalRelationship(assoc);
        if (end < 0 || end >= lr.getEnds().size()) {
            return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("KeyAspect.Invalid_end"), null); //$NON-NLS-1$
        }
        final LogicalRelationshipEnd lre = (LogicalRelationshipEnd)lr.getEnds().get(end);
        MultiplicityKind value = MultiplicityKind.get(mult);
        if (value != null) {
            lre.setMultiplicity(value);
            return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
        } 
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("KeyAspect.Invalid_end"), null); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#getProperties(java.lang.Object, int)
     */
    public String[] getProperties(Object assoc, int end) {
        return StringUtil.Constants.EMPTY_STRING_ARRAY;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setProperties(java.lang.Object, int, java.lang.String)
     */
    public IStatus setProperties(Object assoc, int end, String[] props) {
        throw new UnsupportedOperationException(); 
    }

    protected LogicalRelationshipEnd assertLogicalRelationshipEnd(Object eObject) {
        ArgCheck.isInstanceOf(LogicalRelationshipEnd.class, eObject);

        return (LogicalRelationshipEnd)eObject;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getName(java.lang.Object)
     */
    public String getName(Object eObject) {
        return StringUtil.Constants.EMPTY_STRING;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getToolTip(java.lang.Object)
     */
    public String getToolTip(Object eObject) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append(this.getStereotype(eObject));
        return sb.toString();
    }

    protected LogicalRelationship assertLogicalRelationship(Object eObject) {
        ArgCheck.isInstanceOf(LogicalRelationship.class, eObject);

        return (LogicalRelationship)eObject;
    }
}
