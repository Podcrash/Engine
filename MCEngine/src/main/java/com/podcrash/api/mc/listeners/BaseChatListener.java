package com.podcrash.api.mc.listeners;

import com.podcrash.api.db.pojos.Rank;
import com.podcrash.api.mc.util.PrefixUtil;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class BaseChatListener extends ListenerBase {
    private int largestWordLength;
    private final Map<String, String[]> words;
    public BaseChatListener(JavaPlugin plugin) {
        super(plugin);
        this.words = new HashMap<>();

        loadConfigs();
    }


    //this will happen first
    @EventHandler(priority = EventPriority.LOW)
    public void chat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if(player.hasPermission("invicta.mute")){
            e.setCancelled(true);
            player.sendMessage(String.format("%sInvicta> %sYou are muted.", ChatColor.BLUE, ChatColor.GRAY));
            return;
        }


        /*
        if(badWordsFound(e.getMessage()).size() > 0) {
            player.sendMessage(String.format("%sInvicta> %sProfanity is strictly prohibited from Podcrash servers.", ChatColor.BLUE, ChatColor.GRAY));
            e.setCancelled(true);
            return;
        }

         */
        String prefix = findPrefix(player);

        e.setFormat(String.format("%s%s%s" + ChatColor.RESET + " %s%s",
                prefix,
                ChatColor.YELLOW,
                player.getName(),
                ChatColor.WHITE,
                StringEscapeUtils.escapeJava(e.getMessage()))
        );
    }

    public void loadConfigs() {
        try {
            //check if the file exists first
            if(!new File("/home/invicta/filter.csv").exists()) return;

            FileInputStream stream = new FileInputStream("/home/invicta/filter.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            //BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://docs.google.com/spreadsheets/d/1hIEi2YG3ydav1E06Bzf2mQbGZ12kh2fe4ISgLg_UBuM/export?format=csv").openConnection().getInputStream()));
            String line = "";
            int counter = 0;
            while((line = reader.readLine()) != null) {
                counter++;
                String[] content = null;
                try {
                    content = line.split(",");
                    if(content.length == 0) {
                        continue;
                    }
                    String word = content[0];
                    String[] ignore_in_combination_with_words = new String[]{};
                    if(content.length > 1) {
                        ignore_in_combination_with_words = content[1].split("_");
                    }

                    if(word.length() > largestWordLength) {
                        largestWordLength = word.length();
                    }
                    words.put(word.replaceAll(" ", ""), ignore_in_combination_with_words);
                    System.out.println(word);
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Loaded " + counter + " words to filter out");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String findPrefix(Player chatter) {
        String prefix = "";
        PodcrashSpigot spigot = PodcrashSpigot.getInstance();
        if (spigot.hasPPLOwner() && spigot.getPPLOwner().equals(chatter.getUniqueId())) {
            prefix = ChatColor.AQUA.toString() + ChatColor.BOLD + "PPL HOST";
        } else {
            Rank rank = PrefixUtil.getPlayerRole(chatter);
            if (rank != null) {
                prefix = PrefixUtil.getPrefix(rank);
            }
        }

        return prefix.isEmpty() ? "" : prefix + " ";
    }
    /**
     * Iterates over a String input and checks whether a cuss word was found in a list, then checks if the word should be ignored (e.g. bass contains the word *ss).
     * @param input
     * @return
     */
    public ArrayList<String> badWordsFound(String input) {
        if(input == null) {
            return new ArrayList<>();
        }

        // don't forget to remove leetspeak, probably want to move this to its own function and use regex if you want to use this

        input = input.replaceAll("1","i");
        input = input.replaceAll("!","i");
        input = input.replaceAll("3","e");
        input = input.replaceAll("4","a");
        input = input.replaceAll("@","a");
        input = input.replaceAll("5","s");
        input = input.replaceAll("7","t");
        input = input.replaceAll("0","o");
        input = input.replaceAll("9","g");


        ArrayList<String> badWords = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

        System.out.println(input);
        /*
        for(String[] words : words.values()) {
            System.out.println(Arrays.toString(words));
            for(String word : words) {
                if (input.contains(word)) {
                    System.out.println(word);
                    badWords.add(word);
                }
            }
            //write unit tests later
        }

         */

        // iterate over each letter in the word
        for(int start = 0; start < input.length(); start++) {
            // from each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached.
            for(int offset = 1; offset < (input.length()+1 - start) && offset < largestWordLength; offset++)  {
                String wordToCheck = input.substring(start, start + offset);
                if(words.containsKey(wordToCheck)) {
                    // for example, if you want to say the word bass, that should be possible.
                    String[] ignoreCheck = words.get(wordToCheck);
                    boolean ignore = false;
                    for(int s = 0; s < ignoreCheck.length; s++ ) {
                        if(input.contains(ignoreCheck[s])) {
                            ignore = true;
                            break;
                        }
                    }
                    if(!ignore) {
                        badWords.add(wordToCheck);
                    }
                }
            }
        }

        for(String s: badWords) {
            System.out.println(s + " qualified as a bad word in a username");
        }
        return badWords;

    }
}
