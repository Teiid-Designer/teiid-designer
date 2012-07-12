/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.recursion;


import org.eclipse.emf.ecore.EObject;

import org.teiid.designer.mapping.ui.PluginConstants;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.ui.builder.util.CriteriaStrategy;


/**
 * ChoiceCriteriaStrategy
 */
public class RecursionCriteriaStrategy 
     extends CriteriaStrategy
  implements PluginConstants,
             UiConstants {

         
    public static final String EMPTY_STRING     = "";           //$NON-NLS-1$

    // =======================================================
    // CONSTRUCTORS
    // =======================================================
    
    public RecursionCriteriaStrategy() {
    }
    
    // =======================================================
    // METHODS
    // =======================================================
    
    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.util.ICriteriaStrategy#getNode(org.teiid.query.sql.LanguageObject)
     */
    @Override
    public Object getNode( LanguageObject theLangObj ) {
        Object result = null;

        if (isValid(theLangObj)) {
        
            if (theLangObj instanceof ElementSymbol) {
                Object obj = ((ElementSymbol)theLangObj).getMetadataID();

                if (obj != null) {
                   if (obj instanceof MetadataRecord) {
                        result = ((MetadataRecord)obj).getEObject();
                    } else if (obj instanceof EObject) {
                        result = obj;
                    }
                }
            }
        
        }

        return result;
    }

    @Override
    public boolean isValid( Object o ) {
        return super.isValid( o );   
    }

    @Override
    public String getRuntimeFullName( Object o ) {

//        System.out.println("[ChoiceCriteriaStrategy.getRuntimeFullName] TOP"); //$NON-NLS-1$
        if( !(o instanceof MappingClassColumn) ) {
//            System.out.println("[ChoiceCriteriaStrategy.getRuntimeFullName] NOT a MappingClassColumn: " + o.getClass().getName() ); //$NON-NLS-1$
            return EMPTY_STRING;
        }

        String sResult = EMPTY_STRING;  

        MappingClassColumn mccColumn = (MappingClassColumn)o;
        
        sResult = TransformationHelper.getSqlEObjectFullName( mccColumn );
                
        return sResult;
    }
    
}
