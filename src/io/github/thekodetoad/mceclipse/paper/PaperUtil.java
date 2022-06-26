/**
 * Copyright (C) 2022 TheKodeToad
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
