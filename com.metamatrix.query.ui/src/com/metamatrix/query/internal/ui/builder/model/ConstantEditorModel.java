/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.query.internal.ui.builder.model;

import java.lang.reflect.Constructor;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.common.types.DataTypeManager.DefaultDataTypes;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.util.BuilderUtils;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.symbol.Constant;

/**
 * The <code>ConstantEditorModel</code> class is used as a model for the
 * {@link com.metamatrix.query.internal.ui.builder.expression.ConstantEditor}.
 */
public class ConstantEditorModel extends AbstractLanguageObjectEditorModel implements BuilderUtils.LoggingConstants {

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ConstantEditorModel.class);

    /** The default type for constants. */
    private static final String DEFAULT_TYPE = DefaultDataTypes.STRING;

    // event types
    public static final String TYPE = "TYPE"; //$NON-NLS-1$
    public static final String BOOLEAN = "BOOLEAN"; //$NON-NLS-1$
    public static final String DATE = "DATE"; //$NON-NLS-1$
    public static final String NULL = "NULL"; //$NON-NLS-1$
    public static final String TEXT = "TEXT"; //$NON-NLS-1$
    public static final String TIME = "TIME"; //$NON-NLS-1$
    public static final String TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$

    private Boolean booleanValue = null;
    private Date dateValue = null;
    private String textValue = null;
    private Time timeValue = null;
    private Timestamp timestampValue = null;

    private boolean conversionType = false;
    private String type = DEFAULT_TYPE;

    public ConstantEditorModel() {
        super(Constant.class);
        setDefaults();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#clear()
     */
    @Override
    public void clear() {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "clear()"); //$NON-NLS-1$
        }

        setDefaults();
        type = DEFAULT_TYPE;
        conversionType = false;
        super.clear();

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "clear()"); //$NON-NLS-1$
        }
    }

    /**
     * Gets the <code>boolean</code> value.
     * 
     * @return the value
     * @throws com.metamatrix.core.util.AssertionError if type is not boolean #see BuilderUtils#BOOLEAN_TYPES
     */
    public boolean getBoolean() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "getBoolean():value=" + booleanValue); //$NON-NLS-1$
        }

        if (!isBoolean()) {
            Assertion.assertTrue(isBoolean(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                             new Object[] {"getBoolean()", type})); //$NON-NLS-1$
        }

        return booleanValue.booleanValue();
    }

    /**
     * Gets the current value.
     * 
     * @return the current <code>Constant</code>
     * @throws IllegalStateException if the current value is not complete
     */
    public Constant getConstant() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "getConstant()"); //$NON-NLS-1$
        }

        return (Constant)getLanguageObject();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#getLanguageObject()
     */
    @Override
    public LanguageObject getLanguageObject() {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "getLanguageObject()"); //$NON-NLS-1$
        }

        // return null if not complete or valid
        if (!isComplete() || !isValid()) {
            return null;
        }

        Object value = null;

        if (conversionType) {
            value = type;
        } else {
            if (isText()) {
                Class typeClass = BuilderUtils.getTypeClass(type);

                if (Number.class.isAssignableFrom(typeClass)) {
                    try {
                        Constructor constructor = typeClass.getConstructor(new Class[] {String.class});
                        value = constructor.newInstance(new Object[] {textValue});
                    } catch (Exception theException) {
                        Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "invalidTextValue", //$NON-NLS-1$);
                                                                             new Object[] {textValue, typeClass}));
                    }
                } else if (typeClass == Character.class) {
                    value = new Character(textValue.charAt(0));
                } else {
                    // if both a leading and trailing single quote exist (and not same quote),
                    // delete them both interior quotes are escaped automatically by the Constant class
                    String temp = textValue;
                    StringBuffer sb = new StringBuffer(temp);

                    if (temp.startsWith("'") && temp.endsWith("'") && (temp.length() > 1)) { //$NON-NLS-1$ //$NON-NLS-2$
                        sb.deleteCharAt(sb.length() - 1);
                        sb.deleteCharAt(0);
                    }

                    value = sb.toString();
                }
            } else if (isDate()) {
                value = getDate();
            } else if (isTimestamp()) {
                value = getTimestamp();
            } else if (isTime()) {
                value = getTime();
            } else if (isBoolean()) {
                value = booleanValue;
            }
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "getLanguageObject():value=" + value); //$NON-NLS-1$
        }

        return new Constant(value);
    }

    /**
     * Gets the <code>Date</code> value.
     * 
     * @return the value
     * @throws com.metamatrix.core.util.AssertionError if type is not date #see BuilderUtils#DATE_TYPES
     */
    public Date getDate() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "getDate():value=" + dateValue); //$NON-NLS-1$
        }

        if (!isDate()) {
            Assertion.assertTrue(isDate(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                          new Object[] {"getDate()", type})); //$NON-NLS-1$
        }
        return dateValue;
    }

    public static String getDefaultType() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(ConstantEditorModel.class, "getDefaultType():value=" + DEFAULT_TYPE); //$NON-NLS-1$
        }

        return DEFAULT_TYPE;
    }

    /**
     * Gets the <code>String</code> value.
     * 
     * @return the value
     * @throws com.metamatrix.core.util.AssertionError if type is not String #see BuilderUtils#STRING_TYPES
     */
    public String getText() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "getText():value=" + textValue); //$NON-NLS-1$
        }

        if (!isText()) {
            Assertion.assertTrue(isText(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                          new Object[] {"getText()", type})); //$NON-NLS-1$
        }
        return textValue;
    }

    public static int getTextLimit( String theTextType ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(ConstantEditorModel.class, "getTextLimit():type=" + theTextType); //$NON-NLS-1$
        }

        return BuilderUtils.getTextLimit(theTextType);
    }

    /**
     * Gets the <code>Time</code> value.
     * 
     * @return the value
     * @throws com.metamatrix.core.util.AssertionError if type is not time #see BuilderUtils#DATE_TYPES
     */
    public Time getTime() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "getTime():value=" + timeValue); //$NON-NLS-1$
        }

        Assertion.assertTrue(isTime(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                      new Object[] {"getTime()", type})); //$NON-NLS-1$

        // zero out the seconds and milliseconds
        if (timeValue != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(timeValue);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            timeValue = new Time(date.getTime().getTime());
        }

        return timeValue;
    }

    /**
     * Gets the <code>Time</code> value.
     * 
     * @return the value
     * @throws com.metamatrix.core.util.AssertionError if type is not timestamp #see BuilderUtils#DATE_TYPES
     */
    public Timestamp getTimestamp() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "getTimestamp():value=" + timestampValue); //$NON-NLS-1$
        }

        if (!isTimestamp()) {
            Assertion.assertTrue(isTimestamp(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                               new Object[] {"getTimestamp()", type})); //$NON-NLS-1$
        }

        // zero out the seconds and milliseconds
        if (timestampValue != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(timestampValue);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            timestampValue = new Timestamp(date.getTime().getTime());
        }

        return timestampValue;
    }

    public String getType() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "getType():value=" + type); //$NON-NLS-1$
        }

        return type;
    }

    public boolean isBoolean() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "isBoolean():type=" + type); //$NON-NLS-1$
        }

        return !isConversionType() && BuilderUtils.isBooleanType(type);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#isComplete()
     */
    @Override
    public boolean isComplete() {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "isComplete()"); //$NON-NLS-1$
        }

        boolean result = false;

        if (conversionType) {
            result = true;
        } else {
            if (isText()) {
                result = (textValue != null) && (textValue.length() != 0);
            } else if (isDate()) {
                result = (dateValue != null);
            } else if (isTimestamp()) {
                result = (timestampValue != null);
            } else if (isTime()) {
                result = (timeValue != null);
            } else if (isBoolean()) {
                result = (booleanValue != null);
            } else if (isNull()) {
                result = true;
            }
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "isComplete():result=" + result); //$NON-NLS-1$
        }

        return result;
    }

    public boolean isConversionType() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "isConversionType():value=" + conversionType); //$NON-NLS-1$
        }

        return conversionType;
    }

    public boolean isDate() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "isDate():type=" + type); //$NON-NLS-1$
        }

        return !isConversionType() && BuilderUtils.isDateType(type);
    }

    public boolean isNull() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "isNull():type=" + type); //$NON-NLS-1$
        }

        return !isConversionType() && BuilderUtils.isNullType(type);
    }

    public boolean isText() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "isText():type=" + type); //$NON-NLS-1$
        }

        return !isConversionType() && BuilderUtils.isStringType(type);
    }

    public boolean isTime() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "isTime():type=" + type); //$NON-NLS-1$
        }

        return !isConversionType() && BuilderUtils.isTimeType(type);
    }

    public boolean isTimestamp() {
        if (BuilderUtils.isTraceLogging()) {
            Util.print(this, "isTimestamp():type=" + type); //$NON-NLS-1$
        }

        return !isConversionType() && BuilderUtils.isTimestampType(type);
    }

    public boolean isValid() {
        return (isText()) ? isValidValue(textValue) : true;
    }

    public boolean isValidValue( String theString ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "isValidValue():theString=" + theString); //$NON-NLS-1$
        }

        boolean result = true;

        // could only not be valid if text type or number type
        Class typeClass = BuilderUtils.getTypeClass(type);

        if (Number.class.isAssignableFrom(typeClass)) {
            try {
                Constructor constructor = typeClass.getConstructor(new Class[] {String.class});
                constructor.newInstance(new Object[] {theString});
            } catch (Exception theException) {
                // this will catch values too big or too small
                result = false;
            }
        } else if (isText() && (textValue.length() <= getTextLimit(type))) {
            // check to see if all valid chars
            String validChars = BuilderUtils.getValidChars(type);

            if (validChars != null) {
                for (int length = textValue.length(), i = 0; i < length; i++) {
                    if (validChars.indexOf(textValue.charAt(i)) == -1) {
                        result = false;
                        break;
                    }
                }
            }
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "isValidValue():result=" + result); //$NON-NLS-1$
        }
        return result;
    }

    public String paramString() {
        return new StringBuffer().append("type=").append(type) //$NON-NLS-1$
        .append(", conversionType=").append(conversionType) //$NON-NLS-1$
        .append(", booleanValue=").append(booleanValue) //$NON-NLS-1$
        .append(", dateValue=").append(dateValue) //$NON-NLS-1$
        .append(", textValue=").append(textValue) //$NON-NLS-1$
        .append(", timeValue=").append(timeValue) //$NON-NLS-1$
        .append(", timestampValue=").append(timestampValue) //$NON-NLS-1$
        .toString();
    }

    public void setBoolean( boolean theFlag ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setBoolean():flag=" + theFlag); //$NON-NLS-1$
        }

        if (!isBoolean()) {
            Assertion.assertTrue(isBoolean(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                             new Object[] {"setBoolean()", type})); //$NON-NLS-1$
        }

        if ((booleanValue == null) || (getBoolean() != theFlag)) {
            booleanValue = new Boolean(theFlag);

            if (BuilderUtils.isEventLogging()) {
                Util.print(this, "setBoolean:fireModelChanged:type=" + BOOLEAN); //$NON-NLS-1$
            }

            fireModelChanged(BOOLEAN);
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setBoolean():value=" + booleanValue); //$NON-NLS-1$
        }
    }

    private void setConstant( Constant theConstant ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setConstant():value=" + theConstant); //$NON-NLS-1$
        }

        // return if current value is the same
        if (((theConstant == null) && (getConstant() == null))
            || ((theConstant != null) && (getConstant() != null) && theConstant.equals(getConstant()))) {
            return;
        }

        notifyListeners = false;

        if (theConstant == null || theConstant.getValue() == null) {
            clear();
        } else {
            conversionType = BuilderUtils.isConversionType(theConstant);

            // for conversion types the type is actually the value
            type = (conversionType) ? (String)theConstant.getValue() : BuilderUtils.getType(theConstant);
            setDefaults();

            if (!conversionType) {
                if (isText()) {
                    setText(theConstant.getValue().toString());
                } else if (isBoolean()) {
                    Boolean value = (Boolean)theConstant.getValue();
                    setBoolean(value.booleanValue());
                } else if (isNull()) {
                    setNull();
                } else if (isDate()) {
                    Date value = (Date)theConstant.getValue();
                    setDate(value);
                } else if (isTimestamp()) {
                    Timestamp value = (Timestamp)theConstant.getValue();
                    setTimestamp(value);
                } else if (isTime()) {
                    Time value = (Time)theConstant.getValue();
                    setTime(value);
                } else {
                    type = DEFAULT_TYPE;
                    setText(theConstant.getValue().toString());
                }
            }
        }

        notifyListeners = true;

        if (BuilderUtils.isEventLogging()) {
            Util.print(this, "setConstant:fireModelChanged:type=" + LanguageObjectEditorModelEvent.SAVED); //$NON-NLS-1$
        }

        fireModelChanged(LanguageObjectEditorModelEvent.SAVED);

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setConstant()"); //$NON-NLS-1$
        }
    }

    public void setDate( Date theDate ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setDate()value=" + theDate); //$NON-NLS-1$
        }

        if (!isDate()) {
            Assertion.assertTrue(isDate(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                          new Object[] {"setDate()", type})); //$NON-NLS-1$
        }

        if ((dateValue == null) || !dateValue.equals(theDate)) {
            dateValue = theDate;

            if (BuilderUtils.isEventLogging()) {
                Util.print(this, "setDate:fireModelChanged:type=" + DATE); //$NON-NLS-1$
            }

            fireModelChanged(DATE);
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setDate()"); //$NON-NLS-1$
        }
    }

    private void setDefaults() {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setDefaults()"); //$NON-NLS-1$
        }

        booleanValue = new Boolean(true);
        dateValue = new Date(Calendar.getInstance().getTime().getTime());
        textValue = ""; //$NON-NLS-1$
        timeValue = new Time(Calendar.getInstance().getTime().getTime());
        timestampValue = new Timestamp(Calendar.getInstance().getTime().getTime());

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setDefaults()"); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#setLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLangObj ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setLanguageObject():value=" + theLangObj); //$NON-NLS-1$
        }

        super.setLanguageObject(theLangObj);
        setConstant((Constant)theLangObj);

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setLanguageObject()"); //$NON-NLS-1$
        }
    }

    public void setNull() {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setNull()"); //$NON-NLS-1$
        }

        if (!isNull()) {
            Assertion.assertTrue(isNull(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                          new Object[] {"setNull()", type})); //$NON-NLS-1$
        }

        // does nothing since only the type we care about

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setNull()"); //$NON-NLS-1$
        }
    }

    public void setText( String theText ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setText():value=" + theText); //$NON-NLS-1$
        }

        if (!isText()) {
            Assertion.assertTrue(isText(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                          new Object[] {"setText()", type})); //$NON-NLS-1$
        }

        if ((textValue == null) || !getText().equals(theText)) {
            textValue = theText;

            if (BuilderUtils.isEventLogging()) {
                Util.print(this, "setText:fireModelChanged:type=" + TEXT); //$NON-NLS-1$
            }

            fireModelChanged(TEXT);
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setText()"); //$NON-NLS-1$
        }
    }

    public void setTime( Time theTime ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setTime():value=" + theTime); //$NON-NLS-1$
        }

        if (!isTime()) {
            Assertion.assertTrue(isTime(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                          new Object[] {"setTime()", type})); //$NON-NLS-1$
        }

        if ((timeValue == null) || !timeValue.equals(theTime)) {
            timeValue = theTime;

            if (BuilderUtils.isEventLogging()) {
                Util.print(this, "setTime:fireModelChanged:type=" + TIME); //$NON-NLS-1$
            }

            fireModelChanged(TIME);
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setTime()"); //$NON-NLS-1$
        }
    }

    public void setTimestamp( Timestamp theTimestamp ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setTimestamp():value=" + theTimestamp); //$NON-NLS-1$
        }

        if (!isTimestamp()) {
            Assertion.assertTrue(isTimestamp(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                               new Object[] {"setTimestamp()", type})); //$NON-NLS-1$
        }

        if ((timestampValue == null) || !timestampValue.equals(theTimestamp)) {
            timestampValue = theTimestamp;

            if (BuilderUtils.isEventLogging()) {
                Util.print(this, "setTimestamp:fireModelChanged:type=" + TIMESTAMP); //$NON-NLS-1$
            }

            fireModelChanged(TIMESTAMP);
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setTimestamp()"); //$NON-NLS-1$
        }
    }

    public void setType( String theType ) {
        if (BuilderUtils.isTraceLogging()) {
            Util.printEntered(this, "setType():value=" + theType); //$NON-NLS-1$
        }

        boolean changed = false;

        if (BuilderUtils.isValidType(theType)) {
            if (!type.equals(theType)) {
                type = theType;
                changed = true;
            }
        } else {
            type = DEFAULT_TYPE;
            changed = true;
        }

        if (changed) {
            setDefaults();

            if (BuilderUtils.isEventLogging()) {
                Util.print(this, "setType:fireModelChanged:type=" + TYPE); //$NON-NLS-1$
            }

            fireModelChanged(TYPE);
        }

        if (BuilderUtils.isTraceLogging()) {
            Util.printExited(this, "setType()"); //$NON-NLS-1$
        }
    }
}
