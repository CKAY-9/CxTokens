package ca.cxtokens.Events;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bukkit.configuration.ConfigurationSection;

import com.google.gson.Gson;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.Utils;

class PlayerData {
    String uuid; 
    String username;
    long tokens; 
    long bounty; 

    public PlayerData(String uuid, String username, long tokens, long bounty) {
        this.uuid = uuid;
        this.username = username;
        this.tokens = tokens;
        this.bounty = bounty;
    }
}

class Update implements Runnable {
    CxTokens tokens;

    public Update(CxTokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        ArrayList<PlayerData> playerData = new ArrayList<>();

        ConfigurationSection section = Storage.data.getConfigurationSection("players");
        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            ConfigurationSection playerSection = section.getConfigurationSection(key);
            String uuid = key;
            String name = playerSection.getString("name");
            long tokens = playerSection.getLong("tokens");
            long bounty = playerSection.getLong("bounty");
            PlayerData tempData = new PlayerData(
                uuid,
                name,
                tokens,
                bounty
            );
            playerData.add(tempData);
        }

        String convertedToJSON = gson.toJson(playerData);
        Utils.getPlugin().getLogger().info(convertedToJSON);

        String authorizationKey = Storage.config.getString("config.http.authorization", "EMPTY_KEY");
        String httpURL = Storage.config.getString("config.http.url", "http://localhost:8080/api/tokens/update");

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost(httpURL);
        postRequest.addHeader("Authorization", authorizationKey);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("player_data", convertedToJSON));

        try {
            postRequest.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpClient.execute(postRequest);
            HttpEntity entity = response.getEntity();   

            if (entity != null) {
                // String content = EntityUtils.toString(entity);
            }
        } catch (UnsupportedEncodingException e) {
            Utils.getPlugin().getLogger().warning(e.toString());
        } catch (ClientProtocolException e) {
            Utils.getPlugin().getLogger().warning(e.toString());
        } catch (IOException e) {
            Utils.getPlugin().getLogger().warning(e.toString());
        }
    }
}

public class HTTPUpdate {
    CxTokens tokens;
    Update update;

    public HTTPUpdate(CxTokens tokens) {
        this.tokens = tokens;
        this.tokens.getLogger().info("Registering HTTP Updates Event");

        this.update = new Update(this.tokens);
        int updateSeconds = Storage.config.getInt("config.http.updateInSeconds", 60);
        this.tokens.getServer().getScheduler().scheduleSyncRepeatingTask(
            this.tokens, 
            update,
            20 * updateSeconds, 
            20 * updateSeconds
        );
    }
}
