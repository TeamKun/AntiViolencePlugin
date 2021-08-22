package net.kunmc.lab.antiviolence.config.parser;

public abstract class Parser<T> {
    public abstract T parse(String str);
}
