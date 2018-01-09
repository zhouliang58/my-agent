package com.zl.skypointer.apm.agent.core.plugin.loader;

import com.zl.skypointer.apm.agent.core.boot.AgentPackageNotFoundException;
import com.zl.skypointer.apm.agent.core.boot.AgentPackagePath;
import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.PluginBootstrap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/8 22:52
 * @since JDK1.8
 */
public class AgentClassLoader extends ClassLoader{
    private static final ILog logger = LogManager.getLogger(AgentClassLoader.class);

    /**
     * The default class loader for the agent.
     */
    private static AgentClassLoader DEFAULT_LOADER;

    private List<File> classpath;
    private List<Jar> allJars;
    private ReentrantLock jarScanLock = new ReentrantLock();

    public static AgentClassLoader getDefault() {
        return DEFAULT_LOADER;
    }

    public AgentClassLoader(ClassLoader parent) throws AgentPackageNotFoundException {
        super(parent);
        File agentDictionary = AgentPackagePath.getPath();
        classpath = new LinkedList<File>();
        classpath.add(new File(agentDictionary, "plugins"));
        classpath.add(new File(agentDictionary, "activations"));
    }

    /**
     * Init the default
     *
     * @return
     * @throws AgentPackageNotFoundException
     */
    public static AgentClassLoader initDefaultLoader() throws AgentPackageNotFoundException {
        DEFAULT_LOADER = new AgentClassLoader(PluginBootstrap.class.getClassLoader());
        return getDefault();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        List<Jar> allJars = getAllJars();
        String path = name.replace('.', '/').concat(".class");
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(path);
            if (entry != null) {
                try {
                    URL classFileUrl = new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + path);
                    byte[] data = null;
                    BufferedInputStream is = null;
                    ByteArrayOutputStream baos = null;
                    try {
                        is = new BufferedInputStream(classFileUrl.openStream());
                        baos = new ByteArrayOutputStream();
                        int ch = 0;
                        while ((ch = is.read()) != -1) {
                            baos.write(ch);
                        }
                        data = baos.toByteArray();
                    } finally {
                        if (is != null)
                            try {
                                is.close();
                            } catch (IOException ignored) {
                            }
                        if (baos != null)
                            try {
                                baos.close();
                            } catch (IOException ignored) {
                            }
                    }
                    return defineClass(name, data, 0, data.length);
                } catch (MalformedURLException e) {
                    logger.error(e, "find class fail.");
                } catch (IOException e) {
                    logger.error(e, "find class fail.");
                }
            }
        }
        throw new ClassNotFoundException("Can't find " + name);
    }

    @Override
    protected URL findResource(String name) {
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                try {
                    return new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name);
                } catch (MalformedURLException e) {
                    continue;
                }
            }
        }
        return null;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        List<URL> allResources = new LinkedList<URL>();
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                allResources.add(new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name));
            }
        }

        final Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    /**
     * 加载插件Jar
     * @return
     */
    private List<Jar> getAllJars() {
        if (allJars == null) {
            jarScanLock.lock();
            try {
                if (allJars == null) {
                    allJars = new LinkedList<Jar>();
                    for (File path : classpath) {
                        if (path.exists() && path.isDirectory()) {
                            String[] jarFileNames = path.list(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String name) {
                                    return name.endsWith(".jar");
                                }
                            });
                            for (String fileName : jarFileNames) {
                                try {
                                    File file = new File(path, fileName);
                                    Jar jar = new Jar(new JarFile(file), file);
                                    allJars.add(jar);
                                    logger.info("{} loaded.", file.toString());
                                } catch (IOException e) {
                                    logger.error(e, "{} jar file can't be resolved", fileName);
                                }
                            }
                        }
                    }
                }
            } finally {
                jarScanLock.unlock();
            }
        }

        return allJars;
    }

    private class Jar {
        private JarFile jarFile;
        private File sourceFile;

        private Jar(JarFile jarFile, File sourceFile) {
            this.jarFile = jarFile;
            this.sourceFile = sourceFile;
        }
    }

}
