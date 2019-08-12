package application;

import java.util.Vector;

import controller.DataReader;
import controller.LogManager;
import lamport.CustomVector;
import lamport.TimeInterval;
import utilization.PerformanceParser;

// enterCS.start enterCS.systemtime leaveCS.end leaveCS.systemtime
public class CorrectnessVerification {

    TimeInterval dataArray[][] = null;
    TimeInterval[] sortedResult = null;
    public long sumSD = 0;
    public long sumE = 0;
    public double meanSD = 0;
    public double meanE = 0;
    boolean verifyResult = false;
    int nodeNums;
    String fileName;

    CorrectnessVerification(String fileName, int nums) {
        dataArray = new TimeInterval[nums][];
        nodeNums = nums;
        this.fileName = fileName;
        sumSD = 0;
        sumE = 0;
        meanSD = 0;
        meanE = 0;
        verifyResult = false;
    }

    void readDataFromFile() {
        int vc[] = new int[nodeNums];
        CustomVector enterCSTimeStamp;
        long enterCSSystemTime;
        CustomVector leaveCSTimeStamp;
        long leaveCSSystemTime;

        for (int i = 0; i < nodeNums; i++) {
            String name = fileName + i;
            Vector<String> lines = DataReader.readLines(name);
            int size = lines.size();
            dataArray[i] = new TimeInterval[size];
            for (int j = 0; j < size; j++) {
                String line = lines.get(j);
                String datas[] = line.split(" ");
                int idx = 0;
                for (int vck = 0; vck < nodeNums; vck++) {
                    vc[vck] = Integer.parseInt(datas[idx++]);
                }
                enterCSTimeStamp = CustomVector.copy(vc);
                enterCSSystemTime = Long.parseLong(datas[idx++]);
                for (int vck = 0; vck < nodeNums; vck++) {
                    vc[vck] = Integer.parseInt(datas[idx++]);
                }
                leaveCSTimeStamp = CustomVector.copy(vc);
                leaveCSSystemTime = Long.parseLong(datas[idx++]);
                dataArray[i][j] = new TimeInterval(enterCSTimeStamp, enterCSSystemTime, leaveCSTimeStamp, leaveCSSystemTime, Integer.parseInt(datas[idx++]));
            }
        }
    }

    TimeInterval[] megerSortK(TimeInterval array[][], int start, int end) {
        int mid = (start + end) / 2;
        if (start >= end) {
            return null;
        }
        if (start + 1 == end) {
            return array[start];
        }

        TimeInterval[] l1 = megerSortK(array, start, mid);
        TimeInterval[] l2 = megerSortK(array, mid, end);
        if (l1 == null) {
            return l2;
        }
        if (l2 == null) {
            return l1;
        }

        TimeInterval newArray[] = new TimeInterval[l1.length + l2.length];
        int l1i = 0;
        int l2i = 0;
        int ln = 0;
        while (l1i < l1.length && l2i < l2.length) {
            newArray[ln] = new TimeInterval();
            if (CustomVector.compare(l2[l2i].enterCSTimeStamp, l1[l1i].enterCSTimeStamp) == 1) {
                newArray[ln].copy(l1[l1i]);
                l1i++;
            } else {
                newArray[ln].copy(l2[l2i]);
                l2i++;
            }
            ln++;
        }
        while (l1i < l1.length) {
            newArray[ln] = new TimeInterval();
            newArray[ln].copy(l1[l1i]);
            l1i++;
            ln++;
        }
        while (l2i < l2.length) {
            newArray[ln] = new TimeInterval();
            newArray[ln].copy(l2[l2i]);
            l2i++;
            ln++;
        }
        return newArray;
    }

    void testCorrect(TimeInterval wholeData[]) {
        int len = wholeData.length;
        int i = 1;
        if (CustomVector.compare(wholeData[0].enterCSTimeStamp, wholeData[0].leaveCSTimeStamp) != -1) {
            System.out.println(0);
            verifyResult = false;
            return;
        }
        while (i < len) {
            if (CustomVector.compare(wholeData[i].enterCSTimeStamp, wholeData[i].leaveCSTimeStamp) != -1) {
                System.out.println(i);
                verifyResult = false;
                return;
            }
            if (CustomVector.compare(wholeData[i].enterCSTimeStamp, wholeData[i - 1].leaveCSTimeStamp) != 1) {
                System.out.println(i);
                verifyResult = false;
                return;
            }
            i++;
        }
        verifyResult = true;
        return;
    }

