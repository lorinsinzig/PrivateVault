package org.main.privatevault;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PrivateVault extends JavaPlugin implements Listener {

    public static Map<String, ItemStack[]> menus = new HashMap<String, ItemStack[]>();
    @Override
    public void onEnable() {

        this.getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig(); //<create config.yml

        if(this.getConfig().contains("data")){
            this.restoreInv();
            this.getConfig().set("data", null);
            this.saveConfig();
        }
    }
    @Override
    public void onDisable(){
        if(!menus.isEmpty())
            this.saveInv();
    }

    public void saveInv(){
        for(Map.Entry<String, ItemStack[]> entry : menus.entrySet()) {
            this.getConfig().set("data." + entry.getKey(), entry.getValue());
        }
        this.getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void restoreInv(){
        this.getConfig().getConfigurationSection("data").getKeys(false).forEach(key ->{
            ItemStack[] content = ((List<ItemStack>) this.getConfig().get("data." + key)).toArray(new ItemStack[0]);
            menus.put(key, content);
        });
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;
            Inventory vault = Bukkit.createInventory(player, 9, player.getName() + "'s Vault");

            if(menus.containsKey(player.getUniqueId().toString()))
                vault.setContents(menus.get(player.getUniqueId().toString()));

            player.openInventory(vault);
            return true;
        }

        return false;
    }

    @EventHandler
    public void onGUIClose(InventoryCloseEvent event){
        if(event.getView().getTitle().contains(event.getPlayer().getName() + "'s Vault"))
            menus.put(event.getPlayer().getUniqueId().toString(), event.getInventory().getContents());
    }
}