/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDFixedFacet;
import org.eclipse.xsd.XSDFractionDigitsFacet;
import org.eclipse.xsd.XSDLengthFacet;
import org.eclipse.xsd.XSDMaxFacet;
import org.eclipse.xsd.XSDMaxLengthFacet;
import org.eclipse.xsd.XSDMinFacet;
import org.eclipse.xsd.XSDMinLengthFacet;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.XSDRepeatableFacet;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTotalDigitsFacet;
import org.eclipse.xsd.XSDWhiteSpaceFacet;
import org.eclipse.xsd.impl.XSDConstrainingFacetImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;

public class FacetHelper {
    //
    // Class constants:
    //
    private static final String ERROR_KEY_UNUSABLE_FACET = "FacetHelper.unusableFacet.error"; //$NON-NLS-1$

    public static final String FAKE_FACET_MAXIMUM = "maximum"; //$NON-NLS-1$
    public static final String FAKE_FACET_MINIMUM = "minimum"; //$NON-NLS-1$
    public static final String FACET_FRACTIONDIGITS = "fractionDigits"; //$NON-NLS-1$
    public static final String FACET_TOTALDIGITS = "totalDigits"; //$NON-NLS-1$
    public static final String FACET_MAX_INCLUSIVE = "maxInclusive"; //$NON-NLS-1$
    public static final String FACET_MIN_INCLUSIVE = "minInclusive"; //$NON-NLS-1$
    public static final String FACET_MAX_EXCLUSIVE = "maxExclusive"; //$NON-NLS-1$
    public static final String FACET_MIN_EXCLUSIVE = "minExclusive"; //$NON-NLS-1$
    public static final String FACET_MAXLENGTH = "maxLength"; //$NON-NLS-1$
    public static final String FACET_MINLENGTH = "minLength"; //$NON-NLS-1$
    public static final String FACET_LENGTH = "length"; //$NON-NLS-1$
    public static final String FACET_WHITESPACE = "whitespace"; //$NON-NLS-1$
    public static final String FACET_ENUMERATION = "enumeration"; //$NON-NLS-1$
    public static final String FACET_PATTERN = "pattern"; //$NON-NLS-1$

    // private static final String[] FIXED_FACETS = {FACET_FRACTIONDIGITS,
    // FACET_LENGTH,
    // FACET_MAXIMUM,
    // FACET_MAXLENGTH,
    // FACET_MINIMUM,
    // FACET_MINLENGTH,
    // FACET_TOTALDIGITS,
    // FACET_WHITESPACE,
    // }; // endarray
    private static final String[] NUMERIC_TYPES = {DatatypeConstants.BuiltInNames.INTEGER,
        DatatypeConstants.BuiltInNames.POSITIVE_INTEGER, DatatypeConstants.BuiltInNames.NEGATIVE_INTEGER,
        DatatypeConstants.BuiltInNames.NON_NEGATIVE_INTEGER, DatatypeConstants.BuiltInNames.NON_POSITIVE_INTEGER,
        DatatypeConstants.BuiltInNames.LONG, DatatypeConstants.BuiltInNames.UNSIGNED_LONG, DatatypeConstants.BuiltInNames.INT,
        DatatypeConstants.BuiltInNames.UNSIGNED_INT, DatatypeConstants.BuiltInNames.BYTE,
        DatatypeConstants.BuiltInNames.UNSIGNED_BYTE, DatatypeConstants.BuiltInNames.DECIMAL,}; // endarray

    /** Note that this list is in addition to numeric_types... */
    private static final String[] BOUNDS_TYPES = {DatatypeConstants.BuiltInNames.FLOAT, DatatypeConstants.BuiltInNames.DOUBLE,
        DatatypeConstants.BuiltInNames.BOOLEAN, DatatypeConstants.BuiltInNames.DURATION,
        DatatypeConstants.BuiltInNames.DATE_TIME, DatatypeConstants.BuiltInNames.DATE, DatatypeConstants.BuiltInNames.TIME,
        DatatypeConstants.BuiltInNames.GYEAR, DatatypeConstants.BuiltInNames.GYEAR_MONTH, DatatypeConstants.BuiltInNames.GMONTH,
        DatatypeConstants.BuiltInNames.GMONTH_DAY, DatatypeConstants.BuiltInNames.GDAY,}; // endarray

    public static final int DEFAULT_MAX_BOUNDS = 999999;
    public static final int DEFAULT_MIN_BOUNDS = -DEFAULT_MAX_BOUNDS;

    //
    // Class variables:
    //
    private static Map facetNametoEClass = new HashMap();
    private static Map defaults = new HashMap();

