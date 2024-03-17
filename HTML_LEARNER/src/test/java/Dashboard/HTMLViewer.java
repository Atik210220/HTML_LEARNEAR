package Dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class HTMLViewer extends JFrame {
    private JTextArea htmlTextArea;
    private JEditorPane htmlPane;
    private int practiceCount; // Track practice count
    private JProgressBar progressBar; // Progress bar

    public HTMLViewer() {
        setTitle("HTML Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a JTextArea for entering HTML code
        htmlTextArea = new JTextArea(10, 50);
        htmlTextArea.setLineWrap(true);
        htmlTextArea.setWrapStyleWord(true);

        // Create a JScrollPane for the JTextArea
        JScrollPane scrollPane = new JScrollPane(htmlTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Create a JButton for submitting the HTML code
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the entered HTML code
                String htmlCode = htmlTextArea.getText();
                // Validate and render HTML
                renderHTML(htmlCode);
                // Increase practice count
                practiceCount++;
                // Save practice count and HTML code to local storage
                savePractice(htmlCode, practiceCount);
                // Update progress bar
                updateProgressBar();
            }
        });
        
        
                  JButton openButton = new JButton("Back");
                openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               // Create and display another JFrame when the button is clicked
               Home homeScreen = new Home();
              homeScreen.setVisible(true);
              dispose();
            }
        });
        
        add(openButton, BorderLayout.EAST);
        
        

        // Create a JEditorPane for displaying HTML content
        htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");

        // Create a JScrollPane for the JEditorPane
        JScrollPane resultScrollPane = new JScrollPane(htmlPane);
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add components to the frame
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(submitButton, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(resultScrollPane, BorderLayout.CENTER);

        // Initialize progress bar
        progressBar = new JProgressBar(0, 100); // Assuming maximum practice count as 10
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.SOUTH);

        setSize(600, 400);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Load practice count and HTML code from local storage
        String[] practiceData = loadPractice();
        practiceCount = Integer.parseInt(practiceData[0]);
        htmlTextArea.setText(practiceData[1]);

        // Update progress bar
        updateProgressBar();
    }

    private void renderHTML(String htmlCode) {
        // Validate HTML tags
        boolean isValidHTML = validateHTML(htmlCode);
        if (!isValidHTML) {
            JOptionPane.showMessageDialog(this, "Invalid HTML code. Please check your tags.");
            return;
        }

        // Display HTML content
        htmlPane.setText(htmlCode);
    }

    private boolean validateHTML(String htmlCode) {
        // Regular expression to match HTML tags
        String tagPattern = "<\\s*([a-zA-Z]+)(\\s+[^>]*)?>|<\\s*/\\s*([a-zA-Z]+)\\s*>";

        // Stack to track opening tags
        Stack<String> tagStack = new Stack<>();

        // Compile regular expression pattern
        Pattern pattern = Pattern.compile(tagPattern);
        Matcher matcher = pattern.matcher(htmlCode);

        // Iterate over matches
        while (matcher.find()) {
            String tagName = matcher.group(1); // Get tag name
            if (tagName == null) {
                tagName = matcher.group(3); // Get closing tag name
                // Check if closing tag matches the last opening tag
                if (tagStack.isEmpty() || !tagStack.pop().equals(tagName)) {
                    return false; // Incorrect closing tag
                }
            } else {
                tagStack.push(tagName); // Push opening tag onto the stack
            }
        }

        return tagStack.isEmpty(); // Stack should be empty if all tags are properly closed
    }

    private void savePractice(String htmlCode, int count) {
        try (FileWriter writer = new FileWriter("practice_data.txt")) {
            writer.write(Integer.toString(count) + "\n"); // Save practice count
            writer.write(htmlCode); // Save HTML code
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] loadPractice() {
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
        return new String[]{"0", ""}; // Default values if data couldn't be loaded
    }

    private void updateProgressBar() {
        // Assuming maximum practice count as 10
        int maxCount = 100;
        float progress = (float) practiceCount / maxCount * 100;
        if (progress > 100) progress = 100; // Cap progress to 100%
        progressBar.setValue((int) progress);
        progressBar.setString("Practice Progress: " + practiceCount + "/" + maxCount);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HTMLViewer().setVisible(true);
            }
        });
    }
}
