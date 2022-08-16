# ZenScript library

### The goal of ZenScript was to provide a scripting system that fit several requirements:

* Be usable by people without any prior programming knowledge, for simple tasks
* Be usable for advanced tasks by experienced programmers
* Integrate easily with Java classes
* Provide sufficient syntax and libraries to integrate with its environment
* Be clear and concise in its operation
* Be typesafe, such that both small and large applications can be made with it
* Be efficient to run


ZenScript originated from MineTweaker, where I needed a simple programming
language that would allow users without programming knowledge to be able
to execute simple commands by following the tutorials for it.

Originally MineTweaker had a simple one-line-at-a-time parsed scripting system,
but it quickly became clear that it wasn't flexible enough, so a simple parsed
language was created. This parsed language worked quite well but was very
inefficient as each value was wrapped into its own object. As I wanted to
expand the capabilities, this language was not very efficient.

Additionally, it wasn't type-safe. There were no type definitions. On one hand,
this is handy, since I didn't want to bother people with types. Following the
tutorial was easy and the code was short. However, this creates a problem for the
more experienced user, wanting to go beyond the basic tutorials: what are these
values I'm working with, what properties do they have, and which methods are
available on them? It's not clear.

As a balance, ZenScript allows mixed typed and typeless behavior. You don't
need to define types anywhere - the compiler will infer them where possible
and exhibit typeless behavior when the type is effectively unknown. In nearly
all cases, the type is perfectly known and execution runs at native java
speed. Since there are types, they can be documented and enforced.
