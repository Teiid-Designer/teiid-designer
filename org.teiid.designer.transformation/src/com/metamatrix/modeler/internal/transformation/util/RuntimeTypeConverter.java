/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.common.types.NullType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.query.function.FunctionDescriptor;
import com.metamatrix.query.function.FunctionLibrary;
import com.metamatrix.query.function.FunctionLibraryManager;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.Function;

/**
 * The <code>RuntimeTypeConverter</code> class reconciles the runtime types of 2 lists. For type changes to {@link MetaObject}s,
 * the {@link #commit()} method must be called.
 * 
 * @author Dan Florian
 * @since 3.1
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

    /** Indicates if the primary candidate at a particular index has been converted. */
    private boolean[] primaryConverted;

    /** Indicates if the primary candidates are locked and can't be converted. */
    private boolean primaryLocked;

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

    /** Indicates if the secondary candidate at a particular index has been converted. */
    private boolean[] secondaryConverted;

    /** Indicates if the secondary candidates are locked and can't be converted. */
    private boolean secondaryLocked;

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

    /**
     * Indicates if objects in the candidate lists that are conversions functions should be unwrapped. Defaults to
     * <code>true</code>.
     */
    private boolean unwrap;

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
    public RuntimeTypeConverter( List thePrimaryCandidates,
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
    public RuntimeTypeConverter( List thePrimaryCandidates,
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
        primaryLocked = thePrimaryCandidatesLockedFlag;
        primaryConverted = new boolean[primarySize];
        primaryResults = (List)primaryCandidates.clone();

        secondaryCandidates = new ArrayList(secondarySize);
        secondaryCandidates.addAll(theSecondaryCandidates);
        secondaryLocked = theSecondaryCandidatesLockedFlag;
        secondaryConverted = new boolean[secondarySize];
        secondaryResults = (List)secondaryCandidates.clone();

        compatible = new boolean[size];
        explicitMatch = false;
        unwrap = true;

        setCompatibility();
        setExplicitMatch();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates if the given object's type can be changed. Currently only <code>true</code> for <code>MetaObject</code>s.
     * 
     * @param theObject the object being checked
     * @return <code>true</code> if type can be changed; <code>false</code> otherwise.
     */
    public boolean canChangeType( Object theObject ) {
        return (theObject != null && TransformationHelper.isSqlColumn(theObject));
    }

    /**
     * Indicates if the given object can be changed to the given type.
     * 
     * @param theObject the object being checked
     * @param theType the type being checked
     * @return <code>true</code> if conversion can take place; <code>false</code> otherwise.
     */
    private boolean canConvert( Object theCandidate,
                                String theType ) {
        return (convert(theCandidate, theType) != null);
    }

    /**
     * Indicates if the primary list object at the given index can be converted.
     * 
     * @param theIndex the index into the primary list
     * @return <code>true</code> if conversion can take place; <code>false</code> otherwise.
     */
    public boolean canConvertPrimaryCandidate( int theIndex ) {
        validateIndex(theIndex, size, "canConvertPrimaryCandidate(Object, String)"); //$NON-NLS-1$

        // in order to convert the index must be valid for both the primary and the secondary
        boolean result = (!primaryLocked && !isCompatible(theIndex) && (theIndex < primarySize) && (theIndex < secondarySize));

        if (result) {
            Object candidate = primaryCandidates.get(theIndex);
            String type = getRuntimeType(secondaryCandidates.get(theIndex));
            result = canConvert(candidate, type);
        }

        return result;
    }

    /**
     * Indicates if the secondary list object at the given index can be converted.
     * 
     * @param theIndex the index into the secondary list
     * @return <code>true</code> if conversion can take place; <code>false</code> otherwise.
     */
    public boolean canConvertSecondaryCandidate( int theIndex ) {
        validateIndex(theIndex, size, "canConvertSecondaryCandidate(Object, String)"); //$NON-NLS-1$

        // in order to convert the index must be valid for both the primary and the secondary
        boolean result = (!secondaryLocked && !isCompatible(theIndex) && (theIndex < primarySize) && (theIndex < secondarySize));

        if (result) {
            Object candidate = secondaryCandidates.get(theIndex);
            String type = getRuntimeType(primaryCandidates.get(theIndex));
            result = canConvert(candidate, type);
        }

        return result;
    }

    /**
     * Changes the type of an object.
     * 
     * @param theIndex the index into the candidate list
     * @param theType the type to change to
     * @param thePrimaryFlag indicates if a primary candidate or secondary candidate should be changed
     * @return <code>true</code> if successfully changed; <code>false</code> otherwise.
     */
    public boolean changeType( int theIndex,
                               Object theType,
                               boolean thePrimaryFlag ) {
        validateIndex(theIndex, (thePrimaryFlag) ? primarySize : secondarySize, "changeType(int, ObjectReference, boolean)"); //$NON-NLS-1$

        boolean result = false;

        if (!isCompatible(theIndex)) {
            List candidates = (thePrimaryFlag) ? primaryCandidates : secondaryCandidates;
            Object candidate = candidates.get(theIndex);

            if (canChangeType(candidate)) {
                Object obj = convert(candidate, getRuntimeType(theType));

                if (obj != null) {
                    List candidateResults = (thePrimaryFlag) ? primaryResults : secondaryResults;
                    candidateResults.set(theIndex, obj);

                    boolean[] converted = (thePrimaryFlag) ? primaryConverted : secondaryConverted;
                    converted[theIndex] = true;

                    compatible[theIndex] = true;
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Commits the changes in types for all {@link MetaObject}s by using the {@link MetaObjectEditor}. Also resets the converter
     * to an initial state meaning that no conversions have taken place.
     */
    public void commit() {
        // if (isConverted()) {
        // UserTransaction txn = editor.createWriteTransaction();
        // boolean wasErr = true;
        //    
        // try {
        // txn.begin();
        //    
        // Object original = null;
        //    
        // // loop through primary looking for MetaObjects whose type has been converted
        // // converted will be an ObjectReference if a conversion has taken place
        // for (int i = 0; i < primarySize; i++) {
        // if (primaryConverted[i]) {
        // original = primaryCandidates.get(i);
        // if (original instanceof MetaObject) {
        // setMetaObjectType((MetaObject)original, primaryResults.get(i));
        // }
        // }
        // }
        //                
        // // loop through secondary looking for MetaObjects whose type has been converted
        // // converted will be an ObjectReference if a conversion has taken place
        // for (int i = 0; i < secondarySize; i++) {
        // if (secondaryConverted[i]) {
        // original = secondaryCandidates.get(i);
        // if (original instanceof MetaObject) {
        // setMetaObjectType((MetaObject)original, secondaryResults.get(i));
        // }
        // }
        // }
        //    
        // wasErr = false;
        // }
        // catch (TransactionException theException) {
        // LogManager.logCritical(LogContexts.QUERY,
        // theException,
        // "RuntimeTypeConverter.commit():Changing MetaObject types.");
        // }
        // finally {
        // try {
        // if (wasErr) {
        // txn.rollback();
        // }
        // else {
        // txn.commit();
        // // now update the original lists with the converted objects
        // // and clear converted objects flag
        // // only have to overwrite the SingleElementSymbols items since the meta object was changed
        //                        
        // Object original = null;
        //    
        // for (int i = 0; i < primarySize; i++) {
        // if (primaryConverted[i]) {
        // primaryConverted[i] = false;
        // original = primaryCandidates.get(i);
        //
        // if (original instanceof SingleElementSymbol) {
        // setPrimaryCandidate(i, primaryResults.get(i));
        // }
        // }
        // }
        //    
        // for (int i = 0; i < secondarySize; i++) {
        // if (secondaryConverted[i]) {
        // secondaryConverted[i] = false;
        // original = secondaryCandidates.get(i);
        //
        // if (original instanceof SingleElementSymbol) {
        // setSecondaryCandidate(i, secondaryResults.get(i));
        // }
        // }
        // }
        //                        
        // primaryResults = (List)primaryCandidates.clone();
        // secondaryResults = (List)secondaryCandidates.clone();
        // explicitMatch = true;
        // }
        // }
        // catch (TransactionException theException) {
        // LogManager.logCritical(LogContexts.QUERY,
        // theException,
        // "RuntimeTypeConverter.commit():Failed to " +
        // (wasErr ? "rollback." : "commit."));
        // }
        // catch (ToolkitRuntimeException theException) {
        // LogManager.logCritical(LogContexts.QUERY,
        // theException,
        // "RuntimeTypeConverter.commit():Runtime Exception");
        // }
        // }
        // }
    }

    /**
     * Converts all incompatible rows. First tries converting the secondary item.
     * 
     * @return <code>true</code> if everything in need of conversion was successfully converted; <code>false</code> otherwise.
     */
    public boolean convert() {
        boolean result = true;

        for (int i = 0; i < size; i++) {
            if (!isCompatible(i)) {
                if (!convertSecondaryCandidate(i)) {
                    if (!convertPrimaryCandidate(i)) {
                        result = false;
                    }
                }
            }
        }

        return result;
    }

    private Object convert( Object theCandidate,
                            String theRuntimeType ) {
        Object result = null;
        String primaryType = getRuntimeType(theCandidate);

        // must check BOTH the explicit and implicit conversions to get all available
        if (DataTypeManager.isExplicitConversion(primaryType, theRuntimeType)
            || DataTypeManager.isImplicitConversion(primaryType, theRuntimeType)) {

            // make prime compatible with secondary
            if (theCandidate instanceof EObject) {
                // get new type to be changed during commit
                // result = getTypePropertyValue(theRuntimeType);
            } else if (theCandidate instanceof Expression) {
                // wrap a conversion function around value
                result = createConversionFunction((Expression)theCandidate, primaryType, theRuntimeType);
            }
        }

        return result;
    }

    /**
     * Converts the primary candidate at the given index to be runtime type compatible with the secondary candidate at the same
     * index.
     * 
     * @param theIndex the index of the object to be converted
     * @return <code>true</code> if the candidate was successfully converted; <code>false</code> otherwise.
     */
    public boolean convertPrimaryCandidate( int theIndex ) {
        validateIndex(theIndex, primarySize, "convertPrimaryCandidate(int)"); //$NON-NLS-1$

        if (!compatible[theIndex] && !primaryLocked && (theIndex < secondarySize)) {
            primaryConverted[theIndex] = false;

            Object primaryCandidate = getBaseObject(primaryCandidates.get(theIndex));

            Object secondaryCandidate = getBaseObject(secondaryCandidates.get(theIndex));
            String secondaryType = getRuntimeType(secondaryCandidate);

            Object obj = convert(primaryCandidate, secondaryType);
            if (obj != null) {
                primaryResults.set(theIndex, obj);
                primaryConverted[theIndex] = true;
                compatible[theIndex] = true;
            }
        }

        return primaryConverted[theIndex];
    }

    /**
     * Converts the secondary candidate at the given index to be runtime type compatible with the primary candidate at the same
     * index.
     * 
     * @param theIndex the index of the object to be converted
     * @return <code>true</code> if the candidate was successfully converted; <code>false</code> otherwise.
     */
    public boolean convertSecondaryCandidate( int theIndex ) {
        validateIndex(theIndex, secondarySize, "convertSecondaryCandidate(int)"); //$NON-NLS-1$

        if (!compatible[theIndex] && !secondaryLocked && (theIndex < primarySize)) {
            secondaryConverted[theIndex] = false;

            Object primaryCandidate = getBaseObject(primaryCandidates.get(theIndex));
            String primaryType = getRuntimeType(primaryCandidate);

            Object secondaryCandidate = getBaseObject(secondaryCandidates.get(theIndex));

            Object obj = convert(secondaryCandidate, primaryType);
            if (obj != null) {
                secondaryResults.set(theIndex, obj);
                secondaryConverted[theIndex] = true;
                compatible[theIndex] = true;
            }
        }

        return secondaryConverted[theIndex];
    }

    /**
     * Creates a convert <code>Function</code> wrapping the given <code>Expression</code>.
     * 
     * @param theExpression the expression being converted
     * @param theOriginalTypeName the type of the expression
     * @param theNewTypeName the type to convert to
     * @return the new function
     */
    public Function createConversionFunction( Expression theExpression,
                                              String theOriginalTypeName,
                                              String theNewTypeName ) {
        Class originalType = DataTypeManager.getDataTypeClass(theOriginalTypeName);

        FunctionLibrary library = FunctionLibraryManager.getFunctionLibrary();
        FunctionDescriptor fd = library.findFunction(FunctionLibrary.CONVERT, new Class[] {originalType,
            DataTypeManager.DefaultDataClasses.STRING});

        Function function = new Function(fd.getName(), new Expression[] {theExpression, new Constant(theNewTypeName)});
        function.setType(DataTypeManager.getDataTypeClass(theNewTypeName));
        function.setFunctionDescriptor(fd);

        return function;
    }

    private Object getBaseObject( Object theObj ) {
        Object result = theObj;

        if (unwrap && (theObj instanceof Function)) {
            String functionName = ((Function)theObj).getName();
            if (functionName.equals(FunctionLibrary.CAST) || functionName.equals(FunctionLibrary.CONVERT)) {
                result = getBaseObject(((Function)theObj).getArg(0));
            }
        }

        return result;
    }

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

    private List getCandidateResults( boolean thePrimaryFlag ) {
        int numCandidates = (thePrimaryFlag) ? primarySize : secondarySize;
        ArrayList result = new ArrayList(numCandidates);

        for (int i = 0; i < numCandidates; i++) {
            result.add(getCandidateResultAt(i, thePrimaryFlag));
        }

        return result;
    }

    /**
     * Gets the editor being used by this converter.
     * 
     * @return the editor
     */
    // public MetaObjectEditor getEditor() {
    // return editor;
    // }
    /**
     * Gets the number of incompatible type associations between the two candidate lists.
     * 
     * @return the count
     */
    public int getIncompatibleCount() {
        int result = 0;

        for (int i = 0; i < compatible.length; i++) {
            if (!isCompatible(i)) {
                ++result;
            }
        }

        return result;
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
     * Gets an object from the original primary list.
     * 
     * @param theIndex the index of the object to be retrieved
     * @return the requested object
     */
    public Object getPrimaryCandidateAt( int theIndex ) {
        validateIndex(theIndex, primarySize, "getPrimaryCandidateAt(int)"); //$NON-NLS-1$

        return primaryCandidates.get(theIndex);
    }

    /**
     * Gets a list containing the objects of the original primary list. This is <strong>NOT</strong> the same list passed in on
     * construction.
     * 
     * @return the requested list
     */
    public List getPrimaryCandidates() {
        return primaryCandidates;
    }

    /**
     * Gets a name derived from either the list object being set or the list name.
     * 
     * @return the name associated with the primary candidate list
     * @see #setPrimaryObject(Object)
     * @see #setPrimaryName(String)
     */
    public String getPrimaryName() {
        return getName(true);
    }

    /**
     * Gets the object being used to set the list name.
     * 
     * @return the list object or <code>null</code>
     */
    public Object getPrimaryObject() {
        return primaryObj;
    }

    /**
     * Gets an object from the result list associated with the original primary list. The object returned can be the same object
     * as the original primary list, or it can be another object which is the result of a conversion taken place. For
     * {@link MetaObject}s, if a conversion has taken place but not yet committed, the object will be a type
     * {@link ObjectReference}.
     * 
     * @param theIndex the index of the object to be retrieved
     * @return the requested object
     * @see #commit()
     */
    public Object getPrimaryResultAt( int theIndex ) {
        return getCandidateResultAt(theIndex, true);
    }

    /**
     * The result list associated with the original primary candidate list. If none of the primary candidates have been converted,
     * this list will contain all the same objects as the original list.
     * 
     * @return the requested result list
     * @see #getPrimaryResultAt(int)
     */
    public List getPrimaryResults() {
        return getCandidateResults(true);
    }

    /**
     * Gets the runtime <code>Class</code> associated with the given object.
     * 
     * @param theObj the object whose runtime class is being requested
     * @return the requested runtime class
     */
    public static Class getRuntimeClass( Object theObj ) {
        Class result = null;

        if (theObj instanceof EObject && TransformationHelper.isSqlColumn(theObj)) {
            String type = getRuntimeType(theObj);

            if (type == null) {
                result = NullType.class;
            } else result = DataTypeManager.getDataTypeClass(type);
        } else if (theObj instanceof Expression) {
            result = ((Expression)theObj).getType();
        } else throw new IllegalArgumentException("RuntimeTypeConverter.getRuntimeClass:Class cannot be determined."); //$NON-NLS-1$

        return result;
    }

    /**
     * Gets the runtime type associated with the given object. These types are defined by the {@link DataTypeManager}.
     * 
     * @param theObj the object whose runtime type is being requested
     * @return the requested runtime type
     */
    public static String getRuntimeType( Object theObj ) {
        String type = null;
        if (theObj instanceof EObject) {
            EObject eObj = (EObject)theObj;
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObj, true);

            if (TransformationHelper.isSqlColumn(theObj)) {
                SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(eObj);
                type = columnAspect.getRuntimeType(eObj);
            } else if (dtMgr.isSimpleDatatype((EObject)theObj)) {
                return dtMgr.getRuntimeTypeName(eObj);
            }
        } else if (theObj instanceof Expression) {
            Class objClass = ((Expression)theObj).getType();
            if (objClass == null) {
                type = DataTypeManager.getDataTypeName(NullType.class);
            } else {
                type = DataTypeManager.getDataTypeName(objClass);
            }
        } else {
            throw new IllegalArgumentException(
                                               "RuntimeTypeConverter.getRuntimeType:Object type cannot be determined for <" + theObj + ">."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return type;
    }

    /**
     * Gets an object from the original secondary list.
     * 
     * @param theIndex the index of the object to be retrieved
     * @return the requested object
     */
    public Object getSecondaryCandidateAt( int theIndex ) {
        validateIndex(theIndex, secondarySize, "getSecondaryCandidateAt(int)"); //$NON-NLS-1$

        return secondaryCandidates.get(theIndex);
    }

    /**
     * Gets a list containing the objects of the original secondary list. This is <strong>NOT</strong> the same list passed in on
     * construction.
     * 
     * @return the requested list
     */
    public List getSecondaryCandidates() {
        return secondaryCandidates;
    }

    /**
     * Gets a name derived from either the list object being set or the list name.
     * 
     * @return the name associated with the secondary candidate list
     * @see #setSecondaryObject(Object)
     * @see #setSecondaryName(String)
     */
    public String getSecondaryName() {
        return getName(false);
    }

    /**
     * Gets the object being used to set the list name.
     * 
     * @return the list object or <code>null</code>
     */
    public Object getSecondaryObject() {
        return secondaryObj;
    }

    /**
     * Gets an object from the result list associated with the original secondary list. The object returned can be the same object
     * as the original secondary list, or it can be another object which is the result of a conversion taken place. For
     * {@link MetaObject}s, if a conversion has taken place but not yet committed, the object will be a type
     * {@link ObjectReference}.
     * 
     * @param theIndex the index of the object to be retrieved
     * @return the requested object
     * @see #commit()
     */
    public Object getSecondaryResultAt( int theIndex ) {
        return getCandidateResultAt(theIndex, false);
    }

    /**
     * The result list associated with the original secondary candidate list. If none of the secondary candidates have been
     * converted, this list will contain all the same objects as the original list.
     * 
     * @return the requested result list
     * @see #getSecondaryResultAt(int)
     */
    public List getSecondaryResults() {
        return getCandidateResults(false);
    }

    // public DataTypeResolver getTypeResolver() {
    // return typeResolver;
    // }

    /**
     * Indicates if existing conversion functions should be unwrapped prior to determining current conversion possibilities.
     * 
     * @return <code>true</code> if should be unwrapped; <code>false</code> otherwise.
     */
    public boolean getUnwrapExistingConversions() {
        return unwrap;
    }

    // private Object getTypePropertyValue(String theType) {
    // Object result = null;
    //        
    // // Get the default dataType for the runtime type
    // String dataTypeName = DefaultDatatypeConversions.getDatatypeNamefromRuntimeType(theType);
    //
    // // Get ObjectReference for runtime type string and set Attribute property
    // ObjectID dataTypeUuid = typeResolver.resolveObjectID(dataTypeName);
    // Object dataType = typeResolver.resolveDataTypeObject(dataTypeUuid);
    //        
    // if (dataType != null) {
    // result = new ObjectReference((MetaObject)dataType);
    // }
    // else if (dataTypeUuid != null) {
    // result = new ObjectReference(dataTypeUuid);
    // }
    //
    // return result;
    // }

    /**
     * Gets the size of the converter. The size will be the greater size of the two lists the converter was constructed with.
     * 
     * @return the size of the converter
     */
    public int getSize() {
        return size;
    }

    /**
     * Indicates if all associated object pairs have equal types or can be converted.
     * 
     * @return <code>true</code> if all pairs have compatible types; <code>false</code> otherwise;
     */
    public boolean isCompatible() {
        return (getIncompatibleCount() == 0);
    }

    /**
     * Indicates if the associated object pair at the given index has compatible types.
     * 
     * @return <code>true</code> if the associated object pair has compatible types; <code>false</code> otherwise;
     */
    public boolean isCompatible( int theIndex ) {
        validateIndex(theIndex, size, "isCompatible(int)"); //$NON-NLS-1$

        return compatible[theIndex];
    }

    public static boolean isCompatible( String theFirstType,
                                        String theSecondType ) {
        return (theFirstType.equals(theSecondType) || DataTypeManager.getTransform(theFirstType, theSecondType) != null);
    }

    /**
     * Indicates if a conversion occurred on one or more items.
     * 
     * @return <code>true</code> if a conversion took place; <code>false</code> otherwise;
     */
    public boolean isConverted() {
        boolean converted = false;

        for (int i = 0; i < size; i++) {
            if (isConverted(i)) {
                converted = true;
                break;
            }
        }

        return converted;
    }

    /**
     * Indicates if a conversion occurred to the source or target at the given index.
     * 
     * @param theIndex the index being checked
     * @return <code>true</code> if a conversion took place; <code>false</code> otherwise;
     */
    public boolean isConverted( int theIndex ) {
        validateIndex(theIndex, size, "isConverted(int)"); //$NON-NLS-1$

        boolean result = false;

        if (theIndex < primarySize) {
            result = isPrimaryConverted(theIndex);
        }

        if (!result && (theIndex < secondarySize)) {
            result = isSecondaryConverted(theIndex);
        }

        return result;
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
    public boolean isExplicitMatch() {
        return explicitMatch;
    }

    /**
     * Indicates of the runtime types of the associated item at the given index in each list exactly match.
     * 
     * @return <code>true</code> if types match; <code>false</code> otherwise.
     */
    public boolean isExplicitMatch( int theIndex ) {
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

    /**
     * Indicates of both lists are locked.
     * 
     * @return <code>true</code> if both lists are locked; <code>false</code> otherwise.
     */
    public boolean isLocked() {
        return isPrimaryLocked() && isSecondaryLocked();
    }

    /**
     * Indicates if a conversion occurred to the source at the given index.
     * 
     * @param theIndex the index being checked
     * @return <code>true</code> if a conversion took place; <code>false</code> otherwise;
     */
    public boolean isPrimaryConverted( int theIndex ) {
        validateIndex(theIndex, size, "isPrimaryConverted(int)"); //$NON-NLS-1$

        return (theIndex < primarySize) ? primaryConverted[theIndex] : false;
    }

    /**
     * Indicates of the primary list is locked.
     * 
     * @return <code>true</code> if locked; <code>false</code> otherwise.
     */
    public boolean isPrimaryLocked() {
        return primaryLocked;
    }

    /**
     * Indicates if a conversion occurred to target at the given index.
     * 
     * @param theIndex the index being checked
     * @return <code>true</code> if a conversion took place; <code>false</code> otherwise;
     */
    public boolean isSecondaryConverted( int theIndex ) {
        validateIndex(theIndex, size, "isSecondaryConverted(int)"); //$NON-NLS-1$

        return (theIndex < secondarySize) ? secondaryConverted[theIndex] : false;
    }

    /**
     * Indicates of the secondary list is locked.
     * 
     * @return <code>true</code> if locked; <code>false</code> otherwise.
     */
    public boolean isSecondaryLocked() {
        return secondaryLocked;
    }

    /**
     * Resets the candidate at the given index to the original object.
     * 
     * @param theIndex the index of the object being reset
     * @param thePrimaryFlag a flag indicating if the primary or secondary object should be reset
     */
    private void resetCandidate( int theIndex,
                                 boolean thePrimaryFlag ) {
        validateIndex(theIndex, (thePrimaryFlag) ? primarySize : secondarySize, "resetCandidate(int, boolean"); //$NON-NLS-1$

        if (isConverted(theIndex)) {
            List candidates = (thePrimaryFlag) ? primaryCandidates : secondaryCandidates;
            List convertedCandidates = (thePrimaryFlag) ? primaryResults : secondaryResults;
            boolean[] convertedFlags = (thePrimaryFlag) ? primaryConverted : secondaryConverted;

            convertedCandidates.set(theIndex, candidates.get(theIndex));
            convertedFlags[theIndex] = false;

            setCompatibility(theIndex);
        }
    }

    /**
     * Resets the primary candidate at the given index to the original object.
     * 
     * @param theIndex the index of the object being reset
     */
    public void resetPrimaryCandidate( int theIndex ) {
        // resetCandidate validates the index
        resetCandidate(theIndex, true);
    }

    /**
     * Resets the secondary candidate at the given index to the original object.
     * 
     * @param theIndex the index of the object being reset
     */
    public void resetSecondaryCandidate( int theIndex ) {
        // resetCandidate validates the index
        resetCandidate(theIndex, false);
    }

    private void setCandidate( int theIndex,
                               Object theCandidate,
                               boolean thePrimaryFlag ) {
        validateIndex(theIndex, (thePrimaryFlag) ? primarySize : secondarySize, "setCandidate(int, Object, boolean"); //$NON-NLS-1$

        List candidates = (thePrimaryFlag) ? primaryCandidates : secondaryCandidates;
        candidates.set(theIndex, theCandidate);

        List convertedCandidates = (thePrimaryFlag) ? primaryResults : secondaryResults;
        convertedCandidates.set(theIndex, theCandidate);

        setCompatibility(theIndex);
        setExplicitMatch(theIndex);
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

    private void setExplicitMatch( int theIndex ) {
        // isExplicitMatch(int) validates the index
        boolean match = isExplicitMatch(theIndex);

        if (match != explicitMatch) {
            if (match) {
                boolean foundOne = false;
                for (int i = 0; i < size; i++) {
                    if (!isExplicitMatch(i)) {
                        foundOne = true;
                        break;
                    }
                }
                if (!foundOne) {
                    // must have matched the only non-match
                    explicitMatch = true;
                }
            } else {
                explicitMatch = false;
            }
        }
    }

    // private void setMetaObjectType(EObject theEObj,
    // Object theType) {
    // // !!! Should only be called from the commit method which does the transaction !!!
    // if (!editor.isReadOnly(theEObj)) {
    // // do all MOs have the same type PropertyDefinition???
    // PropertyDefinition def = editor.getDataTypePropertyDefinition(theMetaObj);
    // editor.setValue(theMetaObj, def, theType);
    // }
    // }

    /**
     * Sets a new object into the primary list at the given index.
     * 
     * @param theIndex the index where the new object is being set
     * @param theCandidate the new object
     */
    public void setPrimaryCandidate( int theIndex,
                                     Object theCandidate ) {
        // setCandidate validates the index
        setCandidate(theIndex, theCandidate, true);
    }

    /**
     * Sets the name representing the primary list. Only will be used if the primary object has not been set. The name defaults to
     * "Primary."
     * 
     * @param theName the primary list name
     * @see #setPrimaryObject(Object)
     */
    public void setPrimaryName( String theName ) {
        primaryName = theName;
    }

    /**
     * Sets the object used to determine the primary list name.
     * 
     * @param theObject the object defining the primary list
     * @see setPrimaryName(String)
     */
    public void setPrimaryObject( Object theObject ) {
        primaryObj = theObject;
    }

    /**
     * Sets a new object into the secondary list at the given index.
     * 
     * @param theIndex the index where the new object is being set
     * @param theCandidate the new object
     */
    public void setSecondaryCandidate( int theIndex,
                                       Object theCandidate ) {
        // setCandidate validates the index
        setCandidate(theIndex, theCandidate, false);
    }

    /**
     * Sets the name representing the secondary list. Only will be used if the secondary object has not been set. The name
     * defaults to "Secondary."
     * 
     * @param theName the secondary list name
     * @see #setSecondaryObject(Object)
     */
    public void setSecondaryName( String theName ) {
        secondaryName = theName;
    }

    /**
     * Sets the object used to determine the secondary list name.
     * 
     * @param theObject the object defining the secondary list
     * @see setSecondaryName(String)
     */
    public void setSecondaryObject( Object theObject ) {
        secondaryObj = theObject;
    }

    // /**
    // * A string representation providing detailed information.
    // * @return the detailed information
    // */
    // private String toDebugString() {
    // StringBuffer txt = new StringBuffer();
    //
    // txt.append("explicitMatch=").append(explicitMatch)
    // .append(", compatible=").append(isCompatible())
    // .append(", converted=").append(isConverted())
    // .append(", primaryName=").append(getPrimaryName())
    // .append(", primaryObj=").append(getPrimaryObject())
    // .append(", primaryLocked=").append(isPrimaryLocked())
    // .append(", primary size=").append(primarySize)
    // .append(", secondaryName=").append(getSecondaryName())
    // .append(", secondaryObj=").append(getSecondaryObject())
    // .append(", secondaryLocked=").append(isSecondaryLocked())
    // .append(", secondary size=").append(secondarySize)
    // .append("\n\n");
    //
    // for (int i = 0; i < size; i++) {
    // txt.append(", index=").append(i)
    // .append(", compatible=").append(isCompatible(i))
    // .append(", converted=").append(isConverted(i));
    //          
    // if (i < primarySize) {
    // txt.append(", primary=").append(primaryCandidates.get(i))
    // .append(", primary type=").append(getRuntimeType(primaryCandidates.get(i)))
    // .append(", primaryConverted=").append(isPrimaryConverted(i))
    // .append(", converted object=").append(getCandidateResultAt(i, true));
    // }
    // else {
    // txt.append(", primary=not found");
    // }
    //          
    // if (i < secondarySize) {
    // txt.append(", secondary=").append(secondaryCandidates.get(i))
    // .append(", secondary type=").append(getRuntimeType(secondaryCandidates.get(i)))
    // .append(", secondaryConverted=").append(isSecondaryConverted(i))
    // .append(", converted object=").append(getCandidateResultAt(i, false))
    // .append('\n');
    // }
    // else {
    // txt.append(", secondary=not found");
    // }
    // }
    // txt.append("--------------------------\n\n\n");
    //      
    // return txt.toString();
    // }

    /**
     * Gets both list names with a delimeter between them.
     * 
     * @return a concatenation of the list names
     */
    @Override
    public String toString() {
        return getPrimaryName() + "<->" + getSecondaryName(); //$NON-NLS-1$
    }

    public void setUnwrapExistingConversions( boolean theUnwrapFlag ) {
        unwrap = theUnwrapFlag;
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
