/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyber.coloredChat;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


/**
 *
 * @author robin
 */
public class IRCbot{
    String ICbot = "ICBot";
    String channel = "#imaginecraft";
    PurpleBot bot;
    
    public IRCbot(){
        PurpleIRC pIrc = (PurpleIRC)Bukkit.getPluginManager().getPlugin("PurpleIRC");
        for (PurpleBot ircBot : pIrc.ircBots.values()) {
            if(ircBot.nick.equalsIgnoreCase(ICbot)){
                bot = ircBot;
            }
        }
    }
    
    public void sendMsg(String msg, Player sender, String world){
        String finalMsg = "["+world+"] "+"<"+sender.getName()+"> "+msg;
        bot.asyncIRCMessage(channel, finalMsg);
    }
}
