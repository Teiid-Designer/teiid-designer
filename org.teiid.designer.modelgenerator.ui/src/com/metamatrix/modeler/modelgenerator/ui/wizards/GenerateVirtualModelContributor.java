/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.wizards.INewModelWizardContributor;

/**
 * GenerateVirtualModelContributor is the NewModelWizard builder contributor for generating
 * a virtual model from a UML model.
 */
public class GenerateVirtualModelContributor implements INewModelWizardContributor, 
                                                        ModelGeneratorUiConstants,
                                                        UiConstants.ProductInfo.Capabilities {

    //============================================================================================================================
    // Variables
    private IWizardPage[] pages;
    private IdentifySubsetsWizardPage identifySubsetsPage;
    private RelationshipOptionsWizardPage relationshipOptionsPage;
    private GeneralOptionsWizardPage generalOptionsPage;
    private DatatypeOptionsWizardPage datatypeOptionsPage;
    private GeneratedKeyOptionsWizardPage generatedKeyOptionsPage;
//    private CustomPropertyOptionsWizardPage customPropertyOptionsPage;
    private IModelGeneratorManager modelGeneratorMgr;
    private IWizardPage priorPage;
    
    private boolean finishedEarly = false;

    /**
     * Construct an instance of GenerateVirtualModelContributor.
     */
    public GenerateVirtualModelContributor() {
    	super();
        this.modelGeneratorMgr = new ModelGeneratorManager();
    }
    
    /** 
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#canFinishEarly(org.eclipse.jface.wizard.IWizardPage)
     * @since 4.2
     */
    public boolean canFinishEarly(IWizardPage theCurrentPage) {
        boolean canFinishEarly = false;
        // dont check the last page
        if(theCurrentPage!=generatedKeyOptionsPage) {
            // Get the GeneratorManager options
            GeneratorManagerOptions mgrOptions = this.modelGeneratorMgr.getGeneratorManagerOptions();
            // If all of the required inputs have been supplied, ok to finish
            if(mgrOptions.hasValidUmlInputSelections() && mgrOptions.hasValidRelationshipOptions() &&
               mgrOptions.hasValidDatatypeSelections() && mgrOptions.hasValidGeneratorOptions()) {
                canFinishEarly = true;
            }
        }
        finishedEarly = canFinishEarly;
        return canFinishEarly;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#createWizardPages(org.eclipse.jface.viewers.ISelection, org.eclipse.core.resources.IProject, com.metamatrix.modeler.core.MetamodelDescriptor, boolean)
     */
    public void createWizardPages(
        	ISelection selection,
        	IResource targetResource,
            IPath targetFilePath,
        	MetamodelDescriptor descriptor,
        	boolean isVirtual) {

        // Reset any previous selections
        this.modelGeneratorMgr.init();
        
        // Generate the pages
        pages = new IWizardPage[5];
        
        // GeneratorManager Options 
        GeneratorManagerOptions mgrOptions = this.modelGeneratorMgr.getGeneratorManagerOptions();
        // For now, generated relationships must be placed in a selected relationships model
        mgrOptions.setRelationshipsModelOption(GeneratorManagerOptions.PUT_RELATIONSHIPS_IN_SELECTED_MODEL);

        // ------------------------------------
        // Page1: UML Source selections
        // ------------------------------------
        String title = Util.getString("IdentifySubsetsWizardPage.generateNew.title"); //$NON-NLS-1$
        String descr = Util.getString("IdentifySubsetsWizardPage.generateNew.description"); //$NON-NLS-1$
        identifySubsetsPage = new IdentifySubsetsWizardPage("identifySubsetsWizardPage",    //$NON-NLS-1$
                                            title, descr, this.modelGeneratorMgr); 
        // ------------------------------------
        // Page2: Relationship Model Selection
        // ------------------------------------
        relationshipOptionsPage = new RelationshipOptionsWizardPage(
              "relationshipOptionsWizardPage",mgrOptions,targetResource,targetFilePath); //$NON-NLS-1$
        // ------------------------------------
        // Page3: General Options
        // ------------------------------------
        generalOptionsPage = new GeneralOptionsWizardPage("generalOptionsWizardPage",mgrOptions); //$NON-NLS-1$
        // ------------------------------------
        // Page4: Datatype model selection
        // ------------------------------------
        datatypeOptionsPage = new DatatypeOptionsWizardPage("datatypeOptionsWizardPage",mgrOptions); //$NON-NLS-1$
        // ------------------------------------
        // Page5: Generated Key Options
        // ------------------------------------
        generatedKeyOptionsPage = new GeneratedKeyOptionsWizardPage("generatedKeyOptionsWizardPage",mgrOptions); //$NON-NLS-1$
        // ------------------------------------
        // Page6: Custom Property Options
        // ------------------------------------
        // Commented Out CustomProperties - (UML Model issues)
        //customPropertyOptionsPage = new CustomPropertyOptionsWizardPage("customPropertyOptionsWizardPage",mgrOptions); //$NON-NLS-1$

        pages[0] = identifySubsetsPage;
        pages[1] = relationshipOptionsPage;
        pages[2] = generalOptionsPage;
        pages[3] = datatypeOptionsPage;
        pages[4] = generatedKeyOptionsPage;
        //pages[5] = customPropertyOptionsPage;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#getWizardPages()
     */
    public IWizardPage[] getWizardPages() {
        return pages;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#inputChanged(org.eclipse.jface.viewers.ISelection, org.eclipse.core.resources.IProject, com.metamatrix.modeler.core.MetamodelDescriptor, boolean)
     */
    public void inputChanged(
        ISelection selection,
        IResource targetResource,
        MetamodelDescriptor descriptor,
        boolean isVirtual) {

    }
    
    /** 
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#doCancel()
     * @since 4.2
     */
    public void doCancel() {
    }
    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#doFinish(com.metamatrix.modeler.core.workspace.ModelResource, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doFinish(ModelResource modelResource, IProgressMonitor monitor) {
        if( finishedEarly ) {
            preFinish();
        }
        
        // Populate the newly-created Model (modelFile) with constructs from the
        // Generator business Object
		String transactionName = Util.getString("GenerateVirtualModelContributor.transactionName"); //$NON-NLS-1$
		boolean started = ModelerCore.startTxn(transactionName, this);
        boolean succeeded = false;
		try {
//            // CustomProperties commented out - (UML Model issues)
//            // Get the custom property mappings
//            Map columnCustomPropsMap = this.customPropertyOptionsPage.getColumnCustomPropsMap();
//            Map tableCustomPropsMap = this.customPropertyOptionsPage.getTableCustomPropsMap();
//            // Set mappings on ModelGenerator
//            this.modelGeneratorMgr.getGeneratorManagerOptions().setColumnCustomPropsMap(columnCustomPropsMap);
//            this.modelGeneratorMgr.getGeneratorManagerOptions().setTableCustomPropsMap(tableCustomPropsMap);
            // Generate the Model 
            this.modelGeneratorMgr.generateOutputAndMerge(modelResource,monitor);
            succeeded = true;
        } catch (Exception ex) {
            String message = Util.getString("GenerateVirtualModelContributor.doFinishError", //$NON-NLS-1$
                                            modelResource.getItemName()); 
            Util.log(IStatus.ERROR, ex, message); 
		} finally {
			if (started) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                    this.modelGeneratorMgr.save(monitor);
                } else {
                    ModelerCore.rollbackTxn();
                }
			}
		}
    }
    
    public void preFinish() {
        generalOptionsPage.preFinish();
        datatypeOptionsPage.preFinish();
        generatedKeyOptionsPage.preFinish();
    }
    

    public void currentPageChanged(IWizardPage page) {
        if (priorPage == relationshipOptionsPage
         && page == generalOptionsPage) {
            // open the selected relationship model
            relationshipOptionsPage.nextPressed();
        } // endif

        priorPage = page;
    }
}
 