    //
    // Constructors/Initializers:
    //
    static {
        // ----------------------------------------------------------------------------------
        facetNametoEClass.put(FACET_ENUMERATION, XSDPackage.eINSTANCE.getXSDEnumerationFacet());
        facetNametoEClass.put(FACET_FRACTIONDIGITS, XSDPackage.eINSTANCE.getXSDFractionDigitsFacet());
        facetNametoEClass.put(FACET_LENGTH, XSDPackage.eINSTANCE.getXSDLengthFacet());
        facetNametoEClass.put(FACET_MAXLENGTH, XSDPackage.eINSTANCE.getXSDMaxLengthFacet());
        facetNametoEClass.put(FACET_MINLENGTH, XSDPackage.eINSTANCE.getXSDMinLengthFacet());
        facetNametoEClass.put(FACET_MAX_INCLUSIVE, XSDPackage.eINSTANCE.getXSDMaxInclusiveFacet());
        facetNametoEClass.put(FACET_MAX_EXCLUSIVE, XSDPackage.eINSTANCE.getXSDMaxExclusiveFacet());
        facetNametoEClass.put(FACET_MIN_INCLUSIVE, XSDPackage.eINSTANCE.getXSDMinInclusiveFacet());
        facetNametoEClass.put(FACET_MIN_EXCLUSIVE, XSDPackage.eINSTANCE.getXSDMinExclusiveFacet());
        facetNametoEClass.put(FACET_PATTERN, XSDPackage.eINSTANCE.getXSDPatternFacet());
        facetNametoEClass.put(FACET_TOTALDIGITS, XSDPackage.eINSTANCE.getXSDTotalDigitsFacet());
        facetNametoEClass.put(FACET_WHITESPACE, XSDPackage.eINSTANCE.getXSDWhiteSpaceFacet());
    }

    public static void setEnterpriseFacetValue( XSDSimpleTypeDefinition type,
                                                FacetValue newValue ) {
        if (newValue.value != null) {
            // set new enterprise info:
            EnterpriseDatatypeInfo edi = ModelerCore.getWorkspaceDatatypeManager().getEnterpriseDatatypeInfo(type);
            if (edi == null) {
                edi = new EnterpriseDatatypeInfo();
            } // endif
            edi.setRuntimeTypeFixed(new Boolean(newValue.isFixedLocal));
            edi.setRuntimeType((String)newValue.value);
            ModelEditorImpl.fillWithDefaultValues(edi, type);
            ModelerCore.getModelEditor().setEnterpriseDatatypePropertyValue(type, edi);
        } else {
            // need to unset:
            ModelerCore.getModelEditor().unsetEnterpriseDatatypePropertyValue(type);
        } // endif
    }

    public static FacetValue getEnterpriseFacetValue( final XSDSimpleTypeDefinition startingType ) {
        FacetValue fv = new FacetValue();

        final XSDSimpleTypeDefinition definingEnterpriseType = getDefiningEnterpriseType(startingType);
        fv.type = startingType;
        EnterpriseDatatypeInfo edi = ModelerCore.getWorkspaceDatatypeManager().getEnterpriseDatatypeInfo(definingEnterpriseType);
        fv.value = edi.getRuntimeType();

        if (startingType == definingEnterpriseType) {
            // this "facet" set in the specified type, so it is not default.
            // check parent for real default:
            final XSDSimpleTypeDefinition dftDefiner = getDefiningEnterpriseType(definingEnterpriseType.getBaseTypeDefinition());
            EnterpriseDatatypeInfo ediDft = ModelerCore.getWorkspaceDatatypeManager().getEnterpriseDatatypeInfo(dftDefiner);
            fv.defaultValue = ediDft.getRuntimeType();
            // set a fake facet to allow inherited checks to work:
            fv.facet = new XSDConstrainingFacetImpl() {
                @Override
                public String getFacetName() {
                    return "This is not a real facet!"; //$NON-NLS-1$
                }

                @Override
                public XSDConcreteComponent getContainer() {
                    return dftDefiner;
                }
            }; // endanon
        } else {
            // facet set by a parent, so it is default:
            fv.defaultValue = fv.value;
            // set a fake facet to allow inherited checks to work:
            fv.facet = new XSDConstrainingFacetImpl() {
                @Override
                public String getFacetName() {
                    return "This is not a real facet!"; //$NON-NLS-1$
                }

                @Override
                public XSDConcreteComponent getContainer() {
                    return definingEnterpriseType;
                }
            }; // endanon
        } // endif

        Boolean rtFixed = edi.getRuntimeTypeFixed();
        if (rtFixed != null) {
            fv.isFixedLocal = rtFixed.booleanValue();
        } // endif -- fixed null

        return fv;
    }

