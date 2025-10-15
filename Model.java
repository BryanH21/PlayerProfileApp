//Bryan Hernandez
//Contains Player, StatCategory, and SubsStat classes

import java.util.List;

/*
 * The Player class represents player profule 
 * This incldues name, photo path, and stats 
 */

class Player
{
    final String name;
    final String photoPath; // if null will have a default pic
    final List<StatCategory> categories;

    public Player(String name, String photoPath, List<StatCategory> categories)
    {
        this.name = name;
        this.photoPath = photoPath;
        this.categories = categories;
    }
    public String getDisplayInitials()
    {
        String[] parts = name.trim().split("\\s+");
        String a = parts.length > 0 ? parts[0].substring(0, 1) : "P";
        String b = parts.length > 1 ? parts[1].substring(0, 1) : "";
        return (a + b).toUpperCase();
    }
    public String getPhotoPath() { return photoPath; }
}

/*
 * Each category has a display name, an overall value, and a list of sub stats 
 */

class StatCategory
{
    final String name;
    final int overall;
    final List<SubStat> subStats;

    public StatCategory(String name, int overall, List<SubStat> subStats)
    {
        this.name = name;
        this.overall = overall;
        this.subStats = subStats;
    }
}

/**
 * Represents a single sub stat withiin a category
 */

class SubStat
{ 
    final String name;
    final int value;

    public SubStat(String name, int value)
    {
        this.name = name;
        this.value = value;
    }
}