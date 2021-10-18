package com.sanan.avatarcore.util.player.nation;

import com.sanan.avatarcore.util.data.ConfigManager;
import com.sanan.avatarcore.util.nation.BendingNation;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public final class PlayerNationData {
    private final String nationChosen;
    private final int timesChosen;

    private PlayerNationData(String nationChosen, int timesChosen) {
        this.nationChosen = Objects.requireNonNull(nationChosen, "chosen nation cannot be null");
        this.timesChosen = timesChosen;
    }

    public String getNationChosen() {
        return this.nationChosen;
    }

    public int getTimesChosen() {
        return this.timesChosen;
    }

    public boolean hasNationChosen() {
        return !this.nationChosen.equalsIgnoreCase("none");
    }

    public boolean canJoinNation(String nation) {
        if (this.hasNationChosen()) {
            if (this.nationChosen.equalsIgnoreCase(nation)) {
                return true;
            }
        }

        return this.timesChosen < 3;
    }

    public PlayerNationData updateNation(String nation, PlayerNationData base) {
        if (!this.canJoinNation(nation)) {
            return base;
        }

        return new PlayerNationData(nation, base.timesChosen + 1);
    }

    public PlayerNationData updateBasingOn(PlayerNationData base) {
        return new PlayerNationData(base.nationChosen, base.timesChosen + 1);
    }

    public static PlayerNationData loadFromFile(UUID uuid, FileConfiguration playerFile) {
        final NationManager nationManager = NationManager.getInstance();
        final PlayerNationData defaultResult = PlayerNationData.createDefaultPlayerNationData();
        final ConfigurationSection playerSection = playerFile.getConfigurationSection(uuid.toString());
        if (playerSection == null) {
            return defaultResult;
        }

        final ConfigurationSection playerNationSection = playerSection.getConfigurationSection("nation-data");
        if (playerNationSection == null) {
            return defaultResult;
        }

        final String nationName = playerNationSection.getString("nation-chosen");
        if (nationName.equals("none")) {
            return defaultResult;
        }

        final BendingNation nationChosen = nationManager.getNation(nationName);
        if (nationChosen == null) {
            throw new RuntimeException("Cannot load a nation instance of name '" + nationName + "'.");
        }

        final int timesChosen = playerNationSection.getInt("times-chosen", 0);
        return new PlayerNationData(nationName, timesChosen);
    }

    public void store(Player player) {
        this.store(player.getUniqueId());
    }

    public void store(BendingPlayer player) {
        this.store(player.getSpigotPlayer().getUniqueId());
    }

    private void store(UUID uuid) {
        final ConfigManager configManager = ConfigManager.getInstance();
        final FileConfiguration playerDataFile = configManager.getPlayerData();
        playerDataFile.set(uuid.toString() + ".nation-data.nation-chosen", this.nationChosen);
        playerDataFile.set(uuid.toString() + ".nation-data.times-chosen", this.timesChosen);
    }

    public static PlayerNationData createDefaultPlayerNationData() {
        return new PlayerNationData("none", 0);
    }

    @Override
    public String toString() {
        return "{" + this.nationChosen + ", " + this.timesChosen + "}";
    }
}
