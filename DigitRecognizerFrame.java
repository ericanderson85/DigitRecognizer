import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DigitRecognizerFrame extends JFrame {
    private DrawPanel drawPanel;
    private NeuralNetwork network;
    
    {
        try {
            network = NeuralNetwork.load("network.ser");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public DigitRecognizerFrame() {
        setTitle("Digit Recognizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        
        
        JPanel buttonsPanel = getjPanel();
        add(buttonsPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel getjPanel() {
        JPanel buttonsPanel = new JPanel();
        JButton clearButton = new JButton("Clear");
        JButton predictButton = new JButton("Predict");
        
        clearButton.addActionListener(e -> drawPanel.clear());
        predictButton.addActionListener(e -> {
            BufferedImage img = drawPanel.getImage();
            int predictedDigit = MathUtilities.argMax(network.predict(ImageProcessor.processImage(img)));
            JOptionPane.showMessageDialog(this, "Predicted Digit: " + predictedDigit);
        });
        
        buttonsPanel.add(clearButton);
        buttonsPanel.add(predictButton);
        return buttonsPanel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DigitRecognizerFrame::new);
    }
}
