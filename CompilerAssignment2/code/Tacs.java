import java.util.*;

public class Tacs implements CALParserVisitor
{

	private static int identifierNum = 1;

	public Object visit(SimpleNode node, Object data)
	{
		throw new RuntimeException("Visit SimpleNode"); 
	}

	public Object visit(Program node, Object data) 
	{
		int num = node.jjtGetNumChildren();
		for(int i = 0; i < num; i++) 
		{
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}

	public Object visit(Var node, Object data) 
	{
		String id = (String)node.jjtGetChild(0).jjtAccept(this, data);   
		String type = (String) node.jjtGetChild(1).jjtAccept(this, data);
		return data;
	}


	public Object visit(ID node, Object data) 
	{
		return node.value;
	}

	public Object visit(Const node, Object data)
	{
		String id = (String)node.jjtGetChild(0).jjtAccept(this, data);   
		String type = (String) node.jjtGetChild(1).jjtAccept(this, data);
		return data;
	}

	public Object visit(Main node, Object data) 
	{
		System.out.println("main:");
		int num = node.jjtGetNumChildren();
		for(int i = 0; i < num; i++) 
		{
			node.jjtGetChild(i).jjtAccept(this, data);
		}

		System.out.println("  call _exit, 0");
		return data;
	}

	public Object visit(Function node, Object data) 
	{
		String name = (String) node.jjtGetChild(1).jjtAccept(this, data);
		int num = node.jjtGetNumChildren();
		
		System.out.println(name + ": ");
		String ret = "  return ";
		
		for(int i = 0; i < num; i++) 
		{
			if(node.jjtGetChild(i).toString().equals("FuncReturn")) 
			{
				ret += node.jjtGetChild(i).jjtAccept(this, data);
			}
			else 
			{
				node.jjtGetChild(i).jjtAccept(this, data);
			}
		}
		
		System.out.println(ret);
		return data;
	}

	public Object visit(FuncReturn node, Object data) 
	{
		int num = node.jjtGetNumChildren();
		for(int i = 0; i < num; i++) 
		{
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return node.value; 
	}
  
	public Object visit(Type node, Object data) 
	{
		return node.value;   
	}
  
	public Object visit(NParamList node, Object data) 
	{
		int num = node.jjtGetNumChildren();
		for(int i = 0; i < num; i++) 
		{
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;  
	}

	public Object visit(Statement node, Object data) 
	{
		int counter = 1;
		if(node.value != null) 
		{
			if(node.value.equals("if") || node.value.equals("while")) 
			{
				ArrayList<String> ids = new ArrayList<>();
				ArrayList<String> compArray = new ArrayList<>();
				ArrayList<String> conditionArray = new ArrayList<>();
				for(int i = 0; i < node.jjtGetNumChildren(); i++) 
				{
					String nodeVal = node.jjtGetChild(i).toString();					
					if(nodeVal.equals("Comp")) 
					{
						String value = (String)node.jjtGetChild(i).jjtGetChild(0).jjtAccept(this, data);
						String comp = (String)node.jjtGetChild(i).jjtGetChild(1).jjtAccept(this, data);
						String comparison =  comp + " " + value;
						compArray.add(comparison);
						counter++;
					}
					else if(nodeVal.equals("AndCon")) 
					{
						conditionArray.add((String)node.jjtGetChild(i).jjtAccept(this, data));
						counter++;
					}
					else if(nodeVal.equals("OrCon"))
					{
						conditionArray.add((String)node.jjtGetChild(i).jjtAccept(this, data));
						counter++;
					}
					else if(nodeVal.equals("FuncReturn")) 
					{
						ids.add((String)node.jjtGetChild(i).jjtAccept(this, data));
						counter++;
					}
				}
				String ans = "";
				for(int i = 0; i < ids.size(); i++)
				{
					ans += ids.get(i) + " ";
					if(compArray.size() > i) 
					{
						ans += compArray.get(i);
					}
					if(conditionArray.size() > i) 
					{
						ans +=  " " +  conditionArray.get(conditionArray.size()-i-1) + " ";
					}
				}
				System.out.println(" " + node.value + " " + "(" + ans + ")" + " goto l" + identifierNum);
				System.out.println(" l" + identifierNum+ ":");
				identifierNum++;
			} 
		}
		int num = node.jjtGetNumChildren();
		if(num > 0) 
		{
			String child = node.jjtGetChild(counter).toString();
			String id = (String) node.jjtGetChild(counter-1).jjtAccept(this, data);
			if(child.equals("FuncReturn")) 
			{
				int nodeVal = node.jjtGetChild(counter).jjtGetNumChildren();
				if(nodeVal > 0) 
				{
					String name = (String) node.jjtGetChild(counter).jjtAccept(this, data);
					
					int children = node.jjtGetChild(counter).jjtGetChild(counter-1).jjtGetNumChildren();
					Node childN = node.jjtGetChild(counter).jjtGetChild(counter-1);
					int paramCount = 0;
					for(int i = 0; i < children; i++)
					{
						String param = (String) childN.jjtGetChild(i).jjtAccept(this, data);
						System.out.println("  param " + param);
						paramCount++;
					}
					System.out.println("  " + id + " = call " + name + ", " + paramCount);
				}
				else 
				{
					String ids = (String) node.jjtGetChild(0).jjtAccept(this, data);
					String ans = id + " = ";
					for(int i = 1; i < node.jjtGetNumChildren() - 1; i++)
					{
						ans += " " + node.jjtGetChild(i).jjtAccept(this, data);
					}
					System.out.println("  " + ans);
				}
			}        
			else if(child.equals("ArgList")) 
			{
			   System.out.println("  goto " +  id);
			}
			else 
			{
				String value = (String) node.jjtGetChild(counter).jjtAccept(this, data);
				if(id != null && value != null) 
				{
					System.out.println("  " + id + " = " + value); 
				}
			} 
		}
		return node.value;
	}

	public Object visit(OrCon node, Object data) 
	{
		return node.value;  
	}


	public Object visit(AndCon node, Object data) 
	{
		return node.value;  
	}

	public Object visit(Assign node, Object data) 
	{
		return data; 
	}

	public Object visit(Number node, Object data) 
	{
		return node.value; 
	}

	public Object visit(ArgAssign node, Object data) 
	{
		return data;   
	}

	public Object visit(Add node, Object data) 
	{
		return "+"; 
	}

	public Object visit(Subtract node, Object data) 
	{
		if(node.jjtGetNumChildren() > 0) 
		{
			return "-" + node.jjtGetChild(0).jjtAccept(this, data);
		}
		else 
			return "-"; 
	}

	public Object visit(Bool node, Object data) 
	{
		return node.value;   
	}

	public Object visit(Equal node, Object data) 
	{
		return node.value;   
	}

	public Object visit(NotEqual node, Object data) 
	{
		return node.value;   
	}
  
	public Object visit(LessThan node, Object data) 
	{
		return node.value;   
	}

	public Object visit(LessThanEqualTo node, Object data) 
	{
		return node.value;  
	}

	public Object visit(GreaterThan node, Object data) 
	{
		return node.value;   
	}

	public Object visit(GreaterThanEqualTo node, Object data) 
	{
		return node.value;  
	}

	public Object visit(ArgList node, Object data) 
	{
		int num = node.jjtGetNumChildren();
		for(int i = 0; i < num; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return node.value; 
	}

	public Object visit(Comp node, Object data) 
	{
		node.childrenAccept(this, data);
		return node.value;
	}
}
