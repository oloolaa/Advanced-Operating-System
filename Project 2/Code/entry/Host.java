package entry;

import controller.Controller;

public class Host {
    public static void main(String[] args) {
        int nodeId = 0;
        String configfile = "config.txt";
        String algorithmName = "lamport";
        String transport = "tcp";
        if (args.length > 0) {
            nodeId = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            configfile = args[1];
        }
        if (args.length > 2) {
            algorithmName = args[2];
        }
        if (args.length > 3) {
            transport = args[3];
        }

        String curDir = "";
        if (configfile.contains("/")) {
            curDir = configfile.substring(0, configfile.lastIndexOf("/") + 1);
        } else {
            curDir = "./";
        }

        Controller controller = new Controller(nodeId, configfile, algorithmName, transport);
        controller.setDir(curDir);
        controller.init();
        controller.start();
        System.out.println(nodeId + ": demo is finished!!!");
    }
}
