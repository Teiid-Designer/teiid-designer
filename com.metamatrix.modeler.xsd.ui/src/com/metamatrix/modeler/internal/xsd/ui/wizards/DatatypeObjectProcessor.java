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

package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.xsd.ui.editor.FacetHelper;
import com.metamatrix.modeler.internal.xsd.ui.textimport.DatatypeAtomicRowObject;
import com.metamatrix.modeler.internal.xsd.ui.textimport.DatatypeEnumRowObject;
import com.metamatrix.modeler.internal.xsd.ui.textimport.DatatypeRowFactory;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractObjectProcessor;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractRowObject;
import com.metamatrix.modeler.tools.textimport.ui.wizards.IRowObject;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;


/**
DTYPE,Name,Description,BaseType,Length,MinLength,MaxLength,MinBound,MinInclusive,MaxBound,MaxInclusive,TotalDigits,Fraction Digits 
*/

/** 
 * @since 4.2
 */
public class DatatypeObjectProcessor extends AbstractObjectProcessor {
    //============================================================================================================================
    // Static Constants
    private static final String I18N_PREFIX             = "DatatypeObjectProcessor"; //$NON-NLS-1$
    private static final String SEPARATOR               = "."; //$NON-NLS-1$
    
    // dialog store id constants
    public static final int SDT_ATOMIC = 15;
    public static final int SDT_ENUM = 16;
    
    //============================================================================================================================
    // Static Methods
    
    //
    // Instance variables:
    //
    private IProgressMonitor monitor;
    private Map nameToTypeMap = new HashMap();

    /** 
     * 
     * @since 4.2
     */
    public DatatypeObjectProcessor() {
        super();
    }
    
    
    public Collection createRowObjsFromStrings(Collection rowStrings) {
        Iterator iter = rowStrings.iterator();
        String nextStr = null;
        
        DatatypeRowFactory factory = new DatatypeRowFactory();
        
        Collection stringRows = new ArrayList();
        IRowObject nextRow = null;
        while( iter.hasNext() ) {
            nextStr = (String)iter.next();
            nextRow = factory.createRowObject(nextStr);
            if( nextRow != null && nextRow.isValid()) {
            	stringRows.add(nextRow);
            } else {
            	logParsingError(nextStr);
            }
        }
        return stringRows;
    }

    public void generateObjsFromRowObjs(XSDSchema location, Collection rowObjects) {
        Iterator iter = rowObjects.iterator();
        AbstractRowObject nextRow = null;
        XSDSimpleTypeDefinition lastType = null;
        int iRow = 0;
        
        String sSize = Integer.toString(rowObjects.size());
        
        while( iter.hasNext() ) {
            nextRow = (AbstractRowObject)iter.next();
            
        	iRow++;
        	if( monitor != null ) {
        		monitor.worked(1);
				monitor.subTask(ModelerXsdUiConstants.Util.getString(I18N_PREFIX + SEPARATOR + "incrementalProgress", Integer.toString(iRow), sSize, nextRow.getName())); //$NON-NLS-1$
        	}
            
            if( nextRow.getObjectType() == SDT_ATOMIC ) {
                lastType = createDatatypeAtomic(location, (DatatypeAtomicRowObject)nextRow);
            } else if( nextRow.getObjectType() == SDT_ENUM ) {
                if (lastType != null) {
                    createDatatypeEnum(lastType, (DatatypeEnumRowObject) nextRow);
                } // endif
            } // endif -- row type
            
            if( monitor.isCanceled() ) {
            	break;
            }
        } // endwhile
    }
    
