
package com.jchip8.debugsuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Disassembler {
    public static void main(String[] args){
        // Read rom as byte array 
        byte[] romfile = read_rom("ROM/Fishie.ch8");
        //Decode the byte array 
        decode(romfile); 
    }
    private static byte[] read_rom(String filename) {
        File infile = new File(filename);
        FileInputStream in;
        byte[] bytes = new byte[(int) infile.length()];
        try {
            in = new FileInputStream(infile);
            try {
                in.read(bytes);
            } catch (IOException e) {
                System.out.println("Error reading file");
            }

        } catch (FileNotFoundException e) {
            System.out.println("File Not Found!!");
        }
        return bytes;
    }
    private static void decode(byte[] rom){
     for(int i=0; i<rom.length; i=i+2){
         // All instructions are 2 bytes AKA 4 nibbles
         //first nibble holds instruction  
         byte byte_a = rom[i];
         byte byte_b = rom [i+1]; 
         byte a_upper = (byte)((byte_a & 0xF0)>>4); 
         byte a_lower = (byte)(byte_a & 0x0F);
         byte b_upper = (byte)((byte_b & 0xF0)>>4); 
         byte b_lower = (byte)(byte_b & 0x0F);
         // This may or may not be garbage. 
         // Only use when the last 3 bits are the absolute address
         int address = (a_lower*(16*16)); 
         address+=(b_upper*16); 
         address+=b_lower;
         //Switch to decided command group 
         switch(a_upper){
             case 0x0:
                 if(b_upper==0xE){
                 if(b_lower==0x0){
                    System.out.println("CLS");
                 }else{
                    System.out.println("RET"); 
                 }
                 } 
                 break;
             case 0x1:  
                 System.out.println("JMP(ABS) NNN: " +address);
                 break;
             case 0x2:
                 System.out.println("JMP(SRT) NNN:" +address);
                 break;
             case 0x3: 
                 System.out.println("SKIP.EQ REG: "+a_lower+" KK: "+byte_b);
                 break;
             case 0x4:
                 System.out.println("SKIP.NE REG: "+a_lower+" KK: "+byte_b);
                 break;
             case 0x5:
                 System.out.println("SKIP.EQ REGX: "+a_lower+" REGY: "+b_lower);
                 break;
             case 0x6:
                 System.out.println("LD REG: "+a_lower+" KK: "+byte_b);
                 break;
             case 0x7: 
                 System.out.println("ADD REG: "+a_lower+" KK: "+byte_b);
                 break;
             case 0x8:
                 switch(b_lower){
                     case 0x0:
                         System.out.println("LD REGX: "+a_lower+" REGY: "+b_upper);
                         break; 
                     case 0x1: 
                         System.out.println("OR REGX: "+a_lower+" REGY: "+b_upper); 
                         break;
                     case 0x2: 
                         System.out.println("AND REGX: "+a_lower+" REGY: "+b_upper);
                         break;
                     case 0x3: 
                         System.out.println("XOR REGX: "+a_lower+" REGY: "+b_upper);
                         break;
                     case 0x4: 
                         System.out.println("ADD REGX: "+a_lower+" REGY: "+b_upper);
                         break;
                     case 0x5: 
                         System.out.println("SUB REGX: "+a_lower+" REGY: "+b_upper);
                         break;
                     case 0x6:
                         System.out.println("LSR REGX: "+a_lower+" (OPTIONAL) REGY: "+b_upper);
                         break;
                     case 0x7: 
                         System.out.println("SUBN REGX: "+a_lower+" REGY: "+b_upper);
                         break;
                     case 0xE: 
                         System.out.println("LSL REGX: "+a_lower+" (OPTIONAL) REGY: "+b_upper);
                         break;
                 }
                 break;
             case 0x9: 
                 System.out.println("SKIP.NE REGX: "+a_lower+" REGY: "+b_upper);
                 break;
             case 0xA:
                 System.out.println("LD I NNN: "+address);
                 break;
             case 0xB:
                 System.out.println("JMP V0, NNN: "+address);
                 break;
             case 0xC: 
                 System.out.println("RND REG: "+a_lower+" KK: "+byte_b);
                 break;
             case 0xD:
                 System.out.println("DRW REGX: "+a_lower+" REGY: "+b_upper+" N: "+b_lower);
                 break;
             case 0xE: 
                 switch(byte_b){
                     case (byte)0x9E:
                         System.out.println("SKP REG: "+a_lower); 
                         break; 
                     case (byte)0xA1:
                         System.out.println("SKNP REG:"+a_lower); 
                         break; 
                 }
                 break;
             case 0xF: 
                 switch(byte_b){
                     case (byte)0x07:
                         System.out.println("LD REG: "+a_lower+", DT");
                         break; 
                     case (byte)0x0A:
                         System.out.println("LD REG:"+a_lower+", K"); 
                         break; 
                     case (byte)0x15:
                         System.out.println("LD DT, REG: "+a_lower);
                         break; 
                     case (byte)0x18: 
                         System.out.println("LD ST, REG: "+a_lower);
                         break; 
                     case (byte)0x1E: 
                         System.out.println("ADD I, REG: "+a_lower);
                         break; 
                     case (byte)0x29: 
                         System.out.println("LD F, REG: "+a_lower);
                         break; 
                     case (byte)0x33:
                         System.out.println("LD B, REG: "+a_lower);
                         break;
                     case (byte)0x55: 
                         System.out.println("LD [I], REG: "+a_lower);
                         break; 
                     case (byte)0x65: 
                         System.out.println("LD REG: "+a_lower+", [I]");
                         break; 
                 }
                 break;
         }
     }   
    }
}
