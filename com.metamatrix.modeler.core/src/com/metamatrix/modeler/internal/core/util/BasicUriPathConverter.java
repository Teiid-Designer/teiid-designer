/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.util;

import java.util.Arrays;

import org.eclipse.emf.common.util.URI;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.util.UriPathConverter;

/**
 * BasicUriHelper
 */
public class BasicUriPathConverter implements UriPathConverter {
    
    private static final String SEGMENT_EMPTY   = ""; //$NON-NLS-1$
    private static final String SEGMENT_SELF    = "."; //$NON-NLS-1$
    private static final String SEGMENT_PARENT  = ".."; //$NON-NLS-1$
    private static final char SEGMENT_SEPARATOR = '/';

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of BasicUriHelper.
     * 
     */
    public BasicUriPathConverter() {
        super();
    }
    
    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /** 
     * @see com.metamatrix.modeler.internal.core.resource.EmfUriHelper#makeAbsolute(java.lang.String, java.lang.String)
     */
    public String makeAbsolute(final String relativePath, final String basePath) {
        ArgCheck.isNotNull(relativePath);
        
        final URI relativeURI = URI.createURI(relativePath);
        
        // If relativePath is a workspace relative path of the form "/Project/.../File"
        // then return this path as the absolute path
        if (relativeURI.hasAbsolutePath()) {
            return relativePath;
        }
        
        ArgCheck.isNotNull(basePath);
        final URI baseURI = URI.createURI(basePath);
        
        // Use the URI class to make the relative path absolute
        return makeAbsolute(relativeURI,baseURI).toString();
    }

    /** 
     * @see com.metamatrix.modeler.internal.core.resource.EmfUriHelper#makeAbsolute(org.eclipse.emf.common.util.URI, org.eclipse.emf.common.util.URI)
     */
    public URI makeAbsolute(final URI relativeURI, final URI baseURI) {
        ArgCheck.isNotNull(relativeURI);
        
        // If relativePath is a workspace relative path of the form "/Project/.../File"
        // then return this path as the absolute path
        if (relativeURI.hasAbsolutePath()) {
            return relativeURI;
        }
        
        // Use the URI class to make the relative path absolute
        ArgCheck.isNotNull(baseURI);
        if (baseURI.isRelative()) {
            String[] segments = mergePath(relativeURI,baseURI,true);
            StringBuffer sb = new StringBuffer(200);
            for (int i = 0; i != segments.length; ++i) {
                sb.append(SEGMENT_SEPARATOR);
                sb.append(segments[i]);
            }
            return URI.createURI(sb.toString());
        }
        
        return relativeURI.resolve(baseURI);
    }

    /** 
     * @see com.metamatrix.modeler.internal.core.resource.EmfUriHelper#makeRelative(java.lang.String, java.lang.String)
     */
    public String makeRelative(final String absolutePath, final String basePath) {
        ArgCheck.isNotNull(absolutePath);
        
        final URI absoluteURI = URI.createURI(absolutePath);
        final URI baseURI = URI.createURI(basePath);
        
        // Use the URI class to make the absolute path relative
        return makeRelative(absoluteURI,baseURI).toString();
    }

    /** 
     * @see com.metamatrix.modeler.internal.core.resource.EmfUriHelper#makeRelative(org.eclipse.emf.common.util.URI, org.eclipse.emf.common.util.URI)
     */
    public URI makeRelative(final URI absoluteURI, final URI baseURI) {
        ArgCheck.isNotNull(absoluteURI);
        ArgCheck.isNotNull(baseURI);
        
        // Use the URI class to make the relative path absolute
        ArgCheck.isNotNull(baseURI);
        if (baseURI.isRelative()) {
            String[] segments = findRelativePath(absoluteURI,baseURI,true);
            StringBuffer sb = new StringBuffer(200);
            
            // Prepend a SEGMENT SEPARATOR to the resultant path
            String seg = segments[0];
            if (segments.length > 1 && !SEGMENT_SELF.equals(seg) && !SEGMENT_PARENT.equals(seg) && !SEGMENT_EMPTY.equals(seg)) {
                sb.append(SEGMENT_SEPARATOR);
            }

            for (int i = 0; i != segments.length; ++i) {
                seg = segments[i];
                if (i > 0) {
                    sb.append(SEGMENT_SEPARATOR);
                }
                sb.append(seg);
            }
            return URI.createURI(sb.toString());
        }
        
        // Use the URI class to make the absolute path relative
        return absoluteURI.deresolve(baseURI);
    }
    
