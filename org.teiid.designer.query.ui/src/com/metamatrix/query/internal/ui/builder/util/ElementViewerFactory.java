/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.util;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * ElementViewerFactory is a static class for building TreeViewer instances from a specified
 * ITreeContentProvider and ILabelProvider.
 */
abstract public class ElementViewerFactory {

    private static ITreeContentProvider contentProvider;
    private static ILabelProvider labelProvider;
    private static ICriteriaStrategy criteriaStrategy;
    private static Object viewerInput;

    /**
     * Create a TreeViewer for displaying the desired Elements in the tree.
     * @param parent
     * @return
     */
    public static TreeViewer createElementViewer(Composite parent) {
        TreeViewer viewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        if ( contentProvider != null ) {
            viewer.setContentProvider(contentProvider);
        }
        if ( labelProvider != null ) {
            viewer.setLabelProvider(labelProvider);
        }
        viewer.setInput(viewerInput);
        return viewer;
    }

    public static void setViewerInput(Object input) {
        viewerInput = input;
    }

    /**
     * Set this static class's ITreeContentProvider for use in building TreeViewers.
     * @param provider
     */
    public static void setContentProvider(ITreeContentProvider provider) {
        contentProvider = provider;
    }

    /**
     * Set this static class's ILabelProvider for displaying TreeViewers.
     * @param provider
     */
    public static void setLabelProvider(ILabelProvider provider) {
        labelProvider = provider;
    }
    
    /**
     * Set this static class's ICriteriaStrategy.
     * @param strategy
     */
    public static void setCriteriaStrategy(ICriteriaStrategy strategy) {
        criteriaStrategy = strategy;
    }

    /**
     * Get this static class's ITreeContentProvider.
     * @return
     */
    public static ITreeContentProvider getContentProvider() {
        return contentProvider;
    }

    /**
     * Get this static class's ILabelProvider.
     * @return
     */
    public static ILabelProvider getLabelProvider() {
        return labelProvider;
    }
    
    public static ICriteriaStrategy getCriteriaStrategy() {
        return criteriaStrategy;
    }
    
    /**
     * Get this static class's ICriteriaStrategy.
     * @param theElementViewer
     * @return
     */
    public static ICriteriaStrategy getCriteriaStrategy(TreeViewer theElementViewer) {
        if ( criteriaStrategy == null ) {
            criteriaStrategy = new CriteriaStrategy(theElementViewer);
        }
        return criteriaStrategy;
    }

}
