package io.github.thekodetoad.mceclipse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
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

	public static String format(String source, int kind, Map<String, String> options, int indentationLevel,
			String lineSeparator) {
		TextEdit edit = ToolFactory.createCodeFormatter(options).format(kind, source, 0, source.length(), indentationLevel, lineSeparator);
		Document document = new Document(source);
		try {
			if(edit != null) {
				edit.apply(document);
			}
		}
		catch(BadLocationException error) {
			Util.LOG.error("Error formatting", error);
		}
		return document.get();
	}

	public static String applyImports(String source, ImportRewrite imports) throws CoreException {
		Document document = new Document(source);
		try {
			imports.rewriteImports(null).apply(document);
		}
		catch(BadLocationException | MalformedTreeException error) {
			Util.LOG.error("Error applying imports", error);
		}
		return document.get();
	}

	public static String formatAndApplyImports(String source, int kind, Map<String, String> options,
			int indentationLevel, String lineSeparator, ImportRewrite imports) throws CoreException {
		source = format(source, kind, options, indentationLevel, lineSeparator);
		source = applyImports(source, imports);
		return source;
	}

	public static String getLineBreak(IProject project) {
		return Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR,
				System.getProperty("line.separator", "\n"), projectContext(project));
	}

	private static IScopeContext[] projectContext(IProject project) {
		return new IScopeContext[] { new ProjectScope(project), InstanceScope.INSTANCE };
	}

}
