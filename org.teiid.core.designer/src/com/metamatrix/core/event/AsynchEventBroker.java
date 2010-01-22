/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.event;

import java.util.EventObject;
import java.util.concurrent.TimeUnit;
import com.metamatrix.common.queue.WorkerPool;
import com.metamatrix.common.queue.WorkerPoolFactory;
import com.metamatrix.core.CorePlugin;
import com.metamatrix.core.MetaMatrixRuntimeException;

public class AsynchEventBroker extends AbstractEventBroker {
    private WorkerPool workerPool = WorkerPoolFactory.newWorkerPool("AsyncEventBroker", 1); //$NON-NLS-1$
    private static final String DEFAULT_NAME = CorePlugin.Util.getString("AsynchEventBroker.DefaultName"); //$NON-NLS-1$

    private static final long SHUTDOWN_TIMEOUT_MILLIS = 10000; // 10 seconds

    public AsynchEventBroker() {
        this(null);
    }

    public AsynchEventBroker( String name ) {
        super();
        if (name == null) {
            name = DEFAULT_NAME;
        }
        super.setName(name);
    }

    /**
     * Return whether this broker has at least one event that has yet to be processed and sent to the appropriate listeners.
     * 
     * @return true if there are events that have yet to be processed, or false otherwise.
     */
    public boolean hasUnprocessedEvents() {
        return workerPool.hasWork();
    }

    /**
     * Add an object to the queue. This method assumes that the EventObject reference is never null and that the broker is not
     * suspended.
     */
    @Override
    protected final void process( final EventObject obj ) {
        this.workerPool.execute(new Runnable() {
            public void run() {
                notifyListeners(obj);
            }
        });
    }

    @Override
    protected void waitToCompleteShutdown() {
        workerPool.shutdown();
        try {
            workerPool.awaitTermination(SHUTDOWN_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new MetaMatrixRuntimeException(e);
        }
    }

}
