/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ProcedureParameter;



/** 
 * @since 8.0
 */
public abstract class DatatypeUtilities {
    private static final String COLON = ":"; //$NON-NLS-1$
    private static final String RIGHT_PARENTH = ")"; //$NON-NLS-1$
    private static final String LEFT_PARENTH = "("; //$NON-NLS-1$
    
    public static boolean setColumnSignature(EObject eObject, String newSignature) throws ModelerCoreException {
        MetamodelAspect mmAspect = ModelObjectUtilities.getSqlAspect(eObject);
        if( mmAspect instanceof SqlColumnAspect ) {
            // Now we parse the name
            int index = newSignature.lastIndexOf(COLON);
            boolean canSetLength = ((SqlColumnAspect)mmAspect).canSetLength();
            if( index > 0 ) {
                SqlColumnAspect sqAspect = (SqlColumnAspect)mmAspect;
                String newName = newSignature.substring(0, index).trim();
                index++;
                String fullDatatype = newSignature.substring(index).trim();
                if( fullDatatype.length() > 0 && fullDatatype.toUpperCase().startsWith("STRING(")) { //$NON-NLS-1$

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
                } else {
                    EObject dType = getDatatype(fullDatatype);
                    if( dType != null ) {
                        sqAspect.setDatatype(eObject,dType);
                    } else {
                    	newName += COLON + fullDatatype;
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
    
    public static String getRuntimeTypeName(final String datatype) throws ModelerCoreException {
    	String dType = datatype.toLowerCase();
    	if( dType.equalsIgnoreCase(DatatypeConstants.BuiltInNames.INT)) {
    		dType = DatatypeConstants.BuiltInNames.INTEGER.toLowerCase();
    	}
    	EObject theDataType = getDatatype(dType);
    	if( theDataType == null ) {
    		return datatype;
    	}
    	
    	return ModelerCore.getWorkspaceDatatypeManager().getRuntimeTypeName(theDataType);
    	
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
    
    public static EObject getSqlParameterDatatype(EObject eObject) {
        MetamodelAspect mmAspect = ModelObjectUtilities.getSqlAspect(eObject);
        if( mmAspect instanceof SqlProcedureParameterAspect ) {
        	SqlProcedureParameterAspect sqAspect = (SqlProcedureParameterAspect)mmAspect;
            return sqAspect.getDatatype(eObject);
        }
        return null;
    }
    
    public static EObject getSqlDatatype(EObject eObject) {
        if( eObject instanceof Column ) {
        	return getSqlColumnDatatype(eObject);
        } else if( eObject instanceof ProcedureParameter ) {
        	return getSqlParameterDatatype(eObject);
        }
        return null;
    }
    
    public static boolean canSetLength(EObject eObject, int length) {
    	EObject datatype = getSqlDatatype(eObject);
    	if( datatype != null ) {
    		String typeName = ModelerCore.getWorkspaceDatatypeManager().getRuntimeTypeName(datatype);
    		if( typeName != null && typeName.equals(DatatypeConstants.BuiltInNames.CHAR) ) {
    	    	if( length == 1 ) {
    	    		return true;
    	    	}       
    			return false;
    		}
    	}
    	
    	return true;
    }
    
	/**
	 * Utility method to return the name of the design-time datatype minus the namespace if it exists
	 * @return design time datatype name
	 */
	public static String getDesignTimeTypeName(EObject eObject) {
		String rawName = ModelUtilities.getEMFLabelProvider().getText(eObject);
		int colonIndex = rawName.indexOf(':');
		
		if( colonIndex == -1 ) return rawName;
		
		return rawName.substring(0, colonIndex).trim();
	}
	
	public static Collection<String> getAllDesignTimeTypeNames() throws ModelerCoreException {
		EObject[] types = ModelerCore.getWorkspaceDatatypeManager().getBuiltInDatatypes();
		Collection<String> names = new ArrayList<String>();
		for( EObject type : types ) {
			String name = getDesignTimeTypeName(type);
			if( name != null && !name.equalsIgnoreCase("integer")) {
				names.add(name);
			}
		}
		
		return names;
	}
	
	/**
	 * Helper method to identify if a datatype is numeric or not.
	 * The initial use-case is needing to define a default precision since a 0 precision
	 * will result in a validation warning
	 * 
	 * @param type the datatype
	 * @return true if datataype is a numeric type
	 */
	public static boolean isNumeric(String type) {
		if( DatatypeConstants.BuiltInNames.BIG_DECIMAL.equalsIgnoreCase(type) ||
			DatatypeConstants.BuiltInNames.BIG_INTEGER.equalsIgnoreCase(type) ||
			DatatypeConstants.BuiltInNames.FLOAT.equalsIgnoreCase(type) ||
			DatatypeConstants.BuiltInNames.LONG.equalsIgnoreCase(type) ||
			DatatypeConstants.BuiltInNames.SHORT.equalsIgnoreCase(type) ||
			DatatypeConstants.BuiltInNames.DOUBLE.equalsIgnoreCase(type) ||
			DatatypeConstants.BuiltInNames.INTEGER.equalsIgnoreCase(type) ||
			DatatypeConstants.BuiltInNames.INT.equalsIgnoreCase(type) ||
			DatatypeConstants.BuiltInNames.DECIMAL.equalsIgnoreCase(type) ) {
			return true;
		}
		
		return false;
	}
}
