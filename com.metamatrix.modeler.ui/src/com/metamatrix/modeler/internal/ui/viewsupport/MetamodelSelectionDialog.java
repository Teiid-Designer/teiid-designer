/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * MetamodelSelectionDialog is a tree selection dialog specialized to optionally filter out all non-primary metamodels.
 */
public class MetamodelSelectionDialog extends ElementTreeSelectionDialog {

    private static final String PRIMARY_METAMODELS = UiConstants.Util.getString("MetamodelClassDialog.primaryOnly"); //$NON-NLS-1$
    private static final String ALL_METAMODELS = UiConstants.Util.getString("MetamodelClassDialog.allMetamodels"); //$NON-NLS-1$

    private boolean primaryMetamodelsOnly = true;
    Collection primaryMetamodelResources;
    TreeViewer treeViewer;
    private PrimaryMetamodelFilter filter;
    private ILabelProvider labelProvider;

    /**
     * Construct an instance of MetamodelSelectionDialog.
     * 
     * @param parent
     * @param labelProvider
     * @param contentProvider
     */
    public MetamodelSelectionDialog( Shell parent,
                                     ILabelProvider labelProvider,
                                     ITreeContentProvider contentProvider ) {
        super(parent, labelProvider, contentProvider);
        this.labelProvider = labelProvider;
    }

    /**
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected TreeViewer createTreeViewer( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout());
        treeViewer = super.createTreeViewer(panel);

        GridData gd = new GridData(GridData.FILL_BOTH);
        panel.setLayoutData(gd);

        Button radioButton = WidgetFactory.createRadioButton(panel, PRIMARY_METAMODELS, true);
        radioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setMetamodelFilter(true);
            }
        });

        radioButton = WidgetFactory.createRadioButton(panel, ALL_METAMODELS, false);
        radioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setMetamodelFilter(false);
            }
        });

        filter = new PrimaryMetamodelFilter();
        treeViewer.addFilter(filter);

        return treeViewer;
    }

    public IBaseLabelProvider getViewerLabelProvider() {
        return this.labelProvider;
    }

    protected void setMetamodelFilter( boolean primaryOnly ) {
        this.primaryMetamodelsOnly = primaryOnly;
        if (this.primaryMetamodelsOnly) {
            filter.enable = true;
            // treeViewer.setInput(this.primaryMetamodelDescriptors);
        } else {
            // treeViewer.setInput(this.metamodelDescriptors);
            filter.enable = false;
        }
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                treeViewer.refresh();
            }
        });
    }

    /**
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#setInput(java.lang.Object)
     */
    @Override
    public void setInput( Object input ) {
        if (input instanceof Collection) {
            Collection metamodelDescriptors = (Collection)input;

            // build a collection of all the MetamodelResources that are primary metamodels
            // ... this collection is for the filter to use
            primaryMetamodelResources = new ArrayList(metamodelDescriptors.size());
            for (Iterator iter = metamodelDescriptors.iterator(); iter.hasNext();) {
                MetamodelDescriptor mmd = (MetamodelDescriptor)iter.next();
                if (mmd.isPrimary()) {
                    String stringUri = mmd.getNamespaceURI();
                    URI uri = URI.createURI(stringUri);
                    Resource r = ModelerCore.getMetamodelRegistry().getResource(uri);
                    primaryMetamodelResources.add(r);
                }
            }
        }

        super.setInput(input);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    protected Control createContents( Composite theParent ) {
        Control control = super.createContents(theParent);

        // setting the initial selection was not working with this subclass. Adding this code fixes it.
        getTreeViewer().setSelection(new StructuredSelection(getInitialElementSelections()), true);
        updateOKStatus();

        return control;
    }

    public class PrimaryMetamodelFilter extends ViewerFilter {

        public boolean enable = true;

        /**
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        @Override
        public boolean select( Viewer viewer,
                               Object parentElement,
                               Object element ) {
            if (enable) {
                if (element instanceof Resource) {
                    return primaryMetamodelResources.contains(element);
                }
            }
            return true;
        }

    }

}
