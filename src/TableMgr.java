import java.util.HashMap;

public class TableMgr 
{	
	private static TableMgr manager;
	
	private HashMap<String, Table> tables = new HashMap<>();
	
	private TableMgr()
	{
		
	}
	
	public static TableMgr getInstance()
	{
		if(manager != null)
			return manager;
		else
			return manager = new TableMgr();
	}

	public HashMap<String, Table> getTables() {
		return tables;
	}

	public void setTables(HashMap<String, Table> tables) {
		this.tables = tables;
	}

}
