import java.util.HashMap;

public class TableMgr {
	static TableMgr instance;
	private HashMap<String, Table> tables = new HashMap<>();
	private TableMgr()
	{

	}

	public static TableMgr getInstance() {
		if(instance == null)
		{
			instance = new TableMgr();
			return instance;
		}
		else
			return instance;
	}
	
	public Table getTableByName(String name){
		return tables.get(name);
	}
	
	public void deleteTableByName(String tableName){
		tables.remove(tableName);
	}
	
	public void addTable(Table table)
	{
		getTables().put(table.getName(), table);
	}

	public HashMap<String, Table> getTables() {
		return tables;
	}

	public void setTables(HashMap<String, Table> tables) {
		this.tables = tables;
	}
	
}

