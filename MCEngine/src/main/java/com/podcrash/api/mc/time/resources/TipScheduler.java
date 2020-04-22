package com.podcrash.api.mc.time.resources;

import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TipScheduler implements TimeResource {
    private final List<String> tips;

    private final ChatColor tipHeaderColor = ChatColor.YELLOW;
    private final String tipHeader = "Tip> ";
    private int lastIndex;
    private volatile boolean cancel;

    public static TipScheduler fromURL(String urlString) throws IOException {
        URL url = new URL(urlString);
        return new TipScheduler(url.openConnection().getInputStream());
    }

    private TipScheduler(InputStream stream) throws IOException {
        this.tips = new ArrayList<>();
        readStream(stream);
        this.cancel = false;
    }
    private void readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String data;
        while ((data= reader.readLine()) != null) {
            tips.add(format(data));
        }
        reader.close();
    }

    private String getRandomTip() {
        if (tips.size() == 0) {
            return null;
        }
        Random rand = new Random();
        int index = rand.nextInt(tips.size());
        if(lastIndex == index) return getRandomTip();
        lastIndex = index;
        return tips.get(index);

    }

    private String format(String str) {
        str = str.trim(); //Clear leading/trailing whitespace
        str = str.replaceAll("\\P{Print}", "");
        str = ChatUtil.chat(str);
        return str;
    }


    @Override
    public void task() {
        String tip = getRandomTip();
        if (tip == null || cancel()) {
            return;
        }
        Bukkit.broadcastMessage(tipHeaderColor + tipHeader + ChatColor.RESET + tip);
    }

    @Override
    public boolean cancel() {
        return cancel;
    }

    @Override
    public void cleanup() {

    }

    public void reset() {
        this.cancel = true;
        unregister();

        this.cancel = false;
    }
}
