/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel;

import java.io.Serializable;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.HashCodeUtil;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * Multiplicity is the specification of the range of allowable cardinality values
 * that a set may assume.  Essentially, multiplicity is a (possibly infinite)
 * subset of the nonnegative integers.  The Multiplicity class represents
 * an interface for handling and manipulating multiplicity specifications.
 * <p>
 * In practice, it is usually a finite set of integer intervals, most often a
 * single interval with a minimum and maximum value.  Any set must be
 * finite, but the upper bound can be finite or unbounded
 * (the latter is called "many"); the upper bound must be greater than zero
 * (multiplicity of zero is not useful).  Note that although the set may be unbounded,
 * any particular cardinality is always finite.
 * <p>
 * The multiplicity is usually defined simply as a text expression consisting
 * of a comman-separated list of integer intervals, each in the form
 * "<i>minimum</i>..<i>maximum</i>", where <i>minimum</i> and <i>maximum</i>
 * are integers, or <i>maximum</i> can be a "*" which indicates an unbounded
 * interval.  An interval may also have the form <i>number</i>, where <i>number</i>
 * is an integer representing an interval of a single integer.  The multiplicity
 * defined as a single star ("*") is equivalent to the expression "0..*", and
 * indicates that the cardinality is unrestricted (i.e., "zero or more", or "many").
 * Examples of well-formed multiplicity text expressions include:
 * <ul>0..1</ul>
 * <ul>1</ul>
 * <ul>0..*</ul>
 * <ul>*</ul>
 * <ul>1..*</ul>
 * <ul>1..5</ul>
 * <ul>1..5,10,13..18,20..*</ul>
 * Generally, a well-formed multiplicity text expression will have intervals
 * that monotonically increase (e.g., <i>1..5,10,13</i>
 * rather than <i>10,1..5,13</i>), and will have two continguous intervals combined
 * into a single interval (e.g., <i>1..8</i> rather than <i>1..5,6..8</i>, and
 * <i>0..1</i> rather than <i>0,1</i>).
 * <p>
 * The Multiplicity class is an abstract class that is intended to hide all
 * implementation details from the user.  In this manner, the user only sees
 * and uses the Multiplicity interface definition and its static <code>getInstance</code>
 * and <code>getUnboundedInstance</code> methods, but the best possible
 * implementation class with the most efficient representation is used for each instance.
 * Various implementation classes are provided to efficiently handle the
 * unbounded case and the single interval case, both of which are by far
 * the most commonly used.  However, any well-formed multiplicity text expression
 * will be handled as well.
 * <p>
 * The Multiplicity class (and subclasses) are also immutable, and therefore
 * may be referenced by multiple users without the chance of the instance
 * being modified.  This also simplifies the interface (since no modification
 * methods need be provided) and makes possible the use of different underlying
 * classes to handle different situations without exposing the specific
 * implementation classes to the user; modification methods would make this
 * very difficult to successfully implement.
 */
public abstract class Multiplicity implements Serializable, Comparable {

    /**
     */
    private static final long serialVersionUID = 1L;
    /**
     * The String definition of completely unbounded  of either the maximum or both minimum and maximum if the multiplicity
     * is considered unlimited.
     */
    public static final String UNBOUNDED_DEFINITION = "*"; //$NON-NLS-1$
    protected static final char UNBOUNDED_CHAR = '*';

    /**
     * The delimiter string ".." used between the minimum and maximum values.
     */
    public static final String RANGE_DELIMITER = ".."; //$NON-NLS-1$
    protected static final char RANGE_DELIMITER_CHAR = '.';
    protected static final int RANGE_DELIMITER_LENGTH = RANGE_DELIMITER.length();

    /**
     * The delimiter string "," used between two intervals.
     */
    public static final String INTERVAL_DELIMITER = ","; //$NON-NLS-1$
    protected static final char INTERVAL_DELIMITER_CHAR = ',';
    protected static final int INTERVAL_DELIMITER_LENGTH = INTERVAL_DELIMITER.length();

    /**
     * The value of either the maximum or both minimum and maximum if the multiplicity
     * is considered unlimited.
     */
    public static final int UNBOUNDED_VALUE = Integer.MAX_VALUE;

    /**
     * The minimum single multiplicity value allowed.  A multiplicity of 0 is undefined.
     */
    protected static final int MINIMUM_SINGLE_VALUE = 1;

    /**
     * The default single multiplicity value.
     */
    protected static final int DEFAULT_SINGLE_VALUE = 1;

    /**
     * The default value for <code>isOrdered</code>.
     */
    public static final boolean DEFAULT_ORDERING = false;

    /**
     * The default value for <code>isUnique</code>.
     */
    public static final boolean DEFAULT_UNIQUENESS = false;

    /**
     * @label UNBOUNDED
     * @supplierCardinality 1
     */
    public static final Multiplicity UNBOUNDED      = new UnlimitedMultiplicity();
    public static final Multiplicity ZERO_OR_ONE    = Multiplicity.getInstance(0,1);
    public static final Multiplicity ONLY_ONE       = Multiplicity.getInstance(1);
    public static final Multiplicity ONE_OR_MORE    = Multiplicity.getInstance(1,Multiplicity.UNBOUNDED_VALUE);

