import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Table 
{
	private ArrayList<Index>indexes=new ArrayList<>();
	private LinkedList<String> header = new LinkedList<>();
	private HashMap<Long,Row>rows = new HashMap<>();
	private TableMode tableMode;
	private String primaryKey;
	private ArrayList<ForeignKey> foreignKeys = new ArrayList<>();
	private ArrayList<Table> referencedList = new ArrayList<>();


	public HashMap<Long, Row> getRows() {
		return rows;
	}

	public void setRows(HashMap<Long, Row> rows) {
		this.rows = rows;
	}

	public TableMode getTableMode() {
		return tableMode;
	}

	public void setTableMode(TableMode tableMode) {
		this.tableMode = tableMode;
	}

	public ArrayList<Table> getReferencedList() {
		return referencedList;
	}

	public void setReferencedList(ArrayList<Table> referencedList) {
		this.referencedList = referencedList;
	}

	private String name;
	private long uniqueRowNum=1L;

	/**
	 * 
	 * @param tableName name of table that had been given fk to this table(!)(FUCK!)
	 * @author Farzin_PC & amoo heshmat
	 */

	public void updateRefListForFK(String tableName)
	{
		Table t = TableMgr.getInstance().getTables().get(tableName);
		t.referencedList.add(this);
	}

	public Table(String name, Table table, TableMode mode)
	{
		this.tableMode = mode;
		this.name = name;
		this.header=table.getHeader();
		this.primaryKey = table.getPrimaryKey();
		this.foreignKeys = table.getForeignKey();
		this.referencedList = table.referencedList; //TODO
	}

	public Table(String name, LinkedList<String> cols, TableMode mode)
	{
		this.tableMode = mode;
		this.name = name;
		this.header=cols;
	}


	public String insert(Row data)
	{
		String output = "";
		if(checkC1Constraint(data) && checkC2Constraint(data))
		{
			rows.put(uniqueRowNum,data);
			for(Index i:getIndexes())
			{
				if(i.index.get(data.getColumns().get(i.getColumnName()))!=null&&
						!i.index.get(data.getColumns().get(i.getColumnName())).isEmpty())
				{
					LinkedList<Long>toBeUpdated=i.index.get(data.getColumns().get(i.getColumnName()));
					toBeUpdated.add(uniqueRowNum);
					//				i.index.replace(data.getColumns().get(i.getColumnName()), toBeUpdated);
				}
				else
				{
					LinkedList<Long>toBeAdded=new LinkedList<>();
					toBeAdded.add(uniqueRowNum);
					i.index.put(data.getColumns().get(i.getColumnName()), toBeAdded);
				}

			}
			uniqueRowNum++;
			output = "RECORD INSERTED";
		}
		else if(!checkC1Constraint(data) && !checkC2Constraint(data))
			output = "C1 AND C2 CONSTRAINTS FAILED";
		else if(!checkC1Constraint(data))
			output = "C1 CONSTRAINT FAILED";
		else if(!checkC2Constraint(data))
			output = "C2 CONSTRAINT FAILED";
		return output;
		//		if(primaryKey == null)
		//		{
		//			rows.put(uniqueRowNum,data);
		//			for(Index i:getIndexes())
		//			{
		//				if(i.index.get(data.getColumns().get(i.getColumnName()))!=null&&
		//						!i.index.get(data.getColumns().get(i.getColumnName())).isEmpty())
		//				{
		//					LinkedList<Long>toBeUpdated=i.index.get(data.getColumns().get(i.getColumnName()));
		//					toBeUpdated.add(uniqueRowNum);
		//					//				i.index.replace(data.getColumns().get(i.getColumnName()), toBeUpdated);
		//				}
		//				else
		//				{
		//					LinkedList<Long>toBeAdded=new LinkedList<>();
		//					toBeAdded.add(uniqueRowNum);
		//					i.index.put(data.getColumns().get(i.getColumnName()), toBeAdded);
		//				}
		//
		//			}
		//			uniqueRowNum++;
		//			output = "RECORD INSERTED";
		//		}
		//		else
		//		{
		//			String primaryKeyValue = data.getColumns().get(primaryKey);
		//			Index primaryIndex = getIndexByName(primaryKey);
		//			if(!primaryIndex.index.containsKey(primaryKeyValue))
		//			{
		//				rows.put(uniqueRowNum,data);
		//				for(Index i:getIndexes())
		//				{
		//					if(i.index.get(data.getColumns().get(i.getColumnName()))!=null&&
		//							!i.index.get(data.getColumns().get(i.getColumnName())).isEmpty())
		//					{
		//						LinkedList<Long>toBeUpdated=i.index.get(data.getColumns().get(i.getColumnName()));
		//						toBeUpdated.add(uniqueRowNum);
		//						//				i.index.replace(data.getColumns().get(i.getColumnName()), toBeUpdated);
		//					}
		//					else
		//					{
		//						LinkedList<Long>toBeAdded=new LinkedList<>();
		//						toBeAdded.add(uniqueRowNum);
		//						i.index.put(data.getColumns().get(i.getColumnName()), toBeAdded);
		//					}
		//
		//				}
		//				uniqueRowNum++;
		//				output = "RECORD INSERTED";
		//			}
		//			else
		//			{
		//				output = "C1 CONSTRAINT FAILED";
		//			}
		//		}


	}
	public void insert(long forceUnique,Row data)
	{
		rows.put(forceUnique,data);
		for(Index i:getIndexes()){
			if(i.index.get(data.getColumns().get(i.getColumnName()))!=null&&
					!i.index.get(data.getColumns().get(i.getColumnName())).isEmpty()){
				LinkedList<Long>toBeUpdated=i.index.get(data.getColumns().get(i.getColumnName()));
				toBeUpdated.add(forceUnique);
				i.index.replace(data.getColumns().get(i.getColumnName()), toBeUpdated);
			}
			else{
				LinkedList<Long>toBeAdded=new LinkedList<>();
				toBeAdded.add(forceUnique);
				i.index.put(data.getColumns().get(i.getColumnName()), toBeAdded);
			}

		}
	}
	public void insert(long uniqueRowNum, Table ref)
	{
		rows.put(uniqueRowNum,ref.getRows().get(uniqueRowNum));
	}

	public void insert(LinkedList<Long> uniqueRowNums, Table ref)
	{
		if(uniqueRowNums == null)
			return;
		for (Long rNum : uniqueRowNums) {
			rows.put(rNum, ref.getRows().get(rNum));
		}
	}

	public ArrayList<String> getIndexedHeaders(){
		ArrayList<String>returnValue=new ArrayList<>();
		for(Index i:indexes){
			returnValue.add(i.getColumnName());
		}
		return returnValue;
	}

	public void removeRow(Row r)
	{
		long remove = 0;
		for (Long l : rows.keySet()) {
			if(rows.get(l).equals(r))
			{
				remove = l;
			}
		}
		rows.remove(remove);
	}

	public String delete(Table t)
	{
		String output = "";
		if(deleteRestrict(t))
		{
			deleteCascade(t);
			HashMap<Long, Row> toDelete = t.getRows();
			for(Long key:toDelete.keySet())
			{
				Row row = toDelete.get(key);
				for(String col : row.getColumns().keySet())
				{
					if(getIndexByName(col) != null)
						getIndexByName(col).index.remove(row.getColumns().get(col));
				}
				rows.remove(key);
			}
		}
		else
		{
			output = "FOREIGN KEY CONSTRAINT RESTRICTS";
		}
		return output;
	}

	public Table select(LinkedList<String> columns,Table selectedTable)
	{
		if(columns==null||columns.isEmpty()){
			selectedTable.print();
			return selectedTable;
		}

		Table table = new Table("selected", columns, TableMode.TEMPORALL);
		for(Long key:selectedTable.rows.keySet()){
			Row r=selectedTable.rows.get(key);
			Row newRow=new Row();
			LinkedHashMap<String, String>rowValues=new LinkedHashMap<>();
			for(String s:selectedTable.getHeader()){
				if(columns.contains(s)){
					rowValues.put(s, r.getColumns().get(s));
				}
			}
			newRow.setColumns(rowValues);
			table.insert(key,newRow);
		}
		//		table.print();
		return table;
	}

	public void print()
	{
		if(this.getRows().size() == 0)
		{
			System.out.println("NO RESULT");
			return;
		}
		int i = 0;
		for(String header:this.getHeader())
		{
			if(i == this.getHeader().size()-1)
				System.out.print(header);
			else
				System.out.print(header+",");
			i++;
		}
		i = 0;
		for(Long key:this.getRows().keySet()){
			System.out.println(this.getRows().get(key));

		}
	}

	public String update(Table table,String columnName,String valueToCompute, String whereClause)
	{
		boolean isFK = false;
		String output = "";
		for (ForeignKey foreignKey : foreignKeys) 
		{
			if(foreignKey.getColumnName().equals(columnName))
			{
				isFK = true;
				break;
			}
		}
		for(Long key : table.getRows().keySet())
		{
			Row r = rows.get(key);
			String newVal = Parser.computeValue(valueToCompute, r, table.getHeader());

			if(columnName.equals(primaryKey))
			{
				if(checkC1ForPK(newVal)) 
				{
					if(updateRestrict(table, columnName, valueToCompute))
					{
						updateCascade(table, columnName, valueToCompute); //update children tables
						
						//update this table
						LinkedHashMap<String, String>columns=r.getColumns();
						columns.replace(columnName,newVal);
						r.setColumns(columns);
						rows.replace(key,r);
						
						//update index of table
						String oldVal = r.getColumns().get(primaryKey);
						Index primaryIndex = getIndexByName(primaryKey);
						LinkedList<Long> indexList = primaryIndex.index.get(oldVal);
						primaryIndex.index.remove(oldVal);
						primaryIndex.index.put(newVal, indexList);
					}
				}
				else
				{
					output = "C1 CONSTRAINT FAILED";
				}
			}
			else if(isFK)
			{
				if(checkC2ForFK(newVal))
				{
					//update this table
					LinkedHashMap<String, String>columns=r.getColumns();
					columns.replace(columnName,newVal);
					r.setColumns(columns);
					rows.replace(key,r);
					
					//update index of table
					String oldVal = r.getColumns().get(columnName);
					Index fkIndex = getIndexByName(columnName);
					LinkedList<Long> indexList = fkIndex.index.get(oldVal);
					fkIndex.index.remove(oldVal);
					fkIndex.index.put(newVal, indexList);
				}
				else
				{
					output = "C2 CONSTRAINT FAILED";
				}
			}
			else
			{
				LinkedHashMap<String, String>columns=r.getColumns();
				columns.replace(columnName,newVal);
				r.setColumns(columns);
				rows.replace(key,r);
			}
		}
		return output;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public ArrayList<ForeignKey> getForeignKey() {
		return foreignKeys;
	}

	public void setForeignKey(ArrayList<ForeignKey> foreignKey) {
		this.foreignKeys = foreignKey;
	}

	public LinkedList<String> getHeader() {
		return header;
	}

	public void setHeader(LinkedList<String> header) {
		this.header = header;
	}

	public ArrayList<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(ArrayList<Index> indexes) {
		this.indexes = indexes;
	}

	public void addForeignKey(ForeignKey fk)
	{
		foreignKeys.add(fk);
	}
	public String createIndex(String indexName,String columnName){
		if(getIndexByName(columnName) != null)
			return "";
		Index i=new Index();
		i.setColumnName(columnName);
		i.setIndexName(indexName);
		for(Long tableKey:this.getRows().keySet()){
			if(i.index.get(getRows().get(tableKey).getColumns().get(columnName))!=null)
			{
				LinkedList<Long>newIds=i.index.get(getRows().get(tableKey).getColumns().get(columnName));
				newIds.add(tableKey);
				//				i.index.replace(getRows().get(tableKey), newIds);
			}
			else
			{
				LinkedList<Long>newIds=new LinkedList<>();
				newIds.add(tableKey);
				i.index.put(getRows().get(tableKey).getColumns().get(columnName),newIds);
			}
		}
		indexes.add(i);
		return "INDEX CREATED";
	}
	public static String computeValue(String computable)
	{
		String out = "";
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

	@Override
	public String toString() {
		if(this.getRows().size() == 0)
		{
			return "NO RESULT";
		}
		int i = 0;
		StringBuilder builder = new StringBuilder();
		for(String header:this.getHeader())
		{
			if(i == this.getHeader().size()-1)
				builder.append(header);
			else
				builder.append(header+",");
			i++;
		}
		builder.append("\n");
		i = 0;
		for(Long key:this.getRows().keySet()){
			builder.append(rows.get(key)+"\n");
		}
		return builder.toString();
	}

	public Index getIndexByName(String colName)
	{
		for (Index i : getIndexes()) 
		{
			if(i.getColumnName().equals(colName))
				return i;
		}
		return null;
	}

	public boolean checkC1Constraint(Row data)
	{
		if(primaryKey == null)
		{
			return true;
		}

		else
		{
			String primaryKeyValue = data.getColumns().get(primaryKey);
			Index primaryIndex = getIndexByName(primaryKey);
			if(!primaryIndex.index.containsKey(primaryKeyValue))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	public boolean checkC2Constraint(Row data)
	{
		for (ForeignKey fk : foreignKeys) 
		{
			Table referencedTable = TableMgr.getInstance().getTableByName(fk.getReferencedTable());
			String refTablePK = referencedTable.primaryKey;
			Index refIndex = referencedTable.getIndexByName(refTablePK);
			if(!refIndex.index.containsKey(data.getColumns().get(fk.getColumnName())))
				return false;
		}
		return true;
	}
	
	public boolean checkC2ForFK(String FKValue)
	{
		for (ForeignKey fk : foreignKeys) 
		{
			Table referencedTable = TableMgr.getInstance().getTableByName(fk.getReferencedTable());
			String refTablePK = referencedTable.primaryKey;
			Index refIndex = referencedTable.getIndexByName(refTablePK);
			if(!refIndex.index.containsKey(FKValue))
				return false;
		}
		return true;
	}

	public boolean checkC1ForPK(String PKValue)
	{
		Index primaryIndex = getIndexByName(primaryKey);
		if(primaryIndex.index.containsKey(PKValue))
			return false;
		else
			return true;
	}
	/**
	 * 
	 * @param table to be deleted from original table
	 * @return true if there is no fk values in child tables false o.w
	 */
	public boolean deleteRestrict(Table table)
	{
		HashMap<Table, ForeignKey> childs = getChildTablesByDeleteMode(Mode.RESTRICT);
		for (Table child : childs.keySet()) 
		{
			Index index = child.getIndexByName(childs.get(child).getColumnName());
			for (Long l : table.getRows().keySet()) 
			{
				if(index.index.containsKey(table.getRows().get(l).getColumns().get(table.primaryKey)))
				{
					return false;
				}
			}
		}
		return true;

	}

	public boolean deleteCascade(Table table)
	{
		HashMap<Table, ForeignKey> childs = getChildTablesByDeleteMode(Mode.CASCADE);
		for(Table child : childs.keySet())
		{
			HashMap<Long, Row> rowsToDelete = table.getRows();
			for (Row r : rowsToDelete.values()) 
			{
				String query = "DELETE FROM "+child.getName()+" WHERE "+childs.get(child).getColumnName()+"="+r.getColumns().get(table.getPrimaryKey())+";";
				Parser.parseQuery(query);
			}
		}
		return true;
	}

	public boolean updateRestrict(Table table,String columnName,String valueToCompute)
	{
		HashMap<Table, ForeignKey> childs = getChildTablesByUpdateMode(Mode.RESTRICT);
		for (Table child : childs.keySet()) 
		{
			Index index = child.getIndexByName(childs.get(child).getColumnName());
			for (Long l : table.getRows().keySet()) 
			{
				String newVal = Parser.computeValue(valueToCompute, table.getRows().get(l), getHeader());
				if(index.index.containsKey(newVal))
				{
					return false;
				}
			}
		}
		return true;
	}

	public boolean updateCascade(Table table,String columnName,String valueToCompute)
	{

		HashMap<Table, ForeignKey> childs = getChildTablesByDeleteMode(Mode.CASCADE);
		for(Table child : childs.keySet())
		{
			HashMap<Long, Row> rowsToUpdate = table.getRows();
			for (Row r : rowsToUpdate.values()) 
			{
				String query = "UPDATE "+child.getName()+" SET "+childs.get(child).getColumnName()+"="+valueToCompute+" WHERE "+childs.get(child).getColumnName()+"="+r.getColumns().get(table.getPrimaryKey())+";";
				Parser.parseQuery(query);
			}
		}
		return true;
	}



	public HashMap<Table, ForeignKey> getChildTablesByDeleteMode(Mode mode)
	{
		HashMap<Table, ForeignKey> childs = new HashMap<>();
		for (Table table : referencedList) 
		{
			for (ForeignKey fk : table.foreignKeys) 
			{
				if(fk.getReferencedTable().equals(this.getName()) && fk.getOnDelete() == mode)
					childs.put(table, fk);
			}
		}
		return childs;
	}

	public HashMap<Table, ForeignKey> getChildTablesByUpdateMode(Mode mode)
	{
		HashMap<Table, ForeignKey> childs = new HashMap<>();
		for (Table table : referencedList) 
		{
			for (ForeignKey fk : foreignKeys) 
			{
				if(fk.getReferencedTable().equals(table.getName()) && fk.getOnUpdate() == mode)
					childs.put(table, fk);
			}
		}
		return childs;
	}

}

enum TableMode
{
	TEMPORALL, 
	PERMENANT,
}
