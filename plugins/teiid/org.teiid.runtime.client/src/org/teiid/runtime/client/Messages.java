/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 */
public class Messages {

    private static final String BUNDLE_NAME = "org.teiid.runtime.client.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private static final String DOT = "."; //$NON-NLS-1$

    @SuppressWarnings( "javadoc" )
    public enum ArgCheck {
        isNonNegativeInt,
        isNonPositiveInt,
        isNegativeInt,
        isPositiveInt,
        isStringNonZeroLength,
        isNonNull,
        isNull,
        isInstanceOf,
        isCollectionNotEmpty,
        isMapNotEmpty,
        isArrayNotEmpty,
        isNotSame,
        contains,
        containsKey;

        @Override
        public String toString() {
            return getEnumName(this) + DOT + name();
        }
    }

    @SuppressWarnings( "javadoc" )
    public enum ProcedureService {
        procedureServiceTextTableSqlTemplate,
        procedureServiceTextInvokeHttpTableSqlTemplate,
        procedureServiceXmlGetTextFilesTableSqlTemplate,
        procedureServiceXmlInvokeHttpTableSqlTemplate;

        @Override
        public String toString() {
            return getEnumName(this) + DOT +  name();
        }
    }

    @SuppressWarnings( "javadoc" )
    public enum MMClob {
        MMBlob_0,
        MMBlob_1,
        MMBlob_2,
        MMBlob_3;

        @Override
        public String toString() {
            // Cannot use dots in enums
            return getEnumName(this) + DOT + name().replaceAll("_", DOT); //$NON-NLS-1$
        }
    }

    @SuppressWarnings( "javadoc" )
    public enum TeiidParser {
        Unknown_join_type,
        Aggregate_only_top_level,
        window_only_top_level,
        Unknown_agg_func,
        Invalid_func,
        Integer_parse,
        Float_parse,
        decimal_parse,
        Invalid_id,
        Invalid_alias,
        Invalid_short_name,
        invalid_window,
        function_def,
        view_def,
        pk_exists,
        no_column,
        function_return,
        function_in,
        alter_table_doesnot_exist,
        alter_procedure_doesnot_exist,
        alter_procedure_param_doesnot_exist,
        alter_function_param_doesnot_exist,
        alter_table_param,
        char_val,
        non_position_constant,
        expected_non_reserved,
        lexicalError,
        noParserForVersion,
        invalidNodeType;
        
        @Override
        public String toString() {
            return getEnumName(this) + DOT + name();
        }
    }

    @SuppressWarnings( "javadoc" )
    public enum InvalidPropertyException {
        message;

        @Override
        public String toString() {
            return getEnumName(this) + DOT + name();
        }
    }

    @SuppressWarnings( "javadoc" )
    public enum Mapping {
        unknown_node_type,
        invalid_criteria_node,
        noCriteria,
        invalidName;

        @Override
        public String toString() {
            return getEnumName(this) + DOT + name();
        }
    }

