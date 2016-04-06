/*
 * Copyright 2016 Matej Zachar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mzachar.ratpack.handling.declarative;

import com.github.mzachar.ratpack.handling.declarative.internal.DefaultDeclarativeHandler;

import ratpack.handling.Handler;
import ratpack.registry.Registry;

/**
 * Allows you to expose handlers which where declared using @{@link com.github.mzachar.ratpack.handling.declarative.annotation.Handler} annotation
 *
 * @author mzachar
 */
public interface DeclarativeHandler extends Handler {

	static DeclarativeHandler create(Registry registry) {
		return new DefaultDeclarativeHandler(registry);
	}

}
