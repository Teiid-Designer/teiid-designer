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

package com.metamatrix.modeler.internal.ui.cheatsheets;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.internal.cheatsheets.CheatSheetPlugin;
import org.eclipse.ui.internal.cheatsheets.ICheatSheetResource;
import org.eclipse.ui.internal.cheatsheets.views.CheatSheetView;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * @since 5.0
 */
public class ExtendedCheatSheetView extends CheatSheetView implements UiConstants {

    private static final String EXT_PT = "cheatSheetContent"; //$NON-NLS-1$
    private static final String ID_ATTR = "id"; //$NON-NLS-1$
    private static final String NAME_ATTR = "name"; //$NON-NLS-1$
    private static final String CHEATSHEET_ELEMENT = "cheatsheet"; //$NON-NLS-1$

    private IConfigurationElement[] cheatsheets;

    private Set filters;

    private Viewer directoryViewer;

    public ExtendedCheatSheetView() {
        this.cheatsheets = new IConfigurationElement[0];
        this.filters = new HashSet();
    }

    public void addFilter( ViewerFilter theFilter ) {
        this.filters.add(theFilter);
    }

    /**
     * @see org.eclipse.ui.internal.cheatsheets.views.CheatSheetView#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
    @Override
    public void createPartControl( Composite theParent ) {
        SashForm splitter = new SashForm(theParent, SWT.VERTICAL);
        splitter.setLayout(new GridLayout());
        splitter.setLayoutData(new GridData(GridData.FILL_BOTH));

        // add superclass parts
        super.createPartControl(splitter);

        // add directory of cheatsheets at bottom
        Group group = new Group(splitter, SWT.NONE);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        group.setText(Util.getStringOrKey("ExtendedCheatSheetView.directoryGroup.title")); //$NON-NLS-1$
        group.setFont(JFaceResources.getBannerFont());

        TableViewer tv = new TableViewer(group, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
        tv.setLabelProvider(new CheatSheetLabelProvider());
        tv.setContentProvider(new IStructuredContentProvider() {
            public void dispose() {
            }

            public Object[] getElements( Object theInputElement ) {
                return getCheatSheets();
            }

            public void inputChanged( Viewer theViewer,
                                      Object theOldInput,
                                      Object theNewInput ) {
            }
        });
        tv.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                StructuredSelection selection = (StructuredSelection)theEvent.getSelection();
                handleCheatSheetSelected((IConfigurationElement)selection.getFirstElement());
            }
        });
        tv.setSorter(new DefaultSorter());

        Table table = tv.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        this.directoryViewer = tv;

        // set weights after finished adding both sides
        splitter.setWeights(new int[] {80, 20});

        // load extensions and populate viewer
        loadExtensions();
        this.directoryViewer.setInput(this);
    }

    IConfigurationElement[] getCheatSheets() {
        return this.cheatsheets;
    }

    void handleCheatSheetSelected( IConfigurationElement theCheatSheet ) {
        if (!this.directoryViewer.getControl().isDisposed()) {
            super.setInput(theCheatSheet.getAttribute(ID_ATTR));
        }
    }

    private boolean isCheatSheetIncluded( IConfigurationElement theCheatSheet ) {
        boolean result = true;

        Iterator itr = this.filters.iterator();

        while (itr.hasNext()) {
            ViewerFilter filter = (ViewerFilter)itr.next();

            if (!filter.select(this.directoryViewer, null, theCheatSheet)) {
                result = false;
                break;
            }
        }

        return result;
    }

    private void loadExtensions() {
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(ICheatSheetResource.CHEAT_SHEET_PLUGIN_ID,
                                                                                           EXT_PT);

        if (extensionPoint != null) {
            IExtension[] extensions = extensionPoint.getExtensions();

            if (extensions.length != 0) {
                List temp = new ArrayList();

                for (int i = 0; i < extensions.length; ++i) {
                    IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                    // only care about cheatsheet configuration elements. don't care about category elements.
                    for (int j = 0; j < elements.length; ++j) {
                        if (elements[j].getName().equals(CHEATSHEET_ELEMENT) && isCheatSheetIncluded(elements[j])) {
                            temp.add(elements[j]);
                        }
                    }
                }

                if (!temp.isEmpty()) {
                    temp.toArray(this.cheatsheets = new IConfigurationElement[temp.size()]);
                }
            }
        } else {

        }
    }

    class CheatSheetLabelProvider extends LabelProvider implements ITableLabelProvider {
        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @since 5.0
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            return CheatSheetPlugin.getPlugin().getImageRegistry().get(ICheatSheetResource.CHEATSHEET_OBJ);
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @since 5.0
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {
            String result = null;

            if (theElement instanceof IConfigurationElement) {
                result = ((IConfigurationElement)theElement).getAttribute(NAME_ATTR);
            }

            return result;
        }
    }

    class DefaultSorter extends ViewerSorter {
        /**
         * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 5.0
         */
        @Override
        public int compare( Viewer theViewer,
                            Object theFirst,
                            Object theSecond ) {
            return Collator.getInstance().compare(((IConfigurationElement)theFirst).getAttribute(NAME_ATTR),
                                                  ((IConfigurationElement)theSecond).getAttribute(NAME_ATTR));
        }
    }
}
