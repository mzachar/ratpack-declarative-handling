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

import static com.github.mzachar.ratpack.handling.declarative.internal.Util.getFromAnnotation;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import com.github.mzachar.ratpack.handling.declarative.annotation.Path;
import com.github.mzachar.ratpack.handling.declarative.annotation.Prefix;

import ratpack.handling.Handler;
import ratpack.handling.Handlers;

/**
 * @author mzachar
 */
public class HandlerClass {

	private final ArgumentResolvers resolvers;

	private final Object instance;
	private final Class<?> type;

	private final List<HandlerMethod> methods;

	public HandlerClass(Object instance, ArgumentResolvers resolvers) {
		this.instance = instance;
		this.type = instance.getClass();
		this.resolvers = resolvers;
		this.methods = initMethods();
	}

	private List<HandlerMethod> initMethods() {
		return Arrays.stream(type.getDeclaredMethods())
				.filter(m -> m.isAnnotationPresent(Path.class) || m.isAnnotationPresent(Prefix.class))
				.map(m -> new HandlerMethod(resolvers, instance, m))
				.sorted()
				.collect(toList());
	}

	public Handler createHandler() {
		Handler methodsChainHandler = Handlers.chain(methods.stream().map(HandlerMethod::createHandler).collect(toList()));

		String prefix = getFromAnnotation(Prefix.class, type, Prefix::value, null);
		return prefix != null ? Handlers.prefix(prefix, methodsChainHandler) : methodsChainHandler;
	}

	public static boolean isHandlerClass(Class<?> type) {
		return type.isAnnotationPresent(com.github.mzachar.ratpack.handling.declarative.annotation.Handler.class);
	}

	public static boolean isHandlerClass(Object instance) {
		return isHandlerClass(instance.getClass());
	}
}
