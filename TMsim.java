import java.util.ArrayList;
import java.util.List;

public class TMsim {

    //State object
    public static class State {
        public String name = "";
        public boolean startState = false;
        public boolean acceptState = false;
        public boolean rejectState = false;
        public List<Transition> transitions = new ArrayList<>();

        public State (String name) {
            this.name = name;
        }
    }

    //Transition object
    public static class Transition {
        String startState = "";
        String input = "";
        String endState = "";
        String inputReplace = "";
        String direction = "";

        public Transition (String startState, String input, String endState, String inputReplace, String direction) {
            this.startState = startState;
            this.input = input;
            this.endState = endState;
            this.inputReplace = inputReplace;
            this.direction = direction;
        }
    }
}