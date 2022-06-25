package io.github.thekodetoad.mceclipse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {

	public static final String MAVEN_BUILDER_ID = "org.eclipse.m2e.core.maven2Builder";
	public static final String MAVEN_NATURE_ID = "org.eclipse.m2e.core.maven2Nature";
	private final MavenXpp3Writer MAVEN_WRITER = new MavenXpp3Writer();
	public final Bundle BUNDLE = FrameworkUtil.getBundle(Util.class);
	public final String ID = BUNDLE.getSymbolicName();
	public final ILog LOG = Platform.getLog(BUNDLE);

	public static byte[] modelToByteArray(Model model) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MAVEN_WRITER.write(out, model);
		return out.toByteArray();
	}

	public static String format(int kind, String source, Map<String, String> options, int indentationLevel, String lineSeparator) {
		TextEdit edit = ToolFactory.createCodeFormatter(options).format(kind, source, 0, source.length(), indentationLevel, lineSeparator);
		Document document = new Document(source);
		try {
			edit.apply(document);
		}
		catch(BadLocationException error) {
			Util.LOG.error("Error formatting", error);
		}
		return document.get();
	}

	public static String getLineBreak(IJavaProject project) {
		return project.getOptions(true).getOrDefault("", System.getProperty(ID));
	}

}
