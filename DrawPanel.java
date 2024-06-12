import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class DrawPanel extends JPanel {
    private BufferedImage canvas;
    private Graphics2D g2;
    private int lastX, lastY;
    
    public DrawPanel() {
        setPreferredSize(new Dimension(280, 280));
        canvas = new BufferedImage(280, 280, BufferedImage.TYPE_INT_ARGB);
        g2 = canvas.createGraphics();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2.setPaint(Color.BLACK);
        g2.setStroke(new BasicStroke(26));
        MouseAdapter adapter = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
            
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                g2.drawLine(lastX, lastY, x, y);
                repaint();
                lastX = x;
                lastY = y;
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }
    
    public BufferedImage getImage() {
        float[] blurMatrix = {1f / 16f, 1f / 8f, 1f / 16f, 1f / 8f, 1f / 4f, 1f / 8f, 1f / 16f, 1f / 8f, 1f / 16f};
        Kernel kernel = new Kernel(3, 3, blurMatrix);
        ConvolveOp blurOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage blurredImage = blurOp.filter(canvas, null);
        
        BufferedImage scaledImage = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
        AffineTransform at = new AffineTransform();
        at.scale(28.0 / blurredImage.getWidth(), 28.0 / blurredImage.getHeight());
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage grayImage = new BufferedImage(blurredImage.getWidth(), blurredImage.getHeight(),
                                                    BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = grayImage.createGraphics();
        g.drawImage(blurredImage, 0, 0, null);
        g.dispose();
        
        scaledImage = scaleOp.filter(grayImage, scaledImage);
        return scaledImage;
    }
    
    
    public void clear() {
        g2.setPaint(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setPaint(Color.black);
        repaint();
    }
}
