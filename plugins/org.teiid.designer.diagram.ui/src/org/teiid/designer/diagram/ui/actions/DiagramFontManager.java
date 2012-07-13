/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.actions;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.PluginConstants;
import org.teiid.designer.diagram.ui.editor.DiagramViewer;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;
import org.teiid.designer.ui.common.util.UiUtil;


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
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#increase()
     */
    @Override
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
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#decrease()
     */
    @Override
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
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#canIncrease()
     */
    @Override
	public boolean canIncrease() {
        return ScaledFontManager.canIncrease();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#canDecrease()
     */
    @Override
	public boolean canDecrease() {
        return ScaledFontManager.canDecrease();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#getFont()
     */
    @Override
	public Font getFont() {
        return ScaledFontManager.getFont();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#getName()
     */
    @Override
	public String getName() {
        return ScaledFontManager.getName();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#getSize()
     */
    @Override
	public int getSize() {
        return ScaledFontManager.getSize();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#getStyle()
     */
    @Override
	public int getStyle() {
        return ScaledFontManager.getStyle();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#setFont(org.eclipse.swt.graphics.Font)
     */
    @Override
	public void setFont(Font newFont) {
        ScaledFontManager.setFont(newFont);
    }
    
    @Override
	public void setFont(String typeName, int size, int style) {
        ScaledFontManager.setFont(typeName, size, style);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.actions.ScaledFont#setSize(int)
     */
    @Override
	public void setSize(int newSize) {
        ScaledFontManager.setSize(newSize);
        fireFontChanged();
    }

    /**
     * Adds the given IFontChangeListener to this ZoomManager's list of listeners.
     * @param listener the IFontChangeListener to be added
     */
    @Override
	public void addFontChangeListener(IFontChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Removes the given IFontChangeListener from this ZoomManager's list of listeners.
     * @param listener the IFontChangeListener to be removed
     */
    @Override
	public void removeFontChangeListener(IFontChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies listeners that the zoom level has changed.
     */
    @Override
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
	 * @See org.teiid.designer.diagram.ui.actions.ScaledFont#getFont(int)
	 */
	@Override
	public Font getFont(int style) {
		return ScaledFontManager.getFont(style);
	}

}
