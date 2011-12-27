/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui.translators;

import static com.metamatrix.modeler.vdb.ui.VdbUiConstants.Util;

import java.util.Arrays;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import com.metamatrix.core.util.I18nUtil;

/**
 * Use for adding a translator override when translator types are available.
 */
class ChooseTranslatorOverrideDialog extends ListDialog {

    static final String PREFIX = I18nUtil.getPropertyPrefix(ChooseTranslatorOverrideDialog.class);

    /**
     * @param parent
     * @param translatorTypes
     */
    public ChooseTranslatorOverrideDialog( Shell parent,
                                           final String[] translatorTypes ) {
        super(parent);
        setContentProvider(new IStructuredContentProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( Object inputElement ) {
                return ((translatorTypes == null) ? new Object[0] : translatorTypes);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public void inputChanged( Viewer viewer,
                                      Object oldInput,
                                      Object newInput ) {
                // nothing to do
            }
        });

        setLabelProvider(new LabelProvider());
        setTitle(Util.getString(PREFIX + "title")); //$NON-NLS-1$
        String message;

        if ((translatorTypes == null) || (translatorTypes.length == 0)) {
            message = Util.getString(PREFIX + "noTranslatorsMsg"); //$NON-NLS-1$
            setAddCancelButton(false);
        } else {
            message = Util.getString(PREFIX + "chooseTranslatorMsg"); //$NON-NLS-1$
        }

        setMessage(message);
        setInput(this);
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.dialogs.ListDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite container ) {
        Control control = super.createDialogArea(container);

        // sort the translators
        getTableViewer().setSorter(new ViewerSorter() {
            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.ViewerComparator#sort(org.eclipse.jface.viewers.Viewer, java.lang.Object[])
             */
            @Override
            public void sort( Viewer viewer,
                              Object[] elements ) {
                Arrays.sort(elements);
            }
        });
        
        getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                boolean enable =  !event.getSelection().isEmpty();
                
                if (getOkButton().isEnabled() != enable) {
                    getOkButton().setEnabled(enable);
                }
            }
        });
        
        return control;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.TrayDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createButtonBar( Composite parent ) {
        Control control =  super.createButtonBar(parent);
        
        // initially disable since we are not setting initial selection
        getOkButton().setEnabled(false);
        
        return control;
    }
    
}