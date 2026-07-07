package ca.cxtokens.Accounts;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.entity.Player;

public class Account {

    public String accountName;
    public int pinCode;
    public long accountNumber;
    public long balance;
    public HashSet<UUID> accessed;
    public ArrayList<String> transactions;
    public UUID creator;

    public Account(
        String accountName,
        long accountNumber,
        int pinCode,
        long balance,
        UUID creatorUUID
    ) {
        this.accessed = new HashSet<>();
        this.transactions = new ArrayList<>();
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.pinCode = pinCode;
        this.balance = balance;
        this.creator = creatorUUID;
    }

    private boolean chargeAccount(long amount, String transactionMessage) {
        if (this.balance < amount) {
            return false;
        }

        this.balance -= amount;
        this.transactions.add(transactionMessage);
        return true;
    }

    public boolean close(AccountRegistry registry) {
        if (this.balance != 0) {
            return false;
        }

        try {
            Storage.data.set("accounts." + this.accountNumber, null);
            Storage.data.save(Storage.dataFile);
        } catch (IOException ex) {
            if (CxTokens.LOGGING) {
                Utils.getPlugin().getLogger().warning(ex.toString());
            }
            return false;
        }

        registry.removeAccountByNumber(accountNumber);

        return true;
    }

    public void withdraw(TokenPlayer tPlayer, long amount) {
        if (this.balance < amount) {
            tPlayer.ply.sendMessage(
                Utils.formatText(
                    "&cCannot withdraw an amount higher than the account's balance!"
                )
            );
            return;
        }

        this.chargeAccount(
            amount,
            tPlayer.ply.getName() +
                " withdrew " +
                CxTokens.currency +
                "" +
                amount +
                " from the account."
        );
        this.save();

        tPlayer.ply.sendMessage(
            Utils.formatText(
                "&aWithdrew &a&l" +
                    CxTokens.currency +
                    "" +
                    amount +
                    " &r&afrom the account."
            )
        );
        tPlayer.addTokens(amount, true);
    }

    public void deposit(TokenPlayer tPlayer, long amount) {
        if (amount > tPlayer.getTokens()) {
            tPlayer.ply.sendMessage(
                Utils.formatText("&cCannot deposit more money than you have!")
            );
            return;
        }

        this.balance += amount;
        this.transactions.add(
            tPlayer.ply.getName() +
                " deposited " +
                CxTokens.currency +
                "" +
                amount +
                " into the account."
        );
        this.save();

        tPlayer.ply.sendMessage(
            Utils.formatText(
                "&aDeposited &a&l" +
                    CxTokens.currency +
                    "" +
                    amount +
                    " &r&ainto the account."
            )
        );
        tPlayer.subtractTokens(amount, true);
    }

    public void printTransactions(TokenPlayer tPlayer) {
        Player ply = tPlayer.ply;
        ply.sendMessage(
            Utils.formatText(
                "&aToken Account Transactions - Total: &a&l" +
                    this.transactions.size()
            )
        );
        for (String transaction : this.transactions) {
            ply.sendMessage(Utils.formatText("&a  - " + transaction));
        }
    }

    public void printDetails(TokenPlayer tPlayer) {
        Player ply = tPlayer.ply;
        ply.sendMessage(
            Utils.formatText("&aToken Account Details - " + this.accountName)
        );

        ply.sendMessage(
            Utils.formatText("&a  - Account Number: " + this.accountNumber)
        );
        ply.sendMessage(Utils.formatText("&a  - PIN: " + this.pinCode));
        ply.sendMessage(
            Utils.formatText(
                "&a  - Balance: &a&l" + CxTokens.currency + "" + this.balance
            )
        );
    }

    public void save() {
        try {
            Storage.data.set(
                "accounts." + this.accountNumber + ".name",
                this.accountName
            );
            Storage.data.set(
                "accounts." + this.accountNumber + ".balance",
                this.balance
            );
            Storage.data.set(
                "accounts." + this.accountNumber + ".pin",
                this.pinCode
            );
            Storage.data.set(
                "accounts." + this.accountNumber + ".creator",
                this.creator.toString()
            );
            Storage.data.set(
                "accounts." + this.accountNumber + ".transactions",
                this.transactions
            );

            Storage.data.save(Storage.dataFile);
        } catch (IOException ex) {
            if (CxTokens.LOGGING) {
                Utils.getPlugin().getLogger().warning(ex.toString());
            }
        }
    }
}
