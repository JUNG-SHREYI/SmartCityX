package smartcityx;

import smartcityx.ui.SmartCityXCLI;

/**
 * Primary package entry point for SmartCityX platform.
 * Launches the interactive SmartCityX CLI or automated test suite.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            if (arg.equals("--test") || arg.equals("--verify") || arg.equals("-t")) {
                new SmartCityXCLI().runFullVerification();
                return;
            }
        }

        // Launch CLI directly
        new SmartCityXCLI().run();
    }
}
