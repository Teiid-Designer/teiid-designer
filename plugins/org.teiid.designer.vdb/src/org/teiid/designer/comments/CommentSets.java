/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.comments;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.teiid.core.designer.util.StringConstants;

/**
 *
 */
public class CommentSets implements StringConstants {

    private Map<String, List<String>> commentsMap;

    /**
     * @return element's own comments
     */
    public List<String> getElementCommentSet() {
        return getCommentSet(EMPTY_STRING);
    }

    /**
     * @param key
     * @return comments according to the given key.
     *                  If {@link StringConstants#EMPTY_STRING} then the element's own comments
     *                  are returned. 
     */
    public List<String> getCommentSet(String key) {
        if (commentsMap == null)
            return Collections.emptyList();

        List<String> comments = commentsMap.get(key);
        if (comments == null)
            return Collections.emptyList();

        return comments;
    }

    /**
     * @param key
     * @param index
     *
     * @return comments according to the given key.
     *                  If {@link StringConstants#EMPTY_STRING} then the element's own comments
     *                  are returned. 
     */
    public List<String> getCommentSet(String key, int index) {
        if (commentsMap == null)
            return Collections.emptyList();

        List<String> comments = commentsMap.get(key + HYPHEN + index);
        if (comments == null)
            return Collections.emptyList();

        return comments;
    }

    /**
     * @param key if key is {@link StringConstants#EMPTY_STRING} then this represents
     *                        the element's own comments rather than a sub-element
     * @param comments
     */
    public void addCommentSet(String key, List<String> comments) {
        if (commentsMap == null)
            commentsMap = new HashMap<String, List<String>>();

        List<String> commentList = commentsMap.get(key);
        if (commentList == null)
            commentsMap.put(key, comments);
        else
            commentList.addAll(comments);
    }

    /**
     * @param key if key is {@link StringConstants#EMPTY_STRING} then this represents
     *                        the element's own comments rather than a sub-element
     * @param index the index of the set of comments
     * @param comments
     */
    public void addCommentSet(String key, int index, List<String> comments) {
        if (commentsMap == null)
            commentsMap = new HashMap<String, List<String>>();

        commentsMap.put(key + HYPHEN + index, comments);
    }

    public Collection<String> getCommentKeys() {
        if (commentsMap == null)
            return Collections.emptySet();

        return commentsMap.keySet();
    }

    /**
     * @param comments
     */
    public void add(CommentSets comments) {
        if (comments == null)
            return;

        for (String key : comments.getCommentKeys()) {
            addCommentSet(key, comments.getCommentSet(key));
        }
    }

    @Override
    public String toString() {
        if (commentsMap == null)
            return "No Comments"; //$NON-NLS-1$

        StringBuffer buffer = new StringBuffer("CommentSet: \n"); //$NON-NLS-1$
        for (Map.Entry<String, List<String>> entry : commentsMap.entrySet()) {
            String key = entry.getKey();
            if (key.equals(EMPTY_STRING))
                key = "<Element>"; //$NON-NLS-1$

            buffer.append(key).append(COLON).append(NEW_LINE);
            for (String comment : entry.getValue()) {
                buffer.append(comment).append(NEW_LINE);
            }

            buffer.append(NEW_LINE);
        }

        return buffer.toString();
    }

    /**
     * @return number of comments
     */
    public int size() {
        if (commentsMap == null)
            return 0;

        return commentsMap.size();
    }
}
