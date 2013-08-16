/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.builder;

import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.spi.ITeiidServerManager;

/**
 *
 */
public abstract class AbstractTeiidProjectBuilder extends IncrementalProjectBuilder {

    private class BuildingThread extends Thread {

        private final int kind;
        private final Map<String, String> args;
        private final IProgressMonitor monitor;
        private IProject[] buildResult;
        private CoreException buildException;

        public BuildingThread(int kind, Map<String, String> args, IProgressMonitor monitor) {
            super(AbstractTeiidProjectBuilder.class.getSimpleName() + "." + BuildingThread.class.getSimpleName()); //$NON-NLS-1$
            this.kind = kind;
            this.args = args;
            this.monitor = monitor;
            setDaemon(true);
        }

        @Override
        public void run() {
            ITeiidServerManager serverManager = ModelerCore.getTeiidServerManager();

            while(! serverManager.isStarted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ModelerCore.Util.log(ex);
                }
            }

            try {
                this.buildResult = buildInternal(kind, args, monitor);
            } catch (CoreException ex) {
                this.buildException = ex;
            }
        }

        /**
         * @return result of the build
         */
        IProject[] getBuildResult() {
            return buildResult;
        }

        CoreException getBuildException() {
            return buildException;
        }
    }

    protected abstract IProject[] buildInternal(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException;

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {

        if (ModelerCore.getTeiidServerManager().isStarted())
            return buildInternal(kind, args, monitor);

        BuildingThread thread = new BuildingThread(kind, args, monitor);
        thread.start();
        try {
            // Wait for the building thread to terminate. Since the build occur on their
            // own threads then it should not block the UI but maybe need to monitor
            // this for a while.
            thread.join();
        } catch (InterruptedException ex) {
            ModelerCore.Util.log(ex);
        }

        if (thread.getBuildException() != null)
            throw thread.getBuildException();

        return thread.getBuildResult();
    }
}
