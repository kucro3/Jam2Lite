package org.kucro3.jam2.invoke;

import java.lang.reflect.Field;

class FieldInvokerReflectionImpl extends FieldInvoker {
    protected FieldInvokerReflectionImpl(Field field)
    {
        super(field.getDeclaringClass(),
                field.getModifiers(),
                field.getName(),
                field.getType());
        this.field = field;
    }

    @Override
    public void set(Object obj, Object ref)
    {
        try {
            field.set(obj, ref);
        } catch (IllegalAccessException e) {
            // should not reach here
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object get(Object obj)
    {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            // should not reach here
            throw new IllegalStateException(e);
        }
    }

    private final Field field;
}
