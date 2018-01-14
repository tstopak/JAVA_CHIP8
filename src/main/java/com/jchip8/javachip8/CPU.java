package com.jchip8.javachip8;
import java.util.Timer; 
import java.util.TimerTask; 
/*
 * This is the class that emulates the virtual machine's cpu,  
 * this is a representation of the hardware, not the logic, 
 * all logic is in CHIP8.java
 */
public class CPU {
   private byte [] registers = new byte[16]; 
   private short i_register=0;
   private short[] stack = new short[16]; 
   private short pc = 0x200; 
   private byte sp =0; 
   private byte dt = 0; 
   private byte st=0; 
   private Timer clockdt = new Timer();
   private boolean dt_timer_on = false; 
   private boolean st_timer_on = false; 
   private Timer clockst = new Timer(); 
   private final TimerTask decrement_dt = new TimerTask(){
       @Override 
       public void run(){
           dt--; 
       }
   };
   private final TimerTask decrement_st = new TimerTask(){
       @Override 
       public void run(){
           st--; 
       }
   };
   public CPU(){
   
   }
   public void set_register(byte regnum, byte value){
       registers[regnum] = value; 
   }
   public byte get_register(byte regnum){
       return registers[regnum]; 
   }
   public void set_i_register(short value){
       i_register = value; 
   }
   public short get_i_register(){
       return i_register; 
   }
   public void set_pc(short value){
       pc = value; 
   }
   public short get_pc(){
       return pc; 
   }
   public void set_sp(byte value){
       sp = value; 
   }
   public byte get_sp(){
       return sp; 
   }
   public void set_dt(byte value){
       dt = value; 
   }
   public byte get_dt(){
       return dt; 
   }
   public void set_st(byte value){
       st = value; 
   }
   public byte get_st(){
       return st; 
   }
   public void set_stack(byte pos, short value){
       stack[pos]=value; 
   }
   public short get_stack(byte pos){
      return stack[pos]; 
   }
   public short pop_stack(){
       short top = stack[0];  
       for(int i=0; i <stack.length-1; i++){
           stack[i] = stack[i+1]; 
       } 
       return top; 
   }
   public void push_stack(short value){
        for(int i =1; i<stack.length; i++){
            stack[i] = stack [i-1]; 
        } 
        stack[0] = value; 
   }
   public void starttimer_dt(){
       dt_timer_on = true; 
       clockdt.scheduleAtFixedRate(decrement_dt, 0, 16);
   }
   public void stoptimer_dt(){
       dt_timer_on = false; 
       clockdt.cancel(); 
   }
   public void starttimer_st(){
       st_timer_on=true; 
       clockst.scheduleAtFixedRate(decrement_st, 0, 16);
   }
   public void stoptimer_st(){
       st_timer_on=false; 
       clockst.cancel(); 
   }
   public boolean dt_timer_on_status(){
       return dt_timer_on; 
   }
   public boolean st_timer_on_status(){
       return st_timer_on; 
   }
   
}
