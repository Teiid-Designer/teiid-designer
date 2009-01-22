/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.connection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Font;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.connection.AbstractNodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionAnchor;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.figure.LabeledRectangleFigure;
import com.metamatrix.modeler.diagram.ui.model.AssociationLabelModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.LabelModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.transformation.ui.model.TransformationNode;

/**
 * DiagramAssociation
 */
public class TransformationLink extends AbstractNodeConnectionModel {
                                                                  
    private static final String EMPTY_LABEL = " "; //$NON-NLS-1$ 
    
    private DiagramModelNode sourceNode;
    private DiagramModelNode targetNode;

    private LabelModelNode suidLabelNode;
    private LabelModelNode aliasLabelNode;
    
    private String sName; 
    
    private int ltCurrentLinkType = DiagramLinkType.DIRECTED;
     
    private static final int DELTA_X = 6;
    private static final int DELTA_Y = 4;
    
    private String sRouterStyle;
          
    /**
     * Construct an instance of TransformationLink
     * 
     */
    public TransformationLink(DiagramModelNode source, DiagramModelNode target ) {
        super();
        setSourceNode(source);
        setTargetNode(target);        
        init();
    }
    
    /**
     * Construct an instance of TransformationLink.
     * 
     */
    public TransformationLink(DiagramModelNode source, DiagramModelNode target, String sName ) {
        super();
        
        this.sName = sName;
        setSourceNode(source);
        setTargetNode(target);        
        init();
    }
    
    private void init() {
//        String suidText = getSuidText();
//        if( suidText != null )
//            suidLabelNode  = new AssociationLabelModelNode( suidText, AssociationLabelModelNode.SOURCE_ROLE_NAME );
//        
//        String aliasText = getAliasText();
//        if( aliasText != null )
//            aliasLabelNode  = new AssociationLabelModelNode( aliasText, AssociationLabelModelNode.NAME );
    }
    
    @Override
    public void updateLabels() {
        String suidText = getSuidText();
        if( suidText != null ) {
            if( suidLabelNode == null ) {
                suidLabelNode = new AssociationLabelModelNode( suidText, AssociationLabelModelNode.SOURCE_ROLE_NAME );
            } else
                suidLabelNode.setName(suidText);
            suidLabelNode.update();
        }
        
        String aliasText = getAliasText();
        if( aliasText != null ) {
            if( aliasLabelNode == null ) {
                aliasLabelNode = new AssociationLabelModelNode( aliasText, AssociationLabelModelNode.NAME );
            } else
                aliasLabelNode.setName(aliasText);
            aliasLabelNode.update();
        }
    }
    
