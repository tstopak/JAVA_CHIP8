package com.jchip8.javachip8;
import java.awt.Color;
import java.awt.image.BufferedImage; 
import javax.swing.*;
import java.awt.Graphics; 
/*
 * This is the class to handle all of the graphics for the emulator 
 * All of the behind the scenes work is done here and a simple 
 * interface for reading and writing to the screen is given back to the chip8 
 * class 
 */
public class Display{
    // Constants for the pixel size of the emulator 
    private final int PXHEIGHT = 15, PXWIDTH = 15; 
    // Create Display components 
    private JFrame window; 
    private JPanel screen; 
    private ScreenBuffer buff; 
    Display(){
        // Set up the frame
        window = new JFrame("JCHIP8"); 
        window.setSize(63*PXWIDTH,31*PXHEIGHT);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);
        // Set up a JPanel to take up the whole frame 
        screen = new JPanel();
        screen.setBounds(0,0,63*PXWIDTH,31*PXHEIGHT);
        // set Layout to null for absolute positioning 
        screen.setLayout(null);
        //Add the screen to the frame 
        window.add(screen); 
        // Create the buffer to read/write pixels 
        buff = new ScreenBuffer();
        // Add the buffer to the screen 
        screen.add(buff);
        
    }
    public void drawPixel(int x, int y){
        buff.drawPixelToBuff(x, y, PXWIDTH, PXHEIGHT);
    }
    
    public void clearBuffer(){
        buff.clearBuff();
    }
    
    public void clearPixel(int x, int y){
        buff.clearPixelFromBuff(x, y, PXWIDTH, PXHEIGHT);
    }
    public void update(){
        screen.repaint();
    }
    public boolean pixelOn(int x, int y){
        return buff.pixelIsOn(x, y); 
    }
    // Create a Screen Buffer for Double Buffering
    private class ScreenBuffer extends JComponent{
     // Create a buffered image that holds the state of the screen 
     private final BufferedImage screenbuff;
 
     ScreenBuffer(){
         //make sure this component has actual space to draw to 
         this.setBounds(0,0,63*PXWIDTH, 31*PXHEIGHT);
         //Initialize the buffer, make it the screen size, RGB int format 
         screenbuff=new BufferedImage(63*PXWIDTH, 31*PXHEIGHT, BufferedImage.TYPE_INT_RGB);  
         clearBuff(); 
     }
     // Override the paint method in order to draw the image as a component 
     @Override 
     public void paintComponent(Graphics g){
         g.drawImage(screenbuff, 0, 0, this);
     }
     private void drawPixelToBuff(int x, int y, int width, int height){
         for(int i =0; i<width; i++){
             for(int j = 0; j<height; j++){
                 screenbuff.setRGB(x+i, y+j, Color.green.getRGB()); 
             }
         }
     }
     private boolean pixelIsOn(int x, int y){
         return screenbuff.getRGB(x, y)==Color.green.getRGB(); 
     }
     
     private void clearPixelFromBuff(int x, int y, int width, int height){
         for(int i =0; i<width; i++){
             for(int j = 0; j<height; j++){
                 screenbuff.setRGB(x+i, y+j, Color.black.getRGB()); 
             }
         }
     }
     private void clearBuff(){
          //Loop through all pixels and make them black to start with a blank slate 
         for(int x = 0; x<63*PXWIDTH; x++){
             for(int y =0; y<31*PXHEIGHT;y++){
                 screenbuff.setRGB(x, y, Color.black.getRGB());
             }
         }
     }
    
    }
}