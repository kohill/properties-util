package com.companyname.config.props;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Property Default Loader class for {@link PropertyReader}.
 * <p>
 * Default implementation: <br/>
 * All property files should be stored under the root folder {@value ROOT_FOLDER} in project's resources.
 * Primary property file should be stored in {@value MAIN_PROPERTIES} file.
 * Secondary and tertiary files should be in {@value PROJECT_PROPERTIES} and {@value LOCAL_PROPERTIES} files.
 * {@value PROJECT_PROPERTIES} should be used to define project-specific data in multi-project setup (e.g. when there is a "master"
 * project and a couple of dependent ones), otherwise it is unnecessary. {@value LOCAL_PROPERTIES} should be used for local executions
 * and generally should <b>NOT</b> be pushed to the repository.
 * <p>
 * On top of that properties can be specified via command line as regular system properties.
 * <p>
 * Additionally it is possible to load properties from the file specified in {@value ENV_PROPS_PATH} property supplied via one
 * of the mentioned methods. The value of this property should be resource path to an auxiliary property file <b>relative</b>
 * to {@value ROOT_FOLDER} folder (so for example in default implementation, to load "config/app/app101.properties" one has to specify "test.customprops=app/app101.properties").
 * <p> None of these property files are required, i.e. it is allowed to omit either one or even all of them. The order of loading
 * is as follows (newer properties override older ones):
 * <ol>
 * <li>main property file {@value MAIN_PROPERTIES} if exists</li>
 * <li>project property file {@value PROJECT_PROPERTIES} if exists</li>
 * <li>local property file {@value LOCAL_PROPERTIES} if exists</li>
 * <li>system properties</li>
 * <li>custom property file specified in {@value ENV_PROPS_PATH} property) if exists</li>
 * </ol>
 * <p>
 * New Feature 10/5/2021:
 * Added ability to use varibles as a property value.
 * This feature can be used when you have similar properties across the whole infrastructure and instead of creating a separate environment property file it is possible to create a common one with variables.
 * <p> If there is no such property, the value remains as is.
 * <p> Avoid indirection (variables pointing to properties with other variables) and recursion (both direct and indirect). Indirect variables may be resolved correctly depending on the order of their initialization, but one should not rely on this, because it makes project configuration too sophisticated.
 * <p> Example: test.property2=${other_property_name} will be substituted in properties with other_property_value.
 * <p> @author Anton Perapecha
 */
public class PropertyDefaultLoader implements PropertyLoader {
    public static final String ROOT_FOLDER = "config";
    public static final String MAIN_PROPERTIES = "config.properties";
    public static final String PROJECT_PROPERTIES = "project.properties";
    public static final String LOCAL_PROPERTIES = "local.properties";
    public static final String ENV_PROPS_PATH = "test.customprops";

    @Override
    public String getRootFolder() {
        return ROOT_FOLDER;
    }

    @Override
    public String getEnvPropertyName() {
        return ENV_PROPS_PATH;
    }

    @Override
    public LinkedList<String> getResourceFilesList() {
        return new LinkedList<>(Arrays.asList(MAIN_PROPERTIES, PROJECT_PROPERTIES, LOCAL_PROPERTIES));
    }

    @Override
    public java.util.Properties load() {
        java.util.Properties props = new java.util.Properties();
        getResourceFilesList().stream().forEach(s -> {
            String path = String.join("/", getRootFolder(), s);
            if (ConfigLoader.loadPropertiesFromResource(props, path)) {
                System.out.println("Properties loaded from file: " + path);
            }
        });
        props.putAll(System.getenv());
        props.putAll(System.getProperties());
        String customConfigPath = props.getProperty(getEnvPropertyName());
        if (StringUtils.isNotBlank(customConfigPath)) {
            List<String> customProps = Arrays.asList(customConfigPath.split(","));
            customProps.forEach(o -> {
                if (StringUtils.isNotBlank(o)) {
                    ConfigLoader.loadPropertiesFromResource(props, String.join("/", getRootFolder(), o.trim()));
                }
            });
        }
        return props;
    }

    @Override
    public java.util.Properties parseVariables(java.util.Properties props) {
        Pattern dp = Pattern.compile("\\$\\{([^}]*)}");
        java.util.Properties parsedProps = new java.util.Properties();
        parsedProps.putAll(props);
        if (!parsedProps.containsKey(Constant.ENV_NAME) && parsedProps.containsKey(Constant.ENV_NAME_DEFAULT)) {
            parsedProps.put(Constant.ENV_NAME, parsedProps.getProperty(Constant.ENV_NAME_DEFAULT));
        }
        parsedProps.stringPropertyNames().stream().forEach(s -> {
            StringBuffer sb = new StringBuffer();
            String value = parsedProps.getProperty(s);
            Matcher m = dp.matcher(value);
            while (m.find()) {
                if (StringUtils.isBlank(parsedProps.getProperty(m.group(1)))) {
                    System.err.println(String.format("'%s=%s' Unable to parse variable, variable '%s' is not initialized", s, value, m.group(1)));
                } else {
                    m.appendReplacement(sb, parsedProps.getProperty(m.group(1)));
                }
                m.appendTail(sb);
            }
            if (sb.length() != 0) {
                parsedProps.setProperty(s, sb.toString());
            }
        });
        return parsedProps;
    }
}
