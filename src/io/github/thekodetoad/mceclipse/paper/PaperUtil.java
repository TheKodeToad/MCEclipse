package io.github.thekodetoad.mceclipse.paper;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PaperUtil {

	public final Repository MAVEN_REPO;
	
	static {
		MAVEN_REPO = new Repository();
		MAVEN_REPO.setId("papermc");
		MAVEN_REPO.setUrl("https://repo.papermc.io/repository/maven-public");
	}
	
	public Dependency mavenDependency(String minecraftVersion) {
		Dependency dependency = new Dependency();
		dependency.setGroupId("io.papermc.paper");
		dependency.setArtifactId("paper-api");
		dependency.setVersion(minecraftVersion + "-R0.1-SNAPSHOT");
		dependency.setScope("provided");
		return dependency;
	}
	
}
