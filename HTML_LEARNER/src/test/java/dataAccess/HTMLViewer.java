package dataAccess;

import Presentation.Home;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import businesslogic.ProgressBarManager;

public class HTMLViewer extends JFrame {
    private JTextArea htmlTextArea;
    private JEditorPane htmlPane;
    private int practiceCount; // Track practice count
    private ProgressBarManager progressBarManager; // Progress bar manager
     private JFrame parentFrame; // Reference to the parent JFrame

    public HTMLViewer(JFrame parentFrame) {
        
        this.parentFrame = parentFrame; // Store the reference to the parent JFrame
        setTitle("HTML Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize progress bar manager
        progressBarManager = new ProgressBarManager(0, 100); // Example min and max values


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
                progressBarManager.savePractice(htmlCode, practiceCount);
                // Update progress bar
                updateProgressBar();
            }
        });

        JButton openButton = new JButton("Back");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Navigate back to the parent JFrame
                parentFrame.setVisible(true);
                dispose(); // Close the current JFrame
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

        // Add progress bar to the frame
        add(progressBarManager.getProgressBar(), BorderLayout.SOUTH);
        
        setSize(780,620);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Load practice count and HTML code from local storage
        String[] practiceData = progressBarManager.loadPractice();
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
        String tagPattern = "<\\s*([a-zA-Z]+)(\\s+[^>]*)?>|<\\s*/\\s*([a-zA-Z]+)\\s*>";

        Stack<String> tagStack = new Stack<>();

        Pattern pattern = Pattern.compile(tagPattern);
        Matcher matcher = pattern.matcher(htmlCode);

        while (matcher.find()) {
            String tagName = matcher.group(1);
            if (tagName == null) {
                tagName = matcher.group(3);

                if (tagStack.isEmpty() || !tagStack.pop().equals(tagName)) {
                    return false;
                }
            } else {
                tagStack.push(tagName);
            }
        }

        return tagStack.isEmpty();
    }

    private void updateProgressBar() {
        // Update progress bar using ProgressBarManager
        progressBarManager.updateProgressBar(practiceCount, 100);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                 JFrame parentFrame = new JFrame(); // Create the parent JFrame
                parentFrame.setVisible(true); // Make it visible

                HTMLViewer htmlViewer = new HTMLViewer(parentFrame); // Pass the reference to HTMLViewer
                htmlViewer.setVisible(true); // Make HTMLViewer visible
            }
        });
    }
}
