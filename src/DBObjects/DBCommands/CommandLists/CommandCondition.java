package DBObjects.DBCommands.CommandLists;

import DBException.*;
import DBObjects.DBTable;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommandCondition handles the parsing and interpreting of conditions in certain
 * commands. When evaluated, this class will remove rows from a table that do not
 * meet the conditions. Conditions are converted to reverse Polish notation and
 * then evaluated on a stack. Current stackification requires parentheses surrounding
 * multi-expression conditions.
 */
public class CommandCondition extends CommandList{

    private String conditionString;
    private Stack<String> postFixConditions;
    private DBTable tableToFilter;

    /**
     * Default constructor for child classes.
     */
    protected CommandCondition(){}

    /**
     * Constructor for a CommandCondition.
     * @param conditionArgs String to be used to filter table rows.
     * @throws DBException Thrown if invalid operators are used in conditions.
     */
    public CommandCondition(String conditionArgs) throws DBException{
        conditionString = replaceOperators(conditionArgs);
    }

    /**
     * Replaces all operators with unique symbols for easier parsing.
     * @param conditionString String of conditions to be parsed.
     * @return Returns condition string with single character operators.
     * @throws DBException Thrown if erroneous operators are found during replacement.
     */
    private String replaceOperators(String conditionString) throws DBException {
        //Check & doesn't exist; replace AND with &
        findPhrase(conditionString, "(?<=\\))\\s*&\\s*(?=\\()");
        findPhrase(conditionString, "(?<=\\w)\\s*&\\s*(?=(\\w+|\\'\\w+))");
        conditionString = replacePhrase(conditionString, "(?<=\\))\\s*and\\s*(?=\\()", "&");

        //Check + doesn't exist; replace OR with +
        findPhrase(conditionString, "(?<=\\))\\s*\\+\\s*(?=\\()");
        findPhrase(conditionString, "(?<=\\w)\\s*\\+\\s*(?=(\\w+|\\'\\w+))");
        conditionString = replacePhrase(conditionString, "(?<=\\))\\s*or\\s*(?=\\()", "+");

        //Check ~ doesn't exist; replace LIKE with ~
        findPhrase(conditionString, "(?<=\\w)\\s*~\\s*(?=(\\w+|\\'\\w+))");
        conditionString = replacePhrase(conditionString, "(?<=\\w)\\s+like\\s+(?=(\\w+|\\'\\w+))", "~");

        //Check = doesn't exist; replace == with =
        findPhrase(conditionString, "(?<=\\w)\\s*\\=\\s*(?=(\\w+|\\'\\w+))");
        conditionString = replacePhrase(conditionString, "\\s*\\=\\=\\s*(?=(\\w+|\\'\\w+))", "=");

        //Check ! doesn't exist; replace != with !
        findPhrase(conditionString, "(?<=\\w)\\s*\\!\\s*(?=(\\w+|\\'\\w+))");
        conditionString = replacePhrase(conditionString, "\\s*\\!\\=\\s*(?=(\\w+|\\'\\w+))", "!");

        //Check @ doesn't exist; replace >= with @
        findPhrase(conditionString, "(?<=\\w)\\s*@\\s*(?=(\\w+|\\'\\w+))");
        conditionString = replacePhrase(conditionString, "\\s*\\>\\=\\s*(?=(\\w+|\\'\\w+))", "@");

        //Check £ doesn't exist; replace <= with £
        findPhrase(conditionString, "(?<=\\w)\\s*£\\s*(?=(\\w+|\\'\\w+))");
        conditionString = replacePhrase(conditionString, "\\s*\\<\\=\\s*(?=(\\w+|\\'\\w+))", "£");

        return conditionString;
    }

    /**
     * Looks for the existence of a pattern in a phrase.
     * @param phrase Phrase to search
     * @param regex Pattern to match on.
     * @throws DBException Thrown if the pattern is found within the phrase.
     */
    private void findPhrase(String phrase, String regex) throws DBException {
        Pattern replacePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = replacePattern.matcher(phrase);
        if (m.find()){
            throw new DBConditionFormException("Invalid operator used for conditions.");
        }
    }

