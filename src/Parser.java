import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Parser {
	public static String parseQuery(String query) {
		String output = "";
		Table table;
		query = query.replace(";", "");
		String[] tokens = query.split(" ");
		String insertInsideParanthese;
		String createTableInsideParanthese;
		int startInd = 0;
		int endInd = 0;
		for (int i = 0; i < query.length(); i++) {
			if (query.charAt(i) == '(')
				startInd = i;
			if (query.charAt(i) == ')')
				endInd = i;
		}

		if (tokens[0].equals("CREATE")) {
			if (tokens[1].equals("TABLE")) {
				createTableInsideParanthese = query.substring(startInd, endInd + 1);
				LinkedList<String> headers = new LinkedList<>();
				String tableName = tokens[2].split("\\(")[0];
				createTableInsideParanthese = createTableInsideParanthese.replace("(", "");
				createTableInsideParanthese = createTableInsideParanthese.replace(")", "");
				String[] colNames = createTableInsideParanthese.split(",");
				for (int i = 0; i < colNames.length; i++) {
					headers.add(colNames[i].split(" ")[0]);
				}
				table = new Table(tableName, headers, null);

				if (endInd != query.length() - 1) {
					String[] tokens2 = query.substring(endInd + 2).split(" ");
					String pkColumn = null;
					ArrayList<ForeignKey> fks = new ArrayList<>();
					for (int i = 0; i < tokens2.length; i++)

					{
						if (tokens2[i].equals("PRIMARY"))
							pkColumn = tokens2[i + 2];
						else if (tokens2[i].equals("FOREIGN")) {
							String name = tokens2[i + 2];
							String ref = tokens2[i + 4];
							table.updateRefListForFK(ref);
							Mode del = null;
							Mode update = null;
							if (tokens2[i + 7].equals("CASCADE"))
								del = Mode.CASCADE;
							if (tokens2[i + 7].equals("RESTRICT"))
								del = Mode.RESTRICT;
							if (tokens2[i + 10].equals("CASCADE"))
								update = Mode.CASCADE;
							if (tokens2[i + 10].equals("RESTRICT"))
								update = Mode.RESTRICT;
							ForeignKey fk = new ForeignKey(name, ref, del, update);
							table.createIndex(fk.getColumnName() + "_index", fk.getColumnName());
							fks.add(fk);
						}
					}
					table.setForeignKey(fks);
					if (pkColumn != null) {
						table.setPrimaryKey(pkColumn);
						table.createIndex(pkColumn + "_index", pkColumn);
					}
				}
				TableMgr.getInstance().addTable(table);
				output = "TABLE CREATED";
				// return output;
			} else if (tokens[1].equals("INDEX")) {
				String indexName = tokens[2];
				String tableName = tokens[4].split("\\(")[0];
				String col = tokens[5].substring(1, tokens[5].length() - 1);// tokens[4].split("\\(")[1];
				output = TableMgr.getInstance().getTables().get(tableName).createIndex(indexName, col);
			}
		} else if (tokens[0].equals("INSERT")) {
			insertInsideParanthese = query.substring(startInd, endInd + 1);
			output = parseINSERT(insertInsideParanthese, tokens[2]);
		} else if (tokens[0].equals("UPDATE")) {
			output = parseUPDATE(query);
		} else if (tokens[0].equals("DELETE")) {
			output = parseDELETE(query);
		} else if (tokens[0].equals("SELECT")) {
			output = parseSELECT(query);
		}

		return output;
	}

	private static String parseSELECT(String query) {
		String[] tokens = query.split(" ");
		LinkedList<String> cols = new LinkedList<>();
		for (String string : tokens[1].split(",")) {
			cols.add(string);
		}
		if (query.toUpperCase().contains("JOIN")) {
			return Join.joinQuery(query,cols).toString();
		} else if (!tokens[3].contains(",")) {
			String tableName = tokens[3];
			String whereClause = "";
			for (int i = 5; i < tokens.length; i++) {
				if (i == tokens.length - 1)
					whereClause = whereClause + tokens[i];
				else
					whereClause = whereClause + tokens[i] + " ";
			}
			Table selectedTable = TableMgr.getInstance().getTables().get(tableName);
			WhereRecursive wr = new WhereRecursive(selectedTable);
			return selectedTable.select(cols, wr.ReadCommand(whereClause, selectedTable)).toString();
		}
		else {
			String[]tableNames=new String[2];
			tableNames[0]=tokens[3].split(",")[0];
			tableNames[1]=tokens[3].split(",")[1];
			Table selectedTable=Cartesian.getAnswerByCartesian(tableNames[0],tableNames[1],query);
			WhereRecursive wr= new WhereRecursive(selectedTable);
			return wr.ReadCommand(query.substring(query.toUpperCase().indexOf("WHERE")+6).trim(), selectedTable).select(cols,selectedTable).toString();
		}
	}

	private static String parseUPDATE(String query) {
		String[] tokens = query.split(" ");
		String tableName = tokens[1];
		String updatable = tokens[3];
		String colName = updatable.split("=")[0];
		String computeValue = updatable.split("=")[1];
		String whereClause = "";
		for (int i = 5; i < tokens.length; i++) {
			if (i == tokens.length - 1)
				whereClause = whereClause + tokens[i];
			else
				whereClause = whereClause + tokens[i] + " ";
		}
		Table selectedTable = TableMgr.getInstance().getTables().get(tableName);
		WhereRecursive wr = new WhereRecursive(selectedTable);
		return selectedTable.update(wr.ReadCommand(whereClause, selectedTable), colName, computeValue, whereClause);

	}

	private static String parseDELETE(String query) {
		String[] tokens = query.split(" ");
		String tableName = tokens[2];
		// String deletable = tokens[4];
		String whereClause = "";
		for (int i = 4; i < tokens.length; i++) {
			if (i == tokens.length - 1)
				whereClause = whereClause + tokens[i];
			else
				whereClause = whereClause + tokens[i] + " ";
		}
		Table selectedTable = TableMgr.getInstance().getTables().get(tableName);
		WhereRecursive wr = new WhereRecursive(selectedTable);
		return selectedTable.delete(wr.ReadCommand(whereClause, selectedTable));
	}

	private static String parseINSERT(String query, String tableName) {
		Table selectedTable = TableMgr.getInstance().getTables().get(tableName);
		Row r = new Row();
		LinkedHashMap<String, String> cols = new LinkedHashMap<>();
		int index = 0;

		for (String colName : selectedTable.getHeader()) {
			query = query.replace("(", "").replace(")", "");
			cols.put(colName, query.split(",")[index].replace("\"", ""));
			index++;
		}

		r.setColumns(cols);
		return selectedTable.insert(r);
	}

	public static String computeValue(String computable, Row row, LinkedList<String> tableHeader) {
		String out = "";
		for (int i = 0; i < tableHeader.size(); i++) {
			// TODO: cannot have two colName in compute value!!
			computable = computable.replaceFirst(tableHeader.get(i), row.getColumns().get(tableHeader.get(i)));

		}
		// TODO: minus is not considered!
		if (Character.isDigit(computable.charAt(0))) {
			if (!computable.contains("+") && !computable.contains("-") && !computable.contains("*")
					&& !computable.contains("/")) {
				List<String> oneNum = new ArrayList<>();
				int count = 0;
				while (count != computable.length()) {
					oneNum.add(computable.charAt(count) + "");
					count++;
				}
				StringBuilder listString = new StringBuilder();
				for (String s : oneNum)
					listString.append(s + "");
				return Integer.parseInt(listString.toString()) + "";
			}
			int count = 0;
			Integer num = null;
			char lastOperand = 0;
			List<String> oneNum = new ArrayList<>();
			while (count != computable.length() + 1) {
				if (count != computable.length() && Character.isDigit(computable.charAt(count))) {
					oneNum.add(computable.charAt(count) + "");
					count++;
					continue;
				} else if (num == null) {
					StringBuilder listString = new StringBuilder();
					for (String s : oneNum)
						listString.append(s + "");
					num = Integer.parseInt(listString.toString());
					lastOperand = computable.charAt(count);
					count++;
					oneNum = new ArrayList<>();
				} else {
					StringBuilder listString = new StringBuilder();
					for (String s : oneNum)
						listString.append(s + "");
					switch (lastOperand) {
					case '+':
						num += Integer.parseInt(listString.toString());
						break;
					case '-':
						num -= Integer.parseInt(listString.toString());
						break;
					case '*':
						num *= Integer.parseInt(listString.toString());
						break;
					case '/':
						num /= Integer.parseInt(listString.toString());
						break;
					}
					if (count == computable.length())
						break;
					lastOperand = computable.charAt(count);
					count++;
					oneNum = new ArrayList<>();
				}
			}
			out = String.valueOf(num);
		} else {
			String[] splited = computable.split("\\+");
			for (int i = 0; i < splited.length; i++) {
				out += splited[i];
			}
		}
		out = out.replace("\"", "");
		return out;
	}

	public static boolean isRowDependent(String computable, LinkedList<String> tableHeader) {
		for (int i = 0; i < tableHeader.size(); i++) {
			if (computable.contains(tableHeader.get(i))) {
				return true;
			}
		}
		return false;
	}
}
