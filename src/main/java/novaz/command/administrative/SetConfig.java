package novaz.command.administrative;

import novaz.core.AbstractCommand;
import novaz.handler.GuildSettings;
import novaz.handler.TextHandler;
import novaz.handler.guildsettings.DefaultGuildSettings;
import novaz.main.Config;
import novaz.main.NovaBot;
import novaz.util.Misc;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Map;

/**
 * !config
 * gets/sets the configuration of the bot
 */
public class SetConfig extends AbstractCommand {
	public SetConfig(NovaBot b) {
		super(b);
	}

	@Override
	public String getDescription() {
		return "Gets/sets the configuration of the bot";
	}

	@Override
	public String getCommand() {
		return "config";
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				"config                    //overview",
				"config <property>         //check details of property",
				"config <property> <value> //sets property"};
	}

	@Override
	public String execute(String[] args, IChannel channel, IUser author) {
		int count = args.length;
		if (bot.isOwner(channel.getGuild(), author)) {
			if (count == 0) {
				String ret = "```ini" + Config.EOL;
				ret += "Current Settings for " + channel.getGuild().getName() + Config.EOL + Config.EOL;
				Map<String, String> settings = GuildSettings.get(channel.getGuild()).getSettings();
				ret += String.format("%-24s| %-16s| %s", "Setting name", "current value", "default value") + Config.EOL;
				ret += "------------------------+-----------------+----------------- " + Config.EOL;
				for (Map.Entry<String, String> entry : settings.entrySet()) {
					ret += String.format("%-24s| %-16s| %s", entry.getKey(), entry.getValue(), DefaultGuildSettings.getDefault(entry.getKey())) + Config.EOL;
				}
				return ret + "```";
			} else {
				if (!DefaultGuildSettings.isValidKey(args[0])) {
					return TextHandler.get("command_config_key_not_exists");
				}
				if (count >= 2 && GuildSettings.get(channel.getGuild()).set(args[0], args[1])) {
					return TextHandler.get("command_config_key_modified");
				}
				String tblContent = "";
				GuildSettings setting = GuildSettings.get(channel.getGuild());
				for (String s : setting.getDescription(args[0])) {
					tblContent += s + Config.EOL;
				}
				return "Config help for **" + args[0] + "**" + Config.EOL + Config.EOL +
						"Current value: \"**" + GuildSettings.get(channel.getGuild()).getOrDefault(args[0]) + "**\"" + Config.EOL +
						"Default value: \"**" + setting.getDefaultValue(args[0]) + "**\"" + Config.EOL + Config.EOL +
						"Description: " + Config.EOL +
						Misc.makeTable(tblContent);
			}
		}
		return TextHandler.get("command_config_no_permission");
	}
}