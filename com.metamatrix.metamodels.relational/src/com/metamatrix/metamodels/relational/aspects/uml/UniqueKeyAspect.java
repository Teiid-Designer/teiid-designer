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

package com.metamatrix.metamodels.relational.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalMetamodelConstants;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.UniqueConstraint;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlAssociation;

/**
 * KeyAspect
 */
public abstract class UniqueKeyAspect extends RelationalEntityAspect implements UmlAssociation {
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
        return StringUtil.Constants.EMPTY_STRING_ARRAY; 
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
                throw new MetaMatrixRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
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
        throw new UnsupportedOperationException(); 
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

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
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
        ArgCheck.isInstanceOf(UniqueKey.class, eObject);
    
        return (UniqueKey)eObject;
    }

}
