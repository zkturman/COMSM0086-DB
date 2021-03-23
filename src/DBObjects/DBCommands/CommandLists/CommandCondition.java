package DBObjects.DBCommands.CommandLists;

import DBException.*;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandCondition extends CommandList{

    String conditionString;
    Stack<String> postFixConditions;

    public CommandCondition(String conditionArgs) throws DBException{
        conditionArgs = replaceOperators(conditionArgs);
        conditionString = removeWhiteSpace(conditionArgs);

    }

    public Stack<String> stackifyCondition(String conditionString) throws DBException{
        Stack<String> parenStack = new Stack<>();
        Stack<String> finalStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (int i = 0; i < conditionString.length(); i++){
            char c = conditionString.charAt(i);
            if (c == '('){
                parenStack.push(String.valueOf(c));
            }
            else if (c == ')'){
                if (!operatorStack.isEmpty()){
                    finalStack.push(operatorStack.pop());
                }
                if (!parenStack.isEmpty()){
                    parenStack.pop();
                }
            }
            else if (isOperator(c)){
                operatorStack.push(String.valueOf(c));
            }
            else if (isBoolean(c)){
                operatorStack.push(String.valueOf(c));
            }
            else if (Character.isLetterOrDigit(c)){
                int nameEnd = findName(conditionString, i);
                finalStack.push(conditionString.substring(i, nameEnd));
                i = nameEnd - 1;
            }
        }
        while (!operatorStack.isEmpty()){
            finalStack.push(operatorStack.pop());
        }
        if (!parenStack.isEmpty()){
            throw new DBCommandFormException("Command was not of the correct form. Try parentheses.");
        }
        return finalStack;
    }

    public Stack<String> invertStack(Stack<String> oldStack){
        Stack<String> invertedStack = new Stack<>();
        while (!oldStack.isEmpty()){
            invertedStack.push(oldStack.pop());
        }
        return invertedStack;
    }

    public String replaceOperators(String conditionString) throws DBException {
        if (conditionString.contains("&")){
            throw new InvalidCommandArgumentException("Condition contains invalid characters.");
        }
        if (conditionString.contains("+")){
            throw new InvalidCommandArgumentException("Condition contains invalid characters.");
        }
        if (conditionString.contains("~")){
            throw new InvalidCommandArgumentException("Condition contains invalid characters.");
        }

        //replace AND with &
        conditionString = replacePhrase(conditionString, "(?<=\\))\\s*and\\s*(?=\\()", "&");

        //replace OR with +
        conditionString = replacePhrase(conditionString, "(?<=\\))\\s*or\\s*(?=\\()", "+");

        //replace LIKE with ~
        conditionString = replacePhrase(conditionString, "(?<=\\w)\\s+like\\s+(?=\\w)", "~");

        //replace == with =
        conditionString = replacePhrase(conditionString, "\\s*\\=\\=\\s*", "=");

        //replace != with !
        conditionString = replacePhrase(conditionString, "\\s*\\!\\=\\s*", "!");

        //replace >= with @
        conditionString = replacePhrase(conditionString, "\\s*\\>\\=\\s*", "@");

        //replace <= with £
        conditionString = replacePhrase(conditionString, "\\s*\\<\\=\\s*", "£");

        return conditionString;
    }

    public String replacePhrase(String phrase, String regex, String replacement){
        Pattern replacePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = replacePattern.matcher(phrase);
        m.find();
        phrase = m.replaceAll(replacement);
        return phrase;
    }

    public boolean isOperator(char symbol){
        switch(symbol){
            case '>':
            case '<':
            case '=':
            case '!':
            case '~':
                return true;
            default:
                return false;
        }
    }

    public boolean isBoolean(char symbol){
        return symbol == '&' || symbol == '+';
    }

    public int findName(String conditionString, int startIndex){
        int endIndex = startIndex;
        boolean nameEnd = false;
        int i = startIndex;
        while (!nameEnd){
            char c = conditionString.charAt(i);
            if (isBoolean(c) || isOperator(c)){
                nameEnd = true;
                endIndex = i;
            }
            else if (c == '(' || c == ')'){
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

    public boolean parseList() throws DBException {
        postFixConditions = stackifyCondition(conditionString);
        postFixConditions = invertStack(postFixConditions);
        Stack<String> stackToVerify = (Stack<String>) postFixConditions.clone();
        if (!tryConditions(stackToVerify)){
            throw new DBCommandFormException("Conditions were not of the correct form");
        }
        return true;
    }

    public boolean tryConditions(Stack<String> conditions) throws DBException{
        Stack<String> valueStack = new Stack<>();
        while(!conditions.isEmpty()){
            if (isSymbol(conditions.peek())){
                conditions.pop();
                try {
                    //net gain of one value for testing
                    valueStack.pop();
                }
                catch (EmptyStackException ese){
                    throw new DBCommandFormException("Conditions were not of the correct form.");
                }
            }
            else{
                valueStack.push(conditions.pop());
            }
        }
        return true;
    }

    public boolean isSymbol(String piece){
        switch (piece){
            case "&":
            case "+":
            case "~":
            case "=":
            case "!":
            case "<":
            case ">":
            case "£":
            case "@":
                return true;
            default:
                return false;
        }
    }

    public void convertStringToList() throws DBException {}

    @Override
    protected String[] splitValues(String argString) throws DBException {
        return new String[0];
    }

    @Override
    protected String removeWhiteSpace(String valueString) {
        return null;
    }

    public static void test(){
        try {
            CommandCondition test1 = new CommandCondition("test");
            String replace1 = test1.replaceOperators("((A==x) aNd (B>=c)) or (C != d)");
            assert replace1.equals("((A=x)&(B@c))+(C!d)");
            String replace2 = test1.replaceOperators("(Are==xeo) and (b==c)");
            Stack<String> testStack = test1.stackifyCondition(replace2);
        }
        catch (DBException dbe){}
        System.out.println("CommandCondition passed.");
    }
}