    /**
     * Replaces a portion of a phrase based on a matching regex pattern.
     * @param phrase String to replace part of.
     * @param regex Regex pattern to use for replacement.
     * @param replacement Value to replace matches in phrase.
     * @return Returns updated phrase.
     */
    private String replacePhrase(String phrase, String regex, String replacement){
        Pattern replacePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = replacePattern.matcher(phrase);
        if (!m.find()){
            return phrase;
        }
        phrase = m.replaceAll(replacement);
        return phrase;
    }

    /**
     * Converts the condition string to stack.
     * @return True if the stack was created.
     * @throws DBException Thrown if no conditions were present in the condition string.
     */
    @Override
    public boolean processList() throws DBException {
        convertStringToList();
        if (postFixConditions.isEmpty()){
            throw new InvalidCommandArgumentException("Conditions were empty.");
        }
        return true;
    }

    /**
     * Converts condition string to a stack of attribute names, values, and operators.
     * @throws DBException Thrown if conditions are incorrectly formatted.
     */
    @Override
    protected void convertStringToList() throws DBException {
        postFixConditions = stackifyCondition(conditionString);
        postFixConditions = invertStack(postFixConditions);
    }

    /**
     * Turns a condition string into a stack of conditions in reverse Polish notation (RPN).
     * The string is broken down into operators and words based on parentheses and certain symbols.
     * @param conditionString Condition string from command.
     * @return Returns a stack of conditions in RPN format.
     * @throws DBException Thrown if conditions are incorrectly formatted.
     */
    private Stack<String> stackifyCondition(String conditionString) throws DBException{
        Stack<String> parenStack = new Stack<>();
        Stack<String> finalStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (int i = 0; i < conditionString.length(); i++){
            char c = conditionString.charAt(i);
            if (c == '('){
                parenStack.push(String.valueOf(c));
            }
            if (c == ')'){
                checkCanPop(operatorStack);
                finalStack.push(operatorStack.pop());
                checkCanPop(parenStack);
                parenStack.pop();
            }
            if (isOperator(c) || isBoolean(c)){
                operatorStack.push(String.valueOf(c));
            }
            if (Character.isLetterOrDigit(c)){
                int nameEnd = findName(conditionString, i);
                finalStack.push(conditionString.substring(i, nameEnd));
                i = nameEnd - 1;
            }
            if (c == '\''){
                int stringEnd = findStringLiteral(conditionString, i);
                finalStack.push(conditionString.substring(i, stringEnd));
                i = stringEnd - 1;
            }
        }
        while (!operatorStack.isEmpty()){
            finalStack.push(operatorStack.pop());
        }
        confirmStackEmpty(parenStack);
        return finalStack;
    }

    /**
     * Determines if a stack is empty.
     * @param stackToCheck Stack to evaluate
     * @throws DBException Thrown if the stack is not empty.
     */
    private void confirmStackEmpty(Stack<String> stackToCheck) throws DBException {
        if (!stackToCheck.isEmpty()){
            throw new DBConditionFormException("Command was not of the correct form.");
        }
    }

    /**
     * Determines the end index of a name in a command's condition.
     * @param conditionString String to evaluate.
     * @param startIndex Start index of the name.
     * @return Returns the end index of the name.
     */
    private int findName(String conditionString, int startIndex){
        int i = startIndex;
        while (i < conditionString.length()){
            char c = conditionString.charAt(i);
            if (isNameEnd(c)){
                return i;
            }
            i++;
        }
        return i;
    }

    /**
     * Determines if a character is a parenthesis.
     * @param charToCheck Character to evaluate
     * @return Returns true if charToCheck is '(' or ')'
     */
    private boolean isParentheses(char charToCheck){
        return charToCheck == '(' || charToCheck == ')';
    }

    /**
     * Determines if the character marks the end of a name in a condition.
     * @param charToCheck Character to evaluate
     * @return Returns true if the character is an operator, whitespace, or a parenthesis
     */
    private boolean isNameEnd(char charToCheck){
        return isBoolean(charToCheck) || isOperator(charToCheck) ||
                Character.isWhitespace(charToCheck) || isParentheses(charToCheck);
    }

