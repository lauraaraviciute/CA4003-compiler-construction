/* Generated By:JJTree: Do not edit this line. VarDec.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class VarDec extends SimpleNode {
  public VarDec(int id) {
    super(id);
  }

  public VarDec(CCALParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CCALParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=7b78f8c761e664ae9f479791bf4f6864 (do not edit this line) */
