/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 *
 */
public class WorkspaceUUIDService {

    private static WorkspaceUUIDService instance;

    /**
     * Get the singleton instance
     * 
     * @return the single {@link WorkspaceUUIDService}
     */
    public static WorkspaceUUIDService getInstance() {
        if (instance == null) instance = new WorkspaceUUIDService();

        return instance;
    }

    private File restoreFile;

    private WorkspaceUUIDService() {
    }

    /**
     * File where 
     * @param file
     */
    public void setRestoreLocation(File file) {
        restoreFile = file;
    }

    /**
     * Get the Workspace's UUID
     * 
     * @return UUID
     */
    public UUID getUUID() {
        UUID workspaceUuid = null;

            if (restoreFile != null && restoreFile.exists()) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(restoreFile));
                    workspaceUuid = UUID.fromString(reader.readLine());
                } catch (final IOException error) {
                } finally {
                    try {
                        if (reader != null)
                            reader.close();
                    } catch (final IOException ignored) {
                    }
                }
            }

            if (workspaceUuid == null)
                workspaceUuid = UUID.randomUUID();

            writeUUID(workspaceUuid);
            

        return workspaceUuid;
    }

    /**
     * @param workspaceUuid
     */
    private void writeUUID(UUID workspaceUuid) {
        if (restoreFile == null)
            return;
        
        FileWriter writer = null;
        try {
            writer = new FileWriter(restoreFile);
            writer.write(workspaceUuid.toString());
        } 
        catch (Exception ex) {
        }
        finally {
            try {
                writer.close();
            } catch (final IOException ignored) {
            }
        }
    }
}
