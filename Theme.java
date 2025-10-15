//Bryan Hernandez
//UI color palette and styling constriants

import java.awt.Color;

/**
 * Contains all theme related constants and UI helper methods 
 */



public final class Theme 
{
    private Theme() {} //preventing instantiation
    public static final Color DARK_BG = new Color(20,24,33);
    public static final Color CARD_BG = new Color(28,36,51);
    public static final Color CARD_BORDER = new Color(50,60,78);
    public static final Color NAV_BG = new Color(16,19,27);
    public static final Color GOLD = new Color(255,215,0);
    public static final Color TEXT = Color.WHITE;
    public static final Color SUBTEXT = new Color(180,187,196);
    public static final Color TRACK = new Color(70,79,99);
    public static final Color BUTTON_BG = new Color(28,36,51);
    public static final int  SPACING_XS = 6;
    public static final int  SPACING_SM = 8;
    public static final float GAUGE_STROKE = 6f;
}

/**
 * Utility class for mapping numeric values to colors.
 */

 final class UiColors
 {
    private UiColors() {}
    public static Color colorForValue(int v)
    {
        if (v >= 80) return new Color(86,201,72); //Green
        if (v >= 60) return new Color(230,189,71); //Yellow
        return new Color(221,85,85); // red
    }
 }