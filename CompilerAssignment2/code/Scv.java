import java.util.*;

public class Scv implements CALParserVisitor
{

	static String scope = "program";
	static Hashtable<String, LinkedHashSet<String>> duplicates = new Hashtable<>();
	static HashSet<String> declaredFuncs = new HashSet<>();
	static HashSet<String> readVars = new HashSet<>();
	static SymT ST;

	static int numErrors = 0; 


	public Object visit(SimpleNode node, Object data)
	{
		throw new RuntimeException("Visit SimpleNode"); 
	}

	public Object visit(Program node, Object data) 
	{
		ST = (SymT) data;
		int num = node.jjtGetNumChildren();
		for(int i = 0; i < num; i++) 
		{
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		multipleDeclarations();
		checkInvokedFunctions();
		checkUsedVars();
		if(numErrors == 0)
		{
			System.out.println("No errors found");
		}
		else
		{
			System.out.println(numErrors + " errors found");
		}
		return data;
	}

	public static void multipleDeclarations() 
	{
		Enumeration enumm = duplicates.keys();
		while(enumm.hasMoreElements()) 
		{
			String scope = (String) enumm.nextElement();
			LinkedHashSet<String> dups = duplicates.get(scope);
			Iterator it = dups.iterator();
			System.out.print("Error found: Multiple declarations found for: ");
			while(it.hasNext()) 
			{
			   System.out.print(it.next());
			   numErrors++;
			}
			
			System.out.println(" in " + scope);
		}
	}

	public void checkInvokedFunctions() 
	{
		ArrayList<String> functions = ST.getFunctions();
		for(int i = 0; i < functions.size(); i++) 
		{
			if(!declaredFuncs.contains(functions.get(i))) 
			{
				System.out.println("Error found: " + functions.get(i) + " is declared but never used");
				numErrors ++;
			}
		}
	}


	public static void checkUsedVars()
	{
		ArrayList<String> vars = ST.getVars();
		for(int i = 0; i < vars.size(); i++) 
		{
			if(!readVars.contains(vars.get(i))) 
			{
				System.out.println("Error found: " + vars.get(i) + " is declared but never used");
				numErrors ++;
			}
		}
	}


	public static void duplicateCheck(String id, String scope) 
	{
		if(!ST.checkForDups(id, scope)) 
		{
			HashSet<String> dups = duplicates.get(scope);
			if(dups == null) 
			{
				LinkedHashSet<String> set = new LinkedHashSet<>();
				set.add(id);
				duplicates.put(scope, set);
			}
			else
			{
				dups.add(id);
			} 
		}
	}

	public Object visit(Var node, Object data) 
	{
		String id = (String)node.jjtGetChild(0).jjtAccept(this, data);   
		String type = (String) node.jjtGetChild(1).jjtAccept(this, data);
		duplicateCheck(id, scope);
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
		duplicateCheck(id,scope);
		return data;
	}

	public Object visit(Main node, Object data) 
	{
		this.scope = "main";
		int num = node.jjtGetNumChildren();
		for(int i = 0; i < num; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}

	public Object visit(Function node, Object data) 
	{
		this.scope = (String) node.jjtGetChild(1).jjtAccept(this, data);
		int num = node.jjtGetNumChildren();
		for(int i = 0; i < num; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}

		String id = (String)node.jjtGetChild(num-1).jjtAccept(this,data);
		String returnId = ST.getType(id,this.scope);
		String funcId = (String) node.jjtGetChild(0).jjtAccept(this, data);

		if( returnId == null)
		{
			String funcName = (String) node.jjtGetChild(1).jjtAccept(this, data);
			System.out.println("Error found: Missing variable declarations after IS in " + funcName);
			numErrors++;
			return data;
		}
		if(!returnId.equals(funcId))
		{
			System.out.println("Error found: function should return type: "+funcId+" but got type: "+returnId);
			numErrors++;
		}
		return data;
	}

	public Object visit(Type node, Object data) 
	{
		return node.value;   
	}
  
	public Object visit(NParamList node, Object data) 
	{
		int num = node.jjtGetNumChildren();
		for(int i = 0; i < num; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;  
	}

	public static boolean declared(String id, String scope) 
	{
		LinkedList<String> scopeList = ST.getScopeTable(scope); 
		LinkedList<String> programList = ST.getScopeTable("program");
		if(scopeList != null) 
		{
			if(!programList.contains(id) && !scopeList.contains(id)) 
			{
				return false;
			}
		}
	  return true;
	}
  

	public Object visit(Statement node, Object data) 
	{
		if(node.jjtGetNumChildren() > 0) 
		{
			String id = (String)node.jjtGetChild(0).jjtAccept(this, data);
			// functions with no previous statement contain func name as id
			if(ST.functionCheck(id)) 
			{
				 declaredFuncs.add(id);
			}
			if(declared(id, scope)) 
			{
				String type = ST.getType(id, scope);
				String description = ST.getDescription(id, scope);
				if(description == null)
				{
					return data;
				}
				if(description.equals("const")) 
				{
					System.out.println("Error found: " + id + " is a constant and cannot be redeclared");
					numErrors ++;
				}
				else 
				{
					String rightVal = node.jjtGetChild(1).toString();
					if(type.equals("integer")) 
					{
						readVars.add(id);
						if(rightVal.equals("Number"))
						{
							node.jjtGetChild(1).jjtAccept(this, data);
						}
						else if(rightVal.equals("Bool")) 
						{
							System.out.println("Expected type integer instead got boolean");
							numErrors++;
						}
						else if(rightVal.equals("FuncReturn")) 
						{

							String functionName = (String) node.jjtGetChild(1).jjtAccept(this, data);
							declaredFuncs.add(functionName);
							if(!declared(functionName, "program") && !declared(functionName, scope)) 
							{
								System.out.println(functionName + " is not declared");
							}     
							else if(ST.functionCheck(functionName)) 
							{
								String functionReturn = ST.getType(functionName, "program");
								if(!functionReturn.equals("integer")) 
								{
									System.out.println("Error found: Expected return type of integer instead got " + functionReturn);
									numErrors++;
								}
							
								int numArgs = ST.getParams(functionName);
								int totalArags = node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren();
								if(numArgs != totalArags)
								{
									System.out.println("Error found: Expected " + numArgs + " parameters instead got " + totalArags);
									numErrors++;
								}
								else if(numArgs == totalArags) 
								{
									Node argList = node.jjtGetChild(1).jjtGetChild(0);
									for(int i = 0; i < argList.jjtGetNumChildren(); i++) 
									{
										String arg  = (String)argList.jjtGetChild(i).jjtAccept(this, data);
										if(declared(arg, scope)) 
										{
											String argType = ST.getType(arg, scope);
											String typeExpected = ST.getParamType(i+1, functionName);
											if (argType == null)
											{
												System.out.println("Error found: Missing variable declarations after BEGIN in MAIN");
												numErrors++;
												return data;
											}
											if(!argType.equals(typeExpected)) 
											{
												System.out.println("Error found: " + arg + " is of type " + argType + " expected type of " + typeExpected);
												numErrors++;
											}
										}
										else {
											System.out.println("Error found: " + arg + " is not declared in this scope");
											numErrors++;
										}
									}
								}
							}
						}
					}
					else if(type.equals("boolean"))
					{
						readVars.add(id);
						if(rightVal.equals("Bool"))
						{
							node.jjtGetChild(1).jjtAccept(this, data);
						}
						else if(rightVal.equals("Number")) 
						{
							System.out.println("Error found: Expected type boolean instead got integer");
							numErrors++;

						}
						else if(rightVal.equals("FuncReturn")) 
						{
							String functionName = (String) node.jjtGetChild(1).jjtAccept(this, data);
							declaredFuncs.add(functionName);
							if(!declared(functionName, "program")) 
							{
								System.out.println(functionName + " is not declared");
							}

							else if(ST.functionCheck(functionName)) 
							{
								String functionReturn = ST.getType(functionName, "program");
								if(!functionReturn.equals("boolean")) 
								{
									System.out.println("Error found: Expected return type of integer instead got " + functionReturn);
									numErrors++;
								}							
								int numArgs = ST.getParams(functionName);
								int totalArags = node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren();
								if(numArgs != totalArags)
								{
									System.out.println("Error found: Expected " + numArgs + " parameters instead got " + totalArags);
									numErrors++;
								}
								else if(numArgs == totalArags) 
								{
									Node argList = node.jjtGetChild(1).jjtGetChild(0);
									for(int i = 0; i < argList.jjtGetNumChildren(); i++) 
									{
										String arg  = (String)argList.jjtGetChild(i).jjtAccept(this, data); 
										if(declared(arg, scope)) 
										{
											String argType = ST.getType(arg, scope);
											String typeExpected = ST.getParamType(i+1, functionName);
											if(!argType.equals(typeExpected)) 
											{
												System.out.println("Error found: " + arg + " is of type " + argType + " expected type of " + typeExpected);
												numErrors++;
											}
										}
										else {
											System.out.println("Error found: " + arg + " cannot be used as argument, not declared in this scope");
											numErrors++;
										}
									}
								}
							}
						}
					} 
				}
			}
			else 
			{
				System.out.println("Error found: " + id + " needs to be declared before use in " + scope );
				numErrors++;
			}
		}
		return data;    
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

	public Object visit(FuncReturn node, Object data)
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
		for(int i = 0; i < num; i++) 
		{
		node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;  
	}
  
	public Object visit(Comp node, Object data) 
	{
		node.childrenAccept(this, data);
		return node.value;
	}
}
