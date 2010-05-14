/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.text.TextViewer;

    /**
     * TextFontManager
     */
    public class TextFontManager implements ScaledFont {
        private TextViewer viewer;
        private ScaledFontManager sfmManager;
        private List listeners = new ArrayList();


        /**
         * Construct an instance of TextFontManager.
         * 
         */
        public TextFontManager( TextViewer viewer, ScaledFontManager sfmManager ) {
            super();
            this.viewer     = viewer;
            this.sfmManager = sfmManager;
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#increase()
         */
        public void increase() {
            if ( sfmManager.canIncrease() ) {            
                sfmManager.increase();
                updateTextWidget();
            }                                
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#decrease()
         */
        public void decrease() {
            if ( sfmManager.canDecrease() ) {            
                sfmManager.decrease();
                updateTextWidget();
            }                                
        }

        public void updateTextWidget() {
            if ( sfmManager != null && viewer.getTextWidget() != null ) {
            
                viewer.getTextWidget()
                    .setFont( sfmManager.createFontOfSize( sfmManager.getSize() ) );
                viewer.getTextWidget().update();                    
                fireFontChanged();
            }
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#canIncrease()
         */
        public boolean canIncrease() {
            return sfmManager.canIncrease();
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#canDecrease()
         */
        public boolean canDecrease() {
            return sfmManager.canDecrease();
        }

        /**
         * Adds the given IFontChangeListener to this ZoomManager's list of listeners.
         * @param listener the IFontChangeListener to be added
         */
        public void addFontChangeListener(IFontChangeListener listener) {
            listeners.add(listener);
        }
    
        /**
         * Removes the given IFontChangeListener from this ZoomManager's list of listeners.
         * @param listener the IFontChangeListener to be removed
         */
        public void removeFontChangeListener(IFontChangeListener listener) {
            listeners.remove(listener);
        }

        /**
         * Notifies listeners that the zoom level has changed.
         */
        public void fireFontChanged() {
//            System.out.println("[TextFontManager.fireFontChanged]"); //$NON-NLS-1$            
            Iterator iter = listeners.iterator();
            while (iter.hasNext())
                ((IFontChangeListener)iter.next()).fontChanged();
        }

        public void setViewer( TextViewer viewer ) {
            this.viewer = viewer;
        }
    
    }

