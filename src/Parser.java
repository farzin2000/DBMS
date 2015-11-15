import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Parser
{
	//	static Stack<String> parseTree;
	//	static LinkedList<String> operand = new LinkedList<>();
	//	static
	//	{
	//		operand.add("(");
	//		operand.add("&");
	//		operand.add("|");
	//		operand.add("<=");
	//		operand.add(">=");
	//		operand.add("=");
	//		operand.add(">");
	//		operand.add("<");
	//		operand.add("@");	// True value
	//		operand.add("#");	// False value
	//	}
	public static String parseQuery(String query)
	{
		String output = "";
		query = query.replace(";", "");	
		String[] tokens = query.split(" ");
		
		if(tokens[0].equals("CREATE"))
		{
			if(tokens[1].equals("TABLE"))
			{
				LinkedList<String> headers = new LinkedList<>();
				String tableName = tokens[2].split("\\(")[0];
				String[] cols = query.split("\\(");
//				System.out.println(cols[1]);
				cols[1] = cols[1].replace(")", "");
				String[] colNames = cols[1].split(",");
				for (int i = 0; i < colNames.length; i++) {
					headers.add(colNames[i].split(" ")[0]);
				}
				Table table = new Table(tableName, headers, null);
				TableMgr.getInstance().addTable(table);
				output = "TABLE CREATED";
//				return output;
			}
			else if(tokens[1].equals("INDEX"))
			{
				String indexName = tokens[2]; 
				String tableName = tokens[4].split("\\(")[0];
				String col = tokens[4].split("\\(")[1];
				TableMgr.getInstance().getTables().get(tableName).createIndex(indexName, col);
				output = "INDEX CREATED";
			}
		}
		else if(tokens[0].equals("INSERT"))
		{
			output = parseINSERT(tokens[4], tokens[2]);
		}
		else if(tokens[0].equals("UPDATE"))
		{
			output = parseUPDATE(query);
		}
		else if(tokens[0].equals("DELETE"))
		{
			output = parseDELETE(query);
		}
		else if(tokens[0].equals("SELECT"))
		{
			output = parseSELECT(query);
		}

		return output;
	}

	private static String parseSELECT(String query)
	{
		String[] tokens = query.split(" ");
		LinkedList<String> cols = new LinkedList<>();
		for (String string : tokens[1].split(",")) 
		{
			cols.add(string);
		}
		System.out.println(cols);
		String tableName = tokens[3];
		String whereClause = "";
		for (int i = 5; i < tokens.length; i++) 
		{
			if(i == tokens.length - 1)
				whereClause = whereClause + tokens[i];
			else
				whereClause = whereClause + tokens[i] + " ";
		}
		Table selectedTable = TableMgr.getInstance().getTables().get(tableName);
		WhereRecursive wr = new WhereRecursive(selectedTable);
		wr.ReadCommand(whereClause, selectedTable).select(cols);
		return "";
	}
	private static String parseUPDATE(String query)
	{
		String[] tokens = query.split(" ");
		String tableName = tokens[1];
		String updatable = tokens[3];
		String colName = updatable.split("=")[0];
		String computeValue = updatable.split("=")[1];
		String whereClause = "";
		for (int i = 5; i < tokens.length; i++) 
		{
			if(i == tokens.length - 1)
				whereClause = whereClause + tokens[i];
			else
				whereClause = whereClause + tokens[i] + " ";
		}
		Table selectedTable = TableMgr.getInstance().getTables().get(tableName);
		WhereRecursive wr = new WhereRecursive(selectedTable);
		selectedTable.update(wr.ReadCommand(whereClause, selectedTable), colName, computeValue);
		return "";
		
	}
	private static String parseDELETE(String query)
	{
		String[] tokens = query.split(" ");
		String tableName = tokens[2];
		String deletable = tokens[4];
		Table selectedTable = TableMgr.getInstance().getTables().get(tableName);
		WhereRecursive wr = new WhereRecursive(selectedTable);
		selectedTable.delete(wr.ReadCommand(deletable, selectedTable));
		return "";
	}
	
	private static String parseINSERT(String query, String tableName)
	{
		Table selectedTable = TableMgr.getInstance().getTables().get(tableName);
		Row r = new Row();
		HashMap<String, String> cols = new HashMap<>();
		int index = 0;
		
		for (String colName : selectedTable.getHeader()) 
		{
			query = query.replace("(", "").replace(")", "");
//			System.out.println(query);
			cols.put(colName, query.split(",")[index].replace("\"", ""));
			index++;
		}
		r.setColumns(cols);
		selectedTable.insert(r);
		return "RECORD INSERTED";
	}

	//	public static ArrayList<Boolean> parseWHEREClause(String subQuery, Table table)
	//	{
	//		parseTree = new Stack<>();
	//		subQuery = subQuery.replace(" OR ", "|");
	//		subQuery = subQuery.replace(" AND ", "&");
	//		subQuery = subQuery.replace("NOT ", "!");
	//		//		System.out.println(subQuery.length());
	//		ArrayList<Boolean> selectedRows = new ArrayList<>();
	//
	//		for (int i = 0; i < table.getRows().size(); i++) 
	//		{
	//			Row row = table.getRows().get(i);
	//			boolean done = false;
	//			int index = 0;
	//			while(!done)
	//			{
	//				//				System.out.println(row);
	//				System.out.println(parseTree);
	//				char character = subQuery.charAt(index);
	//				if(operand.contains(String.valueOf(character)))
	//				{
	//					parseTree.push(String.valueOf(character));
	//
	//					index++;
	//				}
	//				else if(character == ')')
	//				{
	//					if(parseTree.size() > 2)
	//					{
	//						String firstPop = parseTree.pop();
	//						String operator = parseTree.pop();
	//						String secondPop = parseTree.pop();
	//						parseTree.pop();
	//						switch (operator) {
	//						case "&":
	//							if(firstPop == "@" && secondPop == "@")
	//								parseTree.push("@");
	//							else
	//								parseTree.push("#");
	//
	//						case "|":
	//							if(firstPop == "#" && secondPop == "#")
	//								parseTree.push("#");
	//							else
	//								parseTree.push("@");
	//							break;
	//
	//						default:
	//							break;
	//						}
	//						index++;
	//					}
	//				}
	//				else
	//				{
	//					int tempIndex = 0;
	//					char [] varOrValue = new char[128];
	//
	//					while((!operand.contains(String.valueOf(subQuery.charAt(index))) && subQuery.charAt(index) != ')') )
	//					{
	//						varOrValue[tempIndex] = subQuery.charAt(index);
	//						tempIndex++;
	//						index++;
	//						//						varOrValue[tempIndex] = subQuery.charAt(index);
	//					}
	//
	//					if(operand.contains(String.valueOf(subQuery.charAt(index))))
	//						parseTree.push(String.valueOf(varOrValue).trim());
	//					else
	//					{
	//
	//						String computed = computeValue(String.valueOf(varOrValue).trim(), row, table.getHeader());
	//						String operator = parseTree.pop();
	//						String colNameValue = parseTree.pop();
	//						int colNameValueInt = 0;
	//						int computedInt = 0;
	//
	//						for (int j = 0; j < table.getHeader().size(); j++) 
	//						{
	//							colNameValue = colNameValue.replaceFirst(table.getHeader().get(j), row.getColumns().get(table.getHeader().get(i)));
	//						}
	//						try			//integer compare case
	//						{
	//							colNameValueInt = Integer.parseInt(colNameValue);
	//							computedInt = Integer.parseInt(computed);
	//							switch (operator) 
	//							{
	//							case ">=":
	//								if(colNameValueInt >= computedInt)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							case "<=":
	//								if(colNameValueInt <= computedInt)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							case "=":
	//								if(colNameValueInt == computedInt)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							case ">":
	//								if(colNameValueInt > computedInt)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							case "<":
	//								if(colNameValueInt < computedInt)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							default:
	//								break;
	//							}
	//						}
	//						catch(Exception e)		//String compare case:
	//						{
	//							switch (operator) 
	//							{
	//							case ">=":
	//								if(colNameValue.compareTo(computed) >= 0)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							case "<=":
	//								if(colNameValue.compareTo(computed) <= 0)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							case "=":
	//								if(colNameValue.compareTo(computed) == 0)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							case ">":
	//								if(colNameValue.compareTo(computed) > 0)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							case "<":
	//								if(colNameValue.compareTo(computed) < 0)
	//									parseTree.push("@");	//push True;
	//								else
	//									parseTree.push("#");	//push False;
	//								break;
	//							default:
	//								break;
	//							}
	//						}
	//						String top = parseTree.peek();
	//						parseTree.pop();
	//						parseTree.pop();
	//						parseTree.push(top);
	//						if(index != subQuery.length() - 1)
	//							index++;
	//					}
	//
	//				}
	//				System.out.println("index: "+index);
	//				if(index == subQuery.length())
	//					done = true;
	//			}
	//			if(parseTree.size() >= 2)
	//			{
	//				String firstPop = parseTree.pop();
	//				String operator = parseTree.pop();
	//				String secondPop = parseTree.pop();
	//
	//				switch (operator) {
	//				case "&":
	//					if(firstPop == "@" && secondPop == "@")
	//						parseTree.push("@");
	//					else
	//						parseTree.push("#");
	//
	//				case "|":
	//					if(firstPop == "#" && secondPop == "#")
	//						parseTree.push("#");
	//					else
	//						parseTree.push("@");
	//					break;
	//
	//				default:
	//					break;
	//				}
	//			}
	//			if(parseTree.pop() == "@")
	//				selectedRows.add(new Boolean(true));
	//			else
	//				selectedRows.add(new Boolean(false));
	//
	//		}
	//		return selectedRows;
	//
	//	}

	public static String computeValue(String computable, Row row, LinkedList<String> tableHeader)
	{
		String out = "";
		//		if(!computable.contains("\""))
		for (int i = 0; i < tableHeader.size(); i++) 
		{
			//				if(Character.is)
			//							System.out.println(row);
			computable = computable.replaceFirst(tableHeader.get(i), row.getColumns().get(tableHeader.get(i)));

		}
		//TODO: minus is not considered!
		if(Character.isDigit(computable.charAt(0)))
		{
			if(!computable.contains("+")&&!computable.contains("-")&&!computable.contains("*")&&!computable.contains("/")) {
				List<String>oneNum=new ArrayList<>();
				int count=0;
				while(count!=computable.length()) {
					oneNum.add(computable.charAt(count)+"");
					count++;
				}
				StringBuilder listString = new StringBuilder();
				for (String s : oneNum)
					listString.append(s+"");
				return Integer.parseInt(listString.toString())+"";
			}
			int count=0;
			Integer num=null;
			char lastOperand = 0;
			List<String>oneNum=new ArrayList<>();
			while(count!=computable.length()+1) {
				if(count!=computable.length()&&Character.isDigit(computable.charAt(count))) {
					oneNum.add(computable.charAt(count)+"");
					count++;
					continue;
				}
				else if(num==null) {
					StringBuilder listString = new StringBuilder();
					for (String s : oneNum)
						listString.append(s+"");
					num=Integer.parseInt(listString.toString());
					lastOperand=computable.charAt(count);
					count++;
					oneNum=new ArrayList<>();
				}
				else {
					StringBuilder listString = new StringBuilder();
					for (String s : oneNum)
						listString.append(s+"");
					switch (lastOperand) {
					case '+':
						num+=Integer.parseInt(listString.toString());
						break;
					case '-':
						num-=Integer.parseInt(listString.toString());
						break;
					case '*':
						num*=Integer.parseInt(listString.toString());
						break;
					case '/':
						num/=Integer.parseInt(listString.toString());
						break;
					}
					if(count==computable.length())
						break;
					lastOperand=computable.charAt(count);
					count++;
					oneNum=new ArrayList<>();
				}
			}
			out=String.valueOf(num);
		}
		else
		{
			String[] splited = computable.split("\\+");
			for (int i = 0; i < splited.length; i++) 
			{
				out += splited[i];
			}
		}
		out = out.replace("\"", "");
		return out;
	}

	public static boolean isRowDependent(String computable, LinkedList<String> tableHeader)
	{
		for (int i = 0; i < tableHeader.size(); i++) 
		{
			if(computable.contains(tableHeader.get(i)))
			{
				return true;
			}
		}
		return false;
	}
	//
	//	public static void main(String[] args) {
	//		String a="12+13";
	//		//		for (String str : "asd+asdf+12".split("+")) 
	//		//		{
	//		//			System.out.println(str);
	//		//		}
	//
	//		LinkedList<String> header = new LinkedList<String>();
	//		header.add("A");
	//		header.add("B");
	//		Row row = new Row();
	//		row.getRow().add("1");
	//		row.getRow().add("4");
	//		Table table = new Table("test", header, null);
	//		table.setHeader(header);
	//		LinkedList<Row> rows = new LinkedList<>();
	//		rows.add(row);
	//		table.setRows(rows);
	//		System.out.println(parseWHEREClause("((B=1) OR (B=2)) AND (A=1)", table));
	//		//		String str1 = "09";
	//		//		String str2 = "10";
	//		//		System.out.println(str1.compareTo(str2));
	//		//		System.out.println(computeValue(str, row, header));
	//	}
}