    // ==================================================================================
    //                         P R I V A T E   M E T H O D S
    // ==================================================================================

//    private void printUriInfo(final URI uri) {
//        System.out.println("URI = "+uri); //$NON-NLS-1$
//        System.out.println("  isFile()          "+uri.isFile()); //$NON-NLS-1$
//        System.out.println("  isHierarchical()  "+uri.isHierarchical()); //$NON-NLS-1$
//        System.out.println("  isPrefix()        "+uri.isPrefix()); //$NON-NLS-1$
//        System.out.println("  isRelative()      "+uri.isRelative()); //$NON-NLS-1$
//        System.out.println("  hasAbsolutePath() "+uri.hasAbsolutePath()); //$NON-NLS-1$
//        System.out.println("  hasAuthority()    "+uri.hasAuthority()); //$NON-NLS-1$
//        System.out.println("  hasDevice()       "+uri.hasDevice()); //$NON-NLS-1$
//        System.out.println("  hasPath()         "+uri.hasPath()); //$NON-NLS-1$ 
//        System.out.println("  hasQuery()        "+uri.hasQuery()); //$NON-NLS-1$
//        System.out.println("  hasRelativePath() "+uri.hasRelativePath()); //$NON-NLS-1$
//    }
    
    // Merges a relative URI's path with the base non-relative path.  If
    // base has no path, treat it as the root absolute path, unless this has
    // no path either.
    private String[] mergePath(final URI relativeURI, final URI baseURI, boolean preserveRootParents) {

        int segmentCount  = relativeURI.segmentCount();
        String[] segments = relativeURI.segments();

        int baseSegmentCount = baseURI.segmentCount();
        String[] stack = new String[baseSegmentCount + segmentCount];
        int sp = 0;

        // use a stack to accumulate segments of base, except for the last
        // (i.e. skip trailing separator and anything following it), and of
        // relative path
        for (int i = 0; i < baseSegmentCount - 1; i++) {
            sp = accumulate(stack, sp, baseURI.segment(i), preserveRootParents);
        }

        for (int i = 0; i < segmentCount; i++) {
            sp = accumulate(stack, sp, segments[i], preserveRootParents);
        }

        // if the relative path is empty or ends in an empty segment, a parent 
        // reference, or a self referenfce, add a trailing separator to a
        // non-empty path
        if (sp > 0
            && (segmentCount == 0
                || SEGMENT_EMPTY.equals(segments[segmentCount - 1])
                || SEGMENT_PARENT.equals(segments[segmentCount - 1])
                || SEGMENT_SELF.equals(segments[segmentCount - 1]))) {
            stack[sp++] = SEGMENT_EMPTY;
        }

        // return a correctly sized result
        String[] result = new String[sp];
        System.arraycopy(stack, 0, result, 0, sp);
        return result;
    }

    // Adds a segment to a stack, skipping empty segments and self references,
    // and interpreting parent references.
    private static int accumulate(String[] stack, int sp, String segment, boolean preserveRootParents) {
        if (SEGMENT_PARENT.equals(segment)) {
            if (sp == 0) {
                // special care must be taken for a root's parent reference: it is
                // either ignored or the symbolic reference itself is pushed
                if (preserveRootParents)
                    stack[sp++] = segment;
            } else {
                // unless we're already accumulating root parent references,
                // parent references simply pop the last segment descended
                if (SEGMENT_PARENT.equals(stack[sp - 1]))
                    stack[sp++] = segment;
                else
                    sp--;
            }
        } else if (!SEGMENT_EMPTY.equals(segment) && !SEGMENT_SELF.equals(segment)) {
            // skip empty segments and self references; push everything else
            stack[sp++] = segment;
        }
        return sp;
    }
    
