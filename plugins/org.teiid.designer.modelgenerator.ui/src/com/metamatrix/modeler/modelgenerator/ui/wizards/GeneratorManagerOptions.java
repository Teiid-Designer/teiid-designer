/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.wizards;

import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceSelections;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2RelationalGenerator;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalOptions;

/**
 * Generator Manager Options 
 */
public class GeneratorManagerOptions implements ModelGeneratorUiConstants {
    
    public final static int PUT_RELATIONSHIPS_IN_GENERATED_MODEL = 0;
    public final static int PUT_RELATIONSHIPS_IN_SELECTED_MODEL = 1;
    public final static int DO_NOT_GENERATE_RELATIONSHIPS = 2;
        
    private final static String OPTIONS_VALID_MSG = Util.getString(
            "GeneratorManagerOptions.optionsValidMsg"); //$NON-NLS-1$
    private final static String SELECT_TARGET_MODEL_MSG = Util.getString(
            "GeneratorManagerOptions.selectTargetModelMsg"); //$NON-NLS-1$

    //============================================================
    // Instance variables
    //============================================================
    private int relationshipsModelOption = PUT_RELATIONSHIPS_IN_GENERATED_MODEL;
    private Uml2RelationalGenerator uml2RelationalModelGenerator;
            
    //============================================================
    // Constructors
    //============================================================
    /**
     * Constructor.
     */
    public GeneratorManagerOptions(Uml2RelationalGenerator modelGenerator) {
        // Uml2Relational ModelGenerator
        this.uml2RelationalModelGenerator = modelGenerator;
    }
    
//    /**
//     *  determine if the current options are valid/complete
//     * @return 'true' if the options are complete, 'false' if not.
//     */
//    public boolean hasValidOptions() {
//        boolean optionsValid = false;
//        //--------------------------------------------
//        // Check the selected Relationships Model
//        //--------------------------------------------
//        if(this.relationshipsModelOption==PUT_RELATIONSHIPS_IN_GENERATED_MODEL ||
//           this.relationshipsModelOption==DO_NOT_GENERATE_RELATIONSHIPS) {
//            optionsValid=true;
//        } else if(this.relationshipsModelOption==PUT_RELATIONSHIPS_IN_SELECTED_MODEL) {
//            ModelResource relationshipsModel = this.uml2RelationalModelGenerator.getRelationshipModel();
//             if(relationshipsModel!=null) {
//                optionsValid = true;
//            } else {
//                optionsValid = false;
//            }
//        } 
//        //------------------------------------------------------------
//        // If Relationships Model is OK, Check the remaining Options
//        //------------------------------------------------------------
//        if(optionsValid) {
//            Uml2RelationalOptions options = this.uml2RelationalModelGenerator.getOptions(); 
//            IStatus status = options.validate();
//            if(status!=null) {
//                if(status.getSeverity()==IStatus.OK) {
//                    optionsValid = true;           
//                } else {
//                    optionsValid = false;
//                }
//            } else {
//                optionsValid=false;
//            }
//        }
//        //------------------------------------------------------------
//        // If Options still OK, Check the remaining generator Options
//        //------------------------------------------------------------
//        if(optionsValid) {
//            // Check dtype selection
//            IStatus dtypeStatus = this.uml2RelationalModelGenerator.validateDatatypeSelection();
//            if(dtypeStatus!=null) {
//                if(dtypeStatus.getSeverity()==IStatus.OK) {
//                    IStatus umlInputStatus = this.uml2RelationalModelGenerator.validateInputUmlSelection();
//                    if(umlInputStatus!=null && umlInputStatus.getSeverity()!=IStatus.OK) {
//                        optionsValid=false;
//                    } 
//                } else {
//                    optionsValid = false;
//                }
//            }
//        }
//        return optionsValid;
//    }
//    
//    /**
//     *  get option settings status message
//     * @return the options status message
//     */
//    public String getOptionsStatusMessage() {
//        String statusMessage = null;
//        //--------------------------------------------
//        // Check the selected Relationships Model
//        //--------------------------------------------
//        if(this.relationshipsModelOption==PUT_RELATIONSHIPS_IN_GENERATED_MODEL) {
//            statusMessage=OPTIONS_VALID_MSG;
//        } else if(this.relationshipsModelOption==PUT_RELATIONSHIPS_IN_SELECTED_MODEL) {
//            ModelResource relationshipsModel = this.uml2RelationalModelGenerator.getRelationshipModel();
//            if(relationshipsModel!=null) {
//                statusMessage=OPTIONS_VALID_MSG;
//            } else {
//                statusMessage=SELECT_TARGET_MODEL_MSG;
//            }
//        } 
//        //------------------------------------------------------------
//        // If Relationships Model is OK, Check the remaining Options
//        //------------------------------------------------------------
//        if(statusMessage.equals(OPTIONS_VALID_MSG)) {
//            Uml2RelationalOptions options = this.uml2RelationalModelGenerator.getOptions(); 
//            IStatus status = options.validate();
//            if(status!=null) {
//                if(status.getSeverity()!=IStatus.OK) {
//                    statusMessage = status.getMessage();           
//                }
//            } 
//        }
//        //------------------------------------------------------------
//        // If Options still OK, Check the remaining generator Options
//        //------------------------------------------------------------
//        if(statusMessage.equals(OPTIONS_VALID_MSG)) {
//            // Check dtype selection
//            IStatus dtypeStatus = this.uml2RelationalModelGenerator.validateDatatypeSelection();
//            if(dtypeStatus!=null) {
//                if(dtypeStatus.getSeverity()==IStatus.OK) {
//                    IStatus umlInputStatus = this.uml2RelationalModelGenerator.validateInputUmlSelection();
//                    if(umlInputStatus!=null) {
//                        if(umlInputStatus.getSeverity()!=IStatus.OK) {
//                            statusMessage = umlInputStatus.getMessage();
//                        } 
//                    }
//                } else {
//                    statusMessage = dtypeStatus.getMessage();
//                }
//            }
//        }
//        return statusMessage;
//    }
    
