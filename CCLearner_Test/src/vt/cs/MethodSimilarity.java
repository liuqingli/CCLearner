package vt.cs;

import com.sun.javafx.fxml.expression.Expression;

import java.util.HashMap;
import java.util.Map;

public class MethodSimilarity {

	public double simTokenReservedWord, simTokenType, simTokenLiteral;
	public double simTokenVariable, simTokenFunctionName, simTokenQualifiedName;
    public double simTokenOperator, simTokenMarker;


	public String str1, str2;
	public TokenList tokenList1 = new TokenList();
    public TokenList tokenList2 = new TokenList();

    // calculate the similarity of both TokenLists
    public double tokenListSim(TokenList tList1, TokenList tList2) {

        if( (tList1.size() == 0 && tList2.size() == 0) )
            return 0.5;
        if( (tList1.getFreqCount() >= tList2.getFreqCount() * 3) || (tList2.getFreqCount() >= tList1.getFreqCount() * 3) )
            return 0;

        int tokenCount1 = 0;
        int tokenCount2 = 0;
        double tokenListDis = 0;

        int pos1 = 0;
        int pos2 = 0;


        while(pos1 != tList1.size() && pos2 != tList2.size()) {
            if(tList1.getTokenVector(pos1).TokenName.compareTo(tList2.getTokenVector(pos2).TokenName) == 0) {
                tokenCount1 += tList1.getTokenVector(pos1).TokenCount;
                tokenCount2 += tList2.getTokenVector(pos2).TokenCount;
                tokenListDis += Math.abs(tList1.getTokenVector(pos1).TokenCount - tList2.getTokenVector(pos2).TokenCount);
                pos1++;
                pos2++;
            }
            else if(tList1.getTokenVector(pos1).TokenName.compareTo(tList2.getTokenVector(pos2).TokenName) > 0) {
                tokenCount2 += tList2.getTokenVector(pos2).TokenCount;
                tokenListDis += tList2.getTokenVector(pos2).TokenCount;
                pos2++;
            }
            else {
                tokenCount1 += tList1.getTokenVector(pos1).TokenCount;
                tokenListDis += tList1.getTokenVector(pos1).TokenCount;
                pos1++;
            }
        }

        if(pos1 == tList1.size()) {
            while(pos2 != tList2.size()) {
                tokenCount2 += tList2.getTokenVector(pos2).TokenCount;
                tokenListDis += tList2.getTokenVector(pos2).TokenCount;
                pos2++;
            }
        }

        if(pos2 == tList2.size()) {
            while(pos1 != tList1.size()) {
                tokenCount1 += tList1.getTokenVector(pos1).TokenCount;
                tokenListDis += tList1.getTokenVector(pos1).TokenCount;
                pos1++;
            }
        }

        if(tokenCount1 == 0 && tokenCount2 == 0)
            return 0.5;
        else
            return 1 - tokenListDis / (tokenCount1 + tokenCount2);
    }


