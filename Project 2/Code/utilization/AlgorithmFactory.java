package utilization;

import controller.Node;
import lamport.LamportAlgorithm;

public class AlgorithmFactory {
    private static AlgorithmFactory instance = new AlgorithmFactory();

    public static AlgorithmFactory getInstance() {
        return instance;
    }

    public MutualExclusiveProtocol getAlgorithm(Node node, String input) {

        if (input.toLowerCase().startsWith("lamport")) {
            return new LamportAlgorithm(node.numNodes, node.localInfor.nodeId);
        }
        return null;
    }
}
