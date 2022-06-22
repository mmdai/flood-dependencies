package cn.flood.lang;

import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 
* <p>Title: ArrayHelper</p>  
* <p>Description: 集合查找工具类</p>  
* 
* 接口	                            参数	                     返回类型	                             描述
Predicate<T>	  T	        boolean	                             用于判别一个对象。比如求一个人是否为男性
Consumer<T>	      T	        void	                             用于接收一个对象进行处理但没有返回，比如接收一个人并打印他的名字
Function<T, R>	  T	        R	                                         转换一个对象为不同类型的对象
Supplier<T>	      None      T	                                         提供一个对象
UnaryOperator<T>  T	        T	                                         接收对象并返回同类型的对象
BinaryOperator<T> (T, T)	T	                                         接收两个同类型的对象，并返回一个原类型对象
* 
* 
* @author mmdai  
* @date 2018年11月26日
 */
public class ArrayUtils {
	
	 /**
     * Don't let anyone instantiate this class
     */
    private ArrayUtils() {
        throw new AssertionError("Cannot create instance");
    }

    /**
     * 用于lamda 排序.stream().sorted(Comparator.comparing(CosAboveAnaWasteWeek::getTotalMoney).reversed()).collect(Collectors.toList());
     * 用于lamda 去重 列（.stream().filter(distinctByField(e->e.getPsid())).collect(Collectors.toList());）
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByField(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>(16);
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
	/**
	 * 
	 * <p>Title: findMax</p>  
	 * <p>Description: 获取最大值</p>  
	 * @param array 集合
	 * @param func 函数方法
	 * @return
	 */
    public static <T, V extends Comparable<V>> T findMax(List<T> array, Function<T, V> func){
        T max = array.get(0);
        for (int i = 1; i < array.size(); i++)
        {
        	//小于(-1 前小后大)、等于(0)或大于(1)
        	if (func.apply(max).compareTo(func.apply(array.get(i)))< 0) {
                max = array.get(i);
            }

        }
        return max;
    }
    /**
     * 
     * <p>Title: findMin</p>  
     * <p>Description: 获取最小值</p>  
     * @param array
     * @param func
     * @return
     */
    public static <T, V extends Comparable<V>> T findMin(List<T> array, Function<T, V> func){
    	T min = array.get(0);
    	for (int i = 1; i < array.size(); i++){
         	//小于(-1 前小后大)、等于(0)或大于(1)
         	if (func.apply(min).compareTo(func.apply(array.get(i)))> 0){
                min = array.get(i);
            }
        }
        return min;
    }
    /**
     * 
     * <p>Title: findAll</p>  
     * <p>Description: 查找所有满足条件的元素[如血量>50的敌人]</p>  
     * @param array
     * @param func
     * @return
     */
    public static <T> List<T> findAll(List<T> array, Predicate<T> func){
        //存储满足条件的元素的数组
        List<T> list = new ArrayList<T>();
        for(T item : array){
        	//满足什么条件 ,如 >50或<30
        	if(func.test(item)){
                //添加满足条件元素
                list.add(item);
            }
        }
        return list;
    }
    
    /**
     * 
     * <p>Title: Find</p>  
     * <p>Description: 查找单个满足条件的元素</p>  
     * @param array
     * @param func
     * @return
     */
    public static <T> T findOne(List<T> array , Predicate<T> func)
    {
    	for(T item : array){
    		if(func.test(item)){
                return item;
            }
    	}
        //泛型的默认值
        return null;
    }
    
