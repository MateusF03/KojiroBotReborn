package com.mateus.kojiroreb;

import com.mateus.kojiroreb.command.DACommand;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = new DotenvBuilder().load();
        try {
            JDA jda = new JDABuilder(dotenv.get("DISCORD_TOKEN")).setActivity(Activity.playing("_deviantart")).addEventListeners(new DACommand()).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
