package vt.cs;

public class MethodVector {

	// variables
    String fileName;
	int startLineNumber;
	int endLineNumber;
	TokenList methodReservedWordTokenList;
    TokenList methodTypeTokenList;
    TokenList methodLiteralTokenList;
    TokenList methodVariableTokenList;
    TokenList methodFunctionNameTokenList;
    TokenList methodQualifiedNameTokenList;
    TokenList methodOperatorTokenList;
    TokenList methodMarkerTokenList;


    // constructor
	MethodVector(String fname, int start, int end,
                 TokenList r_List, TokenList t_List, TokenList l_List, TokenList v_List,
                 TokenList f_List, TokenList q_List, TokenList o_List, TokenList m_List) {
        this.fileName = fname;
		this.startLineNumber = start;
		this.endLineNumber = end;
		this.methodReservedWordTokenList = r_List;
        this.methodTypeTokenList = t_List;
        this.methodLiteralTokenList = l_List;
        this.methodVariableTokenList = v_List;
        this.methodFunctionNameTokenList = f_List;
        this.methodQualifiedNameTokenList = q_List;
        this.methodOperatorTokenList = o_List;
        this.methodMarkerTokenList = m_List;
	}

	// print
	public void print() {
        System.out.println("File name: " + fileName);
		System.out.println("Start #: " + startLineNumber);
		System.out.println("End #: " + endLineNumber);

		System.out.println("ReservedWord Token:");
        methodReservedWordTokenList.print();
        System.out.println("--------------------------------------");

        System.out.println("Type Token:");
        methodTypeTokenList.print();
        System.out.println("--------------------------------------");

        System.out.println("Literal Token:");
        methodLiteralTokenList.print();
        System.out.println("--------------------------------------");

        System.out.println("Variable Token:");
        methodVariableTokenList.print();
        System.out.println("--------------------------------------");

        System.out.println("FunctionName Token:");
        methodFunctionNameTokenList.print();
        System.out.println("--------------------------------------");

        System.out.println("QualifiednName Token:");
        methodQualifiedNameTokenList.print();
        System.out.println("--------------------------------------");

        System.out.println("Operator Token:");
        methodOperatorTokenList.print();
        System.out.println("--------------------------------------");

        System.out.println("Marker Token:");
        methodMarkerTokenList.print();
        System.out.println("--------------------------------------");

        System.out.println();
	}
}