	public double[] methodVectorSim(MethodVector mVector1, MethodVector mVector2, String without_feature) {

        // calculate token_ReservedWord's similarity
        if (!without_feature.equals("reservedword")) {
            tokenList1 = mVector1.methodReservedWordTokenList;
            tokenList2 = mVector2.methodReservedWordTokenList;
            simTokenReservedWord = tokenListSim(tokenList1, tokenList2);
        }

        // calculate token_Type's similarity
        if (!without_feature.equals("type")) {
            tokenList1 = mVector1.methodTypeTokenList;
            tokenList2 = mVector2.methodTypeTokenList;
            simTokenType = tokenListSim(tokenList1, tokenList2);
        }

        // calculate token_Literal's similarity
        if (!without_feature.equals("literal")) {
            tokenList1 = mVector1.methodLiteralTokenList;
            tokenList2 = mVector2.methodLiteralTokenList;
            simTokenLiteral = tokenListSim(tokenList1, tokenList2);
        }

        // calculate token_Variable's similarity
        if (!without_feature.equals("variable")) {
            tokenList1 = mVector1.methodVariableTokenList;
            tokenList2 = mVector2.methodVariableTokenList;
            simTokenVariable = tokenListSim(tokenList1, tokenList2);
        }

        // calculate token_FunctionName's similarity
        if (!without_feature.equals("functionname")) {
            tokenList1 = mVector1.methodFunctionNameTokenList;
            tokenList2 = mVector2.methodFunctionNameTokenList;
            simTokenFunctionName = tokenListSim(tokenList1, tokenList2);
        }

        // calculate token_QualifiedName's similarity
        if (!without_feature.equals("qualifiedname")) {
            tokenList1 = mVector1.methodQualifiedNameTokenList;
            tokenList2 = mVector2.methodQualifiedNameTokenList;
            simTokenQualifiedName = tokenListSim(tokenList1, tokenList2);
        }

        // calculate token_Operator's similarity
        if (!without_feature.equals("operator")) {
            tokenList1 = mVector1.methodOperatorTokenList;
            tokenList2 = mVector2.methodOperatorTokenList;
            simTokenOperator = tokenListSim(tokenList1, tokenList2);
        }

        // calculate token_Marker's similarity
        if (!without_feature.equals("marker")) {
            tokenList1 = mVector1.methodMarkerTokenList;
            tokenList2 = mVector2.methodMarkerTokenList;
            simTokenMarker = tokenListSim(tokenList1, tokenList2);
        }

        // return the similarity vector between two methods
        if (without_feature.equals("reservedword"))
            return (new double[]{simTokenType, simTokenLiteral, simTokenVariable, simTokenFunctionName, simTokenQualifiedName, simTokenOperator, simTokenMarker});
        if (without_feature.equals("type"))
            return (new double[]{simTokenReservedWord, simTokenLiteral, simTokenVariable, simTokenFunctionName, simTokenQualifiedName, simTokenOperator, simTokenMarker});
        if (without_feature.equals("literal"))
            return (new double[]{simTokenReservedWord, simTokenType, simTokenVariable, simTokenFunctionName, simTokenQualifiedName, simTokenOperator, simTokenMarker});
        if (without_feature.equals("variable"))
            return (new double[]{simTokenReservedWord, simTokenType, simTokenLiteral, simTokenFunctionName, simTokenQualifiedName, simTokenOperator, simTokenMarker});
        if (without_feature.equals("functionname"))
            return (new double[]{simTokenReservedWord, simTokenType, simTokenLiteral, simTokenVariable, simTokenQualifiedName, simTokenOperator, simTokenMarker});
        if (without_feature.equals("qualifiedname"))
            return (new double[]{simTokenReservedWord, simTokenType, simTokenLiteral, simTokenVariable, simTokenFunctionName, simTokenOperator, simTokenMarker});
        if (without_feature.equals("operator"))
            return (new double[]{simTokenReservedWord, simTokenType, simTokenLiteral, simTokenVariable, simTokenFunctionName, simTokenQualifiedName, simTokenMarker});
        if (without_feature.equals("marker"))
            return (new double[]{simTokenReservedWord, simTokenType, simTokenLiteral, simTokenVariable, simTokenFunctionName, simTokenQualifiedName, simTokenOperator});

        return null;
    }

    public double[] methodVectorSim(MethodVector mVector1, MethodVector mVector2) {

        // calculate token_ReservedWord's similarity
        tokenList1 = mVector1.methodReservedWordTokenList;
        tokenList2 = mVector2.methodReservedWordTokenList;
        simTokenReservedWord = tokenListSim(tokenList1, tokenList2);

        // calculate token_Type's similarity
        tokenList1 = mVector1.methodTypeTokenList;
        tokenList2 = mVector2.methodTypeTokenList;
        simTokenType = tokenListSim(tokenList1, tokenList2);

        // calculate token_Literal's similarity
        tokenList1 = mVector1.methodLiteralTokenList;
        tokenList2 = mVector2.methodLiteralTokenList;
        simTokenLiteral = tokenListSim(tokenList1, tokenList2);

        // calculate token_Variable's similarity
        tokenList1 = mVector1.methodVariableTokenList;
        tokenList2 = mVector2.methodVariableTokenList;
        simTokenVariable = tokenListSim(tokenList1, tokenList2);

        // calculate token_FunctionName's similarity
        tokenList1 = mVector1.methodFunctionNameTokenList;
        tokenList2 = mVector2.methodFunctionNameTokenList;
        simTokenFunctionName = tokenListSim(tokenList1, tokenList2);

        // calculate token_QualifiedName's similarity
        tokenList1 = mVector1.methodQualifiedNameTokenList;
        tokenList2 = mVector2.methodQualifiedNameTokenList;
        simTokenQualifiedName = tokenListSim(tokenList1, tokenList2);

        // calculate token_Operator's similarity
        tokenList1 = mVector1.methodOperatorTokenList;
        tokenList2 = mVector2.methodOperatorTokenList;
        simTokenOperator = tokenListSim(tokenList1, tokenList2);

        // calculate token_Marker's similarity
        tokenList1 = mVector1.methodMarkerTokenList;
        tokenList2 = mVector2.methodMarkerTokenList;
        simTokenMarker = tokenListSim(tokenList1, tokenList2);


        // return the similarity vector between two methods
        return (new double[] {
            simTokenReservedWord,
            simTokenType,
            simTokenLiteral,
            simTokenVariable,
            simTokenFunctionName,
            simTokenQualifiedName,
            simTokenOperator,
            simTokenMarker
        });
    }
}
