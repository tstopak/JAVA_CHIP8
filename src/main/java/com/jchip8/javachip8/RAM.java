
package com.jchip8.javachip8;
/*
 * This class is a representation of the RAM 
 * The interface provides no logic only read and write
 * Logic is implemented in CHIP8.java 
 */
public class RAM {
    //Create a byte array that holds 4KB
    byte [] ram = new byte[0xFFF]; 
    public RAM(){
        
    }
    public void set_ram(int address, byte value){
        ram[address]=value; 
    }
    public byte get_ram(int address){
        return ram[address]; 
    }
}
