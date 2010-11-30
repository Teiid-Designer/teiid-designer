/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.editor;

import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.DeclareStatement;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.AbstractTableLabelProvider;
import com.metamatrix.ui.internal.widget.DefaultContentProvider;

/**
 * @since 5.0.1
 */
public class InputVariableSection implements IInternalUiConstants {

    private static final String I18N_PFX = I18nUtil.getPropertyPrefix(InputVariableSection.class);

    private static final String VARS_TITLE = UTIL.getString(I18N_PFX + "variablesTitle"); //$NON-NLS-1$

    OperationObjectEditorPage editor;
    private Composite client;
    TableViewer varViewer;
    private Section parentSection;
    IAction deleteVariablesAction;
    IAction renameVariableAction;
    Shell renameEditor;

    public InputVariableSection( Composite parent,
                                 String description,
                                 OperationObjectEditorPage editor ) {
        this.editor = editor;
        FormToolkit toolkit = WebServiceUiPlugin.getDefault().getFormToolkit(parent.getDisplay());
        parentSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
        parentSection.setText(VARS_TITLE);
        parentSection.setDescription(description);
        parentSection.getDescriptionControl().addPaintListener(new PaintListener() {

            public void paintControl( PaintEvent event ) {
                event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_GRAY));
                event.gc.drawLine(0, event.height - 1, event.width - 1, event.height - 1);
            }
        });
        this.client = WidgetFactory.createPanel(parentSection, SWT.NONE);
        parentSection.setClient(this.client);
        toolkit.paintBordersFor(this.client);
        this.client.setBackground(parentSection.getBackground());
        GridLayout layout = (GridLayout)this.client.getLayout();
        layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 1;
    }

    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        this.varViewer.addSelectionChangedListener(listener);
    }

    public void create() {
        createActionControls(this.client);

        // Create variable list
        this.varViewer = WidgetFactory.createTableViewer(this.client, WidgetFactory.NO_DEFAULTS | SWT.H_SCROLL | SWT.V_SCROLL
                                                                      | SWT.MULTI);
        Table table = this.varViewer.getTable();
        this.varViewer.setContentProvider(new DefaultContentProvider());
        this.varViewer.setLabelProvider(new AbstractTableLabelProvider() {

            public String getColumnText( Object element,
                                         int columnIndex ) {

                DeclareStatement declaration = (DeclareStatement)((Entry)element).getKey();
                return declaration.getVariable().getShortName() + " : " + declaration.getVariableType(); //$NON-NLS-1$
            }
        });

        table.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseExit( MouseEvent event ) {
                updateToolTip(event);
            }

            @Override
            public void mouseHover( MouseEvent event ) {
                updateToolTip(event);
            }
        });
        this.varViewer.setInput(this.editor.getDeclarationsToAssignments());

        // -----------------------------------
        // Defect 23414 & 23535
        // need to add focus listener to somehow cache this information so the DeleteWorker & ModelObjectEditHelper framework
        // (via new WebServiceObjectEditHelper) to disallow any EDITING because THIS TABLE IS IN FOCUS
        // If it goes out of focus, set selection to empty so the user doen't get tempted to try and DELETE a variable because it
        // appears to be hilighted and in focus.

        table.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost( FocusEvent fe ) {
                editor.setAllowsExternalEdits(true);
            }

            @Override
            public void focusGained( FocusEvent fe ) {
                editor.setAllowsExternalEdits(false);
            }
        });

    }

    /**
     * Does nothing.
     * 
     * @param parent
     * @since 5.0.1
     */
    protected void createActionControls( Composite parent ) {
    }

    public IStructuredSelection getSelection() {
        return (IStructuredSelection)this.varViewer.getSelection();
    }

    public void refresh() {
        // Defect 23414 & 23535 fixes a thread access problem...
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                varViewer.refresh();
            }
        }, true);
    }

    public void select( Entry entry ) {
        WidgetUtil.select(entry, this.varViewer);
    }

    public void clearSelection() {
        varViewer.setSelection(StructuredSelection.EMPTY);
    }

    public void setEnabled( boolean enabled ) {
        if (enabled) {
            Color fgdColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
            this.client.setForeground(fgdColor);
            parentSection.setForeground(fgdColor);
            parentSection.setTitleBarForeground(fgdColor);
        } else {
            Color fgdColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
            this.client.setForeground(fgdColor);
            parentSection.setForeground(fgdColor);
            parentSection.setTitleBarForeground(fgdColor);
        }
    }

    void updateToolTip( MouseEvent event ) {
        Table table = this.varViewer.getTable();
        TableItem item = table.getItem(new Point(event.x, event.y));
        if (item == null) {
            table.setToolTipText(null);
        } else {
            Entry entry = (Entry)item.getData();
            table.setToolTipText(WebServiceUtil.getXpath((AssignmentStatement)entry.getValue()));
        }
    }
   
}
