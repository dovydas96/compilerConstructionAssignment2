/* Generated By:JavaCC: Do not edit this line. CALParserVisitor.java Version 5.0 */
public interface CALParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(Program node, Object data);
  public Object visit(Var node, Object data);
  public Object visit(Const node, Object data);
  public Object visit(Function node, Object data);
  public Object visit(Type node, Object data);
  public Object visit(NParamList node, Object data);
  public Object visit(Main node, Object data);
  public Object visit(Statement node, Object data);
  public Object visit(Assign node, Object data);
  public Object visit(ArgAssign node, Object data);
  public Object visit(Add node, Object data);
  public Object visit(Subtract node, Object data);
  public Object visit(FuncReturn node, Object data);
  public Object visit(AndCon node, Object data);
  public Object visit(OrCon node, Object data);
  public Object visit(Comp node, Object data);
  public Object visit(Equal node, Object data);
  public Object visit(NotEqual node, Object data);
  public Object visit(LessThan node, Object data);
  public Object visit(LessThanEqualTo node, Object data);
  public Object visit(GreaterThan node, Object data);
  public Object visit(GreaterThanEqualTo node, Object data);
  public Object visit(ArgList node, Object data);
  public Object visit(ID node, Object data);
  public Object visit(Number node, Object data);
  public Object visit(Bool node, Object data);
}
/* JavaCC - OriginalChecksum=016a3a6b83bd4cd0e9894fbb30fcc8e4 (do not edit this line) */
