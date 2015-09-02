/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.common.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.teiid.core.designer.util.CoreArgCheck;

/**
 *
 */
public class CompositeProgressMonitor implements IProgressMonitor {

    private final Set<IProgressMonitor> monitors = new HashSet<IProgressMonitor>();

    /**
     * @param monitors delegate monitors
     */
    public CompositeProgressMonitor(IProgressMonitor... monitors) {
        CoreArgCheck.isNotNull(monitors);
        this.monitors.addAll(Arrays.asList(monitors));
    }

    @Override
    public void beginTask(String name, int totalWork) {
        for (IProgressMonitor monitor : monitors) {
            monitor.beginTask(name, totalWork);
        }
    }

    @Override
    public void done() {
        for (IProgressMonitor monitor : monitors) {
            monitor.done();
        }
    }

    @Override
    public void internalWorked(double work) {
        for (IProgressMonitor monitor : monitors) {
            monitor.internalWorked(work);
        }
    }

    @Override
    public boolean isCanceled() {
        for (IProgressMonitor monitor : monitors) {
            if (monitor.isCanceled())
                return true;
        }

        return false;
    }

    @Override
    public void setCanceled(boolean value) {
        for (IProgressMonitor monitor : monitors) {
            monitor.setCanceled(value);
        }
    }

    @Override
    public void setTaskName(String name) {
        for (IProgressMonitor monitor : monitors) {
            monitor.setTaskName(name);
        }
    }

    @Override
    public void subTask(String name) {
        for (IProgressMonitor monitor : monitors) {
            monitor.subTask(name);
        }
    }

    @Override
    public void worked(int work) {
        for (IProgressMonitor monitor : monitors) {
            monitor.worked(work);
        }
    }
}
