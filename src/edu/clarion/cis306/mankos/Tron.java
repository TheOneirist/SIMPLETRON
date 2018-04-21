package edu.clarion.cis306.mankos;// Under the hood functionality. Contains registers and ways to manipulate them.

import javax.swing.*;
import java.util.Arrays;

public class Tron {

    private final int memorySize = 100; // Memory might grow one day

    private int lazyCounter = 0;
    private int[] memory; // Where program is loaded into.
    private int memoryPointer; // For loading instructions into memory
    private int accumulator; // Sort of the user's interface - where they can see and manipulate data

    // Why? Don't get the point of doing this...
    private int instructionRegister; // Extra wall preventing me from accessing memory directly
    // Info taken from instruction register - tells where we want to go and what we want to do
    private int operationCode; // what we want to do
    private int operand;        // where we want to go
    private boolean stop; // true if the program wants to get user input
    private boolean instructionFlag;
    private boolean error;
    private String s;

    private JTextArea output;
    private JTextArea program;

    private String message; // if the user entered a bad instruction, need to know what went wrong


    // This allows tron to see the text areas directly so I don't have to do weird noodling above
    public Tron(JTextArea areaProgram, JTextArea areaOutput){
        program = areaProgram;
        output = areaOutput;

        memoryPointer = 0;
        memory = new int[memorySize];
        Arrays.fill(memory, 0); // Initialize the array to 0
        // Initialize all special registers to 0
        instructionRegister = 0;
        accumulator = 0;
        message = "";
        operand = 0;
        operationCode = 0;
        stop = false;
        instructionFlag = true;
        error = false;
    }

    // Runs instructions
    public void execute(){
        while(stop == false){
            executeInstruction();
        }
    }

    public void displayInstruction(int instruction){
        String s = program.getText();
        String ph = Integer.toString(lazyCounter);
        if(ph.length() < 2){
            ph = "0"+ph;
        }
        s += "\n"+ ph + " ? " + Integer.toString(instruction);
        program.setText(s);
        lazyCounter++;
    }

    // Should call write every single step somehow
    // Executes a single instruction


    public String getIssue(){
        return "PLACEHOLDER TEXT";
    }

    // Figures out what to do with the input based on instruction
    // Doesn't do anything unless the program is expecting input
    public void continueRunning(int input){
        operand = instructionRegister%100;
        memory[operand] = input;
        stop = false;
        execute();
    }

    // determines if the instruction the user entered is valid
    public boolean validInstruction(int instruction){
        /* Things that can go wrong:
        *       trying to add instructions to a full memory
        *       invalid instruction codes - checked during execution
        *       will, however, check digits length here*/

        boolean result = instruction < 10000 && instruction > -10000 && memoryPointer < memorySize;

        if (result == false){
            message = "You entered an invalid instruction.\nPlease check your instruction size and the " +
                    "\nnumber of instructions you have added - max 100.";
        }

        return result;

    }

    /****************************************************************************************************
     *                      FRAME INTERACTION STUFF
     *****************************************************************************************************/


    // Simply adds the user's instruction to the next available slot
    public void addInstruction(int instruction){
        if(instructionFlag == true) {
            memory[memoryPointer] = instruction;
            memoryPointer++;
            if (instruction == -9999) {
                memoryPointer = 0;
                instructionFlag = false;
                dump();
            }
        }
    }



    // Displays everything to the text area
    public void dump(){
        // All of this is building the string to display to the user
        String scaryFace = message; // https://www.youtube.com/watch?v=MTLp14MKDDU
        int count = 0;

        scaryFace = "REGISTERS:\nAccumulator:             \t" + Integer.toString(accumulator) +
                    "\nInstruction Counter: \t" + Integer.toString(memoryPointer) +
                    "\nInstruction Register:\t" + Integer.toString(instructionRegister) +
                    "\nOperation Code:       \t" + Integer.toString(operationCode) +
                    "\nOperand:                    \t" + Integer.toString(operand) + "\n\n\n";

        scaryFace += "MEMORY:\n \t  ";
        for(int i = 0; i < 10; i++){
            String ph = Integer.toString(i);
            while (ph.length() < 5){
                ph = "0" + ph;
            }
            scaryFace += ph + "\t";
        }

        scaryFace += "\n";

        for(int x = 0; x < 10; x++){
            scaryFace += Integer.toString(x) + "\t";
            for(int y = 0; y < 10; y++){
                String ph = Integer.toString(memory[count]);
                while(ph.length() < 5){
                    ph = "0" + ph;
                }
                scaryFace +=  ph + "\t";
                count++;
            }
            scaryFace += "\n";
        }
        output.setText(scaryFace);
        // reset all the fields to 0
        if(error == true) {
            reset();
        }
    }


