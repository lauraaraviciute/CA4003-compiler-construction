/* Generated By:JJTree: Do not edit this line. MinusOp.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class MinusOp extends SimpleNode {
  public MinusOp(int id) {
    super(id);
  }

  public MinusOp(CCALParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CCALParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1d2e7e8b6e6c4c35c4bd89388169714a (do not edit this line) */
