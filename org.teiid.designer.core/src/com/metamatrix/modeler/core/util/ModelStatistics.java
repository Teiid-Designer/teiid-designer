/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * ModelStatistics
 */
public class ModelStatistics {

    private int numResources;
    private final Map statsByMetaclass;
    private String desc;

    /**
     * Construct an instance of ModelStatisticsVisitor.
     */
    public ModelStatistics() {
        super();
        this.statsByMetaclass = new HashMap();
    }
    
    public String getDescription() {
        return desc;
    }
    
    public void setDescription( final String desc ) {
        this.desc = desc;
    }
    
    /**
     * Return the {@link EClass} objects for which instances were found by this visitor. 
     * @return the set of metaclasses; never null
     */
    public Set getEClasses() {
        return this.statsByMetaclass.keySet();
    }
    
    /**
     * Return the number of instances of the supplied {@link EClass metaclass} that were
     * found by this visitor.
     * @param metaclass the {@link EClass}
     * @return the number of instances of the metaclass found by this visitor.
     */
    public int getCount( final EClass metaclass ) {
        final Object key = metaclass; // new MetaclassKey(metaclass);
        final MetaclassStats stats = (MetaclassStats)statsByMetaclass.get(key);
        return stats != null ? stats.numInstances : 0;
    }
    
    /**
     * Return the number of resources that were found by this visitor.
     * @return the number of resources found by this visitor.
     */
    public int getResourceCount() {
        return this.numResources;
    }
    
    /**
     * Set the total count for the supplied metaclass.
     * @param metaclass the {@link EClass}
     * @param count the number of instances of the metaclass
     */
    public void set( final EClass metaclass, final int count ) {
        ArgCheck.isNotNull(metaclass);
        if ( count == 0 ) {
            statsByMetaclass.remove(metaclass);
            return;     // do nothing
        }
        MetaclassStats stats = (MetaclassStats)statsByMetaclass.get(metaclass);
        if ( stats == null ) {
            stats = new MetaclassStats(metaclass);
            statsByMetaclass.put(metaclass,stats);
        }
        stats.numInstances = count;
    }
    
    /**
     * Add to the total count for the supplied metaclass.  This method does nothing if the 
     * <code>increment</code> is 0.
     * @param metaclass the {@link EClass}
     * @param increment the number of additional instances of the metaclass
     */
    public void add( final EClass metaclass, final int increment ) {
        ArgCheck.isNotNull(metaclass);
        if ( increment == 0 ) {
            return;     // do nothing
        }
        MetaclassStats stats = (MetaclassStats)statsByMetaclass.get(metaclass);
        if ( stats == null ) {
            stats = new MetaclassStats(metaclass);
            statsByMetaclass.put(metaclass,stats);
        }
        stats.numInstances += increment;
    }
    
    /**
     * Set the total count for resources.
     * @param count the total number of resources
     */
    public void setResourceCount( final int count ) {
        this.numResources = count;
    }
    
    /**
     * Add to the total count for the supplied metaclass.
     * @param increment the number of additional resources
     */
    public void addResourceCount( final int increment ) {
        this.numResources += increment;
    }
    
    /**
     * Clear any statistics gathered by this visitor.
     */
    public void clear() {
        this.statsByMetaclass.clear();
        this.numResources = 0;
    }

    protected class MetaclassStats {
        final EClassifier metaclass;
        int numInstances;
        MetaclassStats( final EClassifier classifier ) {
            this.metaclass = classifier;
        }
    }
    
    public IStatus compare( final ModelStatistics that ) {
        ArgCheck.isNotNull(that);
        final List problems = new ArrayList();
        
        // Do the comparison only if the argument is not exactly the same object as this
        if ( this != that ) {
            String thisDesc = this.getDescription();
            String thatDesc = that.getDescription();
            if ( thisDesc == null || thisDesc.trim().length() == 0 ) {
                thisDesc = ModelerCore.Util.getString("ModelStatistics.DefaultDescriptionForThis"); //$NON-NLS-1$
            }
            if ( thatDesc == null || thatDesc.trim().length() == 0 ) {
                thatDesc = ModelerCore.Util.getString("ModelStatistics.DefaultDescriptionForThat"); //$NON-NLS-1$
            }
        
            // Compare the EClasses and the counts ...
            final Set thatEClasses = new HashSet(that.getEClasses());
            final Set thisEClasses = new HashSet(this.getEClasses());
            final Iterator iter = thisEClasses.iterator();
            while (iter.hasNext()) {
                final EClass metaclass = (EClass)iter.next();
                final int thisCount = this.getCount(metaclass);
                final int thatCount = that.getCount(metaclass);
                if ( thisCount != thatCount ) {
                    final Object[] params = new Object[]{metaclass.getName(),thisDesc,new Integer(thisCount),thatDesc,new Integer(thatCount)};
                    final String msg = ModelerCore.Util.getString("ModelStatistics.The_count_of_{0}_differed__{1}_{2},_{3}_{4}_3",params); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.ERROR,ModelerCore.PLUGIN_ID,0,msg,null);
                    problems.add(status);
                }
            
                // Remove the metaclass from each set ...
                iter.remove();
                thatEClasses.remove(metaclass);
            }
        
            // See if there is anything that remains ...
            final Iterator remainingThatIter = thatEClasses.iterator();
            while (remainingThatIter.hasNext()) {
                final EClass metaclass = (EClass)remainingThatIter.next();
                final int thisCount = this.getCount(metaclass);
                final int thatCount = that.getCount(metaclass);
                final Object[] params = new Object[]{metaclass.getName(),thisDesc,new Integer(thisCount),thatDesc,new Integer(thatCount)};
                final String msg = ModelerCore.Util.getString("ModelStatistics.The_count_of_{0}_differed__{1}_{2},_{3}_{4}_4",params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR,ModelerCore.PLUGIN_ID,0,msg,null);
                problems.add(status);
            }
        }
        
        // Convert the problems to a status ...
        if ( problems.isEmpty() ) {
            final String msg = ModelerCore.Util.getString("ModelStatistics.Stats_were_same"); //$NON-NLS-1$
            return new Status(IStatus.OK,ModelerCore.PLUGIN_ID,0,msg,null);
        }
        if ( problems.size() == 1 ) {
            return (IStatus)problems.get(0);
        }
        final IStatus[] children = (IStatus[])problems.toArray(new IStatus[problems.size()]);
        final String msg = ModelerCore.Util.getString("ModelStatistics.Multiple_differences_stats"); //$NON-NLS-1$
        final MultiStatus result = new MultiStatus(ModelerCore.PLUGIN_ID,0,children,msg,null);
        return result;
    }
}