    void verify() {
        readDataFromFile();
        sortedResult = megerSortK(dataArray, 0, dataArray.length);
        int len = sortedResult.length;
        int i = 0;
        while (i < len) {
            LogManager.getSingle().getLog("SortedTime").log(sortedResult[i].toString());
            i++;
        }
        testCorrect(sortedResult);
    }

    void caculate() {
        sortedResult = megerSortK(dataArray, 0, dataArray.length);
        int len = sortedResult.length;
        int i = 0;
        LogManager.getSingle().getLog("SynchronizeDelay").log("SynchronizeDelay \t Sum Of SynchromizeDelay \t fromNodeId \t toNodeId");
        LogManager.getSingle().getLog("ExecutionTime").log("Execution \t Sum Of Execution \t nodeId");
        while (i < len - 1) {
            long sd = sortedResult[i + 1].enterCSSystemTime - sortedResult[i].leaveCSSystemTime;
            sumSD += sd;
            LogManager.getSingle().getLog("SynchronizeDelay").log(sd + "\t" + sumSD + "\t" + sortedResult[i].nodeId + "->" + sortedResult[i + 1].nodeId);

            long e = sortedResult[i].leaveCSSystemTime - sortedResult[i].enterCSSystemTime;
            sumE += e;
            LogManager.getSingle().getLog("ExecutionTime").log(e + "\t" + sumE + "\t" + sortedResult[i].nodeId);
            i++;
        }
        meanSD = sumSD / (double) (len - 1);
        //LogManager.getSingle().getLog("meanSD").log(meanSD+"");
        meanE = sumE / (double) (len - 1);
        //LogManager.getSingle().getLog("meanE").log(meanE+"");
    }

    public static void main(String[] args) {
        String fileName = System.getProperty("user.dir") + "/TimeInterval-";
        String curDirecotry = System.getProperty("user.dir") + "/";
        int numNodes = 3;
        if (args.length > 0) {
            numNodes = Integer.parseInt(args[0]);
        }
        LogManager.getSingle().setDir(curDirecotry);
        CorrectnessVerification test = new CorrectnessVerification(fileName, numNodes);
        test.verify();
        test.caculate();
        System.out.println("verifyResult: " + test.verifyResult);
        PerformanceParser.parseFile(curDirecotry + "performance-", numNodes);
        LogManager.getSingle().getLog("Summary").log("n:\t" + String.valueOf(numNodes));
        LogManager.getSingle().getLog("Summary").log("verifyResult:\t" + String.valueOf(test.verifyResult));
        LogManager.getSingle().getLog("Summary").log("message complexity per cs:\t" + String.valueOf(PerformanceParser.result[0]));
        LogManager.getSingle().getLog("Summary").log("reponse time (ms):\t" + String.valueOf(PerformanceParser.result[1]));
        LogManager.getSingle().getLog("Summary").log("system throughput per s:\t" + String.valueOf(1000 / (test.meanE + test.meanSD)));
        LogManager.getSingle().getLog("Summary").log("meanSD (ms):\t" + String.valueOf(test.meanSD));
        LogManager.getSingle().getLog("Summary").log("meanExecutionTime (ms):\t" + String.valueOf(test.meanE));
        LogManager.getSingle().getLog("Summary").log("sumSD (ms):\t" + String.valueOf(test.sumSD));
        LogManager.getSingle().getLog("Summary").log("sumExecutionTime(ms):\t" + String.valueOf(test.sumE));
        //message complexity
        LogManager.getSingle().getLog("PureData").log(String.valueOf(PerformanceParser.result[0]));
        //response time
        LogManager.getSingle().getLog("PureData").log(String.valueOf(PerformanceParser.result[1]));
        //throughput
        LogManager.getSingle().getLog("PureData").log(String.valueOf(1000 / (test.meanE + test.meanSD)));
        System.out.println("CorrectnessTest finished");
    }
}
