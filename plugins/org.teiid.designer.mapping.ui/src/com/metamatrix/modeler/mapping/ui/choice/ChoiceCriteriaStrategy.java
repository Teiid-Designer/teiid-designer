/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.choice;


import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassObject;
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
public class ChoiceCriteriaStrategy 
     extends CriteriaStrategy
  implements PluginConstants,
             UiConstants {

         
    public static final String EMPTY_STRING     = "";           //$NON-NLS-1$

    // =======================================================
    // CONSTRUCTORS
    // =======================================================
    
    public ChoiceCriteriaStrategy() {
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
    public String getRuntimeFullName(Object theObject) {
        String result = EMPTY_STRING;

        if ((theObject instanceof MappingClassColumn) || (theObject instanceof MappingClass)) {
            result = TransformationHelper.getSqlEObjectFullName((MappingClassObject)theObject);
        }

        return result;
    }
    
}
