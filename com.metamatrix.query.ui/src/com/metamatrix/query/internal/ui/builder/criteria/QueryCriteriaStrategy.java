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

package com.metamatrix.query.internal.ui.builder.criteria;

import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.internal.ui.builder.util.CriteriaStrategy;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.symbol.ElementSymbol;

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
     * @see com.metamatrix.query.internal.ui.builder.util.ICriteriaStrategy#getNode(com.metamatrix.query.sql.LanguageObject)
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
     * @see com.metamatrix.toolbox.ui.query.builder.criteria.CriteriaStrategy#getRuntimeFullName(com.metamatrix.common.tree.TreeNode)
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