    /**
     *  get status message for UML model input selections
     * @return the UML model input selection status message
     */
    public boolean hasValidUmlInputSelections() {
        boolean hasValid = false;
        IStatus umlInputStatus = this.uml2RelationalModelGenerator.validateInputUmlSelection();
        if(umlInputStatus!=null && umlInputStatus.getSeverity()==IStatus.OK) {
            hasValid=true;
        }
        return hasValid;
    }
    
    /**
     *  get relationship options status message
     * @return the relationship options status message
     */
    public boolean hasValidRelationshipOptions() {
        boolean hasValid = false;
        if(this.relationshipsModelOption==DO_NOT_GENERATE_RELATIONSHIPS || 
            this.relationshipsModelOption==PUT_RELATIONSHIPS_IN_GENERATED_MODEL) {
            hasValid=true;
        } else if(this.relationshipsModelOption==PUT_RELATIONSHIPS_IN_SELECTED_MODEL) {
            ModelResource relationshipsModel = this.uml2RelationalModelGenerator.getRelationshipModel();
            if(relationshipsModel!=null) {
                hasValid=true;
            } 
        } 
        return hasValid;
    }

    /**
     *  get status message for datatype selections
     * @return the datatype selection status message
     */
    public boolean hasValidDatatypeSelections() {
        boolean hasValid = false;
        // Check dtype selection
        IStatus dtypeStatus = this.uml2RelationalModelGenerator.validateDatatypeSelection();
        if(dtypeStatus!=null && dtypeStatus.getSeverity()==IStatus.OK) {
            hasValid=true;
        }
        return hasValid;
    }

    /**
     *  get generator options status message
     * @return the generator options status message
     */
    public boolean hasValidGeneratorOptions() {
        boolean hasValid = false;
        Uml2RelationalOptions options = this.uml2RelationalModelGenerator.getOptions(); 
        IStatus status = options.validate();
        if(status!=null && status.getSeverity()==IStatus.OK) {
            hasValid=true;
        } 
        return hasValid;
    }

    /**
     *  get status message for UML model input selections
     * @return the UML model input selection status message
     */
    public String getUmlInputSelectionStatusMessage() {
        String statusMessage = EMPTY_STR;
        IStatus umlInputStatus = this.uml2RelationalModelGenerator.validateInputUmlSelection();
        if(umlInputStatus!=null && umlInputStatus.getSeverity()!=IStatus.OK) {
            statusMessage = umlInputStatus.getMessage();
        }
        return statusMessage;
    }
    
    /**
     *  get relationship options status message
     * @return the relationship options status message
     */
    public String getRelationshipOptionsStatusMessage() {
        String statusMessage = EMPTY_STR;
        //--------------------------------------------
        // Check the selected Relationships Model
        //--------------------------------------------
        if(this.relationshipsModelOption==PUT_RELATIONSHIPS_IN_GENERATED_MODEL) {
            statusMessage=OPTIONS_VALID_MSG;
        } else if(this.relationshipsModelOption==PUT_RELATIONSHIPS_IN_SELECTED_MODEL) {
            ModelResource relationshipsModel = this.uml2RelationalModelGenerator.getRelationshipModel();
            if(relationshipsModel!=null) {
                statusMessage=OPTIONS_VALID_MSG;
            } else {
                statusMessage=SELECT_TARGET_MODEL_MSG;
            }
        } 
        return statusMessage;
    }

