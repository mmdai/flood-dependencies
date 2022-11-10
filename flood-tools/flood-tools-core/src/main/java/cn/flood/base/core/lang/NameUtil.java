package cn.flood.base.core.lang;

/**
 *
 */
public class NameUtil {

	public static String className(String name) {
		return firstUpperCase(shortName(name));
	}
	
	public static String fieldName(String name) {
		if (name.split("_").length == 1) {
			name = name.toLowerCase();
		}
		return firstLowerCase(shortName(name));
	}
	
	public static String tableName(String name) {
		return fullName(name).toUpperCase();
	}
	
	/**
	 * 将下划线后面的字符大写，去掉下划线，其余字母小写
	 * 没有下划线的情况就按原样返回
	 * @return
	 */
	public static String shortName(String name) {
		if (name == null){
			return null;
		}
		//没有下划线的情况就按原样返回
		if (name.split("_").length == 1) {
			return name;
		}
		name = name.toLowerCase();
		name = name.replaceAll("_$", "");
		int index = 0;
		while ((index = name.indexOf("_")) != -1) {
			char ch = name.charAt(index + 1);
			name = name.replaceFirst("_.", String.valueOf(ch).toUpperCase());
			index = name.indexOf("_");
		}
		return name;
	}
	
	/**
	 * 将大写字母前面加上下划线，去掉原有下划线，字母全小写
	 * @return
	 */
	public static String fullName(String name) {
		StringBuffer sb = new StringBuffer();
		String endo = "_";
		name = name.replaceAll(endo, " ");
		for (char ch : name.toCharArray()) {
			//判断是否大写
			if (ch >= 'A' && ch <= 'Z') {
				ch = (char)(ch + 32);
				sb.append(endo).append(ch);
			} else {
				sb.append(ch);
			}
		}
		return sb.toString().replaceAll(" ", endo).replaceAll(endo+"+", endo);
	}
	
	public static String actionName(String name) {
		StringBuffer sb = new StringBuffer();
		String endo = "-";
		name = name.replaceAll(endo, "");
		for (char ch : name.toCharArray()) {
			//判断是否大写
			if (ch >= 'A' && ch <= 'Z') {
				ch = (char)(ch + 32);
				sb.append(endo).append(ch);
			} else {
				sb.append(ch);
			}
		}
		return sb.toString().replaceAll("^" + endo, "");
	}
	
	public static String firstUpperCase(String name) {
		if (name == null){
			return null;
		}
		if (name.length() == 0) {
			return name;
		}
		if (name.length() == 1) {
			return name.toUpperCase();
		}
		char ch = name.charAt(0);
		ch = (char) (ch >= 'A' && ch <= 'Z' ? ch : ch - 32);
		name = ch + name.substring(1);
		return name;
	}
	
	public static String firstLowerCase(String name) {
		if (name == null) {
			return null;
		}
		if (name.length() == 0) {
			return name;
		}
		if (name.length() == 1) {
			return name.toLowerCase();
		}
		char ch = name.charAt(0);
		ch = (char) (ch >= 'A' && ch <= 'Z' ? ch + 32 : ch);
		name = ch + name.substring(1);
		return name;
	}

	public static void main(String[] args) {
		System.out.println(fieldName("ACTION_NAME"));
	}
}
