import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class ImageProcessor {
    public static double[] processImage(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();
            
            double[] grayscaleValues = new double[width * height];
            int index = 0;
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    double grayscale = (r + g + b) / 3.0;
                    grayscaleValues[index++] = grayscale / 255.0;
                }
            }
            
            return grayscaleValues;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static double[] processImage(BufferedImage image) {
        try {
            int width = image.getWidth();
            int height = image.getHeight();
            
            double[] grayscaleValues = new double[width * height];
            int index = 0;
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    double grayscale = (r + g + b) / 3.0;
                    grayscaleValues[index++] = grayscale / 255.0;
                }
            }
            
            return grayscaleValues;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void saveImageData() {
        System.out.println("Loading and saving images...");
        
        File dir = new File("resources/image_data");
        File[] files = dir.listFiles((_, name) -> name.endsWith(".png"));
        List<double[]> trainImages = new ArrayList<>();
        List<double[]> trainLabels = new ArrayList<>();
        List<double[]> testImages = new ArrayList<>();
        List<double[]> testLabels = new ArrayList<>();
        
        if (files == null) {
            System.out.println("No files found.");
            return;
        }
        
        Arrays.sort(files, (file1, file2) -> {
            String[] parts1 = file1.getName().split("\\.");
            String[] parts2 = file2.getName().split("\\.");
            int index1 = Integer.parseInt(parts1[2]);
            int index2 = Integer.parseInt(parts2[2]);
            int typeCompare = parts2[0].compareTo(parts1[0]);
            if (typeCompare != 0) {
                return typeCompare;
            }
            return Integer.compare(index1, index2);
        });
        
        int i = 0;
        for (File file : files) {
            String[] parts = file.getName().split("\\.");
            if (parts.length != 4) {
                continue;
            }
            String type = parts[0];
            int label = Integer.parseInt(parts[1]);
            double[] imageArray = ImageProcessor.processImage(file.getAbsolutePath());
            double[] oneHotLabel = new double[10];
            oneHotLabel[label] = 1.0;
            
            if ("train".equals(type)) {
                trainImages.add(imageArray);
                trainLabels.add(oneHotLabel);
            } else if ("test".equals(type)) {
                testImages.add(imageArray);
                testLabels.add(oneHotLabel);
            }
            
            if (i % 1000 == 0) {
                System.out.printf("%d : %s\n", i, file.getName());
            }
            
            i++;
        }
        
        double[][] trainImagesArray = trainImages.toArray(new double[0][]);
        double[][] trainLabelsArray = trainLabels.toArray(new double[0][]);
        double[][] testImagesArray = testImages.toArray(new double[0][]);
        double[][] testLabelsArray = testLabels.toArray(new double[0][]);
        
        System.out.println("Writing arrays to files.");
        
        writeArrayToFile(trainImagesArray, "resources/processed_data/trainImages.ser");
        writeArrayToFile(trainLabelsArray, "resources/processed_data/trainLabels.ser");
        writeArrayToFile(testImagesArray, "resources/processed_data/testImages.ser");
        writeArrayToFile(testLabelsArray, "resources/processed_data/testLabels.ser");
        
        System.out.println("Image data saving complete.");
    }
    
    private static void writeArrayToFile(double[][] array, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(array);
            System.out.println("Wrote " + fileName + " to file.");
        } catch (IOException e) {
            System.out.println("Failed to write " + fileName + ": " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        saveImageData();
    }
}