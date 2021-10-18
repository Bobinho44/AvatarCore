package com.sanan.avatarcore.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.reflections.Reflections;

import com.sanan.avatarcore.util.bending.ability.CoreBendingAbility;
import sun.reflect.ReflectionFactory;

@SuppressWarnings("restriction")
public class AbilityClassLoader {
	
	public AbilityClassLoader() {}
	
	/**
	 * Returns a list of loaded objects of the provided classType.
	 *
	 * @param classType Type of class to load
	 * @param parentClass Type of class that the class must extend. Use
	 *            {@code Object.class} for classes without a type.
	 * @return
	 */
	public List<CoreBendingAbility> loadAbilities() {
		final ArrayList<CoreBendingAbility> abilities = new ArrayList<>();

		Reflections reflections = new Reflections("com.sanan.avatarcore");
		Set<Class<? extends CoreBendingAbility>> classes = reflections.getSubTypesOf(CoreBendingAbility.class);
	
		try {
			
			for (Class<? extends CoreBendingAbility> clazz : classes) {
				if (clazz.isAssignableFrom(CoreBendingAbility.class) || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
					continue;
				}
				final ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
				final Constructor<?> objDef = CoreBendingAbility.class.getDeclaredConstructor();
				final Constructor<?> intConstr = rf.newConstructorForSerialization(clazz, objDef);
				final CoreBendingAbility ability = (CoreBendingAbility) clazz.cast(intConstr.newInstance());
				if (ability == null) {
					continue;
				}
				
				abilities.add(ability);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.shutdown();
		}
		
		return abilities;
	}
	
}
