/* Generated By:JJTree: Do not edit this line. NParamList.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class NParamList extends SimpleNode {
  public NParamList(int id) {
    super(id);
  }

  public NParamList(CALParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(CALParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=02ada5b2fb6fbf9d0184ab6a1162652e (do not edit this line) */