    private String getSuidText() {
        String text = EMPTY_LABEL;
        
        if( getTargetNode() instanceof TransformationNode ) {
            // get the target for this transformation
            EObject targetTableEObject = TransformationHelper.getTransformationLinkTarget(((DiagramModelNode)getTargetNode()).getModelObject());
            if(  TransformationHelper.tableSupportsUpdate(targetTableEObject) ) {
                // We get the SUID status from the SqlTransformation object
                // Let's get the source node model object
                text = "s"; //$NON-NLS-1$
                if( TransformationHelper.supportsUpdate(((DiagramModelNode)getTargetNode()).getModelObject(), ((DiagramModelNode)getSourceNode()).getModelObject() ))
                    text = text + "u"; //$NON-NLS-1$
                if( TransformationHelper.supportsInsert(((DiagramModelNode)getTargetNode()).getModelObject(), ((DiagramModelNode)getSourceNode()).getModelObject() ))
                    text = text + "i"; //$NON-NLS-1$
                if( TransformationHelper.supportsDelete(((DiagramModelNode)getTargetNode()).getModelObject(), ((DiagramModelNode)getSourceNode()).getModelObject() ))
                    text = text + "d"; //$NON-NLS-1$
            }
        } else if( com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable(((DiagramModelNode)getTargetNode()).getModelObject()) ) {
            EObject tableEObject = ((DiagramModelNode)getTargetNode()).getModelObject();
            if( TransformationHelper.tableSupportsUpdate(tableEObject) )
                text = "suid"; //$NON-NLS-1$
        }
        
        return text;
    }
    
    
    private String getAliasText() {
        String text = EMPTY_LABEL;
        
        if( getTargetNode() instanceof TransformationNode ) {
            List aliases = 
            	TransformationHelper
            		.getSqlAliasesForSource( 
            			((DiagramModelNode)getTargetNode()).getModelObject(),
            	 		((DiagramModelNode)getSourceNode()).getModelObject());
            if( aliases != null && !aliases.isEmpty() && aliases.size() > 1 ) {
                text = EMPTY_LABEL + aliases.size();
//                Iterator iter = aliases.iterator();
//                SqlAlias nextAlias = null;
//                int count = 0;
//                while( iter.hasNext() ) {
//                    nextAlias = (SqlAlias)iter.next();
//                    if( ! nextAlias.getAlias().equals(getSourceNode().getName()) ) {
//                        if( count == 0 )
//                            text = "AS: "; //$NON-NLS-1$
//                        if( count > 0 )
//                            text += ", "; //$NON-NLS-1$
//                        text += nextAlias.getAlias();
//                        count++;
//                    }
//                }
            }
        }
        
        return text;
    }

    public void setSource(DiagramModelNode iSource) {
        sourceNode = iSource;
    }

    public void setTarget(DiagramModelNode iTarget) {
        targetNode = iTarget;
    }


    @Override
    public void setName( String sName ) {
        this.sName = sName;   
    }
    
    /**
     * @param 
     */
    @Override
    public String getName() {
        return sName;
    }

    @Override
    public void layout() {
//        System.out.println( "[DiagramAssociation.layout 0 arg ] TOP, node name: " + getName() );  //$NON-NLS-1$                 
    }

    @Override
    public void setRouterStyle( String sRouterStyle ) {
        
        if ( this.sRouterStyle == null || !this.sRouterStyle.equals( sRouterStyle ) ) {
        
            this.sRouterStyle = sRouterStyle;
            // refresh the label layout manager
            
        }            
    }
    
    @Override
    public int getRouterStyle() {
        return DiagramUiConstants.LinkRouter.DIRECT;
    }

    
    /**
     * @return sourceNode
     */
    @Override
    public Object getSourceNode() {
        return sourceNode;
    }

    /**
     * @return targetNode
     */
    @Override
    public Object getTargetNode() {
        return targetNode;
    }

    /**
     * @param node
     */
    @Override
    public void setSourceNode(Object node) {
        sourceNode = (DiagramModelNode)node;
    }

    /**
     * @param node
     */
    @Override
    public void setTargetNode(Object node) {
        targetNode = (DiagramModelNode)node;
    }

    /**
     * @return
     */
    public LabelModelNode getNameLabel() {
        return aliasLabelNode;
    }

    /**
     * @return
     */
    public LabelModelNode getSuidLabel() {
        return suidLabelNode;
    }


    /**
     * @param node
     */
    public void setName(LabelModelNode node) {
        aliasLabelNode = node;
    }

    /**
     * @param node
     */
    public void setSuidLabel(LabelModelNode node) {
        suidLabelNode = node;
    }

    
    @Override
    public List getLabelNodes() {
        List currentLabels = new ArrayList();
        
        if(suidLabelNode != null )
            currentLabels.add(suidLabelNode);
        if(aliasLabelNode != null )
            currentLabels.add(aliasLabelNode);

        
        if( currentLabels.isEmpty() )
            return Collections.EMPTY_LIST;
        return currentLabels;
    }

    public void clearLabelNodes() {
        suidLabelNode = null;
        aliasLabelNode = null;
    }

// =====================================================================
//  Methods to support firing PropertyChangeEvents
// =====================================================================
   transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    

