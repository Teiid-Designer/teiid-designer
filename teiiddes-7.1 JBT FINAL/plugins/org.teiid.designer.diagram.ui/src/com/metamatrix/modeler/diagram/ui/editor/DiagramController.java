/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.ISelectionListener;
import com.metamatrix.metamodels.diagram.Diagram;

/**
 * DiagramController
 */
public interface DiagramController extends ISelectionListener {
    /**
     * Method to set the editor for the controller
     * @param editor
     */
    void setDiagramEditor(DiagramEditor editor);
    
    /**
     * Method which the controller can use to wire itself to the diagram.
     * @param input
     */
    void wireDiagram(Diagram input);
    
    /**
     * Method to allow the controller to remove all listeners (i.e unwire itself)
     *
     */
    void deactivate();
    
    /**
     *  Method to allow the controller to clean up any remaining wiring
     * 
     * @since 5.0
     */
    void dispose();
    
    ISelectionProvider getSelectionSource();
    
    /**
     * Method to allow the controller to update any contents based on a notification.
     * @param notification
     */
    void handleNotification(Notification notification);
    
    /**
     * Method which the diagram editor can use to determine if the controller needs to be 
     * refreshed
     * @param newDiagram
     * @return
     */
    boolean maintainControl(Diagram newDiagram);
    
    /**
     * Method used by the diagram editor to set the diagram in the controller to the new diagram.
     * @param newDiagram
     */
    void rewireDiagram(Diagram newDiagram);
    
    /**
     * Method used to clear any selections in the diagram without firing selection event.
     *
     */
    void clearDiagramSelection();
    
	/**
	 * Method used to tell the diagram controller that it should adjust because diagram autolayout was called.
	 *
	 */
	void updateForAutoLayout();
    
    /**
     * Method used to tell the diagram controller that the zoom value has changed.
     *
     */
    void handleZoomChanged();
}
