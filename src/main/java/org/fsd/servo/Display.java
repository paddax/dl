package org.fsd.servo;

import com.google.common.collect.EvictingQueue;
import net.miginfocom.swing.MigLayout;
import org.fsd.servo.impl.DummyServo;
import org.fsd.servo.impl.PID;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

/**
 * Created by Peter Davis on 02/01/2017.
 */
public class Display {

    private final IServoControl servo;
    private final JLabel label;
    private final JProgressBar progress;
    private final JTextField text;
    private final JFrame frame;
    private final JPanel graph;
    private FileWriter output;
    private double setPoint;
    private IPID pid;
    private EvictingQueue<Double> queue = EvictingQueue.create(350);

    public Display(IServoControl control, IPID pid) {
        this.servo = control;
        this.pid = pid;
        try {
            output = new FileWriter(new File("data.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame = new JFrame();
        frame.setLayout(new MigLayout("fill", "[300][400]", "grow"));
        JPanel controls = new JPanel();
        controls.setLayout(new MigLayout("fill,ins 10", "grow", "grow"));
        label = new JLabel("Hello");
        text = new JTextField();
        text.setText("0");
        progress = new JProgressBar();
        progress.setMinimum(-1000);
        progress.setMaximum(1000);
        JButton button = new JButton("Update");

        JTextField p = new JTextField("" + pid.getProportional());
        JTextField i = new JTextField("" + pid.getIntegral());
        JTextField d = new JTextField("" + pid.getDerivative());

        controls.add(text, "growx");
        controls.add(button, "wrap");
        controls.add(progress, "growx,spanx,wrap");
        controls.add(new JLabel("Proportional"), "");
        controls.add(p, "growx, wrap");
        controls.add(new JLabel("Integral"), "");
        controls.add(i, "growx, wrap");
        controls.add(new JLabel("Derivative"), "");
        controls.add(d, "growx, wrap");
        controls.add(label, "south");

        graph = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(Color.RED);
                Dimension d = getSize();

                GeneralPath path = new GeneralPath();
                double x = 0;
                Iterator<Double> iter = queue.iterator();
                if(iter.hasNext()) {
                    path.moveTo(x, iter.next() * 100 + d.height / 2);
                }
                while(iter.hasNext()) {
                    x++;
                    path.lineTo(x, iter.next() * 100 + d.getHeight() /2);
                }
                g2.draw(path);
            }
        };

        graph.setBackground(Color.WHITE);

        frame.add(controls, "grow");
        frame.add(graph, "grow");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
        update();

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setPoint = Double.parseDouble(text.getText()) / 1000;
                    pid.setProportional(Double.parseDouble(p.getText()));
                    pid.setIntegral(Double.parseDouble(i.getText()));
                    pid.setDerivative(Double.parseDouble(d.getText()));
                } catch(NumberFormatException nfe) {

                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    output.flush();
                    output.close();
                } catch (IOException e1) {


                }
            }
        });
    }

    private DecimalFormat df = new DecimalFormat("#.####");
    private void update() {
        try {
            Thread.sleep(10);
            double error = setPoint - servo.getSensor().getPosition();
            queue.add(error);
            long dt = System.currentTimeMillis();
            double force = pid.getForce(dt, error); // + 9.8;
            servo.getActuator().setForce(force);
            servo.update(dt);
            label.setText(servo.toString() + " : " + setPoint);
            progress.setValue((int) Math.round(servo.getSensor().getPosition() * 1000));
            try {
                output.write(df.format(force / 1000));
                output.write(",");
                output.write("" + df.format(error));
                output.write(",");
                output.write(df.format(servo.getSensor().getSpeed()));
                output.write(",");
                output.write(df.format(servo.getSensor().getAcceleration()));
                output.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(Math.abs(servo.getSensor().getSpeed()) < 0.001) {
                setPoint = Math.random() * 2 - 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        graph.repaint();
        EventQueue.invokeLater( this::update);
    }

    private int update;

    public static void main(String[] args) {

        new Display(new DummyServo(), new PID());
    }
}
