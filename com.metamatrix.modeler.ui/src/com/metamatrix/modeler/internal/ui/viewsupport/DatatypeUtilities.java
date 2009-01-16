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

package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;


/** 
 * @since 4.3
 */
public abstract class DatatypeUtilities {
    private static final String COLON = ":"; //$NON-NLS-1$
    private static final String RIGHT_PARENTH = ")"; //$NON-NLS-1$
    private static final String LEFT_PARENTH = "("; //$NON-NLS-1$
    
    public static boolean setColumnSignature(EObject eObject, String newSignature) throws ModelerCoreException {
        MetamodelAspect mmAspect = ModelObjectUtilities.getSqlAspect(eObject);
        if( mmAspect instanceof SqlColumnAspect ) {
            // Now we parse the name
            int index = newSignature.indexOf(COLON);
            boolean canSetLength = ((SqlColumnAspect)mmAspect).canSetLength();
            if( index > 0 ) {
                SqlColumnAspect sqAspect = (SqlColumnAspect)mmAspect;
                String newName = newSignature.substring(0, index).trim();
                index++;
                String fullDatatype = newSignature.substring(index);
                if( fullDatatype.length() > 0 ) {

                    String dTypeString = null;
                    
                    // Check to see if length is included
                    int indexLeft = fullDatatype.indexOf(LEFT_PARENTH);
                    int indexRight = fullDatatype.indexOf(RIGHT_PARENTH);
                    if( indexLeft > 0 )
                        dTypeString = fullDatatype.substring(0, indexLeft).trim();
                    else
                        dTypeString = fullDatatype.trim();
                    
                    if( indexRight > 0 && canSetLength) {   
                        String lengthString = fullDatatype.substring(indexLeft+1, indexRight).trim();
                        int length = -1;
                        try {
                            length = Integer.parseInt(lengthString);
                        } catch (NumberFormatException nfe ) {
                            
                        }
                        if( length >= 0 )
                            sqAspect.setLength(eObject, length);
                    }
                    // --------------------
                    // Defect 22275 - Needed to do one more check to see if the aspect supports setting datatype.
                    // XML Document attributes do have a sqlColumnAspect but don't support datatypes
                    // --------------------
                    if( sqAspect.canSetDatatype() && dTypeString != null && dTypeString.length() > 0 ) {
                        EObject dType = getDatatype(dTypeString);
                        if( dType != null ) {
                            sqAspect.setDatatype(eObject,dType);
                        }
                    }
                }
                
                ModelerCore.getModelEditor().rename(eObject, newName);
                return true;
            }
            
            String newName = newSignature.trim();
            ModelerCore.getModelEditor().rename(eObject, newName);
            return true;
        } 
        return false;
    }
    
    public static boolean setParameterSignature(EObject eObject, String newSignature) throws ModelerCoreException {
        MetamodelAspect mmAspect = ModelObjectUtilities.getSqlAspect(eObject);
        if( mmAspect instanceof SqlProcedureParameterAspect ) {
            // Now we parse the name
            int index = newSignature.indexOf(COLON);
            boolean canSetLength = ((SqlProcedureParameterAspect)mmAspect).canSetLength();
            if( index > 0 ) { 
                SqlProcedureParameterAspect sppAspect = (SqlProcedureParameterAspect)mmAspect;
                String newName = newSignature.substring(0, index).trim();
                index++;
                String fullDatatype = newSignature.substring(index);
                if( fullDatatype.length() > 0 ) {

                    String dTypeString = null;
                    
                    // Check to see if length is included
                    int indexLeft = fullDatatype.indexOf(LEFT_PARENTH);
                    int indexRight = fullDatatype.indexOf(RIGHT_PARENTH);
                    if( indexLeft > 0 )
                        dTypeString = fullDatatype.substring(0, indexLeft).trim();
                    else
                        dTypeString = fullDatatype.trim();
                    
                    if( indexRight > 0 && canSetLength ) {   
                        String lengthString = fullDatatype.substring(indexLeft+1, indexRight).trim();
                        int length = -1;
                        try {
                            length = Integer.parseInt(lengthString);
                        } catch (NumberFormatException nfe ) {
                            
                        }
                        if( length >= 0 )
                            sppAspect.setLength(eObject, length);
                    }
                    // --------------------
                    // Defect 22275 - Needed to do one more check to see if the aspect supports setting datatype.
                    // XML Document attributes do have a sqlColumnAspect but don't support datatypes
                    // --------------------
                    if( sppAspect.canSetDatatype() && dTypeString != null && dTypeString.length() > 0 ) {
                        EObject dType = getDatatype(dTypeString);
                        if( dType != null ) {
                            sppAspect.setDatatype(eObject,dType);
                        }
                    }
                }
                
                ModelerCore.getModelEditor().rename(eObject, newName);
                return true;
            }
            
            String newName = newSignature.trim();
            ModelerCore.getModelEditor().rename(eObject, newName);
            return true;
        } 
        return false;
    }
    
    public static EObject getDatatype(String datatype) throws ModelerCoreException {
        Object[] result = ModelerCore.getWorkspaceDatatypeManager().getAllDatatypes();
        String dtName = null;
        for( int i=0; i<result.length; i++ ) {
            dtName = ModelerCore.getWorkspaceDatatypeManager().getName((EObject)result[i]);
            if( dtName != null && dtName.equals(datatype))
                return (EObject)result[i];
        }
        
        return null;
    }
    
    public static boolean renameSqlColumn(EObject newEObject, String newName) throws ModelerCoreException {
        MetamodelAspect mmAspect = ModelObjectUtilities.getSqlAspect(newEObject);
        if( mmAspect instanceof SqlColumnAspect ) {
            if( newName != null && newName.length() > 0 ) {
                DatatypeUtilities.setColumnSignature(newEObject, newName);
            }
            return true;
        }
        if( mmAspect instanceof SqlProcedureParameterAspect ) {
            if( newName != null && newName.length() > 0 ) {
                DatatypeUtilities.setParameterSignature(newEObject, newName);
            }
            return true;
        }
        return false;
    }
    
    public static EObject getSqlColumnDatatype(EObject eObject) {
        MetamodelAspect mmAspect = ModelObjectUtilities.getSqlAspect(eObject);
        if( mmAspect instanceof SqlColumnAspect ) {
            SqlColumnAspect sqAspect = (SqlColumnAspect)mmAspect;
            return sqAspect.getDatatype(eObject);
        }
        return null;
    }
}
