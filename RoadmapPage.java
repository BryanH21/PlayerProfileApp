//Bryan Hernandez
//shows goals + training heatmap

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class RoadmapPage extends JPanel
{
    // 2D array for Chapter 8
    private final int[][] sessions; // minutes trained per day
    private final List<Goal> goals;
    RoadmapPage(List<Goal> goals, int[][] sessions)
    {
        this.goals = goals;
        this.sessions = sessions;
        setLayout(new BorderLayout());
        setBackground(Theme.DARK_BG);
        System.out.println("[ROADMAP] Building M/W/F schedule page");

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
            bar.setForeground(Theme.GOLD);           
            bar.setBackground(Theme.CARD_BG);        
            bar.setOpaque(true);
            bar.setFont(bar.getFont().deriveFont(Font.BOLD, 12f));
            bar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
                @Override protected java.awt.Color getSelectionForeground() { return java.awt.Color.WHITE; }
                @Override protected java.awt.Color getSelectionBackground() { return java.awt.Color.WHITE; }
            });
            row.add(bar, BorderLayout.EAST);
            goalsPanel.add(row);
            goalsPanel.add(Box.createVerticalStrut(8));
        }
        //Weekly Focus (Mon,Wed,Fri) with three 5 minute activities per day
        JPanel scheduleCard = new JPanel(new BorderLayout());
        scheduleCard.setOpaque(false);
        scheduleCard.setBorder(new EmptyBorder(8, 16, 16, 16));

        JPanel schedInner = new JPanel(new BorderLayout());
        schedInner.setBackground(Theme.CARD_BG);
        schedInner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.CARD_BORDER),
            new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel schedTitle = new JLabel("Weekly Focus (Mon/Wed/Fri) — v2");
        schedTitle.setForeground(Theme.TEXT);
        schedTitle.setFont(schedTitle.getFont().deriveFont(Font.BOLD, 14f));
        schedInner.add(schedTitle, BorderLayout.NORTH);

        // Arrays + nested loops (Chapter 8): days and 3 activities per day
        String[] days = {"Monday", "Wednesday", "Friday"};
        String[][] activities = {
            {"Dribbling Cones", "Wall Passes", "Ball Mastery"},
            {"Sprint Intervals", "Plyo Hops", "Core Plank"},
            {"First Touch", "Cross & Finish", "Cooldown Jog"}
        };
        int minutesPerTask = 5;

        JPanel cols = new JPanel(new GridLayout(1, days.length, 12, 0));
        cols.setOpaque(false);

        for (int i = 0; i < days.length; i++) 
        {
            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

            JLabel dayLbl = new JLabel(days[i]);
            dayLbl.setForeground(Theme.TEXT);
            dayLbl.setFont(dayLbl.getFont().deriveFont(Font.BOLD, 13f));
            dayLbl.setBorder(new EmptyBorder(0, 0, 6, 0));
            col.add(dayLbl);

            for (int j = 0; j < activities[i].length; j++) 
            {
                String line = "\u2022 " + activities[i][j] + " — " + minutesPerTask + " min";
                JLabel item = new JLabel(line);
                item.setForeground(new Color(198, 206, 217));
                item.setBorder(new EmptyBorder(2, 0, 2, 0));
                col.add(item);
            }

            cols.add(col);
        }

        schedInner.add(cols, BorderLayout.CENTER);
        scheduleCard.add(schedInner, BorderLayout.CENTER);

        //Compose page
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(goalsPanel);
        content.add(scheduleCard);
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
}