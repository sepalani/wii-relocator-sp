/** Copyright (C) 2013  SPLN (sepalani)
    This file is part of Wii Relocator SP.

    Wii Relocator SP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Wii Relocator SP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Wii Relocator SP.  If not, see <http://www.gnu.org/licenses/>.
 */

//----------------------//
// Relocated data class //
//----------------------//

package Wii.Relocators;

import Wii.IO;
import java.io.*;
import java.util.*;

/**
 *
 * @author spln
 */
public class RelocatedData {
    
    public RelocatedData(int[] prop) {
        this.setEntryProperties(prop);      
    }
    
    public void setEntryProperties(int[] prop) {
        this.entryProperties = prop;
        int size = 0;
        
        for (int i = 0; i < this.entryProperties.length; i++) {
                size += Math.abs(this.entryProperties[i]);
        }
        this.entrySize = size;
    }
    
    public int[] getEntryProperties() {
        return this.entryProperties;
    }
    
    public String readEntry(long offset, File in) throws IOException {
        if (in.length() < offset + this.entrySize) {
            throw new IOException("File doesn't match with tableOffset/Size!");
        } else if (!in.exists() || !in.isFile()) {
            throw new IOException("Can't access input file!");
        } else if (this.entrySize <= 0) {
            throw new IOException("Invalid entry properties!");
        }
        
        String ent = "";
        IO file = new IO(in,"r");
        file.seek(offset);
        
        for (int i = 0; i < entryProperties.length; i++) {
            if (entryProperties[i] < 0) {
                ent += "skipped("+String.format("%0"+2*-entryProperties[i]+"X",file.readUintX(-entryProperties[i]))+")"; // Skipped values
            } else {
                ent += String.format("%0"+2*entryProperties[i]+"X",file.readUintX(entryProperties[i]));
            } 
                
            if (i+1 != entryProperties.length) {
                    ent += ":";
            }
        }
        
        file.close();
        return ent;
    }
    
    public String[] readEntries(long offset, int n, File in) throws IOException {
        if (in.length() < offset + n*this.entrySize) {
            throw new IOException("File doesn't match with tableOffset/Size!");
        } else if (!in.exists() || !in.isFile()) {
            throw new IOException("Can't access input file!");
        } else if (this.entrySize <= 0) {
            throw new IOException("Invalid entry properties!");
        }
        
        List<String> ret = new ArrayList();
        String       ent;
        IO          file = new IO(in,"r");
        file.seek(offset);
        
        for (int i = 0; i < n; i++) {
            ent = "";
            
            for (int j = 0; j < entryProperties.length; j++) {
                if (entryProperties[j] < 0) {
                    ent += "skipped("+String.format("%0"+2*-entryProperties[j]+"X",file.readUintX(-entryProperties[j]))+")"; // Skipped values
                } else {
                    ent += String.format("%0"+2*entryProperties[j]+"X",file.readUintX(entryProperties[j]));
                } 
                
                if (j+1 != entryProperties.length) {
                    ent += ":";
                }
            }
            
            ret.add(ent);
            // System.out.println("Entry added: "+ent);
        }        
        
        file.close();
        return ret.toArray(new String[ret.size()]);
    }
    
    public String[] readEntries(long offset, long size, File in) throws IOException {
        if (in.length() < offset + size) {
            throw new IOException("File doesn't match with tableOffset/Size!");
        } else if (!in.exists() || !in.isFile()) {
            throw new IOException("Can't access input file!");
        } else if (this.entrySize <= 0) {
            throw new IOException("Invalid entry properties!");
        }
        
        List<String> ret = new ArrayList();
        String       ent;
        IO          file = new IO(in,"r");
        file.seek(offset);
        
        int i;
        for (i = 0; i < size; i += this.entrySize) {
            ent = "";
            
            for (int j = 0; j < entryProperties.length; j++) {
                if (entryProperties[j] < 0) {
                    ent += "skipped("+String.format("%0"+2*-entryProperties[j]+"X",
                                      file.readUintX(-entryProperties[j]))+")"; // Skipped values
                } else {
                    ent += String.format("%0"+2*entryProperties[j]+"X",
                              file.readUintX(entryProperties[j]));
                } 
                
                if (j+1 != entryProperties.length) {
                    ent += ":";
                }
            }
            
            ret.add(ent);
            // System.out.println("Entry added: "+ent);
        }        
        
        file.close();
        // System.out.println("Size: "+i+" vs.(org) "+size);
        return ret.toArray(new String[ret.size()]);
    }
    
    //--- Variables declaration
    // public String[] entry;
    public int entrySize;
    public int[] entryProperties;
    
} // [END] class RelocatedData
