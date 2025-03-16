import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
class Process {
    int id, arrivalTime, burstTime, waitingTime, turnaroundTime;
    Process(int id, int arrivalTime, int burstTime) {
        this.id = id; this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }
}

class Page {
    int processId, pageId;

    Page(int processId, int pageId) {
        this.processId = processId;
        this.pageId = pageId;
    }
}

public class SJFInterface extends JFrame {
    private JTextField txtArrivalTime;
    private JTextField txtBurstTime;
    private JTextField txtNumFrames;
    private JTextField txtPageSize;
    private DefaultTableModel model;
    private DefaultTableModel memoryModel;
    private Process[] processes;
    private Page[] frames;
    private int processCount = 0;
    private int frameCount;

    public SJFInterface() { setTitle("SJF Scheduler with Memory Allocation"); setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Painel para entrada de dados de processos
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Tempo de Chegada:"));
        txtArrivalTime = new JTextField();
        inputPanel.add(txtArrivalTime);
        inputPanel.add(new JLabel("Tempo de Burst:"));
        txtBurstTime = new JTextField();
        inputPanel.add(txtBurstTime);
        JButton btnAddProcess = new JButton("Adicionar Processo");
        inputPanel.add(btnAddProcess);
        JButton btnCalculate = new JButton("Calcular");
        inputPanel.add(btnCalculate);
        add(inputPanel, BorderLayout.NORTH);

        // Tabela para mostrar os processos e resultados
        String[] columns = {"ID", "Tempo de Chegada", "Tempo de Burst", "Tempo de Espera", "Tempo de Turnaround"};
        model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        //Painel para entrada de dados de memória

        JPanel memoryPanel = new JPanel(new GridLayout(3, 2));
        memoryPanel.add(new JLabel("Número de Quadros:"));
        txtNumFrames = new JTextField(); memoryPanel.add(txtNumFrames);
        memoryPanel.add(new JLabel("Tamanho da Página:"));
        txtPageSize = new JTextField(); memoryPanel.add(txtPageSize);
        JButton btnAllocateMemory = new JButton("Alocar Memória");
        memoryPanel.add(btnAllocateMemory); add(memoryPanel, BorderLayout.SOUTH);

        // Tabela para mostrar a alocação de memória
        String[] memoryColumns = {"Quadro", "Processo", "Página"};
        memoryModel = new DefaultTableModel(memoryColumns, 0);
        JTable memoryTable = new JTable(memoryModel);
        JScrollPane memoryScrollPane = new JScrollPane(memoryTable);
        add(memoryScrollPane, BorderLayout.EAST);

        // Ação para adicionar processos

        btnAddProcess.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { addProcess(); } });

        // Ação para calcular SJF

        btnCalculate.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { calculateSJF(); } });

        // Ação para alocar memória

        btnAllocateMemory.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { allocateMemory(); } });
    }

    private void addProcess() {
        try {
            int arrivalTime = Integer.parseInt(txtArrivalTime.getText());
            int burstTime = Integer.parseInt(txtBurstTime.getText());
            Process process = new Process(++processCount, arrivalTime, burstTime);
            if (processes == null) { processes = new Process[10]; // máximo 10 processos para este exemplo

            } processes[processCount - 1] = process;
            model.addRow(new Object[]{
                    process.id, process.arrivalTime, process.burstTime, "-", "-"});
            txtArrivalTime.setText(""); txtBurstTime.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores válidos!");
        }
    } private void calculateSJF() {
        if (processes == null || processCount == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum processo adicionado!");
            return;
        } //Ordenar os processos por burst time

        Arrays.sort(processes, 0, processCount, Comparator.comparingInt((Process p) -> p.burstTime) .thenComparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        for (int i = 0; i < processCount; i++) {
            Process process = processes[i];
            if (currentTime < process.arrivalTime) {
                currentTime = process.arrivalTime;
            }

            process.waitingTime = currentTime - process.arrivalTime;
            totalWaitingTime += process.waitingTime;
            process.turnaroundTime = process.waitingTime + process.burstTime;
            totalTurnaroundTime += process.turnaroundTime;
            currentTime += process.burstTime;

            // Atualizar tabela

            model.setValueAt(process.waitingTime, i, 3);
            model.setValueAt(process.turnaroundTime, i, 4);
        }

        JOptionPane.showMessageDialog(this, "Cálculos realizados!\nTempo médio de espera: " + (totalWaitingTime / processCount) + "\nTempo médio de turnaround: " + (totalTurnaroundTime / processCount));
    }

    private void allocateMemory() {
        try {
            frameCount = Integer.parseInt(txtNumFrames.getText());
            int pageSize = Integer.parseInt(txtPageSize.getText());
            frames = new Page[frameCount];
            memoryModel.setRowCount(0);

            for (int i = 0; i < frameCount; i++) {
                int processId = (i % processCount) + 1;
                int pageId = i / processCount;
                frames[i] = new Page(processId, pageId);
                memoryModel.addRow(new Object[]{i, processId, pageId});
            }

            JOptionPane.showMessageDialog(this, "Memória alocada com sucesso!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Insira valores válidos para os quadros e páginas.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { SJFInterface frame = new SJFInterface(); frame.setVisible(true); });
    }
}