    public static FacetValue getFacetValue( XSDSimpleTypeDefinition type,
                                            XSDConstrainingFacet facet ) {
        FacetValue fv = new FacetValue();
        fv.description = ModelObjectUtilities.getDescription(facet);
        fv.type = type;
        fv.facet = facet;
        Object mainVal = getMainFacetValue(facet);
        if (mainVal != null) {
            fv.value = mainVal;
        } else {
            fv.value = getDefaultMainFacetValue(facet.getFacetName());
        } // endif

        if (type == facet.getContainer()) {
            // this facet set in the specified type, so it is not default:
            fv.defaultValue = null; // getMainFacetValue(type.getBaseTypeDefinition(), facet.getFacetName());
        } else {
            // facet set by a parent, so it is default:
            fv.defaultValue = fv.value;
        } // endif
        fv.isFixedLocal = isFixed(facet);

        return fv;
    }

    private static void removeFacet( XSDSimpleTypeDefinition type,
                                     String facetName ) {
        // resolve the name, in case it is one of the bounds facets:
        String nameEx = getRealFacetName(facetName, false);
        String nameIn = getRealFacetName(facetName, true); // most of the time, name == name2

        Iterator itor = type.getFacetContents().iterator();
        while (itor.hasNext()) {
            XSDConstrainingFacet facet = (XSDConstrainingFacet)itor.next();
            String thisName = facet.getFacetName();
            if (nameEx.equals(thisName) || nameIn.equals(thisName)) {
                itor.remove();
            } // endif
        } // endwhile
    }

    private static String getRealFacetName( String facetName,
                                            boolean inclusive ) {
        if (facetName == FAKE_FACET_MAXIMUM) {
            // need to pay attention to inclusive param:
            if (inclusive) {
                facetName = FACET_MAX_INCLUSIVE;
            } else {
                facetName = FACET_MAX_EXCLUSIVE;
            } // endif
        } else if (facetName == FAKE_FACET_MINIMUM) {
            // need to pay attention to inclusive param:
            if (inclusive) {
                facetName = FACET_MIN_INCLUSIVE;
            } else {
                facetName = FACET_MIN_EXCLUSIVE;
            } // endif
        } // endif -- using fake facets?

        return facetName;
    }

    public static void removeFacet( XSDSimpleTypeDefinition type,
                                    XSDConstrainingFacet facet ) {
        try {
            ModelerCore.getModelEditor().removeValue(type, facet, type.getFacetContents());
        } catch (ModelerCoreException err) {
            ModelerXsdUiConstants.Util.log(err);
        } // endtry
    }

    public static XSDConstrainingFacet addOrSetFacetValue( XSDSimpleTypeDefinition type,
                                                           String facetName,
                                                           FacetValue fv ) {
        XSDConstrainingFacet workFacet = fv.facet;
        // do we need to add?
        if (fv.facet == null || fv.facet.getContainer() != type) {
            // need to add to this type:
            boolean inclusiveness = false;
            if (fv.value instanceof InclusiveInteger) {
                inclusiveness = ((InclusiveInteger)fv.value).isInclusive;
            } // endif

            // remove any facets with opposite inclusiveness:
            if (facetName == FAKE_FACET_MAXIMUM || facetName == FAKE_FACET_MINIMUM) {
                // go ahead and remove:
                removeFacet(type, facetName);
            } // endif -- using fake facets?

            workFacet = createFacet(facetName, inclusiveness);

            try {
                ModelerCore.getModelEditor().addValue(type, workFacet, type.getFacetContents());
            } catch (ModelerCoreException err) {
                ModelerXsdUiConstants.Util.log(err);
            } // endtry
        } // endif

        // set main value:
        if (!FormUtil.safeEquals(fv.value, getMainFacetValue(workFacet))) {
            // only set if changed:
            workFacet = setMainFacetValue(type, workFacet, fv.value);
            // in case this is a bounds facet and we swapped them out:
            fv.facet = workFacet;
        } // endif

        // set description:
        String existingDesc = ModelObjectUtilities.getDescription(workFacet);
        if (fv.description != null) {
            // new not null:
            if (!fv.description.equals(existingDesc)) {
                // description changed to a nonnull value:
                ModelObjectUtilities.setDescription(workFacet, fv.description, type);
            } // endif -- different
        } else if (existingDesc != null && existingDesc.length() > 0) {
            // new null, old not null:
            ModelObjectUtilities.setDescription(workFacet, " ", type); //$NON-NLS-1$
        } // endif

        // Lastly, set fixed if applicable:
        setFixed(workFacet, fv.isFixedLocal);

        return workFacet;
    }

