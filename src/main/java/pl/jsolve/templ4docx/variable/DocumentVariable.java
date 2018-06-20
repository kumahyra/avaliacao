package pl.jsolve.templ4docx.variable;

import pl.jsolve.templ4docx.core.Docx;

public class DocumentVariable implements Variable {

	private final String key;
	private final Docx document;
	
	public DocumentVariable(String key, Docx document) {
		this.key = key;
		this.document = document;
	}

	public String getKey() {
		return key;
	}

	public Docx getDocument() {
		return document;
	}
	
}
