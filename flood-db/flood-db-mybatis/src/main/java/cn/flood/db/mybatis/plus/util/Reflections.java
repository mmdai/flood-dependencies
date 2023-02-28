package cn.flood.db.mybatis.plus.util;


import cn.flood.base.core.Func;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 反射工具类. 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 *
 * @Author mmdai
 */
public class Reflections {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
   */
  public static void setFieldValue(final Object obj, final String fieldName, final Object value)
      throws IllegalAccessException {

    if (null == obj || Func.isBlank(fieldName) || null == value) {
      return;
    }
    Field field = getAccessibleField(obj, fieldName);
    if (field == null) {
      return;
    }
    try {
      field.set(obj, value);
    } catch (IllegalAccessException e) {
      throw e;
    }
  }

  /**
   * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
   * <p>
   * 如向上转型到Object仍无法找到, 返回null.
   */
  public static Field getAccessibleField(final Object obj, final String fieldName) {

    if (null == obj || Func.isBlank(fieldName)) {
      return null;
    }
    for (Class<?> superClass = obj.getClass(); superClass != Object.class;
        superClass = superClass.getSuperclass()) {
      try {
        Field field = superClass.getDeclaredField(fieldName);
        makeAccessible(field);
        return field;
      } catch (NoSuchFieldException e) {//NOSONAR
        // Field不在当前类定义,继续向上转型
        continue;// new add
      }
    }
    return null;
  }


  /**
   * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
   */
  public static void makeAccessible(Field field) {

    if ((!Modifier.isPublic(field.getModifiers()) || !Modifier
        .isPublic(field.getDeclaringClass().getModifiers()) || Modifier
        .isFinal(field.getModifiers())) && !field.isAccessible()) {
      field.setAccessible(true);
    }
  }
}
