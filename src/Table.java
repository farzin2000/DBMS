import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Table 
{
	
	private LinkedList<String> header = new LinkedList<>();
	private LinkedList<Row> rows = new LinkedList<>();
	private TableMode tableMode;
	private HashMap<String, ArrayList<String>> columns = new HashMap<>();
	private String name;

	public Table(String name, LinkedList<String> cols, TableMode mode)
	{
		this.tableMode = mode;
		this.name = name;
		for (Iterator iterator = cols.iterator(); iterator.hasNext();) 
		{
			String column = (String) iterator.next();
			getColumns().put(column, new ArrayList<String>());
		}
	}

	public void insert(HashMap<String, String> data)
	{
		for (String col : data.keySet()) 
		{
			getColumns().get(col).add(data.get(col));
		}
	}

	public int sizeOfColumn(String column)
	{
		int size = 0;
		for (String col : this.getColumns().keySet()) 
		{
			if(col.equals(column))
				size = getColumns().get(col).size();
		}
		return size;
	}

	public void select(LinkedList<String> columns)
	{
		Table table = new Table("selected", columns, TableMode.TEMPORALL);
		for (String col : table.getColumns().keySet()) {
			for (String col2 : this.getColumns().keySet()) {
				if(col.equals(col2))
				{
					for (int j = 0; j < this.sizeOfColumn(col2); j++) {
						table.getColumns().get(col).add(this.getColumns().get(col2).get(j));
					}
				}
			}
		}
		table.print();
	}

	public void delete()
	{
		for (String col : this.getColumns().keySet()) {
			this.getColumns().get(col).clear();
		}
	}

	public void update(String[] string)
	{
		for (String col : this.getColumns().keySet()) {
			if(col.equals(string[0]))
			{
				if(string[1].charAt(0) == '"')
				{
					string[1] = string[1].replace("\"", "");
					for (int i = 0; i < this.sizeOfColumn(col); i++) {
						this.getColumns().get(col).set(i, string[1]);
					}
				}
				else if(string[1].charAt(0) >= 65 & string[1].charAt(0) <= 90)
				{
					for (int i = 0; i < this.sizeOfColumn(col); i++) {
						this.getColumns().get(col).set(i, this.getColumns().get(string[1]).get(i));
					}
				}
				else
				{
					for (int i = 0; i < this.sizeOfColumn(col); i++) {
						this.getColumns().get(col).set(i, string[1]);
					}
				}
			}
		}
	}

	public void print()
	{
		int jj = 0;
		for (String col : this.getColumns().keySet()) {
			jj++;
			if(this.getColumns().keySet().size() != jj)
				System.out.print(col + ",");
			else
				System.out.println(col);
		}
		boolean t = true;
		int j = 0;
		while(t) {
			jj = 0;
			for (String col : this.getColumns().keySet()) {
				if(this.sizeOfColumn(col) == j)
				{
					t = false;
					break;
				}
				jj++;
				if(this.getColumns().keySet().size() != jj)
					System.out.print(this.getColumns().get(col).get(j) + ",");
				else
					System.out.println(this.getColumns().get(col).get(j));
			}
			j++;
		}
	}

	public HashMap<String, ArrayList<String>> getColumns() {
		return columns;
	}

	public void setColumns(HashMap<String, ArrayList<String>> columns) {
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<String> getHeader() {
		return header;
	}

	public void setHeader(LinkedList<String> header) {
		this.header = header;
	}

	public LinkedList<Row> getRows() {
		return rows;
	}

	public void setRows(LinkedList<Row> rows) {
		this.rows = rows;
	}
}

enum TableMode
{
	TEMPORALL, 
	PERMENANT,
}
