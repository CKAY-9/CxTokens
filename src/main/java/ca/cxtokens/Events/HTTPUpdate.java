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
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.Utils;
import ca.cxtokens.Shop.Auction.Item;

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

class StoreItemData {
    String itemName;
    String material;
    long price;
    int stack;
    double sellMultiplier;

    public StoreItemData(String itemName, String material, long price, int stack, double sellMultiplier) {
        this.itemName = itemName;
        this.material = material;
        this.price = price;
        this.stack = stack;
        this.sellMultiplier = sellMultiplier;
    }
}

class AuctionItemData {
    String sellerName;
    String sellerUUID;
    String bidderName;
    String bidderUUID;
    long currentBid;
    String itemName;
    String itemMaterial;
    int itemCount;
    boolean sold;
    long sweepsRemaining;

    public AuctionItemData(
        String sellerName, 
        String sellerUUID, 
        String bidderName, 
        String bidderUUID, 
        long bid, 
        String itemName, 
        String material, 
        int count, 
        boolean sold, 
        long sweepsRemaining
    ) {
        this.sellerName = sellerName;
        this.sellerUUID = sellerUUID;
        this.bidderName = bidderName;
        this.bidderUUID = bidderUUID;
        this.currentBid = bid;
        this.itemName = itemName;
        this.itemMaterial = material;
        this.itemCount = count;
        this.sold = sold;
        this.sweepsRemaining = sweepsRemaining;
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
        ArrayList<StoreItemData> storeData = new ArrayList<>();
        ArrayList<AuctionItemData> auctionData = new ArrayList<>();

        ConfigurationSection playersSection = Storage.data.getConfigurationSection("players");
        Set<String> playerKeys = playersSection.getKeys(false);

        for (String key : playerKeys) {
            ConfigurationSection playerSection = playersSection.getConfigurationSection(key);
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

        ConfigurationSection itemsSection = Storage.storeItems.getConfigurationSection("items");
        Set<String> itemKeys = itemsSection.getKeys(false);

        for (String key : itemKeys) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            String material = itemSection.getString("material");
            int amount = itemSection.getInt("amount");
            long price = itemSection.getLong("price");
            double multiplier = itemSection.getDouble("sellMultiplier");
            String name = material;

            Material mat = Material.getMaterial(material);
            if (mat != null) {
                name = mat.name();
            }

            if (itemSection.isSet("customName")) {
                name = itemSection.getString("customName", material);
            }

            StoreItemData tempData = new StoreItemData(
                name,
                material,
                price,
                amount,
                multiplier
            );
            storeData.add(tempData);
        }

        if (this.tokens.auctionHouse != null) {
            for (Item item : this.tokens.auctionHouse.auctionItems) {
                Player bidder = item.bidder;
                Player seller = item.seller;
                ItemStack stack = item.item;
                
                AuctionItemData tempData = new AuctionItemData(
                    seller.getName(), 
                    seller.getUniqueId().toString(), 
                    bidder.getName(),
                    bidder.getUniqueId().toString(),
                    item.currentBid, 
                    stack.getType().name(), 
                    stack.getType().getKey().toString(), 
                    stack.getAmount(), 
                    item.sold, 
                    item.sweepsUntilComplete
                );
                auctionData.add(tempData);
            }
        }

        String playersToJSON = gson.toJson(playerData);
        String storeToJSON = gson.toJson(storeData);
        String auctionToJSON = gson.toJson(auctionData);

        String authorizationKey = Storage.config.getString("config.http.authorization", "EMPTY_KEY");
        String httpURL = Storage.config.getString("config.http.url", "http://localhost:8080/api/tokens/update");

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost(httpURL);
        postRequest.addHeader("Authorization", authorizationKey);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("player_data", playersToJSON));
        params.add(new BasicNameValuePair("store_data", storeToJSON));
        params.add(new BasicNameValuePair("auction_data", auctionToJSON));

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