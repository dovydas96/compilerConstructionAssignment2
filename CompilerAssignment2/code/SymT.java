import java.util.*;

public class SymT
{
	Hashtable<String, LinkedList<String>> stcHash;
	Hashtable<String, String> typeHash;
	Hashtable<String, String> descHash;
	
	SymT() {
		this.stcHash = new Hashtable<>();
		this.typeHash = new Hashtable<>();
		this.descHash = new Hashtable<>();
		
		stcHash.put("program", new LinkedList<>());
	}
	
	public void get(String id, String type, String scope) 
	{
		LinkedList<String> scopeList = stcHash.get(scope);
		if(scopeList == null) 
		{
			System.out.println("Variable " + id + " not declared in " + scope);
		}     
	}

	public String getType(String id, String scope) 
	{
		String type = typeHash.get(id+scope);
		if(type != null) 
		{
			return type;
		}
		else 
		{
			type = typeHash.get(id+"program");
			if(type != null) 
			{
				return type;
			}
		}
		return null;
	}
	
	
	public String getDescription(String id, String scope) 
	{
		String description = descHash.get(id+scope);
		if(description != null) 
			return description;
		else 
		{
			description = descHash.get(id+"program");
			if(description != null) 
			{
				return description;
			}
		}
		return null;
	}

	public LinkedList<String> getScopeTable(String scope) 
	{
		return stcHash.get(scope);
	}

	public int getParams(String id) 
	{
		LinkedList<String> scopeList = stcHash.get(id);
		int count = 0;
		for(int i = 0; i < scopeList.size(); i++) 
		{
			String description = descHash.get(scopeList.get(i)+id);
			if(description.equals("param")) 
			{
				count++;
			}
		}
		return count;
	}
	
	public boolean checkForDups(String id, String scope) 
	{
		LinkedList<String> scopeList = stcHash.get(scope);
		LinkedList<String> programList = stcHash.get("program");
		if(scope.equals("program")) 
		{
			return programList.indexOf(id) == programList.lastIndexOf(id);
		}
		return ((scopeList.indexOf(id) == scopeList.lastIndexOf(id)) && (programList.indexOf(id) == -1));
		
	}

	public String getParamType(int index, String scope) 
	{
		int count = 0;
		LinkedList<String> ids = stcHash.get(scope);
			for(String id : ids) 
			{
				String type = typeHash.get(id+scope);
				String description = descHash.get(id+scope);
				if(description.equals("param")) 
				{
					count++;
					if(count == index) 
					{
						return type;
					}
				}
			}
		return null;
	}
	
	public void put(String id, String type, String information, String scope) 
	{
		LinkedList<String> scopeList = stcHash.get(scope);
		if(scopeList == null) 
		{
			scopeList = new LinkedList<>();
			scopeList.add(id);
			stcHash.put(scope, scopeList);
		}
		else 
		{
			scopeList.addFirst(id);
		}
		typeHash.put(id+scope, type);
		descHash.put(id+scope, information);
	}
	
	public ArrayList<String> getFunctions() 
	{
		LinkedList<String> scopeList = stcHash.get("program");
		ArrayList<String> functions = new ArrayList<String>();
		for(int i = 0; i < scopeList.size(); i++) 
		{
			String description = descHash.get(scopeList.get(i)+"program");
			if(description.equals("function"))
			{
				functions.add(scopeList.get(i));
			}
		}
		return functions;
	}

	public ArrayList<String> getVars() 
	{
		ArrayList<String> vars = new ArrayList<String>();
		Enumeration enumm = stcHash.keys();
		while(enumm.hasMoreElements()) 
		{
			String scope = (String) enumm.nextElement();
			LinkedList<String> scopeList = stcHash.get(scope);
			for(int i = 0; i < scopeList.size(); i++) 
			{
				String description = descHash.get(scopeList.get(i)+scope);
				if(description.equals("var"))
				{
					vars.add(scopeList.get(i));
				}
			}
		}
		return vars;
	}

	public boolean functionCheck(String id) 
	{
		LinkedList<String> scopeList = stcHash.get("program");
		for(int i = 0; i < scopeList.size(); i++) 
		{
				String description = descHash.get(scopeList.get(i)+"program");
				if(description.equals("function") && scopeList.get(i).equals(id)) 
				{
					return true;
				}
		}
		return false;
	}

	public void printSt() 
	{
		Enumeration enumm = stcHash.keys();
		while(enumm.hasMoreElements()) 
		{
			String scope = (String) enumm.nextElement();
			System.out.println("Scope: " + scope);
			LinkedList<String> ids = stcHash.get(scope);
			int num = 0;
			for(String id : ids) 
			{
				num++;
				String type = typeHash.get(id+scope);
				String description = descHash.get(id+scope);
				System.out.println(num + " type: " + type + " id: " + id  + " desc: " + description);
			}
			System.out.println("--------");
		}
	}
	
}