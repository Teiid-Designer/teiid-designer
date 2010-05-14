/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractSelectionWizard;

/**
 * @since 4.0
 */
public class ExportWizard extends AbstractSelectionWizard implements UiConstants, UiConstants.ExtensionPoints.ExportWizards {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ExportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.0
     */
    public ExportWizard( final IWorkbench workbench,
                         final IStructuredSelection selection ) {
        super(UiPlugin.getDefault(), workbench, selection, TITLE,
              WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_WIZBAN_EXPORT_WIZ), ID,
              new SingleColumnTableViewerSorter());
    }

    @Override
    protected IConfigurationElement[] getConfigurationElementsFor() {
        IConfigurationElement[] elements = super.getConfigurationElementsFor();

        // FILTER THESE!!
        List result = new ArrayList(elements.length);
        for (int i = 0; i < elements.length; i++) {
            String contribID = elements[i].getAttribute(UiConstants.ExtensionPoints.ExportWizards.ID_ID);
            if (UiPlugin.getDefault().isProductContextValueSupported(IModelerProductContexts.Contributions.EXPORT, contribID)) {
                result.add(elements[i]);
            }
        }

        return (IConfigurationElement[])result.toArray(new IConfigurationElement[result.size()]);
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractSelectionWizard#createSelectedWizard()
     * @since 4.0
     */
    @Override
    protected IWizard createSelectedWizard( final IConfigurationElement element ) {
        try {
            return (IWizard)element.createExecutableExtension(CLASS);
        } catch (final CoreException err) {
            Util.log(err);
            WidgetUtil.showError(err);
            return null;
        }
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractSelectionWizard#getSelectedWizardIcon(org.eclipse.core.runtime.IConfigurationElement)
     * @since 4.0
     */
    @Override
    protected String getSelectedWizardIcon( final IConfigurationElement element ) {
        return element.getAttribute(ICON);
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractSelectionWizard#getSelectedWizardName(org.eclipse.core.runtime.IConfigurationElement)
     * @since 4.0
     */
    @Override
    protected String getSelectedWizardName( final IConfigurationElement element ) {
        return element.getAttribute(NAME);
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractSelectionWizard#initializeSelectedWizard(org.eclipse.jface.wizard.IWizard)
     * @since 4.0
     */
    @Override
    protected void initializeSelectedWizard( final IWizard wizard,
                                             final IWorkbench workbench,
                                             final IStructuredSelection selection ) {
        ((IExportWizard)wizard).init(workbench, selection);
    }
}
