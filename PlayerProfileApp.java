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
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> 
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.toString(), "Uncaught error", JOptionPane.ERROR_MESSAGE);
            });
            System.out.println("[APP] Launching PlayerProfileApp...");
            PlayerProfileApp app = new PlayerProfileApp();
            app.setVisible(true);
        });
    }
    public PlayerProfileApp() 
    {
        super("Player Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 800);  // make it feel like its a mobile version
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // MODEL: Try to load from CSV, fallback to sample data if not found/unreadable
        System.out.println("[MODEL] Loading player from CSV...");
        Player player = PlayerDataLoader.loadFromCSV("data/player_data.csv");
        if (player == null) {
            System.out.println("[MODEL] CSV load failed or missing. Using samplePlayer().");
            player = samplePlayer();
        } else {
            System.out.println("[MODEL] Loaded player: " + player.name);
        }
        try {
            System.out.println("[UI] Building pages...");
            JPanel homePage = new HomePage(player);
            java.util.List<Goal> goals = RoadmapPage.sampleGoals();
            int[][] sessions = RoadmapPage.sampleSessions();
            JPanel roadmapPage = new RoadmapPage(goals, sessions);
            pages.add(homePage, "HOME");
            pages.add(roadmapPage, "ROADMAP");
            add(pages, BorderLayout.CENTER);
            cardLayout.show(pages, "ROADMAP");
            System.out.println("[UI] Pages added.");
        } catch (Throwable ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.toString(), "UI Build Error", JOptionPane.ERROR_MESSAGE);
        }

        // NAV BAR
        BottomNav nav = new BottomNav("HOME", sel -> 
        {
            System.out.println("[NAV] switch to " + sel);
            cardLayout.show(pages, sel);
        });
        add(nav, BorderLayout.SOUTH);

        revalidate();
        repaint();
        System.out.println("[APP] UI ready. Window should be visible.");
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