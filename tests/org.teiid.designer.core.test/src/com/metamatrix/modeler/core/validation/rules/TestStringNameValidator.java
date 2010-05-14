/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation.rules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.impl.CoreFactoryImpl;

/**
 * TestStringNameValidator
 */
public class TestStringNameValidator extends TestCase {

    public static final int INVALID_MINIUM = -1;
    public static final int INVALID_MAXIMUM = -1;
    public static final char REPLACEMENT_CHAR = 'Z';
    public static final char[] ILLEGAL_CHARS = new char[]{'a','b','c','d'};
    public static final char[] VALID_CHARS = new char[]{'f','g','h','i','1','2'};

    public static final String MODEL_IMPORT_NAME_PREFIX = "ModelImportX"; //$NON-NLS-1$

    public static final String NAME_SHORTER_THAN_DEFAULT = buildString(0);
    public static final String NAME_LONGER_THAN_DEFAULT = buildString(StringNameValidator.DEFAULT_MAXIMUM_LENGTH + 3);
    public static final String NAME_WITH_GOOD_DEFAULT_LENGTH = buildString(10);
    public static final String NAME_WITH_INVALID_FIRST_CHARACTER           = "4This string has invalid$%^ characters"; //$NON-NLS-1$
    public static final String NAME_WITH_INVALID_FIRST_CHARACTER_CORRECTED = "This_string_has_invalid____characters"; //$NON-NLS-1$
    public static final String NAME_WITH_INVALID_CHARACTERS           = "This string has invalid$%^ characters"; //$NON-NLS-1$
    public static final String NAME_WITH_INVALID_CHARACTERS_CORRECTED = "This_string_has_invalid____characters"; //$NON-NLS-1$
    public static final String NAME_WITH_VALID_CHARACTERS = "abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789_"; //$NON-NLS-1$

