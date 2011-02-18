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
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.MultiplicityKind;
import com.metamatrix.metamodels.relational.RelationalMetamodelConstants;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;


/**
 * ForeignKeyAspect
 */
public class ForeignKeyAspect extends RelationalEntityAspect implements UmlAssociation  {
    /**
     * Construct an instance of ForeignKeyAspect.
     * @param entity
     */
    public ForeignKeyAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#getAggregation(java.lang.Object, int)
     */
    public int getAggregation(Object foreignKey, int end) {
//        final ForeignKey fk = assertForeignKey(foreignKey);
        if(end == 0){
//            final CascadeDeletesType cascade = fk.getCascadeDeletes();
//            if ( CascadeDeletesType.ALWAYS_LITERAL.equals(cascade) ) {
//                return AGGREGATION_COMPOSITE;
//            }
            return AGGREGATION_NONE;
        }else if(end == 1){
            return AGGREGATION_NONE;
        }
        
        throw new IllegalArgumentException(RelationalPlugin.Util.getString("KeyAspect.Invalid_end")); //$NON-NLS-1$
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_ForeignKey_type"); //$NON-NLS-1$
    }

    public int getEndCount(Object obj) {
        return 2;
    }

    public EObject getEndTarget(Object foreignKey, int end){
        ForeignKey fk = assertForeignKey(foreignKey);
        if(end == 0){
            return fk.getTable();
        }else if(end == 1){
            if(fk.getUniqueKey() != null){
                return fk.getUniqueKey().getTable();
            }
        }
        
        return null;
    }

    public EObject getEnd(Object foreignKey, int end){
        ForeignKey fk = assertForeignKey(foreignKey);
        if(end == 0){
            return fk;
        }else if(end == 1){
            if(fk.getUniqueKey() != null){
                return fk.getUniqueKey();
            }
        }
        
        return null;
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlAssociation.SIGNATURE_NAME);
    }

    public String getSignature(Object eObject, int showMask) {
        final ForeignKey key = assertForeignKey(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(key.getName() );
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
                result.append(key.getName() );                       
                break;
            case 4 : 
                //Return properties
                result.append( super.getArrayAsString(getProperties(key, 0)) );
                break;
            case 5 :
                //Name and properties
                result.append(key.getName() );
                result.append( super.getArrayAsString(getProperties(key, 0)) );
                break;
            case 6 :
                //Return Properties and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$
                result.append( super.getArrayAsString(getProperties(key, 0)) );
                break;
            case 7 :
                //Name, Stereotype and Properties
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$
                result.append(key.getName() );
                result.append( super.getArrayAsString(getProperties(key, 0)) );
                break;
            default :
                throw new TeiidRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            final ForeignKey key = assertForeignKey(eObject);
            key.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, RelationalMetamodelConstants.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
    }
         
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#getNavigability(java.lang.Object, int)
     */
    public int getNavigability(Object assoc, int end) {
        assertForeignKey(assoc);
        if(end == 0){
            return NAVIGABILITY_NONE;
        }else if(end == 1){
            return NAVIGABILITY_NAVIGABLE;
        }
        
        throw new IllegalArgumentException(RelationalPlugin.Util.getString("KeyAspect.Invalid_end")); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setNavigability(java.lang.Object, int, int)
     */
    public IStatus setNavigability(Object assoc, int end, int navigability) {
        throw new UnsupportedOperationException(); 
    }

    public String getRoleName(Object assoc, int end) {
        ForeignKey fk = assertForeignKey(assoc);
        switch( end ) {
            case 0:
                return fk.getName();
            case 1:
                if( fk.getUniqueKey() != null ){
                    return fk.getUniqueKey().getName();
                }
        }
        return ""; //$NON-NLS-1$
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setRoleName(java.lang.Object, int, java.lang.String)
     */
    public IStatus setRoleName(Object assoc, int end, String name) {
        ForeignKey fk = assertForeignKey(assoc);
        switch( end ) {
            case 0:
                fk.setName(name);
                return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
            case 1:
                if( fk.getUniqueKey() != null ){
                    fk.getUniqueKey().setName(name);
                    return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
                }
        }
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("KeyAspect.Invalid_end"), null); //$NON-NLS-1$
    }

    public String getMultiplicity(Object eObject, int end) {
        final ForeignKey key = assertForeignKey(eObject);
        if(end == 0){
            final MultiplicityKind mult = key.getForeignKeyMultiplicity();
            if ( mult.getValue() == MultiplicityKind.UNSPECIFIED ) {
                return ""; //$NON-NLS-1$
            }
            return mult.toString();
        }else if(end == 1){
            final MultiplicityKind mult = key.getPrimaryKeyMultiplicity();
            if ( mult.getValue() == MultiplicityKind.UNSPECIFIED ) {
                return ""; //$NON-NLS-1$
            }
            return mult.toString();
        }
        
        throw new IllegalArgumentException(RelationalPlugin.Util.getString("KeyAspect.Invalid_end")); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setMultiplicity(java.lang.Object, int, java.lang.String)
     */
    public IStatus setMultiplicity(Object assoc, int end, String mult) {
        final ForeignKey key = assertForeignKey(assoc);
        if(end == 0){
            key.setForeignKeyMultiplicity(MultiplicityKind.get(mult));
            return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
        }else if(end == 1){
            key.setPrimaryKeyMultiplicity(MultiplicityKind.get(mult));
            return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
        }
        
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("KeyAspect.Invalid_end"), null); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#getProperties(java.lang.Object, int)
     */
    public String[] getProperties(Object assoc, int end) {
        return CoreStringUtil.Constants.EMPTY_STRING_ARRAY;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlAssociation#setProperties(java.lang.Object, int, java.lang.String)
     */
    public IStatus setProperties(Object assoc, int end, String[] props) {
        throw new UnsupportedOperationException(); 
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

    protected ForeignKey assertForeignKey(Object eObject) {
        CoreArgCheck.isInstanceOf(ForeignKey.class, eObject);

        return (ForeignKey)eObject;
    }
}
