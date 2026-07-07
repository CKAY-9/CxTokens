package ca.cxtokens.Commands;

import ca.cxtokens.Accounts.Account;
import ca.cxtokens.Accounts.AccountRegistry;
import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AccountCommand implements CommandExecutor {

    private CxTokens tokens;
    private AccountRegistry registry;

    public AccountCommand(CxTokens tokens) {
        if (tokens.accountRegistry == null) {
            return;
        }

        this.tokens = tokens;
        this.registry = tokens.accountRegistry;
    }

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        if (!(sender instanceof Player)) return false;
        if (!Storage.config.getBoolean("accounts.enabled", true)) {
            sender.sendMessage(
                Utils.formatText(
                    "&c&lToken Accounts&r&c are disabled on this server."
                )
            );
            return false;
        }

        if (args.length <= 0) {
            sender.sendMessage(
                Utils.formatText(
                    "&cProper usage: /taccount [create/list/ACCOUNT_NUMBER] [arguments]"
                )
            );
            return false;
        }

        try {
            Player player = (Player) sender;
            TokenPlayer tPlayer = TokenPlayer.getTokenPlayer(
                this.tokens,
                player
            );

            String firstArg = args[0].strip().toLowerCase();
            if (firstArg.equalsIgnoreCase("create")) {
                // create a new account
                String accountName = args[1].strip();
                int pinCode = 0;
                try {
                    pinCode = Integer.parseInt(args[2].strip());
                    if (pinCode > 999_999 || pinCode < 0) {
                        sender.sendMessage(
                            Utils.formatText(
                                "&cAccount PIN Code must be a number between 0-999999"
                            )
                        );
                        return false;
                    }
                } catch (NumberFormatException ex) {
                    sender.sendMessage(
                        Utils.formatText("&cAccount PIN Code must be a number!")
                    );
                    return false;
                }

                Account account = this.registry.createAccount(
                    tPlayer,
                    accountName,
                    pinCode
                );
                if (account == null) {
                    sender.sendMessage(
                        Utils.formatText(
                            "&cFailed to create new Token Account. You may have too many existing accounts."
                        )
                    );
                    return false;
                }

                account.save();
                sender.sendMessage(
                    Utils.formatText(
                        "&aCreated a new Token Account &7(Copy the details below)"
                    )
                );
                account.printDetails(tPlayer);

                player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_HARP,
                    1F,
                    1.2F
                );
                return false;
            } else if (firstArg.equalsIgnoreCase("list")) {
                this.registry.listPersonalAccounts(tPlayer);
                return false;
            }

            long accountNumber = Long.parseLong(firstArg);
            int pinCode = Integer.parseInt(args[1].strip().toLowerCase());

            Account account = this.registry.findAccountByNumber(accountNumber);
            if (account == null || account.pinCode != pinCode) {
                sender.sendMessage(
                    Utils.formatText(
                        "&cFailed to find account and PIN combination!"
                    )
                );
                return false;
            }

            String accountCommand = args[2].strip().toLowerCase();
            switch (accountCommand) {
                case "view":
                    account.printDetails(tPlayer);
                    break;
                case "deposit":
                    account.deposit(tPlayer, Long.parseLong(args[3].strip()));
                    break;
                case "withdraw":
                    account.withdraw(tPlayer, Long.parseLong(args[3].strip()));
                    break;
                case "close":
                    if (account.close(this.registry)) {
                        sender.sendMessage(
                            Utils.formatText("&aSuccessfully closed account!")
                        );
                    } else {
                        sender.sendMessage(
                            Utils.formatText(
                                "&cFailed to close account! Make sure the balance of the account is &c&l" +
                                    CxTokens.currency +
                                    "0"
                            )
                        );
                    }
                    break;
                case "transactions":
                    account.printTransactions(tPlayer);
                    break;
            }
        } catch (Exception ex) {
            if (CxTokens.LOGGING) {
                Utils.getPlugin().getLogger().info(ex.toString());
            }
            sender.sendMessage(
                Utils.formatText(
                    "&cError executing command: " + ex.getMessage()
                )
            );
        }

        return false;
    }
}
