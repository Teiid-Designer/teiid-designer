/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.tree;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.From;
import com.metamatrix.query.sql.lang.JoinPredicate;
import com.metamatrix.query.sql.lang.Query;
import com.metamatrix.query.sql.lang.SetQuery;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.symbol.ExpressionSymbol;
import com.metamatrix.query.sql.symbol.GroupSymbol;
import com.metamatrix.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;


/** 
 * QueryTreeLabelProvider is the ILabelProvider for the QueryTreeViewer.  It handles labels
 * and icons for LanguageObjects in a Command.  It uses a delegate ILabelProvider for rendering
 * MetadataId objects.  This delegate may be supplied through the constructor, or if one is not
 * supplied it will be obtained from the static class ElementViewerFactory.
 * @since 4.2
 */
public class QueryTreeLabelProvider implements ILabelProvider, UiConstants {
    
    private static final Image SELECT_ICON;
    private static final Image FROM_ICON;
    private static final Image WHERE_ICON;
    private static final Image EXPRESSION_ICON;
    private static final Image UNION_ICON;
    private static final Image JOIN_ICON;
    
    private static final String WHERE = "WHERE"; //$NON-NLS-1$
    private static final String FROM = "FROM"; //$NON-NLS-1$
    private static final String UNION = "UNION"; //$NON-NLS-1$
    
    static {
        UiPlugin plugin = UiPlugin.getDefault();

        SELECT_ICON = plugin.getImage(Images.SELECT_ICON);
        FROM_ICON = plugin.getImage(Images.FROM_ICON);
        WHERE_ICON = plugin.getImage(Images.WHERE_ICON);
        EXPRESSION_ICON = plugin.getImage(Images.EXPRESSION_ICON);
        UNION_ICON = plugin.getImage(Images.UNION_ICON);
        JOIN_ICON = plugin.getImage(Images.JOIN_ICON);
    }
    
    private ILabelProvider delegate;
    
    /** 
     * @since 4.2
     */
    public QueryTreeLabelProvider() {
        this(ElementViewerFactory.getLabelProvider());
    }

    /** 
     * @since 4.2
     */
    public QueryTreeLabelProvider(ILabelProvider metadataLabelProvider) {
        super();
        if ( metadataLabelProvider != null ) {
            this.delegate = metadataLabelProvider;
        } else {
            this.delegate = ElementViewerFactory.getLabelProvider();
        }
    }

    /** 
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.2
     */
    public Image getImage(Object element) {
        
        if ( element instanceof LanguageObject ) {
            if ( element instanceof SetQuery ) {
                switch ( ((SetQuery) element).getOperation() ) {
                    case INTERSECT:
                        return null; //INTERSECT_ICON;
                    case EXCEPT:
                        return null; //EXCEPT_ICON;
                    case UNION:
                    default:
                        return UNION_ICON;
                }
            } else if ( element instanceof Query ) {
                return SELECT_ICON;
            } else if ( element instanceof Criteria ) {
                return WHERE_ICON;
            } else if ( element instanceof From ) {
                return FROM_ICON;
            } else if ( element instanceof JoinPredicate ) {
                return JOIN_ICON;
            } else if ( element instanceof SingleElementSymbol ) {
                if ( element instanceof ExpressionSymbol) {
                    return EXPRESSION_ICON;
                }
                return ( delegate == null ? null : delegate.getImage(((ElementSymbol) element).getMetadataID()) );
            } else if ( element instanceof GroupSymbol ) {
                Object id = ((GroupSymbol) element).getMetadataID();
                if ( id != null ) {
                    return ( delegate == null ? null : delegate.getImage(id) );
                }
        	}
            
            return null;
            
        }
        return ( delegate == null ? null : delegate.getImage(element) );
        
    }

    /** 
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.2
     */
    public String getText(Object element) {
        if ( element instanceof LanguageObject ) {
            if ( element instanceof SetQuery ) {
                return UNION;
            } else if ( element instanceof Query ) {
                return ((Query) element).getSelect().toString();
            } else if ( element instanceof From ) {
                return FROM;
            } else if ( element instanceof Criteria ) {
                return WHERE + ' ' + element.toString();
            }
            return element.toString(); 
        }
        return ( delegate == null ? null : delegate.getText(element) );
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     * @since 4.2
     */
    public void dispose() {
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void addListener(ILabelProviderListener listener) {
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    public boolean isLabelProperty(Object element,
                                   String property) {
        return false;
    }

    /** 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void removeListener(ILabelProviderListener listener) {
    }
    

}
