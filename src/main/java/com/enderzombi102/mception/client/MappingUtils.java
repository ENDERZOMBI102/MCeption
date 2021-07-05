package com.enderzombi102.mception.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.fabricmc.mapping.tree.*;
import net.fabricmc.tinyremapper.IMappingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.objectweb.asm.Type;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import net.fabricmc.mapping.reader.v2.TinyMetadata;

public class MappingUtils {
	protected static Logger LOGGER = LogManager.getFormatterLogger("FabricLoader");

	private static TinyTree mappings;
	private static boolean checkedMappings;

	public static TinyTree wrapTree(TinyTree mappings) {
		return new TinyTree() {
			final String primaryNamespace = getMetadata().getNamespaces().get(0); //If the namespaces are empty we shouldn't exist

			private Optional<String> remap(String name, String namespace) {
				return Optional.ofNullable(getDefaultNamespaceClassMap().get(name)).map(mapping -> mapping.getRawName(namespace)).map(Strings::emptyToNull);
			}

			String remapDesc(String desc, String namespace) {
				Type type = Type.getType(desc);

				switch (type.getSort()) {
					case Type.ARRAY: {
						StringBuilder remappedDescriptor = new StringBuilder(desc.substring(0, type.getDimensions()));

						remappedDescriptor.append(remapDesc(type.getElementType().getDescriptor(), namespace));

						return remappedDescriptor.toString();
					}

					case Type.OBJECT:
						return remap(type.getInternalName(), namespace).map(name -> 'L' + name + ';').orElse(desc);

					case Type.METHOD: {
						if ("()V".equals(desc)) return desc;

						StringBuilder stringBuilder = new StringBuilder("(");
						for (Type argumentType : type.getArgumentTypes()) {
							stringBuilder.append(remapDesc(argumentType.getDescriptor(), namespace));
						}

						Type returnType = type.getReturnType();
						if (returnType == Type.VOID_TYPE) {
							stringBuilder.append(")V");
						} else {
							stringBuilder.append(')').append(remapDesc(returnType.getDescriptor(), namespace));
						}

						return stringBuilder.toString();
					}

					default:
						return desc;
				}
			}

			private ClassDef wrap(ClassDef mapping) {
				return new ClassDef() {
					private final boolean common = getMetadata().getNamespaces().stream().skip(1).map(this::getRawName).allMatch(Strings::isNullOrEmpty);

					@Override
					public String getRawName(String namespace) {
						try {
							return mapping.getRawName(common ? primaryNamespace : namespace);
						} catch (ArrayIndexOutOfBoundsException e) {
							return ""; //No name for the namespace
						}
					}

					@Override
					public String getName(String namespace) {
						return mapping.getName(namespace);
					}

					@Override
					public String getComment() {
						return mapping.getComment();
					}

					@Override
					public Collection<MethodDef> getMethods() {
						return Collections2.transform(mapping.getMethods(), method -> new MethodDef() {
							@Override
							public String getRawName(String namespace) {
								try {
									return method.getRawName(namespace);
								} catch (ArrayIndexOutOfBoundsException e) {
									return ""; //No name for the namespace
								}
							}

							@Override
							public String getName(String namespace) {
								return method.getName(namespace);
							}

							@Override
							public String getComment() {
								return method.getComment();
							}

							@Override
							public String getDescriptor(String namespace) {
								String desc = method.getDescriptor(primaryNamespace);
								return primaryNamespace.equals(namespace) ? desc : remapDesc(desc, namespace);
							}

							@Override
							public Collection<ParameterDef> getParameters() {
								return method.getParameters();
							}

							@Override
							public Collection<LocalVariableDef> getLocalVariables() {
								return method.getLocalVariables();
							}
						});
					}

					@Override
					public Collection<FieldDef> getFields() {
						return Collections2.transform(mapping.getFields(), field -> new FieldDef() {
							@Override
							public String getRawName(String namespace) {
								try {
									return field.getRawName(namespace);
								} catch (ArrayIndexOutOfBoundsException e) {
									return ""; //No name for the namespace
								}
							}

							@Override
							public String getName(String namespace) {
								return field.getName(namespace);
							}

							@Override
							public String getComment() {
								return field.getComment();
							}

							@Override
							public String getDescriptor(String namespace) {
								String desc = field.getDescriptor(primaryNamespace);
								return primaryNamespace.equals(namespace) ? desc : remapDesc(desc, namespace);
							}
						});
					}
				};
			}

			@Override
			public TinyMetadata getMetadata() {
				return mappings.getMetadata();
			}

			@Override
			public Map<String, ClassDef> getDefaultNamespaceClassMap() {
				return Maps.transformValues(mappings.getDefaultNamespaceClassMap(), this::wrap);
			}

			@Override
			public Collection<ClassDef> getClasses() {
				return Collections2.transform(mappings.getClasses(), this::wrap);
			}
		};
	}

	public TinyTree getMappings() {
		if (!checkedMappings) {
			InputStream mappingStream = FabricLauncherBase.class.getClassLoader().getResourceAsStream("mappings/mappings.tiny");

			if (mappingStream != null) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(mappingStream))) {
					long time = System.currentTimeMillis();
					mappings = wrapTree(TinyMappingFactory.loadWithDetection(reader));
					LOGGER.debug("Loading mappings took " + (System.currentTimeMillis() - time) + " ms");
				} catch (IOException ee) {
					ee.printStackTrace();
				}

				try {
					mappingStream.close();
				} catch (IOException ee) {
					ee.printStackTrace();
				}
			}

			if (mappings == null) {
				LOGGER.info("Mappings not present!");
				mappings = TinyMappingFactory.EMPTY_TREE;
			}

			checkedMappings = true;
		}

		return mappings;
	}

	public String getTargetNamespace() {
		return FabricLauncherBase.getLauncher().isDevelopment() ? "named" : "intermediary";
	}

	public boolean requiresPackageAccessHack() {
		// TODO
		return getTargetNamespace().equals("named");
	}

	private static IMappingProvider.Member memberOf(String className, String memberName, String descriptor) {
		return new IMappingProvider.Member(className, memberName, descriptor);
	}

	public static String tryName(Mapped mapping, String namespace, String fallback) {
		String name = mapping.getRawName(namespace);
		return !Strings.isNullOrEmpty(name) ? name : fallback;
	}

	public static IMappingProvider create(TinyTree mappings, String from, String to) {
		return (acceptor) -> {
			for (ClassDef classDef : mappings.getClasses()) {
				String className = classDef.getRawName(from);
				if (Strings.isNullOrEmpty(className)) continue; //Class not present
				acceptor.acceptClass(className, tryName(classDef, to, className));

				for (FieldDef field : classDef.getFields()) {
					String fieldName = field.getRawName(from);
					if (Strings.isNullOrEmpty(fieldName)) continue; //Field not present
					acceptor.acceptField(memberOf(className, fieldName, field.getDescriptor(from)), tryName(field, to, fieldName));
				}

				for (MethodDef method : classDef.getMethods()) {
					String methodName = method.getRawName(from);
					if (Strings.isNullOrEmpty(methodName)) continue; //Method not present
					acceptor.acceptMethod(memberOf(className, methodName, method.getDescriptor(from)), tryName(method, to, methodName));
				}
			}
		};
	}
}