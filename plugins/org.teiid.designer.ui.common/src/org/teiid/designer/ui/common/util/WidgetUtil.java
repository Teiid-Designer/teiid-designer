/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.UiConstants;
import org.teiid.designer.ui.common.widget.ITreeViewerController;

/**
 * @since 8.0
 */
public final class WidgetUtil implements
                             InternalUiConstants.Widgets,
                             CoreStringUtil.Constants,
                             UiConstants {

    // ============================================================================================================================
    // Constants

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(WidgetUtil.class);

    private static final String FOLDER_SELECTION_DIALOG_TITLE = getString("folderSelectionDialogTitle"); //$NON-NLS-1$

    private static final String CONFIRM_OVERWRITE_MESSAGE_ID = "confirmOverwriteMessage"; //$NON-NLS-1$
    private static final String ERROR_CAUSE_MESSAGE_ID = "errorCauseMessage"; //$NON-NLS-1$
    private static final String FILE_EXISTS_MESSAGE_ID = "fileExistsMessage"; //$NON-NLS-1$
    private static final String FILE_EXISTS_BUT_NOT_IN_WORKSPACE_MESSAGE_ID = "fileExistsButNotInWorkspaceMessage"; //$NON-NLS-1$

    private static final String INVALID_SEVERITY_MESSAGE = getString("invalidSeverityMessage"); //$NON-NLS-1$
    private static final String NOT_RADIO_BUTTON_MESSAGE = getString("notRadioButtonMessage"); //$NON-NLS-1$
    private static final String FOLDER_SELECTION_DIALOG_MESSAGE = getString("folderSelectionDialogMessage"); //$NON-NLS-1$

    private static final int COMBO_ITEM_LIMIT = 15;

    public static final String A_END_TAG = "</a>"; //$NON-NLS-1$
    public static final String A_START_TAG = "<a>"; //$NON-NLS-1$
    public static final String FORM_TEXT_END_TAG = "</p></html>"; //$NON-NLS-1$
    public static final String FORM_TEXT_START_TAG = "<html><p>"; //$NON-NLS-1$
    
    public static final int TEXT_COLOR_DEFAULT = 0;
    public static final int TEXT_COLOR_BLUE = 1;
    

    // ============================================================================================================================
    // Static Methods

    /**
     * @since 4.0
     */
    public static boolean confirmOverwrite(final File file) {
        return showConfirmation(getString(CONFIRM_OVERWRITE_MESSAGE_ID, getFileExistsMessage(file)));
    }

    /**
     * Stores the enabled state of the specified controls, and their descendents if they are {@link Composite composites}, in the
     * specified map, then disables these controls and, if any, their descendents. The controls may be later
     * {@link #restore(Map) enabled}such that their descendents, if any, will be restored to their original enabled states.
     *
     * @param controls
     *            A list of controls to disable; may not be null.
     * @return A map of the original enabled states of the controls and, if any, their descendents; never null.
     * @since 4.0
     */
    public static Map disable(final Control[] controls) {
        CoreArgCheck.isNotNull(controls);
        final Map map = new HashMap(0);
        for (int ndx = controls.length; --ndx >= 0;) {
            final Control ctrl = controls[ndx];
            CoreArgCheck.isNotNull(ctrl);
            disable(ctrl, map);
        }
        return map;
    }

    /**
     * @since 4.0
     */
    private static void disable(final Control control,
                                final Map map) {
        if (!map.containsKey(control)) {
            map.put(control, new Boolean(control.getEnabled()));
        }
        control.setEnabled(false);
        if (control instanceof Composite) {
            final Control[] ctrls = ((Composite)control).getChildren();
            for (int ndx = ctrls.length; --ndx >= 0;) {
                disable(ctrls[ndx], map);
            }
        }
    }

    public static void disableFormText(FormText formText) {
        HyperlinkSettings settings = formText.getHyperlinkSettings();
        settings.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_NEVER);
        settings.setForeground(formText.getForeground());
        settings.setActiveForeground(formText.getForeground());
        formText.redraw();
    }

    /**
     * Convenience method that simply calls {@link #disable(Control[])}, passing the specified container in a single element
     * array.
     *
     * @param container
     *            A container to disable; may not be null.
     * @return A map of the original enabled states of the container and, if any, its descendents; never null.
     * @since 4.0
     */
    public static Map disable(final Composite container) {
        CoreArgCheck.isNotNull(container);
        return disable(new Control[] {
            container
        });
    }

    /**
     * Enables the specified controls and, if any, their descendents.
     *
     * @param controls
     *            A list of controls to enable; may not be null.
     * @since 4.0
     */
    public static void enable(final Control[] controls) {
        CoreArgCheck.isNotNull(controls);
        for (int ndx = controls.length; --ndx >= 0;) {
            final Control ctrl = controls[ndx];
            CoreArgCheck.isNotNull(ctrl);
            if (ctrl instanceof Composite) {
                enable(((Composite)ctrl).getChildren());
            }
            ctrl.setEnabled(true);
        }
    }

    /**
     * Convenience method that simply calls {@link #enable(Control[])}, passing the specified container in a single element
     * array.
     *
     * @param container
     *            A container to enable; may not be null.
     * @since 4.0
     */
    public static void enable(final Composite container) {
        enable(new Control[] {
            container
        });
    }

    public static void enableFormText(FormText formText) {
        HyperlinkSettings settings = formText.getHyperlinkSettings();
        settings.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_ALWAYS);
        settings.setForeground(JFaceColors.getHyperlinkText(formText.getDisplay()));
        settings.setActiveForeground(JFaceColors.getActiveHyperlinkText(formText.getDisplay()));
        formText.redraw();
    }

    /**
     * @return The TreeItem in the specified Tree containing the specified node, or null if not found.
     * @since 4.1
     */
    public static TreeItem findTreeItem(final Object node,
                                        final Tree tree) {
        CoreArgCheck.isNotNull(node);
        CoreArgCheck.isNotNull(tree);
        return findTreeItem(node, tree.getItems());
    }

    /**
     * @return The TreeItem in the Tree controlled by the specified TreeViewer containing the specified node, or null if not
     *         found.
     * @since 4.1
     */
    public static TreeItem findTreeItem(final Object node,
                                        final TreeViewer viewer) {
        CoreArgCheck.isNotNull(viewer);
        return findTreeItem(node, viewer.getTree());
    }

    /**
     * @since 4.1
     */
    private static TreeItem findTreeItem(final Object node,
                                         final TreeItem[] items) {
        for (int ndx = items.length; --ndx >= 0;) {
            TreeItem item = items[ndx];
            if (node == item.getData()) {
                return item;
            }
            // Search children
            if (item.getItemCount() > 0) {
                item = findTreeItem(node, item.getItems());
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * @param prefix
     *            The prefix of the message to display.
     * @param status
     *            The status containing the cause of the error or warning.
     * @return A message that includes the specified prefix, followed by a statement to see the log for details, and finally
     *         followed by the specified status' message.
     * @since 4.0
     */
    public static String getCauseMessage(final String prefix,
                                         final IStatus status) {
        return getString(ERROR_CAUSE_MESSAGE_ID, prefix, status.getMessage());
    }

    /**
     * @param prefix
     *            The prefix of the message to display.
     * @param throwable
     *            The throwable containing the cause of the error or warning.
     * @return A message that includes the specified prefix, followed by a statement to see the log for details, and finally
     *         followed by the specified throwable's message.
     * @since 4.0
     */
    public static String getCauseMessage(final String prefix,
                                         final Throwable throwable) {
        return getString(ERROR_CAUSE_MESSAGE_ID, prefix, throwable.getLocalizedMessage());
    }

    /**
     * @since 4.0
     */
    public static int getCheckedState(final TreeItem item) {
        CoreArgCheck.isNotNull(item);
        final Object obj = item.getData(CHECKED_STATE_PROPERTY);
        if (obj == null || !(obj instanceof Integer)) {
            return UNCHECKED;
        }
        return ((Integer)obj).intValue();
    }

    /**
     * Stores the checked state of all of the TreeItems in the returned Map. The Map can later be used by setCheckedStates()
     * to restore the checked state as it was when getCheckedStates() was called. If you wish to use the Map directly, its keys
     * are TreeItem.getData(), and its values are Booleans. These utility methods are necessary because, unlike
     * CheckboxTreeViewer, TreeViewer does not have this functionality itself.
     * 
     * @param ttvViewer the TreeViewer whose checked states we wish to capture
     * @return A map of the current checked state for all the nodes in the tree. {@link #setCheckedStates( TreeViewer, Map )}
     * @since 4.2
     */
    public static Map getCheckedStates(TreeViewer tvViewer) {

        HashMap hmItemCheckedStates = new HashMap();

        // get roots
        TreeItem[] tiItems = tvViewer.getTree().getItems();

        // start the recursive process on each root
        for (int i = 0; i < tiItems.length; i++) {
            TreeItem tiTemp = tiItems[i];
            getCheckedStates(hmItemCheckedStates, tiTemp);
        }

        return hmItemCheckedStates;
    }    
    
    private static void getCheckedStates(Map mapItemCheckedStates,
                                         TreeItem tiItem) {

        // process this item
        mapItemCheckedStates.put(tiItem.getData(), new Boolean(tiItem.getChecked()));

        // call this method on its children
        TreeItem[] tiChildren = tiItem.getItems();

        for (int i = 0; i < tiChildren.length; i++) {

            getCheckedStates(mapItemCheckedStates, tiChildren[i]);
        }
    }

    /**
     * @param file
     *            An existing file.
     * @return A standard "File already exists" message for the specified file.
     * @since 4.0
     */
    public static String getFileExistsMessage(final File file) {
        return getString(FILE_EXISTS_MESSAGE_ID, file.getAbsolutePath());
    }

    /**
     * @param file
     *            An existing file.
     * @return A standard "File already exists" message for the specified file.
     * @since 4.0
     */
    public static String getFileExistsMessage(final IFile file) {
        return getString(FILE_EXISTS_MESSAGE_ID, file.getFullPath().makeRelative());
    }

    /**
     * Display this message when a file exists on the file system but not in the workspace.
     *
     * @param file An existing file.
     * @return A standard "File already exists" message for the specified file.
     * @since 4.0
     */
    public static String getFileExistsButNotInWorkspaceMessage( final IFile file ) {
        return getString(FILE_EXISTS_BUT_NOT_IN_WORKSPACE_MESSAGE_ID, file.getFullPath().makeRelative());
    }

    /**
     * Returns the most appropriate displayable message contained within the specified error, analyzing embedded or chained errors
     * when appropriate.
     *
     * @since 4.0
     */
    public static String getMessage(Throwable error) {
        CoreArgCheck.isNotNull(error);
        if (error instanceof WrappedException) {
            final Throwable err = ((WrappedException)error).exception();
            if (err != null) {
                error = err;
            }
        } else if (error instanceof InvocationTargetException) {
            final Throwable err = ((InvocationTargetException)error).getTargetException();
            if (err != null) {
                error = err;
            }
        }
        final String msg = error.getLocalizedMessage();
        return (msg == null ? error.toString() : msg);
    }

    /**
     * @since 4.0
     */
    public static boolean hasState(final int state,
                                   final int queriedState) {
        return ((state & queriedState) == queriedState);
    }

    /**
     * Indicates if the given <code>Widget</code> has the given style.
     *
     * @param widget
     *            the widget being checked
     * @return <code>true</code> if widget has the given style; <code>false</code> otherwise.
     * @since 4.0
     */
    public static boolean hasStyle(final Widget widget,
                                   final int style) {
        CoreArgCheck.isNotNull(widget);
        return hasState(widget.getStyle(), style);
    }
    
    public static Color getDarkBlueColor() {
    	return getSystemColor(SWT.COLOR_DARK_BLUE);
    }
    
    public static Color getReadOnlyBackgroundColor() {
    	return getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
    }
    
    public static Color getSystemColor(int colorId) {
    	return Display.getCurrent().getSystemColor(colorId);
    }
    
    public static void colorizeWidget(Control control, int colorStyle, boolean readOnly) {
    	if( colorStyle == TEXT_COLOR_BLUE ) {
    		control.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	}
    	if( readOnly ) {
    		control.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
    	}
    	
    }

    /**
     * Initializes the specified panel with a layout and layout data.
     *
     * @param panel
     * @param gridStyle
     * @param span
     * @param columns
     * @since 5.0.1
     */
    public static void initializePanel(Composite panel,
                                       int gridStyle,
                                       int span,
                                       int columns) {
        final GridData gridData = new GridData(gridStyle);
        gridData.horizontalSpan = span;
        panel.setLayoutData(gridData);
        final GridLayout layout = new GridLayout(columns, false);
        if (WidgetUtil.hasStyle(panel, SWT.NO_TRIM)) {
            layout.marginWidth = layout.marginHeight = 0;
        }
        panel.setLayout(layout);
    }

    /**
     * Initializes dialog settings for the specified object.
     *
     * @return Dialog settings for the specified object; never null;
     * @since 4.0
     */
    public static IDialogSettings initializeSettings(final Object object,
                                                     final AbstractUIPlugin plugin) {
        // Set dialog settings, creating section if necessary
        final IDialogSettings settings = plugin.getDialogSettings();
        final String name = object.getClass().getName();
        IDialogSettings section = settings.getSection(name);
        if (section == null) {
            section = settings.addNewSection(name);
        }
        return section;
    }

    /**
     * @since 4.0
     */
    public static boolean isChecked(final TreeItem item) {
        return hasState(getCheckedState(item), CHECKED);
    }

    /**
     * @since 4.0
     */
    public static boolean isPartiallyChecked(final TreeItem item) {
        return hasState(getCheckedState(item), PARTIALLY_CHECKED);
    }

    /**
     * @since 4.0
     */
    public static boolean isUnchecked(final TreeItem item) {
        return hasState(getCheckedState(item), UNCHECKED);
    }

    /**
     * @since 4.0
     */
    public static void pack(final TableViewer viewer) {
        pack(viewer.getTable());
    }

    /**
     * @since 4.0
     */
    public static void pack(final Table table) {
        final TableColumn[] cols = table.getColumns();
        for (int ndx = 0; ndx < cols.length; ++ndx) {
            cols[ndx].pack();
        }
    }

    /**
     * @param table the table whose columns are being packed
     * @param columnWidthAddition an additional size to add to each column width
     * @since 5.5.3
     */
    public static void pack( Table table,
                             int columnWidthAddition ) {
        for (TableColumn column : table.getColumns()) {
            column.pack();
            column.setWidth(column.getWidth() + columnWidthAddition);
        }
    }

    /**
     * Removes any missing filesystem resources from the specified <code>IDialogSettings</code> for the given key. All item
     * values found for the specified key will be considered to be a resource name. If that name does not represent a current
     * filesystem resource it is removed from the list. Ideally this method should be called prior to using the settings in a
     * dialog.
     *
     * @param settings
     *            The dialog settings needing to remove missing resource values from; may not be null.
     * @param id
     *            The identifier of the text array containing resources; may not be null.
     * @since 4.1
     */
    public static void removeMissingResources(final IDialogSettings settings,
                                              final String id) {
        CoreArgCheck.isNotNull(settings);
        CoreArgCheck.isNotNull(id);

        String[] resourceItems = settings.getArray(id);

        // check to see if resources still exist
        if ((resourceItems != null) && (resourceItems.length > 0)) {
            List newItems = new ArrayList(Arrays.asList(resourceItems));
            File tmp = null;
            Iterator itr = newItems.iterator();

            while (itr.hasNext()) {
                String sItem = (String)itr.next();
                if (sItem == null) {
                    itr.remove();
                    continue;
                }
                tmp = new File(sItem);

                if (!tmp.exists()) {
                    itr.remove();
                }
            }

            // modify setting if needed
            if (resourceItems.length != newItems.size()) {
                // convert back to string array
                String[] resourceNames = new String[newItems.size()];

                for (int ndx = resourceNames.length; --ndx >= 0;) {
                    resourceNames[ndx] = (String)newItems.get(ndx);
                }

                // persist items
                settings.put(id, resourceNames);
            }
        }
    }

    /**
     * Restores the controls stored as keys in the specified map to their original enabled states, which were stored as values in
     * the map when previously {@link #disable(Control[]) disabled}. Note that normally the specified map should be set to null
     * in the calling code after calling this method, so that subsequent calls to this methods will be skipped if the map is
     * determined to be null by the calling code. This procedure, thus, prevents this method from being called unless a the
     * controls in question have been previously {@link #disable(Control[]) disabled}.
     *
     * @param A
     *            map of controls to their original enabled states; may not be null.
     * @since 4.0
     */
    public static void restore(final Map map) {
        CoreArgCheck.isNotNull(map);
        for (final Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            final Entry entry = (Entry)iter.next();
            ((Control)entry.getKey()).setEnabled(((Boolean)entry.getValue()).booleanValue());
        }
    }

    /**
     * Adds the text from the specified Combo identified by the specified ID to the text array in the specified settings, if the
     * text doesn't already exist in that array. A new array will be created if one doesn't exist. If the item count is greater
     * than the default limit (@link #COMBO_ITEM_LIMIT), items will be deleted.
     *
     * @param settings
     *            The dialog settings in which to add the text; may not be null.
     * @param id
     *            The identifier of the text array within the specified settings; may not be null.
     * @param combo
     *            The Combo from which the text should be added; may not be null.
     * @since 4.0
     */
    public static void saveSettings(final IDialogSettings settings,
                                    final String id,
                                    final Combo combo) {
        saveSettings(settings, id, combo, COMBO_ITEM_LIMIT);
    }

    /**
     * Saves a <code>Combo</code>'s items in the specified dialog settings. If the number of items in the <code>Combo</code>
     * is greater than the specified limit, least recently used items are deleted.
     *
     * @param theSettings
     *            the dialog settings being saved; may not be null.
     * @param theId
     *            the settings identifier being saved; may not be null.
     * @param theCombo
     *            the <code>Combo</code> whose items are being saved; may not be null.
     * @param theLimit
     *            the max number of values to save; must be greater than zero.
     */
    public static void saveSettings(final IDialogSettings theSettings,
                                    final String theId,
                                    final Combo theCombo,
                                    final int theLimit) {
        CoreArgCheck.isNotNull(theSettings);
        CoreArgCheck.isNotNull(theId);
        CoreArgCheck.isNotNull(theCombo);
        CoreArgCheck.isPositive(theLimit);

        String currentValue = theCombo.getText();

        // add current value if necessary
        if ((currentValue.length() > 0) && (theCombo.indexOf(currentValue) == -1)) {
            theCombo.add(currentValue);
        }

        // prune values if over the limit
        if (theCombo.getItemCount() > theLimit) {
            theCombo.remove(0, (theCombo.getItemCount() - theLimit - 1));
        }

        // save settings
        theSettings.put(theId, theCombo.getItems());
    }

    /**
     * Selects and reveals the specified object in the specified viewer only if that object is not the one and only object
     * currently selected.
     *
     * @param object
     * @param viewer
     * @since 5.0.1
     */
    public static void select(Object object,
                              Viewer viewer) {
        if (object != null) {
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
            if (selection.size() != 1 || selection.getFirstElement() != object) {
                viewer.setSelection(new StructuredSelection(object), true);
            }
        }
    }

    /**
     * Selects the given button. Also deselects the first radio button sibling it finds that is selected. The deselection event is
     * fired first and then the selection event. If the given button is already selected no selection event is fired.
     *
     * @param theButton
     *            the button being selected
     * @throws IllegalArgumentException
     *             if button is <code>null</code> or if button does not have radio style
     */
    public static void selectRadioButton(final Button button) {
        CoreArgCheck.isNotNull(button);
        CoreArgCheck.isTrue(hasStyle(button, SWT.RADIO), NOT_RADIO_BUTTON_MESSAGE);

        // first deselect currently selected button
        Composite parent = button.getParent();
        Control[] kids = parent.getChildren();

        for (int i = 0; i < kids.length; i++) {
            if ((kids[i] != button)
                && (kids[i] instanceof Button)
                && hasStyle(kids[i], SWT.RADIO)
                && ((Button)kids[i]).getSelection()) {

                // deselect and fire event
                final Button oldButton = (Button)kids[i];
                oldButton.setSelection(false);
                oldButton.notifyListeners(SWT.Selection, new Event());
                break; // assume only one selected
            }
        }

        // only fire selection if not already selected
        if (!button.getSelection()) {
            // select and fire event
            button.setSelection(true);
            button.notifyListeners(SWT.Selection, new Event());
        }
    }

    /**
     * @since 4.0
     */
    public static void setChecked(final TreeItem item,
                                  final boolean checked,
                                  final boolean selected,
                                  final ITreeViewerController controller) {
        setCheckedState(item, checked ? CHECKED : UNCHECKED, selected, controller);
    }

    /**
     * @since 4.0
     */
    public static void setCheckedState(final TreeItem item,
                                       final int state,
                                       final boolean selected,
                                       final ITreeViewerController controller) {
        CoreArgCheck.isNotNull(item);
        CoreArgCheck.isNotNull(controller);
        item.setData(CHECKED_STATE_PROPERTY, new Integer(state));
        controller.update(item, selected);
    }

    /**
     * Applies the 'checked states' in the Map to all of the TreeItems in the Table Tree managed by the TreeViewer.
     * These utility methods are needed because, unlike CheckboxTreeViewer, TreeViewer does not already have this
     * functionality.
     * 
     * @param ttvViewer
     *            the TreeViewer whose checked states we wish to restore
     * @param mapItemCheckStates
     *            the Map containing the checked state of each TreeItem as it was when the Map was created by
     *            setCheckedStates(). {@link #getCheckedStates( TreeViewer )}
     * @since 4.2
     */
    public static void setCheckedStates(TreeViewer tvViewer,
                                        Map mapItemCheckedStates) {

        // get roots
        TreeItem[] tiItems = tvViewer.getTree().getItems();

        // start the recursive process on each root
        for (int i = 0; i < tiItems.length; i++) {
            TreeItem tiTemp = tiItems[i];
            setCheckedStates(mapItemCheckedStates, tiTemp);
        }
    }

    private static void setCheckedStates(Map mapItemCheckedStates,
                                         TreeItem tiItem) {

        // process this item
        Boolean BChecked = (Boolean)mapItemCheckedStates.get(tiItem.getData());

        tiItem.setChecked(BChecked.booleanValue());

        // call this method on its children
        TreeItem[] tiChildren = tiItem.getItems();

        for (int i = 0; i < tiChildren.length; i++) {

            setCheckedStates(mapItemCheckedStates, tiChildren[i]);
        }
    }

    /**
     * @since 4.0
     */
    public static void setComboItems(final Combo combo,
                                     final List items) {
        setComboItems(combo, items, null);
    }

    /**
     * @since 4.0
     */
    public static void setComboItems(final Combo combo,
                                     final List items,
                                     final ILabelProvider provider) {
        setComboItems(combo, items, provider, false);
    }

    /**
     * @since 4.0
     */
    public static void setComboItems(final Combo combo,
                                     final List items,
                                     final ILabelProvider provider,
                                     final boolean sort) {
        CoreArgCheck.isNotNull(combo);
        setComboItems(combo, items, provider, sort, combo.getText());
    }

    /**
     * @since 4.1
     */
    public static void setComboItems(final Combo combo,
                                     final List items,
                                     final ILabelProvider provider,
                                     final boolean sort,
                                     final String selection) {
        CoreArgCheck.isNotNull(combo);
        CoreArgCheck.isNotNull(items);

        combo.removeAll();

        for (final Iterator iter = items.iterator(); iter.hasNext();) {
            final String text = (provider == null ? iter.next().toString() : provider.getText(iter.next()));
            if (sort) {
                int index = Collections.binarySearch(Arrays.asList(combo.getItems()), text);
                if (index < 0) {
                    // lt 0 means the item was not found in the list.
                    combo.add(text, -(index + 1));
                } else {
                    // gt= 0 means the item was found in the list.
                    combo.add(text, index);
                }
            } else {
                combo.add(text);
            }
        }

        if ((selection != null) && (selection.length() > 0)) {
            combo.setText(selection);
        }
    }

    /**
     * @since 4.0
     */
    public static void setComboText(final Combo combo,
                                    final Object selection) {
        setComboText(combo, selection, null);
    }

    /**
     * @since 4.0
     */
    public static void setComboText(final Combo combo,
                                    final Object selection,
                                    final ILabelProvider provider) {
        setComboText(combo, selection, provider, false);
    }

    /**
     * @param combo
     *            never null.
     * @param selection
     *            may be null, which is converted to an empty string.
     * @param provider
     *            may be null.
     * @param add
     *            True if the specified selection should be added to the Combo's list if not present.
     * @since 4.0
     */
    public static void setComboText(final Combo combo,
                                    Object selection,
                                    final ILabelProvider provider,
                                    final boolean add) {
        CoreArgCheck.isNotNull(combo);
        // Get string value of selection, using provider if provided
        final String text = (selection == null ? EMPTY_STRING : (provider == null ? selection.toString()
                        : provider.getText(selection)));
        // Add selection to list if requested and not already present
        if (add) {
            final ArrayList items = new ArrayList(Arrays.asList(combo.getItems()));
            if (!items.contains(text)) {
                items.add(text);
                setComboItems(combo, items, provider);
            }
        }
        // Set text on Combo
        combo.setText(text);
    }

    /**
     * Sets the layout data to be {{@link GridData } with standard height and width suitable for display in a dialog.
     *
     * @param button the button whose layout data is being set
     * @since 5.5.3
     */
    public static void setLayoutData( Button button ) {
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        GC gc = new GC(button);
        gc.setFont(button.getFont());
        int widthHint = Dialog.convertHorizontalDLUsToPixels(gc.getFontMetrics(), IDialogConstants.BUTTON_WIDTH);
        Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        gd.widthHint = Math.max(widthHint, minSize.x);
        button.setLayoutData(gd);
        gc.dispose();
    }

    /**
     * Gets the preferred weights of the <code>SashForm</code> based on the preferred size of it's children. Therefore, all the
     * children must have been added and completed prior to calling this method.
     *
     * @param theSash
     *            the <code>SashForm</code> whose children are being analyzed
     * @return <code>null</code> if there are no children, otherwise the preferred weights
     * @throws IllegalArgumentException
     *             if input is <code>null</code>
     */
    public static int[] getSashFormWeights(SashForm theSash) {
        CoreArgCheck.isNotNull(theSash);

        Control[] kids = theSash.getChildren();
        boolean vertical = hasStyle(theSash, SWT.VERTICAL);
        int[] weights = null;
        double total = 0;

        if (kids.length > 0) {
            List temp = new ArrayList();

            for (int i = 0; i < kids.length; i++) {
                // a Sash is put between children. these don't need a weight
                if (kids[i] instanceof Sash) {
                    continue;
                }

                Point point = kids[i].computeSize(SWT.DEFAULT, SWT.DEFAULT);
                int size = (vertical) ? point.y : point.x;
                temp.add(new Integer(size));
                total += size;
            }

            weights = new int[temp.size()];

            for (int i = 0; i < weights.length; i++) {
                int size = ((Integer)temp.get(i)).intValue();
                weights[i] = (int)Math.round(size / total * 100.0);
            }
        }
        return weights;
    }

    /**
     * @return an workbench shared image representing the status severity or <code>null</code> if not error, warning, or info
     * @since 5.5.3
     */
    public static Image getStatusImage( IStatus status ) {
        if (status.getSeverity() == IStatus.ERROR) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
        }

        if (status.getSeverity() == IStatus.WARNING) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
        }

        if (status.getSeverity() == IStatus.INFO) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
        }

        return null;
    }

    /**
     * Displays the specified status' message in the appropriate dialog relative to its severity.
     *
     * @since 4.0
     */
    public static void show(final IStatus status) {
        CoreArgCheck.isNotNull(status);
        switch (status.getSeverity()) {
            case IStatus.ERROR: {
                showError(status.getMessage());
                break;
            }
            case IStatus.WARNING: {
                showWarning(status.getMessage());
                break;
            }
            default: {
                showNotification(status.getMessage());
                break;
            }
        }
    }

    /**
     * Displays an error dialog containing a message obtained from passing the specified prefix and throwable to
     * {@link #getCauseMessage(String, Throwable)}.
     *
     * @param prefix
     *            The prefix of the message to display.
     * @param throwable
     *            The throwable containing the cause of the error or warning.
     * @since 4.0
     */
    public static void showCause(final String prefix,
                                 final Throwable throwable) {
        showCause(prefix, throwable, IStatus.ERROR);
    }

    /**
     * Displays the appropriate dialog according to the specified severity, containing a message obtained from passing the
     * specified prefix and throwable to {@link #getCauseMessage(String, Throwable)}.
     *
     * @param prefix
     *            The prefix of the message to display.
     * @param throwable
     *            The throwable containing the cause of the error or warning.
     * @param severity
     *            Either {@link IStatus#ERROR}or {@link IStatus#WARNING}.
     * @since 4.0
     */
    public static void showCause(final String prefix,
                                 final Throwable throwable,
                                 final int severity) {
        CoreArgCheck.isNotNull(prefix);
        CoreArgCheck.isNotNull(throwable);
        CoreArgCheck.isTrue(severity == IStatus.ERROR || severity == IStatus.WARNING, INVALID_SEVERITY_MESSAGE);
        show(new Status(severity, PLUGIN_ID, 0, getCauseMessage(prefix, throwable), throwable));
    }

    /**
     * Displays the specified message in a confirmation dialog.
     *
     * @param message
     *            The message to display; may not be null.
     * @since 4.0
     */
    public static boolean showConfirmation(final String message) {
        CoreArgCheck.isNotNull(message);
        final Shell shell = Display.getCurrent().getActiveShell();
        return MessageDialog.openConfirm(shell, CONFIRM_MESSAGE_TITLE, message);
    }

    /**
     * Displays the specified message in an error dialog.
     *
     * @param message
     *            The message to display; may not be null.
     * @since 4.0
     */
    public static void showError(final String message) {
        CoreArgCheck.isNotNull(message);
        final Shell shell = Display.getCurrent().getActiveShell();
        MessageDialog.openError(shell, ERROR_MESSAGE_TITLE, message);
    }

    /**
     * Displays the localize message contained within the specified Throwable in an error dialog.
     *
     * @param error
     *            The error containing the message to display; may not be null.
     * @since 4.0
     */
    public static void showError(final Throwable error) {
        showError(getMessage(error));
    }

    /**
     * @param selection
     *            An initial folder to be selected in the dialog; may be null.
     * @param filter
     *            The filter that will be used to determine which folders will appear in the dialog; may be null. Whether null or
     *            not, only accessible folders will appear within the dialog or be passed to this filter (if not null).
     * @param validator
     *            The validator that will determine whether the current selection is valid within the dialog caller's context.
     * @since 4.0
     */
    public static IContainer showFolderSelectionDialog(final IContainer selection,
                                                       final ViewerFilter filter,
                                                       final ISelectionStatusValidator validator) {
        final ViewerFilter baseFilter = new ViewerFilter() {

            @Override
            public boolean select(final Viewer viewer,
                                  final Object parent,
                                  final Object element) {
                return (element instanceof IContainer && ((IContainer)element).isAccessible() && (filter == null || filter.select(viewer,
                                                                                                                                  parent,
                                                                                                                                  element)));
            }
        };
        final Object[] objs = showWorkspaceObjectSelectionDialog(FOLDER_SELECTION_DIALOG_TITLE,
                                                                 FOLDER_SELECTION_DIALOG_MESSAGE,
                                                                 false,
                                                                 selection,
                                                                 baseFilter,
                                                                 validator);
        return (objs.length == 0 ? null : (IContainer)objs[0]);
    }

    /**
     * Displays the specified message in a notification dialog.
     *
     * @param message
     *            The message to display; may not be null.
     * @since 4.0
     */
    public static void showNotification(final String message) {
        CoreArgCheck.isNotNull(message);
        final Shell shell = Display.getCurrent().getActiveShell();
        MessageDialog.openInformation(shell, NOTIFICATION_MESSAGE_TITLE, message);
    }

    /**
     * Displays the specified message in a warning dialog.
     *
     * @param message
     *            The message to display; may not be null.
     * @since 4.0
     */
    public static void showWarning(final String message) {
        CoreArgCheck.isNotNull(message);
        final Shell shell = Display.getCurrent().getActiveShell();
        MessageDialog.openWarning(shell, WARNING_MESSAGE_TITLE, message);
    }

    /**
     * @param title
     *            The title of the selection dialog; may not be null.
     * @param message
     *            The message to display within the dialog; may not be null.
     * @param allowMultiple
     *            True if multiple objects may be selected.
     * @param selection
     *            An initial workspace object to be selected in the dialog; may be null.
     * @param filter
     *            A filter specifying which objects will appear in the dialog; may be null.
     * @param validator
     *            A selection validator specifying which objects may be selected in the dialog; may be null.
     * @since 4.0
     */
    public static Object[] showWorkspaceObjectSelectionDialog(final String title,
                                                              final String message,
                                                              final boolean allowMultiple,
                                                              final Object selection,
                                                              final ViewerFilter filter,
                                                              final ISelectionStatusValidator validator) {
        return showWorkspaceObjectSelectionDialog(title,
                                                  message,
                                                  allowMultiple,
                                                  selection,
                                                  filter,
                                                  validator,
                                                  new WorkbenchLabelProvider());
    }

    /**
     * @param title
     *            The title of the selection dialog; may not be null.
     * @param message
     *            The message to display within the dialog; may not be null.
     * @param allowMultiple
     *            True if multiple objects may be selected.
     * @param selection
     *            An initial workspace object to be selected in the dialog; may be null.
     * @param filter
     *            A filter specifying which objects will appear in the dialog; may be null.
     * @param validator
     *            A selection validator specifying which objects may be selected in the dialog; may be null.
     * @param labelProvder
     *            A label provider to set on the dialog
     * @since 4.0
     */
    public static Object[] showWorkspaceObjectSelectionDialog(final String title,
                                                              final String message,
                                                              final boolean allowMultiple,
                                                              final Object selection,
                                                              final ViewerFilter filter,
                                                              final ISelectionStatusValidator validator,
                                                              final ILabelProvider labelProvider) {
        final Shell shell = Display.getCurrent().getActiveShell();
        final ElementTreeSelectionDialog dlg = new ElementTreeSelectionDialog(shell, labelProvider,
                                                                              new WorkbenchContentProvider()) {
        };
        dlg.setAllowMultiple(allowMultiple);
        dlg.setTitle(title);
        dlg.setMessage(message);
        if (filter != null) {
            dlg.addFilter(filter);
        }
        if (validator != null) {
            dlg.setValidator(validator);
        }
        dlg.setInput(ModelerCore.getWorkspace().getRoot());
        if (selection != null) {
            dlg.setInitialSelection(selection);
        }
        if (dlg.open() == Window.OK) {
            return dlg.getResult();
        }
        return EMPTY_STRING_ARRAY;
    }

    /**
     * @param title
     *            The title of the selection dialog; may not be null.
     * @param message
     *            The message to display within the dialog; may not be null.
     * @param allowMultiple
     *            True if multiple objects may be selected.
     * @param selection
     *            An initial workspace object to be selected in the dialog; may be null.
     * @param filter
     *            A filter specifying which objects will appear in the dialog; may be null.
     * @param validator
     *            A selection validator specifying which objects may be selected in the dialog; may be null.
     * @param labelProvder
     *            A label provider to set on the dialog
     * @since 4.0
     */
    public static Object[] showWorkspaceObjectSelectionDialog(final String title,
                                                              final String message,
                                                              final boolean allowMultiple,
                                                              final Object selection,
                                                              final ViewerFilter filter,
                                                              final ISelectionStatusValidator validator,
                                                              final ILabelProvider labelProvider,
                                                              final ITreeContentProvider contentProvider) {
        final Shell shell = Display.getCurrent().getActiveShell();
        final ElementTreeSelectionDialog dlg = new ElementTreeSelectionDialog(shell, labelProvider, contentProvider) {
        };
        dlg.setAllowMultiple(allowMultiple);
        dlg.setTitle(title);
        dlg.setMessage(message);
        if (filter != null) {
            dlg.addFilter(filter);
        }
        if (validator != null) {
            dlg.setValidator(validator);
        }
        dlg.setInput(ModelerCore.getWorkspace().getRoot());
        if (selection != null) {
            dlg.setInitialSelection(selection);
        }
        if (dlg.open() == Window.OK) {
            return dlg.getResult();
        }
        return EMPTY_STRING_ARRAY;
    }
    
    /**
     * Utility method to check the changed status of a Text widget
     * 
     * @param widget
     *            The Text widget; may not be null.
     * @param targetValue
     *            The target string value of the widget
     * @return true if the target value is different than current widget value, false otherwise
     */
    public static boolean widgetValueChanged(Text widget, String targetValue) {
    	CoreArgCheck.isNotNull(widget);
    	return !StringUtilities.equals(widget.getText(), targetValue);
    }
    
    /**
     * Utility method to check the changed status of a Text widget designed for integer input values
     * 
     * @param widget
     *            The Text widget; may not be null.
     * @param targetValue
     *            The target integer value of the widget
     * @return true if the target value is different than current widget value, false otherwise
     */
    public static boolean widgetValueChanged(Text widget, int targetValue) {
    	CoreArgCheck.isNotNull(widget);
    	if( widget.getText() == null || widget.getText().length() == 0 ) {
    		return true;
    	}
    	int widgetValue = Integer.valueOf(widget.getText());
    	return widgetValue != targetValue;
    }
    
    /**
     * Utility method to check the changed status of a Combo widget
     * 
     * @param widget
     *            The Combo widget; may not be null.
     * @param targetValue
     *            The target string value of the widget
     * @return true if the target value is different than current widget value, false otherwise
     */
    public static boolean widgetValueChanged(Combo widget, String targetValue) {
    	CoreArgCheck.isNotNull(widget);
    	return !StringUtilities.equals(widget.getText(), targetValue);
    }
    
    /**
     * Utility method to check the changed status of a Button widget
     * 
     * @param widget
     *            The Button widget; may not be null.
     * @param targetValue
     *            The target string value of the widget
     * @return true if the target value is different than current widget value, false otherwise
     */
    public static boolean widgetValueChanged(Button widget, boolean selected) {
    	CoreArgCheck.isNotNull(widget);
    	return widget.getSelection() != selected;
    }

    // ============================================================================================================================
    // Static Utility Methods

    /**
     * @since 4.0
     */
    private static String getString(final String id) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.0
     */
    private static String getString(final String id,
                                    final Object parameter) {
        return Util.getString(I18N_PREFIX + id, parameter);
    }

    /**
     * @since 4.0
     */
    private static String getString(final String id,
                                    final Object parameter1,
                                    final Object parameter2) {
        return Util.getString(I18N_PREFIX + id, parameter1, parameter2);
    }

    // ============================================================================================================================
    // Constructors

    /**
     * Prevents instantiation.
     *
     * @since 4.0
     */
    private WidgetUtil() {
    }
}