    /**
     * 
     * <p>Title: sortAsc</p>  
     * <p>Description: 升序排列</p>  
     * @param array
     * @param func
     */
    public static <T, V extends Comparable<V>> void sortAsc(List<T> array, Function<T, V> func){
        for (int i = 0; i < array.size()-1; i++){
            for (int j = 0; j < array.size()-1-i; j++){
            	//小于(-1 前小后大)、等于(0)或大于(1)
                if (func.apply(array.get(j)).compareTo(func.apply(array.get(j+1)))>0){
                    Collections.swap(array, j, j + 1);
                }
            }
        }
    }
    /**
     * 
     * <p>Title: sortDesc</p>  
     * <p>Description: 降序排列</p>  
     * @param array
     * @param func
     */
    public static <T, V extends Comparable<V>> void sortDesc(List<T> array, Function<T, V> func){
        for (int i = 0; i < array.size()-1; i++){
            for (int j = 0; j < array.size()-1-i; j++){
            	//小于(-1 前小后大)、等于(0)或大于(1)
                if (func.apply(array.get(j)).compareTo(func.apply(array.get(j+1)))<0){
                    Collections.swap(array, j, j + 1);
                }
            }
        }
    }
    /**
     * 
     * <p>Title: select</p>  
     * <p>Description: 根据集合T,返回集合V</p>  
     * @param array
     * @param func
     * @return
     */
    public static <T, V> List<V> select(List<T> array, Function<T, V> func){
    	//存储满足条件的元素的数组
        List<V> list = new ArrayList<V>();
        for(T item : array){
    		if(func.apply(item)!=null){
                list.add(func.apply(item));
            }
    	}
    	return list;
    }
    
