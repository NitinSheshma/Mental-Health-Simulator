import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

class MentalHealthSimulator {
    private int mood;
    private int energy;
    private int stress;

    public MentalHealthSimulator() {
        mood = 50;
        energy = 50;
        stress = 50;
    }

    public void calculate(int sleepHours, int workHours, int exerciseMinutes, int meditationMinutes) {
        mood = 50;
        energy = 50;
        stress = 50;

        energy += sleepHours * 10 - workHours * 5;
        mood += exerciseMinutes / 2 + meditationMinutes / 2 + sleepHours * 2 - workHours * 2;
        stress += workHours * 5 - exerciseMinutes / 3 - meditationMinutes / 2 - sleepHours;

        mood = Math.max(0, Math.min(100, mood));
        energy = Math.max(0, Math.min(100, energy));
        stress = Math.max(0, Math.min(100, stress));
    }

    public int getMood() { return mood; }
    public int getEnergy() { return energy; }
    public int getStress() { return stress; }

    public String getSuggestion() {
        if (mood < 40) return "You should relax and meditate 😊";
        if (energy < 40) return "You need rest 💤";
        if (stress > 60) return "Take a break! Stress is high 😟";
        return "You are doing well! 👍";
    }
}

public class MentalHealthDashboard {
    private static final String FILE_NAME = "mental_health_history.csv";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mental Health Simulator");
        frame.setSize(750, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        MentalHealthSimulator simulator = new MentalHealthSimulator();

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField sleepField = new JTextField();
        JTextField workField = new JTextField();
        JTextField exerciseField = new JTextField();
        JTextField meditateField = new JTextField();

        inputPanel.add(new JLabel("Hours of Sleep:"));
        inputPanel.add(sleepField);
        inputPanel.add(new JLabel("Work Hours:"));
        inputPanel.add(workField);
        inputPanel.add(new JLabel("Exercise Minutes:"));
        inputPanel.add(exerciseField);
        inputPanel.add(new JLabel("Meditation Minutes:"));
        inputPanel.add(meditateField);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Status panel
        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JProgressBar moodBar = new JProgressBar(0, 100);
        moodBar.setStringPainted(true);
        moodBar.setForeground(Color.GREEN);
        JProgressBar energyBar = new JProgressBar(0, 100);
        energyBar.setStringPainted(true);
        energyBar.setForeground(Color.BLUE);
        JProgressBar stressBar = new JProgressBar(0, 100);
        stressBar.setStringPainted(true);
        stressBar.setForeground(Color.RED);

        statusPanel.add(new JLabel("Mood"));
        statusPanel.add(moodBar);
        statusPanel.add(new JLabel("Energy"));
        statusPanel.add(energyBar);
        statusPanel.add(new JLabel("Stress"));
        statusPanel.add(stressBar);

        frame.add(statusPanel, BorderLayout.CENTER);

        // Suggestion label
        JLabel suggestionLabel = new JLabel("Your suggestion will appear here");
        suggestionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        suggestionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(suggestionLabel, BorderLayout.SOUTH);

        // Table for history
        String[] columns = {"Sleep", "Work", "Exercise", "Meditation", "Mood", "Energy", "Stress", "Suggestion"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable historyTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(historyTable);
        tableScroll.setPreferredSize(new Dimension(700, 200));
        frame.add(tableScroll, BorderLayout.SOUTH);

        // Load previous data
        loadHistory(tableModel);

        // Calculate button
        JButton calculateBtn = new JButton("Calculate Status");
        frame.add(calculateBtn, BorderLayout.EAST);

        calculateBtn.addActionListener(e -> {
            try {
                int sleep = Integer.parseInt(sleepField.getText());
                int work = Integer.parseInt(workField.getText());
                int exercise = Integer.parseInt(exerciseField.getText());
                int meditate = Integer.parseInt(meditateField.getText());

                simulator.calculate(sleep, work, exercise, meditate);

                moodBar.setValue(simulator.getMood());
                moodBar.setString(simulator.getMood() + "%");
                energyBar.setValue(simulator.getEnergy());
                energyBar.setString(simulator.getEnergy() + "%");
                stressBar.setValue(simulator.getStress());
                stressBar.setString(simulator.getStress() + "%");

                String suggestion = simulator.getSuggestion();
                suggestionLabel.setText(suggestion);

                // Save history
                String[] row = {
                        String.valueOf(sleep),
                        String.valueOf(work),
                        String.valueOf(exercise),
                        String.valueOf(meditate),
                        String.valueOf(simulator.getMood()),
                        String.valueOf(simulator.getEnergy()),
                        String.valueOf(simulator.getStress()),
                        suggestion
                };
                tableModel.addRow(row);
                saveHistory(row);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private static void saveHistory(String[] row) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(String.join(",", row) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadHistory(DefaultTableModel tableModel) {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                tableModel.addRow(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
