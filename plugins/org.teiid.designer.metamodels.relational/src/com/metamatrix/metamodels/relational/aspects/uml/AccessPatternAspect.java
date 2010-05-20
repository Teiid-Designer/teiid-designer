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
import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.RelationalMetamodelConstants;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;

/**
 * AccessPatternAspect
 */
public class AccessPatternAspect extends RelationalEntityAspect implements UmlAssociation {

    /**
     * Construct an instance of AccessPatternAspect.
     * 
     */
    public AccessPatternAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }

    public int getEndCount(Object obj) {
        return 0;
    }

    public String getRoleName(Object assoc, int end) {
        final AccessPattern pattern = assertAccessPattern(assoc);
        return pattern.getName();
    }
    
    public EObject getEnd(Object primaryKey, int end){
        return null;
    }

    public EObject getEndTarget(Object primaryKey, int end){
        return null;
    }
    
    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_AccessPattern_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#getAggregation(java.lang.Object, int)
     */
    public int getAggregation(Object assoc, int end) {
        return AGGREGATION_NONE;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#getProperties(java.lang.Object, int)
     */
    public String[] getProperties(Object assoc, int end) {
        return CoreStringUtil.Constants.EMPTY_STRING_ARRAY;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#getNavigability(java.lang.Object, int)
     */
    public int getNavigability(Object assoc, int end) {
        //final UniqueKey key = assertUniqueKey(assoc);
        if(end == 1){
            return NAVIGABILITY_NAVIGABLE;
        }else if (end == 0){
            return NAVIGABILITY_NONE;
        }
        
        return NAVIGABILITY_UNKNOWN;
    }

    public String getMultiplicity(Object eObject, int end) {
        /*final AccessPattern pattern =*/ assertAccessPattern(eObject);
        if(end == 0){
            return("0..1"); //$NON-NLS-1$
        }else if(end == 1){
            return("0..1"); //$NON-NLS-1$
        }
        
        return(""); //$NON-NLS-1$
    }

    public String getSignature(Object eObject, int showMask) {
        final AccessPattern pattern = assertAccessPattern(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(pattern.getName() );
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
                result.append(pattern.getName() );                       
                break;
            case 4 : 
                //Return properties
                result.append( super.getArrayAsString(getProperties(pattern, 0)) );
                break;
            case 5 :
                //Name and properties
                result.append(pattern.getName() );
                result.append( super.getArrayAsString(getProperties(pattern, 0)) );
                break;
            case 6 :
                //Return Properties and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$
                result.append( super.getArrayAsString(getProperties(pattern, 0)) );
                break;
            case 7 :
                //Name, Stereotype and Properties
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$
                result.append(pattern.getName() );
                result.append( super.getArrayAsString(getProperties(pattern, 0)) );
                break;
            default :
                throw new TeiidRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlAssociation.SIGNATURE_NAME);
    }
 
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setRoleName(java.lang.Object, int, java.lang.String)
     */
    public IStatus setRoleName(Object assoc, int end, String name) {
        throw new UnsupportedOperationException("TODO"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setMultiplicity(java.lang.Object, int, java.lang.String)
     */
    public IStatus setMultiplicity(Object assoc, int end, String mult) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setProperties(java.lang.Object, int, java.lang.String)
     */
    public IStatus setProperties(Object assoc, int end, String[] props) {
        throw new UnsupportedOperationException(); 
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setNavigability(java.lang.Object, int, int)
     */
    public IStatus setNavigability(Object assoc, int end, int navigability) {
        throw new UnsupportedOperationException(); 
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            AccessPattern ap = assertAccessPattern(eObject);
            ap.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, RelationalMetamodelConstants.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getName(java.lang.Object)
     */
    public String getName(Object eObject) {
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlRelationship#getToolTip(java.lang.Object)
     */
    public String getToolTip(Object eObject) {
        final StringBuffer sb = new StringBuffer(200);
        sb.append(this.getStereotype(eObject));
        return sb.toString();
    }

    protected AccessPattern assertAccessPattern(Object eObject) {
        CoreArgCheck.isInstanceOf(AccessPattern.class, eObject);
        return (AccessPattern)eObject;
    }

}
