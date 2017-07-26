package vt.cs;

import org.antlr.v4.runtime.*;
import org.apache.commons.lang3.ArrayUtils;

public class MethodTokenParserTool extends Java8BaseListener {

    public TokenList methodReservedWordTokenList = null;
    public TokenList methodOperatorTokenList = null;
    public TokenList methodMarkerTokenList = null;


    public TokenList getReservedWordTokenList(String methodBody) {

        int index;
        methodReservedWordTokenList = new TokenList();

        ANTLRInputStream stream = new ANTLRInputStream(methodBody);
        Java8Lexer lexer = new Java8Lexer(stream);

        int[] reservedType = new int[]{lexer.BOOLEAN, lexer.BYTE, lexer.CHAR, lexer.DOUBLE, lexer.FLOAT, lexer.INT, lexer.LONG, lexer.SHORT};

        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            if (token.getType() <= lexer.WHILE) {
                if (!ArrayUtils.contains(reservedType, token.getType())) {
                    index = methodReservedWordTokenList.getIndexByName(token.getText());
                    if (index != -1)
                        methodReservedWordTokenList.setValueByIndex(index);
                    else {
                        TokenVector tokenVector = new TokenVector(token.getText());
                        methodReservedWordTokenList.addTokenVector(tokenVector);
                    }
                }
            }
        }
        return methodReservedWordTokenList;
    }

    public TokenList getOperatorTokenList(String methodBody) {

        int index;
        methodOperatorTokenList = new TokenList();

        ANTLRInputStream stream = new ANTLRInputStream(methodBody);
        Java8Lexer lexer = new Java8Lexer(stream);

        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            if (token.getType() >= lexer.ASSIGN && token.getType() <= lexer.URSHIFT_ASSIGN) {
                index = methodOperatorTokenList.getIndexByName(token.getText());
                if (index != -1)
                    methodOperatorTokenList.setValueByIndex(index);
                else {
                    TokenVector tokenVector = new TokenVector(token.getText());
                    methodOperatorTokenList.addTokenVector(tokenVector);
                }
            }
        }
        return methodOperatorTokenList;
    }

    public TokenList getMarkerTokenList(String methodBody) {

        int index;
        methodMarkerTokenList = new TokenList();

        ANTLRInputStream stream = new ANTLRInputStream(methodBody);
        Java8Lexer lexer = new Java8Lexer(stream);

        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            if (token.getType() >= lexer.LPAREN && token.getType() <= lexer.DOT) {
                index = methodMarkerTokenList.getIndexByName(token.getText());
                if (index != -1)
                    methodMarkerTokenList.setValueByIndex(index);
                else {
                    TokenVector tokenVector = new TokenVector(token.getText());
                    methodMarkerTokenList.addTokenVector(tokenVector);
                }
            }
        }
        return methodMarkerTokenList;
    }
}
