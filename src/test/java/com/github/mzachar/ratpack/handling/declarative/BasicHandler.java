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

import com.github.mzachar.ratpack.handling.declarative.annotation.Handler;
import com.github.mzachar.ratpack.handling.declarative.annotation.Path;
import com.github.mzachar.ratpack.handling.declarative.annotation.PathToken;
import com.github.mzachar.ratpack.handling.declarative.annotation.Prefix;
import com.github.mzachar.ratpack.handling.declarative.annotation.QueryParam;

import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;
import ratpack.http.Request;
import ratpack.http.Response;
import ratpack.registry.Registry;

@Handler
@Prefix("basic")
public class BasicHandler {

	@Path("context")
	public void context(Context ctx) {
		ctx.render("context");
	}

	@Path("requestResponse")
	public void requestResponse(Request request, Response response) {
		response.send(request.getPath());
	}

	@Path("pathToken/:id")
	public void pathToken(Context ctx, @PathToken("id") String id) {
		ctx.render("pathToken/" + id);
	}

	@Path("queryParam")
	public void queryParam(Context ctx, @QueryParam("param") String param) {
		ctx.render("queryParam?param=" + param);
	}

	@Path("direct")
	public String direct() {
		return "direct";
	}

	@Prefix("person/:id")
	public Action<? super Chain> person(@PathToken("id") String id) throws Exception {
		return chain -> chain
				.all(ctx -> {
					ChainHandler.Person person = new ChainHandler.PersonImpl(id, "example-status", "example-age");
					ctx.next(Registry.single(ChainHandler.Person.class, person));
				})
				.get("status", ctx -> {
					ChainHandler.Person person = ctx.get(ChainHandler.Person.class);
					ctx.render("person " + person.getId() + " status: " + person.getStatus());
				})
				.get("age", ctx -> {
					ChainHandler.Person person = ctx.get(ChainHandler.Person.class);
					ctx.render("person " + person.getId() + " age: " + person.getAge());
				});
	}

	public interface Person {
		String getId();

		String getStatus();

		String getAge();
	}

	public static class PersonImpl implements ChainHandler.Person {
		private final String id;
		private final String status;
		private final String age;

		public PersonImpl(String id, String status, String age) {
			this.id = id;
			this.status = status;
			this.age = age;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getStatus() {
			return status;
		}

		@Override
		public String getAge() {
			return age;
		}
	}

}

