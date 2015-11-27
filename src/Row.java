import java.util.HashMap;
import java.util.LinkedHashMap;

public class Row
{
	private LinkedHashMap<String, String>columns = new LinkedHashMap<>();


	public LinkedHashMap<String, String> getColumns() {
		return columns;
	}

	public void setColumns(LinkedHashMap<String, String> row) {
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
				toReturn=toReturn+","+columns.get(s);
			index++;
		}
		return toReturn;
	}
}
