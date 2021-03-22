package DBObjects.DBCommands.CommandLists;

import DBException.DBException;

import java.nio.charset.StandardCharsets;
import java.util.Stack;

public class CommandCondition extends CommandList{

    String conditionString;

    public CommandCondition(String conditionString){
        this.conditionString = removeWhiteSpace(conditionString);
    }

    public Stack<String> stackifyCondition(String conditionString){
        Stack<String> parenStack = new Stack<>();
        Stack<String> finalStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (int i = 0; i < conditionString.length(); i++){
            if (isOperator(conditionString, i) > 0){
                operatorStack.push(conditionString.substring(i, isOperator(conditionString, i)));
            }
            if (isBoolean(conditionString, i) > 0){
                operatorStack.push(conditionString.substring(i, isBoolean(conditionString, i)));
            }
            if (isName(conditionString, i) > 0){
                finalStack.push(conditionString.substring(i, isName(conditionString, i)));
            }
            if (conditionString.charAt(i) == '('){
                parenStack.push(conditionString.substring(i, i + 1));
            }
            if (conditionString.charAt(i) == ')'){
                if (operatorStack.peek() != null){
                    finalStack.push(operatorStack.pop());
                }
            }
        }
        invertStack(finalStack);
        return finalStack;
    }

    public Stack<String> invertStack(Stack<String> oldStack){
        Stack<String> invertedStack = new Stack<>();
        while (!oldStack.isEmpty()){
            invertedStack.push(oldStack.pop());
        }
        return invertedStack;
    }

    public int isOperator(String conditionString, int operatorIndex){
        int conditionSize = conditionString.length();
        if (conditionString.charAt(operatorIndex) == '>'){
            return conditionSize;
        }
        if (conditionString.charAt(operatorIndex) == '<'){
            return conditionSize;
        }
        String twoDigitOp = conditionString.substring(operatorIndex, operatorIndex + 2);
        if (twoDigitOp.equals("==")){
            return conditionSize;
        }
        if (twoDigitOp.equals("!=")) {
            return conditionSize;
        }
        if (twoDigitOp.equals(">=")){
            return conditionSize;
        }
        if (twoDigitOp.equals("<=")){
            return conditionSize;
        }
        if (conditionString.substring(operatorIndex, operatorIndex + 4).equals("LIKE")){
            return conditionSize;
        }

        return -1;
    }

    public int isBoolean(String conditionString, int operatorIndex){
        int conditionSize = conditionString.length();
        if (isAnd()){
            return conditionSize;
        }
        if (isOr()){
            return conditionSize;
        }
        return -1;
    }
    public boolean isAnd(){
        return true;
    }

    public boolean isOr(){
        return true;
    }

    public boolean isLike(){
        return true;
    }

    public int isName(String conditionString, int startIndex){
        int endIndex = startIndex;
        boolean nameEnd = false;
        int i = startIndex;
        while (!nameEnd){
            if (isBoolean(conditionString, i) > 0){
                nameEnd = true;
                endIndex = i;
            }
            if (isOperator(conditionString, i) > 0){
                nameEnd = true;
                endIndex = i;
            }
            i++;
        }
        if (startIndex == endIndex){
            return -1;
        }
        return endIndex;
    }

    public boolean parseList(){return true;}

    public void convertStringToList() throws DBException {}

    @Override
    protected String[] splitValues(String argString) throws DBException {
        return new String[0];
    }

    @Override
    protected String removeWhiteSpace(String valueString) {
        return null;
    }
}
