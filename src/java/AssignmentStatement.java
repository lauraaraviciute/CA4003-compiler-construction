/* Generated By:JJTree: Do not edit this line. AssignmentStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class AssignmentStatement extends SimpleNode {
  public AssignmentStatement(int id) {
    super(id);
  }

  public AssignmentStatement(CCALParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CCALParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e9f94540db2509bee86becef0ee2035a (do not edit this line) */
