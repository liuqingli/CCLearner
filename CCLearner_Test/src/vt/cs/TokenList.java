package vt.cs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TokenList{

	// ArrayList of TokenVector
	public ArrayList<TokenVector> tokenList = new ArrayList<TokenVector>();

	// get tokenVector
	public TokenVector getTokenVector(int index) {
		return tokenList.get(index);
	}

    // add token vector
	public void addTokenVector(TokenVector tv) {
		tokenList.add(tv);
	}

    // add token list
    public void addTokenList(TokenList tl) {
        int i, j;
        for(i = 0; i < tl.size(); i++) {
            for(j = 0; j < tokenList.size(); j++) {
                if(tokenList.get(j).TokenName.equals(tl.getTokenVector(i).TokenName)) {
                    tokenList.get(j).TokenCount += tl.getTokenVector(i).TokenCount;
                    break;
                }
            }
            if(j == tokenList.size()) {
                tokenList.add(tl.getTokenVector(i));
            }
        }
    }

	// size
	public int size() {
		return tokenList.size();
	}

	// clear
	public void clear() {
		tokenList.clear();
	}

    // reset tag
    public void resetTag() {
        for(int i = 0; i < tokenList.size(); i++)
            tokenList.get(i).TokenUniTag = false;
    }

    // get frequency count
    public int getFreqCount() {
        int tmp = 0;
        for(int i = 0; i < tokenList.size(); i++)
            tmp = tokenList.get(i).TokenCount;
        return tmp;
    }

	// look up with TokenName
	public int getIndexByName(String name) {
		for(int i = 0; i < tokenList.size(); i++) {
			if(tokenList.get(i).TokenName.equals(name))
				return i;
		}
		return -1;
	}

    // remove TokenVector
    public void removeVectorByName(String name) {
        if(getIndexByName(name) != -1)
            tokenList.remove(getIndexByName(name));
    }

	// increase TokenCount
	public void setValueByIndex(int index) {
		tokenList.get(index).TokenCount++;
	}

	// sort ArrayList by TokenName
	public void sortListByName() {
		Collections.sort(tokenList, new Comparator<TokenVector>() {
	        public int compare(TokenVector arg0, TokenVector arg1) {
	            return arg0.TokenName.compareTo(arg1.TokenName);
	        }
	    });
	}

	// sort ArrayList by TokenCount
	public void sortListByCount() {
		Collections.sort(tokenList, new Comparator<TokenVector>() {
	        public int compare(TokenVector arg0, TokenVector arg1) {
	            return arg1.TokenCount - arg0.TokenCount;
	        }
	    });
	}

	// print
	public void print() {
		for(int i = 0; i < tokenList.size(); i++) {
			tokenList.get(i).print();
		}
	}
}
