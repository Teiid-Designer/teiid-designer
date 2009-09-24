/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.util;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.CompoundCriteria;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.lang.NotCriteria;
import com.metamatrix.query.sql.lang.PredicateCriteria;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.Function;
import com.metamatrix.query.sql.symbol.Reference;
import com.metamatrix.query.sql.visitor.SQLStringVisitor;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;

/**
 * LanguageObjectLabelProvider
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

        if (theElement instanceof Constant) {
            result = CONSTANT_IMAGE;
        } else if (theElement instanceof Function) {
            result = FUNCTION_IMAGE;
        } else if (theElement instanceof PredicateCriteria) {
            result = PREDICATE_IMAGE;
        } else if (theElement instanceof CompoundCriteria) {
            result = COMPOUND_CRITERIA_IMAGE;
        } else if (theElement instanceof NotCriteria) {
            result = getNotCriteriaIcon((NotCriteria)theElement);
        } else if (theElement instanceof Reference) {
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
    private Image getNotCriteriaIcon( NotCriteria theCriteria ) {
        Image result = null;
        Criteria crit = theCriteria.getCriteria();

        if (crit instanceof PredicateCriteria) {
            result = PREDICATE_IMAGE;
        }
        if (crit instanceof CompoundCriteria) {
            result = COMPOUND_CRITERIA_IMAGE;
        } else if (crit instanceof NotCriteria) {
            result = getNotCriteriaIcon((NotCriteria)crit);
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
            result = BuilderUtils.UNDEFINED;
        } else if (theElement instanceof LanguageObject) {
            result = SQLStringVisitor.getSQLString((LanguageObject)theElement);
        } else {
            result = super.getText(theElement);
        }

        return result;
    }

}
