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

package com.github.mzachar.ratpack.handling.declarative.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

/**
 * @author mzachar
 */
public final class Util {

	public static <T extends Annotation, R> R getFromAnnotation(Class<T> type, AnnotatedElement element, Function<T, R> extractor, R defaultValue) {
		T annotation = element.getDeclaredAnnotation(type);
		if (annotation != null) {
			return extractor.apply(annotation);
		}
		return defaultValue;
	}
}
