
public class ForeignKey {
	
	private Table referencedTable;
	private String columnName;
	private Mode onDelete;
	private Mode onUpdate;
	
	public Table getReferencedTable() {
		return referencedTable;
	}
	
	public void setReferencedTable(Table referencedTable) {
		this.referencedTable = referencedTable;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public Mode getOnDelete() {
		return onDelete;
	}
	
	public void setOnDelete(Mode onDelete) {
		this.onDelete = onDelete;
	}
	
	public Mode getOnUpdate() {
		return onUpdate;
	}
	
	public void setOnUpdate(Mode onUpdate) {
		this.onUpdate = onUpdate;
	}
	
	public ForeignKey(String name, String ref, Mode del, Mode update)
	{
		this.columnName = name;
		this.referencedTable = TableMgr.getInstance().getTables().get(ref);
		this.onDelete = del;
		this.onUpdate = update;
	}
	
}

enum Mode{
	RESTRICT,
	CASCADE
}