    private static void setFixed( XSDConstrainingFacet workFacet,
                                  boolean isFixed ) {
        if (workFacet instanceof XSDFixedFacet) {
            XSDFixedFacet ff = (XSDFixedFacet)workFacet;
            ff.setFixed(isFixed);
        } // endif
    }

    /**
     * Sets the core value of the specified facet. If facet cannot be properly modified to match fv, facets will be added or
     * removed as necessary to make it work. This occurs when a min or max value is changed from inclusive to exclusive, and when
     * dealing with patterns and enumerations.
     * 
     * @param facet
     * @param fv
     */
    private static XSDConstrainingFacet setMainFacetValue( XSDSimpleTypeDefinition type,
                                                           XSDConstrainingFacet facet,
                                                           Object value ) {
        int facetClassifierID = facet.eClass().getClassifierID();
        switch (facetClassifierID) {
            case XSDPackage.XSD_LENGTH_FACET: {
                XSDLengthFacet lf = (XSDLengthFacet)facet;
                if (value instanceof Integer) {
                    Integer i = (Integer)value;
                    lf.setLexicalValue(i.toString());
                } else if (value instanceof InclusiveInteger) {
                    InclusiveInteger ii = (InclusiveInteger)value;
                    lf.setLexicalValue(Integer.toString(ii.value));
                } // endif
            }
                break;
            case XSDPackage.XSD_MAX_LENGTH_FACET: {
                XSDMaxLengthFacet mf = (XSDMaxLengthFacet)facet;
                if (value instanceof Integer) {
                    Integer i = (Integer)value;
                    mf.setLexicalValue(i.toString());
                } else if (value instanceof InclusiveInteger) {
                    InclusiveInteger ii = (InclusiveInteger)value;
                    mf.setLexicalValue(Integer.toString(ii.value));
                } // endif
            }
                break;
            case XSDPackage.XSD_MIN_LENGTH_FACET: {
                XSDMinLengthFacet mf = (XSDMinLengthFacet)facet;
                if (value instanceof Integer) {
                    Integer i = (Integer)value;
                    mf.setLexicalValue(i.toString());
                } else if (value instanceof InclusiveInteger) {
                    InclusiveInteger ii = (InclusiveInteger)value;
                    mf.setLexicalValue(Integer.toString(ii.value));
                } // endif
            }
                break;
            case XSDPackage.XSD_PATTERN_FACET: {
                XSDPatternFacet pf = (XSDPatternFacet)facet;
                pf.setLexicalValue((String)value);
            }
                break;
            case XSDPackage.XSD_ENUMERATION_FACET: {
                XSDEnumerationFacet ef = (XSDEnumerationFacet)facet;
                ef.setLexicalValue((String)value);
            }
                break;
            case XSDPackage.XSD_WHITE_SPACE_FACET: {
                XSDWhiteSpaceFacet wf = (XSDWhiteSpaceFacet)facet;
                if (value instanceof String) {
                    String white = (String)value;
                    wf.setLexicalValue(white);
                } // endif
            }
                break;
            case XSDPackage.XSD_MIN_EXCLUSIVE_FACET:
            case XSDPackage.XSD_MIN_INCLUSIVE_FACET: {
                XSDMinFacet mf = (XSDMinFacet)facet;
                if (value instanceof Integer) {
                    Integer i = (Integer)value;
                    mf.setLexicalValue(i.toString());
                } else if (value instanceof InclusiveInteger) {
                    InclusiveInteger ii = (InclusiveInteger)value;
                    if (ii.isInclusive == mf.isInclusive()) {
                        // same inclusive types, don't need to do anything crazy
                        mf.setLexicalValue(Integer.toString(ii.value));
                    } else {
                        XSDMinFacet mf2;
                        if (ii.isInclusive) {
                            mf2 = XSDFactory.eINSTANCE.createXSDMinInclusiveFacet();
                        } else {
                            mf2 = XSDFactory.eINSTANCE.createXSDMinExclusiveFacet();
                        } // endif
                        mf2.setLexicalValue(Integer.toString(ii.value));
                        try {
                            // remove old:
                            ModelerCore.getModelEditor().removeValue(type, mf, type.getFacetContents());
                            // add the copy:
                            ModelerCore.getModelEditor().addValue(type, mf2, type.getFacetContents());
                            // update the return value:
                            facet = mf2;
                        } catch (ModelerCoreException err) {
                            ModelerXsdUiConstants.Util.log(err);
                        } // endtry

                        return mf2;
                    } // endif -- same inclusive
                } // endif -- integer or iinteger
            }
                break;
            case XSDPackage.XSD_MAX_EXCLUSIVE_FACET:
            case XSDPackage.XSD_MAX_INCLUSIVE_FACET: {
                XSDMaxFacet mf = (XSDMaxFacet)facet;
                if (value instanceof Integer) {
                    Integer i = (Integer)value;
                    mf.setLexicalValue(i.toString());
                } else if (value instanceof InclusiveInteger) {
                    InclusiveInteger ii = (InclusiveInteger)value;
                    if (ii.isInclusive == mf.isInclusive()) {
                        // same inclusive types, don't need to do anything crazy
                        mf.setLexicalValue(Integer.toString(ii.value));
                    } else {
                        XSDMaxFacet mf2;
                        if (ii.isInclusive) {
                            mf2 = XSDFactory.eINSTANCE.createXSDMaxInclusiveFacet();
                        } else {
                            mf2 = XSDFactory.eINSTANCE.createXSDMaxExclusiveFacet();
                        } // endif
                        mf2.setLexicalValue(Integer.toString(ii.value));
                        try {
                            // remove old:
                            ModelerCore.getModelEditor().removeValue(type, mf, type.getFacetContents());
                            // add the copy:
                            ModelerCore.getModelEditor().addValue(type, mf2, type.getFacetContents());
                            // update the return value:
                            facet = mf2;
                        } catch (ModelerCoreException err) {
                            ModelerXsdUiConstants.Util.log(err);
                        } // endtry

                        return mf2;
                    } // endif -- same inclusive
                } // endif -- integer or iinteger
            }
                break;
            case XSDPackage.XSD_FRACTION_DIGITS_FACET: {
                XSDFractionDigitsFacet ff = (XSDFractionDigitsFacet)facet;
                if (value instanceof Integer) {
                    Integer i = (Integer)value;
                    ff.setLexicalValue(i.toString());
                } else if (value instanceof InclusiveInteger) {
                    InclusiveInteger ii = (InclusiveInteger)value;
                    ff.setLexicalValue(Integer.toString(ii.value));
                } // endif
            }
                break;
            case XSDPackage.XSD_TOTAL_DIGITS_FACET: {
                XSDTotalDigitsFacet tf = (XSDTotalDigitsFacet)facet;
                if (value instanceof Integer) {
                    Integer i = (Integer)value;
                    tf.setLexicalValue(i.toString());
                } else if (value instanceof InclusiveInteger) {
                    InclusiveInteger ii = (InclusiveInteger)value;
                    tf.setLexicalValue(Integer.toString(ii.value));
                } // endif
            }
                break;

            default:
                ModelerXsdUiConstants.Util.log(ModelerXsdUiConstants.Util.getString(ERROR_KEY_UNUSABLE_FACET, facet));
                break;
        } // endswitch

        return facet;
    }

