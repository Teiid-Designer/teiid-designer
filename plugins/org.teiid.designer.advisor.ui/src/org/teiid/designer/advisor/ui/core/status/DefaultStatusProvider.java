package org.teiid.designer.advisor.ui.core.status;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.views.status.StatusValidationConstants;

public class DefaultStatusProvider implements IStatusContentProvider {

    private AdvisorStatusManager statusManager = new DefaultStatusManager();

    private IStatusRowProvider[] providers;

    /**
     * {@inheritDoc}
     * 
     * @see IStatusContentProvider#getDefaultStatus()
     */
    @Override
    public IStatus getDefaultStatus() {
        return StatusValidationConstants.STATUS_MSGS.ADVISOR_NO_PROJECT_SELECTED;
    }

    /**
     * {@inheritDoc}
     * 
     * @see IStatusContentProvider#getDescription()
     */
    @Override
    public String getDescription() {
        return "Focused object not selected for advisor."; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see IStatusContentProvider#getImage(int)
     */
    @Override
    public Image getImage( int id ) {
        return AdvisorUiPlugin.getImageHelper().EMPTY_BOX_IMAGE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see IStatusContentProvider#getLinkTooltip(int)
     */
    @Override
    public String getLinkTooltip( int id ) {
        return "Default Status Tooltip"; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see IStatusContentProvider#getRowsProviders()
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
     * @see IStatusContentProvider#getStatus(int)
     */
    @Override
    public IStatus getStatus( int id ) {
        return statusManager.getCurrentStatus().get(id);
    }

    /**
     * {@inheritDoc}
     * 
     * @see IStatusContentProvider#getStatusImage(int)
     */
    @Override
    public Image getStatusImage( int id ) {
        return AdvisorUiPlugin.getImageHelper().CHECKED_BOX_IMAGE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see IStatusContentProvider#getText(int)
     */
    @Override
    public String getText( int id ) {
        return "Default Advisor Text"; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see IStatusContentProvider#getTitle()
     */
    @Override
    public String getTitle() {
    	if( statusManager == null || statusManager.getCurrentObject() == null ) {
    		return "Advisor Object Title"; //$NON-NLS-1$
    	}
    	
        return ((IProject)statusManager.getCurrentObject()).getName();
    }

    private void initRowProviders() {
    }

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "defaultStatusProviderId"; //$NON-NLS-1$
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