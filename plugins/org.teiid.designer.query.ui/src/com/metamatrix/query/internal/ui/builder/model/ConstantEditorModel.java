/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import java.lang.reflect.Constructor;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import org.eclipse.core.runtime.IStatus;
import org.teiid.core.types.DataTypeManager.DefaultDataTypes;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.internal.ui.builder.util.BuilderUtils;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.Constant;

/**
 * The <code>ConstantEditorModel</code> class is used as a model for the
 * {@link com.metamatrix.modeler.transformation.ui.builder.expression.ConstantEditor}.
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
        setDefaults();
        type = DEFAULT_TYPE;
        conversionType = false;
        super.clear();
    }

    /**
     * Gets the <code>boolean</code> value.
     * 
     * @return the value
     * @throws com.metamatrix.core.util.AssertionError if type is not boolean #see BuilderUtils#BOOLEAN_TYPES
     */
    public boolean getBoolean() {
        if (!isBoolean()) {
            CoreArgCheck.isTrue(isBoolean(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
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
        return (Constant)getLanguageObject();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#getLanguageObject()
     */
    @Override
    public LanguageObject getLanguageObject() {
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
        return new Constant(value);
    }

    /**
     * Gets the <code>Date</code> value.
     * 
     * @return the value
     * @throws com.metamatrix.core.util.AssertionError if type is not date #see BuilderUtils#DATE_TYPES
     */
    public Date getDate() {
        if (!isDate()) {
            CoreArgCheck.isTrue(isDate(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                         new Object[] {"getDate()", type})); //$NON-NLS-1$
        }
        return dateValue;
    }

    public static String getDefaultType() {
        return DEFAULT_TYPE;
    }

    /**
     * Gets the <code>String</code> value.
     * 
     * @return the value
     * @throws com.metamatrix.core.util.AssertionError if type is not String #see BuilderUtils#STRING_TYPES
     */
    public String getText() {
        if (!isText()) {
            CoreArgCheck.isTrue(isText(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                         new Object[] {"getText()", type})); //$NON-NLS-1$
        }
        return textValue;
    }

    public static int getTextLimit( String theTextType ) {
        return BuilderUtils.getTextLimit(theTextType);
    }

    /**
     * Gets the <code>Time</code> value.
     * 
     * @return the value
     * @throws com.metamatrix.core.util.AssertionError if type is not time #see BuilderUtils#DATE_TYPES
     */
    public Time getTime() {
        CoreArgCheck.isTrue(isTime(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
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
        if (!isTimestamp()) {
            CoreArgCheck.isTrue(isTimestamp(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
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
        return type;
    }

    public boolean isBoolean() {
        return !isConversionType() && BuilderUtils.isBooleanType(type);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#isComplete()
     */
    @Override
    public boolean isComplete() {
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
        return result;
    }

    public boolean isConversionType() {
        return conversionType;
    }

    public boolean isDate() {
        return !isConversionType() && BuilderUtils.isDateType(type);
    }

    public boolean isNull() {
        return !isConversionType() && BuilderUtils.isNullType(type);
    }

    public boolean isText() {
        return !isConversionType() && BuilderUtils.isStringType(type);
    }

    public boolean isTime() {
        return !isConversionType() && BuilderUtils.isTimeType(type);
    }

    public boolean isTimestamp() {
        return !isConversionType() && BuilderUtils.isTimestampType(type);
    }

    public boolean isValid() {
        return (isText()) ? isValidValue(textValue) : true;
    }

    public boolean isValidValue( String theString ) {
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
        if (!isBoolean()) {
            CoreArgCheck.isTrue(isBoolean(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                            new Object[] {"setBoolean()", type})); //$NON-NLS-1$
        }

        if ((booleanValue == null) || (getBoolean() != theFlag)) {
            booleanValue = new Boolean(theFlag);
            fireModelChanged(BOOLEAN);
        }
    }

    private void setConstant( Constant theConstant ) {
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
        fireModelChanged(LanguageObjectEditorModelEvent.SAVED);
    }

    public void setDate( Date theDate ) {
        if (!isDate()) {
            CoreArgCheck.isTrue(isDate(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                         new Object[] {"setDate()", type})); //$NON-NLS-1$
        }

        if ((dateValue == null) || !dateValue.equals(theDate)) {
            dateValue = theDate;
            fireModelChanged(DATE);
        }
    }

    private void setDefaults() {
        booleanValue = new Boolean(true);
        dateValue = new Date(Calendar.getInstance().getTime().getTime());
        textValue = ""; //$NON-NLS-1$
        timeValue = new Time(Calendar.getInstance().getTime().getTime());
        timestampValue = new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#setLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLangObj ) {
        super.setLanguageObject(theLangObj);
        setConstant((Constant)theLangObj);
    }

    public void setNull() {
        if (!isNull()) {
            CoreArgCheck.isTrue(isNull(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                         new Object[] {"setNull()", type})); //$NON-NLS-1$
        }

        // does nothing since only the type we care about
    }

    public void setText( String theText ) {
        if (!isText()) {
            CoreArgCheck.isTrue(isText(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                         new Object[] {"setText()", type})); //$NON-NLS-1$
        }

        if ((textValue == null) || !getText().equals(theText)) {
            textValue = theText;
            fireModelChanged(TEXT);
        }
    }

    public void setTime( Time theTime ) {
        if (!isTime()) {
            CoreArgCheck.isTrue(isTime(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                         new Object[] {"setTime()", type})); //$NON-NLS-1$
        }

        if ((timeValue == null) || !timeValue.equals(theTime)) {
            timeValue = theTime;
            fireModelChanged(TIME);
        }
    }

    public void setTimestamp( Timestamp theTimestamp ) {
        if (!isTimestamp()) {
            CoreArgCheck.isTrue(isTimestamp(), Util.getString(PREFIX + "invalidRequest", //$NON-NLS-1$
                                                              new Object[] {"setTimestamp()", type})); //$NON-NLS-1$
        }

        if ((timestampValue == null) || !timestampValue.equals(theTimestamp)) {
            timestampValue = theTimestamp;
            fireModelChanged(TIMESTAMP);
        }
    }

    public void setType( String theType ) {
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
            fireModelChanged(TYPE);
        }
    }
}