    private static Object getDefaultMainFacetValue( String facetName ) {
        Object dft = defaults.get(facetName);
        if (dft == null) {
            XSDConstrainingFacet dftFacet = createFacet(facetName);
            dft = getMainFacetValue(dftFacet);
            defaults.put(facetName, dft);
        } // endif

        return dft;
    }

    // private static Object getMainFacetValue(XSDSimpleTypeDefinition type, String name) {
    // XSDConstrainingFacet facet = null;
    // facet = getFacetByName(type, name);
    //
    // if (facet != null) {
    // return getMainFacetValue(facet);
    // } // endif
    //
    // return getDefaultMainFacetValue(name);
    // }

    // private static XSDConstrainingFacet getFacetByName(XSDSimpleTypeDefinition type, String name) {
    // List facets = type.getFacets();
    // Iterator itor = facets.iterator();
    // while (itor.hasNext()) {
    // XSDConstrainingFacet testFct = (XSDConstrainingFacet) itor.next();
    // if (name.equals(testFct.getFacetName())) {
    // return testFct;
    // } // endif
    // } // endwhile
    //        
    // return null;
    // }

    public static Object getMainFacetValue( XSDConstrainingFacet facet ) {
        Object rv = null;
        int facetClassifierID = facet.eClass().getClassifierID();
        switch (facetClassifierID) {
            case XSDPackage.XSD_LENGTH_FACET: {
                XSDLengthFacet lf = (XSDLengthFacet)facet;
                rv = new Integer(lf.getValue());
            }
                break;
            case XSDPackage.XSD_MAX_LENGTH_FACET: {
                XSDMaxLengthFacet mf = (XSDMaxLengthFacet)facet;
                rv = new Integer(mf.getValue());
            }
                break;
            case XSDPackage.XSD_MIN_LENGTH_FACET: {
                XSDMinLengthFacet mf = (XSDMinLengthFacet)facet;
                rv = new Integer(mf.getValue());
            }
                break;
            case XSDPackage.XSD_PATTERN_FACET: {
                XSDPatternFacet pf = (XSDPatternFacet)facet;
                List value = pf.getValue();
                if (value.size() > 1) {
                    // skip this, it is a multi-element
                    rv = value;
                } else if (value.size() == 1) {
                    // single entry, use it:
                    rv = value.get(0);
                } else {
                    // no elements; try the lex value:
                    rv = pf.getLexicalValue();
                } // endif
            }
                break;
            case XSDPackage.XSD_ENUMERATION_FACET: {
                XSDEnumerationFacet ef = (XSDEnumerationFacet)facet;
                List value = ef.getValue();
                if (value.size() > 1) {
                    // skip this, it is a multi-element
                    rv = value;
                } else if (value.size() == 1) {
                    // single entry, use it:
                    rv = value.get(0);
                } else {
                    // no elements; try the lex value:
                    rv = ef.getLexicalValue();
                } // endif
            }
                break;
            case XSDPackage.XSD_WHITE_SPACE_FACET: {
                XSDWhiteSpaceFacet wf = (XSDWhiteSpaceFacet)facet;
                rv = wf.getValue().getName();
            }
                break;
            case XSDPackage.XSD_MIN_EXCLUSIVE_FACET:
            case XSDPackage.XSD_MIN_INCLUSIVE_FACET: {
                XSDMinFacet mf = (XSDMinFacet)facet;
                // defect 18279 - read as string, since the backing datatype can be in different value spaces.
                int value;
                try {
                    String lexicalValue = mf.getLexicalValue();
                    if (lexicalValue != null) {
                        value = Integer.parseInt(lexicalValue);
                    } else {
                        // it is null, use invalid number:
                        value = -Integer.MAX_VALUE;
                    } // endif
                } catch (NumberFormatException ex) {
                    value = DEFAULT_MIN_BOUNDS;
                } // endtry
                rv = new InclusiveInteger(value, mf.isInclusive());
            }
                break;
            case XSDPackage.XSD_MAX_EXCLUSIVE_FACET:
            case XSDPackage.XSD_MAX_INCLUSIVE_FACET: {
                XSDMaxFacet mf = (XSDMaxFacet)facet;
                // defect 18279 - read as string, since the backing datatype can be in different value spaces.
                int value;
                try {
                    String lexicalValue = mf.getLexicalValue();
                    if (lexicalValue != null) {
                        value = Integer.parseInt(lexicalValue);
                    } else {
                        // it is null, use zero:
                        value = -Integer.MAX_VALUE;
                    } // endif
                } catch (NumberFormatException ex) {
                    value = DEFAULT_MAX_BOUNDS;
                } // endtry
                rv = new InclusiveInteger(value, mf.isInclusive());
            }
                break;
            case XSDPackage.XSD_FRACTION_DIGITS_FACET: {
                XSDFractionDigitsFacet ff = (XSDFractionDigitsFacet)facet;
                rv = new Integer(ff.getValue());
            }
                break;
            case XSDPackage.XSD_TOTAL_DIGITS_FACET: {
                XSDTotalDigitsFacet tf = (XSDTotalDigitsFacet)facet;
                rv = new Integer(tf.getValue());
            }
                break;

            default:
                ModelerXsdUiConstants.Util.log(ModelerXsdUiConstants.Util.getString(ERROR_KEY_UNUSABLE_FACET, facet));
                break;
        } // endswitch

        return rv;
    }

