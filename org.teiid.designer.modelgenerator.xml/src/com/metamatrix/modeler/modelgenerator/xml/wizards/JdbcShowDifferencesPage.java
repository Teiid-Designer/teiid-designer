/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.PropertyDifference;
import com.metamatrix.modeler.compare.ui.tree.DifferenceReportsPanel;
import com.metamatrix.modeler.modelgenerator.xml.IUiConstants;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WizardUtil;

/** 
 *  jh 8/25/2004: I began this on 8/24 as part of the solution to defect 13309.
 *    Today we are back to Pegasus so I am cleaning this up and leaving it until the
 *    next Defect Day.  All commented-out code was commented out just to get a clean
 *    compile for now.
 * 
 *    TODO:
 *      1) Probably turn it into a wizard page to go into the JdbcImportWizard
 *         as the last page.
 *      2) DONE: It needs 'modeler.compare' (& modeler.compare.ui), so that 
 *         may help us decide where to put it.      
 * 
 * @since 4.2
 */
public class JdbcShowDifferencesPage extends WizardPage 
                                   implements InternalUiConstants.Widgets,
                                               IUiConstants,
                                               IUiConstants.Images,
                                               IUiConstants.ProductInfo,
                                               IUiConstants.ProductInfo.Capabilities {
                                               
                                               
                                               
                                               
                                               
                                               

    private XsdAsRelationalImportWizard jiwWizard;
    private DifferenceReportsPanel pnlDiffReport;
//    private DifferenceReport drDifferenceReport;
    private List lstDIfferenceReports;

//    private String sMessage;
//    private String sModelName;
    private boolean bIsVisible;
    
    private static PluginUtil util = XmlImporterUiPlugin.getDefault().getPluginUtil();

    private static final String TITLE      
        = util.getString("JdbcShowDifferencesPage.title"); //$NON-NLS-1$
    private static final String MESSAGE      
        = util.getString("JdbcShowDifferencesPage.message"); //$NON-NLS-1$
//    private static final String TREE_TITLE      
//        = Util.getString("JdbcShowDifferencesPage.treeTitle"); //$NON-NLS-1$
    private static final String DIFF_DESCRIPTOR_TITLE      
        = util.getString("JdbcShowDifferencesPage.diffDescriptorTitle"); //$NON-NLS-1$

    public JdbcShowDifferencesPage( XsdAsRelationalImportWizard jiwWizard ) {
        super(JdbcShowDifferencesPage.class.getSimpleName(), TITLE, null);
        this.jiwWizard = jiwWizard;
    }
    

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    public void createControl( Composite parent ) {

        // Create page
        final Composite pg = new Composite( parent, SWT.NONE );
        pg.setLayout( new GridLayout() );
        pg.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        setControl( pg );

        // Create the Difference Reports Panel
        String treeTitle = "";  //$NON-NLS-1$
        String tableTitle = DIFF_DESCRIPTOR_TITLE;

        boolean enableProperySelection = true;       
        boolean showCheckboxes = true;
        /*
         * DifferenceReportsPanel( Composite theParent,
                                    String theTreeTitle,
                                    String theTableTitle,
                                    boolean enablePropertySelection,
                                    boolean showCheckboxes,
                                    boolean showMessage,                                    
                                    boolean bDisplayOnlyPrimaryMetamodelObjects ) {
         */
        pnlDiffReport 
            = new DifferenceReportsPanel( pg, 
                                          treeTitle, 
                                          tableTitle, 
                                          enableProperySelection, 
                                          showCheckboxes,
                                          true,
                                          true );
        
        pnlDiffReport.setMessage( "" );  //$NON-NLS-1$
        super.setMessage( MESSAGE );
        
        
        TableViewer tableViewer = pnlDiffReport.getTableViewer();
        if(tableViewer instanceof CheckboxTableViewer) {
            ((CheckboxTableViewer)tableViewer).addCheckStateListener(new ICheckStateListener() {
                public void checkStateChanged(CheckStateChangedEvent theEvent) {
                    Object checkedObject = theEvent.getElement();
                    boolean isChecked = theEvent.getChecked();
                    if(checkedObject instanceof PropertyDifference) {
                        PropertyDifference propDiff = (PropertyDifference)checkedObject;
                        propDiff.setSkip(!isChecked);
                    }
                }
            });
        }
    }

    
    @Override
    public void setVisible( boolean bIsVisible ) {
        this.bIsVisible = bIsVisible;
        
        
        if ( bIsVisible ) {
            setDifferenceReport( jiwWizard.getDifferenceReport() );
            
//            pnlDiffReport.setDifferenceReports( lstDIfferenceReports );
        }

        validatePage();

        super.setVisible( bIsVisible );
    }
    
    private void validatePage() {
        WizardUtil.setPageComplete( this );
    }
    
    
    public boolean isVisible() {
        return bIsVisible;
    }
    
    public DifferenceReport getDifferenceReport() {
        return pnlDiffReport.getDifferenceReport();
    }

    public void setDifferenceReport( DifferenceReport drDifferenceReport )  {
        lstDIfferenceReports = new ArrayList( 1 );
        this.lstDIfferenceReports.add( drDifferenceReport );
        if ( pnlDiffReport != null ) {
            pnlDiffReport.setDifferenceReports( lstDIfferenceReports );
        }
    }

    public void setDifferenceReports( List lstDIfferenceReports )  {
        this.lstDIfferenceReports = lstDIfferenceReports;
        
        if ( pnlDiffReport != null ) {
            pnlDiffReport.setDifferenceReports( lstDIfferenceReports );
        }
    }


    @Override
    public void setMessage( String sMessage ) {
//        super.setMessage( sMessage );
    }
    
    public void setModelName( String sModelName ) {
//        this.sModelName = sModelName;
        if ( pnlDiffReport != null ) {
            pnlDiffReport.setModelName( sModelName );
        }
    }
 }
