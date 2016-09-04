package com.cyber.coloredChat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


public class ColoredChat extends JavaPlugin{
    Server server;
    colorblindFile cFile;
    String colorblindFileName = "colorblind.txt";
    IRCbot IRC;
    
    
    
    @Override
    public void onEnable(){
        server = getServer();
        
        cFile = checkColorblindFile();
        
        server.getPluginManager().registerEvents(new MyListener(), this);
                
        ArrayList<String> teams = new ArrayList<>();
        teams.add("Spectators");
        teams.add("optout");
        teams.add("Owner");
        teams.add("Mod");
        teams.add("VIP");
        teams.add("Veteran");
        teams.add("Member");
        
        validateTeams(teams);
        
        IRC = new IRCbot();
        
        System.out.println("ColoredChat enabled!");
    }
    
    @Override
    public void onDisable(){
        System.out.println("ColoredChat disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(command.getName().equalsIgnoreCase("colors"))){
            return false;
        }
        
        if(!(sender instanceof Player)){
           //Dirty workaround for non-players being able to run this command
           if(isUpdateCommand(sender, args)){
               return true;
           }
           
            sender.sendMessage(ChatColor.RED+"This command can only be run by a player!");
            return false;
        }
        
        Player p = (Player)sender;
        
        if(args.length == 0){
            showHelp(p);
            return false;
        }
        
        switch(args[0]){
            case "on": 
                if(!cFile.isPlayerOnList(p)){
                    p.sendMessage(ChatColor.RED+"Colors are already enabled");
                    return false;
                }
                cFile.removePlayer(p);
                return true;
                       
            case "off":
                if(cFile.isPlayerOnList(p)){
                    p.sendMessage(ChatColor.RED+"Colors are already disabled");
                    return false;
                }
                cFile.addPlayer(p);
                return true;
                
            case "update": 
                if(!p.hasPermission("coloredchat.update")){
                    showHelp(p);
                    return false;
                }
                updateTabList();
                updateColoredNames();
                p.sendMessage(ChatColor.GREEN+"Successfully updated player colors!");
                return true;
                
            default:
                showHelp(sender);
        }
        
        return false;
    }
    
    private void showHelp(CommandSender p){
        ChatColor reset = ChatColor.RESET;
        ChatColor DR = ChatColor.DARK_RED;
        ChatColor R = ChatColor.RED;
        ChatColor G = ChatColor.GOLD;
        ChatColor Y = ChatColor.YELLOW;
        ChatColor GR = ChatColor.GREEN;
        ChatColor DG = ChatColor.DARK_GREEN;
        ChatColor A = ChatColor.AQUA;
        ChatColor B = ChatColor.BLUE;
        ChatColor DB = ChatColor.DARK_BLUE;
        ChatColor DP = ChatColor.DARK_PURPLE;
        ChatColor P = ChatColor.LIGHT_PURPLE;
        
        p.sendMessage(ChatColor.BOLD+""+DR+"/"+R+"C"+G+"o"+Y+"l"+GR+"o"+DG+"r"+A+"s "+B+"o"+DB+"n"+DP+": "+reset+"turns the colors on in chat");
        p.sendMessage(ChatColor.BOLD+""+DR+"/"+R+"C"+G+"o"+Y+"l"+GR+"o"+DG+"r"+A+"s "+B+"o"+DB+"f"+DP+"f"+P+": "+reset+"turns the colors off in chat");
    }
    
    private boolean isUpdateCommand(CommandSender sender, String[] args){
        if(args.length == 0){
            return false;
        }
        if(!args[0].equalsIgnoreCase("update")){
            return false;
        }
        updateTabList();
        updateColoredNames();
        sender.sendMessage(ChatColor.GREEN+"Successfully updated player colors!");
        return true;
    }
    
    //Manually update all colors in tab list
    private void updateTabList(){
        for(Player p : server.getOnlinePlayers()){
            setListName(p);
        }
    }
    
    //Manually update all colored names above players heads
    private void updateColoredNames(){
        for(Player p : server.getOnlinePlayers()){
            setColoredName(p);
        }
    }
    
    
    //Check if the colorblind file exists, if not create it!
    private colorblindFile checkColorblindFile(){
        File f = new File("plugins/ColoredChat/"+colorblindFileName);
        if(!f.exists()){
           try {
                String path = "plugins" + File.separator + "ColoredChat" + File.separator + colorblindFileName;
                f = new File(path);
                
                f.getParentFile().mkdirs();
                f.createNewFile();
            } 
            catch (IOException ex) {
                System.out.println("Error while creating files for ColoredChat");
            } 
        }
        
        return new colorblindFile(f);
    }

    //Called when player joins / switches from world
    private void setListName(Player p){
        String worldPrefix = getWorldPrefix(p.getWorld().getName());
        ChatColor wColor = getWorldColor(worldPrefix);
        ChatColor rColor = getRankColor(p);
        ChatColor gray = ChatColor.GRAY;

        String underaged = "";
        //Is the player younger than 17?
        if(p.isOp()){
            //Do nothing
        }
        else if(p.hasPermission("coloredchat.underage.super")){
            underaged = "**";
        }
        else if(p.hasPermission("coloredchat.underage")){
            underaged = "*";
        }
        
        p.setPlayerListName(gray+"["+wColor+worldPrefix+gray+"] "+rColor+p.getName()+ChatColor.WHITE+underaged);
    }

