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
import com.github.mzachar.ratpack.handling.declarative.annotation.Order;
import com.github.mzachar.ratpack.handling.declarative.annotation.Path;
import com.github.mzachar.ratpack.handling.declarative.annotation.PathToken;
import com.github.mzachar.ratpack.handling.declarative.annotation.Prefix;

import ratpack.registry.Registry;

@Handler
@Prefix("chain/person/:id")
public class ChainHandler {

	@Order(1)
	@Path
	public Registry all(@PathToken("id") String id) {
		Person person = new PersonImpl(id, "example-status", "example-age");
		return Registry.single(Person.class, person);
	}

	@Path("status")
	public String status(Person person) {
		return "person " + person.getId() + " status: " + person.getStatus();
	}

	@Path("age")
	public String age(Person person) {
		return "person " + person.getId() + " age: " + person.getAge();
	}

	public interface Person {
		String getId();

		String getStatus();

		String getAge();
	}

	public static class PersonImpl implements Person {
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
