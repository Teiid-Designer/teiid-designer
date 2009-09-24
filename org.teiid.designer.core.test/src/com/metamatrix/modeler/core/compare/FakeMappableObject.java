/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import com.metamatrix.core.util.ArgCheck;

/**
 * FakeMappableObject
 */
public class FakeMappableObject extends EObjectImpl {
    
    /**
     * Method to create a tree of FakeMappableObject instances.
     * @param objects
     * @param namePrefix
     * @param startingType
     * @param endingType
     * @param depth
     */
    public static void createFakeMappableTree( final List objects, final String namePrefix, 
                                               final int startingType, final int endingType,
                                               final int numPerTypeIncrement, final int depth ) {
        for (int type = startingType; type <= endingType; ++type) {
            // Create as many instances of this type as the numeric value of the type ...
            for (int i = 0; i < (type + numPerTypeIncrement); ++i) {
                final String name = namePrefix + (i+1);
                final FakeMappableObject obj = new FakeMappableObject(name,type);
                objects.add(obj);
                
                // Add children ...
                if ( depth > 1 ) {
                    createFakeMappableTree(obj.getChildren(),namePrefix,startingType,endingType,
                                           numPerTypeIncrement,depth-1);
                }
            }
        }
    }
    
    private final String name;
    private final int type;
    private final EList children;

    /**
     * Construct an instance of FakeMappableObject.
     * 
     */
    public FakeMappableObject( final String name, final int type ) {
        super();
        ArgCheck.isNotNull(name);
        this.name = name;
        this.type = type;
        this.children = new BasicEList();
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public int getType() {
        return type;
    }
    
    /**
     * @see org.eclipse.emf.ecore.impl.EObjectImpl#eContents()
     */
    @Override
    public EList eContents() {
        return children;
    }

    /**
     * @return
     */
    public List getChildren() {
        return children;
    }
    
    public void print( final PrintStream stream, final String prefix ) {
        stream.println(prefix + toString());
        final Iterator iter = this.getChildren().iterator();
        while (iter.hasNext()) {
            final FakeMappableObject child = (FakeMappableObject)iter.next();
            child.print(stream,"  " + prefix); //$NON-NLS-1$
        }
        
    }
    
    @Override
    public String toString() {
        return this.getName() + " (type=" + this.getType() + ")";  //$NON-NLS-1$//$NON-NLS-2$
    }

}
