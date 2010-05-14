/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.part;

import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Point;

public class RelationalDropEditPartHelper extends DropEditPartHelper {
	private DiagramEditPart editPart;
	private boolean hilited = false;
	
	
	public RelationalDropEditPartHelper(DiagramEditPart editPart) {
		super();
		this.editPart = editPart;
	}

	@Override
    public void drop(Point dropPoint, List dropList) {

		super.drop(dropPoint, dropList);
	}

	@Override
    public Point getLastHoverPoint() {

		return super.getLastHoverPoint();
	}

	@Override
    public void hilite(boolean hilite) {
        if (hilite && !hilited) {
            hilited = true;
            editPart.hiliteBackground(ColorConstants.lightGreen);

        } else if (!hilite && hilited) {
        	editPart.hiliteBackground(null);
            hilited = false;
        }
	}

	@Override
    public void setLastHoverPoint(Point lastHoverPoint) {

		super.setLastHoverPoint(lastHoverPoint);
	}

	public boolean allowsDrop(Object target, List dropList) {
		return false;
//		return canCreateAssociation(dropList);
	}

//	private List getEObjectList(List dropList) {
//		if( editPart.getModelObject() instanceof Diagram )
//			return Collections.EMPTY_LIST;
//		
//		List eObjs = new ArrayList(dropList.size() + 1);
//		for( Iterator iter = dropList.iterator(); iter.hasNext(); ) {
//			Object nextObj = iter.next();
//			if( nextObj instanceof EObject ) {
//				eObjs.add(nextObj);
//			} else {
//				return Collections.EMPTY_LIST;
//			}
//		}
//		eObjs.add(editPart.getModelObject());
//		
//		return eObjs;
//	}
	
//    private boolean canCreateAssociation(List dropList) {
//        boolean canCreate = false;
//        
//        if( !isDiagramReadOnly() && isPackageDiagram() ) {
//            List selectedEObjects = getEObjectList(dropList);;
//            if( selectedEObjects.size() > 1 ) {
//                try {
//                    Collection descriptors = ModelerCore.getModelEditor().getNewAssociationDescriptors(selectedEObjects);
//                    if( descriptors.size() == 1 ) {
//                        AssociationDescriptor theDescriptor = (AssociationDescriptor)descriptors.iterator().next();
//
//                        if( theDescriptor.isAmbiguous() ) {
//                            DiagramUiConstants.Util.log( 
//                                IStatus.INFO, 
//                                "HiliteDndNodeSelectionEditPolicy.canCreateAssociation() The Association Descriptor is AMBIGUOUS"); //$NON-NLS-1$
//                        }
//                    
//                        if( theDescriptor.isComplete() )
//                            canCreate = true;
//                    } else {
//						Iterator iter = descriptors.iterator();
//						AssociationDescriptor theDescriptor = null;
//						while( iter.hasNext() ) {
//							theDescriptor = (AssociationDescriptor)iter.next();
//							if( theDescriptor.isAmbiguous() ) {
//							DiagramUiConstants.Util.log( 
//								IStatus.INFO, 
//								"HiliteDndNodeSelectionEditPolicy.canCreateAssociation() The Association Descriptor is AMBIGUOUS"); //$NON-NLS-1$
//							}
//							
//							if( theDescriptor.isComplete() )
//								canCreate = true;
//								
//							if( canCreate ) {
//								break;
//							}
//						}
//                    }
//                } catch (ModelerCoreException theException) {
//                    DiagramUiConstants.Util.log( 
//                        IStatus.ERROR, 
//                        "HiliteDndNodeSelectionEditPolicy.canCreateAssociation() ERROR getting New Association Descriptors"); //$NON-NLS-1$
//                }
//            }
//        }
//        return canCreate;
//    }
//	
//    protected boolean isDiagramReadOnly() {
//        Diagram diagram = ((DiagramViewer)editPart.getViewer()).getEditor().getDiagram();
//
//        return ModelObjectUtilities.isReadOnly(diagram);
//    }
//    
//    private boolean isPackageDiagram() {
//        boolean result = false;
//        Diagram diagram = ((DiagramViewer)editPart.getViewer()).getEditor().getDiagram();
//        if( diagram != null && 
//            diagram.getType() != null &&
//            diagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID))
//            result = true;
//            
//        return result;
//    }
}
