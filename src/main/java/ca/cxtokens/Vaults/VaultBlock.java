package ca.cxtokens.Vaults;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Utils;

public class VaultBlock {
    public UUID owner;
    public long value;
    public String id;
    public String world_name;
    public String player_name;
    public Location location;
    private ArrayList<ArmorStand> stands;

    public void clearArmorStands() {
        for (ArmorStand stand : stands) {
            stand.teleport(new Location(this.location.getWorld(), 0, -64, 0));
            stand.setHealth(0);
        }
    }

    public void updateArmorStandUI() {
        if (stands.size() <= 0) {
            this.spawnArmorStandUI();
        }
        stands.get(0).setCustomName(Utils.formatText("&a&l" + this.player_name + "'S VAULT"));
        stands.get(1).setCustomName(Utils.formatText("&aValue: &a&l" + CxTokens.currency + "" + this.value));
    }

    public void spawnArmorStandUI() {
        World world = this.location.getWorld();
        double yOffset = 1.5;

        ArmorStand first_stand = world.spawn(this.location.clone().add(0.5, yOffset + 0.3, 0.5), ArmorStand.class);
        setupArmorStand(first_stand, "&a&l" + this.player_name + "'S VAULT");

        ArmorStand second_stand = world.spawn(this.location.clone().add(0.5, yOffset, 0.5), ArmorStand.class);
        setupArmorStand(second_stand, "&aValue: &a&l" + CxTokens.currency + "" + this.value);
    }

    private void setupArmorStand(ArmorStand armor_stand, String text) {
        armor_stand.setCustomName(Utils.formatText(text));
        armor_stand.setCustomNameVisible(true);
        armor_stand.setVisible(false);
        armor_stand.setGravity(false);
        armor_stand.setMarker(true);
        armor_stand.setSmall(true);
        this.stands.add(armor_stand);
    }

    public VaultBlock(UUID owner, long value, Location location, String player_name) {
        this.stands = new ArrayList<>();
        this.owner = owner;
        this.value = value;
        this.location = location;
        this.player_name = player_name;
        this.id = "" + location.getBlockX() + "" + location.getBlockY() + "" + location.getBlockZ();
        this.world_name = location.getWorld().getName();

        this.spawnArmorStandUI();
    }

    public static VaultBlock vaultBlockFromBlock(Block block, Vaults vaults) {
        for (VaultBlock vault_block : vaults.player_vaults) {
            if (vault_block.location.getBlockX() == block.getLocation().getBlockX()
                    && vault_block.location.getBlockY() == block.getLocation().getBlockY()
                    && vault_block.location.getBlockZ() == block.getLocation().getBlockZ()
                    && vault_block.world_name.equals(block.getWorld().getName())) {
                return vault_block;
            }
        }

        return null;
    }
}
