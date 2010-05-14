/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.editor;

import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;


/** 
 * @since 4.3
 */
public class MappingDiagramBehavior {

    private boolean bDisplayAllMappingClasses = true;
    private boolean bDisplayAllMappingClassesDefaultHasBeenSet = false;
    private boolean bFoldAllMappingClasses = false;     
    private boolean bFoldAllMappingClassesDefaultHasBeenSet = false;
    private boolean bSyncTreeAndDiagram = false;
    private boolean bSyncTreeAndDiagramDefaultHasBeenSet = false;
    private boolean bPopulateDiagramFromTreeSelection = false;
    private boolean bPopulateDiagramFromTreeSelectionHasBeenSet = false;
    
    /** 
     * 
     * @since 4.3
     */
    public MappingDiagramBehavior() {
        super();
    }
    
    
    public boolean getDisplayAllMappingClasses() {
//        System.out.println("[MappingDiagramBehavior.getDisplayAllMappingClasses] About to return: " + bDisplayAllMappingClasses );
        if ( !getDisplayAllMappingClassesDefaultHasBeenSet()  ) {
            
// jhTODO add a preference for this            
//          // apply the preference value to the current state
//          boolean bMappingClassFoldedState 
//              = UiPlugin.getDefault().getPreferenceStore()
//                  .getBoolean( PluginConstants.Prefs.????FOLD_MAPPING_CLASSES_BY_DEFAULT );

          // default to false for now:  
          boolean bDisplayAllMappingClassState = false;
          
          // Only apply the default once
          setDisplayAllMappingClassesToDefault( bDisplayAllMappingClassState );        
        }

        return this.bDisplayAllMappingClasses;
    }
    
    public void setDisplayAllMappingClasses( boolean b ) {
//        System.out.println("[MappingDiagramBehavior.setDisplayAllMappingClasses] About to set flag to: " + b );
        this.bDisplayAllMappingClasses = b;
//        Thread.currentThread().dumpStack();
    }

    public void setDisplayAllMappingClassesToDefault( boolean b ) {
//        System.out.println("[MappingDiagramBehavior.setDisplayAllMappingClassesToDefault] About to set flag to: " + b );
        this.bDisplayAllMappingClasses = b;
        bDisplayAllMappingClassesDefaultHasBeenSet = true;
    }

    public boolean getDisplayAllMappingClassesDefaultHasBeenSet() {
//        System.out.println("[MappingDiagramBehavior.getDisplayAllMappingClassesDefaultHasBeenSet] About to return: " + bDisplayAllMappingClassesDefaultHasBeenSet );

        return this.bDisplayAllMappingClassesDefaultHasBeenSet;
    }
    
    public boolean getDefaultMappingClassFoldedState() {
//        System.out.println("[MappingDiagramBehavior.getDefaultMappingClassFoldedState] About to return: " + bFoldAllMappingClasses );
        if ( !getDefaultMappingClassFoldedStateDefaultHasBeenSet() ) {
            // apply the preference value to the current state
            boolean bMappingClassFoldedState 
                = UiPlugin.getDefault().getPreferenceStore()
                    .getBoolean( PluginConstants.Prefs.FOLD_MAPPING_CLASSES_BY_DEFAULT );
                  
            // only apply the default once
            setDefaultMappingClassFoldedStateToDefault( bMappingClassFoldedState );        
        }

        return bFoldAllMappingClasses;
    }
    
    public void setDefaultMappingClassFoldedState( boolean b ) {
//        System.out.println("[MappingDiagramBehavior.setDefaultMappingClassFoldedState] About to set flag to: " + b );
        this.bFoldAllMappingClasses = b;
    }
    

    public void setDefaultMappingClassFoldedStateToDefault( boolean b ) {
//        System.out.println("[MappingDiagramBehavior.setDefaultMappingClassFoldedStateToDefault] About to set flag to: " + b );
        this.bFoldAllMappingClasses = b;
        bFoldAllMappingClassesDefaultHasBeenSet = true;
    }

    public boolean getDefaultMappingClassFoldedStateDefaultHasBeenSet() {
//        System.out.println("[MappingDiagramBehavior.getDefaultMappingClassFoldedStateDefaultHasBeenSet] About to return: " + bDisplayAllMappingClassesDefaultHasBeenSet );

        return this.bFoldAllMappingClassesDefaultHasBeenSet;
    }
    
