/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.wizard;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.ui.UiConstants;

/**<p>
 * </p>
 * @since 4.0
 */
public abstract class AbstractSelectionWizard extends AbstractWizard implements StringUtil.Constants,
                                                                                UiConstants.Images {
    //============================================================================================================================
    // Variables

    private IWorkbench workbench;
    private IStructuredSelection selection;
    private String id;
    private IConfigurationElement elem;
    private IWizard wizard;
    private ViewerSorter sorter;

    //============================================================================================================================
    // Constructors

    /**<p>
     * </p>
     * @since 4.0
     */
    public AbstractSelectionWizard(final AbstractUIPlugin plugin,
                                   final IWorkbench workbench,
                                   final IStructuredSelection selection,
                                   final String title,
                                   final ImageDescriptor imageDescriptor,
                                   final String id,
                                   final ViewerSorter sorter) {
        super(plugin, title, null);
        ArgCheck.isNotNull(workbench);
        ArgCheck.isNotNull(selection);
        ArgCheck.isNotNull(id);
        this.workbench = workbench;
        this.selection = selection;
        this.id = id;
        this.sorter = sorter;

        // Set default page icon
        setDefaultPageImageDescriptor(imageDescriptor);
    }

    //============================================================================================================================
    // Implemented Methods

    /**<p>
     * </p>
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public final boolean finish() {
        return false;
    }

    //============================================================================================================================
    // Overridden Methods

    /**<p>
     * </p>
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     * @since 4.0
     */
    @Override
    public final void addPages() {
        // Add selection page if more than one wizard available
        final IConfigurationElement[] elems = getConfigurationElementsFor();
        if (elems.length > 0) {
            addPage(new WizardSelectionPage(elems, sorter));
            setForcePreviousAndNextButtons(true);
        }
    }

    //============================================================================================================================
    // Overridden Methods

    /*
     * Method which allows wizards to override this method and user it to filter visible contents of the wizard.
     * Initially needed to customize our ImportWizard/ExportWizard so we can control the available imports/exports
     */
    protected IConfigurationElement[] getConfigurationElementsFor() {
        // Add selection page if more than one wizard available
        return PluginUtilities.getConfigurationElementsFor(id);

    }

    /**<p>
     * </p>
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     * @since 4.0
     */
    @Override
    public final boolean canFinish() {
        return false;
    }

    /**<p>
     * </p>
     * @see org.eclipse.jface.wizard.Wizard#dispose()
     * @since 4.0
     */
    @Override
    public final void dispose() {
        if (this.wizard != null) {
            this.wizard.dispose();
        }
        super.dispose();
    }

    /**<p>
     * </p>
     * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     * @since 4.0
     */
    @Override
    public final IWizardPage getNextPage(final IWizardPage page) {
        if (this.wizard != null) {
            this.wizard.dispose();
        }
        setForcePreviousAndNextButtons(false);
        return createWizard().getStartingPage();
    }

    //============================================================================================================================
    // MVC View Methods

    /**<p>
     * Does nothing.
     * </p>
     * @since 4.0
     */
    protected void initializeSelectedWizard(final IWizard wizard,
                                            final IWorkbench workbench,
                                            final IStructuredSelection selection) {
    }

    //============================================================================================================================
    // Property Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    IWizard createWizard() {
        this.wizard = createSelectedWizard(this.elem);
        initializeSelectedWizard(this.wizard, this.workbench, this.selection);
        this.wizard.addPages();
        return this.wizard;
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    IConfigurationElement getSelectedWizard() {
        return this.elem;
    }

    /**<p>
     * </p>
     * @since 4.0
     */
    void setSelectedWizard(final IStructuredSelection selection) {
        if (selection.isEmpty()) {
            this.elem = null;
        } else {
            this.elem = (IConfigurationElement)selection.getFirstElement();
        }
    }

    //============================================================================================================================
    // Abstract Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    protected abstract IWizard createSelectedWizard(IConfigurationElement element);

    /**<p>
     * </p>
     * @since 4.0
     */
    protected abstract String getSelectedWizardIcon(IConfigurationElement element);

    /**<p>
     * </p>
     * @since 4.0
     */
    protected abstract String getSelectedWizardName(IConfigurationElement element);
}
