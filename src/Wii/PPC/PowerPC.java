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

package Wii.PPC;

import java.io.*;
import java.util.*;

/**
 *
 * @author spln
 */
public class PowerPC {
    
    public static long resolveAddress(long add, int relocationType) {
        return resolveAddress(add, relocationType, 0);
    }
    
    public static long resolveAddress(long add, int relocationType, long dec) {
        add = (add + dec > 0)   ? add + dec
                                : 0;
        long ret;
        switch (relocationType) {
            case 0x01:  
                //;break;
            default:  
                ret = add;
        }
        return ret;
    }
    
    public static long resolveMem1Address(long add, int relocationType) {
        return resolveMem1Address(add, relocationType, 0);
    }
    
    public static long resolveMem1Address(long add, int relocationType, long dec) {
        add = (add + dec > 0)   ? add + dec
                                : 0;
        if (add < MEM1_OFFSET || add > (MEM1_OFFSET + MEM1_SIZE)) {
            System.out.println("Address isn't from MEM1!");
            return -1;
        } else {
            return resolveAddress(add - MEM1_OFFSET, relocationType);
        }
    }
    
    public static int[] extractBinary(File in, long offset) throws IOException {
        FileInputStream fis     = new FileInputStream(in);
        BufferedInputStream bis = new BufferedInputStream(fis);
        List<Integer> list      = new ArrayList();
        
        bis.skip(offset);
        long op = 0x00;
        for (int i = 0; op != OP_BLR; /*wait for blr*/ ) {
            for (int j = 0; j < 4; i++, j++) {
                list.add(bis.read());
            }
            op  = list.get(i-4) << 24 | list.get(i-3) << 16 | list.get(i-2) << 8 |  list.get(i-1);
            // System.out.println(String.format("%08X", op));
        }
        
        int[] ret   = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        
        bis.close();
        fis.close();
        return ret;
    }
    
    public static int[] extractObject(File in, long offset) throws IOException {
        FileInputStream fis     = new FileInputStream(in);
        BufferedInputStream bis = new BufferedInputStream(fis);
        List<Integer> list      = new ArrayList();
        int buffer;
        
        bis.skip(offset);
        while ((buffer = fis.read()) != 0x00) {
            list.add(buffer);
        }
        
        int[] ret = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        
        bis.close();
        fis.close();
        return ret;
    }
    
    //--- Variable declarations
    public static final long OP_BLR         = 0x4E_80_00_20;
    public static final long MEM1_OFFSET    = 0x80_00_00_00;
    public static final long MEM1_SIZE      = 0x01_80_00_00;
    public static final long MEM2_OFFSET    = 0x90_00_00_00;
    public static final long MEM2_SIZE      = 0x04_00_00_00;
}
