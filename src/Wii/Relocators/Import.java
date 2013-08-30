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
    along with RsoTool.  If not, see <http://www.gnu.org/licenses/>.
 */

//----------------------//
// Single import class  //
//----------------------//

package Wii.Relocators;

import Wii.IO;
import java.io.*;
import java.util.*;


/**
 *
 * @author spln
 */
public class Import extends RelocatedData {
    /*
    public Import(long offset, long size) {
        super(new int[] {4, 4, 4}); // Rso/Sel Import
    }
    */
    public Import(int id, long offset, String type) {
        super(type.equals(relFile) ? new int[] {4, 4}       // Rel Import
                                   : new int[] {4, 4, 4} ); // Rso/Sel Import
        this.ID         = id;
        this.position   = offset;
        this.fileType   = type;
    }
    
    public Import(int id, String[] entry) {
        super(new int[] {});
        this.ID         = id;
        this.position   = -1; // Already has an entry, doesn't need to use read();
        
        switch(entry.length) {
            case 2:  // REL Import
                // TODO add relImport support
                this.setEntryProperties(new int[] {4, 4});
                this.fileType           = relFile;
                ;break;
                
            case 4:  // RSO Import + Name
                this.name           = entry[3];
            case 3:  // RSO Import
                this.nameOffset     = Long.parseLong(entry[0].replaceFirst("0x", ""), 16);
                this.sectionType    = Long.parseLong(entry[1].replaceFirst("0x", ""), 16);
                this.contentOffset  = Long.parseLong(entry[2].replaceFirst("0x", ""), 16);
                
                this.setEntryProperties(new int[] {4, 4, 4});
                this.fileType           = rsoFile;
                ;break;
        }
    }   
    
    public String read(File in) throws IOException {
        if (this.position < 0)    return null;
        
        String ret      = readEntry(position, in);
        String[] str    = ret.split(":");
        this.nameOffset     = Long.parseLong(str[0].replaceFirst("0x", ""), 16);
        this.sectionType    = Long.parseLong(str[1].replaceFirst("0x", ""), 16);
        this.contentOffset  = Long.parseLong(str[2].replaceFirst("0x", ""), 16);       
        return ret;
    }
    
    public void getName(long nameOffset, File in) throws IOException {
        IO file = new IO(in, "r");
        file.seek(this.nameOffset + nameOffset);
        
        this.name = file.readString();
        file.close();
    }
    
    public int[] getRelocation(RelocationTable table) {
        List<Integer> list = new ArrayList();
        for (int i = 0; i < table.r_offset.length; i++) {
            if (Long.parseLong(table.r_info[1][i], 16) == this.ID) {
                list.add(i);
                this.r_count += 1;
            }
        }
        this.r_count = list.size();
        if (list.isEmpty()) return null;
        
        this.r_offset       = new long[this.r_count];
        this.r_addend       = new long[this.r_count];
        this.relocationId   = new long[this.r_count];
        this.relocationType = new long[this.r_count];
        int[] ret = new int[list.size()];
        for (int i = 0, j; i < list.size(); i++) {
            j = list.get(i);
            ret[i] = Integer.valueOf(j);
            
            this.r_offset[i]       = Long.parseLong(table.r_offset[j],  16);
            this.r_addend[i]       = Long.parseLong(table.r_addend[j],  16);
            this.relocationId[i]   = Long.parseLong(table.r_info[1][j], 16);
            this.relocationType[i] = Long.parseLong(table.r_info[2][j], 16);
        }
        return ret;
    }
    
    @Override
    public String toString() {
        return (name != null) ? name : "<The Nameless One>";
    }
    
    //--- Static methods
    public static Import[] entriesToArray(String[] imp) {
        Import[] ret = new Import[imp.length];
        for (int i = 0; i < imp.length; i++) {
            ret[i] = new Import(i, imp[i].split(":"));
        }
        
        return ret;
    }
    
    //--- Variable declaration
    public String name, fileType;
    public long position, size, // Import position in the file
                nameOffset,     // Import name offset
                sectionType,    // Import section type (always 0x00)
                contentOffset;  // Import content offset (within section)
    public int      ID, r_count = 0; // Relocation count
    public long[]   r_offset,        // Offset which contains symbol address
                    r_addend,        // Import content offset (within section)
                    relocationId,    // <=> r_info[1] Id
                    relocationType;  // <=> r_info[2] <=> sectionType
                
    // Relocator file
    public final static String rsoFile  = "RSO";
    public final static String relFile  = "REL";
    public final static String selFile  = "SEL"; // same as RSO 
       
}

//--- Import File Format
//--- RSO (Size: 0xC)
// - (0x00) uint32  nameOffset
// - (0x04) uint32  sectionType
// - (0x08) uint32  contentOffset

//--- REL (Size: 0x8)
// - (0x00) uint32  moduleID
// - (0x04) uint32  contentOffset => Relocated Data
//--- REL Relocated Data (Size: 0x8)
// - (0x00) uint16  r_offset
// - (0x02) uint8   r_info[2] => relocationType
// - (0x03) uint8   r_info[1] => relocationId
// - (0x04) uint16  r_addend
