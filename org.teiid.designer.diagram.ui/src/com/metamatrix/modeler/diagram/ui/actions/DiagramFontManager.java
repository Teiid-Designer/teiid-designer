/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * DiagramFontManager
 */
public class DiagramFontManager implements ScaledFont {
    private DiagramViewer viewer;
    private ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

    /**
     * Construct an instance of DiagramFontManager.
     * 
     */
    public DiagramFontManager(DiagramViewer viewer) {
        super();
        setFontFromPreferences();
        this.viewer = viewer;
    }

    public void setFontFromPreferences() {
        IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();
        FontData fontData = PreferenceConverter.getFontData(preferenceStore,
                PluginConstants.Prefs.Appearance.FONT);
        String currentName = fontData.getName();
        int currentSize = fontData.getHeight();
        int currentStyle = fontData.getStyle();
        ScaledFontManager.setFont(currentName, currentSize, currentStyle);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#increase()
     */
    public void increase() {
        ScaledFontManager.increase();
//        System.out.println(" ===>>> [DiagramFontManager.increase()] WOOOOOO WOOOOO!!!! "); //$NON-NLS-1$
        DiagramEditPart diagram = (DiagramEditPart)viewer.getContents();
        diagram.refreshFont(true);
//        viewer.setContents(diagram.getModel());
        diagram = (DiagramEditPart)viewer.getContents();
        diagram.layout();
        fireFontChanged();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#decrease()
     */
    public void decrease() {
        ScaledFontManager.decrease();
//        System.out.println(" ===>>> [DiagramFontManager.decrease()] WOOOOOO WOOOOO!!!! "); //$NON-NLS-1$
        DiagramEditPart diagram = (DiagramEditPart)viewer.getContents();
        diagram.refreshFont(true);
//        viewer.setContents(diagram.getModel());
        diagram = (DiagramEditPart)viewer.getContents();
        diagram.layout();
        fireFontChanged();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#canIncrease()
     */
    public boolean canIncrease() {
        return ScaledFontManager.canIncrease();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#canDecrease()
     */
    public boolean canDecrease() {
        return ScaledFontManager.canDecrease();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#getFont()
     */
    public Font getFont() {
        return ScaledFontManager.getFont();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#getName()
     */
    public String getName() {
        return ScaledFontManager.getName();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#getSize()
     */
    public int getSize() {
        return ScaledFontManager.getSize();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#getStyle()
     */
    public int getStyle() {
        return ScaledFontManager.getStyle();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#setFont(org.eclipse.swt.graphics.Font)
     */
    public void setFont(Font newFont) {
        ScaledFontManager.setFont(newFont);
    }
    
    public void setFont(String typeName, int size, int style) {
        ScaledFontManager.setFont(typeName, size, style);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#setSize(int)
     */
    public void setSize(int newSize) {
        ScaledFontManager.setSize(newSize);
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
        final Object[] handlers = this.listeners.getListeners();

		UiUtil.runInSwtThread(new Runnable() {
			@Override
			public void run() {
		        for (Object handler : handlers) {
		            ((IFontChangeListener)handler).fontChanged();
		        }
			}
		}, false);
    }
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.actions.ScaledFont#getFont(int)
	 */
	public Font getFont(int style) {
		return ScaledFontManager.getFont(style);
	}

}
