package com.healthedge.config;

public class Properties {

    /**
     * Web Driver & Browser properties
     */
    public static final String WEBDRIVER_IE = "webdriver.ie.driver";
    public static final String WEBDRIVER_FF = "webdriver.gecko.driver";
    public static final String WEBDRIVER_CHROME = "webdriver.chrome.driver";
    public static final String WEBDRIVER_EDGE = "webdriver.edge.driver";
    public static final String WEBDRIVER_HEADLESS = "webdriver.headless";
    public static final String WEBDRIVER_HUB_URL = "webdriver.hub.url";
    public static final String BROWSER_MONITOR_NUMBER = "browser.monitor.number";
    public static final String BROWSER_WINDOW_WIDTH = "browser.window.width";
    public static final String BROWSER_WINDOW_HEIGHT = "browser.window.height";

    /**
     * Test Configuration Properties
     */
    public static final String TEST_BROWSER = "test.browser";
    public static final String TEST_OS = "test.os";
    public static final String TEST_LOG_LEVEL = "test.log.level";
    public static final String TEST_WAIT_TIMEOUT = "test.wait.timeout";
    public static final String CLOSE_BROWSER_ON_FINISH = "close.browser.on.finish";
    public static final String BROWSER_DISABLE_WEB_SECURITY = "browser.disable.web.security";
    public static final String TAKE_SCREENSHOT = "take.screenshot";

    /**
     * Reporting Properties
     */
    public static final String REPORT_TITLE = "report.title";
    public static final String REPORT_NAME = "report.name";
    public static final String REPORT_PATH = "report.path";
    public static final String REPORT_THEME = "report.theme";

    /**
     * Environment Properties
     */
    public static final String ENV_NAME = "env.name";

    public static final String DOWNLOADS_TARGET_FOLDER = "downloads.target.folder";

    public static final String DB_DRIVER = "db.driver";
    public static final String DB_URL = "db.url";
    public static final String DB_USER = "db.user";
    public static final String DB_PASSWORD = "db.password";

    public static final String SUITE_THREAD_COUNT = "suite.thread.count";
    public static final String SUITE_NAME = "suite.name";
    public static final String SUITE_VERBOSE = "suite.verbose";

    public static final String RETRY_SUITE_NAME = "retry.suite.name";
    public static final String RETRY_SUITE_PATH = "retry.suite.path";
    public static final String RETRY_SUITE_ENABLE = "retry.suite.enable";

}
