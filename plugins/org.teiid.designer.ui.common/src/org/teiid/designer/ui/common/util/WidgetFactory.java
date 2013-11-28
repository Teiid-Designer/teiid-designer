/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.util;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.widget.DefaultTreeViewerController;
import org.teiid.designer.ui.common.widget.IListPanelController;
import org.teiid.designer.ui.common.widget.ITreeViewerController;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.widget.ListPanel;
import org.teiid.designer.ui.common.widget.WrappingLabel;


/**
 * @since 8.0
 */
public final class WidgetFactory implements
                                InternalUiConstants.Widgets {

    // ============================================================================================================================
    // Constants

    private static final Color[] TITLE_SELECTION_GRADIENT = new Color[] {
        UiUtil.getSystemColor(SWT.COLOR_TITLE_BACKGROUND), UiUtil.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT)
    };

    private static final int[] TITLE_SELECTION_GRADIENT_PERCENTAGES = new int[] {
        100
    };

    private static final Color TITLE_SELECTION_FOREGROUND = UiUtil.getSystemColor(SWT.COLOR_TITLE_FOREGROUND);

    public static final char PASSWORD_ECHO_CHAR = '*';

    public static final int NO_DEFAULTS = 1 << 30;

    // ============================================================================================================================
    // Static Methods

    /**
     * @since 5.0.1
     */
    public static Button createButton(final Composite parent,
                                      int gridStyle) {
        return createButton(parent, null, gridStyle, 1, SWT.PUSH);
    }

    /**
     * @since 4.0
     */
    public static Button createButton(final Composite parent,
                                      final String name) {
        return createButton(parent, name, 0, 1, SWT.PUSH);
    }

    /**
     * @since 4.0
     */
    public static Button createButton(final Composite parent,
                                      final String name,
                                      final int gridStyle) {
        return createButton(parent, name, gridStyle, 1, SWT.PUSH);
    }

    /**
     * @since 4.0
     */
    public static Button createButton(final Composite parent,
                                      final String name,
                                      final int gridStyle,
                                      final int span) {
        return createButton(parent, name, gridStyle, span, SWT.PUSH);
    }

    /**
     * @since 4.0
     */
    public static Button createButton(final Composite parent,
                                      final String name,
                                      final int gridStyle,
                                      final int span,
                                      final int style) {
        final Button button = new Button(parent, style);
        if (name != null) {
            button.setText(name);
        }
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        button.setLayoutData(gridData);
        return button;
    }

    /**
     * @since 4.0
     */
    public static Button createCheckBox(final Composite parent) {
        return createCheckBox(parent, null);
    }

    /**
     * @since 4.0
     */
    public static Button createCheckBox(final Composite parent,
                                        final String name) {
        return createCheckBox(parent, name, 0);
    }

    /**
     * @since 4.0
     */
    public static Button createCheckBox(final Composite parent,
                                        final int gridStyle) {
        return createCheckBox(parent, null, gridStyle);
    }

    /**
     * @since 4.0
     */
    public static Button createCheckBox(final Composite parent,
                                        final String name,
                                        final int gridStyle) {
        return createCheckBox(parent, name, gridStyle, 1, false);
    }

    /**
     * @since 4.0
     */
    public static Button createCheckBox(final Composite parent,
                                        final String name,
                                        final boolean selected) {
        return createCheckBox(parent, name, 0, selected);
    }

    /**
     * @since 4.0
     */
    public static Button createCheckBox(final Composite parent,
                                        final String name,
                                        final int gridStyle,
                                        final boolean selected) {
        return createCheckBox(parent, name, gridStyle, 1, selected);
    }

    /**
     * @since 4.0
     */
    public static Button createCheckBox(final Composite parent,
                                        final String name,
                                        final int gridStyle,
                                        final int span) {
        return createCheckBox(parent, name, gridStyle, span, false);
    }

    /**
     * @since 4.0
     */
    public static Button createCheckBox(final Composite parent,
                                        final String name,
                                        final int gridStyle,
                                        final int span,
                                        final boolean selected) {
        final Button button = createButton(parent, name, gridStyle, span, SWT.CHECK);
        button.setSelection(selected);
        return button;
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style) {
        return createCombo(parent, style, GridData.HORIZONTAL_ALIGN_FILL);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle) {
        return createCombo(parent, style, gridStyle, 1);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final int span) {
        return createCombo(parent, style, gridStyle, span, null, null);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final int span,
                                    final List items,
                                    final ILabelProvider provider) {
        return createCombo(parent, style, gridStyle, span, items, provider, true);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final int span,
                                    final List items,
                                    final ILabelProvider provider,
                                    final boolean sort) {
        return createCombo(parent, style, gridStyle, span, items, null, provider, sort);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final List items) {
        return createCombo(parent, style, gridStyle, items, null);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final Object[] items) {
        return createCombo(parent, style, gridStyle, items, null);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final int span,
                                    final Object[] items) {
        return createCombo(parent, style, gridStyle, span, items == null ? (List)null : Arrays.asList(items), null);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final Object[] items,
                                    final Object selection) {
        return createCombo(parent, style, gridStyle, items == null ? (List)null : Arrays.asList(items), selection);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final List items,
                                    final Object selection) {
        return createCombo(parent, style, gridStyle, items, selection, null);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final List items,
                                    final ILabelProvider provider) {
        return createCombo(parent, style, gridStyle, items, provider, true);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final List items,
                                    final ILabelProvider provider,
                                    final boolean sort) {
        return createCombo(parent, style, gridStyle, items, null, provider, sort);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final List items,
                                    final Object selection,
                                    final ILabelProvider provider) {
        return createCombo(parent, style, gridStyle, items, selection, provider, true);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final List items,
                                    final Object selection,
                                    final ILabelProvider provider,
                                    final boolean sort) {
        return createCombo(parent, style, gridStyle, 1, items, selection, provider, sort);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final ILabelProvider provider) {
        return createCombo(parent, style, gridStyle, null, null, provider);
    }

    /**
     * @since 4.0
     */
    public static Combo createCombo(final Composite parent,
                                    final int style,
                                    final int gridStyle,
                                    final int span,
                                    final List items,
                                    final Object selection,
                                    final ILabelProvider provider,
                                    final boolean sort) {
        CoreArgCheck.isNotNull(parent);
        final Combo combo = new Combo(parent, style | SWT.BORDER);
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        combo.setLayoutData(gridData);
        if (items != null) {
            WidgetUtil.setComboItems(combo, items, provider, sort);
        }
        if (selection != null) {
            WidgetUtil.setComboText(combo, selection, provider, !WidgetUtil.hasStyle(combo, SWT.READ_ONLY));
        }
        return combo;
    }

    public static FormText createFormText(final Composite parent,
                                          FormToolkit toolkit,
                                          String html,
                                          IHyperlinkListener listener) {
        FormText formText = toolkit.createFormText(parent, true);
        formText.setHyperlinkSettings(new HyperlinkSettings(parent.getDisplay()) {

            @Override
            public Cursor getHyperlinkCursor() {
                if (getHyperlinkUnderlineMode() == HyperlinkSettings.UNDERLINE_NEVER) {
                    return parent.getDisplay().getSystemCursor(SWT.CURSOR_ARROW);
                }
                return super.getHyperlinkCursor();
            }
        });
        formText.setText(html, true, false);
        formText.addHyperlinkListener(listener);
        return formText;
    }

    /**
     * @since 4.0
     */
    public static Group createGroup(final Composite parent,
                                    final String name) {
        return createGroup(parent, name, 0);
    }

    /**
     * @since 4.0
     */
    public static Group createGroup(final Composite parent,
                                    final int gridStyle) {
        return createGroup(parent, null, gridStyle);
    }

    /**
     * @since 4.0
     */
    public static Group createGroup(final Composite parent,
                                    final String name,
                                    final int gridStyle) {
        return createGroup(parent, name, gridStyle, 1);
    }

    /**
     * @since 4.0
     */
    public static Group createGroup(final Composite parent,
                                    final String name,
                                    final int gridStyle,
                                    final int span) {
        return createGroup(parent, name, gridStyle, span, 1);
    }

    /**
     * @since 4.0
     */
    public static Group createGroup(final Composite parent,
                                    final String name,
                                    final int gridStyle,
                                    final int span,
                                    final int columns) {
        
        final Group group = new Group(parent, SWT.NONE);
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        group.setLayoutData(gridData);

        GridLayoutFactory gridLayoutFactory = GridLayoutFactory.fillDefaults()
                                                                                                          .numColumns(columns)
                                                                                                          .equalWidth(false);
        Font bannerFont = JFaceResources.getBannerFont();
        group.setFont(bannerFont);

        /*
         * An issue exists on some linux distiros, eg. Fedora, (but not on other's, 
         * eg. Ubuntu), that stops Groups rendering correctly with a title when using
         * group.setText(). The title is overlapped in part by the internal content of
         * the group. Investigation indicates it is tied to the use of grid layout as the
         * layout of the group. Indeed, the issue does not occur in other uses of
         * group where fill layout is used.
         * 
         * This works around the issue by setting extended margins on the group. By
         * giving the top margin a value of '10', it seems to ensure that the layout of
         * the title is correctly observed resulting in no overlap. The only downside is
         * that resizing the parent dialog/panel will result in a slight jump-down of the
         * contents leaving a space of '10' between it and the title. However, rarely
         * would a user resize a dialog so this is a minor irritation.
         */
        if (name != null) {
            gridLayoutFactory = gridLayoutFactory.margins(2, 10);
            group.setText(name);
        }

        gridLayoutFactory.applyTo(group);
        return group;
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent) {
        return createLabel(parent, GridData.VERTICAL_ALIGN_BEGINNING);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final int gridStyle) {
        return createLabel(parent, gridStyle, 1);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final int gridStyle,
                                    final int span) {
        return createLabel(parent, gridStyle, span, null);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final int gridStyle,
                                    final int span,
                                    final String text) {
        return createLabel(parent, gridStyle, span, text, SWT.NONE);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final int gridStyle,
                                    final int span,
                                    final String text,
                                    final int style) {
        return createLabel(parent, gridStyle, span, text, null, style);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final int gridStyle,
                                    final String text) {
        return createLabel(parent, gridStyle, 1, text);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final int gridStyle,
                                    final String text,
                                    final int style) {
        return createLabel(parent, gridStyle, 1, text, style);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final int gridStyle,
                                    final Image image,
                                    final int style) {
        return createLabel(parent, gridStyle, 1, null, image, style);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final String text) {
        return createLabel(parent, text, SWT.NONE);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final String text,
                                    final int style) {
        return createLabel(parent, text, null, style);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final String text,
                                    final Image image,
                                    final int style) {
        return createLabel(parent, GridData.VERTICAL_ALIGN_BEGINNING, 1, text, image, style);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final Image image) {
        return createLabel(parent, image, SWT.NONE);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final Image image,
                                    final int style) {
        return createLabel(parent, GridData.VERTICAL_ALIGN_BEGINNING, 1, null, image, style);
    }

    /**
     * @since 4.0
     */
    public static Label createLabel(final Composite parent,
                                    final int gridStyle,
                                    final int span,
                                    final String text,
                                    final Image image,
                                    final int style) {
        final Label label = new Label(parent, style);
        if (text != null) {
            label.setText(text);
        }
        label.setImage(image);
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        label.setLayoutData(gridData);
        return label;
    }

    /**
     * @since 4.0
     */
    public static ListPanel createListPanel(final Composite parent,
                                            final String title,
                                            final IListPanelController controller) {
        return new ListPanel(parent, title, controller);
    }

    /**
     * Creates a resizable wizard dialog for the specified wizard.
     *
     * @param shell
     *            The shell under which to create the wizard dialog.
     * @param wizard
     *            The wizard to be added to the dialog.
     * @return The resizable wizard dialog.
     * @since 4.0
     */
    public static WizardDialog createOnePageWizardDialog(final Shell shell,
                                                         final IWizard wizard) {
        CoreArgCheck.isNotNull(shell);
        CoreArgCheck.isNotNull(wizard);
        final WizardDialog dlg = new WizardDialog(shell, wizard) {

            // Overridden to make wizard resizable
            @Override
            public void create() {
                setShellStyle(getShellStyle() | SWT.RESIZE);
                super.create();
                getShell().getDefaultButton().setText(OK_LABEL);
                updateSize(getWizard().getStartingPage());
            }
        };
        return dlg;
    }

    /**
     * @since 4.0
     */
    public static Composite createPanel(final Composite parent) {
        return createPanel(parent, SWT.NONE);
    }

    /**
     * @since 4.0
     */
    public static Composite createPanel(final Composite parent,
                                        final int style) {
        return createPanel(parent, style, 0);
    }

    /**
     * @since 4.0
     */
    public static Composite createPanel(final Composite parent,
                                        final int style,
                                        final int gridStyle) {
        return createPanel(parent, style, gridStyle, 1);
    }

    /**
     * @since 4.0
     */
    public static Composite createPanel(final Composite parent,
                                        final int style,
                                        final int gridStyle,
                                        final int span) {
        return createPanel(parent, style, gridStyle, span, 1);
    }

    /**
     * @since 4.0
     */
    public static Composite createPanel(final Composite parent,
                                        final int style,
                                        final int gridStyle,
                                        final int span,
                                        final int columns) {
        final Composite panel = new Composite(parent, style);
        WidgetUtil.initializePanel(panel, gridStyle, span, columns);
        return panel;
    }

    /**
     * @since 4.0
     */
    public static Text createPasswordField(final Composite parent) {
        return createPasswordField(parent, GridData.FILL_HORIZONTAL, 1);
    }

    /**
     * @since 4.0
     */
    public static Text createPasswordField(final Composite parent,
                                           final int gridStyle,
                                           final int span) {
        final Text text = createTextField(parent, gridStyle, span);
        text.setEchoChar(PASSWORD_ECHO_CHAR);
        return text;
    }

    /**
     * @since 4.0
     */
    public static Button createRadioButton(final Composite parent,
                                           final String name) {
        return createRadioButton(parent, name, false);
    }

    /**
     * @since 4.0
     */
    public static Button createRadioButton(final Composite parent,
                                           final String name,
                                           final boolean selected) {
        return createRadioButton(parent, name, 0, 1, selected);
    }

    /**
     * @since 4.0
     */
    public static Button createRadioButton(final Composite parent,
                                           final String name,
                                           final int gridStyle,
                                           final int span,
                                           final boolean selected) {
        final Button button = createButton(parent, name, gridStyle, span, SWT.RADIO);
        button.setSelection(selected);
        return button;
    }

    /**
     * @since 4.0
     */
    public static SashForm createSplitter(final Composite parent) {
        return createSplitter(parent, SWT.HORIZONTAL);
    }

    /**
     * @since 4.0
     */
    public static SashForm createSplitter(final Composite parent,
                                          final int style) {
        return createSplitter(parent, style, GridData.FILL_BOTH);
    }

    /**
     * @since 4.0
     */
    public static SashForm createSplitter(final Composite parent,
                                          final int style,
                                          final int gridStyle) {
        return createSplitter(parent, style, gridStyle, 1);
    }

    /**
     * @since 4.0
     */
    public static SashForm createSplitter(final Composite parent,
                                          final int style,
                                          final int gridStyle,
                                          final int span) {
        final SashForm splitter = new SashForm(parent, style);
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        splitter.setLayoutData(gridData);
        return splitter;
    }

    /**
     * @since 4.0
     */
    public static CTabItem createTab(final CTabFolder folder,
                                     final String name) {
        final CTabItem tab = new CTabItem(folder, SWT.NONE);
        if (name != null) {
            tab.setText(name);
            tab.setToolTipText(name);
        }
        return tab;
    }

    /**
     * @since 4.0
     */
    public static CTabFolder createTabFolder(final Composite parent) {
        return createTabFolder(parent, SWT.TOP);
    }

    /**
     * @since 4.0
     */
    public static CTabFolder createTabFolder(final Composite parent,
                                             final int style) {
        return createTabFolder(parent, style, GridData.FILL_BOTH);
    }

    /**
     * @since 4.0
     */
    public static CTabFolder createTabFolder(final Composite parent,
                                             final int style,
                                             final int gridStyle) {
        return createTabFolder(parent, style, gridStyle, 1);
    }

    /**
     * @since 4.0
     */
    public static CTabFolder createTabFolder(final Composite parent,
                                             final int style,
                                             final int gridStyle,
                                             final int span) {
        final CTabFolder tabFolder = new CTabFolder(parent, style);
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        tabFolder.setLayoutData(gridData);
        if (WidgetUtil.hasStyle(tabFolder, SWT.TOP)) {
            tabFolder.setSelectionBackground(TITLE_SELECTION_GRADIENT, TITLE_SELECTION_GRADIENT_PERCENTAGES);
            tabFolder.setSelectionForeground(TITLE_SELECTION_FOREGROUND);
        }
        return tabFolder;
    }

    /**
     * @since 4.0
     */
    public static TableColumn createTableColumn(final Table table) {
        return createTableColumn(table, null);
    }

    /**
     * @since 4.0
     */
    public static TableColumn createTableColumn(final Table table,
                                                final String name) {
        final TableColumn col = new TableColumn(table, SWT.NONE);
        if (name != null) {
            col.setText(name);
        }
        return col;
    }

    /**
     * @param table the table whose columns are being created
     * @param headers the table column headers
     * @param style the table column style
     * @since 5.5.3
     */
    public static void createTableColumns( Table table,
                                           String[] headers,
                                           int style) {
        for (String header : headers) {
            TableColumn col = new TableColumn(table, style);
            col.setText(header);
        }
    }

    /**
     * @since 4.0
     */
    public static TableViewer createTableViewer(final Composite parent) {
        return createTableViewer(parent, SWT.NONE);
    }

    /**
     * @since 4.0
     */
    public static TableViewer createTableViewer(Composite parent,
                                                int style) {
        if ((style & NO_DEFAULTS) == 0) {
            style |= SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
        }
        final TableViewer viewer = new TableViewer(parent, style);
        final Table table = viewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        return viewer;
    }

    /**
     * @since 4.0
     */
    public static Text createTextBox(final Composite parent) {
        return createTextBox(parent, SWT.NONE, GridData.FILL_BOTH);
    }

    /**
     * @since 4.0
     */
    public static Text createTextBox(final Composite parent,
                                     final int style,
                                     final int gridStyle) {
        return createTextBox(parent, style, gridStyle, 1);
    }

    /**
     * @since 4.0
     */
    public static Text createTextBox(final Composite parent,
                                     final int style,
                                     final int gridStyle,
                                     final int span) {
        return createTextBox(parent, style, gridStyle, span, null);
    }

    /**
     * @since 4.0
     */
    public static Text createTextBox(final Composite parent,
                                     final int style,
                                     final int gridStyle,
                                     final int span,
                                     final String text) {
        final Text box = new Text(parent, style | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        box.setLayoutData(gridData);
        if (text != null) {
            box.setText(text);
        }
        return box;
    }

    /**
     * This method is the minimalist version of createStyledTextBox(). It will create a StyledText instance
     * using the following default values:
     * style = SWT.WRAP | SWT.READ_ONLY
     * styleGrid = GridData.FILL_HORIZONTAL
     * background color = UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND)
     * @param parent
     * @param text
     * @return StyledText
     *
     * @since 5.0.2
     */
    public static StyledText createStyledTextBox(final Composite parent,
                                     	   		 final String text) {
    	return createStyledTextBox(parent,SWT.WRAP | SWT.READ_ONLY,text);
    }

    /**
     * This method is the creates a StyledText instance using the following default values:
     * styleGrid = GridData.FILL_HORIZONTAL
     * background color = UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND)
     * @param parent
     * @param text
     * @return StyledText
     *
     * @since 5.0.2
     */
    public static StyledText createStyledTextBox(final Composite parent,
                                     	   		 final int style,
                                     	   		 final String text) {
    	return createStyledTextBox(parent,style,GridData.FILL_HORIZONTAL,text);
    }

    /**
     * This method is the creates a StyledText instance using the following default values:
     * background color = UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND)
     * @param parent
     * @param style
     * @param gridStyle
     * @param text
     * @return StyledText
     *
     * @since 5.0.2
     */
    public static StyledText createStyledTextBox(final Composite parent,
                                     	   		 final int style,
                                     	   		 final int gridStyle,
                                     	   		 final String text) {
    	return createStyledTextBox(parent,style,gridStyle,text,UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    }

    /**
     * This method is the creates a StyledText instance using the passed values
     * @param parent
     * @param style
     * @param gridStyle
     * @param text
     * @param background
     * @return StyledText
     *
     * @since 5.0.2
     */
    public static StyledText createStyledTextBox(final Composite parent,
                                     	   		 final int style,
                                     	   		 final int gridStyle,
                                     	   		 final String text,
                                     	   		 final Color background) {
        final StyledText box = new StyledText(parent, style );
        final GridData gridData = new GridData(gridStyle);
        box.setLayoutData(gridData);
        box.setBackground(background);
        if (text != null) {
            box.setText(text);
        }
        return box;
    }

    /**
     * @since 4.0
     */
    public static Text createTextField(final Composite parent) {
        return createTextField(parent, GridData.HORIZONTAL_ALIGN_FILL);
    }

    /**
     * @since 4.0
     */
    public static Text createTextField(final Composite parent,
                                       final int gridStyle) {
        return createTextField(parent, gridStyle, 1);
    }

    /**
     * @since 4.0
     */
    public static Text createTextField(final Composite parent,
                                       final int gridStyle,
                                       final int span) {
        return createTextField(parent, gridStyle, span, null);
    }

    /**
     * Creates a {@link org.eclipse.swt.widgets.ToolBar} on the specifiec <code>ViewForm</code>. Actions can be installed in
     * the <code>ToolBar</code> by adding them to the returned <code>IToolBarManager</code>.
     *
     * @param theViewForm
     *            the view form where the toolbar is being created
     * @param theStyle
     *            the toolbar style
     * @param thePosition
     *            the position of the toolbar on the view form (SWT.LEFT, SWT.RIGHT, SWT.CENTER)
     * @return the toolbar manager
     */
    public static IToolBarManager createViewFormToolBar(final ViewForm theViewForm,
                                                        final int theStyle,
                                                        final int thePosition) {
        ToolBar tb = new ToolBar(theViewForm, theStyle);

        // position the toolbar on the view form
        if (thePosition == SWT.LEFT) {
            theViewForm.setTopLeft(tb);
        } else if (thePosition == SWT.CENTER) {
            theViewForm.setTopCenter(tb);
        } else {
            theViewForm.setTopRight(tb);
        }

        return new ToolBarManager(tb);
    }

    /**
     * Creates a {@link org.eclipse.swt.widgets.ToolBar} on the specifiec <code>ViewForm</code>. Actions can be installed in
     * the <code>ToolBar</code> by adding them to the returned <code>IToolBarManager</code>. The <code>ToolBar</code> is
     * intalled on the right and is flat and wraps.
     *
     * @param theViewForm
     *            the view form where the toolbar is being created
     * @return the toolbar manager
     */
    public static IToolBarManager createViewFormToolBar(final ViewForm theViewForm) {
        return createViewFormToolBar(theViewForm, SWT.FLAT | SWT.WRAP, SWT.RIGHT);
    }

    /**
     * @since 4.0
     */
    public static Text createTextField(final Composite parent,
                                       final int gridStyle,
                                       final String text) {
        return createTextField(parent, gridStyle, 1, text);
    }

    /**
     * @since 4.0
     */
    public static Text createTextField(final Composite parent,
                                       final int gridStyle,
                                       final int span,
                                       final String text) {
        return createTextField(parent, gridStyle, span, text, SWT.BORDER);
    }

    /**
     * @since 4.0
     */
    public static Text createTextField(final Composite parent,
                                       final int gridStyle,
                                       final int span,
                                       final String text,
                                       final int style) {
        final Text fld = new Text(parent, SWT.BORDER | style);
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        fld.setLayoutData(gridData);
        if (text != null) {
            fld.setText(text);
        }
        return fld;
    }

    /**
     * @since 4.0
     */
    public static TreeViewer createTreeViewer(final Composite parent,
                                              final int style) {
        return createTreeViewer(parent, style, GridData.FILL_BOTH);
    }

    /**
     * @since 4.0
     */
    public static TreeViewer createTreeViewer(final Composite parent,
                                              final int style,
                                              final int gridStyle) {
        return createTreeViewer(parent, style, gridStyle, null);
    }

    /**
     * @since 4.0
     */
    public static TreeViewer createTreeViewer(final Composite parent,
                                              final int style,
                                              final ITreeViewerController controller) {
        return createTreeViewer(parent, style, GridData.FILL_BOTH, controller);
    }

    /**
     * Creates a <code>TreeViewer</code> or a {@link CheckboxTreeViewer} if the <code>SWT.CHECK</code> style is passed in.
     *
     * @since 4.0
     */
    public static TreeViewer createTreeViewer(final Composite theParent,
                                              final int theStyle,
                                              final int theGridStyle,
                                              final ITreeViewerController theController) {
        TreeViewer viewer = createTreeViewer2(theParent, theStyle | SWT.BORDER, theController);
        viewer.getTree().setLayoutData(new GridData(theGridStyle));
        return viewer;
    }

    /**
     * @param parent
     * @param style
     * @param controller
     * @return
     * @since 5.0.1
     */
    public static TreeViewer createTreeViewer2(Composite parent,
                                               int style,
                                               ITreeViewerController controller) {
        style |= SWT.V_SCROLL | SWT.H_SCROLL;
        final TreeViewer viewer = (WidgetUtil.hasState(style, SWT.CHECK) ? new CheckboxTreeViewer(parent, style)
                        : new TreeViewer(parent, style));
        final ITreeViewerController ctrlr = (controller != null ? controller : new DefaultTreeViewerController());
        // Add listener to handle when a node is selected.
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(final SelectionChangedEvent event) {
                ctrlr.itemSelected(event);
            }
        });
        // Add listener to expand/collapse node when double-clicked
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(final DoubleClickEvent event) {
                final Object node = ((IStructuredSelection)event.getSelection()).getFirstElement();
                viewer.setExpandedState(node, !viewer.getExpandedState(node));
                ctrlr.itemDoubleClicked(event);
            }
        });
        // Add listener to select node when expanded/collapsed
        viewer.addTreeListener(new ITreeViewerListener() {

            public void treeCollapsed(final TreeExpansionEvent event) {
                ctrlr.itemCollapsed(event);
            }

            public void treeExpanded(final TreeExpansionEvent event) {
                ctrlr.itemExpanded(event);
            }
        });
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (!selection.isEmpty()) {
                    Object obj = selection.getFirstElement();
                    if (obj != null) {
                        TreeItem item = WidgetUtil.findTreeItem(obj, viewer);
                        if (item != null) {
                            ctrlr.update(item, false);
                        }
                    }
                }
            }
        });
        // Add listener to select node when check box selected
        if (WidgetUtil.hasStyle(viewer.getTree(), SWT.CHECK)) {
            ((CheckboxTreeViewer)viewer).addCheckStateListener(new ICheckStateListener() {

                public void checkStateChanged(CheckStateChangedEvent event) {
                    // Toggle checked state if checkbox clicked and checkable. Call update even if not toggled in order to
                    // "undo" visual checked state.
                    Object obj = event.getElement();
                    TreeItem item = WidgetUtil.findTreeItem(obj, viewer);
                    if (ctrlr.isItemCheckable(item)) {
                        WidgetUtil.setCheckedState(item, event.getChecked() ? CHECKED : UNCHECKED, true, ctrlr);
                        ctrlr.checkedStateToggled(item);
                    } else {
                        ctrlr.update(item, false);
                    }
                    // Select node
                    viewer.setSelection(new StructuredSelection(item.getData()));
                }
            });
        }
        return viewer;
    }

    /**
     * @since 4.0
     */
    public static ViewForm createViewForm(final Composite parent,
                                          final int style,
                                          final int gridStyle,
                                          final int span) {
        final ViewForm form = new ViewForm(parent, style);
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        form.setLayoutData(gridData);
        return form;
    }

    /**
     * Creates a resizable wizard dialog for the specified wizard.
     *
     * @param shell
     *            The shell under which to create the wizard dialog.
     * @param wizard
     *            The wizard to be added to the dialog.
     * @return The resizable wizard dialog.
     * @since 4.0
     */
    public static WizardDialog createWizardDialog(final Shell shell,
                                                  final IWizard wizard) {
        CoreArgCheck.isNotNull(shell);
        CoreArgCheck.isNotNull(wizard);
        return new WizardDialog(shell, wizard) {

            // Overridden to make wizard resizable
            @Override
            public void create() {
                setShellStyle(getShellStyle() | SWT.RESIZE);
                super.create();
            }
        };
    }

    /**
     * @since 4.0
     */
    public static WrappingLabel createWrappingLabel(final Composite parent,
                                                    final int gridStyle) {
        return createWrappingLabel(parent, gridStyle, 1);
    }

    /**
     * @since 4.0
     */
    public static WrappingLabel createWrappingLabel(final Composite parent,
                                                    final int gridStyle,
                                                    final int span) {
        return createWrappingLabel(parent, gridStyle, span, null);
    }

    /**
     * @since 4.0
     */
    public static WrappingLabel createWrappingLabel(final Composite parent,
                                                    final int gridStyle,
                                                    final String text) {
        return createWrappingLabel(parent, gridStyle, 1, text);
    }

    /**
     * @since 4.0
     */
    public static WrappingLabel createWrappingLabel(final Composite parent,
                                                    final int gridStyle,
                                                    final int span,
                                                    final String text) {
        return new WrappingLabel(parent, gridStyle, span, text);
    }

    // ============================================================================================================================
    // Constructors

    /**
     * Prevents instantiation.
     *
     * @since 4.0
     */
    private WidgetFactory() {
    }
}