    private String getWorldPrefix(String world){
        world = world.toLowerCase();
        switch(world){
            case "world":
                return "S";
            case "world_nether":
                return "N";
            case "world_the_end":
                return "E";
            case "creative":
                return "C";
            case "amplified":
                return "A";
            default:
                return "?";
        }
    }

    private ChatColor getWorldColor(String world){
        switch(world){
            case "S":
                return ChatColor.GREEN;
            case "N":
                return ChatColor.RED;
            case "E":
                return ChatColor.DARK_PURPLE;
            case "C":
                return ChatColor.BLUE;
            case "A":
                return ChatColor.YELLOW;
            default:
                return ChatColor.LIGHT_PURPLE;
        }
    }

    //Get the color depending on the senders rank
    private ChatColor getRankColor(Player p){

        if(p.hasPermission("special.spectators") && !p.isOp()){
            return ChatColor.DARK_GRAY;
        }
        else if(p.hasPermission("event.optout") && !p.isOp()){
            return ChatColor.GRAY;
        }
        if(p.hasPermission("group.admin")){
            return ChatColor.RED;
        }
        else if(p.hasPermission("group.mod")){
            return ChatColor.GREEN;
        }
        else if(p.hasPermission("group.vip")){
            return ChatColor.AQUA;
        }
        else if(p.hasPermission("group.member")){
            return ChatColor.GOLD;
        }
        else if(p.hasPermission("group.trial")){
            return ChatColor.DARK_PURPLE;
        }
        return ChatColor.WHITE;
    }


    //Sends a message to one player
    private void sendMessage(Player target, Player sender, String msg, ChatColor c){
        if(cFile.isPlayerOnList(target)){
            c = ChatColor.WHITE;
        }

        target.sendMessage("<"+c+sender.getName()+ChatColor.WHITE+"> "+msg);
    }

    //Create teams if they dont exist already
    private void validateTeams(ArrayList<String> teams) {
        Scoreboard scoreboard = server.getScoreboardManager().getMainScoreboard();
        
        for(String name : teams){
            if(scoreboard.getTeam(name) == null){
                System.out.println("Register new team: "+name);
                scoreboard.registerNewTeam(name);
            }
            else{
                System.out.println("Successfully found team "+name);
            }
        }
        
        
        for(Team team : scoreboard.getTeams()){
            addTeamColor(team);
        }
    }
    
    private void addTeamColor(Team team){
        String name = team.getName();
        ChatColor cc;
        
        switch (name){
            case "Spectators":
                cc = ChatColor.GRAY;
                break;
            case "optout":
                cc = ChatColor.DARK_GRAY;
                break;
            case "Owner":
                cc = ChatColor.RED;
                break;
            case "Mod":
                cc = ChatColor.GREEN;
                break;
            case "VIP":
                cc = ChatColor.AQUA;
                break;
            case "Veteran":
                cc = ChatColor.GOLD;
                break;
            case "Member":
                cc = ChatColor.DARK_PURPLE;
                break;
            default:
                cc = ChatColor.WHITE;
        }
        
        team.setPrefix(cc+"");
    }
    
    
    //Adds the player to a team, depending on their permissions
    private void setColoredName(Player p){
        Scoreboard board = server.getScoreboardManager().getMainScoreboard();
        Team team = null;
        
        if(p.hasPermission("special.spectators") && !p.isOp()){
            team = board.getTeam("Spectators");
        }
        else if(p.hasPermission("event.optout") && !p.isOp()){
            team = board.getTeam("optout");
        }
        else if(p.hasPermission("group.admin")){
            team = board.getTeam("Owner");
        }
        else if(p.hasPermission("group.mod")){
            team = board.getTeam("Mod");
        }
        else if(p.hasPermission("group.vip")){
            team = board.getTeam("VIP");
        }
        else if(p.hasPermission("group.member")){
            team = board.getTeam("Veteran");
        }
        else if(p.hasPermission("group.trial")){
            team = board.getTeam("Member");
        }
        
        if(team != null){
            team.addEntry(p.getName());
        }
        else{
            removePlayerFromAllTeams(p);
        }
    }
    
    
    //Only used when a player is demoted back to 'white'
    private void removePlayerFromAllTeams (Player p){
        for(Team team : server.getScoreboardManager().getMainScoreboard().getTeams()){
            team.removeEntry(p.getName());
        }
    }
    
    
    public class MyListener implements Listener{
        
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e){
            Player p = e.getPlayer();
            
            //Update tab list and player name colors
            setListName(p);
            setColoredName(p);
            
            //Is this a new player?
            if(!p.hasPlayedBefore()){
                joinMessage JM = new joinMessage("Are the colors a bit too much? You can turn the colored names off by typing /colors off", p);
                JM.start();
            }
        }
        
        //Cancel all incoming messages
        @EventHandler
        public void cancelMessage(AsyncPlayerChatEvent e){
            e.setCancelled(true);
            Player sender = e.getPlayer();
            String msg = e.getMessage();
            ChatColor color = getRankColor(sender);
            
            for(Player target : server.getOnlinePlayers()){
                sendMessage(target, sender, msg, color);
            }
            
            //Send message to IRC channel
            IRC.sendMsg(msg, sender, sender.getWorld().getName());
            
            //Send message to console
            System.out.println("<"+sender.getName()+"> "+msg);
        }
        
        //Player switches to another world
        @EventHandler
        public void changedWorld(PlayerChangedWorldEvent e){
            setListName(e.getPlayer());
        }
    }
}