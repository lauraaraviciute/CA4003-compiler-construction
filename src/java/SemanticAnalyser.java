import java.util.*;
public class SemanticAnalyser implements CCALParserVisitor
{
    private static String scope = "global";
    private static Hashtable<String, LinkedHashSet<String>> duplicates = new Hashtable<>();
    private static HashSet<String> invokedFunctions = new HashSet<>();
    private static SymbolTable ST;
    
  public Object visit(SimpleNode node, Object data) {
     throw new RuntimeException("Visit SimpleNode"); 
  }
  
  private static void setSymbolTable(Object data) {
      ST = (SymbolTable)(data);
  }
  
  public Object visit(Prog node, Object data) {
    setSymbolTable(data);
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    checkForDuplicates();
    checkInvokedFunctions();
    return data;
  }
  
  private void checkInvokedFunctions() {
    ArrayList<String> functions = ST.functionsToList();
    for(int i = 0; i < functions.size(); i++) {
        if(!invokedFunctions.contains(functions.get(i))) {
            System.out.println("ERROR: " + functions.get(i) + " is never invoked");
        }
    }
  }
  
  
  private static void duplicateCheck(String id, String scope) {
    if(!ST.noDuplicates(id, scope)) {
        HashSet<String> dups = duplicates.get(scope);
        if(dups == null) {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            set.add(id);
            duplicates.put(scope, set);
        }
        else {
            dups.add(id);
        } 
    }
    if(!ST.noDuplicates(id, "global")) {
        HashSet<String> dups = duplicates.get(scope);
        if(dups == null) {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            set.add(id);
            duplicates.put(scope, set);
        }
        else {
            dups.add(id);
        } 
    }
  }
  // has two children, ID and TYPE
  public Object visit(VarDec node, Object data) {
    String id = (String)node.jjtGetChild(0).jjtAccept(this, data);   
    String type = (String) node.jjtGetChild(1).jjtAccept(this, data);
    duplicateCheck(id, scope);
    return data;
   
  }
  private static void checkForDuplicates() {
      if(duplicates == null || duplicates.isEmpty()) {
          System.out.println("Program contains no duplicates");
      }
      else {
        Enumeration e = duplicates.keys();
        while(e.hasMoreElements()) {
            String scope = (String) e.nextElement();
            LinkedHashSet<String> dups = duplicates.get(scope);
            Iterator it = dups.iterator();
            System.out.print("ERROR: Multiple declarations of [");
            while(it.hasNext()) {
               System.out.print(" " + it.next());
            }
            
            System.out.println(" ] in " + scope);
      }
    }
  }
  public Object visit(Id node, Object data) {
    return node.value;
  }
  
  public Object visit(ConstDec node, Object data) {
    String id = (String)node.jjtGetChild(0).jjtAccept(this, data);
    duplicateCheck(id, scope);
    String type = (String) node.jjtGetChild(1).jjtAccept(this, data);
    return data;
  }
  
  public Object visit(Main node, Object data) {
    this.scope = "main";
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return data;
  }
  
  public Object visit(Function node, Object data) {
    this.scope = (String) node.jjtGetChild(1).jjtAccept(this, data);
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return data;
  }
  
  public Object visit(Return node, Object data) {
    return data;
  }
  
  public Object visit(FuncReturn node, Object data) {
    return node.value; 
  }
  
  public Object visit(Type node, Object data) {
    return node.value;   
  }
  
