package at.granul.mason.inspector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * JPanel element used by TitledSimpleInspector to produce collapsible sections
 * <p/>
 * copyright by Roman Seidl, 2015
 * <p/>
 * Licensed under the Academic Free License version 3.0
 * See the file "LICENSE" for more information
 *
 * @author Roman Seidl
 * @version 1.0
 */
public class Toggler extends JPanel implements MouseListener{
    private static final long serialVersionUID = 1L;
    private boolean expanded;
    private BufferedImage open, closed;
    private ToggleEventListener listener;
    private Paint paint = Color.gray;
    private Paint background = getBackground();
    private boolean mirrored = false;


    public Toggler(ToggleEventListener listener, boolean expanded, boolean mirrored) {
        super();
        this.expanded = expanded;
        this.listener = listener;
        this.mirrored = mirrored;
        setExpanded(expanded);
        setPreferredSize(new Dimension(18, 18));
        createImages();
        setRequestFocusEnabled(true);
        addMouseListener(this);
    }

    public Toggler(ToggleEventListener listener)     {
        this(listener, false, false);
    }

    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
//        if(components != null)
//            setExpanded(aFlag);
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        listener.toggle(expanded);
    }

    public void setPaint(Color paint, Color background) {
        this.paint = paint;
        this.background = background;
        setBackground(background);
        createImages();
    }

    public void toggleSelection() {
        setExpanded(!expanded);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        if (expanded) {
            g2.drawImage(open, 0, 0, this);
        } else {
            g2.drawImage(closed, 0, 0, this);
        }
    }

    private void mirrorImages()
    {
        final AffineTransform mirrorTransform = AffineTransform.getTranslateInstance(open.getWidth(), 0);
        mirrorTransform.scale(-1.0, 1.0);
        final AffineTransformOp op = new AffineTransformOp(mirrorTransform, AffineTransformOp.TYPE_BILINEAR);
        open = op.filter(open, null);
        closed = op.filter(closed, null);
    }

    private void createImages() {
        final int h = getPreferredSize().height;
        final int h_border = 4;
        final int v_border = 4;

        //Triangle calculations
        final int a_d = (h - 2* h_border);
        final int h_d = (int) (a_d * Math.sqrt(3) / 2.0);
        final int r_d = (int) (a_d * Math.sqrt(3) / 3);

        final int w = a_d + 2* v_border;

        closed = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = closed.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(background);
        g2.fillRect(0, 0, w, h);

        final int xstart = v_border + r_d - h_d / 2;
        int[] x = new int[]{xstart, xstart + h_d, xstart};
        int[] y = new int[]{h_border, h / 2, h-h_border};
        Polygon p = new Polygon(x, y, 3);
        g2.setPaint(paint);
        g2.fill(p);
        g2.dispose();


        //image
        open = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        g2 = open.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(background);
        //fill with background
        g2.fillRect(0, 0, w, h);
        final int vstart = v_border + r_d - h_d / 2;
        x = new int[]{v_border, w / 2, w - v_border};
        y = new int[]{vstart, vstart + h_d, vstart};
        p = new Polygon(x, y, 3);
        g2.setPaint(paint);
        g2.fill(p);
//        g2.setPaint(Color.blue.brighter());
//        g2.draw(p);
        g2.dispose();

        if(mirrored)
            mirrorImages();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        toggleSelection();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

