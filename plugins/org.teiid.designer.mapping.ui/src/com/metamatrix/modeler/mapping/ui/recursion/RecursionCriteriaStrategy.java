/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.recursion;


import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.query.internal.ui.builder.util.CriteriaStrategy;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.symbol.ElementSymbol;


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
     * @see com.metamatrix.query.internal.ui.builder.util.ICriteriaStrategy#getNode(com.metamatrix.query.sql.LanguageObject)
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
