/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.diagram;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.diagram.ui.AbstractDiagramType;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelFactory;
import com.metamatrix.modeler.diagram.ui.pakkage.IPackageDiagramProvider;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory;
import com.metamatrix.modeler.diagram.ui.preferences.DiagramColorObject;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPaletteManager;
import com.metamatrix.modeler.diagram.ui.util.dialog.LargeDiagramDialog;
import com.metamatrix.modeler.internal.ui.util.DiagramProxy;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.relationship.ui.actions.RelationshipDiagramActionAdapter;
import com.metamatrix.modeler.relationship.ui.figure.RelationshipColorPaletteManager;
import com.metamatrix.modeler.relationship.ui.figure.RelationshipDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.model.RelationshipDiagramModelFactory;
import com.metamatrix.modeler.relationship.ui.part.RelationshipDiagramPartFactory;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipDiagramType extends AbstractDiagramType {
	//============================================================================================================================
	// FIELDS
	//============================================================================================================================
	private static DiagramEditPartFactory    editPartFactory;
	private static DiagramModelFactory       modelFactory;
	private static DiagramFigureFactory      figureFactory;
//	private static IDiagramActionAdapter      actionAdapter;
	private static ColorPaletteManager      colorPaletteManager;
	private static DiagramColorObject       bkgdColorObject;

	private static final int N_LARGE_VALUE = 300;
	private static final String LARGE_DIAGRAM_DIALOG_TITLE = UiConstants.Util.getString("RelationshipDiagramType.largeDiagramDialogTitle");  //$NON-NLS-1$
  
	//============================================================================================================================
	// CONSTRUCTORS
	//============================================================================================================================
	
	/**
	 * Construct an instance of PackageDiagramType.
	 * 
	 */
	public RelationshipDiagramType() {
		super();
	}
	
	//============================================================================================================================
	// METHODS implementing IDiagramType
	//============================================================================================================================
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getEditPartFactory()
	 */
	public DiagramEditPartFactory getEditPartFactory() {
		if( editPartFactory == null )
			editPartFactory = new RelationshipDiagramPartFactory();
	            
		return editPartFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getFigureFactory()
	 */
	public DiagramFigureFactory getFigureFactory() {
		if( figureFactory == null )
			figureFactory = new RelationshipDiagramFigureFactory();
	            
		return figureFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getModelFactory()
	 */
	public DiagramModelFactory getModelFactory() {
		if( modelFactory == null )
			modelFactory = new RelationshipDiagramModelFactory();
	            
		return modelFactory;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getActionAdapter()
	 */
	public IDiagramActionAdapter getActionAdapter(ModelEditorPage editor) {
//		if( actionAdapter == null )
//			actionAdapter = new RelationshipDiagramActionAdapter(editor);
	            
//		return actionAdapter;
		return new RelationshipDiagramActionAdapter(editor);
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDisplayName()
	 */
	public String getDisplayName() {
		return UiConstants.Util.getString("DiagramNames.relationshipDiagram"); //$NON-NLS-1$) ;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getColorPaletteManager()
	 */
	@Override
    public ColorPaletteManager getColorPaletteManager() {
		if( colorPaletteManager == null )
			colorPaletteManager = new RelationshipColorPaletteManager();
	            
		return colorPaletteManager;
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#canOpenContext(java.lang.Object)
	 */
	public boolean canOpenContext(Object input) {
		boolean canOpen = false;
        if( input instanceof EObject ) {
            EObject eObj = (EObject)input;
            // Note that transient diagrams will appear stale (i.e. eObj.eResource() == NULL
            // so we need to defer to their "target" eObjects to make the call
            boolean eObjectIsStale = ModelObjectUtilities.isStale(eObj);
            
    		if( input instanceof Diagram &&
    			((Diagram)input).getType() != null &&
    			((Diagram)input).getType().equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID)) {
                EObject targetEObj = ((Diagram)eObj).getTarget();
                if( targetEObj != null && !ModelObjectUtilities.isStale(targetEObj))
                    canOpen = true;
            } else if(!eObjectIsStale ) {
    		    if( RelationshipDiagramUtil.isRelationshipObject((EObject)input)) {
    		        canOpen = true;
                }
    		}
        }
		return canOpen;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDiagramForContext(java.lang.Object)
	 */
	public Diagram getDiagramForContext(Object actualInput) {
		Diagram depDiagram = null;
        Object input = actualInput;
        if( input instanceof DiagramProxy ) {
            input = ((DiagramProxy)actualInput).getTarget();
            if( input == null ) {
                // We have a package diagram under model resource
                // Need to set the input to the ModelResource
                input = ((DiagramProxy)actualInput).getModelResource();
            }
        }
        
		if( input instanceof Diagram &&
			((Diagram)input).getType() != null &&
			((Diagram)input).getType().equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID)) {
			depDiagram = (Diagram)input;
		} else 	if( input instanceof Diagram &&
			((Diagram)input).getType() != null &&
			((Diagram)input).getType().equals(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID)) {
			depDiagram = (Diagram)input;
		} else if( input instanceof EObject && RelationshipDiagramUtil.isRelationshipObject((EObject)input)) {
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject((EObject)input);
			depDiagram = RelationshipDiagramUtil.getRelationshipDiagram(modelResource, (EObject)input, this, true);
		} else if( input instanceof ModelResource ) {
			// We have an input of a model, so we want to find it's top level relationship diagram
			if( RelationshipDiagramUtil.isRelationshipModelResource((ModelResource)input) )
				depDiagram = RelationshipDiagramUtil.getRelationshipDiagram((ModelResource)input, null, this, true);
		}
	            
		return depDiagram;
	}
	
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDiagramForGoToMarkerEObject(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Diagram getDiagramForGoToMarkerEObject(EObject eObject) {
        return getDiagramForContext(eObject);
    }
	    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getPackageDiagramProvider()
	 */
	@Override
    public IPackageDiagramProvider getPackageDiagramProvider() {
		return new RelationshipDiagramProvider();
	}
	    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getBackgroundColorObject()
	 */
	public DiagramColorObject getBackgroundColorObject(String extensionID) {
		  if( bkgdColorObject == null ) {
			  bkgdColorObject = new DiagramColorObject(getDisplayName(), PluginConstants.Prefs.Appearance.RELATIONSHIP_BKGD_COLOR);
		  }
	            
		return bkgdColorObject;
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDisplayedPath(com.metamatrix.metamodels.diagram.Diagram, org.eclipse.emf.ecore.EObject)
	 */
	public String getDisplayedPath(Diagram diagram, EObject eObject) {
		String path = null;
		if( diagram.getType() != null && diagram.getType().equals(PluginConstants.RELATIONSHIP_DIAGRAM_TYPE_ID)) {
			// Check to see if the modelResource for this class is same as diagram.
			if( ! ModelUtilities.areModelResourcesSame(diagram, eObject) )
				path = ModelObjectUtilities.getTrimmedFullPath(eObject);
			else  
				path = ModelObjectUtilities.getTrimmedRelativePath(eObject);
		}
	        
		return path;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#isDiagramLarge(com.metamatrix.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean isDiagramLarge(Diagram diagram) {
		return getDiagramObjectCount(diagram) > N_LARGE_VALUE;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.IDiagramType#isDiagramTooLarge(com.metamatrix.metamodels.diagram.Diagram)
	 */
	@Override
    public boolean isDiagramTooLarge(Diagram diagram) {
	    int maxLimit = DiagramUiPlugin.getDefault().getPreferenceStore().getInt(com.metamatrix.modeler.diagram.ui.PluginConstants.Prefs.LARGE_MODEL_SIZE);
	    int nObjects = getDiagramObjectCount(diagram);
		boolean tooLarge = nObjects > maxLimit;
		if( tooLarge ) {
			Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			LargeDiagramDialog largeDiagramDialog = new LargeDiagramDialog(shell,LARGE_DIAGRAM_DIALOG_TITLE, nObjects, maxLimit); 
			largeDiagramDialog.open();
		}
		return tooLarge;
	}

	private int getDiagramObjectCount(Diagram diagram) {
		int nObjects = 0;
		
		Object targetObject = diagram.getTarget();
		List contents = null;

		if( targetObject != null && targetObject instanceof EObject && !(targetObject instanceof ModelAnnotation) ) {            
			contents = diagram.getTarget().eContents();
		} else if( targetObject != null && targetObject instanceof EObject && targetObject instanceof ModelAnnotation) {
			ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(diagram);
			if( modelResource != null ) {
				try {
					contents = modelResource.getEObjects();
				} catch (ModelWorkspaceException e) {
					contents = Collections.EMPTY_LIST;
					String message = DiagramUiConstants.Util.getString("ModelErrors.getContentError", modelResource.getItemName()); //$NON-NLS-1$
					UiConstants.Util.log(IStatus.ERROR, e, message); 
				}
			} else {
				contents = Collections.EMPTY_LIST;
			}
		} else {
			contents = Collections.EMPTY_LIST;
		}
		
		nObjects = contents.size();

		
		if( nObjects > 1 ) {
			EObject nextEObject = null;
			// Let's gather up all sub objects
			Iterator iter = contents.iterator();
			while( iter.hasNext() ) {
				nextEObject = (EObject)iter.next();
				if( nextEObject.eContents() != null && !nextEObject.eContents().isEmpty() )
					nObjects += nextEObject.eContents().size();
			}
		}
		
		return nObjects;
	}
    
    /** 
     * @see com.metamatrix.modeler.diagram.ui.IDiagramType#getDiagramSelectionStandin(com.metamatrix.metamodels.diagram.Diagram)
     * @since 4.2
     */
    @Override
    public Object getDiagramSelectionStandin(Diagram diagram) {
        // for package diagrams, let's get the target
        Object target = diagram.getTarget();
        if( target instanceof ModelAnnotation ) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(diagram);
            IResource ir = null;
            if( mr != null ) {
                
                
                try {
                    ir = mr.getCorrespondingResource();
                } catch (ModelWorkspaceException err) {
                    String message = DiagramUiConstants.Util.getString("ModelErrors.getIResourceerror", mr.getItemName()); //$NON-NLS-1$
                    DiagramUiConstants.Util.log(IStatus.ERROR, err, message); 
                }
            }
            return ir;
        }
        
        return target;
    }

}
