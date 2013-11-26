/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.util;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.type.NullType;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 * The <code>RuntimeTypeConverter</code> class reconciles the runtime types of 2 lists.
 * 
 * @author Dan Florian
 * @since 8.0
 * @version 1.0
 */
public class RuntimeTypeConverter {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Indicates if the candidates are compatible. */
    private boolean[] compatible;

    /** Indicates if the corresponding elements of each list have the same runtime type. */
    private boolean explicitMatch;

    /** One set of candidate objects. The other set is called <code>secondaryCandidates</code>. */
    private ArrayList primaryCandidates;

    /** The name to use to describe the primary list if no <code>primaryObj</code> is null. */
    private String primaryName = "Primary"; //$NON-NLS-1$

    /** The object to use to describe the primary list of candidates. */
    private Object primaryObj;

    /**
     * The list of results corresponding to the primary candidates. The object at an index will either be the same as the original
     * list (<code>primaryCandidates</code>) or be a converted value.
     */
    private List primaryResults;

    /** The size of the primary candidate list. */
    private int primarySize;

    /** One set of candidate objects. The other set is called <code>primaryCandidates</code>. */
    private ArrayList secondaryCandidates;

    /** The name to use to describe the secondary list if <code>secondaryObj</code> is null. */
    private String secondaryName = "Secondary"; //$NON-NLS-1$

    /** The object to use to describe the secondary list of candidates. */
    private Object secondaryObj;

    /**
     * The list of results corresponding to the secondary candidates. The object at an index will either be the same as the
     * original list (<code>secondaryCandidates</code>) or be a converted value.
     */
    private List secondaryResults;

    /** The size of the secondary candidate list. */
    private int secondarySize;

    /** The size of the largest input list (<code>Math.max(primarySize, secondarySize)</code>). */
    private int size;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a <code>RuntimeTypeConverter</code> with both list being unlocked.
     * 
     * @param theEditor the editor to use with <code>MetaObject</code>s
     * @param theTypeResolver the type resolver to use
     * @param thePrimaryCandidates the first list of candidates for conversion
     * @param theSecondaryCandidates the second list of candidates for conversion
     */
    private RuntimeTypeConverter( List thePrimaryCandidates,
                                 List theSecondaryCandidates ) {
        this(thePrimaryCandidates, false, theSecondaryCandidates, false);
    }