    public static boolean isFixed( XSDConstrainingFacet facet ) {
        return facet instanceof XSDFixedFacet && ((XSDFixedFacet)facet).isFixed();
    }

    // public static boolean supportsFixed(String facetName) {
    // // scan names to see if fixed supported:
    // for (int i = 0; i < FIXED_FACETS.length; i++) {
    // String fixableTypeName = FIXED_FACETS[i];
    // if (fixableTypeName.equalsIgnoreCase(facetName)) {
    // return true;
    // } // endif
    // } // endfor
    //
    // return false;
    // }

    /**
     * This method gets all relevant facets. It behaves differently from a simple getFacets() call in that enumerations are not
     * congealed.
     */
    public static Set getUsefulFacets( XSDSimpleTypeDefinition type ) {
        Set rv = new HashSet();

        Iterator itor = type.getFacets().iterator();
        while (itor.hasNext()) {
            XSDConstrainingFacet facet = (XSDConstrainingFacet)itor.next();
            if (facet instanceof XSDRepeatableFacet && facet.getElement() == null) {
                // this is a fake pattern or enum; get the individual entries:
                if (facet instanceof XSDPatternFacet) {
                    XSDPatternFacet pf = (XSDPatternFacet)facet;
                    XSDSimpleTypeDefinition realParent = (XSDSimpleTypeDefinition)pf.getContainer();
                    // only add patterns if they belong to this type:
                    if (realParent == type) {
                        rv.addAll(realParent.getPatternFacets());
                    } // endif
                } else if (facet instanceof XSDEnumerationFacet) {
                    XSDEnumerationFacet ef = (XSDEnumerationFacet)facet;
                    XSDSimpleTypeDefinition realParent = (XSDSimpleTypeDefinition)ef.getContainer();
                    // only add enums if they belong to this type:
                    if (realParent == type) {
                        rv.addAll(realParent.getEnumerationFacets());
                    } // endif
                } // endif -- which kind of repeatable?
            } else {
                rv.add(facet);
            } // endif -- fake facet
        } // endwhile -- facets

        return rv;
    }