    public void reset(){
        memoryPointer = 0;
        Arrays.fill(memory, 0); // empty our memory
        instructionRegister = 0;
        accumulator = 0;
        message = "";
        operand = 0;
        operationCode = 0;
        stop = false;
        instructionFlag = true;
        error = false;
    }

    /**********************************************************************************
     *          OBNOXIOUSLY LARGE METHOD I'M HIDING AT THE BOTTOM OF THE FILE
     ************************************************************************************/

    // executes a single instruction
    public void executeInstruction() {
        if (memoryPointer >= memorySize) {
            message = "MAXIMUM MEMORY EXCEEDED!\n";
            error = true;
            dump();
        }
        else {
            instructionRegister = memory[memoryPointer];
            operationCode = instructionRegister / 100;
            operand = instructionRegister % 100;

            switch (operationCode) {
                // I/O
                // Read word from keyboard into memory loc
                // Don't want this doing anything without getting user input - pauses the program waiting on button press
                case 10:
                    stop = true;
                    memoryPointer++;
                    break;
                // Write word from memory location to screen
                case 11:
                    s = output.getText() + "\nAccumulator: " + Integer.toString(memory[operand]);
                    output.setText(s);
                    memoryPointer++;
                    break;

                // Load/Store
                // Load word from memory location to accumulator
                case 20:
                    accumulator = memory[operand];
                    s = output.getText() + "\nAccumulator: " + Integer.toString(memory[operand]);
                    output.setText(s);
                    memoryPointer++;
                    break;
                // Store word from accumulator to memory location
                case 21:
                    memory[operand] = accumulator;
                    memoryPointer++;
                    break;

                // TRY CATCHES NEEDED HERE
                // Arithmetic Operations
                // Add memory location to accumulator (leaving value in accumulator
                case 30:
                    accumulator += memory[operand];
                    if (accumulator > 9999) {
                        error = true;
                        message = "**** OVERFLOW ERROR ****/n****SIMPLETRON EXECUTION ABNORMALLY TERMINATED****\n";
                        dump();
                    } else {
                        s = output.getText() + "\nAccumulator: " + Integer.toString(memory[operand]);
                        output.setText(s);
                        memoryPointer++;
                    }
                    break;

                // Subtract
                case 31:
                    accumulator -= memory[operand];
                    if (accumulator < -9999) {
                        error = true;
                        message = "****OVERFLOW ERROR****/n****SIMPLETRON EXECUTION ABNORMALLY TERMINATED****\n";
                        dump();
                    } else {
                        s = output.getText() + "\nAccumulator: " + Integer.toString(memory[operand]);
                        output.setText(s);
                        memoryPointer++;
                    }
                    //
                    break;
                // Divide
                case 32:
                    try {
                        accumulator /= memory[operand];
                        s = output.getText() + "\nAccumulator: " + Integer.toString(memory[operand]);
                        output.setText(s);
                        memoryPointer++;
                    } catch (Exception ex) {
                        error = true;
                        message = "****DIVISION BY 0****/n****SIMPLETRON EXECUTION ABNORMALLY TERMINATED****\n";
                        dump();
                    }
                    break;
                // Multiply
                case 33:
                    accumulator *= memory[operand];
                    if (accumulator > 9999 || accumulator < -9999) {
                        error = true;
                        message = "****OVERFLOW ERROR****/n****SIMPLETRON EXECUTION ABNORMALLY TERMINATED****\n";
                        dump();
                    } else {
                        s = output.getText() + "\nAccumulator: " + Integer.toString(memory[operand]);
                        output.setText(s);
                        memoryPointer++;
                    }
                    break;

                // Conditionals
                // Jump to specific memory location
                case 40:
                    memoryPointer = operand;
                    break;
                // Branch to location if value in accumulator is negative
                case 41:
                    if (accumulator < 0) {
                        memoryPointer = operand;
                    } else {
                        accumulator++;
                    }
                    break;
                // Branch if value in accumulator is 0
                case 42:
                    if (accumulator == 0) {
                        memoryPointer = operand;
                    } else {
                        memoryPointer++;
                    }
                    break;
                // Halt - program is done
                case 43:
                    stop = true;
                    dump();
                    break;
                default:
                    error = true;
                    message = "**** INVALID OPERATION CODE ****/n****SIMPLETRON EXECUTION ABNORMALLY TERMINATED****\n";
                    dump();
                    break;
            }
        }
    }
}
