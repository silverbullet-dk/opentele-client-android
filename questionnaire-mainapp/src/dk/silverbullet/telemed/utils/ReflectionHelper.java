package dk.silverbullet.telemed.utils;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

public class ReflectionHelper {

    private static final String TAG = Util.getTag(ReflectionHelper.class);

    public static boolean classCanBeLoaded(Context context, String packageAndClassName) {
        try {
            context.getClassLoader().loadClass(packageAndClassName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Method getMethod(Context context, String packageAndClassName , String methodName, Class... methodArgumentypes) throws ReflectionHelperException{
        try {
            Class clazz = loadClass(context, packageAndClassName);
            return clazz.getMethod(methodName, methodArgumentypes);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Could not find method:'" + methodName + "' in class:'" + packageAndClassName + "'", e);
            throw new ReflectionHelperException(e);
        }
    }

    public static Object getInstance(Context context, String packageAndClassName) throws ReflectionHelperException {
        try {
            return loadClass(context, packageAndClassName).getDeclaredConstructors()[0].newInstance();
        } catch (Exception e) { //TODO: Pak ind i fornuftig exception
            Log.e(TAG, "Could not create instance of class", e);
            throw new ReflectionHelperException(e);
        }
    }

    private static Class loadClass(Context context, String packageAndClassName) throws ReflectionHelperException {
        try {
            return context.getClassLoader().loadClass(packageAndClassName);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Could not load class", e);
            throw new ReflectionHelperException(e);
        }
    }
}
