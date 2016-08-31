/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyber.coloredChat;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author robin
 */
public class joinMessage extends Thread {
    String message;
    Player target;

    joinMessage(String msg, Player p){
        this.message = msg;
        this.target = p;
    }

    public void run(){
        try {
            Thread.sleep(60000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ColoredChat.class.getName()).log(Level.SEVERE, null, ex);
        }

        target.sendMessage(ChatColor.YELLOW+message);
    }
}