    private XSDSimpleTypeDefinition createDatatypeAtomic(XSDSchema location, DatatypeAtomicRowObject rowObject) {
        
        // Get all of the properties from the Row Object
        
        String name = rowObject.getName();
        
        String description = rowObject.getDescription();
        String baseType = rowObject.getBaseType();
        
        int length         = rowObject.getLength();
        int minLength      = rowObject.getMinLength();
        int maxLength      = rowObject.getMaxLength();
        int minBound       = rowObject.getMinBound();
        int minInclusive   = rowObject.getMinInclusive();
        int maxBound       = rowObject.getMaxBound();
        int maxInclusive   = rowObject.getMaxInclusive();
        int totalDigits    = rowObject.getTotalDigits();
        int fractionDigits = rowObject.getFractionDigits();

        // Create the Datatype using the RowObject properties

        XSDSimpleTypeDefinition base = null;
        if (baseType != null
         && baseType.length() > 0) {
            // base type specified:
            try {
                base = (XSDSimpleTypeDefinition) ModelerCore.getWorkspaceDatatypeManager().findDatatype(baseType);
            } catch (ModelerCoreException err) {
                Util.log(err);
            } // endtry

            // see if we need to look in import file:
            if (base == null) {
                // if we read it previously, we can pull it out here:
                base = (XSDSimpleTypeDefinition) nameToTypeMap.get(baseType);
            } // endif
        } // endif

        // create and describe:
        XSDSimpleTypeDefinition asd = FacetHelper.createAtomicSimpleTypeDefinintion(location, name, base);
        nameToTypeMap.put(name, asd);
        if (description != null
         && description.length() > 0) {
            ModelObjectUtilities.setDescription(asd, description, this);
        } // endif

        if (length > 0) {
            XSDConstrainingFacet xcf = FacetHelper.createFacet(FacetHelper.FACET_LENGTH);
            xcf.setLexicalValue(Integer.toString(length));
            addValue(asd, xcf, asd.getFacetContents());
        } // endif

        if (minLength > 0) {
            XSDConstrainingFacet xcf = FacetHelper.createFacet(FacetHelper.FACET_MINLENGTH);
            xcf.setLexicalValue(Integer.toString(minLength));
            addValue(asd, xcf, asd.getFacetContents());
        } // endif

        if (maxLength > 0) {
            XSDConstrainingFacet xcf = FacetHelper.createFacet(FacetHelper.FACET_MAXLENGTH);
            xcf.setLexicalValue(Integer.toString(maxLength));
            addValue(asd, xcf, asd.getFacetContents());
        } // endif

        if (minBound > 0) {
            XSDConstrainingFacet xcf = FacetHelper.createFacet(FacetHelper.FACET_MIN_EXCLUSIVE);
            xcf.setLexicalValue(Integer.toString(minBound));
            addValue(asd, xcf, asd.getFacetContents());
        } // endif

        if (minInclusive > 0) {
            XSDConstrainingFacet xcf = FacetHelper.createFacet(FacetHelper.FACET_MIN_INCLUSIVE);
            xcf.setLexicalValue(Integer.toString(minInclusive));
            addValue(asd, xcf, asd.getFacetContents());
        } // endif

        if (maxBound > 0) {
            XSDConstrainingFacet xcf = FacetHelper.createFacet(FacetHelper.FACET_MAX_EXCLUSIVE);
            xcf.setLexicalValue(Integer.toString(maxBound));
            addValue(asd, xcf, asd.getFacetContents());
        } // endif

        if (maxInclusive > 0) {
            XSDConstrainingFacet xcf = FacetHelper.createFacet(FacetHelper.FACET_MAX_INCLUSIVE);
            xcf.setLexicalValue(Integer.toString(maxInclusive));
            addValue(asd, xcf, asd.getFacetContents());
        } // endif

        if (totalDigits > 0) {
            XSDConstrainingFacet xcf = FacetHelper.createFacet(FacetHelper.FACET_TOTALDIGITS);
            xcf.setLexicalValue(Integer.toString(totalDigits));
            addValue(asd, xcf, asd.getFacetContents());
        } // endif

        if (fractionDigits > 0) {
            XSDConstrainingFacet xcf = FacetHelper.createFacet(FacetHelper.FACET_FRACTIONDIGITS);
            xcf.setLexicalValue(Integer.toString(fractionDigits));
            addValue(asd, xcf, asd.getFacetContents());
        } // endif
        
        return asd;
    }
    
    private void createDatatypeEnum(XSDSimpleTypeDefinition enumTarget, DatatypeEnumRowObject rowObject) {
        // create/add:
        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet) FacetHelper.createFacet(FacetHelper.FACET_ENUMERATION);
        addValue(enumTarget, enumFacet, enumTarget.getFacetContents());

        // set name/desc:
        String name = rowObject.getName();
        String description = rowObject.getDescription();

        enumFacet.setLexicalValue(name);
        if (description != null
         && description.length() > 0) {
            ModelObjectUtilities.setDescription(enumFacet, description, this);
        } // endif

    }

	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
}
