package de.coop.tgvertretung.utils;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;

public class SubjectSymbols implements Serializable {

    private static final long serialVersionUID = -5263223032432580842L;

    private HashMap<String, String> symbols = new HashMap<>();

    public void setSymbol(String symbol, String name) {
        symbols.put(symbol, name);
    }

    public void removeSymbol(String symbol) {
        symbols.remove(symbol);
    }

    public String getSymbolName(String symbol) {
        if(symbols.containsKey(symbol))
            return symbols.get(symbol);
        return symbol;
    }

    public String getSymbolName(int index) {
        try {
            return (String) symbols.values().toArray()[index];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSymbol(int index) {
        try {
            return (String) symbols.keySet().toArray()[index];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getSymbolIndex(String symbol) {
        Object[] keys = symbols.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            if (key.equals(symbol)) {
                return i;
            }
        }

        return -1;
    }

    public int getCount() {
        return symbols.size();
    }

    public String getJson(){
        Gson gson = new Gson();
        return gson.toJson(symbols);
    }

    public void readJson(String json){
        Gson gson = new Gson();
        symbols = gson.fromJson(json, new HashMap<String, String>().getClass());
    }
}