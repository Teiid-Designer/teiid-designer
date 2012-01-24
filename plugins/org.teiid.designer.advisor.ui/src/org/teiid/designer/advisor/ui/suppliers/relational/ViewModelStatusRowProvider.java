/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.suppliers.relational;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.core.status.IStatusRowProvider;

public class ViewModelStatusRowProvider  implements IStatusRowProvider {

    /**
     * 
     */
    public ViewModelStatusRowProvider() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusRowProvider.StatusRowProvider#getId()
     */
    @Override
    public int getId() {
        return AdvisorUiConstants.Groups.GROUP_VIEWS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusRowProvider.StatusRowProvider#getImage(org.eclipse.core.runtime.IStatus)
     */
    @Override
    public Image getImage( IStatus status ) {

        return AdvisorUiPlugin.getImageHelper().CHECKED_BOX_IMAGE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusRowProvider.StatusRowProvider#getImageTooltip(org.eclipse.core.runtime.IStatus)
     */
    @Override
    public String getImageTooltip( IStatus status ) {
        return "View Models Tooltip";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusRowProvider.StatusRowProvider#getLinkImage(org.eclipse.core.runtime.IStatus)
     */
    @Override
    public Image getLinkImage( IStatus status ) {
        return AdvisorUiPlugin.getImageHelper().LIGHTBULB_IMAGE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusRowProvider.StatusRowProvider#getLinkTooltip(org.eclipse.core.runtime.IStatus)
     */
    @Override
    public String getLinkTooltip( IStatus status ) {
        return "View Models Tooltip";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusRowProvider.StatusRowProvider#getText(org.eclipse.core.runtime.IStatus)
     */
    @Override
    public String getText( IStatus status ) {
        return "View Models Text";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusRowProvider.StatusRowProvider#getTextTooltip(org.eclipse.core.runtime.IStatus)
     */
    @Override
    public String getTextTooltip( IStatus status ) {
        return "View Models Text Tooltip";
    }

}
