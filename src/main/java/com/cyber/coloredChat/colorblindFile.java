/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyber.coloredChat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Robin
 */
public class colorblindFile {
    File file;
    
    public colorblindFile(File f){
        file = f;
    }
    
    public void addPlayer(Player p){
        
        try {
            //add player to the list
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(p.getName());
            bw.newLine();
            bw.close();
            fw.close();
            
            p.sendMessage(ChatColor.GREEN+"Successfully removed colors");
            
        } catch (IOException ex) {
            p.sendMessage(ChatColor.RED+"Something horrible went wrong!! Please contact CyberDrain about this!");
            p.sendMessage(ChatColor.RED+"...and no, you can't disable the colors at this moment..");
        }
    }
    
    public void removePlayer(Player p){
        //remove player from list
        
        try{
            File inputFile = file;
            String path = "plugins" + File.separator + file.getName();
            File tempFile = new File(path);

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(p.getName())) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            
            writer.close(); 
            reader.close(); 
        
            Path source = Paths.get(tempFile.getPath());
            Path newdir = Paths.get("plugins" + File.separator + "ColoredChat");
            Files.move(source, newdir.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            
            p.sendMessage(ChatColor.GREEN+"Successfully enabled colors");
            
        }
        catch(IOException e){
            System.out.println(e.getMessage());
            p.sendMessage(ChatColor.RED+"Something horrible went wrong!! Please contact CyberDrain about this!");
            p.sendMessage(ChatColor.RED+"...and no, you can't enable the chatcolors at this moment..");
        }
    }
    
    public boolean isPlayerOnList(Player p){
        //check if player is listed on the file
        
        try{
            File inputFile = file;

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                System.out.println(currentLine);
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(p.getName())){
                    reader.close();
                    return true;
                }
            }
            reader.close();
         }
        catch(IOException e){
            System.out.println(e.getMessage());
            p.sendMessage(ChatColor.RED+"Something horrible went wrong!! Please contact CyberDrain about this!");
            p.sendMessage(ChatColor.RED+"...and no, you can't enable chatcolors at this moment..");
        }
        return false;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
