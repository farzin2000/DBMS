import java.util.HashMap;
import java.util.LinkedList;

public class WhereRecursive {

	static Table table;

	public WhereRecursive(Table t)
	{
		this.table = t;
	}
	public Table ReadCommand(String com, Table table) throws NullPointerException {
		if(com.equals(""))
			return table;
		int len = com.length();

		int openPr = 0;
		for (int i = 0; i < len; i++)
		{
			if (com.charAt(i) == '(')
				openPr++;
			else if (com.charAt(i) == ')')
				openPr--;
			if (openPr == 0) 
			{
				if (i != len - 1) 
				{// in case that we are not at the end of the
					// message
					int nexPrPos = 0;
					for (int j = i; j < len; j++) 
					{
						if (com.charAt(j) == '(') 
						{
							nexPrPos = j;
							break;
						}
					}
					if (nexPrPos != 0) {
						String operator = com.substring(i, nexPrPos);
						operator = findOperator(operator);

						String beforeOP = com.substring(1, i);
						String afterOP = com.substring(nexPrPos + 1, len - 1);
						if(operator =="AND"){
							Table temp = new Table("temp", table, null);
							if((isActiveTuple(beforeOP) && isActiveTuple(afterOP)) 
									|| (!isActiveTuple(beforeOP) && !isActiveTuple(afterOP)))
							{
								Table before = ReadCommand(beforeOP, table);
								Table after = ReadCommand(afterOP, before);
								temp = after;
							}
							else
							{
								if(isActiveTuple(beforeOP))
								{
									Table before = ReadCommand(beforeOP, table);
									Table after = ReadCommand(afterOP, before);
									temp = after;
								}
								else if(isActiveTuple(afterOP))
								{
									Table after = ReadCommand(afterOP, table);
									Table before = ReadCommand(beforeOP, after);
									temp = before;
								}
							}
							return temp;
						}
						if(operator == "OR"){
							Table temp = new Table("temp", table, null);
							Table before = ReadCommand(beforeOP, table);
							Table after = ReadCommand(afterOP, table);
							for (long l : before.getRows().keySet()) {
								temp.insert(l, table);
							}
							for (long l : after.getRows().keySet()) {
								temp.insert(l, table);
							}
							return temp;
						}
						break;
					}
				} else {

					if (com.equals("TRUE")) {
						return table;
					} else if (com.equals("FALSE")) {
						return new Table("temp3", table,null);
					} else {

						boolean not = false;
						String computevalue="";
						if (com.contains("NOT")) {
							not = true;
							computevalue = com.substring(4, len);
						}
						else{
							computevalue = com;
						}

						int computeValueLength=computevalue.length();
						Table temp = new Table("temp2", table, null);
						for (int j = 0; j < computeValueLength; j++) {
							if (computevalue.charAt(j) == '=') {
								String colName = computevalue.substring(0, j );
								String value = computevalue.substring(j + 1, computeValueLength);
								if(Parser.isRowDependent(value, table.getHeader()) || !isActiveTuple(computevalue))
								{
									int computedValInt = 0 ;
									String computedVal= "";
									try
									{
										for (Long k : table.getRows().keySet()) {
											computedVal = Parser.computeValue(value, table.getRows().get(k), table.getHeader());
											computedValInt = Integer.parseInt(computedVal);
											if(computedValInt == Integer.parseInt(table.getRows().get(k).getColumns().get(colName)))
											{
												temp.insert(k, table);
											}
										}
									}
									catch(Exception e)
									{
										for (Long k : table.getRows().keySet()) {
											if(computedVal.compareTo(table.getRows().get(k).getColumns().get(colName)) == 0)
											{
												temp.insert(k, table);
											}
										}
									}
								}
								else
								{
									int index = 0;
									for (int k = 0; k < table.getIndexes().size(); k++) {
										if(table.getIndexes().get(k).getColumnName().equals(colName))
											index = k;
									}
									LinkedList<Long> indexedMap = table.getIndexes().get(index).index.get(value);
									temp.insert(indexedMap, table);
								}
							}
							else if(computevalue.charAt(j) == '<'  && computevalue.charAt(j+1) != '='){
								String colName = computevalue.substring(0, j );
								String value = computevalue.substring(j + 1, computeValueLength);
								String computedVal="";
								int computedValInt =0 ;
								try
								{
									for (Long k : table.getRows().keySet()) {
										computedVal = Parser.computeValue(value, table.getRows().get(k), table.getHeader());
										computedValInt = Integer.parseInt(computedVal);
										if(computedValInt > Integer.parseInt(table.getRows().get(k).getColumns().get(colName)))
										{
											temp.insert(k, table);
										}
									}
								}
								catch(Exception e)
								{
									for (Long k : table.getRows().keySet()) {
										if(computedVal.compareTo(table.getRows().get(k).getColumns().get(colName)) > 0)
										{
											temp.insert(k, table);
										}
									}
								}
							}
							else if(computevalue.charAt(j) == '>'  && computevalue.charAt(j+1) != '='){
								String colName = computevalue.substring(0, j );
								String value = computevalue.substring(j + 1, computeValueLength);
								int computedValInt = 0;
								String computedVal="";
								try
								{
									for (Long k : table.getRows().keySet()) {
										computedVal = Parser.computeValue(value, table.getRows().get(k), table.getHeader());
										computedValInt = Integer.parseInt(computedVal);
										if(computedValInt < Integer.parseInt(table.getRows().get(k).getColumns().get(colName)))
										{
											temp.insert(k, table);
										}
									}
								}
								catch(Exception e)
								{
									for (Long k : table.getRows().keySet()) {
										if(computedVal.compareTo(table.getRows().get(k).getColumns().get(colName)) < 0)
										{
											temp.insert(k, table);
										}
									}
								}
							}
							else if(computevalue.charAt(j) == '<'  && computevalue.charAt(j+1) == '='){
								String colName = computevalue.substring(0, j );
								String value = computevalue.substring(j + 2, computeValueLength);
								int computedValInt = 0;
								String computedVal="";
								try
								{
									for (Long k : table.getRows().keySet()) {
										computedVal = Parser.computeValue(value, table.getRows().get(k), table.getHeader());
										computedValInt = Integer.parseInt(computedVal);
										if(computedValInt >= Integer.parseInt(table.getRows().get(k).getColumns().get(colName)))
										{
											temp.insert(k, table);
										}
									}
								}
								catch(Exception e)
								{
									for (Long k : table.getRows().keySet()) {
										if(computedVal.compareTo(table.getRows().get(k).getColumns().get(colName)) >= 0)
										{
											temp.insert(k, table);
										}
									}
								}
							}
							else if(computevalue.charAt(j) == '>'  && computevalue.charAt(j+1) == '='){
								String colName = computevalue.substring(0, j );
								String value = computevalue.substring(j + 2, computeValueLength);
								String computedVal="";
								int computedValInt = 0;
								try
								{
									for (Long k : table.getRows().keySet()) {
										computedVal = Parser.computeValue(value, table.getRows().get(k), table.getHeader());
										computedValInt = Integer.parseInt(computedVal);
										if(computedValInt <= Integer.parseInt(table.getRows().get(k).getColumns().get(colName)))
										{
											temp.insert(k, table);
										}
									}
								}
								catch(Exception e)
								{
									for (Long k : table.getRows().keySet()) {
										if(computedVal.compareTo(table.getRows().get(k).getColumns().get(colName)) <= 0)
										{
											temp.insert(k, table);
										}
									}
								}
							}
						}
						return temp;
					}

				}
			}

		}
		return null;

	}

