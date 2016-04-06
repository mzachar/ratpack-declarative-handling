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

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.function.BiFunction;
import java.util.function.Function;

import ratpack.handling.Context;

/**
 * Pluggable provider for parameter binding in handler methods
 *
 * @author mzachar
 */
@FunctionalInterface
public interface ArgumentResolver {

	static <T> ArgumentResolver forType(Class<T> type, Function<Context, T> extractor) {
		return (ctx, argument) -> type.isAssignableFrom(argument.getType()) ? extractor.apply(ctx) : null;
	}

	static <T extends Annotation> ArgumentResolver forAnnotation(Class<T> type, BiFunction<Context, T, Object> extractor) {
		return (ctx, argument) -> {
			T annotation = argument.getDeclaredAnnotation(type);
			return (annotation != null) ? extractor.apply(ctx, annotation) : null;
		};
	}

	Object getValueFor(Context ctx, Parameter argument);

}
