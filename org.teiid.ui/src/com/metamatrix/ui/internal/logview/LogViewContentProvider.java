/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.logview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/** 
 * @since 4.3
 */
public class LogViewContentProvider implements ITreeContentProvider {
    private LogView logView;

    public LogViewContentProvider(LogView logView) {
        this.logView = logView;
    }
    public void dispose() {
    }
    public Object[] getChildren(Object element) {
        return ((LogEntry) element).getChildren(element);
    }
    public Object[] getElements(Object element) {
        return logView.getLogs();
    }
    public Object getParent(Object element) {
        return ((LogEntry) element).getParent(element);
    }
    public boolean hasChildren(Object element) {
        return ((LogEntry) element).hasChildren();
    }
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    public boolean isDeleted(Object element) {
        return false;
    }
}