   @Override
public void addPropertyChangeListener(PropertyChangeListener l) {
       listeners.addPropertyChangeListener(l);
   }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        listeners.removePropertyChangeListener(l);
    }
    
    @Override
    public void firePropertyChange(String prop, Object old, Object newValue) {
        listeners.firePropertyChange(prop, old, newValue);
    }

    // Used to alert the Edit Part that the children have been modified
    // and a refreshChildren() is needed.
    public void fireStructureChange(String prop, Object child) {
        listeners.firePropertyChange(prop, null, child);
    }
    

// =====================================================================
//  Layout code (rewritten from DCAssocation (modeler 3.1))
// =====================================================================


    public int getLinkType() {
        return ltCurrentLinkType;
    }   

    private void updateFonts( DiagramEditPart adepParentEditPart ) {
        // always run this; it may have changed since the last time layout was called...
        Font fntNew = getLabelFont();
            
        updateFontOnLabelFigure( adepParentEditPart, suidLabelNode, fntNew );
        updateFontOnLabelFigure( adepParentEditPart, aliasLabelNode, fntNew );                      
    }
          
    private void updateFontOnLabelFigure( DiagramEditPart adepParentEditPart,
                                          LabelModelNode lmnLabel,                                 
                                          Font fntNew ) {
                                              
                                              
        if( lmnLabel != null ) {
    
            DiagramEditPart dep = (DiagramEditPart)adepParentEditPart.getParent();
                                                       
            DiagramEditPart depLabelled = dep.getEditPart( lmnLabel );
            
            if( depLabelled != null ) {
                DiagramFigure df1 = depLabelled.getDiagramFigure();        
        
                if ( df1 instanceof LabeledRectangleFigure ) {
                    ((LabeledRectangleFigure)df1).updateForFont( fntNew );   
                }
            }
        }
                      
    }
    
    private Font getLabelFont() {
		return ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);
