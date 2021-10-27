package com.example;

import java.util.Map;

class FooBuilderAction implements IFooBuilderAction
{
    public void configure(Map<String, Object> options)
    {
    }

    public void parse(String source)
    {
    }

    public void enableBar()
    {
    }

    public void baz()
    {
    }

    public void withQuux()
    {
    }

    public void put(String name, Object value)
    {
    }

    public Foo build()
    {
        return new Foo();
    }
}
