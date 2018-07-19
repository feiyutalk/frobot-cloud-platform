package org.iceslab.frobot.commons.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.log4j.Logger;

public class ExtensionLoader<C> {
	private static final Logger LOGGER = Logger.getLogger(ExtensionLoader.class);
	
	public C loadClass(String dir, String projectName, Class<C> parentClass) throws ClassNotFoundException{
		File jar = new File(dir);
		try {
			URLClassLoader loader = URLClassLoader.newInstance(new URL[]{jar.toURI().toURL()}, getClass().getClassLoader());
			Class<?> clazz = Class.forName(projectName, true, loader);
			Class<? extends C> newClass = clazz.asSubclass(parentClass);
			Constructor<? extends C> constructor = newClass.getConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			LOGGER.error("Extension loader error!");
		}
		throw new ClassNotFoundException("ClassNotFind");
	}

}
