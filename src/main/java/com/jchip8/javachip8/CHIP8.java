package com.jchip8.javachip8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
/*
 * This class ties everything together and handles all interaction between 
 * the cpu, ram, input/output, and the display. 
 * All hardware logic is implemented here 
 */
public class CHIP8 {
     
    private static CPU cpu = new CPU();
    private static RAM ram = new RAM();
    private static Display window = new Display();
    public static void main(String args[]) {
        String filename = "ROM/BRIX";
        byte[] rom = read_buffer(filename);
        // Define System Reserved Memory characters
        byte[] reserved = new byte[]{(byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90,
            (byte) 0xF0, (byte) 0x20, (byte) 0x60, (byte) 0x20, (byte) 0x20,(byte) 0x70,
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0,(byte)0xF0,
            (byte) 0x10, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x90,
            (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0x10, (byte) 0xF0, (byte) 0x80,
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0xF0, (byte) 0x80, (byte) 0xF0,
            (byte) 0x90, (byte) 0xF0, (byte) 0xF0, (byte) 0x10, (byte) 0x20, (byte) 0x40,
            (byte) 0x40, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0,
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0xF0,
            (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0xE0, (byte) 0x90,
            (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0xF0, (byte) 0x80, (byte) 0x80,
            (byte) 0x80, (byte) 0xF0, (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90,
            (byte) 0xE0, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0,
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80};
        //Load All Reserved Into Ram
        for (int i = 0; i < reserved.length; i++) {
            ram.set_ram(i, reserved[i]);
        }
        // Load All ROM starting at 0x200;
        int offset = 0x200;
        for (int i = 0; i < rom.length; i++) {
            ram.set_ram((i + offset), rom[i]);
        }
        short pc_val;
        boolean jumpflag = false; 
        while (true) {
            window.update();
            dumpAllReg(); 
            pc_val = cpu.get_pc();
            System.out.print("pc_val: " +pc_val +" ");
            // If for some reason the pc is about to be set over addressable
            // memory, break 
            if (pc_val == 4094) {
                break;
            }
            // All instructions are 2 bytes AKA 4 nibbles
            //first nibble holds instruction 
            byte byte_a = ram.get_ram(pc_val);
            byte byte_b = ram.get_ram(pc_val + 1);
            byte a_upper = (byte) ((byte_a & 0xF0) >> 4);
            byte a_lower = (byte) (byte_a & 0x0F);
            byte b_upper = (byte) ((byte_b & 0xF0) >> 4);
            byte b_lower = (byte) (byte_b & 0x0F);
            // This may or may not be garbage. 
            // Only use when the last 3 bits are the absolute address
            int address = (a_lower * (16 * 16));
            address += (b_upper * 16);
            address += b_lower;
            //Switch to decided command group 
            switch (a_upper) {
                case 0x0:
                    if (b_upper == 0xE) {
                        if (b_lower == 0x0) {
                           System.out.println("CLS");
                          CLS(); 
                        } else {
                            System.out.println("RTS"); 
                            RTS();
                        }
                    }else{
                    System.out.println(); 
                    }
                    break;
                case 0x1:
                    System.out.print("JMPABS Address:" + address);
                    JMPABS((short) address);
                    System.out.println(" PC now set to: " + cpu.get_pc());
                    jumpflag = true; 
                    System.out.println("jumpflag set to true"); 
                    break;
                case 0x2:
                    System.out.println("JMPSRT Address:" + address);
                    JSRT((short) address); 
                    break;
                case 0x3:
                    System.out.print("SKIPEQ VX:" + a_lower + " KK:" + byte_b + "Xval: "+ cpu.get_register(a_lower));
                    SKIPEQ(a_lower, byte_b);
                    break;
                case 0x4:
                    System.out.print("SKIPNE VX:" + a_lower + " KK:" + byte_b + " Xval: "+ cpu.get_register(a_lower));
                    SKIPNE(a_lower, byte_b);
                    break; 
                case 0x5:
                    System.out.print("SKIPEQ VX:" + a_lower + " VY:" + b_lower + "Xval: "+ cpu.get_register(a_lower)  
                            + "Yval: "+ cpu.get_register(b_lower));
                    SKIPEQREG(a_lower, b_lower);
                    break;
                case 0x6:
                    System.out.println("LDREGKK VX:" + a_lower + " KK:" + byte_b);
                    LDREGKK(a_lower, byte_b);
                    break;
                case 0x7:
                    System.out.println("ADDREGKK VX:" + a_lower + " KK:" + byte_b);
                    ADDREGKK(a_lower, byte_b);
                    break;
                case 0x8:
                    switch (b_lower) {
                        case 0x0:
                            System.out.println("LDRXRY VX:" + a_lower + " VY:" + b_upper);
                            LDRXRY(a_lower, b_upper);
                            break;
                        case 0x1:
                            System.out.println("ORRXRY VX:" + a_lower + " VY:" + b_upper);
                            ORRXRY(a_lower, b_upper);
                            break;
                        case 0x2:
                            System.out.println("ADDRXRY VX:" + a_lower + " VY:" + b_upper);
                            ANDRXRY(a_lower, b_upper);
                            break;
                        case 0x3:
                            System.out.println("XORRXRY VX:" + a_lower + " VY:" + b_upper);
                            XORRXRY(a_lower, b_upper);
                            break;
                        case 0x4:
                            System.out.println("ADDRXRY VX:" + a_lower + " VY:" + b_upper);
                            ADDRXRY(a_lower, b_upper);
                            break;
                        case 0x5:
                            System.out.println("SUBRXRY VX:" + a_lower + " VY:" + b_upper);
                            SUBRXRY(a_lower, b_upper);
                            break;
                        case 0x6:
                            System.out.println("LSR VX:" + a_lower + " VY:" + b_upper);
                            LSR(a_lower, b_upper);
                            break;
                        case 0x7:
                            System.out.println("SUBN VX:" + a_lower + " VY:" + b_upper);
                            SUBN(a_lower, b_upper);
                            break;
                        case 0xE:
                            System.out.println("LSL VX:" + a_lower + " VY:" + b_upper);
                            LSL(a_lower, b_upper);
                            break;
                    }
                    break;
                case 0x9:
                    System.out.print("SKIPNERXRY VX:" + a_lower + " VY:" + b_upper);
                    SKIPNERXRY(a_lower, b_upper);
                    break;
                case 0xA:
                    System.out.println("LDI N:" + address);
                    LDI((short) address);
                    break;
                case 0xB:
                    System.out.println("JMPV0 address:" + address);
                    JMPV0((short) address);
                    break;
                case 0xC:
                    System.out.println("RND VX: "+ a_lower +"KK: "+byte_b);
                    RND(a_lower, byte_b);
                    break;
                case 0xD:
                     System.out.println("DRW VX: "+a_lower+" VY:"+b_upper+" n: "+b_lower);
                     DRW((byte)a_lower, (byte)b_upper, (byte)b_lower); 
                    break;
                case 0xE:
                    switch (byte_b) {
                        case (byte) 0x9E:
                            System.out.println("SKP REG: " + a_lower);
                            break;
                        case (byte) 0xA1:
                            System.out.println("SKNP REG:" + a_lower);
                            break;
                    }
                    break;
                case 0xF:
                    switch (byte_b) {
                        case (byte) 0x07:
                            System.out.println("LDDT");
                            LDDT(a_lower);
                            break;
                        case (byte) 0x0A:
                            break;
                        case (byte) 0x15:
                            System.out.println("LDDTREG");
                            LDDTREG(a_lower);
                            break;
                        case (byte) 0x18:
                            System.out.println("LDST");
                            LDST(a_lower);
                            break;
                        case (byte) 0x1E:
                            System.out.println("ADDI");
                            ADDI(a_lower);
                            break;
                        case (byte) 0x29:
                            System.out.println("LD F, REG: " + a_lower);
                            break;
                        case (byte) 0x33:
                            System.out.println("LDB");
                            LDB(a_lower);
                            break;
                        case (byte) 0x55:
                            System.out.println("LDIVX");
                            LDIVX(a_lower);
                            break;
                        case (byte) 0x65:
                            System.out.println("LDVXI");
                            LDVXI(a_lower);
                            break;
                    }
                        
                    break; 
            }
            // Move forward two bytes to next instruction if this wasnt a jump 
            if(!jumpflag){
            cpu.set_pc((short) (cpu.get_pc() + 2));
            }
               jumpflag = false; 
        }

    }

    private static byte[] read_buffer(String filename) {
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

    private static void CLS() {
        window.clearBuffer();
    }

    private static void RTS() {
        short addr = cpu.pop_stack();
        System.out.println("STACK POP PC SET to: " + addr);
        cpu.set_sp((byte) (cpu.get_sp() - 1));
        cpu.set_pc(addr);
    }

    private static void JMPABS(short addr) {
        cpu.set_pc(addr);
    }

    private static void JSRT(short addr) {
        short stackaddr = cpu.get_pc();
        cpu.push_stack(stackaddr);
        System.out.println("STACK PUSH ADDRESS: "+addr); 
        cpu.set_sp((byte) (cpu.get_sp() + 1));
        cpu.set_pc(addr);
    }

    private static void SKIPEQ(byte VX, byte KK) {
        if (cpu.get_register(VX) == KK) {
            cpu.set_pc((short) (cpu.get_pc() + 2));
            System.out.println(" SKIPPED");
        }else{
            System.out.println(" NOT SKIPPED"); 
        }
    }

    private static void SKIPEQREG(byte VX, byte VY) {
        if (cpu.get_register(VX) == cpu.get_register(VY)) {
            cpu.set_pc((short) (cpu.get_pc() + 2));
            System.out.println(" SKIPPED");
        }else{
            System.out.println(" NOT SKIPPED"); 
        }
    }

    private static void SKIPNE(byte VX, byte KK) {
        if (cpu.get_register(VX) != KK) {
            cpu.set_pc((short) (cpu.get_pc() + 2));
            System.out.println(" SKIPPED");
        }else{
            System.out.println( " NOT SKIPPED"); 
        }
    }

    private static void LDREGKK(byte VX, byte KK) {
        System.out.println("CPU REG: "+VX+" VAL: "+KK);
        cpu.set_register(VX, KK);
    }

    private static void ADDREGKK(byte VX, byte KK) {
        cpu.set_register(VX, (byte) (cpu.get_register(VX) + KK));
    }

    private static void LDRXRY(byte VX, byte VY) {
        cpu.set_register(VX, cpu.get_register(VY));
    }

    private static void ORRXRY(byte VX, byte VY) {
        byte x = cpu.get_register(VX);
        byte y = cpu.get_register(VY);
        byte result = (byte) (x | y);
        cpu.set_register(VX, result);
    }

    private static void ANDRXRY(byte VX, byte VY) {
        byte x = cpu.get_register(VX);
        byte y = cpu.get_register(VY);
        byte result = (byte) (x & y);
        cpu.set_register(VX, result);
    }

    private static void XORRXRY(byte VX, byte VY) {
        byte x = cpu.get_register(VX);
        byte y = cpu.get_register(VY);
        byte result = (byte) (x ^ y);
        cpu.set_register(VX, result);
    }

    private static void ADDRXRY(byte VX, byte VY) {
        byte x = cpu.get_register(VX);
        byte y = cpu.get_register(VY);
        int result = x + y;
        if (result > 255) {
            byte lowest8 = (byte) (result << 24);
            cpu.set_register(VX, lowest8);
            cpu.set_register((byte) 0xF, (byte) 1);
        } else {
            cpu.set_register(VX, (byte) result);
            cpu.set_register((byte) 0xF, (byte) 0);
        }
    }

    private static void SUBRXRY(byte VX, byte VY) {
        if (VX > VY) {
            cpu.set_register((byte) 0xF, (byte) 1);
        } else {
            cpu.set_register((byte) 0xF, (byte) 0);
        }
        byte result = (byte) (cpu.get_register(VX) - cpu.get_register(VY));
        cpu.set_register(VX, result);

    }

    private static void LSR(byte VX, byte VY) {
        byte lsb = (byte) cpu.get_register(VY);
        lsb = (byte) (lsb & 0b00000001);
        cpu.set_register((byte) 0xF, lsb);
        byte post_shift_vx = (byte) ((byte) cpu.get_register(VY) >> 1);
        cpu.set_register(VX, post_shift_vx);
    }

    private static void LSL(byte VX, byte VY) {
        byte msb = (byte) cpu.get_register(VY);
        msb = (byte) ((byte) (msb & 0b10000000 >> 7));
        cpu.set_register((byte) 0xF, msb);
        byte post_shift_vx = (byte) ((byte) cpu.get_register(VY) << 1);
        cpu.set_register(VX, post_shift_vx);
    }

    private static void SUBN(byte VX, byte VY) {
        byte x = cpu.get_register(VX);
        byte y = cpu.get_register(VY);
        if (y > x) {
            cpu.set_register((byte) 0xF, (byte) 1);
        } else {
            cpu.set_register((byte) 0xF, (byte) 0);
        }
        byte result = (byte) (y - x);
        cpu.set_register(VX, result);
    }

    private static void SKIPNERXRY(byte VX, byte VY) {
        byte x = cpu.get_register(VX);
        byte y = cpu.get_register(VY);
        if (x != y) {
            cpu.set_pc((short) (cpu.get_pc() + 2));
            System.out.println(" SKIPPED");
        }else{
            System.out.println(); 
        }
    }

    private static void LDI(short I) {
        cpu.set_i_register(I);
    }

    private static void JMPV0(short NNN) {
        byte V0 = cpu.get_register((byte) 0x0);
        short new_pc = (short) (NNN + V0);
        cpu.set_pc(new_pc);
    }

    private static void RND(byte VX, byte KK) {
        Random randgen = new Random();
        byte rand = (byte) (randgen.nextInt(255));
        byte result = (byte) (rand & KK);
        cpu.set_register(VX, result);
    }

    private static void SKP() {
        System.out.println("Unimplemented OpCode");
    }

    private static void SKNP() {
        System.out.println("Unimplemented OpCode");
    }

    private static void DRW(byte VX, byte VY, byte n) {
        // Save the location we are pulling from 
        short init_location = cpu.get_i_register();
        // Pull a byte array for the specified sprite 
        byte [] sprite  = new byte [n]; 
        for(int i = 0; i<n; i++){
            sprite[i] = ram.get_ram(init_location+i); 
        }
        System.out.println("Drawing At X: "+ (cpu.get_register(VX))+ " Y: " +(cpu.get_register(VY)));
        // Parse bits, display to screen 
        for(int j =0; j<n; j++){
            byte thisbyte = sprite[j];
            for(int k =0; k<8; k++){
                byte onoff = (byte)((thisbyte>>(7-k))& 0b00000001); 
                if(onoff == 0b1){
                    window.drawPixel((cpu.get_register(VX)*15)+(k*15), (cpu.get_register(VY)*15)+(+j*15));
                }
            } 
        }
    }

    private static void LDDT(byte VX) {
        cpu.set_dt(cpu.get_register(VX));
    }

    private static void LDDTREG(byte VX) {
        cpu.set_register(VX, cpu.get_dt());
    }

    private static void LDST(byte VX) {
        cpu.set_st(cpu.get_register(VX));
    }

    private static void LDK() {
        System.out.println("Unimplemented OpCode");
    }

    private static void LDF() {
        System.out.println("Unimplemented OpCode");
        System.out.println("Unimplemented OpCode");
    }

    private static void ADDI(byte VX) {
        byte x = cpu.get_register(VX);
        short i = cpu.get_i_register();
        short answer = (short) (i + x);
        cpu.set_i_register(answer);
    }

    private static void LDB(byte VX) {
        byte x = cpu.get_register(VX);
        int hundreds = (int) ((int) x / 100);
        int tens = (int) (((int) x % 100) / 10);
        int ones = (int) ((int) x % 10);
        ram.set_ram(cpu.get_i_register(), (byte) hundreds);
        ram.set_ram(cpu.get_i_register() + 1, (byte) tens);
        ram.set_ram(cpu.get_i_register() + 2, (byte) ones);
    }

    private static void LDIVX(byte VX) {
        for (byte k = 0; k < VX; k++) {
            byte value = cpu.get_register(k);
            ram.set_ram(cpu.get_i_register() + k, value);
        }
    }

    private static void LDVXI(byte VX) {
        for (byte k = 0; k < VX; k++) {
            byte value = ram.get_ram(cpu.get_i_register() + k);
            cpu.set_register(k, value);
        }
    }
    private static void dumpAllReg(){
        System.out.println("___________________REG DUMP_____________________");
        for(int i =0; i<3; i++){
            System.out.println("REG: "+ i + " = "+cpu.get_register((byte)i));
        }
        System.out.println("I: " + cpu.get_i_register()); 
        System.out.println("_________________END REG DUMP___________________");
    
    }

}
