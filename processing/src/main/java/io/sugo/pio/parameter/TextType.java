package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.Serializable;


/**
 * The possible text types for the {@link ParameterTypeText} type.
 *
 * @author Ingo Mierswa
 */
public enum TextType implements Serializable {

	PLAIN(SyntaxConstants.SYNTAX_STYLE_NONE, false, false), XML(SyntaxConstants.SYNTAX_STYLE_XML, true, false), HTML(
			SyntaxConstants.SYNTAX_STYLE_HTML, true, false), SQL(SyntaxConstants.SYNTAX_STYLE_SQL, false, true), JAVA(
					SyntaxConstants.SYNTAX_STYLE_JAVA, true, true), GROOVY(SyntaxConstants.SYNTAX_STYLE_GROOVY, true, true), PYTHON(
			SyntaxConstants.SYNTAX_STYLE_PYTHON, true, true), R("text/r", true, true);

	@JsonProperty
	private String syntaxIdentifier;
	@JsonProperty
	private boolean isAutoIntending;
	@JsonProperty
	private boolean isBracketMatching;

	TextType(String syntaxIdentifier, boolean isAutoIntending, boolean isBracketMatching) {
		this.syntaxIdentifier = syntaxIdentifier;
		this.isAutoIntending = isAutoIntending;
		this.isBracketMatching = isBracketMatching;
	}

	public String getSyntaxIdentifier() {
		return syntaxIdentifier;
	}

	public boolean isAutoIntending() {
		return isAutoIntending;
	}

	public boolean isBracketMatching() {
		return isBracketMatching;
	}
}