    private static final Map InstancePool = Collections.synchronizedMap( new HashMap() );
    
    public static boolean DEBUG_MULTIPLICITY_CREATION = false;
    public static boolean DEBUG_MULTIPLICITY_LOCATION = false;

    /**
     * @label DEFAULT
     * @supplierCardinality 1
     */
    protected static final Multiplicity DEFAULT = new IntervalMultiplicity(DEFAULT_SINGLE_VALUE);

    public static Multiplicity get( String multiplicityValue, boolean isOrdered, boolean isUnique ) throws MultiplicityExpressionException {
        if(multiplicityValue == null){
            Assertion.isNotNull(multiplicityValue,ModelerCore.Util.getString("Multiplicity.The_multiplicity_string_may_not_be_null")); //$NON-NLS-1$
        }
        MultiplicityHolder key = new MultiplicityHolder(multiplicityValue,isOrdered,isUnique);
        Multiplicity result = (Multiplicity) InstancePool.get(key);
        if ( result == null ) {
            result = Multiplicity.getInstance(multiplicityValue,isOrdered,isUnique);
            InstancePool.put(key,result);
            if ( DEBUG_MULTIPLICITY_CREATION ) {
                ModelerCore.Util.log(IStatus.INFO,ModelerCore.Util.getString("Multiplicity.DEBUG.Adding_Multiplicity") + result + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
            if ( DEBUG_MULTIPLICITY_LOCATION ) {
                ModelerCore.Util.log(IStatus.INFO,ModelerCore.Util.getString("Multiplicity.DEBUG.Found_Multiplicity") + result + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        return result;
    }
    public static Multiplicity get( String multiplicityValue )  throws MultiplicityExpressionException {
        return get(multiplicityValue,Multiplicity.DEFAULT_ORDERING,Multiplicity.DEFAULT_UNIQUENESS);
    }
    
    /**
     * Utility method that clears the pool.
     */
    public static void clearPool() {
        InstancePool.clear();
    }

    private boolean isOrdered = false;
    private boolean isUnique = false;

    /**
     * Parse the specified text expression and return the list of Multiplicity
     * objects that capture the expression.
     * @param expression the well-formed text expression for the Multiplicity
     * @param isOrdered the ordering constraint for the multiplicity
     * @param isUnique the uniqueness constraint for the multiplicity
     * @return the list of intervals defined by the expression
     * @throws MultiplicityExpressionException if the specified expression cannot
     * be parsed without errors or is not well-formed
     */
    static List parseExpression( String defn, boolean isOrdered, boolean isUnique ) throws MultiplicityExpressionException {
        List results = new ArrayList();
        if (defn == null || defn.trim().length() == 0) {
            results.add(DEFAULT);
            return results;
        }

        // If it is "*" then return ...
        if (defn == UNBOUNDED_DEFINITION || defn.equals(UNBOUNDED_DEFINITION)) {
            results.add(UNBOUNDED);
            return results;
        }

        // Parse the expression by iterating through it and finding all intervals ...
        int minimum = -1;
        int maximum = -1;
        int startIndex = 0;
        int currentIndex = 0;
        CharacterIterator iter = new StringCharacterIterator(defn);
        for( char c = iter.first(); c != CharacterIterator.DONE; c = iter.next() ) {
            currentIndex = iter.getIndex();

            // If a range delimiter is found ...
            if ( c == RANGE_DELIMITER_CHAR ) {

                // If minimum was not yet found, parse it ...
                if ( minimum == -1 ) {
                    try {
                        minimum = Integer.parseInt(defn.substring(startIndex, currentIndex));
                    } catch ( NumberFormatException e ) {
                        final Object[] params = new Object[]{RANGE_DELIMITER,new Integer(currentIndex),defn};
                        throw new MultiplicityExpressionException(ModelerCore.Util.getString("Multiplicity.Malformed_text_before_the_range_delimiter_at_index_is_not_an_integer",params)); //$NON-NLS-1$
                    }
                }
                startIndex = currentIndex + RANGE_DELIMITER_LENGTH;
                iter.setIndex(startIndex);
            }

            // If an interval delimiter is found ...
            if ( c == INTERVAL_DELIMITER_CHAR ) {

                if ( defn.charAt(startIndex) == UNBOUNDED_CHAR ) {
                    throw new MultiplicityExpressionException(ModelerCore.Util.getString("Multiplicity.Malformed_unbounded_interval_maximum_must_appear_at_the_end_of_the_expression",new Integer(startIndex),defn)); //$NON-NLS-1$
                }
                try {
                    maximum = Integer.parseInt(defn.substring(startIndex, currentIndex));
                    // If maximum was not yet found, set it to the minimum ...
                    if ( minimum == -1 ) {
                        minimum = maximum;
                    }
                } catch ( NumberFormatException e ) {
                    throw new MultiplicityExpressionException(ModelerCore.Util.getString("Multiplicity.Malformed_text_at_indexes_is_not_an_integer",new Integer(startIndex),new Integer(currentIndex),defn)); //$NON-NLS-1$
                }
                try {
                    results.add( new IntervalMultiplicity(minimum,maximum,isOrdered,isUnique) );
                } catch ( IllegalArgumentException e ) {
                    throw new MultiplicityExpressionException(ModelerCore.Util.getString("Multiplicity.Malformed_minimum_must_be_equal_to_or_less_than_the_maximum_interval",new Integer(minimum),new Integer(maximum),defn)); //$NON-NLS-1$
                }
                minimum = -1;
                maximum = -1;

                startIndex = currentIndex + INTERVAL_DELIMITER_LENGTH;
                iter.setIndex(startIndex);
            }
        }
        if ( defn.length() <= startIndex ) {
            throw new MultiplicityExpressionException(ModelerCore.Util.getString("Multiplicity.Malformed_expression_may_not_end_with_a_delimiter",new Integer((defn.length()-1)),defn)); //$NON-NLS-1$
        }

        // Check if the last value is a "*"
        if ( defn.charAt(startIndex) == UNBOUNDED_CHAR ) {
            maximum = UNBOUNDED_VALUE;
        }

        // Parse the last value ...
        else {
            try {
                maximum = Integer.parseInt(defn.substring(startIndex));
            } catch ( NumberFormatException e ) {
              throw new MultiplicityExpressionException(ModelerCore.Util.getString("Multiplicity.Malformed_text_at_indexes_is_not_an_integer",new Integer(startIndex),new Integer((defn.length()-1)),defn)); //$NON-NLS-1$
            }
        }
        // If maximum was not yet found, set it to the minimum ...
        if ( minimum == -1 ) {
            minimum = maximum;
        }
        try {
            results.add( new IntervalMultiplicity(minimum,maximum,isOrdered,isUnique) );
        } catch ( IllegalArgumentException e ) {
            throw new MultiplicityExpressionException(ModelerCore.Util.getString("Multiplicity.Malformed_minimum_must_be_equal_to_or_less_than_the_maximum",new Integer(minimum),new Integer(maximum),defn)); //$NON-NLS-1$
        }

        // Sort the intervals ...
        Collections.sort(results);

        // Merge adjacent intervals ...
        if ( results.size() > 1 ) {
            int index = 0;
            Multiplicity first = (Multiplicity) results.get(index);
            Multiplicity next = (Multiplicity) results.get(++index);
            while ( next != null ) {
                if ( first.getMaximum() >= next.getMinimum() || first.getMaximum() == (next.getMinimum()-1) ) {
                    int min = Math.min( first.getMinimum(), next.getMinimum() );
                    int max = Math.max( first.getMaximum(), next.getMaximum() );
                    first = new IntervalMultiplicity(min,max,isOrdered,isUnique);
                    results.set(index-1, first);
                    results.remove(index);
                } else {
                    ++index;
                    first = next;
                }
                if ( index < results.size() ) {
                    next = (Multiplicity) results.get(index);
                } else {
                    next = null;
                }
            }
        }

        // Return the intervals ...
        return results;
    }

    protected Multiplicity( boolean isOrdered, boolean isUnique ) {
        this.isOrdered = isOrdered;
        this.isUnique = isUnique;
    }

    protected Multiplicity() {
        this(DEFAULT_ORDERING,DEFAULT_UNIQUENESS);
    }

    /**
     * Obtain an instance by parsing the specified multiplicity text expression.
     * The text expression must be well-formed.
     * @param expression the string definition of the multiplicity specification;
     * if null, then the default Multiplicity instance of "1" is returned.
     * @param isOrdered the ordering constraint for the multiplicity
     * @param isUnique the uniqueness constraint for the multiplicity
     * @return the MultiplicityInstance that best captures the specified definition.
     * @throws MultiplicityExpressionException if the expression is not well-formed and
     * cannot be parsed.
     */
    public static Multiplicity getInstance(String defn, boolean isOrdered, boolean isUnique ) throws MultiplicityExpressionException {
        if ( (UNBOUNDED_DEFINITION.equals(defn) || "0..*".equals(defn)) &&  //$NON-NLS-1$
             UNBOUNDED.isOrdered() == isOrdered && UNBOUNDED.isUnique() == isUnique) { 
            return UNBOUNDED;
        }
        if ( "0..1".equals(defn) && ZERO_OR_ONE.isOrdered() == isOrdered && ZERO_OR_ONE.isUnique() == isUnique ) { //$NON-NLS-1$
            return ZERO_OR_ONE;
        }
        if ( "1".equals(defn) && ONLY_ONE.isOrdered() == isOrdered && ONLY_ONE.isUnique() == isUnique ) { //$NON-NLS-1$
            return ONLY_ONE;
        }
        if ( "1..*".equals(defn) && ONE_OR_MORE.isOrdered() == isOrdered && ONE_OR_MORE.isUnique() == isUnique ) { //$NON-NLS-1$
            return ONE_OR_MORE;
        }
        List intervals = parseExpression(defn,isOrdered,isUnique);
        if ( intervals.size() == 1 ) {
            return (Multiplicity) intervals.get(0);
        }
        return new RangeMultiplicity(intervals,isOrdered,isUnique);
    }

    /**
     * Obtain an instance by parsing the specified multiplicity text expression,
     * that is unordered and not unique.  The text expression must be well-formed.
     * @param expression the string definition of the multiplicity specification;
     * if null, then the default Multiplicity instance of "1" is returned.
     * @return the MultiplicityInstance that best captures the specified definition.
     * @throws MultiplicityExpressionException if the expression is not well-formed and
     * cannot be parsed.
     */
    public static Multiplicity getInstance(String defn) throws MultiplicityExpressionException {
        List intervals = parseExpression(defn,DEFAULT_ORDERING,DEFAULT_UNIQUENESS);
        if ( intervals.size() == 1 ) {
            return (Multiplicity) intervals.get(0);
        }
        return new RangeMultiplicity(intervals,DEFAULT_ORDERING,DEFAULT_UNIQUENESS);
    }

    /**
     * Obtain an instance that represents an unbounded multiplicity, which is
     * equivalent to the text expression "*", that is unordered and not unique.
     * @return the MultiplicityInstance representing "*".
     */
    public static Multiplicity getUnboundedInstance() {
        return UNBOUNDED;
    }

    /**
     * Obtain an instance that represents an unbounded multiplicity, which is
     * equivalent to the text expression "*".
     * @param isOrdered the ordering constraint for the multiplicity
     * @param isUnique the uniqueness constraint for the multiplicity
     * @return the MultiplicityInstance representing "*".
     */
    public static Multiplicity getUnboundedInstance( boolean isOrdered, boolean isUnique) {
        return new UnlimitedMultiplicity(isOrdered,isUnique);
    }

    /**
     * Obtain an instance that represents a singular multiplicity of "1"
     * that is unordered and not unique.
     * This is considered the default multiplicity,
     * since many situations will require a cardinality of 1.
     * @return the MultiplicityInstance representing "1".
     */
    public static Multiplicity getInstance() {
        return DEFAULT;
    }

    /**
     * Obtain an instance for a single-interval multiplicity of the specified
     * value.  The corresponding text expression is "<i>number</i>".
     * @param number the size of the single interval;  must be a positive integer value.
     * @param isOrdered the ordering constraint for the multiplicity
     * @param isUnique the uniqueness constraint for the multiplicity
     * @return the MultiplicityInstance that best captures the specified definition.
     * @throws IllegalArgumentException if the value is negative or zero.
     */
    public static Multiplicity getInstance(int number, boolean isOrdered, boolean isUnique) {
        return new IntervalMultiplicity(number, number, isOrdered, isUnique);
    }

    /**
     * Obtain an instance for a single-interval multiplicity over the specified
     * range.  The corresponding text expression is "<i>minimum</i>..<i>maximum</i>".
     * @param minimum the minimum value for the interval; must be positive, and
     * may be zero only if <code>maximum</code> is non-zero.
     * @param maximum the maximum value for the interval; must be equal to or
     * greater than <code>minimum</code>, and may be Multiplicity.UNBOUNDED_VALUE for
     * an unbounded maximum value.
     * @param isOrdered the ordering constraint for the multiplicity
     * @param isUnique the uniqueness constraint for the multiplicity
     * @return the MultiplicityInstance that best captures the specified definition.
     * @throws IllegalArgumentException if the two values are not compatible.
     */
    public static Multiplicity getInstance(int minimum, int maximum, boolean isOrdered, boolean isUnique) {
        return new IntervalMultiplicity(minimum, maximum, isOrdered, isUnique);
    }

    /**
     * Obtain an instance for a single-interval multiplicity of the specified
     * value that is unordered and not unique.  The corresponding text expression is "<i>number</i>".
     * @param number the size of the single interval;  must be a positive integer value.
     * @return the MultiplicityInstance that best captures the specified definition.
     * @throws IllegalArgumentException if the value is negative or zero.
     */
    public static Multiplicity getInstance(int number) {
        return new IntervalMultiplicity(number);
    }

    /**
     * Obtain an instance for a single-interval multiplicity over the specified
     * range that is unordered and not unique.  The corresponding text expression is "<i>minimum</i>..<i>maximum</i>".
     * @param minimum the minimum value for the interval; must be positive, and
     * may be zero only if <code>maximum</code> is non-zero.
     * @param maximum the maximum value for the interval; must be equal to or
     * greater than <code>minimum</code>, and may be Multiplicity.UNBOUNDED_VALUE for
     * an unbounded maximum value.
     * @return the MultiplicityInstance that best captures the specified definition.
     * @throws IllegalArgumentException if the two values are not compatible.
     */
    public static Multiplicity getInstance(int minimum, int maximum) {
        return new IntervalMultiplicity(minimum, maximum,DEFAULT_ORDERING,DEFAULT_UNIQUENESS);
    }

    /**
     * Get the maximum number of values required for this property.
     * The result from this method will be equal to or greater than
     * zero.
     * @return the maximum number of property values required.
     */
    public abstract int getMaximum();

    /**
     * Return whether the multiplicity is defined as requiring an order.
     * @return true if the instances order is important.
     */
    public boolean isOrdered() {
        return this.isOrdered;
    }

    /**
     * Return whether the multiplicity is defined as requiring uniqueness.
     * @return true if the instances uniqueness is important.
     */
    public boolean isUnique() {
        return this.isUnique;
    }

    /**
     * Get the minimum number of values required for this property.
     * The result from this method will be equal to or greater than
     * zero.
     * @return the minimum number of property values required.
     */
    public abstract int getMinimum();

    /**
     * Determine whether the specified cardinality is included in this multiplicity
     * expression.
     * @return true if the cardinality is included in the range of allowable values
     * for this multiplicity.
     */
    public abstract boolean isIncluded( int cardinality );

    /**
     * Obtain whether the multiplicity has a maximum value that is unlimited.
     * @return true if the maximum value of this multiplicity is unlimited.
     */
    public abstract boolean isUnlimited();

    /**
     * Returns a string representing the current state of the object.
     * @return the string representation of this instance.
     */
    @Override
    public abstract String toString();

    /**
     * Compares this object to another. If the specified object is
     * an instance of the MetaMatrixSessionID class, then this
     * method compares the contents; otherwise, it throws a
     * ClassCastException (as instances are comparable only to
     * instances of the same
     *  class).
     * <p>
     * Note:  this method <i>is</i> consistent with
     * <code>equals()</code>, meaning
     *  that
     * <code>(compare(x, y)==0) == (x.equals(y))</code>.
     * <p>
     * @param obj the object that this instance is to be compared to.
     * @return a negative integer, zero, or a positive integer as this object
     *      is less than, equal to, or greater than the specified object, respectively.
     * @throws IllegalArgumentException if the specified object reference is null
     * @throws ClassCastException if the specified object's type prevents it
     *      from being compared to this instance.
     */
    public abstract int compareTo(Object obj);

    protected int compareFlags(Multiplicity that) {
        // Assumed to not be null ...
        if ( this.isOrdered() ) {
            if ( !that.isOrdered() ) {
                return -1;
            }
        } else {
            if ( that.isOrdered() ) {
                return 1;
            }
        }

        if ( this.isUnique() ) {
            if ( !that.isUnique() ) {
                return -1;
            }
        } else {
            if ( that.isUnique() ) {
                return 1;
            }
        }
        return 0;   // otherwise these values are the same ...
    }

    static int compare(Multiplicity obj1, Multiplicity obj2) {

        if (obj1 instanceof UnlimitedMultiplicity) {
            return obj1.compareTo(obj2);
        }
        if (obj2 instanceof UnlimitedMultiplicity) {
            return - obj2.compareTo(obj1);
        }

        if (obj1 instanceof IntervalMultiplicity) {
            int diffInMinimum = obj1.getMinimum() - obj2.getMinimum();
            int diffInMaximum = obj1.getMaximum() - obj2.getMaximum();
            if ( obj2 instanceof IntervalMultiplicity ) {
                if (diffInMinimum != 0) {
                    return diffInMinimum;
                }
                return obj1.compareFlags(obj2);
            }
            if (diffInMinimum != 0) {
                return diffInMinimum;
            }
            if (diffInMaximum != 0) {
                return diffInMaximum;
            }
            return 1;   // Interval is a complete interval, so is greater
        }

        if ( obj1 instanceof RangeMultiplicity ) {
            if ( obj2 instanceof IntervalMultiplicity ) {
                return - compare(obj2,obj1);
            }
            int diffInMinimum = obj1.getMinimum() - obj2.getMinimum();
            int diffInMaximum = obj1.getMaximum() - obj2.getMaximum();
            if (diffInMinimum != 0) {
                return diffInMinimum;
            }
            if (diffInMaximum != 0) {
                return diffInMaximum;
            }
            RangeMultiplicity r1 = (RangeMultiplicity) obj1;
            RangeMultiplicity r2 = (RangeMultiplicity) obj2;
            Iterator r1Iter = r1.getIntervals().iterator();
            Iterator r2Iter = r2.getIntervals().iterator();
            while ( r1Iter.hasNext() ) {
                if ( ! r2Iter.hasNext() ) {
                    return 1;
                }
                IntervalMultiplicity r1Interval = (IntervalMultiplicity) r1Iter.next();
                IntervalMultiplicity r2Interval = (IntervalMultiplicity) r2Iter.next();
                int diff = r1Interval.compareTo(r2Interval);
                if ( diff != 0 ) {
                    return diff;
                }
            }
            return obj1.compareFlags(obj2);
        }

        throw new IllegalArgumentException(ModelerCore.Util.getString("Multiplicity.The_multiplicity_object_is_not_a_known_subclass",obj1)); //$NON-NLS-1$
    }

/*
    public static void main(String[] args) {
        try {
            Multiplicity.getInstance("5..3");
            System.out.println("Attempting to create \"5..3\" DID NOT failed as it should have");
        } catch ( Exception e ) {
            System.out.println("Attempting to create \"5..3\" successfully failed");
        }

        try {
            Multiplicity star    = Multiplicity.getUnboundedInstance();
            Multiplicity m1_3    = Multiplicity.getInstance(1, 3);
            Multiplicity m0_3    = Multiplicity.getInstance(0, 3);
            Multiplicity m1_1    = Multiplicity.getInstance(1);
            Multiplicity m1_7    = Multiplicity.getInstance(1, 7);
            Multiplicity m1_star = Multiplicity.getInstance(1, Multiplicity.UNBOUNDED_VALUE);
            Multiplicity m1_3c7  = Multiplicity.getInstance("1..3,7");
            Multiplicity m1c3c7  = Multiplicity.getInstance("1,3,7");
            Multiplicity m1_7c3  = Multiplicity.getInstance("1..7,3");
            Multiplicity m1c2    = Multiplicity.getInstance("1,2");
            Multiplicity m1c1    = Multiplicity.getInstance("1,1");
            Multiplicity m1c3    = Multiplicity.getInstance("1,3");
            Multiplicity m1c3_6c7_9c10_star = Multiplicity.getInstance("1,3..6,7..9,10..*");
            Multiplicity m1c7_9c3_6c10_star = Multiplicity.getInstance("1,7..9,3..6,10..*");

            System.out.println("star     = \"" + star + "\" (" + getType(star) + ")");
            System.out.println("m1_3     = \"" + m1_3 + "\" (" + getType(m1_3) + ")");
            System.out.println("m0_3     = \"" + m0_3 + "\" (" + getType(m0_3) + ")");
            System.out.println("m1_1     = \"" + m1_1 + "\" (" + getType(m1_1) + ")");
            System.out.println("m1_7     = \"" + m1_7 + "\" (" + getType(m1_7) + ")");
            System.out.println("m1_star  = \"" + m1_star + "\" (" + getType(m1_star) + ")");
            System.out.println("m1_3c7   = \"" + m1_3c7 + "\" (" + getType(m1_3c7) + ")");
            System.out.println("m1c3c7   = \"" + m1c3c7 + "\" (" + getType(m1c3c7) + ")");
            System.out.println("m1_7c3   = \"" + m1_7c3 + "\" (" + getType(m1_7c3) + ")");
            System.out.println("m1c2     = \"" + m1c2 + "\" (" + getType(m1c2) + ")");
            System.out.println("m1c1     = \"" + m1c1 + "\" (" + getType(m1c1) + ")");
            System.out.println("m1c3     = \"" + m1c3 + "\" (" + getType(m1c3) + ")");
            System.out.println("m1c3_6c7_9c10_star     = \"" + m1c3_6c7_9c10_star + "\" (" + getType(m1c3_6c7_9c10_star) + ")");
            System.out.println("m1c7_9c3_6c10_star     = \"" + m1c7_9c3_6c10_star + "\" (" + getType(m1c7_9c3_6c10_star) + ")");

            System.out.println();

            Multiplicity star_STR    = Multiplicity.getInstance(star.toString());
            Multiplicity m1_3_STR    = Multiplicity.getInstance(m1_3.toString());
            Multiplicity m0_3_STR    = Multiplicity.getInstance(m0_3.toString());
            Multiplicity m1_1_STR    = Multiplicity.getInstance(m1_1.toString());
            Multiplicity m1_7_STR    = Multiplicity.getInstance(m1_7.toString());
            Multiplicity m1_3c7_STR  = Multiplicity.getInstance(m1_3c7.toString());
            Multiplicity m1c3c7_STR  = Multiplicity.getInstance(m1c3c7.toString());
            Multiplicity m1_star_STR = Multiplicity.getInstance(m1_star.toString());
            Multiplicity m1c2_STR    = Multiplicity.getInstance(m1c2.toString());
            Multiplicity m1c1_STR    = Multiplicity.getInstance(m1c1.toString());
            Multiplicity m1c3_STR    = Multiplicity.getInstance(m1c3.toString());

            System.out.println("star_STR     = \"" + star_STR + "\" (" + getType(star_STR) + ")");
            System.out.println("m1_3_STR     = \"" + m1_3_STR + "\" (" + getType(m1_3_STR) + ")");
            System.out.println("m0_3_STR     = \"" + m0_3_STR + "\" (" + getType(m0_3_STR) + ")");
            System.out.println("m1_1_STR     = \"" + m1_1_STR + "\" (" + getType(m1_1_STR) + ")");
            System.out.println("m1_7_STR     = \"" + m1_7_STR + "\" (" + getType(m1_7_STR) + ")");
            System.out.println("m1_3c7_STR   = \"" + m1_3c7_STR + "\" (" + getType(m1_3c7_STR) + ")");
            System.out.println("m1c3c7_STR   = \"" + m1c3c7_STR + "\" (" + getType(m1c3c7_STR) + ")");
            System.out.println("m1_star_STR  = \"" + m1_star_STR + "\" (" + getType(m1_star_STR) + ")");
            System.out.println("m1c2_STR     = \"" + m1c2_STR + "\" (" + getType(m1c2_STR) + ")");
            System.out.println("m1c1_STR     = \"" + m1c1_STR + "\" (" + getType(m1c1_STR) + ")");
            System.out.println("m1c3_STR     = \"" + m1c3_STR + "\" (" + getType(m1c3_STR) + ")");

            System.out.println("\nTest equals ...");
            testEquality(star, star_STR, true);
            testEquality(m1_3, m1_3_STR, true);
            testEquality(m0_3, m0_3_STR, true);
            testEquality(m1_1, m1_1_STR, true);
            testEquality(m1_star, m1_star_STR, true);
            testEquality(m1_3c7, m1_3c7, true);
            testEquality(m0_3, m1_3_STR, false);
            testEquality(m0_3, m1_star, false);
            testEquality(m0_3, m1_1_STR, false);

            System.out.println("\nTest compareTo between same types ...");
            testCompare(star, star_STR, 0);
            testCompare(m1_3, m1_3_STR, 0);
            testCompare(m0_3, m0_3_STR, 0);
            testCompare(m1_1, m1_1_STR, 0);
            testCompare(m1_star, m1_star_STR, 0);
            testCompare(m1_3c7, m1_3c7, 0);

            System.out.println("\nTest compareTo between Unlimited and Unlimited ...");
            testCompare(star, star_STR, 0);

            System.out.println("\nTest compareTo between Unlimited and Interval ...");
            testCompare(star, m1_3, 1);
            testCompare(m1_3, star, -1);
            testCompare(m1_3, m1_3, 0);

            System.out.println("\nTest compareTo between Interval and Interval ...");
            testCompare(m1_3, m1_star, -1);
            testCompare(m1_star, m1_3, 1);
            testCompare(m1_3, m0_3, 1);
            testCompare(m0_3, m1_3, -1);
            testCompare(m0_3, m0_3, 0);

            System.out.println("\nTest compareTo between Explicit and Explicit ...");
            testCompare(m1_3c7, m1c3c7, 1); //??
            testCompare(m1c3c7, m1_3c7, -1); //??

            System.out.println("\nTest compareTo between Unlimited and Explicit ...");
            testCompare(star, m1_3c7, 1);
            testCompare(m1_3c7, star, -1);

            System.out.println("\nTest compareTo between Interval and Explicit ...");
            testCompare(m1_7, m1_3c7, 1);
            testCompare(m1_3c7, m1_7, -1);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }
    private static void testEquality(Multiplicity m1, Multiplicity m2, boolean equal) {
        if (equal != m1.equals(m2)) {
            System.out.println("\"" + m1 + " should" + (equal ? " " : " not ") + "be equal to \"" + m2 + "\"");
        } else {
            System.out.println("\"" + m1 + " is correctly" + (equal ? " " : " not ") + "equal to \"" + m2 + "\"");
        }
    }
    private static void testCompare(Multiplicity m1, Multiplicity m2, int lessOrGreater) {
        int result = m1.compareTo(m2);
        int result2 = m2.compareTo(m1);
        if (result < 0 && lessOrGreater >= 0 ||
        result > 0 && lessOrGreater <= 0 ||
        result == 0 && lessOrGreater != 0) {
            System.out.println("\"" + m1 + " compares to \"" + m2 + "\" as " + result + " (should be " + lessOrGreater + ")");
        } else {
            System.out.println("\"" + m1 + " compares to \"" + m2 + "\" as " + result + " (correct)");
        }

    }
    private static String getType(Multiplicity m) {
        if (m instanceof UnlimitedMultiplicity) {
            return "Unlimited";
        }
        if (m instanceof RangeMultiplicity) {
            return "Range";
        }
        if (m instanceof IntervalMultiplicity) {
            return "Interval";
        }
        return "Unknown";
    }
*/
}


final class UnlimitedMultiplicity extends Multiplicity {
    /**
     */
    private static final long serialVersionUID = 1L;

    UnlimitedMultiplicity() {
        super();
    }

    UnlimitedMultiplicity( boolean isOrdered, boolean isUnique ) {
        super(isOrdered,isUnique);
    }

    @Override
    public int getMaximum() {
        return Multiplicity.UNBOUNDED_VALUE;
    }

    @Override
    public int getMinimum() {
        return 0;
    }

    @Override
    public boolean isUnlimited() {
        return true;
    }

    @Override
    public boolean isIncluded( int cardinality ) {
        return cardinality >= 0;
    }

    @Override
    public String toString() {
        return Multiplicity.UNBOUNDED_DEFINITION;
    }

    @Override
    public int compareTo(Object obj) {
        Assertion.isNotNull(obj);
        Multiplicity that = (Multiplicity)obj; // May throw ClassCastException
        if (that instanceof UnlimitedMultiplicity) {
            return super.compareFlags(that);
        }
        return 1;   // this is always greater than that
    }

    @Override
    public boolean equals(Object obj) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        if (obj instanceof UnlimitedMultiplicity) {
            // equal so far ...
            return super.compareFlags((Multiplicity)obj) == 0;
        }

        // Otherwise not comparable ...
        return false;
    }

}


final class IntervalMultiplicity extends Multiplicity {
    /**
     */
    private static final long serialVersionUID = 1L;
    private int minimum = 0;
    private int maximum = Multiplicity.UNBOUNDED_VALUE;

    IntervalMultiplicity(int number) {
        super();
        if (number < 0) {
            throw new IllegalArgumentException(ModelerCore.Util.getString("Multiplicity.The_multiplicity_must_be_a_positive_number_")); //$NON-NLS-1$
        }
        if (number < Multiplicity.MINIMUM_SINGLE_VALUE) {
            throw new IllegalArgumentException(ModelerCore.Util.getString("Multiplicity.The_multiplicity_may_not_be_0")); //$NON-NLS-1$
        }
        this.maximum = number;
        this.minimum = number;
    }

    IntervalMultiplicity(int minimum, int maximum, boolean isOrdered, boolean isUnique ) {
        super(isOrdered,isUnique);
        if (maximum < 0) {
            throw new IllegalArgumentException(ModelerCore.Util.getString("Multiplicity.The_maximum_multiplicity_must_be_between_0_and_UNBOUNDED_VALUE",maximum)); //$NON-NLS-1$
        }
        if (minimum > maximum) {
            throw new IllegalArgumentException(ModelerCore.Util.getString("Multiplicity.The_maximum_multiplicity_must_be_equal_to_or_greater_than_the_minimum_multiplicity",new Integer(maximum),new Integer(minimum))); //$NON-NLS-1$
        }
        this.maximum = maximum;
        this.minimum = minimum;
    }

    /**
     * Constructs an instance that represents the default multiplicity of "1..1"
     */
    IntervalMultiplicity() {
        this(MINIMUM_SINGLE_VALUE);
    }

    @Override
    public int getMaximum() {
        return maximum;
    }

    @Override
    public int getMinimum() {
        return minimum;
    }

    @Override
    public boolean isUnlimited() {
        return this.maximum == UNBOUNDED_VALUE;
    }

    @Override
    public boolean isIncluded( int cardinality ) {
        return ( cardinality >= minimum && cardinality <= maximum );
    }

    @Override
    public String toString() {
        String result = null;
        if (this.minimum != this.maximum) {
            if (this.maximum != UNBOUNDED_VALUE) {
                result = Integer.toString(this.minimum) + RANGE_DELIMITER + Integer.toString(this.maximum);
            } else {
                result = Integer.toString(this.minimum) + RANGE_DELIMITER + UNBOUNDED_CHAR;
            }
        } else {
            result = Integer.toString(this.minimum);
        }
        return result;
    }

    @Override
    public int compareTo(Object obj) {
        Assertion.isNotNull(obj);
        Multiplicity that = (Multiplicity)obj; // May throw ClassCastException
        return Multiplicity.compare(this,that);
    }

    @Override
    public boolean equals(Object obj) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        //if ( this.getClass().isInstance(obj) ) {
        if (obj instanceof IntervalMultiplicity) {
            IntervalMultiplicity that = (IntervalMultiplicity)obj;
            if (that.minimum == this.minimum && that.maximum == this.maximum) {
                // equal so far ...
                return super.compareFlags(that) == 0 ;
            }
            return false;
        }

        // Otherwise not comparable ...
        return false;
    }
}


final class RangeMultiplicity extends Multiplicity {
    /**
     */
    private static final long serialVersionUID = 1L;
    private List intervals = new ArrayList();
    private Multiplicity first;
    private Multiplicity last;

    RangeMultiplicity(List intervals, boolean isOrdered, boolean isUnique ) {
        super(isOrdered,isUnique);
        this.intervals = intervals;
        if ( this.intervals == null || this.intervals.size() == 0 ) {
            throw new IllegalArgumentException(ModelerCore.Util.getString("Multiplicity.Unable_to_create_a_RangeMultiplicity_with_zero-length_or_null_list_of_intervals")); //$NON-NLS-1$
        }
        this.first = (Multiplicity) this.intervals.get(0);
        this.last = (Multiplicity) this.intervals.get(this.intervals.size()-1);
    }

    public List getIntervals() {
        return intervals;
    }

    @Override
    public int getMaximum() {
        return this.last.getMaximum();
    }

    @Override
    public int getMinimum() {
        return this.first.getMinimum();
    }

    @Override
    public boolean isUnlimited() {
        return this.last.isUnlimited();
    }

    @Override
    public boolean isIncluded( int cardinality ) {
        boolean result = false;
        Iterator iter = intervals.iterator();
        while ( iter.hasNext() ) {
            Multiplicity m = (Multiplicity) iter.next();
            if ( m.isIncluded(cardinality) ) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator iter = intervals.iterator();
        if ( iter.hasNext() ) {
            sb.append(iter.next().toString());
        }
        while ( iter.hasNext() ) {
            sb.append(INTERVAL_DELIMITER_CHAR);
            sb.append(iter.next().toString());
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Object obj) {
        Assertion.isNotNull(obj);
        Multiplicity that = (Multiplicity)obj; // May throw ClassCastException
        return Multiplicity.compare(this,that);
    }

    @Override
    public boolean equals(Object obj) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }

        // Check if object can be compared to this one
        if (obj instanceof Multiplicity) {
            Multiplicity that = (Multiplicity)obj;
            return Multiplicity.compare(this,that) == 0;
        }

        // Otherwise not comparable ...
        return false;
    }

}


class MultiplicityHolder {
    String multiplicity = null;
    boolean isOrdered = true;
    boolean isUnique = true;
    public MultiplicityHolder(String m, boolean isOrdered, boolean isUnique ) {
        this.multiplicity = m;
        this.isOrdered = isOrdered;
        this.isUnique = isUnique;
    }
    @Override
    public int hashCode() {
        int seed = 0;
        seed = HashCodeUtil.hashCode(seed,multiplicity);
        seed = HashCodeUtil.hashCode(seed,isOrdered);
        seed = HashCodeUtil.hashCode(seed,isUnique);
        return seed;
    }
    @Override
    public boolean equals(Object obj) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        if (this.getClass().isInstance(obj)) {
            MultiplicityHolder that = (MultiplicityHolder)obj;
            if ( that.isOrdered != this.isOrdered ) {
                return false;
            }
            if ( that.isUnique != this.isUnique ) {
                return false;
            }
            if ( ! this.multiplicity.equals(that.multiplicity) ) {
                return false;
            }
            return true;
        }

        // Otherwise not comparable ...
        return false;
    }
}









