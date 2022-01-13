/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.enderzombi102.mception.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import net.fabricmc.mapping.tree.*;
import net.fabricmc.tinyremapper.IMappingProvider;

import org.objectweb.asm.Type;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import net.fabricmc.mapping.reader.v2.TinyMetadata;

public class MappingUtils {
	public static TinyTree wrapTree(TinyTree mappings) {
		return new TinyTree() {
			final String primaryNamespace = getMetadata().getNamespaces().get(0); //If the namespaces are empty we shouldn't exist

			private Optional<String> remap(String name, String namespace) {
				return Optional.ofNullable( getDefaultNamespaceClassMap().get(name) )
						.map( mapping -> Strings.emptyToNull( mapping.getRawName(namespace) ) );
			}

			String remapDesc(String desc, String namespace) {
				Type type = Type.getType(desc);

				switch (type.getSort()) {
					case Type.ARRAY: {
						return desc.substring( 0, type.getDimensions() ) + remapDesc( type.getElementType().getDescriptor(), namespace );
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
							stringBuilder.append(')').append( remapDesc( returnType.getDescriptor(), namespace ) );
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
							return mapping.getRawName( common ? primaryNamespace : namespace );
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

	private static IMappingProvider.Member memberOf(String className, String memberName, String descriptor) {
		return new IMappingProvider.Member(className, memberName, descriptor);
	}

	private static String tryName(Mapped mapping, String namespace, String fallback) {
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
