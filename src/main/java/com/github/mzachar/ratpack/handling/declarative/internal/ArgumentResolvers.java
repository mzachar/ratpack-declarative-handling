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

import static com.github.mzachar.ratpack.handling.declarative.ArgumentResolver.forAnnotation;
import static com.github.mzachar.ratpack.handling.declarative.ArgumentResolver.forType;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.github.mzachar.ratpack.handling.declarative.ArgumentResolver;
import com.github.mzachar.ratpack.handling.declarative.annotation.PathToken;
import com.github.mzachar.ratpack.handling.declarative.annotation.QueryParam;

import ratpack.handling.Context;
import ratpack.http.Request;
import ratpack.http.Response;
import ratpack.path.PathTokens;
import ratpack.registry.Registry;

/**
 * @author mzachar
 */
public class ArgumentResolvers {

	private final List<ArgumentResolver> resolvers;

	public ArgumentResolvers(Registry registry) {
		List<ArgumentResolver> list = new ArrayList<>();

		// build in resolvers
		list.add(forType(Context.class, Function.identity()));
		list.add(forType(Request.class, ctx -> ctx.getRequest()));
		list.add(forType(Response.class, ctx -> ctx.getResponse()));
		list.add(forType(PathTokens.class, ctx -> ctx.getPathTokens()));

		list.add(forAnnotation(PathToken.class, (ctx, annotation) -> ctx.getPathTokens().get(annotation.value())));
		list.add(forAnnotation(QueryParam.class, (ctx, annotation) -> ctx.getRequest().getQueryParams().get(annotation.value())));

		// resolve remaining arguments directly from registry
		list.add((ctx, argument) -> ctx.get(argument.getType()));

		// resolvers discovered from registry
		registry.getAll(ArgumentResolver.class).forEach(list::add);

		this.resolvers = list;
	}

	public Object resolve(Context ctx, Parameter parameter) {
		return resolvers.stream()
				.map(p -> p.getValueFor(ctx, parameter))
				.filter(arg -> arg != null)
				.findFirst().orElse(null);
	}

}
