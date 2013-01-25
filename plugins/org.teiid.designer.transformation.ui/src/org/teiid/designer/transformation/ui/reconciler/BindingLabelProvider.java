/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.reconciler;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;

/**
 * ModelOutlineLabelProvider is a specialization of ModelExplorerLabelProvider
 * that allows us to display or hide additional features of a model in the outline view.
 *
 * @since 8.0
 */
public class BindingLabelProvider extends ModelExplorerLabelProvider
    implements ITableLabelProvider, PluginConstants.Images {

    private boolean showDatatype = false;
    
    /**
     * Constructor.
     * @param showDatatypes flag whether to show datatypes as part of text string
     */
    public BindingLabelProvider(boolean showDatatype) {
        this.showDatatype = showDatatype;
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    @Override
	public String getColumnText(Object element, int columnIndex) {
        String result = PluginConstants.EMPTY_STRING;
        Binding binding = (Binding) element;
        switch (columnIndex) {
            case 0:  // Attribute Column
                result = binding.getAttributeText(showDatatype);
                break;
            case 1 : // SQL Symbol Column
                result = binding.getSqlSymbolText(showDatatype);
                break;
            default :
                break;  
        }
        return result;
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    @Override
	public Image getColumnImage(Object element, int columnIndex) {
            Image image = null;
        Binding binding = (Binding) element;
        switch (columnIndex) {
            case 0:  // Attribute Column
                Object attr = binding.getAttribute();
                if(attr!=null && attr instanceof EObject) {
                    image = super.getImage(attr);
                } 
                break;
            case 1 : // SQL Symbol Column
                Object sqlSymbol = binding.getCurrentSymbol();
                if(sqlSymbol instanceof IExpression) {
                    // Defect 23945 - added private method to get image for multiple types
                    // of SQL symbols
                    image = getImageForSymbol((IExpression)sqlSymbol);
                }
                break;
            default :
                break;  
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
            } else if ( expression!=null && expression instanceof IFunction ) {
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
