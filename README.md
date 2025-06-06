<div align="center">
    <img src="./assets/Icon.png" width=250 height = 250 />
</div>

*Plugin Version: 1.1.1*</br>
*Minecraft Version: 1.19+*

CxTokens is a token (money) system for Spigot/Paper Minecraft servers. 

## How to Install
1. Download the latest release of CxTokens
2. Move the .jar file into your servers /plugins folder
3. Reload/restart your server
4. Edit the config and store files in /plugins/CxTokens
5. Reload/restart your server to update config

## Features
- Lottery event
- Auction House
- Bounties
- Item Store
- Commands
- Local data storing (no need for a database)
- HTTP updates
- Highly configurable

### Commands
- cxtokens/tabout: Information about CxTokens
- tadmin: Admin commands/tools for CxTokens (e.g. add/subtract tokens)
- tbounty: Place a bounty on a player
- tbal: Get the balance of yourself or someone else
- tstore: Open the static token store
- tauction: Open the live auction house
- ttop: See who has the most tokens
- tsend: Send tokens to a player
- tsell: Sell a container's (e.g. chests) contents all at once
- treset: Reset your token profile
- tlottery: Join the token lottery if its happening

### HTTP Updates
**Note: by default this is off.** 

This allows you to POST request the CxTokens data to your webserver.

```
{
    player_data: [
        {
            uuid: string,
            name: string,
            tokens: long (number),
            bounty: long (number)
        }, 
        ...
    ],
    store_data: [
        {
            itemName: string,
            material: string,
            price: long (number),
            stack: integer (number),
            sellMultiplier: double (number)
        },
        ...
    ],
    auction_data: [
        {
            sellerName: string,
            sellerUUID: string,
            biddername: string,
            bidderUUID: string,
            currentBid: long (number),
            itemName: string,
            itemMaterial: string,
            itemCount: integer (number),
            sold: boolean,
            sweepsRemaining: long (number)
        },
        ...
    ]
}
```

Additionally, the authorization header is sent with whatever is provided in the config file. This is to make sure you are processing valid requests from verified sources:
```
Headers {
    Authorization: YOUR_AUTH_KEY
}
```

<a href="https://github.com/CKAY-9/cxtokens-web">Example website using CxTokens HTTP data</a>

<div align="center">
    <img src="./assets/2024-03-23_16.07.07-min.png" height=auto width=auto>
    <img src="./assets/2024-03-23_16.07.27-min.png" height=auto width=auto>
    <img src="./assets/2024-03-23_16.08.27-min.png" height=auto width=auto>
    <img src="./assets/2024-03-23_16.09.00-min.png" height=auto width=auto>
</div>
