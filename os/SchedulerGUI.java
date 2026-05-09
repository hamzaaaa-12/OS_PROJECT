
package os;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SchedulerGUI extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(18, 25, 35);
    private static final Color PANEL_COLOR = new Color(28, 36, 52);
    private static final Color CONTROL_BACKGROUND = new Color(34, 44, 62);
    private static final Color BUTTON_COLOR = new Color(90, 155, 255);
    private static final Color BUTTON_HOVER = new Color(75, 135, 235);
    private static final Color TEXT_COLOR = new Color(235, 241, 250);
    private static final Color HEADER_COLOR = new Color(165, 185, 220);
    private static final Color BORDER_COLOR = new Color(70, 86, 109);

    // Input components
    private JTextField numProcessesField;
    private JPanel processInputPanel;
    private JTextField quantumField;
    private java.util.List<JTextField> pidFields;
    private java.util.List<JTextField> arrivalFields;
    private java.util.List<JTextField> burstFields;
    private JButton addProcessButton;
    private JButton removeProcessButton;
    private JButton resetInputsButton;
    private JButton clearResultsButton;

    // Result components
    private JTable rrTable;
    private JTable srtfTable;
    private JPanel rrGanttPanel;
    private JPanel srtfGanttPanel;
    private JTextArea comparisonArea;

    // Data
    private SchedulingResult rrResult;
    private SchedulingResult srtfResult;

    public SchedulerGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        } catch (Exception ignored) {
        }

        setTitle("CPU Scheduling Comparator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 920);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        pidFields = new ArrayList<>();
        arrivalFields = new ArrayList<>();
        burstFields = new ArrayList<>();

        // Main layout
        setLayout(new BorderLayout(10, 10));

        // Top: Input Section
        add(createInputPanel(), BorderLayout.NORTH);

        // Center: Results and Gantt Charts
        add(createResultsPanel(), BorderLayout.CENTER);

        // Bottom: Comparison Section
        add(createComparisonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(PANEL_COLOR);
        TitledBorder inputBorder = BorderFactory.createTitledBorder("Input Section");
        inputBorder.setTitleColor(TEXT_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), inputBorder));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topPanel.setBackground(PANEL_COLOR);
        JLabel quantumLabel = new JLabel("Time Quantum (RR):");
        quantumLabel.setForeground(TEXT_COLOR);
        topPanel.add(quantumLabel);
        quantumField = new JTextField("2", 6);
        quantumField.setBackground(CONTROL_BACKGROUND);
        quantumField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        quantumField.setForeground(TEXT_COLOR);
        topPanel.add(quantumField);

        topPanel.add(Box.createHorizontalStrut(24));
        addProcessButton = new JButton("Add Process");
        removeProcessButton = new JButton("Remove Process");
        resetInputsButton = new JButton("Reset Inputs");
        styleButton(addProcessButton);
        styleButton(removeProcessButton);
        styleButton(resetInputsButton);
        topPanel.add(addProcessButton);
        topPanel.add(removeProcessButton);
        topPanel.add(resetInputsButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JLabel instructionLabel = new JLabel("Change process rows, edit values, then run a scheduler. Reset restores default inputs.");
        instructionLabel.setForeground(TEXT_COLOR);
        JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        instructionPanel.setBackground(PANEL_COLOR);
        instructionPanel.add(instructionLabel);
        mainPanel.add(instructionPanel, BorderLayout.SOUTH);

        // Process input panel with scroll
        processInputPanel = new JPanel();
        processInputPanel.setLayout(new BoxLayout(processInputPanel, BoxLayout.Y_AXIS));
        processInputPanel.setBackground(PANEL_COLOR);

        // Add header
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        headerPanel.setBackground(PANEL_COLOR);
        JLabel pidHeader = new JLabel("Process ID", SwingConstants.CENTER);
        JLabel arrivalHeader = new JLabel("Arrival Time", SwingConstants.CENTER);
        JLabel burstHeader = new JLabel("Burst Time", SwingConstants.CENTER);
        pidHeader.setForeground(TEXT_COLOR);
        arrivalHeader.setForeground(TEXT_COLOR);
        burstHeader.setForeground(TEXT_COLOR);
        headerPanel.add(pidHeader);
        headerPanel.add(arrivalHeader);
        headerPanel.add(burstHeader);
        headerPanel.setMaximumSize(new Dimension(400, 30));
        processInputPanel.add(headerPanel);

        // Add 3 default processes
        for (int i = 0; i < 3; i++) {
            addProcessRow();
        }

        JScrollPane scrollPane = new JScrollPane(processInputPanel);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10));
        buttonPanel.setBackground(PANEL_COLOR);
        JButton runRRBtn = new JButton("Run RR");
        JButton runSRTFBtn = new JButton("Run SRTF");
        JButton compareBothBtn = new JButton("Compare Both");
        clearResultsButton = new JButton("Clear Results");

        styleButton(runRRBtn);
        styleButton(runSRTFBtn);
        styleButton(compareBothBtn);
        styleButton(clearResultsButton);

        runRRBtn.addActionListener(e -> runRR());
        runSRTFBtn.addActionListener(e -> runSRTF());
        compareBothBtn.addActionListener(e -> runBoth());
        clearResultsButton.addActionListener(e -> clearResults());

        buttonPanel.add(runRRBtn);
        buttonPanel.add(runSRTFBtn);
        buttonPanel.add(compareBothBtn);
        buttonPanel.add(clearResultsButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add/Remove button actions
        addProcessButton.addActionListener(e -> {
            addProcessRow();
            processInputPanel.revalidate();
            processInputPanel.repaint();
        });

        removeProcessButton.addActionListener(e -> {
            if (pidFields.size() > 1) {
                removeLastProcessRow();
                processInputPanel.revalidate();
                processInputPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Must have at least one process!");
            }
        });

        resetInputsButton.addActionListener(e -> resetInputs());

        return mainPanel;
    }

    private void addProcessRow() {
        JPanel rowPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        rowPanel.setBackground(PANEL_COLOR);
        rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JTextField pidField = new JTextField(String.valueOf(pidFields.size() + 1));
        JTextField arrivalField = new JTextField("0");
        JTextField burstField = new JTextField("5");
        styleInputField(pidField);
        styleInputField(arrivalField);
        styleInputField(burstField);

        rowPanel.add(pidField);
        rowPanel.add(arrivalField);
        rowPanel.add(burstField);

        pidFields.add(pidField);
        arrivalFields.add(arrivalField);
        burstFields.add(burstField);

        rowPanel.setMaximumSize(new Dimension(400, 30));
        processInputPanel.add(rowPanel);
    }

    private void removeLastProcessRow() {
        if (!pidFields.isEmpty()) {
            processInputPanel.remove(processInputPanel.getComponentCount() - 1);
            pidFields.remove(pidFields.size() - 1);
            arrivalFields.remove(arrivalFields.size() - 1);
            burstFields.remove(burstFields.size() - 1);
        }
    }

    private void resetInputs() {
        quantumField.setText("2");
        while (!pidFields.isEmpty()) {
            removeLastProcessRow();
        }
        for (int i = 0; i < 3; i++) {
            addProcessRow();
        }
        processInputPanel.revalidate();
        processInputPanel.repaint();
        clearResults();
    }

    private void clearResults() {
        DefaultTableModel rrModel = (DefaultTableModel) rrTable.getModel();
        DefaultTableModel srtfModel = (DefaultTableModel) srtfTable.getModel();
        rrModel.setRowCount(0);
        srtfModel.setRowCount(0);

        rrGanttPanel.removeAll();
        rrGanttPanel.revalidate();
        rrGanttPanel.repaint();

        srtfGanttPanel.removeAll();
        srtfGanttPanel.revalidate();
        srtfGanttPanel.repaint();

        comparisonArea.setText("Run both algorithms to see comparison.");
        rrResult = null;
        srtfResult = null;
    }

    private void styleButton(JButton button) {
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorderPainted(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(BUTTON_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
    }

    private void styleInputField(JTextField field) {
        field.setBackground(CONTROL_BACKGROUND);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private JPanel createResultsPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Round Robin Results
        JPanel rrPanel = new JPanel(new BorderLayout(5, 5));
        rrPanel.setBackground(PANEL_COLOR);
        TitledBorder rrBorder = BorderFactory.createTitledBorder("Round Robin Results");
        rrBorder.setTitleColor(TEXT_COLOR);
        rrPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), rrBorder));

        String[] columns = {"Process", "Arrival", "Burst", "Completion", "TAT", "WT", "RT"};
        rrTable = new JTable(new DefaultTableModel(columns, 0));
        rrTable.setBackground(Color.WHITE);
        rrTable.setForeground(Color.BLACK);
        rrTable.setGridColor(Color.LIGHT_GRAY);
        rrTable.setRowHeight(26);
        rrTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        rrTable.getTableHeader().setForeground(Color.BLACK);
        rrTable.getTableHeader().setFont(rrTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        JScrollPane rrScrollPane = new JScrollPane(rrTable);
        rrScrollPane.setPreferredSize(new Dimension(600, 220));

        rrGanttPanel = new JPanel();
        rrGanttPanel.setPreferredSize(new Dimension(600, 120));
        rrGanttPanel.setBackground(Color.WHITE);
        TitledBorder rrGanttBorder = BorderFactory.createTitledBorder("Gantt Chart");
        rrGanttBorder.setTitleColor(Color.BLACK);
        rrGanttPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), rrGanttBorder));

        rrPanel.add(rrScrollPane, BorderLayout.CENTER);
        rrPanel.add(rrGanttPanel, BorderLayout.SOUTH);

        // SRTF Results
        JPanel srtfPanel = new JPanel(new BorderLayout(5, 5));
        srtfPanel.setBackground(PANEL_COLOR);
        TitledBorder srtfBorder = BorderFactory.createTitledBorder("SRTF Results");
        srtfBorder.setTitleColor(TEXT_COLOR);
        srtfPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), srtfBorder));

        srtfTable = new JTable(new DefaultTableModel(columns, 0));
        srtfTable.setBackground(Color.WHITE);
        srtfTable.setForeground(Color.BLACK);
        srtfTable.setGridColor(Color.LIGHT_GRAY);
        srtfTable.setRowHeight(26);
        srtfTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        srtfTable.getTableHeader().setForeground(Color.BLACK);
        srtfTable.getTableHeader().setFont(srtfTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        JScrollPane srtfScrollPane = new JScrollPane(srtfTable);
        srtfScrollPane.setPreferredSize(new Dimension(600, 220));

        srtfGanttPanel = new JPanel();
        srtfGanttPanel.setPreferredSize(new Dimension(600, 120));
        srtfGanttPanel.setBackground(Color.WHITE);
        TitledBorder srtfGanttBorder = BorderFactory.createTitledBorder("Gantt Chart");
        srtfGanttBorder.setTitleColor(Color.BLACK);
        srtfGanttPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), srtfGanttBorder));

        srtfPanel.add(srtfScrollPane, BorderLayout.CENTER);
        srtfPanel.add(srtfGanttPanel, BorderLayout.SOUTH);

        mainPanel.add(rrPanel);
        mainPanel.add(srtfPanel);

        return mainPanel;
    }

    private JPanel createComparisonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        TitledBorder compBorder = BorderFactory.createTitledBorder("Comparison");
        compBorder.setTitleColor(TEXT_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), compBorder));

        comparisonArea = new JTextArea(7, 50);
        comparisonArea.setEditable(false);
        comparisonArea.setBackground(CONTROL_BACKGROUND);
        comparisonArea.setForeground(TEXT_COLOR);
        comparisonArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        comparisonArea.setText("Run both algorithms to see comparison.");
        comparisonArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(comparisonArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private List<ProcessData> getInputProcesses() {
        List<ProcessData> processes = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();

        for (int i = 0; i < pidFields.size(); i++) {
            try {
                int pid = Integer.parseInt(pidFields.get(i).getText().trim());
                int arrival = Integer.parseInt(arrivalFields.get(i).getText().trim());
                int burst = Integer.parseInt(burstFields.get(i).getText().trim());

                // Validation
                if (pid <= 0) {
                    throw new IllegalArgumentException("PID must be positive for process " + (i + 1));
                }
                if (usedIds.contains(pid)) {
                    throw new IllegalArgumentException("Duplicate PID: " + pid);
                }
                if (arrival < 0) {
                    throw new IllegalArgumentException("Arrival time cannot be negative for PID " + pid);
                }
                if (burst <= 0) {
                    throw new IllegalArgumentException("Burst time must be positive for PID " + pid);
                }

                usedIds.add(pid);
                processes.add(new ProcessData(pid, arrival, burst));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in process " + (i + 1));
            }
        }

        if (processes.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one process");
        }

        return processes;
    }

    private int getQuantum() {
        try {
            int quantum = Integer.parseInt(quantumField.getText().trim());
            if (quantum <= 0) {
                throw new IllegalArgumentException("Time quantum must be positive");
            }
            return quantum;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid time quantum format");
        }
    }

    private void runRR() {
        try {
            List<ProcessData> processes = getInputProcesses();
            int quantum = getQuantum();
            rrResult = RRAlgorithm.execute(processes, quantum);
            displayResults(rrResult, rrTable, rrGanttPanel);
            updateComparison();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runSRTF() {
        try {
            List<ProcessData> processes = getInputProcesses();
            srtfResult = SRTFAlgorithm.execute(processes);
            displayResults(srtfResult, srtfTable, srtfGanttPanel);
            updateComparison();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runBoth() {
        try {
            List<ProcessData> processes = getInputProcesses();
            int quantum = getQuantum();

            rrResult = RRAlgorithm.execute(processes, quantum);
            srtfResult = SRTFAlgorithm.execute(processes);

            displayResults(rrResult, rrTable, rrGanttPanel);
            displayResults(srtfResult, srtfTable, srtfGanttPanel);
            updateComparison();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResults(SchedulingResult result, JTable table, JPanel ganttPanel) {
        // Update table
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (ProcessData p : result.processes) {
            model.addRow(new Object[]{
                "P" + p.id,
                p.arrivalTime,
                p.burstTime,
                p.completionTime,
                p.turnaroundTime,
                p.waitingTime,
                p.responseTime
            });
        }

        // Update Gantt chart
        ganttPanel.removeAll();
        ganttPanel.setLayout(new BorderLayout());
        ganttPanel.add(new GanttChartPanel(result.ganttChart), BorderLayout.CENTER);
        ganttPanel.revalidate();
        ganttPanel.repaint();
    }

    private void updateComparison() {
        if (rrResult == null || srtfResult == null) {
            comparisonArea.setText("Run both algorithms to see comparison.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-30s %-14s %-14s %-14s%n", "Metric", "Round Robin", "SRTF", "Best Algorithm"));
        sb.append("=".repeat(74)).append("\n");

        double rrTat = rrResult.avgTurnaroundTime;
        double srtfTat = srtfResult.avgTurnaroundTime;
        String bestTat = rrTat < srtfTat ? "Round Robin" : rrTat > srtfTat ? "SRTF" : "Tie";
        sb.append(String.format("%-30s %-14.2f %-14.2f %-14s%n", "Avg Turnaround Time", rrTat, srtfTat, bestTat));

        double rrWt = rrResult.avgWaitingTime;
        double srtfWt = srtfResult.avgWaitingTime;
        String bestWt = rrWt < srtfWt ? "Round Robin" : rrWt > srtfWt ? "SRTF" : "Tie";
        sb.append(String.format("%-30s %-14.2f %-14.2f %-14s%n", "Avg Waiting Time", rrWt, srtfWt, bestWt));

        double rrRt = rrResult.avgResponseTime;
        double srtfRt = srtfResult.avgResponseTime;
        String bestRt = rrRt < srtfRt ? "Round Robin" : rrRt > srtfRt ? "SRTF" : "Tie";
        sb.append(String.format("%-30s %-14.2f %-14.2f %-14s", "Avg Response Time", rrRt, srtfRt, bestRt));

        comparisonArea.setText(sb.toString());
    }

    // Inner class for Gantt Chart visualization
    private class GanttChartPanel extends JPanel {
        private List<GanttSlot> ganttChart;
        private Color[] processColors = {
            new Color(255, 179, 186),
            new Color(255, 223, 186),
            new Color(255, 255, 186),
            new Color(186, 255, 201),
            new Color(186, 225, 255),
            new Color(220, 186, 255),
            new Color(255, 186, 255),
            new Color(186, 255, 255)
        };

        public GanttChartPanel(List<GanttSlot> ganttChart) {
            this.ganttChart = ganttChart;
            setPreferredSize(new Dimension(600, 80));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (ganttChart == null || ganttChart.isEmpty()) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int maxTime = ganttChart.get(ganttChart.size() - 1).endTime;
            int width = getWidth() - 40;
            int height = 50;
            int y = 20;

            for (GanttSlot slot : ganttChart) {
                int x = 20 + (slot.startTime * width / maxTime);
                int slotWidth = ((slot.endTime - slot.startTime) * width / maxTime);

                // Draw slot
                if (slot.processId == 0) {
                    g2d.setColor(new Color(100, 100, 100)); // Darker gray for IDLE on dark background
                } else {
                    g2d.setColor(processColors[slot.processId % processColors.length]);
                }
                g2d.fillRect(x, y, slotWidth, height);

                // Draw border
                g2d.setColor(BORDER_COLOR);
                g2d.drawRect(x, y, slotWidth, height);

                // Draw label
                g2d.setColor(Color.BLACK);
                String label = slot.processId == 0 ? "IDLE" : "P" + slot.processId;
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                if (slotWidth >= labelWidth + 4) {
                    g2d.drawString(label, x + (slotWidth - labelWidth) / 2, y + height / 2 + 5);
                }

                // Draw time markers
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.setColor(Color.BLACK);
                String timeStr = String.valueOf(slot.startTime);
                g2d.drawString(timeStr, x - fm.stringWidth(timeStr) / 2, y + height + 15);
            }

            // Draw final time marker
            g2d.setColor(Color.BLACK);
            int finalX = 20 + width;
            String finalTime = String.valueOf(maxTime);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(finalTime, finalX - fm.stringWidth(finalTime) / 2, y + height + 15);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchedulerGUI gui = new SchedulerGUI();
            gui.setVisible(true);
        });
    }
}

