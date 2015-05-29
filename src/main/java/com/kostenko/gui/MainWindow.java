/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.gui;

import static com.kostenko.gui.SwingConsole.run;
import com.kostenko.logic.Analyzer;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Pavel
 */
public class MainWindow {
    public MainWindow(int daysToAnalyze){
        
        Analyzer analyzer = new Analyzer();
        
        analyzer.getDifference(daysToAnalyze);

        JFrame f = new JFrame();
        
        JPanel tempPanel = new ChartPanel(analyzer.temperatures, analyzer.NAMES, "Temperature faults");
        tempPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        JPanel humidPanel = new ChartPanel(analyzer.humidities, analyzer.NAMES, "Humidity faults");
        humidPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        f.getContentPane().setLayout(new GridLayout(1,2));
        f.getContentPane().add(tempPanel);
        f.getContentPane().add(humidPanel);


        run(f,800,400);
    }
}
