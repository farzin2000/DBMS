import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	}

	public Table(String name, LinkedList<String> cols, TableMode mode)
	{
		this.tableMode = mode;
		this.name = name;
		this.header=cols;
	}


	public void insert(Row data)
	{
		rows.put(uniqueRowNum,data);
		for(Index i:getIndexes()){
			if(i.index.get(data.getColumns().get(i.getColumnName()))!=null&&
					!i.index.get(data.getColumns().get(i.getColumnName())).isEmpty()){
				LinkedList<Long>toBeUpdated=i.index.get(data.getColumns().get(i.getColumnName()));
				toBeUpdated.add(uniqueRowNum);
				i.index.replace(data.getColumns().get(i.getColumnName()), toBeUpdated);
			}
			else{
				LinkedList<Long>toBeAdded=new LinkedList<>();
				toBeAdded.add(uniqueRowNum);
				i.index.put(data.getColumns().get(i.getColumnName()), toBeAdded);
			}

		}
		uniqueRowNum++;
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

	public void delete(Table t)
	{
		
		boolean isRestricted = true;
		for(Table refTable : referencedList)
		{
			for (ForeignKey fk : refTable.getForeignKey()) 
			{
				if(fk.getReferencedTable().getName() == this.getName() && fk.getOnDelete() == Mode.CASCADE)
				{
					isRestricted = false;
					HashMap<Long, Row> del = t.getRows();
					for (Row rT : del.values()) {
						String query = "DELETE FROM "+refTable.getName()+" WHERE "+fk.getColumnName()+"="+rT.getColumns().get(t.getPrimaryKey())+";";
						Parser.parseQuery(query);
					}
				}
			}
		}
		if(!isRestricted)
		{
			HashMap<Long, Row> toDelete = t.getRows();
			for(Long key:toDelete.keySet()){
				rows.remove(key);
			}
		}
		else
		{
			System.out.println("FOREIGN KEY CONSTRAINT RESTRICTS");
		}
		return;
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
		table.print();
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
		System.out.println();
		i = 0;
		//		System.out.println("================");
		for(Long key:this.getRows().keySet()){
			System.out.println(this.getRows().get(key));

		}
	}

	public void update(Table table,String columnName,String valueToCompute){

		if(columnName.equals(primaryKey))
		{
			boolean isRestricted = true;
			for(Table refTable : referencedList)
			{
				for (ForeignKey fk : refTable.getForeignKey()) 
				{
					if(fk.getReferencedTable().getName() == this.getName() && fk.getOnUpdate() == Mode.CASCADE)
					{
						isRestricted = false;
						HashMap<Long, Row> del = table.getRows();
						for (Row rT : del.values()) {
							String query = "UPDATE "+refTable.getName()+" SET "+fk.getColumnName()+"="+valueToCompute+" WHERE "+fk.getColumnName()+"="+rT.getColumns().get(table.getPrimaryKey())+";";
							Parser.parseQuery(query);
						}
					}
				}
			}
			if(!isRestricted)
			{
				for(Long key:table.getRows().keySet()){
					Row r=rows.get(key);
					LinkedHashMap<String, String>columns=r.getColumns();
					columns.replace(columnName,Parser.computeValue(valueToCompute, r, table.getHeader()));
					r.setColumns(columns);
					rows.replace(key,r);
				}
			}
			else
			{
				System.out.println("FOREIGN KEY CONSTRAINT RESTRICTS");
				
			}
		}
		else
		{
			for(Long key:table.getRows().keySet()){
				Row r=rows.get(key);
				LinkedHashMap<String, String>columns=r.getColumns();
				columns.replace(columnName,Parser.computeValue(valueToCompute, r, table.getHeader()));
				r.setColumns(columns);
				rows.replace(key,r);
			}
		}
	
	return;
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
public void createIndex(String indexName,String columnName){
	Index i=new Index();
	i.setColumnName(columnName);
	i.setIndexName(indexName);
	for(Long tableKey:this.getRows().keySet()){
		if(i.index.get(columnName)!=null){
			LinkedList<Long>newIds=i.index.get(columnName);
			newIds.add(tableKey);
			i.index.replace(columnName, newIds);
		}
		else{
			LinkedList<Long>newIds=new LinkedList<>();
			newIds.add(tableKey);
			i.index.put(columnName,newIds);
		}
	}
	indexes.add(i);
	System.out.println("INDEX CREATED");
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
	return this.getName();
}
}

enum TableMode
{
	TEMPORALL, 
	PERMENANT,
}
