package io.github.thekodetoad.mceclipse;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import lombok.Getter;

public class MCEclipsePlugin extends AbstractUIPlugin {

	private static MCEclipsePlugin active;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		active = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		active = null;
		super.stop(context);
	}
	
	public static MCEclipsePlugin active() {
		return active;
	}
	
	public static ILog log() {
		return active.getLog();
	}
	
}
