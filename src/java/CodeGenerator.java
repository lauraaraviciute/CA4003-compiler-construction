import java.util.*;
public class CodeGenerator implements CCALParserVisitor
{
    private static int labelNumber = 1;
    
  public Object visit(SimpleNode node, Object data) {
     throw new RuntimeException("Visit SimpleNode"); 
  }
  
  public Object visit(Prog node, Object data) {
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    return data;
  }
  // has two children, ID and TYPE
  public Object visit(VarDec node, Object data) {
    String id = (String)node.jjtGetChild(0).jjtAccept(this, data);   
    String type = (String) node.jjtGetChild(1).jjtAccept(this, data);
    if(node.jjtGetParent().toString().equals("Prog")) {
        System.out.println("VAR\t" + id);
    }
    return data;
   
  }
  public Object visit(Id node, Object data) {
    return node.value;
  }
  
  public Object visit(ConstDec node, Object data) {
    String id = (String)node.jjtGetChild(0).jjtAccept(this, data);   
    String type = (String) node.jjtGetChild(1).jjtAccept(this, data);
    String num = (String) node.jjtGetChild(2).jjtAccept(this, data);
    if(node.jjtGetParent().toString().equals("Prog")) {
        System.out.println("CONST\t" + id + "\t=\t" + num);
    }
    return data;
  }
  
  public Object visit(Main node, Object data) {
    System.out.println("MAIN:");
    System.out.println("\tbegin ");
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
    System.out.println("\tend");
    return data;
  }
  
  public Object visit(Function node, Object data) {
    String func_name = (String) node.jjtGetChild(1).jjtAccept(this, data);
    int num = node.jjtGetNumChildren();
    // number of parameters
    int bytes = node.jjtGetChild(2).jjtGetNumChildren() * 4;
    
    System.out.println(func_name.toUpperCase() + ":\t");
    System.out.println("\tbegin ");
    String ret = "\t\treturn ";
    
    for(int i = 0; i < num; i++) {
        // append return values
        if(node.jjtGetChild(i).toString().equals("FuncReturn")) {
            ret += node.jjtGetChild(i).jjtAccept(this, data);
        }
        else {
            node.jjtGetChild(i).jjtAccept(this, data);
        }
    }
    
    System.out.println(ret);
    // pop parameters
    System.out.println("\t\tpop " + bytes);
    System.out.println("\tend ");
    return data;
  }
  
  public Object visit(Comparison node, Object data) {
      node.childrenAccept(this, data);
      return node.value;
  }
  
  public Object visit(Expression node, Object data) {
      System.out.println("expression children " + node.jjtGetNumChildren());
      int num = node.jjtGetNumChildren();
      for(int i = 0; i < num; i++) {
          System.out.println(node.jjtGetChild(i).jjtAccept(this, data));
      }
      return node.value;
  }
  
  public Object visit(Return node, Object data) {
    return "";
  }
  
  public Object visit(FuncReturn node, Object data) {
    int num = node.jjtGetNumChildren();
    for(int i = 0; i < num; i++) {
        node.jjtGetChild(i).jjtAccept(this, data);
    }
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
 
  public Object visit(Statement node, Object data) {
    int next = 1;
    if(node.value != null) {
        if(node.value.equals("if") || node.value.equals("while")) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> comps = new ArrayList<>();
        ArrayList<String> conds = new ArrayList<>();
        for(int i = 0; i < node.jjtGetNumChildren(); i++) {
            String n = node.jjtGetChild(i).toString();
            if(n.equals("FuncReturn")) {
                ids.add((String)node.jjtGetChild(i).jjtAccept(this, data));
                next++;
            }
            else if(n.equals("Comparison")) {
                String value = (String)node.jjtGetChild(i).jjtGetChild(0).jjtAccept(this, data);
                String comp = (String)node.jjtGetChild(i).jjtGetChild(1).jjtAccept(this, data);
                String comparison =  comp + " " + value;
                comps.add(comparison);
                next++;
            }
            else if(n.equals("ANDCondition") || n.equals("ORCondition")) {
                conds.add((String)node.jjtGetChild(i).jjtAccept(this, data));
                next++;
            }
        }
        // populate result
        String result = "";
        for(int i = 0; i < ids.size(); i++)
        {
            result += ids.get(i) + " ";
            if(comps.size() > i) {
                result += comps.get(i);
            }
            if(conds.size() > i) {
                // condition statements are amended in reverse order so we traverse from the end
                result +=  " " +  conds.get(conds.size()-i-1) + " ";
            }
        }
        System.out.println("\t" + node.value + "\t" + "(" + result.trim() + ")" + " goto label" + labelNumber);
        System.out.println("\tlabel" + labelNumber + ":");
        labelNumber++;
       } 
    }       
    int num = node.jjtGetNumChildren();
    // make sure its not a blank statement
    if(num > 0) {
        String childNode = node.jjtGetChild(next).toString();
        String id = (String) node.jjtGetChild(next-1).jjtAccept(this, data);
        if(childNode.equals("FuncReturn")) {
            int n = node.jjtGetChild(next).jjtGetNumChildren();
            if(n > 0) {
                String func_name = (String) node.jjtGetChild(next).jjtAccept(this, data);
                
                int children = node.jjtGetChild(next).jjtGetChild(next-1).jjtGetNumChildren();
                Node child = node.jjtGetChild(next).jjtGetChild(next-1);
                int param_count = 0;
                for(int i = 0; i < children; i++)
                {
                    String param = (String) child.jjtGetChild(i).jjtAccept(this, data);
                    System.out.println("\t\tparam\t" + param);
                    param_count++;
                }
                System.out.println("\t\t" + id + "\t=\tcall " + func_name + ", " + param_count);
            }
            else {
                // must be an operation
                printOperation(node, data);
            }
        }        
        else if(childNode.equals("ArgList")) {
            int children = node.jjtGetChild(next).jjtGetNumChildren();
            for(int i = 0; i < children; i++) {
                String param = (String)node.jjtGetChild(next).jjtGetChild(i).jjtAccept(this, data);
                System.out.println("\t\tpush\t" + param);
            }
           System.out.println("\t\tgoto\t" +  id);
        }
        else {
            String value = (String) node.jjtGetChild(next).jjtAccept(this, data);
            
            if(id != null && value != null) {
                System.out.println("\t\t" + id + "\t= " + value); 
            }
        } 
      }
    return node.value;   
    
  }
  
  private void printOperation(Statement node, Object data) {
    String id = (String) node.jjtGetChild(0).jjtAccept(this, data);
    // last child will be Assign
    String result = id + " = ";
    for(int i = 1; i < node.jjtGetNumChildren() - 1; i++)
    {
        result += " " + node.jjtGetChild(i).jjtAccept(this, data);
    }
    System.out.println("\t\t" + result);
  }
  public Object visit(Assign node, Object data) {
    return data; 
  }
  
  public Object visit(FuncAssign node, Object data) { 
    return data;   
  }
  
  public Object visit(PlusOp node, Object data) {
    return "+" ; 
  }
  
  public Object visit(MinusOp node, Object data) {
    if(node.jjtGetNumChildren() > 0) 
    {
        return "-" + node.jjtGetChild(0).jjtAccept(this, data);
    }
    else 
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
    return node.value;  
  }
}