    @SuppressWarnings( "javadoc" )
    public enum ERR {
        ERR_003_029_0002,
        ERR_003_029_0003,
        ERR_015_001_0005,
        ERR_015_001_0044,
        ERR_015_001_0066,
        ERR_015_001_0069,
        ERR_015_002_0009,
        ERR_015_002_0010,
        ERR_015_002_0011,
        ERR_015_004_0010,
        ERR_015_004_0036,
        ERR_015_006_0001,
        ERR_015_006_0034,
        ERR_015_006_0042,
        ERR_015_006_0048,
        ERR_015_006_0049,
        ERR_015_006_0051,
        ERR_015_006_0054,
        ERR_015_008_0022,
        ERR_015_008_0025,
        ERR_015_008_0046,
        ERR_015_008_0047,
        ERR_015_008_0049,
        ERR_015_008_0055,
        ERR_015_008_0056,
        ERR_015_009_0002,
        ERR_015_010_0001,
        ERR_015_010_0002,
        ERR_015_010_0003,
        ERR_015_010_0006,
        ERR_015_010_0009,
        ERR_015_010_0010,
        ERR_015_010_0011,
        ERR_015_010_0014,
        ERR_015_010_0015,
        ERR_015_010_0016,
        ERR_015_010_0017,
        ERR_015_010_0018,
        ERR_015_010_0021,
        ERR_015_010_0022,
        ERR_015_010_0023,
        ERR_015_010_0029,
        ERR_015_010_0031,
        ERR_015_010_0032,
        ERR_015_010_0035,
        ERR_015_010_0036,
        ERR_015_010_0037,
        ERR_015_010_0038,
        ERR_015_010_0039,
        ERR_015_012_0001,
        ERR_015_012_0002,
        ERR_015_012_0003,
        ERR_015_012_0004,
        ERR_015_012_0005,
        ERR_015_012_0006,
        ERR_015_012_0007,
        ERR_015_012_0008,
        ERR_015_012_0009,
        ERR_015_012_0010,
        ERR_015_012_0011,
        ERR_015_012_0013,
        ERR_015_012_0014,
        ERR_015_012_0015,
        ERR_015_012_0016,
        ERR_015_012_0017,
        ERR_015_012_0018,
        ERR_015_012_0024,
        ERR_015_012_0025,
        ERR_015_012_0026,
        ERR_015_012_0027,
        ERR_015_012_0029,
        ERR_015_012_0030,
        ERR_015_012_0033,
        ERR_015_012_0034,
        ERR_015_012_0037,
        ERR_015_012_0039,
        ERR_015_012_0041,
        ERR_015_012_0042,
        ERR_015_012_0052,
        ERR_015_012_0053,
        ERR_015_012_0055,
        ERR_015_012_0059,
        ERR_015_012_0060,
        ERR_015_012_0062,
        ERR_015_012_0063,
        ERR_015_012_0064,
        ERR_015_012_0067,
        ERR_015_012_0069,
        ERR_018_005_0095;
        
        @Override
        public String toString() {
            // Cannot use dots in enums
            return this.name().replaceAll("_", DOT); //$NON-NLS-1$
        }
    }

