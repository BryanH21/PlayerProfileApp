//Bryan Hernandez
//UI color palette and styling constriants, shared UI components

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

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
    public static final class Spacing 
    {
        public static final int XS = 6, SM = 8, MD = 12, LG = 16;
        private Spacing() {}
    }
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


 /** Circular photo/initials with gold ring */
class Avatar extends JComponent
{
    private final String initials;
    private BufferedImage image;

    Avatar(String initials, String imagePath)
    {
        this.initials = initials;
        setPreferredSize(new Dimension(128, 128));
        if (imagePath != null)
        {
            try { image = ImageIO.read(new File(imagePath)); }
            catch (IOException ex) { System.err.println("Avatar load failed: " + imagePath + " (" + ex.getMessage() + ")"); }
        }
        if (image == null)
        {
            try {
                java.io.InputStream in = getClass().getResourceAsStream("/profile.jpg");
                if (in != null) image = ImageIO.read(in);
            } catch (IOException ex) {
                System.err.println("Resource avatar load failed: " + ex.getMessage());
            }
        }
    }

    @Override protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.GOLD);
        g2.fillOval(x, y, size, size);
        int inset = 6;
        Shape clip = new Ellipse2D.Float(x + inset, y + inset, size - 2*inset, size - 2*inset);
        g2.setClip(clip);
        if (image != null)
        {
            g2.drawImage(image, x + inset, y + inset, size - 2*inset, size - 2*inset, null);
        }
        else
        {
            g2.setColor(new Color(35, 41, 54));
            g2.fillOval(x + inset, y + inset, size - 2*inset, size - 2*inset);
            g2.setClip(null);
            g2.setColor(Theme.TEXT);
            Font f = getFont().deriveFont(Font.BOLD, size * 0.30f);
            g2.setFont(f);
            FontMetrics fm = g2.getFontMetrics();
            int tx = getWidth()/2 - fm.stringWidth(initials)/2;
            int ty = getHeight()/2 + fm.getAscent()/2 - 6;
            g2.drawString(initials, tx, ty);
        }
        g2.dispose();
    }
}

/** Top semicircle gauge (∩) */
class MiniGauge extends JComponent
{
    private int value; // 0..100
    MiniGauge(int value)
    {
        this.value = Math.max(0, Math.min(100, value));
        setPreferredSize(new Dimension(64, 44));
        setMinimumSize(new Dimension(64, 44));
    }
    void setValue(int v) {
        this.value = Math.max(0, Math.min(100, v));
        repaint();
    }
    @Override protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        int padX = 6, padY = 6; float stroke = Theme.GAUGE_STROKE; int safety = 2;

        int maxBoxW = w - padX*2 - (int)stroke;
        int maxBoxH = h - padY*2 - (int)stroke - safety;
        int box = Math.max(10, Math.min(maxBoxW, maxBoxH));

        int x = padX + (int)(stroke/2f);
        int y = padY + (int)(stroke/2f) + safety;

        g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Theme.TRACK); g2.drawArc(x, y, box, box, 180, -180);
        g2.setColor(UiColors.colorForValue(value));
        int sweep = (int)Math.round(180 * (value / 100.0));
        g2.drawArc(x, y, box, box, 180, -sweep);
        g2.dispose();
    }
}

/** Category card with title, gauge, sub stat grid, and editable button */
class StatCard extends JPanel
{
    private final StatCategory cat;
    private final Player owner;
    private final JLabel overallLbl; // updated after edits
    private final MiniGauge gauge; // NEW: keep reference to update after edits
    private final Map<String, JLabel> subValueLabels = new HashMap<>(); // NEW: substat name -> label

    StatCard(StatCategory cat, Player owner)
    {
        this.cat = cat;
        this.owner = owner;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));

        // Header: title on left, gauge + overall + edit button on right
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel(cat.name.toUpperCase());
        title.setForeground(Theme.TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        gauge = new MiniGauge(cat.overall);
        right.add(gauge);

        overallLbl = new JLabel(String.valueOf(cat.overall));
        overallLbl.setForeground(Theme.TEXT);
        overallLbl.setFont(overallLbl.getFont().deriveFont(Font.BOLD, 14f));
        right.add(overallLbl);

        JButton edit = new JButton("✏");
        edit.setMargin(new Insets(2, 6, 2, 6));
        edit.setFocusable(false);
        edit.setToolTipText("Edit this category's sub-stats");
        edit.addActionListener(e -> editSubstatAndSave());
        right.add(edit);

        header.add(right, BorderLayout.EAST);

        // Grid of sub stats
        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 6));
        grid.setOpaque(false);
        for (SubStat s : cat.subStats)
        {
            JLabel left = new JLabel(s.name);
            left.setForeground(new Color(198, 206, 217));

            JLabel rightStat = new JLabel(String.valueOf(s.value));
            rightStat.setHorizontalAlignment(SwingConstants.RIGHT);
            rightStat.setForeground(UiColors.colorForValue(s.value));

            grid.add(left);
            grid.add(rightStat);
            subValueLabels.put(s.name, rightStat);
        }

        card.add(header, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(6), BorderLayout.CENTER);
        card.add(grid, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);
    }

    private void editSubstatAndSave()
    {
        if (cat.subStats == null || cat.subStats.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "This category has no sub-stats to edit.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] names = new String[cat.subStats.size()];
        for (int i = 0; i < cat.subStats.size(); i++)
            names[i] = cat.subStats.get(i).name;

        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Choose a sub-stat to edit:",
                "Edit Sub-Stat",
                JOptionPane.PLAIN_MESSAGE,
                null,
                names,
                names[0]
        );
        if (choice == null) return; // cancelled

        SubStat target = null;
        for (SubStat s : cat.subStats)
        {
            if (s.name.equals(choice)) { target = s; break; }
        }
        if (target == null) return;

        String input = JOptionPane.showInputDialog(
                this,
                "Enter new value for " + target.name + " (0–100):",
                target.value
        );
        if (input == null) return; // cancelled

        input = input.trim();
        if (input.length() == 0) {
            JOptionPane.showMessageDialog(this, "Value cannot be empty.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newVal;
        try {
            newVal = Integer.parseInt(input);
            // Nested if (Chapter 5)
            if (newVal < 0 || newVal > 100) {
                if (newVal < 0) {
                    JOptionPane.showMessageDialog(this, "Too low (must be ≥ 0).", "Out of Range", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Too high (must be ≤ 100).", "Out of Range", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a whole number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // apply change
        target.value = newVal;

        JLabel lbl = subValueLabels.get(target.name);
        if (lbl != null) {
            lbl.setText(String.valueOf(target.value));
            lbl.setForeground(UiColors.colorForValue(target.value));
        }

        // recompute overall as average of sub stats (rounded)
        int sum = 0;
        for (SubStat s : cat.subStats) sum += s.value;
        cat.overall = Math.round(sum / (float) cat.subStats.size());
        gauge.setValue(cat.overall);

        // persist to CSV and refresh UI
        PlayerDataLoader.saveToCSV(owner, "data/player_data.csv");
        // LIVE RELOAD: read back from CSV and sync in-memory model so app state matches file
        Player reloaded = PlayerDataLoader.loadFromCSV("data/player_data.csv");
        if (reloaded != null && reloaded.categories != null) {
            // categories list in Player is final reference but mutable; replace contents
            owner.categories.clear();
            owner.categories.addAll(reloaded.categories);
        }
        overallLbl.setText(String.valueOf(cat.overall));
        revalidate();
        repaint();
        SwingUtilities.getWindowAncestor(this).repaint();
    }
}