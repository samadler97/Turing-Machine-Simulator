import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class sadler_TM {

    public static void main(String[] args) {
        File inputFile = null;
        if (args.length < 1 || args.length > 3) {
            System.out.println("Incorrect number of arguments.");
            System.exit(0);
        }
        else {
            inputFile = new File(args[0]);
        }

        //Check for verbose
        boolean isVerbose = false;
        if (args.length == 3) {
            if (args[2].equals("verbose")) {
                isVerbose = true;
            }
        }

        //General variables used in this program
        String inputString = args[1];
        List<String> states = new ArrayList<>();
        List<String> inputAlphabet = new ArrayList<>();
        List<String> stackAlphabet = new ArrayList<>();
        List<String> transitions = new ArrayList<>();
        String startState = "";
        String acceptState = "";
        String rejectState = "";

        //Reading in file and setting up all of the variables
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String tempStates = reader.readLine();
            states = Arrays.asList(tempStates.split(","));
            String tempInpAlph = reader.readLine();
            inputAlphabet = Arrays.asList(tempInpAlph.split(","));
            String tempStkAlph = reader.readLine();
            stackAlphabet = Arrays.asList(tempStates.split(","));

            String tempLine;
            while ((tempLine = reader.readLine()).contains("->")) {
                transitions.add(tempLine);
            }

            startState = tempLine;
            acceptState = reader.readLine();
            rejectState = reader.readLine();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException x) {
            x.printStackTrace();
        }

        //Forming state objects and adding transitions to the state where they start in
        List<TMsim.State> stateObjects = new ArrayList<TMsim.State>();
        //States
        for (int i = 0; i < states.size(); i++) {
            String stateName = "";
            boolean isStart = false;
            boolean isAccept = false;
            boolean isReject = false;

            stateName = states.get(i);
            TMsim.State newState = new TMsim.State(stateName/*, isStart, isAccept, isReject*/);
            if (stateName.equals(startState)) {
                newState.startState = true;
            }
            else if (stateName.equals(acceptState)) {
                newState.acceptState = true;
            }
            else if (stateName.equals(rejectState)) {
                newState.rejectState = true;
            }

            //TMsim.State newState = new TMsim.State(stateName/*, isStart, isAccept, isReject*/);
            stateObjects.add(newState);
        }
        //Transitions
        for (int i = 0; i < transitions.size(); i++) {
            String tranStart = "";
            String tranInput = "";
            String tranEnd = "";
            String tranEndInput = "";
            String tranDirection = "";

            String[] tempTran = null;
            tempTran = transitions.get(i).split("\\)->\\(");
            String startTran = tempTran[0];
            startTran = startTran.substring(1);
            String endTran = tempTran[1];
            endTran = endTran.substring(0, endTran.length() - 1);

            String[] tempStart = startTran.split(",");
            tranStart = tempStart[0];
            tranInput = tempStart[1];
            String[] tempEnd = endTran.split(",");
            tranEnd = tempEnd[0];
            tranEndInput = tempEnd[1];
            tranDirection = tempEnd[2];

            TMsim.Transition newTransition = new TMsim.Transition(tranStart, tranInput, tranEnd, tranEndInput, tranDirection);

            for (int j = 0; j < stateObjects.size(); j++) {
                if (newTransition.startState.equals(stateObjects.get(j).name)) {
                    stateObjects.get(j).transitions.add(newTransition);
                    break;
                }
            }
        }

        //Checking if input string is malformed
        String[] inpStrCheck = inputString.split("");
        for (int i = 0; i < inpStrCheck.length; i++) {
            boolean pass = false;
            for (int j = 0; j < inputAlphabet.size(); j++) {
                if (inpStrCheck[i].equals(inputAlphabet.get(j))) {
                    pass = true;
                }
            }

            if (!pass) {
                System.out.println("Malformed.");
                System.exit(0);
            }
        }

        //Getting the current state
        TMsim.State currentState = null;
        for (int i = 0; i < stateObjects.size(); i++) {
            if (stateObjects.get(i).startState == true) {
                currentState = stateObjects.get(i);
                break;
            }
        }

        //Passing cleaned variables to TMSimulator for simulation and tape output
        System.out.println(TMSimulator(inputString, currentState, stateObjects, isVerbose));

    }

    public static String TMSimulator (String input, TMsim.State currentState, List<TMsim.State> states, boolean verbose) {
        //General variables needed for this method
        List<String> TMConfiguration = new ArrayList<>();
        List<String> tapeParts = new ArrayList<>();
        String tapeHead = currentState.name;
        int tapeHeadIndex = 0;
        String inputString = input;
        String direction = "";
        String tape = "";

        //Simulates the Turing machine running until an accept or reject state is reached
        while (!currentState.acceptState && !currentState.rejectState) {

            //Creating the tape and TM configuration
            //If the turing machine just stared, create a new tape using input contents
            if (currentState.startState) {
                for (int i = 0; i < tapeHeadIndex; i++) {
                    tape += inputString.charAt(i) + " ";
                }
                tape += tapeHead + " ";
                for (int i = tapeHeadIndex; i < inputString.length(); i++) {
                    tape += inputString.charAt(i) + " ";
                }
            }
            //Create tape using altered contents if turing machine is not at the start
            else {
                String newTape = "";
                for (int i = 0; i < tapeHeadIndex; i++) {
                    newTape += tapeParts.get(i) + " ";
                }
                newTape += tapeHead + " ";
                for (int i = tapeHeadIndex; i < tapeParts.size(); i++) {
                    newTape += tapeParts.get(i) + " ";
                }
                tape = newTape;
            }

            //Splitting tape into its parts
            tapeParts = Arrays.asList(tape.split(" "));
            tapeParts = new ArrayList<>(tapeParts);

            //Checking if tape head is at the end of the tape, and adds an underscore if it is
            if (tapeHeadIndex + 1 == tapeParts.size()) {
                tape += "_";
                tapeParts.add("_");
            }

            //Find current symbol to the right of the tape head
            String currInput = "";
            int currInputIndex = 0;
            for (int i = 0; i <= tapeParts.size(); i++) {
                if (tapeParts.get(i).equals(tapeHead)) {
                    i += 1;
                    currInput = tapeParts.get(i);
                    currInputIndex = i;
                    break;
                }

            }

            //Finding the appropriate transition for the input and making any replacements if necessary
            firstLoop:
            for (int i = 0; i < currentState.transitions.size(); i++) {
                if (currentState.transitions.get(i).input.equals(currInput)) {
                    tapeHead = currentState.transitions.get(i).endState;
                    direction = currentState.transitions.get(i).direction;
                    tapeParts.set(currInputIndex, currentState.transitions.get(i).inputReplace);

                    secondLoop:
                    for (int j = 0; j < states.size(); j++) {
                        if (states.get(j).name.equals(tapeHead)) {
                            currentState = states.get(j);
                            break firstLoop;
                        }
                    }
                }
            }

            //Adding tape contents to full tape configuration
            TMConfiguration.add(tape);

            //Setting up tape contents for use at next loop
            outer:
            for (int i = 0; i < states.size(); i++) {
                innter:
                for (int j = 0; j < tapeParts.size(); j++) {
                    if (tapeParts.get(j).equals(states.get(i).name)) {
                        tapeParts.remove(tapeParts.get(j));
                    }
                }
            }
            //Adding last tape contents if turing machine has reached an accept or reject state
            if (tapeHeadIndex >= 0) {
                if (currentState.acceptState || currentState.rejectState) {
                    String endTape = "";
                    for (int i = 0; i < tapeHeadIndex; i++) {
                        endTape += tapeParts.get(i) + " ";
                    }
                    endTape += tapeHead + " ";
                    for (int i = tapeHeadIndex; i < tapeParts.size(); i++) {
                        endTape += tapeParts.get(i) + " ";
                    }
                    TMConfiguration.add(endTape);
                }
            }

            //Moving forward or backward through the tape depending on direction
            if (direction.equals("R")) {
                tapeHeadIndex++;
            } else if (direction.equals("L")) {
                tapeHeadIndex--;
            }
        }

        if (verbose) {
            for (int i = 0; i < TMConfiguration.size(); i++) {
                System.out.println(TMConfiguration.get(i));
            }
        }

        if (currentState.acceptState) {
            return "Accepted!";
        }
        else {
            return "Rejected.";
        }
    }
}