    @SuppressWarnings( "javadoc" )
    public enum TEIID {
        TEIID10030,
        TEIID10032,
        TEIID10052,
        TEIID10058,
        TEIID10059,
        TEIID10060,
        TEIID10061,
        TEIID10063,
        TEIID10068,
        TEIID10070,
        TEIID10071,
        TEIID10072,
        TEIID10073,
        TEIID10074,
        TEIID10076,
        TEIID10077,
        TEIID10078,
        TEIID10080,
        TEIID10081,
        TEIID10083,
        TEIID30001,
        TEIID30002,
        TEIID30003,
        TEIID30004,
        TEIID30005,
        TEIID30006,
        TEIID30008,
        TEIID30009,
        TEIID30011,
        TEIID30012,
        TEIID30013,
        TEIID30014,
        TEIID30015,
        TEIID30016,
        TEIID30017,
        TEIID30018,
        TEIID30019,
        TEIID30020,
        TEIID30021,
        TEIID30022,
        TEIID30023,
        TEIID30024,
        TEIID30025,
        TEIID30026,
        TEIID30027,
        TEIID30028,
        TEIID30029,
        TEIID30030,
        TEIID30031,
        TEIID30032,
        TEIID30033,
        TEIID30034,
        TEIID30035,
        TEIID30040,
        TEIID30041,
        TEIID30042,
        TEIID30045,
        TEIID30048,
        TEIID30059,
        TEIID30060,
        TEIID30061,
        TEIID30065,
        TEIID30066,
        TEIID30067,
        TEIID30068,
        TEIID30069,
        TEIID30070,
        TEIID30071,
        TEIID30072,
        TEIID30074,
        TEIID30075,
        TEIID30077,
        TEIID30079,
        TEIID30082,
        TEIID30083,
        TEIID30084,
        TEIID30085,
        TEIID30086,
        TEIID30087,
        TEIID30088,
        TEIID30089,
        TEIID30090,
        TEIID30091,
        TEIID30093,
        TEIID30094,
        TEIID30095,
        TEIID30096,
        TEIID30097,
        TEIID30098,
        TEIID30099,
        TEIID30100,
        TEIID30101,
        TEIID30102,
        TEIID30112,
        TEIID30114,
        TEIID30116,
        TEIID30117,
        TEIID30118,
        TEIID30121,
        TEIID30123,
        TEIID30124,
        TEIID30126,
        TEIID30127,
        TEIID30128,
        TEIID30129,
        TEIID30130,
        TEIID30131,
        TEIID30133,
        TEIID30134,
        TEIID30135,
        TEIID30136,
        TEIID30137,
        TEIID30138,
        TEIID30139,
        TEIID30140,
        TEIID30141,
        TEIID30143,
        TEIID30144,
        TEIID30145,
        TEIID30146,
        TEIID30147,
        TEIID30151,
        TEIID30152,
        TEIID30153,
        TEIID30154,
        TEIID30155,
        TEIID30156,
        TEIID30158,
        TEIID30160,
        TEIID30161,
        TEIID30164,
        TEIID30166,
        TEIID30168,
        TEIID30170,
        TEIID30171,
        TEIID30172,
        TEIID30174,
        TEIID30175,
        TEIID30176,
        TEIID30177,
        TEIID30178,
        TEIID30179,
        TEIID30181,
        TEIID30182,
        TEIID30183,
        TEIID30184,
        TEIID30190,
        TEIID30192,
        TEIID30193,
        TEIID30211,
        TEIID30212,
        TEIID30213,
        TEIID30216,
        TEIID30226,
        TEIID30227,
        TEIID30229,
        TEIID30230,
        TEIID30231,
        TEIID30232,
        TEIID30233,
        TEIID30236,
        TEIID30238,
        TEIID30239,
        TEIID30240,
        TEIID30241,
        TEIID30244,
        TEIID30250,
        TEIID30251,
        TEIID30253,
        TEIID30254,
        TEIID30258,
        TEIID30259,
        TEIID30263,
        TEIID30267,
        TEIID30268,
        TEIID30269,
        TEIID30270,
        TEIID30272,
        TEIID30275,
        TEIID30278,
        TEIID30281,
        TEIID30283,
        TEIID30287,
        TEIID30288,
        TEIID30295,
        TEIID30296,
        TEIID30297,
        TEIID30300,
        TEIID30301,
        TEIID30302,
        TEIID30303,
        TEIID30306,
        TEIID30307,
        TEIID30308,
        TEIID30309,
        TEIID30311,
        TEIID30312,
        TEIID30314,
        TEIID30323,
        TEIID30326,
        TEIID30328,
        TEIID30329,
        TEIID30333,
        TEIID30336,
        TEIID30341,
        TEIID30342,
        TEIID30345,
        TEIID30347,
        TEIID30350,
        TEIID30351,
        TEIID30358,
        TEIID30359,
        TEIID30363,
        TEIID30364,
        TEIID30372,
        TEIID30373,
        TEIID30375,
        TEIID30376,
        TEIID30377,
        TEIID30378,
        TEIID30382,
        TEIID30384,
        TEIID30385,
        TEIID30387,
        TEIID30388,
        TEIID30389,
        TEIID30390,
        TEIID30391,
        TEIID30392,
        TEIID30396,
        TEIID30398,
        TEIID30399,
        TEIID30400,
        TEIID30401,
        TEIID30402,
        TEIID30403,
        TEIID30404,
        TEIID30405,
        TEIID30406,
        TEIID30407,
        TEIID30409,
        TEIID30410,
        TEIID30411,
        TEIID30412,
        TEIID30413,
        TEIID30416,
        TEIID30424,
        TEIID30425,
        TEIID30427,
        TEIID30428,
        TEIID30429,
        TEIID30430,
        TEIID30431,
        TEIID30432,
        TEIID30434,
        TEIID30448,
        TEIID30449,
        TEIID30452,
        TEIID30457,
        TEIID30476,
        TEIID30477,
        TEIID30479,
        TEIID30481,
        TEIID30482,
        TEIID30489,
        TEIID30491,
        TEIID30495,
        TEIID30497,
        TEIID30498,
        TEIID30499,
        TEIID30505,
        TEIID30517,
        TEIID30518,
        TEIID30519,
        TEIID30520,
        TEIID30521,
        TEIID30522,
        TEIID30524,
        TEIID30525,
        TEIID30546,
        TEIID30548,
        TEIID30549,
        TEIID30554,
        TEIID30555,
        TEIID30561,
        TEIID30562,
        TEIID30563,
        TEIID30564,
        TEIID30565,
        TEIID30581,
        TEIID30590,
        TEIID30591,
        TEIID30600,
        TEIID30601,
        TEIID30602,
        TEIID31069,
        TEIID31070,
        TEIID31071,
        TEIID31072,
        TEIID31073,
        TEIID31075,
        TEIID31077,
        TEIID31078,
        TEIID31079,
        TEIID31080,
        TEIID31081,
        TEIID31082,
        TEIID31083,
        TEIID31084,
        TEIID31085,
        TEIID31086,
        TEIID31087,
        TEIID31088,
        TEIID31089,
        TEIID31090,
        TEIID31091,
        TEIID31092,
        TEIID31093,
        TEIID31094,
        TEIID31095,
        TEIID31096,
        TEIID31097,
        TEIID31099,
        TEIID31100,
        TEIID31101,
        TEIID31102,
        TEIID31103,
        TEIID31104,
        TEIID31105,
        TEIID31106,
        TEIID31107,
        TEIID31109,
        TEIID31110,
        TEIID31111,
        TEIID31112,
        TEIID31113,
        TEIID31114,
        TEIID31115,
        TEIID31116,
        TEIID31117,
        TEIID31118,
        TEIID31119,
        TEIID31120,
        TEIID31121,
        TEIID31122,
        TEIID31123,
        TEIID31124,
        TEIID31125,
        TEIID31126,
        TEIID31127,
        TEIID31128,
        TEIID31129,
        TEIID31130,
        TEIID31131,
        TEIID31132,
        TEIID31133,
        TEIID31134,
        TEIID31135,
        TEIID31136,
        TEIID31137,
        TEIID31138,
        TEIID31139,
        TEIID31140,
        TEIID31141,
        TEIID31142,
        TEIID31143,
        TEIID31144,
        TEIID31146;