  public Object visit(ParameterList node, Object data) {
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return data;  
  }
 // has 3 children 
  public Object visit(Statement node, Object data) {
    if(node.jjtGetNumChildren() > 0) {
        String id = (String)node.jjtGetChild(0).jjtAccept(this, data);
        // functions with no previous statement contain func name as id
        if(ST.isFunction(id)) {
            invokedFunctions.add(id);
        }
        if(isDeclared(id, scope)) {
            String type = ST.getType(id, scope);
            String description = ST.getDescription(id, scope);
            if(description.equals("const")) {
                System.out.println("ERROR: " + id + " is a constant and cannot be redeclared");
            }
            else {
            String rhs = node.jjtGetChild(1).toString();
            if(type.equals("integer")) {
                if(rhs.equals("Num"))
                {
                    node.jjtGetChild(1).jjtAccept(this, data);
                }
                else if(rhs.equals("BoolOp")) {
                    System.out.println("ERROR: Expected type integer instead got boolean");
                }
                else if(rhs.equals("FuncReturn")) {
                    String func_name = (String) node.jjtGetChild(1).jjtAccept(this, data);
                    // check if function is declared in global scope
                    if(!isDeclared(func_name, "global") && !isDeclared(func_name, scope)) {
                        System.out.println(func_name + " is not declared");
                    }     
                    else if(ST.isFunction(func_name)) {
                        invokedFunctions.add(func_name);
                        // get return type of function
                        String func_return = ST.getType(func_name, "global");
                        if(!func_return.equals("integer")) {
                            System.out.println("ERROR: Expected return type of integer instead got " + func_return);
                        }
                
                    int num_args = ST.getParams(func_name);
                    // Statement -> FuncReturn -> ArgList -> children of arglist
                    int actual_args = node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren();
                    // check that the correct number of args is used
                    if(num_args != actual_args) 
                        System.out.println("ERROR: Expected " + num_args + " parameters instead got " + actual_args);
                    else if(num_args == actual_args) {
                        // check that the arguments are of the correct type
                        Node arg_list = node.jjtGetChild(1).jjtGetChild(0);
                        for(int i = 0; i < arg_list.jjtGetNumChildren(); i++) {
                            String arg  = (String)arg_list.jjtGetChild(i).jjtAccept(this, data);
                            // check if argument in arglist is actually declared 
                            if(isDeclared(arg, scope)) {
                                String arg_type = ST.getType(arg, scope);
                                String type_expected = ST.getParamType(i+1, func_name);
                                if(!arg_type.equals(type_expected)) {
                                    System.out.println("ERROR: " + arg + " is of type " + arg_type + " expected type of " + type_expected);
                                }
                            }
                            else {
                                System.out.println("ERROR: " + arg + " is not declared in this scope");
                            }
                        }
                    }
                    }
                }
            }
            else if(type.equals("boolean")) {
              if(rhs.equals("BoolOp"))
                {
                    node.jjtGetChild(1).jjtAccept(this, data);
                }
                else if(rhs.equals("Num")) {
                    System.out.println("ERROR: Expected type boolean instead got integer");
                }
                else if(rhs.equals("FuncReturn")) {
                    String func_name = (String) node.jjtGetChild(1).jjtAccept(this, data);
                    // check if function is declared in global scope
                    if(!isDeclared(func_name, "global")) {
                        System.out.println(func_name + " is not declared");
                    }
                    else {
                        // get return type of function
                        String func_return = ST.getType(func_name, "global");
                        if(!func_return.equals("boolean")) {
                            System.out.println("ERROR: Expected return type of boolean instead got " + func_return);
                        }
                    }
                }
            }
            }
        }
        else if(!isDeclared(id, scope)) {
            System.out.println(id + " " + scope);
            System.out.println("ERROR: " + id + " needs to be declared before use");
        }
        }
    return data;    
  }
  
  private static boolean isDeclared(String id, String scope) {
      LinkedList<String> list = ST.getScopeTable(scope); 
      LinkedList<String> global_list = ST.getScopeTable("global");
      if(list != null) {
          if(!global_list.contains(id) && !list.contains(id)) {
              return false;
          }
      }
      return true;
  }
  
  public Object visit(Assign node, Object data) {
    return data; 
  }
  
  public Object visit(FuncAssign node, Object data) {
    return data;   
  }
  
  public Object visit(Expression node, Object data) {
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return data; 
  }
  
  public Object visit(PlusOp node, Object data) {
    return "+"; 
  }
  
  public Object visit(MinusOp node, Object data) {
      // TO DO
      /*
    if(node.jjtGetNumChildren() > 0) {
        if(!node.jjtGetChild(0).toString().equals("Num")
    }*/
    return "-";  
  }
  
  public Object visit(Num node, Object data) {
    return node.value; 
  }
  
  public Object visit(BoolOp node, Object data) {
    return node.value;   
  }
  
  public Object visit(Func node, Object data) {
    return data;   
  }
  
  public Object visit(EQComp node, Object data) {
    return node.value;   
  }
  
  public Object visit(NEComp node, Object data) {
    return node.value;   
  }
  
  public Object visit(LTComp node, Object data) {
    return node.value;   
  }
  
  public Object visit(LEComp node, Object data) {
    return node.value;  
  }
  
  public Object visit(GTComp node, Object data) {
    return node.value;   
  }
  
  public Object visit(GEComp node, Object data) {
    return node.value;  
  }
  
  public Object visit(ORCondition node, Object data) {
    return node.value;  
  }
  
  public Object visit(ANDCondition node, Object data) {
    return node.value;  
  }
  
  public Object visit(ArgList node, Object data) {
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return data;  
  }
  
  public Object visit(Comparison node, Object data) {
      node.childrenAccept(this, data);
      return node.value;
  }
}