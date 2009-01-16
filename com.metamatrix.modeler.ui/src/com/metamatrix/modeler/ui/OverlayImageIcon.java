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

package com.metamatrix.modeler.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author balajik
 * 
 * This class is used for overlaying image icons
 *
 * 
 */
public class OverlayImageIcon extends CompositeImageDescriptor
{
  /**
   * Base image of the object
   */ 
  private Image baseImage;
  
  /**
   * Size of the base image 
   */ 
  private Point sizeOfImage;
  
  /**
   * Overlay Image 
   */
  private ImageData overlayImageData;
  
  public static final int TOP_LEFT = 0;
  public static final int TOP_RIGHT = 1;
  public static final int BOTTOM_LEFT = 2;
  public static final int BOTTOM_RIGHT = 3;
  
  private static int LOCATION = BOTTOM_RIGHT;
  
  /**
   * Constructor for overlayImageIcon.
   */
  public OverlayImageIcon(Image baseImage, 
                          Image demoImage)
  {
    // Base image of the object
    this.baseImage = baseImage;
    // Demo Image Object 
    overlayImageData = demoImage.getImageData();
    //imageKey_ = imageKey;
    sizeOfImage = new Point(baseImage.getBounds().width, 
                             baseImage.getBounds().height);
  }

  public OverlayImageIcon(Image baseImage, 
                          Image demoImage, int location)
  {
    // Base image of the object
    LOCATION = location;
    this.baseImage = baseImage;
    // Demo Image Object 
    overlayImageData = demoImage.getImageData();
    //imageKey_ = imageKey;
    sizeOfImage = new Point(baseImage.getBounds().width, 
                             baseImage.getBounds().height);
  }  
  /**
   * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
   * DrawCompositeImage is called to draw the composite image.
   * 
   */
  @Override
protected void drawCompositeImage(int arg0, int arg1)
  {
    // Draw the base image
    drawImage(baseImage.getImageData(), 0, 0); 
    ImageData imageData = overlayImageData;
    if(imageData!=null) {
        switch(LOCATION) {
          // Draw on the top left corner
          case TOP_LEFT:
            drawImage(imageData, 0, 0);
            break;
            
          // Draw on top right corner  
          case TOP_RIGHT:
            drawImage(imageData, sizeOfImage.x - imageData.width, 0);
            break;
            
          // Draw on bottom left  
          case BOTTOM_LEFT:
            drawImage(imageData, 0, sizeOfImage.y - imageData.height);
            break;
            
          // Draw on bottom right corner  
          case BOTTOM_RIGHT:
            drawImage(imageData, sizeOfImage.x - imageData.width,
                      sizeOfImage.y - imageData.height);
            break;
        }
    }
   
  }
  
//  /**
//   * Organize the images. This function scans through the image key and 
//   * finds out the location of the images
//   */ 
//  private int [] organizeImages()
//  {
//    int[] locations = new int[imageKey_.size()];
//    String imageKeyValue;
//    for (int i = 0; i < imageKey_.size(); i++)
//    {
//      imageKeyValue = (String)imageKey_.get(i);
//      if (imageKeyValue.equals("Lock"))
//      {
//        // Draw he lock icon in top left corner. 
//        locations[i] = TOP_LEFT;
//      }
//      if (imageKeyValue.equals("Dirty"))
//      {
//        // Draw dirty flag indicator in the top right corner
//        locations[i] = TOP_RIGHT;
//      }
//      if (imageKeyValue.equals("Extract"))
//      {
//        // Draw the extract indicator in the top right corner. 
//        locations[i] = TOP_RIGHT;
//      }
//      if (imageKeyValue.equals("Owner"))
//      {
//        // Draw he lock icon in top left corner. 
//        locations[i] = BOTTOM_LEFT;
//      }
//      
//    }
//    return locations;
//  }
      

  /**
   * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
   * get the size of the object
   */
  @Override
protected Point getSize()
  {
    return sizeOfImage;
  }
  
  /**
   * Get the image formed by overlaying different images on the base image
   * 
   * @return composite image
   */ 
  public Image getImage()
  {
    return createImage();
  }


}
