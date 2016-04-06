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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ratpack.registry.Registry;
import ratpack.test.embed.EmbeddedApp;

public class Example {

	@Test
	public void context() throws Exception {
		declarativeEmbeddedApp().test(httpClient ->
				assertEquals("context", httpClient.getText("basic/context")));
	}

	@Test
	public void requestResponse() throws Exception {
		declarativeEmbeddedApp().test(httpClient ->
				assertEquals("basic/requestResponse", httpClient.getText("basic/requestResponse")));
	}

	@Test
	public void pathToken() throws Exception {
		declarativeEmbeddedApp().test(httpClient ->
				assertEquals("pathToken/10", httpClient.getText("basic/pathToken/10")));
	}

	@Test
	public void queryParam() throws Exception {
		declarativeEmbeddedApp().test(httpClient ->
				assertEquals("queryParam?param=hello", httpClient.getText("basic/queryParam?param=hello")));
	}

	@Test
	public void direct() throws Exception {
		declarativeEmbeddedApp().test(httpClient ->
				assertEquals("direct", httpClient.getText("basic/direct")));
	}

	@Test
	public void basicPersonStatus() throws Exception {
		declarativeEmbeddedApp().test(httpClient ->
				assertEquals("person 10 status: example-status", httpClient.getText("basic/person/10/status")));
	}

	@Test
	public void basicPersonAge() throws Exception {
		declarativeEmbeddedApp().test(httpClient ->
				assertEquals("person 6 age: example-age", httpClient.getText("basic/person/6/age")));
	}

	@Test
	public void chainPersonStatus() throws Exception {
		declarativeEmbeddedApp().test(httpClient ->
				assertEquals("person 20 status: example-status", httpClient.getText("chain/person/20/status")));
	}

	@Test
	public void chainPersonAge() throws Exception {
		declarativeEmbeddedApp().test(httpClient ->
				assertEquals("person 12 age: example-age", httpClient.getText("chain/person/12/age")));
	}

	private EmbeddedApp declarativeEmbeddedApp() throws Exception {
		return EmbeddedApp.of(s -> {
			s.registry(Registry.of(r -> {
				r.add(new BasicHandler());
				r.add(new ChainHandler());
			}));
			s.handler(DeclarativeHandler::create);
		});
	}

}