    public static String buildString( final int length ) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            sb.append('c');
        }
        return sb.toString();
    }

    private StringNameValidator defaultValidator;
    private StringNameValidator invalidCharValidator;
    private List siblings;
    private ModelImport eObject;
    private final int nameFeatureId = CorePackage.MODEL_IMPORT__NAME;
    private final CoreFactory factory = new CoreFactoryImpl();

    /**
     * Constructor for TestStringNameValidator.
     * @param name
     */
    public TestStringNameValidator(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.defaultValidator = new StringNameValidator();
        this.invalidCharValidator = new StringNameValidator(ILLEGAL_CHARS);

        // Create the model objects ...
        this.siblings = new ArrayList();
        final ModelAnnotation annotation = factory.createModelAnnotation();
        for (int i = 0; i < 10; ++i) {
            final ModelImport modelImport = factory.createModelImport();
            modelImport.setModel(annotation);
            if ( i==0 ) {
                eObject = modelImport;
                modelImport.setName(MODEL_IMPORT_NAME_PREFIX);
            } else {
                this.siblings.add(modelImport);
                modelImport.setName(MODEL_IMPORT_NAME_PREFIX + i);
            }
        }

    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestStringNameValidator"); //$NON-NLS-1$
        suite.addTestSuite(TestStringNameValidator.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            }
            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================

    protected void helpCheckValidator(final StringNameValidator v, int min, int max, boolean caseSensitive,
                                      char replacementChar, char[] illegalChars) {
        if ( min < 0 ) {
            min = StringNameValidator.DEFAULT_MINIMUM_LENGTH;
        }
        if ( max < 0 ) {
            max = StringNameValidator.MAXIMUM_LENGTH;
        }
        assertEquals(min, v.getMinimumLength());
		assertEquals(max, v.getMaximumLength());
		assertEquals(caseSensitive, v.isCaseSensitive());
        assertEquals(replacementChar,v.getReplacementCharacter());
        assertEquals(illegalChars, v.getInvalidCharacters());
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

//    public void testDataPathSupplied() {
//        UnitTestUtil.assertTestDataPathSet();
//    }

    /*
     * Test for void StringNameValidator(int, int, boolean, char, char[])
     */
    public void testStringNameValidatorintintbooleancharcharArray1() {
        final int min = 4;
        final int max = 10;
        final boolean caseSensitive = true;
        final char replacementChar = REPLACEMENT_CHAR;
        final char[] illegalChars = ILLEGAL_CHARS;
        final StringNameValidator v = new StringNameValidator(min,max,caseSensitive,replacementChar,illegalChars);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int, int, boolean, char, char[])
     */
    public void testStringNameValidatorintintbooleancharcharArray2() {
        final int min = -1;
        final int max = 10;
        final boolean caseSensitive = true;
        final char replacementChar = REPLACEMENT_CHAR;
        final char[] illegalChars = ILLEGAL_CHARS;
        final StringNameValidator v = new StringNameValidator(min,max,caseSensitive,replacementChar,illegalChars);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int, int, boolean, char, char[])
     */
    public void testStringNameValidatorintintbooleancharcharArray3() {
        final int min = 2;
        final int max = -10;
        final boolean caseSensitive = true;
        final char replacementChar = REPLACEMENT_CHAR;
        final char[] illegalChars = ILLEGAL_CHARS;
        final StringNameValidator v = new StringNameValidator(min,max,caseSensitive,replacementChar,illegalChars);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int, int, boolean, char, char[])
     */
    public void testStringNameValidatorintintbooleancharcharArray4() {
        final int min = 2;
        final int max = 10;
        final boolean caseSensitive = false;
        final char replacementChar = REPLACEMENT_CHAR;
        final char[] illegalChars = ILLEGAL_CHARS;
        final StringNameValidator v = new StringNameValidator(min,max,caseSensitive,replacementChar,illegalChars);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int, int, boolean, char, char[])
     */
    public void testStringNameValidatorintintbooleancharcharArray5() {
        final int min = 2;
        final int max = 10;
        final boolean caseSensitive = false;
        final char replacementChar = 'C';
        final char[] illegalChars = ILLEGAL_CHARS;
        final StringNameValidator v = new StringNameValidator(min,max,caseSensitive,replacementChar,illegalChars);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(boolean)
     */
    public void testStringNameValidatorboolean() {
        final int min = StringNameValidator.DEFAULT_MINIMUM_LENGTH;
        final int max = StringNameValidator.DEFAULT_MAXIMUM_LENGTH;
        final boolean caseSensitive = !StringNameValidator.DEFAULT_CASE_SENSITIVE_NAME_COMPARISON;
        final char replacementChar = StringNameValidator.DEFAULT_REPLACEMENT_CHARACTER;
        final char[] illegalChars = null;
        final StringNameValidator v = new StringNameValidator(caseSensitive);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(char[])
     */
    public void testStringNameValidatorcharArray() {
        final int min = StringNameValidator.DEFAULT_MINIMUM_LENGTH;
        final int max = StringNameValidator.DEFAULT_MAXIMUM_LENGTH;
        final boolean caseSensitive = StringNameValidator.DEFAULT_CASE_SENSITIVE_NAME_COMPARISON;
        final char replacementChar = StringNameValidator.DEFAULT_REPLACEMENT_CHARACTER;
        final char[] illegalChars = ILLEGAL_CHARS;
        final StringNameValidator v = new StringNameValidator(illegalChars);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int, char[])
     */
    public void testStringNameValidatorintcharArray() {
        final int min = StringNameValidator.DEFAULT_MINIMUM_LENGTH;
        final int max = 50;
        final boolean caseSensitive = StringNameValidator.DEFAULT_CASE_SENSITIVE_NAME_COMPARISON;
        final char replacementChar = StringNameValidator.DEFAULT_REPLACEMENT_CHARACTER;
        final char[] illegalChars = ILLEGAL_CHARS;
        final StringNameValidator v = new StringNameValidator(max,illegalChars);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int, int, char[])
     */
    public void testStringNameValidatorintintcharArray() {
        final int min = 4;
        final int max = 50;
        final boolean caseSensitive = StringNameValidator.DEFAULT_CASE_SENSITIVE_NAME_COMPARISON;
        final char replacementChar = StringNameValidator.DEFAULT_REPLACEMENT_CHARACTER;
        final char[] illegalChars = ILLEGAL_CHARS;
        final StringNameValidator v = new StringNameValidator(min,max,illegalChars);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int, int, boolean, char)
     */
    public void testStringNameValidatorintintbooleanchar() {
        final int min = 4;
        final int max = 50;
        final boolean caseSensitive = !StringNameValidator.DEFAULT_CASE_SENSITIVE_NAME_COMPARISON;
        final char replacementChar = 'C';
        final char[] illegalChars = null;
        final StringNameValidator v = new StringNameValidator(min,max,caseSensitive,replacementChar);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int, int, boolean)
     */
    public void testStringNameValidatorintintboolean() {
        final int min = 4;
        final int max = 50;
        final boolean caseSensitive = !StringNameValidator.DEFAULT_CASE_SENSITIVE_NAME_COMPARISON;
        final char replacementChar = StringNameValidator.DEFAULT_REPLACEMENT_CHARACTER;
        final char[] illegalChars = null;
        final StringNameValidator v = new StringNameValidator(min,max,caseSensitive);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int, int)
     */
    public void testStringNameValidatorintint() {
        final int min = 4;
        final int max = 50;
        final boolean caseSensitive = StringNameValidator.DEFAULT_CASE_SENSITIVE_NAME_COMPARISON;
        final char replacementChar = StringNameValidator.DEFAULT_REPLACEMENT_CHARACTER;
        final char[] illegalChars = null;
        final StringNameValidator v = new StringNameValidator(min,max);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator(int)
     */
    public void testStringNameValidatorint() {
        final int min = StringNameValidator.DEFAULT_MINIMUM_LENGTH;
        final int max = 50;
        final boolean caseSensitive = StringNameValidator.DEFAULT_CASE_SENSITIVE_NAME_COMPARISON;
        final char replacementChar = StringNameValidator.DEFAULT_REPLACEMENT_CHARACTER;
        final char[] illegalChars = null;
        final StringNameValidator v = new StringNameValidator(max);
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    /*
     * Test for void StringNameValidator()
     */
    public void testStringNameValidator() {
        final int min = StringNameValidator.DEFAULT_MINIMUM_LENGTH;
        final int max = StringNameValidator.DEFAULT_MAXIMUM_LENGTH;
        final boolean caseSensitive = StringNameValidator.DEFAULT_CASE_SENSITIVE_NAME_COMPARISON;
        final char replacementChar = StringNameValidator.DEFAULT_REPLACEMENT_CHARACTER;
        final char[] illegalChars = null;
        final StringNameValidator v = new StringNameValidator();
        helpCheckValidator(v,min,max,caseSensitive,replacementChar,illegalChars);
    }

    public void testCheckNameLengthWithTooShort() {
        final StringNameValidator val = this.defaultValidator;
        final String name = NAME_SHORTER_THAN_DEFAULT;
        final String reason = val.checkNameLength(name);
        assertNotNull(reason);
        assertTrue(reason.startsWith("The name must be at least " + val.getMinimumLength())); //$NON-NLS-1$
    }

    public void testCheckNameLengthWithTooLong() {
        final StringNameValidator val = this.defaultValidator;
        final String name = NAME_LONGER_THAN_DEFAULT;
        final String reason = val.checkNameLength(name);
        assertNotNull(reason);
        assertTrue(reason.startsWith("The name length (" + name.length() + ") is longer than")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testCheckNameLengthWithValidLength() {
        final StringNameValidator val = this.defaultValidator;
        final String name = NAME_WITH_GOOD_DEFAULT_LENGTH;
        final String reason = val.checkNameLength(name);
        assertNull(reason);
    }

    public void testCheckNameCharactersInvalidCharacters() {
        final StringNameValidator val = this.defaultValidator;
        final String name = NAME_LONGER_THAN_DEFAULT;
        final String reason = val.checkNameLength(name);
        assertNotNull(reason);
        assertTrue(reason.startsWith("The name length (" + name.length() + ") is longer than")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testCheckNameCharactersWithInvalidFirstCharacter() {
        final StringNameValidator val = this.defaultValidator;
        final String name = NAME_WITH_INVALID_FIRST_CHARACTER;
        final String reason = val.checkNameCharacters(name);
        assertNotNull(reason);
        assertTrue(reason.startsWith("The first character of the name ")); //$NON-NLS-1$
    }

    public void testCheckNameCharactersWithInvalidCharacter() {
        final StringNameValidator val = this.defaultValidator;
        final String name = NAME_WITH_INVALID_CHARACTERS;
        final String reason = val.checkNameCharacters(name);
        assertNotNull(reason);
        assertTrue(reason.startsWith("The character ")); //$NON-NLS-1$
    }

    public void testCheckNameCharactersWithValidCharacters() {
        final StringNameValidator val = this.defaultValidator;
        final String name = NAME_WITH_VALID_CHARACTERS;
        final String reason = val.checkNameCharacters(name);
        assertNull(reason);
    }

    public void testIsValidCharacter() {
        for (int i = 0; i < ILLEGAL_CHARS.length; ++i) {
            char c = ILLEGAL_CHARS[i];
            assertEquals(false, this.invalidCharValidator.isValidCharacter(c));
			assertEquals(true, this.defaultValidator.isValidCharacter(c));
        }
        for (int i = 0; i < VALID_CHARS.length; ++i) {
            char c = VALID_CHARS[i];
            assertEquals(true, this.invalidCharValidator.isValidCharacter(c));
			assertEquals(true, this.defaultValidator.isValidCharacter(c));
        }
    }

    public void testIsValidName1() {
        assertEquals(true, this.defaultValidator.isValidName(NAME_WITH_VALID_CHARACTERS));
		assertEquals(false, this.invalidCharValidator.isValidName(NAME_WITH_VALID_CHARACTERS));
    }
    public void testIsValidName2() {
        assertEquals(false, this.defaultValidator.isValidName(NAME_WITH_INVALID_CHARACTERS));
		assertEquals(false, this.invalidCharValidator.isValidName(NAME_WITH_INVALID_CHARACTERS));
    }
    public void testIsValidName3() {
        assertEquals(false, this.defaultValidator.isValidName(NAME_WITH_INVALID_FIRST_CHARACTER));
		assertEquals(false, this.invalidCharValidator.isValidName(NAME_WITH_INVALID_FIRST_CHARACTER));
    }

    public void testCheckValidName() {
    }

    public void testCheckUniquenessForUniquelyNamedObject() {
        final StringNameValidator val = this.defaultValidator;
        final String name = MODEL_IMPORT_NAME_PREFIX + "XX"; //$NON-NLS-1$
        final String reason = val.checkUniqueness(name,eObject,siblings,nameFeatureId);
        assertNull(reason);
    }

    public void testCheckUniquenessWithFailure() {
        final StringNameValidator val = this.defaultValidator;
        final String name = MODEL_IMPORT_NAME_PREFIX + 3;
        final String reason = val.checkUniqueness(name,eObject,siblings,nameFeatureId);
        assertNotNull(reason);
    }

    /*
     * Test for String createValidName(String)
     */
    public void testCreateValidNameStringWithValidName() {
        final String newName = this.defaultValidator.createValidName(NAME_WITH_VALID_CHARACTERS);
        assertNull(newName);
    }
    public void testCreateValidNameStringWithInvalidName() {
        final String newName = this.defaultValidator.createValidName(NAME_WITH_INVALID_CHARACTERS);
        assertNotNull(newName);
        assertEquals(NAME_WITH_INVALID_CHARACTERS_CORRECTED, newName);
    }
    public void testCreateValidNameStringWithInvalidFirstCharName() {
        final String newName = this.defaultValidator.createValidName(NAME_WITH_INVALID_FIRST_CHARACTER);
        assertNotNull(newName);
        assertEquals(NAME_WITH_INVALID_FIRST_CHARACTER_CORRECTED, newName);
    }

    /*
     * Test method that checks against invalid char string only.  This method supplies some invalid Strings.
     * Expected behavior is that the message string is not null - there is an error
     */
    public void testCheckStringInvalidCharOnly() {
    	char[] INVALID_PROJECT_CHARS = {'[',']','{','}','%','#','&','$','+',',',';','=','@','!','~','^'};
        StringNameValidator invalidCharOnlyValidator = new StringNameValidator(INVALID_PROJECT_CHARS);

        // Test string
        String msg = invalidCharOnlyValidator.checkInvalidCharacters("Test&1"); //$NON-NLS-1$
        assertNotNull(msg);

        // Test string
        msg = invalidCharOnlyValidator.checkInvalidCharacters("Test = Me"); //$NON-NLS-1$
        assertNotNull(msg);

        // Test string
        msg = invalidCharOnlyValidator.checkInvalidCharacters("& Another #"); //$NON-NLS-1$
        assertNotNull(msg);

        // Test string
        msg = invalidCharOnlyValidator.checkInvalidCharacters("1 + 2 = 3"); //$NON-NLS-1$
        assertNotNull(msg);
    }

    /*
     * Test method that checks against invalid char string only.  This method supplies some valid Strings.
     * Expected behavior is that the message string is null - there is no error
     */
    public void testCheckStringInvalidCharOnly2() {
    	char[] INVALID_PROJECT_CHARS = {'[',']','{','}','%','#','&','$','+',',',';','=','@','!','~','^'};
        StringNameValidator invalidCharOnlyValidator = new StringNameValidator(INVALID_PROJECT_CHARS);

        // Test string
        String msg = invalidCharOnlyValidator.checkInvalidCharacters("Test Me"); //$NON-NLS-1$
        assertNull(msg);

        // Test string
        msg = invalidCharOnlyValidator.checkInvalidCharacters("Test_Me"); //$NON-NLS-1$
        assertNull(msg);

        // Test string
        msg = invalidCharOnlyValidator.checkInvalidCharacters("Another - Test"); //$NON-NLS-1$
        assertNull(msg);

        // Test string
        msg = invalidCharOnlyValidator.checkInvalidCharacters("_Still_1checking1"); //$NON-NLS-1$
        assertNull(msg);
    }

    /*
     * Test for String createValidName(String, boolean)
     */
    public void testCreateValidNameStringboolean() {
    }

    public void testCreateValidUniqueName() {
    }

    public void testCreateUniqueName6() {
        helpTestCreateUniqueName(this.defaultValidator,6,MODEL_IMPORT_NAME_PREFIX+6);
    }
    public void testCreateUniqueName21() {
        helpTestCreateUniqueName(this.defaultValidator,21,MODEL_IMPORT_NAME_PREFIX+21);
    }
    public void testCreateUniqueName21NearLimit() {
        final int length = MODEL_IMPORT_NAME_PREFIX.length();
        final StringNameValidator val = new StringNameValidator( length + 2 );
        helpTestCreateUniqueName(val,21,MODEL_IMPORT_NAME_PREFIX+21);
    }
//    public void testCreateUniqueName21WithTooMany() {
//        final int length = MODEL_IMPORT_NAME_PREFIX.length();
//        final StringNameValidator val = new StringNameValidator( length + 1 );
//        helpTestCreateUniqueName(val,21,MODEL_IMPORT_NAME_PREFIX.substring(0,length-1)+10);
//    }
    public void testCreateUniqueName21WithWayTooMany() {
        final int length = MODEL_IMPORT_NAME_PREFIX.length();
        final StringNameValidator val = new StringNameValidator( length + 2 );
        helpTestCreateUniqueName(val,201,MODEL_IMPORT_NAME_PREFIX.substring(0,length-1)+100);
    }
    public void testCreateUniqueName21WithWayWayTooMany() {
        final int length = MODEL_IMPORT_NAME_PREFIX.length();
        final StringNameValidator val = new StringNameValidator( length + 1 );
        helpTestCreateUniqueName(val,201,MODEL_IMPORT_NAME_PREFIX.substring(0,length-1)+10);
    }

    protected void helpTestCreateUniqueName( final StringNameValidator val, final int largest, final String expectedName ) {
        final String name = MODEL_IMPORT_NAME_PREFIX;
        final List existingNames = new LinkedList();
        existingNames.add(MODEL_IMPORT_NAME_PREFIX);
        for ( int j=0;j!=3;j++) {       // add 3 sets of duplicates
            for ( int i=1;i!=largest;++i ) {
                existingNames.add(MODEL_IMPORT_NAME_PREFIX+i);
            }
        }
        final String newName = val.createUniqueName(name,existingNames);
        assertNotNull(newName);
        assertEquals(expectedName, newName);
    }

}
