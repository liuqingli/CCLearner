package vt.cs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.*;

public class ASTParserTool {

    public MethodList methodVectorList = new MethodList();

    public void setFileName(String fname) {
        this.fileName = fname;
    }

    private Stack<MethodVector> methodVectorStack;

    public String fileName;
    public int startLineNumber, endLineNumber;

    public TokenList methodReservedWordTokenList = null;
    public TokenList methodTypeTokenList = null;
    public TokenList methodLiteralTokenList = null;
    public TokenList methodVariableTokenList = null;
    public TokenList methodFunctionNameTokenList = null;
    public TokenList methodQualifiedNameTokenList = null;
    public TokenList methodOperatorTokenList = null;
    public TokenList methodMarkerTokenList = null;

    public boolean enterMethod = false;


    public MethodList parseMethod(String javaFilePath) {
		byte[] input = null;
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(javaFilePath));
			input = new byte[bufferedInputStream.available()];
			bufferedInputStream.read(input);
			bufferedInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ASTParser astParser = ASTParser.newParser(AST.JLS3);

		astParser.setSource(new String(input).toCharArray());
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setResolveBindings(true);
	    final CompilationUnit result = (CompilationUnit) astParser.createAST(null);

        methodVectorStack = new Stack<>();

	    result.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration method) {

                if(method.getBody() != null) {

                    enterMethod = true;

                    // get method start line #
                    startLineNumber = result.getLineNumber(method.getBody().getStartPosition());

                    // get method end line #
                    endLineNumber = result.getLineNumber(method.getBody().getStartPosition() + method.getBody().getLength());

                    // get method body
                    String methodBody = method.getBody().toString();

                    methodReservedWordTokenList = new TokenList();
                    methodOperatorTokenList = new TokenList();
                    methodMarkerTokenList = new TokenList();

                    MethodTokenParserTool methodTokenParserTool = new MethodTokenParserTool();

                    methodReservedWordTokenList = methodTokenParserTool.getReservedWordTokenList(methodBody);
                    methodOperatorTokenList = methodTokenParserTool.getOperatorTokenList(methodBody);
                    methodMarkerTokenList = methodTokenParserTool.getMarkerTokenList(methodBody);

                    methodTypeTokenList = new TokenList();
                    methodLiteralTokenList = new TokenList();
                    methodVariableTokenList = new TokenList();
                    methodFunctionNameTokenList = new TokenList();
                    methodQualifiedNameTokenList = new TokenList();

                    MethodVector methodVector = new MethodVector(fileName, startLineNumber, endLineNumber,
                        methodReservedWordTokenList, methodTypeTokenList, methodLiteralTokenList, methodVariableTokenList,
                        methodFunctionNameTokenList, methodQualifiedNameTokenList, methodOperatorTokenList, methodMarkerTokenList);

                    methodVectorStack.push(methodVector);

                    method.getBody().accept(this);
                }
                return false;
            }

            //////////////////////////////////////////////////////
            // Add Literals into LiteralTokenList
            //////////////////////////////////////////////////////
            @Override
            public boolean visit(BooleanLiteral node) {
                if(enterMethod)
                    add_TokenList(methodLiteralTokenList, node.toString());
                return false;
            }
            @Override
            public boolean visit(CharacterLiteral node) {
                if(enterMethod)
                    add_TokenList(methodLiteralTokenList, node.toString());
                return false;
            }
            @Override
            public boolean visit(NullLiteral node) {
                if(enterMethod)
                    add_TokenList(methodLiteralTokenList, node.toString());
                return false;
            }
            @Override
            public boolean visit(NumberLiteral node) {
                if(enterMethod)
                    add_TokenList(methodLiteralTokenList, node.toString());
                return false;
            }
            @Override
            public boolean visit(StringLiteral node) {
                if(enterMethod)
                    add_TokenList(methodLiteralTokenList, node.toString());
                return false;
            }
            @Override
            public boolean visit(TypeLiteral node) {
                if(enterMethod) {
                    add_TokenList(methodLiteralTokenList, node.toString());
                }
                return false;
            }

            //////////////////////////////////////////////////////
            // Add Variables into VariableTokenList
            //////////////////////////////////////////////////////
            @Override
            public boolean visit(SimpleName node) {
                if(enterMethod)
                    add_TokenList(methodVariableTokenList, node.toString());
                return false;
            }

            //////////////////////////////////////////////////////
            // Add FunctionName into FunctionNameTokenList
            //////////////////////////////////////////////////////
            @Override
            public boolean visit(MethodInvocation node) {
                if(enterMethod) {
                    add_TokenList(methodFunctionNameTokenList, node.getName().toString());
                    if (node.getExpression() != null) {
                        node.getExpression().accept(this);
                    }
                    if (node.arguments().size() > 0) {
                        List<ASTNode> list = node.arguments();
                        for (ASTNode nodeInList : list) {
                            nodeInList.accept(this);
                        }
                    }
                }
                return false;
            }

            //////////////////////////////////////////////////////
            // Add QualifiedName into QualifiedNameTokenList
            //////////////////////////////////////////////////////
            @Override
            public boolean visit(QualifiedName node) {
                if(enterMethod)
                    add_TokenList(methodQualifiedNameTokenList, node.toString());
                return false;
            }

            //////////////////////////////////////////////////////
            // Add Type into TypeTokenList
            //////////////////////////////////////////////////////
            @Override
            public boolean visit(SimpleType node) {
                if(enterMethod)
                    add_TokenList(methodTypeTokenList, node.toString());
                return false;
            }
            @Override
            public boolean visit(QualifiedType node) {
                if(enterMethod) {
                    add_TokenList(methodTypeTokenList, node.getQualifier().toString());
                }
                return false;
            }
            @Override
            public boolean visit(PrimitiveType node) {
                if(enterMethod)
                    add_TokenList(methodTypeTokenList, node.toString());
                return false;
            }

            @Override
            public void endVisit(MethodDeclaration node) {

                MethodVector methodVector;
                if(!methodVectorStack.empty()) {
                    methodVector = methodVectorStack.pop();
                    if(methodVector.endLineNumber - methodVector.startLineNumber  + 1 >= 6) {
                        methodVector.methodReservedWordTokenList.sortListByName();
                        methodVector.methodTypeTokenList.sortListByName();
                        methodVector.methodLiteralTokenList.sortListByName();
                        methodVector.methodVariableTokenList.sortListByName();
                        methodVector.methodFunctionNameTokenList.sortListByName();
                        methodVector.methodQualifiedNameTokenList.sortListByName();
                        methodVector.methodOperatorTokenList.sortListByName();
                        methodVector.methodMarkerTokenList.sortListByName();
                        methodVectorList.addMethodVector(methodVector);
                    }
                }

                if(!methodVectorStack.empty()) {
                    methodVector = methodVectorStack.peek();
                    methodVector.methodTypeTokenList.addTokenList(methodTypeTokenList);
                    methodTypeTokenList = methodVector.methodTypeTokenList;
                    methodVector.methodLiteralTokenList.addTokenList(methodLiteralTokenList);
                    methodLiteralTokenList = methodVector.methodLiteralTokenList;
                    methodVector.methodVariableTokenList.addTokenList(methodVariableTokenList);
                    methodVariableTokenList = methodVector.methodVariableTokenList;
                    methodVector.methodFunctionNameTokenList.addTokenList(methodFunctionNameTokenList);
                    methodFunctionNameTokenList = methodVector.methodFunctionNameTokenList;
                    methodVector.methodQualifiedNameTokenList.addTokenList(methodQualifiedNameTokenList);
                    methodQualifiedNameTokenList = methodVector.methodQualifiedNameTokenList;
                }
                else {
                    enterMethod = false;
                }
            }
        });

	    return methodVectorList;
	}


    public void add_TokenList(TokenList tokenList, String str){
        int index;
        index = tokenList.getIndexByName(str);
        if (index != -1)
            tokenList.setValueByIndex(index);
        else {
            TokenVector tokenVector = new TokenVector(str);
            tokenList.addTokenVector(tokenVector);
        }
    }
}
