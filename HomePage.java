//Bryan Hernandez
// scrollable player profile screen (header + stat cards)

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

// Uses: Theme, Avatar, StatCard, Player, StatCategory (already defined in Theme.java / Model.java)
class HomePage extends JPanel
{
    HomePage(Player player)
    {
        setLayout(new BorderLayout());
        setBackground(Theme.DARK_BG);

        // Header (avatar, name, date)
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

        //Scrollable stat cards
        JPanel statsList = new JPanel();
        statsList.setOpaque(false);
        statsList.setLayout(new BoxLayout(statsList, BoxLayout.Y_AXIS));
        statsList.setBorder(new EmptyBorder(8, 16, 80, 16)); // leave space above nav

        for (StatCategory cat : player.categories)
        {
            statsList.add(new StatCard(cat, player));
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
