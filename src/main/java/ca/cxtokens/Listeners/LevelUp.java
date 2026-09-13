package ca.cxtokens.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;

public class LevelUp implements Listener {
  private CxTokens tokens;

  public LevelUp(CxTokens tokens) {
    this.tokens = tokens;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onLevelUp(PlayerLevelChangeEvent event) {
    if (!Storage.config.getBoolean("level_up.enabled", true)) {
      return;
    }

    Player player = event.getPlayer();
    int old_level = event.getOldLevel();
    int new_level = event.getNewLevel();
    int diff = new_level - old_level;
    if (diff <= 0) {
      return;
    }

    int flat_rate = Storage.config.getInt("level_up.flat_rate", 50);
    int multiplier = Storage.config.getInt("level_up.multiplier", 5);
    int reward = ((new_level * diff) * multiplier) + flat_rate;
    TokenPlayer.getTokenPlayer(tokens, player).addTokens(reward, false);
  }
}
