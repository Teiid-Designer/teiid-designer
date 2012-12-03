/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.util;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IPredicateCriteria;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.query.ui.UiConstants;
import org.teiid.query.ui.UiPlugin;

/**
 * LanguageObjectLabelProvider
 *
 * @since 8.0
 */
public class LanguageObjectLabelProvider extends LabelProvider implements UiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** The image used when rendering a {@link CompoundCriteria} node. */
    private static final Image COMPOUND_CRITERIA_IMAGE;

    /** The image used when rendering a {@link Constant} node. */
    private static final Image CONSTANT_IMAGE;

    /** The image used when rendering a {@link Function} node. */
    private static final Image FUNCTION_IMAGE;

    /** The image used when rendering a {@link PredicateCriteria} node. */
    private static final Image PREDICATE_IMAGE;

    /** The image used when rendering a {@link Reference} node. */
    private static final Image REFERENCE_IMAGE;

    /** The image used when rendering an undefined node. */
    private static final Image UNDEFINED_IMAGE;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    // /////////////////////////////////////////////////////////////////////////////////////////////

    static {
        UiPlugin plugin = UiPlugin.getDefault();

        COMPOUND_CRITERIA_IMAGE = plugin.getImage(Images.COMPOUND_CRITERIA_LANG_OBJ);
        CONSTANT_IMAGE = plugin.getImage(Images.CONSTANT_LANG_OBJ);
        FUNCTION_IMAGE = plugin.getImage(Images.FUNCTION_LANG_OBJ);
        PREDICATE_IMAGE = plugin.getImage(Images.PREDICATE_LANG_OBJ);
        REFERENCE_IMAGE = plugin.getImage(Images.REFERENCE_LANG_OBJ);
        UNDEFINED_IMAGE = plugin.getImage(Images.UNDEFINED_LANG_OBJ);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( Object theElement ) {
        Image result = null;

        if (theElement instanceof IConstant) {
            result = CONSTANT_IMAGE;
        } else if (theElement instanceof IFunction) {
            result = FUNCTION_IMAGE;
        } else if (theElement instanceof IPredicateCriteria) {
            result = PREDICATE_IMAGE;
        } else if (theElement instanceof ICompoundCriteria) {
            result = COMPOUND_CRITERIA_IMAGE;
        } else if (theElement instanceof INotCriteria) {
            result = getNotCriteriaIcon((INotCriteria)theElement);
        } else if (theElement instanceof IReference) {
            result = REFERENCE_IMAGE;
        } else {
            result = UNDEFINED_IMAGE;
        }

        return result;
    }

    /**
     * Gets the appropriate icon to display with a <code>NotCriteria</code>. The {@link Criteria} contained in the
     * <code>NotCriteria</code> determines the icon.
     * 
     * @param theCriteria the not criteria being rendered
     * @return the appropriate icon
     */
    private Image getNotCriteriaIcon( INotCriteria theCriteria ) {
        Image result = null;
        ICriteria crit = theCriteria.getCriteria();

        if (crit instanceof IPredicateCriteria) {
            result = PREDICATE_IMAGE;
        }
        if (crit instanceof ICompoundCriteria) {
            result = COMPOUND_CRITERIA_IMAGE;
        } else if (crit instanceof INotCriteria) {
            result = getNotCriteriaIcon((INotCriteria)crit);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( Object theElement ) {
        String result = null;

        if (theElement == null) {
            result = ISQLStringVisitor.UNDEFINED;
        } else if (theElement instanceof ILanguageObject) {
            IQueryService queryService = ModelerCore.getTeiidQueryService();
            ISQLStringVisitor visitor = queryService.getSQLStringVisitor();
            result = visitor.getSQLString((ILanguageObject)theElement);
        } else {
            result = super.getText(theElement);
        }

        return result;
    }

}