    // /** This method will create a new facet and add it to the specified type.
    // * @param type The type to work with
    // * @param facetName name of the facet to create
    // */
    // private static XSDConstrainingFacet createAndAddFacet(XSDSimpleTypeDefinition type, String facetName) {
    // return createAndAddFacet(type, facetName, false);
    // }

    // /** This method will create a new facet and add it to the specified type.
    // * @param type The type to work with
    // * @param facetName name of the facet to create
    // */
    // private static XSDConstrainingFacet createAndAddFacet(XSDSimpleTypeDefinition type, String facetName, boolean inclusive) {
    // // adding a new (unrestricted) facet:
    // XSDConstrainingFacet facet = createFacet(facetName, inclusive);
    // // add the copy:
    // type.getFacetContents().add(facet);
    //        
    // return facet;
    // }

    // /** This method will copy the specified facet and add it to the specified type.
    // * The existing facet will be removed.
    // * @param type The type to work with
    // * @param facet the facet to copy
    // */
    // private static XSDConstrainingFacet copyAndAddFacet(XSDSimpleTypeDefinition type, XSDConstrainingFacet facet) {
    // // modifying an existing facet:
    // try {
    // ModelEditor me = ModelerCore.getModelEditor();
    // XSDConstrainingFacet newFacet = (XSDConstrainingFacet) me.copy(facet);
    //
    // // remove original if present in this type:
    // type.getFacetContents().remove(facet);
    // // add the copy:
    // type.getFacetContents().add(newFacet);
    // } catch (ModelerCoreException ex) {
    // ModelerXsdUiPlugin.Util.log(ex);
    // } // endtry
    //        
    // return facet;
    // }

    public static XSDConstrainingFacet createFacet( String name ) {
        return createFacet(name, false);
    }

    public static XSDConstrainingFacet createFacet( String name,
                                                    boolean inclusive ) {
        name = getRealFacetName(name, inclusive);
        return (XSDConstrainingFacet)XSDFactory.eINSTANCE.create((EClass)facetNametoEClass.get(name));
    }

    public static boolean needsNumeric( XSDSimpleTypeDefinition simpleType ) {
        // check main first:
        if (ModelerCore.getWorkspaceDatatypeManager().isNumeric(simpleType)) return true;

        // above only checks against decimal:
        XSDSimpleTypeDefinition base = simpleType.getBaseTypeDefinition();

        if (base != null) {
            String basename = base.getName();
            for (int i = 0; i < NUMERIC_TYPES.length; i++) {
                String numericTypeName = NUMERIC_TYPES[i];
                if (numericTypeName.equalsIgnoreCase(basename)) {
                    return true;
                } // endif
            } // endfor
        } // endif

        return false;
    }

    public static boolean needsBounds( XSDSimpleTypeDefinition simpleType ) {
        // check numeric first:
        if (ModelerCore.getWorkspaceDatatypeManager().isBounded(simpleType)) return true;
        if (needsNumeric(simpleType)) return true;

        // above only checks against decimal:
        XSDSimpleTypeDefinition base = simpleType.getBaseTypeDefinition();

        if (base != null) {
            String basename = base.getName();

            for (int i = 0; i < BOUNDS_TYPES.length; i++) {
                String boundsTypeName = BOUNDS_TYPES[i];
                if (boundsTypeName.equalsIgnoreCase(basename)) {
                    return true;
                } // endif
            } // endfor
        } // endif

        return false;
    }

