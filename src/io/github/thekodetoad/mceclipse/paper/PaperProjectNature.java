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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import lombok.Getter;
import lombok.Setter;

public class PaperProjectNature implements IProjectNature {

	public static final String ID = PaperProjectNature.class.getPackageName() + ".nature";

	@Getter
	@Setter
	private IProject project;

	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

}
