/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.wizard;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.AbstractTableLabelProvider;

/**
 * @since 4.0
 */
final class WizardSelectionPage extends WizardPage implements UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(WizardSelectionPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$

    private static final String SELECT_LABEL = getString("selectLabel"); //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    IConfigurationElement[] elems;
    private ViewerSorter sorter;

    /**
     * @since 4.0
     */
    public WizardSelectionPage( final IConfigurationElement[] elements,
                                final ViewerSorter sorter ) {
        super(WizardSelectionPage.class.getSimpleName(), TITLE, null);
        ArgCheck.isNotNull(elements);
        this.elems = elements;
        this.sorter = sorter;
        setMessage(INITIAL_MESSAGE);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    public void createControl( final Composite parent ) {
        // Create page
        final Composite pg = WidgetFactory.createPanel(parent);
        setControl(pg);
        // Add widgets to page
        final Label label = new Label(pg, SWT.NONE);
        label.setText(SELECT_LABEL);
        final TableViewer viewer = WidgetFactory.createTableViewer(pg);
        viewer.setContentProvider(new IStructuredContentProvider() {
            public void dispose() {
            }

            public Object[] getElements( final Object inputElement ) {
                return WizardSelectionPage.this.elems;
            }

            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
            }
        });
        viewer.setLabelProvider(new AbstractTableLabelProvider() {
            @Override
            public Image getColumnImage( final Object element,
                                         final int column ) {
                try {
                    final AbstractSelectionWizard wizard = (AbstractSelectionWizard)getWizard();
                    final String icon = wizard.getSelectedWizardIcon((IConfigurationElement)element);
                    if (icon != null) {
                        final URL baseUrl = Platform.getBundle(((IConfigurationElement)element).getDeclaringExtension().getNamespaceIdentifier()).getEntry("/"); //$NON-NLS-1$
                        final URL url = new URL(baseUrl, icon);
                        return ImageDescriptor.createFromURL(url).createImage();
                    }
                } catch (final MalformedURLException err) {
                    Util.log(err);
                }
                return ImageDescriptor.getMissingImageDescriptor().createImage();
            }

            public String getColumnText( final Object element,
                                         final int column ) {
                final AbstractSelectionWizard wizard = (AbstractSelectionWizard)getWizard();
                return wizard.getSelectedWizardName((IConfigurationElement)element);
            }
        });
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( final SelectionChangedEvent event ) {
                IStructuredSelection sel = (IStructuredSelection)event.getSelection();
                ((AbstractSelectionWizard)getWizard()).setSelectedWizard(sel);

                if (sel != null && !sel.isEmpty()) {
                    IConfigurationElement elem = (IConfigurationElement)sel.getFirstElement();
                    WorkbenchWizardElement wwe = new WorkbenchWizardElement(elem);
                    String message = wwe.getDescription();
                    setMessage(message);
                }
            }
        });
        if (sorter != null) viewer.setSorter(sorter);
        viewer.setInput(this);
        viewer.setSelection(new StructuredSelection(viewer.getElementAt(0)));
    }

    /**
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     * @since 4.0
     */
    @Override
    public boolean canFlipToNextPage() {
        return (((AbstractSelectionWizard)getWizard()).getSelectedWizard() != null);
    }
}
