//Bryan Hernandez
// Final Project

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

/* 
 * PlayerProfileApp
 * Focus: Home Page (mobile style)
 * Shows: circular profile photo, name, scrollable stat categories with sub stats, bottom nav
 * Next steps (later): Roadmap and split up program files
 */
public class PlayerProfileApp extends JFrame 
{
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> 
        {
            PlayerProfileApp app = new PlayerProfileApp();
            app.setVisible(true);
        });
    }
    public PlayerProfileApp() 
    {
        super("Player Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 800);  // mobile feel
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // MODEL (Example data)
        Player player = samplePlayer();
        // PAGES
        JPanel homePage = new HomePage(player);
        JPanel roadmapPage = placeholderPage("Roadmap (coming next)"); 
        pages.add(homePage, "HOME");
        pages.add(roadmapPage, "ROADMAP");
        add(pages, BorderLayout.CENTER);

        // NAV BAR
        BottomNav nav = new BottomNav("HOME", sel -> 
        {
            cardLayout.show(pages, sel);
        });
        add(nav, BorderLayout.SOUTH);
    }

    private JPanel placeholderPage(String text) 
    {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.DARK_BG);
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setForeground(Theme.TEXT);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }
    // HOME PAGE
    static class HomePage extends JPanel 
    {
        public HomePage(Player player) 
        {
            setLayout(new BorderLayout());
            setBackground(Theme.DARK_BG); // dark theme.. to be changed later into my business colors after project is over

            // Header with circular avatar + name
            JPanel header = new JPanel();
            header.setOpaque(false);
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
            header.setBorder(new EmptyBorder(24, 24, 8, 24));
            Avatar avatar = new Avatar(player.getDisplayInitials(), player.getPhotoPath());
            avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel name = new JLabel(player.name);
            name.setAlignmentX(Component.CENTER_ALIGNMENT);
            name.setForeground(Theme.TEXT);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 22f));
            JLabel subtitle = new JLabel("Updated " + LocalDate.now());
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitle.setForeground(Theme.SUBTEXT);
            subtitle.setFont(subtitle.getFont().deriveFont(12f));
            header.add(avatar);
            header.add(Box.createVerticalStrut(12));
            header.add(name);
            header.add(subtitle);

            // Scrollable stats 
            JPanel statsList = new JPanel();
            statsList.setOpaque(false);
            statsList.setLayout(new BoxLayout(statsList, BoxLayout.Y_AXIS));
            statsList.setBorder(new EmptyBorder(8, 16, 80, 16)); // bottom space above nav
            for (StatCategory cat : player.categories) 
            {
                statsList.add(new StatCard(cat));
                statsList.add(Box.createVerticalStrut(12));
            }
            JScrollPane scroll = new JScrollPane(statsList);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            add(header, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
        }
    }

    // UI: Avatar 
    static class Avatar extends JComponent 
    {
        private final String initials;
        private BufferedImage image;
        public Avatar(String initials, String imagePath) 
        {
            this.initials = initials;
            setPreferredSize(new Dimension(128, 128));
            if (imagePath != null) 
            {
                try 
                {
                    image = ImageIO.read(new File(imagePath));
                }
                catch (IOException ex) 
                {
                    System.err.println("Avatar load failed: " + imagePath + " (" + ex.getMessage() + ")");
                    // fallback to initials
                }
            }
            // Fallback: try to load /profile.jpg from classpath if not already loaded
            if (image == null) 
            {
                try 
                {
                    java.io.InputStream in = getClass().getResourceAsStream("/profile.jpg");
                    if (in != null) 
                    {
                        image = ImageIO.read(in);
                    }
                }
                catch (IOException ex) 
                {
                    System.err.println("Resource avatar load failed: " + ex.getMessage());
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) 
        {
            // Omit super.paintComponent(g); avatar fully repaints its background/clip intentionally.
            Graphics2D g2 = (Graphics2D) g.create();
            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // Outer ring
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

    // UI; Stat Card 
    static class StatCard extends JPanel 
    {
        public StatCard(StatCategory cat) 
        {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(12, 12, 12, 12));

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Theme.CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.CARD_BORDER),
                    new EmptyBorder(12, 12, 12, 12)
            ));

            // Header: Category Name + Gauge + Overall 
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);

            JLabel title = new JLabel(cat.name.toUpperCase());
            title.setForeground(Theme.TEXT);
            title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));

            header.add(title, BorderLayout.WEST);

            // Composite right side: gauge left, number right
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            right.setOpaque(false);

            MiniGauge gauge = new MiniGauge(cat.overall);
            JLabel overallLbl = new JLabel(String.valueOf(cat.overall));
            overallLbl.setForeground(Theme.TEXT);
            overallLbl.setFont(overallLbl.getFont().deriveFont(Font.BOLD, 14f));

            right.add(gauge); // gauge on the left
            right.add(overallLbl); // number on the right

            header.add(right, BorderLayout.EAST);

            // Sub stats grid
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
            }

            card.add(header, BorderLayout.NORTH);
            card.add(Box.createVerticalStrut(6), BorderLayout.CENTER);
            card.add(grid, BorderLayout.SOUTH);
            add(card, BorderLayout.CENTER);
        }
    }

    static class MiniGauge extends JComponent 
    {
        private final int value; // 0-99

        public MiniGauge(int value)
        {
            this.value = Math.max(0, Math.min(100, value));
            setPreferredSize(new Dimension(64, 44));
            setMinimumSize(new Dimension(64, 44));
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Internal padding
            int padX = 6;
            int padY = 6;

            // Stroke thickness and avoid clipping
            float stroke = 6f;
            int safety = 2;
            int maxBoxW = w - padX * 2 - (int) stroke;           // leave room for stroke
            int maxBoxH = h - padY * 2 - (int) stroke - safety;  // leave room for stroke + safety
            int box = Math.max(10, Math.min(maxBoxW, maxBoxH));  // square size that fits (finally!!)

            int x = padX + (int)(stroke / 2f);
            int y = padY + (int)(stroke / 2f) + safety; // push down so the top isnt out of frame

            // Apply stroke
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Background arc 
            g2.setColor(Theme.TRACK);
            g2.drawArc(x, y, box, box, 180, -180);

            // Value arc
            g2.setColor(UiColors.colorForValue(value));
            int sweep = (int) Math.round(180 * (value / 100.0));
            g2.drawArc(x, y, box, box, 180, -sweep);

            g2.dispose();
        }
    }

    // Navigation
    static class BottomNav extends JPanel 
    {
        private final JButton btnHome = roundButton("Home");
        private final JButton btnRoadmap = roundButton("Roadmap");

        public BottomNav(String initial, java.util.function.Consumer<String> onSelect) 
        {
            setBackground(Theme.NAV_BG);
            setBorder(new EmptyBorder(10, 18, 16, 18));
            setLayout(new GridLayout(1, 2, 20, 0));

            btnRoadmap.addActionListener(e -> onSelect.accept("ROADMAP"));
            btnHome.addActionListener(e -> onSelect.accept("HOME"));

            add(btnRoadmap);
            add(btnHome);
        }

        private static JButton roundButton(String text) 
        {
            JButton b = new JButton(text) 
            {
                @Override public void paintComponent(Graphics g) 
                {
                    // Omit super.paintComponent(g); painting full oval background intentionally
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    int d = Math.min(w, h);

                    // outer ring
                    g2.setColor(Theme.GOLD);
                    g2.fillOval(0, 0, d, d);

                    // inner fill
                    g2.setColor(Theme.BUTTON_BG);
                    g2.fillOval(4, 4, d - 8, d - 8);

                    // label
                    g2.setColor(Theme.TEXT);
                    Font f = getFont().deriveFont(Font.BOLD, 13f);
                    g2.setFont(f);
                    FontMetrics fm = g2.getFontMetrics(f);
                    String s = getText();
                    int tx = (d - fm.stringWidth(s)) / 2;
                    int ty = (d + fm.getAscent()) / 2 - 2;
                    g2.drawString(s, tx, ty);

                    g2.dispose();
                }

                @Override public Dimension getPreferredSize()
                {
                    return new Dimension(92, 92);
                }

                @Override public boolean contains(int x, int y) 
                {
                    int r = Math.min(getWidth(), getHeight())/2;
                    int cx = getWidth()/2, cy = getHeight()/2;
                    int dx = x - cx, dy = y - cy;
                    return dx*dx + dy*dy <= r*r;
                }
            };
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setForeground(Theme.TEXT);
            b.setBackground(Theme.BUTTON_BG);
            return b;
        }
    }

    // Sample Data (replace later with real player) 
    private static Player samplePlayer() 
    {
        List<StatCategory> cats = new ArrayList<>();
        cats.add(new StatCategory("Pace", 91, Arrays.asList(
                new SubStat("Acceleration", 92),
                new SubStat("Sprint Speed", 91)
        )));
        cats.add(new StatCategory("Shooting", 79, Arrays.asList(
                new SubStat("Positioning", 80),
                new SubStat("Finishing", 82),
                new SubStat("Shot Power", 84),
                new SubStat("Long Shots", 71),
                new SubStat("Volleys", 74),
                new SubStat("Penalties", 71)
        )));
        cats.add(new StatCategory("Passing", 70, Arrays.asList(
                new SubStat("Vision", 70),
                new SubStat("Crossing", 72),
                new SubStat("Free Kick", 54),
                new SubStat("Short Passing", 75),
                new SubStat("Long Passing", 58),
                new SubStat("Curve", 75)
        )));
        cats.add(new StatCategory("Dribbling", 87, Arrays.asList(
                new SubStat("Agility", 86),
                new SubStat("Balance", 85),
                new SubStat("Reactions", 81),
                new SubStat("Ball Control", 84),
                new SubStat("Dribbling", 89)
        )));
        cats.add(new StatCategory("Defending", 42, Arrays.asList(
                new SubStat("Interceptions", 42),
                new SubStat("Heading", 72),
                new SubStat("Marking", 35),
                new SubStat("Standing Tackle", 39),
                new SubStat("Sliding Tackle", 38)
        )));
        cats.add(new StatCategory("Physical", 76, Arrays.asList(
                new SubStat("Jumping", 81),
                new SubStat("Stamina", 80),
                new SubStat("Strength", 80),
                new SubStat("Aggression", 62)
        )));

        String photo = new File("data/profile.jpg").exists() ? "data/profile.jpg" : null;
        return new Player("Bryan Hernandez", photo, cats);
    }
}