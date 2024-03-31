package businesslogic;

import javax.swing.JProgressBar;
import java.io.*;

public class ProgressBarManager {
    private JProgressBar progressBar;

    public ProgressBarManager(int min, int max) {
        progressBar = new JProgressBar(min, max);
        progressBar.setStringPainted(true);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void updateProgressBar(int currentValue, int maxValue) {
        int progressValue = 100;
        float progress = (float) currentValue / maxValue * 100;
        if (progress > progressValue) progress = progressValue; // Cap progress to 100%
        progressBar.setValue((int) progress);
        progressBar.setString("Practice Progress: " + currentValue + "/" + maxValue);
    }

    public void savePractice(String htmlCode, int count) {
        try (FileWriter writer = new FileWriter("practice_data.txt")) {
            writer.write(Integer.toString(count) + "\n");
            writer.write(htmlCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] loadPractice() {
        try (BufferedReader reader = new BufferedReader(new FileReader("practice_data.txt"))) {
            String practiceCountStr = reader.readLine();
            String htmlCode = "";
            String line;
            while ((line = reader.readLine()) != null) {
                htmlCode += line + "\n";
            }
            return new String[]{practiceCountStr, htmlCode};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{"0", ""};
    }
}

