package com.qq44920040.Minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class InventoryLimit extends JavaPlugin implements Listener {
    private String Msg;
    private HashMap<Integer,String[]> InvEntoryInfo = new HashMap<>();
    @Override
    public void onEnable() {
        ReloadConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        super.onEnable();
    }

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent event){
            ItemStack itemStack = event.getPlayer().getItemInHand();
            if (itemStack!=null&&itemStack.getType()!= Material.AIR){
                int Slot = event.getPlayer().getInventory().getHeldItemSlot();
                    if (InvEntoryInfo.get(Slot)[0].equalsIgnoreCase("true")){
                        List<String> list = Arrays.asList(InvEntoryInfo.get(Slot)[1].split("-"));
                        if (list.contains(String.valueOf(itemStack.getTypeId()))){
                            ItemStack itemStack1 = event.getPlayer().getInventory().getItem(Slot);
                            event.getPlayer().getInventory().setItem(Slot,null);
                            try{
                                event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation().add(2.0, 0.0, 0.0),itemStack1);
                            }catch (Exception e) {
                            }
                            event.getPlayer().sendMessage(Msg);
                            event.setCancelled(true);
                        }
                }
            }
    }

    @EventHandler
    public void PlayerDrag(InventoryDragEvent event){
        if (event.getInventory().getType()== InventoryType.CRAFTING){
            if (event.getInventorySlots().isEmpty()){
                return;
            }
            for (Integer Slot:event.getInventorySlots()){
                if (Slot>=0&&Slot<=8){
                    System.out.println(Slot);
                    if (InvEntoryInfo.get(Slot)[0].equalsIgnoreCase("true")){
                        List<String> list = Arrays.asList(InvEntoryInfo.get(Slot)[1].split("-"));
                        if (list.contains(String.valueOf(event.getInventory().getItem(Slot).getTypeId()))){
                            event.setCancelled(true);
                            ((Player)event.getWhoClicked()).sendMessage(Msg);
                        }
                    }
                }
            }
        }
        System.out.println(event.getInventorySlots().toString());
    }

    @EventHandler
    public void PlayerClickInventory(InventoryClickEvent event){
        if (event.getInventory().getType() == InventoryType.CRAFTING){
            int ClickSolt = event.getSlot();
            if (ClickSolt<=8&&ClickSolt>=0){
                ItemStack itemStack =event.getCursor();
                if (itemStack!=null&&itemStack.getType()!=Material.AIR){
                    if (InvEntoryInfo.get(ClickSolt)[0].equalsIgnoreCase("true")){
                    List<String> list = Arrays.asList(InvEntoryInfo.get(ClickSolt)[1].split("-"));
                    if (list.contains(String.valueOf(itemStack.getTypeId()))){
                        event.setCancelled(true);
                        ((Player)event.getWhoClicked()).sendMessage(Msg);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void ReloadConfig(){
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(),"config.yml");
        if (!(file.exists())){
            saveDefaultConfig();
        }

        Msg = getConfig().getString("Msg");
        Set<String> mines = getConfig().getConfigurationSection("InventoryLimit").getKeys(false);
        for (String temp:mines){
            System.out.println(temp);
            String IsOpen = getConfig().getString("InventoryLimit."+temp+".IsOpen");
            String AllowID = getConfig().getString("InventoryLimit."+temp+".AllowID");
            InvEntoryInfo.put(Integer.parseInt(temp),new String[]{IsOpen,AllowID});
        }
    }
}
