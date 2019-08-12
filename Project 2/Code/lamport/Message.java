package lamport;

import java.util.HashMap;
import java.util.Map;

public abstract class Message {
    Map<String, String> attributValues;

    public void add(String attr, String val) {
        attributValues.put(attr, val);
    }

    public Message() {
        attributValues = new HashMap<String, String>();
    }

    public void setAttributValues(Map<String, String> attributValues) {
        this.attributValues = attributValues;
    }

    public String getValue(String attr) {
        String val = null;
        if (attributValues.containsKey(attr)) 
        	val = attributValues.get(attr);
        return val;
    }

    public void setType(String type) {
        add("TYPE", type);
    }

    public void setNodeId(int id) {
        add("NODEID", String.valueOf(id));
    }

    public void setTimpeStamp(int timestamp) {
        add("TIMESTAMP", String.valueOf(timestamp));
    }

    public String getType() {
        return getValue("TYPE");
    }

    public int getNodeId() {
        return Integer.parseInt(getValue("NODEID"));
    }

    public int getTimpeStamp() {
        return Integer.parseInt(getValue("TIMESTAMP"));
    }

    public String toString() {
        Object keys[] = attributValues.keySet().toArray();

        String res = "";
        for (Object key : keys) {
            String skey = (String) key;
            String val = attributValues.get(skey);
            if (val == "") 
            	continue;
            res += skey + ":" + val + ";";
        }
        return res;
    }
}
