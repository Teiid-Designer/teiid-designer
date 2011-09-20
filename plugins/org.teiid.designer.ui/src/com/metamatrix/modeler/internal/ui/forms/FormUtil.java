/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class FormUtil {

    public static final String HTML_BEGIN = "<form><p>"; //$NON-NLS-1$
    public static final String HTML_END = "</p></form>"; //$NON-NLS-1$

    // CLEAR
    public static final int DEFAULT_CLEAR_MARGIN = 2;
    public static final int CLEAR_MARGIN_TOP = DEFAULT_CLEAR_MARGIN;
    public static final int CLEAR_MARGIN_BOTTOM = DEFAULT_CLEAR_MARGIN;
    public static final int CLEAR_MARGIN_LEFT = DEFAULT_CLEAR_MARGIN;
    public static final int CLEAR_MARGIN_RIGHT = DEFAULT_CLEAR_MARGIN;
    public static final int CLEAR_HORIZONTAL_SPACING = 0;
    public static final int CLEAR_VERTICAL_SPACING = 0;
    public static final int CLEAR_MARGIN_HEIGHT = 0;
    public static final int CLEAR_MARGIN_WIDTH = 0;

    // FORM BODY
    public static final int FORM_BODY_MARGIN_TOP = 12;
    public static final int FORM_BODY_MARGIN_BOTTOM = 12;
    public static final int FORM_BODY_MARGIN_LEFT = 6;
    public static final int FORM_BODY_MARGIN_RIGHT = 6;
    public static final int FORM_BODY_HORIZONTAL_SPACING = 20;
    public static final int FORM_BODY_VERTICAL_SPACING = 17;
    public static final int FORM_BODY_MARGIN_HEIGHT = 0;
    public static final int FORM_BODY_MARGIN_WIDTH = 0;

    // FORM PANE
    public static final int FORM_PANE_MARGIN_TOP = 0;
    public static final int FORM_PANE_MARGIN_BOTTOM = 0;
    public static final int FORM_PANE_MARGIN_LEFT = 0;
    public static final int FORM_PANE_MARGIN_RIGHT = 0;
    public static final int FORM_PANE_HORIZONTAL_SPACING = FORM_BODY_HORIZONTAL_SPACING;
    public static final int FORM_PANE_VERTICAL_SPACING = FORM_BODY_VERTICAL_SPACING;
    public static final int FORM_PANE_MARGIN_HEIGHT = 0;
    public static final int FORM_PANE_MARGIN_WIDTH = 0;

    // SECTION CLIENT
    public static final int SECTION_CLIENT_MARGIN_TOP = 5;
    public static final int SECTION_CLIENT_MARGIN_BOTTOM = 5;
    public static final int SECTION_CLIENT_MARGIN_LEFT = 2;
    public static final int SECTION_CLIENT_MARGIN_RIGHT = 2;
    public static final int SECTION_CLIENT_HORIZONTAL_SPACING = 5;
    public static final int SECTION_CLIENT_VERTICAL_SPACING = 5;
    public static final int SECTION_CLIENT_MARGIN_HEIGHT = 0;
    public static final int SECTION_CLIENT_MARGIN_WIDTH = 0;
    public static final int SECTION_HEADER_VERTICAL_SPACING = 6;

    public static Button createButton( Composite parent,
                                       FormToolkit toolkit,
                                       String label ) {
        Button button = toolkit.createButton(parent, label, SWT.PUSH);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        button.setLayoutData(gd);
        return button;
    }

    public static Button[] createButtonsContainer( Composite parent,
                                                   FormToolkit toolkit,
                                                   String[] buttonLabels ) {
        Composite container = toolkit.createComposite(parent);
        GridData gd = new GridData(GridData.FILL_VERTICAL);
        container.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);

        Button[] buttons = new Button[buttonLabels.length];
        int i = 0;

        for (String label : buttonLabels) {
            Button button = createButton(container, toolkit, label);
            buttons[i++] = button;
        }

        return buttons;
    }

    /**
     * For miscellaneous grouping composites. For sections (as a whole - header plus client).
     * 
     * @param makeColumnsEqualWidth
     * @param numColumns
     * @return
     */
    public static GridLayout createClearGridLayout( boolean makeColumnsEqualWidth,
                                                    int numColumns ) {
        GridLayout layout = new GridLayout();

        layout.marginHeight = CLEAR_MARGIN_HEIGHT;
        layout.marginWidth = CLEAR_MARGIN_WIDTH;

        layout.marginTop = CLEAR_MARGIN_TOP;
        layout.marginBottom = CLEAR_MARGIN_BOTTOM;
        layout.marginLeft = CLEAR_MARGIN_LEFT;
        layout.marginRight = CLEAR_MARGIN_RIGHT;

        layout.horizontalSpacing = CLEAR_HORIZONTAL_SPACING;
        layout.verticalSpacing = CLEAR_VERTICAL_SPACING;

        layout.makeColumnsEqualWidth = makeColumnsEqualWidth;
        layout.numColumns = numColumns;

        return layout;
    }

    /**
     * For form bodies.
     * 
     * @param makeColumnsEqualWidth
     * @param numColumns
     * @return
     */
    public static GridLayout createFormGridLayout( boolean makeColumnsEqualWidth,
                                                   int numColumns ) {
        GridLayout layout = new GridLayout();

        layout.marginHeight = FORM_BODY_MARGIN_HEIGHT;
        layout.marginWidth = FORM_BODY_MARGIN_WIDTH;

        layout.marginTop = FORM_BODY_MARGIN_TOP;
        layout.marginBottom = FORM_BODY_MARGIN_BOTTOM;
        layout.marginLeft = FORM_BODY_MARGIN_LEFT;
        layout.marginRight = FORM_BODY_MARGIN_RIGHT;

        layout.horizontalSpacing = FORM_BODY_HORIZONTAL_SPACING;
        layout.verticalSpacing = FORM_BODY_VERTICAL_SPACING;

        layout.makeColumnsEqualWidth = makeColumnsEqualWidth;
        layout.numColumns = numColumns;

        return layout;
    }

    /**
     * For composites used to group sections in left and right panes.
     * 
     * @param makeColumnsEqualWidth
     * @param numColumns
     * @return
     */
    public static GridLayout createFormPaneGridLayout( boolean makeColumnsEqualWidth,
                                                       int numColumns ) {
        GridLayout layout = new GridLayout();

        layout.marginHeight = FORM_PANE_MARGIN_HEIGHT;
        layout.marginWidth = FORM_PANE_MARGIN_WIDTH;

        layout.marginTop = FORM_PANE_MARGIN_TOP;
        layout.marginBottom = FORM_PANE_MARGIN_BOTTOM;
        layout.marginLeft = FORM_PANE_MARGIN_LEFT;
        layout.marginRight = FORM_PANE_MARGIN_RIGHT;

        layout.horizontalSpacing = FORM_PANE_HORIZONTAL_SPACING;
        layout.verticalSpacing = FORM_PANE_VERTICAL_SPACING;

        layout.makeColumnsEqualWidth = makeColumnsEqualWidth;
        layout.numColumns = numColumns;

        return layout;
    }

    /**
     * For composites set as section clients. For composites containg form text.
     * 
     * @param makeColumnsEqualWidth
     * @param numColumns
     * @return
     */
    public static GridLayout createSectionClientGridLayout( boolean makeColumnsEqualWidth,
                                                            int numColumns ) {
        GridLayout layout = new GridLayout();

        layout.marginHeight = SECTION_CLIENT_MARGIN_HEIGHT;
        layout.marginWidth = SECTION_CLIENT_MARGIN_WIDTH;

        layout.marginTop = SECTION_CLIENT_MARGIN_TOP;
        layout.marginBottom = SECTION_CLIENT_MARGIN_BOTTOM;
        layout.marginLeft = SECTION_CLIENT_MARGIN_LEFT;
        layout.marginRight = SECTION_CLIENT_MARGIN_RIGHT;

        layout.horizontalSpacing = SECTION_CLIENT_HORIZONTAL_SPACING;
        layout.verticalSpacing = SECTION_CLIENT_VERTICAL_SPACING;

        layout.makeColumnsEqualWidth = makeColumnsEqualWidth;
        layout.numColumns = numColumns;

        return layout;
    }

    public static boolean safeEquals( Object leftVal,
                                      Object rightVal ) {
        if (leftVal == null) {
            return rightVal == null;
        } // endif

        return leftVal.equals(rightVal);
    }

    public static boolean safeEquals( String leftVal,
                                      String rightVal,
                                      boolean treatNullAsEmpty ) {
        if (leftVal == null) {
            // left was null; right can be either null or (when treatNullAsEmpty) empty
            return rightVal == null || (treatNullAsEmpty && rightVal.length() == 0);
        } // endif

        if (leftVal.length() == 0) {
            // left was empty; right can be either empty or (when treatNullAsEmpty) null
            if (rightVal == null) {
                return treatNullAsEmpty;
            } // endif

            // right not null
            return rightVal.length() == 0;
        } // endif

        // left not null or empty, so right can't be either; just eq:
        return leftVal.equals(rightVal);
    }

    public static ScrolledForm getScrolledForm( Control c ) {
        Composite parent = c.getParent();

        while (parent != null) {
            if (parent instanceof ScrolledForm) {
                return (ScrolledForm)parent;
            } // endif
            parent = parent.getParent();
        } // endwhile

        // no scrolled form in hierarchy, return null:
        return null;
    }

    public static void tweakColors( FormToolkit ftk,
                                    Display display ) {
        ftk.refreshHyperlinkColors();
        HyperlinkGroup hlg = ftk.getHyperlinkGroup();
        if (hlg.getActiveForeground() == null) {
            hlg.setActiveForeground(display.getSystemColor(SWT.COLOR_RED));
            hlg.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
        } // endif
    }

}
