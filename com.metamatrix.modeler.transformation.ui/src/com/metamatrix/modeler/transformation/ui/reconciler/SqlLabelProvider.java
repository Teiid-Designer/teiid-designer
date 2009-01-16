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

package com.metamatrix.modeler.transformation.ui.reconciler;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.query.sql.symbol.AggregateSymbol;
import com.metamatrix.query.sql.symbol.AliasSymbol;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.ExpressionSymbol;
import com.metamatrix.query.sql.symbol.Function;
import com.metamatrix.query.sql.symbol.SingleElementSymbol;

/**
 * Label provider for the SqlSymbolList - the provided Objects are SingleElementSymbols.
 * ElementSymbols or ExpressionSymbols...
 * 
 * @see org.eclipse.jface.viewers.LabelProvider 
 */
public class SqlLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider, PluginConstants.Images {

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = PluginConstants.EMPTY_STRING;
        if(columnIndex==0) {
            if(element!=null) {
                // Alias Symbol
                if(element instanceof AliasSymbol) {
                    AliasSymbol aSymbol = (AliasSymbol)element;
                    SingleElementSymbol uSymbol = aSymbol.getSymbol();
                    String symName = TransformationSqlHelper.getSingleElementSymbolShortName(uSymbol,true);
                    result = symName + " AS "+aSymbol.getShortName(); //$NON-NLS-1$
                // SingleElementSymbol
                } else if(element instanceof SingleElementSymbol) {
                    result = TransformationSqlHelper.getSingleElementSymbolShortName((SingleElementSymbol)element,true);
                }
            }
        }
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
        Image image = null;
        if(columnIndex==0) {
            if(element instanceof ExpressionSymbol) {
                image = UiPlugin.getDefault().getImage(FUNCTION_ICON);
            } else if(element instanceof SingleElementSymbol) {
                // Defect 23945 - added private method to get image for multiple types
                // of SQL symbols
                image = getImageForSymbol((SingleElementSymbol)element);
            }
        }
		return image;
	}
    
    /**
     *  Get the Image for the SingleElementSymbol
     */
    private Image getImageForSymbol(SingleElementSymbol seSymbol) {
        Image result = null;
        
        // If symbol is AliasSymbol, get underlying symbol
        if( seSymbol!=null && seSymbol instanceof AliasSymbol ) {
            seSymbol = ((AliasSymbol)seSymbol).getSymbol();
        }
        // ElementSymbol
        if ( (seSymbol instanceof ElementSymbol) ) {
            result = UiPlugin.getDefault().getImage(SYMBOL_ICON);
        // AggregateSymbol
        } else if ( seSymbol instanceof AggregateSymbol ) {
            result = UiPlugin.getDefault().getImage(FUNCTION_ICON);
        // ExpressionSymbol
        } else if ( seSymbol instanceof ExpressionSymbol ) {
            Expression expression = ((ExpressionSymbol)seSymbol).getExpression();
            if(expression!=null && expression instanceof Constant) {
                result = UiPlugin.getDefault().getImage(CONSTANT_ICON);
            } else if ( expression!=null && expression instanceof Function ) {
                result = UiPlugin.getDefault().getImage(FUNCTION_ICON);
            }
        }
        // Undefined
        if(result==null) {
            result = UiPlugin.getDefault().getImage(UNDEFINED_ICON);
        }
        
        return result; 
    }

}
