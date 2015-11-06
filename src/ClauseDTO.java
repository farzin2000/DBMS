
public class ClauseDTO {
	private String colName;
	private String operator;
	private String computeValue;
	private boolean andOr;
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getComputeValue() {
		return computeValue;
	}
	public void setComputeValue(String computeValue) {
		this.computeValue = computeValue;
	}
	
}
