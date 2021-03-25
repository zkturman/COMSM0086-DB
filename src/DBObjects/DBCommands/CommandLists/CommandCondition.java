package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.DBTable;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandCondition extends CommandList{

    String conditionString;
    Stack<String> postFixConditions;
    DBTable tableToFilter;

    protected CommandCondition(){}

    public CommandCondition(String conditionArgs) throws DBException{
        conditionString = replaceOperators(conditionArgs);
    }

    public boolean parseList() throws DBException {
        convertStringToList();
        if (postFixConditions.isEmpty()){
            throw new InvalidCommandArgumentException("Conditions were empty.");
        }
        return true;
    }

    @Override
    protected void convertStringToList() throws DBException {
        postFixConditions = stackifyCondition(conditionString);
        postFixConditions = invertStack(postFixConditions);
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
                else {
                    throw new InvalidCommandArgumentException("Your parentheses are broken.");
                }
            }
            else if (isOperator(c) || isBoolean(c)){
                operatorStack.push(String.valueOf(c));
            }
            else if (Character.isLetterOrDigit(c)){
                int nameEnd = findName(conditionString, i);
                finalStack.push(conditionString.substring(i, nameEnd));
                i = nameEnd - 1;
            }
            else if (c == '\''){
                int stringEnd = findStringLiteral(conditionString, i);
                finalStack.push(conditionString.substring(i, stringEnd));
                i = stringEnd - 1;
            }
        }
        while (!operatorStack.isEmpty()){
            finalStack.push(operatorStack.pop());
        }
        if (!parenStack.isEmpty()){
            throw new DBConditionFormException("Command was not of the correct form. Try parentheses.");
        }
        return finalStack;
    }

    public int findName(String conditionString, int startIndex){
        int i = startIndex;
        while (i < conditionString.length()){
            char c = conditionString.charAt(i);
            if (isBoolean(c) || isOperator(c) || Character.isWhitespace(c)){
                return i;
            }
            else if (c == '(' || c == ')'){
                return i;
            }
            i++;
        }
        return i;
    }

    public int findStringLiteral(String conditionString, int startIndex){
        int endIndex = startIndex;
        boolean stringEnd = false;
        int i = startIndex + 1;

        while (i < conditionString.length() && !stringEnd){
            char c = conditionString.charAt(i);
            if (c == '\''){
                endIndex = i;
                stringEnd = true;
            }
            i++;
        }
        return endIndex + 1;
    }

    public Stack<String> invertStack(Stack<String> oldStack){
        Stack<String> invertedStack = new Stack<>();
        while (!oldStack.isEmpty()){
            invertedStack.push(oldStack.pop());
        }
        return invertedStack;
    }

    public void executeConditions(DBTable tableToFilter) throws DBException{
        this.tableToFilter = tableToFilter;
        tableToFilter.loadAttributeFile();
        int rowNumber = 0;
        for (int i = 0; i < tableToFilter.getNumRows() ; i++) {
            Stack<String> evaluation = (Stack<String>) postFixConditions.clone();
            if (!evaluateConditions(evaluation, i)) {
                tableToFilter.removeTableRow(i);
                i--;
            }
        }
    }

    public boolean evaluateConditions(Stack<String> conditions, int rowNumber) throws DBException {
        Stack<String> valueStack = new Stack<>();
        String value1, value2, operator;
        while(!conditions.isEmpty()){
            if (isSymbol(conditions.peek())){
                operator = conditions.pop();
                if (valueStack.isEmpty()){
                    throw new DBConditionFormException("Conditions were not of the correct form.");
                }
                value1 = valueStack.pop();
                if (valueStack.isEmpty()){
                    throw new DBConditionFormException("Conditions were not of the correct form.");
                }
                value2 = valueStack.pop();
                //values reversed after popping off stack
                DBExpression expression = new DBExpression(value2, value1, operator);
                valueStack.push(expression.performOperation(tableToFilter, rowNumber));
            }
            else{
                valueStack.push(conditions.pop());
            }
        }
        return getConditionResult(valueStack);
    }

    public boolean getConditionResult(Stack<String> resultStack) throws DBException {
        if (resultStack.isEmpty()){
            throw new DBConditionFormException("Conditions were not of the correct form.");
        }
        String finalValue = resultStack.pop();
        if (!resultStack.isEmpty()){
            throw new DBConditionFormException("Conditions were not of the correct form.");
        }
        if (finalValue.equals("1")){
            return true;
        }
        if (finalValue.equals("0")){
            return false;
        }
        else{
            throw new DBConditionFormException("Conditions were not of the correct form.");
        }
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

    public boolean isOperator(char symbol){
        switch(symbol){
            case '>':
            case '<':
            case '=':
            case '!':
            case '~':
            case '@':
            case '£':
                return true;
            default:
                return false;
        }
    }

    public boolean isBoolean(char symbol){
        return symbol == '&' || symbol == '+';
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
        conditionString = replacePhrase(conditionString, "(?<=\\w)\\s+like\\s+(?=(\\w+|\\'\\w+))", "~");

        //replace == with =
        conditionString = replacePhrase(conditionString, "\\s*\\=\\=\\s*(?=(\\w+|\\'\\w+))", "=");

        //replace != with !
        conditionString = replacePhrase(conditionString, "\\s*\\!\\=\\s*(?=(\\w+|\\'\\w+))", "!");

        //replace >= with @
        conditionString = replacePhrase(conditionString, "\\s*\\>\\=\\s*(?=(\\w+|\\'\\w+))", "@");

        //replace <= with £
        conditionString = replacePhrase(conditionString, "\\s*\\<\\=\\s*(?=(\\w+|\\'\\w+))", "£");

        return conditionString;
    }

    public String replacePhrase(String phrase, String regex, String replacement){
        Pattern replacePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = replacePattern.matcher(phrase);
        m.find();
        phrase = m.replaceAll(replacement);
        return phrase;
    }

    @Override
    protected String[] splitValues(String argString) throws DBException {
        return new String[0];
    }

    @Override
    protected String removeWhiteSpace(String valueString) {
        return valueString;
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
