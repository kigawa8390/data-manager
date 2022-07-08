package net.kigawa.kutil.data.mysql;


import net.kigawa.kutil.data.database.DatabaseOption;
import net.kigawa.kutil.data.database.DatabaseResolver;
import net.kigawa.kutil.data.javaConstraint.JavaOption;
import net.kigawa.kutil.data.javaField.IntField;

import java.util.function.Function;

public enum OptionResolver implements DatabaseResolver.ResolverInterface<JavaOption, DatabaseOption>
{
    INT(IntField.class, intField -> {
        return null;
    });

    private final Class<? extends JavaOption> javaFieldClass;
    private final Function<JavaOption, DatabaseOption> resolver;

    <T extends JavaOption> OptionResolver(Class<T> javaFieldClass, Function<T, DatabaseOption> resolver)
    {
        this.javaFieldClass = javaFieldClass;
        this.resolver = (Function<JavaOption, DatabaseOption>) resolver;
    }

    @Override
    public boolean isInstance(JavaOption dataField)
    {
        return javaFieldClass.isInstance(dataField);
    }

    @Override
    public Function<JavaOption, DatabaseOption> getResolver()
    {
        return resolver;
    }
}