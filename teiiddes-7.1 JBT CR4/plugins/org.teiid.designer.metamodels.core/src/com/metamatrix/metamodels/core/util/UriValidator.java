/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.CoreMetamodelPlugin;

/**
 * The <code>UriValidator</code> class validates textual representations of {@link org.eclipse.emf.common.util.URI} and its
 * components.
 * 
 * @author <a href="mailto:dflorian@metamatrix.com">Dan Florian</a>
 * @since 4.3
 */
public class UriValidator {

    /**
     * Properties key prefix used when obtaining localized messages.
     * 
     * @since 4.3
     */
    static final String PREFIX = I18nUtil.getPropertyPrefix(UriValidator.class);

    /**
     * Properties key prefix used when obtaining URI component labels.
     * 
     * @since 4.3
     */
    private static final String URI_COMPONENTS_PREFIX = "uricomponents."; //$NON-NLS-1$

    /**
     * Label for the URI.
     * 
     * @since 4.3
     */
    private static final String URI_LABEL = CoreMetamodelPlugin.Util.getString(PREFIX + "uri"); //$NON-NLS-1$

    /**
     * Multiplication factor on status codes for invalid values.
     */
    private static final int INVALID_FACTOR = -1;

    /**
     * Values for code field of the {@link IStatus} objects returned by the validate methods.
     * 
     * @since 4.3
     */
    public interface StatusCodes {
        /**
         * Status code indicating the value is valid.
         * 
         * @since 4.3
         */
        int VALID_URI = 1;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI}.
         * 
         * @since 4.3
         */
        int INVALID_URI = VALID_URI * INVALID_FACTOR;