    /**
     * Finds the end index of a string literal in a condition.
     * @param conditionString String of conditions to search.
     * @param startIndex Start index of string literal.
     * @return End index of string literal.
     */
    private int findStringLiteral(String conditionString, int startIndex){
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

    /**
     * Rebuilds stack so it is in the correct order for operations.
     * @param oldStack Reversed stack.
     * @return Returns stack in reverse order.
     */
    private Stack<String> invertStack(Stack<String> oldStack){
        Stack<String> invertedStack = new Stack<>();
        while (!oldStack.isEmpty()){
            invertedStack.push(oldStack.pop());
        }
        return invertedStack;
    }

    /**
     * Executes the conditions for all rows in a table.
     * @param tableToFilter DBTable on which to perform conditions.
     * @throws DBException Thrown if conditions are incorrectly formed.
     */
    public void executeConditions(DBTable tableToFilter) throws DBException{
        this.tableToFilter = tableToFilter;
        tableToFilter.loadAttributeFile();
        for (int i = 0; i < tableToFilter.getNumRows() ; i++) {
            @SuppressWarnings("unchecked")
            Stack<String> evaluation = (Stack<String>) postFixConditions.clone();
            if (!evaluateConditions(evaluation, i)) {
                tableToFilter.removeTableRow(i);
                i--;
            }
        }
    }

    /**
     * Evaluates the expressions in the command's conditions.
     * @param conditions Reverse Polish notation stack of values and operators.
     * @param rowNumber Table row index to evaluate
     * @return Returns true if the condition passed, false otherwise.
     * @throws DBException Thrown if the stack is incorrectly formed.
     */
    private boolean evaluateConditions(Stack<String> conditions, int rowNumber) throws DBException {
        Stack<String> valueStack = new Stack<>();
        String value1, value2, operator;
        while(!conditions.isEmpty()){
            if (isSymbol(conditions.peek())){
                operator = conditions.pop();
                checkCanPop(valueStack);
                value1 = valueStack.pop();
                checkCanPop(valueStack);
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

    /**
     * Determines if a stack is empty.
     * @param valueStack Stack to evaluate.
     * @throws DBException Thrown if the stack is empty.
     */
    private void checkCanPop(Stack<String> valueStack) throws DBException {
        if (valueStack.isEmpty()){
            throw new DBConditionFormException("Conditions were not of the correct form.");
        }
    }

    /**
     * Evaluates whether the condition result was true or false.
     * @param resultStack Resulting stack from condition evaluation.
     * @return Returns true if condition was true for row, else false.
     * @throws DBException Thrown if the final result was neither "1" nor "0".
     */
    private boolean getConditionResult(Stack<String> resultStack) throws DBException {
        String finalValue = popFinalResult(resultStack);
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

    /**
     * Gets the final result from a condition.
     * @param resultStack Final condition stack.
     * @return Returns the top value from the stack.
     * @throws DBException Thrown if the result stack has more than one value or if it is empty.
     */
    private String popFinalResult(Stack<String> resultStack) throws DBException {
        if (resultStack.isEmpty()){
            throw new DBConditionFormException("Conditions were not of the correct form.");
        }
        String finalValue = resultStack.pop();
        if (!resultStack.isEmpty()){
            throw new DBConditionFormException("Conditions were not of the correct form.");
        }
        return finalValue;
    }

    /**
     * Determines is part of a string is a recognized symbol.
     * @param piece Substring to check.
     * @return Returns true if substring is a recognized symbol.
     */
    private boolean isSymbol(String piece){
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

    /**
     * Determines if a symbol is a recognized operator.
     * @param symbol Character to check
     * @return Returns true if the symbol is an operator.
     */
    private boolean isOperator(char symbol){
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

    /**
     * Determines whether an operator is boolean.
     * @param symbol Symbol to check
     * @return Returns true if symbol is & or +
     */
    private boolean isBoolean(char symbol){
        return symbol == '&' || symbol == '+';
    }

    /**
     * Not used for CommandConditions. The condition is turned into a stack instead.
     * @param argString List string from the initial command.
     * @return Empty String array
     */
    @Override
    protected String[] splitValues(String argString) {
        return new String[0];
    }

    /**
     * Not used for CommandConditions as whitespace is ignored instead.
     */
    @Override
    protected String removeWhiteSpace(String valueString) {
        return valueString;
    }

    /**
     * Testing for CommandCondition.
     */
    public static void test(){
        try {
            CommandCondition test1 = new CommandCondition("test");
            String replace1 = test1.replaceOperators("((A==x) aNd (B>=c)) or (C != d)");
            assert replace1.equals("((A=x)&(B@c))+(C!d)");
        }
        catch (DBException dbe){
            System.out.println("Error in testing CommandCondition.");
        }
        System.out.println("CommandCondition passed.");
    }
}
