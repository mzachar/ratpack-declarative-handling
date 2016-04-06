#ratpack-declarative-handling

Brings declarative way of defining ratpack handlers (similar to Spring @Controller)

This is **experimental** implementation just to showcase the idea even though that following examples are fully functional.

Declaring handler is as simple as providing *@Handler* annotation. If such object is registered in ratpack *Registry* it will get auto discovered.
```java
@Handler
@Prefix("basic")
public class BasicHandler {

	@Path("context")
	public void context(Context ctx) {
		ctx.render("context");
	}
}
```


You can also specify other method arguments which will be obtained from context
```java
@Path("requestResponse")
public void requestResponse(Request request, Response response) {
	response.send(request.getPath());
}
```

Also there is support for PathTokens and QueryParameters
```java
@Path("pathToken/:id")
public void pathToken(Context ctx, @PathToken("id") String id) {
	ctx.render("pathToken/" + id);
}

@Path("queryParam")
public void queryParam(Context ctx, @QueryParam("param") String param) {
	ctx.render("queryParam?param=" + param);
}
```

Any return value except few build in types will be wired directly to ctx.render() method
```java
@Path("direct")
public String direct() {
	return "direct";
}
```

Of course you can build your own chains/handlers as you would expect
```java
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
```

Or you can setup the chain in dedicated class with injected person object exposed by first handler
```java
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

}
```