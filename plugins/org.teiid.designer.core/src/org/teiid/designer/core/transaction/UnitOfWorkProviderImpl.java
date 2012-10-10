/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.teiid.core.designer.id.LongIDFactory;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;

/**
 * @author lphillips
 * @since 8.0
 */
public class UnitOfWorkProviderImpl implements UnitOfWorkProvider {
    private final static boolean DEBUG = false;
    private final static HashMap current = new HashMap();

    private final ResourceSet resources;
    private final Collection undoableListeners;

    private static LongIDFactory idFactory = new LongIDFactory();

    /**
     * Constructor for SimpleEmfUnitOfWorkProvider
     * 
     * @param resources - ResourceSet used to create the UnitOfWorkImpl
     */
    public UnitOfWorkProviderImpl( final ResourceSet resources ) {
        CoreArgCheck.isNotNull(resources,
                               ModelerCore.Util.getString("UnitOfWorkProviderImpl.ResourceSet_may_not_be_null_during_SimpleEmfUnitOfWorkProvider_construction_1")); //$NON-NLS-1$

        this.resources = resources;
        undoableListeners = new ArrayList();
    }

    /**
     * @see UnitOfWorkProvider#getCurrent()
     */
    @Override
	public UnitOfWork getCurrent() {
        UnitOfWork uow = null;
        synchronized (current) {
            final Iterator threads = current.entrySet().iterator();
            while (threads.hasNext()) {
                final Map.Entry entry = (Map.Entry)threads.next();
                if (entry.getKey() == Thread.currentThread()) {
                    uow = (UnitOfWork)entry.getValue();
                    // If this uow is complete... don't use it
                    if (uow.isComplete()) {
                        uow = null;
                    }
                } else if (!((Thread)entry.getKey()).isAlive()) {
                    // Maintenance of map of current threads to ensure we don't
                    // create a memory leak.
                    threads.remove();
                }
            }

            if (uow == null) {
                uow = new UnitOfWorkImpl(resources);
                current.put(Thread.currentThread(), uow);
            }
        }

        return uow;
    }

    /**
     * Remove the txn for the given thread
     * 
     * @param thread
     */
    @Override
	public void cleanup( final Thread thread ) {
        synchronized (current) {
            if (DEBUG) {
                System.out.println(ModelerCore.Util.getString("UnitOfWorkProviderImpl.Removing__2") + current.get(thread)); //$NON-NLS-1$
            }

            current.remove(thread);
        }
    }

    @Override
	public void addUndoableEditListener( final UndoableListener listener ) {
        if (!undoableListeners.contains(listener)) {
            undoableListeners.add(listener);
        }
    }

    @Override
	public void removeUndoableEditListener( final UndoableListener listener ) {
        if (undoableListeners.contains(listener)) {
            undoableListeners.remove(listener);
        }
    }

    public synchronized void processUndoable( final Undoable undoable ) {
        final Iterator listeners = undoableListeners.iterator();
        while (listeners.hasNext()) {
            final UndoableListener next = (UndoableListener)listeners.next();
            next.process(undoable);
        }
    }

    /**
     * @return
     */
    public static LongIDFactory getIdFactory() {
        return idFactory;
    }

    /**
     * @param factory
     */
    public static void setIdFactory( final LongIDFactory factory ) {
        idFactory = factory;
    }

}
