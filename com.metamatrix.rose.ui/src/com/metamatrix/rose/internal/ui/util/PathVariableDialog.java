/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.rose.internal.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * PathVariableSelectionDialog
 */
public final class PathVariableDialog extends ElementTreeSelectionDialog implements IRoseUiConstants {

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(PathVariableDialog.class);

    static final Image IMG_FOLDER = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

    static final Image IMG_FILE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

    private static final Status NO_VALUE_STATUS = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK,
                                                             UTIL.getString(PREFIX + "msg.noPathVariableValue"), //$NON-NLS-1$
                                                             null);

    private static final Status GOOD_STATUS = new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, "", //$NON-NLS-1$
                                                         null);

    private File endPt;

    private String initialValue;

    private PathVariableChooserLabelProvider labelProvider;

    private File startPt;

    private String variableName;

    private String value = ""; //$NON-NLS-1$

    private Button btnStart;

    private Button btnEnd;

    private Text txfPathValue;

    private TreeViewer viewer;

    /**
     * @param theVariableName
     * @param theInitialValue
     * @since 4.1
     */
    public PathVariableDialog( String theVariableName,
                               String theInitialValue ) {
        super(Display.getCurrent().getActiveShell(), new PathVariableChooserLabelProvider(), new FileSystemContentProvider());

        this.variableName = theVariableName;
        this.initialValue = theInitialValue;

        // filter out the files. only show directories.
        addFilter(new ViewerFilter() {
            @Override
            public boolean select( Viewer theViewer,
                                   Object theParent,
                                   Object theElement ) {
                return ((theElement instanceof File) && ((File)theElement).isDirectory());
            }
        });

        setAllowMultiple(false);
        setDoubleClickSelects(false);
        setTitle(UTIL.getString(PREFIX + "title", new Object[] {this.variableName})); //$NON-NLS-1$
        setMessage(UTIL.getString(PREFIX + "msg.dialog")); //$NON-NLS-1$
        setInput(this);
    }

    /**
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite theParent ) {
        final int COLUMNS = 2;
        Composite pnlMain = new Composite(theParent, SWT.NONE);
        pnlMain.setLayout(new GridLayout(COLUMNS, false));
        pnlMain.setLayoutData(new GridData(GridData.FILL_BOTH));

        // create super's dialog area first
        super.createDialogArea(pnlMain);

        // panel for start, end buttons
        Composite pnlButtons = WidgetFactory.createPanel(pnlMain, GridData.VERTICAL_ALIGN_CENTER);

        // panel for value label and field
        Composite pnlValue = WidgetFactory.createPanel(pnlMain, SWT.NONE, GridData.FILL_HORIZONTAL, COLUMNS, 2);

        //
        // pnlButtons content
        //

        // select all button
        this.btnStart = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.start"), //$NON-NLS-1$
                                                   GridData.FILL_HORIZONTAL);
        this.btnStart.setEnabled(false);
        this.btnStart.setToolTipText(UTIL.getString(PREFIX + "button.start.tip")); //$NON-NLS-1$
        this.btnStart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleStartSelected();
            }
        });

        // unselect all button
        this.btnEnd = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.end"), //$NON-NLS-1$
                                                 GridData.FILL_HORIZONTAL);
        this.btnEnd.setEnabled(false);
        this.btnEnd.setToolTipText(UTIL.getString(PREFIX + "button.end.tip")); //$NON-NLS-1$
        this.btnEnd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleEndSelected();
            }
        });

        //
        // pnlValue contents
        //

        // label for path value
        WidgetFactory.createLabel(pnlValue, UTIL.getString(PREFIX + "label.pathValue", //$NON-NLS-1$
                                                           new Object[] {this.variableName}));

        // textfield for path value
        this.txfPathValue = WidgetFactory.createTextField(pnlValue, GridData.FILL_HORIZONTAL);
        this.txfPathValue.setText((this.initialValue == null) ? "" : this.initialValue); //$NON-NLS-1$
        this.txfPathValue.setEditable(false);
        this.txfPathValue.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                handleValueChanged();
            }
        });

        return pnlMain;
    }

    /**
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected TreeViewer createTreeViewer( Composite theParent ) {
        this.viewer = super.createTreeViewer(theParent);
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleDirectorySelected();
            }
        });

        this.labelProvider = (PathVariableChooserLabelProvider)this.viewer.getLabelProvider();

        return this.viewer;
    }

    private List getObjectsToHighlight() {
        List result = null;

        if ((this.startPt != null) || (this.endPt != null)) {
            result = new ArrayList();

            if ((this.startPt == null) && (this.endPt != null)) {
                // add all objects from end pt to root
                result.add(this.endPt);
                File parent = this.endPt.getParentFile();

                while (parent != null) {
                    result.add(parent);
                    parent = parent.getParentFile();
                }
            } else if (this.endPt == null) {
                result.add(this.startPt);
            } else {
                // both set
                result.add(this.endPt);
                File parent = this.endPt.getParentFile();

                while (parent != null) {
                    result.add(parent);

                    if (parent.equals(this.startPt)) {
                        break;
                    }

                    parent = parent.getParentFile();
                }
            }
        }

        return (result == null) ? Collections.EMPTY_LIST : result;
    }

    private File getSelectedDirectory() {
        ISelection selection = this.viewer.getSelection();
        return (selection.isEmpty()) ? null : (File)((IStructuredSelection)selection).getFirstElement();

    }

    /**
     * @return
     * @since 4.1
     */
    public String getValue() {
        return this.value;
    }

    void handleDirectorySelected() {
        boolean enableStart = (getSelectedDirectory() != null);
        boolean enableEnd = enableStart;

        if (enableStart) {
            File dir = getSelectedDirectory();

            if (this.startPt != null) {
                // if not equal to or descendent of startPt then disable
                enableEnd = dir.equals(this.startPt) || isAncestor(this.startPt, dir);
            }
        }

        this.btnStart.setEnabled(enableStart);
        this.btnEnd.setEnabled(enableEnd);
    }

    void handleEndSelected() {
        this.endPt = getSelectedDirectory();
        setValue();
    }

    void handleStartSelected() {
        this.startPt = getSelectedDirectory();

        // if end pt selected and not a descendant of this new start pt null out end pt
        if (this.endPt != null) {
            if (!isAncestor(this.startPt, this.endPt)) {
                this.endPt = null;
            }
        }

        setValue();
    }

    void handleValueChanged() {
        updateButtonsEnableState((this.value.length() == 0) ? NO_VALUE_STATUS : GOOD_STATUS);
    }

    private boolean isAncestor( File theAncestor,
                                File theDescendant ) {
        boolean result = false;
        File parent = theDescendant.getParentFile();

        while (parent != null) {
            if (parent.equals(theAncestor)) {
                result = true;
                break;
            }
            return isAncestor(theAncestor, parent);
        }

        return result;
    }

    private void setValue() {
        if (this.startPt == null) {
            this.value = (this.endPt == null) ? "" //$NON-NLS-1$
            : this.endPt.getAbsolutePath();
        } else {
            if (this.endPt == null) {
                String name = this.startPt.getName();
                this.value = (name.length() == 0) ? this.startPt.getAbsolutePath() : name;
            } else {
                String startPath = this.startPt.getAbsolutePath();
                String startName = this.startPt.getName();
                String endPath = this.endPt.getAbsolutePath();

                this.value = endPath.substring(startPath.length() - startName.length());
            }
        }

        this.txfPathValue.setText(this.value);

        // inform label provider of new values
        Collection oldHighlights = this.labelProvider.getSpecialObjects();
        Collection newHighlights = new HashSet(getObjectsToHighlight());

        this.labelProvider.setSpecialObjects(new ArrayList(newHighlights));

        // refresh tree with objects from both old and new collections
        newHighlights.addAll(oldHighlights);
        Iterator itr = newHighlights.iterator();

        while (itr.hasNext()) {
            getTreeViewer().refresh(itr.next(), true);
        }
    }

    /**
     * @see org.eclipse.ui.dialogs.SelectionStatusDialog#updateButtonsEnableState(org.eclipse.core.runtime.IStatus)
     */
    @Override
    protected void updateButtonsEnableState( IStatus theStatus ) {
        super.updateButtonsEnableState((this.value.length() == 0) ? NO_VALUE_STATUS : theStatus);
    }

    static class PathVariableChooserLabelProvider extends LabelProvider implements IColorProvider {

        private Collection specialObjects;

        /**
         * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
         */
        public Color getBackground( Object theElement ) {
            Color result = null; // to use default color

            if ((this.specialObjects != null) && this.specialObjects.contains(theElement)) {
                result = UiUtil.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
         */
        public Color getForeground( Object theElement ) {
            return null; // to use default color
        }

        /**
         * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object theElement ) {
            Image result = null;

            if (theElement instanceof File) {
                result = (((File)theElement).isDirectory() ? IMG_FOLDER : IMG_FILE);
            }

            return result;
        }

        /**
         * @return
         * @since 4.1
         */
        public Collection getSpecialObjects() {
            return (this.specialObjects == null) ? Collections.EMPTY_LIST : this.specialObjects;
        }

        /**
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object theElement ) {
            String result = null;

            if (theElement instanceof File) {
                result = ((File)theElement).getName();

                if (result.length() == 0) {
                    result = ((File)theElement).getAbsolutePath();
                }
            }

            return (result == null) ? super.getText(theElement) : result;
        }

        /**
         * @param theObjects
         * @since 4.1
         */
        public void setSpecialObjects( Collection theObjects ) {
            this.specialObjects = theObjects;
        }
    }

    static class FileSystemContentProvider implements ITreeContentProvider {
        private final Object[] EMPTY = new Object[0];

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        public Object[] getChildren( Object theParent ) {
            Object[] result = null;

            if (theParent instanceof File) {
                result = ((File)theParent).listFiles();
            }

            return (result == null) ? this.EMPTY : result;
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object theInputElement ) {
            return File.listRoots();
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        public Object getParent( Object theElement ) {
            return (theElement instanceof File) ? ((File)theElement).getParentFile() : null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        public boolean hasChildren( Object theElement ) {
            Object[] kids = getChildren(theElement);
            return ((kids != null) && (kids.length > 0));
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
        }
    }
}
