/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import com.metamatrix.core.util.Assertion;

/**
 * The <code>GridPanel</code> is a panel that only uses {@link org.eclipse.swt.layout.GridLayout}.
 * It is initially set to have no margins.
 */
public class GridPanel extends Composite {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Default margin width. */
    private static final int DEFAULT_MARGIN_WIDTH = 0;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a default <code>GridPanel</code> (no style and one column).
     * @param theParent the parent container
     */
    public GridPanel(Composite theParent) {
        this(theParent, SWT.NONE);
    }
    
    /**
     * Constructs a <code>GridPanel</code> with the specified style and one column.
     * @param theParent the parent container
     * @param theStyle the style
     */
    public GridPanel(Composite theParent,
                     int theStyle) {
        this(theParent, theStyle, 1);
    }
    
    /**
     * Constructs a <code>GridPanel</code> using the specified parameters.
     * @param theParent the parent container
     * @param theStyle the style
     * @param theColumnCount the number of columns
     */
    public GridPanel(Composite theParent,
                     int theStyle,
                     int theColumnCount) {
        super(theParent, theStyle);
        
        GridLayout layout = new GridLayout(theColumnCount, false);
        setLayout(layout);

        setMarginHeight(DEFAULT_MARGIN_WIDTH);
        setMarginWidth(DEFAULT_MARGIN_WIDTH);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Gets the number of columns in the layout.
     * @param the column count
     */
    public int getColumnCount() {
        return ((GridLayout)getLayout()).numColumns;
    }
    
    /**
     * Gets the layout's horizontal spacing pixel count.
     * @param the horizontal spacing
     */
    public int getHorizontalSpacing() {
        return ((GridLayout)getLayout()).horizontalSpacing;
    }
    
    /**
     * Gets the layout's margin height pixel count.
     * @param the margin height
     */
    public int getMarginHeight() {
        return ((GridLayout)getLayout()).marginHeight;
    }
    
    /**
     * Gets the layout's margin width pixel count.
     * @param the margin width
     */
    public int getMarginWidth() {
        return ((GridLayout)getLayout()).marginWidth;
    }
    
    /**
     * Gets the layout's vertical spacing pixel count.
     * @param the vertical spacing
     */
    public int getVerticalSpacing() {
        return ((GridLayout)getLayout()).verticalSpacing;
    }
    
    /**
     * Indicates if the layout has equal width columns.
     * @return <code>true</code> if equal width; <code>false</code> otherwise.
     */
    public boolean isColumnsEqualWidth() {
        return ((GridLayout)getLayout()).makeColumnsEqualWidth;
    }
    
    /**
     * Sets the layout's number of columns.
     * @param theColumnCount the column count
     */
    public void setColumnCount(int theColumnCount) {
        ((GridLayout)getLayout()).numColumns = theColumnCount;
    }
    
    /**
     * Sets the layout's equal width columns property.
     * @param theEqualWidthFlag the flag indicating if the layout's columns should be equal width
     */
    public void setColumnsEqualWidth(boolean theEqualWidthFlag) {
        ((GridLayout)getLayout()).makeColumnsEqualWidth = theEqualWidthFlag;
    }
    
    /**
     * Sets the layout's horizontal spacing to the specified pixel amount.
     * @param thePixels the horizontal spacing
     */
    public void setHorizontalSpacing(int thePixels) {
        ((GridLayout)getLayout()).horizontalSpacing = thePixels;
    }
    
    /**
     * @throws com.metamatrix.core.util.AssertionError if layout is not {@link GridLayout}.
     * @see org.eclipse.swt.widgets.Composite#setLayout(org.eclipse.swt.widgets.Layout)
     */
    @Override
    public void setLayout(Layout theLayout) {
        Assertion.assertTrue(theLayout instanceof GridLayout);
        super.setLayout(theLayout);
    }

    /**
     * Sets the layout's margin height to the specified pixel amount.
     * @param thePixels the margin height
     */
    public void setMarginHeight(int thePixels) {
        ((GridLayout)getLayout()).marginHeight = thePixels;
    }
    
    /**
     * Sets the layout's margin width to the specified pixel amount.
     * @param thePixels the margin width
     */
    public void setMarginWidth(int thePixels) {
        ((GridLayout)getLayout()).marginWidth = thePixels;
    }
    
    /**
     * Sets the layout's vertical spacing to the specified pixel amount.
     * @param thePixels the vertical spacing
     */
    public void setVerticalSpacing(int thePixels) {
        ((GridLayout)getLayout()).verticalSpacing = thePixels;
    }
    
}
