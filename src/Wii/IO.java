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
// Homemade I/O class   //
//----------------------//

package Wii;

import java.io.*;
import Wii.Relocators.*;


/**
 *
 * @author spln
 */
public class IO extends RandomAccessFile {
    
    public String getPath;
    
    public IO (String name, String mode) throws IOException  {       
        super(name, mode);
        getPath = name;
    }
    
    public IO (File file, String mode) throws IOException {    
        super(file, mode);
        getPath = file.getAbsolutePath();
    }
    
    public long readUintX(int X) throws IOException {
        switch (X) {
            case 1:
                return readUint8();
            case 2:
                return readUint16();
            case 3:
                return readUint24();
            case 4:
                return readUint32();
            default:
                long l = 0;
                for(--X; X >= 1; X--) {
                    l |= ((long)this.read() << 8*X);
                }
                return l|this.read();
        }
    }
    
    public int readUint8() throws IOException {
        return this.read();
    }
    
    public int readUint16() throws IOException {
        return (this.read() << 8)|this.read();
    }
    
    public int readUint24() throws IOException {
        return (this.read() << 16)|(this.read() << 8)|this.read();
    }
    
    public long readUint32() throws IOException {
        return ((long)this.read() << 24)|(long)this.readUint24();
    }
    
    public String readString() throws IOException {
        int chr; String buff = "";
        while ((chr=this.readUint8())!= 0x00) {
            buff += (char)chr;
        }
        return buff;
    }
    
    public String readString(int lim) throws IOException {
        int i, chr; String buff = "";
        for (i = 0; (chr=this.readUint8()) != 0x00 && i < lim; i++) {
            buff += (char)chr;
        }
        return buff;
    }
    
    public RelocationTable readRelocationTable(int type) throws IOException {
        return readRelocationTable(type, 0);
    }
    
    public RelocationTable readRelocationTable(int type, long dec) throws IOException {
        return new RelocationTable(this.readUint32()+dec, this.readUint32(), type);
    }
    
    public Imports readImports() throws IOException {
        return readImports(0);
    }
    
    public Imports readImports(long dec) throws IOException {
        return new Imports(this.readUint32()+dec, this.readUint32(), "RSO");
    }
        
}

