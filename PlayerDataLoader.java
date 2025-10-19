// PlayerDataLoader.java, read/write player + substats from CSV

import java.io.*;
import java.util.*;

class PlayerDataLoader 
{

    static Player loadFromCSV(String playerPath) 
    {
        Player player = null;
        List<StatCategory> cats = new ArrayList<>();

        // Load player data (overall categories)
        try (BufferedReader reader = new BufferedReader(new FileReader(playerPath)))
        {
            String header = reader.readLine();
            String line = reader.readLine();
            if (line != null) {
                String[] p = line.split(",");
                String name = p[0];
                String photo = p[1].isEmpty() ? null : p[1];

                cats.add(new StatCategory("Pace",      Integer.parseInt(p[2]),  new ArrayList<>()));
                cats.add(new StatCategory("Shooting",  Integer.parseInt(p[3]),  new ArrayList<>()));
                cats.add(new StatCategory("Passing",   Integer.parseInt(p[4]),  new ArrayList<>()));
                cats.add(new StatCategory("Dribbling", Integer.parseInt(p[5]),  new ArrayList<>()));
                cats.add(new StatCategory("Defending", Integer.parseInt(p[6]),  new ArrayList<>()));
                cats.add(new StatCategory("Physical",  Integer.parseInt(p[7]),  new ArrayList<>()));

                player = new Player(name, photo, cats);
            }
        } catch (IOException e) 
        {
            System.err.println("Error reading player file: " + e.getMessage());
            return null;
        }

        System.out.println("[DATA] Loaded player row from " + playerPath + ": " + (player != null ? player.name : "<none>"));

        // Load sub stats (Category,SubStat,Value)
        File subFile = new File("data/substats.csv");
        System.out.println("[DATA] Looking for substats at " + subFile.getAbsolutePath() + " (exists=" + subFile.exists() + ")");
        if (player != null && subFile.exists()) {
            Map<String, StatCategory> byName = new HashMap<>();
            for (StatCategory c : cats) byName.put(c.name, c);

            try (BufferedReader br = new BufferedReader(new FileReader(subFile))) 
            {
                String header = br.readLine(); // skip header
                System.out.println("[DATA] Reading substats.csv...");
                String row;
                while ((row = br.readLine()) != null) {
                    String[] t = row.split(",");
                    System.out.println("[DATA] row: " + Arrays.toString(t));
                    if (t.length < 3) continue;
                    String catName = t[0].trim();
                    String statName = t[1].trim();
                    int val;
                    try { val = Integer.parseInt(t[2].trim()); }
                    catch (NumberFormatException ex) { continue; }

                    StatCategory c = byName.get(catName);
                    if (c != null) c.subStats.add(new SubStat(statName, val));
                }
            } catch (IOException e) {
                System.err.println("Error reading substats file: " + e.getMessage());
            }
        }

        System.out.println("[DATA] Loaded substat counts:");
        for (StatCategory c : cats) {
            System.out.println("  - " + c.name + ": " + (c.subStats == null ? 0 : c.subStats.size()) + " substats");
        }
        System.out.println("[DATA] Substat counts:");
        for (StatCategory c : cats) {
            System.out.println(" - " + c.name + ": " + (c.subStats == null ? "null" : c.subStats.size()));
        }
        return player;
    }

    static void saveToCSV(Player player, String playerPath) 
    {
        // Save category overalls back to player_data.csv
        try (PrintWriter writer = new PrintWriter(new FileWriter(playerPath))) {
            writer.println("Name,PhotoPath,Pace,Shooting,Passing,Dribbling,Defending,Physical");
            List<StatCategory> cats = player.categories;
            writer.printf("%s,%s", player.name, player.getPhotoPath() == null ? "" : player.getPhotoPath());
            for (StatCategory cat : cats) writer.printf(",%d", cat.overall);
            writer.println();
        } catch (IOException e) {
            System.err.println("Error saving player file: " + e.getMessage());
        }

        // Save substats back to substats.csv
        File subFile = new File("data/substats.csv");
        try (PrintWriter out = new PrintWriter(new FileWriter(subFile))) {
            out.println("Category,SubStat,Value");
            for (StatCategory c : player.categories) {
                if (c.subStats == null) continue;
                for (SubStat s : c.subStats) {
                    out.printf("%s,%s,%d%n", c.name, s.name, s.value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving substats file: " + e.getMessage());
        }
    }
}
