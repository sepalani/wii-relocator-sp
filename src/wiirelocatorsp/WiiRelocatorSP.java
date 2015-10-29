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

package wiirelocatorsp;

import java.io.*;
import Wii.IO;
import Wii.Relocators.*;
import UI.*;

/**
 *
 * @author spln
 */

public class WiiRelocatorSP {
    
    public static void main(String[] args) {
        // DEBUG MAIN CLASS
        
        /*
        File debugFileRso = new File("/home/spln/Bureau/Jeux & Emulation/Jeux/Wii/MHTri/Tree/01 (rso)/net_data.rso");
        File debugFileRsoRam = new File("/home/spln/Bureau/Jeux & Emulation/Jeux/Wii/MHTri/Tree/01 (rso)/net_data/ram.raw");
        File debugFileRel = new File("/home/spln/Bureau/Jeux & Emulation/Outils/RSO Tools/test/ft_captain.rel");
        RSO rsoDebug = new RSO(debugFileRsoRam);
        REL relDebug = new REL(debugFileRel);
        String[] parse;
        */
        //--- strcpy
        /*long add = Long.parseLong("4BAE0CC1",16);
        long r_offset = Long.parseLong("8097EF14", 16);
        String str = String.format("%08X", Wii.PPC.
                PowerPC.resolveAddress(add, 0x0A, r_offset));
        System.out.println(str);
        
        add = Long.parseLong("4BAE0CD5",16);
        r_offset = Long.parseLong("8097EF00", 16);
        str = String.format("%08X", Wii.PPC.
                PowerPC.resolveAddress(add, 0x0A, r_offset));
        System.out.println(str);*/
        
        GuiB1.main(args);
        /*
        try {
            
            relDebug.load();
            relDebug.getRelocations();
            
            rsoDebug.load(0x97D080, -0x80000000);
            rsoDebug.getInternalsRelocation();
            rsoDebug.getExternalsRelocation();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        /*
        // Get IRT
        for (int i = 0; i < rsoDebug.relocation[0].length; i++) {
                System.out.println("r_offset: "+rsoDebug.relocation[0].r_offset[i]+" | "+
                                   "r_info: "  +rsoDebug.relocation[0].r_info[i]  +" | "+
                                   "r_addend: "+rsoDebug.relocation[0].r_addend[i]+" | ");
        }
        
        // Get ERT
        for (int i = 0; i < rsoDebug.relocation[1].length; i++) {
                System.out.println("r_offset: "+rsoDebug.relocation[1].r_offset[i]+" | "+
                                   "r_info: "  +rsoDebug.relocation[1].r_info[i]  +" | "+
                                   "r_addend: "+rsoDebug.relocation[1].r_addend[i]+" | ");
        }
        */
        // Get Import
        /*long newOffset; IO file; int[] asm;
        try {
            file = new IO(debugFileRsoRam,"r");
            newOffset = Long.parseLong(rsoDebug.relocation[1].r_offset[0].replaceFirst("0x", ""), 16);
            System.out.println("Offset (0): 0x"+Long.toHexString(newOffset & 0xFF_FF_FF));
            file.seek(newOffset & 0xFF_FF_FF);
            newOffset = file.readUint32();
            System.out.println("Offset (1): 0x"+Long.toHexString(newOffset & 0xFF_FF_FF));
            asm = Imports.getBinary(newOffset & 0xFF_FF_FF, debugFileRsoRam, 0x01);
            
            FileOutputStream fos = new FileOutputStream(new File("/home/spln/Bureau/Jeux & Emulation/Jeux/Wii/MHTri/Tree/01 (rso)/net_data/ram.dump.001"));
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            for (int i = 0; i < asm.length; i++) {
                bos.write(asm[i]);
            }
            bos.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }*/
            
        
        
        
    }    
    
}
