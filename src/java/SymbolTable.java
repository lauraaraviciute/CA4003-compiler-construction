import java.util.*;
public class SymbolTable {
    // the main symbol table with scope of "global"
    Hashtable<String, LinkedList<String>> symbolTable;
    // table that contains the type (ie. var, int, void etc) for each scope+id
    Hashtable<String, String> typeTable;
    // table that describes types as functions/parameters/const/var
    Hashtable<String, String> descriptionTable;
    //values associated with each type
    
    SymbolTable() {
        this.symbolTable = new Hashtable<>();
        this.typeTable = new Hashtable<>();
        this.descriptionTable = new Hashtable<>();
        
        symbolTable.put("global", new LinkedList<>());
    }
    
    public void get(String id, String type, String scope) {
        LinkedList<String> list = symbolTable.get(scope);
        if(list == null) {
            System.out.println("Variable " + id + " not declared in " + scope);
        }     
    }
    public String getType(String id, String scope) {
        String type = typeTable.get(id+scope);
        if(type != null) 
            return type;
        else {
            type = typeTable.get(id+"global");
            if(type != null) {
                return type;
            }
        }
        return null;
    }
    
    public String getParamType(int index, String scope) {
        int count = 0;
        LinkedList<String> idList = symbolTable.get(scope);
            for(String id : idList) {
                String type = typeTable.get(id+scope);
                String description = descriptionTable.get(id+scope);
                if(description.equals("param")) {
                    count++;
                    if(count == index) {
                        return type;
                    }
                }
            }
            return null;
    }
    
    public String getDescription(String id, String scope) {
        String description = descriptionTable.get(id+scope);
        if(description != null) 
            return description;
        else {
            description = descriptionTable.get(id+"global");
            if(description != null) {
                return description;
            }
        }
        return null;
    }
    public LinkedList<String> getScopeTable(String scope) {
        return symbolTable.get(scope);
    }
    
    public int getParams(String id) {
        LinkedList<String> list = symbolTable.get(id);
        int count = 0;
        for(int i = 0; i < list.size(); i++) {
            String description = descriptionTable.get(list.get(i)+id);
            if(description.equals("param")) {
                count++;
            }
        }
        return count;
    }
    
    public boolean noDuplicates(String id, String scope) {
        LinkedList<String> list = symbolTable.get(scope);
        LinkedList<String> global_list = symbolTable.get("global");
        if(scope.equals("global")) {
            return global_list.indexOf(id) == global_list.lastIndexOf(id);
        }
        return ((list.indexOf(id) == list.lastIndexOf(id)) && (global_list.indexOf(id) == -1));
        
    }
    
    public void put(String id, String type, String information, String scope) {
        // see if current scope already exists
        LinkedList<String> list = symbolTable.get(scope);
        if(list == null) {
            list = new LinkedList<>();
            // new scope with id
            list.add(id);
            // add to list of scopes available in global scope
            symbolTable.put(scope, list);
        }
        else {
            // add as first element as we want most current value for later semantic analysis
            list.addFirst(id);
        }
        // unique has for each is id+type / id+information
        typeTable.put(id+scope, type);
        descriptionTable.put(id+scope, information);
    }
    
    public void printSymbolTable() {
        Enumeration e = symbolTable.keys();
        while(e.hasMoreElements()) {
            // get id for each scope
            String scope = (String) e.nextElement();
            System.out.println("Scope: " + scope);
            // get contents associated with that scope
            LinkedList<String> idList = symbolTable.get(scope);
            for(String id : idList) {
                String type = typeTable.get(id+scope);
                String description = descriptionTable.get(id+scope);
                System.out.println("[" + id + ", " + type + ", " + description + "]");
            }
            System.out.println();
        }
    }
    
    public ArrayList<String> functionsToList() {
        LinkedList<String> list = symbolTable.get("global");
        ArrayList<String> functions = new ArrayList<String>();
        for(int i = 0; i < list.size(); i++) {
                String description = descriptionTable.get(list.get(i)+"global");
                if(description.equals("function"))
                    functions.add(list.get(i));
        }
        return functions;
    }
    
    public boolean isFunction(String id) {
        LinkedList<String> list = symbolTable.get("global");
        ArrayList<String> functions = new ArrayList<String>();
        for(int i = 0; i < list.size(); i++) {
                String description = descriptionTable.get(list.get(i)+"global");
                if(description.equals("function") && list.get(i).equals(id)) {
                    return true;
                }
        }
        return false;
    }
}