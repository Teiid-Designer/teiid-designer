/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.ui.common.util.WizardUtil;


/**
 * <p>
 * An extension of Wizard that provides the following additional features:
 * </p>
 * <ul>
 * <li>Dynamic manipulation of pages during wizard execution.</li>
 * </ul>
 * <p>
 * </p>
 * 
 * @since 8.0
 */
public abstract class AbstractWizard extends Wizard {
    // ============================================================================================================================
    // Constants

    private static final String WIDTH = "width"; //$NON-NLS-1$
    private static final String HEIGHT = "height"; //$NON-NLS-1$

    // ============================================================================================================================
    // Variables

    private final List pgs;

    // ============================================================================================================================
    // Constructors

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public AbstractWizard( final AbstractUIPlugin plugin,
                           final String title,
                           final ImageDescriptor image ) {
        this.pgs = new ArrayList();
        WizardUtil.initialize(this, plugin, title, image);
    }

    // ============================================================================================================================
    // Overridden Methods

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPage(org.eclipse.jface.wizard.IWizardPage)
     * @since 4.0
     */
    @Override
    public final void addPage( final IWizardPage page ) {
        addPage(page, this.pgs.size());
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public final void addPage( final IWizardPage page,
                               final int index ) {
        CoreArgCheck.isNotNull(page);
        CoreArgCheck.isNonNegative(index);
        this.pgs.add(index, page);
        page.setWizard(this);
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        // Can finish if all pages are complete
        for (final Iterator iter = this.pgs.iterator(); iter.hasNext();) {
            if (!((IWizardPage)iter.next()).isPageComplete()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indicates if the wizard can go to the page after the specified page.
     * 
     * @param thePage the page requested to flip to the next page
     * @return <code>true</code> if can go to next page; <code>false</code> otherwise.
     * @since 4.1
     */
    public boolean canFlipToNextPage( final IWizardPage thePage ) {
        final int index = indexOf(thePage);
        return ((index != -1) && thePage.isPageComplete() && ((getPageCount() - 1) > index));
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    public void createPageControls( final Composite pageContainer ) {
        // Default behavior is to size to previously saved size
        createPageControls(pageContainer, true);
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    public void createPageControls( final Composite pageContainer,
                                    final boolean restorePrevSize ) {
        CoreArgCheck.isNotNull(pageContainer);
        for (final Iterator iter = this.pgs.iterator(); iter.hasNext();) {
            final IWizardPage pg = (IWizardPage)iter.next();
            pg.createControl(pageContainer);
            // page is responsible for ensuring the created control is accessible
            // via getControl.
            Assert.isNotNull(pg.getControl());
        }
        // Add resize listener to container that will save size in settings when user changes it
        final IDialogSettings settings = getDialogSettings();
        final Shell shell = getShell();
        shell.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized( final ControlEvent event ) {
                if (shell.isVisible()) {
                    final Point size = shell.getSize();
                    settings.put(WIDTH, size.x);
                    settings.put(HEIGHT, size.y);
                }
            }
        });
        // Restore container's previous size if desired
        if (restorePrevSize) {
            try {
                shell.setSize(settings.getInt(WIDTH), settings.getInt(HEIGHT));
                shell.layout();

                // reposition shell if necessary when bounds are off-screen

                final Rectangle screenSize = shell.getDisplay().getClientArea();
                final Rectangle bounds = shell.getBounds();
                int newX = -1;
                int newY = -1;

                if (bounds.x + bounds.width > screenSize.width) {
                    newX = bounds.x - (bounds.width + bounds.x - screenSize.width);
                }

                if (bounds.y + bounds.height > screenSize.height) {
                    newY = bounds.y - (bounds.height + bounds.y - screenSize.height);
                }

                if ((newX != -1) || (newY != -1)) {
                    shell.setLocation((newX == -1) ? bounds.x : newX, (newY == -1) ? bounds.y : newY);
                }
            } catch (final NumberFormatException ignored) {
                // Not even worth logging
            }
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        for (final Iterator iter = this.pgs.iterator(); iter.hasNext();) {
            ((IWizardPage)iter.next()).dispose();
        }
        super.dispose();
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public abstract boolean finish();

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     * @since 4.0
     */
    @Override
    public IWizardPage getNextPage( final IWizardPage page ) {
        CoreArgCheck.isNotNull(page);
        final int ndx = indexOf(page);
        // Return null if last page or page not found
        if (ndx == this.pgs.size() - 1 || ndx < 0) {
            return null;
        }
        return (IWizardPage)this.pgs.get(ndx + 1);
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#getPage(java.lang.String)
     * @since 4.0
     */
    @Override
    public final IWizardPage getPage( final String name ) {
        CoreArgCheck.isNotNull(name);
        for (final Iterator iter = this.pgs.iterator(); iter.hasNext();) {
            final IWizardPage pg = (IWizardPage)iter.next();
            if (name.equals(pg.getName())) {
                return pg;
            }
        }
        return null;
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#getPageCount()
     * @since 4.0
     */
    @Override
    public int getPageCount() {
        return this.pgs.size();
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#getPages()
     * @since 4.0
     */
    @Override
    public final IWizardPage[] getPages() {
        return (IWizardPage[])this.pgs.toArray(new IWizardPage[this.pgs.size()]);
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     * @since 4.0
     */
    @Override
    public IWizardPage getPreviousPage( final IWizardPage page ) {
        CoreArgCheck.isNotNull(page);
        final int ndx = indexOf(page);
        // Return null if last page or page not found
        if (ndx <= 0) {
            return null;
        }
        return (IWizardPage)this.pgs.get(ndx - 1);
    }

    // ============================================================================================================================
    // Property Methods

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#getStartingPage()
     * @since 4.0
     */
    @Override
    public final IWizardPage getStartingPage() {
        if (this.pgs.size() == 0) {
            return null;
        }
        return (IWizardPage)this.pgs.get(0);
    }

    /**
     * Gets the page index of the specified page.
     * 
     * @param thePage the page whose index is being requested
     * @return the page index or -1 if not found
     * @since 4.1
     */
    public int indexOf( final IWizardPage thePage ) {
        return this.pgs.indexOf(thePage);
    }

    // ============================================================================================================================
    // MVC Controller Methods

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#needsPreviousAndNextButtons()
     * @since 4.0
     */
    @Override
    public final boolean needsPreviousAndNextButtons() {
        return (super.needsPreviousAndNextButtons() || this.pgs.size() > 1);
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     * @since 4.0
     */
    @Override
    public final boolean performFinish() {
        final boolean finished = finish();
        if (finished) {
            WizardUtil.saveSettings(this);
        }
        return finished;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public final void removePage( final IWizardPage page ) {
        CoreArgCheck.isNotNull(page);
        this.pgs.remove(page);
    }

}
