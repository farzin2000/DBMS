import java.util.HashMap;

public class Row
{
	private HashMap<String, String>columns = new HashMap<>();


	public HashMap<String, String> getColumns() {
		return columns;
	}

	public void setColumns(HashMap<String, String> row) {
		this.columns = row;
	}
	@Override
	public String toString(){
		String toReturn="";
		int index = 0;
		for(String s:columns.keySet())
		{
//			System.out.println(s);
			if(index == 0)
				toReturn=columns.get(s);
			else
				toReturn=toReturn+"\t"+columns.get(s);
			index++;
		}
		return toReturn;
	}
}
