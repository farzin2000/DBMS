import java.util.LinkedList;

public class Row
{
	private LinkedList<String> row = new LinkedList<>();

	public LinkedList<String> getRow() {
		return row;
	}

	public void setRow(LinkedList<String> row) {
		this.row = row;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return row.toString();
	}
}
