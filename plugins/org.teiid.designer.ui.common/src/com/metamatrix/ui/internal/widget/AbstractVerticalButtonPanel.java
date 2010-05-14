/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.widget;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * <p>
 * </p>
 * 
 * @since 4.0
 */
public abstract class AbstractVerticalButtonPanel extends Composite {

    // ============================================================================================================================
    // Constants

    private static final int COLUMN_COUNT = 2;

    // ============================================================================================================================
    // Variables

    private Viewer viewer;
    private Composite buttonBar;
    private Group group;

    // ============================================================================================================================
    // Constructors

    /**
     * <p>
     * </p>
     * 
     * @param parent
     * @param style
     * @since 4.0
     */
    protected AbstractVerticalButtonPanel(final String name,
                                          final Composite parent,
                                          final int style) {
        this(name, parent, style, GridData.FILL_BOTH);
    }

    /**
     * <p>
     * </p>
     * 
     * @param parent
     * @param style
     * @param gridStyle
     * @since 4.0
     */
    protected AbstractVerticalButtonPanel(final String name,
                                          final Composite parent,
                                          final int style,
                                          final int gridStyle) {
        this(name, parent, style, gridStyle, 1);
    }

    /**
     * <p>
     * </p>
     * 
     * @param parent
     * @param style
     * @param gridStyle
     * @param span
     * @since 4.0
     */
    protected AbstractVerticalButtonPanel(final String name,
                                          final Composite parent,
                                          final int style,
                                          final int gridStyle,
                                          final int span) {
        super(parent, SWT.NONE);
        constructVerticalButtonPanel(name, style, gridStyle, span);
    }

    // ============================================================================================================================
    // Initialization Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    protected void constructVerticalButtonPanel(final String name,
                                                final int style,
                                                final int gridStyle,
                                                final int span) {
        CoreArgCheck.isNotEmpty(name);
        // Set layout data
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        setLayoutData(gridData);
        // Set layout
        final GridLayout layout = new GridLayout();
        layout.marginWidth = layout.marginHeight = 0;
        setLayout(layout);
        // Add widgets
        group = WidgetFactory.createGroup(this, name, GridData.FILL_BOTH, 1, COLUMN_COUNT);
        this.viewer = createViewer(group, style | SWT.BORDER);
        if (this.viewer != null) {
            this.viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        }
        this.buttonBar = WidgetFactory.createPanel(group, SWT.NO_TRIM, GridData.VERTICAL_ALIGN_CENTER);
    }

    // ============================================================================================================================
    // Property Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public Viewer getViewer() {
        return this.viewer;
    }

    /**
     * Returns the Group containing the viewer and buttons.
     * 
     * @return
     * @since 4.2
     */
    public Group getGroup() {
        return this.group;
    }

    // ============================================================================================================================
    // MVC View Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public Button addButton(final String name) {
        return WidgetFactory.createButton(this.buttonBar, name, GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    protected abstract Viewer createViewer(final Composite parent,
                                           final int style);

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public IStructuredSelection getSelection() {
        final Viewer viewer = getViewer();
        if (viewer != null) {
            final ISelection selection = viewer.getSelection();
            if (selection instanceof IStructuredSelection) {
                return (IStructuredSelection)selection;
            }
        }
        return null;
    }
}
