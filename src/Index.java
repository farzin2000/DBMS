import java.util.HashMap;
import java.util.LinkedList;


public class Index {
	private String columnName;
	private String indexName;
	public HashMap<String, LinkedList<Long>>index=new HashMap<>();
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	
}
