/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.eclipse.ui.IMemento;

/**
 * Contributer class to the {@link TeiidServerContentProvider} and {@link TeiidServerActionProvider}
 * that determines the values of the preview data source and preview vdb options.
 *
 * The content provider uses its memento state to save and restore the option values while the
 * action provider is responsible for changing the values. The content provider is registered as
 * a property change listener to redisplay based on the new values.
 */
public class TeiidServerPreviewOptionContributor {

    /**
     * Property passed to listeners containing the values of the preview options
     */
    public static String PREVIEW_OPTIONS_PROPERTY = "PreviewOptionsProperty"; //$NON-NLS-1$

    /**
     * Wrapping class for the preview options
     */
    public static class PreviewOptions {

        /**
         * <code>true</code> if the viewer should show preview VDBs
         */
        private final boolean showPreviewVdbs;

        /**
         * <code>true</code> if the viewer should show preview data sources
         */
        private final boolean showPreviewDataSources;

        /**
         * @param showPreviewDataSources
         * @param showPreviewVdbs
         */
        public PreviewOptions(boolean showPreviewDataSources, boolean showPreviewVdbs) {
            this.showPreviewDataSources = showPreviewDataSources;
            this.showPreviewVdbs = showPreviewVdbs;
        }

        /**
         * @return the showPreviewDataSources
         */
        public boolean isShowPreviewDataSources() {
            return this.showPreviewDataSources;
        }

        /**
         * @return the showPreviewVdbs
         */
        public boolean isShowPreviewVdbs() {
            return this.showPreviewVdbs;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (this.showPreviewDataSources ? 1231 : 1237);
            result = prime * result + (this.showPreviewVdbs ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PreviewOptions other = (PreviewOptions)obj;
            if (this.showPreviewDataSources != other.showPreviewDataSources) return false;
            if (this.showPreviewVdbs != other.showPreviewVdbs) return false;
            return true;
        }
    }

    private static TeiidServerPreviewOptionContributor instance;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Memento info for saving and restoring menu state from session to session
     */
    private static final String MENU_MEMENTO = "menu-settings"; //$NON-NLS-1$
    private static final String SHOW_PREVIEW_VDBS = "show-preview-vdbs"; //$NON-NLS-1$
    private static final String SHOW_PREVIEW_DATA_SOURCES = "show-preview-data-sources"; //$NON-NLS-1$

    private PreviewOptions previewOptions = new PreviewOptions(false, false);

    /**
     * @return the default instance
     */
    public static TeiidServerPreviewOptionContributor getDefault() {
        if (instance == null)
            instance = new TeiidServerPreviewOptionContributor();

        return instance;
    }

    /**
     * @return the showPreviewDataSources
     */
    public boolean isShowPreviewDataSources() {
        return previewOptions.isShowPreviewDataSources();
    }

    /**
     * @return the showPreviewVdbs
     */
    public boolean isShowPreviewVdbs() {
        return previewOptions.isShowPreviewVdbs();
    }

    /**
     * @param showPreviewDataSources
     * @param showPreviewVdbs
     */
    public void setShowPreviewOptions(boolean showPreviewDataSources, boolean showPreviewVdbs) {
        PreviewOptions oldValue = previewOptions;
        previewOptions = new PreviewOptions(showPreviewDataSources, showPreviewVdbs);

        if (! oldValue.equals(previewOptions))
            propertyChangeSupport.firePropertyChange(PREVIEW_OPTIONS_PROPERTY, oldValue, previewOptions);
    }

    /**
     * Adds a property change listener
     *
     * @param propertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    /**
     * Removes a property change listener
     *
     * @param propertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }

    /**
     * Save the state to the given memento
     *
     * @param memento
     */
    public void saveState(IMemento memento) {
        IMemento menuMemento = memento.createChild(MENU_MEMENTO);
        menuMemento.putBoolean(SHOW_PREVIEW_DATA_SOURCES, previewOptions.isShowPreviewDataSources());
        menuMemento.putBoolean(SHOW_PREVIEW_VDBS, previewOptions.isShowPreviewVdbs());
    }

    /**
     * Restore the state according to the given memento
     *
     * @param memento
     */
    public void restoreState(IMemento memento) {
        if (memento == null)
            return;

        IMemento childMemento = memento.getChild(MENU_MEMENTO);
        if (childMemento == null)
            return;

        setShowPreviewOptions(childMemento.getBoolean(SHOW_PREVIEW_DATA_SOURCES), childMemento.getBoolean(SHOW_PREVIEW_VDBS));
    }
}