    private static XSDSimpleTypeDefinition getDefiningEnterpriseType( XSDSimpleTypeDefinition simpleType ) {
        XSDSimpleTypeDefinition root = simpleType.getRootTypeDefinition();
        XSDSimpleTypeDefinition rv = root; // if nothing else is found, root will be right.

        while (simpleType != root) {
            if (ModelerCore.getWorkspaceDatatypeManager().isEnterpriseDatatype(simpleType)) {
                rv = simpleType;
                break;
            } // endif

            // try the parent:
            simpleType = simpleType.getBaseTypeDefinition();
        } // endwhile

        return rv;
    }

    /**
     * @param location the XSDSchema object to add this type to.
     * @param typeName the name to give the newly created type.
     * @param baseType (optional) if not null, use this type as the base type of the new type.
     * @return
     */
    public static XSDSimpleTypeDefinition createAtomicSimpleTypeDefinintion( XSDSchema location,
                                                                             String typeName,
                                                                             XSDSimpleTypeDefinition baseType ) {
        XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition)XSDFactory.eINSTANCE.create(XSDPackage.eINSTANCE.getXSDSimpleTypeDefinition());
        std.setName(typeName);
        // defect 18444 - make sure things are wrapped in transactions
        try {
            ModelerCore.getModelEditor().addValue(location, std, location.getContents());

            if (baseType != null) {
                ModelerCore.getDatatypeManager(std).setBasetypeDefinition(std, baseType);
            } else {
                XSDSimpleTypeDefinition defaultBaseType = getDefaultBaseType();
                ModelerCore.getDatatypeManager(std).setBasetypeDefinition(std, defaultBaseType);
            } // endif
        } catch (ModelerCoreException ex) {
            ModelerXsdUiConstants.Util.log(ex);
        } // endtry

        return std;
    }

    private static XSDSimpleTypeDefinition getDefaultBaseType() throws ModelerCoreException {
        return (XSDSimpleTypeDefinition)ModelerCore.getBuiltInTypesManager().findDatatype(DatatypeConstants.BuiltInNames.STRING);
    }

    public static String getFacetName( XSDConstrainingFacet facet ) {
        String facetName = facet.getFacetName();

        if (FACET_MAX_INCLUSIVE.equals(facetName) || FACET_MAX_EXCLUSIVE.equals(facetName)) {
            facetName = FAKE_FACET_MAXIMUM;
        } else if (FACET_MIN_INCLUSIVE.equals(facetName) || FACET_MIN_EXCLUSIVE.equals(facetName)) {
            facetName = FAKE_FACET_MINIMUM;
        } // endif
        return facetName;
    }

    public static boolean isSubtypeOf( XSDSimpleTypeDefinition startFrom,
                                       XSDSimpleTypeDefinition lookFor ) {
        XSDSimpleTypeDefinition current = startFrom;
        XSDSimpleTypeDefinition root = startFrom.getRootTypeDefinition();

        while (current != root) {
            if (current == lookFor) {
                return true;
            } // endif
            current = current.getBaseTypeDefinition();
        } // endwhile

        return false;
    }

    // public static List findSubtypes(XSDTypeDefinition type) throws ModelerCoreException {
    // return Arrays.asList(ModelerCore.getWorkspaceDatatypeManager().getSubtypes(type));
    // }

    // public static List findUses(IProgressMonitor monitor, XSDTypeDefinition type) throws CoreException {
    // MetadataSearch search = ModelerCore.createMetadataSearch();
    // search.setDatatype(type, false);
    // search.execute(monitor);
    //
    // List searchResults = search.getResults();
    // List rv = new ArrayList(searchResults.size());
    //        
    // Iterator itor = searchResults.iterator();
    // while (itor.hasNext()) {
    // TypedObjectRecord searchRecord = (TypedObjectRecord) itor.next();
    // EObject eoj = ModelerCore.getModelContainer().getEObject(URI.createURI(searchRecord.getObjectURI()), true);
    // rv.add(eoj);
    // } // endwhile
    //        
    // return rv;
    // }

    // public static void nullReferencesTo(XSDSimpleTypeDefinition type, List uses) {
    // XSDSimpleTypeDefinition newBaseType = type.getBaseTypeDefinition();
    // Iterator itor = uses.iterator();
    // while (itor.hasNext()) {
    // Object element = itor.next();
    // if (element instanceof XSDSimpleTypeDefinition) {
    // // a type, assume it is superclass relation:
    // XSDSimpleTypeDefinition typeToChange = (XSDSimpleTypeDefinition) element;
    // ModelerCore.getWorkspaceDatatypeManager().setBasetypeDefinition(typeToChange, newBaseType);
    // // } else {
    // // System.out.println(" skipping "+element);
    // } //endif
    // } // endwhile
    // }
}