    /**
     *  get status message for datatype selections
     * @return the datatype selection status message
     */
    public String getDatatypeSelectionStatusMessage() {
        String statusMessage = EMPTY_STR;
        // Check dtype selection
        IStatus dtypeStatus = this.uml2RelationalModelGenerator.validateDatatypeSelection();
        if(dtypeStatus!=null && dtypeStatus.getSeverity()!=IStatus.OK) {
            statusMessage = dtypeStatus.getMessage();
        }
        return statusMessage;
    }

    /**
     *  get generator options status message
     * @return the generator options status message
     */
    public String getGeneratorOptionsStatusMessage() {
        String statusMessage = EMPTY_STR;
        Uml2RelationalOptions options = this.uml2RelationalModelGenerator.getOptions(); 
        IStatus status = options.validate();
        if(status!=null && status.getSeverity()!=IStatus.OK) {
            statusMessage = status.getMessage();           
        } 
        return statusMessage;
    }

    //============================================================
    // public methods
    //============================================================

    /**
     *  set the relationships target mode option
     * @param relationshipsMode the desired relationships mode 
     *        (PUT_RELATIONSHIPS_IN_GENERATED_MODEL or PUT_RELATIONSHIPS_IN_SELECTED_MODEL
     *         or DO_NOT_GENERATE_RELATIONSHIPS)
     */
    public void setRelationshipsModelOption(int relationshipsModelOption) {
        if(relationshipsModelOption==PUT_RELATIONSHIPS_IN_GENERATED_MODEL) {
            this.relationshipsModelOption = relationshipsModelOption;
            setRelationshipsModel(getRelationalOutputModel());
        } else if (relationshipsModelOption==PUT_RELATIONSHIPS_IN_SELECTED_MODEL) {
            this.relationshipsModelOption = relationshipsModelOption;
            setRelationshipsModel(null);
        } else if (relationshipsModelOption==DO_NOT_GENERATE_RELATIONSHIPS) {
            this.relationshipsModelOption = relationshipsModelOption;
            setRelationshipsModel(null);
        }
    }
    
    /**
     *  get the relationships target mode option
     * @return the current relationships mode (GENERATED_MODEL or SELECTED_MODEL)
     */
    public int getRelationshipsModelOption( ) {
        return relationshipsModelOption;    
    }
    
    /**
     *  set the map of uml to relational custom properties for generated relational columns
     * @param customPropsmap the custom properties Map 
     */
    public void setColumnCustomPropsMap(Map customPropsMap) {
        this.uml2RelationalModelGenerator.setColumnCustomPropsMap(customPropsMap);
    }

    /**
     *  set the map of uml to relational custom properties for generated relational tables
     * @param customPropsmap the custom properties Map 
     */
    public void setTableCustomPropsMap(Map customPropsMap) {
        this.uml2RelationalModelGenerator.setTableCustomPropsMap(customPropsMap);
    }

    /**
     *  set the target relationships Model
     * @param relationshipsModel the desired relationships model 
     */
    public void setRelationshipsModel(ModelResource relationshipsModel) {
        this.uml2RelationalModelGenerator.setRelationshipModel(relationshipsModel);
    }
    
    /**
     *  get the target relationships Model
     * @return the current relationships model 
     */
    public ModelResource getRelationshipsModel() {
        return this.uml2RelationalModelGenerator.getRelationshipModel();
    }
    
    /**
     *  get the current Datatype Model Selections
     * @return the Model Workspace Selections
     */
    public ModelWorkspaceSelections getDatatypeSelections() {
        return this.uml2RelationalModelGenerator.getModelWorkspaceDatatypeSelections();
    }
    
    /**
     *  get the current Uml Input Model Selections
     * @return the Input Model Workspace Selections
     */
    public ModelWorkspaceSelections getModelWorkspaceUmlInputSelections() {
        return this.uml2RelationalModelGenerator.getModelWorkspaceUmlInputSelections();
    }

    /**
     *  set the target relational Model
     * @param relationalModel the desired target relational model 
     */
    public void setRelationalOutputModel(ModelResource targetModel) {
        // Set the target Relational Output Model
        this.uml2RelationalModelGenerator.setRelationalOutputModel(targetModel);
        // If Relationships option is GENERATED_MODEL, set relationship model also.
        if(this.relationshipsModelOption==PUT_RELATIONSHIPS_IN_GENERATED_MODEL) {
            this.uml2RelationalModelGenerator.setRelationshipModel(targetModel);
        }
    }

    /**
     *  get the target relational output Model
     * @return the target relational output model 
     */
    public ModelResource getRelationalOutputModel() {
        return this.uml2RelationalModelGenerator.getRelationalOutputModel();
    }

    /**
     *  get the UML to Relational Options object
     * @return options
     */
    public Uml2RelationalOptions getUml2RelationalOptions() {
        return this.uml2RelationalModelGenerator.getOptions();
    }
    
    //============================================================
    // private methods
    //============================================================
    

}
