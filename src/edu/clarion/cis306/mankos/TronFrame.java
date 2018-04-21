package edu.clarion.cis306.mankos;/*
* FORGOT THIS FOR DRAWING - DON'T FORGET IT FOR THIS
*
* AUTHOR: Chris Mankos
* DATE: 4/15/2018
*
* PURPOSE: Simulate an assembly language. Details such as opcodes and registers scattered throughout the code
*
* INPUT: A series of 'executions' from the user
* OUTPUT: Values in the accumulator register, varying based on user input
* */


// THE GUI IS OBNOXIOUS

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TronFrame extends JFrame {

    private Tron tron;

    // Simple labels directing user
    private JLabel lblInstruction;
    private JLabel lblInput;
    // Input area, getting instruction from user or input from user
    private JTextField fieldInstruction;
    private JTextField fieldInput;
    // Confirmation for above input
    private JButton btnInstruction;
    private JButton btnInput;
    // Stuff on the west
    private JTextArea areaProgram; // Shows the program as the user enters them on the right
    private JScrollPane paneProgram; // The user's program can be 100 lines - want to be able to scroll
    private JButton btnDump; // Memory dump on click
    // User relevant info
    private JTextArea areaOutput; // Come back later if scrolling issues - think there is enough space right now
    private JScrollPane paneOutput;

    // Override these later so that they take a tron object so that they can get display info from it
    private JPanel container; // Holding some of the elements above
    private JPanel leftStuff; // Self documenting code in action, folks
    private JPanel output; // Add panels to frames

    public TronFrame(){

        setTitle("SIMPLETRON");
        setSize(700, 500); // arbitrary and I /should/ put these as constants, right?
        setLayout(new BorderLayout()); // Because I want things in different places

        // Administrative bullshit. Creating labels and buttons and adding them to the panel to add to the frame
        lblInstruction = new JLabel("Enter Instructions:");
        lblInput = new JLabel("User Input:");
        fieldInstruction = new JTextField(10);
        fieldInput = new JTextField(10);
        btnInstruction = new JButton("Submit Instruction");
        btnInput = new JButton("Submit Input");
        // Interesting, weighs everything the same and ignores whitespace
        container = new JPanel(new GridLayout(2,3));

        container.add(lblInstruction);
        container.add(fieldInstruction);
        container.add(btnInstruction);
        container.add(lblInput);
        container.add(fieldInput);
        container.add(btnInput);
        add(container,"South");

        areaProgram = new JTextArea(30, 15);
        paneProgram = new JScrollPane(areaProgram);
        btnDump = new JButton("Display Memory Dump");
        leftStuff = new JPanel(new GridLayout(2,1));
        leftStuff.add(paneProgram);
        leftStuff.add(btnDump);
        add(leftStuff, "West");

        areaOutput = new JTextArea(25, 40);
        paneOutput = new JScrollPane(areaOutput);
        output = new JPanel();
        output.add(paneOutput);
        add(output);

        tron = new Tron(areaProgram, areaOutput);

        setVisible(true); // Seriously though, why isn't this true by default?


        // Right now copied from other button. Rewrite to behave
        btnInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get user submitted input
                try{
                    int input = Integer.parseInt(fieldInput.getText());
                    tron.continueRunning(input);
                }
                // They didn't enter an appropriate number
                catch (Exception ex){
                    areaOutput.setText("You did not enter an integer. Please try again.");
                }
            }
        });

        // Wants to start off tron if value is correct
        // Does minimal checking of instructions beyond "is it in instruction range" and "is it a number?"
        btnInstruction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get user submitted input
                try {
                    int instruction = Integer.parseInt(fieldInstruction.getText());
                    // If it is a valid instruction, want to add to our instruction array.
                    if(instruction != -9999) {
                        tron.addInstruction(instruction);
                        tron.displayInstruction(instruction);
                    }
                        // Need to add this to left panel as well
                    else { // Not entirely sure what the value to make things stop was
                        tron.execute();
                    }
                    // Otherwise, need to let user know they entered bad input
                }
                // They didn't enter an appropriate number
                catch (Exception ex){
                    areaOutput.setText("You did not enter an integer. Please try again.");
                }
            }
        });


        // Wanted to add a way of bailing out of a program other than closing and rerunning the app - this
        // button also resets everything to 0.
        btnDump.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tron.dump();
            }
        });

    }

    /* Good lord, end constructor*/

}
