/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.criteria;


import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.ui.builder.util.CriteriaStrategy;

/**
 * QueryCriteriaStrategy
 */
public class QueryCriteriaStrategy extends CriteriaStrategy {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public QueryCriteriaStrategy() {
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.util.ICriteriaStrategy#getNode(org.teiid.query.sql.LanguageObject)
     */
    @Override
    public Object getNode(LanguageObject theLangObj) {
        Object result = null;

        if (isValid(theLangObj)) {
        
            if (theLangObj instanceof ElementSymbol) {
                result = theLangObj;
            }
        
        }

        return result;
    }
    
    /**
     * Uses the TreeViewer's ITreeContentProvider and ILabelProvider to build a full name.
     * @see CriteriaStrategy#getRuntimeFullName(Object)
     */
    @Override
    public String getRuntimeFullName(Object theNode) {
        checkState();

        String result = labelProvider.getText(theNode);
        if(theNode instanceof InputParameter) {
            result = "InputSet" + getDelimiterChar() + result; //$NON-NLS-1$
        } else {
        	//If ElementSymbol, labelProvider is already returning the entire String, so
        	//do not recurse.  BWP 11/24/03
        	if (!(theNode instanceof ElementSymbol)) {
        		while (true) {
            		Object parent = contentProvider.getParent(theNode);
                	if ( parent == null ) {
                    	break;
                	}
                	result = labelProvider.getText(parent) + getDelimiterChar() + 
                			result;
                	theNode = parent;
                } 
            }
        }
        return result;
    }
    
    /**
     * Checks to see if the strategy is in a complete state.
     * @throws IllegalStateException if the viewer, content provider, or label provider is null
     */
    private void checkState() {
        if ((viewer == null) || (labelProvider == null) || (contentProvider == null)) {
            throw new IllegalStateException(UiConstants.Util.getString("CriteriaStrategy.invalidStateMsg")); //$NON-NLS-1$);
        }
    }

}
