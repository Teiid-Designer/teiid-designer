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