	private static String findOperator(String operator) {
		// TODO Auto-generated method stub
		if (operator.contains("AND"))
			return "AND";
		if (operator.contains("OR"))
			return "OR";
		return null;
	}

	private static boolean isActiveTuple(String com)
	{

		int len = com.length();

		int openPr = 0;
		for (int i = 0; i < len; i++) {
			if (com.charAt(i) == '(')
				openPr++;
			else if (com.charAt(i) == ')')
				openPr--;
			if (openPr == 0) {
				if (i != len - 1) {// in case that we are not at the end of the
					// message
					int nexPrPos = 0;
					for (int j = i; j < len; j++) {
						if (com.charAt(j) == '(') {
							nexPrPos = j;
							break;
						}
					}
					if (nexPrPos != 0) {
						String operator = com.substring(i, nexPrPos);
						operator = findOperator(operator);

						String beforeOP = com.substring(1, i);
						String afterOP = com.substring(nexPrPos + 1, len - 1);
						boolean before = isActiveTuple(beforeOP);
						boolean after = isActiveTuple(afterOP);
						if(operator =="AND")
						{
							return before || after;
						}
						if(operator == "OR"){
							return before && after;
						}
						break;
					}
				} else {

					if (com.equals("TRUE")) {
						return true;
					} else if (com.equals("FALSE")) {
						return true;
					}
					else if(com.contains("NOT "))
					{
						return false;
					}
					else 
					{

						boolean not = false;
						String computevalue="";
						if (com.contains("NOT")) {
							not = true;
							computevalue = com.substring(4, len);
						}
						else{
							computevalue = com;
						}

						int computeValueLength=computevalue.length();
						for (int j = 0; j < computeValueLength; j++) {
							if (computevalue.charAt(j) == '=') {
								String colName = computevalue.substring(0, j );
								if(table.getIndexedHeaders().contains(colName))
									return true;
							}
						}
						return false;
					}

				}
			}

		}
		return false;

	}

}
