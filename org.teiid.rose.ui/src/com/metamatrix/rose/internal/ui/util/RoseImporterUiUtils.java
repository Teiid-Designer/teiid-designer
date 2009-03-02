/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal.ui.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.rose.internal.IUnit;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;
import com.metamatrix.rose.internal.ui.RoseUiPlugin;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.internal.widget.StatusLabel;

/**
 * RoseImporterUiUtils
 */
public final class RoseImporterUiUtils implements
                                      FileUtils.Constants,
                                      IRoseUiConstants,
                                      IRoseUiConstants.Images,
                                      StringUtil.Constants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Image associated with a Rose CAT file. */
    private static final Image CAT_FILE_IMAGE;
    
    /** Error image suitable for a label provider. */
    private static final Image ERROR_IMAGE;
    
    /** Info image suitable for a label provider. */
    private static final Image INFO_IMAGE;
    
    /** Image associated with a Rose model file. */
    private static final Image MODEL_FILE_IMAGE;
    
    /** TableColumn header image for columns showing status icons. */ 
    private static final Image PROBLEM_ICON;
    
    /** Warning image suitable for a label provider. */
    private static final Image WARNING_IMAGE;
  
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    static {
        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        ERROR_IMAGE = sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
        INFO_IMAGE = sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
        WARNING_IMAGE = sharedImages.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
        
        AbstractUiPlugin plugin = RoseUiPlugin.getDefault();
        CAT_FILE_IMAGE = plugin.getImage(CAT_FILE_ICON);
        MODEL_FILE_IMAGE = plugin.getImage(MODEL_FILE_ICON);
        PROBLEM_ICON = plugin.getImage(PROBLEMS_VIEW_ICON);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** Don't allow construction. */
    private RoseImporterUiUtils() {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the image commonly displayed in a table column header for columns just showing status images.
     * @return the image
     */
    public static Image getProblemViewImage() {
        return PROBLEM_ICON;
    }
    
    /**
     * Gets the appropriate image for the specified severity level. The severity is based on the
     * {@link IStatus} codes.
     * @param theSeverity the severity
     * @return the image or <code>null</code>
     */
    public static Image getStatusImage(int theSeverity) {
        Image result = null;
        
        if (theSeverity == IStatus.ERROR) {
            result = ERROR_IMAGE;
        } else if (theSeverity == IStatus.WARNING) {
            result = WARNING_IMAGE;
        } else if (theSeverity == IStatus.INFO) {
            result = INFO_IMAGE;
        }
        
        return result;
    }
    
    /**
     * Gets a status image suitable of label providers.
     * @param theUnit
     * @param theSourcePhase indicates if an image for the source phase is being requested
     * @return @since 4.1
     */
    public static Image getStatusImage(IUnit theUnit,
                                 boolean theSourcePhase) {
        Image result = null;

        if (theUnit != null) {
            int severity = (theSourcePhase) ? theUnit.getSourceStatus()
                                            : theUnit.getTargetStatus();
            result = getStatusImage(severity);
        }

        return result;
    }
    
    /**
     * Gets the image associated with the specific type of Rose unit.
     * @param theUnit the unit whose icon is being requested
     * @return the image or <code>null</code>
     */
    public static Image getUnitImage(IUnit theUnit) {
        Image result = null;
        
        if (isCatUnit(theUnit)) {
            result = CAT_FILE_IMAGE;
        } else if (isModelUnit(theUnit)) {
            result = MODEL_FILE_IMAGE;
        }
        
        return result;
    }

    /**
     * Indicates if the specified Rose unit is a CAT file.
     * 
     * @param theUnit
     *            the unit being checked
     * @return <code>true</code> if a CAT file; <code>false</code> otherwise.
     */
    public static boolean isCatUnit(IUnit theUnit) {
        return theUnit.getUnresolvedPath().endsWith(FILE_EXTENSION_SEPARATOR_CHAR + CAT_UNIT_EXTENSION);
    }

    /**
     * Indicates if the specified Rose unit is a model.
     * 
     * @param theUnit
     *            the unit being checked
     * @return <code>true</code> if a model; <code>false</code> otherwise.
     */
    public static boolean isModelUnit(IUnit theUnit) {
        return theUnit.getUnresolvedPath().endsWith(FILE_EXTENSION_SEPARATOR_CHAR + MODEL_UNIT_EXTENSION);
    }

    /**
     * Sets the specified <code>CLabel</code>'s image and text based on the specified <code>IUnit</code>'s status.
     * 
     * @param theUnit
     *            the unit being used or <code>null</code>
     * @param theLabel
     *            the label being set
     * @param theSourcePhase indicates if the label is being shown during the source phase
     */
      public static void setLabelProperties(IUnit theUnit,
                                            StatusLabel theLabel,
                                            boolean theSourcePhase) {
          // set image
          theLabel.setImage(getStatusImage(theUnit, theSourcePhase));

          // set label text
          String text = ""; //$NON-NLS-1$

          if (theUnit != null) {
              if (theSourcePhase) {
                  text = (theUnit.getSourceMessage() == null) ? EMPTY_STRING
                                                              : theUnit.getSourceMessage();
              } else {
                  text = (theUnit.getTargetMessage() == null) ? EMPTY_STRING
                                                              : theUnit.getTargetMessage();
              }
          }

          theLabel.setText(text);
          theLabel.update();
      }
}
