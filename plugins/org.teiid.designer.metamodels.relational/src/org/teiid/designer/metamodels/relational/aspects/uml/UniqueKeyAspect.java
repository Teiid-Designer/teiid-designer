/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.metamodel.aspect.uml.UmlAssociation;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.RelationalMetamodelConstants;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.UniqueKey;


/**
 * KeyAspect
 *
 * @since 8.0
 */
public abstract class UniqueKeyAspect extends RelationalEntityAspect implements UmlAssociation {
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlAssociation#getAggregation(java.lang.Object, int)
     */
    @Override
	public int getAggregation(Object assoc, int end) {
        return AGGREGATION_NONE;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlAssociation#getProperties(java.lang.Object, int)
     */
    @Override
	public String[] getProperties(Object assoc, int end) {
        return CoreStringUtil.Constants.EMPTY_STRING_ARRAY; 
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlAssociation#getNavigability(java.lang.Object, int)
     */
    @Override
	public int getNavigability(Object assoc, int end) {
        //final UniqueKey key = assertUniqueKey(assoc);
        if(end == 1){
            return NAVIGABILITY_NAVIGABLE;
        }else if (end == 0){
            return NAVIGABILITY_NONE;
        }
        
        return NAVIGABILITY_UNKNOWN;
    }

    @Override
	public String getMultiplicity(Object eObject, int end) {
        final UniqueKey key = assertUniqueKey(eObject);
        if (key instanceof PrimaryKey) {
            if(end == 0){
                return("0..1"); //$NON-NLS-1$
            }else if(end == 1){
                return("0..1"); //$NON-NLS-1$
            }
        } else if (key instanceof UniqueConstraint) {
            if(end == 0){
                return("0..1"); //$NON-NLS-1$
            }else if(end == 1){
                return("0..*"); //$NON-NLS-1$
            }
        }
        
        return(""); //$NON-NLS-1$
    }

    @Override
	public String getSignature(Object eObject, int showMask) {
        final UniqueKey key = assertUniqueKey(eObject);
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

    @Override
	public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlAssociation.SIGNATURE_NAME);
    }
 
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlAssociation#setRoleName(java.lang.Object, int, java.lang.String)
     */
    @Override
	public IStatus setRoleName(Object assoc, int end, String name) {
        throw new UnsupportedOperationException(); 
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlAssociation#setMultiplicity(java.lang.Object, int, java.lang.String)
     */
    @Override
	public IStatus setMultiplicity(Object assoc, int end, String mult) {
        throw new UnsupportedOperationException(); 
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlAssociation#setProperties(java.lang.Object, int, java.lang.String)
     */
    @Override
	public IStatus setProperties(Object assoc, int end, String[] props) {
        throw new UnsupportedOperationException(); 
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlAssociation#setNavigability(java.lang.Object, int, int)
     */
    @Override
	public IStatus setNavigability(Object assoc, int end, int navigability) {
        throw new UnsupportedOperationException(); 
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature(Object eObject, String newSignature) {
        try {
            final UniqueKey key = assertUniqueKey(eObject);
            key.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, RelationalMetamodelConstants.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
    }
   

    protected UniqueKey assertUniqueKey(Object eObject) {
        CoreArgCheck.isInstanceOf(UniqueKey.class, eObject);
    
        return (UniqueKey)eObject;
    }

}
