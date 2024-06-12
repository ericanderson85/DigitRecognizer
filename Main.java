import java.io.*;

public class Main {
    private static final String TRAIN_IMAGES_PATH = "resources/processed_data/trainImages.ser";
    private static final String TRAIN_LABELS_PATH = "resources/processed_data/trainLabels.ser";
    private static final String TEST_IMAGES_PATH = "resources/processed_data/testImages.ser";
    private static final String TEST_LABELS_PATH = "resources/processed_data/testLabels.ser";
    
    private static final int IMAGE_WIDTH = 28;
    private static final int IMAGE_HEIGHT = 28;
    private static final int INPUT_SIZE = IMAGE_HEIGHT * IMAGE_WIDTH;
    private static final int OUTPUT_SIZE = 10;
    
    private static final int TOTAL_EPOCHS = 45;
    private static final int EPOCHS = 15;
    
    private static final int[] HIDDEN_LAYERS = {64};
    private static final int BATCH_SIZE = 500;
    private static final double LEARNING_RATE = 0.0005;
    private static final ActivationFunction ACTIVATION_FUNCTION = new Tanh();
    private static final LossFunction LOSS_FUNCTION = new CrossEntropyLoss();
    
    
    public static void main(String[] args) {
        NeuralNetwork network = new NeuralNetwork(INPUT_SIZE, HIDDEN_LAYERS, OUTPUT_SIZE, ACTIVATION_FUNCTION,
                                                  LOSS_FUNCTION);
        train(network);
        System.out.println(predict(network, "testDigit.png"));
    }
    
    private static int predict(NeuralNetwork network, String imagePath) {
        double[] image = ImageProcessor.processImage(imagePath);
        return MathUtilities.argMax(network.predict(image));
    }
    
    private static void train(NeuralNetwork network) {
        double[][] trainImages = readData(TRAIN_IMAGES_PATH);
        double[][] trainLabels = readData(TRAIN_LABELS_PATH);
        double[][] testImages = readData(TEST_IMAGES_PATH);
        double[][] testLabels = readData(TEST_LABELS_PATH);
        
        int epochCount = 0;
        while (epochCount < TOTAL_EPOCHS) {
            network.train(trainImages, trainLabels, EPOCHS, LEARNING_RATE, BATCH_SIZE);
            
            int correct = 0;
            for (int i = 0; i < testLabels.length; i++) {
                double[] input = testImages[i];
                double[] output = network.predict(input);
                int actual = MathUtilities.argMax(testLabels[i]);
                int guess = MathUtilities.argMax(output);
                
                if (actual == guess) {
                    correct++;
                }
            }
            
            epochCount += EPOCHS;
            System.out.printf("\nEpoch %d:\n%d/%d = %.3f\n\n", epochCount, correct, testLabels.length,
                              (double) correct / testLabels.length);
        }
        
        try {
            network.save("network.ser");
        } catch (java.io.IOException e) {
            System.out.println("Unable to save network: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static double[][] readData(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (double[][]) in.readObject();
        } catch (IOException |
                 ClassNotFoundException e) {
            System.out.println("Failed to read " + fileName + ": " + e.getMessage());
            return null;
        }
    }
}