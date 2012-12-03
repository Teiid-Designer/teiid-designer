/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.choice;


import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.mapping.ui.PluginConstants;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.metamodels.transformation.MappingClassObject;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.query.ui.builder.util.CriteriaStrategy;


/**
 * ChoiceCriteriaStrategy
 *
 * @since 8.0
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
     * @see org.teiid.query.ui.builder.util.ICriteriaStrategy#getNode(org.teiid.query.sql.LanguageObject)
     */
    @Override
    public Object getNode( ILanguageObject theLangObj ) {
        Object result = null;

        if (isValid(theLangObj)) {
        
            if (theLangObj instanceof IElementSymbol) {
                Object obj = ((IElementSymbol)theLangObj).getMetadataID();

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
