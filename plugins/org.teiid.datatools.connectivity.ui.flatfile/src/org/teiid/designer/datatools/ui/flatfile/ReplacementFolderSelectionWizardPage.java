/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datatools.ui.flatfile;

import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.datatools.connectivity.oda.flatfile.ui.i18n.Messages;
import org.eclipse.swt.widgets.Composite;

/**
 * Replacement wizard page to
 * org.eclipse.datatools.connectivity.oda.flatfile.ui.wizards.FolderSelectionWizardPage
 * which has validation that is too strict, ie. a selected directory must exist on the host
 * system and this is not necessarily appropriate if we want to deploy this connection
 * profile to a remote server.
 */
public class ReplacementFolderSelectionWizardPage extends DataSourceWizardPage
{

    private ReplacementFolderSelectionPageHelper pageHelper;
    private Properties folderProperties;

    /**
     * @param pageName
     */
    public ReplacementFolderSelectionWizardPage( String pageName )
    {
        super( pageName );
        setMessage( Messages.getString( "wizard.WizardTitle.DEFAULT_MESSAGE" ) );  //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage#createPageCustomControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageCustomControl( Composite parent )
    {
        if ( pageHelper == null )
            pageHelper = new ReplacementFolderSelectionPageHelper( this );
        pageHelper.setResourceIdentifiers( getHostResourceIdentifiers( ) );
        pageHelper.createCustomControl( parent );
        pageHelper.initCustomControl( folderProperties ); // in case init was called before create

        /*
         * Optionally hides the Test Connection button, using
         *      setPingButtonVisible( false );
         */
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage#initPageCustomControl(java.util.Properties)
     */
    @Override
    public void setInitialProperties( Properties dataSourceProps )
    {
        folderProperties = dataSourceProps;
        if ( pageHelper == null )
            return; // ignore, wait till createPageCustomControl to initialize
        pageHelper.initCustomControl( folderProperties );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage#refresh()
     */
    @Override
    public void refresh()
    {
        // enable/disable all controls on page in respect of the editable session state
        enableAllControls( getControl(), isSessionEditable() );

        if ( pageHelper != null && isSessionEditable() )
            pageHelper.resetUIStatus( );
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage#collectCustomProperties()
     */
    @Override
    public Properties collectCustomProperties( )
    {
        /*
         * Optionally assign a custom designer state, for inclusion
         * in the ODA design session response, using
         * setResponseDesignerState( DesignerState customState );
         */

        if ( pageHelper != null )
            return pageHelper.collectCustomProperties( folderProperties );

        return ( folderProperties != null ) ? folderProperties
                : new Properties( );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
        getControl( ).setFocus( );
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSourceWizardPageCore#createTestConnectionRunnable(org.eclipse.datatools.connectivity.IConnectionProfile)
     */
    @Override
    protected Runnable createTestConnectionRunnable( IConnectionProfile profile )
    {
        return pageHelper.createTestConnectionRunnable( profile );
    }

}