    /**
     * Constructs a <code>RuntimeTypeConverter</code>.
     * 
     * @param theEditor the editor to use with <code>MetaObject</code>s
     * @param theTypeResolver the type resolver to use
     * @param thePrimaryCandidates the first list of candidates for conversion
     * @param thePrimaryCandidatesLockedFlag indicates if the primary list is locked
     * @param theSecondaryCandidates the second list of candidates for conversion
     * @param theSecondaryCandidatesLockedFlag indicates if the secondary list is locked
     */
    private RuntimeTypeConverter( List thePrimaryCandidates,
                                 boolean thePrimaryCandidatesLockedFlag,
                                 List theSecondaryCandidates,
                                 boolean theSecondaryCandidatesLockedFlag ) {
        if ((thePrimaryCandidates == null) || (theSecondaryCandidates == null)) {
            throw new IllegalArgumentException("RuntimeTypeConverter.init:Primary or secondary candidate list is null."); //$NON-NLS-1$
        }

        primarySize = thePrimaryCandidates.size();
        secondarySize = theSecondaryCandidates.size();
        size = Math.max(primarySize, secondarySize);

        primaryCandidates = new ArrayList(primarySize);
        primaryCandidates.addAll(thePrimaryCandidates);
        primaryResults = (List)primaryCandidates.clone();

        secondaryCandidates = new ArrayList(secondarySize);
        secondaryCandidates.addAll(theSecondaryCandidates);
        secondaryResults = (List)secondaryCandidates.clone();

        compatible = new boolean[size];
        explicitMatch = false;

        setCompatibility();
        setExplicitMatch();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets either the converted object if it exists or the original list item.
     * 
     * @param theIndex the index of the object being requested
     * @param thePrimaryFlag a flag indicating if the object is from the primary or secondary list
     * @return the requested result object
     */
    private Object getCandidateResultAt( int theIndex,
                                         boolean thePrimaryFlag ) {
        validateIndex(theIndex, (thePrimaryFlag) ? primarySize : secondarySize, "getCandidateResultAt(int, boolean"); //$NON-NLS-1$

        List candidates = (thePrimaryFlag) ? primaryResults : secondaryResults;
        return candidates.get(theIndex);
    }

    private String getName( boolean thePrimaryFlag ) {
        String result = (thePrimaryFlag) ? primaryName : secondaryName;
        Object obj = (thePrimaryFlag) ? primaryObj : secondaryObj;

        if (obj != null && obj instanceof EObject && TransformationHelper.isSqlColumn(obj)) {
            EObject eObj = (EObject)obj;
            SqlColumnAspect columnAspect = ((SqlColumnAspect)AspectManager.getSqlAspect(eObj));
            result = columnAspect.getFullName(eObj);
        }

        return result;
    }

    /**
     * Gets a name derived from either the list object being set or the list name.
     * 
     * @return the name associated with the primary candidate list
     * @see #setPrimaryObject(Object)
     * @see #setPrimaryName(String)
     */
    private String getPrimaryName() {
        return getName(true);
    }

    /**
     * Gets an object from the result list associated with the original primary list. The object returned can be the same object
     * as the original primary list, or it can be another object which is the result of a conversion taken place.
     * 
     * @param theIndex the index of the object to be retrieved
     * @return the requested object
     * @see #commit()
     */
    private Object getPrimaryResultAt( int theIndex ) {
        return getCandidateResultAt(theIndex, true);
    }

    /**
     * Gets the runtime type associated with the given object. These types are defined by the DataTypeManager.
     * 
     * @param theObj the object whose runtime type is being requested
     * @return the requested runtime type
     */
    public static String getRuntimeType( Object theObj ) {
        String type = null;
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        
        if (theObj instanceof EObject) {
            EObject eObj = (EObject)theObj;
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObj, true);

            if (TransformationHelper.isSqlColumn(theObj)) {
                SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(eObj);
                type = columnAspect.getRuntimeType(eObj);
            } else if (dtMgr.isSimpleDatatype((EObject)theObj)) {
                return dtMgr.getRuntimeTypeName(eObj);
            }
        } else if (theObj instanceof IExpression) {
            Class objClass = ((IExpression)theObj).getType();
            if (objClass == null) {
                type = service.getDataTypeName(NullType.class);
            } else {
                type = service.getDataTypeName(objClass);
            }
        } else {
            throw new IllegalArgumentException(
                                               "RuntimeTypeConverter.getRuntimeType:Object type cannot be determined for <" + theObj + ">."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return type;
    }

    /**
     * Gets a name derived from either the list object being set or the list name.
     * 
     * @return the name associated with the secondary candidate list
     * @see #setSecondaryObject(Object)
     * @see #setSecondaryName(String)
     */
    private String getSecondaryName() {
        return getName(false);
    }

    /**
     * Gets an object from the result list associated with the original secondary list. The object returned can be the same object
     * as the original secondary list, or it can be another object which is the result of a conversion taken place.
     * 
     * @param theIndex the index of the object to be retrieved
     * @return the requested object
     * @see #commit()
     */
    private Object getSecondaryResultAt( int theIndex ) {
        return getCandidateResultAt(theIndex, false);
    }

    private boolean isCompatible( String theFirstType,
                                        String theSecondType ) {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return (theFirstType.equals(theSecondType) || service.isTransformable(theFirstType, theSecondType));
    }

    /**
     * Static Method to determine if the runtime types of two items exactly match.
     * 
     * @param primaryObj the primary Object
     * @param secondaryObj the secondary Object
     * @return <code>true</code> if items match; <code>false</code> otherwise.
     */
    public static boolean isExplicitMatch( Object primaryObj,
                                           Object secondaryObj ) {
        boolean isExplicitMatch = false;
        if (primaryObj != null && secondaryObj != null) {
            List primaryList = new ArrayList(1);
            List secondaryList = new ArrayList(1);
            primaryList.add(primaryObj);
            secondaryList.add(secondaryObj);
            RuntimeTypeConverter rtc = new RuntimeTypeConverter(primaryList, true, secondaryList, true);
            isExplicitMatch = rtc.isExplicitMatch();
        }
        return isExplicitMatch;
    }

    /**
     * Indicates of the runtime types of the associated item in each list exactly match.
     * 
     * @return <code>true</code> if list items match; <code>false</code> otherwise.
     */
    private boolean isExplicitMatch() {
        return explicitMatch;
    }

    /**
     * Indicates of the runtime types of the associated item at the given index in each list exactly match.
     * 
     * @return <code>true</code> if types match; <code>false</code> otherwise.
     */
    private boolean isExplicitMatch( int theIndex ) {
        validateIndex(theIndex, size, "isExplicitMatch(int)"); //$NON-NLS-1$

        boolean result = false;

        if ((theIndex < primarySize) && (theIndex < secondarySize)) {
            Object primaryCandidate = primaryCandidates.get(theIndex);
            String primaryType = getRuntimeType(primaryCandidate);

            Object secondaryCandidate = secondaryCandidates.get(theIndex);
            String secondaryType = getRuntimeType(secondaryCandidate);

            result = primaryType.equals(secondaryType);
        }

        return result;
    }

    private void setCompatibility() {
        for (int i = 0; i < size; i++) {
            setCompatibility(i);
        }
    }

    private void setCompatibility( int theIndex ) {
        validateIndex(theIndex, size, "setCompatibility(int)"); //$NON-NLS-1$

        boolean result = false;

        if ((theIndex < primarySize) && (theIndex < secondarySize)) {
            Object primary = getPrimaryResultAt(theIndex);
            String primaryType = getRuntimeType(primary);

            Object secondary = getSecondaryResultAt(theIndex);
            String secondaryType = getRuntimeType(secondary);

            result = (isCompatible(primaryType, secondaryType) || isCompatible(secondaryType, primaryType));
        }

        compatible[theIndex] = result;
    }

    private void setExplicitMatch() {
        // don't call setExplicitMatch(int) since it also loops up to "size" times
        boolean match = true;

        for (int i = 0; i < size; i++) {
            if (!isExplicitMatch(i)) {
                match = false;
                break;
            }
        }

        explicitMatch = match;
    }

    /**
     * Gets both list names with a delimeter between them.
     * 
     * @return a concatenation of the list names
     */
    @Override
    public String toString() {
        return getPrimaryName() + "<->" + getSecondaryName(); //$NON-NLS-1$
    }

    private void validateIndex( int theIndex,
                                int theSize,
                                String theMethod ) {
        if (theSize <= theIndex) {
            throw new IllegalArgumentException(new StringBuffer().append(getClass().getName()).append(".") //$NON-NLS-1$
            .append(theMethod).append(":Index <") //$NON-NLS-1$
            .append(theIndex).append("> is invalid for size of <") //$NON-NLS-1$
            .append(theSize).append(">.") //$NON-NLS-1$
            .toString());
        }
    }

}