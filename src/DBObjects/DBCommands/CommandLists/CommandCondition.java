package DBObjects.DBCommands.CommandLists;

import DBException.DBException;

import java.util.Stack;

public class CommandCondition extends CommandList{

    String conditionString;

    public boolean findCondition(String conditions, int startIndex, int endIndex){
        int openCount = 0, closeCount = 0;
        int openIndex = 0, closeIndex = 0;
        if (conditions.charAt(0) != '('){
            return false;
        }
        for (int i = 0; i < conditions.length(); i++){
            if (conditions.charAt(i) == '('){
                if (openCount == 0){
                    openIndex = i;
                }
                openCount ++;
            }
            if (conditions.charAt(i) == ')'){
                closeCount++;
                if (openCount == closeCount){
                    closeIndex = i;
                    closeCount = 0;
                    openCount = 0;
                    if (!findCondition(conditions, openIndex + 1, closeIndex)){
                        return false;
                    }
                }
            }
        }
        if (openCount != closeCount){
            return false;
        }
        return true;
    }

    public void stackifyCondition(){
        Stack<String> parenStack;
        Stack<String> finalStack;
        Stack<String> operatorStack;

        for (int i = 0; i < conditionString.length(); i++){
            if (isOperator(conditionString, i) > 0){

            }
            if (isBoolean(conditionString, i) > 0){

            }
            if (isName(conditionString, i) > 0){

            }
        }
    }

    public int isOperator(String conditionString, int operatorIndex){
        if (conditionString.charAt(operatorIndex) == '>'){
            return 1;
        }
        if (conditionString.charAt(operatorIndex) == '<'){
                return 1;
        }
        String twoDigitOp = conditionString.substring(operatorIndex, operatorIndex + 2);
        if (twoDigitOp.equals("==")){
            return 2;
        }
        if (twoDigitOp.equals("!=")) {
            return 2;
        }
        if (twoDigitOp.equals(">=")){
            return 2;
        }
        if (twoDigitOp.equals("<=")){
            return 2;
        }
        if (conditionString.substring(operatorIndex, operatorIndex + 4).equals("LIKE")){
            return 4;
        }

        return -1;
    }

    public int isBoolean(String conditionString, int operatorIndex){
        if (isAnd()){
            return 3;
        }
        if (isOr()){
            return 2;
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
}
