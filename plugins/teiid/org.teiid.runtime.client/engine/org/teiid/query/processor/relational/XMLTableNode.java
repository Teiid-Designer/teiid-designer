/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.processor.relational;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.source.XMLSystemFunctions;
import org.teiid.query.util.CommandContext;
import net.sf.saxon.Configuration;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.ConversionResult;
import net.sf.saxon.type.Converter;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.CalendarValue;
import net.sf.saxon.value.StringValue;

/**
 * Cutdown version of original XMLTableNode
 */
@Since(Version.TEIID_8_10)
public class XMLTableNode {

    private static Map<Class<?>, BuiltInAtomicType> typeMapping = new HashMap<Class<?>, BuiltInAtomicType>();
    
    static {
        typeMapping.put(DefaultDataTypes.TIMESTAMP.getTypeClass(), BuiltInAtomicType.DATE_TIME);
        typeMapping.put(DefaultDataTypes.TIME.getTypeClass(), BuiltInAtomicType.TIME);
        typeMapping.put(DefaultDataTypes.DATE.getTypeClass(), BuiltInAtomicType.DATE);
        typeMapping.put(DefaultDataTypes.FLOAT.getTypeClass(), BuiltInAtomicType.FLOAT);
        typeMapping.put(DefaultDataTypes.DOUBLE.getTypeClass(), BuiltInAtomicType.DOUBLE);
        typeMapping.put(DefaultDataTypes.BLOB.getTypeClass(), BuiltInAtomicType.HEX_BINARY);
        typeMapping.put(DefaultDataTypes.VARBINARY.getTypeClass(), BuiltInAtomicType.HEX_BINARY);
    }

    /**
     * @param value
     * @param context
     * @return value
     * @throws XPathException
     */
    public static Object getValue(AtomicValue value, CommandContext context) throws XPathException {
        if (value instanceof CalendarValue) {
            CalendarValue cv = (CalendarValue)value;
            if (!cv.hasTimezone()) {
                TimeZone tz = context.getServerTimeZone();
                int tzMin = tz.getRawOffset()/60000;
                if (tz.getDSTSavings() > 0) {
                    tzMin = tz.getOffset(cv.getCalendar().getTimeInMillis())/60000;
                }
                cv.setTimezoneInMinutes(tzMin);
                Calendar cal = cv.getCalendar();
                return new Timestamp(cal.getTime().getTime());
            }
        }
        return SequenceTool.convertToJava(value);
    }

    /**
     * @param type
     * @param colItem
     * @param config
     * @param context
     * @return value
     * @throws Exception
     */
    @Since(Version.TEIID_8_10)
    public static Object getValue(Class<?> type, Item colItem, Configuration config, CommandContext context)
        throws Exception {
        Object value = colItem;
        if (value instanceof AtomicValue) {
            value = getValue((AtomicValue)colItem, context);
        } else if (value instanceof Item) {
            Item i = (Item)value;
            if (XMLSystemFunctions.isNull(i)) {
                return null;
            }
            BuiltInAtomicType bat = typeMapping.get(type);
            if (bat != null) {
                AtomicValue av = new StringValue(i.getStringValueCS());
                ConversionResult cr = Converter.convert(av, bat, config.getConversionRules());
                value = cr.asAtomic();
                value = getValue((AtomicValue)value, context);
                if (value instanceof Item) {
                    value = ((Item)value).getStringValue();
                }
            } else {
                value = i.getStringValue();
            }
        }
        return FunctionDescriptor.importValue(context.getTeiidVersion(), value, type);
    }
}
