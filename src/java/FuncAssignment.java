/* Generated By:JJTree: Do not edit this line. FuncAssignment.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class FuncAssignment extends SimpleNode {
  public FuncAssignment(int id) {
    super(id);
  }

  public FuncAssignment(CCALParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CCALParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f1977d301c592e431a70dc7f9dbe6726 (do not edit this line) */
