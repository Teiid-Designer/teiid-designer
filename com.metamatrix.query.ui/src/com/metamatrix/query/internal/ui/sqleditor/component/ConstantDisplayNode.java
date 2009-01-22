/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.ArrayList;

import com.metamatrix.common.log.LogManager;
import com.metamatrix.common.types.DataTypeManager;

import com.metamatrix.query.sql.symbol.Constant;

/**
 * The <code>ConstantDisplayNode</code> class is used to represent Constants.
 */
public class ConstantDisplayNode extends ExpressionDisplayNode {
    private static final String LOG_CONTEXT = "toolbox.query"; //$NON-NLS-1$
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////

    /**
     *   ConstantDisplayNode constructor
     *  @param parentNode the parent DisplayNode of this.
     *  @param constant the query language object used to construct this display node.
     */
    public ConstantDisplayNode(DisplayNode parentNode, Constant constant) {
        this.parentNode = parentNode;
        this.languageObject = constant;
        createDisplayNodeList();
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    public Object getValue() {
        Constant constant = (Constant)this.getLanguageObject();
        return constant.getValue();
    }

    /**
     *   Create the DisplayNode list for this type of DisplayNode.  This is a list of
     *  all the lowest level nodes for this DisplayNode.
     */
    private void createDisplayNodeList() {
        displayNodeList = new ArrayList();
//        int indent = this.getIndentLevel();

        Constant constant = (Constant)this.getLanguageObject();
        String str = null;
        if(constant.isNull()) {
        	str = NULL;
        } else {
			try {
	            Class type = constant.getType();
			    if(type.equals(DataTypeManager.DefaultDataClasses.STRING)) {
	                String strValue = (String) constant.getValue();
	                strValue = escapeStringValue(strValue);
	                str = "'"+strValue+"'"; //$NON-NLS-1$  //$NON-NLS-2$
			    } else if(Number.class.isAssignableFrom(type)) {
	                str = constant.getValue().toString();
	            } else if(type.equals(DataTypeManager.DefaultDataClasses.BOOLEAN)) {
	                str = constant.getValue().equals(Boolean.TRUE) ? "TRUE" : "FALSE"; //$NON-NLS-1$  //$NON-NLS-2$
				} else if(type.equals(DataTypeManager.DefaultDataClasses.TIMESTAMP)) {
	                str = "{ts'"+constant.getValue().toString()+"'}"; //$NON-NLS-1$  //$NON-NLS-2$
	            } else if(type.equals(DataTypeManager.DefaultDataClasses.TIME)) {
	                str = "{t'"+constant.getValue().toString()+"'}"; //$NON-NLS-1$  //$NON-NLS-2$
	            } else if(type.equals(DataTypeManager.DefaultDataClasses.DATE)) {
	                str = "{d'"+constant.getValue().toString()+"'}"; //$NON-NLS-1$  //$NON-NLS-2$
	            } else {
	                str = "'"+constant.getValue().toString()+"'"; //$NON-NLS-1$  //$NON-NLS-2$
				}
			} catch(Exception e) {
	            LogManager.logError( LOG_CONTEXT, "[ConstantDisplayNode.createDNList] exception" ); //$NON-NLS-1$
			}
        }
		
		if(str!=null) {
        	displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,str));
		}
		return;
    }

 	/**
 	 * Take a string literal and escape it as necessary.  By default, this converts ' to ''.
 	 * @param str String literal value (unquoted), never null
 	 * @return Escaped string literal value
 	 */
    private String escapeStringValue(String str) {
        int index = str.indexOf('\''); 
        if(index < 0) {
            return str;
        }
        int last = 0;
    	StringBuffer temp = new StringBuffer();        	
    	while(index >= 0) {
        	temp.append(str.substring(last, index));
    		temp.append("''"); //$NON-NLS-1$
    		last = index+1;
    		index = str.indexOf('\'', last); 
    	}
    	
    	if(last <= (str.length()-1)) {
    		temp.append(str.substring(last));    
    	}
    	
    	return temp.toString();
    }

}
