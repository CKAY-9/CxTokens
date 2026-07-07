package ca.cxtokens.Accounts;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;

public class AccountRegistry {

    private HashMap<Long, Account> accounts;
    private CxTokens tokens;

    public AccountRegistry(CxTokens tokens) {
        this.tokens = tokens;
        this.accounts = new HashMap<>();
    }

    public Account findAccountByNumber(Long accountNumber) {
        return this.accounts.get(accountNumber);
    }

    public void removeAccountByNumber(Long accountNumber) {
        this.accounts.remove(accountNumber);
    }

    public void loadAccounts() {
        if (CxTokens.LOGGING) {
            Utils.getPlugin()
                .getLogger()
                .info("Loading existing token accounts...");
        }

        ConfigurationSection accountsSection =
            Storage.data.getConfigurationSection("accounts");
        if (accountsSection == null) {
            // if this is null, there are no accounts saved in data.yml
            return;
        }

        for (String key : accountsSection.getKeys(false)) {
            String name = accountsSection.getString(key + ".name", "");
            Long balance = accountsSection.getLong(key + ".balance", 0L);
            int pin = accountsSection.getInt(key + ".pin", 0);
            String uuidStr = accountsSection.getString(key + ".creator", "");
            ArrayList<String> transactions = new ArrayList<>(
                accountsSection.getStringList(key + ".transactions")
            );

            Account account = new Account(
                name,
                Long.parseLong(key),
                pin,
                balance,
                UUID.fromString(uuidStr)
            );
            account.transactions = transactions;

            this.accounts.put(Long.parseLong(key), account);
        }

        if (CxTokens.LOGGING) {
            Utils.getPlugin()
                .getLogger()
                .info("Loaded existing token accounts!");
        }
    }

    public Account createAccount(
        TokenPlayer tokenPlayer,
        String accountName,
        int pinCode
    ) {
        Long maxAccountNumber = 10_000_000_000L;
        Random random = new Random();
        long newAccountNumber = random.nextLong(1L, maxAccountNumber);
        Account existingAccountWithNumber = this.findAccountByNumber(
            newAccountNumber
        );
        while (existingAccountWithNumber != null) {
            newAccountNumber = random.nextLong(1L, maxAccountNumber);
            existingAccountWithNumber = this.findAccountByNumber(
                newAccountNumber
            );
        }

        Account account = new Account(
            accountName,
            newAccountNumber,
            pinCode,
            0L,
            tokenPlayer.ply.getUniqueId()
        );

        this.accounts.put(newAccountNumber, account);

        return account;
    }
}