    public boolean getSyncTreeAndDiagramState() {
//        System.out.println("[MappingDiagramBehavior.getSyncTreeAndDiagramState] About to return: " + bSyncTreeAndDiagram );

        if ( !getDefaultSyncTreeAndDiagramDefaultHasBeenSet() ) {

//          jhTODO make a preference for...            
//             // apply the preference value to the current state
//             boolean bSyncTreeAndDiagramState 
//                 = UiPlugin.getDefault().getPreferenceStore()
//                     .getBoolean( PluginConstants.Prefs.FOLD_MAPPING_CLASSES_BY_DEFAULT );
                   
             // Default to false for now:
             boolean bSyncTreeAndDiagramState = false; 
//                     System.out.println("\n\n $$$$$ [ToggleSyncTreeAndDiagramExpandsAction.setDiagramEditor] About to set to default: " + bSyncTreeAndDiagramState );
             setDefaultSyncTreeAndDiagramDefault( bSyncTreeAndDiagramState );       
         }
        
        
        return this.bSyncTreeAndDiagram;
    }

    public void setSyncTreeAndDiagramState( boolean b ) {
//        System.out.println("\n\n[MappingDiagramBehavior.setSyncTreeAndDiagramState] About to set flag to: " + b );

        this.bSyncTreeAndDiagram = b;
    }

    public void setDefaultSyncTreeAndDiagramDefault( boolean b ) {
//        System.out.println("\n $$$$$ [MappingDiagramBehavior.setDefaultSyncTreeAndDiagramDefault] About to set flag to: " + b );
        this.bSyncTreeAndDiagram = b;
        bSyncTreeAndDiagramDefaultHasBeenSet = true;
    }

    public boolean getDefaultSyncTreeAndDiagramDefaultHasBeenSet() {
//        System.out.println("[MappingDiagramBehavior.getDefaultSyncTreeAndDiagramDefaultHasBeenSet] About to return: " + bDisplayAllMappingClassesDefaultHasBeenSet );

        return this.bSyncTreeAndDiagramDefaultHasBeenSet;
    }
    
    
    public boolean getPopulateDiagramFromTreeSelectionState() {
//        System.out.println("[MappingDiagramBehavior.getPopulateDiagramFromTreeSelectionState] About to return: " + bPopulateDiagramFromTreeSelection );
        if ( !getDefaultPopulateDiagramFromTreeSelectionDefaultHasBeenSet() ) {

//  jhTODO make a preference for...            
//             // apply the preference value to the current state
//             boolean bPopulateDiagramFromTreeSelection 
//                 = UiPlugin.getDefault().getPreferenceStore()
//                     .getBoolean( PluginConstants.Prefs.????FOLD_MAPPING_CLASSES_BY_DEFAULT );
                   
             // Default to false:
             boolean bPopulateDiagramFromTreeSelection = false; 
             setDefaultPopulateDiagramFromTreeSelectionDefault( bPopulateDiagramFromTreeSelection );       
         }

        return this.bPopulateDiagramFromTreeSelection;
    }

    public void setPopulateDiagramFromTreeSelectionState( boolean b ) {
//        System.out.println("\n\n[MappingDiagramBehavior.setPopulateDiagramFromTreeSelectionState] About to set flag to: " + b );

        this.bPopulateDiagramFromTreeSelection = b;
    }

    public void setDefaultPopulateDiagramFromTreeSelectionDefault( boolean b ) {
//        System.out.println("\n $$$$$ [MappingDiagramBehavior] About to set flag to: " + b );
        this.bPopulateDiagramFromTreeSelection = b;
        bPopulateDiagramFromTreeSelectionHasBeenSet = true;
    }

    public boolean getDefaultPopulateDiagramFromTreeSelectionDefaultHasBeenSet() {
//        System.out.println("[MappingDiagramBehavior.getDefaultPopulateDiagramFromTreeSelectionDefaultHasBeenSet] About to return: " + bPopulateDiagramFromTreeSelectionHasBeenSet );

        return this.bPopulateDiagramFromTreeSelectionHasBeenSet;
    }

}