        /**
         * Status code indicating the value is a valid {@link org.eclipse.emf.common.util.URI} authority component.
         * 
         * @since 4.3
         */
        int VALID_AUTHORITY = 10;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI} authority component.
         * 
         * @since 4.3
         */
        int INVALID_AUTHORITY = VALID_AUTHORITY * INVALID_FACTOR; // NO_UCD

        /**
         * Status code indicating the value is a valid {@link org.eclipse.emf.common.util.URI} device component.
         * 
         * @since 4.3
         */
        int VALID_DEVICE = 20;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI} device component.
         * 
         * @since 4.3
         */
        int INVALID_DEVICE = VALID_DEVICE * INVALID_FACTOR; // NO_UCD

        /**
         * Status code indicating the value is a valid {@link org.eclipse.emf.common.util.URI} fragment component.
         * 
         * @since 4.3
         */
        int VALID_FRAGMENT = 30;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI} fragment component.
         * 
         * @since 4.3
         */
        int INVALID_FRAGMENT = VALID_FRAGMENT * INVALID_FACTOR; // NO_UCD

        /**
         * Status code indicating the value is a valid {@link org.eclipse.emf.common.util.URI} jar authority component.
         * 
         * @since 4.3
         */
        int VALID_ARCHIVE_AUTHORITY = 40;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI} jar authority component.
         * 
         * @since 4.3
         */
        int INVALID_ARCHIVE_AUTHORITY = VALID_ARCHIVE_AUTHORITY * INVALID_FACTOR; // NO_UCD

        /**
         * Status code indicating the value is a valid {@link org.eclipse.emf.common.util.URI} opaque part component.
         * 
         * @since 4.3
         */
        int VALID_OPAQUE_PART = 50;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI} opaque part component.
         * 
         * @since 4.3
         */
        int INVALID_OPAQUE_PART = VALID_OPAQUE_PART * INVALID_FACTOR; // NO_UCD

        /**
         * Status code indicating the value is a valid {@link org.eclipse.emf.common.util.URI} query component.
         * 
         * @since 4.3
         */
        int VALID_QUERY = 60;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI} query component.
         * 
         * @since 4.3
         */
        int INVALID_QUERY = VALID_QUERY * INVALID_FACTOR; // NO_UCD

        /**
         * Status code indicating the value is a valid {@link org.eclipse.emf.common.util.URI} scheme component.
         * 
         * @since 4.3
         */
        int VALID_SCHEME = 70;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI} scheme component.
         * 
         * @since 4.3
         */
        int INVALID_SCHEME = VALID_SCHEME * INVALID_FACTOR; // NO_UCD

        /**
         * Status code indicating the value is a valid {@link org.eclipse.emf.common.util.URI} segment component.
         * 
         * @since 4.3
         */
        int VALID_SEGMENT = 80;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI} segment component.
         * 
         * @since 4.3
         */
        int INVALID_SEGMENT = VALID_SEGMENT * INVALID_FACTOR; // NO_UCD

        /**
         * Status code indicating the value is a valid {@link org.eclipse.emf.common.util.URI} segment array component.
         * 
         * @since 4.3
         */
        int VALID_SEGMENTS = 90;

        /**
         * Status code indicating the value is not a valid {@link org.eclipse.emf.common.util.URI} segment array component.
         * 
         * @since 4.3
         */
        int INVALID_SEGMENTS = VALID_SEGMENTS * INVALID_FACTOR; // NO_UCD
    }

    /**
     * Labels for the {@link org.eclipse.emf.common.util.URI} components.
     * 
     * @since 4.3
     */
    public interface UriComponents {
        /**
         * Label for the authority component.
         * 
         * @since 4.3
         */
        String AUTHORITY = CoreMetamodelPlugin.Util.getString(PREFIX + URI_COMPONENTS_PREFIX + "authority"); //$NON-NLS-1$

        /**
         * Label for the device component.
         * 
         * @since 4.3
         */
        String DEVICE = CoreMetamodelPlugin.Util.getString(PREFIX + URI_COMPONENTS_PREFIX + "device"); //$NON-NLS-1$

        /**
         * Label for the fragment component.
         * 
         * @since 4.3
         */
        String FRAGMENT = CoreMetamodelPlugin.Util.getString(PREFIX + URI_COMPONENTS_PREFIX + "fragment"); //$NON-NLS-1$

        /**
         * Label for the jar authority component.
         * 
         * @since 4.3
         */
        String JAR_AUTHORITY = CoreMetamodelPlugin.Util.getString(PREFIX + URI_COMPONENTS_PREFIX + "jarauthority"); //$NON-NLS-1$

        /**
         * Label for the opaque part component.
         * 
         * @since 4.3
         */
        String OPAQUE_PART = CoreMetamodelPlugin.Util.getString(PREFIX + URI_COMPONENTS_PREFIX + "opaquepart"); //$NON-NLS-1$

        /**
         * Label for the query component.
         * 
         * @since 4.3
         */
        String QUERY = CoreMetamodelPlugin.Util.getString(PREFIX + URI_COMPONENTS_PREFIX + "query"); //$NON-NLS-1$

        /**
         * Label for the scheme component.
         * 
         * @since 4.3
         */
        String SCHEME = CoreMetamodelPlugin.Util.getString(PREFIX + URI_COMPONENTS_PREFIX + "scheme"); //$NON-NLS-1$

        /**
         * Label for the segment component.
         * 
         * @since 4.3
         */
        String SEGMENT = CoreMetamodelPlugin.Util.getString(PREFIX + URI_COMPONENTS_PREFIX + "segment"); //$NON-NLS-1$

        /**
         * Label for multiple segment components.
         * 
         * @since 4.3
         */
        String SEGMENTS = CoreMetamodelPlugin.Util.getString(PREFIX + URI_COMPONENTS_PREFIX + "multiplesegment"); //$NON-NLS-1$
    }

    /**
     * Obtains a message reporting that the value being set for the specified {@link org.eclipse.emf.common.util.URI} component is
     * <code>null</code> or empty.
     * 
     * @param theComponent the component whose value is being validated
     * @return the message
     * @since 4.3
     */
    private static String createNullValueMessage( String theComponent ) {
        return CoreMetamodelPlugin.Util.getString(PREFIX + "nullValue", theComponent); //$NON-NLS-1$;
    }

    /**
     * Obtains an <code>IStatus</code> suitable for the specified parameters.
     * 
     * @param theComponent the URI component label
     * @param theValue the value being validated
     * @param theIsValidFlag the flag indicating if the value is valid
     * @since 4.3
     */
    private static IStatus createStatus( String theComponent,
                                         String theValue,
                                         boolean theIsValidFlag ) {
        return new Status(getSeverity(theIsValidFlag), CoreMetamodelPlugin.PLUGIN_ID, getCode(theComponent, theIsValidFlag),
                          createValidationMessage(theComponent, theValue, theIsValidFlag), null); // no exception
    }

    /**
     * Obtains a validation message suitable for the specified parameters.
     * 
     * @param theComponent the component whose value is being validated
     * @param theValue the proposed value
     * @param theIsValidFlag the flag indicating if the value is valid
     * @return the validation message
     * @since 4.3
     */
    private static String createValidationMessage( String theComponent,
                                                   String theValue,
                                                   boolean theIsValidFlag ) {
        if (theIsValidFlag) {
            return CoreMetamodelPlugin.Util.getString(PREFIX + "componentValid", new Object[] {theComponent, theValue}); //$NON-NLS-1$;
        }

        return CoreMetamodelPlugin.Util.getString(PREFIX + "componentNotValid", new Object[] {theComponent, theValue}); //$NON-NLS-1$;
    }

    /**
     * Obtains an <code>IStatus</code> code suitable for the specified parameters.
     * 
     * @param theComponent the URI component label
     * @param theIsValidFlag the flag indicating if the value is valid
     * @since 4.3
     */
    private static int getCode( String theComponent,
                                boolean theIsValidFlag ) {
        int result = StatusCodes.VALID_URI;

        if (URI_LABEL.equals(theComponent)) {
            result = StatusCodes.VALID_URI;
        } else if (UriComponents.AUTHORITY.equals(theComponent)) {
            result = StatusCodes.VALID_AUTHORITY;
        } else if (UriComponents.DEVICE.equals(theComponent)) {
            result = StatusCodes.VALID_DEVICE;
        } else if (UriComponents.FRAGMENT.equals(theComponent)) {
            result = StatusCodes.VALID_FRAGMENT;
        } else if (UriComponents.JAR_AUTHORITY.equals(theComponent)) {
            result = StatusCodes.VALID_ARCHIVE_AUTHORITY;
        } else if (UriComponents.OPAQUE_PART.equals(theComponent)) {
            result = StatusCodes.VALID_OPAQUE_PART;
        } else if (UriComponents.QUERY.equals(theComponent)) {
            result = StatusCodes.VALID_QUERY;
        } else if (UriComponents.SCHEME.equals(theComponent)) {
            result = StatusCodes.VALID_SCHEME;
        } else if (UriComponents.SEGMENT.equals(theComponent)) {
            result = StatusCodes.VALID_SEGMENT;
        } else if (UriComponents.SEGMENTS.equals(theComponent)) {
            result = StatusCodes.VALID_SEGMENTS;
        }

        if (!theIsValidFlag) {
            result *= INVALID_FACTOR;
        }

        return result;
    }

    /**
     * Obtains an {@link IStatus} severity for the specified validation result.
     * 
     * @param theIsValidFlag the flag indicating if the validation was successful
     * @return the severity
     * @since 4.3
     */
    private static int getSeverity( boolean theIsValidFlag ) {
        return (theIsValidFlag ? IStatus.OK : IStatus.ERROR);
    }

    /**
     * Indicates if the specified value can be converted into a valid {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theUri the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.3
     */
    public static boolean isValid( String theUri ) { // NO_UCD
        boolean result = false;

        if (CoreStringUtil.isEmpty(theUri)) {
            // clearing the URI is OK
            result = true;
        } else {
            try {
                URI.createURI(theUri);
                result = true;
            } catch (IllegalArgumentException theException) {
                // no handling required since result is already false
            }
        }

        return result;
    }

    /**
     * Indicates if the specified value can be converted into a valid {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theUri the value being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @since 4.3
     */
    public static IStatus validate( String theUri ) {
        IStatus result = null;

        if (!CoreStringUtil.isEmpty(theUri)) {
            try {
                URI.createURI(theUri);
            } catch (IllegalArgumentException theException) {
                int code = StatusCodes.INVALID_URI;
                String uriMsg = theException.getMessage(); // exception caught will have details on why invalid
                String msg = null;

                // look at msg searching for key words in order to set the code properly.
                // not crazy about doing this but the URI class is not localized and wanted to get
                // the status code and message to represent a detailed error
                if ((uriMsg != null) && (uriMsg.length() > 0)) {
                    String component = null;

                    if (uriMsg.indexOf("scheme") != -1) { //$NON-NLS-1$
                        component = UriComponents.SCHEME;
                    } else if (uriMsg.indexOf("opaquePart") != -1) { //$NON-NLS-1$
                        component = UriComponents.OPAQUE_PART;
                    } else if (uriMsg.indexOf("authority") != -1) { //$NON-NLS-1$
                        if (CoreStringUtil.startsWithIgnoreCase(theUri, "jar")) { //$NON-NLS-1$
                            component = UriComponents.JAR_AUTHORITY;
                        } else {
                            component = UriComponents.AUTHORITY;
                        }
                    } else if (uriMsg.indexOf("device") != -1) { //$NON-NLS-1$
                        component = UriComponents.DEVICE;
                    } else if (uriMsg.indexOf("segments") != -1) { //$NON-NLS-1$
                        component = UriComponents.SEGMENTS;
                    } else if (uriMsg.indexOf("query") != -1) { //$NON-NLS-1$
                        component = UriComponents.QUERY;
                    } else if (uriMsg.indexOf("fragment") != -1) { //$NON-NLS-1$
                        component = UriComponents.FRAGMENT;
                    } else {
                        msg = uriMsg;
                    }

                    // found the component that is in error
                    if (component != null) {
                        code = getCode(component, false);
                        msg = CoreMetamodelPlugin.Util.getString(PREFIX + "uriComponentInvalid", new Object[] {component, theUri}); //$NON-NLS-1$;
                    }
                } else {
                    // exception message is null or empty
                    code = getCode(URI_LABEL, false);
                    msg = CoreMetamodelPlugin.Util.getString(PREFIX + "genericUriError", new Object[] {theUri}); //$NON-NLS-1$;
                }

                result = new Status(getSeverity(false), CoreMetamodelPlugin.PLUGIN_ID, code, msg, null);
            }
        }

        // create OK status message if no error found
        if (result == null) {
            result = createStatus(URI_LABEL, theUri, true);
        }

        return result;
    }

    /**
     * Indicates if the specified value is a valid authority component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theAuthority the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.3
     * @see URI#validAuthority(java.lang.String)
     */
    public static boolean isValidAuthority( String theAuthority ) {
        return URI.validAuthority(theAuthority);
    }

    /**
     * Indicates if the specified value is a valid authority component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theAuthority the value being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @since 4.3
     * @see URI#validAuthority(java.lang.String)
     */
    public static IStatus validateAuthority( String theAuthority ) { // NO_UCD
        return createStatus(UriComponents.AUTHORITY, theAuthority, isValidAuthority(theAuthority));
    }

    /**
     * Indicates if the specified value is a valid device component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theDevice the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.3
     * @see URI#validDevice(java.lang.String)
     */
    public static boolean isValidDevice( String theDevice ) {
        return URI.validDevice(theDevice);
    }

    /**
     * Indicates if the specified value is a valid device component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theDevice the value being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @since 4.3
     * @see URI#validDevice(java.lang.String)
     */
    public static IStatus validateDevice( String theDevice ) { // NO_UCD
        return createStatus(UriComponents.DEVICE, theDevice, isValidDevice(theDevice));
    }

    /**
     * Indicates if the specified value is a valid fragment component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theFragment the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.3
     * @see URI#validFragment(java.lang.String)
     */
    public static boolean isValidFragment( String theFragment ) {
        return URI.validFragment(theFragment);
    }

    /**
     * Indicates if the specified value is a valid fragment component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theFragment the value being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @since 4.3
     * @see URI#validFragment(java.lang.String)
     */
    public static IStatus validateFragment( String theFragment ) { // NO_UCD
        return createStatus(UriComponents.FRAGMENT, theFragment, isValidFragment(theFragment));
    }

    /**
     * Indicates if the specified value is a valid archive authority component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theArchiveAuthority the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.3
     * @see URI#validArchiveAuthority(java.lang.String)
     */
    public static boolean isValidArchiveAuthority( String theArchiveAuthority ) {
        return URI.validArchiveAuthority(theArchiveAuthority);
    }

    /**
     * Indicates if the specified value is a valid archive authority component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theArchiveAuthority the value being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @since 4.3
     * @see URI#validArchiveAuthority(java.lang.String)
     */
    public static IStatus validateArchiveAuthority( String theArchiveAuthority ) { // NO_UCD
        return createStatus(UriComponents.JAR_AUTHORITY, theArchiveAuthority, isValidArchiveAuthority(theArchiveAuthority));
    }

    /**
     * Indicates if the specified value is a valid opaque part component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theOpaquePart the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.3
     * @see URI#validOpaquePart(java.lang.String)
     */
    public static boolean isValidOpaquePart( String theOpaquePart ) {
        return URI.validOpaquePart(theOpaquePart);
    }

    /**
     * Indicates if the specified value is a valid opaque part component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theOpaquePart the value being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @since 4.3
     * @see URI#validOpaquePart(java.lang.String)
     */
    public static IStatus validateOpaquePart( String theOpaquePart ) { // NO_UCD
        return createStatus(UriComponents.OPAQUE_PART, theOpaquePart, isValidOpaquePart(theOpaquePart));
    }

    /**
     * Indicates if the specified value is a valid query component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theQuery the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.3
     * @see URI#validQuery(java.lang.String)
     */
    public static boolean isValidQuery( String theQuery ) {
        return URI.validQuery(theQuery);
    }

    /**
     * Indicates if the specified value is a valid query component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theQuery the value being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @since 4.3
     * @see URI#validQuery(java.lang.String)
     */
    public static IStatus validateQuery( String theQuery ) { // NO_UCD
        return createStatus(UriComponents.QUERY, theQuery, isValidQuery(theQuery));
    }

    /**
     * Indicates if the specified value is a valid query component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theScheme the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.3
     * @see URI#validScheme(java.lang.String)
     */
    public static boolean isValidScheme( String theScheme ) {
        return URI.validScheme(theScheme);
    }

    /**
     * Indicates if the specified value is a valid query component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theScheme the value being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @since 4.3
     * @see URI#validScheme(java.lang.String)
     */
    public static IStatus validateScheme( String theScheme ) { // NO_UCD
        return createStatus(UriComponents.SCHEME, theScheme, isValidScheme(theScheme));
    }

    /**
     * Indicates if the specified value is a valid seqment component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theSegment the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.3
     * @see URI#validSegment(java.lang.String)
     */
    public static boolean isValidSegment( String theSegment ) {
        return URI.validSegment(theSegment);
    }

    /**
     * Indicates if the specified value is a valid seqment component of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theSegment the value being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @since 4.3
     * @see URI#validSegment(java.lang.String)
     */
    public static IStatus validateSegment( String theSegment ) { // NO_UCD
        return createStatus(UriComponents.SEGMENT, theSegment, isValidSegment(theSegment));
    }

    /**
     * Indicates if all the specified values are valid seqment components of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theSegments the value being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @throws AssertionError if value is <code>null</code>
     * @since 4.3
     * @see URI#validSegments(java.lang.String[])
     */
    public static boolean isValidSegments( String[] theSegments ) throws AssertionError { // NO_UCD
        CoreArgCheck.isNotNull(theSegments, createNullValueMessage(UriComponents.SEGMENTS));
        return URI.validSegments(theSegments);
    }

    /**
     * Indicates if all the specified values are valid seqment components of a {@link org.eclipse.emf.common.util.URI}.
     * 
     * @param theSegments the values being checked
     * @return a status indicating the result of the validity check; never <code>null</code>
     * @throws AssertionError if value is <code>null</code>
     * @since 4.3
     * @see URI#validSegments(java.lang.String[])
     */
    public static IStatus validateSegments( String[] theSegments ) throws AssertionError { // NO_UCD
        CoreArgCheck.isNotNull(theSegments, createNullValueMessage(UriComponents.SEGMENTS));

        IStatus result = null;

        for (int i = 0; i < theSegments.length; ++i) {
            boolean valid = isValidSegment(theSegments[i]);

            if (!valid) {
                result = createStatus(UriComponents.SEGMENTS, theSegments[i], false);
                break;
            }
        }

        // all segments valid
        if (result == null) {
            result = new Status(IStatus.OK, CoreMetamodelPlugin.PLUGIN_ID, StatusCodes.VALID_SEGMENTS,
                                CoreMetamodelPlugin.Util.getString(PREFIX + "allsegmentsvalid"), //$NON-NLS-1$
                                null);
        }

        return result;
    }

    /**
     * Don't allow object construction.
     */
    private UriValidator() {
        // no impl required
    }
}
