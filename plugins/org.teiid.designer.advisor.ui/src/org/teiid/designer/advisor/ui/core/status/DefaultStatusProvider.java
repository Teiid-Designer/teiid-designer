package org.teiid.designer.advisor.ui.core.status;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.views.DSPValidationConstants;

public class DefaultStatusProvider implements IStatusContentProvider {

    private AdvisorStatusManager statusManager = new DefaultStatusManager();

    private IStatusRowProvider[] providers;

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusContentProvider.StatusContentProvider#getDefaultStatus()
     */
    @Override
    public IStatus getDefaultStatus() {
        return DSPValidationConstants.STATUS_MSGS.ADVISOR_NO_PROJECT_SELECTED;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusContentProvider.StatusContentProvider#getDescription()
     */
    @Override
    public String getDescription() {
        return "Focused object not selected for advisor.";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusContentProvider.StatusContentProvider#getImage(int)
     */
    @Override
    public Image getImage( int id ) {
        return AdvisorUiPlugin.getImageHelper().EMPTY_BOX_IMAGE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusContentProvider.StatusContentProvider#getLinkTooltip(int)
     */
    @Override
    public String getLinkTooltip( int id ) {
        return "Default Status Tooltip";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusContentProvider.StatusContentProvider#getRowsProviders()
     */
    @Override
    public IStatusRowProvider[] getRowsProviders() {
        if (this.providers == null) {
            initRowProviders();
        }
        return this.providers;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusContentProvider.StatusContentProvider#getStatus(int)
     */
    @Override
    public IStatus getStatus( int id ) {
        return statusManager.getCurrentStatus().get(id);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusContentProvider.StatusContentProvider#getStatusImage(int)
     */
    @Override
    public Image getStatusImage( int id ) {
        return AdvisorUiPlugin.getImageHelper().CHECKED_BOX_IMAGE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusContentProvider.StatusContentProvider#getText(int)
     */
    @Override
    public String getText( int id ) {
        return "Default Advisor Text";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.IStatusContentProvider.StatusContentProvider#getTitle()
     */
    @Override
    public String getTitle() {
    	if( statusManager == null || statusManager.getCurrentObject() == null ) {
    		return "Advisor Object Title";
    	}
    	
        return ((IProject)statusManager.getCurrentObject()).getName();
    }

    private void initRowProviders() {
    }

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "defaultStatusProviderId";
	}
	
    @Override
    public void updateStatus( boolean forceUpdate ) {
        // DO NOTHING
    }
    

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}
}