//        int iCurrGeneralFontSize = ScaledFontManager.getSize();
//        int iNewLabelFontSize    = 0;
//        
//        if ( ScaledFontManager.canDecrease( iCurrGeneralFontSize - 2 ) ) {
//            // we can decrease by 3
//            iNewLabelFontSize   = iCurrGeneralFontSize - 3;                       
//        } 
//        else
//        if ( ScaledFontManager.canDecrease( iCurrGeneralFontSize - 1 ) )  {
//            // we can decrease by 2
//            iNewLabelFontSize   = iCurrGeneralFontSize - 2;                       
//        }
//        else
//        if ( ScaledFontManager.canDecrease( iCurrGeneralFontSize 
//        ) ) {
//            // we can only decrease by 1
//            iNewLabelFontSize   = iCurrGeneralFontSize - 1;                                       
//        } else {
//            // no room to decrease at all
//            iNewLabelFontSize   = iCurrGeneralFontSize;                                                       
//        }
//                    
//        
//        // construct the new font
//        Font fnt =  new Font( null,
//                              ScaledFontManager.getName(), 
//                              iNewLabelFontSize, 
//                              ScaledFontManager.getStyle() );
//
//        return fnt;
    }


   /**
    * Creates all the labels and icons for the link.
    */
    @Override
    public void layout( ConnectionAnchor sourceAnchor, 
						ConnectionAnchor targetAnchor,
                        DiagramEditPart adepParentEditPart ) {
        boolean isTNode = (sourceAnchor instanceof ChopboxAnchor);
        
        if( isTNode ) {
            if( canShowSuid() ) {
                // We need to treat this differently.
                // Go ahead and place the suid at a point right justified above the arrow point.
                updateLabels();
                
                // update the fonts of each label before any other calcs                    
                updateFonts( adepParentEditPart );
                
                int arrowPtX = ((DiagramModelNode)getSourceNode()).getX();
                int arrowPtY = ((DiagramModelNode)getSourceNode()).getY() + ((DiagramModelNode)getSourceNode()).getHeight()/2;
                
                double strHeight    = suidLabelNode.getHeight();
                double strWidth     = suidLabelNode.getWidth();

                int strX = arrowPtX - (int)strWidth - 4;
                int strY = arrowPtY - (int)strHeight - 4;
                suidLabelNode.setPosition(new Point( strX, strY ));
            }
        } else{
    		// Check here!!! because we don't want to process generic connection anchors...
    		if( !(sourceAnchor instanceof NodeConnectionAnchor) )
    			return; 
    			
            // This is the hook to go back to model properties and get the SUID and Name values
            // layout is called from refreshAssociations() in the Node edit part.
    
            
            if( suidLabelNode == null ) {
                // If SUID was turned from false to true
                // this will update the label "name" and should fire a property change
                // and ultimately change the label figure text.
                updateLabels();
                return;
            }
    
            updateLabels();
                         
            // update the fonts of each label before any other calcs                    
            updateFonts( adepParentEditPart );   
            
            Point pSourceAnchor = null;                     
            
            int sourceX = ((NodeConnectionAnchor)sourceAnchor).getOffsetH() + ((DiagramModelNode)getSourceNode()).getX();
            int sourceY = ((NodeConnectionAnchor)sourceAnchor).getOffsetV() + ((DiagramModelNode)getSourceNode()).getY();
            
            pSourceAnchor = new Point( sourceX, sourceY );
            
            double deltaX = DELTA_X;
            double deltaY = DELTA_Y;
    
            int sourceSide = 0;
        
            // Get End Points (Intersections)
            Point sourceStartPt  = pSourceAnchor;
            sourceSide = ((NodeConnectionAnchor)sourceAnchor).getDirection();

            double sourceAngleInRadians = getSourceAngleInRadians( sourceSide );

            int tmpPtX = 0;
            int tmpPtY = 0;
            double startX = 0;
            double startY = 0;
            double strHeight = 0;
            double strWidth = 0;
            double tmpAngle = 0;
        
            if( canShowSuid() ) {
               // LOCATE SOURCE SUID STRING
                   strHeight = suidLabelNode.getHeight();
                   strWidth = suidLabelNode.getWidth();
                   startX = sourceStartPt.x;
                   startY = sourceStartPt.y;
                   if( getLinkType() == DiagramLinkType.ORTHOGONAL ) {
                       tmpAngle = Math.toDegrees(sourceAngleInRadians);
                   } else
                       tmpAngle = 360.0 - Math.toDegrees(sourceAngleInRadians);
     
                    switch( sourceSide ) {
                       case AnchorManager.NORTH: {
                           if( tmpAngle >= 60 && tmpAngle <= 120 ) {
                               // Baseline.  Place at upper left of intersection.
                           tmpPtX = (int)(startX - deltaX - strWidth);
                           tmpPtY = (int)(startY - deltaY - strHeight);
                       } else if( tmpAngle < 60 ) {
                           // Baseline.  Place at X of intersection left justified.
                           tmpPtX = (int)(startX - strWidth);
                           tmpPtY = (int)(startY - deltaY - strHeight);
                       } else {
                           // Baseline.  Place at X of intersection right justified.
                           tmpPtX = (int)(startX);
                           tmpPtY = (int)(startY - deltaY - strHeight);
                       }
                   } break;
                
                   case AnchorManager.SOUTH: {
                       if( tmpAngle >= 240 && tmpAngle <= 300 ) {
                           // Baseline.  Place at lower left of intersection.
                           tmpPtX = (int)(startX - deltaX - strWidth);
                           tmpPtY = (int)(startY + deltaY);
                       } else if( tmpAngle < 240 ) {
                           // Baseline.  Place at X of intersection left right justified.
                           tmpPtX = (int)(startX - strWidth);
                           tmpPtY = (int)(startY + deltaY);
                       } else {
                           // Baseline.  Place at X of intersection right justified.
                           tmpPtX = (int)(startX);
                           tmpPtY = (int)(startY + deltaY );
                       }
                   } break;
                
                   case AnchorManager.EAST: {
                       if( tmpAngle <= 20 || tmpAngle >= 340 ) {
                           // Baseline.  Place at upper right of intersection.
                           tmpPtX = (int)(startX + deltaX);
                           tmpPtY = (int)(startY - deltaY - strHeight);
                       } else if( tmpAngle > 20 && tmpAngle <= 91 ) {
                           // Baseline.  Place at Y of intersection left justified.
                           tmpPtX = (int)(startX + deltaX);
                           tmpPtY = (int)(startY);
                       } else {
                           // Baseline.  Place at Y of intersection right justified.
                           tmpPtX = (int)(startX + deltaX);
                           tmpPtY = (int)(startY - strHeight);
                       }    
                   } break;
                
                   case AnchorManager.WEST: {
                       if( tmpAngle <= 200 && tmpAngle >= 160 ) {
                           // Baseline.  Place at upper left of intersection.
                           tmpPtX = (int)(startX - deltaX - strWidth);
                           tmpPtY = (int)(startY - deltaY - strHeight);
                       } else if( tmpAngle > 89 && tmpAngle < 160 ) {
                           // Baseline.  Place at Y of intersection left justified.
                           tmpPtX = (int)(startX - deltaX - strWidth);
                           tmpPtY = (int)(startY - strHeight);
                       } else {
                           // Baseline.  Place at Y of intersection right justified.
                           tmpPtX = (int)(startX - deltaX - strWidth);
                           tmpPtY = (int)(startY - strHeight);
                       }
                   } break;
                
                   default:
                   break;
               }
               suidLabelNode.setPosition(new Point( tmpPtX, tmpPtY ));
           }
           
           if( canShowAliasedValues() ) {
               // LOCATE SOURCE ALIAS STRING
               strHeight = aliasLabelNode.getHeight();
               strWidth = aliasLabelNode.getWidth();
               startX = sourceStartPt.x;
               startY = sourceStartPt.y;
               if( getLinkType() == DiagramLinkType.ORTHOGONAL ) {
                   tmpAngle = Math.toDegrees(sourceAngleInRadians);
               } else
                   tmpAngle = 360.0 - Math.toDegrees(sourceAngleInRadians);
    
               switch( sourceSide ) {
                  case AnchorManager.NORTH: {
                      if( tmpAngle >= 60 && tmpAngle <= 120 ) {
                          // Baseline.  Place at upper left of intersection.
                          tmpPtX = (int)(startX + deltaX);
                          tmpPtY = (int)(startY - deltaY - strHeight);
                      } else if( tmpAngle < 60 ) {
                          // Baseline.  Place at X of intersection left justified.
                          tmpPtX = (int)(startX - strWidth);
                          tmpPtY = (int)(startY - deltaY - strHeight*2);
                      } else {
                          // Baseline.  Place at X of intersection right justified.
                          tmpPtX = (int)(startX);
                          tmpPtY = (int)(startY - deltaY - strHeight*2);
                      }
                  } break;
                
                  case AnchorManager.SOUTH: {
                      if( tmpAngle >= 240 && tmpAngle <= 300 ) {
                          // Baseline.  Place at lower left of intersection.
                          tmpPtX = (int)(startX + deltaX);
                          tmpPtY = (int)(startY + deltaY);
                      } else if( tmpAngle < 240 ) {
                          // Baseline.  Place at X of intersection left right justified.
                          tmpPtX = (int)(startX );
                          tmpPtY = (int)(startY + deltaY + strHeight);
                      } else {
                          // Baseline.  Place at X of intersection right justified.
                          tmpPtX = (int)(startX - strWidth);
                          tmpPtY = (int)(startY + deltaY + strHeight );
                      }
                  } break;
                
                  case AnchorManager.EAST: {
                      if( tmpAngle <= 20 || tmpAngle >= 340 ) {
                          // Baseline.  Place at upper right of intersection.
                          tmpPtX = (int)(startX + deltaX);
                          tmpPtY = (int)(startY + deltaY);
                      } else if( tmpAngle > 20 && tmpAngle <= 91 ) {
                          // Baseline.  Place at Y of intersection left justified.
                          tmpPtX = (int)(startX + deltaX);
                          tmpPtY = (int)(startY + strHeight);
                      } else {
                          // Baseline.  Place at Y of intersection right justified.
                          tmpPtX = (int)(startX + deltaX);
                          tmpPtY = (int)(startY - strHeight*2);
                      }    
                  } break;
                
                  case AnchorManager.WEST: {
                      if( tmpAngle <= 200 && tmpAngle >= 160 ) {
                          // Baseline.  Place at upper left of intersection.
                          tmpPtX = (int)(startX - deltaX - strWidth);
                          tmpPtY = (int)(startY + deltaY);
                      } else if( tmpAngle > 89 && tmpAngle < 160 ) {
                          // Baseline.  Place at Y of intersection left justified.
                          tmpPtX = (int)(startX - deltaX - strWidth);
                          tmpPtY = (int)(startY + strHeight);
                      } else {
                          // Baseline.  Place at Y of intersection right justified.
                          tmpPtX = (int)(startX - deltaX - strWidth);
                          tmpPtY = (int)(startY - strHeight*2);
                      }
                  } break;
                
                  default:
                  break;
               }
               aliasLabelNode.setPosition(new Point( tmpPtX, tmpPtY ));
            }
        }
    }
    



    @Override
    public void placeStereotypeAndName(  int iSourceSide,   
                                         int iTargetSide, 
                                         PointList plConnectionPoints ) {
    }
 
        
    private double getSourceAngleInRadians( int iSourceSide ) {
        // Code for Source End Object
        double sourceAngleInRadians = 0.0;


        // calc angle           
        if( getLinkType() == DiagramLinkType.ORTHOGONAL ) {
            if( iSourceSide == AnchorManager.WEST )
                sourceAngleInRadians = Math.PI;
            else if( iSourceSide == AnchorManager.EAST )
                sourceAngleInRadians = 0;
            else if( iSourceSide == AnchorManager.NORTH )
                sourceAngleInRadians = Math.PI/2;
            else sourceAngleInRadians = 3*Math.PI/2;
        


        } else {            
            // NO logic yet
        } 
        
        return sourceAngleInRadians;
    }

   private boolean canShowSuid() {
       if( suidLabelNode != null && 
           suidLabelNode.getName().length() > 0 ) {
           
                return true;
           }
            return false;
   }
   
    
   private boolean canShowAliasedValues() {
       if( aliasLabelNode != null && 
           aliasLabelNode.getName().length() > 0 ) {
                return true;
           }
           return false;
   }


    @Override
    public String toString() {
        String suidText = null;
        if (suidLabelNode != null)
            suidText = suidLabelNode.getDisplayString();
        String nameText = null;
        if (aliasLabelNode != null)
            nameText = aliasLabelNode.getDisplayString();
            
        return new StringBuffer().append(" TransformationLink:") //$NON-NLS-1$
        .append(" Source Node = ").append(sourceNode.getName()) //$NON-NLS-1$
        .append(" Target Node = ").append(targetNode.getName()) //$NON-NLS-1$
        .append(" SUID  = ").append(suidText) //$NON-NLS-1$
        .append(" Aliases  = ").append(nameText) //$NON-NLS-1$
        .toString();
    }
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDecoratorId()
	 */
	@Override
    public int getTargetDecoratorId() {
		return BinaryAssociation.DECORATOR_NONE;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDecoratorId()
	 */
	@Override
    public int getSourceDecoratorId() {
		return BinaryAssociation.DECORATOR_NONE;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getLineStyle()
	 */
	@Override
    public int getLineStyle() {
		return BinaryAssociation.LINE_SOLID;
	}
	

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getToolTipString()
	 */
	@Override
    public List getToolTipStrings() {
		List newList = new ArrayList(1);
		newList.add(sName);
		return newList;
	}

}


