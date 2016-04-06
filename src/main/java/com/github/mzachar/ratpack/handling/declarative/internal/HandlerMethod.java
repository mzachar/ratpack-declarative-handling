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
import static ratpack.util.Exceptions.uncheck;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mzachar.ratpack.handling.declarative.annotation.Order;
import com.github.mzachar.ratpack.handling.declarative.annotation.Path;
import com.github.mzachar.ratpack.handling.declarative.annotation.Prefix;

import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.handling.Handlers;
import ratpack.registry.Registry;

/**
 * @author mzachar
 */
class HandlerMethod implements Comparable<HandlerMethod> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HandlerMethod.class);

	private static final String EMPTY_STRING = "";

	private final ArgumentResolvers paramProviders;

	private final Object owner;
	private final Method method;
	private final String pathBindingSpec;
	private final boolean isPrefixPath;
	private final int order;

	public HandlerMethod(ArgumentResolvers resolvers, Object owner, Method method) {
		this.paramProviders = resolvers;
		this.owner = owner;
		this.method = method;

		this.pathBindingSpec = getPath(method);
		this.isPrefixPath = isPrefixPath(method);
		this.order = getFromAnnotation(Order.class, method, Order::value, Integer.MAX_VALUE);
	}

	public Handler createHandler() {
		if (pathBindingSpec.isEmpty()) {
			return this::doHandle;
		}

		if (isPrefixPath) {
			return Handlers.prefix(pathBindingSpec, this::doHandle);
		}

		return Handlers.path(pathBindingSpec, this::doHandle);
	}

	private void doHandle(Context ctx) throws Exception {
		long start = System.nanoTime();
		try {
			Object[] args = buildArguments(ctx);

			Object result = method.invoke(owner, args);
			if (result != null) {
				if (result instanceof Handler) {
					ctx.insert((Handler) result);

				} else if (result instanceof Registry) {
					ctx.next((Registry) result);

				} else if (result instanceof Action) { // FIXME use type tokens
					ctx.insert(Handlers.chain(ctx, (Action<? super Chain>) result));

				} else {
					ctx.render(result);
				}
			}
		} catch (IllegalAccessException e) {
			throw uncheck(e);

		} catch (InvocationTargetException e) {
			throw uncheck(e.getTargetException());

		} finally {
			long stop = System.nanoTime();
			LOGGER.debug("[{}] took {}", pathBindingSpec, Duration.ofNanos(stop - start));
		}
	}

	@Override
	public int compareTo(HandlerMethod o) {
		return Integer.compare(this.order, o.order);
	}

	@Override
	public String toString() {
		return pathBindingSpec;
	}

	private Object[] buildArguments(Context ctx) {
		return Arrays.stream(method.getParameters())
				.map(p -> paramProviders.resolve(ctx, p))
				.toArray();
	}

	private String getPath(AnnotatedElement element) {
		String fromPrefix = getFromAnnotation(Prefix.class, element, Prefix::value, EMPTY_STRING);
		String fromPath = getFromAnnotation(Path.class, element, Path::value, EMPTY_STRING);
		return !fromPrefix.isEmpty() ? fromPrefix : fromPath;
	}

	private boolean isPrefixPath(AnnotatedElement element) {
		return getFromAnnotation(Prefix.class, element, path -> true, false);
	}

}
