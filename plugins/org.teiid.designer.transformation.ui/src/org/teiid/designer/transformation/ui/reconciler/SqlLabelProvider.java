/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.reconciler;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.transformation.util.TransformationSqlHelper;

/**
 * Label provider for the SqlSymbolList - the provided Objects are SingleElementSymbols.
 * ElementSymbols or ExpressionSymbols...
 * 
 * @see org.eclipse.jface.viewers.LabelProvider 
 *
 * @since 8.0
 */
public class SqlLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider, PluginConstants.Images {

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		String result = PluginConstants.EMPTY_STRING;
        if(columnIndex==0) {
            if(element!=null) {
                // Alias Symbol
                if(element instanceof IAliasSymbol) {
                    IAliasSymbol aSymbol = (IAliasSymbol)element;
                    IExpression uSymbol = aSymbol.getSymbol();
                    String symName = TransformationSqlHelper.getSingleElementSymbolShortName(uSymbol,true);
                    result = symName + " AS " + aSymbol.getShortName(); //$NON-NLS-1$
                // SingleElementSymbol
                } else if(element instanceof ILanguageObject && ((ILanguageObject) element).isExpression()) {
                    result = TransformationSqlHelper.getSingleElementSymbolShortName((IExpression)element,true);
                }
            }
        }
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
        Image image = null;
        if(columnIndex==0) {
            if(element instanceof IExpressionSymbol) {
                image = UiPlugin.getDefault().getImage(FUNCTION_ICON);
            } else if(element instanceof ILanguageObject && ((ILanguageObject) element).isExpression()) {
                // Defect 23945 - added private method to get image for multiple types
                // of SQL symbols
                image = getImageForSymbol((IExpression)element);
            }
        }
		return image;
	}
    
    /**
     *  Get the Image for the SingleElementSymbol
     */
    private Image getImageForSymbol(IExpression seSymbol) {
        Image result = null;
        
        // If symbol is AliasSymbol, get underlying symbol
        if( seSymbol!=null && seSymbol instanceof IAliasSymbol ) {
            seSymbol = ((IAliasSymbol)seSymbol).getSymbol();
        }
        // ElementSymbol
        if ( (seSymbol instanceof IElementSymbol) ) {
            result = UiPlugin.getDefault().getImage(SYMBOL_ICON);
        // AggregateSymbol
        } else if ( seSymbol instanceof IAggregateSymbol ) {
            result = UiPlugin.getDefault().getImage(FUNCTION_ICON);
        // ExpressionSymbol
        } else if ( seSymbol instanceof IExpressionSymbol ) {
            IExpression expression = ((IExpressionSymbol)seSymbol).getExpression();
            if(expression!=null && expression instanceof IConstant) {
                result = UiPlugin.getDefault().getImage(CONSTANT_ICON);
            } else if ( expression!=null && expression.isFunction() ) {
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