    // Returns the shortest relative path between the the non-relative path of
    // the given base and this absolute path.  If the base has no path, it is
    // treated as the root absolute path.
    private String[] findRelativePath(final URI absoluteURI, final URI baseURI, boolean preserveRootParents) {

        String[] segments = absoluteURI.segments();

        // treat an empty base path as the root absolute path
        String[] startPath = collapseSegments(baseURI,preserveRootParents);
        String[] endPath = segments;

        // drop last segment from base, as in resolving
        int startCount = startPath.length > 0 ? startPath.length - 1 : 0;
        int endCount = endPath.length;

        // index of first segment that is different between endPath and startPath
        int diff = 0;

        // if endPath is shorter than startPath, the last segment of endPath may
        // not be compared: because startPath has been collapsed and had its
        // last segment removed, all preceeding segments can be considered non-
        // empty and followed by a separator, while the last segment of endPath
        // will either be non-empty and not followed by a separator, or just empty
        for (int count = startCount < endCount ? startCount : endCount - 1;
            diff < count && startPath[diff].equals(endPath[diff]);
            diff++) {
            
        }

        int upCount = startCount - diff;
        int downCount = endCount - diff;

        // a single separator, possibly preceeded by some parent reference
        // segments, is redundant
        if (downCount == 1 && SEGMENT_EMPTY.equals(endPath[endCount - 1])) {
            downCount = 0;
        }

        // an empty path needs to be replaced by a single "." if there is no
        // query, to distinguish it from a current document reference
        if (upCount + downCount == 0) {
            return new String[] { SEGMENT_SELF };
        }

        // return a correctly sized result
        String[] result = new String[upCount + downCount];
        Arrays.fill(result, 0, upCount, SEGMENT_PARENT);
        System.arraycopy(endPath, diff, result, upCount, downCount);
        return result;
    }

    // Collapses non-ending empty segments, parent references, and self
    // references in a non-relative path, returning the same path that would
    // be produced from the base hierarchical URI as part of a resolve.
    String[] collapseSegments(final URI uri, boolean preserveRootParents) {
        String[] segments = uri.segments();

        if (!hasCollapsableSegments(uri,preserveRootParents))
            return segments;

        // use a stack to accumulate segments
        int segmentCount = segments.length;
        String[] stack = new String[segmentCount];
        int sp = 0;

        for (int i = 0; i < segmentCount; i++) {
            sp = accumulate(stack, sp, segments[i], preserveRootParents);
        }

        // if the path is non-empty and originally ended in an empty segment, a
        // parent reference, or a self reference, add a trailing separator
        if (sp > 0
            && (SEGMENT_EMPTY.equals(segments[segmentCount - 1])
                || SEGMENT_PARENT.equals(segments[segmentCount - 1])
                || SEGMENT_SELF.equals(segments[segmentCount - 1]))) {
            stack[sp++] = SEGMENT_EMPTY;
        }

        // return a correctly sized result
        String[] result = new String[sp];
        System.arraycopy(stack, 0, result, 0, sp);
        return result;
    }
    // Returns true if the non-relative path includes segments that would be
    // collapsed when resolving; false otherwise.  If preserveRootParents is
    // true, collapsable segments include any empty segments, except for the
    // last segment, as well as and parent and self references.  If
    // preserveRootsParents is false, parent references are not collapsable if
    // they are the first segment or preceeded only by other parent
    // references.
    private boolean hasCollapsableSegments(final URI uri, boolean preserveRootParents) {

        String[] segments = uri.segments();
        for (int i = 0, len = segments.length; i < len; i++) {
            String segment = segments[i];
            if ((i < len - 1 && SEGMENT_EMPTY.equals(segment))
                || SEGMENT_SELF.equals(segment)
                || SEGMENT_PARENT.equals(segment)
                && (!preserveRootParents || (i != 0 && !SEGMENT_PARENT.equals(segments[i - 1])))) {
                return true;
            }
        }
        return false;
    }

}
