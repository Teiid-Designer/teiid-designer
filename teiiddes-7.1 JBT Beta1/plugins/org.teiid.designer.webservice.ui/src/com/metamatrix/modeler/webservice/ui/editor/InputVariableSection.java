/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.editor;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.symbol.ElementSymbol;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.query.internal.ui.sqleditor.component.DisplayNode;
import com.metamatrix.query.ui.sqleditor.SqlEditorPanel;
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

    private static final String DELETE = UTIL.getString(I18N_PFX + "delete"); //$NON-NLS-1$
    private static final String RENAME = UTIL.getString(I18N_PFX + "rename"); //$NON-NLS-1$
    private static final String VARS_TITLE = UTIL.getString(I18N_PFX + "variablesTitle"); //$NON-NLS-1$

    private static final String NAME_EXISTS = UTIL.getString(I18N_PFX + "nameExists"); //$NON-NLS-1$

    OperationObjectEditorPage editor;
    private Composite client;
    TableViewer varViewer;
    private KeyListener deleteListener, renameListener;
    private Section parentSection;
    IAction deleteVariablesAction;
    IAction renameVariableAction;
    Shell renameEditor;
    private ShellListener renameEditorListener;

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
        this.varViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged( SelectionChangedEvent event ) {
                variablesSelected(event);
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

        // Create delete & rename actions.
        deleteVariablesAction = new Action(DELETE) {

            @Override
            public void run() {
                deleteVariables();
            }
        };

        renameVariableAction = new Action(RENAME) {

            @Override
            public void run() {
                openRenameEditor();
            }
        };

        // Create and set the menu manager on the table
        MenuManager popupMenuManager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        IMenuListener listener = new IMenuListener() {
            public void menuAboutToShow( IMenuManager manager ) {
                if (!getSelection().isEmpty()
                    && !ModelUtilities.getModelResourceForModelObject(editor.getCurrentMappingRoot()).isReadOnly()) {
                    manager.add(deleteVariablesAction);
                    if (getSelection().size() == 1) {
                        manager.add(renameVariableAction);
                    }
                }
            }
        };
        popupMenuManager.addMenuListener(listener);
        popupMenuManager.setRemoveAllWhenShown(true);
        Menu menu = popupMenuManager.createContextMenu(table);
        table.setMenu(menu);
        // END Defect 23414 & 23535 mods
        // -----------------------------------
    }

    /**
     * Does nothing.
     * 
     * @param parent
     * @since 5.0.1
     */
    protected void createActionControls( Composite parent ) {
    }

    private void createDeleteListener() {
        if (this.deleteListener != null) {
            return;
        }

        this.deleteListener = new KeyAdapter() {

            @Override
            public void keyReleased( KeyEvent event ) {
                if (event.keyCode == SWT.DEL) {
                    deleteVariables();
                }
            }
        };
        this.varViewer.getTable().addKeyListener(this.deleteListener);
    }

    private void createRenameListener() {
        if (this.renameListener != null) {
            return;
        }

        this.renameListener = new KeyAdapter() {

            @Override
            public void keyReleased( KeyEvent event ) {
                if (event.keyCode == SWT.F2) {
                    openRenameEditor();
                }
            }
        };
        this.varViewer.getTable().addKeyListener(this.renameListener);
    }

    /**
     * @since 5.0.1
     */
    void deleteVariables() {
        IStructuredSelection selection = ((IStructuredSelection)this.varViewer.getSelection());
        for (Iterator varIter = selection.iterator(); varIter.hasNext();) {
            Entry entry = (Entry)varIter.next();
            DeclareStatement declaration = (DeclareStatement)entry.getKey();
            AssignmentStatement assignment = (AssignmentStatement)entry.getValue();
            this.editor.getDeclarationsToAssignments().remove(declaration);
            DisplayNode block = this.editor.findBlock();
            if (block != null) {
                for (Iterator childIter = block.getChildren().iterator(); childIter.hasNext();) {
                    DisplayNode child = (DisplayNode)childIter.next();
                    if (child.getLanguageObject() == declaration || child.getLanguageObject() == assignment) {
                        childIter.remove();
                        List childNodes = child.getDisplayNodeList();
                        for (DisplayNode ancestor = block.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
                            ancestor.getDisplayNodeList().removeAll(childNodes);
                        } // for
                    }
                } // for
            } // if
        } // for
        SqlEditorPanel editorPanel = this.editor.getCurrentSqlEditor();
        editorPanel.setText(editorPanel.getQueryDisplayComponent().toDisplayString());
        editorPanel.setHasPendingChanges();
        variablesDeleted(selection.toList());
        this.varViewer.refresh();
    }

    private void destroyDeleteListener() {
        removeListener(this.deleteListener);
        this.deleteListener = null;
    }

    private void destroyRenameListener() {
        removeListener(this.renameListener);
        this.renameListener = null;
    }

    public IStructuredSelection getSelection() {
        return (IStructuredSelection)this.varViewer.getSelection();
    }

    /**
     * @since 5.0.1
     */
    void openRenameEditor() {
        // Create "rename" window
        this.renameEditor = new Shell(this.varViewer.getTable().getShell(), SWT.ON_TOP);
        // Give window a single pixel gray border
        this.renameEditor.setLayout(new FillLayout());
        // Create name editor in window
        IStructuredSelection selection = ((IStructuredSelection)this.varViewer.getSelection());
        final Entry entry = (Entry)selection.getFirstElement();
        final DeclareStatement declaration = (DeclareStatement)entry.getKey();
        final Text textFld = new Text(this.renameEditor, SWT.NONE);
        // Close window and rename variable if enter pressed
        textFld.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased( KeyEvent event ) {
                if (event.keyCode == SWT.CR && renameVariable(declaration.getVariable().getName(), textFld.getText())) {
                    InputVariableSection.this.renameEditor.close();
                    variableRenamed(entry);
                }
            }
        });
        // Initialize editor with current name and select it
        textFld.setText(declaration.getVariable().getShortName());
        textFld.selectAll();
        // Close window if it gets deactivated
        this.renameEditorListener = new ShellAdapter() {

            @Override
            public void shellDeactivated( ShellEvent event ) {
                InputVariableSection.this.renameEditor.close();
            }
        };
        this.renameEditor.addShellListener(this.renameEditorListener);
        // Size window
        this.renameEditor.pack();
        // Set window's location
        Rectangle bounds = this.varViewer.getTable().getSelection()[0].getBounds(0);
        this.renameEditor.setLocation(this.varViewer.getTable().toDisplay(bounds.x, bounds.y));
        // Display window
        this.renameEditor.open();
    }

    public void refresh() {
        // Defect 23414 & 23535 fixes a thread access problem...
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                varViewer.refresh();
            }
        }, true);
    }

    private void removeListener( KeyListener listener ) {
        if (listener != null) {
            this.varViewer.getTable().removeKeyListener(listener);
        }
    }

    boolean renameVariable( String oldName,
                            String newName ) {
        if (!newName.startsWith(WebServiceUtil.INPUT_VARIABLE_UNQUALIFIED_PREFIX)) {
            newName = WebServiceUtil.INPUT_VARIABLE_UNQUALIFIED_PREFIX + newName;
        }
        newName = oldName.substring(0, oldName.lastIndexOf('.') + 1) + newName;
        // Validate name
        for (Iterator iter = this.editor.getDeclarationsToAssignments().entrySet().iterator(); iter.hasNext();) {
            Entry entry = (Entry)iter.next();
            if (newName.equalsIgnoreCase(((DeclareStatement)entry.getKey()).getVariable().getName())) {
                // Remove rename editor's shell listener to stop editor shell from being closed due to error shell getting
                // activated.
                this.renameEditor.removeShellListener(this.renameEditorListener);
                // Display error message
                WidgetUtil.showError(NAME_EXISTS);
                // Re-add shell listener to allow editor to be closed when it either loses focus or ENTER is pressed.
                this.renameEditor.addShellListener(this.renameEditorListener);
                return false;
            }
        } // for
        // Update all variables in block to use new name
        for (Iterator iter = this.editor.findBlock().getDisplayNodeList().iterator(); iter.hasNext();) {
            Object node = ((DisplayNode)iter.next()).getLanguageObject();
            if (node instanceof ElementSymbol) {
                ElementSymbol var = (ElementSymbol)node;
                if (oldName.equalsIgnoreCase(var.getName())) {
                    var.setName(newName);
                }
            }
        } // for
        SqlEditorPanel editorPanel = this.editor.getCurrentSqlEditor();
        editorPanel.setText(editorPanel.getQueryDisplayComponent().toDisplayString());
        editorPanel.setHasPendingChanges();
        this.varViewer.refresh();
        return true;
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

    /**
     * Does nothing.
     * 
     * @since 5.0.1
     */
    protected void variableRenamed( Entry entry ) {
    }

    /**
     * Does nothing.
     * 
     * @since 5.0.1
     */
    protected void variablesDeleted( List entries ) {
    }

    void variablesSelected( SelectionChangedEvent event ) {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        if (selection.size() == 0) {
            destroyDeleteListener();
            destroyRenameListener();
        } else {
            if (selection.size() == 1) {
                createDeleteListener();
                createRenameListener();
            } else {
                createDeleteListener();
                destroyRenameListener();
            }
        }
    }
}
