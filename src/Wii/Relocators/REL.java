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
// REL files class      //
//----------------------//

package Wii.Relocators;

import Wii.IO;
import Wii.PPC.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author spln
 */
public class REL extends Relocator {
    
    public REL(File in) {
        super(in);
    }
    
    @Override
    public void load() {
        // Load REL header, sections, relocation tables
        IO file;
        try {           
                //--------------------- HEADER ---------------------//                
                file = new IO(fileLocation, "r");
                //file.seek(pos);
                /*0x00 (uint32)*/ identifier       =   file.readUint32();
                /*0x04 (uint32)*/ nextLink         =   file.readUint32();
                /*0x08 (uint32)*/ prevLink         =   file.readUint32();
                /*0x0C (uint32)*/ sectionCount     =   file.readUint32(); 
                /*0x10 (uint32)*/ sectionTable     =   file.readUint32();
                /*0x14 (uint32)*/ nameOffset       =   file.readUint32();
                /*0x18 (uint32)*/ nameSize         =   file.readUint32();
                /*0x1C (uint32)*/ version          =   file.readUint32();

                /*0x20 (uint32)*/ bssSize          =   file.readUint32();
                /*0x24 (uint32)*/ relOffset        =   file.readUint32();
                /*0x28 (uint32)*/ importsOffset    =   file.readUint32(); 
                /*0x2C (uint32)*/ importsSize      =   file.readUint32();
                
                /*0x30 (uint8) */ hasProlog        =   file.readUint8();
                /*0x31 (uint8) */ hasEpilog        =   file.readUint8();
                /*0x32 (uint8) */ hasUnresolved    =   file.readUint8();
                /*0x33 (uint8) */ hasBss           =   file.readUint8();
                /*0x34 (uint32)*/ prologOffset     =   file.readUint32();
                /*0x38 (uint32)*/ epilogOffset     =   file.readUint32();
                /*0x3C (uint32)*/ unresolvedOffset =   file.readUint32();
                if (version >= 2) {
                /*0x40 (uint32)*/ moduleAlign      =   file.readUint32();
                /*0x44 (uint32)*/ bssAlign         =   file.readUint32();
                }
                if (version >= 3) {
                /*0x48 (uint32)*/ fixSize          =   file.readUint32();
                }
                
                //--- Sections
                file.seek(sectionTable);
                section = new Section[(int)sectionCount];
                for (int i = 0; i < sectionCount; i++) {
                    section[i] = new Section(file.readUint32(), file.readUint32());
                    switch (i) {
                        case 1: section[1].setType(Section.ASM_SECTION)         ;break;
                        case 2: section[2].setType(Section.CTORS_SECTION)       ;break;
                        case 3: section[3].setType(Section.DTORS_SECTION)       ;break;
                        case 4: section[4].setType(Section.CONSTANTS_SECTION)   ;break;
                        case 5: section[5].setType(Section.OBJECTS_SECTION)     ;break;
                        case 6: section[6].setType(Section.BSS_SECTION)         ;break;
                        default: section[i].setType(Section.UNKNOWN_SECTION);
                    }
                }
                
            
                //--- Optional
                file.seek(nameOffset);
                nameString = file.readString((int)nameSize);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String[] getRelocations() throws IOException {
        return new relTable().read(relOffset, fileLocation);
    }
    
    //--- Variables declaration
    public long nextLink, prevLink, bssSize, relOffset, moduleAlign, bssAlign, fixSize;
}


class relTable extends RelocatedData {
    
    public relTable() {
        super(new int[] {2,1,1,4});
    }
    
    public String[] read(long offset, File in) throws IOException {
       List<String> ret = new ArrayList();
       String buff;
       int c_type = 0;
       for (long i = offset; c_type != Assembly.R_RVL_STOP2; i+= entrySize) {
           buff = readEntry(i, in);
           ret.add(buff);
           c_type = Integer.parseInt(buff.split(":")[1], 16);
           System.out.println("Entry added ("+c_type+"): "+buff);
       }
       
       return ret.toArray(new String[ret.size()]);
    }
    
    
     
    
}