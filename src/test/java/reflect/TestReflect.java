package reflect;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

public class TestReflect {
	public static void main(String[] args) {
		Common common1 = new Common();
		common1.setValue(true);
		Common common2 = new Common();
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(common1.getClass());
		for (PropertyDescriptor pd : pds) {

			String propertyName = pd.getName();

			if (PropertyUtils.isReadable(common2, propertyName) && PropertyUtils.isWriteable(common2, propertyName)
					&& PropertyUtils.isReadable(common1, propertyName)) {
				Object value;
				try {
					value = PropertyUtils.getProperty(common1, propertyName);
					if (value != null) {
						pd.getWriteMethod().invoke(common2, value);
					}
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println(common2.isValue());

	}
}
