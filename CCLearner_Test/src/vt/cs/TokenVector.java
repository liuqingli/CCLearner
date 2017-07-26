package vt.cs;

public class TokenVector {

	// variables
	String TokenName;
	int TokenCount;
	boolean TokenUniTag;

	// constructor
	TokenVector(String name) {
		this.TokenName = name;
		this.TokenCount = 1;
		this.TokenUniTag = false;
	}

	// print
	public void print() {
		System.out.printf("%15s%15d\n", this.TokenName, this.TokenCount);
	}
}
