/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.util.AspectManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectEditHelper;

/**
 * @author jhelbling
 *
 * 
 */
public class RelationalObjectEditHelper extends ModelObjectEditHelper {

//	/* (non-Javadoc)
//	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
//	 */
//	public boolean canDelete(Object obj) {
//
//		return true;
//	}

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
     */
    @Override
    public boolean canUndoDelete(Object obj) {

        if ( isMultiClassifierForeignKeyCutDeleteCase( obj ) ) {
            return false;
        }
        return true;
    }

//    /* (non-Javadoc)
//	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canCut(java.lang.Object)
//	 */
//	public boolean canCut(Object obj) {
//
//		return true;
//	}
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canCut(java.lang.Object)
     */
    @Override
    public boolean canUndoCut(Object obj) {
        
        if ( isMultiClassifierForeignKeyCutDeleteCase( obj ) ) {
            return false;
        }
        return true;
    }
    
    private boolean isMultiClassifierForeignKeyCutDeleteCase( Object obj ) {
        
        /*
         * Use case:
         *      if a CUT or DELETE will break an FK relationahship, we cannot UNDO it later.
         *      [Note: As MetaData codebase evolves, this situation may improve, and this
         *             edit should be removed at that time.]
         *      
         *      Ex:  If table A has a PK, table B has an FK that points to A's PK,
         *           then a CUT or DELETE of A will break the PK/FK relationship,
         *           and cannot later be undone if the classifiers on BOTH ends
         *           of an FK relationship are in the list of objects to be CUT or DELETEd.           
         */

        if( obj instanceof List ) {
            AspectManager amgr = DiagramUiPlugin.getDiagramAspectManager();
            List lstSelected = (List)obj;
            Iterator it = lstSelected.iterator();
            
            while( it.hasNext() ) {
                Object o = it.next();
                
                if ( o instanceof EObject ) {
                    EObject eoThisEObject = (EObject)o;
                    MetamodelAspect mma = amgr.getUmlAspect( eoThisEObject );        
                    
                    if ( mma instanceof UmlClassifier ) {
                        UmlClassifier classif = (UmlClassifier)mma;
                                                
                        Collection col = classif.getRelationships( eoThisEObject );
                        Iterator it2 = col.iterator();
                        
                        while( it2.hasNext() ) {
                            Object o2 = it2.next();
                            if ( o2 instanceof ForeignKey ) {
                                ForeignKey fk = (ForeignKey)o2;
                                UniqueKey uk = fk.getUniqueKey();
                                if ( uk != null ) { 
                                    EObject eoContainer = uk.eContainer(); 
                                    
                                    if ( selectionContainsOtherEndOfForeignKey( lstSelected, eoContainer, eoThisEObject ) ) {
                                        return true;
                                    } 
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
    
    
    private boolean selectionContainsOtherEndOfForeignKey( List lstSelected, EObject eoContainer, EObject eoThisClassifier ) {
        Iterator it = lstSelected.iterator();
        
        while( it.hasNext() ) {
            Object o = it.next();
            
            if ( o instanceof EObject ) {
                if ( o != eoThisClassifier && o == eoContainer ) {
                    return true;
                }                
            }
        }
        
        return false;
    }

	@Override
    public boolean canClone(Object obj) {
		if( obj instanceof ModelImport )
			return false;
		
		return super.canClone(obj);
	}

	@Override
    public boolean canCopy(Object obj) {
		if( obj instanceof ModelImport )
			return false;
		
		return super.canCopy(obj);
	}

	@Override
    public boolean canCut(Object obj) {
		if( obj instanceof ModelImport )
			return false;
		
		return super.canCut(obj);
	}

	@Override
    public boolean canPaste(Object obj, Object pasteParent) {
		if( obj instanceof ModelImport )
			return false;
		
		return super.canPaste(obj, pasteParent);
	}

	@Override
    public boolean canRename(Object obj) {
		if( obj instanceof ModelImport )
			return false;
		
		return super.canRename(obj);
	}
      
}
