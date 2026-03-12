package lol.sylvie.lauve.std.objects;

import lol.sylvie.lauve.parsing.LauveCallable;
import lol.sylvie.lauve.parsing.LauveClass;
import lol.sylvie.lauve.parsing.LauveFunction;
import lol.sylvie.lauve.parsing.Token;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NativeLauveClass<T> extends LauveClass {
    Class<T> clazz;
    Map<String, Field> fields = new HashMap<>();

    public NativeLauveClass(Class<T> clazz, LauveClass superclass, Map<String, LauveFunction> methods, Set<String> fields) {
        super(clazz.getSimpleName(), superclass, methods);
        this.clazz = clazz;
        for (String fieldName : fields) {
            try {
                this.fields.put(fieldName, clazz.getDeclaredField(fieldName));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Field not found " + e);
            }
        }
    }

    public Object get(T object, Token name) {

        return null;
    }

    public void set(T object, Token name, Object value) {

    }

    public Class<T> getBackingClass() {
        return clazz;
    }
}
