package com.companyname.config.props;

import java.util.LinkedList;
import java.util.Properties;

public interface PropertyLoader {
    String getRootFolder();

    String getEnvPropertyName();

    LinkedList<String> getResourceFilesList();

    Properties load();

    Properties parseVariables(Properties props);
}
