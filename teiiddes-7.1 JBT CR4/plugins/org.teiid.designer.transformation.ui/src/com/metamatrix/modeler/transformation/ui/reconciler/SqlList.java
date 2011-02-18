/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.teiid.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;

/**
 * Class that plays the role of the domain model in the TableViewerExample
 * In real life, this class would access a persistent store of some kind.
 * 
 */

public class SqlList {

    private final int COUNT = 10;
    private List currentSymbolsList = new ArrayList(COUNT);
    private Set<ISqlListViewer> changeListeners = new HashSet<ISqlListViewer>();

    /**
     * Constructor
     */
    public SqlList() {
    }
    
    /**
     * Return the collection of symbols
     */
    public List getAll() {
        return currentSymbolsList;
    }
    
    /**
     * Return the number of symbols
     */
    public int size() {
        return currentSymbolsList.size();
    }
    
    /**
     * Add a new task to the collection of tasks
     */
    public void add(SingleElementSymbol symbol) {
        currentSymbolsList.add(currentSymbolsList.size(), symbol);
        Iterator<ISqlListViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            iterator.next().addSymbol(symbol);
    }
    
    public boolean containsSymbol(SingleElementSymbol singleSymbol) {
        return TransformationSqlHelper.containsElementSymbol(getAll(), singleSymbol);
    }
    
    /**
     * Add a new task to the collection of tasks
     */
    public void addAll(List theseSymbols) {
        Collection addedSymbols = new ArrayList();
        
        Iterator iter = theseSymbols.iterator();
        while(iter.hasNext()) {
            SingleElementSymbol nextSymbol = (SingleElementSymbol)iter.next();
            if( !containsSymbol(nextSymbol)) {
                addedSymbols.add(nextSymbol);
            }
        }
        if( !addedSymbols.isEmpty() ) {
            currentSymbolsList.addAll(addedSymbols);
            Iterator<ISqlListViewer> iterator = changeListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().addSymbols(addedSymbols.toArray());
            }
        }
    }

    /**
     * Add a new task to the collection of tasks
     */
    public void insert(SingleElementSymbol symbol,int index) {
        currentSymbolsList.add(index, symbol);
        Iterator<ISqlListViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            iterator.next().insertSymbol(symbol,index);
    }
    
    /**
     * @param symbol
     */
    public void remove(SingleElementSymbol symbol) {
        currentSymbolsList.remove(symbol);
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext())
            ((ISqlListViewer) iterator.next()).removeSymbol(symbol);
    }

    /**
     * @param symbol
     */
    public void removeAll(List theseSymbols) {
        if( ! theseSymbols.isEmpty() ) {
            currentSymbolsList.removeAll(theseSymbols);
            Iterator<ISqlListViewer> iterator = changeListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().removeSymbols(theseSymbols.toArray());
            }
        }
    }

    /**
     * @param symbol
     */
    public void symbolChanged(SingleElementSymbol symbol) {
        Iterator<ISqlListViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            iterator.next().updateSymbol(symbol);
    }

    /**
     * @param symbol
     */
    public void refresh(boolean updateLabels) {
        Iterator<ISqlListViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            iterator.next().refresh(updateLabels);
    }

    /**
     * Get the index of a symbol in the list
     */
    public int indexOf(SingleElementSymbol symbol) {
        return currentSymbolsList.indexOf(symbol);
    }
    
    /**
     * Get the symbol at the supplied index.  If the index doesn't exist, return the 
     * first symbol in the list
     */
    public SingleElementSymbol getSymbolAt(int index) {
        SingleElementSymbol result = null;
        if( index>=0 && index<currentSymbolsList.size()) {
            result = (SingleElementSymbol)currentSymbolsList.get(index);
        }
        return result;
    }
    
    /**
     * Get the symbol at the supplied index.  If the index doesn't exist, return the 
     * first symbol in the list
     */
    public SingleElementSymbol getFirstSymbol() {
        SingleElementSymbol result = null;
        if( currentSymbolsList.size()>0 ) {
            result = (SingleElementSymbol)currentSymbolsList.get(0);
        }
        return result;
    }

    /**
     * @param viewer
     */
    public void removeChangeListener(ISqlListViewer viewer) {
        changeListeners.remove(viewer);
    }

    /**
     * @param viewer
     */
    public void addChangeListener(ISqlListViewer viewer) {
        changeListeners.add(viewer);
    }

}
