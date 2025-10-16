//shows goals + training heatmap

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class RoadmapPage extends JPanel
{
    // 2D array (weeks x days) for Chapter 8
    private final int[][] sessions; // minutes trained per day
    private final List<Goal> goals;
    RoadmapPage(List<Goal> goals, int[][] sessions)
    {
        this.goals = goals;
        this.sessions = sessions;
        setLayout(new BorderLayout());
        setBackground(Theme.DARK_BG);

        //Goals list
        JPanel goalsPanel = new JPanel();
        goalsPanel.setOpaque(false);
        goalsPanel.setLayout(new BoxLayout(goalsPanel, BoxLayout.Y_AXIS));
        goalsPanel.setBorder(new EmptyBorder(16, 16, 8, 16));
        JLabel h1 = new JLabel("Goals");
        h1.setForeground(Theme.TEXT);
        h1.setFont(h1.getFont().deriveFont(Font.BOLD, 18f));
        goalsPanel.add(h1);
        goalsPanel.add(Box.createVerticalStrut(8));
        for (Goal g : goals)
        {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBackground(Theme.CARD_BG);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER),
                new EmptyBorder(8, 12, 8, 12)
            ));
            JLabel title = new JLabel(g.summary());
            title.setForeground(Theme.TEXT);
            row.add(title, BorderLayout.WEST);
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(g.progress());
            bar.setStringPainted(true);
            row.add(bar, BorderLayout.EAST);
            goalsPanel.add(row);
            goalsPanel.add(Box.createVerticalStrut(8));
        }
        // Heatmap (Chapter 8: 2D array + nested loops)
        JPanel heatmapCard = new JPanel(new BorderLayout());
        heatmapCard.setOpaque(false);
        heatmapCard.setBorder(new EmptyBorder(8, 16, 16, 16));
        JPanel inner = new JPanel();
        inner.setBackground(Theme.CARD_BG);
        inner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.CARD_BORDER),
            new EmptyBorder(12, 12, 12, 12)
        ));
        inner.setLayout(new BorderLayout());
        JLabel heatTitle = new JLabel("Training Heatmap (Weeks × Days)");
        heatTitle.setForeground(Theme.TEXT);
        heatTitle.setFont(heatTitle.getFont().deriveFont(Font.BOLD, 14f));
        inner.add(heatTitle, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(sessions.length, sessions[0].length, 4, 4));
        grid.setOpaque(false);

        // Nested loops over 2D array (Chapter 8)
        for (int w = 0; w < sessions.length; w++) {
            for (int d = 0; d < sessions[w].length; d++) {
                int val = sessions[w][d]; // minutes
                JLabel cell = new JLabel(String.valueOf(val), SwingConstants.CENTER);
                cell.setOpaque(true);
                // simple shading by value
                int shade = Math.min(255, 60 + (val * 3));
                cell.setBackground(new Color(28,36,51, 255));
                cell.setForeground(new Color(shade, shade, shade));
                cell.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER));
                grid.add(cell);
            }
        }
        inner.add(grid, BorderLayout.CENTER);
        heatmapCard.add(inner, BorderLayout.CENTER);

        //Compose page
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(goalsPanel);
        content.add(heatmapCard);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }
    // helper for example data
    static List<Goal> sampleGoals()
    {
        List<Goal> list = new ArrayList<>();
        list.add(new TrainingGoal("Increase Sprint Speed", "In Progress", 12, 5));
        list.add(new TrainingGoal("Finish 5/5 Dribbling Drills", "In Progress", 5, 3));
        list.add(new TrainingGoal("Cardio 100 min this week", "Not Started", 100, 0));
        return list;
    }
    static int[][] sampleSessions()
    {
        // 5 weeks × 7 days (In realworld this would be different)
        return new int[][] {
           {10,  0, 20,  0,  0, 30,  0},
           { 0, 15,  0,  0, 25,  0,  0},
           { 5,  0,  0, 10,  0, 20,  0},
           { 0, 10, 10,  0,  0, 25,  0},
           {15,  0,  0, 15,  0,  0, 30}
        };
    }
}