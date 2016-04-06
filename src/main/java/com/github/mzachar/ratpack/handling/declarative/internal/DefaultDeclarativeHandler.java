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

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.StreamSupport;

import com.github.mzachar.ratpack.handling.declarative.DeclarativeHandler;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.handling.Handlers;
import ratpack.registry.Registry;

/**
 * @author mzachar
 */
public class DefaultDeclarativeHandler implements DeclarativeHandler {

	private final Registry registry;
	private final Handler chain;
	private final ArgumentResolvers resolvers;

	public DefaultDeclarativeHandler(Registry registry) {
		this.registry = registry;
		this.resolvers = new ArgumentResolvers(registry);
		this.chain = initChain();
	}

	@Override
	public void handle(Context ctx) throws Exception {
		if (chain != null) {
			ctx.insert(chain);
		} else {
			ctx.next();
		}
	}

	private Handler initChain() {
		List<Handler> chainHandlers = StreamSupport.stream(registry.getAll(Object.class).spliterator(), false)
				.filter(HandlerClass::isHandlerClass)
				.map(instance -> new HandlerClass(instance, resolvers))
				.map(HandlerClass::createHandler)
				.collect(toList());

		return Handlers.chain(chainHandlers);
	}

}