    /**
     * 判断是否为空或者为{@code null}
     *
     * @param array 数组
     * @return 当数组为空或{@code null}时返回{@code true}
     */
    public static boolean isEmpty(final char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断是否为空或者为{@code null}
     *
     * @param array 数组
     * @return 当数组为空或{@code null}时返回{@code true}
     */
    public static boolean isEmpty(final boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断是否为空或者为{@code null}
     *
     * @param array 数组
     * @return 当数组为空或{@code null}时返回{@code true}
     */
    public static boolean isEmpty(final byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断是否为空或者为{@code null}
     *
     * @param array 数组
     * @return 当数组为空或{@code null}时返回{@code true}
     */
    public static boolean isEmpty(final short[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断是否为空或者为{@code null}
     *
     * @param array 数组
     * @return 当数组为空或{@code null}时返回{@code true}
     */
    public static boolean isEmpty(final int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断是否为空或者为{@code null}
     *
     * @param array 数组
     * @return 当数组为空或{@code null}时返回{@code true}
     */
    public static boolean isEmpty(final long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断是否为空或者为{@code null}
     *
     * @param array 数组
     * @return 当数组为空或{@code null}时返回{@code true}
     */
    public static boolean isEmpty(final float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断是否为空或者为{@code null}
     *
     * @param array 数组
     * @return 当数组为空或{@code null}时返回{@code true}
     */
    public static boolean isEmpty(final double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断是否为空或为{@code null}
     *
     * @param array 数组
     * @param <T>   泛型类
     * @return 当数组为空或{@code null}时返回{@code true}
     */
    public static <T> boolean isEmpty(final T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断是否不为空或不为{@code null}
     *
     * @param array 数组
     * @param <T>   泛型类
     * @return 当数组不为空且不是{@code null}时返回{@code true}
     */
    public static <T> boolean isNotEmpty(final T[] array) {
        return array != null && array.length != 0;
    }

    /**
     * 判断数组非空且长度大于0
     *
     * @param array 数组
     * @return 判断成功则返回{@code true},否则返回{@code false}
     */
    public static boolean isNotEmpty(final char[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 判断数组非空且长度大于0
     *
     * @param array 数组
     * @return 判断成功则返回{@code true},否则返回{@code false}
     */
    public static boolean isNotEmpty(final int[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 判断数组非空且长度大于0
     *
     * @param array 数组
     * @return 判断成功则返回{@code true},否则返回{@code false}
     */
    public static boolean isNotEmpty(final float[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 判断数组非空且长度大于0
     *
     * @param array 数组
     * @return 判断成功则返回{@code true},否则返回{@code false}
     */
    public static boolean isNotEmpty(final double[] array) {
        return array != null && array.length > 0;
    }

    /**
     * 判断数组非空且长度大于0
     *
     * @param array 数组
     * @return 判断成功则返回{@code true},否则返回{@code false}
     */
    public static boolean isNotEmpty(final long[] array) {
        return array != null && array.length > 0;
    }
    
    /**
     * <p>判断数组中是否包含了指定的元素</p>
     * <pre class="code">
     * ObjectUtils.containsElement(new String[]{"aaaa","bbb","cc",null},null); //--- true
     * ObjectUtils.containsElement(new String[]{"aaaa","bbb","cc"},"cc"); //--- true
     * ObjectUtils.containsElement(new String[]{"aaaa","bbb","cc",null},"xx"); //--- false
     * </pre>
     *
     * @param array   数组
     * @param element 检查的元素对象
     * @param <T>     泛型类型声明
     * @return 如果数组中存在则返回{@code true},否则返回{@code false}
     * @see ObjectUtils#nullSafeEquals(Object, Object)
     */
    public static <T> boolean containsElement(T[] array, T element) {
        if (isEmpty(array)) {
            return false;
        }
        for (Object arrayEle : array) {
            if (ObjectUtils.nullSafeEquals(arrayEle, element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将数组转为字符串，使用英文半角逗号连接
     *
     * @param array 数组
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(Object[] array) {
        return toString(array, ",");
    }

    /**
     * 将数组转为字符串，使用{@code delimiter}将元素连接起来
     *
     * @param array     数组
     * @param delimiter 数组元素分隔符
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(Object[] array, String delimiter) {
        if (isEmpty(array)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        int length = array.length - 1;
        for (int i = 0; ; i++) {
            builder.append(String.valueOf(array[i]));
            if (i == length) {
                return builder.toString();
            }
            builder.append(delimiter);
        }
    }

    /**
     * 将数组转为字符串，使用英文半角逗号连接
     *
     * @param array     数组
     * @param delimiter 数组元素分隔符
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(char[] array, String delimiter) {
        if (isEmpty(array)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        int length = array.length - 1;
        for (int i = 0; ; i++) {
            builder.append(array[i]);
            if (i == length) {
                return builder.toString();
            }
            builder.append(delimiter);
        }
    }

    /**
     * 将数组转为字符串，使用{@code delimiter}将元素连接起来
     *
     * @param array 数组
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(char[] array) {
        return toString(array, ",");
    }

    /**
     * 将数组转为字符串，使用英文半角逗号连接
     *
     * @param array     数组
     * @param delimiter 数组元素分隔符
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(boolean[] array, String delimiter) {
        if (isEmpty(array)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        int length = array.length - 1;
        for (int i = 0; ; i++) {
            builder.append(array[i]);
            if (i == length) {
                return builder.toString();
            }
            builder.append(delimiter);
        }
    }

    /**
     * 将数组转为字符串，使用{@code delimiter}将元素连接起来
     *
     * @param array 数组
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(boolean[] array) {
        return toString(array, ",");
    }

    /**
     * 将数组转为字符串，使用英文半角逗号连接
     *
     * @param array     数组
     * @param delimiter 数组元素分隔符
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(byte[] array, String delimiter) {
        if (isEmpty(array)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        int length = array.length - 1;
        for (int i = 0; ; i++) {
            builder.append(array[i]);
            if (i == length) {
                return builder.toString();
            }
            builder.append(delimiter);
        }
    }

    /**
     * 将数组转为字符串，使用{@code delimiter}将元素连接起来
     *
     * @param array 数组
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(byte[] array) {
        return toString(array, ",");
    }

    /**
     * 将数组转为字符串，使用英文半角逗号连接
     *
     * @param array     数组
     * @param delimiter 数组元素分隔符
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(short[] array, String delimiter) {
        if (isEmpty(array)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        int length = array.length - 1;
        for (int i = 0; ; i++) {
            builder.append(array[i]);
            if (i == length) {
                return builder.toString();
            }
            builder.append(delimiter);
        }
    }

    /**
     * 将数组转为字符串，使用{@code delimiter}将元素连接起来
     *
     * @param array 数组
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(short[] array) {
        return toString(array, ",");
    }

    /**
     * 将数组转为字符串，使用英文半角逗号连接
     *
     * @param array 数组
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(int[] array) {
        return toString(array, ",");
    }

    /**
     * 将数组转为字符串，使用{@code delimiter}将元素连接起来
     *
     * @param array     数组
     * @param delimiter 数组元素分隔符
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(int[] array, String delimiter) {
        if (isEmpty(array)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        int length = array.length - 1;
        for (int i = 0; ; i++) {
            builder.append(array[i]);
            if (i == length) {
                return builder.toString();
            }
            builder.append(delimiter);
        }
    }

    /**
     * 将数组转为字符串，使用英文半角逗号连接
     *
     * @param array 数组
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(long[] array) {
        return toString(array, ",");
    }

    /**
     * 将数组转为字符串，使用{@code delimiter}将元素连接起来
     *
     * @param array     数组
     * @param delimiter 数组元素分隔符
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(long[] array, String delimiter) {
        if (isEmpty(array)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        int length = array.length - 1;
        for (int i = 0; ; i++) {
            builder.append(array[i]);
            if (i == length) {
                return builder.toString();
            }
            builder.append(delimiter);
        }
    }

    /**
     * 将数组转为字符串，使用英文半角逗号连接
     *
     * @param array     数组
     * @param delimiter 数组元素分隔符
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(float[] array, String delimiter) {
        if (isEmpty(array)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        int length = array.length - 1;
        for (int i = 0; ; i++) {
            builder.append(array[i]);
            if (i == length) {
                return builder.toString();
            }
            builder.append(delimiter);
        }
    }

    /**
     * 将数组转为字符串，使用{@code delimiter}将元素连接起来
     *
     * @param array 数组
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(float[] array) {
        return toString(array, ",");
    }

    /**
     * 将数组转为字符串，使用英文半角逗号连接
     *
     * @param array     数组
     * @param delimiter 数组元素分隔符
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(double[] array, String delimiter) {
        if (isEmpty(array)) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        int length = array.length - 1;
        for (int i = 0; ; i++) {
            builder.append(array[i]);
            if (i == length) {
                return builder.toString();
            }
            builder.append(delimiter);
        }
    }

    /**
     * 将数组转为字符串，使用{@code delimiter}将元素连接起来
     *
     * @param array 数组
     * @return 如果数组为空则返回空字符串
     */
    public static String toString(double[] array) {
        return toString(array, ",");
    }


    /**
     * <p>
     * 向数组中追加一个字符串，返回一个新的字符串数组.
     * </p>
     * <pre class="code">
     * StringUtils.addStringToArray(new String[]{"hello"},"world"); //--- [hello,world]
     * StringUtils.addStringToArray(null,"hah"); //--- [haha]
     * </pre>
     *
     * @param array  原始数组
     * @param addStr 追加的字符串
     * @return 返回一个新的数组，该数组永远不为{@code null}
     */
    public static String[] addStringToArray(String[] array, String addStr) {
        if (ArrayUtils.isEmpty(array)) {
            return new String[]{addStr};
        }
        String[] newArray = new String[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = addStr;
        return newArray;
    }

    /**
     * <p>
     * 将两个字符串数组对接,返回一个新的字符串数组.
     * </p>
     * <pre class="code">
     * StringUtils.concatStringArrays(new String[]{"girl","women"},new String[]{"boy","girl"}); //--- [girl,women,boy,girl]
     * </pre>
     *
     * @param arr1 数组
     * @param arr2 数组
     * @return 返回一个新的字符串数组
     */
    public static String[] concatStringArrays(String[] arr1, String[] arr2) {
        if (ArrayUtils.isEmpty(arr1)) {
            return arr2;
        }
        if (ArrayUtils.isEmpty(arr2)) {
            return arr1;
        }
        String[] newArr = new String[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, newArr, 0, arr1.length);
        System.arraycopy(arr2, 0, newArr, arr1.length, arr2.length);
        return newArr;
    }

    public static void main(String[] args){
    	Pers[] array = {new Pers("zs", 10), 
    			new Pers("ls", 25), 
    			new Pers("ww", 19),
    			new Pers("ww", 30)};
    	List<Pers> list = Arrays.asList(array);
    	System.out.println( findMax(list, e -> e.getAge()));
    	System.out.println( findMin(list, e -> e.getAge()));
    	System.out.println( findAll(list, e -> e.getAge()>10 && e.getAge()<30));
    	System.out.println( findOne(list, e -> e.getAge()>10 && e.getAge()<30));
    	sortAsc(list, e -> e.getAge());
    	for(Pers p: array){
    		System.out.println("排序："+ p);
    	}
    	List<Long> ageList = select(list, e -> e.getAge());
    	System.out.println("ageList："+ ageList);
    }
    


}

class Pers{
	
	private String name;
	
	private long age;
	
	public Pers() {
		super();
	}

	public Pers(String name, long age) {
		super();
		this.name = name;
		this.age = age;
	}

	@Override
	public String toString() {
		return "persion [name=" + name + ", age=" + age + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}
	
}