        @Override
        public String toString() {
            return name();
        }
    }

    private static String getEnumName(Enum<?> enumValue) {
        String className = enumValue.getClass().getName();
        String[] components = className.split("\\$"); //$NON-NLS-1$
        return components[components.length - 1];
    }
    
    private Messages() {
    }

    /**
     * Get the message string for the given {@link TEIID}
     * key. This will output with the TEIID error number
     * prepended to the message in the same way as the
     * teiid client.
     *
     * @param key
     * @param parameters
     *
     * @return error message associated with key
     */
    public static String gs(TEIID key, final Object... parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(" "); //$NON-NLS-1$
        sb.append(getString(key, parameters));
        return sb.toString();
    }

    /**
     * Get message string
     *
     * @param key
     *
     * @return i18n string
     */
    private static String getString(Enum<?> key) {
        try {
            return RESOURCE_BUNDLE.getString(key.toString());
        } catch (final Exception err) {
            String msg;

            if (err instanceof NullPointerException) {
                msg = "<No message available>"; //$NON-NLS-1$
            } else if (err instanceof MissingResourceException) {
                msg = "<Missing message for key \"" + key + "\" in: " + BUNDLE_NAME + '>'; //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                msg = err.getLocalizedMessage();
            }

            return msg;
        }
    }

    /**
     * Get message string with parameters
     *
     * @param key
     * @param parameters
     *
     * @return i18n string
     */
    public static String getString(Enum<?> key, Object... parameters) {
        String text = getString(key);

        // Check the trivial cases ...
        if (text == null) {
            return '<' + key.toString() + '>';
        }
        if (parameters == null || parameters.length == 0) {
            return text;
        }

        return MessageFormat.format(text, parameters);
    }
}
