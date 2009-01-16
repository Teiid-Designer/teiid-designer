/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.ui.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.graphics.Font;

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
//            System.out.println("[TextFontManager.increase]"); //$NON-NLS-1$            
            
            if ( sfmManager.canIncrease() ) {            
                sfmManager.increase();
                updateTextWidget();
            }                                
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#decrease()
         */
        public void decrease() {
//            System.out.println("[TextFontManager.decrease]"); //$NON-NLS-1$            
                                            
            if ( sfmManager.canDecrease() ) {            
                sfmManager.decrease();
                updateTextWidget();
            }                                
        }

        public void updateTextWidget() {
//            System.out.println("[TextFontManager.updateTextWidget]"); //$NON-NLS-1$

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

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#getFont()
         */
        public Font getFont() {
            return sfmManager.getFont();
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#getName()
         */
        public String getName() {
            return sfmManager.getName();
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#getSize()
         */
        public int getSize() {
            return sfmManager.getSize();
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#getStyle()
         */
        public int getStyle() {
            return sfmManager.getStyle();
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#setFont(org.eclipse.swt.graphics.Font)
         */
        public void setFont(Font newFont) {
            sfmManager.setFont(newFont);
        }
    
        public void setFont(String typeName, int size, int style) {
            sfmManager.setFont(typeName, size, style);
        }

        /* (non-Javadoc)
         * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#setSize(int)
         */
        public void setSize(int newSize) {
            sfmManager.setSize(newSize);
            fireFontChanged();
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

