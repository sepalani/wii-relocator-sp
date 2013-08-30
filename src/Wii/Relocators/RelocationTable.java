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

//--------------------------//
// Relocation table class   //
//--------------------------//

package Wii.Relocators;

import java.io.*;

/**
 *
 * @author spln
 */
public class RelocationTable extends RelocatedData {
    
    public RelocationTable(long offset, long size, int type) {
        super(new int [] {4, -2, 1, 1, 4});
        tableOffset     = offset;
        tableSize       = size;
        relocationType  = type;        
    }
    
    public String[] read(File in) throws IOException {
        String[] ret = readEntries(this.tableOffset, this.tableSize, in);
        String[] str;
        length      = ret.length;
        r_offset    = new String[length];
        r_info[0]   = new String[length];
        r_info[1]   = new String[length];
        r_info[2]   = new String[length];
        r_addend    = new String[length];
        
        for (int i = 0; i < length; i++) {
            str = ret[i].split(":");
            r_offset[i] = str[0];
            r_info[0][i]= str[1]; // Skipped
            r_info[1][i]= str[2]; // ID
            r_info[2][i]= str[3]; // Relocation type
            r_addend[i] = str[4];
        }
        return ret;
    }  
    
    //--- Table Properties
    public static final int INTERNALS_RELOCATION  = 0x00;
    public static final int EXTERNALS_RELOCATION  = 0x01;
    public int length, relocationType; 
    
    //--- Variables declaration
    public final long tableOffset, tableSize;
    public String[] r_offset, r_addend;
    public String[][] r_info = new String[3][];
          
} // [END] class RelocationTable
